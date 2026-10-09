package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doritech.api.Service.SmsArchivalService;

@RestController
public class SmsArchivalController {

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
}