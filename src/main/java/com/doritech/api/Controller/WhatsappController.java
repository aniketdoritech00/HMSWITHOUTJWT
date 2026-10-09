package com.doritech.api.Controller;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.ReadMail;
import com.doritech.api.Service.WhatsappService;

@RestController
public class WhatsappController {

	@Autowired
	WhatsappService whatsappService;
	@Autowired
	ReadMail readMail;

	@GetMapping("getSitesCount")
	public ResponseEntity getDeviceStatusCount(String branchCode)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return whatsappService.getDeviceStatusCount(branchCode);
	}

	/*
	 * @GetMapping("getWhatsappLogsByBranchCode") public ResponseEntity
	 * getWhatsappLogsByBranchCode(String branchCode) { return
	 * whatsappService.getWhatsappLogsByBranchCode(branchCode); }
	 */

	@GetMapping("getWhatsappLogsByBranchCode")
	public ResponseEntity getWhatsappLogsByBranchCode(@RequestParam String branchCode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "false") boolean downloadExcel) {

		return whatsappService.getWhatsappLogsByBranchCode(branchCode, page, size, downloadExcel);
	}

	/*
	 * @GetMapping("getCallLogsByBranchCode") public ResponseEntity
	 * getCallLogsByBranchCode(String branchCode) { return
	 * whatsappService.getCallLogsByBranchCode(branchCode); }
	 */
	
	@GetMapping("getCallLogsByBranchCode")
	public ResponseEntity getCallLogsByBranchCode(
	        @RequestParam String branchCode,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "false") boolean downloadExcel)
		  {

	    return whatsappService.getCallLogsByBranchCode(branchCode, page, size, downloadExcel);
	}

	@PostMapping("/send-pdf")
	public org.springframework.http.ResponseEntity<String> sendPdf(@RequestParam("filePath") String filePath) {
		try {
			whatsappService.sendPdfUsingOkHttp(filePath);
			return org.springframework.http.ResponseEntity.ok("PDF sent successfully to all branch mobiles.");
		} catch (Exception e) {
			e.printStackTrace();
			return org.springframework.http.ResponseEntity.status(500)
					.body("Error while sending PDF: " + e.getMessage());
		}
	}

	@PostMapping("/send-file")
	public org.springframework.http.ResponseEntity<String> sendFile(@RequestParam("filePath") String filePath) {
		try {
			readMail.sendFileUsingOkHttp(filePath);
			return org.springframework.http.ResponseEntity.ok("PDF sent successfully to all branch mobiles.");
		} catch (Exception e) {
			e.printStackTrace();
			return org.springframework.http.ResponseEntity.status(500)
					.body("Error while sending PDF: " + e.getMessage());
		}
	}
}
