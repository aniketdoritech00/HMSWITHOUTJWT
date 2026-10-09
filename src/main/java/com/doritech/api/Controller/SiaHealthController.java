package com.doritech.api.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.SiaHealthResponseDTO;
import com.doritech.api.Service.SiaHealthService;

@RestController
@RequestMapping("/api/sia")
public class SiaHealthController {

    @Autowired
    private SiaHealthService siaHealthService;

    
    @GetMapping("/health")
    public ResponseEntity<List<SiaHealthResponseDTO>> getSiaHealth(
            @RequestParam("branchCode") String loggedInBranchCode) {

        List<SiaHealthResponseDTO> data =
            siaHealthService.getSiaHealthWithZoneNames(loggedInBranchCode);

        return ResponseEntity.ok(data);
    }
    }