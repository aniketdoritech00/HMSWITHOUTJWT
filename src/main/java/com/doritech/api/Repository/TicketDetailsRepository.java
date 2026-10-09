package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.TicketDetailsEntity;
@Repository
public interface TicketDetailsRepository extends JpaRepository<TicketDetailsEntity, Long> {

 
    TicketDetailsEntity findByTicketId(String ticketId);

	boolean existsByTicketId(String ticketNumber);
	
	TicketDetailsEntity findByPanelSerialNumberAndDateTime(String panelSerialNumber, String dateTime);
}
