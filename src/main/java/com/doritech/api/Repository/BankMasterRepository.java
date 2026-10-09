package com.doritech.api.Repository;

import com.doritech.api.Entity.BankMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankMasterRepository extends JpaRepository<BankMasterEntity, String> {
    Optional<BankMasterEntity> findByBankCode(String bankCode);
    boolean existsByBankCode(String bankCode);
	List<BankMasterEntity> findByBankCodeIn(List<String> bankCodes);
}