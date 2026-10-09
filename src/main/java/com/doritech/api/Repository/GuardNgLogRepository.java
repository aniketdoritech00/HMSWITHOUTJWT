package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.GuardNgLogEntity;
import java.util.List;

@Repository
public interface GuardNgLogRepository extends JpaRepository<GuardNgLogEntity, Long> {
	
	List<GuardNgLogEntity> findByBranchCode(String branchCode);
	
	List<GuardNgLogEntity> findByBranchCodeAndStatus(String branchCode, String status);

}
