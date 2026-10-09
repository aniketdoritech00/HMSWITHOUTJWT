package com.doritech.api.Controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;

import com.doritech.api.DTO.IntegratorDeviceMasterDTO;
import com.doritech.api.DTO.IntegratorPenalMasterDTO;
import com.doritech.api.DTO.IntegratorSimMasterDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.IntegratorDeviceService;

@RestController
@RequestMapping("/api/device/integrator")
public class IntegratorDeviceController {

    @Autowired
    private IntegratorDeviceService deviceService;

    @PostMapping("/save") 
    public ResponseEntity saveDevice(@ModelAttribute IntegratorPenalMasterDTO dto) {
            return deviceService.saveDevice(dto);
    }
    
    @PutMapping("/ContinueIntegratorDeviceForSim")     
	public ResponseEntity ContinueIntegratorDeviceForSim(IntegratorSimMasterDTO dto) throws IOException {
	return deviceService.ContinueIntegratorDeviceForSim(dto);
	}
    
    @GetMapping("/getAllPanelSerialNo")
    public ResponseEntity getAllDeviceByPanelSerialNo() {
    	return deviceService.getAllDevicePanelSerialNo();
    }
    
    @GetMapping("/getDeviceByPanelSerialNo")
	public ResponseEntity getDeviceByPanelSerialNo(String panelSerialNo) throws IllegalAccessException, InvocationTargetException {
	return deviceService.getDeviceByPanelSerialNo(panelSerialNo);
	}
    
    @GetMapping("/getAllIntegratedDeviceDetails")
	public ResponseEntity getAllIntegratedDeviceDetails() {
	return deviceService.getAllIntegratedDeviceDetails();
	}
}
