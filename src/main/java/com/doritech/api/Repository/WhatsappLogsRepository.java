package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.WhatsAppLogs;
@Repository
public interface WhatsappLogsRepository extends JpaRepository<WhatsAppLogs, Long> {

	List<WhatsAppLogs> findByBranchCode(String item);
	
	/* List<WhatsAppLogs> findByBranchCodeIn(List<String> branchCodes); */

	long countByEscalationLogId(Long escalationLogId);

    List<WhatsAppLogs> findByEscalationLogIdIn(List<Long> escalationLogIds);

	Page<WhatsAppLogs> findByBranchCodeIn(List<String> branchCodes, Pageable pageable);

	
	List<WhatsAppLogs> findByBranchCodeInOrderByTimestampDesc(List<String> branchCodes);
}
