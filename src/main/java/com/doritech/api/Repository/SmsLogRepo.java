package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SmsLogEntity;

@Repository
@EnableJpaRepositories
public interface SmsLogRepo extends JpaRepository<SmsLogEntity, Long>{
	
	@Query(value = "select sent_to, status, date_format(datetime,'%Y'),SMS_LOG_ID from sms_log where sent_to = :phone and type = :type", nativeQuery = true)
	List<Object[]> getSmsLogByPhoneAndType(String phone, String type);
	
	List<SmsLogEntity> findByBranchCode(String branchCode);
	
	 List<SmsLogEntity> findBySmsLogIdIn(List<Long> ids);

	long countByEscalationLogId(Long escalationLogId);
    List<SmsLogEntity> findByEscalationLogIdIn(List<Long> escalationLogIds);

	Page<SmsLogEntity> findByBranchCodeIn(List<String> branchCodes, Pageable pageable);

	List<SmsLogEntity> findByBranchCodeInOrderBySmsLogIdDesc(List<String> branchCodes);


}
