package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.DTO.ZoneVendorProjection;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;

@Repository
public interface DeviceZoneDetailsRepository extends JpaRepository<DeviceZoneDetailsEntity, String> {
	@Query(value = "SELECT * FROM device_zone_details WHERE device_id = :deviceId", nativeQuery = true)
	List<DeviceZoneDetailsEntity> getZoneDetailsByDeviceId(String deviceId);

	@Query(value = "SELECT * FROM device_zone_details WHERE device_id = :deviceId AND zone_original_name = :zoneOriginalName", nativeQuery = true)
	DeviceZoneDetailsEntity findByDeviceIdAndZoneName(String deviceId, String zoneOriginalName);

	@Query(value = "SELECT * FROM device_zone_details WHERE device_id = :deviceId", nativeQuery = true)
	List<DeviceZoneDetailsEntity> findAllByDeviceId(String deviceId);

	List<DeviceZoneDetailsEntity> findByDeviceId(String deviceId);

	DeviceZoneDetailsEntity findByDeviceIdAndAlertType(String deviceId, String alertType);

	List<DeviceZoneDetailsEntity> findByZoneDesiredNameAndDeviceId(String zoneDesiredName, String deviceId);

	@Query(value = "SELECT distinct alert_type FROM device_zone_details", nativeQuery = true)
	List<String> getAllAlertTypes();

	List<DeviceZoneDetailsEntity> findByDeviceIdAndZoneDesiredName(String deviceId, String zoneDesiredName);

	List<DeviceZoneDetailsEntity> findByDeviceIdIn(List<String> deviceIds);

	DeviceZoneDetailsEntity findByDeviceIdAndId(String deviceId, String id);

	List<DeviceZoneDetailsEntity> findAllByDeviceIdIn(List<String> deviceIds);

	DeviceZoneDetailsEntity findByDeviceIdAndZoneOriginalName(String deviceId, String zoneOriginalName);

//	// Existing
//	    DeviceZoneDetailsEntity findByDeviceIdAndZoneOriginalName(String deviceId, String zoneOriginalName);

	// NEW: Get all distinct zoneDesiredName for dropdown/filter
	@Query("SELECT DISTINCT d.zoneDesiredName FROM DeviceZoneDetailsEntity d "
			+ "WHERE d.zoneDesiredName IS NOT NULL ORDER BY d.zoneDesiredName ASC")
	List<String> findAllDistinctZoneDesiredNames();

	// NEW: Find zone details by deviceId + zoneDesiredName
//	    @Query("SELECT d FROM DeviceZoneDetailsEntity d " +
//	           "WHERE d.deviceId = :deviceId AND d.zoneDesiredName = :zoneDesiredName")
//	    DeviceZoneDetailsEntity findByDeviceIdAndZoneDesiredName(
//	        @Param("deviceId") String deviceId,
//	        @Param("zoneDesiredName") String zoneDesiredName);

	// NEW: Get all deviceIds that have this zoneDesiredName
	@Query("SELECT d.deviceId FROM DeviceZoneDetailsEntity d " + "WHERE d.zoneDesiredName = :zoneDesiredName")
	List<String> findDeviceIdsByZoneDesiredName(@Param("zoneDesiredName") String zoneDesiredName);

	@Query(value = "SELECT zone_desired_name AS zoneDesiredName, vendors AS vendors " + "FROM device_zone_details "
			+ "WHERE device_id = :deviceId " + "AND input_type = :groupName " + "LIMIT 1", nativeQuery = true)
	ZoneVendorProjection findZoneDesiredAndVendorsByDeviceIdAndInputType(@Param("deviceId") String deviceId,
			@Param("groupName") String groupName);

	DeviceZoneDetailsEntity findByDeviceIdAndAlertTypeAndInputType(String deviceId, String alertType, String groupName);

	@Query(value = "SELECT device_id AS deviceId, input_type AS inputType,zone_desired_name AS zoneDesiredName, vendors AS vendors FROM device_zone_details WHERE device_id IN (:deviceIds)", nativeQuery = true)
	List<ZoneVendorProjection> findZoneDataByDeviceIds(@Param("deviceIds") List<String> deviceIds);

	List<DeviceZoneDetailsEntity> findByZoneDesiredNameInAndDeviceIdIn(List<String> zoneTypes,
			List<String> activeDeviceIds);

	@Query("SELECT d FROM DeviceZoneDetailsEntity d WHERE d.deviceId IN :deviceIds AND d.zoneDesiredName IN :zoneTypes")
	List<DeviceZoneDetailsEntity> findByDeviceIdsAndZoneTypaes(@Param("deviceIds") List<String> deviceIds,
			@Param("zoneTypes") List<String> zoneTypes);
	@Query("SELECT d FROM DeviceZoneDetailsEntity d WHERE d.deviceId IN :deviceIds AND d.zoneDesiredName IN :zoneTypes")
	List<DeviceZoneDetailsEntity> findByDeviceIdsAndZoneTypes(
	    @Param("deviceIds") List<String> deviceIds,
	    @Param("zoneTypes") List<String> zoneTypes);
	
	 List<DeviceZoneDetailsEntity> findByZoneDesiredNameAndDeviceIdIn(String zoneDesiredName, List<String> deviceIds);


}
