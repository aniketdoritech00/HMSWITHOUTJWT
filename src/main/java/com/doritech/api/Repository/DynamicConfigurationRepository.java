package com.doritech.api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.DynamicConfigurationEntity;

@Repository
public interface DynamicConfigurationRepository extends JpaRepository<DynamicConfigurationEntity, Long>{

	List<DynamicConfigurationEntity> findByDeviceId(String deviceId);


	Optional<DynamicConfigurationEntity> findByDeviceIdAndAlertType(String deviceId, String alertType);


	DynamicConfigurationEntity findByDeviceIdAndDeviceZoneId(String deviceId, String id);
	
	@Query(value = "SELECT MAX(id) AS max_id FROM dynamic_configuration", nativeQuery = true)
	Long findMaxId();

}
