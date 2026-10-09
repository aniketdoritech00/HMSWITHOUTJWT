package com.doritech.api.Service;

import com.doritech.api.Common.CommonUtils;
import com.doritech.api.DTO.TicketDetailsDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.TicketDetailsEntity;
import com.doritech.api.Entity.WhatsAppLogs;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.TicketDetailsRepository;
import com.doritech.api.Repository.WhatsappLogsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketGenerationService {

	@Autowired
	private TicketDetailsRepository ticketDetailsRepository;
	@Autowired
	CommonUtils commonUtils;
	 @Value("${app.tracking.url}")
	    private String baseTrackingUrl;
	 
	 @Value("${URL_SAVE_TICKET_CMS}")
	 private String URL_SAVE_TICKET_CMS;
	 
	 @Value("${URL_UPDATE_TICKET_CMS}")
	 private String URL_UPDATE_TICKET_CMS;
	 
	 @Value("${URL_GET_TICKET_INFO}")
	 private String URL_GET_TICKET_INFO;
	 
	 @Value("${WhatsappKey}")
	private String WhatsappKey;
	 
	 @Autowired
	 BranchMasterRepository branchMasterRepository;
	 
	 @Autowired
	 DeviceMasterRepository deviceMasterRepository;
	 @Autowired
	 WhatsappLogsRepository whatsappLogsRepository;
	 @Autowired
	 ReadMail readMail;
	 public String generateSmsForEmail(TicketDetailsDTO ticketDetailsRequest) throws IllegalAccessException, InvocationTargetException {       
		
		String ticketNo= commonUtils.genernateTicketNumber();
		 ticketDetailsRequest.setTicketId(ticketNo);
		 LocalDateTime currentTime = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String formattedTime = currentTime.format(formatter);
		 ticketDetailsRequest.setMailTime(formattedTime);
		 String completeSMS = String.format(
		            "Ticket ID: %s has been generated for %s, located at the %s branch, under vendor: %s. The issue reported is: '%s'.",
		            ticketDetailsRequest.getTicketId(),
		            ticketDetailsRequest.getBankName(),
		            ticketDetailsRequest.getBranchName(),
		            ticketDetailsRequest.getVendorName(), 
		            ticketDetailsRequest.getAlertType()
		        );
		 String whatsappMessage = String.format(
					"Your ticket has been successfully generated! 🎟  \r\n" + "Here are the details:\r\n" + "\r\n"
							+ "- Ticket ID:   %s\r\n" + "- Bank Name:  %s \r\n" + "- Branch Name:  %s \r\n"
							+ "- Vendor: %s \r\n" + "\r\n"
							+ "We are working on resolving this issue promptly. Thank you for your patience. 🙏\r\n"
							+ "\r\n" + "For further assistance or to track your ticket, feel free to contact us.\r\n"
							+ "\r\n" + "Best regards,  \r\n" + "Digitals India Security Products Pvt. Ltd.",
					ticketDetailsRequest.getTicketId(), ticketDetailsRequest.getBankName(),
					ticketDetailsRequest.getBranchName(), ticketDetailsRequest.getVendorName(),
					ticketDetailsRequest.getAlertType());
			// System.out.println(whatsappMessage);
		 ticketDetailsRequest.setCompleteSMS(completeSMS);
		 String trackingUrl = baseTrackingUrl + "/track-ticket?ticketId=" + ticketDetailsRequest.getTicketId();

		 String emailContent = "<html><body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f9;'>"
		            + "<div style='max-width: 600px; margin: 20px auto; background: #ffffff; border: 1px solid #d0d0d0; border-radius: 10px; overflow: hidden;'>"
		            + "<div style='background: #1e90ff; padding: 20px; text-align: center; color: #ffffff;'>"
		            + "<h1 style='margin: 0; font-size: 24px;'>Ticket Confirmation</h1>"
		            + "</div>"
		            + "<div style='padding: 20px;'>"
		            + "<p style='font-size: 16px; color: #333;'>The ticket has been generated successfully. Please find the details below:</p>"
		            + "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>"
		            + "<tr style='background: #f0f8ff;'>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #1e90ff; font-weight: bold;'>Bank Name:</td>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #333;'>" + ticketDetailsRequest.getBankName() + "</td>"
		            + "</tr>"
		            + "<tr>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #1e90ff; font-weight: bold;'>Branch:</td>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #333;'>" + ticketDetailsRequest.getBranchName() + "</td>"
		            + "</tr>"
		            + "<tr style='background: #f0f8ff;'>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #1e90ff; font-weight: bold;'>Issue:</td>"
		            + "<td style='padding: 10px; border: 1px solid #d0d0d0; color: #333;'>" + ticketDetailsRequest.getAlertType() + "</td>"
		            + "</tr>"
		            + "</table>"
		            + "<p style='font-size: 16px; color: #333; margin-top: 20px;'>Click the link below to track your ticket:</p>"
		            + "<p style='text-align: center; margin: 20px 0;'>"
		            + "<a href='" + trackingUrl + "' style='text-decoration: none; background: #1e90ff; color: #ffffff; padding: 10px 20px; border-radius: 5px;'>Track Ticket</a>"
		            + "</p>"
		            + "</div>"
		            + "<div style='background: #1e90ff; padding: 10px; text-align: center; color: #ffffff;'>"
		            + "<p style='margin: 0; font-size: 14px;'>Thank you for choosing our service!</p>"
		            + "</div>"
		            + "</div>"
		            + "</body></html>";
		    
		 //   readMail.sendWhatsAppMessage(completeSMS);
		    ResponseEntity<String> responseEntity=sendWhatsAppMessageForTicket(whatsappMessage,ticketNo,ticketDetailsRequest.getBranchCode());
			responseEntity.getBody();
			ticketDetailsRequest.setMailStatus("Success");
			ticketDetailsRequest.setWhatsAppStatus(responseEntity.toString());
			saveComplaintTicketDetails(ticketDetailsRequest);
		    saveComplaintTicket(ticketDetailsRequest);
	 		    return emailContent; 
	 		}


	 	
	 	 public ResponseEntity<Object> saveComplaintTicketDetails(TicketDetailsDTO ticketRequest) 
	 	            throws IllegalAccessException, InvocationTargetException { 
	 	        TicketDetailsEntity ticketDetailsEntity = new TicketDetailsEntity();
	 	        ticketDetailsEntity.setTicketId(ticketRequest.getTicketId());
	 	        ticketDetailsEntity.setBankName(ticketRequest.getBankName());
	 	        ticketDetailsEntity.setBranchName(ticketRequest.getBranchName());
	 	        ticketDetailsEntity.setVendorName(ticketRequest.getVendorName());
	 	        ticketDetailsEntity.setAlertType(ticketRequest.getAlertType());
	 	        ticketDetailsEntity.setPanelSerialNumber(ticketRequest.getPanelSerialNumber());
	 	        ticketDetailsEntity.setDateTime(ticketRequest.getDateTime());
	 	        ticketDetailsEntity.setCompleteSMS(ticketRequest.getCompleteSMS());
	 	        ticketDetailsEntity.setStatus("PENDING");
	 	        ticketDetailsEntity.setMailStatus("Success");
	 	        ticketDetailsEntity.setWhatsAppStatus(ticketRequest.getWhatsAppStatus());
	 	        ticketDetailsEntity = ticketDetailsRepository.save(ticketDetailsEntity);
	 	        String responseMessage = String.format("Complaint ticket saved successfully with Ticket ID: %s", ticketDetailsEntity.getTicketId());
	 	        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
	 	    }
	 	 
	 	   
	 	    
	 	   
	 	public ResponseEntity<Object> saveComplaintTicket(TicketDetailsDTO ticketRequest) {
	 	    System.out.println("Data Send Successfully.." + ticketRequest.toString());
	 	    try {
	 	        ObjectMapper objectMapper = new ObjectMapper();
	 	        String requestBody = objectMapper.writeValueAsString(ticketRequest);
	 	        System.out.print("Request Body: " + requestBody);

	 	        HttpClient client = HttpClient.newHttpClient();
	 	        HttpRequest request = HttpRequest.newBuilder()
	 	                .uri(URI.create(URL_SAVE_TICKET_CMS))
	 	                .header("Content-Type", "application/json")
	 	                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
	 	                .build();

	 	        System.out.print("Request: " + request);
	 	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	 	        System.out.println("Response Code: " + response.statusCode());
	 	        System.out.println("Response Body: " + response.body());

	 	        // Return response to caller
	 	        return ResponseEntity.status(response.statusCode()).body(response.body());
	 	    } catch (java.net.ConnectException e) {
	 	        System.err.println("Connection failed: Unable to reach the server.");
	 	        e.printStackTrace();
	 	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Unable to reach CMS server.");
	 	    } catch (Exception e) {
	 	        System.err.println("An error occurred while sending the request.");
	 	        e.printStackTrace();
	 	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
	 	    }
	 	}

	 	    
	 	   public ResponseEntity<Object> UpdateComplaintTicket(String ticketId) {
	 		    System.out.println("Data Send Successfully..");

	 		    if (ticketId == null || ticketId.isEmpty()) {
	 		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	 		                .body("TicketId not provided or is empty.");
	 		    }

	 		    try {
	 		        // Construct the request URL with the ticketId as a query parameter
	 		        String requestUrl = URL_UPDATE_TICKET_CMS + "?ticketId=" + URLEncoder.encode(ticketId, StandardCharsets.UTF_8);
	 		        System.out.println("Request URL: " + requestUrl);

	 		        HttpClient client = HttpClient.newHttpClient();
	 		        HttpRequest request = HttpRequest.newBuilder()
	 		                .uri(URI.create(requestUrl))
	 		                .header("Content-Type", "application/json")
	 		                .POST(HttpRequest.BodyPublishers.noBody()) // Sending an empty body if needed
	 		                .build();
	 		        System.out.println("HTTP Request: " + request);

	 		        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	 		        System.out.println("Response Code: " + response.statusCode());
	 		        System.out.println("Response Body: " + response.body());

	 		        return ResponseEntity.status(response.statusCode()).body(response.body());
	 		    } catch (IOException | InterruptedException e) {
	 		        e.printStackTrace();
	 		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	 		                .body("An error occurred while updating the ticket: " + e.getMessage());
	 		    } catch (Exception e) {
	 		        e.printStackTrace();
	 		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	 		                .body("Unexpected error: " + e.getMessage());
	 		    }
	 		}

	 	  public ResponseEntity<Object> getComplaintsByPanelSerialNumberAndDateTime(TicketDetailsDTO ticketRequest) {
	 		    System.out.println("Data Received: " + ticketRequest);
	 		    try {
	 		        ObjectMapper objectMapper = new ObjectMapper();
	 		        String requestBody = objectMapper.writeValueAsString(ticketRequest);
	 		        System.out.println("Request Body: " + requestBody);

	 		        HttpClient client = HttpClient.newHttpClient();
	 		        HttpRequest request = HttpRequest.newBuilder()
	 		                .uri(URI.create(URL_GET_TICKET_INFO))
	 		                .header("Content-Type", "application/json")
	 		                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
	 		                .build();
	 		        
	 		        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	 		        
	 		        System.out.println("Response Code: " + response.statusCode());
	 		        System.out.println("Response Body: " + response.body());
	 		        
	 		        return ResponseEntity.status(response.statusCode())
	 		                .body(response.body());
	 		    } catch (Exception e) {
	 		        e.printStackTrace();
	 		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	 		                .body("An error occurred while processing the request.");
	 		    }
	 		}
	 
	 	  
	 	 public ResponseEntity<String> sendWhatsAppMessageForTicket(String message,String ticketId,String branchCode) {
	 		WhatsAppLogs whatsAppLogs = new WhatsAppLogs();
	 		try {
	 			System.out.println("Message FINAL :::::::: " + message);
//	 			String whatsappNo = "919027063126";
	 			BranchMasterEntity branchEntity=branchMasterRepository.findBybranchCode(branchCode);
	 	    	if(branchEntity != null) {
	            	    String vendorNumber = deviceMasterRepository.findBybranchCode(branchCode).getVendorMobile();
	 	    		List<String> numbers = new ArrayList<>();
	 				numbers.add(branchEntity.getMobile());
	 				if (branchEntity.getMobileSecondary() != null) {
	 					numbers.add(branchEntity.getMobileSecondary());
	 				}
	 				if (branchEntity.getMobileTertiary()!= null) {
	 					numbers.add(branchEntity.getMobileTertiary());
	 				}
	 				if (branchEntity.getMobileQuinary() != null) {
	 					numbers.add(branchEntity.getMobileQuinary());
	 				}
	 				if (branchEntity.getMobileQuaternary() != null) {
	 					numbers.add(branchEntity.getMobileQuaternary());
	 				}
	 				if (branchEntity.getMobileSenary() != null) {
	 					numbers.add(branchEntity.getMobileSenary());
	 				}
	 				if (vendorNumber != null && !vendorNumber.isEmpty()) {
	 			            numbers.add(vendorNumber);
	 			    }
	 				if (numbers.isEmpty()) {
	 	                System.err.println("No valid phone numbers found for branch: " + branchCode);
	 	                return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body("Mobile Number not found in the branch");
	 	            }
	 				 String finalResponse = ""; 
	 	            for (String number : numbers) {
	 			String encodedMessage = URLEncoder.encode(message, "UTF-8");
	 			String urlString = "https://wasmsapi.com/api/sendText?token=" + WhatsappKey + "&phone=" + number
	 					+ "&message=" + encodedMessage;
	 			whatsAppLogs.setPhoneNo(number);
	 			whatsAppLogs.setResponseMessage(message);

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

//	 	 					System.out.println("Response: " + response.toString() + "URL----->>>>" + urlString);
//	 	 					System.out.println("Response code: " + responseCode);
	 			finalResponse = response.toString();
	 			System.out.println("Response message: " + response.toString());
	 			if (responseCode == 200) {
	 				whatsAppLogs.setStatus("SEND");
	 			} else {
	 				whatsAppLogs.setStatus("NOT SEND");
	 			}

	 			whatsAppLogs.setUrl(urlString);
	 			whatsAppLogs.setResponseMessage(response.toString());
	 			whatsAppLogs.setWhatsappMessage(message);
	 			whatsAppLogs.setTicketId(ticketId);
	 			whatsappLogsRepository.save(whatsAppLogs);
	 		
	 	            }
	 	        	return ResponseEntity.status(HttpStatus.OK.value()).body(finalResponse);
	 	    	}
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while sending the message."+e.getLocalizedMessage());
	 			 
	 		}
	 		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Branch not found or invalid input.");	
	 	}
	 }
