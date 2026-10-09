package com.doritech.api.Controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.BranchComplaintDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.BranchComplaintService;

@RestController
public class BranchComplaintController {
	
	@Autowired
	BranchComplaintService branchComplaintService;
	
	@PostMapping("saveBranchComplaint")
	public ResponseEntity saveBranchComplaint(@RequestBody BranchComplaintDTO branchcomplaintRequest) {
		return branchComplaintService.saveBranchComplaint(branchcomplaintRequest);
	}
		

}
