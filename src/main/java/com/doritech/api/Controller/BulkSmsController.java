package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.BulkSmsRequestDTO;
import com.doritech.api.Service.BulkSmsService;

@RestController
@RequestMapping("/api/sms")
public class BulkSmsController {

	@Autowired
BulkSmsService bulkSmsService;

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkSms(@RequestBody BulkSmsRequestDTO request) {

        if (request.getCount() <= 0) {
            return ResponseEntity.badRequest().body("Count must be greater than 0");
        }
        if (request.getMobileNumber() == null || request.getMobileNumber().isBlank()) {
            return ResponseEntity.badRequest().body("Mobile number is required");
        }

        bulkSmsService.sendBulkSms(request);  // @Async - turant return
        return ResponseEntity.ok("Bulk SMS process started. Total: "
                + request.getCount() + " SMS, every "
                + request.getWaitMinutes() + " min.");
    }
}