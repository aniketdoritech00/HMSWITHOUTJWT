package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SocialMediaEntity;

@Repository
public interface SocialMediaRepository extends  JpaRepository<SocialMediaEntity, Long>{

	List<SocialMediaEntity> findByBranchCode(String branchCode);

	SocialMediaEntity findByBranchCodeAndAlertType(String branchCode, String alertType);


}
