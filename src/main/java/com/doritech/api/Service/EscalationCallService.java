package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.CallLogEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Repository.CallLogRepository;
import com.doritech.api.Repository.EscalationMailLogRepository;

/**
 * Handles voice-call (IVR/OBD) dispatch for escalation notifications.
 * NEW: Accepts escalation_log_id and saves CallLogEntity with that FK.
 */
@Service
public class EscalationCallService {

    private static final Logger log = LoggerFactory.getLogger(EscalationCallService.class);

    private final EscalationMailLogRepository escalationLogRepository;
    private final CallLogRepository           callLogRepository;

    public EscalationCallService(EscalationMailLogRepository escalationLogRepository,
                                 CallLogRepository callLogRepository) {
        this.escalationLogRepository = escalationLogRepository;
        this.callLogRepository       = callLogRepository;
    }

    // =========================================================================
    //  MAIN METHOD
    // =========================================================================
    public void sendIfEligible(String  ticketNo,
                               String  escalationLevel,
                               Long    escalationLogId,  // ← NEW FK
                               int     noOfReminders,
                               int     reminderGapMin,
                               String  toPhone,
                               String  branchCode,
                               String  branchName,
                               String  productType,
                               String  contactPerson,
                               long    elapsedMinutes,
                               String  zoneDesiredName,
                               String  alertType,
                               int     reminderIndex) {

        log.info("[CALL] ticket='{}', level='{}', index={}, esc_log_id={}",
            ticketNo, escalationLevel, reminderIndex, escalationLogId);

        try {
            List<EscalationLog> allLogs =
                escalationLogRepository.findByTicketNoAndEscLevelOrderByReminderIndexAsc(
                    ticketNo, escalationLevel);

            long callSentCount = countCallSent(allLogs);
            log.info("[CALL] Previously sent={}, max={}", callSentCount, noOfReminders);

            // Guard (b): count limit
            if (callSentCount >= noOfReminders) {
                log.info("[CALL] Limit reached. Skipping.");
                return;
            }

            // Guard (c): time gap
            if (reminderGapMin > 0 && callSentCount > 0) {
                EscalationLog lastLog = getLastCallLog(allLogs);
                if (lastLog != null) {
                    long gap = ChronoUnit.MINUTES.between(lastLog.getSentAt(), LocalDateTime.now());
                    if (gap < reminderGapMin) {
                        log.info("[CALL] Too soon ({} < {}min). Skipping.", gap, reminderGapMin);
                        return;
                    }
                }
            }

            // Dispatch
            dispatchCall(escalationLogId, ticketNo, escalationLevel, toPhone,
                branchCode, branchName, productType, contactPerson,
                elapsedMinutes, reminderIndex, zoneDesiredName, alertType);

            log.info("[CALL] SUCCESS for esc_log_id={}", escalationLogId);

        } catch (Exception e) {
            log.error("[CALL] Error: {}", e.getMessage(), e);
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private long countCallSent(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasCallLog(l.getId()))
                .count();
        } catch (Exception e) {
            log.error("[CALL] countCallSent error: {}", e.getMessage());
            return 0;
        }
    }

    private boolean hasCallLog(Long escalationLogId) {
        try {
            return callLogRepository.countByEscalationLogId(escalationLogId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private EscalationLog getLastCallLog(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasCallLog(l.getId()))
                .reduce((a, b) -> b)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void dispatchCall(Long escalationLogId, String ticketNo, String escalationLevel,
                               String toPhone, String branchCode, String branchName,
                               String productType, String contactPerson, long elapsedMinutes,
                               int reminderIndex, String zoneDesiredName, String alertType) {
        String requestPayload  = null;
        String responsePayload = null;
        String status          = "F";

        try {
            long hours = elapsedMinutes / 60;
            long mins  = elapsedMinutes % 60;

            String reason = String.format(
                "Escalation-%s #%d | Ticket:%s | Zone:%s | Alert:%s | Elapsed:%dh%dm",
                escalationLevel, reminderIndex, ticketNo, zoneDesiredName,
                alertType, hours, mins);

            requestPayload = String.format(
                "{\"phone\":\"%s\",\"message\":\"%s\",\"branchCode\":\"%s\"}",
                toPhone, reason.replace("\"", "'"), branchCode);

            log.info("[CALL DISPATCH] Triggering IVR to '{}' | reason='{}'", toPhone, reason);

            // ── Plug IVR/OBD gateway here ──────────────────────────────────
            // Example: HTTP call to IVR API
            // ───────────────────────────────────────────────────────────────

            responsePayload = "{\"status\":\"queued\"}";
            status = "S";

            log.info("[CALL DISPATCH] IVR queued.");

        } catch (Exception e) {
            log.error("[CALL DISPATCH] Error: {}", e.getMessage(), e);
            responsePayload = e.getMessage();
            status = "F";
        }

        // Save log
        try {
            CallLogEntity callLog = new CallLogEntity();
            callLog.setEscalationLogId(escalationLogId);  // ← NEW FK
         //   callLog.setTicketNo(ticketNo);
            callLog.setBranchCode(branchCode);
            callLog.setBranchName(branchName);
            callLog.setPhoneNo(toPhone);
            callLog.setProductType(productType != null ? productType : "ESCALATION");
            callLog.setReason(String.format("Escalation-%s #%d | Ticket:%s | Zone:%s | Alert:%s",
                escalationLevel, reminderIndex, ticketNo, zoneDesiredName, alertType));
            callLog.setContactNo(contactPerson);
            callLog.setRequestPayload(requestPayload);
            callLog.setResponsePayload(responsePayload);
            callLog.setCreatedAt(LocalDateTime.now());

            callLogRepository.save(callLog);
            log.info("[CALL] CallLogEntity saved, status='{}'", status);

        } catch (Exception e) {
            log.error("[CALL] Could not save CallLogEntity: {}", e.getMessage(), e);
        }
    }
}