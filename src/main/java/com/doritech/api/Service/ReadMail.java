package com.doritech.api.Service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.doritech.api.Common.CommunicationService;
import com.doritech.api.Common.General;
import com.doritech.api.DTO.SocialMediaDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.MailParamEntity;
import com.doritech.api.Entity.ManualSyncLog;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ReadMailEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.WhatsAppLogs;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.MailParamRepo;
import com.doritech.api.Repository.ManualSyncLogRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.ReadMailRepository;
import com.doritech.api.Repository.StaticDataRepo;
import com.doritech.api.Repository.WhatsappLogsRepository;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ReadMail {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	MailParamRepo mailParamRepo;

	@Autowired
	ServletContext context;

	@Autowired
	ParamService paramService;

	@Autowired
	ReadMailRepository readMailrepo;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	DeviceMasterRepository deviceMasterrepository;

	@Autowired
	StaticDataRepo staticDataRepo;

	@Autowired
	WhatsappLogsRepository whatappLogsRepo;
	@Autowired
	EmailLogRepo emailLogRepo;
	@Autowired
	ParamRepository paramRepository;
	@Value("${apiUrl}")
	private String apiUrl;
	
	@Value("${WhatsappKey}")
	private String WhatsappKey;

	@Autowired
	CommunicationService communicationService;
	
	@Autowired
	SocialMediaService socialMediaService;
	
	@Autowired
	General general;
	@Autowired
	ManualSyncLogRepository manualSyncLogRepository;
	
    private static final Logger logger = LoggerFactory.getLogger(ReadMail.class);


//	@Scheduled(cron = "0 0 */1 * * *") // Executes every 2 minutes
	public void check() {
		ReadMailEntity readMailEntity = new ReadMailEntity();

		String host = "imap.gmail.com";
		String mailStoreType = "imap";
		String username = "digitalshealthalert@gmail.com";
		String password = "ddqd vsgj mnrr ionp";
		readMailEntity.setMailReceiver(username);
		readMailEntity.setMailPassowrd(password);
		try {
			Properties properties = new Properties();
			properties.put("mail.imap.host", host);
			properties.put("mail.imap.port", "993");
			properties.put("mail.imap.starttls.enable", "true");
			properties.put("mail.imap.ssl.trust", host);

			Session emailSession = Session.getDefaultInstance(properties);

			Store store = emailSession.getStore("imaps");
			store.connect(host, username, password);

			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);

			Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			System.out.println("Total unread messages: " + messages.length);

			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				String subject = message.getSubject();

				// Check if the subject matches the specified criteria
				if (subject != null && (subject.contains("Network Video Recorder: Alarm In On Channel A<-1")
						|| subject.contains("NVR ALERT") || subject.contains("XVR ALERT"))) {
					message.setFlag(Flags.Flag.SEEN, true);
					System.out.println("---------------------------------");
					System.out.println("Email Number " + (i + 1));
					System.out.println("Subject: " + subject);
					System.out.println("From: " + message.getFrom()[0]);

					String messageContent = getTextFromMessage(message);
					readMailEntity.setMailContent(messageContent);
					System.out.println("Text: " + messageContent);

					List<String> attachments = new ArrayList<>();
					if (message.getContentType().contains("multipart")) {
						attachments = downloadAttachments(message);
						System.out.println("Attachment Paths:");
						for (String path : attachments) {
							System.out.println(path);
						}
					}

					String combined = "" + message.getFrom()[0];

					System.out.println(combined);
					readMailEntity.setMailSender(combined);
					String combinedMessage =" SITE SNAPSHOT SUMMARY : " +messageContent;
					System.out.println(combinedMessage);
					if (attachments.isEmpty()) {
						 sendWhatsAppMessage(combinedMessage);
						String emailSubject = subject;
					//	System.out.println("SUBJECT>>>>>>>>>>>>>>>>>>" + subject);
						String emailBody = messageContent;
						//System.out.println("Email Body" + emailBody);
						sendMailOnBranchCode(emailSubject, emailBody);

					} else {
						for (String attachment : attachments) {
							String rootPath = context.getRealPath("Attachments/");
							// File uploadedFile = new File(absolutePath
							System.out.println("root path>>>>>>>>>>>>>>" + rootPath);
							String imageLink = rootPath + attachment;
							System.out.println("Image Link: " + imageLink);
							readMailEntity.setMailAttachmentPath(attachment);
                             sendWhatsAppMessage(combinedMessage);
                            sendFileUsingOkHttp(imageLink);
							String emailSubject = subject;
							String emailBody = messageContent;
							//sendMailOnBranchCode(emailSubject, emailBody);
						}
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String timestampString = dateFormat.format(new Date());
					readMailEntity.setTimeStamp(timestampString);

					if (messageContent != null) {
						readMailrepo.save(readMailEntity);
					} else {
						System.out.println("Data Not Saved Because no mail found.");
					}
				}
			}

			inbox.close(false);
			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendFileUsingOkHttp(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				System.err.println("File not found: " + filePath);
				return;
			}

			List<BranchMasterEntity> branchMasterEntitiesList = branchMasterRepository.findAll();
			OkHttpClient client = new OkHttpClient();
			for (BranchMasterEntity entity : branchMasterEntitiesList) {
				String mobileNumber = entity.getMobile();
				MediaType mediaType = MediaType.parse("text/plain");
				RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file",
						filePath, RequestBody.create(MediaType.parse("application/octet-stream"), file)).build();
				String url = "https://wasmsapi.com/api/sendFiles?token="+ WhatsappKey + "&phone=" + mobileNumber;
				Request request = new Request.Builder().url(url).method("POST", body).build();
				Response response = client.newCall(request).execute();
				System.out.println("Response: " + response + " URL: " + url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendWhatsAppMessage(String message) {
		try {
            System.out.println("Message FINAL :::::::: "+message);
			List<BranchMasterEntity> branchMasterEntitiesList = branchMasterRepository.findAll();

			for (BranchMasterEntity entity : branchMasterEntitiesList) {
				String whatsappNo = entity.getMobile();
				String encodedMessage = URLEncoder.encode(message, "UTF-8");
				String urlString = "https://wasmsapi.com/api/sendText?token="+ WhatsappKey + "&phone="
						+ whatsappNo + "&message=" + encodedMessage;

				URL url = new URL(urlString);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				System.out.println("connection report-:>>>>>>>>>>>>>>>>>>>>>" + connection);
				int responseCode = connection.getResponseCode();
				System.out.println("ResponseCode:>>>>>>>>>>>>>" + responseCode);
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				System.out.println("Response: " + response.toString() + "URL----->>>>" + urlString);
				System.out.println("Response code: " + responseCode);
				System.out.println("Response message: " + response.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	public void sendWhatsAppMessage(String message) {
//	    try {
//	        List<BranchMasterEntity> branchMasterEntitiesList = branchMasterRepository.findAll();
//
//	        for (BranchMasterEntity entity : branchMasterEntitiesList) {
//	            List<String> mobileNumbers = new ArrayList<>();
//	            mobileNumbers.add(entity.getMobile());
//	            mobileNumbers.add(entity.getMobileQuaternary());
//	            mobileNumbers.add(entity.getMobileQuinary());
//	            mobileNumbers.add(entity.getMobileSecondary());
//	            mobileNumbers.add(entity.getMobileSenary());
//	            mobileNumbers.add(entity.getMobileTertiary());
//
//	            for (String whatsappNo : mobileNumbers) {
//	            	System.out.println("*******************************");
//	            	System.out.println(whatsappNo);
//	            	System.out.println("*******************************");
//	                String encodedMessage = URLEncoder.encode(message, "UTF-8");
//	                String urlString = "https://wasmsapi.com/api/sendText?token=" + WhatsappKey + "&phone="
//	                        + whatsappNo + "&message=" + encodedMessage;
//        
//	                URL url = new URL(urlString);
//					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + url);
//	                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//	                connection.setRequestMethod("POST");
//	                int responseCode = connection.getResponseCode();
//	                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//	                String inputLine;
//	                StringBuffer response = new StringBuffer();
//
//	                while ((inputLine = in.readLine()) != null) {
//	                    response.append(inputLine);
//	                }
//	                in.close();
//
//	                System.out.println("Response: " + response.toString() + " URL: " + urlString);
//	                System.out.println("Response code: " + responseCode);
//	                System.out.println("Response message: " + response.toString());
//	            }
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}
	public void sendWhatsAppMessageForBranchCodeAndMessage(String branchCode, String activeZone) {
        try {
            String[] activeZoneArray = activeZone.split("_");

            StringBuilder concatenatedZones = new StringBuilder();
            String[] zoneNames = new String[activeZoneArray.length];
            int index = 0;
            for (String zone : activeZoneArray) {
                int colonIndex = zone.indexOf(':');
                if (colonIndex != -1) {
                    zoneNames[index++] = zone.substring(0, colonIndex);
                    concatenatedZones.append(zoneNames[index - 1]);
                    if (index < activeZoneArray.length) {
                        concatenatedZones.append(", ");
                    }
                }
            }

            if (concatenatedZones.length() > 0) {
                concatenatedZones.setLength(concatenatedZones.length() - 2);
            }

            String[] zoneValuesForValidation = concatenatedZones.toString().split(",\\s*");
            System.out.println("Zone Value For validation ::  " + Arrays.toString(zoneValuesForValidation));

            String matchedAlertTypes = "";
            BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);

            List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");
            List<String> mobileNumbers = new ArrayList<>();
            mobileNumbers.add(branchEntity.getMobile());
            if (branchEntity.getMobileSecondary() != null) {
                mobileNumbers.add(branchEntity.getMobileSecondary());
            }
            if (branchEntity.getMobileTertiary() != null) {
                mobileNumbers.add(branchEntity.getMobileTertiary());
            }
            if (branchEntity.getMobileQuaternary() != null) {
                mobileNumbers.add(branchEntity.getMobileQuaternary());
            }
            if (branchEntity.getMobileQuinary() != null) {
                mobileNumbers.add(branchEntity.getMobileQuinary());
            }
            if (branchEntity.getMobileSenary() != null) {
                mobileNumbers.add(branchEntity.getMobileSenary());
            }
            System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);

            org.springframework.http.ResponseEntity<List<SocialMediaDTO>> socialMediaList = socialMediaService.getSocialMediaDetailsByBranchCode(branchCode);
            if (socialMediaList == null || socialMediaList.getStatusCode() == HttpStatus.NO_CONTENT || socialMediaList.getBody() == null) {
                System.out.println("Data not present in social media report with branchCode: " + branchCode);
                return;
            }

            @SuppressWarnings("unchecked")
            List<SocialMediaDTO> responseList = (List<SocialMediaDTO>) socialMediaList.getBody();
            if (responseList != null && !responseList.isEmpty()) {
                for (String activeZoneValue : zoneValuesForValidation) {
                    activeZoneValue = activeZoneValue.trim();
                    for (SocialMediaDTO socialMediaDTO : responseList) {
                        String alertType = socialMediaDTO.getAlertType();
                        String whatsapp = socialMediaDTO.getWhatsapp();
                        if (activeZoneValue.equals(alertType) && "Y".equalsIgnoreCase(whatsapp)) {
                            System.out.println("Active zone value: " + activeZoneValue + " WhatsApp status: " + whatsapp);
                            matchedAlertTypes += alertType + ", ";
                        }
                    }
                }

                if (matchedAlertTypes.length() > 0) {
                    matchedAlertTypes = matchedAlertTypes.substring(0, matchedAlertTypes.length() - 2);
                }

                for (Object[] object : staticData) {
                    String whatsappText = String.valueOf(object[2] == null ? "" : object[2]);
                    whatsappText = whatsappText.replaceAll("XNAME", branchEntity.getBranchDesc());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                    String currentDate = dateFormat.format(new Date());
                    String currentTime = timeFormat.format(new Date());

                    whatsappText = whatsappText.replaceAll("DATE", currentDate);
                    whatsappText = whatsappText.replaceAll("TIME", currentTime);
                    whatsappText = whatsappText.replaceAll("YNAME", matchedAlertTypes.toString());
                    System.out.println("ALERTS : " + matchedAlertTypes.toString());

                    for (String whatsappNo : mobileNumbers) {
                        sendWhatsAppMessage(whatsappNo, whatsappText);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void sendWhatsAppMessage(String whatsappNo, String whatsappText) throws IOException {
		System.out.println("NUMBER ::: "+whatsappNo + "  TEXT ::::: "+whatsappText);
        String encodedMessage = URLEncoder.encode(whatsappText, "UTF-8");
        String urlString = "https://wasmsapi.com/api/sendText?token=" + WhatsappKey + "&phone=" + whatsappNo + "&message=" + encodedMessage;

        URL url = new URL(urlString);
     //   System.out.println("URL : " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Response Code : " + responseCode);
        System.out.println("Response : " + response.toString());
    }


	public ResponseEntity sendMailOnBranchCode(String emailSubject, String emailBody) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<EmailLogEntity> emailEntities = processMessageByType(emailSubject, emailBody);
			if (!emailEntities.isEmpty()) {
				ResponseEntity emailResponse = communicationService.sendEmail(mailParamRepo.getById("1"),
						emailEntities);
				response.setPayload(emailResponse.getPayload());
				response.setMessage("Emails sent successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No emails sent.");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} catch (Exception e) {
			response.setMessage("Error occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	private List<EmailLogEntity> processMessageByType(String emailSubject, String emailBody) {
		List<EmailLogEntity> emailEntities = new ArrayList<>();
		try {
			String emailSubjectTemplate = emailSubject;
			String emailBodyTemplate = emailBody;
			List<BranchMasterEntity> branchMasterEntities = branchMasterRepository.findAll();
			for (BranchMasterEntity entity : branchMasterEntities) {
				EmailLogEntity emailEntity = new EmailLogEntity();
				MailParamEntity mailParam = mailParamRepo.getById("1");
				emailEntity.setBodyText(emailBodyTemplate);
				emailEntity.setDatetime(LocalDateTime.now());
				emailEntity.setEmailIdTo(entity.getEmail());
				emailEntity.setFromEmail(mailParam.getSmtpMailId());
				emailEntity.setMailId(1L);
				emailEntity.setStatus("N");
				emailEntity.setSubject(emailSubjectTemplate);
				emailEntities.add(emailEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailEntities;
	}

	public List<String> downloadAttachments(Message message) throws IOException, MessagingException {
		List<String> downloadedAttachments = new ArrayList<>();
		Multipart multiPart = (Multipart) message.getContent();
		int numberOfParts = multiPart.getCount();
		for (int partCount = 0; partCount < numberOfParts; partCount++) {
			MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
			if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
				String fileName = part.getFileName();
				String downloadDirectory = context.getRealPath("Attachments");
				part.saveFile(downloadDirectory + File.separator + fileName);
				downloadedAttachments.add(fileName);

				// Check if the attachment is an image
				if (isImage(fileName)) {
					System.out.println("Attachment: " + fileName);
					System.out.println("Original Image:");
					displayImage(part.getInputStream());
					System.out.println();
				}
			}
		}
		return downloadedAttachments;
	}

	private boolean isImage(String fileName) {
		// This is a simple check based on file extension, you might need a more robust
		// check in production
		return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")
				|| fileName.toLowerCase().endsWith(".png");
	}

	private void displayImage(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] imageBytes = buffer.toByteArray();
		String base64Encoded = Base64.getEncoder().encodeToString(imageBytes);
		System.out.println(base64Encoded); // Output the Base64 encoded image
	}

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			return getTextFromMimeMultipart(mimeMultipart);
		}
		return "";
	}

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result.append("\n").append(bodyPart.getContent()); // without return, same text appears twice in my
																	// tests
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
			}
		}
		return result.toString();
	}

	public org.springframework.http.ResponseEntity<FileSystemResource> getImage(String imageName) {
		try {
			if (StringUtils.isEmpty(imageName)) {
				return org.springframework.http.ResponseEntity.badRequest().build();
			}
			String path = context.getRealPath("Attachments");
			String imagePath = path + File.separator + imageName;
			File file = new File(imagePath);
			if (file.exists()) {
				return org.springframework.http.ResponseEntity.ok().body(new FileSystemResource(file));
			} else {
				return org.springframework.http.ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public ResponseEntity getReadMailDetails() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<ReadMailEntity> readMailEntities = readMailrepo.findAll();
			List<ReadMailEntity> list = new ArrayList<>();

			for (ReadMailEntity readMail : readMailEntities) {
				if (readMail != null) {
					list.add(readMail);
				}
			}
			response.setMessage("Data fetched successfully.");
			response.setPayload(list);
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public void hitUrlForReset(String deviceSerialNumber) {
        ManualSyncLog logEntry = new ManualSyncLog();
        logEntry.setDeviceSerialNumber(deviceSerialNumber);
        logEntry.setApiUrl("http://localhost:9091/sendManualSyncSMS");
        logEntry.setRequestPayload("{\"PanelPhoneNo\":\"" + deviceSerialNumber + "\",\"MessageType\":\"R\"}");
        logEntry.setRequestTimestamp(LocalDateTime.now());

        try {
            DeviceMasterEntity deviceEntity = deviceMasterrepository.getByDeviceSerialNo(deviceSerialNumber);
            if (deviceEntity == null) {
                logger.error("No device found with serial number: {}", deviceSerialNumber);
                logEntry.setStatus("FAILED");
                logEntry.setResponseMessage("No device found");
                logEntry.setResponseTimestamp(LocalDateTime.now());
                manualSyncLogRepository.save(logEntry);
                return;
            }

            String panelPhoneNumber = deviceEntity.getPhoneNumber();
            String jsonInputString = "{\"PanelPhoneNo\":\"" + panelPhoneNumber + "\",\"MessageType\":\"R\"}";

            logger.info("Sending request to URL: {}", apiUrl);
            logger.info("Request payload: {}", jsonInputString);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            logEntry.setResponseCode(responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    logEntry.setResponseMessage(response.toString());
                    logEntry.setStatus("SUCCESS");
                    logger.info("Response: {}", response.toString());
                }
            } else {
                logEntry.setStatus("FAILED");
                logEntry.setResponseMessage("HTTP request failed with response code " + responseCode);
                logger.error("HTTP request failed with response code {}", responseCode);
            }
            logEntry.setResponseTimestamp(LocalDateTime.now());
            manualSyncLogRepository.save(logEntry);

        } catch (SocketException e) {
            logEntry.setStatus("FAILED");
            logEntry.setResponseMessage("SocketException: Unexpected end of file from server");
            logEntry.setResponseTimestamp(LocalDateTime.now());
            manualSyncLogRepository.save(logEntry);
            logger.error("SocketException: Unexpected end of file from server", e);
        } catch (IOException e) {
            logEntry.setStatus("FAILED");
            logEntry.setResponseMessage("IOException occurred while sending request: " + e.getMessage());
            logEntry.setResponseTimestamp(LocalDateTime.now());
            manualSyncLogRepository.save(logEntry);
            logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
        }
    }

	
	
//	@Scheduled(fixedRate = 6 * 60 * 60 * 1000)
	public ResponseEntity sendSmsForEveryBranchByPanelSerialNo(){
    	  ResponseEntity response=new ResponseEntity();
    	  List<DeviceMasterEntity> deviceMaster=deviceMasterrepository.findAll();
    	  for(DeviceMasterEntity device:deviceMaster) {
    		  hitUrlForStatus(device.getDeviceSerialNo());
    	  }
    	  response.setMessage("Manual Sync Complete for all Branch");
    	  response.setPayload(null);
    	  response.setStatusCode(HttpStatus.OK.value());
    	  return response;
      }
	
	public void hitUrlForStatus(String deviceSerialNumber) {
		ParamEntity paramEntity = paramRepository.findByCodeAndSerial("HMS", "EXE_MANUAL_SYNC_URL");
		
	    ManualSyncLog logEntry = new ManualSyncLog();
	    logEntry.setDeviceSerialNumber(deviceSerialNumber);
	    logEntry.setApiUrl(paramEntity.getDescp1()+ deviceSerialNumber);
	    logEntry.setRequestPayload("N/A - GET Request");
	    logEntry.setRequestTimestamp(LocalDateTime.now());

	    try {
	        DeviceMasterEntity deviceEntity = deviceMasterrepository.getByDeviceSerialNo(deviceSerialNumber);
	        if (deviceEntity == null) {
	            logger.error("No device found with serial number: {}", deviceSerialNumber);
	            logEntry.setStatus("FAILED");
	            logEntry.setResponseMessage("No device found");
	            logEntry.setResponseTimestamp(LocalDateTime.now());
	            manualSyncLogRepository.save(logEntry);
	            return;
	        }

	        // Construct URL
	        String apiUrl = paramEntity.getDescp1() + deviceSerialNumber;
	        logger.info("Sending GET request to URL: {}", apiUrl);

	        URL url = new URL(apiUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setConnectTimeout(5000);
	        connection.setReadTimeout(10000);

	        int responseCode = connection.getResponseCode();
	        logEntry.setResponseCode(responseCode);

	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            try (BufferedReader br = new BufferedReader(
	                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
	                StringBuilder response = new StringBuilder();
	                String responseLine;
	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }
	                logEntry.setResponseMessage(response.toString());
	                logEntry.setStatus("SUCCESS");
	                logger.info("Response: {}", response.toString());
	            }
	        } else {
	            logEntry.setStatus("FAILED");
	            logEntry.setResponseMessage("HTTP request failed with response code " + responseCode);
	            logger.error("HTTP request failed with response code {}", responseCode);
	        }

	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        manualSyncLogRepository.save(logEntry);

	    } catch (SocketException e) {
	        logEntry.setStatus("FAILED");
	        logEntry.setResponseMessage("SocketException: Unexpected end of file from server");
	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        manualSyncLogRepository.save(logEntry);
	        logger.error("SocketException: Unexpected end of file from server", e);
	    } catch (IOException e) {
	        logEntry.setStatus("FAILED");
	        logEntry.setResponseMessage("IOException occurred while sending request: " + e.getMessage());
	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        manualSyncLogRepository.save(logEntry);
	        logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
	    }
	}

	 public void hitUrlForStatusSBI(String deviceSerialNumber) {
	        ManualSyncLog logEntry = new ManualSyncLog();
	        logEntry.setDeviceSerialNumber(deviceSerialNumber);
	        logEntry.setApiUrl("http://localhost:9091/sendManualSyncSMS");
	        logEntry.setRequestPayload("{\"PanelPhoneNo\":\"" + deviceSerialNumber + "\",\"MessageType\":\"SBI\"}");
	        logEntry.setRequestTimestamp(LocalDateTime.now());

	        try {
	            DeviceMasterEntity deviceEntity = deviceMasterrepository.getByDeviceSerialNo(deviceSerialNumber);
	            if (deviceEntity == null) {
	                logger.error("No device found with serial number: {}", deviceSerialNumber);
	                logEntry.setStatus("FAILED");
	                logEntry.setResponseMessage("No device found");
	                logEntry.setResponseTimestamp(LocalDateTime.now());
	                manualSyncLogRepository.save(logEntry);
	                return;
	            }

	            String panelPhoneNumber = deviceEntity.getPhoneNumber();
	            String jsonInputString = "{\"PanelPhoneNo\":\"" + panelPhoneNumber + "\",\"MessageType\":\"S\"}";

	            logger.info("Sending request to URL: {}", apiUrl);
	            logger.info("Request payload: {}", jsonInputString);

	            URL url = new URL(apiUrl);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setRequestProperty("Accept", "application/json");
	            connection.setConnectTimeout(5000);
	            connection.setReadTimeout(10000);
	            connection.setDoOutput(true);

	            try (OutputStream os = connection.getOutputStream()) {
	                byte[] input = jsonInputString.getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }

	            int responseCode = connection.getResponseCode();
	            logEntry.setResponseCode(responseCode);

	            if (responseCode == HttpURLConnection.HTTP_OK) {
	                try (BufferedReader br = new BufferedReader(
	                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	                    StringBuilder response = new StringBuilder();
	                    String responseLine;
	                    while ((responseLine = br.readLine()) != null) {
	                        response.append(responseLine.trim());
	                    }
	                    logEntry.setResponseMessage(response.toString());
	                    logEntry.setStatus("SUCCESS");
	                    logger.info("Response: {}", response.toString());
	                }
	            } else {
	                logEntry.setStatus("FAILED");
	                logEntry.setResponseMessage("HTTP request failed with response code " + responseCode);
	                logger.error("HTTP request failed with response code {}", responseCode);
	            }
	            logEntry.setResponseTimestamp(LocalDateTime.now());
	            manualSyncLogRepository.save(logEntry);

	        } catch (SocketException e) {
	            logEntry.setStatus("FAILED");
	            logEntry.setResponseMessage("SocketException: Unexpected end of file from server");
	            logEntry.setResponseTimestamp(LocalDateTime.now());
	            manualSyncLogRepository.save(logEntry);
	            logger.error("SocketException: Unexpected end of file from server", e);
	        } catch (IOException e) {
	            logEntry.setStatus("FAILED");
	            logEntry.setResponseMessage("IOException occurred while sending request: " + e.getMessage());
	            logEntry.setResponseTimestamp(LocalDateTime.now());
	            manualSyncLogRepository.save(logEntry);
	            logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
	        }
	    }
}