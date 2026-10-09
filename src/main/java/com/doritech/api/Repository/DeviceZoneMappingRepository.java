package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.DeviceZoneMapping;

public interface DeviceZoneMappingRepository 
        extends JpaRepository<DeviceZoneMapping, Long> {

    List<DeviceZoneMapping> findByStatus(String status);

}