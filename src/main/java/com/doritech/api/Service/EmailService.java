package com.doritech.api.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.TicketDetailsDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;

@Service
public class EmailService {
	 
	
	 	@Autowired
	    private JavaMailSender javaMailSender;

	    @Value("${spring.mail.username}")
	    private String senderEmail;
	    
	    @Autowired
	    private TicketGenerationService ticketGenerationService;
	    
	    @Autowired
	    private BranchMasterRepository branchMasterRepository;
	    
	    @Autowired
	    private DeviceMasterRepository deviceMasterRepositoy;
	    
	    public ResponseEntity<Object> collectEmails(TicketDetailsDTO ticketDetailsDTO) throws IllegalAccessException, InvocationTargetException, MessagingException {
	    	System.out.println(ticketDetailsDTO.toString());
	    	
	    	BranchMasterEntity branchEntity=branchMasterRepository.findBybranchCode(ticketDetailsDTO.getBranchCode());
	    	if(branchEntity != null) {
//           	    String vendorEmail = deviceMasterRepositoy.findBybranchCode(branchEntity.getBranchCode()).getVendorEmail();
	    		List<String> emails = new ArrayList<>();
				emails.add(branchEntity.getEmail());
				if (branchEntity.getEmail2() != null) {
					emails.add(branchEntity.getEmail2());
				}
				if (branchEntity.getEmail3() != null) {
					emails.add(branchEntity.getEmail3());
				}
				if (branchEntity.getEmail4() != null) {
					emails.add(branchEntity.getEmail4());
				}
				if (branchEntity.getEmail5() != null) {
					emails.add(branchEntity.getEmail5());
				}
				if (branchEntity.getEmail6() != null) {
					emails.add(branchEntity.getEmail6());
				}
//				 if (vendorEmail != null && !vendorEmail.isEmpty()) {
			            emails.add("suyash@doritech.in");
//			    }
				String[] ccEmails = new String[emails.size()];
		        ccEmails = emails.toArray(ccEmails);
		        String recipientEmail = emails.get(0);
		        System.out.println("recipientEmail"+recipientEmail);
//		        return ResponseEntity.ok("Email sent successfully.");
				// String recipientEmail = "divyanshuyadav26680@gmail.com";
	//		        System.out.println("recipientEmail");
			        sendEmail(recipientEmail, ticketDetailsDTO, ccEmails);
			        return ResponseEntity.status(HttpStatus.OK).body(ticketDetailsDTO);
	    	}else {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Branch not found.");
		    }
	    }	    	
	    	
	    
	    public void sendEmail(String recipientEmail, TicketDetailsDTO ticketDetailsRequest, String... ccEmails) throws IllegalAccessException, InvocationTargetException, MessagingException {
	    	MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); // 'true' to send multipart messages (including images)

	        // Set email properties
	        helper.setFrom(senderEmail);
	        helper.setTo(recipientEmail);  
	        helper.setSubject("Ticket Notification");

	        String emailContent = ticketGenerationService.generateSmsForEmail(ticketDetailsRequest);
	        helper.setText(emailContent, true);
	       
	        if (ccEmails != null && ccEmails.length > 0) {
	            helper.setCc(ccEmails);  
	        }
	        javaMailSender.send(message);
	    }
}
