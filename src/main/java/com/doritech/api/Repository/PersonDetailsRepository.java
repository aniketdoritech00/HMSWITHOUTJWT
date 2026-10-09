package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.PersonDetailsEntity;
import java.util.List;

@Repository
public interface PersonDetailsRepository  extends JpaRepository<PersonDetailsEntity, Integer>{
	
	List<PersonDetailsEntity> findByBranchCode(String branchCode);

}
