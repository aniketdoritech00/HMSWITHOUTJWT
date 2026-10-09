package com.doritech.api.Controller;

import java.lang.reflect.InvocationTargetException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.TicketDetailsDTO;
import com.doritech.api.Service.EmailService;

@RestController
@RequestMapping("/api/tickets")
public class EmailTicketController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/send-email")
	public ResponseEntity<Object> collectEmails(@RequestBody TicketDetailsDTO ticketDetailsDTO) throws IllegalAccessException, InvocationTargetException, MessagingException {
		return emailService.collectEmails(ticketDetailsDTO);
	}
	
	
}
