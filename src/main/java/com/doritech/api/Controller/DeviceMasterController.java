package com.doritech.api.Controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.DeviceDropdownDTO;
import com.doritech.api.DTO.DeviceMasterDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SiaProtocalEntity;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Service.DeviceBranchService;
import com.doritech.api.Service.DeviceMasterService;
import com.doritech.api.Service.ParamService;

@RestController
public class DeviceMasterController {
	@Autowired
	DeviceMasterService deviceMasterService;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	ParamService paramService;
	@Autowired
	private DeviceBranchService deviceBranchService;

	@GetMapping("/getDeviceMaster")
	public ResponseEntity getTotalDeviceCount(String branchCode) {
		return deviceMasterService.getTotalDeviceCount(branchCode);
	}

	@PostMapping("/addDeviceMaster")
	@ResponseBody
	public ResponseEntity addDeviceMaster(@RequestBody DeviceMasterDTO deviceMasterDTO) throws Exception {
		ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ID");
		deviceMasterDTO.setDeviceId(paramEntity.getDescp1() + paramEntity.getDescp2());
		paramService.updateDeviceDescp2Value(paramEntity);
		return deviceMasterService.saveDeviceMaster(deviceMasterDTO);
	}

	@PostMapping("/editDeviceMaster")
	@ResponseBody
	public ResponseEntity editDeviceMaster(@RequestBody DeviceMasterDTO deviceMasterDTO, HttpSession session)
			throws Exception {
		return deviceMasterService.editDeviceMaster(deviceMasterDTO, session);
	}

	@GetMapping("/getAllDevicesByBranchCode")
	@ResponseBody
	public ResponseEntity getAllDevicesByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getAllDevicesByBranchCode(branchCode);
	}

	@GetMapping("/getDeviceAndZoneDetailsByDeviceId")
	@ResponseBody
	public ResponseEntity getDeviceAndZoneDetailsByDeviceId(String deviceId) throws Exception {
		return deviceMasterService.getDeviceAndZoneDetailsByDeviceId(deviceId);
	}

	@GetMapping("getAllDevicesAndSmsDetailsByBranchCode")
	@ResponseBody
	public ResponseEntity getAllDevicesAndSmsDetailsByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getAllDevicesAndSmsDetailsByBranchCode(branchCode);
	}

	@GetMapping("/getDeviceCountByBranchCode")
	@ResponseBody
	public ResponseEntity getDeviceCountByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getDeviceCountByBranchCode(branchCode);
	}

	@GetMapping("getDeviceLogDetails")
	@ResponseBody
	public ResponseEntity getDeviceLogDetails(@RequestParam String branchName) throws Exception {
		return deviceMasterService.getDeviceLogDetails(branchName);
	}

	@GetMapping("/getActiveDeviceLogDetails")
	@ResponseBody
	public ResponseEntity getActiveDeviceLogDetails(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getActiveDeviceLogDetails(branchCode);
	}

	@DeleteMapping("deleteDeviceMasterByDeviceId")
	@ResponseBody
	public ResponseEntity deleteDeviceMasterByDeviceId(@RequestParam String deviceId) throws Exception {
		return deviceMasterService.deleteDeviceMasterByDeviceId(deviceId);
	}

	@GetMapping("getDeviceMasterByDeviceId")
	@ResponseBody
	public ResponseEntity getDeviceMasterByDeviceId(@RequestParam String deviceId) throws Exception {
		return deviceMasterService.getDeviceMasterByDeviceId(deviceId);
	}

	@GetMapping("checkBranch")
	@ResponseBody
	public ResponseEntity checkBranch(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.checkBranch(branchCode);
	}

	@GetMapping("getCountOfZonesByType")
	@ResponseBody
	public ResponseEntity getCountOfZonesByType(@RequestParam String branchCode) {
		return deviceMasterService.getCountOfZonesByType(branchCode);
	}

	@GetMapping("getDeviceDetailsByBranchCode")
	@ResponseBody
	public ResponseEntity getDeviceDetailsByBranchCode(@RequestParam String branchCode) {
		return deviceMasterService.getDeviceDetailsByBranchCode(branchCode);
	}

	@GetMapping("getAllDeviceMaster")
	@ResponseBody
	public ResponseEntity getAllDeviceMaster() {
		return deviceMasterService.getAllDeviceMaster();
	}

	@GetMapping("getInputStatusDetails")
	@ResponseBody
	public ResponseEntity getInputStatusDetails(@RequestParam String branchCodeRequest) {
		return deviceMasterService.getInputStatusDetails(branchCodeRequest);
	}

	@GetMapping("getAllAlertTypes")
	public ResponseEntity getAllAlertTypes(@RequestParam String branchCode) {
		return deviceMasterService.getAllAlertTypes(branchCode);
	}

	@GetMapping("getAlertFlag")
	public ResponseEntity getAlertFlag(@RequestParam String branchCode) {
		return deviceMasterService.getAlertFlag(branchCode);
	}

	@GetMapping("getAllPanelSerialNumbersByBranchCode")
	public ResponseEntity getAllPanelSerialNumbersByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getAllPanelSerialNumbersByBranchCode(branchCode);
	}

	@GetMapping("getAllNgDevices")
	public ResponseEntity getAllNgDevices(@RequestParam String branchCode) {
		return deviceMasterService.getAllNgDevicesByBranchCode(branchCode);
	}

	@GetMapping("getAllVendorNames")
	public ResponseEntity getAllVendorNames() {
		return deviceMasterService.getAllVendorNames();
	}

	@GetMapping("checkDevice")
	@ResponseBody
	public ResponseEntity checkDevice(@RequestParam String serialNo) throws Exception {
		return deviceMasterService.checkDevice(serialNo);
	}

	@PostMapping("UpdateSiaProtocols")
	public ResponseEntity UpdateSiaProtocols(@RequestBody List<SiaProtocalEntity> siaList) {
		return deviceMasterService.UpdateSiaProtocols(siaList);
	}

	@GetMapping("getAllDeviceSiaMessageType")
	@ResponseBody
	public ResponseEntity getAllDeviceSiaCommand(@RequestParam String command, @RequestParam String deviceId)
			throws Exception {
		return deviceMasterService.getAllDeviceSiaCommand(command, deviceId);
	}

	@GetMapping("/getDeviceCountOfSiaByBranchCode")
	@ResponseBody
	public ResponseEntity getDeviceCountOfSiaByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getDeviceCountOfSiaByBranchCode(branchCode);
	}

	@GetMapping("getAllDeviceZoneNameByBranchCode")
	@ResponseBody
	public ResponseEntity getAllDeviceZoneNameByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getAllDeviceZoneNameByBranchCode(branchCode);
	}

	@GetMapping("getAllDeviceOfSia")
	@ResponseBody
	public ResponseEntity getAllDeviceOfSia(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getAllDeviceOfSia(branchCode);
	}

	@GetMapping("getDeviceForSiaByPanelSerialNo")
	@ResponseBody
	public ResponseEntity getDeviceForSiaByPanelSerialNo(@RequestParam String deviceSerialNo) throws Exception {
		return deviceMasterService.getDeviceForSiaByPanelSerialNo(deviceSerialNo);
	}

	@GetMapping("getInputStatusDetailsByAlarmDateRange")
	@ResponseBody
	public ResponseEntity getInputStatusDetailsByAlarmDateRange(@RequestParam String branchCodeRequest,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

		return deviceMasterService.getInputStatusDetailsByAlarmDateRange(branchCodeRequest, startDate, endDate);
	}

	@GetMapping("/vendorResolutionReport")
	public org.springframework.http.ResponseEntity<?> getVendorResolutionStats(@RequestParam String branchCode) {
		return deviceMasterService.getVendorResolutionStats(branchCode);
	}

	@GetMapping("getResponseTimeReport")
	public ResponseEntity getResponseTimeReport(@RequestParam String branchCode,
			@RequestParam(required = false) String vendorFilter, @RequestParam(required = false) String productFilter,
			@RequestParam(required = false) String statusFilter, @RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate) {

		return deviceMasterService.getResponseTimeReport(branchCode, vendorFilter, productFilter, statusFilter,
				fromDate, toDate);
	}

	/*
	 * @GetMapping("/getTicketReport") public ResponseEntity
	 * getTicketReport(@RequestParam String branchCode, // @RequestParam(required =
	 * false) String systemIntegrator, @RequestParam(required = false) String
	 * product,
	 * 
	 * @RequestParam(required = false) String status, @RequestParam(required =
	 * false) String fromDate,
	 * 
	 * @RequestParam(required = false) String toDate, @RequestParam(required =
	 * false) String ticketNo,
	 * 
	 * @RequestParam(defaultValue = "0") int page, // ← page number
	 * 
	 * @RequestParam(defaultValue = "50") int size // ← page size ) { return
	 * deviceMasterService.getTicketReport(branchCode, status, fromDate, toDate,
	 * ticketNo, page, size); }
	 */
	
	@GetMapping("/getTicketReport")
	public ResponseEntity getTicketReport(@RequestParam String branchCode,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false) String toDate,
	        @RequestParam(required = false) String ticketNo,
	        @RequestParam(required = false) List<String> alertTypes, 
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "50") int size) {
	    return deviceMasterService.getTicketReport(branchCode, status, fromDate, toDate, ticketNo, alertTypes, page,
	            size);
	}

	@GetMapping("getAlertZoneDetailsByTicket")
	public ResponseEntity getAlertZoneDetailsByTicket(String ticketNo) {
		return deviceMasterService.getAlertZoneDetailsByTicket(ticketNo);
	}

	@GetMapping("getDeviceAndZoneDetailsByBranchCode")
	@ResponseBody
	public ResponseEntity getDeviceAndZoneDetailsByBranchCode(@RequestParam String branchCode) throws Exception {
		return deviceMasterService.getDeviceAndZoneDetailsByBranchCode(branchCode);
	}

	@GetMapping("checkPanelSerialNo")
	public ResponseEntity checkPanelSerialPresent(String panelSerialNo) {
		return deviceMasterService.checkPanelSerialPresent(panelSerialNo);
	}

	@GetMapping("checkPhoneNo")
	public ResponseEntity checkPhoneNoPresent(String phoneNo) {
		return deviceMasterService.checkPhoneNoPresent(phoneNo);
	}

	@GetMapping("getVendorByBranchCodeAndAlertType")
	public ResponseEntity getVendorByBranchCodeAndAlertType(@RequestParam String branchCode,
			@RequestParam String alertType) {
		return deviceMasterService.getVendorByBranchCodeAndAlertType(branchCode, alertType);
	}

	@GetMapping("getAllDevicesByBranchCodeWithoutLastSync")
	public ResponseEntity getAllDevicesByBranchCodeWithoutLastSync(@RequestParam String branchCode) {
		return deviceMasterService.getAllDevicesByBranchCodeWithoutLastSync(branchCode);
	}

	@GetMapping("getDeviceWiseTotalCount")
	public ResponseEntity getDeviceWiseTotalCount(@RequestParam String branchCode) {
		return deviceMasterService.getDeviceWiseTotalCount(branchCode);
	}

	@GetMapping("getOpenAlertsWithCommunication")
	public ResponseEntity getOpenAlertsWithCommunication(@RequestParam String branchCode,
			@RequestParam(required = false) String systemIntegrator,
			@RequestParam(required = false) String productFilter, @RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate) {
		return deviceMasterService.getOpenAlertsWithCommunication(branchCode, systemIntegrator, productFilter, fromDate,
				toDate);
	}

	@GetMapping("getAllTicketColour")
	public ResponseEntity getAllTicketColour() {
		ResponseEntity responseEntity = new ResponseEntity();
		List<ParamEntity> param = paramRepository.findByDescp1("COLOUR");
		responseEntity.setStatusCode(HttpStatus.OK.value());
		responseEntity.setMessage("Success");
		responseEntity.setPayload(param);
		return responseEntity;
	}

	@GetMapping("/getVendorDropdown")
	public ResponseEntity getVendorDropdown(@RequestParam String branchCode) {
		return deviceMasterService.getVendorDropdown(branchCode);
	}

	@GetMapping("/getZoneOriginalNameDropdown")
	public ResponseEntity getZoneOriginalNameDropdown(@RequestParam String branchCode) {
		return deviceMasterService.getZoneOriginalNameDropdown(branchCode);
	}

	@GetMapping("/getZoneDesiredNameDropdown")
	public ResponseEntity getZoneDesiredNameDropdown(@RequestParam String branchCode) {
		return deviceMasterService.getZoneDesiredNameDropdown(branchCode);
	}

	@GetMapping("/getAlertTypeDropdown")
	public ResponseEntity getAlertTypeDropdown(@RequestParam String branchCode) {
		return deviceMasterService.getAlertTypeDropdown(branchCode);
	}

	@GetMapping("/TatReportfilters")
	public ResponseEntity getDropdownFilters(@RequestParam String branchCode,
			@RequestParam(required = false) String vendorId, @RequestParam(required = false) String zoneOriginalName,
			@RequestParam(required = false) String zoneDesiredName, @RequestParam(required = false) String alertType) {
		return deviceMasterService.getDropdownFilters(branchCode, vendorId, zoneOriginalName, zoneDesiredName,
				alertType);
	}

	@GetMapping("getCountOfNotSyncedDevices")
	public ResponseEntity getNonSyncedDevicesCountNew(String branchCode) {
		return deviceMasterService.getNonSyncedDevicesCountNew(branchCode);
	}

	@GetMapping("/deviceBranchMapdropdown")
	public ResponseEntity getDevicesForDropdown(@RequestParam String branchCode) {
		List<DeviceDropdownDTO> devices = deviceBranchService.getDeviceListForDropdown(branchCode);
		return new ResponseEntity("Devices fetched successfully for dropdown", HttpStatus.OK.value(), devices);
	}

	@GetMapping("/getParamByCode")
	public ResponseEntity getParamByCode(@RequestParam String code) {
		List<ParamEntity> params = deviceBranchService.getParamByCode(code);
		return new ResponseEntity("Devices fetched successfully for dropdown", HttpStatus.OK.value(), params);
	}
}
