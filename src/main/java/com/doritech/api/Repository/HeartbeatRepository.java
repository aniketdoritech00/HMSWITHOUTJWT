package com.doritech.api.Repository;

import com.doritech.api.Entity.Heartbeat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HeartbeatRepository extends JpaRepository<Heartbeat, Long> {
	@Query(value = "SELECT * FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY panel_serial_number ORDER BY id DESC) AS rn FROM heartbeat WHERE panel_serial_number IN (:serialNos)) t WHERE rn <= 5 ORDER BY panel_serial_number, id DESC", nativeQuery = true)
    List<Heartbeat> findAllByPanelSerialNumberIn(@Param("serialNos") List<String> serialNos);


	 @Query(value = "SELECT h.heartbeat_datetime FROM heartbeat h " +
             "WHERE h.panel_serial_number = :serialNo " +
             "AND h.heartbeat_datetime > :afterDatetime " +
             "ORDER BY h.heartbeat_datetime ASC LIMIT 1",
     nativeQuery = true)
	 LocalDateTime findNextHeartbeatDatetimeAfter(
      @Param("serialNo") String serialNo,
      @Param("afterDatetime") LocalDateTime afterDatetime);


	 @Query(value = "SELECT * FROM heartbeat WHERE panel_serial_number = :serialNo ORDER BY heartbeat_datetime DESC LIMIT 1", nativeQuery = true)
	 Heartbeat findLatestHeartbeat(@Param("serialNo") String serialNo);
}