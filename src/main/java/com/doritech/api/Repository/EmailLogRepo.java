package com.doritech.api.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.EmailLogEntity;

@Repository
@EnableJpaRepositories
public interface EmailLogRepo extends JpaRepository<EmailLogEntity, Long> {

	@Query(value = "select email_id_to, subject, body_text, from_email from email_log where datetime= :date", nativeQuery = true)
	List<Object[]> getEmailsByDate(String date);

	@Query(value = "select employee_code, date_format(dob,'%Y-%m-%d'), date_format(anniversary_date,'%Y-%m-%d'), date_format(joining_date,'%Y-%m-%d'), employee_name from employee_master where date_format(dob, '%m-%d') = :sysDate or date_format(anniversary_date, '%m-%d')= :sysDate or date_format(joining_date, '%m-%d')= :sysDate and active_flag='Y'", nativeQuery = true)
	List<Object[]> getEmployeeByDate(String sysDate);

	@Query(value = "select email_id_to, status, date_format(datetime,'%Y'), email_log_id from email_log where email_id_to = :email and type = :type and emp_id=:empId", nativeQuery = true)
	List<Object[]> getEmailLogByEmailAndType(String email, String type, String empId);

	@Query(value = "select email_id_to, status, date_format(datetime,'%d-%M-%Y'), type, emp_id from email_log where type = :type", nativeQuery = true)
	List<Object[]> getEmailLogByType(String type);

	@Query(value = "select status, type from email_log where email_id_to = :email", nativeQuery = true)
	List<Object[]> getEmailLogByEmail(String email);

	@Query(value = "select count(*) from email_log where type= :type and status='Y'", nativeQuery = true)
	String getWishesCountByType(String type);

	@Query(value = "select employee_name, date_format(dob,'%d'), date_format(anniversary_date,'%d'), date_format(joining_date,'%d') from employee_master where date_format(dob, '%m') = :sysDate or date_format(anniversary_date, '%m')= :sysDate or date_format(joining_date, '%m')= :sysDate", nativeQuery = true)
	List<Object[]> getEventByMonth(String sysDate);

	@Query(value = "delete from email_log where email_log_id=id", nativeQuery = true)
	void deleteEmailLogById(Long id);

	List<EmailLogEntity> findByBranchCode(String branchCode);

	@Query("SELECT COUNT(e) FROM EmailLogEntity e " + "WHERE e.branchCode = :branchCode "
			+ "AND e.bodyText LIKE %:alertType% " + "AND FUNCTION('DATE', e.datetime) = FUNCTION('DATE', CURRENT_DATE)")
	long countTodayEmailByBranchAndAlert(@Param("branchCode") String branchCode, @Param("alertType") String alertType);

	List<EmailLogEntity> findByEmailLogIdIn(List<Long> ids);

	long countByEscalationLogId(Long escalationLogId);

	// escalation_log_id se emails — sent time ke order mein
	List<EmailLogEntity> findByEscalationLogIdOrderByDatetimeAsc(Long escalationLogId);

	List<EmailLogEntity> findByEscalationLogIdIn(List<Long> escalationLogIds);

	List<EmailLogEntity> findByEscalationLogId(Long escLogId);

	Page<EmailLogEntity> findByBranchCodeIn(List<String> branchCodes, Pageable pageable);

	List<EmailLogEntity> findByBranchCodeInOrderByDatetimeDesc(List<String> branchCodes);

	@Query("SELECT e FROM EmailLogEntity e " + "WHERE (:branchCode IS NULL OR e.branchCode = :branchCode) "
			+ "AND (:status IS NULL OR e.status = :status) "
			+ "AND (:emailIdTo IS NULL OR LOWER(e.emailIdTo) LIKE LOWER(CONCAT('%', :emailIdTo, '%'))) "
			+ "AND (:fromDate IS NULL OR e.datetime >= :fromDate) " + "AND (:toDate IS NULL OR e.datetime <= :toDate) "
			+ "ORDER BY e.datetime DESC")
	List<EmailLogEntity> filterEmailLogs(@Param("branchCode") String branchCode, @Param("status") String status,
			@Param("emailIdTo") String emailIdTo, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT DISTINCT e.emailIdTo FROM EmailLogEntity e WHERE e.emailIdTo IS NOT NULL")
	List<String> getDistinctEmails();

	@Query("SELECT DISTINCT e.emailIdTo " + "FROM EmailLogEntity e " + "WHERE e.emailIdTo IS NOT NULL "
			+ "AND e.branchCode IN :branchCodes")
	List<String> getDistinctEmailsByBranchCodes(@Param("branchCodes") List<String> branchCodes);

}
