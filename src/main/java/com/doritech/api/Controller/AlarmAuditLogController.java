package com.doritech.api.Controller;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doritech.api.DTO.AlarmAuditLogDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.AlarmAuditLogService;


@Controller
public class AlarmAuditLogController {
	@Autowired
	AlarmAuditLogService alarmService;
	
	@PostMapping("/saveAudit")
	@ResponseBody
	public ResponseEntity saveAudit(@RequestBody AlarmAuditLogDTO alarmAuditRequest) {
		return alarmService.saveAudit(alarmAuditRequest);
	}
	
//	@GetMapping("/getAllAlarmAuditLogs")
//	@ResponseBody
//	public ResponseEntity getAllAlarmAuditLogs() throws Exception {
//		return alarmService.getAllAlarmAuditLogs();
//	}
	
	@GetMapping("getStatusCountofOneDay")
	@ResponseBody
	public ResponseEntity getStatusCount() throws Exception {
		return alarmService.getStatusCountofOneDay();
	}
	
	@GetMapping("getLatestAuditRecord")
	@ResponseBody
	public ResponseEntity getLatestAuditRecord() {
		return alarmService.getLatestAuditRecord();
	}

	@GetMapping("getStatusCountForAll")
	@ResponseBody
	public ResponseEntity getStatusCountForAll() throws Exception {
		return alarmService.getStatusCountForAll();
	}

	@GetMapping("getStatusCountByDate")
	@ResponseBody
	public ResponseEntity getStatusCountByDate(@RequestParam String dateTime) {
		return alarmService.getStatusCountByDate(dateTime);
	}
	
	@GetMapping("getAllRecordsByDate")
	@ResponseBody
	public ResponseEntity getAllRecordsByDate(@RequestParam String dateTime) {
		return alarmService.getAllRecordsByDate(dateTime);
	}
	
	@GetMapping("getStatusCountByMonth")
	@ResponseBody
	public ResponseEntity getStatusCountByMonth(@RequestParam String month ,@RequestParam String year)  {
		return alarmService.getStatusCountByMonth(month, year);
	}

	@GetMapping("getStatusCountByYear")
	@ResponseBody
	public ResponseEntity getStatusCountByYear(@RequestParam String year)  {
		return alarmService.getStatusCountByYear(year);
	}
	
	@GetMapping("getAllRecordsByMonth")
	@ResponseBody
	public ResponseEntity getAllRecordsByMonth(@RequestParam String month ,@RequestParam String year) {
		return alarmService.getAllRecordsByMonth(month, year);
	}
	
	@GetMapping("getAllRecordsByYear")
	@ResponseBody
	public ResponseEntity getAllRecordsByYear(@RequestParam String year) {
		return alarmService.getAllRecordsByYear(year);
	}
	
	@GetMapping("getStatusCountByActiveZones")
	@ResponseBody
	public ResponseEntity getStatusCountByActiveZones(@RequestParam String activeZone) {
		return alarmService.getStatusCountByActiveZones(activeZone);
	}
	
	@GetMapping("getAllRecordsByActiveZone")
	@ResponseBody
	public ResponseEntity getAllRecordsByActiveZone(String activeZone ) {
		return alarmService.getAllRecordsByActiveZone(activeZone);
	}
	@GetMapping("/getAllAlarmAuditLogs")
	@ResponseBody
	public ResponseEntity getAllAlarmAuditLogs(@RequestParam String branchCode) throws Exception {
		return alarmService.getAllAlarmAuditLogs(branchCode);
	}
	
	@GetMapping("/getStatusCountByDateRange")
	@ResponseBody
	public ResponseEntity getStatusCountByDateRange(@RequestParam String startDate,@RequestParam String endDate,@RequestParam String branchCode ) {
		return alarmService.getStatusCountByDateRange(startDate, endDate,branchCode);
	}
	
	
	@GetMapping("/getAllRecordsByDateRange")
	@ResponseBody
	public ResponseEntity getAllRecordsByDateRange(@RequestParam String startDate,@RequestParam String endDate,@RequestParam String branchCode  ) {
		
		return alarmService.getAllRecordsByDateRange(startDate, endDate,branchCode);
	}
	@GetMapping("/getRecordsByTimeFrame")
	@ResponseBody
	public ResponseEntity getRecordsByTimeFrame(@RequestParam String timeFrame,HttpSession session ) {
		
		String loggedInUserBranchId = (String) session.getAttribute("branchId");
		return alarmService.getRecordsByTimeFrame(timeFrame,loggedInUserBranchId);
	}

	@GetMapping("/getAllRecordsOfTodayByTimeRange")
	@ResponseBody
	public ResponseEntity getAllRecordsOfTodayByTimeRange(@RequestParam String startTime,
			@RequestParam String endTime ,HttpSession session) {
		
		String loggedInUserBranchId = (String) session.getAttribute("branchId");
		return alarmService.getAllRecordsByTimeRange(startTime, endTime,loggedInUserBranchId);
	}
	@GetMapping("getAllAcknowledgedAlerts")
	@ResponseBody
	public ResponseEntity getAllAcknowledgedAlerts( @RequestParam String branchCode) {
		//String loggedInUserBranchId = (String) session.getAttribute("branchId");
		return alarmService.getAllAcknowledgedAlerts(branchCode);
	}
	
	@GetMapping("/getAlaramAuditLogcount")
	public org.springframework.http.ResponseEntity<Object> getAlaramAuditLogcount(@RequestParam String remark ,@RequestParam String branchCode  ){
		return alarmService.getAlaramAuditLogcount(remark ,branchCode);
		
	}
}