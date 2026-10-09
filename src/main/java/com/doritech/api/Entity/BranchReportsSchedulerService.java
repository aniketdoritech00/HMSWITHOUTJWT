//package com.doritech.api.Entity;
//
//import com.doritech.api.Repository.EmailLogRepo;
//import com.doritech.api.Repository.SchedulerRunLogRepo;
//import com.doritech.api.Service.BranchMasterService;
//import com.doritech.api.Service.EmailSchedularService;
//import com.doritech.api.Service.vice;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Branch Reports Scheduler Service
// *
// * Features:
// * 1. Daily 5 PM aur 8 AM pe emails send karta hai
// * 2. Har run DB mein save karta hai (SUCCESS / FAILED)
// * 3. System restart hone pe check karta hai ki aaj ka scheduler chala ya nahi
// *    - Agar nahi chala AND scheduled time nikal gayi → startup pe hi chala deta hai
// *
// * Last updated: 20-Mar-2026
// */
//@Service
//public class BranchReportsSchedulerService {
//
//    // ===================== Scheduler Names (Constants) =====================
//    public static final String SCHEDULER_5PM = "DAILY_5PM";
//    public static final String SCHEDULER_8AM = "DAILY_8AM";
//
//    // ===================== Scheduled Times =====================
//    private static final LocalTime TIME_5PM = LocalTime.of(17, 0);
//    private static final LocalTime TIME_8AM = LocalTime.of(8, 0);
//
//    // ===================== Autowired Dependencies =====================
//    @Autowired
//    private BranchMasterService branchMasterService;
//
//    @Autowired
//    private vice smsService;
//
//    @Autowired
//    private EmailSchedularService emailSchedulerService;
//
//    @Autowired
//    private ReportHtmlGenerator reportHtmlGenerator;
//
//    @Autowired
//    private EmailLogRepo emailLogRepository;
//
//    @Autowired
//    private SchedulerRunLogRepo schedulerRunLogRepo;
//
//
//    // =====================================================================
//    //                        CRON SCHEDULERS
//    // =====================================================================
//
//    /**
//     * Daily 5 PM scheduler
//     * Cron: 0 0 17 * * ? 
//     */
//    @Scheduled(cron = "0 0 17 * * ?")
//    public void sendDailyBranchReports5PM() {
//        System.out.println("=== [CRON] 5PM Scheduler triggered ===");
//        runScheduler(SCHEDULER_5PM, "SCHEDULED");
//    }
//
//    /**
//     * Daily 8 AM scheduler
//     * Cron: 0 0 8 * * ? 
//     */
//    @Scheduled(cron = "0 0 8 * * ?")
//    public void sendDailyBranchReports8AM() {
//        System.out.println("=== [CRON] 8AM Scheduler triggered ===");
//        runScheduler(SCHEDULER_8AM, "SCHEDULED");
//    }
//
//
//    // =====================================================================
//    //                  STARTUP RECOVERY (ApplicationRunner)
//    // =====================================================================
//
//    /**
//     * ApplicationRunner - Spring Boot application start hone ke baad automatically run hota hai.
//     *
//     * Logic:
//     * - Agar 5 PM nikal gayi aur DAILY_5PM aaj successful nahi chala → abhi chala do
//     * - Agar 8 AM nikal gayi aur DAILY_8AM aaj successful nahi chala → abhi chala do
//     */
//    @Component
//    public class SchedulerStartupRecovery implements ApplicationRunner {
//
//        @Autowired
//        private BranchReportsSchedulerService schedulerService;
//
//        @Autowired
//        private SchedulerRunLogRepo schedulerRunLogRepo;
//
//        @Override
//        public void run(ApplicationArguments args) {
//            System.out.println("=== [STARTUP] Checking for missed schedulers ===");
//
//            LocalDate today = LocalDate.now();
//            LocalTime now = LocalTime.now();
//
//            System.out.println("Current time: " + now + " | Date: " + today);
//
//            // 5 PM check
//            checkAndRecover(SCHEDULER_5PM, TIME_5PM, today, now);
//
//            // 8 AM check
//            checkAndRecover(SCHEDULER_8AM, TIME_8AM, today, now);
//
//            System.out.println("=== [STARTUP] Missed scheduler check complete ===");
//        }
//
//        private void checkAndRecover(String schedulerName, LocalTime scheduledTime,
//                                     LocalDate today, LocalTime now) {
//            if (now.isAfter(scheduledTime)) {
//                boolean alreadyRanSuccessfully = schedulerRunLogRepo
//                    .findTopBySchedulerNameAndRunDateAndStatus(schedulerName, today, "SUCCESS")
//                    .isPresent();
//
//                if (alreadyRanSuccessfully) {
//                    System.out.println("[STARTUP] " + schedulerName + " already ran successfully today. Skipping.");
//                } else {
//                    System.out.println("[STARTUP] " + schedulerName + " was MISSED today! Running now as recovery...");
//                    schedulerService.runScheduler(schedulerName, "STARTUP_RECOVERY");
//                }
//            } else {
//                System.out.println("[STARTUP] " + schedulerName + " not due yet today (scheduled at "
//                    + scheduledTime + ", current time: " + now + "). Skipping.");
//            }
//        }
//    }
//
//
//    // =====================================================================
//    //                        CORE SCHEDULER RUNNER
//    // =====================================================================
//
//    
//    public String runScheduler(String schedulerName, String triggerType) {
//        System.out.println("[" + schedulerName + "] Starting... TriggerType: " + triggerType);
//
//        String result = sendBranchReports();
//
//        String status = result.startsWith("Failed") ? "FAILED" : "SUCCESS";
//
//       saveSchedulerRunLog(schedulerName, status, result, triggerType);
//
//        System.out.println("[" + schedulerName + "] Completed with status: " + status);
//        return result;
//    }
//
//    
//    private void saveSchedulerRunLog(String schedulerName, String status,
//                                      String remarks, String triggerType) {
//        try {
//            SchedulerRunLog log = new SchedulerRunLog();
//            log.setSchedulerName(schedulerName);
//            log.setRunDate(LocalDate.now());
//            log.setRunDatetime(LocalDateTime.now());
//            log.setStatus(status);
//            log.setRemarks(remarks);
//            log.setTriggerType(triggerType);
//
//            schedulerRunLogRepo.save(log);
//            System.out.println("[LOG] Scheduler run log saved | Name: " + schedulerName
//                + " | Status: " + status + " | Trigger: " + triggerType);
//
//        } catch (Exception e) {
//            System.err.println("[LOG ERROR] Failed to save scheduler run log: " + e.getMessage());
//        }
//    }
//
//
//    // =====================================================================
//    //                     MAIN EMAIL SENDING LOGIC
//    // =====================================================================
//
//    public String sendBranchReports() {
//        try {
//            com.doritech.api.Entity.ResponseEntity responseEntity = branchMasterService.getAllSuperHOAndZones();
//            @SuppressWarnings("unchecked")
//            List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
//
//            int successCount = 0;
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
//
//            for (BranchMasterEntity branch : branchList) {
//                String branchCode = branch.getBranchCode();
//                String branchName = branch.getBranchDesc();
//                String branchType = branch.getBranchType();
//                String bankName = branch.getBank();
//
//                List<String> emailList = new ArrayList<>();
//                addIfNotEmpty(emailList, branch.getEmail());
//                addIfNotEmpty(emailList, branch.getEmail2());
//                addIfNotEmpty(emailList, branch.getEmail3());
//                addIfNotEmpty(emailList, branch.getEmail4());
//                addIfNotEmpty(emailList, branch.getEmail5());
//                addIfNotEmpty(emailList, branch.getEmail6());
//
//                if (emailList.isEmpty()) {
//                    System.out.println("[SKIP] No emails configured for branch: " + branchCode);
//
//                    saveEmailLog(
//                        null,
//                        branchCode,
//                        branchName,
//                        "No email configured for this branch",
//                        "Y",
//                        "N/A"
//                    );
//                    continue;
//                }
//
//                String htmlContent = generateReportHtml(branchCode);
//
//                String currentDateTime = LocalDateTime.now().format(formatter);
//                String subject = String.format(
//                    "DI-HMS Report | %s | %s (%s - %s [%s]) - %s",
//                    bankName  != null ? bankName  : "Bank",
//                    branchType != null ? branchType : "Branch",
//                    branchName,
//                    branchType,
//                    branchCode,
//                    currentDateTime
//                );
//
//                for (String email : emailList) {
//                    try {
//                        emailSchedulerService.sendHtmlMail(email, subject, htmlContent);
//                        successCount++;
//
//                        saveEmailLog(email, branchCode, branchName, subject, "Y", htmlContent);
//                        System.out.println("[SUCCESS] Email sent to: " + email + " | Branch: " + branchCode);
//
//                    } catch (Exception e) {
//                        System.err.println("[FAILED] Email failed to: " + email + " | Branch: " + branchCode
//                            + " | Error: " + e.getMessage());
//
//                        saveEmailLog(email, branchCode, branchName, subject, "Y", "Error: " + e.getMessage());
//                    }
//                }
//
//                System.out.println("[DONE] Branch: " + branchCode + " | Emails sent to: " + emailList.size());
//            }
//
//            String result = "Mails sent successfully to all branches. Total emails sent: " + successCount;
//            System.out.println(result);
//            return result;
//
//        } catch (Exception e) {
//            String error = "Failed: " + e.getMessage();
//            System.err.println("[ERROR] Branch reports scheduler failed: " + error);
//            e.printStackTrace();
//            return error;
//        }
//    }
//
//
//    // =====================================================================
//    //                         HELPER METHODS
//    // =====================================================================
//
//    /**
//     * Email log DB mein save karo
//     */
//    private void saveEmailLog(String emailTo, String branchCode, String branchName,
//                               String subject, String status, String bodyText) {
//        try {
//            EmailLogEntity log = new EmailLogEntity();
//            log.setEmailIdTo(emailTo);
//            log.setBranchCode(branchCode);
//            log.setBranchName(branchName);
//            log.setSubject(subject);
//            log.setStatus(status);
//            log.setBodyText(bodyText);
//            log.setDatetime(LocalDateTime.now());
//            log.setType("SCHEDULER");
//            log.setPriority("N");
//
//            emailLogRepository.save(log);
//            System.out.println("[EMAIL LOG] Saved | Branch: " + branchCode + " | Status: " + status);
//
//        } catch (Exception e) {
//            System.err.println("[EMAIL LOG ERROR] Failed to save log for branch: "
//                + branchCode + " | Error: " + e.getMessage());
//        }
//    }
//
//   
//    private String generateReportHtml(String branchCode) {
//        return reportHtmlGenerator.generateBranchReportHtml(
//            branchCode,
//            smsService.getSystemWiseBranchReport(branchCode, "", ""),
//            smsService.getUpTimeReport(branchCode),
//            smsService.getTATReport(branchCode, "CCTV"),
//            smsService.getTATReport(branchCode, "FIRE ALARM"),
//            smsService.getTATReport(branchCode, "SECURITY ALARM"),
//            smsService.getTATReport(branchCode, "BACS"),
//            smsService.getTATReport(branchCode, "ETL")
//        );
//    }
//
//    
//    private void addIfNotEmpty(List<String> emailList, String email) {
//        if (email != null && !email.trim().isEmpty()) {
//            emailList.add(email.trim());
//        }
//    }
//}