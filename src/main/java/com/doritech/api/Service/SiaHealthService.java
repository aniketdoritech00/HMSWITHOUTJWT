package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.SiaHealthResponseDTO;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SiaPhysicalZoneMaster;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.SiaPhysicalZoneMasterRepository;

@Service
public class SiaHealthService {

    Logger log = LoggerFactory.getLogger(SiaHealthService.class);

    // Precompiled once — was being compiled on every call before.
    private static final Pattern ZONE_NUMBER_PATTERN = Pattern.compile("\\d+");

    @Autowired
    private SiaPhysicalZoneMasterRepository zoneMasterRepository;

    @Autowired
    private BranchMasterService branchMasterService;

    @Autowired
    private BranchMasterRepository branchMasterRepository;

    @Autowired
    private BankMasterRepository bankMasterRepository;

    @Autowired
    private EntityManager entityManager;

    // NOTE: removed the instance-level zoneMappingCache field.
    // It was a shared mutable Map on a singleton @Service bean —
    // concurrent requests from different branches could read/overwrite
    // each other's cached zone names. Zone mapping is now built once
    // per request call as a local variable (see buildZoneNameMappings).

    /**
     * Fetches zone name mappings for MULTIPLE sia device ids in a single query,
     * instead of one query per device id (which was the previous N+1 behavior).
     */
    private Map<Long, Map<String, String>> buildZoneNameMappings(List<Long> siaDeviceIds) {

        Map<Long, Map<String, String>> result = new HashMap<>();

        if (siaDeviceIds == null || siaDeviceIds.isEmpty()) {
            return result;
        }

        try {

            List<SiaPhysicalZoneMaster> zones =
                    zoneMasterRepository.findBySiaDeviceIdInAndStatus(
                            siaDeviceIds,
                            "A");

            if (zones == null || zones.isEmpty()) {
                return result;
            }

            for (SiaPhysicalZoneMaster z : zones) {

                Long siaDeviceId = z.getSiaDeviceId();

                if (siaDeviceId == null) {
                    continue;
                }

                String numericZone = extractZoneNumber(z.getZone());

                if (numericZone == null || numericZone.isEmpty()) {
                    continue;
                }

                String value = z.getZoneName();

                if (value == null || value.trim().isEmpty()) {
                    continue;
                }

                result.computeIfAbsent(siaDeviceId, k -> new HashMap<>())
                        .put("ZONE_" + numericZone, value);
            }

        } catch (Exception e) {

            log.error(
                    "Error fetching zone mapping for siaDeviceIds: {}",
                    siaDeviceIds,
                    e);
        }

        return result;
    }

    private String extractZoneNumber(String zoneValue) {

        if (zoneValue == null || zoneValue.isEmpty()) {
            return "";
        }

        Matcher matcher = ZONE_NUMBER_PATTERN.matcher(zoneValue);

        if (matcher.find()) {
            return matcher.group();
        }

        return zoneValue;
    }

    public List<SiaHealthResponseDTO> getSiaHealthWithZoneNames(
            String loggedInBranchCode) {

        log.info(
                "Starting SIA health data for branchCode: {}",
                loggedInBranchCode);

        long startTime = System.currentTimeMillis();

        List<SiaHealthResponseDTO> finalResponse = new ArrayList<>();

        try {

            ResponseEntity responseEntity =
                    branchMasterService.getAllChildren(loggedInBranchCode);

            @SuppressWarnings("unchecked")
            List<BranchMasterEntity> branchList =
                    responseEntity.getPayload() != null
                            ? (List<BranchMasterEntity>) responseEntity.getPayload()
                            : null;

            if (branchList == null || branchList.isEmpty()) {
                return finalResponse;
            }

            Map<String, BranchMasterEntity> branchEntityMap = new LinkedHashMap<>();

            for (BranchMasterEntity branch : branchList) {
                branchEntityMap.put(branch.getBranchCode(), branch);
            }

            List<String> branchCodes = new ArrayList<>(branchEntityMap.keySet());

            if (branchCodes.isEmpty()) {
                return finalResponse;
            }

            String deviceMasterSql =
                    "SELECT dm.device_id, dm.device_serial_no, dm.branch_code " +
                    "FROM device_master dm " +
                    "WHERE dm.branch_code IN (:branchCodes)";

            Query deviceQuery = entityManager.createNativeQuery(deviceMasterSql);
            deviceQuery.setParameter("branchCodes", branchCodes);

            @SuppressWarnings("unchecked")
            List<Object[]> deviceRows = deviceQuery.getResultList();

            if (deviceRows == null || deviceRows.isEmpty()) {
                return finalResponse;
            }

            Map<String, String> deviceIdToSerialMap = new LinkedHashMap<>();
            Map<String, String> deviceIdToBranchMap = new LinkedHashMap<>();

            for (Object[] row : deviceRows) {

                String deviceId = String.valueOf(row[0]);
                String deviceSerialNo = String.valueOf(row[1]);
                String branchCode = String.valueOf(row[2]);

                deviceIdToSerialMap.put(deviceId, deviceSerialNo);
                deviceIdToBranchMap.put(deviceId, branchCode);
            }

            List<String> allDeviceIds = new ArrayList<>(deviceIdToSerialMap.keySet());

            if (allDeviceIds.isEmpty()) {
                return finalResponse;
            }

            String zoneSql =
                    "SELECT DISTINCT dzd.device_id " +
                    "FROM device_zone_details dzd " +
                    "WHERE dzd.device_id IN (:deviceIds) " +
                    "AND dzd.zone_desired_name = 'Security Alarm SIA'";

            Query zoneQuery = entityManager.createNativeQuery(zoneSql);
            zoneQuery.setParameter("deviceIds", allDeviceIds);

            @SuppressWarnings("unchecked")
            List<Object> securityDeviceRaw = zoneQuery.getResultList();

            if (securityDeviceRaw == null || securityDeviceRaw.isEmpty()) {
                return finalResponse;
            }

            List<String> securityDeviceIds =
                    securityDeviceRaw.stream()
                            .map(String::valueOf)
                            .collect(Collectors.toList());

            List<String> deviceSerialNos =
                    securityDeviceIds.stream()
                            .filter(deviceIdToSerialMap::containsKey)
                            .map(deviceIdToSerialMap::get)
                            .collect(Collectors.toList());

            if (deviceSerialNos.isEmpty()) {
                return finalResponse;
            }

            String siaMasterSql =
                    "SELECT sm.sia_serial_no, sm.panel_serial_no, sm.id " +
                    "FROM sia_master sm " +
                    "WHERE sm.panel_serial_no IN (:deviceSerialNos) " +
                    "AND sm.status = 'A'";

            Query siaMasterQuery = entityManager.createNativeQuery(siaMasterSql);
            siaMasterQuery.setParameter("deviceSerialNos", deviceSerialNos);

            @SuppressWarnings("unchecked")
            List<Object[]> siaMasterRows = siaMasterQuery.getResultList();

            if (siaMasterRows == null || siaMasterRows.isEmpty()) {
                return finalResponse;
            }

            List<String> connectedPanelSerials =
                    siaMasterRows.stream()
                            .map(row -> String.valueOf(row[1]))
                            .collect(Collectors.toList());

            if (connectedPanelSerials.isEmpty()) {
                return finalResponse;
            }

            Map<String, Long> panelToSiaDeviceMap = new HashMap<>();

            for (Object[] row : siaMasterRows) {

                String panelSerial = String.valueOf(row[1]);
                Long siaDeviceId = ((Number) row[2]).longValue();

                panelToSiaDeviceMap.put(panelSerial, siaDeviceId);
            }

            String healthSql =
                    "SELECT sh.id, " +
                    "sh.ac_main_status, " +
                    "sh.battery_status, " +
                    "sh.comm_status, " +
                    "sh.created_at, " +
                    "sh.network_status, " +
                    "sh.panel_serial, " +
                    "sh.pannel_status, " +
                    "sh.sia_panel, " +
                    "sh.zone1, " +
                    "sh.zone2, " +
                    "sh.zone3, " +
                    "sh.zone4, " +
                    "sh.zone5, " +
                    "sh.zone6, " +
                    "sh.zone7, " +
                    "sh.zone8, " +
                    "sh.zone9, " +
                    "sh.zone10, " +
                    "sh.zone11, " +
                    "sh.zone12, " +
                    "sh.zone13, " +
                    "sh.zone14, " +
                    "sh.zone15, " +
                    "sh.zone16 " +
                    "FROM sia_health sh " +
                    "WHERE sh.panel_serial IN (:connectedPanelSerials) " +
                    "ORDER BY sh.created_at DESC";

            Query healthQuery = entityManager.createNativeQuery(healthSql);
            healthQuery.setParameter("connectedPanelSerials", connectedPanelSerials);

            @SuppressWarnings("unchecked")
            List<Object[]> rawRows = healthQuery.getResultList();

            if (rawRows == null || rawRows.isEmpty()) {
                return finalResponse;
            }

            Map<String, String> panelToBranchMap = new LinkedHashMap<>();

            for (String deviceId : securityDeviceIds) {

                String serial = deviceIdToSerialMap.get(deviceId);
                String branchCode = deviceIdToBranchMap.get(deviceId);

                if (serial != null && branchCode != null) {
                    panelToBranchMap.put(serial, branchCode);
                }
            }

            // Bulk-fetch zone name mappings for ALL relevant sia device ids at once
            // (previously fetched one-by-one inside the row loop -> N+1 queries).
            List<Long> allSiaDeviceIds =
                    new ArrayList<>(new java.util.LinkedHashSet<>(panelToSiaDeviceMap.values()));

            Map<Long, Map<String, String>> zoneMappingsBySiaDeviceId =
                    buildZoneNameMappings(allSiaDeviceIds);

            Map<String, List<Map<String, Object>>> branchWiseHealth = new LinkedHashMap<>();

            for (Object[] raw : rawRows) {

                String panelSerial =
                        raw[6] != null ? String.valueOf(raw[6]) : null;

                if (panelSerial == null || panelSerial.isEmpty()) {
                    continue;
                }

                String branchCode = panelToBranchMap.get(panelSerial);

                if (branchCode == null) {
                    continue;
                }

                Long siaDeviceId = panelToSiaDeviceMap.get(panelSerial);

                Map<String, String> zoneMap =
                        siaDeviceId != null
                                ? zoneMappingsBySiaDeviceId.getOrDefault(siaDeviceId, new HashMap<>())
                                : new HashMap<>();

                Map<String, Object> row = new LinkedHashMap<>();

                row.put("id", raw[0]);
                row.put("ac_main_status", raw[1]);
                row.put("battery_status", raw[2]);
                row.put("comm_status", raw[3]);
                row.put("created_at", raw[4]);
                row.put("panel_serial", raw[6]);
                row.put("pannel_status", raw[7]);
                row.put("sia_panel", raw[8]);
                row.put("network_status", raw[5]);

                for (int i = 1; i <= 16; i++) {

                    int rawIndex = 8 + i;

                    String zoneKey = "zone" + i;
                    String zoneNameKey = "ZONE_" + i;

                    String zoneName = zoneMap.getOrDefault(zoneNameKey, zoneKey);

                    Object zoneValue = raw[rawIndex];

                    String status = zoneValue != null ? String.valueOf(zoneValue) : "N";

                    String formattedValue = zoneName + " - " + status;

                    row.put(zoneKey, formattedValue);
                }

                branchWiseHealth
                        .computeIfAbsent(branchCode, k -> new ArrayList<>())
                        .add(row);
            }

            // Bulk-fetch parent branches for "controlling office" instead of
            // querying findBybranchCode() one-by-one inside the loop below.
            List<String> parentCodesNeeded =
                    branchWiseHealth.keySet().stream()
                            .map(branchEntityMap::get)
                            .filter(b -> b != null
                                    && "BRANCH".equalsIgnoreCase(b.getBranchType())
                                    && b.getParentCode() != null
                                    && !b.getParentCode().isEmpty())
                            .map(BranchMasterEntity::getParentCode)
                            .distinct()
                            .collect(Collectors.toList());

            Map<String, BranchMasterEntity> parentBranchMap = new HashMap<>();

            if (!parentCodesNeeded.isEmpty()) {

                List<BranchMasterEntity> parentBranches =
                        branchMasterRepository.findByBranchCodeIn(parentCodesNeeded);

                if (parentBranches != null) {
                    for (BranchMasterEntity pb : parentBranches) {
                        parentBranchMap.put(pb.getBranchCode(), pb);
                    }
                }
            }

            // Bulk-fetch bank names for ALL relevant bank codes in ONE query
            // instead of hitting BankMasterRepository once per branch.
            List<String> bankCodesNeeded =
                    branchWiseHealth.keySet().stream()
                            .map(branchEntityMap::get)
                            .filter(b -> b != null
                                    && b.getBank() != null
                                    && !b.getBank().isEmpty())
                            .map(BranchMasterEntity::getBank)
                            .distinct()
                            .collect(Collectors.toList());

            Map<String, String> bankCodeToNameMap = new HashMap<>();

            if (!bankCodesNeeded.isEmpty()) {

                List<BankMasterEntity> bankList =
                        bankMasterRepository.findByBankCodeIn(bankCodesNeeded);

                if (bankList != null) {
                    for (BankMasterEntity bank : bankList) {
                        bankCodeToNameMap.put(bank.getBankCode(), bank.getBankName());
                    }
                }
            }

            for (Map.Entry<String, List<Map<String, Object>>> entry : branchWiseHealth.entrySet()) {

                String branchCode = entry.getKey();
                List<Map<String, Object>> healthData = entry.getValue();

                BranchMasterEntity branch = branchEntityMap.get(branchCode);

                SiaHealthResponseDTO dto = new SiaHealthResponseDTO();

                dto.setBranchDesc(
                        branch != null && branch.getBranchDesc() != null
                                ? branch.getBranchDesc()
                                : "");

                dto.setBank(
                        branch != null && branch.getBank() != null
                                ? bankCodeToNameMap.getOrDefault(branch.getBank(), "")
                                : "");

                String controllingOffice = "";

                if (branch != null && "BRANCH".equalsIgnoreCase(branch.getBranchType())) {

                    String parentCode = branch.getParentCode();

                    if (parentCode != null && !parentCode.isEmpty()) {

                        BranchMasterEntity parentBranch = parentBranchMap.get(parentCode);

                        if (parentBranch != null && parentBranch.getBranchDesc() != null) {
                            controllingOffice = parentBranch.getBranchDesc();
                        }
                    }
                }

                dto.setControllingOffice(controllingOffice);
                dto.setHealthData(healthData);

                finalResponse.add(dto);
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info("SIA health completed in {} ms", duration);

        } catch (Exception e) {

            log.error(
                    "Error in getSiaHealthWithZoneNames for branchCode: {}",
                    loggedInBranchCode,
                    e);

            throw new RuntimeException(
                    "Failed to fetch SIA health data: " + e.getMessage(),
                    e);
        }

        return finalResponse;
    }
}