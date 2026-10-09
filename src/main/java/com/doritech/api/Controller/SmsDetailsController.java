package com.doritech.api.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.CallDTO;
import com.doritech.api.DTO.MailReportDTO;
import com.doritech.api.DTO.SmsDetailsDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.MailService;
import com.doritech.api.Service.SmsDetailsService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
public class SmsDetailsController {
	@Autowired
	SmsDetailsService smsDetailsService;

	@Autowired
	ServletContext context;
	@Autowired
	MailService mailService;

	@GetMapping("/getAllSmsDetails")
	@ResponseBody
	public ResponseEntity getAllSmsDetails() throws Exception {
		return smsDetailsService.getAllSmsDetails();
	}

	@GetMapping("getSmsDetailsById")
	@ResponseBody
	public ResponseEntity getSmsDetailsById(@RequestParam int id) throws Exception {
		return smsDetailsService.getSmsDetailsById(id);
	}

	@GetMapping("getActiveDevices")
	@ResponseBody
	public ResponseEntity getActiveDevices(@RequestParam String branchCode) {
		return smsDetailsService.getActiveDevices(branchCode);
	}

	@GetMapping("/getNotSyncedDeviceCount")
	@ResponseBody
	public ResponseEntity getNotSyncedDeviceCount(@RequestParam String branchCode) {
		return smsDetailsService.getNotSyncedDeviceCount(branchCode);
	}

	@GetMapping("/getIsolatedDevicesCount")
	@ResponseBody
	public ResponseEntity getIsolatedDevicesCount(@RequestParam String branchCode) {
		return smsDetailsService.getIsolatedDevicesCount(branchCode);
	}

	@GetMapping("/getLatestRecord")
	@ResponseBody
	public ResponseEntity getLatestRecord(@RequestParam String branchCode) {
		return smsDetailsService.getLatestRecord(branchCode);
	}

	@GetMapping("/sendSmsByBranchCode")
	@ResponseBody
	public ResponseEntity sendSmsByBranchCode(@RequestParam String branchCode, String activeZone,
			Long escalationLogId) {
		return smsDetailsService.sendSmsByBranchCode(branchCode, activeZone, escalationLogId);
	}

	@GetMapping("/getStatusReport")
	@ResponseBody
	public ResponseEntity getStatusReport(@RequestParam String branchCode) {
		return smsDetailsService.getStatusReport(branchCode);
	}

	@GetMapping("/sendMailByBranchCode")
	@ResponseBody
	public ResponseEntity sendMailByBranchCode(@RequestParam String branchCode, String activeZone) {
		return smsDetailsService.sendMailByBranchCode(branchCode, activeZone);
	}

	@GetMapping("/encryptAndDecryptMessage")
	@ResponseBody
	public ResponseEntity getdata() {
		return smsDetailsService.encryptAndDecryptMessage();
	}

	@PostMapping("/changeFlag")
	@ResponseBody
	public ResponseEntity changeFlag() {
		return smsDetailsService.changeFlag();
	}

	@GetMapping("/getCountOfWorkingDevices")
	@ResponseBody
	public ResponseEntity getCountOfWorkingDevices(String branchCode) {
		return smsDetailsService.getCountOfWorkingDevices(branchCode);
	}

	@GetMapping("/getAllNotWorkingDevices")
	@ResponseBody
	public ResponseEntity getAllNotWorkingDevices(String branchCode) {
		return smsDetailsService.getAllNotWorkingDevices(branchCode);
	}

	// @GetMapping("/getAllAlertsReport")
	// @ResponseBody
	// public ResponseEntity getAllAlertsReport(@RequestParam String branchCode,
	// @RequestParam int page,
	// @RequestParam int size) {
	// return smsDetailsService.getAllAlertsReport(branchCode, page, size);
	// }

	@GetMapping("/getAllAlertsReport")
	@ResponseBody
	public ResponseEntity getAllAlertsReport(
			@RequestParam(required = true) String branchCode,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String zoneDesiredName,
			@RequestParam(required = false) String alertType,
			@RequestParam(required = false) String fromDate, // yyyy-MM-ddTHH:mm:ss
			@RequestParam(required = false) String toDate // yyyy-MM-ddTHH:mm:ss
	) {
		try {
			return smsDetailsService.getAllAlertsReport(
					branchCode,
					page,
					size,
					zoneDesiredName,
					alertType,
					fromDate,
					toDate);

		} catch (Exception e) {
			e.printStackTrace();
			ResponseEntity response = new ResponseEntity();
			response.setMessage("Error processing request: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}
	}

	/*
	 * @PostMapping("makeCall") public ResponseEntity makeCall(@RequestBody CallDTO
	 * callDTO) { return smsDetailsService.makeCall(callDTO);
	 * 
	 * }
	 */

	@GetMapping("getCountsByStatusOfAllDevices")
	public ResponseEntity getCountsByStatusOfAllDevices(@RequestParam String branchCode) {
		return smsDetailsService.getCountsByStatusOfAllDevices(branchCode);
	}

	@GetMapping("getAllAlertsReportWithoutPagination")
	public ResponseEntity getAllAlertsReportWithoutPagination(@RequestParam String branchCode) {
		return smsDetailsService.getAllAlertsReportWithoutPagination(branchCode);
	}

	@GetMapping("sendReportForMonthlyAlertReports")
	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForMonthlyAlertReports() {
		return smsDetailsService.sendReportForMonthlyAlertReports();
	}

	@GetMapping("/getSmsDetailsByPanelSerialNumberAndDate")
	public ResponseEntity getSmsDetailsByPanelSerialNumberAndDate(@RequestParam String panelSerialNumber,
			@RequestParam int month, @RequestParam int year) throws Exception {
		return smsDetailsService.getSmsDetailsForSpecificPeriod(panelSerialNumber, month, year);
	}

	@GetMapping("/historical-details")
	public ResponseEntity loadHistoricalSmsDetails(@RequestParam(required = false) String panelSerialNumber,
			@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			ResponseEntity response = smsDetailsService.loadHistoricalSmsDetails(panelSerialNumber, month, year);
			responseEntity.setMessage(response.getMessage());
			responseEntity.setStatusCode(response.getStatusCode());
			responseEntity.setPayload(response.getPayload());
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("An unexpected error occurred.");
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return responseEntity;
	}

	@GetMapping("/fetchSmsDetailsForCurrentWeek")
	public ResponseEntity fetchSmsDetailsForCurrentWeek(@RequestParam String panelSerialNumber) throws Exception {
		return smsDetailsService.fetchSmsDetailsForCurrentWeek(panelSerialNumber);
	}

	@GetMapping("/fetchSmsDetailsForCurrentDay")
	public ResponseEntity fetchSmsDetailsForCurrentDay(@RequestParam String panelSerialNumber) throws Exception {
		return smsDetailsService.fetchSmsDetailsForCurrentDay(panelSerialNumber);
	}

	@GetMapping("/sendReportForWeeklyAlertReports")
	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForWeeklyAlertReports()
			throws Exception {
		return smsDetailsService.sendReportForWeeklyAlertReports();
	}

	@GetMapping("/sendReportForDailyAlertReports")
	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForDailyAlertReports()
			throws Exception {
		return smsDetailsService.sendReportForDailyAlertReports();
	}

	@GetMapping("exportMonthlyAlertReports")
	@Scheduled(cron = "0 50 17 * * ?")
	public org.springframework.http.ResponseEntity<String> exportMonthlyAlertReports() {
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

		if (today.isEqual(lastDayOfMonth)) {
			smsDetailsService.sendMailOnMonthlyBases();
			System.out.println("Excel file exported successfully on " + LocalDateTime.now());
			return org.springframework.http.ResponseEntity
					.ok("Excel file exported successfully on " + LocalDateTime.now());
		} else {
			return org.springframework.http.ResponseEntity.ok("Today is not the last day of the month.");
		}
	}

	@GetMapping("exportWeeklyAlertReports")
	@Scheduled(cron = "0 0 18 * * 1")
	public org.springframework.http.ResponseEntity<String> exportWeeklyAlertReports() {
		smsDetailsService.sendMailOnWeeklyBases();
		return org.springframework.http.ResponseEntity.ok("Excel file exported successfully");
	}

	@GetMapping("exportDailyAlertReports")
	@Scheduled(cron = "0 55 17 * * *")
	public org.springframework.http.ResponseEntity<String> exportDailyAlertReports() {
		smsDetailsService.sendMailOnDailyBases();
		return org.springframework.http.ResponseEntity.ok("Excel file exported successfully. ");
	}

	@GetMapping("getLatestRecordWithAlertType")
	public ResponseEntity getLatestRecordWithAlertType(@RequestParam String branchCode) {
		return smsDetailsService.getLatestRecordWithAlertType(branchCode);
	}

	@GetMapping("extractCodesFromMessage")
	public String extractCodesFromMessage(@RequestParam String message) {
		return smsDetailsService.extractCodesFromMessage(message);
	}

	@GetMapping("getLeoStaticDataCodes")
	public ResponseEntity getLeoStaticDataCodes(@RequestParam("messageCode") List<String> messageCode) {
		return smsDetailsService.getLeoStaticDataCodes(messageCode);
	}

	// @Scheduled(fixedRate = 21600000) // 6 hours in milliseconds
	@GetMapping("getLastSyncScheduler")
	public ResponseEntity getLastSyncScheduler() {
		return smsDetailsService.getLastSyncScheduler();
	}

	@GetMapping("getAllNotWorkingPanelSerialNumbers")
	public ResponseEntity getAllNotWorkingPanelSerialNumbers(String branchCode) {
		return smsDetailsService.getAllNotWorkingPanelSerialNumbers(branchCode);
	}

	@GetMapping("findAllAlertsMessage")
	public org.springframework.http.ResponseEntity<Object> findAllAlertsMessage() {
		return smsDetailsService.findAllAlertsMessage();
	}

	@GetMapping("countingZonesByStatus")
	public ResponseEntity countingZonesByStatus(@RequestParam String branchCode, @RequestParam String zoneType,
			@RequestParam String status) {
		return smsDetailsService.countingZonesByStatus(branchCode, zoneType, status);
	}

	// @GetMapping("getCountsByZoneStatus")
	// public ResponseEntity getCountsByZoneStatus(@RequestParam String branchCode)
	// {
	// return smsDetailsService.getCountsByZoneStatus(branchCode);
	//
	// }
	@GetMapping("getCountsByZoneStatus")
	public ResponseEntity getCountsByZoneStatusFast(@RequestParam String branchCode) {
		return smsDetailsService.getCountsByZoneStatusFast(branchCode);

	}

	@GetMapping("getCountsByZoneStatusForToday")
	public ResponseEntity getCountsByZoneStatusForToday(@RequestParam String branchCode) {
		return smsDetailsService.getCountsByZoneStatusForToday(branchCode);

	}

	@GetMapping("countingZonesForReportTable")
	public ResponseEntity countingZonesForReportTable(
			@RequestParam String branchCode,
			@RequestParam(required = false) String zoneType,
			@RequestParam(required = false) String status) {
		return smsDetailsService.countingZonesForReportTable(branchCode, zoneType, status);
	}

	@PostMapping("saveNewSMS")
	public org.springframework.http.ResponseEntity<Object> saveNewSMS(@RequestBody SmsDetailsDTO smsDetailsDTO) {
		return smsDetailsService.saveNewSMS(smsDetailsDTO);
	}

	@GetMapping("getTATReport")
	public ResponseEntity getTATReport(@RequestParam String branchCode, @RequestParam String zoneType) {
		return smsDetailsService.getTATReport(branchCode, zoneType);
	}

	@GetMapping("getSystemWiseBranchReport")
	public ResponseEntity getSystemWiseBranchReport(
			@RequestParam String branchCode,
			@RequestParam(required = false) String zoneType,
			@RequestParam(required = false) String status) {
		return smsDetailsService.getSystemWiseBranchReport(branchCode, zoneType, status);
	}

	@GetMapping("getUpTimeReport")
	public ResponseEntity getUpTimeReport(@RequestParam String branchCode) {
		return smsDetailsService.getUpTimeReport(branchCode);
	}

	@GetMapping("getUptimeReportByTime")
	public ResponseEntity getUptimeReportByTime(@RequestParam String branchCode) {
		return smsDetailsService.getUptimeReportByTime(branchCode);
	}

	// @GetMapping("getReportForAlertsMoreThan24Hours")
	// public ResponseEntity getReportForAlertsMoreThan24Hours(String branchCode)
	// throws MessagingException {
	// return smsDetailsService.getReportForAlertsMoreThan24Hours(branchCode);
	// }

	// @GetMapping("getCountOfNotSyncedDevices")
	// public ResponseEntity getCountOfNotSyncedDevices(String branchCode) {
	// return smsDetailsService.getCountOfNotSyncedDevicesNew(branchCode);
	// }
	//
	// @GetMapping("sendIssueMail")
	// public String sendIssueMail(@RequestParam String branchCode,@RequestParam
	// List<String> issueDetails) throws MessagingException {
	// mailService.sendIssueMail(branchCode, issueDetails);
	// return "Mail sent to vendor successfully";
	// }

	@GetMapping("getUptimeReportByDateRange")
	public ResponseEntity getUptimeReportByDateRange(
			@RequestParam String branchCode,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		// Convert LocalDate → LocalDateTime range
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

		return smsDetailsService.getUptimeReportByDateRange(branchCode, startDateTime, endDateTime);

	}

	@GetMapping("getVendorWiseTATReport")
	public ResponseEntity getVendorWiseTATReport(
			@RequestParam String branchCode,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		// Convert LocalDate → LocalDateTime range
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

		return smsDetailsService.getVendorWiseTATReport(branchCode, startDateTime, endDateTime);

	}

	@GetMapping("getAlertWiseTATReport")
	public ResponseEntity getAlertWiseTATReport(
			@RequestParam String branchCode,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		// Convert LocalDate → LocalDateTime range
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

		return smsDetailsService.getAlertWiseTATReport(branchCode, startDateTime, endDateTime);

	}

	// @PutMapping("/update-datetime")
	// public org.springframework.http.ResponseEntity<String> updateDateTime() {
	// int updated = smsDetailsService.updateDateTimeColumn();
	// return org.springframework.http.ResponseEntity.ok(updated + " rows updated
	// successfully.");
	// }

	@GetMapping("getWorkingDevicesRecords")
	public ResponseEntity getWorkingDevicesRecords(@RequestParam String branchCode) {
		return smsDetailsService.getWorkingDevicesRecords(branchCode);
	}

	@GetMapping("getRecordsOfNotSyncedDevices")
	public ResponseEntity getRecordsOfNotSyncedDevices(@RequestParam String branchCode) {
		return smsDetailsService.getRecordsOfNotSyncedDevices(branchCode);
	}
	/*
	 * @GetMapping("getDataForExecutiveSummary") public ResponseEntity
	 * getDataForExecutiveSummary(@RequestParam String branchCode,@RequestParam
	 * String zoneType) { return
	 * smsDetailsService.getDataForExecutiveSummary(branchCode, zoneType); }
	 */

	@GetMapping("getDataForExecutiveSummary")
	public ResponseEntity getDataForExecutiveSummary(
			@RequestParam String branchCode,
			@RequestParam String zoneType,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String branchName,
			@RequestParam(defaultValue = "false") boolean export) { // <-- NEW: true = export ke liye pura data
		return smsDetailsService.getDataForExecutiveSummary(branchCode, zoneType, page, size, branchName, export);
	}

	@GetMapping("/uptime-tat-report")
	public ResponseEntity getUptimeTatReport(
			@RequestParam String branchCode,
			@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate,
			@RequestParam(required = false) String filterBranchName,
			@RequestParam(required = false) String filterVendorName,
			@RequestParam(required = false) String filterZone,
			@RequestParam(required = false) String filterAlertType,
			@RequestParam(required = false) String filterDevice,
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int size) {
		return smsDetailsService.getUptimeTatReport(
				branchCode, fromDate, toDate,
				filterBranchName, filterVendorName, filterZone, filterAlertType, filterDevice, page, size);
	}

	@GetMapping("/TatReport")
	public ResponseEntity getFinalTATReport(
			@RequestParam String branchCode,
			@RequestParam String startDate,
			@RequestParam String endDate) {

		LocalDateTime start = parseDateTime(startDate, true); // start of day
		LocalDateTime end = parseDateTime(endDate, false); // end of day

		if (start == null || end == null) {
			ResponseEntity response = new ResponseEntity();
			response.setMessage("Invalid date format. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		return smsDetailsService.getFinalTATReport(branchCode, start, end);
	}

	private LocalDateTime parseDateTime(String dateStr, boolean startOfDay) {
		if (dateStr == null || dateStr.isBlank())
			return null;
		try {
			// Try full datetime first: "2026-03-01T00:00:00"
			if (dateStr.contains("T")) {
				return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			}
			// Only date: "2026-03-01"
			LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
			return startOfDay
					? date.atStartOfDay() // 2026-03-01T00:00:00
					: date.atTime(23, 59, 59); // 2026-03-31T23:59:59
		} catch (Exception e) {
			System.out.println("parseDateTime FAILED for value={} | error={}" + dateStr + e.getMessage());
			return null;
		}
	}

	@GetMapping("/getNotWorkingDevicesByZone")
	public ResponseEntity getNotWorkingDevicesByZone(
			@RequestParam String branchCode,
			@RequestParam(required = false) String zoneName) {

		return smsDetailsService.getAllNotWorkingDevicesByZone(branchCode, zoneName);
	}

	@GetMapping("/getZoneTypes")
	public List<String> getZoneTypesFromDB() {
		return smsDetailsService.getZoneTypesFromDB();
	}
}
