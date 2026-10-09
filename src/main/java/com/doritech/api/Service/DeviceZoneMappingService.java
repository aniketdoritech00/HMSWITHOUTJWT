package com.doritech.api.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.DeviceZoneMapping;
import com.doritech.api.Repository.DeviceZoneMappingRepository;

@Service
public class DeviceZoneMappingService {

    private DeviceZoneMappingRepository repository;

    public DeviceZoneMappingService(DeviceZoneMappingRepository repository) {
        this.repository = repository;
    }

    //  Get All Zones
    public List<DeviceZoneMapping> getAllZones() {
        try {
            List<DeviceZoneMapping> zoneList = repository.findAll();
            return zoneList;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching all zones: " + e.getMessage(), e);
        }
    }

    //  Get Active Zones
    public List<DeviceZoneMapping> getActiveZones() {
        try {
            List<DeviceZoneMapping> activeZones = repository.findByStatus("ACTIVE");
            return activeZones;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching active zones: " + e.getMessage(), e);
        }
    }
}