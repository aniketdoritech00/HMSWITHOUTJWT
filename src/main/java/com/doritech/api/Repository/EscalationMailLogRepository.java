package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.EscalationLog;

@Repository
public interface EscalationMailLogRepository extends JpaRepository<EscalationLog, Long> {

	List<EscalationLog> findByTicketNoAndEscLevelOrderByReminderIndexAsc(String ticketNo, String escLevel);

	List<EscalationLog> findByTicketNoOrderBySentAtDesc(String ticketNo);

	@Query(value = "SELECT esc_level FROM escalation_log " + "WHERE ticket_no = :ticketNo "
			+ "ORDER BY sent_at DESC LIMIT 1", nativeQuery = true)
	String findLatestEscLevelByTicketNo(@Param("ticketNo") String ticketNo);

// Existing batch method
	@Query("SELECT e FROM EscalationLog e WHERE e.ticketNo IN :ticketNos "
			+ "AND e.sentAt = (SELECT MAX(e2.sentAt) FROM EscalationLog e2 WHERE e2.ticketNo = e.ticketNo)")
	List<EscalationLog> findLatestEscalationByTicketNos(@Param("ticketNos") List<String> ticketNos);

	@Query(value = "SELECT * FROM escalation_log " + "WHERE ticket_no = :ticketId " + "ORDER BY esc_level DESC "
			+ "LIMIT 1", nativeQuery = true)
	EscalationLog findTopEscalationByTicketId(@Param("ticketId") String ticketId);

@Query(value = "SELECT e1.* FROM escalation_log e1 INNER JOIN (SELECT ticket_no, MAX(esc_level) AS max_level FROM escalation_log WHERE ticket_no IN (:ticketIds)GROUP BY ticket_no) e2 ON e1.ticket_no = e2.ticket_no AND e1.esc_level = e2.max_level", nativeQuery = true)

	List<EscalationLog> findTopEscalationsByTicketIds(@Param("ticketIds") List<String> ticketIds);

//EscalationMailLogRepository.java mein add karo
	List<EscalationLog> findByTicketNoOrderBySentAtAsc(String ticketNo);

	/**
	 * Used for gap-fill: returns ALL escalation log records for a ticket across ALL
	 * levels.
	 */
	List<EscalationLog> findByTicketNo(String ticketNo);

	/**
	 * ✅ NEW — Used for per-level reminder tracking, scoped by criticalFlag.
	 * Critical path → criticalFlag = true Normal path → criticalFlag = false
	 */
	List<EscalationLog> findByTicketNoAndEscLevelAndCriticalFlagOrderByReminderIndexAsc(String ticketNo,
			String escLevel, boolean criticalFlag);

	/**
	 * Used for already-done level detection scoped by criticalFlag.
	 */
	List<EscalationLog> findByTicketNoAndCriticalFlag(String ticketNo, boolean criticalFlag);

	List<EscalationLog> findByTicketNoIn(List<String> ticketNos);
}