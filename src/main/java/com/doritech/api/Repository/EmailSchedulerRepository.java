package com.doritech.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.EmailSchedulerEntity;

@Repository
public interface EmailSchedulerRepository extends JpaRepository<EmailSchedulerEntity, Long> {

	String findByBranchId(String branchId);
	Optional<EmailSchedulerEntity> findByBranchIdAndTimePeriod(String branchId, String timePeriod);


}
