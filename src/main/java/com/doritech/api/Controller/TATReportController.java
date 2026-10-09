package com.doritech.api.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.SmsDetailsService;

@RestController
@RequestMapping("/api/tat")
public class TATReportController {

	@Autowired
	private SmsDetailsService tatReportService;

	/**
	 * Combined TAT Report
	 *
	 * Examples: GET
	 * /api/tat/combined-report?branchCode=BR001&startDate=2024-01-01&endDate=2024-01-31
	 * GET
	 * /api/tat/combined-report?branchCode=BR001&startDate=...&endDate=...&category=vendor
	 * GET
	 * /api/tat/combined-report?branchCode=BR001&startDate=...&endDate=...&category=alert
	 */
	@GetMapping("/combined-report")
	public ResponseEntity getCombinedTATReport(@RequestParam String branchCode, @RequestParam String startDate,
			@RequestParam String endDate, @RequestParam(required = false) String category) {

		LocalDateTime start;
		LocalDateTime end;

		try {
			// Accept both "yyyy-MM-dd" and "yyyy-MM-ddTHH:mm:ss"
			if (startDate.contains("T")) {
				start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			} else {
				start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
			}

			if (endDate.contains("T")) {
				end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			} else {
				end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59, 59);
			}

		} catch (Exception e) {
			ResponseEntity errorResponse = new ResponseEntity();
			errorResponse.setMessage("Invalid date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'");
			errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return errorResponse;
		}

		return tatReportService.getCombinedTATReport(branchCode, start, end, category);
	}

	/**
	 * Combined TAT Report
	 *
	 * Examples: GET
	 * /api/tat/getAllTATReports?branchCode=BR001&startDate=2024-01-01&endDate=2024-01-31
	 * GET
	 * /api/tat/getAllTATReports?branchCode=BR001&startDate=...&endDate=...&category=vendor
	 * GET
	 * /api/tat/getAllTATReports?branchCode=BR001&startDate=...&endDate=...&category=alert
	 */
	@GetMapping("/getAllTATReports")
	public ResponseEntity getAllTATReports(@RequestParam String branchCode, @RequestParam String startDate,
			@RequestParam String endDate, @RequestParam(required = false) String category) {

		LocalDateTime start;
		LocalDateTime end;

		try {
			// Accept both "yyyy-MM-dd" and "yyyy-MM-ddTHH:mm:ss"
			if (startDate.contains("T")) {
				start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			} else {
				start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
			}

			if (endDate.contains("T")) {
				end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			} else {
				end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59, 59);
			}

		} catch (Exception e) {
			ResponseEntity errorResponse = new ResponseEntity();
			errorResponse.setMessage("Invalid date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'");
			errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return errorResponse;
		}

		return tatReportService.getAllTATReports(branchCode, start, end, category);
	}
}