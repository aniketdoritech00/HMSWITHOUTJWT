package com.doritech.api.Common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.MailParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SmsLogEntity;
import com.doritech.api.Framework.ApiMessage;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.SmsLogRepo;
import com.doritech.api.Repository.SmsParamRepo;
import com.doritech.api.Repository.StaticDataRepo;

@Service
public class CommunicationService {

	@Autowired
	EmailLogRepo myEmailRepo;
    @Autowired
    SmsLogRepo smsLogRepo;
    @Autowired
	ServletContext context;
    @Autowired
    StaticDataRepo staticDataRepo;

    @Autowired
    SmsParamRepo smsParamRepo;

    @Autowired
    General general;

    public ResponseEntity sendCommunication(List<SmsLogEntity> smsLogList) {
        ResponseEntity response = new ResponseEntity();
        
        System.err.println("ParamEntityPk");
        try {
            List<SmsLogEntity> sentSmsLogs = new ArrayList<>();
            for (SmsLogEntity smsLog : smsLogList) {
                // Assuming sendSMS updates the SMS log accordingly
                sendSMS(smsLog);
                sentSmsLogs.add(smsLog);
            }
            response.setMessage("SMS sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            // Set the list of sent SMS log entities as payload
            response.setPayload(sentSmsLogs);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Error Occurred: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public ResponseEntity sendEmail(MailParamEntity mailParam, List<EmailLogEntity> emailEntityList) {
        ResponseEntity response = new ResponseEntity();
        List<EmailLogEntity> sentEmailLogs = new ArrayList<>();
        try {
            for (EmailLogEntity emailLog : emailEntityList) {
                // Assuming prepareEmail updates the email log accordingly
                prepareEmail(mailParam, emailLog);
                sentEmailLogs.add(emailLog);
            }
            response.setMessage("Mail sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setPayload(sentEmailLogs);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Error Occurred: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
       
    public List<SmsLogEntity> sendSMS(SmsLogEntity smsLogEntity) {
        List<SmsLogEntity> smsTableList2 = new ArrayList<>();
        if (!general.checkNull(smsLogEntity.getSentTo()).equals("") && !general.checkNull(smsLogEntity.getStatus()).equals("Y")) {
            try {
                String smsUrl = smsLogEntity.getMessageRef();
                System.out.println("URL=" + smsUrl);
                BufferedReader reader = null;
                URLConnection myURLConnection = null;
                URL obj = new URL(smsUrl);
                myURLConnection = obj.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null)
                    System.out.println(response);
                reader.close();
                smsLogEntity.setStatus("Y");
                smsLogRepo.saveAndFlush(smsLogEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return smsTableList2;
    }

    
    public List<EmailLogEntity> prepareEmail(MailParamEntity mailParam, EmailLogEntity emailLogs) throws Exception {
        List<EmailLogEntity> emailLogList = new ArrayList<EmailLogEntity>();
        Session session = generateSession(mailParam);
        System.out.println("Email Id To=[" + general.checkNull(emailLogs.getEmailIdTo()) + "]");
        if (!general.checkNull(emailLogs.getStatus()).equals("Y")
                && !general.checkNull(emailLogs.getEmailIdTo()).trim().equals("")) {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailParam.getSmtpMailId()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailLogs.getEmailIdTo()));
            if (emailLogs.getEmailIdCc() != null && !emailLogs.getEmailIdCc().isEmpty()) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailLogs.getEmailIdCc()));
            }
            
            String currentDate = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

            String updatedSubject = emailLogs.getSubject() + " - " + currentDate;
            message.setSubject(updatedSubject);

            
//            message.setSubject(emailLogs.getSubject());
            message.setText(emailLogs.getBodyText());

            System.out.println("Message:>>>>>>>>>>>" + message);
            try {
                Transport.send(message);
                emailLogs.setStatus("Y");
                myEmailRepo.saveAndFlush(emailLogs);
                emailLogList.add(emailLogs);
                System.out.println("Email log list:" + emailLogList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emailLogList;
    }


	private Session generateSession(MailParamEntity mailParam) throws Exception {
		String ssl = mailParam.getSmtpSsl();
		String smtp_tls = mailParam.getSmtpSsl();
		Properties properties = new Properties();
		properties.put("mail.smtp.host", mailParam.getSmtpServer());
		properties.put("mail.smtp.port", mailParam.getSmtpPort());
		properties.put("mail.smtp.auth", "true");
		if (mailParam.getSmtpServer().contains("gmail")) {

			properties.put("mail.smtp.socketFactory.port", "465");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			properties.put("mail.smtp.socketFactory.fallback", "false");
			properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
			// properties.put("mail.debug", "true");
			System.out.println("MAIL PROPERTIES:>>>>>>>>>>>>>>>>>>>"+properties);
		} else {

			properties.put("mail.smtp.auth", "true");
			// properties.put("mail.debug", "true");
			properties.put("mail.smtp.starttls.enable", smtp_tls.equalsIgnoreCase("Y") ? "true" : "false");
			properties.put("mail.smtp.ssl.enable", ssl.equalsIgnoreCase("Y") ? "true" : "false");
			properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
			System.out.println("MAIL PROPERTIES:<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+properties);
		}
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailParam.getSmptAccountName(), mailParam.getSmtpMailPwd());
			}
		});

		return session;
	}

//	private Message sendEmail(Session session, MailParamEntity mailParam, EmailLogEntity emailLogs) throws Exception {
//		Message message = null;
//		message = new MimeMessage(session);
//
//		// String body = CommonUtils.convertClobtoString(emailLogs.getBodyText());
//		String body = emailLogs.getBodyText();
//
//		MimeBodyPart textPart = new MimeBodyPart();
//		// textPart.setText("Sending POP-SP Email");
//		textPart.setContent(body, "text/html");
//		System.out.println("Mail Param=" + mailParam.toString());
//		String fromAddress = mailParam.getSmptAccountName() + "<" + mailParam.getSmptAccountName() + ">";
//		message.setFrom(new InternetAddress(fromAddress));
//		message.setRecipient(Message.RecipientType.TO,
//				new InternetAddress(general.checkNull(emailLogs.getEmailIdTo())));
//		String[] cCArray = general.checkNull(staticDataRepo.getDescp1ByCode("CC EMAIL", "1")).split(",");
//		for (String cc : cCArray) {
//			System.out.println("cc=" + cc);
//			message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
//		}
//		String[] bcCArray = general.checkNull(staticDataRepo.getDescp1ByCode("BCC EMAIL", "1")).split(",");
//		for (String bcc : bcCArray) {
//			System.out.println("bcc=" + bcc);
//			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
//		}
//		message.setSubject(emailLogs.getSubject());
//
//		if (emailLogs.getMailDoc() != null) {
//			// Blob blob = emailLogs.getMailDoc();
//			// byte [] bytes = blob.getBytes(1l, (int)blob.length());
//			byte[] bytes = emailLogs.getMailDoc();
//			Multipart mp = new MimeMultipart();
//			mp.addBodyPart(textPart);
//			textPart = new MimeBodyPart();
//			textPart.setFileName("Contribution.pdf");
//			textPart.setDisposition(MimeBodyPart.ATTACHMENT);
//			textPart.setContent(bytes, "application/pdf");
//			mp.addBodyPart(textPart);
//			message.setContent(mp);
//		} else {
//			MimeMultipart multipart = new MimeMultipart("related");
//			BodyPart messageBodyPart = new MimeBodyPart();
//			messageBodyPart.setContent(body, "text/html");
//			multipart.addBodyPart(messageBodyPart);
//			messageBodyPart = new MimeBodyPart();
//			DataSource fds = new FileDataSource(context.getRealPath("/WEB-INF/images/" + emailLogs.getType() + ".jpg"));
//			messageBodyPart.setDataHandler(new DataHandler(fds));
//			messageBodyPart.setHeader("Content-ID", "<image>");
//			// add image to the multipart
//			multipart.addBodyPart(messageBodyPart);
//			// put everything together
//			message.setContent(multipart);
//		}
//		return message;
//	}
//    
	private Message sendPlainEmail(Session session, MailParamEntity mailParam, EmailLogEntity emailLogs)
	        throws Exception {
	    Message message = null;
	    message = new MimeMessage(session);
	    
	    System.out.println("session:>>>>>>>>>"+session);
	    
	    // Set From address
	    if (mailParam.getSmtpMailId() != null) {
	        message.setFrom(new InternetAddress(mailParam.getSmtpMailId()));
	    } else {
	        // Handle the case where From address is null
	        throw new IllegalArgumentException("From address is null");
	    }
	    
	    // Set To address
	    if (emailLogs.getEmailIdTo() != null) {
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailLogs.getEmailIdTo()));
	    } else {
	        // Handle the case where To address is null
	        throw new IllegalArgumentException("To address is null");
	    }
	    
	    // Set CC address if not null
	    if (emailLogs.getEmailIdCc() != null) {
	        message.setRecipient(Message.RecipientType.CC, new InternetAddress(emailLogs.getEmailIdCc()));
	    }
	    
	    message.setSubject(emailLogs.getSubject());
	    System.out.println("emailBody text"+emailLogs.getBodyText());
	    message.setText(emailLogs.getBodyText());
	    message.setContent(emailLogs.getBodyText(), "text/html");
	    
	    return message;
	}

}
