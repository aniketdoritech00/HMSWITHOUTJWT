// SiaMasterRepository.java
package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.SiaMaster;

@Repository
public interface SiaMasterRepository extends JpaRepository<SiaMaster, Long> {

	boolean existsBySiaSerialNo(String siaSerialNo);

	boolean existsBySiaSerialNoAndIdNot(String siaSerialNo, Long siaDeviceId);

	Page<SiaMaster> findByPanelSerialNoIn(List<String> panelSerialNos, Pageable pageable);

	List<SiaMaster> findByPanelSerialNoIn(List<String> panelSerialNos);
}