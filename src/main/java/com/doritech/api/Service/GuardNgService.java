package com.doritech.api.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.CommandRequestDTO;
import com.doritech.api.DTO.GuardNgLogDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Entity.GuardNgLogEntity;
import com.doritech.api.Entity.ManualSyncLog;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.GuardNgLogRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class GuardNgService {
	
	
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	GuardNgLogRepository guardNgLogRepository;
	@Value("${guardNgapiUrl}")
	private String apiUrl;
	@Autowired
	EmployeeMasterRepository employeeMasterRepository;
	@Autowired
	BranchMasterService branchMasterService;
	
	
	

    private static final Logger logger = LoggerFactory.getLogger(ReadMail.class);
	
	
    public void sendCommandToDevice(String deviceSerialNumber, String commandType) {
        GuardNgLogEntity logEntry = new GuardNgLogEntity();
        logEntry.setDeviceSerialNumber(deviceSerialNumber);
        logEntry.setRequestPayload(commandType);
        logEntry.setRequestTimestamp(LocalDateTime.now());

        try {
            DeviceMasterEntity deviceEntity = deviceMasterRepository.getByDeviceSerialNo(deviceSerialNumber);
            if (deviceEntity == null) {
                logger.error("No device found with serial number: {}", deviceSerialNumber);
                logEntry.setStatus("FAILED");
                logEntry.setResponseMessage("No device found");
                logEntry.setResponseTimestamp(LocalDateTime.now());
                guardNgLogRepository.save(logEntry);
                return;
            }

            
//            if("Caller ID".equalsIgnoreCase(deviceEntity.getProtectionType())) {
//            	
//            }else if("Passcode".equalsIgnoreCase(deviceEntity.getProtectionType())) {
//            	
//            }else if("Passcode+ID".equalsIgnoreCase(deviceEntity.getProtectionType())) {
//            	
//            }
            
            
            String panelPhoneNumber = deviceEntity.getPhoneNumber();
            String command = buildCommand(deviceEntity.getProtectionType(),deviceEntity,commandType);

            if (command == null) {
                logger.error("Invalid command type: {}", commandType);
                logEntry.setStatus("FAILED");
                logEntry.setResponseMessage("Invalid command type");
                logEntry.setResponseTimestamp(LocalDateTime.now());
                guardNgLogRepository.save(logEntry);
                return;
            }

            // Prepare command for external execution
            String exePath = "path/to/SMSSender.exe"; // Replace with the actual path
            String[] commandArgs = {exePath, "COM5", panelPhoneNumber, command};

            try {
                Process process = Runtime.getRuntime().exec(commandArgs);
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    logger.info("Command sent successfully via SMSSender.exe");
                    logEntry.setStatus("SUCCESS");
                    logEntry.setResponseMessage("Command sent successfully");
                } else {
                    logger.error("SMSSender.exe returned error code: {}", exitCode);
                    logEntry.setStatus("FAILED");
                    logEntry.setResponseMessage("SMSSender.exe execution failed with code: " + exitCode);
                }
            } catch (Exception e) {
                logger.error("Error while executing SMSSender.exe: {}", e.getMessage(), e);
                logEntry.setStatus("FAILED");
                logEntry.setResponseMessage("Error while executing external command: " + e.getMessage());
            }

            logEntry.setResponseTimestamp(LocalDateTime.now());
            guardNgLogRepository.save(logEntry);

        } catch (Exception e) {
            logEntry.setStatus("FAILED");
            logEntry.setResponseMessage("Exception occurred: " + e.getMessage());
            logEntry.setResponseTimestamp(LocalDateTime.now());
            guardNgLogRepository.save(logEntry);
            logger.error("Exception occurred while sending command: {}", e.getMessage(), e);
        }
    }


	private String buildCommand(String protectionType,DeviceMasterEntity deviceEntity, String commandType) {
	    String command = null;

	    switch (protectionType) {
	        case "Caller ID":
	            command = getCallerIdCommand(deviceEntity,commandType);
	            break;
	        case "Passcode":
	            command = "001234 " + getPasscodeCommand(deviceEntity,commandType);
	            break;
	        case "Passcode+ID":
	            command = "1234 " + getPasscodeCommand(deviceEntity, commandType);
	            break;
	        default:
	            logger.error("Unknown protection type: {}", protectionType);
	    }

	    return command;
	}

	private String getCallerIdCommand(DeviceMasterEntity deviceEntity ,String commandType) {
		
		LocalDateTime currenTime =  LocalDateTime.now();
	    switch (commandType) {
	        case "User Info Test":
	            return "Test Usp 00";
	        case "HealthTest":
	            return "Status";
	        case "Arm":
	            return "Arm";
	        case "Disarm":
	            return "Disarm";
	        case "PasscodeChange":
	            return "Pass 1234(new)";
	        case "Time Sync":
	            return "Time DD.MM.YY,HH:MM:SS,5";
	        case "ZoneLabel":
	            return "Lable Zone.no,xyz";
	        case "DialRetry":
	            return "Retry 3,3";
	        case "Update user info":
	            return "Retry 3,3";    
	        default:
	            return null;
	    }
	}

	private String getPasscodeCommand(DeviceMasterEntity deviceEntity , String commandType) {
		LocalDateTime currenTime =  LocalDateTime.now();
		
		String commandString="";
		
	    switch (commandType) {
	        case "User Info Test":
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Test Usp";
	            return commandString;
	        case "HealthTest":
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Status";
	            
	            return commandString;
	        case "Arm":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Arm";
	            
	            return commandString;
	        case "Disarm":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Disarm";
	            
	            return commandString;
	        case "Passcode change":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" pass";
	            
	            return commandString;
	        case "TimeSync":
	        	
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        case "ZoneLabel":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        case "DialRetry":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        default:
	            return null;
	    }
	}
	
	private String getPasscodeIdCommand(DeviceMasterEntity deviceEntity , String commandType) {
		LocalDateTime currenTime =  LocalDateTime.now();
		
		String commandString="";
		
	    switch (commandType) {
	        case "User Info Test":
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Test Usp";
	            return commandString;
	        case "HealthTest":
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Status";
	            
	            return commandString;
	        case "Arm":
	        	commandString = deviceEntity.getPasscode()+" Arm";
	            
	            return commandString;
	        case "Disarm":
	        	commandString = deviceEntity.getPasscode()+" Disarm";
	            
	            return commandString;
	        case "Passcode change":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" pass";
	            
	            return commandString;
	        case "TimeSync":
	        	
	        	
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        case "ZoneLabel":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        case "DialRetry":
	        	commandString = deviceEntity.getUserNo()+deviceEntity.getPasscode()+" Time "+currenTime+"5(Day)";
	            
	            return commandString;
	        default:
	            return null;
	    }
	}
	
	/*
	 * public void hitUrlForCommands(String deviceSerialNumber, String command) {
	 * String orgCommand = ""; GuardNgLogEntity logEntry = new GuardNgLogEntity();
	 * logEntry.setDeviceSerialNumber(deviceSerialNumber);
	 * logEntry.setApiUrl(apiUrl); logEntry.setRequestPayload("{\"PanelPhoneNo\":\""
	 * + deviceSerialNumber + "\",\"MessageType\":\"" + command + "\"}");
	 * logEntry.setRequestTimestamp(LocalDateTime.now());
	 * 
	 * try { DeviceMasterEntity deviceEntity =
	 * deviceMasterRepository.getByDeviceSerialNo(deviceSerialNumber); if
	 * (deviceEntity == null) {
	 * logger.error("No device found with serial number: {}", deviceSerialNumber);
	 * logEntry.setStatus("FAILED"); logEntry.setResponseMessage("No device found");
	 * logEntry.setResponseTimestamp(LocalDateTime.now());
	 * guardNgLogRepository.save(logEntry); return; }else {
	 * if(deviceEntity.getProtectionType().equalsIgnoreCase("Caller ID")) {
	 * 
	 * orgCommand = getCallerIdCommand(deviceEntity,command);
	 * 
	 * }else if(deviceEntity.getProtectionType().equalsIgnoreCase("Passcode")) {
	 * orgCommand = getPasscodeCommand(deviceEntity,command); }else
	 * if(deviceEntity.getProtectionType().equalsIgnoreCase("Passcode+ID")){
	 * orgCommand = getPasscodeIdCommand(deviceEntity,command); }
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * String panelPhoneNumber = deviceEntity.getPhoneNumber(); String
	 * jsonInputString = "{\"PanelPhoneNo\":\"" + panelPhoneNumber +
	 * "\",\"MessageType\":\"" + orgCommand + "\"}";
	 * 
	 * System.out.println("Command--->>"+orgCommand);
	 * 
	 * logger.info("Sending request to URL: {}", apiUrl);
	 * logger.info("Request payload: {}", jsonInputString);
	 * 
	 * URL url = new URL(apiUrl); HttpURLConnection connection = (HttpURLConnection)
	 * url.openConnection(); connection.setRequestMethod("POST");
	 * connection.setRequestProperty("Content-Type", "application/json");
	 * connection.setRequestProperty("Accept", "application/json");
	 * connection.setConnectTimeout(5000); connection.setReadTimeout(10000);
	 * connection.setDoOutput(true);
	 * 
	 * try (OutputStream os = connection.getOutputStream()) { byte[] input =
	 * jsonInputString.getBytes("utf-8"); os.write(input, 0, input.length); }
	 * 
	 * int responseCode = connection.getResponseCode();
	 * logEntry.setResponseCode(responseCode);
	 * 
	 * if (responseCode == HttpURLConnection.HTTP_OK) { try (BufferedReader br = new
	 * BufferedReader( new InputStreamReader(connection.getInputStream(), "utf-8")))
	 * { StringBuilder response = new StringBuilder(); String responseLine; while
	 * ((responseLine = br.readLine()) != null) {
	 * response.append(responseLine.trim()); }
	 * logEntry.setResponseMessage(response.toString());
	 * logEntry.setStatus("SUCCESS"); logger.info("Response: {}",
	 * response.toString()); } } else { logEntry.setStatus("FAILED");
	 * logEntry.setResponseMessage("HTTP request failed with response code " +
	 * responseCode); logger.error("HTTP request failed with response code {}",
	 * responseCode); } logEntry.setResponseTimestamp(LocalDateTime.now());
	 * guardNgLogRepository.save(logEntry);
	 * 
	 * } catch (SocketException e) { logEntry.setStatus("FAILED"); logEntry.
	 * setResponseMessage("SocketException: Unexpected end of file from server");
	 * logEntry.setResponseTimestamp(LocalDateTime.now());
	 * guardNgLogRepository.save(logEntry);
	 * logger.error("SocketException: Unexpected end of file from server", e); }
	 * catch (IOException e) { logEntry.setStatus("FAILED");
	 * logEntry.setResponseMessage("IOException occurred while sending request: " +
	 * e.getMessage()); logEntry.setResponseTimestamp(LocalDateTime.now());
	 * guardNgLogRepository.save(logEntry);
	 * logger.error("IOException occurred while sending request: {}",
	 * e.getMessage(), e); } }
	 */

	
public org.springframework.http.ResponseEntity<Object> hitUrlForCommands(CommandRequestDTO commandRequestDTO) {
		
		System.out.println("commandRequestDTO---->>>>"+apiUrl);
	    
	    GuardNgLogEntity logEntry = new GuardNgLogEntity();
	    logEntry.setDeviceSerialNumber(commandRequestDTO.getDeviceSerialNumber());
	    logEntry.setApiUrl(apiUrl);
	    logEntry.setRequestTimestamp(LocalDateTime.now());

	    try {
	        DeviceMasterEntity deviceEntity = deviceMasterRepository.getByDeviceSerialNo(commandRequestDTO.getDeviceSerialNumber());
	        if (deviceEntity == null) {
	            logger.error("No device found with serial number: {}", commandRequestDTO.getDeviceSerialNumber());
	            logEntry.setStatus("FAILED");
	            logEntry.setResponseMessage("No device found");
	            logEntry.setResponseTimestamp(LocalDateTime.now());
	            logEntry.setCommand(commandRequestDTO.getMessageType());
	            logEntry.setUserId(commandRequestDTO.getLoggedInUser());
	            logEntry.setBranchCode(commandRequestDTO.getBranchCode());
	            guardNgLogRepository.save(logEntry);
	           return new org.springframework.http.ResponseEntity<Object>("No device found", HttpStatus.NOT_FOUND);
	        }

	        String panelPhoneNumber = deviceEntity.getPhoneNumber();

	        // Construct JSON payload with all the fields
	        String jsonInputString = String.format(
	        	    "{\"PanelPhoneNo\":\"%s\",\"MessageType\":\"%s\",\"UserId\":\"%s\",\"Name\":\"%s\"," +
	        	    "\"Passcode\":\"%s\",\"Password\":\"%s\",\"ProtectionType\":\"%s\",\"ZoneId\":\"%s\"," +
	        	    "\"NewLabel\":\"%s\",\"Dial\":\"%s\",\"Retry\":\"%s\",\"UserGroup\":\"%s\",\"ReceiverGroup\":\"%s\"}",
	        	    panelPhoneNumber, commandRequestDTO.getMessageType(), deviceEntity.getUserNo(), commandRequestDTO.getName(),
	        	    deviceEntity.getPasscode(), commandRequestDTO.getPassword(), deviceEntity.getProtectionType(),
	        	    commandRequestDTO.getZoneNo(), commandRequestDTO.getNewLabel(), commandRequestDTO.getDial(),
	        	    commandRequestDTO.getRetry(), commandRequestDTO.getUserGroup(), commandRequestDTO.getReceiverGroup()
	        	);


	        System.out.println(jsonInputString);
	        
	        logEntry.setRequestPayload(jsonInputString);

	        logger.info("Sending request to URL: {}", apiUrl);
	        logger.info("Request payload: {}", jsonInputString);

	        logger.debug("Establishing connection to: {}", apiUrl);
	        logger.debug("Connection established. Sending request...");

	        
	        URL url = new URL(apiUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Connection", "close");

	        connection.setConnectTimeout(10000);
	        connection.setReadTimeout(200000);
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
	                logEntry.setUserId(commandRequestDTO.getLoggedInUser());
	                logEntry.setCommand(commandRequestDTO.getMessageType());
		            logEntry.setBranchCode(commandRequestDTO.getBranchCode());
	                logger.info("Response: {}", response.toString());
	            }
	        } else {
	            logEntry.setStatus("FAILED");

	            logEntry.setUserId(commandRequestDTO.getLoggedInUser());
                logEntry.setCommand(commandRequestDTO.getMessageType());
	            logEntry.setBranchCode(commandRequestDTO.getBranchCode());
	            logEntry.setResponseMessage("HTTP request failed with response code " + responseCode);
	            logger.error("HTTP request failed with response code {}", responseCode);
	        }
	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        guardNgLogRepository.save(logEntry);

	    } catch (SocketException e) {
	        logEntry.setStatus("FAILED");
	        logEntry.setResponseMessage("SocketException: Unexpected end of file from server");
	        logEntry.setUserId(commandRequestDTO.getLoggedInUser());
            logEntry.setCommand(commandRequestDTO.getMessageType());
            logEntry.setBranchCode(commandRequestDTO.getBranchCode());
	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        guardNgLogRepository.save(logEntry);
	        logger.error("SocketException: Unexpected end of file from server", e);
	    } catch (IOException e) {
	        logEntry.setStatus("FAILED");
	        logEntry.setResponseMessage("IOException occurred while sending request: " + e.getMessage());
	        logEntry.setResponseTimestamp(LocalDateTime.now());
	        logEntry.setUserId(commandRequestDTO.getLoggedInUser());
            logEntry.setCommand(commandRequestDTO.getMessageType());
            logEntry.setBranchCode(commandRequestDTO.getBranchCode());
	        guardNgLogRepository.save(logEntry);
	        
	        logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
	    }
	

return new org.springframework.http.ResponseEntity<Object>("Command sent successfully", HttpStatus.OK);
    
}


	public ResponseEntity getGuardNgLogsByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			
			List<GuardNgLogDTO> responseList = new ArrayList<>(); 
			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
			if(responseEntity!=null&&responseEntity.getPayload()!=null) {
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
				
				branchList.forEach(branch->{
					

					
					List<GuardNgLogEntity> guardNgLogEntity = guardNgLogRepository.findByBranchCodeAndStatus(branch.getBranchCode(),"SUCCESS");
					
					if(guardNgLogEntity.isEmpty()) {
						response.setMessage("No logs found for this branch code :"+branchCode);
						response.setStatusCode(HttpStatus.NOT_FOUND.value());
						
					}
					
					guardNgLogEntity.forEach(item->{
						EmployeeMasterEntity employeeMasterEntity = employeeMasterRepository.findByEmpId(item.getUserId());
						
						if(employeeMasterEntity!=null) {
							GuardNgLogDTO guardNgLogDTO = new GuardNgLogDTO();
							
							BeanUtils.copyProperties(item, guardNgLogDTO);
							guardNgLogDTO.setBranchName(branch.getBranchDesc());
							guardNgLogDTO.setUserName(employeeMasterEntity.getEmpName());
							responseList.add(guardNgLogDTO);
						}
					});
					
				});
				
			}
			
			
			
			
			response.setPayload(responseList);
			response.setMessage("Data fetched successfully by branch code:"+branchCode);
			response.setStatusCode(HttpStatus.OK.value());
		}catch (Exception e) {
			response.setMessage("Exception occured while fetching data by branch code :"+branchCode);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
		
	}
}
