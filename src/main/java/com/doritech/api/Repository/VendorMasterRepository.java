package com.doritech.api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.VendorMasterEntity;

@Repository
public interface VendorMasterRepository extends JpaRepository<VendorMasterEntity, String> {

	Optional<VendorMasterEntity> findByEmail(String email);

	List<VendorMasterEntity> findAllByStatus(String status);

	Optional<VendorMasterEntity> findByVendorName(String vendorName);

	VendorMasterEntity findByVendorId(String vendorId);

	Optional<VendorMasterEntity> findByMobile(String mobileToCheck);

	Page<VendorMasterEntity> findAllByOrderByVendorNameAsc(Pageable pageable);

	@Query("SELECT v FROM VendorMasterEntity v WHERE v.status IN ('Active', 'A') ORDER BY v.vendorName ASC")
	List<VendorMasterEntity> findByStatusInOrderByVendorNameAsc();
}
