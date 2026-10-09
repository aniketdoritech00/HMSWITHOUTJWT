package com.doritech.api.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Entity.DeviceZoneMapping;
import com.doritech.api.Service.DeviceZoneMappingService;

@RestController
@RequestMapping("/api/zones")
public class DeviceZoneMappingController {

    private DeviceZoneMappingService service;

    // Constructor Injection
    public DeviceZoneMappingController(DeviceZoneMappingService service) {
        this.service = service;
    }

    // 🔹 Get All Zones
    @GetMapping
    public ResponseEntity<?> getAllZones() {
        try {
            List<DeviceZoneMapping> zoneList = service.getAllZones();

            if (zoneList.isEmpty()) {
                return new ResponseEntity<>("No zones found", HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(zoneList, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching zones: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Get Only Active Zones
    @GetMapping("/active")
    public ResponseEntity<?> getActiveZones() {
        try {
            List<DeviceZoneMapping> activeZones = service.getActiveZones();

            if (activeZones.isEmpty()) {
                return new ResponseEntity<>("No active zones found", HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(activeZones, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching active zones: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}