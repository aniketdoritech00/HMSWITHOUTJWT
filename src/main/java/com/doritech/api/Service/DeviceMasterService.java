package com.doritech.api.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.api.Common.Message;
import com.doritech.api.DTO.ActiveDeviceLogDetails;
import com.doritech.api.DTO.AlertDetailsDTO;
import com.doritech.api.DTO.AlertTypeDropdownDto;
import com.doritech.api.DTO.AllAlertsDTO;
import com.doritech.api.DTO.DeviceMasterDTO;
import com.doritech.api.DTO.DeviceMasterLogResponse;
import com.doritech.api.DTO.OpenAlertCommunicationDTO;
import com.doritech.api.DTO.SiaProtocalDTO;
import com.doritech.api.DTO.SyncRecord;
import com.doritech.api.DTO.TicketDataDTO;
import com.doritech.api.DTO.VendorDropdownDto;
import com.doritech.api.DTO.ZoneDesiredNameDropdownDto;
import com.doritech.api.DTO.ZoneOriginalNameDropdownDto;
import com.doritech.api.DTO.ZoneVendorProjection;
import com.doritech.api.DTO.siaDetailsDTO;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.CommunicationMapping;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.DynamicConfigurationEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.Heartbeat;
import com.doritech.api.Entity.InputStatusDetailsEntity;
import com.doritech.api.Entity.NonSyncLogEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SiaProtocalEntity;
import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;
import com.doritech.api.Entity.Ticket;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.CallLogRepository;
import com.doritech.api.Repository.CommunicationMappingRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.DynamicConfigurationRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.HeartbeatRepository;
import com.doritech.api.Repository.NonSiaZoneCameraMappingRepository;
import com.doritech.api.Repository.NonSyncLogRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.SiaProtocalRepository;
import com.doritech.api.Repository.SmsDetailsHistoryRepository;
import com.doritech.api.Repository.SmsDetailsRepository;
import com.doritech.api.Repository.SmsLogRepo;
import com.doritech.api.Repository.TicketRepository;
import com.doritech.api.Repository.VendorMasterRepository;

@Service
public class DeviceMasterService {
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	DeviceZoneDetailsRepository deviceZoneDetailsRepository;
	@Autowired
	SmsDetailsRepository smsDetailsRepository;
	@Autowired
	BranchMasterService branchService;
	@Autowired
	SiaProtocalRepository siaProtocalRepository;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	SiaProtocalRepository protocalRepository;
	@Autowired
	ParamService paramService;
	@Autowired
	DynamicConfigurationRepository dynamicConfigurationRepository;
	@Autowired
	TicketRepository ticketDetailsRepository;
	@Autowired
	VendorMasterRepository vendorMasterRepository;
	@Autowired
	HeartbeatRepository heartbeatRepository;
	@Autowired
	private NonSyncLogRepository nonSyncLogRepository;
	@Autowired
	SmsDetailsHistoryRepository smsDetailsHistoryRepository;
	@Autowired
	CommunicationMappingRepository communicationMappingRepository;
	@Autowired
	EmailLogRepo emailLogRepository;
	@Autowired
	SmsLogRepo smsLogRepository;
	@Autowired
	BankMasterRepository bankMasterRepository;
	@Autowired
	CallLogRepository callLogRepository;
	@Autowired
	EscalationMailLogRepository escalationMailLogRepository;

	@Autowired
	NonSiaZoneCameraMappingRepository nonSiaZoneCameraMappingRepository;

	public ResponseEntity editDeviceMaster(DeviceMasterDTO deviceMasterDTO, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		try {
			String prefix = "+91";
			List<Message> messages = validations(deviceMasterDTO);

			LocalDateTime date = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDate = date.format(formatter);

			String loggedInUserEmpId = (String) session.getAttribute("empId");

			if (messages.isEmpty()) {
				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
						.findByDeviceId(deviceMasterDTO.getDeviceId());

				if (deviceMasterEntity == null) {
					response.setMessage("Device not found in the database.");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
				} else {
					// DeviceMasterEntity deviceMaster =
					// deviceMasterRepository.findByBranchCodeAndDeviceSerialNo(deviceMasterDTO.getBranchCode(),
					// deviceMasterDTO.getDeviceSerialNo());

//	                if (deviceMaster != null) {
//	                    response.setMessage("Given Device Serial No is already allocated to the given Branch.");
//	                    response.setStatusCode(HttpStatus.CONFLICT.value());
//	                } else {
//	            	if(deviceMasterDTO.getDeviceType().equals("SIA")) {
//	            		DeviceMasterEntity deviceEntity=new DeviceMasterEntity();
//	    				deviceEntity.setDeviceId(deviceMasterDTO.getDeviceId());
//	    				deviceEntity.setDeviceName(deviceMasterDTO.getDeviceName());
//	    				deviceEntity.setDeviceType(deviceMasterDTO.getDeviceType());
//	    				deviceEntity.setDeviceLocation(deviceMasterDTO.getDeviceLocation());
//	    				deviceEntity.setPhoneNumber(prefix +deviceMasterDTO.getPhoneNumber());
//	    				deviceEntity.setProtectionType(deviceMasterDTO.getProtectionType());
//	    				deviceEntity.setUserNo(deviceMasterDTO.getUserNo());
//	    				deviceEntity.setUserPassword(deviceMasterDTO.getUserPassword());
//	    				deviceEntity.setEmail1(deviceMasterDTO.getEmail1());
//	    				deviceEntity.setProductType(deviceMasterDTO.getProductType());
//	    				deviceEntity.setPasscode(deviceMasterDTO.getPasscode());
//	    				deviceEntity.setAddress(deviceMasterDTO.getAddress());
//	    				deviceEntity.setSupportNo(deviceMasterDTO.getSupportNo());
//	    				deviceEntity.setBranchCode(deviceMasterDTO.getBranchCode());
//	    				deviceEntity.setVendorName(deviceMasterDTO.getVendorName());
//	    				deviceEntity.setVendorEmail(deviceMasterDTO.getVendorEmail());
//	    				deviceEntity.setVendorMobile(deviceMasterDTO.getVendorMobile());
//	    				//deviceEntity.setCreatedBy(httpSession.getAttribute("empId").toString());
//	                    int newCount = deviceMasterEntity.getCount() + 1;
//	    				deviceEntity.setUpdatedDate(formattedDate);
//		                deviceMasterEntity.setCount(newCount); 
//	                    deviceMasterEntity.setUpdationStatus("Edit"); 
//	                    deviceMasterEntity.setUpdatedBy(loggedInUserEmpId);
//	    				deviceEntity.setStatus("Active");
//	    				deviceMasterRepository.save(deviceEntity);
//	    			    List<SiaProtocalEntity> protocalEntities=new ArrayList<>();
//	    			    for(SiaProtocalEntity siaProtocal:deviceMasterDTO.getSiaProtocol()) {
//	    			 	siaProtocal.setDeviceId(deviceMasterDTO.getDeviceId());
//	    			 	protocalEntities.add(siaProtocal);
//	    		     	}
//	    			    protocalRepository.saveAll(protocalEntities);
//	    				response.setPayload(deviceMasterDTO);
//	    				response.setMessage("New SIA Device Updated Successfully..");
//	    				response.setStatusCode(HttpStatus.OK.value());	
//	            	}else {

					BeanUtils.copyProperties(deviceMasterDTO, deviceMasterEntity);
					deviceMasterEntity.setPhoneNumber(prefix + deviceMasterDTO.getPhoneNumber());
					deviceMasterEntity.setUserPassword(deviceMasterDTO.getPhoneNumber());

					int newCount = deviceMasterEntity.getCount() + 1;
					deviceMasterEntity.setUpdatedDate(formattedDate);
					deviceMasterEntity.setCount(newCount);
					deviceMasterEntity.setUpdationStatus("Edit");
					deviceMasterEntity.setUpdatedBy(loggedInUserEmpId);
					deviceMasterEntity.setStatus("Active");

					deviceMasterRepository.save(deviceMasterEntity);

					response.setPayload(deviceMasterEntity);
					response.setMessage("Device Master updated successfully.");
					response.setStatusCode(HttpStatus.OK.value());
//	                }
				}
			} else {
				response.setPayload(messages);
				response.setMessage("Enter valid entries");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error editing Device Master in the database.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/*
	 * public ResponseEntity getAllDevicesByBranchCode(String branchCode) throws
	 * Exception { ResponseEntity response = new ResponseEntity(); try {
	 * List<DeviceMasterDTO> responseList = new ArrayList<>();
	 * 
	 * //List<String> requestList =
	 * branchMasterRepository.findAllByBranchCode(branchCode); ResponseEntity
	 * responseEntity = branchService.getAllChildren(branchCode);
	 * List<BranchMasterEntity> requestList = (List<BranchMasterEntity>)
	 * responseEntity.getPayload();
	 * 
	 * for(BranchMasterEntity x : requestList) {
	 * 
	 * DeviceMasterEntity device =
	 * deviceMasterRepository.findBybranchCode(x.getBranchCode()); if(device!=null)
	 * { DeviceMasterDTO deviceResponse = new DeviceMasterDTO();
	 * 
	 * BeanUtils.copyProperties(device, deviceResponse); BranchMasterEntity branch =
	 * branchMasterRepository.findBybranchCode(device.getBranchCode());
	 * if(branch!=null) {
	 * 
	 * String branchAddressString =
	 * branch.getAddress1()+" "+branch.getAddress2()+" "+branch.getAddress3()+" "
	 * +branch.getAddress4(); deviceResponse.setBranchName(branch.getBranchDesc());
	 * deviceResponse.setBranchAddress(branchAddressString);
	 * deviceResponse.setBankName(branch.getBank()); }
	 * 
	 * 
	 * 
	 * responseList.add(deviceResponse); }else {
	 * response.setMessage("No data present against the given branch code");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); } }
	 * response.setPayload(responseList);
	 * response.setMessage("All devices by branch code fetched successfully.");
	 * response.setStatusCode(HttpStatus.OK.value());
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * response.setMessage("Error fetching data");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); }
	 * 
	 * return response; }
	 */

	/*
	 * public ResponseEntity getAllDevicesByBranchCode(String branchCode) {
	 * ResponseEntity response = new ResponseEntity(); try { // Step 1: Fetch all
	 * child branches in one go ResponseEntity responseEntity =
	 * branchService.getAllChildren(branchCode); List<BranchMasterEntity> branchList
	 * = (List<BranchMasterEntity>) responseEntity.getPayload(); branchList =
	 * branchList == null ? new ArrayList<>() : branchList;
	 * 
	 * if (branchList.isEmpty()) {
	 * response.setMessage("No branches found for given branch code");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); return response; }
	 * 
	 * // Step 2: Collect all branch codes List<String> branchCodes =
	 * branchList.stream().map(BranchMasterEntity::getBranchCode)
	 * .collect(Collectors.toList());
	 * 
	 * // Step 3: Fetch all devices in ONE query List<DeviceMasterEntity> devices =
	 * deviceMasterRepository.findAllByBranchCodeIn(branchCodes);
	 * 
	 * if (devices.isEmpty()) {
	 * response.setMessage("No devices present against the given branch codes");
	 * response.setStatusCode(HttpStatus.OK.value()); return response; }
	 * 
	 * // Step 4: Fetch last 5 SMS records for all devices in one go List<String>
	 * serialNos = devices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
	 * .collect(Collectors.toList());
	 * 
	 * Map<String, List<SmsDetailsEntity>> smsMap =
	 * smsDetailsRepository.findLastFiveRecordsForDevices(serialNos);
	 * 
	 * // Step 5: Prepare final response list List<DeviceMasterDTO> responseList =
	 * new ArrayList<>();
	 * 
	 * for (DeviceMasterEntity device : devices) { DeviceMasterDTO deviceDtO = new
	 * DeviceMasterDTO();
	 * 
	 * deviceDtO.setDeviceId(device.getDeviceId());
	 * deviceDtO.setDeviceName(device.getDeviceName());
	 * deviceDtO.setDeviceSerialNo(device.getDeviceSerialNo());
	 * deviceDtO.setDeviceType(device.getDeviceType());
	 * deviceDtO.setDeviceLocation(device.getDeviceLocation());
	 * deviceDtO.setPhoneNumber(device.getPhoneNumber());
	 * deviceDtO.setProtectionType(device.getProtectionType());
	 * deviceDtO.setUserNo(device.getUserNo());
	 * deviceDtO.setUserPassword(device.getUserPassword());
	 * deviceDtO.setEmail1(device.getEmail1());
	 * deviceDtO.setProductType(device.getProductType());
	 * deviceDtO.setPasscode(device.getPasscode());
	 * deviceDtO.setAddress(device.getAddress());
	 * deviceDtO.setSupportNo(device.getSupportNo());
	 * deviceDtO.setBranchCode(device.getBranchCode());
	 * deviceDtO.setVendorName(device.getVendorName());
	 * deviceDtO.setVendorEmail(device.getVendorEmail());
	 * deviceDtO.setVendorMobile(device.getVendorMobile());
	 * deviceDtO.setStatus(device.getStatus());
	 * 
	 * // Attach branch details BranchMasterEntity branch = branchList.stream()
	 * .filter(b ->
	 * b.getBranchCode().equals(device.getBranchCode())).findFirst().orElse(null);
	 * 
	 * if (branch != null) { String branchAddressString = branch.getAddress1() + " "
	 * + branch.getAddress2() + " " + branch.getAddress3() + " " +
	 * branch.getAddress4(); String parentBranch =
	 * branchMasterRepository.findParentBranchName(device.getBranchCode());
	 * 
	 * deviceDtO.setBranchName(branch.getBranchDesc() + " - " + parentBranch);
	 * deviceDtO.setBranchAddress(branchAddressString);
	 * deviceDtO.setBankName(branch.getBank()); deviceDtO.setIfsc(branch.getIfsc());
	 * }
	 * 
	 * // Attach SMS records List<SmsDetailsEntity> smsRecords =
	 * smsMap.getOrDefault(device.getDeviceSerialNo(), new ArrayList<>()); if
	 * (!smsRecords.isEmpty()) { List<String> lastSyncedTimes =
	 * smsRecords.stream().map(sms -> sms.getDateTime().toString())
	 * .collect(Collectors.toList());
	 * 
	 * deviceDtO.setLastSyncedTimes(lastSyncedTimes);
	 * deviceDtO.setArmedFlag(Integer.parseInt(smsRecords.get(0).getArmed()) == 1 ?
	 * "Armed" : "Disarmed"); }
	 * 
	 * // Exclude inactive devices if
	 * (!"inactive".equalsIgnoreCase(device.getStatus())) {
	 * responseList.add(deviceDtO); } }
	 * 
	 * response.setPayload(responseList);
	 * response.setMessage("All devices by branch code fetched successfully.");
	 * response.setStatusCode(HttpStatus.OK.value());
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * response.setMessage("Error fetching data");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); }
	 * 
	 * return response; }
	 */

//	public ResponseEntity getAllDevicesByBranchCode(String branchCode) {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	        // Step 1: Fetch all child branches in one go
//	        ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
//	        List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
//	        branchList = branchList == null ? new ArrayList<>() : branchList;
//
//	        if (branchList.isEmpty()) {
//	            response.setMessage("No branches found for given branch code");
//	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//	            return response;
//	        }
//
//	        // Step 2: Collect all branch codes
//	        List<String> branchCodes = branchList.stream()
//	                .map(BranchMasterEntity::getBranchCode)
//	                .collect(Collectors.toList());
//
//	        // Step 3: Fetch all devices in ONE query
//	        List<DeviceMasterEntity> devices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);
//
//	        if (devices.isEmpty()) {
//	            response.setMessage("No devices present against the given branch codes");
//	            response.setStatusCode(HttpStatus.OK.value());
//	            return response;
//	        }
//
//	        // Step 4: Fetch last 5 SMS records for all devices (with history fallback)
//	        List<String> serialNos = devices.stream()
//	                .map(DeviceMasterEntity::getDeviceSerialNo)
//	                .collect(Collectors.toList());
//
//	        Map<String, List<SmsDetailsEntity>> smsMap = fetchLastFiveRecordsWithHistory(serialNos);
//
//	        // Step 5: Prepare final response list
//	        List<DeviceMasterDTO> responseList = new ArrayList<>();
//
//	        for (DeviceMasterEntity device : devices) {
//	            DeviceMasterDTO deviceDtO = new DeviceMasterDTO();
//
//	            deviceDtO.setDeviceId(device.getDeviceId());
//	            deviceDtO.setDeviceName(device.getDeviceName());
//	            deviceDtO.setDeviceSerialNo(device.getDeviceSerialNo());
//	            deviceDtO.setDeviceType(device.getDeviceType());
//	            deviceDtO.setDeviceLocation(device.getDeviceLocation());
//	            deviceDtO.setPhoneNumber(device.getPhoneNumber());
//	            deviceDtO.setProtectionType(device.getProtectionType());
//	            deviceDtO.setUserNo(device.getUserNo());
//	            deviceDtO.setUserPassword(device.getUserPassword());
//	            deviceDtO.setEmail1(device.getEmail1());
//	            deviceDtO.setProductType(device.getProductType());
//	            deviceDtO.setPasscode(device.getPasscode());
//	            deviceDtO.setAddress(device.getAddress());
//	            deviceDtO.setSupportNo(device.getSupportNo());
//	            deviceDtO.setBranchCode(device.getBranchCode());
//	            deviceDtO.setVendorName(device.getVendorName());
//	            deviceDtO.setVendorEmail(device.getVendorEmail());
//	            deviceDtO.setVendorMobile(device.getVendorMobile());
//	            deviceDtO.setStatus(device.getStatus());
//
//	            // Attach branch details
//	            BranchMasterEntity branch = branchList.stream()
//	                    .filter(b -> b.getBranchCode().equals(device.getBranchCode()))
//	                    .findFirst()
//	                    .orElse(null);
//
//	            if (branch != null) {
//	                String branchAddressString = branch.getAddress1() + " " + branch.getAddress2() + " "
//	                        + branch.getAddress3() + " " + branch.getAddress4();
//	                String parentBranch = branchMasterRepository.findParentBranchName(device.getBranchCode());
//
//	                deviceDtO.setBranchName(branch.getBranchDesc() + " - " + parentBranch);
//	                deviceDtO.setBranchAddress(branchAddressString);
//	                deviceDtO.setBankName(branch.getBank());
//	                deviceDtO.setIfsc(branch.getIfsc());
//	            }
//
//	            // Attach SMS records
//	         // Attach SMS records
//	            List<SmsDetailsEntity> smsRecords = smsMap.getOrDefault(device.getDeviceSerialNo(), new ArrayList<>());
//	            if (!smsRecords.isEmpty()) {
//
//	                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
//	                List<String> lastSyncedTimes = smsRecords.stream()
//	                        .map(sms -> {
//	                            // Format datetime
//	                            String formattedDate = formatter.format(sms.getDateTime());
//
//	                            // Determine medium GPRS or SMS
//	                            String sender = sms.getSenderMobileNo();
//	                            String medium = (sender != null && sender.toUpperCase().contains("GPRS")) ? "GPRS" : "SMS";
//
//	                            return formattedDate + " : " + medium;
//	                        })
//	                        .collect(Collectors.toList());
//
//	                deviceDtO.setLastSyncedTimes(lastSyncedTimes);
//	                deviceDtO.setArmedFlag(Integer.parseInt(smsRecords.get(0).getArmed()) == 1 ? "Armed" : "Disarmed");
//	            }
//
//
//	            // Exclude inactive devices
//	            if (!"inactive".equalsIgnoreCase(device.getStatus())) {
//	                responseList.add(deviceDtO);
//	            }
//	        }
//
//	        response.setPayload(responseList);
//	        response.setMessage("All devices by branch code fetched successfully.");
//	        response.setStatusCode(HttpStatus.OK.value());
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.setMessage("Error fetching data");
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//
//	    return response;
//	}

//	// Helper method to fetch SMS records with history fallback
//	private Map<String, List<SmsDetailsEntity>> fetchLastFiveRecordsWithHistory(List<String> serialNos) {
//	    // First fetch from main sms_details table
//	    Map<String, List<SmsDetailsEntity>> smsMap = smsDetailsRepository.findLastFiveRecordsForDevices(serialNos);
//	    
//	    // Check which devices need more records
//	    Map<String, List<SmsDetailsEntity>> finalMap = new HashMap<>();
//	    
//	    for (String serialNo : serialNos) {
//	        List<SmsDetailsEntity> mainRecords = smsMap.getOrDefault(serialNo, new ArrayList<>());
//	        int recordsNeeded = 5 - mainRecords.size();
//	        
//	        if (recordsNeeded > 0) {
//	            // Fetch remaining records from history table
//	            List<SmsDetailsHistoryEntity> historyRecords = 
//	                smsDetailsHistoryRepository.findTopByPanelSerialNoOrderByDateTimeDesc(serialNo, recordsNeeded);
//	            
//	            // Convert history entities to main entities and combine
//	            List<SmsDetailsEntity> combinedRecords = new ArrayList<>(mainRecords);
//	            for (SmsDetailsHistoryEntity historyRecord : historyRecords) {
//	                SmsDetailsEntity convertedRecord = convertHistoryToMain(historyRecord);
//	                combinedRecords.add(convertedRecord);
//	            }
//	            finalMap.put(serialNo, combinedRecords);
//	        } else {
//	            finalMap.put(serialNo, mainRecords);
//	        }
//	    }
//	    
//	    return finalMap;
//	}
//
//	// Helper method to convert history entity to main entity
//	private SmsDetailsEntity convertHistoryToMain(SmsDetailsHistoryEntity historyEntity) {
//	    SmsDetailsEntity entity = new SmsDetailsEntity();
//	    entity.setPanelSerialNo(historyEntity.getPanelSerialNo());
//	    entity.setDateTime(historyEntity.getDateTime());
//	    entity.setArmed(historyEntity.getArmed());
//	    // Add other fields mapping as per your entity structure
//	    // entity.setOtherField(historyEntity.getOtherField());
//	    return entity;
//	}

//comment on 25 mar 2026
	// -------------------------------------------------------
	// MAIN METHOD
	// -------------------------------------------------------
//	public ResponseEntity getAllDevicesByBranchCode(String branchCode) {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	        // Step 1: Fetch all child branches
//	        ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
//	        List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
//	        branchList = branchList == null ? new ArrayList<>() : branchList;
//
//	        if (branchList.isEmpty()) {
//	            response.setMessage("No branches found for given branch code");
//	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//	            return response;
//	        }
//
//	        // Step 2: Collect all branch codes
//	        List<String> branchCodes = branchList.stream()
//	                .map(BranchMasterEntity::getBranchCode)
//	                .collect(Collectors.toList());
//
//	        // Step 3: Fetch all devices in ONE query
//	        List<DeviceMasterEntity> devices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);
//
//	        if (devices.isEmpty()) {
//	            response.setMessage("No devices present against the given branch codes");
//	            response.setStatusCode(HttpStatus.OK.value());
//	            return response;
//	        }
//
//	        // Step 4: Collect all serial numbers
//	        List<String> serialNos = devices.stream()
//	                .map(DeviceMasterEntity::getDeviceSerialNo)
//	                .collect(Collectors.toList());
//	        System.out.println("=== Serial Numbers in IN clause (" + serialNos + ") ===");
//
//	        // Step 5: Fetch SMS + history ONCE — reused in both sync and armedFlag
//	        Map<String, List<SmsDetailsEntity>> smsMap = fetchLastFiveRecordsWithHistory(serialNos);
//
//	        // Step 6: Fetch merged top-5 sync records (SMS + GPRS + HEARTBEAT)
//	        Map<String, List<SyncRecord>> syncMap = fetchTop5MergedRecords(serialNos, smsMap);
//
//	        // Step 7: Build response list
//	        List<DeviceMasterDTO> responseList = new ArrayList<>();
//	        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
//	        for (DeviceMasterEntity device : devices) {
//
//	            // Exclude inactive devices early
//	            if ("inactive".equalsIgnoreCase(device.getStatus())) {
//	                continue;
//	            }
//
//	            DeviceMasterDTO deviceDtO = new DeviceMasterDTO();
//
//	            deviceDtO.setDeviceId(device.getDeviceId());
//	            deviceDtO.setDeviceName(device.getDeviceName());
//	            deviceDtO.setDeviceSerialNo(device.getDeviceSerialNo());
//	            deviceDtO.setDeviceType(device.getDeviceType());
//	            deviceDtO.setDeviceLocation(device.getDeviceLocation());
//	            deviceDtO.setPhoneNumber(device.getPhoneNumber());
//	            deviceDtO.setProtectionType(device.getProtectionType());
//	            deviceDtO.setUserNo(device.getUserNo());
//	            deviceDtO.setUserPassword(device.getUserPassword());
//	            deviceDtO.setEmail1(device.getEmail1());
//	            deviceDtO.setProductType(device.getProductType());
//	            deviceDtO.setPasscode(device.getPasscode());
//	            deviceDtO.setAddress(device.getAddress());
//	            deviceDtO.setSupportNo(device.getSupportNo());
//	            deviceDtO.setBranchCode(device.getBranchCode());
//	            deviceDtO.setVendorName(device.getVendorName());
//	            deviceDtO.setVendorEmail(device.getVendorEmail());
//	            deviceDtO.setVendorMobile(device.getVendorMobile());
//	            deviceDtO.setStatus(device.getStatus());
//
//	            // Attach branch details
//	            BranchMasterEntity branch = branchList.stream()
//	                    .filter(b -> b.getBranchCode().equals(device.getBranchCode()))
//	                    .findFirst()
//	                    .orElse(null);
//
//	            if (branch != null) {
//	                String branchAddressString = branch.getAddress1() + " " + branch.getAddress2() + " "
//	                        + branch.getAddress3() + " " + branch.getAddress4();
//	                String parentBranch = branchMasterRepository.findParentBranchName(device.getBranchCode());
//
//	                deviceDtO.setBranchName(branch.getBranchDesc() + " - " + parentBranch);
//	                deviceDtO.setBranchAddress(branchAddressString);
//	                deviceDtO.setBankName(branch.getBank());
//	                deviceDtO.setIfsc(branch.getIfsc());
//	            }
//
//	            // Attach merged sync records — top 5 by datetime DESC
//	            List<SyncRecord> syncRecords = syncMap.getOrDefault(device.getDeviceSerialNo(), new ArrayList<>());
//	            if (!syncRecords.isEmpty()) {
//	                List<String> lastSyncedTimes = syncRecords.stream()
//	                        .map(r -> {
//	                            Date date = Date.from(r.getDatetime()
//	                                    .atZone(ZoneId.systemDefault()).toInstant());
//	                            return formatter.format(date) + " : " + r.getMedium();
//	                        })
//	                        .collect(Collectors.toList());
//
//	                deviceDtO.setLastSyncedTimes(lastSyncedTimes);
//	            }
//
//	            // Armed flag — only from SMS (heartbeat has no armed status)
//	            List<SmsDetailsEntity> smsList = smsMap.getOrDefault(device.getDeviceSerialNo(), new ArrayList<>());
//	            if (!smsList.isEmpty()) {
//	                deviceDtO.setArmedFlag(
//	                        Integer.parseInt(smsList.get(0).getArmed()) == 1 ? "Armed" : "Disarmed"
//	                );
//	            }
//
//	            responseList.add(deviceDtO);
//	        }
//
//	        response.setPayload(responseList);
//	        response.setMessage("All devices by branch code fetched successfully.");
//	        response.setStatusCode(HttpStatus.OK.value());
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.setMessage("Error fetching data");
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//
//	    return response;
//	}

	public ResponseEntity getNonSyncedDevicesCountNew(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		try {

			// Default payload so null never comes
			Map<String, Object> result = new HashMap<>();
			result.put("notSyncedDeviceCount", 0);

			// Step 1: Get threshold hours
			int thresholdHours = getThresholdHours(); // e.g. 6

			// Step 2: Get all devices
			ResponseEntity devicesResponse = getAllDevicesByBranchCode(branchCode);

			if (!Integer.valueOf(200).equals(devicesResponse.getStatusCode())) {

				response.setMessage(devicesResponse.getMessage());
				response.setStatusCode(devicesResponse.getStatusCode());

				response.setPayload(result);

				return response;
			}

			@SuppressWarnings("unchecked")
			List<DeviceMasterDTO> devices = (List<DeviceMasterDTO>) devicesResponse.getPayload();

			if (devices == null || devices.isEmpty()) {

				response.setMessage("No active devices found");
				response.setStatusCode(HttpStatus.OK.value());

				response.setPayload(result);

				return response;
			}

			// Step 3: Current time
			LocalDateTime currentTime = LocalDateTime.now();

			// Step 4: Har device ka panelSerialNo → non_sync_log mein check karo
			long nonSyncedCount = devices.stream().filter(device -> {

				String serialNo = device.getDeviceSerialNo();

				if (serialNo == null) {
					return false;
				}

				// DB se open entry fetch karo
				Optional<NonSyncLogEntity> openEntry = nonSyncLogRepository
						.findByPanelSerialNoAndNonSyncEndDatetimeIsNull(serialNo);

				// Entry nahi mili → synced hai
				if (openEntry.isEmpty()) {
					return false;
				}

				LocalDateTime startDatetime = openEntry.get().getNonSyncStartDatetime();

				// startDatetime null hai → skip
				if (startDatetime == null) {
					return false;
				}

				// Current time - startDatetime >= thresholdHours?
				long hoursDiff = ChronoUnit.HOURS.between(startDatetime, currentTime);

				System.out.println("Device: " + serialNo + " | start: " + startDatetime + " | hoursDiff: " + hoursDiff
						+ " | threshold: " + thresholdHours);

				return hoursDiff >= thresholdHours;

			}).count();

			// Step 5: Response banao
			result.put("notSyncedDeviceCount", nonSyncedCount);

			response.setPayload(result);

			response.setMessage("Non-synced devices count fetched from DB. Total: " + devices.size() + ", Non-synced: "
					+ nonSyncedCount);

			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {

			Map<String, Object> result = new HashMap<>();
			result.put("notSyncedDeviceCount", 0);

			response.setPayload(result);

			e.printStackTrace();

			response.setMessage("Error: " + e.getMessage());

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	/**
	 * Get count of devices that haven't synced within threshold hours for a given
	 * branch
	 * 
	 * @param branchCode The branch code to check
	 * @return ResponseEntity containing count of non-synced devices
	 */
//	public ResponseEntity getNonSyncedDevicesCountNew(String branchCode) {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	        // Step 1: Get threshold hours
//	        int thresholdHours = getThresholdHours();
//	        
//	        // Step 2: Get all devices using existing method logic
//	        ResponseEntity devicesResponse = getAllDevicesByBranchCode(branchCode);
//	       
//
//	        if (!Integer.valueOf(200).equals(devicesResponse.getStatusCode())) {
//	            response.setMessage(devicesResponse.getMessage());
//	            response.setStatusCode(devicesResponse.getStatusCode());
//	            return response;
//	        }
//	        List<DeviceMasterDTO> devices = (List<DeviceMasterDTO>) devicesResponse.getPayload();
//	        
//	        if (devices == null || devices.isEmpty()) {
//	            response.setMessage("No active devices found");
//	            response.setStatusCode(HttpStatus.OK.value());
//	            response.setPayload(0);
//	            return response;
//	        }
//	        
//	        // Step 3: Get current system time
//	        LocalDateTime currentTime = LocalDateTime.now();
//	        
//	        // Step 4: Count devices where latest sync time is beyond threshold OR no sync time
//	        long nonSyncedCount = devices.stream()
//	            .filter(device -> {
//	                List<String> lastSyncedTimes = device.getLastSyncedTimes();
//	                
//	                // Case 1: No sync records at all - treat as non-synced
//	                if (lastSyncedTimes == null || lastSyncedTimes.isEmpty()) {
//	                    return true;
//	                }
//	                
//	                // Get the latest sync time (first element in the list)
//	                String latestSyncTimeStr = lastSyncedTimes.get(0);
//	                // Parse the date from format "dd-MM-yyyy HH:mm:ss : Medium"
//	                String datePart = latestSyncTimeStr.split(" : ")[0];
//	                
//	                try {
//	                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//	                    Date syncDate = formatter.parse(datePart);
//	                    LocalDateTime syncDateTime = syncDate.toInstant()
//	                        .atZone(ZoneId.systemDefault())
//	                        .toLocalDateTime();
//	                    
//	                    // Calculate hours difference
//	                    long hoursDiff = Duration.between(syncDateTime, currentTime).toHours();
//	                    
//	                    // Case 2: Device is non-synced if hours difference >= threshold
//	                    return hoursDiff >= thresholdHours;
//	                    
//	                } catch (ParseException e) {
//	                    System.out.println("Error parsing date: " + datePart);
//	                    return true; // On parse error, consider as non-synced
//	                }
//	            })
//	            .count();
//	     // BAAD MEIN (sahi)
//	        Map<String, Object> result = new HashMap<>();
//	        result.put("notSyncedDeviceCount", nonSyncedCount);
//	        response.setPayload(result);
//	        response.setMessage("Non-synced devices count calculated successfully. Total devices: " + devices.size() + ", Non-synced: " + nonSyncedCount);
//	        response.setStatusCode(HttpStatus.OK.value());
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.setMessage("Error calculating non-synced devices count: " + e.getMessage());
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//	    return response;
//	}
	public ResponseEntity getOfflineDeviceCountByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			// Step 1: Get all devices (reuse existing method internally)
			// We'll duplicate the core logic to access syncMap directly
			// OR refactor getAllDevicesByBranchCode to return internal data

			// --- Fetch branches ---
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No branches found for given branch code");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.collect(Collectors.toList());

			// --- Fetch active devices ---
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);
			List<DeviceMasterEntity> devices = allDevices.stream()
					.filter(d -> !"inactive".equalsIgnoreCase(d.getStatus())).collect(Collectors.toList());

			if (devices.isEmpty()) {
				response.setMessage("No active devices found");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(0);
				return response;
			}

			List<String> serialNos = devices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.collect(Collectors.toList());

			// Step 2: Get smsMap and syncMap (reuse existing methods)
			Map<String, List<SmsDetailsEntity>> smsMap = fetchLastFiveRecordsWithHistory(serialNos);
			Map<String, List<SyncRecord>> syncMap = fetchTop5MergedRecords(serialNos, smsMap);

			// Step 3: Get threshold
			int hours = getThresholdHours();
			LocalDateTime now = LocalDateTime.now();

			// Step 4: Count devices where latest sync >= threshold hours ago
			long offlineCount = devices.stream().filter(device -> {
				List<SyncRecord> records = syncMap.getOrDefault(device.getDeviceSerialNo(), Collections.emptyList());

				if (records.isEmpty()) {
					// No sync record at all → definitely offline
					return true;
				}

				// Only 1st (latest) record lena hai
				LocalDateTime latestSyncTime = records.get(0).getDatetime();

				// Gap in hours between now and last sync
				long gapHours = ChronoUnit.HOURS.between(latestSyncTime, now);

				// >= threshold → offline/stale
				return gapHours >= hours;
			}).count();

			response.setPayload(offlineCount);
			response.setMessage("Offline device count fetched successfully. Threshold: " + hours + " hours");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching offline device count");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	private static final int DEFAULT_SYNC_THRESHOLD_HOURS = 36;

	private int getThresholdHours() {
		System.out.println("Enter in param to get TIme In houurs...");
		try {
			ParamEntity paramEntity = paramRepository.findByCodeAndSerial("SCHEDULAR", "NON_SYNC");
			if (paramEntity != null && paramEntity.getDescp3() != null) {
				return Integer.parseInt(paramEntity.getDescp3());
			}
		} catch (NumberFormatException e) {
			System.out.println("SYNC_THRESHOLD_HOURS param invalid, using default: {}" + DEFAULT_SYNC_THRESHOLD_HOURS);
		}
		return DEFAULT_SYNC_THRESHOLD_HOURS;
	}

	// latest coded written in 25 mar 2026
	public ResponseEntity getAllDevicesByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			// Step 1: Fetch all child branches
			long t0 = System.currentTimeMillis();
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
			System.out.println("Step1 getAllChildren: " + (System.currentTimeMillis() - t0) + "ms");
			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No branches found for given branch code");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}
			long t1 = System.currentTimeMillis();
			// Step 2: Collect all branch codes
			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.collect(Collectors.toList());

			// Step 3: Fetch all devices in ONE query
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);
			System.out.println("Step2 findAllByBranchCodeIn (" + allDevices.size() + " devices): "
					+ (System.currentTimeMillis() - t1) + "ms");

			// Filter inactive early — before any heavy processing
			List<DeviceMasterEntity> devices = allDevices.stream()
					.filter(d -> !"inactive".equalsIgnoreCase(d.getStatus())).collect(Collectors.toList());

			if (devices.isEmpty()) {
				response.setMessage("No devices present against the given branch codes");
				response.setStatusCode(HttpStatus.OK.value());
				return response;
			}

			// Step 4: Collect serial numbers (only active devices)
			List<String> serialNos = devices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.collect(Collectors.toList());
			System.out.println("Active devices: " + devices.size() + ", SerialNos: " + serialNos.size());
			long t2 = System.currentTimeMillis();
			// Step 5: FIX N+1 — Fetch ALL parent branch names in ONE query
			// branchCodes list already available — batch fetch
			Map<String, String> parentBranchNameMap = new HashMap<>();
			branchMasterRepository.findParentBranchNames(branchCodes).forEach(arr -> {
				String branchCodee = (String) arr[0];
				String parentName = arr[1] != null ? (String) arr[1] : "";
				if (branchCodee != null) { // ✅ branchCodee
					parentBranchNameMap.put(branchCodee, parentName); // ✅ branchCodee
				}
			});
			System.out.println("Step3 findParentBranchNames: " + (System.currentTimeMillis() - t2) + "ms");

			// --- NEW: Bulk fetch bank names from bank_master using the bank code from
			// branch_master ---
			long tBank = System.currentTimeMillis();
			List<String> bankCodes = branchList.stream().map(BranchMasterEntity::getBank) // ✅ column 'bank' now holds
																							// the bank_code
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			final Map<String, String> bankNameMap;
			if (!bankCodes.isEmpty()) {
				List<BankMasterEntity> bankMasters = bankMasterRepository.findByBankCodeIn(bankCodes);
				bankNameMap = bankMasters.stream().collect(
						Collectors.toMap(BankMasterEntity::getBankCode, BankMasterEntity::getBankName, (a, b) -> a));
			} else {
				bankNameMap = Collections.emptyMap();
			}
			System.out.println("Step3b fetchBankNames: " + (System.currentTimeMillis() - tBank) + "ms");

			// Step 6: Branch lookup map
			Map<String, BranchMasterEntity> branchMap = branchList.stream()
					.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, b -> b, (a, b) -> a));
			long t3 = System.currentTimeMillis();
			// Step 7: Fetch SMS + history ONCE — reused in both sync and armedFlag
			Map<String, List<SmsDetailsEntity>> smsMap = fetchLastFiveRecordsWithHistory(serialNos);
//	        System.out.println("fetchLastFiveRecordsWithHistory :: "+smsMap);
//			System.out.println("Step4 fetchLastFiveRecordsWithHistory: " + (System.currentTimeMillis() - t3) + "ms");
			long t4 = System.currentTimeMillis();
			// Step 8: Fetch merged top-5 sync records (SMS + GPRS + HEARTBEAT)
			Map<String, List<SyncRecord>> syncMap = fetchTop5MergedRecords(serialNos, smsMap);
			// System.out.println("fetchTop5MergedRecords ::"+syncMap);
			System.out.println("Step5 fetchTop5MergedRecords: " + (System.currentTimeMillis() - t4) + "ms");
			long t5 = System.currentTimeMillis();
			// Step 9: Build response list
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			List<DeviceMasterDTO> responseList = devices.stream().map(device -> {
				DeviceMasterDTO dto = mapDeviceToDTO(device);

				// Attach branch details — O(1) lookup
				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				if (branch != null) {
					String branchAddressString = branch.getAddress1() + " " + branch.getAddress2() + " "
							+ branch.getAddress3() + " " + branch.getAddress4();
					String parentName = parentBranchNameMap.getOrDefault(device.getBranchCode(), "");

					// --- Resolve bank name using bank_master ---
					String bankCodeFromBranch = branch.getBank(); // now holds the bank code
					String bankName = bankNameMap.getOrDefault(bankCodeFromBranch, "");
					// -------------------------------------------

					dto.setBranchName(branch.getBranchDesc() + " - " + parentName);
					dto.setBranchAddress(branchAddressString);
					dto.setBankName(bankName); // actual bank name
					dto.setIfsc(branch.getIfsc());
				}

				// Attach merged sync records
				List<SyncRecord> syncRecords = syncMap.getOrDefault(device.getDeviceSerialNo(),
						Collections.emptyList());
				if (!syncRecords.isEmpty()) {
					List<String> lastSyncedTimes = syncRecords.stream().map(r -> {
						Date date = Date.from(r.getDatetime().atZone(ZoneId.systemDefault()).toInstant());
						return formatter.format(date) + " : " + r.getMedium();
					}).collect(Collectors.toList());
					dto.setLastSyncedTimes(lastSyncedTimes);
				}

				// Armed flag — only from SMS
				List<SmsDetailsEntity> smsList = smsMap.getOrDefault(device.getDeviceSerialNo(),
						Collections.emptyList());
				if (!smsList.isEmpty()) {
					dto.setArmedFlag("1".equals(smsList.get(0).getArmed()) ? "Armed" : "Disarmed");
				}

				return dto;
			}).collect(Collectors.toList());
			System.out.println("Step6 build response list: " + (System.currentTimeMillis() - t5) + "ms");
			System.out.println("=== TOTAL TIME: " + (System.currentTimeMillis() - t0) + "ms ===");

			response.setPayload(responseList);
			response.setMessage("All devices by branch code fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	// Clean mapper — extracted from loop
	private DeviceMasterDTO mapDeviceToDTO(DeviceMasterEntity device) {
		DeviceMasterDTO dto = new DeviceMasterDTO();
		dto.setDeviceId(device.getDeviceId());
		dto.setDeviceName(device.getDeviceName());
		dto.setDeviceSerialNo(device.getDeviceSerialNo());
		dto.setDeviceType(device.getDeviceType());
		dto.setDeviceLocation(device.getDeviceLocation());
		dto.setPhoneNumber(device.getPhoneNumber());
		dto.setProtectionType(device.getProtectionType());
		dto.setUserNo(device.getUserNo());
		dto.setUserPassword(device.getUserPassword());
		dto.setEmail1(device.getEmail1());
		dto.setProductType(device.getProductType());
		dto.setPasscode(device.getPasscode());
		dto.setAddress(device.getAddress());
		dto.setSupportNo(device.getSupportNo());
		dto.setBranchCode(device.getBranchCode());
		dto.setVendorName(device.getVendorName());
		dto.setVendorEmail(device.getVendorEmail());
		dto.setVendorMobile(device.getVendorMobile());
		dto.setStatus(device.getStatus());
		return dto;
	}

	// -------------------------------------------------------
	// HELPER 1: Fetch SMS + history, fill up to 5 per device
	// -------------------------------------------------------
//	private Map<String, List<SmsDetailsEntity>> fetchLastFiveRecordsWithHistory(List<String> serialNos) {
//	    Map<String, List<SmsDetailsEntity>> smsMap = smsDetailsRepository.findLastFiveRecordsForDevices(serialNos);
//
//	    Map<String, List<SmsDetailsEntity>> finalMap = new HashMap<>();
//
//	    for (String serialNo : serialNos) {
//	        List<SmsDetailsEntity> mainRecords = smsMap.getOrDefault(serialNo, new ArrayList<>());
//	        int recordsNeeded = 5 - mainRecords.size();
//
//	        if (recordsNeeded > 0) {
//	            List<SmsDetailsHistoryEntity> historyRecords =
//	                    smsDetailsHistoryRepository.findTopByPanelSerialNoOrderByDateTimeDesc(serialNo, recordsNeeded);
//
//	            List<SmsDetailsEntity> combinedRecords = new ArrayList<>(mainRecords);
//	            for (SmsDetailsHistoryEntity historyRecord : historyRecords) {
//	                combinedRecords.add(convertHistoryToMain(historyRecord));
//	            }
//	            finalMap.put(serialNo, combinedRecords);
//	        } else {
//	            finalMap.put(serialNo, mainRecords);
//	        }
//	    }
//
//	    return finalMap;
//	}

	// comment on 25 mar 2026
	/*
	 * private Map<String, List<SmsDetailsEntity>>
	 * fetchLastFiveRecordsWithHistory(List<String> serialNos) {
	 * 
	 * List<SmsDetailsEntity> mainRecords =
	 * smsDetailsRepository.getLatestFiveRecords(serialNos);
	 * 
	 * Map<String, List<SmsDetailsEntity>> finalMap = new HashMap<>();
	 * 
	 * // group main records by panel Map<String, List<SmsDetailsEntity>> mainMap =
	 * mainRecords.stream()
	 * .collect(Collectors.groupingBy(SmsDetailsEntity::getPanelSerialNo));
	 * 
	 * for (String serialNo : serialNos) {
	 * 
	 * List<SmsDetailsEntity> records = new
	 * ArrayList<>(mainMap.getOrDefault(serialNo, new ArrayList<>()));
	 * 
	 * int remaining = 5 - records.size();
	 * 
	 * if (remaining > 0) {
	 * 
	 * List<SmsDetailsHistoryEntity> historyRecords =
	 * smsDetailsHistoryRepository.getLatestFiveRecords(List.of(serialNo));
	 * 
	 * historyRecords.stream() .limit(remaining) .map(this::convertHistoryToMain)
	 * .forEach(records::add); }
	 * 
	 * finalMap.put(serialNo, records); }
	 * 
	 * return finalMap; }
	 */

	private Map<String, List<SmsDetailsEntity>> fetchLastFiveRecordsWithHistory(List<String> serialNos) {

		Map<String, List<SmsDetailsEntity>> finalMap = new HashMap<>();

		for (String serialNo : serialNos) {
			List<SmsDetailsEntity> records = new ArrayList<>();

			// Step A: sms_details table se fetch karo
			List<SmsDetailsEntity> mainRecords = smsDetailsRepository.getLatestFiveBySerial(serialNo);
			System.out.println("Serial: " + serialNo + " | sms_details count: " + mainRecords.size());
			records.addAll(mainRecords);

			// Step B: agar 5 se kam hai to history se fill karo
			int remaining = 5 - records.size();
			if (remaining > 0) {
				List<SmsDetailsHistoryEntity> history = smsDetailsHistoryRepository.getLatestFiveBySerial(serialNo);
				System.out.println("Serial: " + serialNo + " | sms_details_history count: " + history.size());

				history.stream().limit(remaining).map(this::convertHistoryToMain).forEach(records::add);
			}

			// Step C: datetime ke basis pe sort karo — latest pehle
			records.sort(Comparator.comparing(SmsDetailsEntity::getDateTime).reversed());

			finalMap.put(serialNo, records);
		}

		return finalMap;
	}

	// -------------------------------------------------------
	// HELPER 2: Merge SMS + Heartbeat, return top 5 per device
	// -------------------------------------------------------
	private Map<String, List<SyncRecord>> fetchTop5MergedRecords(List<String> serialNos,
			Map<String, List<SmsDetailsEntity>> smsMap) {

		// Fetch all heartbeats in ONE query — ordered by id DESC from DB
		List<Heartbeat> allHeartbeats = heartbeatRepository.findAllByPanelSerialNumberIn(serialNos);

		// Group by serial number — order preserved
		Map<String, List<Heartbeat>> heartbeatMap = allHeartbeats.stream()
				.collect(Collectors.groupingBy(Heartbeat::getPanelSerialNumber));

		Map<String, List<SyncRecord>> finalMap = new HashMap<>();

		for (String serialNo : serialNos) {
			List<SyncRecord> unified = new ArrayList<>();

			// Convert SMS records → SyncRecord
			List<SmsDetailsEntity> smsList = smsMap.getOrDefault(serialNo, new ArrayList<>());
			for (SmsDetailsEntity sms : smsList) {
				String sender = sms.getSenderMobileNo();
				String medium = (sender != null && sender.toUpperCase().contains("GPRS")) ? "GPRS" : "SMS";
				LocalDateTime dt = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				unified.add(new SyncRecord(dt, medium));
			}

			// Convert Heartbeat records → SyncRecord (max 5 per device)
			List<Heartbeat> hbList = heartbeatMap.getOrDefault(serialNo, new ArrayList<>());
			hbList.stream().limit(5).forEach(hb -> unified.add(new SyncRecord(hb.getHeartbeatDatetime(), "GPRS")));

			// Merge both, sort by datetime DESC, take top 5 overall
			List<SyncRecord> top5 = unified.stream().sorted(Comparator.comparing(SyncRecord::getDatetime).reversed())
					.limit(5).collect(Collectors.toList());

			finalMap.put(serialNo, top5);
		}

		return finalMap;
	}

	// -------------------------------------------------------
	// HELPER 3: Convert history entity → main SMS entity
	// -------------------------------------------------------
	private SmsDetailsEntity convertHistoryToMain(SmsDetailsHistoryEntity historyEntity) {
		SmsDetailsEntity entity = new SmsDetailsEntity();
		entity.setPanelSerialNo(historyEntity.getPanelSerialNo());
		entity.setDateTime(historyEntity.getDateTime());
		entity.setArmed(historyEntity.getArmed());
		entity.setSenderMobileNo(historyEntity.getSenderMobileNo());
		return entity;
	}

	public ResponseEntity getDeviceMasterByDeviceId(String deviceId) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<DeviceMasterEntity> deviceMasterList = deviceMasterRepository
					.findByDeviceIdContainingPattern(deviceId);
			List<DeviceMasterEntity> responseList = new ArrayList();
			if (deviceMasterList != null) {
				for (DeviceMasterEntity device : deviceMasterList) {
					DeviceMasterEntity deviceMasterEntity = new DeviceMasterEntity();
					BeanUtils.copyProperties(device, deviceMasterEntity);

					responseList.add(deviceMasterEntity);
				}
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data fetched with the given deviceId suggestion");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching details.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getDeviceAndZoneDetailsByDeviceId(String deviceId) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findByDeviceId(deviceId);
			if (device.getDeviceType().equals("SIA")) {
				List<SiaProtocalEntity> sia = protocalRepository.findByDeviceId(deviceId);
				DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();

				deviceMasterDTO.setDeviceSerialNo(device.getDeviceSerialNo());
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceName(device.getDeviceName());
				deviceMasterDTO.setDeviceType(device.getDeviceType());
				deviceMasterDTO.setDeviceLocation(device.getDeviceLocation());
				deviceMasterDTO.setPhoneNumber(device.getPhoneNumber());
				deviceMasterDTO.setProtectionType(device.getProtectionType());
				deviceMasterDTO.setUserNo(device.getUserNo());
				deviceMasterDTO.setUserPassword(device.getUserPassword());
				deviceMasterDTO.setEmail1(device.getEmail1());
				deviceMasterDTO.setProductType(device.getProductType());
				deviceMasterDTO.setPasscode(device.getPasscode());
				deviceMasterDTO.setAddress(device.getAddress());
				deviceMasterDTO.setSupportNo(device.getSupportNo());
				deviceMasterDTO.setBranchCode(device.getBranchCode());
				deviceMasterDTO.setVendorName(device.getVendorName());
				deviceMasterDTO.setVendorEmail(device.getVendorEmail());
				deviceMasterDTO.setVendorMobile(device.getVendorMobile());
				deviceMasterDTO.setStatus(device.getStatus());
				deviceMasterDTO.setSiaProtocol(sia);
				response.setPayload(deviceMasterDTO);
				response.setMessage("Device Details fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				List<DeviceZoneDetailsEntity> list = deviceZoneDetailsRepository.getZoneDetailsByDeviceId(deviceId);
				// System.out.println(list);
				List<DynamicConfigurationEntity> dynamicConfigurationEntities = dynamicConfigurationRepository
						.findByDeviceId(deviceId);
				if (device != null && list != null && dynamicConfigurationEntities != null) {
					DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
					BeanUtils.copyProperties(device, deviceMasterDTO);
					deviceMasterDTO.setPhoneNumber(device.getPhoneNumber().substring(3));

					deviceMasterDTO.setZoneList(list);
					deviceMasterDTO.setDynamicConfigurationList(dynamicConfigurationEntities);

					response.setPayload(deviceMasterDTO);
					response.setMessage("Device Details fetched successfully.");
					response.setStatusCode(HttpStatus.OK.value());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllDevicesAndSmsDetailsByBranchCode(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<DeviceMasterEntity> listOfDevices = deviceMasterRepository.getAllDevicesByBranchCode(branchCode);
			List<DeviceMasterDTO> responseList = new ArrayList<>();

			if (listOfDevices != null) {
				for (DeviceMasterEntity entity : listOfDevices) {
					DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
					String deviceSerialNo = entity.getDeviceSerialNo();

					// Retrieve SMS details list instead of optional
					List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository.findByPanelSerialNo(deviceSerialNo);

					// Check if the list is not empty
					if (!smsDetailsList.isEmpty()) {
						// Assuming you want to work with the latest SMS detail for each device
						SmsDetailsEntity latestSmsDetails = smsDetailsList.get(smsDetailsList.size() - 1); // Assuming
																											// latest
																											// record is
																											// at the
																											// end of
																											// the list
						BeanUtils.copyProperties(entity, deviceMasterDTO);
						deviceMasterDTO.setSmsDetails(latestSmsDetails);
						responseList.add(deviceMasterDTO);
					} else {
						response.setMessage("No sms details are present in the database.");
						response.setStatusCode(HttpStatus.NOT_FOUND.value());
						return response; // Return response immediately if no SMS details found for a device
					}
				}

				response.setPayload(responseList);
				response.setMessage("All Devices and SMS details fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("There is no data related to the provided branch code");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getDeviceCountByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAll();

			List<DeviceMasterEntity> devicesByBranchCode = allDevices.stream()
					.filter(device -> branchCode.equals(device.getBranchCode())).collect(Collectors.toList());

			if (devicesByBranchCode.isEmpty()) {
				response.setMessage("No Device present in the db for branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				int deviceCountByBranchCode = devicesByBranchCode.size();
				int totalDeviceCount = allDevices.size();
				List<Integer> deviceCounts = new ArrayList<>();
				deviceCounts.add(deviceCountByBranchCode);
				deviceCounts.add(totalDeviceCount);

				System.out.println(deviceCounts.get(0) + "/" + deviceCounts.get(1));

				response.setPayload(deviceCounts);
				response.setMessage(
						"Total number of devices for branch code " + branchCode + ": " + deviceCountByBranchCode);
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching device count by branch code");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getTotalDeviceCount(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {

			int count = 0, totalCount = 0;
			List<Integer> deviceCounts = new ArrayList<>();

			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> requestList = (List<BranchMasterEntity>) responseEntity.getPayload();

			// List<String> requestList =
			// branchMasterRepository.findAllByBranchCode(branchCode);

			requestList = requestList == null ? new ArrayList<>() : requestList;

			for (BranchMasterEntity item : requestList) {

				DeviceMasterEntity deviceList = deviceMasterRepository.findBybranchCode(item.getBranchCode());

				if (deviceList != null) {

					totalCount++;
				}

			}

			deviceCounts.add(totalCount);
			response.setPayload(deviceCounts);
			response.setMessage("Total number of devices for branch code ");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching device count by branch code");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getDeviceLogDetails(String branchCodeRequest) {
		ResponseEntity response = new ResponseEntity();

		try {
			ResponseEntity branchResponseEntity = branchService.getAllChildren(branchCodeRequest);
			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

			if (branches == null || branches.isEmpty()) {
				response.setMessage("No branches found for the given branch code.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			List<DeviceMasterLogResponse> responseList = new ArrayList<>();
			boolean deviceFound = false;

			for (BranchMasterEntity branch : branches) {
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());

				if (device != null) {
					deviceFound = true;
					BranchMasterEntity branchMasterEntity = branchMasterRepository
							.findBybranchCode(device.getBranchCode());

					if (branchMasterEntity != null) {
						SmsDetailsEntity latestRecord = smsDetailsRepository
								.getLatestRecord(device.getDeviceSerialNo());

						if (latestRecord != null) {
							Date lastUpdatedDateTime = latestRecord.getDateTime();
							Date currentDate = new Date();

							long timeDifferenceMillis = currentDate.getTime() - lastUpdatedDateTime.getTime();
							String notSyncedFromFormatted = formatTimeDifference(timeDifferenceMillis);

							DeviceMasterLogResponse deviceMasterLogResponse = new DeviceMasterLogResponse();
							deviceMasterLogResponse.setDeviceName(device.getDeviceName());
							deviceMasterLogResponse.setPanelSerialNo(device.getDeviceSerialNo());
							deviceMasterLogResponse.setLastUpdatedDateTime(
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastUpdatedDateTime));
							deviceMasterLogResponse.setNotSyncedFrom(notSyncedFromFormatted);
							deviceMasterLogResponse.setBranchName(branchMasterEntity.getBranchDesc());
							deviceMasterLogResponse.setBankName(branchMasterEntity.getBank());
							deviceMasterLogResponse.setVendorName(device.getVendorName());
							deviceMasterLogResponse.setMobile(branchMasterEntity.getMobile());

							responseList.add(deviceMasterLogResponse);
						}
					}
				}
			}

			if (!responseList.isEmpty()) {
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else if (!deviceFound) {
				response.setMessage("No device found for any of the given branch codes");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setMessage("No data is present for the given branch codes");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching details");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	private String formatTimeDifference(long timeDifferenceMillis) {
		long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) - TimeUnit.HOURS.toSeconds(hours)
				- TimeUnit.MINUTES.toSeconds(minutes);

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	/*
	 * public ResponseEntity deleteDeviceMasterByDeviceId(String deviceId) throws
	 * Exception { ResponseEntity response = new ResponseEntity(); try {
	 * DeviceMasterEntity deviceMasterEntity =
	 * deviceMasterRepository.getById(deviceId); if (deviceMasterEntity != null) {
	 * List<DeviceZoneDetailsEntity> list =
	 * deviceZoneDetailsRepository.findAllByDeviceId(deviceId); if (list != null &&
	 * !list.isEmpty()) { for (DeviceZoneDetailsEntity detailsEntity : list) {
	 * deviceZoneDetailsRepository.delete(detailsEntity); } // //
	 * deviceMasterEntity.setStatus("inActive"); //
	 * deviceMasterRepository.save(deviceMasterEntity);
	 * deviceMasterRepository.deleteById(deviceId);
	 * response.setMessage("Device Master Deleted successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } else {
	 * deviceMasterRepository.deleteById(deviceId);
	 * response.setMessage("Device Master Deleted successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } } else {
	 * response.setMessage("No device is available with the given device id");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); }
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * response.setMessage("Error deleting device master"); // Fix: Correct typo in
	 * the error message
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); }
	 * 
	 * return response; }
	 */

	@Transactional
	public ResponseEntity deleteDeviceMasterByDeviceId(String deviceId) {

		ResponseEntity response = new ResponseEntity();

		try {

			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findById(deviceId).orElse(null);

			if (deviceMasterEntity == null) {
				response.setMessage("No device is available with the given device id");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			List<DeviceZoneDetailsEntity> zones = deviceZoneDetailsRepository.findAllByDeviceId(deviceId);

			if (zones != null && !zones.isEmpty()) {

				for (DeviceZoneDetailsEntity zone : zones) {

					nonSiaZoneCameraMappingRepository.deleteByZoneId(zone.getId());
				}

				deviceZoneDetailsRepository.deleteAll(zones);
			}

			deviceMasterRepository.delete(deviceMasterEntity);

			response.setMessage("Device Master Deleted successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {

			e.printStackTrace();
			response.setMessage("Error deleting device master");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getActiveDeviceLogDetails(String branchCodeRequest) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<ActiveDeviceLogDetails> responseList = new ArrayList<>();
			// List<String> branchCodeList =
			// branchMasterRepository.findAllByBranchCode(branchCodeRequest);

			ResponseEntity responseEntity = branchService.getAllChildren(branchCodeRequest);
			List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) responseEntity.getPayload();

			// System.out.println(branchCodeList);
			for (BranchMasterEntity branchCode : branchCodeList) {

				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode.getBranchCode());

				if (device != null) {
					List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository
							.findByPanelSerialNo(device.getDeviceSerialNo());

					String deviceName = device.getDeviceName();
					String deviceType = device.getDeviceType();
					String deviceLocation = device.getDeviceLocation();
					String mobileNo = device.getPhoneNumber();
					String branchName = branchCode.getBranchDesc();
					String bankName = branchCode.getBank();
					String vendor = device.getVendorName();

					for (SmsDetailsEntity smsDetailsEntity : smsDetailsList) {
						String alarmTime = String.valueOf(smsDetailsEntity.getDateTime());
						String alarmDescription = smsDetailsEntity.getCompleteMessage();
						List<String> alertList = getDesiredNamesOfActiveZones(smsDetailsEntity);

						for (String name : alertList) {

							ActiveDeviceLogDetails activeDetails = new ActiveDeviceLogDetails();
							activeDetails.setDeviceSerialNo(smsDetailsEntity.getPanelSerialNo());
							activeDetails.setDeviceName(deviceName);
							activeDetails.setDeviceType(deviceType);
							activeDetails.setDeviceLocation(deviceLocation);
							activeDetails.setMobileNo(mobileNo);
							activeDetails.setAlertType(name);
							activeDetails.setAlarmTime(alarmTime);
							activeDetails.setAlarmDescription(alarmDescription);
							activeDetails.setBranchName(branchName);
							activeDetails.setBankName(bankName);
							activeDetails.setVendorName(vendor);

							responseList.add(activeDetails);
						}
					}
				}

			}

			if (!responseList.isEmpty()) {
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the given branch codes.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public List<String> getDesiredNamesOfActiveZones(SmsDetailsEntity smsDetailsEntity) {
		List<String> responseList = new ArrayList();
		DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
				.findByDeviceSerialNo(smsDetailsEntity.getPanelSerialNo());
		String deviceId = deviceMasterEntity.getDeviceId();
		// System.out.println(deviceId);
		if (smsDetailsEntity.getZone1().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE1").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone2().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE2").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone3().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE3").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone4().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE4").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone5().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE5").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone6().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE6").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone7().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE7").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone8().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE8").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone9().equals("A")) {
			responseList
					.add(deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE9").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone10().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE10").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone11().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE11").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone12().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE12").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone13().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE13").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone14().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE14").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone15().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE15").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone16().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE16").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone17().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE17").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone18().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE18").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone19().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE19").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone20().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE20").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone21().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE21").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone22().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE22").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone23().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE23").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone24().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE24").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone25().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE25").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone26().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE26").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone27().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE27").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone28().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE28").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone29().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE29").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone30().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE30").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone31().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE31").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone32().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE32").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone33().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE33").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone34().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE34").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone35().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE35").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone36().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE36").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone37().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE37").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone38().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE38").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone39().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE39").getZoneDesiredName());
		}
		if (smsDetailsEntity.getZone40().equals("A")) {
			responseList.add(
					deviceZoneDetailsRepository.findByDeviceIdAndZoneName(deviceId, "ZONE40").getZoneDesiredName());
		}

		// System.out.println(responseList);
		return responseList;
	}

	public ResponseEntity addDeviceMaster(DeviceMasterDTO deviceMasterDTO) {
		ResponseEntity response = new ResponseEntity();

		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = date.format(formatter);

//		String loggedInUserEmpId = (String) session.getAttribute("empId");
		try {

			String prefix = "+91";
			List<Message> messages = validations(deviceMasterDTO);

			if (messages.isEmpty()) {
				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
						.findByDeviceId(deviceMasterDTO.getDeviceId());

				if (deviceMasterEntity != null) {
					response.setMessage("Device already present in the database.");
					response.setStatusCode(HttpStatus.FOUND.value());
				} else {
					DeviceMasterEntity deviceMaster = deviceMasterRepository.findByBranchCodeAndDeviceSerialNo(
							deviceMasterDTO.getBranchCode(), deviceMasterDTO.getDeviceSerialNo());

					if (deviceMaster != null && deviceMaster.getStatus().equalsIgnoreCase("Active")) {
						response.setMessage("Given Device Serial No is already allocated to the given Branch.");
						response.setStatusCode(HttpStatus.CONFLICT.value());
					} else {
						List<DeviceMasterEntity> dEntities = deviceMasterRepository
								.getAllByDeviceSerialNo(deviceMasterDTO.getDeviceSerialNo());

						if (dEntities != null && !dEntities.isEmpty()
								&& deviceMaster.getStatus().equalsIgnoreCase("Active")) {
							response.setMessage("Given Serial No is already allocated to a branch.");
							response.setStatusCode(HttpStatus.CONFLICT.value());
						} else {

							DeviceMasterEntity device = new DeviceMasterEntity();
							BeanUtils.copyProperties(deviceMasterDTO, device);

							if ("".equalsIgnoreCase(deviceMasterDTO.getDeviceSerialNo())) {
								ParamEntity paramEntity = paramRepository.findByCode("UNIQUE_DEVICE_ID");
								device.setDeviceSerialNo(paramEntity.getDescp1());

								System.out.println("---------->>>>>" + device.getDeviceSerialNo() + "=========>>>>>>>>>"
										+ paramEntity.getDescp1());
								paramService.updateDeviceUniqueId(paramEntity);
							}

							device.setUserPassword(deviceMasterDTO.getPhoneNumber());
							device.setPhoneNumber(prefix + deviceMasterDTO.getPhoneNumber());

							device.setCount(1);
							device.setCreatedDate(formattedDate);
							device.setUpdationStatus("Save");
//							device.setCreatedBy(loggedInUserEmpId);

							device.setStatus("Active");
							deviceMasterRepository.save(device);
							response.setPayload(device);
							response.setMessage("Device Master saved successfully.");
							response.setStatusCode(HttpStatus.OK.value());
						}
					}
				}
			} else {
				response.setPayload(messages);
				response.setMessage("Enter valid entries");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error adding Device Master to the database.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity checkBranch(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
			if (device == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Device not found");
			} else if (device != null && "active".equalsIgnoreCase(device.getStatus())) {
				response.setMessage("Device already exists for this branch code");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error fetching device");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/*
	 * public ResponseEntity addDeviceMaster(DeviceMasterDTO deviceMasterDTO) throws
	 * Exception { ResponseEntity response = new ResponseEntity(); try { String
	 * prefix = "+91"; List<Message> messages = validations(deviceMasterDTO); if
	 * (messages.isEmpty()) { DeviceMasterEntity deviceMasterEntity =
	 * deviceMasterRepository.findByDeviceId(deviceMasterDTO.getDeviceId()); if
	 * (deviceMasterEntity != null) {
	 * response.setMessage("Device already present in the database.");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); } else {
	 * DeviceMasterEntity device = new DeviceMasterEntity();
	 * BeanUtils.copyProperties(deviceMasterDTO, device);
	 * device.setUserPassword(deviceMasterDTO.getPhoneNumber());
	 * device.setPhoneNumber(prefix+deviceMasterDTO.getPhoneNumber());
	 * 
	 * deviceMasterRepository.save(device);
	 * 
	 * response.setPayload(device);
	 * response.setMessage("Device Master saved successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } } else {
	 * response.setPayload(messages);
	 * response.setMessage("Enter the valid enteries");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); } } catch (Exception
	 * e) { e.printStackTrace();
	 * response.setMessage("Error adding device Master in the database.");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 */

	public List<Message> validations(DeviceMasterDTO deviceMasterDTO) {
		List<Message> messageList = new ArrayList<>();

		if (deviceMasterDTO.getDeviceName() == null || StringUtils.isEmpty(deviceMasterDTO.getDeviceName())) {
			messageList.add(new Message("Device Name", "Device Name can't be null or empty"));
		}
		if (deviceMasterDTO.getDeviceType() == null || StringUtils.isEmpty(deviceMasterDTO.getDeviceType())) {
			messageList.add(new Message("Device Type", "Device Type can't be null or empty"));
		}
//		if (deviceMasterDTO.getDeviceSerialNo() == null || StringUtils.isEmpty(deviceMasterDTO.getDeviceSerialNo())) {
//			messageList.add(new Message("Device Serial No", "Device Serial No can't be null or empty"));
//		}
		if (deviceMasterDTO.getDeviceLocation() == null || StringUtils.isEmpty(deviceMasterDTO.getDeviceLocation())) {
			messageList.add(new Message("Device Location", "Device Location can't be null or empty"));
		}
		if (deviceMasterDTO.getPhoneNumber() == null || StringUtils.isEmpty(deviceMasterDTO.getPhoneNumber())) {
			messageList.add(new Message("Mobile Number", "Mobile Number can't be null or empty"));
		}
		if (deviceMasterDTO.getProtectionType() == null || StringUtils.isEmpty(deviceMasterDTO.getProtectionType())) {
			messageList.add(new Message("Protection Type", "Protection Type can't be null or empty"));
		}
		if (deviceMasterDTO.getUserNo() == null || StringUtils.isEmpty(deviceMasterDTO.getUserNo())) {
			messageList.add(new Message("User No", "Usre No can't be null or empty"));
		}
		if (deviceMasterDTO.getEmail1() == null || StringUtils.isEmpty(deviceMasterDTO.getEmail1())) {
			messageList.add(new Message("Email1", "Email1 can't be null or empty"));
		}

		if (deviceMasterDTO.getAddress() == null || StringUtils.isEmpty(deviceMasterDTO.getAddress())) {
			messageList.add(new Message("Address", "Address can't be null or empty"));
		}
//		if (deviceMasterDTO.getSupportNo() == null || StringUtils.isEmpty(deviceMasterDTO.getSupportNo())) {
//			messageList.add(new Message("Support No", "Support No can't be null or empty"));
//		}
		if (deviceMasterDTO.getBranchCode() == null || StringUtils.isEmpty(deviceMasterDTO.getBranchCode())) {
			messageList.add(new Message("Branch Code", "Branch Code can't be null or empty"));
		}
		return messageList;
	}

	public ResponseEntity getCountOfZonesByType(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			int cctvCount = 0, securityAlarmCount = 0, fireAlarmCount = 0;
			Map<String, Object> zoneCountList = new HashMap<>();
			ResponseEntity branches = branchService.getAllChildren(branchCode);
			if (!branches.equals(null)) {

				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branches.getPayload();
				for (BranchMasterEntity branchMasterEntity : branchList) {
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(branchMasterEntity.getBranchCode());
					if (deviceMasterEntity != null) {
						List<DeviceZoneDetailsEntity> deviceZoneDetailsEntity = deviceZoneDetailsRepository
								.findAllByDeviceId(deviceMasterEntity.getDeviceId());
						if (!deviceZoneDetailsEntity.isEmpty()) {
							for (DeviceZoneDetailsEntity x : deviceZoneDetailsEntity) {
								String zoneName = x.getZoneDesiredName();
								cctvCount += "CCTV".equalsIgnoreCase(zoneName) ? 1 : 0;
								securityAlarmCount += "SECURITY ALARM".equalsIgnoreCase(zoneName) ? 1 : 0;
								fireAlarmCount += "FIRE ALARM".equalsIgnoreCase(zoneName) ? 1 : 0;
							}
						}

					}

				}
				zoneCountList.put("cctv", cctvCount);
				zoneCountList.put("securityAlarm", securityAlarmCount);
				zoneCountList.put("fireAlarm", fireAlarmCount);

				response.setPayload(zoneCountList);
				response.setMessage("Count fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}

		} catch (Exception e) {
			response.setMessage("Error occured while fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getDeviceDetailsByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
			if (device != null && device.getDeviceType().equalsIgnoreCase("SIA")) {

				List<SiaProtocalEntity> sia = protocalRepository.findByDeviceId(device.getDeviceId());
				DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceName(device.getDeviceName());
				deviceMasterDTO.setDeviceType(device.getDeviceType());
				deviceMasterDTO.setDeviceLocation(device.getDeviceLocation());
				deviceMasterDTO.setPhoneNumber(device.getPhoneNumber());
				deviceMasterDTO.setProtectionType(device.getProtectionType());
				deviceMasterDTO.setUserNo(device.getUserNo());
				deviceMasterDTO.setUserPassword(device.getUserPassword());
				deviceMasterDTO.setEmail1(device.getEmail1());
				deviceMasterDTO.setProductType(device.getProductType());
				deviceMasterDTO.setPasscode(device.getPasscode());
				deviceMasterDTO.setAddress(device.getAddress());
				deviceMasterDTO.setSupportNo(device.getSupportNo());
				deviceMasterDTO.setBranchCode(device.getBranchCode());
				deviceMasterDTO.setVendorName(device.getVendorName());
				deviceMasterDTO.setVendorEmail(device.getVendorEmail());
				deviceMasterDTO.setVendorMobile(device.getVendorMobile());
				deviceMasterDTO.setStatus(device.getStatus());
				deviceMasterDTO.setSiaProtocol(sia);
				response.setPayload(deviceMasterDTO);
				response.setMessage("Device Details fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());

			} else {
				response.setPayload(device);
				response.setMessage("Device not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			}
		} catch (Exception e) {
			response.setMessage("Device fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		}
		return response;
	}

	public ResponseEntity getAllDeviceMaster() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<DeviceMasterEntity> listDeviceMaster = deviceMasterRepository.findAll();
			List<BranchMasterEntity> listBranchMaster = branchMasterRepository.findAll();
			List<DeviceMasterDTO> list = new ArrayList<>();

			for (DeviceMasterEntity deviceMaster : listDeviceMaster) {
				DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
				BeanUtils.copyProperties(deviceMaster, deviceMasterDTO);

				SmsDetailsEntity sms = smsDetailsRepository.getLatestRecord(deviceMaster.getDeviceSerialNo());

				for (BranchMasterEntity branchMaster : listBranchMaster) {
					if (branchMaster.getBranchCode().equals(deviceMaster.getBranchCode())) {
						deviceMasterDTO.setBankName(branchMaster.getBank());
						deviceMasterDTO.setBranchName(branchMaster.getBranchDesc());

						if (sms != null) {

							deviceMasterDTO.setLastSyncedTime(sms.getDateTime().toString());
						}

						break;
					}
				}

				list.add(deviceMasterDTO);
			}

			response.setPayload(list);
			response.setMessage("Data Fetching..");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred!!" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getInputStatusDetails(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<InputStatusDetailsEntity> inputResponseList = new ArrayList<>();
			Map<String, Integer> alarmTypeCountMap = new HashMap<>();
			Set<String> processedAlerts = new HashSet<>();
			Map<String, SmsDetailsEntity> lastOpenAlertPerZone = new HashMap<>();

			ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
			branchCodeList = branchCodeList == null ? new ArrayList<>() : branchCodeList;

			for (BranchMasterEntity branchCodeEntity : branchCodeList) {
				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
						.findBybranchCode(branchCodeEntity.getBranchCode());
				if (deviceMasterEntity == null)
					continue;

				List<SmsDetailsEntity> smsDetailsEntityList = smsDetailsRepository
						.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo());
				if (smsDetailsEntityList == null || smsDetailsEntityList.isEmpty())
					continue;

				for (SmsDetailsEntity sms : smsDetailsEntityList) {
					for (int i = 1; i <= 40; i++) {
						String methodName = "getZone" + i;
						try {
							Method method = sms.getClass().getMethod(methodName);
							String zone = (String) method.invoke(sms);

							if (!"A".equals(zone))
								continue;

							String zoneKey = sms.getPanelSerialNo() + "_Zone" + i;

							// Prevent repeated open alerts for same zone
							if (lastOpenAlertPerZone.containsKey(zoneKey)) {
								continue; // Already tracking an open alert for this zone
							}

							String uniqueAlertKey = sms.getPanelSerialNo() + "_Zone" + i + "_"
									+ sms.getDateTime().toString();
							if (processedAlerts.contains(uniqueAlertKey))
								continue;

							boolean isResolved = false;
							SmsDetailsEntity resolvedSms = null;

							for (SmsDetailsEntity nextSms : smsDetailsEntityList) {
								if (!nextSms.getDateTime().after(sms.getDateTime()))
									continue;

								Method nextMethod = nextSms.getClass().getMethod(methodName);
								String nextZone = (String) nextMethod.invoke(nextSms);

								if ("N".equals(nextZone)) {
									isResolved = true;
									resolvedSms = nextSms;
									break;
								}
							}

							// Track only unresolved open alerts
							if (!isResolved) {
								lastOpenAlertPerZone.put(zoneKey, sms);
							}

							// If resolved, mark both current and resolved alerts as processed
							if (isResolved && resolvedSms != null) {
								processedAlerts.add(uniqueAlertKey);
								processedAlerts.add(sms.getPanelSerialNo() + "_Zone" + i + "_"
										+ resolvedSms.getDateTime().toString());
							} else {
								processedAlerts.add(uniqueAlertKey);
							}

							InputStatusDetailsEntity inputStatusDetailsEntity = new InputStatusDetailsEntity();
							BranchMasterEntity branch = branchCodeEntity;
							String branchName = branch.getBranchDesc();
							String bankName = branch.getBank();
							String mobile = branch.getMobile();

							String zoneOriginalNameString = "Zone" + i;
							String zoneName = "";
							DeviceZoneDetailsEntity deviceZoneEntity = deviceZoneDetailsRepository
									.findByDeviceIdAndZoneName(deviceMasterEntity.getDeviceId(),
											zoneOriginalNameString);
							if (deviceZoneEntity != null) {
								zoneName = deviceZoneEntity.getZoneDesiredName();
								inputStatusDetailsEntity.setAlertType(deviceZoneEntity.getAlertType());
							}

							DateTimeFormatter formatterFirst = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
							ZoneId zoneId = ZoneId.systemDefault(); // Or use specific ZoneId if needed

							LocalDateTime alarmTime = sms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();
							inputStatusDetailsEntity.setAlarmTime(alarmTime.format(formatterFirst));

							inputStatusDetailsEntity.setDeviceSerialNo(sms.getPanelSerialNo());
							inputStatusDetailsEntity.setAlarmtype(zoneName);
							// inputStatusDetailsEntity.setAlarmTime(sms.getDateTime().toString());
							inputStatusDetailsEntity.setDeviceName(deviceMasterEntity.getDeviceName());
							inputStatusDetailsEntity.setBankName(bankName);
							inputStatusDetailsEntity.setBranchName(branchName);
							inputStatusDetailsEntity.setMobileNo(mobile);
							inputStatusDetailsEntity.setVendorName(deviceMasterEntity.getVendorName());
							inputStatusDetailsEntity.setIfsc(branch.getIfsc());

							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

							if (isResolved && resolvedSms != null) {
								LocalDateTime resolvedTime = resolvedSms.getDateTime().toInstant().atZone(zoneId)
										.toLocalDateTime();
								inputStatusDetailsEntity.setIssueResolvedTime(resolvedTime.format(formatterFirst));

								inputStatusDetailsEntity.setAlarmDescription("Resolved");

								LocalDateTime alarmDateTime = LocalDateTime.parse(sms.getDateTime().toString(),
										formatter);
								LocalDateTime issueResolvedTime = LocalDateTime
										.parse(resolvedSms.getDateTime().toString(), formatter);
								Duration duration = Duration.between(alarmDateTime, issueResolvedTime);

								long days = duration.toDays();
								long hours = duration.minusDays(days).toHours();
								long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

								String timeDiff = (days > 0)
										? String.format("%d days, %d hours, and %d minutes", days, hours, minutes)
										: (hours > 0) ? String.format("%d hours and %d minutes", hours, minutes)
												: String.format("%d minutes", minutes);

								inputStatusDetailsEntity.setTimeDifference(timeDiff);
							} else {
								// inputStatusDetailsEntity.setIssueResolvedTime("2025-04-24 00:00:00.0");
								inputStatusDetailsEntity.setAlarmDescription("Open");
								inputStatusDetailsEntity.setTimeDifference("Ongoing");
							}

							alarmTypeCountMap.put(zoneName, alarmTypeCountMap.getOrDefault(zoneName, 0) + 1);
							inputResponseList.add(inputStatusDetailsEntity);

						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}

			Map<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("inputStatusDetails", inputResponseList);
			finalResponse.put("alarmTypeCounts", alarmTypeCountMap);

			response.setPayload(finalResponse);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred while fetching");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity getInputStatusDetails(String branchCode) {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	        // Get all branch codes in one call
//	        ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);
//	        List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
//	        if (branchCodeList == null || branchCodeList.isEmpty()) {
//	            return createEmptyResponse();
//	        }
//
//	        // Extract branch codes for batch operations
//	        List<String> branchCodes = branchCodeList.stream()
//	                .map(BranchMasterEntity::getBranchCode)
//	                .collect(Collectors.toList());
//
//	        // Batch fetch all devices for all branches
//	        List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);
//	        if (allDevices.isEmpty()) {
//	            return createEmptyResponse();
//	        }
//
//	        // Create maps for quick lookup
//	        Map<String, BranchMasterEntity> branchMap = branchCodeList.stream()
//	                .collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));
//	        
//	        Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
//	                .collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, Function.identity()));
//
//	        // Get all device serial numbers for batch SMS fetch
//	        List<String> deviceSerialNos = allDevices.stream()
//	                .map(DeviceMasterEntity::getDeviceSerialNo)
//	                .collect(Collectors.toList());
//
//	        // Batch fetch all SMS details ordered by dateTime for efficient processing
//	        List<SmsDetailsEntity> allSmsDetails = smsDetailsRepository
//	                .findByPanelSerialNoInOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos);
//
//	        // Group SMS by panel serial number for efficient processing
//	        Map<String, List<SmsDetailsEntity>> smsGroupedByPanel = allSmsDetails.stream()
//	                .collect(Collectors.groupingBy(SmsDetailsEntity::getPanelSerialNo));
//
//	        // Batch fetch all device zone details
//	        List<String> deviceIds = allDevices.stream()
//	                .map(DeviceMasterEntity::getDeviceId)
//	                .collect(Collectors.toList());
//	        
//	        List<DeviceZoneDetailsEntity> allZoneDetails = deviceZoneDetailsRepository
//	                .findByDeviceIdIn(deviceIds);
//	        
//	        Map<String, Map<String, DeviceZoneDetailsEntity>> zoneDetailsMap = allZoneDetails.stream()
//	                .collect(Collectors.groupingBy(
//	                    DeviceZoneDetailsEntity::getDeviceId,
//	                    Collectors.toMap(DeviceZoneDetailsEntity::getZoneOriginalName, Function.identity())
//	                ));
//
//	        // Process data efficiently
//	        List<InputStatusDetailsEntity> inputResponseList = new ArrayList<>();
//	        Map<String, Integer> alarmTypeCountMap = new HashMap<>();
//	        
//	        // Pre-compile zone methods for reflection optimization
//	        Map<Integer, Method> zoneMethods = new HashMap<>();
//	        for (int i = 1; i <= 40; i++) {
//	            try {
//	                zoneMethods.put(i, SmsDetailsEntity.class.getMethod("getZone" + i));
//	            } catch (NoSuchMethodException e) {
//	                // Skip if method doesn't exist
//	            }
//	        }
//
//	        // Process each device
//	        for (DeviceMasterEntity device : allDevices) {
//	            BranchMasterEntity branch = branchMap.get(device.getBranchCode());
//	            if (branch == null) continue;
//
//	            List<SmsDetailsEntity> deviceSmsDetails = smsGroupedByPanel.get(device.getDeviceSerialNo());
//	            if (deviceSmsDetails == null || deviceSmsDetails.isEmpty()) continue;
//
//	            Map<String, DeviceZoneDetailsEntity> deviceZones = zoneDetailsMap.get(device.getDeviceId());
//
//	            // Process alerts more efficiently
//	            processDeviceAlerts(device, branch, deviceSmsDetails, deviceZones, 
//	                             zoneMethods, inputResponseList, alarmTypeCountMap);
//	        }
//
//	        // Prepare final response
//	        Map<String, Object> finalResponse = new HashMap<>();
//	        finalResponse.put("inputStatusDetails", inputResponseList);
//	        finalResponse.put("alarmTypeCounts", alarmTypeCountMap);
//
//	        response.setPayload(finalResponse);
//	        response.setMessage("Data fetched successfully");
//	        response.setStatusCode(HttpStatus.OK.value());
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.setMessage("Error occurred while fetching");
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//	    return response;
//	}

//	private void processDeviceAlerts(DeviceMasterEntity device, BranchMasterEntity branch,
//	                               List<SmsDetailsEntity> smsDetailsList, 
//	                               Map<String, DeviceZoneDetailsEntity> deviceZones,
//	                               Map<Integer, Method> zoneMethods,
//	                               List<InputStatusDetailsEntity> inputResponseList,
//	                               Map<String, Integer> alarmTypeCountMap) {
//	    
//	    // Track open alerts per zone to avoid duplicates
//	    Map<String, SmsDetailsEntity> lastOpenAlertPerZone = new HashMap<>();
//	    Set<String> processedAlerts = new HashSet<>();
//	    
//	    // Pre-calculate zone status for all SMS entries
//	    Map<String, Map<Integer, String>> smsZoneStatusMap = new HashMap<>();
//	    for (SmsDetailsEntity sms : smsDetailsList) {
//	        Map<Integer, String> zoneStatuses = new HashMap<>();
//	        for (Map.Entry<Integer, Method> entry : zoneMethods.entrySet()) {
//	            try {
//	                String zoneStatus = (String) entry.getValue().invoke(sms);
//	                zoneStatuses.put(entry.getKey(), zoneStatus);
//	            } catch (Exception e) {
//	                // Skip on error
//	            }
//	        }
//	        smsZoneStatusMap.put(sms.getUniqueId(), zoneStatuses);
//	    }
//	    
//	    // Process each SMS entry
//	    for (int smsIndex = 0; smsIndex < smsDetailsList.size(); smsIndex++) {
//	        SmsDetailsEntity sms = smsDetailsList.get(smsIndex);
//	        Map<Integer, String> currentZoneStatuses = smsZoneStatusMap.get(sms.getUniqueId());
//	        
//	        for (int zoneNum = 1; zoneNum <= 40; zoneNum++) {
//	            String zoneStatus = currentZoneStatuses.get(zoneNum);
//	            if (!"A".equals(zoneStatus)) continue;
//
//	            String zoneKey = sms.getPanelSerialNo() + "_Zone" + zoneNum;
//	            String uniqueAlertKey = zoneKey + "_" + sms.getDateTime().getTime();
//	            
//	            if (processedAlerts.contains(uniqueAlertKey)) continue;
//	            if (lastOpenAlertPerZone.containsKey(zoneKey)) continue;
//
//	            // Look for resolution more efficiently
//	            SmsDetailsEntity resolvedSms = findResolution(smsDetailsList, smsZoneStatusMap, 
//	                                                        smsIndex, zoneNum);
//	            
//	            boolean isResolved = (resolvedSms != null);
//	            
//	            if (!isResolved) {
//	                lastOpenAlertPerZone.put(zoneKey, sms);
//	            }
//
//	            // Mark as processed
//	            processedAlerts.add(uniqueAlertKey);
//	            if (isResolved) {
//	                processedAlerts.add(zoneKey + "_" + resolvedSms.getDateTime().getTime());
//	            }
//
//	            // Create response entity
//	            InputStatusDetailsEntity inputEntity = createInputStatusEntity(
//	                device, branch, sms, resolvedSms, zoneNum, deviceZones, isResolved);
//	            
//	            inputResponseList.add(inputEntity);
//	            
//	            // Update alarm type count
//	            String zoneName = inputEntity.getAlarmtype();
//	            alarmTypeCountMap.put(zoneName, alarmTypeCountMap.getOrDefault(zoneName, 0) + 1);
//	        }
//	    }
//	}
//
//	private SmsDetailsEntity findResolution(List<SmsDetailsEntity> smsDetailsList,
//	                                      Map<String, Map<Integer, String>> smsZoneStatusMap,
//	                                      int currentIndex, int zoneNum) {
//	    Date currentDateTime = smsDetailsList.get(currentIndex).getDateTime();
//	    
//	    // Look forward from current position for efficiency
//	    for (int i = currentIndex + 1; i < smsDetailsList.size(); i++) {
//	        SmsDetailsEntity nextSms = smsDetailsList.get(i);
//	        if (!nextSms.getDateTime().after(currentDateTime)) continue;
//	        
//	        Map<Integer, String> nextZoneStatuses = smsZoneStatusMap.get(nextSms.getUniqueId());
//	        if ("N".equals(nextZoneStatuses.get(zoneNum))) {
//	            return nextSms;
//	        }
//	    }
//	    return null;
//	}
//
//	private InputStatusDetailsEntity createInputStatusEntity(DeviceMasterEntity device,
//	                                                       BranchMasterEntity branch,
//	                                                       SmsDetailsEntity sms,
//	                                                       SmsDetailsEntity resolvedSms,
//	                                                       int zoneNum,
//	                                                       Map<String, DeviceZoneDetailsEntity> deviceZones,
//	                                                       boolean isResolved) {
//	    
//	    InputStatusDetailsEntity entity = new InputStatusDetailsEntity();
//	    
//	    // Set basic info
//	    entity.setDeviceSerialNo(sms.getPanelSerialNo());
//	    entity.setDeviceName(device.getDeviceName());
//	    entity.setBankName(branch.getBank());
//	    entity.setBranchName(branch.getBranchDesc());
//	    entity.setMobileNo(branch.getMobile());
//	    entity.setVendorName(device.getVendorName());
//	    entity.setIfsc(branch.getIfsc());
//	    
//	    // Set zone info
//	    String zoneOriginalName = "ZONE" + zoneNum;
//	    DeviceZoneDetailsEntity zoneEntity = deviceZones != null ? deviceZones.get(zoneOriginalName) : null;
//	    String zoneName = (zoneEntity != null) ? zoneEntity.getZoneDesiredName() : zoneOriginalName;
//	    entity.setAlarmtype(zoneName);
//	    if (zoneEntity != null) {
//	        entity.setAlertType(zoneEntity.getAlertType());
//	    }
//	    
//	    // Set time info
//	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
//	    ZoneId zoneId = ZoneId.systemDefault();
//	    
//	    LocalDateTime alarmTime = sms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();
//	    entity.setAlarmTime(alarmTime.format(formatter));
//	    
//	    if (isResolved && resolvedSms != null) {
//	        LocalDateTime resolvedTime = resolvedSms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();
//	        entity.setIssueResolvedTime(resolvedTime.format(formatter));
//	        entity.setAlarmDescription("Resolved");
//	        
//	        // Calculate duration
//	        Duration duration = Duration.between(alarmTime, resolvedTime);
//	        entity.setTimeDifference(formatDuration(duration));
//	    } else {
//	        entity.setAlarmDescription("Open");
//	        entity.setTimeDifference("Ongoing");
//	    }
//	    
//	    return entity;
//	}
//
//	private String formatDuration(Duration duration) {
//	    long days = duration.toDays();
//	    long hours = duration.minusDays(days).toHours();
//	    long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
//	    
//	    if (days > 0) {
//	        return String.format("%d days, %d hours, and %d minutes", days, hours, minutes);
//	    } else if (hours > 0) {
//	        return String.format("%d hours and %d minutes", hours, minutes);
//	    } else {
//	        return String.format("%d minutes", minutes);
//	    }
//	}
//
//	private ResponseEntity createEmptyResponse() {
//	    ResponseEntity response = new ResponseEntity();
//	    Map<String, Object> emptyResponse = new HashMap<>();
//	    emptyResponse.put("inputStatusDetails", new ArrayList<>());
//	    emptyResponse.put("alarmTypeCounts", new HashMap<>());
//	    response.setPayload(emptyResponse);
//	    response.setMessage("No data found");
//	    response.setStatusCode(HttpStatus.OK.value());
//	    return response;
//	}

	public ResponseEntity getAllAlertTypes(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {

			List<AllAlertsDTO> responseList = new ArrayList<>();
			ResponseEntity branchResponseList = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseList.getPayload();
			if (!branchList.isEmpty()) {
				branchList.forEach(branch -> {
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(branch.getBranchCode());
					if (deviceMasterEntity != null) {
						DeviceMasterDTO responseDto = new DeviceMasterDTO();
						List<DeviceZoneDetailsEntity> deviceZoneDetailsEntity = deviceZoneDetailsRepository
								.findByDeviceId(deviceMasterEntity.getDeviceId());
						AllAlertsDTO alertResponse = new AllAlertsDTO();
						alertResponse.setBranchCode(branch.getBranchCode());
						alertResponse.setBankName(branch.getBank());
						BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
								? branchMasterRepository.findBybranchCode(branch.getParentCode())
								: null;

						if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
							alertResponse
									.setBranchName(branch.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
						} else {
							alertResponse.setBranchName(branch.getBranchDesc());
						}

//						alertResponse.setBranchName(branch.getBranchDesc());
						alertResponse.setBranchType(branch.getBranchType());
						if (!deviceZoneDetailsEntity.isEmpty()) {
							List<String> alertsList = new ArrayList<>();
							deviceZoneDetailsEntity.forEach(x -> {
								alertsList.add(x.getAlertType() + ":" + x.getCriticalAlertFlag());
							});
							alertResponse.setAlertTypes(alertsList);
							responseList.add(alertResponse);

						}
					}
				});
			}

			response.setPayload(responseList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred!!" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAlertFlag(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);

			if (device != null) {
				List<DynamicConfigurationEntity> entity = dynamicConfigurationRepository
						.findByDeviceId(device.getDeviceId());
				response.setPayload(entity);
				response.setMessage("Alert flag fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Device not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			}
		} catch (Exception e) {
			response.setMessage("Device fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		}
		return response;
	}

	public ResponseEntity getAllPanelSerialNumbersByBranchCode(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<DeviceMasterDTO> responseList = new ArrayList<>();
			List<String> list = new ArrayList<>();

			// List<String> requestList =
			// branchMasterRepository.findAllByBranchCode(branchCode);
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> requestList = (List<BranchMasterEntity>) responseEntity.getPayload();

			for (BranchMasterEntity x : requestList) {

				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(x.getBranchCode());
				if (device != null) {
					list.add(device.getDeviceSerialNo());

				} else {
					response.setMessage("No data present against the given branch code");
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				}
			}
			response.setPayload(list);
			response.setMessage("All devices by branch code fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getAllNgDevicesByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		try {
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);

			if (responseEntity == null || responseEntity.getPayload() == null) {
				response.setMessage("No branches found with this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			// Using Stream API to process branches and filter NG devices
			List<DeviceMasterEntity> responselList = ((List<BranchMasterEntity>) responseEntity.getPayload()).stream()
					.map(branch -> deviceMasterRepository.findBybranchCode(branch.getBranchCode()))
					.filter(device -> device != null && "NG".equals(device.getProductType()))
					.collect(Collectors.toList());

			response.setPayload(responselList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("All Guard NG devices fetched successfully");
		} catch (Exception e) {
			// Handle exception
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("An error occurred while fetching devices: " + e.getMessage());
		}

		return response;
	}

	public ResponseEntity getAllVendorNames() {
		ResponseEntity response = new ResponseEntity();

		List<String> vendorNameList = deviceMasterRepository.getAllVendorNames();

		if (!vendorNameList.isEmpty()) {
			response.setPayload(vendorNameList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		}
		return response;

	}

	public ResponseEntity checkDevice(String serialNo) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(serialNo);
			if (device == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Device not found");
			} else if (device != null && "active".equalsIgnoreCase(device.getStatus())) {
				response.setMessage("Device already exists for this branch code");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error fetching device");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllDeviceZoneNameByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
			if (device == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Device not found");
			} else {
				ResponseEntity responseEntity = getDeviceAndZoneDetailsByDeviceId(device.getDeviceId());
				response.setPayload(responseEntity.getPayload());
				response.setMessage("Device already exists for this branch code");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error fetching device");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity saveDeviceMaster(DeviceMasterDTO deviceMasterDTO, HttpSession httpSession) {
//		ResponseEntity response = new ResponseEntity();
//		String prefix = "+91";
//		if (deviceMasterDTO == null) {
//			response.setMessage("Data not present in the device master request");
//			response.setStatusCode(HttpStatus.NO_CONTENT.value());
//			return response;
//		} else {
//			if ("SIA".equalsIgnoreCase(deviceMasterDTO.getDeviceType())) {
//
//				DeviceMasterEntity deviceEntity = new DeviceMasterEntity();
//				deviceEntity.setDeviceId(deviceMasterDTO.getDeviceId());
//				deviceEntity.setDeviceName(deviceMasterDTO.getDeviceName());
//				deviceEntity.setDeviceType(deviceMasterDTO.getDeviceType());
//				deviceEntity.setDeviceLocation(deviceMasterDTO.getDeviceLocation());
//				deviceEntity.setPhoneNumber(prefix + deviceMasterDTO.getPhoneNumber());
//				deviceEntity.setProtectionType(deviceMasterDTO.getProtectionType());
//				deviceEntity.setUserNo(deviceMasterDTO.getUserNo());
//				deviceEntity.setUserPassword(deviceMasterDTO.getUserPassword());
//				deviceEntity.setEmail1(deviceMasterDTO.getEmail1());
//				deviceEntity.setProductType(deviceMasterDTO.getProductType());
//				deviceEntity.setPasscode(deviceMasterDTO.getPasscode());
//				deviceEntity.setAddress(deviceMasterDTO.getAddress());
//				deviceEntity.setSupportNo(deviceMasterDTO.getSupportNo());
//				deviceEntity.setBranchCode(deviceMasterDTO.getBranchCode());
//				deviceEntity.setVendorName(deviceMasterDTO.getVendorName());
//				deviceEntity.setVendorEmail(deviceMasterDTO.getVendorEmail());
//				deviceEntity.setVendorMobile(deviceMasterDTO.getVendorMobile());
//				deviceEntity.setVoltage1(deviceMasterDTO.getVoltage1());
//				deviceEntity.setVoltage2(deviceMasterDTO.getVoltage2());
//				deviceEntity.setVoltage3(deviceMasterDTO.getVoltage3());
//				deviceEntity.setCreatedBy(httpSession.getAttribute("empId").toString());
//				deviceEntity.setCreatedDate(new Date().toString());
//				deviceEntity.setUpdationStatus("SAVE");
//				deviceEntity.setStatus("Active");
//				deviceMasterRepository.save(deviceEntity);
//				List<SiaProtocalEntity> protocalEntities = new ArrayList<>();
//				for (SiaProtocalEntity siaProtocal : deviceMasterDTO.getSiaProtocol()) {
////			 	SiaProtocalEntity siaProtocalEntity=new SiaProtocalEntity();
////			 	siaProtocalEntity.setCommand(siaProtocal.getCommand());
//					siaProtocal.setDeviceId(deviceMasterDTO.getDeviceId());
////			 	siaProtocalEntity.setMessage(siaProtocal.getMessage());
////			 	siaProtocalEntity.setMessageType(siaProtocal.getMessageType());
//					protocalEntities.add(siaProtocal);
//				}
//				protocalRepository.saveAll(protocalEntities);
//				response.setPayload(deviceMasterDTO);
//				response.setMessage("New SIA Device Added Successfully..");
//				response.setStatusCode(HttpStatus.OK.value());
//			} else {
//				ResponseEntity responseEntity = addDeviceMaster(deviceMasterDTO, httpSession);
//
//				response.setPayload(responseEntity.getPayload());
//				response.setMessage(responseEntity.getMessage());
//				response.setStatusCode(responseEntity.getStatusCode());
//			}
//			return response;
//
//		}
//	}

	public ResponseEntity saveDeviceMaster(DeviceMasterDTO deviceMasterDTO) {
		ResponseEntity response = new ResponseEntity();
		String prefix = "+91";
		if (deviceMasterDTO == null) {
			response.setMessage("Data not present in the device master request");
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return response;
		} else {
			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
					.findBybranchCode(deviceMasterDTO.getBranchCode());
			if (deviceMasterEntity != null) {
				response.setMessage("Device Already mapped with this branch. Please change ");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				return response;
			} else {
				if ("SIA".equalsIgnoreCase(deviceMasterDTO.getDeviceType())) {

					DeviceMasterEntity deviceEntity = new DeviceMasterEntity();
					deviceEntity.setDeviceId(deviceMasterDTO.getDeviceId());
					deviceEntity.setDeviceName(deviceMasterDTO.getDeviceName());
					deviceEntity.setDeviceType(deviceMasterDTO.getDeviceType());
					deviceEntity.setDeviceLocation(deviceMasterDTO.getDeviceLocation());
					deviceEntity.setPhoneNumber(prefix + deviceMasterDTO.getPhoneNumber());
					deviceEntity.setProtectionType(deviceMasterDTO.getProtectionType());
					deviceEntity.setUserNo(deviceMasterDTO.getUserNo());
					deviceEntity.setUserPassword(deviceMasterDTO.getUserPassword());
					deviceEntity.setEmail1(deviceMasterDTO.getEmail1());
					deviceEntity.setProductType(deviceMasterDTO.getProductType());
					deviceEntity.setPasscode(deviceMasterDTO.getPasscode());
					deviceEntity.setAddress(deviceMasterDTO.getAddress());
					deviceEntity.setSupportNo(deviceMasterDTO.getSupportNo());
					deviceEntity.setBranchCode(deviceMasterDTO.getBranchCode());
					deviceEntity.setVendorName(deviceMasterDTO.getVendorName());
					deviceEntity.setVendorEmail(deviceMasterDTO.getVendorEmail());
					deviceEntity.setVendorMobile(deviceMasterDTO.getVendorMobile());
					deviceEntity.setVoltage1(deviceMasterDTO.getVoltage1());
					deviceEntity.setVoltage2(deviceMasterDTO.getVoltage2());
					deviceEntity.setVoltage3(deviceMasterDTO.getVoltage3());
//					deviceEntity.setCreatedBy(deviceMasterDTO.ge);
					deviceEntity.setCreatedDate(new Date().toString());
					deviceEntity.setUpdationStatus("SAVE");
					deviceEntity.setStatus("Active");
					deviceMasterRepository.save(deviceEntity);
					List<SiaProtocalEntity> protocalEntities = new ArrayList<>();
					for (SiaProtocalEntity siaProtocal : deviceMasterDTO.getSiaProtocol()) {
//			 	SiaProtocalEntity siaProtocalEntity=new SiaProtocalEntity();
//			 	siaProtocalEntity.setCommand(siaProtocal.getCommand());
						siaProtocal.setDeviceId(deviceMasterDTO.getDeviceId());
//			 	siaProtocalEntity.setMessage(siaProtocal.getMessage());
//			 	siaProtocalEntity.setMessageType(siaProtocal.getMessageType());
						protocalEntities.add(siaProtocal);
					}
					protocalRepository.saveAll(protocalEntities);
					response.setPayload(deviceMasterDTO);
					response.setMessage("New SIA Device Added Successfully..");
					response.setStatusCode(HttpStatus.OK.value());
				} else {
					ResponseEntity responseEntity = addDeviceMaster(deviceMasterDTO);

					response.setPayload(responseEntity.getPayload());
					response.setMessage(responseEntity.getMessage());
					response.setStatusCode(responseEntity.getStatusCode());
				}
			}
			return response;

		}
	}

	public ResponseEntity UpdateSiaProtocols(List<SiaProtocalEntity> siaList) {

		ResponseEntity response = new ResponseEntity();
		for (SiaProtocalEntity entity : siaList) {
			protocalRepository.save(entity); // save() will update if ID exists
		}
		response.setPayload(siaList);
		response.setMessage("Data edited succesfully");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	public ResponseEntity getAllDeviceSiaCommand(String command, String deviceId) {
		ResponseEntity response = new ResponseEntity();
		SiaProtocalEntity siaProtocalEntity = siaProtocalRepository.findByCommandAndDeviceId(command, deviceId);

		if (siaProtocalEntity == null) {
			response.setMessage("Sia data not present by command.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response; // return early if not found
		}

		SiaProtocalDTO siaProtocalDTO = new SiaProtocalDTO();
		BeanUtils.copyProperties(siaProtocalEntity, siaProtocalDTO);
		response.setPayload(siaProtocalDTO);
		response.setMessage("Sia data fetched by command.");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	public ResponseEntity getDeviceCountOfSiaByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();

			List<String> branchCodes = getBranchCodes(branches);

			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAll();

			List<DeviceMasterEntity> filteredDevices = allDevices.stream()
					.filter(device -> branchCodes.contains(device.getBranchCode())).collect(Collectors.toList());

			long totalDeviceCount = filteredDevices.size();
			long siaDeviceCount = filteredDevices.stream()
					.filter(device -> "SIA".equalsIgnoreCase(device.getDeviceType())).count();

			if (filteredDevices.isEmpty()) {
				response.setMessage("No devices found for branch code: " + branchCode + " and its children");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				Map<String, Object> payload = new HashMap<>();
				payload.put("branchCode", branchCode);
				payload.put("totalDeviceCount", totalDeviceCount);
				payload.put("siaDeviceCount", siaDeviceCount);

				response.setPayload(payload);
				response.setMessage("Device counts fetched successfully for branch and children");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching device count by branch code");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllDeviceOfSia(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<siaDetailsDTO> siaDetailsList = new ArrayList<>();

			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			for (BranchMasterEntity branch : branchList) {
				List<DeviceMasterEntity> devices = deviceMasterRepository.findByBranchCode(branch.getBranchCode());

				for (DeviceMasterEntity device : devices) {
					if ("SIA".equalsIgnoreCase(device.getDeviceType())) {
						siaDetailsDTO dto = new siaDetailsDTO();
						dto.setBranchName(branch.getBranchDesc());
						dto.setBranchAddress(branch.getAddress1() + branch.getAddress2() + branch.getAddress3()
								+ branch.getAddress4());
						dto.setBranchIfsc(branch.getIfsc());
						dto.setBranchNumber(branch.getMobile());
						dto.setDevicePanelSerialNo(device.getDeviceSerialNo());
						dto.setDeviceType(device.getDeviceType());
						dto.setBranchEmail(branch.getEmail());
						siaDetailsList.add(dto);
					}
				}
			}

			if (siaDetailsList.isEmpty()) {
				response.setMessage("No SIA devices found for the given branch and its children.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(siaDetailsList);
				response.setMessage("All SIA device details fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching SIA device details");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getDeviceForSiaByPanelSerialNo(String deviceSerialNo) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(deviceSerialNo);
			if (device != null) {
				response.setPayload(device);
				response.setMessage("Device fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Device not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			response.setMessage("Error fetching device");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public List<String> getBranchCodes(List<BranchMasterEntity> branches) {
		// Check if branches is null before streaming
		return branches != null
				? branches.stream().filter(branch -> branch != null && branch.getBranchCode() != null)
						.map(BranchMasterEntity::getBranchCode).collect(Collectors.toList())
				: List.of(); // Return an empty list if branches is null
	}

	public ResponseEntity getInputStatusDetailsByAlarmDateRange(String branchCode, Date startDate, Date endDate) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<InputStatusDetailsEntity> inputResponseList = new ArrayList<>();
			Map<String, Integer> alarmTypeCountMap = new HashMap<>();

			ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
			branchCodeList = branchCodeList == null ? new ArrayList<>() : branchCodeList;

			for (BranchMasterEntity branchCodeEntity : branchCodeList) {
				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
						.findBybranchCode(branchCodeEntity.getBranchCode());

				if (deviceMasterEntity != null) {
					List<SmsDetailsEntity> smsDetailsEntity;
					List<SmsDetailsEntity> smsDetailsResponse;

					if (startDate != null && endDate != null) {
						// Setting startDate to 00:00:00 and endDate to 23:59:59
						startDate = setToStartOfDay(startDate);
						endDate = setToEndOfDay(endDate);

						smsDetailsEntity = smsDetailsRepository.findByPanelSerialNoAndDateTimeBetween(
								deviceMasterEntity.getDeviceSerialNo(), startDate, endDate);

						smsDetailsResponse = new ArrayList<>(smsDetailsEntity);
						if (!smsDetailsResponse.isEmpty())
							smsDetailsResponse.remove(0);
					} else {
						smsDetailsEntity = smsDetailsRepository
								.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo());
						smsDetailsResponse = smsDetailsRepository
								.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo());
						if (!smsDetailsResponse.isEmpty())
							smsDetailsResponse.remove(0);
					}

					// Your existing processing code for SmsDetailsEntity
					for (SmsDetailsEntity sms : smsDetailsEntity) {
						for (int i = 1; i <= 40; i++) {
							try {
								Method method = sms.getClass().getMethod("getZone" + i);
								String zone = (String) method.invoke(sms);
								if ("A".equals(zone)) {
									for (SmsDetailsEntity smsDetails : smsDetailsResponse) {
										method = smsDetails.getClass().getMethod("getZone" + i);
										String zoneValue = (String) method.invoke(smsDetails);
										if ("N".equals(zoneValue)) {
											InputStatusDetailsEntity inputStatusDetailsEntity = new InputStatusDetailsEntity();

											BranchMasterEntity branch = branchCodeEntity;
											String branchName = branch.getBranchDesc();
											String bankName = branch.getBank();
											String mobile = branch.getMobile();

											String zoneOriginalNameString = "Zone" + i;
											String zoneName = "";

											DeviceZoneDetailsEntity deviceZoneEntity = deviceZoneDetailsRepository
													.findByDeviceIdAndZoneName(deviceMasterEntity.getDeviceId(),
															zoneOriginalNameString);
											if (deviceZoneEntity != null) {
												zoneName = deviceZoneEntity.getZoneDesiredName();
												inputStatusDetailsEntity.setAlertType(deviceZoneEntity.getAlertType());
											}

											inputStatusDetailsEntity.setDeviceSerialNo(sms.getPanelSerialNo());
											inputStatusDetailsEntity.setAlarmtype(zoneName);
											inputStatusDetailsEntity.setAlarmTime(sms.getDateTime().toString());
											inputStatusDetailsEntity
													.setIssueResolvedTime(smsDetails.getDateTime().toString());
											inputStatusDetailsEntity.setDeviceName(deviceMasterEntity.getDeviceName());
											inputStatusDetailsEntity.setBankName(bankName);
											inputStatusDetailsEntity.setBranchName(branchName);
											inputStatusDetailsEntity.setMobileNo(mobile);
											inputStatusDetailsEntity.setVendorName(deviceMasterEntity.getVendorName());

											// Time difference
											long diffMillis = smsDetails.getDateTime().getTime()
													- sms.getDateTime().getTime();
											long minutes = diffMillis / (1000 * 60);
											long hours = minutes / 60;
											long days = hours / 24;
											hours %= 24;
											minutes %= 60;

											String humanReadableTimeDifference = (days > 0)
													? String.format("%d days, %d hours, and %d minutes", days, hours,
															minutes)
													: (hours > 0)
															? String.format("%d hours and %d minutes", hours, minutes)
															: String.format("%d minutes", minutes);

											inputStatusDetailsEntity.setTimeDifference(humanReadableTimeDifference);

											alarmTypeCountMap.put(zoneName,
													alarmTypeCountMap.getOrDefault(zoneName, 0) + 1);
											inputResponseList.add(inputStatusDetailsEntity);
										}
										break;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			Map<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("inputStatusDetails", inputResponseList);
			finalResponse.put("alarmTypeCounts", alarmTypeCountMap);

			response.setPayload(finalResponse);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred while fetching");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	private Date setToStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Date setToEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public org.springframework.http.ResponseEntity<?> getVendorResolutionStats(String branchCode) {
		try {
			ResponseEntity inputStatusResponse = getInputStatusDetails(branchCode);
			Map<String, Object> responsePayload = (Map<String, Object>) inputStatusResponse.getPayload();
			List<InputStatusDetailsEntity> inputList = (List<InputStatusDetailsEntity>) responsePayload
					.get("inputStatusDetails");

			Map<String, List<InputStatusDetailsEntity>> vendorMap = new HashMap<>();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(180);

			for (InputStatusDetailsEntity input : inputList) {
				try {
					LocalDateTime alarmTime = LocalDateTime.parse(input.getAlarmTime(), formatter);

					if (alarmTime.isBefore(oneMonthAgo)) {
						continue;
					}

					vendorMap.computeIfAbsent(input.getVendorName(), k -> new ArrayList<>()).add(input);
				} catch (Exception e) {
					continue;
				}
			}

			List<Map<String, Object>> vendorStatsList = new ArrayList<>();

			for (Map.Entry<String, List<InputStatusDetailsEntity>> entry : vendorMap.entrySet()) {
				String vendor = entry.getKey();
				List<InputStatusDetailsEntity> alerts = entry.getValue();

				long resolvedWithin3Days = alerts.stream().filter(alert -> {
					try {
						LocalDateTime alarmTime = LocalDateTime.parse(alert.getAlarmTime(), formatter);
						LocalDateTime resolveTime = LocalDateTime.parse(alert.getIssueResolvedTime(), formatter);
						long days = Duration.between(alarmTime, resolveTime).toDays();
						return days <= 3;
					} catch (Exception e) {
						return false;
					}
				}).count();

				Map<String, Object> vendorStat = new HashMap<>();
				vendorStat.put("vendorName", vendor);
				vendorStat.put("totalAlerts", alerts.size());
				vendorStat.put("resolvedWithin30Days", resolvedWithin3Days);
				vendorStat.put("resolutionRate", (alerts.size() == 0) ? "N/A"
						: String.format("%.0f%%", (resolvedWithin3Days * 100.0 / alerts.size())));

				vendorStatsList.add(vendorStat);
			}

			Map<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("vendorResolutionStats", vendorStatsList);

			return new org.springframework.http.ResponseEntity<>(finalResponse, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new org.springframework.http.ResponseEntity<>("Error calculating vendor resolution stats",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity getResponseTimeReport(String branchCode, String systemIntegrator, String product,
			String status, String fromDate, String toDate) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<InputStatusDetailsEntity> inputResponseList = new ArrayList<>();
			Map<String, Integer> alarmTypeCountMap = new HashMap<>();
			Set<String> processedAlerts = new HashSet<>();
			Map<String, SmsDetailsEntity> lastOpenAlertPerZone = new HashMap<>();

			// Get all branch codes at once
			ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);

			List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
			if (branchCodeList == null || branchCodeList.isEmpty()) {
				return createEmptyResponse(response);
			}

			// Extract branch codes for batch queries
			List<String> branchCodes = branchCodeList.stream().map(BranchMasterEntity::getBranchCode)
					.collect(Collectors.toList());

			// Batch fetch all devices for all branches at once
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);
			if (allDevices.isEmpty()) {
				return createEmptyResponse(response);
			}

			if (allDevices.isEmpty()) {
				return createEmptyResponse(response);
			}

			// Create maps for quick lookups
			Map<String, DeviceMasterEntity> branchToDeviceMap = allDevices.stream()
					.collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, Function.identity()));

			Map<String, BranchMasterEntity> branchCodeMap = branchCodeList.stream()
					.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

			// Get all device serial numbers for batch SMS query
			List<String> deviceSerialNos = allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.collect(Collectors.toList());

			// ✅ NEW: Fetch SMS data with history table support if date range > 2 days
			List<SmsDetailsEntity> allSmsDetails;
			if (fromDate != null && toDate != null && !fromDate.trim().isEmpty() && !toDate.trim().isEmpty()) {
				// Parse dates and add time boundaries
				LocalDate startDate = LocalDate.parse(fromDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				LocalDate endDate = LocalDate.parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				Date fromDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date toDateTime = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

				// ✅ Check if date range is more than 2 days
				long daysDifference = ChronoUnit.DAYS.between(startDate, endDate);

				if (daysDifference > 2) {
					// Fetch from both tables and combine
					allSmsDetails = fetchSmsDataWithHistory(deviceSerialNos, fromDateTime, toDateTime);
				} else {
					// Only fetch from sms_details table
					allSmsDetails = smsDetailsRepository
							.findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos,
									fromDateTime, toDateTime);
				}
			} else {
				allSmsDetails = smsDetailsRepository
						.findByPanelSerialNoInOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos);
			}

			if (allSmsDetails.isEmpty()) {
				return createEmptyResponse(response);
			}

			// Group SMS details by panel serial number for efficient processing
			Map<String, List<SmsDetailsEntity>> smsGroupedByPanel = allSmsDetails.stream()
					.collect(Collectors.groupingBy(SmsDetailsEntity::getPanelSerialNo));

			// Batch fetch all device zone details
			List<String> deviceIds = allDevices.stream().map(DeviceMasterEntity::getDeviceId)
					.collect(Collectors.toList());

			List<DeviceZoneDetailsEntity> allZoneDetails = deviceZoneDetailsRepository.findByDeviceIdIn(deviceIds);

			// ✅ Filter zones by system integrator (vendor) if provided
			if (systemIntegrator != null && !systemIntegrator.trim().isEmpty()
					&& !"all".equalsIgnoreCase(systemIntegrator)) {
				String vendorFilter = systemIntegrator.trim().toLowerCase();
				allZoneDetails = allZoneDetails.stream()
						.filter(zone -> zone.getVendors() != null
								&& zone.getVendors().trim().toLowerCase().equals(vendorFilter))
						.collect(Collectors.toList());
			}

			// Create zone lookup map: deviceId_zoneName -> DeviceZoneDetailsEntity
			Map<Object, DeviceZoneDetailsEntity> zoneDetailsMap = allZoneDetails.stream().collect(Collectors
					.toMap(zone -> zone.getDeviceId() + "_" + zone.getZoneOriginalName(), Function.identity()));

			// ✅ Keep only devices that have at least one zone left after filtering
			Set<String> validDeviceIds = allZoneDetails.stream().map(DeviceZoneDetailsEntity::getDeviceId)
					.collect(Collectors.toSet());

			allDevices = allDevices.stream().filter(device -> validDeviceIds.contains(device.getDeviceId()))
					.collect(Collectors.toList());

			if (allDevices.isEmpty()) {
				return createEmptyResponse(response);
			}

			// Pre-compile zone method names to avoid repeated reflection
			Map<Integer, Method> zoneMethods = new HashMap<>();
			try {
				for (int i = 1; i <= 40; i++) {
					Method method = SmsDetailsEntity.class.getMethod("getZone" + i);
					zoneMethods.put(i, method);
				}
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Zone methods not found", e);
			}

			// Process each device's SMS data
			for (DeviceMasterEntity device : allDevices) {
				List<SmsDetailsEntity> deviceSmsData = smsGroupedByPanel.get(device.getDeviceSerialNo());
				if (deviceSmsData == null || deviceSmsData.isEmpty())
					continue;

				BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
				if (branch == null)
					continue;
//	            System.out.println("DEVICE SMS DATA :: "+deviceSmsData);
//	            System.out.println("DEVICE  :: "+device);
//	            System.out.println("BRANCH DATA :: "+branch);
//	            System.out.println("ZONE DETAILS MAP DATA :: "+zoneDetailsMap);
//	            System.out.println("ZONE METHODS DATA :: "+zoneMethods);
//	            System.out.println("INPUT RESPONSE DATA :: "+inputResponseList);
//	            System.out.println("ALARM TYPE COUNT MAP DATA :: "+alarmTypeCountMap);
//	            System.out.println("PROCESSED ALERTS DATA :: "+processedAlerts);
//	            System.out.println("LAST OPEN ALERT PER ZONE DATA :: "+lastOpenAlertPerZone);

				processDeviceAlerts(deviceSmsData, device, branch, zoneDetailsMap, zoneMethods, inputResponseList,
						alarmTypeCountMap, processedAlerts, lastOpenAlertPerZone);
			}

			// Apply product and status filters after processing
			List<InputStatusDetailsEntity> filteredResponseList = applyFilters(inputResponseList, product, status);

			filteredResponseList.sort((a, b) -> {
				String alarmTimeA = a.getAlarmTime();
				String alarmTimeB = b.getAlarmTime();

				if (alarmTimeA == null && alarmTimeB == null)
					return 0;
				if (alarmTimeA == null)
					return 1;
				if (alarmTimeB == null)
					return -1;

				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
					LocalDateTime dateTimeA = LocalDateTime.parse(alarmTimeA.trim(), formatter);
					LocalDateTime dateTimeB = LocalDateTime.parse(alarmTimeB.trim(), formatter);
					return dateTimeB.compareTo(dateTimeA);
				} catch (Exception e) {
					try {
						String cleanA = alarmTimeA.replace(",", "").trim();
						String cleanB = alarmTimeB.replace(",", "").trim();
						DateTimeFormatter altFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
						LocalDateTime dateTimeA = LocalDateTime.parse(cleanA, altFormatter);
						LocalDateTime dateTimeB = LocalDateTime.parse(cleanB, altFormatter);
						return dateTimeB.compareTo(dateTimeA);
					} catch (Exception e2) {
						return alarmTimeB.compareTo(alarmTimeA);
					}
				}
			});

			// Recalculate alarm type counts for filtered data
			Map<String, Integer> filteredAlarmTypeCountMap = new HashMap<>();
			for (InputStatusDetailsEntity entity : filteredResponseList) {
				String alarmType = entity.getAlarmtype() != null ? entity.getAlarmtype() : "";
				filteredAlarmTypeCountMap.put(alarmType, filteredAlarmTypeCountMap.getOrDefault(alarmType, 0) + 1);
			}

			// Create final response
			Map<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("inputStatusDetails", filteredResponseList);
			finalResponse.put("alarmTypeCounts", filteredAlarmTypeCountMap);

			response.setPayload(finalResponse);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error occurred while fetching");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	// ✅ NEW METHOD: Fetch SMS data with history table support (batch processing)
	private List<SmsDetailsEntity> fetchSmsDataWithHistory(List<String> deviceSerialNos, Date fromDateTime,
			Date toDateTime) {
		List<SmsDetailsEntity> allSmsDetails = new ArrayList<>();

		// Fetch from main sms_details table
		List<SmsDetailsEntity> mainTableData = smsDetailsRepository
				.findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos,
						fromDateTime, toDateTime);

		allSmsDetails.addAll(mainTableData);

		// Fetch from sms_details_history table in batches
		int batchSize = 500; // Process 500 serial numbers at a time
		for (int i = 0; i < deviceSerialNos.size(); i += batchSize) {
			int endIndex = Math.min(i + batchSize, deviceSerialNos.size());
			List<String> batch = deviceSerialNos.subList(i, endIndex);

			List<SmsDetailsHistoryEntity> historyData = smsDetailsHistoryRepository
					.findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAscOrder(batch, fromDateTime,
							toDateTime);

			// Convert history entities to main entities
			for (SmsDetailsHistoryEntity historyEntity : historyData) {
				SmsDetailsEntity convertedEntity = convertHistoryToMain2(historyEntity);
				allSmsDetails.add(convertedEntity);
			}
		}

		// Sort combined data by panel serial no and date time
		allSmsDetails.sort(
				Comparator.comparing(SmsDetailsEntity::getPanelSerialNo).thenComparing(SmsDetailsEntity::getDateTime));

		return allSmsDetails;
	}

	// ✅ Helper method to convert history entity to main entity
	private SmsDetailsEntity convertHistoryToMain2(SmsDetailsHistoryEntity historyEntity) {
		SmsDetailsEntity entity = new SmsDetailsEntity();
		entity.setPanelSerialNo(historyEntity.getPanelSerialNo());
		entity.setDateTime(historyEntity.getDateTime());
		entity.setArmed(historyEntity.getArmed());

		// Copy all zone fields (Zone1 to Zone40)
		entity.setZone1(historyEntity.getZone1());
		entity.setZone2(historyEntity.getZone2());
		entity.setZone3(historyEntity.getZone3());
		entity.setZone4(historyEntity.getZone4());
		entity.setZone5(historyEntity.getZone5());
		entity.setZone6(historyEntity.getZone6());
		entity.setZone7(historyEntity.getZone7());
		entity.setZone8(historyEntity.getZone8());
		entity.setZone9(historyEntity.getZone9());
		entity.setZone10(historyEntity.getZone10());
		entity.setZone11(historyEntity.getZone11());
		entity.setZone12(historyEntity.getZone12());
		entity.setZone13(historyEntity.getZone13());
		entity.setZone14(historyEntity.getZone14());
		entity.setZone15(historyEntity.getZone15());
		entity.setZone16(historyEntity.getZone16());
		entity.setZone17(historyEntity.getZone17());
		entity.setZone18(historyEntity.getZone18());
		entity.setZone19(historyEntity.getZone19());
		entity.setZone20(historyEntity.getZone20());
		entity.setZone21(historyEntity.getZone21());
		entity.setZone22(historyEntity.getZone22());
		entity.setZone23(historyEntity.getZone23());
		entity.setZone24(historyEntity.getZone24());
		entity.setZone25(historyEntity.getZone25());
		entity.setZone26(historyEntity.getZone26());
		entity.setZone27(historyEntity.getZone27());
		entity.setZone28(historyEntity.getZone28());
		entity.setZone29(historyEntity.getZone29());
		entity.setZone30(historyEntity.getZone30());
		entity.setZone31(historyEntity.getZone31());
		entity.setZone32(historyEntity.getZone32());
		entity.setZone33(historyEntity.getZone33());
		entity.setZone34(historyEntity.getZone34());
		entity.setZone35(historyEntity.getZone35());
		entity.setZone36(historyEntity.getZone36());
		entity.setZone37(historyEntity.getZone37());
		entity.setZone38(historyEntity.getZone38());
		entity.setZone39(historyEntity.getZone39());
		entity.setZone40(historyEntity.getZone40());

		return entity;
	}

	private List<InputStatusDetailsEntity> applyFilters(List<InputStatusDetailsEntity> inputList, String product,
			String status) {

		return inputList.stream().filter(entity -> {
			// Product filter (based on alarmtype)
			if (product != null && !product.trim().isEmpty() && !"all".equalsIgnoreCase(product)) {
				String entityAlarmType = entity.getAlarmtype() != null ? entity.getAlarmtype() : "";
				if (!entityAlarmType.equalsIgnoreCase(product.trim())) {
					return false;
				}
			}

			// Status filter (based on alarmDescription)
			if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
				String entityStatus = entity.getAlarmDescription() != null ? entity.getAlarmDescription() : "";
				if (!entityStatus.equalsIgnoreCase(status.trim())) {
					return false;
				}
			}

			return true;
		}).collect(Collectors.toList());
	}

	private void processDeviceAlerts(List<SmsDetailsEntity> smsDetailsEntityList, DeviceMasterEntity device,
			BranchMasterEntity branch, Map<Object, DeviceZoneDetailsEntity> zoneDetailsMap,
			Map<Integer, Method> zoneMethods, List<InputStatusDetailsEntity> inputResponseList,
			Map<String, Integer> alarmTypeCountMap, Set<String> processedAlerts,
			Map<String, SmsDetailsEntity> lastOpenAlertPerZone) {

		DateTimeFormatter formatterFirst = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
		ZoneId zoneId = ZoneId.systemDefault();

		for (SmsDetailsEntity sms : smsDetailsEntityList) {

			for (int zoneNum = 1; zoneNum <= 40; zoneNum++) {
				try {
					Method method = zoneMethods.get(zoneNum);
					String zoneValue = (String) method.invoke(sms);

					// ❌ Ignore everything except A or R
					if (!"A".equals(zoneValue) && !"R".equals(zoneValue)) {
						continue;
					}

					String zoneKey = sms.getPanelSerialNo() + "_Zone" + zoneNum;

					// 🔁 R means alert already open → don't create new alert
					if ("R".equals(zoneValue) && lastOpenAlertPerZone.containsKey(zoneKey)) {
						continue;
					}

					// 🔍 Determine opening SMS
					SmsDetailsEntity openingSms = sms;

					if ("R".equals(zoneValue)) {
						SmsDetailsEntity previousA = findPreviousOpenAlert(smsDetailsEntityList, sms, zoneNum,
								zoneMethods);

						if (previousA == null) {
							continue; // R without previous A → invalid case
						}
						openingSms = previousA;
					}

					String uniqueAlertKey = openingSms.getPanelSerialNo() + "_Zone" + zoneNum + "_"
							+ openingSms.getDateTime().toString();

					if (processedAlerts.contains(uniqueAlertKey)) {
						continue;
					}

					// 🔍 Find resolution
					SmsDetailsEntity resolvedSms = findResolution(smsDetailsEntityList, sms, zoneNum, zoneMethods);

					boolean isResolved = resolvedSms != null;

					if (!isResolved) {
						lastOpenAlertPerZone.put(zoneKey, openingSms);
					}

					processedAlerts.add(uniqueAlertKey);

					if (isResolved) {
						processedAlerts.add(resolvedSms.getPanelSerialNo() + "_Zone" + zoneNum + "_"
								+ resolvedSms.getDateTime().toString());
					}

					// 📍 Zone details
					String zoneOriginalName = "ZONE" + zoneNum;
					DeviceZoneDetailsEntity deviceZoneEntity = zoneDetailsMap
							.get(device.getDeviceId() + "_" + zoneOriginalName);

					String zoneDesiredName = "";
					if (deviceZoneEntity != null && deviceZoneEntity.getZoneDesiredName() != null) {
						zoneDesiredName = deviceZoneEntity.getZoneDesiredName();
					}

					InputStatusDetailsEntity inputEntity = createInputStatusEntity(openingSms, resolvedSms, device,
							branch, zoneNum, zoneDetailsMap, formatterFirst, zoneId, isResolved);

					alarmTypeCountMap.put(zoneDesiredName, alarmTypeCountMap.getOrDefault(zoneDesiredName, 0) + 1);

					inputResponseList.add(inputEntity);

				} catch (Exception e) {
					System.out.println("Zone processing error: " + e.getMessage());
				}
			}
		}
	}

	private SmsDetailsEntity findPreviousOpenAlert(List<SmsDetailsEntity> sortedSmsData, SmsDetailsEntity currentSms,
			int zoneNum, Map<Integer, Method> zoneMethods) {

		Method method = zoneMethods.get(zoneNum);

		for (int i = sortedSmsData.indexOf(currentSms) - 1; i >= 0; i--) {
			try {
				SmsDetailsEntity prevSms = sortedSmsData.get(i);
				String prevZoneValue = (String) method.invoke(prevSms);

				if ("A".equals(prevZoneValue)) {
					return prevSms;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

	private SmsDetailsEntity findResolution(List<SmsDetailsEntity> sortedSmsData, SmsDetailsEntity currentSms,
			int zoneNum, Map<Integer, Method> zoneMethods) {
		Method method = zoneMethods.get(zoneNum);

		boolean foundCurrent = false;
		for (SmsDetailsEntity nextSms : sortedSmsData) {
			if (!foundCurrent) {
				if (nextSms.equals(currentSms)) {
					foundCurrent = true;
				}
				continue;
			}

			try {
				String nextZone = (String) method.invoke(nextSms);
				if ("N".equals(nextZone)) {
					return nextSms;
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				// logger.warn("Error checking resolution for zone " + zoneNum, e);
			}
		}
		return null;
	}

	private InputStatusDetailsEntity createInputStatusEntity(SmsDetailsEntity openingSms, SmsDetailsEntity resolvedSms,
			DeviceMasterEntity device, BranchMasterEntity branch, int zoneNum,
			Map<Object, DeviceZoneDetailsEntity> zoneDetailsMap, DateTimeFormatter formatterFirst, ZoneId zoneId,
			boolean isResolved) {

		// STEP 4

		InputStatusDetailsEntity entity = new InputStatusDetailsEntity();

		entity.setSmsId(openingSms.getId());
		entity.setDeviceSerialNo(openingSms.getPanelSerialNo());
		entity.setDeviceName(device.getDeviceName());
		entity.setBankName(branch.getBank());
		entity.setMobileNo(branch.getMobile());
		entity.setIfsc(branch.getIfsc());

		entity.setBranchName(branch.getBranchDesc());

		String zoneKey = device.getDeviceId() + "_ZONE" + zoneNum;
//	    System.out.println("ZONE ENTITY :::::   "+zoneKey);

		DeviceZoneDetailsEntity zoneEntity = zoneDetailsMap.get(zoneKey);
		String vendorName = (zoneEntity != null && zoneEntity.getVendors() != null) ? zoneEntity.getVendors()
				: device.getVendorName();

		entity.setVendorName(vendorName);

		LocalDateTime alarmTime = openingSms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();

		entity.setAlarmTime(alarmTime.format(formatterFirst));

		String zoneName = (zoneEntity != null && zoneEntity.getZoneDesiredName() != null)
				? zoneEntity.getZoneDesiredName()
				: "ZONE" + zoneNum;

		entity.setAlarmtype(zoneName);

		String alertType = (zoneEntity != null && zoneEntity.getAlertType() != null) ? zoneEntity.getAlertType()
				: "ZONE" + zoneNum;
		entity.setAlertType(alertType);

		if (isResolved && resolvedSms != null) {
			LocalDateTime resolvedTime = resolvedSms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();

			entity.setIssueResolvedTime(resolvedTime.format(formatterFirst));
			entity.setAlarmDescription("Resolved");

			Duration duration = Duration.between(alarmTime, resolvedTime);
			entity.setTimeDifference(formatDuration(duration));
		} else {
			entity.setAlarmDescription("Open");
			entity.setTimeDifference("Ongoing");
		}

		// STEP 5

		Ticket ticket = ticketDetailsRepository.findTicketNoBySerialZoneAndAlertId(openingSms.getPanelSerialNo(),
				String.valueOf(zoneNum), Long.valueOf(openingSms.getId()));

		if (ticket != null && ticket.getTicketNo() != null) {

			EscalationLog lastEscalation = escalationMailLogRepository
					.findTopEscalationByTicketId(ticket.getTicketNo());
			StringBuilder ticketIdBuilder = new StringBuilder();

			if (ticket.getTicketNo() != null) {
				ticketIdBuilder.append(ticket.getTicketNo());
			}

			if (ticket.getAlertGeneratedId() != null) {
				ticketIdBuilder.append("|").append(ticket.getAlertGeneratedId());
			}

			if (ticket.getResolvedAlertId() != null) {
				ticketIdBuilder.append("|").append(ticket.getResolvedAlertId());
			}

			entity.setTicketId(ticketIdBuilder.toString());
			if (lastEscalation != null) {
				entity.setEscalationLevel(lastEscalation.getEscLevel());

			}

			if ("O".equals(ticket.getTicketStatus()) && lastEscalation != null
					&& lastEscalation.getEscLevel() != null) {

				ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("TICKET",
						lastEscalation.getEscLevel());

				if (paramEntity != null) {
					entity.setEscalationColor(paramEntity.getDescp2());
				}

			} else if ("C".equals(ticket.getTicketStatus()) && "C".equals(ticket.getZoneStatus())) {

				ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("TICKET", "RESOLVED");

				if (paramEntity != null) {
					entity.setEscalationColor(paramEntity.getDescp2());
				}
			}

		} else {

			ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("TICKET", "NO_ESCALATION");

			if (paramEntity != null) {
				entity.setEscalationColor(paramEntity.getDescp2());
			}
		}
		return entity;
	}

	private String formatDuration(Duration duration) {
		long days = duration.toDays();
		long hours = duration.minusDays(days).toHours();
		long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

		if (days > 0) {
			return String.format("%d days, %d hours, and %d minutes", days, hours, minutes);
		} else if (hours > 0) {
			return String.format("%d hours and %d minutes", hours, minutes);
		} else if (minutes > 0) {
			return String.format("%d minutes", minutes);
		} else {
			return "0 minutes";
		}
	}

	private ResponseEntity createEmptyResponse(ResponseEntity response) {
		Map<String, Object> emptyResponse = new HashMap<>();
		emptyResponse.put("inputStatusDetails", new ArrayList<>());
		emptyResponse.put("alarmTypeCounts", new HashMap<>());

		response.setPayload(emptyResponse);
		response.setMessage("No data found");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}
	/*
	 * public ResponseEntity getResponseTimeReport(String branchCode, String
	 * systemIntegrator, String product, String status, String fromDate, String
	 * toDate) { ResponseEntity response = new ResponseEntity(); try {
	 * List<InputStatusDetailsEntity> inputResponseList = new ArrayList<>();
	 * Map<String, Integer> alarmTypeCountMap = new HashMap<>(); Set<String>
	 * processedAlerts = new HashSet<>(); Map<String, SmsDetailsEntity>
	 * lastOpenAlertPerZone = new HashMap<>();
	 * 
	 * // Get all branch codes at once ResponseEntity branchServiceResponse =
	 * branchService.getAllChildren(branchCode);
	 * 
	 * List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>)
	 * branchServiceResponse.getPayload(); if (branchCodeList == null ||
	 * branchCodeList.isEmpty()) { return createEmptyResponse(response); }
	 * 
	 * // Extract branch codes for batch queries List<String> branchCodes =
	 * branchCodeList.stream().map(BranchMasterEntity::getBranchCode)
	 * .collect(Collectors.toList());
	 * 
	 * // Batch fetch all devices for all branches at once List<DeviceMasterEntity>
	 * allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes); if
	 * (allDevices.isEmpty()) { return createEmptyResponse(response); }
	 * 
	 * // Filter devices by system integrator (vendor name) if provided // if
	 * (systemIntegrator != null && !systemIntegrator.trim().isEmpty() // &&
	 * !"all".equalsIgnoreCase(systemIntegrator)) { // allDevices =
	 * allDevices.stream() // .filter(device -> device.getVendorName() != null // &&
	 * device.getVendorName().equalsIgnoreCase(systemIntegrator.trim())) //
	 * .collect(Collectors.toList()); // }
	 * 
	 * 
	 * if (allDevices.isEmpty()) { return createEmptyResponse(response); }
	 * 
	 * // Create maps for quick lookups Map<String, DeviceMasterEntity>
	 * branchToDeviceMap = allDevices.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getBranchCode,
	 * Function.identity()));
	 * 
	 * Map<String, BranchMasterEntity> branchCodeMap = branchCodeList.stream()
	 * .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	 * Function.identity()));
	 * 
	 * // Get all device serial numbers for batch SMS query List<String>
	 * deviceSerialNos =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
	 * .collect(Collectors.toList());
	 * 
	 * // Batch fetch all SMS details for all devices at once - with sorting and
	 * date // filtering List<SmsDetailsEntity> allSmsDetails; if (fromDate != null
	 * && toDate != null && !fromDate.trim().isEmpty() && !toDate.trim().isEmpty())
	 * { // Parse dates and add time boundaries LocalDate startDate =
	 * LocalDate.parse(fromDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	 * LocalDate endDate = LocalDate.parse(toDate.trim(),
	 * DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	 * 
	 * Date fromDateTime =
	 * Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); Date
	 * toDateTime = Date.from(endDate.atTime(23, 59,
	 * 59).atZone(ZoneId.systemDefault()).toInstant());
	 * 
	 * allSmsDetails = smsDetailsRepository
	 * .findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(
	 * deviceSerialNos, fromDateTime, toDateTime);
	 * 
	 * } else { allSmsDetails = smsDetailsRepository
	 * .findByPanelSerialNoInOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos); }
	 * 
	 * if (allSmsDetails.isEmpty()) { return createEmptyResponse(response); }
	 * 
	 * // Group SMS details by panel serial number for efficient processing
	 * Map<String, List<SmsDetailsEntity>> smsGroupedByPanel =
	 * allSmsDetails.stream()
	 * .collect(Collectors.groupingBy(SmsDetailsEntity::getPanelSerialNo));
	 * 
	 * 
	 * // Batch fetch all device zone details List<String> deviceIds =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceId)
	 * .collect(Collectors.toList());
	 * 
	 * List<DeviceZoneDetailsEntity> allZoneDetails =
	 * deviceZoneDetailsRepository.findByDeviceIdIn(deviceIds);
	 * 
	 * 
	 * // ✅ Filter zones by system integrator (vendor) if provided if
	 * (systemIntegrator != null && !systemIntegrator.trim().isEmpty() &&
	 * !"all".equalsIgnoreCase(systemIntegrator)) { String vendorFilter =
	 * systemIntegrator.trim().toLowerCase(); allZoneDetails =
	 * allZoneDetails.stream() .filter(zone -> zone.getVendors() != null &&
	 * zone.getVendors().trim().toLowerCase().equals(vendorFilter))
	 * .collect(Collectors.toList()); }
	 * 
	 * 
	 * 
	 * // Create zone lookup map: deviceId_zoneName -> DeviceZoneDetailsEntity
	 * Map<Object, DeviceZoneDetailsEntity> zoneDetailsMap =
	 * allZoneDetails.stream().collect(Collectors .toMap(zone -> zone.getDeviceId()
	 * + "_" + zone.getZoneOriginalName(), Function.identity()));
	 * 
	 * // ✅ Keep only devices that have at least one zone left after filtering
	 * Set<String> validDeviceIds = allZoneDetails.stream()
	 * .map(DeviceZoneDetailsEntity::getDeviceId) .collect(Collectors.toSet());
	 * 
	 * allDevices = allDevices.stream() .filter(device ->
	 * validDeviceIds.contains(device.getDeviceId())) .collect(Collectors.toList());
	 * 
	 * if (allDevices.isEmpty()) { return createEmptyResponse(response); }
	 * 
	 * 
	 * // Pre-compile zone method names to avoid repeated reflection Map<Integer,
	 * Method> zoneMethods = new HashMap<>(); try { for (int i = 1; i <= 40; i++) {
	 * Method method = SmsDetailsEntity.class.getMethod("getZone" + i);
	 * zoneMethods.put(i, method); } } catch (NoSuchMethodException e) { throw new
	 * RuntimeException("Zone methods not found", e); }
	 * 
	 * // Process each device's SMS data for (DeviceMasterEntity device :
	 * allDevices) { List<SmsDetailsEntity> deviceSmsData =
	 * smsGroupedByPanel.get(device.getDeviceSerialNo()); if (deviceSmsData == null
	 * || deviceSmsData.isEmpty()) continue;
	 * 
	 * BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode()); if
	 * (branch == null) continue;
	 * 
	 * processDeviceAlerts(deviceSmsData, device, branch, zoneDetailsMap,
	 * zoneMethods, inputResponseList, alarmTypeCountMap, processedAlerts,
	 * lastOpenAlertPerZone); }
	 * 
	 * // Apply product and status filters after processing
	 * List<InputStatusDetailsEntity> filteredResponseList =
	 * applyFilters(inputResponseList, product, status);
	 * 
	 * 
	 * 
	 * filteredResponseList.sort((a, b) -> { String alarmTimeA = a.getAlarmTime();
	 * String alarmTimeB = b.getAlarmTime();
	 * 
	 * // Handle null values - put nulls at the end if (alarmTimeA == null &&
	 * alarmTimeB == null) return 0; if (alarmTimeA == null) return 1; if
	 * (alarmTimeB == null) return -1;
	 * 
	 * try { // Parse the string dates to Date objects for proper chronological
	 * sorting // Format: "01/08/2025, 09:38:28" DateTimeFormatter formatter =
	 * DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss"); LocalDateTime dateTimeA
	 * = LocalDateTime.parse(alarmTimeA.trim(), formatter); LocalDateTime dateTimeB
	 * = LocalDateTime.parse(alarmTimeB.trim(), formatter);
	 * 
	 * // Sort in descending order (latest first) int result =
	 * dateTimeB.compareTo(dateTimeA); // System.out.println("Comparing: " +
	 * alarmTimeB + " vs " + alarmTimeA + " = " + // result);
	 * 
	 * 
	 * return result; } catch (Exception e) { // If parsing fails, try alternative
	 * formats or fall back to string comparison //
	 * System.out.println("Date parsing failed for: '" + alarmTimeA + "' or '" + //
	 * alarmTimeB + "', error: " + e.getMessage());
	 * 
	 * // Try alternative parsing without comma try { String cleanA =
	 * alarmTimeA.replace(",", "").trim(); String cleanB = alarmTimeB.replace(",",
	 * "").trim(); DateTimeFormatter altFormatter =
	 * DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); LocalDateTime dateTimeA =
	 * LocalDateTime.parse(cleanA, altFormatter); LocalDateTime dateTimeB =
	 * LocalDateTime.parse(cleanB, altFormatter); return
	 * dateTimeB.compareTo(dateTimeA); } catch (Exception e2) { // Final fallback to
	 * string comparison (reverse order for descending) return
	 * alarmTimeB.compareTo(alarmTimeA); } } });
	 * 
	 * 
	 * // Recalculate alarm type counts for filtered data Map<String, Integer>
	 * filteredAlarmTypeCountMap = new HashMap<>(); for (InputStatusDetailsEntity
	 * entity : filteredResponseList) { String alarmType = entity.getAlarmtype() !=
	 * null ? entity.getAlarmtype() : ""; filteredAlarmTypeCountMap.put(alarmType,
	 * filteredAlarmTypeCountMap.getOrDefault(alarmType, 0) + 1); }
	 * 
	 * // Create final response Map<String, Object> finalResponse = new HashMap<>();
	 * finalResponse.put("inputStatusDetails", filteredResponseList);
	 * finalResponse.put("alarmTypeCounts", filteredAlarmTypeCountMap);
	 * 
	 * response.setPayload(finalResponse);
	 * response.setMessage("Data fetched successfully");
	 * response.setStatusCode(HttpStatus.OK.value());
	 * 
	 * } catch (Exception e) { //
	 * logger.error("Error occurred while fetching input status details", e);
	 * response.setMessage("Error occurred while fetching");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 * 
	 * private List<InputStatusDetailsEntity>
	 * applyFilters(List<InputStatusDetailsEntity> inputList, String product, String
	 * status) {
	 * 
	 * return inputList.stream().filter(entity -> { // Product filter (based on
	 * alarmtype) if (product != null && !product.trim().isEmpty() &&
	 * !"all".equalsIgnoreCase(product)) { String entityAlarmType =
	 * entity.getAlarmtype() != null ? entity.getAlarmtype() : ""; if
	 * (!entityAlarmType.equalsIgnoreCase(product.trim())) { return false; } }
	 * 
	 * // Status filter (based on alarmDescription) if (status != null &&
	 * !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) { String
	 * entityStatus = entity.getAlarmDescription() != null ?
	 * entity.getAlarmDescription() : ""; if
	 * (!entityStatus.equalsIgnoreCase(status.trim())) { return false; } }
	 * 
	 * return true; }).collect(Collectors.toList()); }
	 * 
	 * private void processDeviceAlerts(List<SmsDetailsEntity> smsDetailsEntityList,
	 * DeviceMasterEntity device, BranchMasterEntity branch, Map<Object,
	 * DeviceZoneDetailsEntity> zoneDetailsMap, Map<Integer, Method> zoneMethods,
	 * List<InputStatusDetailsEntity> inputResponseList, Map<String, Integer>
	 * alarmTypeCountMap, Set<String> processedAlerts, Map<String, SmsDetailsEntity>
	 * lastOpenAlertPerZone) {
	 * 
	 * DateTimeFormatter formatterFirst =
	 * DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss"); DateTimeFormatter
	 * formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"); ZoneId
	 * zoneId = ZoneId.systemDefault();
	 * 
	 * for (SmsDetailsEntity sms : smsDetailsEntityList) { for (int zoneNum = 1;
	 * zoneNum <= 40; zoneNum++) { try { Method method = zoneMethods.get(zoneNum);
	 * String zone = (String) method.invoke(sms);
	 * 
	 * if (!"A".equals(zone)) continue;
	 * 
	 * String zoneKey = sms.getPanelSerialNo() + "_Zone" + zoneNum;
	 * 
	 * // Prevent repeated open alerts for same zone if
	 * (lastOpenAlertPerZone.containsKey(zoneKey)) { continue; }
	 * 
	 * String uniqueAlertKey = sms.getPanelSerialNo() + "Zone" + zoneNum + "" +
	 * sms.getDateTime().toString();
	 * 
	 * if (processedAlerts.contains(uniqueAlertKey)) {
	 * 
	 * continue; }
	 * 
	 * 
	 * // Find resolution using binary search or efficient lookup since data is
	 * sorted SmsDetailsEntity resolvedSms = findResolution(smsDetailsEntityList,
	 * sms, zoneNum, zoneMethods); boolean isResolved = resolvedSms != null;
	 * 
	 * // Track only unresolved open alerts if (!isResolved) {
	 * lastOpenAlertPerZone.put(zoneKey, sms); }
	 * 
	 * // System.out.println("sms---->>"+sms.toString());
	 * 
	 * // Mark alerts as processed processedAlerts.add(uniqueAlertKey); if
	 * (isResolved) { processedAlerts.add( sms.getPanelSerialNo() + "Zone" + zoneNum
	 * + "" + resolvedSms.getDateTime().toString()); }
	 * 
	 * // Get zone details for counting - FIXED PART String zoneOriginalNameString =
	 * "Zone" + zoneNum; String deviceZoneKey = device.getDeviceId() + "_" +
	 * zoneOriginalNameString; DeviceZoneDetailsEntity deviceZoneEntity =
	 * zoneDetailsMap.get(deviceZoneKey);
	 * 
	 * // Use zoneDesiredName for counting, fallback to empty string if null String
	 * zoneDesiredName = ""; if (deviceZoneEntity != null &&
	 * deviceZoneEntity.getZoneDesiredName() != null) { zoneDesiredName =
	 * deviceZoneEntity.getZoneDesiredName(); }
	 * 
	 * 
	 * // System.out.println("formatterFirst---->>"+formatterFirst.toString()); //
	 * Create and populate the entity InputStatusDetailsEntity
	 * inputStatusDetailsEntity = createInputStatusEntity(sms, resolvedSms, device,
	 * branch, zoneNum, zoneDetailsMap, formatterFirst, formatter, zoneId,
	 * isResolved);
	 * 
	 * 
	 * // Use zoneDesiredName for alarm type counting instead of the entity's
	 * alarmtype alarmTypeCountMap.put(zoneDesiredName,
	 * alarmTypeCountMap.getOrDefault(zoneDesiredName, 0) + 1);
	 * inputResponseList.add(inputStatusDetailsEntity);
	 * 
	 * } catch (IllegalAccessException | InvocationTargetException e) {
	 * System.out.println("Error processing zone " + zoneNum + " for SMS: " +
	 * sms.getId()+ e); } } } }
	 * 
	 * private SmsDetailsEntity findResolution(List<SmsDetailsEntity> sortedSmsData,
	 * SmsDetailsEntity currentSms, int zoneNum, Map<Integer, Method> zoneMethods) {
	 * Method method = zoneMethods.get(zoneNum);
	 * 
	 * // Since data is sorted by datetime, we can start from current position and
	 * move // forward boolean foundCurrent = false; for (SmsDetailsEntity nextSms :
	 * sortedSmsData) { if (!foundCurrent) { if (nextSms.equals(currentSms)) {
	 * foundCurrent = true; } continue; }
	 * 
	 * try { String nextZone = (String) method.invoke(nextSms); if
	 * ("N".equals(nextZone)) { return nextSms; } } catch (IllegalAccessException |
	 * InvocationTargetException e) { //
	 * logger.warn("Error checking resolution for zone " + zoneNum, e); } } return
	 * null; }
	 * 
	 * private InputStatusDetailsEntity createInputStatusEntity(SmsDetailsEntity
	 * sms, SmsDetailsEntity resolvedSms, DeviceMasterEntity device,
	 * BranchMasterEntity branch, int zoneNum, Map<Object, DeviceZoneDetailsEntity>
	 * zoneDetailsMap, DateTimeFormatter formatterFirst, DateTimeFormatter
	 * formatter, ZoneId zoneId, boolean isResolved) {
	 * 
	 * InputStatusDetailsEntity entity = new InputStatusDetailsEntity();
	 * 
	 * // Basic info entity.setDeviceSerialNo(sms.getPanelSerialNo());
	 * entity.setDeviceName(device.getDeviceName());
	 * entity.setBankName(branch.getBank());
	 * entity.setBranchName(branch.getBranchDesc());
	 * entity.setMobileNo(branch.getMobile()); entity.setIfsc(branch.getIfsc());
	 * 
	 * // Fetch zone details String zoneOriginalNameString = "ZONE" + zoneNum;
	 * String deviceZoneKey = device.getDeviceId() + "_" + zoneOriginalNameString;
	 * 
	 * DeviceZoneDetailsEntity deviceZoneEntity = zoneDetailsMap.get(deviceZoneKey);
	 * if (deviceZoneEntity == null) { deviceZoneEntity =
	 * deviceZoneDetailsRepository.findByDeviceIdAndAlertType(device.getDeviceId(),
	 * "Zone" + zoneNum); }
	 * 
	 * // 🟩 Vendor name from DeviceZoneDetailsEntity (fallback to device) String
	 * vendorName = null; if (deviceZoneEntity != null &&
	 * deviceZoneEntity.getVendors() != null &&
	 * !deviceZoneEntity.getVendors().trim().isEmpty()) { vendorName =
	 * deviceZoneEntity.getVendors(); } else { vendorName = device.getVendorName();
	 * } entity.setVendorName(vendorName);
	 * 
	 * // Alarm time LocalDateTime alarmTime =
	 * sms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();
	 * 
	 * // System.out.println("--->>>>>>alarmTime.format(formatterFirst)"+alarmTime.
	 * format(formatterFirst));
	 * entity.setAlarmTime(alarmTime.format(formatterFirst));
	 * 
	 * // Zone name (desired name) String zoneName = ""; if (deviceZoneEntity !=
	 * null && deviceZoneEntity.getZoneDesiredName() != null) { zoneName =
	 * deviceZoneEntity.getZoneDesiredName(); } entity.setAlarmtype(zoneName);
	 * 
	 * // Alert type if (deviceZoneEntity != null && deviceZoneEntity.getAlertType()
	 * != null) { entity.setAlertType(deviceZoneEntity.getAlertType()); }
	 * 
	 * // Resolution if (isResolved && resolvedSms != null) { LocalDateTime
	 * resolvedTime =
	 * resolvedSms.getDateTime().toInstant().atZone(zoneId).toLocalDateTime();
	 * entity.setIssueResolvedTime(resolvedTime.format(formatterFirst));
	 * entity.setAlarmDescription("Resolved"); Duration duration =
	 * Duration.between(alarmTime, resolvedTime);
	 * entity.setTimeDifference(formatDuration(duration)); } else {
	 * entity.setAlarmDescription("Open"); entity.setTimeDifference("Ongoing"); }
	 * 
	 * return entity; }
	 * 
	 * 
	 * private String formatDuration(Duration duration) { long days =
	 * duration.toDays(); long hours = duration.minusDays(days).toHours(); long
	 * minutes = duration.minusDays(days).minusHours(hours).toMinutes();
	 * 
	 * if (days > 0) { return String.format("%d days, %d hours, and %d minutes",
	 * days, hours, minutes); } else if (hours > 0) { return
	 * String.format("%d hours and %d minutes", hours, minutes); } else if (minutes
	 * > 0) { return String.format("%d minutes", minutes); } else { return
	 * "0 minutes"; // Handle case where duration is less than a minute } }
	 * 
	 * private ResponseEntity createEmptyResponse(ResponseEntity response) {
	 * Map<String, Object> emptyResponse = new HashMap<>();
	 * emptyResponse.put("inputStatusDetails", new ArrayList<>());
	 * emptyResponse.put("alarmTypeCounts", new HashMap<>());
	 * 
	 * response.setPayload(emptyResponse); response.setMessage("No data found");
	 * response.setStatusCode(HttpStatus.OK.value()); return response; }
	 * 
	 */

	public ResponseEntity getDeviceAndZoneDetailsByBranchCode(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
			if (device.getDeviceType().equals("SIA")) {
				List<SiaProtocalEntity> sia = protocalRepository.findByDeviceId(device.getDeviceId());
				DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();

				deviceMasterDTO.setDeviceSerialNo(device.getDeviceSerialNo());
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceId(device.getDeviceId());
				deviceMasterDTO.setDeviceName(device.getDeviceName());
				deviceMasterDTO.setDeviceType(device.getDeviceType());
				deviceMasterDTO.setDeviceLocation(device.getDeviceLocation());
				deviceMasterDTO.setPhoneNumber(device.getPhoneNumber());
				deviceMasterDTO.setProtectionType(device.getProtectionType());
				deviceMasterDTO.setUserNo(device.getUserNo());
				deviceMasterDTO.setUserPassword(device.getUserPassword());
				deviceMasterDTO.setEmail1(device.getEmail1());
				deviceMasterDTO.setProductType(device.getProductType());
				deviceMasterDTO.setPasscode(device.getPasscode());
				deviceMasterDTO.setAddress(device.getAddress());
				deviceMasterDTO.setSupportNo(device.getSupportNo());
				deviceMasterDTO.setBranchCode(device.getBranchCode());
				deviceMasterDTO.setVendorName(device.getVendorName());
				deviceMasterDTO.setVendorEmail(device.getVendorEmail());
				deviceMasterDTO.setVendorMobile(device.getVendorMobile());
				deviceMasterDTO.setStatus(device.getStatus());
				deviceMasterDTO.setSiaProtocol(sia);
				response.setPayload(deviceMasterDTO);
				response.setMessage("Device Details fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				List<DeviceZoneDetailsEntity> list = deviceZoneDetailsRepository
						.getZoneDetailsByDeviceId(device.getDeviceId());
				// System.out.println(list);
				List<DynamicConfigurationEntity> dynamicConfigurationEntities = dynamicConfigurationRepository
						.findByDeviceId(device.getDeviceId());
				if (device != null && list != null && dynamicConfigurationEntities != null) {
					DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
					BeanUtils.copyProperties(device, deviceMasterDTO);
					deviceMasterDTO.setPhoneNumber(device.getPhoneNumber().substring(3));

					deviceMasterDTO.setZoneList(list);
					deviceMasterDTO.setDynamicConfigurationList(dynamicConfigurationEntities);

					response.setPayload(deviceMasterDTO);
					response.setMessage("Device Details fetched successfully.");
					response.setStatusCode(HttpStatus.OK.value());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity checkPanelSerialPresent(String panelSerialNo) {

		ResponseEntity response = new ResponseEntity();

		DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(panelSerialNo);
		if (device != null) {
			response.setPayload(true);
		} else {
			response.setPayload(false);
			response.setMessage("Device Serial No " + panelSerialNo
					+ " already present in the records.Please use another device serial no.");
		}
		return response;
	}

	public ResponseEntity checkPhoneNoPresent(String phoneNo) {
		ResponseEntity response = new ResponseEntity();

		DeviceMasterEntity device = deviceMasterRepository.findByPhoneNumber(phoneNo);
		if (device != null) {
			response.setPayload(true);
		} else {
			response.setPayload(false);
			response.setMessage("Device Phone No " + phoneNo
					+ " already present in the records.Please use another device serial no.");
		}
		return response;
	}

	public ResponseEntity getVendorByBranchCodeAndAlertType(String branchCode, String alertType) {
		ResponseEntity response = new ResponseEntity();
		DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branchCode);
		Map<String, String> vendorMap = new HashMap<>();
		if (deviceMasterEntity == null) {

			response.setMessage("No device found for this branch code :" + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneDetailsRepository
					.findByDeviceIdAndAlertType(deviceMasterEntity.getDeviceId(), alertType);
			if (deviceZoneDetailsEntity != null) {
				String vendorName = deviceZoneDetailsEntity.getVendors();
				String mobile = vendorMasterRepository.findByVendorName(deviceZoneDetailsEntity.getVendors()).get()
						.getMobile();
				vendorMap.put("vendorName", vendorName);
				vendorMap.put("mobile", mobile);
				response.setPayload(vendorMap);
			}

		}

		return response;
	}

	public ResponseEntity getAllDevicesByBranchCodeWithoutLastSync(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			// Step 1: Fetch all child branches in one go
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
			branchList = branchList == null ? new ArrayList<>() : branchList;

			if (branchList.isEmpty()) {
				response.setMessage("No branches found for given branch code");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			// Step 2: Collect all branch codes
			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.collect(Collectors.toList());

			// Step 3: Fetch all devices in ONE query
			List<DeviceMasterEntity> devices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);

			if (devices.isEmpty()) {
				response.setMessage("No devices present against the given branch codes");
				response.setStatusCode(HttpStatus.OK.value());
				return response;
			}

			// Step 5: Prepare final response list
			List<DeviceMasterDTO> responseList = new ArrayList<>();

			for (DeviceMasterEntity device : devices) {
				DeviceMasterDTO deviceDtO = new DeviceMasterDTO();

				deviceDtO.setDeviceId(device.getDeviceId());
				deviceDtO.setDeviceName(device.getDeviceName());
				deviceDtO.setDeviceSerialNo(device.getDeviceSerialNo());
				deviceDtO.setDeviceType(device.getDeviceType());
				deviceDtO.setDeviceLocation(device.getDeviceLocation());
				deviceDtO.setPhoneNumber(device.getPhoneNumber());
				deviceDtO.setProtectionType(device.getProtectionType());
				deviceDtO.setUserNo(device.getUserNo());
				deviceDtO.setUserPassword(device.getUserPassword());
				deviceDtO.setEmail1(device.getEmail1());
				deviceDtO.setProductType(device.getProductType());
				deviceDtO.setPasscode(device.getPasscode());
				deviceDtO.setAddress(device.getAddress());
				deviceDtO.setSupportNo(device.getSupportNo());
				deviceDtO.setBranchCode(device.getBranchCode());
				deviceDtO.setVendorName(device.getVendorName());
				deviceDtO.setVendorEmail(device.getVendorEmail());
				deviceDtO.setVendorMobile(device.getVendorMobile());
				deviceDtO.setStatus(device.getStatus());

				// Attach branch details
				BranchMasterEntity branch = branchList.stream()
						.filter(b -> b.getBranchCode().equals(device.getBranchCode())).findFirst().orElse(null);

				if (branch != null) {
					String branchAddressString = branch.getAddress1() + " " + branch.getAddress2() + " "
							+ branch.getAddress3() + " " + branch.getAddress4();
					String parentBranch = branchMasterRepository.findParentBranchName(device.getBranchCode());

					deviceDtO.setBranchName(branch.getBranchDesc() + " - " + parentBranch);
					deviceDtO.setBranchAddress(branchAddressString);
					deviceDtO.setBankName(branch.getBank());
					deviceDtO.setIfsc(branch.getIfsc());
				}

				// Exclude inactive devices
				if (!"inactive".equalsIgnoreCase(device.getStatus())) {
					responseList.add(deviceDtO);
				}
			}

			response.setPayload(responseList);
			response.setMessage("All devices by branch code fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getDeviceWiseTotalCount(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		// Step 1: Get all child branches
		ResponseEntity responseEntity = branchService.getAllChildren(branchCode);

		@SuppressWarnings("unchecked")
		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

		if (branchList == null || branchList.isEmpty()) {

			response.setPayload(0);

			response.setMessage("No branches found for given branch code");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());

			return response;
		}

		// Step 2: Collect branch codes
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Step 3: Fetch devices
		List<DeviceMasterEntity> devices = deviceMasterRepository.findAllByBranchCodeIn(branchCodes);

		if (devices == null || devices.isEmpty()) {

			response.setPayload(0);

			response.setMessage("No devices present");
			response.setStatusCode(HttpStatus.OK.value());

			return response;
		}

		// Step 4: Collect device IDs
		List<String> deviceIds = devices.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());

		// Step 5: Fetch all zones for these devices
		List<DeviceZoneDetailsEntity> zones = deviceZoneDetailsRepository.findAllByDeviceIdIn(deviceIds);

		// Step 6: Count ONLY primary zones per device type
		Map<String, Long> deviceTypeCountMap = zones.stream().filter(z -> "Y".equalsIgnoreCase(z.getPrimaryFlag()))
				.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName, Collectors.counting()));

		// Step 7: Prepare response
		response.setPayload(deviceTypeCountMap);

		response.setMessage("Device counts fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

//	public ResponseEntity getOpenAlertsWithCommunication(String branchCode,String systemIntegrator,String productFilter,String fromDate,String toDate) {
//
//	    ResponseEntity response = new ResponseEntity();
//	    
////	    ResponseEntity responseTimeReportEntity = getResponseTimeReport(branchCode, systemIntegrator, branchCode, systemIntegrator, fromDate, toDate);
////	    
////	    List<InputStatusDetailsEntity> inputStatusDetailsEntities = (List<InputStatusDetailsEntity>) responseTimeReportEntity.getPayload().get(inputStatusDetails);
//	    
//	    ResponseEntity responseTimeReportEntity =
//	            getResponseTimeReport(branchCode, systemIntegrator, productFilter, "open", fromDate, toDate);
//	    
////	    System.out.println(responseTimeReportEntity.getPayload().toString());
//
//
//	    @SuppressWarnings("unchecked")
//	    Map<String, Object> payload =
//	            (Map<String, Object>) responseTimeReportEntity.getPayload();
//
//	    List<InputStatusDetailsEntity> inputStatusDetailsEntities =
//	            payload != null && payload.get("inputStatusDetails") != null
//	                    ? (List<InputStatusDetailsEntity>) payload.get("inputStatusDetails")
//	                    : new ArrayList<>();
//
//	    
////	    System.out.println(inputStatusDetailsEntities.toString());
//	    
//	    
//	    	
//	    List<OpenAlertCommunicationDTO> responseList = new ArrayList<>();
//	    
//	    for(InputStatusDetailsEntity inputItem:inputStatusDetailsEntities) {
//	    	if(inputItem.getAlertType()==null) {
//	    		continue;
//	    	}
//	    	
//	    	OpenAlertCommunicationDTO openAlertCommunicationDTO = new OpenAlertCommunicationDTO();
//	    	
//	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
//	    	LocalDateTime alarmDateTime = LocalDateTime.parse(inputItem.getAlarmTime(), formatter);
//	    	LocalDateTime now = LocalDateTime.now();
//	    	Duration duration = Duration.between(alarmDateTime, now);
//	    	long days = duration.toDays();
//	    	long hours = duration.minusDays(days).toHours();
//	    	long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
//	    	String timeDiff;
//	    	if (days > 0) {
//	    	    timeDiff = days + " days " + hours + " hours " + minutes + " minutes";
//	    	} else if (hours > 0) {
//	    	    timeDiff = hours + " hours " + minutes + " minutes";
//	    	} else {
//	    	    timeDiff = minutes + " minutes";
//	    	}
//
//	    	
//	    	openAlertCommunicationDTO.setAlarmTime(inputItem.getAlarmTime());
//	    	openAlertCommunicationDTO.setDeviceName(inputItem.getDeviceName());
//	    	openAlertCommunicationDTO.setAlertType(inputItem.getAlertType());
//	    	openAlertCommunicationDTO.setBankName(inputItem.getBankName());
//	    	openAlertCommunicationDTO.setBranchName(inputItem.getBranchName());
//	    	openAlertCommunicationDTO.setAlarmType(inputItem.getAlarmtype());
//	    	openAlertCommunicationDTO.setDeviceSerialNo(inputItem.getDeviceSerialNo());
//	    	openAlertCommunicationDTO.setVendorName(inputItem.getVendorName());
//	    	openAlertCommunicationDTO.setStatus(inputItem.getAlarmDescription());
//	    	openAlertCommunicationDTO.setTimeDiff(timeDiff);
//	    	
//	    	List<CommunicationMapping> communicationMappingList = communicationMappingRepository.findByPanelSerialNoAndSmsDetailsId(inputItem.getDeviceSerialNo(), inputItem.getSmsId());
//	    	
//	    	if(communicationMappingList!=null) {
//	    		
//	    		communicationMappingList.forEach(communicationMapping->{
//	    			
//	    			java.util.Optional<EmailLogEntity> optionalEmail= emailLogRepository.findById(Long.valueOf(communicationMapping.getMail()));
//		    		
//		    		if(optionalEmail.isPresent()) {
//		    			openAlertCommunicationDTO.setEmailLog(optionalEmail.get());
//		    		}
//		    		
//		    		java.util.Optional<SmsLogEntity> optionalSms = smsLogRepository.findById(Long.valueOf(communicationMapping.getSms()));	
//		    		
//		    		if(optionalSms.isPresent()) {
//		    			openAlertCommunicationDTO.setSmsLog(optionalSms.get());
//		    		}
//		    		
//		    		java.util.Optional<CallLogEntity> optionalCall = callLogRepository.findById(Long.valueOf(communicationMapping.getCalls()));
//		    		
//		    		if(optionalCall.isPresent()) {
//		    			openAlertCommunicationDTO.setCallLog(optionalCall.get());
//		    		}
//	    			
//	    		});
//	    		
//	    		
//	    		
//	    		
//	    		
//	    	}
//	    	
//	    	responseList.add(openAlertCommunicationDTO);
//	    	
//	    	
//	    }
//	    
//	    response.setPayload(responseList);
//	    response.setMessage("Data fetched succesfully");
//	    response.setStatusCode(HttpStatus.OK.value());
//	    
//	    
//
//	    
//	    return response;
//	}

	public ResponseEntity getOpenAlertsWithCommunication(String branchCode, String systemIntegrator,
			String productFilter, String fromDate, String toDate) {

		System.out.println("==== getOpenAlertsWithCommunication START ====");
		System.out.println("Params -> branchCode: " + branchCode + ", systemIntegrator: " + systemIntegrator
				+ ", productFilter: " + productFilter + ", fromDate: " + fromDate + ", toDate: " + toDate);

		ResponseEntity response = new ResponseEntity();

		ResponseEntity responseTimeReportEntity = getResponseTimeReport(branchCode, systemIntegrator, productFilter,
				"open", fromDate, toDate);

		System.out.println("ResponseTimeReport API called");

		@SuppressWarnings("unchecked")
		Map<String, Object> payload = (Map<String, Object>) responseTimeReportEntity.getPayload();

		List<InputStatusDetailsEntity> inputStatusDetailsEntities = payload != null
				&& payload.get("inputStatusDetails") != null
						? (List<InputStatusDetailsEntity>) payload.get("inputStatusDetails")
						: new ArrayList<>();

		System.out.println("Total inputStatusDetails count: " + inputStatusDetailsEntities.size());

		List<OpenAlertCommunicationDTO> responseList = new ArrayList<>();

		for (InputStatusDetailsEntity inputItem : inputStatusDetailsEntities) {

			if (inputItem.getAlertType() == null) {
				System.out.println("Skipping record due to NULL alertType | Device: " + inputItem.getDeviceSerialNo());
				continue;
			}

			System.out.println("Processing alert | Device: " + inputItem.getDeviceSerialNo() + ", AlertType: "
					+ inputItem.getAlertType() + ", SmsId: " + inputItem.getSmsId());

			OpenAlertCommunicationDTO openAlertCommunicationDTO = new OpenAlertCommunicationDTO();

			// ---- Time Difference Calculation ----
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
			LocalDateTime alarmDateTime = LocalDateTime.parse(inputItem.getAlarmTime(), formatter);
			LocalDateTime now = LocalDateTime.now();

			Duration duration = Duration.between(alarmDateTime, now);
			long days = duration.toDays();
			long hours = duration.minusDays(days).toHours();
			long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

			String timeDiff;
			if (days > 0) {
				timeDiff = days + " days " + hours + " hours " + minutes + " minutes";
			} else if (hours > 0) {
				timeDiff = hours + " hours " + minutes + " minutes";
			} else {
				timeDiff = minutes + " minutes";
			}

			// ---- DTO Mapping ----
			openAlertCommunicationDTO.setAlarmTime(inputItem.getAlarmTime());
			openAlertCommunicationDTO.setDeviceName(inputItem.getDeviceName());
			openAlertCommunicationDTO.setAlertType(inputItem.getAlertType());
			openAlertCommunicationDTO.setBankName(inputItem.getBankName());
			openAlertCommunicationDTO.setBranchName(inputItem.getBranchName());
			openAlertCommunicationDTO.setAlarmType(inputItem.getAlarmtype());
			openAlertCommunicationDTO.setDeviceSerialNo(inputItem.getDeviceSerialNo());
			openAlertCommunicationDTO.setVendorName(inputItem.getVendorName());
			openAlertCommunicationDTO.setStatus(inputItem.getAlarmDescription());
			openAlertCommunicationDTO.setTimeDiff(timeDiff);

			List<CommunicationMapping> communicationMappingList = communicationMappingRepository
					.findByPanelSerialNoAndSmsDetailsId(inputItem.getDeviceSerialNo(), inputItem.getSmsId());

			System.out.println("Communication mappings found: "
					+ (communicationMappingList != null ? communicationMappingList.size() : 0));

			if (communicationMappingList != null) {

				communicationMappingList.forEach(communicationMapping -> {

					System.out.println("Mapping -> Mail: " + communicationMapping.getMail() + ", Sms: "
							+ communicationMapping.getSms() + ", Call: " + communicationMapping.getCalls());

					// -------- EMAIL --------
					String mailId = communicationMapping.getMail();
					if (mailId != null && !mailId.trim().isEmpty()) {
						try {
							emailLogRepository.findById(Long.valueOf(mailId)).ifPresent(email -> {
								openAlertCommunicationDTO.setEmailLog(email);
								System.out.println("Email log attached");
							});
						} catch (NumberFormatException e) {
							System.out.println("Invalid Email ID: " + mailId);
						}
					} else {
						System.out.println("Email ID empty/null, skipped");
					}

					// -------- SMS --------
					String smsId = communicationMapping.getSms();
					if (smsId != null && !smsId.trim().isEmpty()) {
						try {
							smsLogRepository.findById(Long.valueOf(smsId)).ifPresent(sms -> {
								openAlertCommunicationDTO.setSmsLog(sms);
								System.out.println("SMS log attached");
							});
						} catch (NumberFormatException e) {
							System.out.println("Invalid SMS ID: " + smsId);
						}
					} else {
						System.out.println("SMS ID empty/null, skipped");
					}

					// -------- CALL --------
					String callId = communicationMapping.getCalls();
					if (callId != null && !callId.trim().isEmpty()) {
						try {
							callLogRepository.findById(Long.valueOf(callId)).ifPresent(call -> {
								openAlertCommunicationDTO.setCallLog(call);
								System.out.println("Call log attached");
							});
						} catch (NumberFormatException e) {
							System.out.println("Invalid Call ID: " + callId);
						}
					} else {
						System.out.println("Call ID empty/null, skipped");
					}
				});
			}

			responseList.add(openAlertCommunicationDTO);
		}

		System.out.println("Final response list size: " + responseList.size());

		response.setPayload(responseList);
		response.setMessage("Data fetched succesfully");
		response.setStatusCode(HttpStatus.OK.value());

		System.out.println("==== getOpenAlertsWithCommunication END ====");

		return response;
	}

//	public ResponseEntity getTicketReport(String branchCode, String systemIntegrator, String product,
//	        String status, String fromDate, String toDate,String ticketNo) {
//	    ResponseEntity response = new ResponseEntity();
//
//	    // Parse dates
//	    LocalDateTime fromDateTime = null;
//	    LocalDateTime toDateTime = null;
//	    try {
//	        if (fromDate != null && !fromDate.isEmpty()) {
//	            fromDateTime = LocalDate.parse(fromDate).atStartOfDay();
//	        }
//	        if (toDate != null && !toDate.isEmpty()) {
//	            toDateTime = LocalDate.parse(toDate).atTime(23, 59, 59);
//	        }
//	    } catch (Exception e) {
//	        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//	        response.setMessage("Invalid date format. Use yyyy-MM-dd");
//	        return response;
//	    }
//
//	    ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);
//	    List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
//
//	    if (branchCodeList == null || branchCodeList.isEmpty()) {
//	        return createEmptyResponse(response);
//	    }
//
//	    List<String> branchCodes = branchCodeList.stream()
//	            .map(BranchMasterEntity::getBranchCode)
//	            .collect(Collectors.toList());
//
//	    List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);
//	    if (allDevices == null || allDevices.isEmpty()) {
//	        return createEmptyResponse(response);
//	    }
//
//	    Map<String, BranchMasterEntity> branchCodeMap = branchCodeList.stream()
//	            .collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));
//
//	    Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
//	            .collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, Function.identity()));
//
//	    List<String> deviceSerialNos = allDevices.stream()
//	            .map(DeviceMasterEntity::getDeviceSerialNo)
//	            .collect(Collectors.toList());
//
//	    // ✅ Filters directly query mein — agar null/empty hai toh ignore hoga
//	    List<Ticket> allTickets = ticketDetailsRepository.findTicketsByFilters(
//	            deviceSerialNos,
//	            (ticketNo != null && !ticketNo.isEmpty()) ? ticketNo : null,
//	            (status != null && !status.isEmpty()) ? status : null,
//	            fromDateTime,
//	            toDateTime
//	    );
//
//	    if (allTickets == null || allTickets.isEmpty()) {
//	        return createEmptyResponse(response);
//	    }
//
//	    Map<String, List<Ticket>> ticketGroupMap = allTickets.stream()
//	            .collect(Collectors.groupingBy(Ticket::getTicketNo));
//
//	    List<TicketDataDTO> ticketReportList = new ArrayList<>();
//
//	    for (Map.Entry<String, List<Ticket>> entry : ticketGroupMap.entrySet()) {
//
//	        List<Ticket> ticketGroup = entry.getValue();
//
//	        Ticket baseTicket = ticketGroup.stream()
//	                .filter(t -> t.getCreateAt() != null)
//	                .min(Comparator.comparing(Ticket::getCreateAt))
//	                .orElse(ticketGroup.get(0));
//
//	        DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findByDeviceSerialNo(baseTicket.getDeviceSerialNo());
//	       // System.out.println("Processing Ticket GROUP :: " + baseTicket.getGroupName() + ", Device ID :: " + deviceMasterEntity.getDeviceId());
//	        ZoneVendorProjection data =
//	                deviceZoneDetailsRepository
//	                        .findZoneDesiredAndVendorsByDeviceIdAndInputType(
//	                                deviceMasterEntity.getDeviceId(),
//	                                baseTicket.getGroupName()
//	                        );
//
//	        String zoneDesiredName = data != null ? data.getZoneDesiredName() : null;
//	        String vendors = data != null ? data.getVendors() : null;
//	        Ticket latestTicket = ticketGroup.stream()
//	                .filter(t -> t.getUpdateAt() != null)
//	                .max(Comparator.comparing(Ticket::getUpdateAt))
//	                .orElse(baseTicket);
//
//	        TicketDataDTO dto = new TicketDataDTO();
//
//	        String panelSerialNo = baseTicket.getDeviceSerialNo();
//	        DeviceMasterEntity device = deviceMap.get(panelSerialNo);
//	        if (device == null) continue;
//
//	        BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
//
//	        dto.setDeviceSerialNo(device.getDeviceSerialNo());
//	        dto.setDeviceName(device.getDeviceName());
//	        dto.setDeviceType(device.getDeviceType());
//	        dto.setDeviceLocation(device.getDeviceLocation());
//	        dto.setMobileNo(device.getPhoneNumber());
//	        dto.setProduct(zoneDesiredName);
//	        dto.setVendorName(vendors);
//	        if (branch != null) {
//	            dto.setBranchName(branch.getBranchDesc());
//	            dto.setBankName(branch.getBank());
//	            dto.setBranchContactNo(branch.getMobile());
//	        }
//
//	        dto.setTicketId(baseTicket.getTicketNo());
//	        dto.setTicketStatus(baseTicket.getTicketStatus());
//	        dto.setTicketTime(baseTicket.getCreateAt() != null ? baseTicket.getCreateAt().toString() : null);
//	        dto.setTicketResolvedTime(latestTicket.getUpdateAt() != null ? latestTicket.getUpdateAt().toString() : null);
//
//	        if (baseTicket.getCreateAt() != null && latestTicket.getUpdateAt() != null) {
//	            Duration duration = Duration.between(baseTicket.getCreateAt(), latestTicket.getUpdateAt());
//	            long hours = duration.toHours();
//	            long minutes = duration.toMinutes() % 60;
//	            dto.setTimeDifference(hours + "h " + minutes + "m");
//
//	            EscalationLog escalationLog = escalationMailLogRepository.findTopEscalationByTicketId(baseTicket.getTicketNo());
//	            if (escalationLog != null) {
//	                dto.setEscalationLevel(escalationLog.getEscLevel());
//	                if (escalationLog.getEscLevel() != null) {
//	                    ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("TICKET", escalationLog.getEscLevel());
//	                    if (paramEntity != null) {
//	                        dto.setEscalationColor(paramEntity.getDescp2());
//	                    }
//	                }
//	            }
//	        }
//
//	        ticketReportList.add(dto);
//	    }
//
//	    // ✅ Latest ticket upar
//	    ticketReportList.sort((a, b) -> {
//	        if (a.getTicketTime() == null) return 1;
//	        if (b.getTicketTime() == null) return -1;
//	        return b.getTicketTime().compareTo(a.getTicketTime());
//	    });
//
//	    response.setStatusCode(HttpStatus.OK.value());
//	    response.setPayload(ticketReportList);
//	    response.setMessage("Data fetched successfully");
//	    return response;
//	}
//	

	/*
	 * public ResponseEntity getTicketReport(String branchCode, String
	 * systemIntegrator, String product, String status, String fromDate, String
	 * toDate, String ticketNo, int page, int size) {
	 * 
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * // Step 1: Parse dates — default last 30 days LocalDateTime fromDateTime;
	 * LocalDateTime toDateTime; try { fromDateTime = (fromDate != null &&
	 * !fromDate.isEmpty()) ? LocalDate.parse(fromDate).atStartOfDay() :
	 * LocalDateTime.now().minusDays(30);
	 * 
	 * toDateTime = (toDate != null && !toDate.isEmpty()) ?
	 * LocalDate.parse(toDate).atTime(23, 59, 59) : LocalDateTime.now(); } catch
	 * (Exception e) { response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	 * response.setMessage("Invalid date format. Use yyyy-MM-dd"); return response;
	 * }
	 * 
	 * // Step 2: Branch codes ResponseEntity branchServiceResponse =
	 * branchService.getAllChildren(branchCode); List<BranchMasterEntity>
	 * branchCodeList = (List<BranchMasterEntity>)
	 * branchServiceResponse.getPayload(); if (branchCodeList == null ||
	 * branchCodeList.isEmpty()) return createEmptyResponse(response);
	 * 
	 * List<String> branchCodes =
	 * branchCodeList.stream().map(BranchMasterEntity::getBranchCode)
	 * .collect(Collectors.toList());
	 * 
	 * // Step 3: Devices List<DeviceMasterEntity> allDevices =
	 * deviceMasterRepository.findByBranchCodeIn(branchCodes); if (allDevices ==
	 * null || allDevices.isEmpty()) return createEmptyResponse(response);
	 * 
	 * // Step 4: Lookup maps Map<String, BranchMasterEntity> branchCodeMap =
	 * branchCodeList.stream()
	 * .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	 * Function.identity(), (a, b) -> a));
	 * 
	 * Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo,
	 * Function.identity(), (a, b) -> a));
	 * 
	 * List<String> deviceSerialNos =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
	 * .collect(Collectors.toList());
	 * 
	 * List<String> deviceIds =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceId).filter(Objects::
	 * nonNull) .collect(Collectors.toList());
	 * 
	 * // Step 5: Paginated distinct ticketNos first, then fetch all their rows
	 * Pageable pageable = PageRequest.of(page, size); Page<String> ticketNoPage =
	 * ticketDetailsRepository.findDistinctTicketNosByFilters(deviceSerialNos,
	 * (ticketNo != null && !ticketNo.isEmpty()) ? ticketNo : null, (status != null
	 * && !status.isEmpty()) ? status : null, fromDateTime, toDateTime, pageable);
	 * 
	 * List<String> pagedTicketNos = ticketNoPage.getContent(); if
	 * (pagedTicketNos.isEmpty()) return createEmptyResponse(response);
	 * 
	 * List<Ticket> allTickets =
	 * ticketDetailsRepository.findTicketsByTicketNos(pagedTicketNos); if
	 * (allTickets.isEmpty()) return createEmptyResponse(response);
	 * 
	 * // Step 6: Group by ticketNo Map<String, List<Ticket>> ticketGroupMap =
	 * allTickets.stream() .collect(Collectors.groupingBy(Ticket::getTicketNo));
	 * 
	 * // Step 7: Collect all alertGeneratedIds and alertResolvedIds (as Integer)
	 * List<Integer> allAlertGeneratedIds = new ArrayList<>(); List<Integer>
	 * allAlertResolvedIds = new ArrayList<>();
	 * 
	 * for (List<Ticket> tickets : ticketGroupMap.values()) { for (Ticket ticket :
	 * tickets) { if (ticket.getAlertGeneratedId() != null) {
	 * allAlertGeneratedIds.add(ticket.getAlertGeneratedId().intValue()); } if
	 * (ticket.getResolvedAlertId() != null) {
	 * allAlertResolvedIds.add(ticket.getResolvedAlertId().intValue()); } } }
	 * 
	 * // Step 8: BATCH - Fetch SmsDetailsEntity for alertGeneratedIds Map<Integer,
	 * SmsDetailsEntity> smsDetailsGeneratedMap = new HashMap<>(); if
	 * (!allAlertGeneratedIds.isEmpty()) { try { List<SmsDetailsEntity>
	 * smsDetailsList = smsDetailsRepository.findAllById(allAlertGeneratedIds); for
	 * (SmsDetailsEntity sms : smsDetailsList) {
	 * smsDetailsGeneratedMap.put(sms.getId(), sms); } } catch (Exception e) {
	 * System.err.println("SmsDetailsEntity fetch error for generated IDs: " +
	 * e.getMessage()); } }
	 * 
	 * // Step 9: BATCH - Fetch SmsDetailsHistoryEntity for alertGeneratedIds
	 * Map<Integer, SmsDetailsHistoryEntity> historyGeneratedMap = new HashMap<>();
	 * Set<Integer> generatedIdsNotFoundInSms = new HashSet<>(); for (Integer id :
	 * allAlertGeneratedIds) { if (!smsDetailsGeneratedMap.containsKey(id)) {
	 * generatedIdsNotFoundInSms.add(id); } }
	 * 
	 * if (!generatedIdsNotFoundInSms.isEmpty()) { try {
	 * List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
	 * .findByOriginalIdIn(new ArrayList<>(generatedIdsNotFoundInSms)); for
	 * (SmsDetailsHistoryEntity history : historyList) {
	 * historyGeneratedMap.put(history.getOriginalId(), history); } } catch
	 * (Exception e) {
	 * System.err.println("SmsDetailsHistoryEntity fetch error for generated IDs: "
	 * + e.getMessage()); } }
	 * 
	 * // Step 10: BATCH - Fetch SmsDetailsEntity for alertResolvedIds Map<Integer,
	 * SmsDetailsEntity> smsDetailsResolvedMap = new HashMap<>(); if
	 * (!allAlertResolvedIds.isEmpty()) { try { List<SmsDetailsEntity>
	 * smsDetailsList = smsDetailsRepository.findAllById(allAlertResolvedIds); for
	 * (SmsDetailsEntity sms : smsDetailsList) {
	 * smsDetailsResolvedMap.put(sms.getId(), sms); } } catch (Exception e) {
	 * System.err.println("SmsDetailsEntity fetch error for resolved IDs: " +
	 * e.getMessage()); } }
	 * 
	 * // Step 11: BATCH - Fetch SmsDetailsHistoryEntity for alertResolvedIds
	 * Map<Integer, SmsDetailsHistoryEntity> historyResolvedMap = new HashMap<>();
	 * Set<Integer> resolvedIdsNotFoundInSms = new HashSet<>(); for (Integer id :
	 * allAlertResolvedIds) { if (!smsDetailsResolvedMap.containsKey(id)) {
	 * resolvedIdsNotFoundInSms.add(id); } }
	 * 
	 * if (!resolvedIdsNotFoundInSms.isEmpty()) { try {
	 * List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
	 * .findByOriginalIdIn(new ArrayList<>(resolvedIdsNotFoundInSms)); for
	 * (SmsDetailsHistoryEntity history : historyList) {
	 * historyResolvedMap.put(history.getOriginalId(), history); } } catch
	 * (Exception e) {
	 * System.err.println("SmsDetailsHistoryEntity fetch error for resolved IDs: " +
	 * e.getMessage()); } }
	 * 
	 * // Step 12: Base + latest ticket per group with new time calculation logic
	 * Map<String, Ticket> baseTicketMap = new HashMap<>(); Map<String, Ticket>
	 * latestTicketMap = new HashMap<>();
	 * 
	 * // Store calculated times per ticket group - USING Date type Map<String,
	 * Date> calculatedTicketTimeMap = new HashMap<>(); Map<String, Date>
	 * calculatedAlertResolvedTimeMap = new HashMap<>();
	 * 
	 * for (Map.Entry<String, List<Ticket>> entry : ticketGroupMap.entrySet()) {
	 * String key = entry.getKey(); List<Ticket> group = entry.getValue();
	 * 
	 * // Calculate oldest time from all alertGeneratedIds in this ticket group Date
	 * oldestGeneratedTime = null; for (Ticket ticket : group) { if
	 * (ticket.getAlertGeneratedId() != null) { Integer genId =
	 * ticket.getAlertGeneratedId().intValue();
	 * 
	 * // Check in SmsDetailsEntity first SmsDetailsEntity smsDetails =
	 * smsDetailsGeneratedMap.get(genId); if (smsDetails != null &&
	 * smsDetails.getDateTime() != null) { if (oldestGeneratedTime == null ||
	 * smsDetails.getDateTime().before(oldestGeneratedTime)) { oldestGeneratedTime =
	 * smsDetails.getDateTime(); } } else { // Fallback: check in
	 * SmsDetailsHistoryEntity SmsDetailsHistoryEntity history =
	 * historyGeneratedMap.get(genId); if (history != null && history.getDateTime()
	 * != null) { if (oldestGeneratedTime == null ||
	 * history.getDateTime().before(oldestGeneratedTime)) { oldestGeneratedTime =
	 * history.getDateTime(); } } } } } calculatedTicketTimeMap.put(key,
	 * oldestGeneratedTime);
	 * 
	 * // Calculate latest time from all alertResolvedIds if status is "C" Date
	 * latestResolvedTime = null; boolean hasStatusC = group.stream().anyMatch(t ->
	 * "C".equalsIgnoreCase(t.getTicketStatus()));
	 * 
	 * if (hasStatusC) { for (Ticket ticket : group) { if
	 * (ticket.getResolvedAlertId() != null) { Integer resId =
	 * ticket.getResolvedAlertId().intValue();
	 * 
	 * // Check in SmsDetailsEntity first SmsDetailsEntity smsDetails =
	 * smsDetailsResolvedMap.get(resId); if (smsDetails != null &&
	 * smsDetails.getDateTime() != null) { if (latestResolvedTime == null ||
	 * smsDetails.getDateTime().after(latestResolvedTime)) { latestResolvedTime =
	 * smsDetails.getDateTime(); } } else { // Fallback: check in
	 * SmsDetailsHistoryEntity SmsDetailsHistoryEntity history =
	 * historyResolvedMap.get(resId); if (history != null && history.getDateTime()
	 * != null) { if (latestResolvedTime == null ||
	 * history.getDateTime().after(latestResolvedTime)) { latestResolvedTime =
	 * history.getDateTime(); } } } } } } calculatedAlertResolvedTimeMap.put(key,
	 * latestResolvedTime);
	 * 
	 * // Get base and latest tickets for other fields Ticket base =
	 * group.stream().filter(t -> t.getCreateAt() != null)
	 * .min(Comparator.comparing(Ticket::getCreateAt)).orElse(group.get(0));
	 * 
	 * Ticket latest = group.stream().filter(t -> t.getUpdateAt() != null)
	 * .max(Comparator.comparing(Ticket::getUpdateAt)).orElse(base);
	 * 
	 * baseTicketMap.put(key, base); latestTicketMap.put(key, latest); }
	 * 
	 * List<String> allTicketIds = new ArrayList<>(baseTicketMap.keySet());
	 * 
	 * // Step 13: BATCH — Zone data Map<String, ZoneVendorProjection> zoneMap = new
	 * HashMap<>(); if (!deviceIds.isEmpty()) { try {
	 * deviceZoneDetailsRepository.findZoneDataByDeviceIds(deviceIds).forEach(z -> {
	 * if (z.getDeviceId() != null && z.getInputType() != null) {
	 * zoneMap.put(z.getDeviceId() + "::" + z.getInputType(), z); } }); } catch
	 * (Exception e) { System.err.println("Zone data fetch error: " +
	 * e.getMessage()); } }
	 * 
	 * // Step 14: BATCH — Escalation logs Map<String, EscalationLog> escalationMap
	 * = new HashMap<>(); if (!allTicketIds.isEmpty()) {
	 * System.out.println("Fetching escalation logs for ticket IDs: " +
	 * allTicketIds); try {
	 * escalationMailLogRepository.findTopEscalationsByTicketIds(allTicketIds).
	 * forEach(e -> { if (e.getTicketNo() != null) {
	 * escalationMap.put(e.getTicketNo(), e); } }); } catch (Exception e) {
	 * System.err.println("Escalation fetch error: " + e.getMessage()); } }
	 * System.out.println("Total escalation logs fetched: " +
	 * escalationMap.values()); // Step 15: BATCH — Param entities Map<String,
	 * ParamEntity> paramMap = new HashMap<>(); List<String> escLevels =
	 * escalationMap.values().stream().map(EscalationLog::getEscLevel)
	 * .filter(Objects::nonNull).distinct().collect(Collectors.toList());
	 * System.out.println("Unique escalation levels to fetch params for: " +
	 * escLevels); if (!escLevels.isEmpty()) {
	 * System.out.println("Fetching param entities for escalation levels: " +
	 * escLevels); try { paramRepository.getParamsByCodeAndSerials("TICKET",
	 * escLevels).forEach(p -> { if (p.getSerial() != null) {
	 * paramMap.put(p.getSerial(), p); } }); } catch (Exception e) {
	 * System.err.println("Param fetch error: " + e.getMessage()); } }
	 * System.out.println("Total param entities fetched: " + paramMap.values()); //
	 * Step 16: Build response List<TicketDataDTO> ticketReportList = new
	 * ArrayList<>();
	 * 
	 * for (Map.Entry<String, Ticket> entry : baseTicketMap.entrySet()) { String tNo
	 * = entry.getKey(); Ticket baseTicket = entry.getValue(); Ticket latestTicket =
	 * latestTicketMap.getOrDefault(tNo, baseTicket);
	 * 
	 * String currentStatus = latestTicket.getTicketStatus();
	 * 
	 * String panelSerialNo = baseTicket.getDeviceSerialNo(); DeviceMasterEntity
	 * device = deviceMap.get(panelSerialNo); if (device == null) continue;
	 * 
	 * BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
	 * 
	 * ZoneVendorProjection zoneData = null; if (device.getDeviceId() != null &&
	 * baseTicket.getGroupName() != null) { zoneData =
	 * zoneMap.get(device.getDeviceId() + "::" + baseTicket.getGroupName()); }
	 * 
	 * TicketDataDTO dto = new TicketDataDTO();
	 * dto.setDeviceSerialNo(device.getDeviceSerialNo());
	 * dto.setDeviceName(device.getDeviceName());
	 * dto.setDeviceType(device.getDeviceType());
	 * dto.setDeviceLocation(device.getDeviceLocation());
	 * dto.setMobileNo(device.getPhoneNumber()); dto.setProduct(zoneData != null ?
	 * zoneData.getZoneDesiredName() : null); dto.setVendorName(zoneData != null ?
	 * zoneData.getVendors() : null);
	 * 
	 * if (branch != null) { dto.setBranchName(branch.getBranchDesc());
	 * dto.setBankName(branch.getBank());
	 * dto.setBranchContactNo(branch.getMobile()); }
	 * 
	 * dto.setTicketId(tNo); dto.setTicketStatus(currentStatus);
	 * 
	 * // Ticket time Date calculatedTicketTime = calculatedTicketTimeMap.get(tNo);
	 * dto.setTicketTime(calculatedTicketTime != null ?
	 * calculatedTicketTime.toString() : (baseTicket.getCreateAt() != null ?
	 * baseTicket.getCreateAt().toString() : null));
	 * 
	 * Date calculatedResolvedTime = calculatedAlertResolvedTimeMap.get(tNo); if
	 * (currentStatus != null && currentStatus.equalsIgnoreCase("C")) {
	 * dto.setTicketResolvedTime(calculatedResolvedTime != null ?
	 * calculatedResolvedTime.toString() : (latestTicket.getUpdateAt() != null ?
	 * latestTicket.getUpdateAt().toString() : null)); } else {
	 * dto.setTicketResolvedTime(null); }
	 * 
	 * if (calculatedTicketTime != null && calculatedResolvedTime != null) { long
	 * diffInMillis = calculatedResolvedTime.getTime() -
	 * calculatedTicketTime.getTime(); long diffInHours = diffInMillis / (60 * 60 *
	 * 1000); long diffInMinutes = (diffInMillis / (60 * 1000)) % 60;
	 * dto.setTimeDifference(diffInHours + "h " + diffInMinutes + "m");
	 * 
	 * } else if (calculatedTicketTime != null && currentStatus != null &&
	 * !currentStatus.equalsIgnoreCase("C")) { long diffInMillis = new
	 * Date().getTime() - calculatedTicketTime.getTime(); long diffInHours =
	 * diffInMillis / (60 * 60 * 1000); long diffInMinutes = (diffInMillis / (60 *
	 * 1000)) % 60; dto.setTimeDifference(diffInHours + "h " + diffInMinutes +
	 * "m (Open)"); }
	 * 
	 * EscalationLog escalationLog = escalationMap.get(tNo);
	 * System.out.println("EscalationLog for " + tNo + ": " + escalationLog); if
	 * (escalationLog != null) {
	 * dto.setEscalationLevel(escalationLog.getEscLevel()); if
	 * (escalationLog.getEscLevel() != null) { ParamEntity paramEntity =
	 * paramMap.get(escalationLog.getEscLevel());
	 * System.out.println("ParamEntity for level " + escalationLog.getEscLevel() +
	 * ": " + paramEntity); // debug if (paramEntity != null) {
	 * dto.setEscalationColor(paramEntity.getDescp2()); } } }
	 * 
	 * ticketReportList.add(dto); }
	 * 
	 * // Step 17: Sort ticketReportList.sort((a, b) -> { String statusA =
	 * a.getTicketStatus(); String statusB = b.getTicketStatus();
	 * 
	 * // Open tickets first if (!Objects.equals(statusA, statusB)) { if
	 * ("O".equalsIgnoreCase(statusA)) return -1; if ("O".equalsIgnoreCase(statusB))
	 * return 1; }
	 * 
	 * // Then sort by latest ticket time if (a.getTicketTime() == null) return 1;
	 * if (b.getTicketTime() == null) return -1;
	 * 
	 * return b.getTicketTime().compareTo(a.getTicketTime()); });
	 * 
	 * // Step 18: Pagination metadata Map<String, Object> paginatedResponse = new
	 * HashMap<>(); paginatedResponse.put("data", ticketReportList);
	 * paginatedResponse.put("currentPage", ticketNoPage.getNumber());
	 * paginatedResponse.put("totalPages", ticketNoPage.getTotalPages());
	 * paginatedResponse.put("totalRecords", ticketNoPage.getTotalElements());
	 * paginatedResponse.put("pageSize", ticketNoPage.getSize());
	 * paginatedResponse.put("hasNext", ticketNoPage.hasNext());
	 * paginatedResponse.put("hasPrevious", ticketNoPage.hasPrevious());
	 * 
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(paginatedResponse);
	 * response.setMessage("Data fetched successfully"); return response; }
	 */
	
	
	
	//30 sep 2026
	/*
	 * public ResponseEntity getTicketReport(String branchCode, String status,
	 * String fromDate, String toDate, String ticketNo, int page, int size) {
	 * 
	 * long startTime = System.currentTimeMillis(); ResponseEntity response = new
	 * ResponseEntity();
	 * 
	 * // Step 1: Parse dates LocalDateTime fromDateTime; LocalDateTime toDateTime;
	 * try { fromDateTime = (fromDate != null && !fromDate.isEmpty()) ?
	 * LocalDate.parse(fromDate).atStartOfDay() : LocalDateTime.now().minusDays(30);
	 * toDateTime = (toDate != null && !toDate.isEmpty()) ?
	 * LocalDate.parse(toDate).atTime(23, 59, 59) : LocalDateTime.now(); } catch
	 * (Exception e) { response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	 * response.setMessage("Invalid date format. Use yyyy-MM-dd"); return response;
	 * }
	 * 
	 * // Status validation String normalizedStatus = null; if (status != null &&
	 * !status.isEmpty()) { normalizedStatus = status.toUpperCase(); if
	 * (!normalizedStatus.equals("O") && !normalizedStatus.equals("C")) {
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	 * response.setMessage("Invalid status. Use 'O' for Open or 'C' for Closed");
	 * return response; } }
	 * 
	 * // Step 2: Branch codes ResponseEntity branchServiceResponse =
	 * branchService.getAllChildren(branchCode); List<BranchMasterEntity>
	 * branchCodeList = (List<BranchMasterEntity>)
	 * branchServiceResponse.getPayload(); if (branchCodeList == null ||
	 * branchCodeList.isEmpty()) return createEmptyResponse(response);
	 * 
	 * List<String> branchCodes =
	 * branchCodeList.stream().map(BranchMasterEntity::getBranchCode)
	 * .collect(Collectors.toList());
	 * 
	 * // Step 3: Devices List<DeviceMasterEntity> allDevices =
	 * deviceMasterRepository.findByBranchCodeIn(branchCodes); if (allDevices ==
	 * null || allDevices.isEmpty()) return createEmptyResponse(response);
	 * 
	 * // Step 4: Lookup maps (optimized) Map<String, BranchMasterEntity>
	 * branchCodeMap = branchCodeList.stream()
	 * .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	 * Function.identity(), (a, b) -> a));
	 * 
	 * Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo,
	 * Function.identity(), (a, b) -> a));
	 * 
	 * List<String> deviceSerialNos =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
	 * .collect(Collectors.toList());
	 * 
	 * List<String> deviceIds =
	 * allDevices.stream().map(DeviceMasterEntity::getDeviceId).filter(Objects::
	 * nonNull) .collect(Collectors.toList());
	 * 
	 * // Step 5: Paginated distinct ticketNos Pageable pageable =
	 * PageRequest.of(page, size); Page<String> ticketNoPage; String searchTicketNo
	 * = (ticketNo != null && !ticketNo.isEmpty()) ? ticketNo : null;
	 * 
	 * if ("O".equals(normalizedStatus)) { ticketNoPage =
	 * ticketDetailsRepository.findDistinctOpenTicketNos( deviceSerialNos,
	 * searchTicketNo, fromDateTime, toDateTime, pageable); } else if
	 * ("C".equals(normalizedStatus)) { ticketNoPage =
	 * ticketDetailsRepository.findDistinctClosedTicketNos( deviceSerialNos,
	 * searchTicketNo, fromDateTime, toDateTime, pageable); } else { ticketNoPage =
	 * ticketDetailsRepository.findDistinctTicketNosByFilters( deviceSerialNos,
	 * searchTicketNo, fromDateTime, toDateTime, pageable); }
	 * 
	 * List<String> pagedTicketNos = ticketNoPage.getContent(); if
	 * (pagedTicketNos.isEmpty()) return createEmptyResponse(response);
	 * 
	 * List<Ticket> allTickets =
	 * ticketDetailsRepository.findTicketsByTicketNos(pagedTicketNos); if
	 * (allTickets.isEmpty()) return createEmptyResponse(response);
	 * 
	 * // Step 6: Group by ticketNo Map<String, List<Ticket>> ticketGroupMap =
	 * allTickets.stream() .collect(Collectors.groupingBy(Ticket::getTicketNo));
	 * 
	 * // Step 7: Collect all alert IDs Set<Integer> allAlertGeneratedIds = new
	 * HashSet<>(); Set<Integer> allAlertResolvedIds = new HashSet<>();
	 * 
	 * for (List<Ticket> tickets : ticketGroupMap.values()) { for (Ticket ticket :
	 * tickets) { if (ticket.getAlertGeneratedId() != null) {
	 * allAlertGeneratedIds.add(ticket.getAlertGeneratedId().intValue()); } if
	 * (ticket.getResolvedAlertId() != null) {
	 * allAlertResolvedIds.add(ticket.getResolvedAlertId().intValue()); } } }
	 * 
	 * // Step 8-11: Fetch SMS details (batch optimized) Map<Integer,
	 * SmsDetailsEntity> smsDetailsGeneratedMap = new HashMap<>(); Map<Integer,
	 * SmsDetailsHistoryEntity> historyGeneratedMap = new HashMap<>(); Map<Integer,
	 * SmsDetailsEntity> smsDetailsResolvedMap = new HashMap<>(); Map<Integer,
	 * SmsDetailsHistoryEntity> historyResolvedMap = new HashMap<>();
	 * 
	 * // Generated IDs if (!allAlertGeneratedIds.isEmpty()) {
	 * List<SmsDetailsEntity> smsDetailsList =
	 * smsDetailsRepository.findAllById(allAlertGeneratedIds);
	 * smsDetailsList.forEach(sms -> smsDetailsGeneratedMap.put(sms.getId(), sms));
	 * 
	 * Set<Integer> notFound = new HashSet<>(allAlertGeneratedIds);
	 * notFound.removeAll(smsDetailsGeneratedMap.keySet());
	 * 
	 * if (!notFound.isEmpty()) { List<SmsDetailsHistoryEntity> historyList =
	 * smsDetailsHistoryRepository .findByOriginalIdIn(new ArrayList<>(notFound));
	 * historyList.forEach(h -> historyGeneratedMap.put(h.getOriginalId(), h)); } }
	 * 
	 * // Resolved IDs if (!allAlertResolvedIds.isEmpty()) { List<SmsDetailsEntity>
	 * smsDetailsList = smsDetailsRepository.findAllById(allAlertResolvedIds);
	 * smsDetailsList.forEach(sms -> smsDetailsResolvedMap.put(sms.getId(), sms));
	 * 
	 * Set<Integer> notFound = new HashSet<>(allAlertResolvedIds);
	 * notFound.removeAll(smsDetailsResolvedMap.keySet());
	 * 
	 * if (!notFound.isEmpty()) { List<SmsDetailsHistoryEntity> historyList =
	 * smsDetailsHistoryRepository .findByOriginalIdIn(new ArrayList<>(notFound));
	 * historyList.forEach(h -> historyResolvedMap.put(h.getOriginalId(), h)); } }
	 * 
	 * // Step 12: Calculate times Map<String, Ticket> baseTicketMap = new
	 * HashMap<>(); Map<String, Ticket> latestTicketMap = new HashMap<>();
	 * Map<String, Date> calculatedTicketTimeMap = new HashMap<>(); Map<String,
	 * Date> calculatedAlertResolvedTimeMap = new HashMap<>();
	 * 
	 * for (Map.Entry<String, List<Ticket>> entry : ticketGroupMap.entrySet()) {
	 * String key = entry.getKey(); List<Ticket> group = entry.getValue();
	 * 
	 * // Calculate oldest generated time Date oldestGeneratedTime = null; for
	 * (Ticket ticket : group) { if (ticket.getAlertGeneratedId() != null) { Integer
	 * genId = ticket.getAlertGeneratedId().intValue(); SmsDetailsEntity smsDetails
	 * = smsDetailsGeneratedMap.get(genId); Date dateTime = null;
	 * 
	 * if (smsDetails != null) { dateTime = smsDetails.getDateTime(); } else {
	 * SmsDetailsHistoryEntity history = historyGeneratedMap.get(genId); if (history
	 * != null) dateTime = history.getDateTime(); }
	 * 
	 * if (dateTime != null && (oldestGeneratedTime == null ||
	 * dateTime.before(oldestGeneratedTime))) { oldestGeneratedTime = dateTime; } }
	 * } calculatedTicketTimeMap.put(key, oldestGeneratedTime);
	 * 
	 * // Calculate latest resolved time Date latestResolvedTime = null; boolean
	 * hasStatusC = group.stream().anyMatch(t ->
	 * "C".equalsIgnoreCase(t.getTicketStatus()));
	 * 
	 * if (hasStatusC) { for (Ticket ticket : group) { if
	 * (ticket.getResolvedAlertId() != null) { Integer resId =
	 * ticket.getResolvedAlertId().intValue(); SmsDetailsEntity smsDetails =
	 * smsDetailsResolvedMap.get(resId); Date dateTime = null;
	 * 
	 * if (smsDetails != null) { dateTime = smsDetails.getDateTime(); } else {
	 * SmsDetailsHistoryEntity history = historyResolvedMap.get(resId); if (history
	 * != null) dateTime = history.getDateTime(); }
	 * 
	 * if (dateTime != null && (latestResolvedTime == null ||
	 * dateTime.after(latestResolvedTime))) { latestResolvedTime = dateTime; } } } }
	 * calculatedAlertResolvedTimeMap.put(key, latestResolvedTime);
	 * 
	 * // Get base and latest tickets Ticket base = group.stream().filter(t ->
	 * t.getCreateAt() != null)
	 * .min(Comparator.comparing(Ticket::getCreateAt)).orElse(group.get(0)); Ticket
	 * latest = group.stream().filter(t -> t.getUpdateAt() != null)
	 * .max(Comparator.comparing(Ticket::getUpdateAt)).orElse(base);
	 * 
	 * baseTicketMap.put(key, base); latestTicketMap.put(key, latest); }
	 * 
	 * List<String> allTicketIds = new ArrayList<>(baseTicketMap.keySet());
	 * 
	 * // Step 13: Zone data (batch) Map<String, ZoneVendorProjection> zoneMap = new
	 * HashMap<>(); if (!deviceIds.isEmpty()) {
	 * deviceZoneDetailsRepository.findZoneDataByDeviceIds(deviceIds).forEach(z -> {
	 * if (z.getDeviceId() != null && z.getInputType() != null) {
	 * zoneMap.put(z.getDeviceId() + "::" + z.getInputType(), z); } }); }
	 * 
	 * // Step 14: Escalation logs (batch) Map<String, EscalationLog> escalationMap
	 * = new HashMap<>(); if (!allTicketIds.isEmpty()) {
	 * escalationMailLogRepository.findTopEscalationsByTicketIds(allTicketIds).
	 * forEach(e -> { if (e.getTicketNo() != null) {
	 * escalationMap.put(e.getTicketNo(), e); } }); }
	 * 
	 * // Step 15: Param entities (batch) Map<String, ParamEntity> paramMap = new
	 * HashMap<>(); List<String> escLevels =
	 * escalationMap.values().stream().map(EscalationLog::getEscLevel)
	 * .filter(Objects::nonNull).distinct().collect(Collectors.toList());
	 * 
	 * if (!escLevels.isEmpty()) {
	 * paramRepository.getParamsByCodeAndSerials("TICKET", escLevels).forEach(p -> {
	 * if (p.getSerial() != null) { paramMap.put(p.getSerial(), p); } }); }
	 * 
	 * // Step 16: Build response List<TicketDataDTO> ticketReportList = new
	 * ArrayList<>();
	 * 
	 * for (Map.Entry<String, Ticket> entry : baseTicketMap.entrySet()) { String tNo
	 * = entry.getKey(); Ticket baseTicket = entry.getValue(); Ticket latestTicket =
	 * latestTicketMap.getOrDefault(tNo, baseTicket); String currentStatus =
	 * latestTicket.getTicketStatus();
	 * 
	 * // Status filter if ("O".equals(normalizedStatus) &&
	 * "C".equalsIgnoreCase(currentStatus)) continue; if
	 * ("C".equals(normalizedStatus) && !"C".equalsIgnoreCase(currentStatus))
	 * continue;
	 * 
	 * String panelSerialNo = baseTicket.getDeviceSerialNo(); DeviceMasterEntity
	 * device = deviceMap.get(panelSerialNo); if (device == null) continue;
	 * 
	 * BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
	 * ZoneVendorProjection zoneData = null; if (device.getDeviceId() != null &&
	 * baseTicket.getGroupName() != null) { zoneData =
	 * zoneMap.get(device.getDeviceId() + "::" + baseTicket.getGroupName()); }
	 * 
	 * TicketDataDTO dto = new TicketDataDTO();
	 * dto.setDeviceSerialNo(device.getDeviceSerialNo());
	 * dto.setDeviceName(device.getDeviceName());
	 * dto.setDeviceType(device.getDeviceType());
	 * dto.setDeviceLocation(device.getDeviceLocation());
	 * dto.setMobileNo(device.getPhoneNumber()); dto.setProduct(zoneData != null ?
	 * zoneData.getZoneDesiredName() : null); dto.setVendorName(zoneData != null ?
	 * zoneData.getVendors() : null);
	 * 
	 * if (branch != null) { dto.setBranchName(branch.getBranchDesc());
	 * dto.setBankName(branch.getBank());
	 * dto.setBranchContactNo(branch.getMobile()); }
	 * 
	 * dto.setTicketId(tNo); dto.setTicketStatus(currentStatus);
	 * 
	 * Date calculatedTicketTime = calculatedTicketTimeMap.get(tNo);
	 * dto.setTicketTime(calculatedTicketTime != null ?
	 * calculatedTicketTime.toString() : (baseTicket.getCreateAt() != null ?
	 * baseTicket.getCreateAt().toString() : null));
	 * 
	 * Date calculatedResolvedTime = calculatedAlertResolvedTimeMap.get(tNo); if
	 * (currentStatus != null && currentStatus.equalsIgnoreCase("C")) {
	 * dto.setTicketResolvedTime(calculatedResolvedTime != null ?
	 * calculatedResolvedTime.toString() : (latestTicket.getUpdateAt() != null ?
	 * latestTicket.getUpdateAt().toString() : null)); } else {
	 * dto.setTicketResolvedTime(null); }
	 * 
	 * if (calculatedTicketTime != null && calculatedResolvedTime != null) { long
	 * diffInMillis = calculatedResolvedTime.getTime() -
	 * calculatedTicketTime.getTime(); long diffInHours = diffInMillis / (60 * 60 *
	 * 1000); long diffInMinutes = (diffInMillis / (60 * 1000)) % 60;
	 * dto.setTimeDifference(diffInHours + "h " + diffInMinutes + "m"); } else if
	 * (calculatedTicketTime != null && currentStatus != null &&
	 * !currentStatus.equalsIgnoreCase("C")) { long diffInMillis =
	 * System.currentTimeMillis() - calculatedTicketTime.getTime(); long diffInHours
	 * = diffInMillis / (60 * 60 * 1000); long diffInMinutes = (diffInMillis / (60 *
	 * 1000)) % 60; dto.setTimeDifference(diffInHours + "h " + diffInMinutes +
	 * "m (Open)"); }
	 * 
	 * EscalationLog escalationLog = escalationMap.get(tNo); if (escalationLog !=
	 * null) { dto.setEscalationLevel(escalationLog.getEscLevel()); if
	 * (escalationLog.getEscLevel() != null) { ParamEntity paramEntity =
	 * paramMap.get(escalationLog.getEscLevel()); if (paramEntity != null) {
	 * dto.setEscalationColor(paramEntity.getDescp2()); } } }
	 * 
	 * ticketReportList.add(dto); }
	 * 
	 * // Step 17: Sort - Open pehle, phir time se ticketReportList.sort((a, b) -> {
	 * boolean aIsOpen = "O".equalsIgnoreCase(a.getTicketStatus()); boolean bIsOpen
	 * = "O".equalsIgnoreCase(b.getTicketStatus());
	 * 
	 * if (aIsOpen && !bIsOpen) return -1; if (!aIsOpen && bIsOpen) return 1;
	 * 
	 * if (a.getTicketTime() == null) return 1; if (b.getTicketTime() == null)
	 * return -1; return b.getTicketTime().compareTo(a.getTicketTime()); });
	 * 
	 * // Step 18: Pagination metadata Map<String, Object> paginatedResponse = new
	 * HashMap<>(); paginatedResponse.put("data", ticketReportList);
	 * paginatedResponse.put("currentPage", ticketNoPage.getNumber());
	 * paginatedResponse.put("totalPages", ticketNoPage.getTotalPages());
	 * paginatedResponse.put("totalRecords", ticketNoPage.getTotalElements());
	 * paginatedResponse.put("pageSize", ticketNoPage.getSize());
	 * paginatedResponse.put("hasNext", ticketNoPage.hasNext());
	 * paginatedResponse.put("hasPrevious", ticketNoPage.hasPrevious());
	 * 
	 * long endTime = System.currentTimeMillis();
	 * paginatedResponse.put("responseTimeMs", (endTime - startTime));
	 * 
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(paginatedResponse);
	 * response.setMessage("Data fetched successfully"); return response; }
	 */	
	
	public ResponseEntity getTicketReport(String branchCode, String status, String fromDate, String toDate,
	        String ticketNo, List<String> alertTypes, int page, int size) {

	    long startTime = System.currentTimeMillis();
	    ResponseEntity response = new ResponseEntity();

	    // Step 1: Parse dates
	    LocalDateTime fromDateTime;
	    LocalDateTime toDateTime;
	    try {
	        fromDateTime = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate).atStartOfDay()
	                : LocalDateTime.now().minusDays(30);
	        toDateTime = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate).atTime(23, 59, 59)
	                : LocalDateTime.now();
	    } catch (Exception e) {
	        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        response.setMessage("Invalid date format. Use yyyy-MM-dd");
	        return response;
	    }

	    // Status validation
	    String normalizedStatus = null;
	    if (status != null && !status.isEmpty()) {
	        normalizedStatus = status.toUpperCase();
	        if (!normalizedStatus.equals("O") && !normalizedStatus.equals("C")) {
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setMessage("Invalid status. Use 'O' for Open or 'C' for Closed");
	            return response;
	        }
	    }

	    // alertTypes normalize: empty ho to "__ALL__" (matlab koi filter nahi)
	    alertTypes = (alertTypes == null) ? new ArrayList<>()
	            : alertTypes.stream().filter(a -> a != null && !a.trim().isEmpty()).map(String::trim)
	                    .collect(Collectors.toList());
	    if (alertTypes.isEmpty()) {
	        alertTypes.add("__ALL__");
	    }

	    // Step 2: Branch codes
	    ResponseEntity branchServiceResponse = branchService.getAllChildren(branchCode);
	    List<BranchMasterEntity> branchCodeList = (List<BranchMasterEntity>) branchServiceResponse.getPayload();
	    if (branchCodeList == null || branchCodeList.isEmpty())
	        return createEmptyResponse(response);

	    List<String> branchCodes = branchCodeList.stream().map(BranchMasterEntity::getBranchCode)
	            .collect(Collectors.toList());

	    // Step 3: Devices
	    List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);
	    if (allDevices == null || allDevices.isEmpty())
	        return createEmptyResponse(response);

	    // Step 4: Lookup maps
	    Map<String, BranchMasterEntity> branchCodeMap = branchCodeList.stream()
	            .collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity(), (a, b) -> a));

	    Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
	            .collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, Function.identity(), (a, b) -> a));

	    List<String> deviceSerialNos = allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
	            .collect(Collectors.toList());

	    List<String> deviceIds = allDevices.stream().map(DeviceMasterEntity::getDeviceId).filter(Objects::nonNull)
	            .collect(Collectors.toList());

	    // Step 5: Paginated distinct ticketNos (alertTypes filter ke saath)
	    Pageable pageable = PageRequest.of(page, size);
	    Page<String> ticketNoPage;
	    String searchTicketNo = (ticketNo != null && !ticketNo.isEmpty()) ? ticketNo : null;

	    if ("O".equals(normalizedStatus)) {
	        ticketNoPage = ticketDetailsRepository.findDistinctOpenTicketNos(deviceSerialNos, searchTicketNo,
	                fromDateTime, toDateTime, alertTypes, pageable);
	    } else if ("C".equals(normalizedStatus)) {
	        ticketNoPage = ticketDetailsRepository.findDistinctClosedTicketNos(deviceSerialNos, searchTicketNo,
	                fromDateTime, toDateTime, alertTypes, pageable);
	    } else {
	        ticketNoPage = ticketDetailsRepository.findDistinctTicketNosByFilters(deviceSerialNos, searchTicketNo,
	                fromDateTime, toDateTime, alertTypes, pageable);
	    }

	    List<String> pagedTicketNos = ticketNoPage.getContent();
	    if (pagedTicketNos.isEmpty())
	        return createEmptyResponse(response);

	    List<Ticket> allTickets = ticketDetailsRepository.findTicketsByTicketNos(pagedTicketNos);
	    if (allTickets.isEmpty())
	        return createEmptyResponse(response);

	    // Step 6: Group by ticketNo
	    Map<String, List<Ticket>> ticketGroupMap = allTickets.stream()
	            .collect(Collectors.groupingBy(Ticket::getTicketNo));

	    // Step 7: Collect all alert IDs
	    Set<Integer> allAlertGeneratedIds = new HashSet<>();
	    Set<Integer> allAlertResolvedIds = new HashSet<>();

	    for (List<Ticket> tickets : ticketGroupMap.values()) {
	        for (Ticket ticket : tickets) {
	            if (ticket.getAlertGeneratedId() != null) {
	                allAlertGeneratedIds.add(ticket.getAlertGeneratedId().intValue());
	            }
	            if (ticket.getResolvedAlertId() != null) {
	                allAlertResolvedIds.add(ticket.getResolvedAlertId().intValue());
	            }
	        }
	    }

	    // Step 8-11: Fetch SMS details
	    Map<Integer, SmsDetailsEntity> smsDetailsGeneratedMap = new HashMap<>();
	    Map<Integer, SmsDetailsHistoryEntity> historyGeneratedMap = new HashMap<>();
	    Map<Integer, SmsDetailsEntity> smsDetailsResolvedMap = new HashMap<>();
	    Map<Integer, SmsDetailsHistoryEntity> historyResolvedMap = new HashMap<>();

	    if (!allAlertGeneratedIds.isEmpty()) {
	        List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository.findAllById(allAlertGeneratedIds);
	        smsDetailsList.forEach(sms -> smsDetailsGeneratedMap.put(sms.getId(), sms));

	        Set<Integer> notFound = new HashSet<>(allAlertGeneratedIds);
	        notFound.removeAll(smsDetailsGeneratedMap.keySet());

	        if (!notFound.isEmpty()) {
	            List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
	                    .findByOriginalIdIn(new ArrayList<>(notFound));
	            historyList.forEach(h -> historyGeneratedMap.put(h.getOriginalId(), h));
	        }
	    }

	    if (!allAlertResolvedIds.isEmpty()) {
	        List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository.findAllById(allAlertResolvedIds);
	        smsDetailsList.forEach(sms -> smsDetailsResolvedMap.put(sms.getId(), sms));

	        Set<Integer> notFound = new HashSet<>(allAlertResolvedIds);
	        notFound.removeAll(smsDetailsResolvedMap.keySet());

	        if (!notFound.isEmpty()) {
	            List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
	                    .findByOriginalIdIn(new ArrayList<>(notFound));
	            historyList.forEach(h -> historyResolvedMap.put(h.getOriginalId(), h));
	        }
	    }

	    // Step 12: Calculate times
	    Map<String, Ticket> baseTicketMap = new HashMap<>();
	    Map<String, Ticket> latestTicketMap = new HashMap<>();
	    Map<String, Date> calculatedTicketTimeMap = new HashMap<>();
	    Map<String, Date> calculatedAlertResolvedTimeMap = new HashMap<>();

	    for (Map.Entry<String, List<Ticket>> entry : ticketGroupMap.entrySet()) {
	        String key = entry.getKey();
	        List<Ticket> group = entry.getValue();

	        Date oldestGeneratedTime = null;
	        for (Ticket ticket : group) {
	            if (ticket.getAlertGeneratedId() != null) {
	                Integer genId = ticket.getAlertGeneratedId().intValue();
	                SmsDetailsEntity smsDetails = smsDetailsGeneratedMap.get(genId);
	                Date dateTime = null;

	                if (smsDetails != null) {
	                    dateTime = smsDetails.getDateTime();
	                } else {
	                    SmsDetailsHistoryEntity history = historyGeneratedMap.get(genId);
	                    if (history != null)
	                        dateTime = history.getDateTime();
	                }

	                if (dateTime != null && (oldestGeneratedTime == null || dateTime.before(oldestGeneratedTime))) {
	                    oldestGeneratedTime = dateTime;
	                }
	            }
	        }
	        calculatedTicketTimeMap.put(key, oldestGeneratedTime);

	        Date latestResolvedTime = null;
	        boolean hasStatusC = group.stream().anyMatch(t -> "C".equalsIgnoreCase(t.getTicketStatus()));

	        if (hasStatusC) {
	            for (Ticket ticket : group) {
	                if (ticket.getResolvedAlertId() != null) {
	                    Integer resId = ticket.getResolvedAlertId().intValue();
	                    SmsDetailsEntity smsDetails = smsDetailsResolvedMap.get(resId);
	                    Date dateTime = null;

	                    if (smsDetails != null) {
	                        dateTime = smsDetails.getDateTime();
	                    } else {
	                        SmsDetailsHistoryEntity history = historyResolvedMap.get(resId);
	                        if (history != null)
	                            dateTime = history.getDateTime();
	                    }

	                    if (dateTime != null && (latestResolvedTime == null || dateTime.after(latestResolvedTime))) {
	                        latestResolvedTime = dateTime;
	                    }
	                }
	            }
	        }
	        calculatedAlertResolvedTimeMap.put(key, latestResolvedTime);

	        Ticket base = group.stream().filter(t -> t.getCreateAt() != null)
	                .min(Comparator.comparing(Ticket::getCreateAt)).orElse(group.get(0));
	        Ticket latest = group.stream().filter(t -> t.getUpdateAt() != null)
	                .max(Comparator.comparing(Ticket::getUpdateAt)).orElse(base);

	        baseTicketMap.put(key, base);
	        latestTicketMap.put(key, latest);
	    }

	    List<String> allTicketIds = new ArrayList<>(baseTicketMap.keySet());

	    // Step 13: Zone data
	    Map<String, ZoneVendorProjection> zoneMap = new HashMap<>();
	    if (!deviceIds.isEmpty()) {
	        deviceZoneDetailsRepository.findZoneDataByDeviceIds(deviceIds).forEach(z -> {
	            if (z.getDeviceId() != null && z.getInputType() != null) {
	                zoneMap.put(z.getDeviceId() + "::" + z.getInputType(), z);
	            }
	        });
	    }

	    // Step 14: Escalation logs
	    Map<String, EscalationLog> escalationMap = new HashMap<>();
	    if (!allTicketIds.isEmpty()) {
	        escalationMailLogRepository.findTopEscalationsByTicketIds(allTicketIds).forEach(e -> {
	            if (e.getTicketNo() != null) {
	                escalationMap.put(e.getTicketNo(), e);
	            }
	        });
	    }

	    // Step 15: Param entities
	    Map<String, ParamEntity> paramMap = new HashMap<>();
	    List<String> escLevels = escalationMap.values().stream().map(EscalationLog::getEscLevel)
	            .filter(Objects::nonNull).distinct().collect(Collectors.toList());

	    if (!escLevels.isEmpty()) {
	        paramRepository.getParamsByCodeAndSerials("TICKET", escLevels).forEach(p -> {
	            if (p.getSerial() != null) {
	                paramMap.put(p.getSerial(), p);
	            }
	        });
	    }

	    // Step 16: Build response
	    List<TicketDataDTO> ticketReportList = new ArrayList<>();

	    for (Map.Entry<String, Ticket> entry : baseTicketMap.entrySet()) {
	        String tNo = entry.getKey();
	        Ticket baseTicket = entry.getValue();
	        Ticket latestTicket = latestTicketMap.getOrDefault(tNo, baseTicket);
	        String currentStatus = latestTicket.getTicketStatus();

	        if ("O".equals(normalizedStatus) && "C".equalsIgnoreCase(currentStatus))
	            continue;
	        if ("C".equals(normalizedStatus) && !"C".equalsIgnoreCase(currentStatus))
	            continue;

	        String panelSerialNo = baseTicket.getDeviceSerialNo();
	        DeviceMasterEntity device = deviceMap.get(panelSerialNo);
	        if (device == null)
	            continue;

	        BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
	        ZoneVendorProjection zoneData = null;
	        if (device.getDeviceId() != null && baseTicket.getGroupName() != null) {
	            zoneData = zoneMap.get(device.getDeviceId() + "::" + baseTicket.getGroupName());
	        }

	        TicketDataDTO dto = new TicketDataDTO();
	        dto.setDeviceSerialNo(device.getDeviceSerialNo());
	        dto.setDeviceName(device.getDeviceName());
	        dto.setDeviceType(device.getDeviceType());
	        dto.setDeviceLocation(device.getDeviceLocation());
	        dto.setMobileNo(device.getPhoneNumber());
	        dto.setProduct(zoneData != null ? zoneData.getZoneDesiredName() : null);
	        dto.setVendorName(zoneData != null ? zoneData.getVendors() : null);

	        if (branch != null) {
	            dto.setBranchName(branch.getBranchDesc());
	            dto.setBankName(branch.getBank());
	            dto.setBranchContactNo(branch.getMobile());
	        }

	        dto.setTicketId(tNo);
	        dto.setTicketStatus(currentStatus);

	        Date calculatedTicketTime = calculatedTicketTimeMap.get(tNo);
	        dto.setTicketTime(calculatedTicketTime != null ? calculatedTicketTime.toString()
	                : (baseTicket.getCreateAt() != null ? baseTicket.getCreateAt().toString() : null));

	        Date calculatedResolvedTime = calculatedAlertResolvedTimeMap.get(tNo);
	        if (currentStatus != null && currentStatus.equalsIgnoreCase("C")) {
	            dto.setTicketResolvedTime(calculatedResolvedTime != null ? calculatedResolvedTime.toString()
	                    : (latestTicket.getUpdateAt() != null ? latestTicket.getUpdateAt().toString() : null));
	        } else {
	            dto.setTicketResolvedTime(null);
	        }

	        if (calculatedTicketTime != null && calculatedResolvedTime != null) {
	            long diffInMillis = calculatedResolvedTime.getTime() - calculatedTicketTime.getTime();
	            long diffInHours = diffInMillis / (60 * 60 * 1000);
	            long diffInMinutes = (diffInMillis / (60 * 1000)) % 60;
	            dto.setTimeDifference(diffInHours + "h " + diffInMinutes + "m");
	        } else if (calculatedTicketTime != null && currentStatus != null && !currentStatus.equalsIgnoreCase("C")) {
	            long diffInMillis = System.currentTimeMillis() - calculatedTicketTime.getTime();
	            long diffInHours = diffInMillis / (60 * 60 * 1000);
	            long diffInMinutes = (diffInMillis / (60 * 1000)) % 60;
	            dto.setTimeDifference(diffInHours + "h " + diffInMinutes + "m (Open)");
	        }

	        EscalationLog escalationLog = escalationMap.get(tNo);
	        if (escalationLog != null) {
	            dto.setEscalationLevel(escalationLog.getEscLevel());
	            if (escalationLog.getEscLevel() != null) {
	                ParamEntity paramEntity = paramMap.get(escalationLog.getEscLevel());
	                if (paramEntity != null) {
	                    dto.setEscalationColor(paramEntity.getDescp2());
	                }
	            }
	        }

	        ticketReportList.add(dto);
	    }

	    // Step 17: Sort - Open pehle, phir time se
	    ticketReportList.sort((a, b) -> {
	        boolean aIsOpen = "O".equalsIgnoreCase(a.getTicketStatus());
	        boolean bIsOpen = "O".equalsIgnoreCase(b.getTicketStatus());

	        if (aIsOpen && !bIsOpen)
	            return -1;
	        if (!aIsOpen && bIsOpen)
	            return 1;

	        if (a.getTicketTime() == null)
	            return 1;
	        if (b.getTicketTime() == null)
	            return -1;
	        return b.getTicketTime().compareTo(a.getTicketTime());
	    });

	    // Step 18: Pagination metadata
	    Map<String, Object> paginatedResponse = new HashMap<>();
	    paginatedResponse.put("data", ticketReportList);
	    paginatedResponse.put("currentPage", ticketNoPage.getNumber());
	    paginatedResponse.put("totalPages", ticketNoPage.getTotalPages());
	    paginatedResponse.put("totalRecords", ticketNoPage.getTotalElements());
	    paginatedResponse.put("pageSize", ticketNoPage.getSize());
	    paginatedResponse.put("hasNext", ticketNoPage.hasNext());
	    paginatedResponse.put("hasPrevious", ticketNoPage.hasPrevious());

	    long endTime = System.currentTimeMillis();
	    paginatedResponse.put("responseTimeMs", (endTime - startTime));

	    response.setStatusCode(HttpStatus.OK.value());
	    response.setPayload(paginatedResponse);
	    response.setMessage("Data fetched successfully");
	    return response;
	}
	
	/*
	 * public ResponseEntity getTicketReport(String branchCode, String
	 * systemIntegrator, String product, String status, String fromDate, String
	 * toDate, String ticketNo) {
	 * 
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * // Step 1: Parse dates — default last 30 days agar null ho LocalDateTime
	 * fromDateTime = null; LocalDateTime toDateTime = null; try { fromDateTime =
	 * (fromDate != null && !fromDate.isEmpty()) ?
	 * LocalDate.parse(fromDate).atStartOfDay() : LocalDateTime.now().minusDays(30);
	 * // default
	 * 
	 * toDateTime = (toDate != null && !toDate.isEmpty()) ?
	 * LocalDate.parse(toDate).atTime(23, 59, 59) : LocalDateTime.now(); } catch
	 * (Exception e) { response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	 * response.setMessage("Invalid date format. Use yyyy-MM-dd"); return response;
	 * }
	 * 
	 * // Step 2: Branch codes ResponseEntity branchServiceResponse =
	 * branchService.getAllChildren(branchCode); List<BranchMasterEntity>
	 * branchCodeList = (List<BranchMasterEntity>)
	 * branchServiceResponse.getPayload(); if (branchCodeList == null ||
	 * branchCodeList.isEmpty()) return createEmptyResponse(response);
	 * 
	 * List<String> branchCodes = branchCodeList.stream()
	 * .map(BranchMasterEntity::getBranchCode) .collect(Collectors.toList());
	 * 
	 * // Step 3: Devices List<DeviceMasterEntity> allDevices =
	 * deviceMasterRepository.findByBranchCodeIn(branchCodes); if (allDevices ==
	 * null || allDevices.isEmpty()) return createEmptyResponse(response);
	 * 
	 * // Step 4: Build lookup maps Map<String, BranchMasterEntity> branchCodeMap =
	 * branchCodeList.stream() .collect(Collectors.toMap(
	 * BranchMasterEntity::getBranchCode, Function.identity(), (a, b) -> a ));
	 * 
	 * Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
	 * .collect(Collectors.toMap( DeviceMasterEntity::getDeviceSerialNo,
	 * Function.identity(), (a, b) -> a ));
	 * 
	 * List<String> deviceSerialNos = allDevices.stream()
	 * .map(DeviceMasterEntity::getDeviceSerialNo) .collect(Collectors.toList());
	 * 
	 * List<String> deviceIds = allDevices.stream()
	 * .map(DeviceMasterEntity::getDeviceId) .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // Step 5: Fetch tickets — LIMIT 2000, date filtered List<Ticket> allTickets
	 * = ticketDetailsRepository.findTicketsByFilters( deviceSerialNos, (ticketNo !=
	 * null && !ticketNo.isEmpty()) ? ticketNo : null, (status != null &&
	 * !status.isEmpty()) ? status : null, fromDateTime, toDateTime ); if
	 * (allTickets == null || allTickets.isEmpty()) return
	 * createEmptyResponse(response);
	 * 
	 * // Step 6: Group tickets by ticketNo Map<String, List<Ticket>> ticketGroupMap
	 * = allTickets.stream() .collect(Collectors.groupingBy(Ticket::getTicketNo));
	 * 
	 * // Step 7: Base + latest ticket per group — single pass Map<String, Ticket>
	 * baseTicketMap = new HashMap<>(); Map<String, Ticket> latestTicketMap = new
	 * HashMap<>();
	 * 
	 * for (Map.Entry<String, List<Ticket>> entry : ticketGroupMap.entrySet()) {
	 * List<Ticket> group = entry.getValue(); String key = entry.getKey();
	 * 
	 * Ticket base = group.stream() .filter(t -> t.getCreateAt() != null)
	 * .min(Comparator.comparing(Ticket::getCreateAt)) .orElse(group.get(0));
	 * 
	 * Ticket latest = group.stream() .filter(t -> t.getUpdateAt() != null)
	 * .max(Comparator.comparing(Ticket::getUpdateAt)) .orElse(base);
	 * 
	 * baseTicketMap.put(key, base); latestTicketMap.put(key, latest); }
	 * 
	 * List<String> allTicketIds = new ArrayList<>(baseTicketMap.keySet());
	 * 
	 * // Step 8: BATCH — Zone data — ONE query Map<String, ZoneVendorProjection>
	 * zoneMap = new HashMap<>(); if (!deviceIds.isEmpty()) { try {
	 * deviceZoneDetailsRepository.findZoneDataByDeviceIds(deviceIds) .forEach(z ->
	 * { if (z.getDeviceId() != null && z.getInputType() != null) {
	 * zoneMap.put(z.getDeviceId() + "::" + z.getInputType(), z); } }); } catch
	 * (Exception e) { // zone data optional — continue without it
	 * System.err.println("Zone data fetch error: " + e.getMessage()); } }
	 * 
	 * // Step 9: BATCH — Escalation logs — ONE query Map<String, EscalationLog>
	 * escalationMap = new HashMap<>(); if (!allTicketIds.isEmpty()) { try {
	 * escalationMailLogRepository.findTopEscalationsByTicketIds(allTicketIds)
	 * .forEach(e -> { if (e.getTicketNo() != null) {
	 * escalationMap.put(e.getTicketNo(), e); } }); } catch (Exception e) {
	 * System.err.println("Escalation fetch error: " + e.getMessage()); } }
	 * 
	 * // Step 10: BATCH — Param entities — ONE query Map<String, ParamEntity>
	 * paramMap = new HashMap<>(); List<String> escLevels =
	 * escalationMap.values().stream() .map(EscalationLog::getEscLevel)
	 * .filter(Objects::nonNull) .distinct() .collect(Collectors.toList());
	 * 
	 * if (!escLevels.isEmpty()) { try {
	 * paramRepository.getParamsByCodeAndSerials("TICKET", escLevels) .forEach(p ->
	 * { if (p.getSerial() != null) { paramMap.put(p.getSerial(), p); } }); } catch
	 * (Exception e) { System.err.println("Param fetch error: " + e.getMessage()); }
	 * }
	 * 
	 * // Step 11: Build response — ZERO queries in loop List<TicketDataDTO>
	 * ticketReportList = new ArrayList<>();
	 * 
	 * for (Map.Entry<String, Ticket> entry : baseTicketMap.entrySet()) { String tNo
	 * = entry.getKey(); Ticket baseTicket = entry.getValue(); Ticket latestTicket =
	 * latestTicketMap.getOrDefault(tNo, baseTicket);
	 * 
	 * String panelSerialNo = baseTicket.getDeviceSerialNo(); DeviceMasterEntity
	 * device = deviceMap.get(panelSerialNo); if (device == null) continue;
	 * 
	 * BranchMasterEntity branch = branchCodeMap.get(device.getBranchCode());
	 * 
	 * // Zone data — O(1) lookup ZoneVendorProjection zoneData = null; if
	 * (device.getDeviceId() != null && baseTicket.getGroupName() != null) {
	 * zoneData = zoneMap.get(device.getDeviceId() + "::" +
	 * baseTicket.getGroupName()); }
	 * 
	 * TicketDataDTO dto = new TicketDataDTO();
	 * dto.setDeviceSerialNo(device.getDeviceSerialNo());
	 * dto.setDeviceName(device.getDeviceName());
	 * dto.setDeviceType(device.getDeviceType());
	 * dto.setDeviceLocation(device.getDeviceLocation());
	 * dto.setMobileNo(device.getPhoneNumber()); dto.setProduct(zoneData != null ?
	 * zoneData.getZoneDesiredName() : null); dto.setVendorName(zoneData != null ?
	 * zoneData.getVendors() : null);
	 * 
	 * if (branch != null) { dto.setBranchName(branch.getBranchDesc());
	 * dto.setBankName(branch.getBank());
	 * dto.setBranchContactNo(branch.getMobile()); }
	 * 
	 * dto.setTicketId(baseTicket.getTicketNo());
	 * dto.setTicketStatus(baseTicket.getTicketStatus());
	 * dto.setTicketTime(baseTicket.getCreateAt() != null ?
	 * baseTicket.getCreateAt().toString() : null);
	 * dto.setTicketResolvedTime(latestTicket.getUpdateAt() != null ?
	 * latestTicket.getUpdateAt().toString() : null);
	 * 
	 * if (baseTicket.getCreateAt() != null && latestTicket.getUpdateAt() != null) {
	 * Duration duration = Duration.between( baseTicket.getCreateAt(),
	 * latestTicket.getUpdateAt()); dto.setTimeDifference(duration.toHours() + "h "
	 * + (duration.toMinutes() % 60) + "m");
	 * 
	 * // Escalation — O(1) lookup EscalationLog escalationLog =
	 * escalationMap.get(baseTicket.getTicketNo()); if (escalationLog != null) {
	 * dto.setEscalationLevel(escalationLog.getEscLevel()); if
	 * (escalationLog.getEscLevel() != null) { ParamEntity paramEntity =
	 * paramMap.get(escalationLog.getEscLevel()); if (paramEntity != null) {
	 * dto.setEscalationColor(paramEntity.getDescp2()); } } } }
	 * 
	 * ticketReportList.add(dto); }
	 * 
	 * // Step 12: Sort by ticket time DESC ticketReportList.sort((a, b) -> { if
	 * (a.getTicketTime() == null) return 1; if (b.getTicketTime() == null) return
	 * -1; return b.getTicketTime().compareTo(a.getTicketTime()); });
	 * 
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(ticketReportList);
	 * response.setMessage("Data fetched successfully"); return response; }
	 */
	public ResponseEntity getAlertZoneDetailsByTicket(String ticketNo) {
		ResponseEntity response = new ResponseEntity();

		// Step 1: Get all tickets by ticketNo
		List<Ticket> tickets = ticketDetailsRepository.findByTicketNo(ticketNo);
		if (tickets == null || tickets.isEmpty()) {
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("No ticket found with the provided ticket number");
			return response;
		}

		List<AlertDetailsDTO> alertDetailsList = new ArrayList<>();

		for (Ticket ticket : tickets) {

			// Step 2: Get panelSerialNo from ticket
			String panelSerialNo = ticket.getDeviceSerialNo();

			// Step 3: Find device by deviceSerialNo
			DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(panelSerialNo);
			if (device == null)
				continue;

			// Step 4: Get deviceId from device
			String deviceId = device.getDeviceId();

			// Step 5: Find zone details by deviceId
			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneDetailsRepository.findByDeviceId(deviceId);
			if (zoneDetails == null || zoneDetails.isEmpty())
				continue;

			// Step 6: Build zone name from ticket
			String zoneToMatch = "ZONE" + ticket.getZone();

			// Step 7: Find matching zone from zoneDetails
			DeviceZoneDetailsEntity matchedZone = zoneDetails.stream()
					.filter(z -> zoneToMatch.equalsIgnoreCase(z.getZoneOriginalName())).findFirst().orElse(null);

			// Step 8: Fetch alert SMS details
			// Variables to hold final datetime and id values
			Integer alertGeneratedId = null;
			Date alertGeneratedDateTime = null;

			Integer alertResolvedId = null;
			Date alertResolvedDateTime = null;

			// --- Alert Generated ---
			if (ticket.getAlertGeneratedId() != null) {
				int alertId = ticket.getAlertGeneratedId().intValue();

				Optional<SmsDetailsEntity> alertEntityOpt = smsDetailsRepository.findById(alertId);

				if (alertEntityOpt.isPresent()) {
					SmsDetailsEntity alertEntity = alertEntityOpt.get();
					alertGeneratedId = alertEntity.getId();
					alertGeneratedDateTime = alertEntity.getDateTime();
				} else {
					// Fallback: search in history table (returns SmsDetailsHistoryEntity)
					Optional<SmsDetailsHistoryEntity> alertHistoryOpt = smsDetailsHistoryRepository
							.findByOriginalId(alertId);
					if (alertHistoryOpt.isPresent()) {
						SmsDetailsHistoryEntity alertHistory = alertHistoryOpt.get();
						alertGeneratedId = alertHistory.getOriginalId(); // original SMS id
						alertGeneratedDateTime = alertHistory.getDateTime();
					}
				}
			}

			// --- Alert Resolved ---
			if (ticket.getResolvedAlertId() != null) {
				int resolvedId = ticket.getResolvedAlertId().intValue();

				Optional<SmsDetailsEntity> resolvedEntityOpt = smsDetailsRepository.findById(resolvedId);

				if (resolvedEntityOpt.isPresent()) {
					SmsDetailsEntity resolvedEntity = resolvedEntityOpt.get();
					alertResolvedId = resolvedEntity.getId();
					alertResolvedDateTime = resolvedEntity.getDateTime();
				} else {
					// Fallback: search in history table (returns SmsDetailsHistoryEntity)
					Optional<SmsDetailsHistoryEntity> resolvedHistoryOpt = smsDetailsHistoryRepository
							.findByOriginalId(resolvedId);
					if (resolvedHistoryOpt.isPresent()) {
						SmsDetailsHistoryEntity resolvedHistory = resolvedHistoryOpt.get();
						alertResolvedId = resolvedHistory.getOriginalId(); // original SMS id
						alertResolvedDateTime = resolvedHistory.getDateTime();
					}
				}
			}

			// Step 9: Build DTO
			AlertDetailsDTO alertDto = new AlertDetailsDTO();
			alertDto.setZoneStatus(ticket.getZoneStatus());

			// Set alertType from matched zone
			if (matchedZone != null) {
				alertDto.setAlertType(matchedZone.getAlertType());
			}

			// Set alert generated details
			if (alertGeneratedId != null) {
				alertDto.setAlertGeneratedId(alertGeneratedId);
				alertDto.setAlertGeneratedTime(
						alertGeneratedDateTime != null ? alertGeneratedDateTime.toString() : null);
			}

			// Set alert resolved details
			if (alertResolvedId != null) {
				alertDto.setAlertResovedId(alertResolvedId);
				alertDto.setAlertResovedTime(alertResolvedDateTime != null ? alertResolvedDateTime.toString() : null);
			}

			// Step 10: Calculate time difference
			if (alertGeneratedDateTime != null && alertResolvedDateTime != null) {
				LocalDateTime start = alertGeneratedDateTime.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				LocalDateTime end = alertResolvedDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

				Duration duration = Duration.between(start, end);
				long h = duration.toHours();
				long m = duration.toMinutes() % 60;
				alertDto.setTimeDifference(h + "h " + m + "m");
			}

			alertDetailsList.add(alertDto);
		}

		if (alertDetailsList.isEmpty()) {
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("No zone details found for the device associated with the ticket");
			return response;
		}

		response.setStatusCode(HttpStatus.OK.value());
		response.setPayload(alertDetailsList);
		response.setMessage("Zone details fetched successfully");
		return response;
	}

	/*
	 * public ResponseEntity getAlertZoneDetailsByTicket(String ticketNo) {
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * // Step 1: Get all tickets by ticketNo List<Ticket> tickets =
	 * ticketDetailsRepository.findByTicketNo(ticketNo); if (tickets == null ||
	 * tickets.isEmpty()) { response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setMessage("No ticket found with the provided ticket number");
	 * return response; }
	 * 
	 * List<AlertDetailsDTO> alertDetailsList = new ArrayList<>();
	 * 
	 * for (Ticket ticket : tickets) {
	 * 
	 * // Step 2: Get panelSerialNo from ticket String panelSerialNo =
	 * ticket.getDeviceSerialNo();
	 * 
	 * // Step 3: Find device by deviceSerialNo DeviceMasterEntity device =
	 * deviceMasterRepository.findByDeviceSerialNo(panelSerialNo); if (device ==
	 * null) continue;
	 * 
	 * // Step 4: Get deviceId from device String deviceId = device.getDeviceId();
	 * 
	 * // Step 5: Find zone details by deviceId List<DeviceZoneDetailsEntity>
	 * zoneDetails = deviceZoneDetailsRepository.findByDeviceId(deviceId); if
	 * (zoneDetails == null || zoneDetails.isEmpty()) continue;
	 * 
	 * // Step 6: Build zone name from ticket → "ZONE" + ticket.getZone() String
	 * zoneToMatch = "ZONE" + ticket.getZone(); System.out.println("Zone to match: "
	 * + zoneToMatch);
	 * 
	 * // Step 7: Find matching zone from zoneDetails DeviceZoneDetailsEntity
	 * matchedZone = zoneDetails.stream() .filter(z ->
	 * zoneToMatch.equalsIgnoreCase(z.getZoneOriginalName())) .findFirst()
	 * .orElse(null);
	 * 
	 * // Step 8: Fetch alert & resolved SMS details // First try
	 * smsDetailsRepository, if not found then try smsDetailsHistoryRepository
	 * SmsDetailsEntity alertEntity = null; SmsDetailsEntity resolvedEntity = null;
	 * 
	 * if (ticket.getAlertGeneratedId() != null) { int alertId =
	 * ticket.getAlertGeneratedId().intValue();
	 * 
	 * Optional<SmsDetailsEntity> alertEntityOpt =
	 * smsDetailsRepository.findById(alertId);
	 * 
	 * if (alertEntityOpt.isPresent()) { alertEntity = alertEntityOpt.get(); } else
	 * { // Fallback: search in history table Optional<SmsDetailsEntity>
	 * alertHistoryOpt = smsDetailsHistoryRepository.findByOriginalId(alertId); if
	 * (alertHistoryOpt.isPresent()) { alertEntity = alertHistoryOpt.get(); } } }
	 * 
	 * if (ticket.getResolvedAlertId() != null) { int resolvedId =
	 * ticket.getResolvedAlertId().intValue();
	 * 
	 * Optional<SmsDetailsEntity> resolvedEntityOpt =
	 * smsDetailsRepository.findById(resolvedId);
	 * 
	 * if (resolvedEntityOpt.isPresent()) { resolvedEntity =
	 * resolvedEntityOpt.get(); } else { // Fallback: search in history table
	 * Optional<SmsDetailsEntity> resolvedHistoryOpt =
	 * smsDetailsHistoryRepository.findByOriginalId(resolvedId); if
	 * (resolvedHistoryOpt.isPresent()) { resolvedEntity = resolvedHistoryOpt.get();
	 * } } }
	 * 
	 * // Step 9: Build DTO AlertDetailsDTO alertDto = new AlertDetailsDTO();
	 * alertDto.setZoneStatus(ticket.getTicketStatus());
	 * 
	 * // Set alertType from matched zone if (matchedZone != null) {
	 * alertDto.setAlertType(matchedZone.getAlertType()); }
	 * 
	 * // Set alert generated details if (alertEntity != null) {
	 * alertDto.setAlertGeneratedId(alertEntity.getId());
	 * alertDto.setAlertGeneratedTime(alertEntity.getDateTime() != null ?
	 * alertEntity.getDateTime().toString() : null); }
	 * 
	 * // Set alert resolved details if (resolvedEntity != null) {
	 * alertDto.setAlertResovedId(resolvedEntity.getId());
	 * alertDto.setAlertResovedTime(resolvedEntity.getDateTime() != null ?
	 * resolvedEntity.getDateTime().toString() : null); }
	 * 
	 * // Step 10: Calculate time difference if (alertEntity != null &&
	 * resolvedEntity != null) { if (alertEntity.getDateTime() != null &&
	 * resolvedEntity.getDateTime() != null) { LocalDateTime start =
	 * alertEntity.getDateTime()
	 * .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); LocalDateTime
	 * end = resolvedEntity.getDateTime()
	 * .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	 * 
	 * Duration duration = Duration.between(start, end); long h =
	 * duration.toHours(); long m = duration.toMinutes() % 60;
	 * alertDto.setTimeDifference(h + "h " + m + "m"); } }
	 * 
	 * alertDetailsList.add(alertDto); }
	 * 
	 * if (alertDetailsList.isEmpty()) {
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); response.
	 * setMessage("No zone details found for the device associated with the ticket"
	 * ); return response; }
	 * 
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(alertDetailsList);
	 * response.setMessage("Zone details fetched successfully"); return response; }
	 */
//	public ResponseEntity getAlertZoneDetailsByTicket(String ticketNo) {
//	    ResponseEntity response = new ResponseEntity();
//
//	    // Step 1: Get all tickets by ticketNo
//	    List<Ticket> tickets = ticketDetailsRepository.findByTicketNo(ticketNo);
//	    if (tickets == null || tickets.isEmpty()) {
//	        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//	        response.setMessage("No ticket found with the provided ticket number");
//	        return response;
//	    }
//
//	    List<AlertDetailsDTO> alertDetailsList = new ArrayList<>();
//
//	    for (Ticket ticket : tickets) {
//
//	        // Step 2: Get panelSerialNo from ticket
//	        String panelSerialNo = ticket.getDeviceSerialNo();
//
//	        // Step 3: Find device by deviceSerialNo
//	        DeviceMasterEntity device = deviceMasterRepository.findByDeviceSerialNo(panelSerialNo);
//	        if (device == null) continue;
//
//	        // Step 4: Get deviceId from device
//	        String deviceId = device.getDeviceId();
//
//	        // Step 5: Find zone details by deviceId
//	        List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneDetailsRepository.findByDeviceId(deviceId);
//	        if (zoneDetails == null || zoneDetails.isEmpty()) continue;
//
//	        // Step 6: Build zone name from ticket → "ZONE" + ticket.getZone()
//	        String zoneToMatch = "ZONE" + ticket.getZone();
//	        System.out.println("Zone to match: " + zoneToMatch);
//
//	        // Step 7: Find matching zone from zoneDetails
//	        DeviceZoneDetailsEntity matchedZone = zoneDetails.stream()
//	                .filter(z -> zoneToMatch.equalsIgnoreCase(z.getZoneOriginalName()))
//	                .findFirst()
//	                .orElse(null);
//
//	        // Step 8: Fetch alert & resolved SMS details
//	        Optional<SmsDetailsEntity> alertEntityOpt = Optional.empty();
//	        Optional<SmsDetailsEntity> resolvedEntityOpt = Optional.empty();
//
//	        if (ticket.getAlertGeneratedId() != null) {
//	            alertEntityOpt = smsDetailsRepository.findById(
//	                    ticket.getAlertGeneratedId().intValue());
//	        }else {
//	        	alertEntityOpt = smsDetailsHistoryRepository.findByOriginalId(
//	                    ticket.getAlertGeneratedId().intValue());
//	        }
//
//	        if (ticket.getResolvedAlertId() != null) {
//	            resolvedEntityOpt = smsDetailsRepository.findById(
//	                    ticket.getResolvedAlertId().intValue());
//	        }else {
//				resolvedEntityOpt = smsDetailsHistoryRepository
//						.findByOriginalId(ticket.getResolvedAlertId().intValue());
//	        }
//	        
//	        // Step 9: Build DTO
//	        AlertDetailsDTO alertDto = new AlertDetailsDTO();
//	        alertDto.setZoneStatus(ticket.getTicketStatus());
//	        // Set alertType from matched zone
//	        if (matchedZone != null) {
//	            alertDto.setAlertType(matchedZone.getAlertType());
//	        }
//
//	        // Set alert generated details
//	        if (alertEntityOpt.isPresent()) {
//	            SmsDetailsEntity alertEntity = alertEntityOpt.get();
//	            alertDto.setAlertGeneratedId(alertEntity.getId());
//	            alertDto.setAlertGeneratedTime(alertEntity.getDateTime() != null
//	                    ? alertEntity.getDateTime().toString() : null);
//	        }
//
//	        // Set alert resolved details
//	        if (resolvedEntityOpt.isPresent()) {
//	            SmsDetailsEntity resolvedEntity = resolvedEntityOpt.get();
//	            alertDto.setAlertResovedId(resolvedEntity.getId());
//	            alertDto.setAlertResovedTime(resolvedEntity.getDateTime() != null
//	                    ? resolvedEntity.getDateTime().toString() : null);
//	        }
//
//	        // Step 10: Calculate time difference
//	        if (alertEntityOpt.isPresent() && resolvedEntityOpt.isPresent()) {
//	            SmsDetailsEntity alertEntity = alertEntityOpt.get();
//	            SmsDetailsEntity resolvedEntity = resolvedEntityOpt.get();
//
//	            if (alertEntity.getDateTime() != null && resolvedEntity.getDateTime() != null) {
//	                LocalDateTime start = alertEntity.getDateTime()
//	                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//	                LocalDateTime end = resolvedEntity.getDateTime()
//	                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//
//	                Duration duration = Duration.between(start, end);
//	                long h = duration.toHours();
//	                long m = duration.toMinutes() % 60;
//	                alertDto.setTimeDifference(h + "h " + m + "m");
//	            }
//	        }
//
//	        alertDetailsList.add(alertDto);
//	    }
//
//	    if (alertDetailsList.isEmpty()) {
//	        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//	        response.setMessage("No zone details found for the device associated with the ticket");
//	        return response;
//	    }
//
//	    response.setStatusCode(HttpStatus.OK.value());
//	    response.setPayload(alertDetailsList);
//	    response.setMessage("Zone details fetched successfully");
//	    return response;
//	}
//	
	public ResponseEntity getVendorDropdown(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		// Step 1: Get all child branches
		ResponseEntity branchResponseEntity = branchService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Step 2: Get all devices for these branches
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 3: Get all device IDs
		List<String> deviceIds = deviceList.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());

		// Step 4: Get all zone details for these devices
		List<DeviceZoneDetailsEntity> zoneDetailsList = deviceZoneDetailsRepository.findByDeviceIdIn(deviceIds);
		if (zoneDetailsList == null || zoneDetailsList.isEmpty()) {
			response.setMessage("No zone details found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 5: Distinct vendors by vendorId
		Map<String, String> distinctVendorMap = new LinkedHashMap<>();
		for (DeviceZoneDetailsEntity zone : zoneDetailsList) {
			String vendorId = zone.getVendorId();
			String vendorName = zone.getVendors();
			// System.out.println("VENDOR NAME::"+vendorName);
			if (vendorId != null && !vendorId.trim().isEmpty() && vendorName != null && !vendorName.trim().isEmpty()) {
				distinctVendorMap.putIfAbsent(vendorId.trim(), vendorName.trim());
			}
		}
		System.out.println(distinctVendorMap);
		if (distinctVendorMap.isEmpty()) {
			response.setMessage("No vendors found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<VendorDropdownDto> result = distinctVendorMap.entrySet().stream()
				.map(entry -> new VendorDropdownDto(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(VendorDropdownDto::getVendorName)).collect(Collectors.toList());

		response.setPayload(result);
		response.setTotalElements(result.size());
		response.setMessage("Vendor list fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	private List<DeviceZoneDetailsEntity> getZoneDetailsForBranch(String branchCode, ResponseEntity response) {

		// Step 1: branches
		ResponseEntity branchResponseEntity = branchService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return null;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Step 2: devices
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return null;
		}

		// Step 3: device IDs
		List<String> deviceIds = deviceList.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());

		// Step 4: zone details
		List<DeviceZoneDetailsEntity> zoneDetailsList = deviceZoneDetailsRepository.findByDeviceIdIn(deviceIds);
		if (zoneDetailsList == null || zoneDetailsList.isEmpty()) {
			response.setMessage("No zone details found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return null;
		}

		return zoneDetailsList;
	}

	// 1. Zone Original Name dropdown
	public ResponseEntity getZoneOriginalNameDropdown(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		List<DeviceZoneDetailsEntity> zoneDetailsList = getZoneDetailsForBranch(branchCode, response);
		if (zoneDetailsList == null)
			return response;

		List<ZoneOriginalNameDropdownDto> result = zoneDetailsList.stream()
				.map(DeviceZoneDetailsEntity::getZoneOriginalName).filter(z -> z != null && !z.trim().isEmpty())
				.map(String::trim).distinct().sorted().map(ZoneOriginalNameDropdownDto::new)
				.collect(Collectors.toList());

		if (result.isEmpty()) {
			response.setMessage("No zone original names found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		response.setPayload(result);
		response.setTotalElements(result.size());
		response.setMessage("Zone original name list fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	// 2. Zone Desired Name dropdown
	public ResponseEntity getZoneDesiredNameDropdown(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		List<DeviceZoneDetailsEntity> zoneDetailsList = getZoneDetailsForBranch(branchCode, response);
		if (zoneDetailsList == null)
			return response;

		List<ZoneDesiredNameDropdownDto> result = zoneDetailsList.stream()
				.map(DeviceZoneDetailsEntity::getZoneDesiredName).filter(z -> z != null && !z.trim().isEmpty())
				.map(String::trim).distinct().sorted().map(ZoneDesiredNameDropdownDto::new)
				.collect(Collectors.toList());

		if (result.isEmpty()) {
			response.setMessage("No zone desired names found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		response.setPayload(result);
		response.setTotalElements(result.size());
		response.setMessage("Zone desired name list fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	// 3. Alert Type dropdown
	public ResponseEntity getAlertTypeDropdown(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		List<DeviceZoneDetailsEntity> zoneDetailsList = getZoneDetailsForBranch(branchCode, response);
		if (zoneDetailsList == null)
			return response;

		List<AlertTypeDropdownDto> result = zoneDetailsList.stream().map(DeviceZoneDetailsEntity::getAlertType)
				.filter(a -> a != null && !a.trim().isEmpty()).map(String::trim).distinct().sorted()
				.map(AlertTypeDropdownDto::new).collect(Collectors.toList());

		if (result.isEmpty()) {
			response.setMessage("No alert types found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		response.setPayload(result);
		response.setTotalElements(result.size());
		response.setMessage("Alert type list fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	public ResponseEntity getDropdownFilters(String branchCode, String vendorId, String zoneOriginalName,
			String zoneDesiredName, String alertType) {

		Logger logger = LoggerFactory.getLogger(this.getClass());

		logger.info("==== getDropdownFilters START ====");
		logger.info(
				"Input Parameters -> branchCode: {}, vendorId: {}, zoneOriginalName: {}, zoneDesiredName: {}, alertType: {}",
				branchCode, vendorId, zoneOriginalName, zoneDesiredName, alertType);

		ResponseEntity response = new ResponseEntity();

		// Step 1: branches
		logger.info("Step 1: Fetching all children branches for branchCode: {}", branchCode);
		ResponseEntity branchResponseEntity = branchService.getAllChildren(branchCode);

		if (branchResponseEntity.getPayload() == null) {
			logger.error("No branches found for branch code: {}", branchCode);
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		logger.info("Step 1 Complete: Total branches found: {}", branchList.size());

		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());
		logger.debug("Branch codes: {}", branchCodes);

		// Step 2: devices
		logger.info("Step 2: Fetching devices for {} branches", branchCodes.size());
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);

		if (deviceList == null || deviceList.isEmpty()) {
			logger.error("No devices found for branches: {}", branchCodes);
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		logger.info("Step 2 Complete: Total devices found: {}", deviceList.size());
		deviceList.forEach(device -> logger.debug("Device - BranchCode: {}, DeviceId: {}, SerialNo: {}",
				device.getBranchCode(), device.getDeviceId(), device.getDeviceSerialNo()));

		// Step 3: device IDs
		logger.info("Step 3: Extracting device IDs from {} devices", deviceList.size());
		List<String> deviceIds = deviceList.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());
		logger.info("Step 3 Complete: Device IDs count: {}", deviceIds.size());
		logger.debug("Device IDs: {}", deviceIds);

		// Step 4: zone details
		logger.info("Step 4: Fetching zone details for {} device IDs", deviceIds.size());
		List<DeviceZoneDetailsEntity> zoneDetailsList = deviceZoneDetailsRepository.findByDeviceIdIn(deviceIds);

		if (zoneDetailsList == null || zoneDetailsList.isEmpty()) {
			logger.error("No zone details found for device IDs: {}", deviceIds);
			response.setMessage("No zone details found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		logger.info("Step 4 Complete: Total zone details found: {}", zoneDetailsList.size());
		logger.debug("Sample zone details (first 3):");
		zoneDetailsList.stream().limit(3)
				.forEach(zone -> logger.debug(
						"Zone - DeviceId: {}, Original: {}, Desired: {}, AlertType: {}, Vendor: {}, VendorId: {}",
						zone.getDeviceId(), zone.getZoneOriginalName(), zone.getZoneDesiredName(), zone.getAlertType(),
						zone.getVendors(), zone.getVendorId()));

		// ----------------------------------------------------------------
		// Step 5: Filter prepare — trim + lowercase
		// ----------------------------------------------------------------
		logger.info("Step 5: Preparing filters (trim + lowercase)");
		String fVendorId = (vendorId != null && !vendorId.trim().isEmpty()) ? vendorId.trim().toLowerCase() : null;
		String fZoneOriginal = (zoneOriginalName != null && !zoneOriginalName.trim().isEmpty())
				? zoneOriginalName.trim().toLowerCase()
				: null;
		String fZoneDesired = (zoneDesiredName != null && !zoneDesiredName.trim().isEmpty())
				? zoneDesiredName.trim().toLowerCase()
				: null;
		String fAlertType = (alertType != null && !alertType.trim().isEmpty()) ? alertType.trim().toLowerCase() : null;

		logger.info(
				"Step 5 Complete: Processed filters -> VendorId: {}, ZoneOriginal: {}, ZoneDesired: {}, AlertType: {}",
				fVendorId, fZoneOriginal, fZoneDesired, fAlertType);

		// ----------------------------------------------------------------
		// Step 6: Apply filters on zone details list
		// ----------------------------------------------------------------
		logger.info("Step 6: Applying filters on {} zone details", zoneDetailsList.size());

		// Track counts at each filter stage
		int beforeFilter = zoneDetailsList.size();
		logger.debug("Before any filter: {} records", beforeFilter);

		// Filter by VendorId
		List<DeviceZoneDetailsEntity> vendorFilteredList = zoneDetailsList.stream()
				.filter(z -> fVendorId == null
						|| (z.getVendorId() != null && z.getVendorId().trim().toLowerCase().contains(fVendorId)))
				.collect(Collectors.toList());
		logger.info("After vendor filter (VendorId: {}): {} records", fVendorId, vendorFilteredList.size());

		// Filter by ZoneOriginalName
		List<DeviceZoneDetailsEntity> zoneOriginalFilteredList = vendorFilteredList.stream()
				.filter(z -> fZoneOriginal == null || (z.getZoneOriginalName() != null
						&& z.getZoneOriginalName().trim().equalsIgnoreCase(zoneOriginalName.trim())))
				.collect(Collectors.toList());
		logger.info("After zone original filter ({}): {} records", zoneOriginalName, zoneOriginalFilteredList.size());

		// Filter by ZoneDesiredName
		List<DeviceZoneDetailsEntity> zoneDesiredFilteredList = zoneOriginalFilteredList.stream()
				.filter(z -> fZoneDesired == null || (z.getZoneDesiredName() != null
						&& z.getZoneDesiredName().trim().toLowerCase().contains(fZoneDesired)))
				.collect(Collectors.toList());
		logger.info("After zone desired filter ({}): {} records", zoneDesiredName, zoneDesiredFilteredList.size());

		// Filter by AlertType
		List<DeviceZoneDetailsEntity> filteredList = zoneDesiredFilteredList.stream()
				.filter(z -> fAlertType == null
						|| (z.getAlertType() != null && z.getAlertType().trim().toLowerCase().contains(fAlertType)))
				.collect(Collectors.toList());
		logger.info("After alert type filter ({}): {} records", alertType, filteredList.size());

		if (filteredList.isEmpty()) {
			logger.warn("No records found after applying all filters");
		} else {
			logger.info("Step 6 Complete: Final filtered list size: {}", filteredList.size());
			logger.debug("Filtered zone details (first 3):");
			filteredList.stream().limit(3)
					.forEach(zone -> logger.debug(
							"Filtered Zone - DeviceId: {}, Original: {}, Desired: {}, AlertType: {}, Vendor: {}",
							zone.getDeviceId(), zone.getZoneOriginalName(), zone.getZoneDesiredName(),
							zone.getAlertType(), zone.getVendors()));
		}

		// ----------------------------------------------------------------
		// Step 7: Distinct vendors from filteredList
		// ----------------------------------------------------------------
		logger.info("Step 7: Extracting distinct vendors from filtered list ({} records)", filteredList.size());
		Map<String, String> distinctVendorMap = new LinkedHashMap<>();

		int vendorNullCount = 0;
		int vendorAddedCount = 0;

		for (DeviceZoneDetailsEntity zone : filteredList) {
			String vid = zone.getVendorId();
			String vname = zone.getVendors();

			if (vid != null && !vid.trim().isEmpty() && vname != null && !vname.trim().isEmpty()) {
				if (!distinctVendorMap.containsKey(vid.trim())) {
					distinctVendorMap.put(vid.trim(), vname.trim());
					vendorAddedCount++;
					logger.debug("Added new vendor - ID: {}, Name: {}", vid.trim(), vname.trim());
				}
			} else {
				vendorNullCount++;
				logger.debug("Skipped zone with null/empty vendor - ID: {}, Name: {}", vid, vname);
			}
		}

		logger.info("Step 7 Complete: Total distinct vendors found: {}, Skipped (null/empty): {}",
				distinctVendorMap.size(), vendorNullCount);

		List<VendorDropdownDto> vendors = distinctVendorMap.entrySet().stream()
				.map(e -> new VendorDropdownDto(e.getKey(), e.getValue()))
				.sorted(Comparator.comparing(VendorDropdownDto::getVendorName)).collect(Collectors.toList());

		logger.info("Vendors list created with {} entries", vendors.size());
		vendors.forEach(
				vendor -> logger.debug("Vendor - ID: {}, Name: {}", vendor.getVendorId(), vendor.getVendorName()));

		// ----------------------------------------------------------------
		// Step 8: Distinct zoneOriginalNames from filteredList
		// ----------------------------------------------------------------
		logger.info("Step 8: Extracting distinct zone original names from filtered list");
		List<ZoneOriginalNameDropdownDto> zoneOriginalNames = filteredList.stream()
				.map(DeviceZoneDetailsEntity::getZoneOriginalName).filter(z -> z != null && !z.trim().isEmpty())
				.map(String::trim).distinct().sorted().map(ZoneOriginalNameDropdownDto::new)
				.collect(Collectors.toList());

		logger.info("Step 8 Complete: Total distinct zone original names: {}", zoneOriginalNames.size());
		zoneOriginalNames.forEach(name -> logger.debug("Zone Original Name: {}", name.getZoneOriginalName()));

		// ----------------------------------------------------------------
		// Step 9: Distinct zoneDesiredNames from filteredList
		// ----------------------------------------------------------------
		logger.info("Step 9: Extracting distinct zone desired names from filtered list");
		List<ZoneDesiredNameDropdownDto> zoneDesiredNames = filteredList.stream()
				.map(DeviceZoneDetailsEntity::getZoneDesiredName).filter(z -> z != null && !z.trim().isEmpty())
				.map(String::trim).distinct().sorted().map(ZoneDesiredNameDropdownDto::new)
				.collect(Collectors.toList());

		logger.info("Step 9 Complete: Total distinct zone desired names: {}", zoneDesiredNames.size());
		zoneDesiredNames.forEach(name -> logger.debug("Zone Desired Name: {}", name.getZoneDesiredName()));

		// ----------------------------------------------------------------
		// Step 10: Distinct alertTypes from filteredList
		// ----------------------------------------------------------------
		logger.info("Step 10: Extracting distinct alert types from filtered list");
		List<AlertTypeDropdownDto> alertTypes = filteredList.stream().map(DeviceZoneDetailsEntity::getAlertType)
				.filter(a -> a != null && !a.trim().isEmpty()).map(String::trim).distinct().sorted()
				.map(AlertTypeDropdownDto::new).collect(Collectors.toList());

		logger.info("Step 10 Complete: Total distinct alert types: {}", alertTypes.size());
		alertTypes.forEach(alert -> logger.debug("Alert Type: {}", alert.getAlertType()));

		// ----------------------------------------------------------------
		// Step 11: Build final response
		// ----------------------------------------------------------------
		logger.info("Step 11: Building final response payload");
		Map<String, Object> finalPayload = new LinkedHashMap<>();
		finalPayload.put("vendors", vendors);
		finalPayload.put("zoneOriginalNames", zoneOriginalNames);
		finalPayload.put("zoneDesiredNames", zoneDesiredNames);
		finalPayload.put("alertTypes", alertTypes);

		logger.info("Final payload size - vendors: {}, zoneOriginalNames: {}, zoneDesiredNames: {}, alertTypes: {}",
				vendors.size(), zoneOriginalNames.size(), zoneDesiredNames.size(), alertTypes.size());

		response.setPayload(finalPayload);
		response.setMessage("Dropdown filters fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());

		logger.info("==== getDropdownFilters END - Success ====");
		return response;
	}

	public List<ParamEntity> getParamByCode(String code) {
		List<ParamEntity> params = paramRepository.findAllByCode("STATUS");
		return params;
	}
	
	

}
