package com.doritech.api.Repository;

import com.doritech.api.Entity.BOIMasterDataEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository 
public interface BOIMasterDataRepository extends JpaRepository<BOIMasterDataEntity, Long> {

	BOIMasterDataEntity findByBranchCode(String branchCode);

	List<BOIMasterDataEntity> findByFgmo(String fgmo);

	List<BOIMasterDataEntity> findByZoneName(String zoneName);

	List<BOIMasterDataEntity> findByBankName(String bankName);



	List<BOIMasterDataEntity> findByFgmoAndBankName(String fgmo, String bankName);

	List<BOIMasterDataEntity> findByZoneNameAndBankName(String zoneName, String bankName);
	
	List<BOIMasterDataEntity> findByBranchCodeAndBankName(String branchCode, String bankName);
	
	
	
	// 1. All distinct bank names
    @Query("SELECT DISTINCT b.bankName FROM BOIMasterDataEntity b WHERE b.bankName IS NOT NULL ORDER BY b.bankName")
    List<String> findAllDistinctBankNames();

    // 2. All distinct fgmo by bank_name
    @Query("SELECT DISTINCT b.fgmo FROM BOIMasterDataEntity b WHERE b.bankName = :bankName AND b.fgmo IS NOT NULL ORDER BY b.fgmo")
    List<String> findDistinctFgmoByBankName(@Param("bankName") String bankName);

    // 3a. Distinct zone by bank name only
    @Query("SELECT DISTINCT b.zoneName FROM BOIMasterDataEntity b WHERE b.bankName = :bankName AND b.zoneName IS NOT NULL ORDER BY b.zoneName")
    List<String> findDistinctZoneByBankName(@Param("bankName") String bankName);

    // 3b. Distinct zone by fgmo only
    @Query("SELECT DISTINCT b.zoneName FROM BOIMasterDataEntity b WHERE b.fgmo = :fgmo AND b.zoneName IS NOT NULL ORDER BY b.zoneName")
    List<String> findDistinctZoneByFgmo(@Param("fgmo") String fgmo);

    // 3c. Distinct zone by both bank name and fgmo
    @Query("SELECT DISTINCT b.zoneName FROM BOIMasterDataEntity b WHERE b.bankName = :bankName AND b.fgmo = :fgmo AND b.zoneName IS NOT NULL ORDER BY b.zoneName")
    List<String> findDistinctZoneByBankNameAndFgmo(@Param("bankName") String bankName, @Param("fgmo") String fgmo);
}
