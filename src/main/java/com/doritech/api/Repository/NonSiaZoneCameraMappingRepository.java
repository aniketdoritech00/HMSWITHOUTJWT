package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.api.Entity.NonSiaZoneCameraMapping;

@Repository
public interface NonSiaZoneCameraMappingRepository extends JpaRepository<NonSiaZoneCameraMapping, Long> {

	@Transactional
	void deleteByZoneId(String zoneId);

	List<NonSiaZoneCameraMapping> findByZoneId(String zoneId);

	List<NonSiaZoneCameraMapping> findAllByZoneId(String zoneId);

}