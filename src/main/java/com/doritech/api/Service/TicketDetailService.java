package com.doritech.api.Service;

import com.doritech.api.DTO.ChannelLogDTO;
import com.doritech.api.DTO.EscalationLevelDTO;
import com.doritech.api.DTO.TicketDetailDTO;

import com.doritech.api.Entity.*;
import com.doritech.api.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketDetailService {

    private static final Logger log = LoggerFactory.getLogger(TicketDetailService.class);

    @Autowired
    SmsDetailsHistoryRepository smsDetailsHistoryRepository;
    // "12 FEB 26, 10:23 AM"
    private static final DateTimeFormatter DT_FMT =
    	    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final TicketRepository              ticketRepository;
    private final SmsDetailsRepository          smsDetailsRepository;
    private final DeviceMasterRepository        deviceMasterRepository;
    private final DeviceZoneDetailsRepository   deviceZoneDetailsRepository;
    private final BranchMasterRepository        branchMasterRepository;
    private final EscalationMailLogRepository   escalationLogRepository;
    private final EscalationMatrixRepository    escalationMatrixRepository;
    private final EmailLogRepo                  emailLogRepository;
    private final SmsLogRepo             smsLogRepository;
    private final CallLogRepository             callLogRepository;
    private final WhatsappLogsRepository         whatsAppLogRepository;
    private final BranchMasterService           branchMasterService;
    
    public TicketDetailService(
            TicketRepository            ticketRepository,
            SmsDetailsRepository        smsDetailsRepository,
            DeviceMasterRepository      deviceMasterRepository,
            DeviceZoneDetailsRepository deviceZoneDetailsRepository,
            BranchMasterRepository      branchMasterRepository,
            EscalationMailLogRepository escalationLogRepository,
            EscalationMatrixRepository  escalationMatrixRepository,
            EmailLogRepo                emailLogRepository,
            SmsLogRepo           smsLogRepository,
            CallLogRepository           callLogRepository,
            WhatsappLogsRepository       whatsAppLogRepository,
            BranchMasterService           branchMasterService) {

        this.ticketRepository            = ticketRepository;
        this.smsDetailsRepository        = smsDetailsRepository;
        this.deviceMasterRepository      = deviceMasterRepository;
        this.deviceZoneDetailsRepository = deviceZoneDetailsRepository;
        this.branchMasterRepository      = branchMasterRepository;
        this.escalationLogRepository     = escalationLogRepository;
        this.escalationMatrixRepository  = escalationMatrixRepository;
        this.emailLogRepository          = emailLogRepository;
        this.smsLogRepository            = smsLogRepository;
        this.callLogRepository           = callLogRepository;
        this.whatsAppLogRepository       = whatsAppLogRepository;
        this.branchMasterService         = branchMasterService;
    }

    // =========================================================================
    //  MAIN — Get full ticket detail by ticketNo
    // =========================================================================
    public TicketDetailDTO getTicketDetail(String ticketNo) {
        log.info("[DETAIL] Building detail for ticket='{}'", ticketNo);

        TicketDetailDTO dto = new TicketDetailDTO();
        dto.setTicketNo(ticketNo);

        // ── 1. Open ticket row ────────────────────────────────────────────────
        List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
        if (allRows.isEmpty()) {
            log.warn("[DETAIL] No rows for ticket '{}'", ticketNo);
            return null;
        }

        Ticket openRow = allRows.stream()
            .filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
            .filter(r -> r.getResolvedType() == null)
            .findFirst()
            .orElse(allRows.get(0));

     // ── 2. sms_details → firstInfoDatetime, panelSerialNo ────────────────
        String panelSerialNo = null;
        LocalDateTime alertDateTime = null;

        try {
            Optional<SmsDetailsEntity> smsOpt = smsDetailsRepository
                    .findById(Math.toIntExact(openRow.getAlertGeneratedId())); // ✅ findById, not getById

            if (smsOpt.isPresent()) {
                SmsDetailsEntity sms = smsOpt.get();
                if (sms.getDateTime() != null) {
                    panelSerialNo = sms.getPanelSerialNo();
                    alertDateTime = sms.getDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String formatted = alertDateTime
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                            .toUpperCase();

                    dto.setFirstInfoDatetime(formatted);
                    dto.setAlertTime(formatted);
                }
            }  else {
                Optional<SmsDetailsHistoryEntity> histOpt = smsDetailsHistoryRepository
                        .findByOriginalId(Math.toIntExact(openRow.getAlertGeneratedId()));

                if (histOpt.isPresent() && histOpt.get().getDateTime() != null) {
                    SmsDetailsHistoryEntity sms2 = histOpt.get();
                    panelSerialNo = sms2.getPanelSerialNo();         
                    alertDateTime = sms2.getDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();                        

                    String formatted = alertDateTime
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                            .toUpperCase();

                    dto.setFirstInfoDatetime(formatted);
                    dto.setAlertTime(formatted);
                }
            }
        } catch (Exception e) {
            log.warn("[DETAIL] sms lookup failed: {}", e.getMessage());
        }
        // 🔥 CALCULATE DURATION
        if (alertDateTime != null) {

            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(alertDateTime, now);

            long totalMinutes = duration.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;

            String durationStr;

            if (days > 0) {
                durationStr = days + " Days " + hours + " Hours " + minutes + " Min";
            } else if (hours > 0) {
                durationStr = hours + " Hours " + minutes + " Min";
            } else {
                durationStr = minutes + " Min";
            }

            // dto.setDuration(durationStr);
            dto.setDuration("O".equalsIgnoreCase(openRow.getTicketStatus())?durationStr:"N/A");
       } else {
           dto.setDuration("N/A");
       }


        // ── 3. device_master → product, branchCode ───────────────────────────
        String branchCode  = null;
        String productType = "N/A";
        DeviceMasterEntity device = null;
        if (panelSerialNo != null) {
            try {
                device = deviceMasterRepository.getByDeviceSerialNo(panelSerialNo);
                if (device != null) {
                    branchCode  = device.getBranchCode();
                    productType = device.getProductType() != null
                        ? device.getProductType() : "N/A";
                }
            } catch (Exception e) {
                log.warn("[DETAIL] device lookup failed: {}", e.getMessage());
            }
        }
        dto.setProduct(productType);
        dto.setBranchCode(branchCode != null ? branchCode : "N/A");

        // ── 4. branch_master → branchName, vendorName ───────────────────────
        if (branchCode != null) {
            try {
                BranchMasterEntity branch =
                    branchMasterRepository.findBybranchCode(branchCode);
                if (branch != null) {
                    dto.setBranchName(branch.getBranchDesc() != null
                        ? branch.getBranchDesc() : "N/A");
                    dto.setVendorName(branch.getContractPerson() != null
                        ? branch.getContractPerson() : "N/A");
                }
            } catch (Exception e) {
                log.warn("[DETAIL] branch lookup failed: {}", e.getMessage());
            }
        }

     // ── 5. device_zone_details → alertType (collect from ALL zones) ──────────────
        if (device != null) {
            try {
                // Collect alert types from ALL rows of this ticket (all zones)
                Set<String> alertTypesSet = new LinkedHashSet<>();
                String vendorFromZone = null;
                
                for (Ticket ticketRow : allRows) {
                    String zoneNumber = "ZONE" + ticketRow.getZone();
                    DeviceZoneDetailsEntity zoneDetail =
                        deviceZoneDetailsRepository.findByDeviceIdAndZoneOriginalName(
                            device.getDeviceId(), zoneNumber);
                    if (zoneDetail != null) {
                        // Collect alert type
                        if (zoneDetail.getAlertType() != null) {
                            alertTypesSet.add(zoneDetail.getAlertType());
                        }
                        
                        // Get vendor name (from first zone that has it)
                        if (vendorFromZone == null && zoneDetail.getVendors() != null) {
                            vendorFromZone = zoneDetail.getVendors();
                        }
                    }
                }
                
                // Set alert type - join multiple types with comma
                String alertTypeValue = alertTypesSet.isEmpty() ? "N/A" : 
                    String.join(", ", alertTypesSet);
                dto.setAlertType(alertTypeValue);
                
                // Set vendor name if found from any zone
                if (vendorFromZone != null) {
                    dto.setVendorName(vendorFromZone);
                }
                
                if (alertTypesSet.size() > 1) {
                    log.info("[DETAIL] Ticket '{}' has multiple zones ({} zones): alert types = {}", 
                        ticketNo, allRows.size(), alertTypeValue);
                }
            } catch (Exception e) {
                log.warn("[DETAIL] zone lookup failed: {}", e.getMessage());
            }
        }
        
     // ── 6. escalation_log → all logs for this ticket ─────────────────────
        List<EscalationLog> allEscLogs = new ArrayList<>();
        try {
            allEscLogs = escalationLogRepository
                .findByTicketNoOrderBySentAtDesc(ticketNo);
        } catch (Exception e) {
            log.warn("[DETAIL] escalation_log lookup failed: {}", e.getMessage());
        }

        // Current status — latest esc_level
        String currentEscLevel = allEscLogs.isEmpty()
            ? null : allEscLogs.get(0).getEscLevel();

        dto.setCurrentStatus(currentEscLevel != null
            ? currentEscLevel + " ESCALATED" : "VENDOR ASSIGNED");

        // Last reminder datetime (latest escalation log)
        if (!allEscLogs.isEmpty() && allEscLogs.get(0).getSentAt() != null) {
            dto.setLastReminderDatetime(
                allEscLogs.get(0).getSentAt().format(DT_FMT).toUpperCase());
        }

        // ✅ NEW: Build reminderSentOn list (L1 reminders with criticalFlag=0 and reminder_index>0)
        List<String> reminderSentOn = new ArrayList<>();
        if (!allEscLogs.isEmpty()) {
            // Sort ascending by sent_at for proper serial numbering
            List<EscalationLog> escLogsAsc = new ArrayList<>(allEscLogs);
            Collections.reverse(escLogsAsc);
            
            int reminderSerial = 1;
            for (EscalationLog el : escLogsAsc) {
                // Only L1 entries, criticalFlag=false, reminderIndex > 0
                if ("L1".equalsIgnoreCase(el.getEscLevel()) 
                        && !el.isCriticalFlag() 
                        && el.getReminderIndex() > 0) {
                    
                    if (el.getSentAt() != null) {
                        String formatted = el.getSentAt()
                                .format(DateTimeFormatter.ofPattern("dd MMM yy, hh:mm a"))
                                .toUpperCase();
                        reminderSentOn.add(reminderSerial + ". " + formatted);
                        reminderSerial++;
                    }
                }
            }
            log.info("[DETAIL] ReminderSentOn built for '{}': {} entries", ticketNo, reminderSentOn.size());
        }
        dto.setReminderSentOn(reminderSentOn.isEmpty() ? null : reminderSentOn);

        // ── 7. Collect all escalation_log IDs ────────────────────────────────
        List<Long> allEscLogIds = allEscLogs.stream()
            .map(EscalationLog::getId)
            .collect(Collectors.toList());
        // Output: 19 FEB 26, 02:35 PM

        
        // ── 8. Fetch ALL channel logs in bulk ─────────────────────────────────
        List<EmailLogEntity>  allEmails    = safeList(() ->
            emailLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<SmsLogEntity>    allSmsList   = safeList(() ->
            smsLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<CallLogEntity>   allCalls     = safeList(() ->
            callLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<WhatsAppLogs>    allWhatsApps = safeList(() ->
            whatsAppLogRepository.findByEscalationLogIdIn(allEscLogIds));

        // Total reminders = unique escalation_log_ids that have at least one email
        long totalReminders = allEscLogs.stream()
            .filter(el -> allEmails.stream()
                .anyMatch(e -> el.getId().equals(e.getEscalationLogId())))
            .count();
        dto.setTotalRemindersSent((int) totalReminders);

        // ── 9. Group logs by esc_level → build EscalationLevelDTO list ────────
        // Group escalation_logs by level
        Map<String, List<EscalationLog>> logsByLevel = allEscLogs.stream()
            .collect(Collectors.groupingBy(EscalationLog::getEscLevel));

        // Level order: L1 → L2 → L3
        List<String> levelOrder = Arrays.asList("L1", "L2", "L3");
        List<EscalationLevelDTO> levelDTOs = new ArrayList<>();

        for (String level : levelOrder) {
            List<EscalationLog> logsForLevel = logsByLevel.getOrDefault(level, List.of());
            if (logsForLevel.isEmpty()) continue;

            // Get IDs for this level
            List<Long> levelLogIds = logsForLevel.stream()
                .map(EscalationLog::getId)
                .collect(Collectors.toList());

            // Recipient label from escalation_matrix
            String recipientLabel = level;
            try {
                EscalationMatrix matrix = escalationMatrixRepository.findByEscLevel(level);
                if (matrix != null && matrix.getRecipient() != null) {
                    recipientLabel = matrix.getRecipient(); // e.g., VENDOR / ZONE / HEAD_OFFICE
                }
            } catch (Exception ignored) {}

            EscalationLevelDTO levelDTO = new EscalationLevelDTO();
            levelDTO.setLevel(level);
            levelDTO.setRecipient(recipientLabel);

            // ── Email logs for this level ─────────────────────────────────────
            levelDTO.setEmail(
                allEmails.stream()
                    .filter(e -> levelLogIds.contains(e.getEscalationLogId()))
                    .map(e -> {
                        ChannelLogDTO c = new ChannelLogDTO();
                        c.setRecipient(e.getEmailIdTo());
                        c.setTimestamp(formatDateTime(e.getDatetime()));
                        c.setStatus("S".equals(e.getStatus()) ? "DELIVERED" : "FAILED");
                        return c;
                    })
                    .collect(Collectors.toList())
            );

            // ── SMS logs for this level ───────────────────────────────────────
            levelDTO.setSms(
                allSmsList.stream()
                    .filter(s -> levelLogIds.contains(s.getEscalationLogId()))
                    .map(s -> {
                        ChannelLogDTO c = new ChannelLogDTO();
                        c.setRecipient(s.getSentTo());
                        c.setTimestamp(formatDate(s.getDatetime()));
                        c.setStatus("S".equals(s.getStatus()) ? "DELIVERED" : "FAILED");
                        return c;
                    })
                    .collect(Collectors.toList())
            );

            // ── Call logs for this level ──────────────────────────────────────
            levelDTO.setCall(
                allCalls.stream()
                    .filter(cl -> levelLogIds.contains(cl.getEscalationLogId()))
                    .map(cl -> {
                        ChannelLogDTO c = new ChannelLogDTO();
                        c.setRecipient(cl.getPhoneNo());
                        c.setTimestamp(formatDateTime(cl.getCreatedAt()));
                        c.setStatus("DELIVERED");
                        return c;
                    })
                    .collect(Collectors.toList())
            );

            // ── WhatsApp logs for this level ──────────────────────────────────
            levelDTO.setWhatsapp(
                allWhatsApps.stream()
                    .filter(w -> levelLogIds.contains(w.getEscalationLogId()))
                    .map(w -> {
                        ChannelLogDTO c = new ChannelLogDTO();
                        c.setRecipient(w.getPhoneNo());
                        c.setTimestamp(formatDate(w.getTimestamp()));
                        c.setStatus("S".equals(w.getStatus()) ? "DELIVERED" : "FAILED");
                        return c;
                    })
                    .collect(Collectors.toList())
            );

            levelDTOs.add(levelDTO);
        }

        dto.setEscalationLevels(levelDTOs);

        log.info("[DETAIL] Done for '{}': levels={}, reminders={}",
            ticketNo, levelDTOs.size(), dto.getTotalRemindersSent());

        return dto;
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================
    private <T> List<T> safeList(java.util.function.Supplier<List<T>> supplier) {
        try { return supplier.get(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    private String formatDate(Date date) {
        if (date == null) return "N/A";
        return date.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime().format(DT_FMT).toUpperCase();
    }

    private String formatDateTime(java.time.LocalDateTime ldt) {
        if (ldt == null) return "N/A";
        return ldt.format(DT_FMT).toUpperCase();
    }
    
    
    
    
    public Object getTicketDetail(String ticketNo, String branchCode) {
        
        // ✅ If ticketNo is null/empty, return all tickets for the branch hierarchy
        if (ticketNo == null || ticketNo.isBlank()) {
            log.info("[DETAIL] No ticketNo provided, fetching ALL tickets for branchCode='{}'", branchCode);
            return getAllTicketDetails(branchCode);
        }
        
        // Otherwise, return single ticket detail
        log.info("[DETAIL] Building detail for ticket='{}'", ticketNo);
        return buildSingleTicketDetail(ticketNo);
    }

    // ✅ Method to get ALL ticket details with branch hierarchy
    private List<TicketDetailDTO> getAllTicketDetails(String branchCode) {
        log.info("[DETAIL] Fetching ALL ticket details for branchCode='{}'", branchCode);
        
        List<TicketDetailDTO> allTicketDetails = new ArrayList<>();
        
        try {
            // STEP 1: Get all child branches
            ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
            List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
                    ? (List<BranchMasterEntity>) responseEntity.getPayload()
                    : null;

            if (branchList == null || branchList.isEmpty()) {
                log.warn("[DETAIL] No child branches found for branchCode='{}'", branchCode);
                return allTicketDetails;
            }
            
            // Extract all branch codes as List<String>
            List<String> allBranchCodes = branchList.stream()
                    .map(BranchMasterEntity::getBranchCode)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            
            log.info("[DETAIL] Total branch codes found: {}", allBranchCodes.size());
            log.info("[DETAIL] Branch codes: {}", allBranchCodes);
            
            // STEP 2: Get all devices for all branches in ONE query using List<String>
            List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(allBranchCodes);
            log.info("[DETAIL] Total devices found: {}", allDevices.size());
            
            if (allDevices.isEmpty()) {
                log.warn("[DETAIL] No devices found for branches");
                return allTicketDetails;
            }
            
            // STEP 3: Extract all panelSerialNos
            Set<String> allPanelSerialNos = allDevices.stream()
                    .map(DeviceMasterEntity::getDeviceSerialNo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            log.info("[DETAIL] Total panel serial numbers: {}", allPanelSerialNos.size());
            
            if (allPanelSerialNos.isEmpty()) {
                log.warn("[DETAIL] No panel serial numbers found");
                return allTicketDetails;
            }
            
            // STEP 4: Get all tickets for these panel serial numbers
            List<Ticket> allTickets = ticketRepository.findAllTicketsBydeviceSerialNos(allPanelSerialNos);
            log.info("[DETAIL] Total tickets found: {}", allTickets.size());
            
            if (allTickets.isEmpty()) {
                log.warn("[DETAIL] No tickets found for panel serial numbers");
                return allTicketDetails;
            }
            
            // STEP 5: Extract distinct ticket numbers
            Set<String> distinctTicketNos = allTickets.stream()
                    .map(Ticket::getTicketNo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            log.info("[DETAIL] Distinct ticket numbers found: {}", distinctTicketNos.size());
            
            // STEP 6: Build TicketDetailDTO for each ticket
            for (String tNo : distinctTicketNos) {
                try {
                    TicketDetailDTO detail = buildSingleTicketDetail(tNo);
                    if (detail != null) {
                        allTicketDetails.add(detail);
                        log.info("[DETAIL] Successfully built detail for ticket='{}'", tNo);
                    }
                } catch (Exception e) {
                    log.warn("[DETAIL] Failed to build detail for ticket='{}': {}", tNo, e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("[DETAIL] Failed to fetch all ticket details: {}", e.getMessage(), e);
        }
        
        log.info("[DETAIL] Total ticket details built: {}", allTicketDetails.size());
        return allTicketDetails;
    }

    // Original buildSingleTicketDetail method (unchanged)
    private TicketDetailDTO buildSingleTicketDetail(String ticketNo) {
        log.info("[DETAIL] Building detail for ticket='{}'", ticketNo);

        TicketDetailDTO dto = new TicketDetailDTO();
        dto.setTicketNo(ticketNo);

        // ── 1. Open ticket row ────────────────────────────────────────────────
        List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
        if (allRows.isEmpty()) {
            log.warn("[DETAIL] No rows for ticket '{}'", ticketNo);
            return null;
        }

        Ticket openRow = allRows.stream()
            .filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
            .filter(r -> r.getResolvedType() == null)
            .findFirst()
            .orElse(allRows.get(0));

        // ── 2. sms_details → firstInfoDatetime, panelSerialNo ────────────────
        String panelSerialNo = null;
        LocalDateTime alertDateTime = null;

        try {
            Optional<SmsDetailsEntity> smsOpt = smsDetailsRepository
                    .findById(Math.toIntExact(openRow.getAlertGeneratedId()));

            if (smsOpt.isPresent()) {
                SmsDetailsEntity sms = smsOpt.get();
                if (sms.getDateTime() != null) {
                    panelSerialNo = sms.getPanelSerialNo();
                    alertDateTime = sms.getDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String formatted = alertDateTime
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                            .toUpperCase();

                    dto.setFirstInfoDatetime(formatted);
                    dto.setAlertTime(formatted);
                }
            } else {
                Optional<SmsDetailsHistoryEntity> histOpt = smsDetailsHistoryRepository
                        .findByOriginalId(Math.toIntExact(openRow.getAlertGeneratedId()));

                if (histOpt.isPresent() && histOpt.get().getDateTime() != null) {
                    SmsDetailsHistoryEntity sms2 = histOpt.get();
                    panelSerialNo = sms2.getPanelSerialNo();
                    alertDateTime = sms2.getDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String formatted = alertDateTime
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                            .toUpperCase();

                    dto.setFirstInfoDatetime(formatted);
                    dto.setAlertTime(formatted);
                }
            }
        } catch (Exception e) {
            log.warn("[DETAIL] sms lookup failed: {}", e.getMessage());
        }

        // 🔥 CALCULATE DURATION
        if (alertDateTime != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(alertDateTime, now);

            long totalMinutes = duration.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;

            String durationStr;

            if (days > 0) {
                durationStr = days + " Days " + hours + " Hours " + minutes + " Min";
            } else if (hours > 0) {
                durationStr = hours + " Hours " + minutes + " Min";
            } else {
                durationStr = minutes + " Min";
            }

            dto.setDuration(durationStr);
        } else {
            dto.setDuration("N/A");
        }

        // ── 3. device_master → product, branchCode ───────────────────────────
        String branchCodeFromDevice = null;
        String productType = "N/A";
        DeviceMasterEntity device = null;
        if (panelSerialNo != null) {
            try {
                device = deviceMasterRepository.getByDeviceSerialNo(panelSerialNo);
                if (device != null) {
                    branchCodeFromDevice = device.getBranchCode();
                    productType = device.getProductType() != null ? device.getProductType() : "N/A";
                }
            } catch (Exception e) {
                log.warn("[DETAIL] device lookup failed: {}", e.getMessage());
            }
        }
        dto.setProduct(productType);
        dto.setBranchCode(branchCodeFromDevice != null ? branchCodeFromDevice : "N/A");

        // ── 4. branch_master → branchName, vendorName ───────────────────────
        if (branchCodeFromDevice != null) {
            try {
                BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCodeFromDevice);
                if (branch != null) {
                    dto.setBranchName(branch.getBranchDesc() != null ? branch.getBranchDesc() : "N/A");
                    dto.setVendorName(branch.getContractPerson() != null ? branch.getContractPerson() : "N/A");
                }
            } catch (Exception e) {
                log.warn("[DETAIL] branch lookup failed: {}", e.getMessage());
            }
        }

        // ── 5. device_zone_details → alertType and vendorName ──────────────
        if (device != null) {
            try {
                String zoneNumber = "ZONE" + openRow.getZone();
                DeviceZoneDetailsEntity zoneDetail = deviceZoneDetailsRepository
                        .findByDeviceIdAndZoneOriginalName(device.getDeviceId(), zoneNumber);
                if (zoneDetail != null) {
                    dto.setAlertType(zoneDetail.getAlertType() != null ? zoneDetail.getAlertType() : "N/A");

                    // 🔥 GET VENDOR NAME FROM DEVICE_ZONE_DETAILS
                    String vendorFromZone = zoneDetail.getVendors();
                    if (vendorFromZone != null) {
                        dto.setVendorName(vendorFromZone);
                    }
                }
            } catch (Exception e) {
                log.warn("[DETAIL] zone lookup failed: {}", e.getMessage());
            }
        }

        // ── 6. escalation_log → all logs for this ticket ─────────────────────
        List<EscalationLog> allEscLogs = new ArrayList<>();
        try {
            allEscLogs = escalationLogRepository.findByTicketNoOrderBySentAtDesc(ticketNo);
        } catch (Exception e) {
            log.warn("[DETAIL] escalation_log lookup failed: {}", e.getMessage());
        }

        // Current status — latest esc_level
        String currentEscLevel = allEscLogs.isEmpty() ? null : allEscLogs.get(0).getEscLevel();

        dto.setCurrentStatus(currentEscLevel != null ? currentEscLevel + " ESCALATED" : "VENDOR ASSIGNED");

        // Last reminder datetime (latest escalation log)
        if (!allEscLogs.isEmpty() && allEscLogs.get(0).getSentAt() != null) {
            dto.setLastReminderDatetime(
                    allEscLogs.get(0).getSentAt().format(DT_FMT).toUpperCase());
        }

        // ✅ Build reminderSentOn list from escalation_logs
        List<String> reminderSentOn = new ArrayList<>();
        if (!allEscLogs.isEmpty()) {
            // Sort ascending by sent_at for proper serial numbering
            List<EscalationLog> escLogsAsc = new ArrayList<>(allEscLogs);
            Collections.reverse(escLogsAsc);

            int reminderSerial = 1;
            for (EscalationLog el : escLogsAsc) {
                // Only L1 entries, criticalFlag=false, reminderIndex > 0
                if ("L1".equalsIgnoreCase(el.getEscLevel()) 
                        && !el.isCriticalFlag() 
                        && el.getReminderIndex() > 0) {

                    if (el.getSentAt() != null) {
                        String formatted = el.getSentAt()
                                .format(DateTimeFormatter.ofPattern("dd MMM yy, hh:mm a"))
                                .toUpperCase();
                        reminderSentOn.add(reminderSerial + ". " + formatted);
                        reminderSerial++;
                    }
                }
            }
            log.info("[DETAIL] ReminderSentOn built for '{}': {} entries", ticketNo, reminderSentOn.size());
        }
        dto.setReminderSentOn(reminderSentOn.isEmpty() ? null : reminderSentOn);

        // ── 7. Collect all escalation_log IDs ────────────────────────────────
        List<Long> allEscLogIds = allEscLogs.stream()
                .map(EscalationLog::getId)
                .collect(Collectors.toList());

        // ── 8. Fetch ALL channel logs in bulk ─────────────────────────────────
        List<EmailLogEntity> allEmails = safeList(() -> 
                emailLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<SmsLogEntity> allSmsList = safeList(() -> 
                smsLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<CallLogEntity> allCalls = safeList(() -> 
                callLogRepository.findByEscalationLogIdIn(allEscLogIds));
        List<WhatsAppLogs> allWhatsApps = safeList(() -> 
                whatsAppLogRepository.findByEscalationLogIdIn(allEscLogIds));

        // Total reminders = unique escalation_log_ids that have at least one email
        long totalReminders = allEscLogs.stream()
                .filter(el -> allEmails.stream()
                        .anyMatch(e -> el.getId().equals(e.getEscalationLogId())))
                .count();
        dto.setTotalRemindersSent((int) totalReminders);

        // ── 9. Group logs by esc_level → build EscalationLevelDTO list ────────
        Map<String, List<EscalationLog>> logsByLevel = allEscLogs.stream()
                .collect(Collectors.groupingBy(EscalationLog::getEscLevel));

        // Level order: L1 → L2 → L3
        List<String> levelOrder = Arrays.asList("L1", "L2", "L3");
        List<EscalationLevelDTO> levelDTOs = new ArrayList<>();

        for (String level : levelOrder) {
            List<EscalationLog> logsForLevel = logsByLevel.getOrDefault(level, List.of());
            if (logsForLevel.isEmpty())
                continue;

            // Get IDs for this level
            List<Long> levelLogIds = logsForLevel.stream()
                    .map(EscalationLog::getId)
                    .collect(Collectors.toList());

            // Recipient label from escalation_matrix
            String recipientLabel = level;
            try {
                EscalationMatrix matrix = escalationMatrixRepository.findByEscLevel(level);
                if (matrix != null && matrix.getRecipient() != null) {
                    recipientLabel = matrix.getRecipient();
                }
            } catch (Exception ignored) {
            }

            EscalationLevelDTO levelDTO = new EscalationLevelDTO();
            levelDTO.setLevel(level);
            levelDTO.setRecipient(recipientLabel);

            // ── Email logs for this level ─────────────────────────────────────
            levelDTO.setEmail(
                    allEmails.stream()
                            .filter(e -> levelLogIds.contains(e.getEscalationLogId()))
                            .map(e -> {
                                ChannelLogDTO c = new ChannelLogDTO();
                                c.setRecipient(e.getEmailIdTo());
                                c.setTimestamp(formatDateTime(e.getDatetime()));
                                c.setStatus("S".equals(e.getStatus()) ? "DELIVERED" : "FAILED");
                                return c;
                            })
                            .collect(Collectors.toList()));

            // ── SMS logs for this level ───────────────────────────────────────
            levelDTO.setSms(
                    allSmsList.stream()
                            .filter(s -> levelLogIds.contains(s.getEscalationLogId()))
                            .map(s -> {
                                ChannelLogDTO c = new ChannelLogDTO();
                                c.setRecipient(s.getSentTo());
                                c.setTimestamp(formatDate(s.getDatetime()));
                                c.setStatus("S".equals(s.getStatus()) ? "DELIVERED" : "FAILED");
                                return c;
                            })
                            .collect(Collectors.toList()));

            // ── Call logs for this level ──────────────────────────────────────
            levelDTO.setCall(
                    allCalls.stream()
                            .filter(cl -> levelLogIds.contains(cl.getEscalationLogId()))
                            .map(cl -> {
                                ChannelLogDTO c = new ChannelLogDTO();
                                c.setRecipient(cl.getPhoneNo());
                                c.setTimestamp(formatDateTime(cl.getCreatedAt()));
                                c.setStatus("DELIVERED");
                                return c;
                            })
                            .collect(Collectors.toList()));

            // ── WhatsApp logs for this level ──────────────────────────────────
            levelDTO.setWhatsapp(
                    allWhatsApps.stream()
                            .filter(w -> levelLogIds.contains(w.getEscalationLogId()))
                            .map(w -> {
                                ChannelLogDTO c = new ChannelLogDTO();
                                c.setRecipient(w.getPhoneNo());
                                c.setTimestamp(formatDate(w.getTimestamp()));
                                c.setStatus("S".equals(w.getStatus()) ? "DELIVERED" : "FAILED");
                                return c;
                            })
                            .collect(Collectors.toList()));

            levelDTOs.add(levelDTO);
        }

        dto.setEscalationLevels(levelDTOs);

        log.info("[DETAIL] Done for '{}': levels={}, reminders={}",
                ticketNo, levelDTOs.size(), dto.getTotalRemindersSent());

        return dto;
    }

   
}