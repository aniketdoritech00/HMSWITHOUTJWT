package com.doritech.api.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;

@Repository
public interface SmsDetailsRepository extends JpaRepository<SmsDetailsEntity, Integer> {
//	SmsDetailsEntity findByPanelSerialNo(String panelSerialNo);
	Page<SmsDetailsEntity> findByPanelSerialNo(String panelSerialNo, Pageable pageable);

	@Query(value = "SELECT * FROM sms_details WHERE PANELSERIALNO = :panelSerialNo ORDER BY DATETIME DESC LIMIT 1", nativeQuery = true)
	SmsDetailsEntity getLatestRecord(String panelSerialNo);

	List<SmsDetailsEntity> findByPanelSerialNo(String panelSerialNo);

	@Query(value = "SELECT * FROM sms_details WHERE PANELSERIALNO = ?1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
	SmsDetailsEntity findLastRecordByPanelSerialNo(String panelSerialNumber);
	@Query(value = "SELECT * FROM sms_details WHERE PANELSERIALNO = :panelSerialNo ORDER BY DATETIME DESC LIMIT 5", nativeQuery = true)
	List<SmsDetailsEntity> getLastFiveRecords(@Param("panelSerialNo") String panelSerialNo);
	@Query(value = "select max(id) from sms_details", nativeQuery = true)
	int getMaxId();

	@Modifying
	@Transactional
	@Query(value = "UPDATE sms_details set broadcastFlag='Y' where ID =:id", nativeQuery = true)
	void updateFlag(int id);

	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo = :panelSerialNo AND MONTH(s.dateTime) = :month AND YEAR(s.dateTime) = :year")
	List<SmsDetailsEntity> findByPanelSerialNoAndMonth(@Param("panelSerialNo") String panelSerialNo,
			@Param("month") int month, @Param("year") int year);
	
	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo = :panelSerialNo AND WEEK(s.dateTime) = :week AND YEAR(s.dateTime) = :year")
	List<SmsDetailsEntity> findByPanelSerialNoAndWeek(@Param("panelSerialNo") String panelSerialNo,
	                                                  @Param("week") int week, 
	                                                  @Param("year") int year);
	
	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo = :panelSerialNo AND DATE(s.dateTime) = :date")
	List<SmsDetailsEntity> findByPanelSerialNoAndDate(@Param("panelSerialNo") String panelSerialNo,
	                                                  @Param("date") LocalDate date);

	@Query(value = "SELECT * FROM sms_details ORDER BY Id DESC", nativeQuery = true)
	List<SmsDetailsEntity> getAllLatestRecord();
	
	
	@Query(value = "SELECT * FROM sms_details WHERE PANELSERIALNO = :panelSerialNo ORDER BY DATETIME DESC", nativeQuery = true)
	List<SmsDetailsEntity> getByPanelSerialNoAndDateTime(String panelSerialNo);
	
	@Query(value = "SELECT * FROM sms_details " +
            "WHERE PANELSERIALNO = :panelSerialNo " +
            "AND DATE(DATETIME) = CURRENT_DATE " +
            "ORDER BY DATETIME ASC", nativeQuery = true)
List<SmsDetailsEntity> getTodayRecordsByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);

	List<SmsDetailsEntity> findByPanelSerialNoAndDateTimeBetween(String deviceSerialNo, Date startDate, Date endDate);

	
	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo = :panelSerialNo AND DATE(s.dateTime) = CURRENT_DATE")
	List<SmsDetailsEntity> findTodayByPanelSerialNo(@Param("panelSerialNo") String panelSerialNo);
	
	List<SmsDetailsEntity> findByPanelSerialNoIn(List<String> panelSerialNo);

	List<SmsDetailsEntity> findByPanelSerialNoInOrderByPanelSerialNoAscDateTimeAsc(List<String> deviceSerialNos);

	List<SmsDetailsEntity> findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(
			List<String> deviceSerialNos, Date fromDateParsed, Date toDateParsed);

	List<SmsDetailsEntity> findByPanelSerialNoInAndDateTimeGreaterThanEqualOrderByPanelSerialNoAscDateTimeAsc(
			List<String> deviceSerialNos, Date fromDateParsed);

	List<SmsDetailsEntity> findByPanelSerialNoInAndDateTimeLessThanEqualOrderByPanelSerialNoAscDateTimeAsc(
			List<String> deviceSerialNos, Date toDateParsed);
	  @Query(value = "SELECT * FROM (SELECT s.*, ROW_NUMBER() OVER (PARTITION BY s.panelserialno ORDER BY s.id DESC) rn FROM sms_details s WHERE s.panelserialno IN (:panelList)) t WHERE t.rn <= 5 ORDER BY t.panelserialno, t.id DESC", nativeQuery = true)
	    List<SmsDetailsEntity> getLatestFiveRecords(@Param("panelList") List<String> panelList);
//	   
	// SmsDetailsRepository.java
	// ✅ ROW_NUMBER() hata do — simple LIMIT wala query fast hoga
      @Query(value = "SELECT * FROM (SELECT s.*, ROW_NUMBER() OVER (PARTITION BY s.panelserialno ORDER BY s.id DESC) rn FROM sms_details s WHERE s.panelserialno IN (:panelList)) t WHERE t.rn <= 5 ORDER BY t.panelserialno, t.id DESC", nativeQuery = true)
      List<SmsDetailsEntity> getLatestFiveRecordsNew(@Param("panelList") List<String> panelList);
	  
//	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo IN :serialNos ORDER BY s.dateTime DESC")
//    List<SmsDetailsEntity> findAllByPanelSerialNos(@Param("serialNos") List<String> serialNos);
//
//    // Custom default method: group and pick only last 5 records per device
//    default Map<String, List<SmsDetailsEntity>> findLastFiveRecordsForDevices(List<String> serialNos) {
//        List<SmsDetailsEntity> allRecords = findAllByPanelSerialNos(serialNos);
//
//        return allRecords.stream()
//                .collect(Collectors.groupingBy(
//                        SmsDetailsEntity::getPanelSerialNo,
//                        Collectors.collectingAndThen(Collectors.toList(),
//                                list -> list.stream().limit(5).collect(Collectors.toList()))
//                ));
//    }

    @Query(value = "SELECT * FROM sms_details " +
            "WHERE panelserialno = :panelSerialNo " +
            "AND DATETIME < :startOfDay " +
            "ORDER BY DATETIME DESC " +
            "LIMIT 1", nativeQuery = true)
SmsDetailsEntity findLastSmsBeforeDate(@Param("panelSerialNo") String panelSerialNo,
                                    @Param("startOfDay") LocalDateTime startOfDay);
    
    
    
    @Query(value = "SELECT s.* FROM sms_details s INNER JOIN (SELECT panelserialno, MAX(datetime) AS latesttime FROM sms_details WHERE panelserialno IN ('2507210038', '2507300059', '2507300116') GROUP BY panelserialno) latest ON s.panelserialno = latest.panelserialno AND s.datetime = latest.latesttime", nativeQuery = true)
    List<SmsDetailsEntity> getLatestRecordsForDevices(@Param("deviceSerials") List<String> deviceSerials);

    
    // NEW: Find by panel serial number and start date
    List<SmsDetailsEntity> findByPanelSerialNoAndDateTimeGreaterThanEqual(
        String panelSerialNo, Date startDate);
    
    // NEW: Find by panel serial number and end date
    List<SmsDetailsEntity> findByPanelSerialNoAndDateTimeLessThanEqual(
        String panelSerialNo, Date endDate);
    
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE sms_details SET datetime = NOW()", nativeQuery = true)
    int updateAllDateTimeToCurrent();

	Optional<SmsDetailsEntity> findById(int alertGeneratedId);
    
	@Query(value = "SELECT panelserialno, TRIM(BOTH ',' FROM CONCAT(IF(MAX(zone1='A'),'zone1,',''),IF(MAX(zone2='A'),'zone2,',''),IF(MAX(zone3='A'),'zone3,',''),IF(MAX(zone4='A'),'zone4,',''),IF(MAX(zone5='A'),'zone5,',''),IF(MAX(zone6='A'),'zone6,',''),IF(MAX(zone7='A'),'zone7,',''),IF(MAX(zone8='A'),'zone8,',''),IF(MAX(zone9='A'),'zone9,',''),IF(MAX(zone10='A'),'zone10,',''),IF(MAX(zone11='A'),'zone11,',''),IF(MAX(zone12='A'),'zone12,',''),IF(MAX(zone13='A'),'zone13,',''),IF(MAX(zone14='A'),'zone14,',''),IF(MAX(zone15='A'),'zone15,',''),IF(MAX(zone16='A'),'zone16,',''),IF(MAX(zone17='A'),'zone17,',''),IF(MAX(zone18='A'),'zone18,',''),IF(MAX(zone19='A'),'zone19,',''),IF(MAX(zone20='A'),'zone20,',''),IF(MAX(zone21='A'),'zone21,',''),IF(MAX(zone22='A'),'zone22,',''),IF(MAX(zone23='A'),'zone23,',''),IF(MAX(zone24='A'),'zone24,',''),IF(MAX(zone25='A'),'zone25,',''),IF(MAX(zone26='A'),'zone26,',''),IF(MAX(zone27='A'),'zone27,',''),IF(MAX(zone28='A'),'zone28,',''),IF(MAX(zone29='A'),'zone29,',''),IF(MAX(zone30='A'),'zone30,',''),IF(MAX(zone31='A'),'zone31,',''),IF(MAX(zone32='A'),'zone32,',''),IF(MAX(zone33='A'),'zone33,',''),IF(MAX(zone34='A'),'zone34,',''),IF(MAX(zone35='A'),'zone35,',''),IF(MAX(zone36='A'),'zone36,',''),IF(MAX(zone37='A'),'zone37,',''),IF(MAX(zone38='A'),'zone38,',''),IF(MAX(zone39='A'),'zone39,',''),IF(MAX(zone40='A'),'zone40,',''))) AS sms_zones FROM sms_details GROUP BY panelserialno", nativeQuery = true)
	List<Object[]> getSmsZones();
	
	// SmsDetailsHistoryRepository.java
	@Query(value = "SELECT * FROM sms_details WHERE panelserialno = :serialNo ORDER BY id DESC LIMIT 5", nativeQuery = true)
	List<SmsDetailsEntity> getLatestFiveBySerial(@Param("serialNo") String serialNo);

	@Query(value = "SELECT s.datetime FROM sms_details s " +
            "WHERE s.panelserialno = :serialNo " +
            "AND s.datetime > :afterDatetime " +
            "ORDER BY s.datetime ASC LIMIT 1",
    nativeQuery = true)
LocalDateTime findNextSmsDatetimeAfter(
     @Param("serialNo") String serialNo,
     @Param("afterDatetime") LocalDateTime afterDatetime);
	
	
	@Query(value = "SELECT s.* FROM sms_details s INNER JOIN (SELECT panelserialno, MAX(datetime) AS max_time FROM sms_details WHERE panelserialno IN (:serialNos) GROUP BY panelserialno) latest ON s.panelserialno = latest.panelserialno AND s.datetime = latest.max_time", nativeQuery = true)
	List<SmsDetailsEntity> findLatestSmsForSerialNos(@Param("serialNos") List<String> serialNos);


	@Query(value = "SELECT s.* FROM sms_details s INNER JOIN (SELECT PANELSERIALNO, MAX(DATETIME) as maxDt FROM sms_details WHERE PANELSERIALNO IN (:serialNos) GROUP BY PANELSERIALNO) latest ON s.PANELSERIALNO = latest.PANELSERIALNO AND s.DATETIME = latest.maxDt", nativeQuery = true)
		List<SmsDetailsEntity> getLatestRecordsForSerialNos(@Param("serialNos") List<String> serialNos);

	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo IN :serialNos AND s.dateTime = (SELECT MAX(s2.dateTime) FROM SmsDetailsEntity s2 WHERE s2.panelSerialNo = s.panelSerialNo)")
		List<SmsDetailsEntity> findLatestRecordsForSerialNos(@Param("serialNos") List<String> serialNos);

	@Query("SELECT s FROM SmsDetailsEntity s WHERE s.panelSerialNo IN :serialNos " +
		       "AND s.id IN (SELECT MAX(s2.id) FROM SmsDetailsEntity s2 GROUP BY s2.panelSerialNo)")
		List<SmsDetailsEntity> findLatestBySerialNos(@Param("serialNos") List<String> serialNos);
	

	// sms_details table
	@Query("SELECT s FROM SmsDetailsEntity s " +
	       "WHERE s.panelSerialNo IN :serialNos " +
	       "AND s.dateTime = (" +
	       "  SELECT MAX(s2.dateTime) FROM SmsDetailsEntity s2 " +
	       "  WHERE s2.panelSerialNo = s.panelSerialNo)")
	List<SmsDetailsEntity> findLatestForSerialNos(@Param("serialNos") List<String> serialNos);
	
	@Query("SELECT s FROM SmsDetailsEntity s WHERE (s.panelSerialNo, s.dateTime) IN "
			+ "(SELECT s2.panelSerialNo, MAX(s2.dateTime) FROM SmsDetailsEntity s2 "
			+ "WHERE s2.panelSerialNo IN :serials GROUP BY s2.panelSerialNo)")
	List<SmsDetailsEntity> findLatestByPanelSerialNoIn(@Param("serials") List<String> serialNumbers);


}