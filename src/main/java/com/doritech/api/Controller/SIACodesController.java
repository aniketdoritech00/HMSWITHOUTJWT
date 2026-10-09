package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.SIACodesService;

@RestController
public class SIACodesController {
	
	@Autowired
	SIACodesService siaCodesService;
	
	@GetMapping("getAllSiaCodes")
	public ResponseEntity getAllSiaCodes() {
		return siaCodesService.getAllSiaCodes();
		
	}

}
