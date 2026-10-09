package com.doritech.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.BranchComplainEntity;

@Repository
public interface BranchComplaintRepository extends JpaRepository<BranchComplainEntity, String> {

	Optional<BranchComplainEntity> findById(String id);
}
