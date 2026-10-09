package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.CallLogEntity;

public interface CallLogRepository extends JpaRepository<CallLogEntity, Long> {

	List<CallLogEntity> findByBranchCodeIn(List<String> branchCodes);
	
	List<CallLogEntity> findByIdIn(List<Long> ids);

	long countByEscalationLogId(Long escalationLogId);
	
	List<CallLogEntity> findByEscalationLogIdIn(List<Long> escalationLogIds);

	Page<CallLogEntity> findByBranchCodeIn(List<String> branchCodes, Pageable pageable);

	List<CallLogEntity> findByBranchCodeInOrderByCreatedAtDesc(List<String> branchCodes);
}
