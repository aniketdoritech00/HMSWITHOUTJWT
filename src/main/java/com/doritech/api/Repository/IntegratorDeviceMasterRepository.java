package com.doritech.api.Repository;

import com.doritech.api.Entity.IntegratorDeviceMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IntegratorDeviceMasterRepository extends JpaRepository<IntegratorDeviceMasterEntity, Integer> {

	IntegratorDeviceMasterEntity findByPanelSerialNo(String panelSerialNo);

}
