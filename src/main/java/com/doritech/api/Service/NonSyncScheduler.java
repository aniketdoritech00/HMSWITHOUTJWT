package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;  //  ADD THIS IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.doritech.api.DTO.DeviceMasterDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.NonSyncLogEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.HeartbeatRepository;
import com.doritech.api.Repository.NonSyncLogRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.SmsDetailsRepository;

@Configuration
@EnableScheduling
public class NonSyncScheduler implements SchedulingConfigurer {

    // ----------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final String PARAM_CODE             = "SCHEDULAR";
    private static final String PARAM_SERIAL           = "NON_SYNC";
    private static final String DEFAULT_CRON           = "0 0 */6 * * ?";
    private static final long   DEFAULT_THRESHOLD_HOURS = 24L;

    // ----------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------
    @Autowired
    private NonSyncLogRepository nonSyncLogRepository;

    @Autowired
    private ParamRepository paramEntityRepository;

    @Autowired
    private DeviceMasterService deviceService;

    @Autowired
    private BranchMasterRepository branchMasterRepository;

    @Autowired
    private SmsDetailsRepository smsDetailsRepository;

    @Autowired
    private HeartbeatRepository heartbeatRepository;

    // ================================================================
    // SCHEDULER CONFIG — cron from param_entity
    // ================================================================
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::executeNonSyncJob,
                triggerContext -> {
                    String cron = getCronFromParamTable();
                    System.out.println("Cron loaded from param_entity: " + cron);
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }

    // ================================================================
    // PARAM HELPERS
    // ================================================================

    /** param1 = cron expression */
    private String getCronFromParamTable() {
        try {
            ParamEntity param = paramEntityRepository
                    .findByCodeAndSerial(PARAM_CODE, PARAM_SERIAL);
            if (param != null
                    && param.getDescp2() != null
                    ) {
            	System.out.println("The schedular Time in DB is :: "+param.getDescp2() );
                return param.getDescp2();
            }
        } catch (Exception e) {
            System.out.println("Error reading cron: " + e.getMessage());
        }
        return DEFAULT_CRON;
    }

    /** param3 = threshold hours (It is using for data should be based on 24 hour(not sync time) from DB.)*/
    private long getThresholdHoursFromParam() {
        try {
            ParamEntity param = paramEntityRepository
                    .findByCodeAndSerial(PARAM_CODE, PARAM_SERIAL);
            if (param != null
                    && param.getDescp3() != null
                   ) {
            	System.out.println("The Threshold Time in DB is :: "+param.getDescp3() );

                return Long.parseLong(param.getDescp3().trim());
            }
        } catch (Exception e) {
            System.out.println("Error reading threshold: " + e.getMessage());
        }
        return DEFAULT_THRESHOLD_HOURS;
    }

    // ================================================================
    // MAIN JOB
    // ================================================================
    private void executeNonSyncJob() {

        System.out.println("=== NonSync Job Started: " + LocalDateTime.now() + " ===");

        // Step 1: Param check
        ParamEntity param = null;
        try {
            param = paramEntityRepository.findByCodeAndSerial(PARAM_CODE, PARAM_SERIAL);
        } catch (Exception e) {
            System.out.println("Param table read error: " + e.getMessage());
            return;
        }

        if (param == null) {
            System.out.println("NONSYNC_SCHEDULER param not found. Skipping.");
            return;
        }

        // Step 2: SUPER HO branches fetch
        List<BranchMasterEntity> superHoBranches =
                branchMasterRepository.findByBranchType("SUPER HO");

        if (superHoBranches == null || superHoBranches.isEmpty()) {
            System.out.println("No SUPER HO branch found. Skipping.");
            return;
        }

        System.out.println("SUPER HO branches found: " + superHoBranches.size());

        // Step 3: Collect all not-synced devices across all branches
        List<Map<String, Object>> allNotSyncedDevices = new ArrayList<>();

        for (BranchMasterEntity branch : superHoBranches) {
            String branchCode = branch.getBranchCode();
            if (branchCode == null) continue;

            System.out.println("Processing branchCode: " + branchCode);
            List<Map<String, Object>> devices = fetchNotSyncedDevices(branchCode);
            allNotSyncedDevices.addAll(devices);
        }

        System.out.println("Total not-synced devices: " + allNotSyncedDevices.size());

        // Step 4: Insert new / update existing entries
        processNotSyncedDevices(allNotSyncedDevices);

        // Step 5: Close devices that are now synced
        closeResolvedEntries(allNotSyncedDevices);

        System.out.println("=== NonSync Job Completed: " + LocalDateTime.now() + " ===");
    }

    // ================================================================
    // FETCH NOT-SYNCED DEVICES using DeviceMasterDTO
    // ================================================================
    private List<Map<String, Object>> fetchNotSyncedDevices(String branchCode) {
        try {
            ResponseEntity apiResponse =
                    deviceService.getAllDevicesByBranchCode(branchCode);

            if (apiResponse == null || apiResponse.getPayload() == null) {
                System.out.println("No payload for branchCode: " + branchCode);
                return new ArrayList<>();  // ✅ FIXED: Return empty list instead of List.of()
            }

            // ✅ DeviceMasterDTO list
            List<DeviceMasterDTO> deviceList =
                    (List<DeviceMasterDTO>) apiResponse.getPayload();

            if (deviceList == null || deviceList.isEmpty()) return new ArrayList<>();  // ✅ FIXED

            long thresholdHours = getThresholdHoursFromParam();
            LocalDateTime now   = LocalDateTime.now();

            List<Map<String, Object>> notSyncedList = new ArrayList<>();

            for (DeviceMasterDTO device : deviceList) {

                String serialNo = device.getDeviceSerialNo();
                if (serialNo == null) continue;

                List<String> lastSyncedTimes = device.getLastSyncedTimes();

                // Latest sync time from lastSyncedTimes list
                LocalDateTime latestSyncTime = getLatestSyncTime(lastSyncedTimes);

                if (latestSyncTime == null) {
                    System.out.println("Device never synced: " + serialNo);
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("deviceSerialNo", serialNo);
                    entry.put("notSyncedSince", now.format(FORMATTER)); // ✅ current time
                    notSyncedList.add(entry);
                    continue;
                }
                long hoursDiff = ChronoUnit.HOURS.between(latestSyncTime, now);

                System.out.println("Device: " + serialNo
                        + " | LatestSync: " + latestSyncTime
                        + " | Diff: " + hoursDiff + "h"
                        + " | Threshold: " + thresholdHours + "h");

                if (hoursDiff >= thresholdHours) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("deviceSerialNo", serialNo);
                    entry.put("notSyncedSince", latestSyncTime.format(FORMATTER));
                    notSyncedList.add(entry);
                    System.out.println("NOT SYNCED → device: " + serialNo
                            + " | since: " + latestSyncTime);
                }
            }

            System.out.println("Not-synced count for branch ["
                    + branchCode + "]: " + notSyncedList.size());
            return notSyncedList;

        } catch (Exception e) {
            System.out.println("Error fetching devices for branch ["
                    + branchCode + "]: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();  // ✅ FIXED: Return empty list instead of List.of()
    }

    // ================================================================
    // PROCESS — Insert new OR update existing open entry
    // Logic:
    //   - Open entry nahi hai → fresh INSERT
    //   - Open entry hai + naya time > saved start → close old + INSERT new
    //   - Open entry hai + same time → skip
    // ================================================================
    private void processNotSyncedDevices(List<Map<String, Object>> notSyncedDevices) {

        if (notSyncedDevices == null || notSyncedDevices.isEmpty()) return;

        for (Map<String, Object> device : notSyncedDevices) {

            String serialNo = (String) device.get("deviceSerialNo");
            String startStr = (String) device.get("notSyncedSince");

            if (serialNo == null) continue;

            // ✅ Current latest time from lastSyncedTimes
            LocalDateTime currentLatestTime = parseDateTime(startStr);

            // Check open entry in DB
            Optional<NonSyncLogEntity> existingOpen =
                    nonSyncLogRepository
                            .findByPanelSerialNoAndNonSyncEndDatetimeIsNull(serialNo);

            if (existingOpen.isPresent()) {

                NonSyncLogEntity existingEntry = existingOpen.get();
                LocalDateTime savedStartTime  = existingEntry.getNonSyncStartDatetime();

                if (currentLatestTime != null
                        && savedStartTime != null
                        && currentLatestTime.isAfter(savedStartTime)) {

                    // ✅ Naya time aaya — SMS/Heartbeat se exact end time nikalo
                    LocalDateTime endDatetime =
                            getNextSmsDatetime(serialNo, savedStartTime);

                    if (endDatetime == null) {
                        endDatetime = getNextHeartbeatDatetime(serialNo, savedStartTime);
                    }

                    // ✅ Agar SMS/Heartbeat mein bhi nahi mila toh currentLatestTime use karo
                    if (endDatetime == null) {
                        endDatetime = currentLatestTime;
                        System.out.println("No SMS/Heartbeat record found, "
                                + "using currentLatestTime as endDatetime for: " + serialNo);
                    }

                    // Purani entry close karo
                    existingEntry.setNonSyncEndDatetime(endDatetime);
                    nonSyncLogRepository.save(existingEntry);
                    System.out.println("Closed old entry → device: " + serialNo
                            + " | start: " + savedStartTime
                            + " | end: "   + endDatetime);

                    // Nayi open entry insert karo with new start time
                    NonSyncLogEntity newLog = new NonSyncLogEntity(
                            serialNo,
                            currentLatestTime,
                            null
                    );
                    nonSyncLogRepository.save(newLog);
                    System.out.println("Inserted new open entry → device: " + serialNo
                            + " | newStart: " + currentLatestTime);

                } else {
                    // Same time — kuch nahi karna
                    System.out.println("Same sync time, no update needed for: " + serialNo);
                }

            } else {

            	if (currentLatestTime != null) {
            	    NonSyncLogEntity newLog = new NonSyncLogEntity(
            	            serialNo,
            	            currentLatestTime,
            	            null
            	    );
            	    nonSyncLogRepository.save(newLog);
            	    System.out.println("Inserted non-sync log → device: " + serialNo
            	            + " | start: " + currentLatestTime);
            	} else {
            	    //  Fallback — never synced device ke liye current time use karo
            	    NonSyncLogEntity newLog = new NonSyncLogEntity(
            	            serialNo,
            	            LocalDateTime.now(),
            	            null
            	    );
            	    nonSyncLogRepository.save(newLog);
            	    System.out.println("Inserted never-synced device → device: " + serialNo
            	            + " | start: now");
            	}
            }
        }
    }

    // ================================================================
    // CLOSE — Devices jo ab not-synced list mein NAHI hain
    // SMS ya Heartbeat se next time nikalo → endDatetime mein daalo
    // ================================================================
    private void closeResolvedEntries(List<Map<String, Object>> currentNotSyncedDevices) {

        // Current run mein jo abhi bhi not-synced hain
        Set<String> currentNotSyncedSerials = new HashSet<>();
        if (currentNotSyncedDevices != null) {
            for (Map<String, Object> d : currentNotSyncedDevices) {
                String serial = (String) d.get("deviceSerialNo");
                if (serial != null) currentNotSyncedSerials.add(serial);
            }
        }

        // ✅ FIXED: Replaced .toList() with .collect(Collectors.toList())
        List<NonSyncLogEntity> openEntries = nonSyncLogRepository.findAll()
                .stream()
                .filter(e -> e.getNonSyncEndDatetime() == null)
                .collect(Collectors.toList());  // ✅ THIS WAS LINE 359 - FIXED

        if (openEntries.isEmpty()) {
            System.out.println("No open entries to close.");
            return;
        }

        for (NonSyncLogEntity openEntry : openEntries) {

            String serialNo = openEntry.getPanelSerialNo();

            // Abhi bhi not-synced → processNotSyncedDevices handle karega
            if (currentNotSyncedSerials.contains(serialNo)) continue;

            LocalDateTime startDatetime = openEntry.getNonSyncStartDatetime();

            if (startDatetime == null) {
                System.out.println("Start datetime null for: " + serialNo + " — skipping.");
                continue;
            }

            // ✅ SMS table se next record ka time nikalo
            LocalDateTime endDatetime = getNextSmsDatetime(serialNo, startDatetime);

            // ✅ SMS mein nahi mila → Heartbeat check karo
            if (endDatetime == null) {
                endDatetime = getNextHeartbeatDatetime(serialNo, startDatetime);
            }

            if (endDatetime != null) {
                openEntry.setNonSyncEndDatetime(endDatetime);
                nonSyncLogRepository.save(openEntry);
                System.out.println("Closed resolved entry → device: " + serialNo
                        + " | start: " + startDatetime
                        + " | end: "   + endDatetime);
            } else {
                System.out.println("No next SMS/Heartbeat record yet for: "
                        + serialNo + " — will retry next run.");
            }
        }
    }

    // ================================================================
    // SMS TABLE — Next datetime after startDatetime
    // Query: SELECT datetime FROM sms_details
    //        WHERE panelserialno = ? AND datetime > ? LIMIT 1
    // ================================================================
    private LocalDateTime getNextSmsDatetime(String serialNo,
                                              LocalDateTime afterDatetime) {
        try {
            LocalDateTime result = smsDetailsRepository
                    .findNextSmsDatetimeAfter(serialNo, afterDatetime);

            if (result != null) {
                System.out.println("SMS next record → device: " + serialNo
                        + " | time: " + result);
            }
            return result;

        } catch (Exception e) {
            System.out.println("Error fetching next SMS for ["
                    + serialNo + "]: " + e.getMessage());
        }
        return null;
    }

    // ================================================================
    // HEARTBEAT TABLE — Next datetime after startDatetime
    // Query: SELECT heartbeat_datetime FROM heartbeat
    //        WHERE panel_serial_number = ? AND heartbeat_datetime > ? LIMIT 1
    // ================================================================
    private LocalDateTime getNextHeartbeatDatetime(String serialNo,
                                                    LocalDateTime afterDatetime) {
        try {
            LocalDateTime result = heartbeatRepository
                    .findNextHeartbeatDatetimeAfter(serialNo, afterDatetime);

            if (result != null) {
                System.out.println("Heartbeat next record → device: " + serialNo
                        + " | time: " + result);
            }
            return result;

        } catch (Exception e) {
            System.out.println("Error fetching next Heartbeat for ["
                    + serialNo + "]: " + e.getMessage());
        }
        return null;
    }

    // ================================================================
    // LATEST SYNC TIME from lastSyncedTimes list
    // Input format: "30-03-2026 15:59:13 : GPRS"
    // ================================================================
    private LocalDateTime getLatestSyncTime(List<String> lastSyncedTimes) {

        if (lastSyncedTimes == null || lastSyncedTimes.isEmpty()) return null;

        LocalDateTime latest = null;

        for (String syncTimeStr : lastSyncedTimes) {
            if (syncTimeStr == null ) continue;
            try {
                // "30-03-2026 15:59:13 : GPRS" → "30-03-2026 15:59:13"
                String datePart = syncTimeStr.contains(" : ")
                        ? syncTimeStr.substring(0,
                                syncTimeStr.lastIndexOf(" : ")).trim()
                        : syncTimeStr.trim();

                LocalDateTime parsed = LocalDateTime.parse(datePart, FORMATTER);
                if (latest == null || parsed.isAfter(latest)) latest = parsed;

            } catch (Exception e) {
                System.out.println("Error parsing sync time: [" + syncTimeStr + "]");
            }
        }
        return latest;
    }

    // ================================================================
    // UTILITY
    // ================================================================
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null 
                || dateTimeStr.equalsIgnoreCase("Never Synced")) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, FORMATTER);
        } catch (Exception e) {
            System.out.println("Date parse error: [" + dateTimeStr + "]");
            return null;
        }
    }
}