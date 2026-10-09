package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.EscalationMatrix;

@Repository
public interface EscalationMatrixRepository extends JpaRepository<EscalationMatrix, Long> {

	/**
	 * Find escalation level by elapsed minutes. L1 = <= 1440 min (24 hrs) L2 = <=
	 * 2880 min (48 hrs) L3 = <= 4320 min (72 hrs)
	 */
	@Query(value = "SELECT * FROM escalation_matrix WHERE esc_time_range <= :elapsedMinutes ORDER BY esc_time_range DESC", nativeQuery = true)
	List<EscalationMatrix> findApplicableLevels(@Param("elapsedMinutes") int elapsedMinutes);

	/**
	 * Find escalation config by exact level label (L1, L2, L3)
	 */
	EscalationMatrix findByEscLevel(String escLevel);
	
	

    /**
     * NEW – fetch ALL levels sorted by threshold_minutes ASC.
     * Used in TicketEscalationService to replace hardcoded L1/L2/L3 thresholds.
     *
     * Requires the following columns in escalation_matrix:
     *   threshold_minutes  INT   (replaces the old hardcoded 1440 / 2880 / 4320)
     *   no_of_reminders    INT
     *   reminder_time      INT   (gap in minutes between reminders)
     */
    List<EscalationMatrix> findAllByOrderByEscTimeRangeAsc();
	@Query(value = "SELECT * FROM escalation_matrix WHERE critical_flag=:i", nativeQuery = true)
	List<EscalationMatrix> findByCriticalFlag(int i);

	EscalationMatrix findByEscLevelAndCriticalFlag(String escalationLevel, boolean isCritical);
}