package com.doritech.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.AutoRefreshConfigEntity;

@Repository
public interface AutoRefreshConfigRepository extends JpaRepository<AutoRefreshConfigEntity, Long> {

	Optional<AutoRefreshConfigEntity> findByEmpId(String empId);
}