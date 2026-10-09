package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.SmsLogEntity;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.SmsLogRepo;

/**
 * Handles SMS dispatch for escalation notifications.
 * NEW: Accepts escalation_log_id and saves SmsLogEntity with that FK.
 */
@Service
public class EscalationSmsService {

    private static final Logger log = LoggerFactory.getLogger(EscalationSmsService.class);
    private static final String TO_PHONE = "91XXXXXXXXXX";

    private final EscalationMailLogRepository escalationLogRepository;
    private final SmsLogRepo                  smsLogRepository;

    public EscalationSmsService(EscalationMailLogRepository escalationLogRepository,
                                SmsLogRepo smsLogRepository) {
        this.escalationLogRepository = escalationLogRepository;
        this.smsLogRepository        = smsLogRepository;
    }

    // =========================================================================
    //  MAIN METHOD
    // =========================================================================
    public void sendIfEligible(String  ticketNo,
                               String  escalationLevel,
                               Long    escalationLogId,  // ← NEW FK
                               int     noOfReminders,
                               int     reminderGapMin,
                               String  branchCode,
                               String  branchName,
                               String  contactPerson,
                               long    elapsedMinutes,
                               String  zoneDesiredName,
                               String  alertType,
                               int     reminderIndex) {

        log.info("[SMS] ticket='{}', level='{}', index={}, esc_log_id={}",
            ticketNo, escalationLevel, reminderIndex, escalationLogId);

        try {
            List<EscalationLog> allLogs =
                escalationLogRepository.findByTicketNoAndEscLevelOrderByReminderIndexAsc(
                    ticketNo, escalationLevel);

            long smsSentCount = countSmsSent(allLogs);
            log.info("[SMS] Previously sent={}, max={}", smsSentCount, noOfReminders);

            // Guard (b): count limit
            if (smsSentCount >= noOfReminders) {
                log.info("[SMS] Limit reached. Skipping.");
                return;
            }

            // Guard (c): time gap
            if (reminderGapMin > 0 && smsSentCount > 0) {
                EscalationLog lastLog = getLastSmsLog(allLogs);
                if (lastLog != null) {
                    long gap = ChronoUnit.MINUTES.between(lastLog.getSentAt(), LocalDateTime.now());
                    if (gap < reminderGapMin) {
                        log.info("[SMS] Too soon ({} < {}min). Skipping.", gap, reminderGapMin);
                        return;
                    }
                }
            }

            // Dispatch
            dispatchSms(escalationLogId, ticketNo, escalationLevel, branchCode, branchName,
                contactPerson, elapsedMinutes, reminderIndex, zoneDesiredName, alertType);

            log.info("[SMS] SUCCESS for esc_log_id={}", escalationLogId);

        } catch (Exception e) {
            log.error("[SMS] Error: {}", e.getMessage(), e);
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private long countSmsSent(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasSmsLog(l.getId()))
                .count();
        } catch (Exception e) {
            log.error("[SMS] countSmsSent error: {}", e.getMessage());
            return 0;
        }
    }

    private boolean hasSmsLog(Long escalationLogId) {
        try {
            return smsLogRepository.countByEscalationLogId(escalationLogId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private EscalationLog getLastSmsLog(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasSmsLog(l.getId()))
                .reduce((a, b) -> b)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void dispatchSms(Long escalationLogId, String ticketNo, String escalationLevel,
                             String branchCode, String branchName, String contactPerson,
                             long elapsedMinutes, int reminderIndex,
                             String zoneDesiredName, String alertType) {
        try {
            long hours = elapsedMinutes / 60;
            long mins  = elapsedMinutes % 60;

            String message = String.format(
                "[ESC-%s #%d] Ticket:%s | Branch:%s | Zone:%s | Alert:%s | Elapsed:%dh%dm | Contact:%s",
                escalationLevel, reminderIndex, ticketNo, branchName,
                zoneDesiredName, alertType, hours, mins, contactPerson);

            log.info("[SMS DISPATCH] Sending to '{}': {}", TO_PHONE, message);

            // ── Plug SMS gateway here ───────────────────────────────────────
            // Example: HTTP call to SMS API
            // ────────────────────────────────────────────────────────────────

            // Save log
            SmsLogEntity smsLog = new SmsLogEntity();
            smsLog.setEscalationLogId(escalationLogId);  // ← NEW FK
          //  smsLog.setTicketNo(ticketNo);
            smsLog.setSentTo(TO_PHONE);
            smsLog.setMessage(message);
            smsLog.setMessageDate(new java.util.Date());
            smsLog.setProductCode("ESCALATION");
            smsLog.setPriority("HIGH");
            smsLog.setStatus("S");
            smsLog.setDatetime(new java.util.Date());
            smsLog.setBranchCode(branchCode);
            smsLog.setBranchName(branchName);
            smsLog.setType("ESCALATION");
            smsLog.setMessageRef(ticketNo + "-" + escalationLevel + "-" + reminderIndex);

            smsLogRepository.save(smsLog);
            log.info("[SMS] SmsLogEntity saved.");

        } catch (Exception e) {
            log.error("[SMS DISPATCH] Error: {}", e.getMessage(), e);
        }
    }
}