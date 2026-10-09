package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.EmployeeMasterEntity;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMasterEntity, String> {
	EmployeeMasterEntity findByEmpId(String empId);
	@Query(value = "select * from employee_master where email = :email and password = :password", nativeQuery = true)
	EmployeeMasterEntity getEmployeeMasterByEmailAndPass(String email, String password);

	@Query(value = "select * from employee_master where email = :email", nativeQuery = true)
	EmployeeMasterEntity getEmployeeMasterByEmailAndMobile(String email);

	
	EmployeeMasterEntity findByEmpName(String empName);
	EmployeeMasterEntity findByEmail(String email);
	List<EmployeeMasterEntity> findByGroupId(String groupId);
	List<EmployeeMasterEntity> findByEmpRole(String empRole);
	List<EmployeeMasterEntity> findByBranch(String branch);
	boolean existsByEmail(String email);
	
	List<EmployeeMasterEntity> findByActiveLoginFlag(String activeLoginFlag);
	
}