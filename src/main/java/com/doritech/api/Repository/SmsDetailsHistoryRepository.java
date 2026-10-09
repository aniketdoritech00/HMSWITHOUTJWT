
package com.doritech.api.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;

@Repository
public interface SmsDetailsHistoryRepository extends JpaRepository<SmsDetailsHistoryEntity, Integer> {
	// Custom queries can be added here if needed

	// Find by panel serial number
	List<SmsDetailsHistoryEntity> findByPanelSerialNo(String panelSerialNo);

	// Find by panel serial number and date range
	List<SmsDetailsHistoryEntity> findByPanelSerialNoAndDateTimeBetween(String panelSerialNo, Date startDate,
			Date endDate);

	// Find by panel serial number and start date
	List<SmsDetailsHistoryEntity> findByPanelSerialNoAndDateTimeGreaterThanEqual(String panelSerialNo, Date startDate);

	// Find by panel serial number and end date
	List<SmsDetailsHistoryEntity> findByPanelSerialNoAndDateTimeLessThanEqual(String panelSerialNo, Date endDate);

	@Query("SELECT s FROM SmsDetailsHistoryEntity s WHERE s.panelSerialNo IN :serialNos "
			+ "AND s.dateTime BETWEEN :fromDate AND :toDate " + "ORDER BY s.panelSerialNo ASC, s.dateTime ASC")
	List<SmsDetailsHistoryEntity> findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAscOrder(
			@Param("serialNos") List<String> serialNos, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	// Custom query to find records older than specific date
	@Query("SELECT s FROM SmsDetailsHistoryEntity s WHERE s.panelSerialNo = :panelSerialNo "
			+ "AND s.dateTime < :cutoffDate ORDER BY s.dateTime DESC")
	List<SmsDetailsHistoryEntity> findOldRecordsByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo,
			@Param("cutoffDate") Date cutoffDate);

	List<SmsDetailsHistoryEntity> findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(
			List<String> panelSerialNos, Date fromDate, Date toDate);

	List<SmsDetailsHistoryEntity> findByPanelSerialNoInOrderByPanelSerialNoAscDateTimeAsc(List<String> panelSerialNos);

	@Query(value = "SELECT * FROM (SELECT s.*, ROW_NUMBER() OVER (PARTITION BY s.panelserialno ORDER BY s.id DESC) rn FROM sms_details_history s WHERE s.panelserialno IN (:panelList)) t WHERE t.rn <= 5 ORDER BY t.panelserialno, t.id DESC", nativeQuery = true)
	List<SmsDetailsHistoryEntity> getLatestFiveRecords(@Param("panelList") List<String> panelList);

	@Query(value = "SELECT s FROM SmsDetailsHistoryEntity s WHERE s.panelSerialNo = :serialNo ORDER BY s.dateTime DESC")
	List<SmsDetailsHistoryEntity> findByPanelSerialNoOrderByDateTimeDesc(@Param("serialNo") String serialNo);
	// SmsDetailsHistoryRepository.java
//        @Query(value = "SELECT * FROM sms_details_history WHERE panelserialno IN (:panelList) ORDER BY panelserialno, id DESC LIMIT 450", nativeQuery = true)
//        List<SmsDetailsHistoryEntity> getLatestFiveRecordsNew(@Param("panelList") List<String> panelList);
//    // Custom default method to fetch top N records

	// SmsDetailsHistoryRepository.java — original query wapas
	@Query(value = "SELECT * FROM (SELECT s.*, ROW_NUMBER() OVER (PARTITION BY s.panelserialno ORDER BY s.id DESC) rn FROM sms_details_history s WHERE s.panelserialno IN (:panelList)) t WHERE t.rn <= 5 ORDER BY t.panelserialno, t.id DESC", nativeQuery = true)
	List<SmsDetailsHistoryEntity> getLatestFiveRecordsNew(@Param("panelList") List<String> panelList);

	default List<SmsDetailsHistoryEntity> findTopByPanelSerialNoOrderByDateTimeDesc(String serialNo, int limit) {
		List<SmsDetailsHistoryEntity> allRecords = findByPanelSerialNoOrderByDateTimeDesc(serialNo);
		return allRecords.stream().limit(limit).collect(Collectors.toList());
	}

	Optional<SmsDetailsHistoryEntity> findByOriginalId(int intValue);

	// SmsDetailsRepository.java — same
	@Query(value = "SELECT * FROM sms_details_history WHERE panelserialno = :serialNo ORDER BY id DESC LIMIT 5", nativeQuery = true)
	List<SmsDetailsHistoryEntity> getLatestFiveBySerial(@Param("serialNo") String serialNo);

	List<SmsDetailsHistoryEntity> findByOriginalIdIn(ArrayList arrayList);

	@Query(value = "SELECT s.* FROM sms_details_history s INNER JOIN (SELECT PANELSERIALNO, MAX(DATETIME) as maxDt FROM sms_details_history WHERE PANELSERIALNO IN (:serialNos) GROUP BY PANELSERIALNO) latest ON s.PANELSERIALNO = latest.PANELSERIALNO AND s.DATETIME = latest.maxDt", nativeQuery = true)
	List<SmsDetailsHistoryEntity> getLatestHistoryForSerialNos(@Param("serialNos") List<String> serialNos);

	// SmsDetailsHistoryRepository — same pattern
	@Query("SELECT h FROM SmsDetailsHistoryEntity h WHERE h.panelSerialNo IN :serialNos AND h.dateTime = (SELECT MAX(h2.dateTime) FROM SmsDetailsHistoryEntity h2 WHERE h2.panelSerialNo = h.panelSerialNo)")
	List<SmsDetailsHistoryEntity> findLatestForSerialNos(@Param("serialNos") List<String> serialNos);
	
	// SmsDetailsHistoryRepository.java
		@Query("SELECT h FROM SmsDetailsHistoryEntity h WHERE h.panelSerialNo IN :serialNos "
				+ "AND h.id IN (SELECT MAX(h2.id) FROM SmsDetailsHistoryEntity h2 GROUP BY h2.panelSerialNo)")
		List<SmsDetailsHistoryEntity> findLatestBySerialNos(@Param("serialNos") List<String> serialNos);
		

	// Batch: latest history record per serial number
	@Query("SELECT h FROM SmsDetailsHistoryEntity h WHERE (h.panelSerialNo, h.dateTime) IN "
			+ "(SELECT h2.panelSerialNo, MAX(h2.dateTime) FROM SmsDetailsHistoryEntity h2 "
			+ "WHERE h2.panelSerialNo IN :serials GROUP BY h2.panelSerialNo)")
	List<SmsDetailsHistoryEntity> findLatestByPanelSerialNoIn(@Param("serials") List<String> serialNumbers);
}
