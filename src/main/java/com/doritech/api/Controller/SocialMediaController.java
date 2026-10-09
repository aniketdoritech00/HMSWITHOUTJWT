package com.doritech.api.Controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.SocialMediaDTO;
import com.doritech.api.Service.SmsArchivalService;
import com.doritech.api.Service.SocialMediaService;

@RestController
@RequestMapping("/api/social-media")
@Validated
public class SocialMediaController {

	@Autowired
	private SocialMediaService socialMediaService;

	@Autowired
	private SmsArchivalService archivalService;

	@PostMapping("/trigger")
	public ResponseEntity<String> triggerArchival() {
		String result = archivalService.manualArchive();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/pending-count")
	public ResponseEntity<Long> getPendingArchivalCount() {
		Long count = archivalService.getRecordsToArchiveCount();
		return ResponseEntity.ok(count);
	}

	@PostMapping("/save")
	public ResponseEntity<Object> saveSocialMediaDetails(
			@Valid @RequestBody List<SocialMediaDTO> socialMediaRequestList, HttpServletRequest httpServletRequest) {
		return socialMediaService.saveSocialMediaDetails(socialMediaRequestList, httpServletRequest);
	}

	@GetMapping("/getSocialMediaDetailsByBranchCode")
	public ResponseEntity<List<SocialMediaDTO>> getSocialMediaDetailsByBranchCode(@RequestParam String branchCode) {
		return socialMediaService.getSocialMediaDetailsByBranchCode(branchCode);
	}

	@GetMapping("/getSavedAlertTypes")
	public com.doritech.api.Entity.ResponseEntity getSavedAlertTypes(@RequestParam String branchCode) {
		return socialMediaService.getSavedAlertTypes(branchCode);
	}

}
