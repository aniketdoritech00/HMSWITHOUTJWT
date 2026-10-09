package com.doritech.api.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.SiaZoneCameraMapping;

public interface SiaZoneCameraMappingRespository extends JpaRepository<SiaZoneCameraMapping, Long> {


	List<SiaZoneCameraMapping> findBySiaZoneIdIn(List<Long> oldZoneIds);


}
