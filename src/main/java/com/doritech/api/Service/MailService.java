package com.doritech.api.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.doritech.api.DTO.BranchComplaintDTO;
import com.doritech.api.DTO.PersonDetailsDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.VendorMasterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.AcroFields.Item;

@Service
public class MailService {

	@Autowired
	EmployeeMasterRepository employeeRepo;

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	BranchMasterRepository branchMasterRepository;

	@Autowired
	DeviceMasterRepository deviceMasterRepository;

	@Autowired
	EmailSchedularService emailSchedularService;
	@Autowired
	DeviceZoneDetailsRepository deviceZoneDetailsRepository;
	@Autowired
	VendorMasterRepository vendorMasterRepository;

	@Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    public boolean sendOTPToEmail(String subject, String message, String to) {

        boolean f = false;
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        session.setDebug(true);

        try {
            if (to == null || to.isBlank()) {
                throw new IllegalArgumentException("Recipient email address is null or empty");
            }
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Sender email (spring.mail.username) is not configured");
            }

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html");

            Transport.send(mimeMessage);
            System.out.println("Email sent successfully to: " + to);
            f = true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return f;
    }
	public String sendComplaintMail(MultipartHttpServletRequest request) throws Exception {
		try {
			BranchComplaintDTO branchComplaintRequest = new ObjectMapper()
					.readValue(request.getParameter("branchComplaint"), BranchComplaintDTO.class);
			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchComplaintRequest.getBranchCode());
			if (branch != null) {
				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
				if (deviceMasterEntity != null) {
					MimeMessage mimeMessage = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

					String htmlMsg = "<html>" + "<head>" + "<style>" + "body { font-family: Arial, sans-serif; }"
							+ ".container { max-width: 600px; margin: 20px auto; padding: 20px; background-color: #f8f8f8; border-radius: 10px; box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.1); }"
							+ "h3 { color: #333; }" + "p { margin-bottom: 10px; }" + "strong { font-weight: bold; }"
							+ "</style>" + "</head>" + "<body>" + "<div class=\"container\">"
							+ "<h3>Complaint Notification</h3>" + "<p>Dear" + deviceMasterEntity.getVendorName()
							+ "</p>"
							+ "<p>Please be informed that a complaint has been registered with the following details:</p>"
							+ "<p><strong>Complaint ID:</strong> " + branchComplaintRequest.getId() + "</p>"
							+ "<p><strong>Branch:</strong> " + branch.getBranchDesc() + " (" + branch.getBranchCode()
							+ ")</p>" + "<p><strong>Product:</strong> " + branchComplaintRequest.getProduct() + "</p>"
							+ "<p><strong>Date of Complaint:</strong> " + branchComplaintRequest.getDateTime() + "</p>"
							+ "<p><strong>Remarks:</strong> " + branchComplaintRequest.getRemarks() + "</p>"
							+ "<p>We request you to address this complaint at the earliest convenience.</p>"
							+ "<p>Thank you for your prompt attention to this matter.</p>" + "<p>Best Regards,</p>"
							+ "<p>Digitals India</p>" + "</div>" + "</body>" + "</html>";

					helper.setText(htmlMsg, true);
					helper.setTo(deviceMasterEntity.getVendorEmail());
					helper.setBcc(deviceMasterEntity.getVendorEmail());
					helper.setSubject("Complaint Notification for Complaint Id: " + branchComplaintRequest.getId());

					for (MultipartFile file : request.getFiles("files")) {
						helper.addAttachment(file.getOriginalFilename(), file);
					}

					mailSender.send(mimeMessage);
					return "Email Sent.";
				} else {
					return "Device details not found for the branch.";
				}
			} else {
				return "Branch not found.";
			}
		} catch (MessagingException e) {
			throw new Exception("Email send failed: " + e.getLocalizedMessage());
		}
	}

	public void sendEmailWithAttachment(String filePath, String to, String subject, String body)
			throws MessagingException, IOException {
		// final String username = "di.internal@digitalsindia.com";
		// final String password = "uzgrfilbcdemyvmw";
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject(subject);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(body);

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.attachFile(filePath);
		multipart.addBodyPart(attachmentPart);

		message.setContent(multipart);

		Transport.send(message);

		System.out.println("Email sent successfully with attachment.");
	}

	public void sendMail(List<String> recipients, String subject, String body, boolean isHtml) {
	    String from = "di.internal@digitalsindia.com";
	    String password = "uzgrfilbcdemyvmw";

	    // Filter valid emails
	    List<String> validRecipients = recipients.stream()
	            .filter(email -> email != null && !email.trim().isEmpty())
	            .toList();

	    if (validRecipients.isEmpty()) {
	        System.out.println("No valid recipients — email not sent.");
	        return;
	    }

	    // SMTP configuration
	    Properties props = new Properties();
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "465");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.ssl.enable", "true");

	    // Create a mail session
	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(from, password);
	        }
	    });

	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        InternetAddress[] recipientAddresses = validRecipients.stream()
	                .map(email -> {
	                    try {
	                        return new InternetAddress(email);
	                    } catch (Exception e) {
	                        return null;
	                    }
	                })
	                .filter(Objects::nonNull)
	                .toArray(InternetAddress[]::new);
	        message.setRecipients(Message.RecipientType.TO, recipientAddresses);
	        message.setSubject(subject);

	        if (isHtml) {
	            message.setContent(body, "text/html; charset=utf-8");
	        } else {
	            message.setText(body);
	        }

	        Transport.send(message);
	        System.out.println("Mail sent successfully to: " + validRecipients);

	    } catch (Exception e) {
	        System.err.println("Mail send failed:");
	        e.printStackTrace();
	    }
	}

	public void sendIssueMail(String branchCode, List<String> issueDetails) throws MessagingException {

	    if (branchCode == null || branchCode.trim().isEmpty()) {
	        System.err.println("branchCode is null or empty, cannot send email.");
	        return;
	    }

	    if (issueDetails == null || issueDetails.isEmpty()) {
	        System.err.println("No issues provided, skipping email sending.");
	        return;
	    }

	    // Format date & time
	    String dateTime = LocalDateTime.now()
	            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

	    DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branchCode);

	    if (deviceMasterEntity == null) {
	        System.err.println("No device found for branchCode = " + branchCode);
	        return;
	    }

	    String deviceId = deviceMasterEntity.getDeviceId();
	    String branchName = deviceMasterEntity.getDeviceName();

	    if (deviceId == null || branchName == null) {
	        System.err.println("Device ID or Branch name is null for branchCode = " + branchCode);
	        return;
	    }

	    for (String issue : issueDetails) {

	        if (issue == null || issue.trim().isEmpty()) {
	            System.err.println("Skipping empty/null issue.");
	            continue;
	        }

	        // Get vendor for this issue safely
	        DeviceZoneDetailsEntity zoneDetails = deviceZoneDetailsRepository
	                .findByDeviceIdAndAlertType(deviceId, issue);

	        if (zoneDetails == null) {
	            System.err.println("No zone details found for issue: " + issue);
	            continue;
	        }

	        String vendorName = zoneDetails.getVendors();
	        if (vendorName == null || vendorName.trim().isEmpty()) {
	            System.err.println("No vendor assigned for issue: " + issue);
	            continue;
	        }

	        // Get vendor email safely
	        Optional<VendorMasterEntity> vendorMaster = vendorMasterRepository.findByVendorName(vendorName);

	        if (vendorMaster.isEmpty() || vendorMaster.get().getEmail() == null) {
	            System.err.println("No vendor email found for vendor: " + vendorName);
	            continue;
	        }

	        String vendorEmail = vendorMaster.get().getEmail();
	        
	        String currentDate = java.time.LocalDate.now()
	                .format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));


	        // Prepare Email
	        String subject = "Branch Issue Notification - " + branchName+" - " +currentDate;

	        String body = "<html>" +
	                "<body>" +
	                "<p>Dear Vendor,</p>" +
	                "<p>This is to notify you that the branch <b>" + branchName + "</b> has reported an issue.</p>" +
	                "<table border='1' cellpadding='6' cellspacing='0'>" +
	                "<tr><td><b>Branch</b></td><td>" + branchName + "</td></tr>" +
	                "<tr><td><b>Issue</b></td><td>" + issue + "</td></tr>" +
	                "<tr><td><b>Date & Time</b></td><td>" + dateTime + "</td></tr>" +
	                "</table>" +
	                "<br/>" +
	                "<p>Please take necessary action.</p>" +
	                "<p>Regards,<br/>Support Team</p>" +
	                "</body>" +
	                "</html>";

	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);

	            helper.setTo(vendorEmail);
	            helper.setSubject(subject);
	            helper.setText(body, true);

	            mailSender.send(message);
	            System.out.println("Email sent to vendor: " + vendorEmail + " for issue: " + issue);

	        } catch (Exception e) {
	            System.err.println("Error sending email to vendor: " + vendorEmail + " for issue: " + issue);
	            e.printStackTrace();
	        }
	    }
	}


}
