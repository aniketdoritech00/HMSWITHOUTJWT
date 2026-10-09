package com.doritech.api.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doritech.api.Service.EmailAndSmsLogsService;
import com.doritech.api.Service.EmailSchedularService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Controller
public class EmailAndSmsLogsController {

	@Autowired
	private EmailSchedularService emailSchedulerService;

	@Autowired
	private EmailAndSmsLogsService emailAndSmsLogsService;

	@GetMapping("emailLog")
	public String emailLog() {
		return "emailLog";
	}

	@GetMapping("smsLog")
	public String smsLog() {
		return "smsLog";
	}

	/*
	 * @GetMapping("getAllEmailLogReport")
	 * 
	 * @ResponseBody public com.doritech.api.Entity.ResponseEntity
	 * getAllEmailLogs(String branchCode) { return
	 * emailAndSmsLogsService.getAllEmailLogsByBranchCode(branchCode); }
	 */

	@GetMapping("getAllEmailLogReport")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity getAllEmailLogs(@RequestParam String branchCode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "false") boolean downloadExcel) {

		return emailAndSmsLogsService.getAllEmailLogsByBranchCode(branchCode, page, size, downloadExcel);
	}

	@GetMapping("getEmailBodyById")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity getEmailBodyById(@RequestParam Long emailId) {

		return emailAndSmsLogsService.getEmailBodyById(emailId);
	}

	/*
	 * @GetMapping("getAllSmsLogReport")
	 * 
	 * @ResponseBody public com.doritech.api.Entity.ResponseEntity
	 * getAllSmsLogReport(String branchCode) { return
	 * emailAndSmsLogsService.getAllSmsLogReportByBranchCode(branchCode); }
	 */

	@GetMapping("getAllSmsLogReport")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity getAllSmsLogReport(@RequestParam String branchCode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "false") boolean downloadExcel) {

		return emailAndSmsLogsService.getAllSmsLogReportByBranchCode(branchCode, page, size, downloadExcel);
	}

	@GetMapping("filterEmailLogReport")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity filterEmailLogs(@RequestParam(required = false) String branchCode,
			@RequestParam(required = false) String status,  @RequestParam(required = false) String emailIdTo,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {

		return emailAndSmsLogsService.filterEmailLogs(branchCode, status, emailIdTo, fromDate, toDate);
	}
	
	@GetMapping("/getAllEmails")
	@ResponseBody
	 public com.doritech.api.Entity.ResponseEntity getAllEmails(@RequestParam String branchCode){
		 return emailAndSmsLogsService.getAllEmails(branchCode);
	 }
	
	
	@GetMapping("/getBranchListForEmailLogFilter")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity getBranchListForEmailLogFilter(@RequestParam String branchCode) {
		return emailAndSmsLogsService.getBranchListForEmailLogFilter(branchCode);
	}
}




