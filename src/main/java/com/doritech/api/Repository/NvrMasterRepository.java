package com.doritech.api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.CameraMaster;
import com.doritech.api.Entity.NvrHardDisk;
import com.doritech.api.Entity.NvrMaster;

@Repository
public interface NvrMasterRepository extends JpaRepository<NvrMaster, Long> {

	List<NvrMaster> findByVendorId(String vendorId);

	@Query("SELECT n.nvrId, n.nvrName FROM NvrMaster n")
	List<Object[]> findNvrIdAndName();

	boolean existsByNvrSerialNo(String nvrSerialNo);

	boolean existsByNvrSerialNoAndNvrIdNot(String nvrSerialNo, Long nvrId);

	List<NvrMaster> findByPanelSerialNo(String panelSerialNo);

	@Query("SELECT c FROM CameraMaster c " + "JOIN c.nvr n " + "WHERE n.panelSerialNo = :panelSerialNo")
	List<CameraMaster> findCameraByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);

	@Query("SELECT h FROM NvrHardDisk h JOIN h.nvr n WHERE n.panelSerialNo = :panelSerialNo")
	List<NvrHardDisk> getHardDisksByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);

	List<NvrMaster> findByStatusAndVendorId(String status, String vendorId);

	@Query("SELECT c FROM CameraMaster c " + "JOIN c.nvr n " + "WHERE n.panelSerialNo = :panelSerialNo "
			+ "AND c.channelNo = :channelNo")
	Optional<CameraMaster> findCameraByPanelSerialNoAndChannelNo(@Param("panelSerialNo") String panelSerialNo,
			@Param("channelNo") Integer channelNo);

	Optional<NvrMaster> findNvrByPanelSerialNo(String panelSerialNo);

	@Query(value = "SELECT n.vendor_id, " + "v.vendor_name, " + "n.nvr_id, " + "n.ip_address, " + "n.port_no, "
			+ "n.panel_serial_no, " + "c.username, " + "c.password " + "FROM nvr_master n "
			+ "JOIN vendor_master v ON n.vendor_id = v.vendor_id "
			+ "JOIN nvr_connection_details c ON n.nvr_id = c.nvr_id "
			+ "WHERE UPPER(v.vendor_name) LIKE '%HIKVISION%'", nativeQuery = true)
	List<Object[]> findAllHikvisionNvrs();


	@Query(value = "SELECT " + "nm.ip_address, " + "nm.port_no, " + "ncd.username, " + "ncd.password, " + "nm.nvr_id, "
			+ "nm.panel_serial_no, " + "nm.vendor_id, " + "vm.vendor_name, " + "ncd.rtsp_port " + "FROM nvr_master nm "
			+ "LEFT JOIN nvr_connection_details ncd ON nm.nvr_id = ncd.nvr_id "
			+ "LEFT JOIN vendor_master vm ON nm.vendor_id = vm.vendor_id "
			+ "WHERE nm.panel_serial_no = :panelSerialNo", nativeQuery = true)
	List<Object[]> findNvrDetailsByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);

	@Query(value = "SELECT " + "nm.ip_address, " + "nm.port_no, " + "ncd.username, " + "ncd.password, " + "nm.nvr_id, "
			+ "nm.panel_serial_no, " + "nm.vendor_id, " + "vm.vendor_name " + "FROM nvr_master nm "
			+ "LEFT JOIN nvr_connection_details ncd ON nm.nvr_id = ncd.nvr_id "
			+ "LEFT JOIN vendor_master vm ON nm.vendor_id = vm.vendor_id "
			+ "WHERE nm.panel_serial_no = :panelSerialNo "
			+ "AND UPPER(vm.vendor_name) LIKE '%HIKVISION%'", nativeQuery = true)
	List<Object[]> findHikvisionNvrDetailsByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);
}