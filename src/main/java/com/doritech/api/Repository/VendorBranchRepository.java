package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.VendorBranchMappedEntity;
@Repository
public interface VendorBranchRepository extends JpaRepository<VendorBranchMappedEntity, Integer> {
}
