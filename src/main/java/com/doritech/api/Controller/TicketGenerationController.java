package com.doritech.api.Controller;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.doritech.api.DTO.TicketDetailsDTO;
import com.doritech.api.Service.TicketGenerationService;

@RestController
@RequestMapping("/api/tickets")
public class TicketGenerationController {
		@Autowired
	private TicketGenerationService ticketService;

	@PostMapping("/save")
	public ResponseEntity<Object> saveTicketDetails(@RequestBody TicketDetailsDTO ticketDetailsDTO) {
		try {
			ResponseEntity<Object> response = ticketService.saveComplaintTicketDetails(ticketDetailsDTO);
			return response;
		} catch (IllegalAccessException | InvocationTargetException e) {
			return new ResponseEntity<>("Error saving ticket details: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("getComplaintsByPanelSerialNumberAndDateTime")
	public ResponseEntity<Object> getComplaintsByPanelSerialNumberAndDateTime(
	        @RequestParam String panelSerialNumber, 
	        @RequestParam String dateTime) {
	    
	    TicketDetailsDTO ticketRequest = new TicketDetailsDTO();
	    ticketRequest.setPanelSerialNumber(panelSerialNumber);
	    ticketRequest.setDateTime(dateTime);
	    
	    System.out.println("Data Send Successfully: " + ticketRequest);
	    return ticketService.getComplaintsByPanelSerialNumberAndDateTime(ticketRequest);
	}
	
}