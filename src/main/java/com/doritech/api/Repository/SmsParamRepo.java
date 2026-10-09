package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SmsParamEntity;

@Repository
public interface SmsParamRepo extends JpaRepository<SmsParamEntity, Long> {

}
