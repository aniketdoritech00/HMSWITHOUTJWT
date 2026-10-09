package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.AlarmAuditLogEntity;

@Repository
public interface AlarmAuditLogRepository extends JpaRepository<AlarmAuditLogEntity, Integer> {

	@Query(value = "SELECT * FROM hms.alarm_audit_log WHERE date_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR);", nativeQuery = true)
	List<AlarmAuditLogEntity> findAllLatestRecords();

	@Query(value = "SELECT count(remarks) FROM hms.alarm_audit_log WHERE date_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) AND remarks = ?1", nativeQuery = true)
	int getByRemarksCount(String remarks);

	@Query(value = "SELECT count(remarks) FROM hms.alarm_audit_log WHERE remarks = ?1", nativeQuery = true)
	int getAllLatestAuditCount(String remarks);

	@Query(value = "SELECT  remarks, COUNT(*) AS count FROM  hms.alarm_audit_log WHERE   remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING')  AND DATE(date_time) = :dateTime  GROUP BY remarks", nativeQuery = true)
	List<Object[]> getStatusCountByDate(String dateTime);

	@Query(value = "SELECT  remarks, COUNT(*) AS count FROM  hms.alarm_audit_log WHERE   remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING')  AND MONTH(date_time) = :month AND YEAR(date_time) = :year GROUP BY remarks", nativeQuery = true)
	List<Object[]> getStatusCountByMonth(String month, String year);

	@Query(value = "SELECT  remarks, COUNT(*) AS count FROM  hms.alarm_audit_log WHERE   remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING')  AND YEAR(date_time) = :year GROUP BY remarks", nativeQuery = true)
	List<Object[]> getStatusCountByYear(String year);

	@Query(value = "SELECT * FROM hms.alarm_audit_log WHERE DATE(date_time) = :dateTime", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsByDate(String dateTime);

	@Query(value = "SELECT * FROM hms.alarm_audit_log WHERE  MONTH(date_time) =:month AND YEAR(date_time) =:year", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsByMonth(String month, String year);

	@Query(value = "SELECT * FROM hms.alarm_audit_log WHERE  YEAR(date_time) =:year", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsByYear(String year);

	@Query(value = "SELECT  remarks, COUNT(*) AS count FROM  hms.alarm_audit_log WHERE   remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING')  AND active_zone=:activeZone GROUP BY remarks", nativeQuery = true)
	List<Object[]> getStatusCountByActiveZones(String activeZone);

	@Query(value = "SELECT * FROM  hms.alarm_audit_log WHERE   remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING')  AND active_zone=:activeZone GROUP BY remarks", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsByActiveZone(String activeZone);

	List<AlarmAuditLogEntity> findByBranchCode(String branchCode);

	@Query(value = "SELECT remarks, COUNT(*) AS count FROM alarm_audit_log WHERE branch_code IN (:branchCodes) AND remarks IN ('ACKNOWLEDGED', 'FALSE ALARM', 'SECURITY VENDOR INFORMED', 'TESTING') AND DATE(date_time) BETWEEN :startDate AND :endDate GROUP BY remarks", nativeQuery = true)
	List<Object[]> getStatusCountByDateRange(String startDate, String endDate , List<String> branchCodes);

	@Query(value = "SELECT * FROM alarm_audit_log WHERE DATE(date_time) BETWEEN :startDate AND :endDate AND branch_code IN (:branchCodes);", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsByDateRange(String startDate, String endDate , List<String> branchCodes);

	@Query(value = "SELECT * FROM alarm_audit_log WHERE branch_code IN (:branchCodes) AND YEAR(STR_TO_DATE(date_time, '%Y-%m-%d %H:%i:%s')) = YEAR(CURRENT_DATE) AND MONTH(STR_TO_DATE(date_time, '%Y-%m-%d %H:%i:%s')) = MONTH(CURRENT_DATE)", nativeQuery = true)
	List<AlarmAuditLogEntity> getRecordsOfCurrentMonth(List<String> branchCodes);

	@Query(value = "SELECT * \r\n" + "FROM alarm_audit_log \r\n"
			+ "WHERE branch_code IN (:branchCodes) AND YEAR(date_time) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)\r\n"
			+ "AND MONTH(date_time) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH);\r\n", nativeQuery = true)
	List<AlarmAuditLogEntity> getAllRecordsOfPreviousMonth(List<String> branchCodes);

	@Query(nativeQuery = true, value = "SELECT * FROM alarm_audit_log WHERE date_time >= CURDATE() AND branch_code IN (:branchCodes);")
	List<AlarmAuditLogEntity> getRecordsOfToday(List<String> branchCodes);

	@Query(nativeQuery = true, value = "SELECT * FROM alarm_audit_log WHERE  branch_code IN (:branchCodes) AND DATE(date_time) = CURDATE() - INTERVAL 1 DAY")
	List<AlarmAuditLogEntity> getRecordsOfYesterday(List<String> branchCodes);

	@Query(nativeQuery = true, value = "SELECT * FROM alarm_audit_log WHERE branch_code IN (:branchCodes) AND date_time >= CURDATE() - INTERVAL 6 DAY")
	List<AlarmAuditLogEntity> getRecordsOfLast7Days(List<String> branchCodes);

	@Query(nativeQuery = true, value = "SELECT * FROM alarm_audit_log WHERE branch_code IN (:branchCodes) AND date_time >= CURDATE() - INTERVAL 29 DAY")
	List<AlarmAuditLogEntity> getRecordsOfLast30Days(List<String> branchCodes);


	@Query(value = "SELECT aal.*,dzd.zone_desired_name\r\n" + "FROM alarm_audit_log aal\r\n"
			+ "JOIN device_master dm ON aal.branch_code = dm.branch_code\r\n"
			+ "JOIN device_zone_details dzd ON dm.device_id = dzd.device_id\r\n"
			+ "WHERE aal.branch_code IN (:branchCodes) AND DATE(aal.date_time) = CURDATE()\r\n"
			+ "AND TIME(aal.date_time) BETWEEN :startTime AND :endTime \r\n"
			+ "AND dzd.alert_type = aal.active_zone;", nativeQuery = true)
	List<Object[]> getAllRecordsByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime ,List<String> branchCodes);


	List<AlarmAuditLogEntity> findByBranchCodeIn(List<String> branchCodes);
	
	@Query(value = "SELECT "+"a.id, \r\n"
			+ "       a.active_zone, \r\n"
			+ "       a.date_time, \r\n"
			+ "       a.device_id, \r\n"
			+ "       a.remarks, \r\n"
			+ "       e.emp_name AS user, \r\n"
			+ "       a.address, \r\n"
			+ "       a.branch_code, \r\n"
			+ "       a.mobile, \r\n"
			+ "       a.vendor_name\r\n" +
            "FROM hms_boi.alarm_audit_log a\r\n" +
            "JOIN hms_boi.employee_master e ON a.user = e.emp_id\r\n" +
            "WHERE a.remarks = 'ACKNOWLEDGED' \r\n" +
            "AND a.branch_code IN (:branchCodes);", nativeQuery = true)
List<AlarmAuditLogEntity> findAcknowledgedAlerts(@Param("branchCodes") List<String> branchCodes);
}
