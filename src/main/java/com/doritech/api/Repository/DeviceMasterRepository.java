package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.DeviceMasterEntity;

@Repository
public interface DeviceMasterRepository extends JpaRepository<DeviceMasterEntity, String> {

	@Query(value = "SELECT * FROM device_master d where d.status='active'  AND d.device_id=:deviceId ", nativeQuery = true)
	DeviceMasterEntity findByDeviceId(String deviceId);

	@Query(value = "SELECT * FROM device_master WHERE branch_code = :branchCode", nativeQuery = true)
	List<DeviceMasterEntity> getAllDevicesByBranchCode(String branchCode);

	List<DeviceMasterEntity> findByBranchCode(String branchCode);

	@Query(value = "SELECT * FROM device_master d where d.status='active' AND d.branch_code=:branchCode ", nativeQuery = true)
	DeviceMasterEntity findBybranchCode(String branchCode);

	@Query(value = "select * from device_master where branch_code in (select branch_code from branch_master where branch_desc=:branchName)", nativeQuery = true)
	List<DeviceMasterEntity> getAllDevicesByBranchName(String branchName);

	@Query(value = "SELECT * FROM device_master WHERE device_id LIKE %:deviceId%", nativeQuery = true)
	List<DeviceMasterEntity> findByDeviceIdContainingPattern(@Param("deviceId") String deviceId);

	DeviceMasterEntity findByDeviceSerialNo(String deviceSerialNo);

	@Query(value = "SELECT * FROM device_master where branch_code = :branchCode and device_serial_no = :deviceSerialNo", nativeQuery = true)
	DeviceMasterEntity findByBranchCodeAndDeviceSerialNo(String branchCode, String deviceSerialNo);

	@Query(value = "SELECT * FROM device_master where device_serial_no = :deviceSerialNo", nativeQuery = true)
	List<DeviceMasterEntity> getAllByDeviceSerialNo(String deviceSerialNo);

	@Query(value = "SELECT * FROM device_master where device_serial_no = :deviceSerialNo", nativeQuery = true)
	DeviceMasterEntity getByDeviceSerialNo(String deviceSerialNo);

	@Query(value = "SELECT distinct vendor_name FROM device_master", nativeQuery = true)
	List<String> getAllVendorNames();

	List<DeviceMasterEntity> findByBranchCodeIn(List<String> branchCodes);

	boolean existsByDeviceSerialNo(String serialNo);

	List<DeviceMasterEntity> findByProductType(String productType);

	DeviceMasterEntity findByPhoneNumber(String phoneNumber);

	List<DeviceMasterEntity> findAllByBranchCodeIn(List<String> branchCodes);

	List<DeviceMasterEntity> findByDeviceIdIn(List<String> deviceIds);

	@Query(value = "SELECT dm.device_serial_no,dm.device_id,GROUP_CONCAT(dzd.zone_original_name) AS connected_zones FROM device_master dm JOIN device_zone_details dzd ON dm.device_id = dzd.device_id GROUP BY dm.device_serial_no, dm.device_id", nativeQuery = true)
	List<Object[]> getConnectedZones();
	
	@Query(value = "SELECT * FROM device_master d WHERE d.status = 'active' AND d.branch_code IN (:branchCodes)", nativeQuery = true)
	List<DeviceMasterEntity> findActiveByBranchCodes(@Param("branchCodes") List<String> branchCodes);

	List<DeviceMasterEntity> findByDeviceSerialNoIn(List<String> serialNos);
}