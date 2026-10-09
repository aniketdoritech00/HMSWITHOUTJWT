package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.CommandRequestDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.GuardNgService;

@RestController
public class GuardNgLogController {

	@Autowired
	GuardNgService guardNgService;
	
	@PostMapping("hitUrlForCommands")
	public org.springframework.http.ResponseEntity<Object> hitUrlForCommands(@RequestBody CommandRequestDTO commandRequestDTO) {
	return	 guardNgService.hitUrlForCommands(commandRequestDTO);
	}
	
	@GetMapping("getGuardNgLogsByBranchCode")
	public ResponseEntity getGuardNgLogsByBranchCode(@RequestParam String branchCode) {
		return guardNgService.getGuardNgLogsByBranchCode(branchCode);
	}
}
