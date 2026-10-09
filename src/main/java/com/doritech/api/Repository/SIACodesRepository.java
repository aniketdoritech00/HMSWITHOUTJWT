package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SIACodesEntity;
@Repository
public interface SIACodesRepository extends JpaRepository<SIACodesEntity, Integer> {

}
