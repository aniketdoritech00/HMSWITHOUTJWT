package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.DeviceZoneDetailsDTO;
import com.doritech.api.Service.DynamicConfigrationService;

@RestController
public class DynamicConfigurationController {

	@Autowired
	DynamicConfigrationService dynamicConfigrationService;

	@PostMapping("saveDyanamicConfiguration")
	public ResponseEntity<Object> saveDyanamicConfiguration(DeviceZoneDetailsDTO deviceZoneDetailsRequest) {
		return dynamicConfigrationService.saveDyanamicConfiguration(deviceZoneDetailsRequest);
	}

	@GetMapping("/getDynamicConfigurationForDeviceId")
	public ResponseEntity<Object> getDynamicConfigurationForDeviceId(@RequestParam String deviceId) {
		return dynamicConfigrationService.getDynamicConfigurationForDeviceId(deviceId);
	}
	
	@PostMapping("editDyanamicConfiguration")
	public ResponseEntity<Object> editDynamicConfigurationByDeviceId(@RequestBody DeviceZoneDetailsDTO deviceZoneDetailsRequest) {
     return dynamicConfigrationService.editDynamicConfigurationByDeviceId(deviceZoneDetailsRequest);
	}
}
