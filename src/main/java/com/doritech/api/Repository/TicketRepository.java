package com.doritech.api.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ==================== ALERT TYPE FILTER (shared condition) ====================
    // alertTypes me "__ALL__" aaye to koi filter nahi lagta
    String ALERT_TYPE_FILTER =
        "AND ('__ALL__' IN (:alertTypes) OR EXISTS (" +
        "    SELECT 1 FROM device_zone_details z " +
        "    JOIN device_master d ON d.device_id = z.device_id " +
        "    WHERE d.device_serial_no = t.device_serial_no " +
        "    AND z.input_type = t.group_name " +
        "    AND z.alert_type IN (:alertTypes)" +
        ")) ";

    /**
     * Find all ticket numbers where at least one row is OPEN
     */
    @Query(value = "SELECT DISTINCT ticket_no FROM ticket WHERE ticket_status = 'O'", nativeQuery = true)
    List<String> findOpenTicketNos();

    @Query(value = "SELECT DISTINCT ticket_no FROM ticket", nativeQuery = true)
    List<String> findTicketNos();
    /**
     * Find all rows for a given ticket_no
     */
    List<Ticket> findByTicketNo(String ticketNo);


    /**
     * Get all rows of tickets that are OPEN
     * (If ANY zone row is OPEN, return all rows of that ticket)
     */
    @Query(value = " SELECT * FROM ticket t WHERE t.ticket_no IN (SELECT ticket_no FROM ticket WHERE ticket_status = 'O')", nativeQuery = true)
    List<Ticket> findAllRowsOfOpenTickets();


    /**
     * Find ticket numbers where all zones are CLOSED
     */
    @Query(value = "SELECT ticket_no FROM ticket GROUP BY ticket_no HAVING SUM(CASE WHEN ticket_status = 'O' THEN 1 ELSE 0 END) = 0 ", nativeQuery = true)
    List<String> findFullyClosedTicketNos();


    /**
     * Get alert_generated_id for an OPEN ticket_no
     */
    @Query(value = "SELECT alert_generated_id FROM ticket WHERE ticket_no = :ticketNo AND ticket_status = 'O'", nativeQuery = true)
    List<Long> findAlertGeneratedIdsByOpenTicketNo(@Param("ticketNo") String ticketNo);


    @Query(value = "SELECT t.* FROM ticket t WHERE t.ticket_status = 'O' AND (t.resolved_type IS NULL OR t.resolved_type = '') GROUP BY t.ticket_no ORDER BY t.created_at DESC", nativeQuery = true)
        List<Ticket> findOpenTicketsSummary();


    // Find ticketNo by deviceSerialNo + zone number + alert_gernated_id (smsId)
    @Query(
            value = "SELECT * FROM ticket " +
                    "WHERE device_serial_no = :deviceSerialNo " +
                    "AND zone = :zone " +
                    "AND alert_gernated_id = :alertGeneratedId " +   // note: typo in your DB column is intentional
                    "ORDER BY id DESC LIMIT 1",
            nativeQuery = true
        )
        Ticket findTicketNoBySerialZoneAndAlertId(
                @Param("deviceSerialNo") String deviceSerialNo,
                @Param("zone") String zone,
                @Param("alertGeneratedId") Long alertGeneratedId);

        List<Ticket> findByDeviceSerialNoIn(List<String> deviceSerialNos);

		Ticket findByZoneAndDeviceSerialNoAndAlertGeneratedId(String zoneNum, String panelSerialNo, int id);
		// Status aur Date dono filter ke saath
//		@Query("SELECT t FROM Ticket t WHERE t.deviceSerialNo IN :deviceSerialNos " +
//				 "AND (:ticketNo IS NULL OR :ticketNo = '' OR t.ticketNo = :ticketNo) " +
//		       "AND (:status IS NULL OR :status = '' OR t.ticketStatus = :status) " +
//		       "AND (:fromDate IS NULL OR t.createAt >= :fromDate) " +
//		       "AND (:toDate IS NULL OR t.createAt <= :toDate) order by id desc")
//		List<Ticket> findTicketsByFilters(
//		    @Param("deviceSerialNos") List<String> deviceSerialNos,
//		    @Param("ticketNo") String ticketNo,
//		    @Param("status") String status,
//		    @Param("fromDate") LocalDateTime fromDate,
//		    @Param("toDate") LocalDateTime toDate
//		);

		@Query(value = "SELECT * FROM ticket " +
			    "WHERE device_serial_no IN (:deviceSerialNos) " +
			    "AND (:ticketNo IS NULL OR ticket_no = :ticketNo) " +
			    "AND (:status IS NULL OR ticket_status = :status) " +
			    "AND (:fromDate IS NULL OR create_at >= :fromDate) " +
			    "AND (:toDate IS NULL OR create_at <= :toDate) " +
			    "ORDER BY id DESC",
			    countQuery = "SELECT COUNT(*) FROM ticket " +
			    "WHERE device_serial_no IN (:deviceSerialNos) " +
			    "AND (:ticketNo IS NULL OR ticket_no = :ticketNo) " +
			    "AND (:status IS NULL OR ticket_status = :status) " +
			    "AND (:fromDate IS NULL OR create_at >= :fromDate) " +
			    "AND (:toDate IS NULL OR create_at <= :toDate)",
			    nativeQuery = true)
			Page<Ticket> findTicketsByFilters(
			    @Param("deviceSerialNos") List<String> deviceSerialNos,
			    @Param("ticketNo") String ticketNo,
			    @Param("status") String status,
			    @Param("fromDate") LocalDateTime fromDate,
			    @Param("toDate") LocalDateTime toDate,
			    Pageable pageable
			);

		// ==================== ALL TICKETS (alertTypes filter added) ====================
		@Query(value = "SELECT t.ticket_no FROM ticket t " +
			       "WHERE t.device_serial_no IN :deviceSerialNos " +
			       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
			       "AND t.create_at BETWEEN :from AND :to " +
			       ALERT_TYPE_FILTER +
			       "GROUP BY t.ticket_no " +
			       "ORDER BY " +
			       "MAX(CASE WHEN t.ticket_status = 'O' THEN 1 ELSE 0 END) DESC, " +
			       "MAX(t.id) DESC",
			       countQuery = "SELECT COUNT(DISTINCT t.ticket_no) FROM ticket t " +
			       "WHERE t.device_serial_no IN :deviceSerialNos " +
			       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
			       "AND t.create_at BETWEEN :from AND :to " +
			       ALERT_TYPE_FILTER,
			       nativeQuery = true)
			Page<String> findDistinctTicketNosByFilters(
			    @Param("deviceSerialNos") List<String> deviceSerialNos,
			    @Param("ticketNo") String ticketNo,
			    @Param("from") LocalDateTime from,
			    @Param("to") LocalDateTime to,
			    @Param("alertTypes") List<String> alertTypes,
			    Pageable pageable
			);

		// ==================== OPEN TICKETS ONLY (alertTypes filter added) ====================
		@Query(value = "SELECT t.ticket_no FROM ticket t " +
		       "WHERE t.device_serial_no IN :deviceSerialNos " +
		       "AND t.create_at BETWEEN :from AND :to " +
		       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
		       ALERT_TYPE_FILTER +
		       "AND t.ticket_no NOT IN (" +
		       "    SELECT DISTINCT t2.ticket_no FROM ticket t2 " +
		       "    WHERE t2.ticket_status = 'C'" +
		       ") " +
		       "GROUP BY t.ticket_no " +
		       "ORDER BY MAX(t.id) DESC",
		       countQuery = "SELECT COUNT(DISTINCT t.ticket_no) FROM ticket t " +
		       "WHERE t.device_serial_no IN :deviceSerialNos " +
		       "AND t.create_at BETWEEN :from AND :to " +
		       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
		       ALERT_TYPE_FILTER +
		       "AND t.ticket_no NOT IN (" +
		       "    SELECT DISTINCT t2.ticket_no FROM ticket t2 " +
		       "    WHERE t2.ticket_status = 'C'" +
		       ")",
		       nativeQuery = true)
		Page<String> findDistinctOpenTicketNos(
		    @Param("deviceSerialNos") List<String> deviceSerialNos,
		    @Param("ticketNo") String ticketNo,
		    @Param("from") LocalDateTime from,
		    @Param("to") LocalDateTime to,
		    @Param("alertTypes") List<String> alertTypes,
		    Pageable pageable
		);

		// ==================== CLOSED TICKETS ONLY (alertTypes filter added) ====================
		@Query(value = "SELECT t.ticket_no FROM ticket t " +
		       "WHERE t.device_serial_no IN :deviceSerialNos " +
		       "AND t.create_at BETWEEN :from AND :to " +
		       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
		       ALERT_TYPE_FILTER +
		       "AND t.ticket_no IN (" +
		       "    SELECT DISTINCT t2.ticket_no FROM ticket t2 " +
		       "    WHERE t2.ticket_status = 'C'" +
		       ") " +
		       "GROUP BY t.ticket_no " +
		       "ORDER BY MAX(t.id) DESC",
		       countQuery = "SELECT COUNT(DISTINCT t.ticket_no) FROM ticket t " +
		       "WHERE t.device_serial_no IN :deviceSerialNos " +
		       "AND t.create_at BETWEEN :from AND :to " +
		       "AND (:ticketNo IS NULL OR t.ticket_no = :ticketNo) " +
		       ALERT_TYPE_FILTER +
		       "AND t.ticket_no IN (" +
		       "    SELECT DISTINCT t2.ticket_no FROM ticket t2 " +
		       "    WHERE t2.ticket_status = 'C'" +
		       ")",
		       nativeQuery = true)
		Page<String> findDistinctClosedTicketNos(
		    @Param("deviceSerialNos") List<String> deviceSerialNos,
		    @Param("ticketNo") String ticketNo,
		    @Param("from") LocalDateTime from,
		    @Param("to") LocalDateTime to,
		    @Param("alertTypes") List<String> alertTypes,
		    Pageable pageable
		);

		@Query("SELECT t FROM Ticket t WHERE t.ticketNo IN :ticketNos")
		List<Ticket> findTicketsByTicketNos(@Param("ticketNos") List<String> ticketNos);

		@Query("SELECT t FROM Ticket t WHERE t.deviceSerialNo IN :serialNos " +
			       "AND t.createAt BETWEEN :startDate AND :endDate")
			List<Ticket> findByDeviceSerialNoInAndCreateAtBetween(
			    @Param("serialNos") List<String> serialNos,
			    @Param("startDate") LocalDateTime startDate,
			    @Param("endDate") LocalDateTime endDate
			);

			@Query("SELECT DISTINCT t FROM Ticket t WHERE t.ticketStatus = 'O' AND t.deviceSerialNo IN :deviceSerialNos")
			List<Ticket> findOpenTicketsBydeviceSerialNos(@Param("deviceSerialNos") Set<String> deviceSerialNos);

			@Query("SELECT DISTINCT t FROM Ticket t WHERE t.deviceSerialNo IN :deviceSerialNos")
			List<Ticket> findAllTicketsBydeviceSerialNos(@Param("deviceSerialNos") Set<String> deviceSerialNos);


			@Query("SELECT DISTINCT t FROM Ticket t WHERE " +
				       "(:status IS NULL OR t.ticketStatus = :status) AND " +
				       "t.deviceSerialNo IN :deviceSerialNos")
				List<Ticket> findTicketsByStatusAndDeviceSerialNos(
				    @Param("status") String status,
				    @Param("deviceSerialNos") Set<String> deviceSerialNos);

		List<Ticket> findByDeviceSerialNo(String panelSerialNo);


		@Query("SELECT DISTINCT t.ticketNo FROM Ticket t " +
		           "WHERE t.ticketStatus = 'O' " +
		           "AND t.zoneStatus = 'O' " +
		           "AND t.deviceSerialNo IN :deviceSerials")  // ← Filter by device serials
		    List<String> findOpenTicketNosByDeviceSerials(
		            @Param("deviceSerials") List<String> deviceSerials);

		@Query("SELECT DISTINCT t.ticketNo FROM Ticket t")
		List<String> findAllDistinctTicketNos();
}