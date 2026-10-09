package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.DashboardEntity;
@Repository
public interface DashboardRepository extends  JpaRepository<DashboardEntity, Integer>{

	DashboardEntity findByEmpId(String empId);

}
