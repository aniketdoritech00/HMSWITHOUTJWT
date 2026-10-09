package com.doritech.api.Controller;

import com.doritech.api.Service.DailyReportEmailService;
import com.doritech.api.Service.TestReportEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/test/daily-report")
public class DailyReportTestController {

	 @Autowired
	    private TestReportEmailService testReportEmailService;

    @Autowired
    private DailyReportEmailService dailyReportEmailService;

    /**
     * 1. Browser mein HTML preview dekho — email nahi jaayegi
     * GET /api/test/daily-report/preview?branchCode=ZO001
     */
    @GetMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    public String previewReport(@RequestParam String branchCode) {
        String reportDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        // rawHtml directly build karo preview ke liye
        String html = dailyReportEmailService.buildFinalHtml(
                getRawTemplate(), branchCode, branchCode, reportDate);

        return html != null ? html : "<h3 style='color:red'>HTML generation failed. Check logs.</h3>";
    }

    /**
     * 2. Specific branch + apne email pe test bhejo
     * GET /api/test/daily-report/send?branchCode=ZO001&email=test@example.com
     */
    @GetMapping("/send")
    public ResponseEntity<String> testSend(
            @RequestParam String branchCode,
            @RequestParam(required = false) String email) {
        String result = dailyReportEmailService.sendBranchReports(branchCode, email);
        return ResponseEntity.ok(result);
    }

    /**
     * 3. Full job manually trigger karo (saari branches ko mail jaayegi)
     * GET /api/test/daily-report/trigger-all
     */
    @GetMapping("/trigger-all")
    public ResponseEntity<String> triggerAll() {
        String result = dailyReportEmailService.sendBranchReports();
        return ResponseEntity.ok(result);
    }

   
    private String getRawTemplate() {
        return "<!-- TEMPLATE PLACEHOLDER — wire EmailTemplateLoader here -->";
    }
    
    

   
    @GetMapping("/test-report")
    public ResponseEntity<String> testReport(
            @RequestParam String branchCode,
            @RequestParam String toEmail) {

        String result = testReportEmailService.sendTestReport(branchCode, toEmail);
        return ResponseEntity.ok(result);
    }
}