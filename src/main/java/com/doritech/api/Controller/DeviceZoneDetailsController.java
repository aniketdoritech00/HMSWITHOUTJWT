package com.doritech.api.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.DeviceZoneDetailsDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.DeviceZoneDetailsService;

@RestController
public class DeviceZoneDetailsController {
	@Autowired
	DeviceZoneDetailsService deviceZoneDetailsService;

	@PostMapping("/addMultipleDeviceZoneDetails")
	@ResponseBody
	public ResponseEntity addMultipleDeviceZoneDetails(@RequestBody List<DeviceZoneDetailsDTO> deviceZoneDetailsDTOs)
			throws Exception {
		return deviceZoneDetailsService.addMultipleDeviceZoneDetails(deviceZoneDetailsDTOs);
	}

	@PostMapping("/editMultipleDeviceZoneDetails")
	@ResponseBody
	public ResponseEntity editMultipleDeviceZoneDetails(@RequestBody List<DeviceZoneDetailsDTO> deviceZoneDetailsDTOs)
			throws Exception {
		return deviceZoneDetailsService.editMultipleDeviceZoneDetails(deviceZoneDetailsDTOs);
	}

	@GetMapping("getAllDistinctAlertTypes")
	public ResponseEntity getAllDistinctAlertTypes() {
		return deviceZoneDetailsService.getAllDistinctAlertTypes();
	}

	@DeleteMapping("deleteZoneByDeviceId")
	public ResponseEntity deleteZoneByDeviceId(String deviceId, String id) {
		return deviceZoneDetailsService.deleteZoneByDeviceId(deviceId, id);
	}

	@GetMapping("/getAllNoneSiaCameraMappingDetailsByZoneId/{zoneId}")
	public ResponseEntity getByZoneId(@PathVariable String zoneId) {
		return deviceZoneDetailsService.getAllNoneSiaCameraMappingDetailsByZoneId(zoneId);
	}
	
	@GetMapping("/getAlertDetails")
	public ResponseEntity getAlertDetails(@RequestParam String deviceSerialNo) {
		return deviceZoneDetailsService.getAlertDetailsByDeviceSerialNo(deviceSerialNo);
	}

}
