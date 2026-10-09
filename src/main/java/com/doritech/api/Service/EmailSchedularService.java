package com.doritech.api.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Common.General;
import com.doritech.api.DTO.EmailSchedularDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.EmailSchedulerEntity;
import com.doritech.api.Entity.JspRenderer;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.EmailSchedulerRepository;
import com.doritech.api.Repository.ParamRepository;

@Service
public class EmailSchedularService {

	private static final Logger logger = LoggerFactory.getLogger(EmailSchedularService.class);

	@Autowired
	EmailSchedulerRepository emailSchedulerRepository;

	@Autowired
	ParamRepository paramRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;
	
	@Autowired
	JspRenderer jspRenderer;
	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	SmsDetailsService smsService;

	@Autowired
	private General general;

	public ResponseEntity<Object> saveEmailSchedularService(EmailSchedularDTO emailSchedularDTO) {
		if (emailSchedularDTO == null) {
			logger.warn("Received Empty Email Schedular");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not present in the Email Schedular request");
		} else {
			try {
				Optional<EmailSchedulerEntity> existingSchedulerOptional = emailSchedulerRepository
						.findByBranchIdAndTimePeriod(emailSchedularDTO.getBranchId(),
								emailSchedularDTO.getTimePeriod());
				if (existingSchedulerOptional.isPresent()) {
					logger.warn("Email Schedular with ID {} already exists",
							emailSchedularDTO.getBranchId() + " And " + emailSchedularDTO.getTimePeriod());
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.body("Email Schedular with the same ID already exists");
				} else {
					EmailSchedulerEntity emailSchedulerEntity = new EmailSchedulerEntity();
					BeanUtils.copyProperties(emailSchedularDTO, emailSchedulerEntity);

					Timestamp currentTimestamp = general.getCurrentTimeStamp();
					emailSchedulerEntity.setTimestamp(currentTimestamp.toString());

					emailSchedulerRepository.save(emailSchedulerEntity);
					logger.info("Saved Email Schedular details for ID: {}", emailSchedulerEntity.getId());

					return ResponseEntity.status(HttpStatus.CREATED).body("Email Schedular details saved successfully");
				}
			} catch (Exception e) {
				logger.error("Error saving Email Schedular details", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("An error occurred while saving Email Schedular details");
			}
		}
	}

	public ResponseEntity<Object> getEmailSchedulerByBranchIdAndTimePeriod(String branchId, String timePeriod) {
		try {
			Optional<EmailSchedulerEntity> existingSchedulerOptional = emailSchedulerRepository
					.findByBranchIdAndTimePeriod(branchId, timePeriod);

			if (existingSchedulerOptional.isPresent()) {
				EmailSchedulerEntity emailSchedulerEntity = existingSchedulerOptional.get();
				return ResponseEntity.ok().body(emailSchedulerEntity);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Email Scheduler not found for branch ID " + branchId + " and time period " + timePeriod);
			}
		} catch (Exception e) {
			logger.error("Error retrieving Email Scheduler details", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving Email Scheduler details");
		}
	}

	public com.doritech.api.Entity.ResponseEntity getAllRecords() {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();
		try {
			List<EmailSchedulerEntity> branchList = emailSchedulerRepository.findAll();

			if (branchList.isEmpty()) {
				response.setMessage("Data not found for this time period");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Data fetched successfully");
				response.setPayload(branchList);
			}

		} catch (Exception e) {
			response.setMessage("Error occured while fetching data by this timePeriod ");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}


	public com.doritech.api.Entity.ResponseEntity sendEmailWithAttachment(MultipartFile[] attachments) {
	    com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();
	    String toEmail = null;
	    String subject = null;

	    try {
	        ParamEntity paramEntity = paramRepository.findByCodeAndSerial("EMAIL", "SMTP");
	        toEmail = paramEntity.getDescp1();
	        subject = "Follow-Up: Device Health Report and Performance Insights";
	    } catch (Exception e) {
	        response.setMessage("Error fetching email parameters: " + e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        return response;
	    }

	    if (toEmail == null || !isValidEmail(toEmail)) {
	        response.setMessage("Invalid email address: " + toEmail);
	        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        return response;
	    }

	    String sanitizedTo = sanitizeEmail(toEmail);

	    try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

	        helper.setTo(sanitizedTo);
	        helper.setSubject(subject);
	        helper.setText(createEmailBody(attachments), true);

	        if (attachments != null && attachments.length > 0) {
	            try {
	                for (MultipartFile file : attachments) {
	                    if (file.getContentType().startsWith("image/")) {
	                        ByteArrayDataSource dataSource = new ByteArrayDataSource(file.getBytes(), file.getContentType());
	                        String cid = "image_" + file.getOriginalFilename();
	                        helper.addInline(cid, dataSource);
	                    } else {
	                        ByteArrayDataSource dataSource = new ByteArrayDataSource(file.getBytes(), file.getContentType());
	                        helper.addAttachment(file.getOriginalFilename(), dataSource);
	                    }
	                }
	            } catch (IOException e) {
	                response.setMessage("Error processing attachments: " + e.getLocalizedMessage());
	                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	                return response;
	            }
	        }

	        try {
	            mailSender.send(mimeMessage);
	        } catch (Exception e) {
	            response.setMessage("Error sending email: " + e.getLocalizedMessage());
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            return response;
	        }

	        response.setMessage("Email sent successfully");
	        response.setStatusCode(HttpStatus.OK.value());
	        return response;
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setMessage("Unexpected error: " + e.getLocalizedMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        return response;
	    }
	}

	private boolean isValidEmail(String email) {
	    String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	    return email.matches(emailRegex);
	}

	private String sanitizeEmail(String email) {
	    return email.replaceAll("[^a-zA-Z0-9@._-]", "").trim();
	}

	private String createEmailBody(MultipartFile[] attachments) {
	    String imageTag = "";

	    if (attachments != null && attachments.length > 0) {
	        for (MultipartFile file : attachments) {
	            if (file.getContentType().startsWith("image/")) {
	                String cid = "image_" + file.getOriginalFilename();
	                imageTag = "<img src='cid:" + cid + "' alt='Device Health Image' style='width: 100%; max-width: 600px;'/>";
	                break;
	            }
	        }
	    }

	    return "<html>"
	            + "<body style='font-family: Arial, sans-serif; background-color: #ffffff; color: #f0f0f0; padding: 20px; margin: 0;'>"
	            + "<table align='center' style='background-color: #2a2a2a; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.1); width: 100%; max-width: 800px; word-wrap: break-word;'>"
	            + "<tr><td style='padding-bottom: 20px;'>"
	            + "<p style='color: #f0f0f0; font-size: 12px; line-height: 1.5;'>" + "Dear Sir,<br><br>"
	            + "I hope you're doing well.<br><br>"
	            + "I wanted to follow up and resend the Device Health Report that I shared with you previously through our Hardware Monitoring System (HMS). "
	            + "Attached, you’ll find a comprehensive analysis of your devices' performance, real-time health metrics, and actionable insights to optimize efficiency and ensure seamless operation.<br><br>"
	            + "If you have any questions or need additional support, please feel free to reach out. I’m here to assist you!<br><br>"
	            + "For support, please visit our contact page: <a href='http://122.176.136.51:8082/HMS2/helpPage' style='color: #f0f0f0; text-decoration: underline;'>Contact Support</a>\r\n<br><br>"
	            + "Please checkout the health details through attachment."
	            + "<tr><td>"
	            + imageTag  
	            + "<br><br>" + "Thank you for your continued trust in our services." + "</p>" 
	            + "<tr><td>"
	            + "<p style='font-size: 12px; color: #f0f0f0;'>Best regards,<br>" + "HMS Team<br>"
	            + "Digitals India Security Products Pvt. Ltd.</p>" 
	            + "</table></body></html>";
	}
	
	public void sendHtmlMail(String to, String subject, String htmlContent) throws Exception {
		
		System.out.println("Email sent To --->>>>"+to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }
	
	public Transport openTransport() throws Exception {
		if (!(mailSender instanceof JavaMailSenderImpl)) {
			throw new IllegalStateException("mailSender is not JavaMailSenderImpl; connection reuse not possible");
		}
		JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
		Session session = senderImpl.getSession();
		Transport transport = session.getTransport("smtp");
		transport.connect(senderImpl.getHost(), senderImpl.getPort(), senderImpl.getUsername(),
				senderImpl.getPassword());
		logger.info("[MAIL] SMTP transport connected (reusable) for batch send.");
		return transport;
	}

	public void sendHtmlMailUsingTransport(Transport transport, String to, String subject, String htmlContent)
			throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
	}

	/**
	 * Connection band karta hai — batch complete hone ke baad call karo.
	 */
	public void closeTransport(Transport transport) {
		try {
			if (transport != null && transport.isConnected()) {
				transport.close();
				logger.info("[MAIL] SMTP transport closed.");
			}
		} catch (Exception e) {
			logger.error("[MAIL] Error closing transport: {}", e.getMessage());
		}
	}

	
	public void sendBranchReportsJob(HttpServletRequest request, HttpServletResponse response) {
        try {
            com.doritech.api.Entity.ResponseEntity responseEntity = branchMasterService.getAllSuperHO();

            @SuppressWarnings("unchecked")
            List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

            for (BranchMasterEntity branch : branchList) {
                String branchCode = branch.getBranchCode();
                String branchName = branch.getBranchDesc();

                List<String> emailList = new ArrayList<>();
                addIfNotEmpty(emailList, branch.getEmail());
                addIfNotEmpty(emailList, branch.getEmail2());
                addIfNotEmpty(emailList, branch.getEmail3());
                addIfNotEmpty(emailList, branch.getEmail4());
                addIfNotEmpty(emailList, branch.getEmail5());
                addIfNotEmpty(emailList, branch.getEmail6());

                if (emailList.isEmpty()) continue;

                // set attributes like before
                request.setAttribute("branchCode", branchCode);
                request.setAttribute("branchReport", smsService.getSystemWiseBranchReport(branchCode, "", ""));
                request.setAttribute("uptimeReport", smsService.getUpTimeReport(branchCode));
                request.setAttribute("cctvTatReport", smsService.getTATReport(branchCode, "CCTV"));
                request.setAttribute("fasTatReport", smsService.getTATReport(branchCode, "FIRE ALARM"));
                request.setAttribute("sasTatReport", smsService.getTATReport(branchCode, "SECURITY ALARM"));
                request.setAttribute("bacsTatReport", smsService.getTATReport(branchCode, "BACS"));
                request.setAttribute("etlTatReport", smsService.getTATReport(branchCode, "ETL"));

                String htmlContent = jspRenderer.renderJspToString("/WEB-INF/view/NewFile.jsp", request, response);
                	
                String currentDate = java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
                
                for (String email : emailList) {
                    sendHtmlMail(
                            email,
                            "Branch Report - " + branchName + " (" + branchCode + ")" + currentDate,
                            htmlContent
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addIfNotEmpty(List<String> list, String email) {
        if (email != null && !email.trim().isEmpty()) {
            list.add(email);
        }
    }

    // 👉 Scheduler - run every day at 5 PM
//    @Scheduled(cron = "0 0 17 * * ?")
//    public void scheduleDailyBranchReports() {
//        // Create mock request/response since no real HTTP call here
//        HttpServletRequest request = new MockHttpServletRequest();
//        HttpServletResponse response = new MockHttpServletResponse();
//        sendBranchReportsJob(request, response);
//    }

}
