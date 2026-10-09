package com.doritech.api.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.AlertNotificationEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.InputStatusDetailsEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.AlertNotificationRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.VendorMasterRepository;

@Service
public class AlertNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AlertNotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DeviceMasterService deviceMasterService;
   
    @Autowired
    private BranchMasterRepository branchMasterRepository;
    
    @Autowired
    private AlertNotificationRepository alertNotificationRepository;
    
    @Autowired
    private DeviceZoneDetailsRepository deviceZoneDetailsRepository;
    @Autowired 
    private DeviceMasterRepository deviceMasterRepository;
    @Autowired
    VendorMasterRepository vendorMasterRepository;
    
    /**
     * Scheduled task wrapper with comprehensive exception handling
     * This prevents application shutdown if any error occurs
     */
//    @Scheduled(fixedRate = 300000) // 5 minutes = 5 * 60 * 1000 ms
    public void scheduleProcessAndCheckAlarms() {
        try {
            logger.info("Starting scheduled alarm check process");
            System.out.println("Starting scheduled alarm check process");
            List<BranchMasterEntity> branches = branchMasterRepository.findByBranchType("SUPER HO");
            
            if (branches == null || branches.isEmpty()) {
                logger.warn("No branches found with type 'SUPER HO'");
                return;
            }
            
            logger.info("Processing alarms for {} branches", branches.size());
            int successCount = 0;
            int failureCount = 0;
            
            for (BranchMasterEntity branchMasterEntity : branches) {
                try {
                    if (branchMasterEntity == null) {
                        logger.warn("Null branch entity encountered, skipping");
                        continue;
                    }
                    
                    String branchCode = branchMasterEntity.getBranchCode();
                    
                    if (branchCode == null || branchCode.trim().isEmpty()) {
                        logger.warn("Branch with empty code encountered, skipping");
                        continue;
                    }
                    
                    logger.debug("Processing branch: {}", branchCode);
                    
                    String systemIntegrator = null;
                    String product = null;
                    String status = null;
                    String fromDate = null;
                    String toDate = null;
                    
                    processAndCheckAlarms(branchCode, systemIntegrator, product, status, fromDate, toDate);
                    successCount++;
                    
                } catch (Exception e) {
                    failureCount++;
                    logger.error("Error processing branch: {} - Error: {}", 
                            branchMasterEntity != null ? branchMasterEntity.getBranchCode() : "unknown", 
                            e.getMessage(), e);
                    // Continue with next branch instead of failing entire process
                }
            }
            
            logger.info("Alarm check process completed. Success: {}, Failures: {}", successCount, failureCount);
            
        } catch (Exception e) {
            // CRITICAL: Catch ALL exceptions to prevent application shutdown
            logger.error("Critical error in scheduleProcessAndCheckAlarms: {}", e.getMessage(), e);
            // Don't rethrow - let the scheduler continue
        }
    }
    
    /**
     * Process and check alarms for a specific branch
     */
    public void processAndCheckAlarms(String branchCode, String systemIntegrator, String product, String status,
            String fromDate, String toDate) {
        
        try {
            if (branchCode == null || branchCode.trim().isEmpty()) {
                logger.warn("Invalid branch code provided: {}", branchCode);
                return;
            }
            
            logger.debug("Getting response time report for branch: {}", branchCode);
            
            ResponseEntity reportResponse = deviceMasterService.getResponseTimeReport(
                    branchCode, systemIntegrator, product, status, fromDate, toDate);

            if (reportResponse == null) {
                logger.warn("Null response received for branch: {}", branchCode);
                return;
            }
            
            if (!Integer.valueOf(HttpStatus.OK.value()).equals(reportResponse.getStatusCode())) {
           // if(HttpStatus.OK.value().) {
                logger.warn("Non-OK status code received for branch: {}. Status: {}", 
                        branchCode, reportResponse.getStatusCode());
                return;
            }
            
            Map<String, Object> payload = (Map<String, Object>) reportResponse.getPayload();
            
            if (payload == null) {
                logger.warn("Null payload received for branch: {}", branchCode);
                return;
            }
            
            if (!payload.containsKey("inputStatusDetails")) {
                logger.debug("No inputStatusDetails found in payload for branch: {}", branchCode);
                return;
            }
            
            List<InputStatusDetailsEntity> inputStatusDetails = 
                    (List<InputStatusDetailsEntity>) payload.get("inputStatusDetails");

            if (inputStatusDetails == null || inputStatusDetails.isEmpty()) {
                logger.debug("No input status details found for branch: {}", branchCode);
                return;
            }
            
            logger.info("Processing {} input status details for branch: {}", 
                    inputStatusDetails.size(), branchCode);
            
            checkAndSendOpenAlarms(inputStatusDetails);
            
        } catch (Exception e) {
            logger.error("Error in processAndCheckAlarms for branch: {} - Error: {}", 
                    branchCode, e.getMessage(), e);
            throw new RuntimeException("Failed to process alarms for branch: " + branchCode, e);
        }
    }

    /**
     * Check for overdue alarms and send notifications
     */
    public void checkAndSendOpenAlarms(List<InputStatusDetailsEntity> inputStatusDetails) {
        
        if (inputStatusDetails == null || inputStatusDetails.isEmpty()) {
            logger.debug("No input status details to check");
            return;
        }
        
        try {
            List<InputStatusDetailsEntity> overdueAlarms = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
            
            int processedCount = 0;
            int skippedCount = 0;

            for (InputStatusDetailsEntity entity : inputStatusDetails) {
                try {
                    if (entity == null) {
                        logger.warn("Null entity encountered, skipping");
                        skippedCount++;
                        continue;
                    }
                    
                    if (!"Open".equalsIgnoreCase(entity.getAlarmDescription())) {
                        continue;
                    }
                    
                    if (entity.getAlarmTime() == null || entity.getAlarmTime().trim().isEmpty()) {
                        logger.warn("Empty alarm time for device: {}, skipping", entity.getDeviceName());
                        skippedCount++;
                        continue;
                    }
                    
                    LocalDateTime alarmTime = LocalDateTime.parse(entity.getAlarmTime().trim(), formatter);
                    LocalDateTime now = LocalDateTime.now();

                    Duration diff = Duration.between(alarmTime, now);
                    
                    if (diff.toHours() >= 24) {
                        logger.debug("Overdue alarm found: Device={}, Branch={}, AlertType={}, AlarmTime={}", 
                                entity.getDeviceName(), entity.getBranchName(), 
                                entity.getAlertType(), entity.getAlarmTime());
                        
                        // Check if already notified
                        boolean alreadyNotified = alertNotificationRepository
                        	    .findByDeviceNameAndBranchNameAndAlertTypeAndAlarmStatus(
                        	            entity.getDeviceName(),
                        	            entity.getBranchName(),
                        	            entity.getAlertType(),
                        	            "OPEN"
                        	    ).isPresent();


                        if (!alreadyNotified) {
                            overdueAlarms.add(entity);
                            logger.debug("Added to overdue list: {}", entity.getDeviceName());
                        } else {
                            logger.debug("Already notified for: {}", entity.getDeviceName());
                        }
                    }
                    
                    processedCount++;
                    
                } catch (DateTimeParseException e) {
                    logger.error("Invalid date format for device: {} - AlarmTime: {} - Error: {}", 
                            entity != null ? entity.getDeviceName() : "unknown",
                            entity != null ? entity.getAlarmTime() : "null",
                            e.getMessage());
                    skippedCount++;
                    // Continue with next entity
                } catch (Exception e) {
                    logger.error("Error processing entity: {} - Error: {}", 
                            entity != null ? entity.getDeviceName() : "unknown", 
                            e.getMessage(), e);
                    skippedCount++;
                    // Continue with next entity
                }
            }

            logger.info("Alarm check completed. Processed: {}, Skipped: {}, Overdue: {}", 
                    processedCount, skippedCount, overdueAlarms.size());

            if (!overdueAlarms.isEmpty()) {
                logger.info("Sending email notifications for {} overdue alarms", overdueAlarms.size());
                sendEmail(overdueAlarms);
                sendVendorWiseEmail(overdueAlarms);
            }
            
        } catch (Exception e) {
            logger.error("Critical error in checkAndSendOpenAlarms: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check and send open alarms", e);
        }
    }

    /**
     * Send email notifications for overdue alarms
     */
    private void sendEmail(List<InputStatusDetailsEntity> overdueAlarms) {
        
        if (overdueAlarms == null || overdueAlarms.isEmpty()) {
            logger.debug("No overdue alarms to send");
            return;
        }
        
        try {
            Map<String, List<InputStatusDetailsEntity>> branchWiseAlarms = overdueAlarms.stream()
                    .collect(Collectors.groupingBy(InputStatusDetailsEntity::getBranchName));

            logger.info("Sending emails for {} branches", branchWiseAlarms.size());
            
            int emailsSent = 0;
            int emailsFailed = 0;

            for (Map.Entry<String, List<InputStatusDetailsEntity>> entry : branchWiseAlarms.entrySet()) {
                String branchName = entry.getKey();
                List<InputStatusDetailsEntity> branchAlarms = entry.getValue();

                try {
                    if (branchName == null || branchName.trim().isEmpty()) {
                        logger.warn("Empty branch name encountered, skipping email");
                        emailsFailed++;
                        continue;
                    }
                    
                    logger.debug("Processing email for branch: {}", branchName);
                    
                 // ❌ Skip if any alertType is null
                    boolean hasNullAlertType = branchAlarms.stream()
                            .anyMatch(a -> a.getAlertType() == null || a.getAlertType().trim().isEmpty());

                    if (hasNullAlertType) {
                        logger.warn("Skipping email for branch {} because one or more alarms have null Alert Type", branchName);
                        emailsFailed++;
                        continue;
                    }
                    
                    BranchMasterEntity branchMasterEntity = branchMasterRepository.findByBranchDesc(branchName);
                    
                    if (branchMasterEntity == null) {
                        logger.warn("Branch master not found for: {}", branchName);
                        emailsFailed++;
                        continue;
                    }

                    // Collect all valid email addresses
                    List<String> emailList = new ArrayList<>();
                    if (branchMasterEntity.getEmail() != null && !branchMasterEntity.getEmail().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail());
                    }
                    if (branchMasterEntity.getEmail2() != null && !branchMasterEntity.getEmail2().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail2());
                    }
                    if (branchMasterEntity.getEmail3() != null && !branchMasterEntity.getEmail3().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail3());
                    }
                    if (branchMasterEntity.getEmail4() != null && !branchMasterEntity.getEmail4().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail4());
                    }
                    if (branchMasterEntity.getEmail5() != null && !branchMasterEntity.getEmail5().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail5());
                    }
                    if (branchMasterEntity.getEmail6() != null && !branchMasterEntity.getEmail6().trim().isEmpty()) {
                        emailList.add(branchMasterEntity.getEmail6());
                    }

                    String[] recipients = emailList.stream()
                            .map(String::trim)
                            .filter(e -> !e.isEmpty() && e.contains("@"))
                            .toArray(String[]::new);

                    if (recipients.length == 0) {
                        logger.warn("No valid email recipients found for branch: {}", branchName);
                        emailsFailed++;
                        continue;
                    }

                    logger.info("Sending email to {} recipients for branch: {}", recipients.length, branchName);

                    // Build email body
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><body>");
                    sb.append("<h2>Overdue Open Alarms (More than 24 hours)</h2>");
                    sb.append("<h3>Branch: ").append(escapeHtml(branchName)).append("</h3>");
                    sb.append("<p>Total Alarms: ").append(branchAlarms.size()).append("</p>");
                    sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
                    sb.append("<tr style='background-color: #f2f2f2;'>");
                    sb.append("<th>Device</th><th>Alert Type</th><th>Alarm Time</th>");
                    sb.append("</tr>");

                    for (InputStatusDetailsEntity e : branchAlarms) {
                        sb.append("<tr>")
                                .append("<td>").append(escapeHtml(e.getDeviceName())).append("</td>")
                                .append("<td>").append(escapeHtml(e.getAlertType())).append("</td>")
                                .append("<td>").append(escapeHtml(e.getAlarmTime())).append("</td>")
                                .append("</tr>");
                    }
                    sb.append("</table>");
                    sb.append("<p><small>This is an automated notification. Please do not reply.</small></p>");
                    sb.append("</body></html>");

                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(recipients);
                    helper.setSubject("Overdue Open Alarms Report - " + branchName);
                    helper.setText(sb.toString(), true);
                    
                    mailSender.send(message);
                    
                    logger.info("✅ Email sent successfully for branch: {} to {} recipients", 
                            branchName, recipients.length);
                    emailsSent++;

                    // Save notification records
                    int savedCount = 0;
                    for (InputStatusDetailsEntity e : branchAlarms) {
                        try {
                            AlertNotificationEntity alertEntity = new AlertNotificationEntity(
                                    e.getDeviceName(),
                                    e.getBranchName(),
                                    e.getAlertType(),
                                    e.getAlarmTime(),
                                    LocalDateTime.now(),
                                    "OPEN"
                            );
                            alertNotificationRepository.save(alertEntity);
                            savedCount++;
                        } catch (Exception saveEx) {
                            logger.error("Failed to save notification record for device: {} - Error: {}", 
                                    e.getDeviceName(), saveEx.getMessage());
                            // Continue saving other records
                        }
                    }
                    
                    logger.info("Saved {} notification records for branch: {}", savedCount, branchName);

                } catch (MessagingException ex) {
                    emailsFailed++;
                    logger.error("❌ MessagingException while sending email for branch: {} - Error: {}", 
                            branchName, ex.getMessage(), ex);
                } catch (Exception ex) {
                    emailsFailed++;
                    logger.error("❌ Unexpected error while sending email for branch: {} - Error: {}", 
                            branchName, ex.getMessage(), ex);
                }
            }
            
            logger.info("Email sending completed. Sent: {}, Failed: {}", emailsSent, emailsFailed);
            
        } catch (Exception e) {
            logger.error("Critical error in sendEmail: {}", e.getMessage(), e);
            // Don't rethrow - we've already logged the error
        }
    }
    
    /**
     * Escape HTML special characters to prevent XSS
     */
    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
    
    private void sendVendorWiseEmail(List<InputStatusDetailsEntity> overdueAlarms) {

        if (overdueAlarms == null || overdueAlarms.isEmpty()) {
            logger.debug("No overdue alarms to send vendor wise email");
            return;
        }

        Map<String, List<InputStatusDetailsEntity>> vendorWiseAlarms = new HashMap<>();

        for (InputStatusDetailsEntity alarm : overdueAlarms) {
            try {
                if (alarm.getDeviceSerialNo() == null || alarm.getAlertType() == null)
                    continue;

                // ✅ Get device details by serial number
                DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(alarm.getDeviceSerialNo());
                if (device == null) {
                    logger.warn("Device not found for serialNo: {}", alarm.getDeviceSerialNo());
                    continue;
                }

                // ✅ Fetch vendor mapping by deviceId + alertType (only ONE zone record)
                DeviceZoneDetailsEntity zone =
                        deviceZoneDetailsRepository.findByDeviceIdAndAlertType(device.getDeviceId(), alarm.getAlertType());

                if (zone == null) {
                    logger.warn("No vendor zone mapping found for deviceId: {} and alertType: {}",
                            device.getDeviceId(), alarm.getAlertType());
                    continue;
                }

                String vendorName = zone.getVendors();
                if (vendorName == null || vendorName.trim().isEmpty())
                    continue;

                // ✅ Group alarms vendor-wise
                vendorWiseAlarms.computeIfAbsent(vendorName, k -> new ArrayList<>()).add(alarm);

            } catch (Exception ex) {
                logger.error("Error while building vendor email map: {}", ex.getMessage(), ex);
            }
        }

        logger.info("Vendor wise grouped alarms count: {}", vendorWiseAlarms.size());

        int emailsSent = 0, emailsFailed = 0;

        for (Map.Entry<String, List<InputStatusDetailsEntity>> entry : vendorWiseAlarms.entrySet()) {

            String vendorName = entry.getKey();
            List<InputStatusDetailsEntity> alarms = entry.getValue();

            try {
                Optional<VendorMasterEntity> optional = vendorMasterRepository.findByVendorName(vendorName);

                if (optional == null|| optional.isEmpty()) {
                    logger.warn("Vendor not found: {}", vendorName);
                    emailsFailed++;
                    continue;
                }
                	VendorMasterEntity vendor = optional.get();
               

                
                List<String> vendorEmails = Arrays.asList(
                        vendor.getEmail()).stream()
                 .filter(email -> email != null && !email.isBlank() && email.contains("@"))
                 .collect(Collectors.toList());

                if (vendorEmails.isEmpty()) {
                    logger.warn("No valid email registered for vendor: {}", vendorName);
                    emailsFailed++;
                    continue;
                }

                // Build email HTML body
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append("<h2>Overdue Alarms (More than 24 hours)</h2>");
                sb.append("<h3>Vendor: ").append(escapeHtml(vendorName)).append("</h3>");
                sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
                sb.append("<tr><th>Branch</th><th>Device</th><th>Alert Type</th><th>Alarm Time</th></tr>");

                alarms.forEach(a -> sb.append("<tr>")
                        .append("<td>").append(escapeHtml(a.getBranchName())).append("</td>")
                        .append("<td>").append(escapeHtml(a.getDeviceName())).append("</td>")
                        .append("<td>").append(escapeHtml(a.getAlertType())).append("</td>")
                        .append("<td>").append(escapeHtml(a.getAlarmTime())).append("</td>")
                        .append("</tr>")
                );

                sb.append("</table>");
                sb.append("<p><small>This is an automated email. Do not reply.</small></p>");
                sb.append("</body></html>");

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(vendorEmails.toArray(new String[0]));
                helper.setSubject("Overdue Alarm Notification - Vendor: " + vendorName);
                helper.setText(sb.toString(), true);

                mailSender.send(message);

                logger.info("✅ Vendor email sent to: {} ({} alarms)", vendorName, alarms.size());
                emailsSent++;

            } catch (Exception ex) {
                emailsFailed++;
                logger.error("❌ Failed to send vendor email: {} - {}", vendorName, ex.getMessage());
            }
        }

        logger.info("Vendor Email Summary ➤ Sent: {} | Failed: {}", emailsSent, emailsFailed);
    }

}