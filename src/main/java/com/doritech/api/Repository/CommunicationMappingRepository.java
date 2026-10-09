package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.CommunicationMapping;

@Repository
public interface CommunicationMappingRepository extends JpaRepository<CommunicationMapping, Long> {
	
	List<CommunicationMapping> findByPanelSerialNoAndSmsDetailsId(
	            String panelSerialNo, int smsDetailsId);
	 
	 List<CommunicationMapping> findBySmsDetailsIdIn(List<Integer> smsDetailsIds);
}