package com.doritech.api.Service;

import com.doritech.api.Entity.*;
import com.doritech.api.Repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TestReportEmailService {

    Logger log = LoggerFactory.getLogger(TestReportEmailService.class);

    @Autowired
    private BranchMasterService branchMasterService;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private EmailTemplateLoader emailTemplateLoader;

    @Autowired
    private EmailSchedularService emailSchedulerService;

    @Autowired
    private DailyReportEmailService dailyReportEmailService;

    // ─────────────────────────────────────────────
    // Main Test Method
    // branchCode → report data source
    // toEmail    → jahan mail bhejna hai
    // ─────────────────────────────────────────────

    public String sendTestReport(String branchCode, String toEmail) {

        log.info("[TEST-MAIL] Starting test | BranchCode: {} | ToEmail: {}", branchCode, toEmail);

        // ── Validation ──────────────────────────────
        if (branchCode == null || branchCode.isBlank()) {
            return "ERROR: branchCode is required.";
        }
        if (toEmail == null || toEmail.isBlank()) {
            return "ERROR: toEmail is required.";
        }
        if (!toEmail.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            return "ERROR: Invalid email format → " + toEmail;
        }

        try {
            // ── Step 1: Branch resolve ───────────────────
            com.doritech.api.Entity.ResponseEntity responseEntity =
                    branchMasterService.getAllSuperHOAndZones();

            @SuppressWarnings("unchecked")
            List<BranchMasterEntity> branchList =
                    (List<BranchMasterEntity>) responseEntity.getPayload();

            if (branchList == null || branchList.isEmpty()) {
                return "ERROR: No branches found in system.";
            }

            BranchMasterEntity targetBranch = branchList.stream()
                    .filter(b -> branchCode.equalsIgnoreCase(b.getBranchCode()))
                    .findFirst()
                    .orElse(null);

            if (targetBranch == null) {
                return "ERROR: Branch not found for code → " + branchCode;
            }

            String branchName = targetBranch.getBranchDesc();
            log.info("[TEST-MAIL] Branch resolved | Code: {} | Name: {}", branchCode, branchName);

            // ── Step 2: Template load ────────────────────
            Optional<EmailTemplateEntity> templateOpt = emailTemplateRepository
                    .findByEscalationLevelAndEscalationReminderAndIsActiveTrue("Daily", 0);

            if (templateOpt.isEmpty()) {
                return "ERROR: Email template not found (level=Daily, reminder=0).";
            }

            EmailTemplateEntity template = templateOpt.get();

            String rawHtml = emailTemplateLoader.loadTemplate(template.getEmailBody());
            if (rawHtml == null || rawHtml.isBlank()) {
                return "ERROR: Template HTML is empty.";
            }

            // ── Step 3: Build HTML ───────────────────────
            String reportDate = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

            String htmlContent = dailyReportEmailService
                    .buildFinalHtml(rawHtml, branchCode, branchName, reportDate);

            if (htmlContent == null) {
                return "ERROR: HTML generation failed for branch → " + branchCode;
            }

            // ── Step 4: Subject ──────────────────────────
            String formattedDateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a", Locale.ENGLISH));

            String subject = "[TEST] "
                    + (template.getEmailSubject() != null ? template.getEmailSubject() : "Daily Report")
                    + " (" + formattedDateTime + ")";

            // ── Step 5: Send ─────────────────────────────
            emailSchedulerService.sendHtmlMail(toEmail, subject, htmlContent);

            String successMsg = String.format(
                    "TEST mail sent successfully | Branch: %s (%s) | To: %s",
                    branchCode, branchName, toEmail);

            log.info("[TEST-MAIL] {}", successMsg);
            return successMsg;

        } catch (Exception e) {
            log.error("[TEST-MAIL] Error: {}", e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }
}