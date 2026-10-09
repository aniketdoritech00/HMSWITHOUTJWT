// SiaPhysicalZoneMasterRepository.java
package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SiaPhysicalZoneMaster;

@Repository
public interface SiaPhysicalZoneMasterRepository extends JpaRepository<SiaPhysicalZoneMaster, Long> {


    List<SiaPhysicalZoneMaster> findBySiaDeviceIdAndStatus(Long siaDeviceId, String status);

	List<SiaPhysicalZoneMaster> findBySiaDeviceId(Long id);
    
	List<SiaPhysicalZoneMaster> findBySiaDeviceIdIn(List<Long> siaDeviceIds);
	
	List<SiaPhysicalZoneMaster> findBySiaDeviceIdInAndStatus(List<Long> siaDeviceIds, String string);
}