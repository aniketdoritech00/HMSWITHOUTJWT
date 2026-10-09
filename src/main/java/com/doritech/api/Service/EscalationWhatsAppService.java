package com.doritech.api.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.WhatsAppLogs;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.WhatsappLogsRepository;

/**
 * Handles WhatsApp dispatch for escalation notifications.
 * NEW: Accepts escalation_log_id and saves WhatsAppLogs with that FK.
 */
@Service
public class EscalationWhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(EscalationWhatsAppService.class);

    @Value("${WhatsappKey}")
    private String whatsappKey;

    private final EscalationMailLogRepository escalationLogRepository;
    private final WhatsappLogsRepository      whatsappLogsRepository;

    public EscalationWhatsAppService(EscalationMailLogRepository escalationLogRepository,
                                     WhatsappLogsRepository whatsappLogsRepository) {
        this.escalationLogRepository = escalationLogRepository;
        this.whatsappLogsRepository  = whatsappLogsRepository;
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
                               String  contactPerson,
                               long    elapsedMinutes,
                               String  zoneDesiredName,
                               String  alertType,
                               int     reminderIndex) {

        log.info("[WA] ticket='{}', level='{}', index={}, esc_log_id={}",
            ticketNo, escalationLevel, reminderIndex, escalationLogId);

        try {
            List<EscalationLog> allLogs =
                escalationLogRepository.findByTicketNoAndEscLevelOrderByReminderIndexAsc(
                    ticketNo, escalationLevel);

            long waSentCount = countWaSent(allLogs);
            log.info("[WA] Previously sent={}, max={}", waSentCount, noOfReminders);

            // Guard (b): count limit
            if (waSentCount >= noOfReminders) {
                log.info("[WA] Limit reached. Skipping.");
                return;
            }

            // Guard (c): time gap
            if (reminderGapMin > 0 && waSentCount > 0) {
                EscalationLog lastLog = getLastWaLog(allLogs);
                if (lastLog != null) {
                    long gap = ChronoUnit.MINUTES.between(lastLog.getSentAt(), LocalDateTime.now());
                    if (gap < reminderGapMin) {
                        log.info("[WA] Too soon ({} < {}min). Skipping.", gap, reminderGapMin);
                        return;
                    }
                }
            }

            // Dispatch
            dispatchWhatsApp(escalationLogId, ticketNo, escalationLevel, toPhone,
                branchCode, branchName, contactPerson, elapsedMinutes,
                reminderIndex, zoneDesiredName, alertType);

            log.info("[WA] SUCCESS for esc_log_id={}", escalationLogId);

        } catch (Exception e) {
            log.error("[WA] Error: {}", e.getMessage(), e);
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private long countWaSent(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasWaLog(l.getId()))
                .count();
        } catch (Exception e) {
            log.error("[WA] countWaSent error: {}", e.getMessage());
            return 0;
        }
    }

    private boolean hasWaLog(Long escalationLogId) {
        try {
            return whatsappLogsRepository.countByEscalationLogId(escalationLogId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private EscalationLog getLastWaLog(List<EscalationLog> logs) {
        try {
            return logs.stream()
                .filter(l -> hasWaLog(l.getId()))
                .reduce((a, b) -> b)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void dispatchWhatsApp(Long escalationLogId, String ticketNo, String escalationLevel,
                                   String toPhone, String branchCode, String branchName,
                                   String contactPerson, long elapsedMinutes, int reminderIndex,
                                   String zoneDesiredName, String alertType) {
        String urlString = null;
        int    responseCode = -1;
        String responseBody = "";

        try {
            long hours = elapsedMinutes / 60;
            long mins  = elapsedMinutes % 60;

            String message = String.format(
                "[ALERT-%s #%d]\nTicket:%s\nBranch:%s\nZone:%s\nAlert:%s\nElapsed:%dh%dm\nContact:%s",
                escalationLevel, reminderIndex, ticketNo, branchName,
                zoneDesiredName, alertType, hours, mins, contactPerson);

            String encoded = URLEncoder.encode(message, "UTF-8");
            urlString = "https://wasmsapi.com/api/sendText?token=" + whatsappKey
                      + "&phone=" + toPhone + "&message=" + encoded;

            log.info("[WA DISPATCH] Calling gateway for phone={}", toPhone);

            URL conn = new URL(urlString);
            HttpURLConnection http = (HttpURLConnection) conn.openConnection();
            http.setRequestMethod("POST");
            responseCode = http.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            responseBody = sb.toString();

            log.info("[WA DISPATCH] Response: code={}, body={}", responseCode, responseBody);

            // Save log
            WhatsAppLogs waLog = new WhatsAppLogs();
            waLog.setEscalationLogId(escalationLogId);  // ← NEW FK
         //   waLog.setTicketNo(ticketNo);
            waLog.setPhoneNo(toPhone);
            waLog.setWhatsappMessage(message);
            waLog.setStatus(responseCode == 200 ? "S" : "F");
            waLog.setResponseMessage(responseBody);
            waLog.setUrl(urlString);
            waLog.setBranchCode(branchCode);
            waLog.setTicketId(ticketNo + "-" + escalationLevel + "-R" + reminderIndex);
            waLog.setTimestamp(new Date());

            whatsappLogsRepository.save(waLog);
            log.info("[WA] WhatsAppLogs saved.");

        } catch (Exception e) {
            log.error("[WA DISPATCH] Error: {}", e.getMessage(), e);

            // Save failure log
            try {
                WhatsAppLogs waLog = new WhatsAppLogs();
                waLog.setEscalationLogId(escalationLogId);
              //  waLog.setTicketNo(ticketNo);
                waLog.setPhoneNo(toPhone);
                waLog.setWhatsappMessage("ERROR");
                waLog.setStatus("F");
                waLog.setResponseMessage(e.getMessage());
                waLog.setUrl(urlString != null ? urlString : "N/A");
                waLog.setBranchCode(branchCode);
                waLog.setTicketId(ticketNo + "-" + escalationLevel + "-R" + reminderIndex);
                waLog.setTimestamp(new Date());
                whatsappLogsRepository.save(waLog);
            } catch (Exception ex) {
                log.error("[WA] Could not save failure log: {}", ex.getMessage());
            }
        }
    }
}