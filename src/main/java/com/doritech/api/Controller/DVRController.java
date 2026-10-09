package com.doritech.api.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Service.DVRService;

@RestController
@RequestMapping("/api/dvr")
public class DVRController {
    
	@Autowired
	DVRService dvrService;
    
  	@GetMapping("/panel-serials")
    public ResponseEntity<List<String>> getDistinctPanelSerials() {
        List<String> panelSerials = dvrService.getDistinctPanelSerials();
        return ResponseEntity.ok(panelSerials);
    }
}