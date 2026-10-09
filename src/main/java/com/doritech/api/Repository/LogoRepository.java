package com.doritech.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.LogoEntity;

@Repository
public interface LogoRepository extends JpaRepository<LogoEntity, Long> {
	Optional<LogoEntity> findByBranchCode(String branchCode);
    LogoEntity findById(String id);
}
