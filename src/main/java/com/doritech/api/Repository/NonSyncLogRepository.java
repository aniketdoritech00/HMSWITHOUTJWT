package com.doritech.api.Repository;

import com.doritech.api.Entity.NonSyncLogEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NonSyncLogRepository extends JpaRepository<NonSyncLogEntity, Long> {

    // Check karne ke liye ki device already open (end = null) non-sync entry hai ya nahi
    Optional<NonSyncLogEntity> findByPanelSerialNoAndNonSyncEndDatetimeIsNull(String panelSerialNo);
    
 // NonSyncLogRepository.java
    @Query("SELECT n FROM NonSyncLogEntity n WHERE n.panelSerialNo IN :serialNos " +
           "AND n.nonSyncStartDatetime < :filterEnd " +
           "AND (n.nonSyncEndDatetime IS NULL OR n.nonSyncEndDatetime > :filterStart)")
    List<NonSyncLogEntity> findByPanelSerialNoInAndDateRange(
            @Param("serialNos") List<String> serialNos,
            @Param("filterStart") LocalDateTime filterStart,
            @Param("filterEnd") LocalDateTime filterEnd);

	Optional<NonSyncLogEntity> findByPanelSerialNoAndNonSyncStartDatetime(String serialNo, LocalDateTime startDatetime);
	
	@Query("SELECT n FROM NonSyncLogEntity n " +
		       "WHERE n.panelSerialNo = :panelSerialNo " +
		       "AND n.nonSyncEndDatetime IS NULL " +
		       "ORDER BY n.nonSyncStartDatetime DESC")
		List<NonSyncLogEntity> findActiveNonSyncByPanelSerialNo(
		        @Param("panelSerialNo") String panelSerialNo);
	
	@Query("SELECT n FROM NonSyncLogEntity n WHERE n.panelSerialNo IN :serialNos AND n.nonSyncEndDatetime IS NULL")
	List<NonSyncLogEntity> findActiveNonSyncByPanelSerialNos(@Param("serialNos") List<String> serialNos);
	List<NonSyncLogEntity> findActiveNonSyncByPanelSerialNoIn(List<String> panelSerialNos);
	
	
}