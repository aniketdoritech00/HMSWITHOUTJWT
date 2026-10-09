package com.doritech.api.Repository;

import com.doritech.api.Entity.BranchMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMasterEntity, String> {

//	BranchMasterEntity findByBranchDesc(String branchDesc);	

	@Query(value = "SELECT * FROM branch_master WHERE status = 'active' AND branch_code LIKE CONCAT('%', :branchCode, '%')", nativeQuery = true)
	List<BranchMasterEntity> findByBranchCodeContainingPattern(@Param("branchCode") String branchCode);

	@Query(value = "SELECT * FROM branch_master WHERE branch_type = 'ZONE' AND branch_code LIKE CONCAT('%', :branchCode, '%')", nativeQuery = true)
	List<BranchMasterEntity> findByBranchCodeContainingPatternForZone(@Param("branchCode") String branchCode);

	@Query(value = "SELECT * FROM branch_master WHERE branch_type = 'ZONE' AND branch_code LIKE CONCAT('%', :branchCode, '%') AND status = :status", nativeQuery = true)
	List<BranchMasterEntity> findByBranchCodeContainingPatternForZoneWithStatus(@Param("branchCode") String branchCode,
			@Param("status") String status);

	List<BranchMasterEntity> findByParentCode(String parentCode);

	BranchMasterEntity findBybranchCode(String branchCode);

	@Query(value = "SELECT * FROM branch_master b where b.status='active'  AND b.branch_code=:branchCode ", nativeQuery = true)
	List<BranchMasterEntity> findByBranchCode(String branchCode);

	@Query(value = "SELECT * FROM branch_master b where b.status='active'  AND b.branch_type=:branchType ", nativeQuery = true)
	List<BranchMasterEntity> findByBranchType(String branchType);

	@Query(value = "SELECT  branch_code FROM  branch_master WHERE  branch_code =:branchCode UNION SELECT b1.branch_code FROM  branch_master b1 JOIN branch_master b2 ON b1.parent_code = b2.branch_code WHERE b2.branch_code = :branchCode", nativeQuery = true)
	List<String> findAllByBranchCode(String branchCode);

	List<BranchMasterEntity> findByStatus(String status);
//	List<BranchMasterEntity> findByBranchType(String branchType);

	BranchMasterEntity findByIfsc(String ifsc);

	BranchMasterEntity findByIfscAndBank(String ifsc, String bank);

	boolean existsByIfsc(String ifsc);

	List<BranchMasterEntity> findBySystemIntegrator(String vendorId);

	BranchMasterEntity findByBranchDesc(String branchName);

	List<BranchMasterEntity> findByBranchTypeAndBank(String branchType, String bank);

	@Query(value = "SELECT parent.branch_desc AS parent_branch_name " + "FROM branch_master child "
			+ "LEFT JOIN branch_master parent " + "ON child.parent_code = parent.branch_code "
			+ "WHERE child.branch_code = :branchCode", nativeQuery = true)
	String findParentBranchName(@Param("branchCode") String branchCode);

	@Query(value = " SELECT child.branch_code AS branchCode, parent.branch_desc AS parentBranchName FROM branch_master child LEFT JOIN branch_master parent ON child.parent_code = parent.branch_code WHERE child.branch_code IN (:branchCodes) ", nativeQuery = true)
	List<Object> findParentBranchNamesForBranches(@Param("branchCodes") List<String> branchCodes);

	// ONE query for ALL branch codes — replaces per-device loop query
	@Query(value = "SELECT b.branch_code, p.branch_desc FROM branch_master b LEFT JOIN branch_master p ON b.parent_code = p.branch_code WHERE b.branch_code IN (:branchCodes)", nativeQuery = true)
	List<Object[]> findParentBranchNames(@Param("branchCodes") List<String> branchCodes);

	List<BranchMasterEntity> findByBranchCodeIn(List<String> branchCodes);

	@Query(value = "SELECT DISTINCT bm.branch_code, bm.branch_desc " + "FROM email_log el "
			+ "INNER JOIN branch_master bm ON el.branch_code = bm.branch_code " + "WHERE el.branch_code IS NOT NULL "
			+ "AND el.branch_code <> '' " + "AND el.branch_code IN (:branchCodes) "
			+ "ORDER BY bm.branch_desc", nativeQuery = true)
	List<Object[]> getBranchListForEmailLogFilter(@Param("branchCodes") List<String> branchCodes);

}