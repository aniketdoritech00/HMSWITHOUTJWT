package com.doritech.api.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.doritech.api.Common.CommunicationService;
import com.doritech.api.Common.EncryptionDecryption;
import com.doritech.api.Common.General;
import com.doritech.api.DTO.AlertApiResponse;
import com.doritech.api.DTO.AllAlarmsDTO;
import com.doritech.api.DTO.BranchMasterDTO;
import com.doritech.api.DTO.CallDTO;
import com.doritech.api.DTO.DeviceMasterDTO;
import com.doritech.api.DTO.MailReportDTO;
import com.doritech.api.DTO.MsisdnList;
import com.doritech.api.DTO.NotWorkingDeviceDTO;
import com.doritech.api.DTO.SmsDetailsDTO;
import com.doritech.api.DTO.SocialMediaDTO;
import com.doritech.api.DTO.TATReportDTO;
import com.doritech.api.DTO.UptimeTatEntity;
import com.doritech.api.DTO.ZoneReportDTO;
import com.doritech.api.DTO.ZonesDTO;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.CallLogEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.EmailSchedulerEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.EscalationMatrix;
import com.doritech.api.Entity.Heartbeat;
import com.doritech.api.Entity.InputStatusDetailsEntity;
import com.doritech.api.Entity.LeoStaticDataEntity;
import com.doritech.api.Entity.MailParamEntity;
import com.doritech.api.Entity.NonSyncLogEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;
import com.doritech.api.Entity.SmsLogEntity;
import com.doritech.api.Entity.SmsParamEntity;
import com.doritech.api.Entity.Ticket;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.CallLogRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.EmailSchedulerRepository;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.EscalationMatrixRepository;
import com.doritech.api.Repository.HeartbeatRepository;
import com.doritech.api.Repository.LeoStaticDataRepository;
import com.doritech.api.Repository.MailParamRepo;
import com.doritech.api.Repository.NonSyncLogRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.SmsDetailsHistoryRepository;
import com.doritech.api.Repository.SmsDetailsRepository;
import com.doritech.api.Repository.SmsLogRepo;
import com.doritech.api.Repository.SmsParamRepo;
import com.doritech.api.Repository.StaticDataRepo;
import com.doritech.api.Repository.TicketRepository;
import com.doritech.api.Repository.VendorMasterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SmsDetailsService {
	@Autowired
	SmsDetailsRepository smsDetailsRepository;
	@Autowired
	DeviceMasterRepository deviceRepo;
	@Autowired
	General general;
	@Autowired
	BranchMasterRepository branchRepo;
	@Autowired
	BranchMasterService branchService;
	@Autowired
	DeviceMasterService deviceMasterService;
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	StaticDataRepo staticDataRepo;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	SmsParamRepo smsParamRepo;
	@Autowired
	VendorMasterRepository vendorRepo;
	@Autowired
	SmsLogRepo smsLogRepo;
	@Autowired
	CommunicationService communicationService;
	@Autowired
	DeviceZoneDetailsRepository zoneRepo;
	@Autowired
	MailParamRepo mailParamRepo;
	@Autowired
	EmailLogRepo emailLogRepo;
	@Autowired
	DeviceZoneDetailsRepository deviceZoneRepo;
	@Autowired
	EscalationMailLogRepository escalationLogRepository;
	@Autowired
	LeoStaticDataRepository leoStaticDataRepository;
	@Autowired
	ParamService paramService;
	@Autowired
	SmsDetailsHistoryRepository smsDetailsHistoryRepository;
	@Autowired
	EscalationMatrixRepository escalationMatrixRepo;
	@Autowired
	ReadMail readMail;
	@Value("${webSocketUrl}")
	private String webSocketUrl;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	SocialMediaService socialMediaService;

	@Autowired
	BranchMasterService branchMasterService;

	@Autowired
	MailService mailService;

	@Autowired
	EmailSchedulerRepository emailSchedulerRepository;

	@Autowired
	ServletContext servletContext;
	@Autowired
	WhatsappService whatsappService;

	@Autowired
	CallLogRepository callLogRepository;

	@Autowired
	NonSyncLogRepository nonSyncLogRepository;

	@Autowired
	ParamRepository paramRepository;

	@Autowired
	HeartbeatRepository heartbeatRepository;

	@Autowired
	private BankMasterRepository bankMasterRepository;

	long id = 0;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
			.withZone(ZoneId.systemDefault());

	private static final Logger logger = LoggerFactory.getLogger(SmsDetailsService.class);

	public ResponseEntity getWebSocketUrl() {
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			if (webSocketUrl.isEmpty()) {
				responseEntity.setMessage("Url Not Found :" + webSocketUrl);
				responseEntity.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				responseEntity.setPayload(webSocketUrl);
				responseEntity.setMessage("Url Is Available and it is -:" + webSocketUrl);
				responseEntity.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("Error Occured!!" + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return responseEntity;
	}

	public ResponseEntity getAllSmsDetails() throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<SmsDetailsEntity> responseList = smsDetailsRepository.findAll();
			if (responseList != null) {
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data present in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from database.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getSmsDetailsById(int id) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getById(id);
			if (smsDetailsEntity != null) {
				response.setPayload(smsDetailsEntity);
				response.setMessage("data fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from the database.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getActiveDevices(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			int count = 0, totalCount = 0;

			List<Integer> countList = new ArrayList<>();

			List<DeviceMasterEntity> deviceMasterList = new ArrayList<>();

			// List<String> requestList = branchRepo.findAllByBranchCode(branchCode);

			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> requestList = (List<BranchMasterEntity>) responseEntity.getPayload();

			for (BranchMasterEntity item : requestList) {

				DeviceMasterEntity deviceList = deviceRepo.findBybranchCode(item.getBranchCode());

				if (deviceList != null) {
					deviceMasterList.add(deviceList);
				}

			}
			totalCount = deviceMasterList.size();
			for (DeviceMasterEntity x : deviceMasterList) {
				List<SmsDetailsEntity> list = smsDetailsRepository.findByPanelSerialNo(x.getDeviceSerialNo());
				if (!list.isEmpty()) {

					Collections.sort(list, Comparator.comparing(SmsDetailsEntity::getDateTime).reversed());
					SmsDetailsEntity latestRecord = list.get(0);
					if ("1".equalsIgnoreCase(latestRecord.getMain())) {
						count++;
					}
				}
			}

			countList.add(count);
			countList.add(totalCount);
			response.setPayload(countList);
			response.setMessage("Activate Devices Fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error occurred fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getIsolatedDevicesCount(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			@SuppressWarnings("unused")
			int isolatedDeviceCount = 0, connectedDevice = 0, totalDeviceCount = 0;
			List<Integer> countList = new ArrayList<>();
			List<DeviceMasterEntity> deviceList = new ArrayList<>();
			// List<String> requestList = branchRepo.findAllByBranchCode(branchCode);

			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> requestList = (List<BranchMasterEntity>) responseEntity.getPayload();

			for (BranchMasterEntity x : requestList) {
				DeviceMasterEntity deviceMaster = deviceRepo.findBybranchCode(x.getBranchCode());
				if (deviceMaster != null) {
					deviceList.add(deviceMaster);
				}

			}

			totalDeviceCount = deviceList.size();
			if (deviceList.isEmpty()) {
				response.setMessage("Data not found ");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			for (DeviceMasterEntity x : deviceList) {
				List<SmsDetailsEntity> list = smsDetailsRepository.findByPanelSerialNo(x.getDeviceSerialNo());
				if (list.isEmpty()) {
					connectedDevice++;
				} else {

					Collections.sort(list, Comparator.comparing(SmsDetailsEntity::getDateTime).reversed());
					SmsDetailsEntity latestRecord = list.get(0);
					System.out.println(latestRecord.getDateTime());
					// Check if the latest SMS record date is not today
					if (!latestRecord.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
							.equals(LocalDate.now())) {
						// Increment isolated device count
						isolatedDeviceCount++;
					}

				}
			}

			countList.add(isolatedDeviceCount);
			countList.add(totalDeviceCount);
			response.setPayload(countList);
			response.setMessage("Isolated Devices Count Fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error occurred fetching data ");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getNotSyncedDeviceCount(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<Integer> countList = new ArrayList<>();
			int totalDeviceCount = 0, notSyncedDevice = 0, syncedDevice = 0;
			List<DeviceMasterEntity> deviceMasterList = new ArrayList<>();

			// List<String> requestList = branchRepo.findAllByBranchCode(branchCode);

			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> requestList = (List<BranchMasterEntity>) responseEntity.getPayload();

			for (BranchMasterEntity x : requestList) {
				DeviceMasterEntity deviceMaster = deviceRepo.findBybranchCode(x.getBranchCode());

				if (deviceMaster != null) {
					deviceMasterList.add(deviceMaster);
				}

			}

			if (deviceMasterList.isEmpty()) {
				response.setMessage("Devices not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			} else {

				long thresholdHours = getThresholdHours();

				for (DeviceMasterEntity x : deviceMasterList) {
					SmsDetailsEntity sms = getLatestSmsForThresholdTime(x.getDeviceSerialNo(), thresholdHours);

					if (sms == null) {
						// 36 hour me msg nahi aaya → non_sync_log check karo
						if (checkNonSyncLog(x.getDeviceSerialNo())) {
							notSyncedDevice++;
						}
					} else {
						syncedDevice++;
					}
				}
				totalDeviceCount = deviceMasterList.size();
				countList.add(notSyncedDevice);
				countList.add(totalDeviceCount);

				countList.add(syncedDevice);

				response.setMessage("Devices fetched successfully");
				response.setPayload(countList);
				response.setStatusCode(HttpStatus.OK.value());

			}
		} catch (Exception e) {
			response.setMessage("Error fetching devices");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getLatestRecord(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceRepo.findBybranchCode(branchCode);
			if (device != null) {
				List<SmsDetailsEntity> smsDetail = smsDetailsRepository.findByPanelSerialNo(device.getDeviceSerialNo());
				if (!smsDetail.isEmpty()) {
					Collections.sort(smsDetail, Comparator.comparing(SmsDetailsEntity::getDateTime).reversed());
					SmsDetailsEntity latestRecord = smsDetail.get(0);

					SmsDetailsDTO smsResponse = new SmsDetailsDTO();
					BeanUtils.copyProperties(latestRecord, smsResponse);

					smsResponse.setPanelSerialNo(
							deviceRepo.findByDeviceSerialNo(latestRecord.getPanelSerialNo()).getDeviceName());

					@SuppressWarnings("unused")
					String deviceName = device.getDeviceName();
					List<DeviceZoneDetailsEntity> zoneList = zoneRepo.findAllByDeviceId(device.getDeviceId());
					Map<String, String> zoneNameMap = new HashMap<>();
					zoneList.forEach(zone -> zoneNameMap.put(zone.getZoneOriginalName(), zone.getZoneDesiredName()));

					List<String> zoneValues = new ArrayList<>();
					for (int i = 1; i <= 40; i++) {
						String zoneValue = (String) latestRecord.getClass().getMethod("getZone" + i)
								.invoke(latestRecord);
						if (zoneValue != null && !zoneValue.isEmpty()) {
							String zoneName = zoneNameMap.get("ZONE" + i);
							if (zoneName != null) {
								zoneValues.add(zoneName + ": " + zoneValue);
							}
						}
					}

					Map<String, Object> responseBody = new HashMap<>();
					responseBody.put("latestRecord", smsResponse);
					responseBody.put("zoneValues", zoneValues);

					response.setPayload(responseBody);
					response.setMessage("Data fetched successfully");
					response.setStatusCode(HttpStatus.OK.value());
				} else {
					response.setMessage("No records found for the given branch code");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
				}
			} else {
				response.setMessage("Device not found for the given branch code");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			response.setMessage("Error occurred while fetching data: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	// @SuppressWarnings("deprecation")
	// public List<SmsLogEntity> processSmsForAlertNew(String branchCode, String
	// activeZone, Long escalationLogId,String escalationLevel) {
	// List<SmsLogEntity> smsEntityList = new ArrayList<>();
	// try {
	// String[] activeZoneArray = activeZone.split(",");
	//
	// for (String activeZoneValue : activeZoneArray) {
	// activeZoneValue = activeZoneValue.trim();
	//
	// BranchMasterEntity branchEntity =
	// branchMasterRepository.findBybranchCode(branchCode);
	// List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE",
	// "ALERT");
	//
	// List<String> mobileNumbers = new ArrayList<>();
	// if (branchEntity.getMobile() != null &&
	// !branchEntity.getMobile().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobile().replaceFirst("^91", ""));
	// }
	// if (branchEntity.getMobileSecondary() != null &&
	// !branchEntity.getMobileSecondary().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobileSecondary().replaceFirst("^91", ""));
	// }
	// if (branchEntity.getMobileTertiary() != null &&
	// !branchEntity.getMobileTertiary().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobileTertiary().replaceFirst("^91", ""));
	// }
	// if (branchEntity.getMobileQuaternary() != null &&
	// !branchEntity.getMobileQuaternary().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobileQuaternary().replaceFirst("^91",
	// ""));
	// }
	// if (branchEntity.getMobileQuinary() != null &&
	// !branchEntity.getMobileQuinary().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobileQuinary().replaceFirst("^91", ""));
	// }
	// if (branchEntity.getMobileSenary() != null &&
	// !branchEntity.getMobileSenary().trim().isEmpty()) {
	// mobileNumbers.add(branchEntity.getMobileSenary().replaceFirst("^91", ""));
	// }
	//
	// System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);
	//
	// for (Object[] object : staticData) {
	// String smsText = String.valueOf(object[2] == null ? "" : object[2]);
	// smsText = smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
	//
	// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	// SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	//
	// String currentDate = dateFormat.format(new Date());
	// String currentTime = timeFormat.format(new Date());
	//
	// smsText = smsText.replaceAll("DATE", currentDate);
	// smsText = smsText.replaceAll("TIME", currentTime);
	// smsText = smsText.replaceAll("YNAME", activeZoneValue);
	// smsText = URLEncoder.encode(smsText);
	//
	// SmsParamEntity smsParam = smsParamRepo.getById(1L);
	// String smsURL = smsParam.getUrl();
	//
	// String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
	// StandardCharsets.UTF_8.toString());
	// String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
	// StandardCharsets.UTF_8.toString());
	// String encodedAlertTypes = URLEncoder.encode(activeZoneValue,
	// StandardCharsets.UTF_8.toString());
	//
	// smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword())
	// .replace("XROUTE", "transactional")
	// .replace("XSENDERID", smsParam.getSenderid())
	// .replace("XMOBILE", String.join(",", mobileNumbers))
	// .replace("XV1", encodedDateTime)
	// .replace("XV2", encodedBranchDesc)
	// .replace("XV3", encodedAlertTypes);
	//
	// String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
	// String decodedText = URLDecoder.decode(textPart,
	// StandardCharsets.UTF_8.toString());
	//
	// SmsLogEntity smsEntity = new SmsLogEntity();
	// smsEntity.setDatetime(new Date());
	// smsEntity.setMessage(decodedText);
	// smsEntity.setSentTo(String.join(",", mobileNumbers));
	// smsEntity.setSmsId(1L);
	// smsEntity.setStatus("N");
	// smsEntity.setType("ALERT");
	// smsEntity.setMessageRef(smsURL);
	// smsEntity.setBranchCode(branchCode);
	// smsEntity.setEscalationLogId(escalationLogId);
	//
	// BranchMasterEntity parentBranchEntity =
	// (branchEntity.getParentCode() != null)
	// ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
	// : null;
	//
	// if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null)
	// {
	// smsEntity.setBranchName(branchEntity.getBranchDesc() + " - " +
	// parentBranchEntity.getBranchDesc());
	// } else {
	// smsEntity.setBranchName(branchEntity.getBranchDesc());
	// }
	//
	// smsLogRepo.save(smsEntity);
	// System.out.println(smsEntity);
	//
	// // ✅ List mein add karo har ek entity
	// smsEntityList.add(smsEntity);
	//
	// }
	// }
	// //Here! we call only once
	// // whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNew(branchCode,
	// activeZone,escalationLogId,escalationLevel);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return smsEntityList;
	// }

	/*
	 * @SuppressWarnings("deprecation") public List<SmsLogEntity>
	 * processSmsForAlertNew(String branchCode, String activeZone, Long
	 * escalationLogId, String escalationLevel) { List<SmsLogEntity> smsEntityList =
	 * new ArrayList<>(); try { String[] activeZoneArray = activeZone.split(",");
	 * 
	 * for (String activeZoneValue : activeZoneArray) { activeZoneValue =
	 * activeZoneValue.trim();
	 * 
	 * // ✅ Step 1: Fetch escalation matrix with critical_flag = 1
	 * List<EscalationMatrix> escalationMatrixList =
	 * escalationMatrixRepo.findByCriticalFlag(1);
	 * 
	 * List<String> mobileNumbers = new ArrayList<>();
	 * 
	 * // ✅ Step 2: Loop through escalation matrix recipients for (EscalationMatrix
	 * escalationMatrix : escalationMatrixList) { String recipientType =
	 * escalationMatrix.getRecipient(); // "BRANCH" or "ZONE"
	 * 
	 * if ("BRANCH".equalsIgnoreCase(recipientType)) { // ✅ Type = BRANCH → directly
	 * use branchCode passed in param BranchMasterEntity branchEntity =
	 * branchMasterRepository.findBybranchCode(branchCode); if (branchEntity !=
	 * null) { addMobileNumbers(mobileNumbers, branchEntity); }
	 * 
	 * } else if ("ZONE".equalsIgnoreCase(recipientType)) { // ✅ Type = ZONE → check
	 * branchmaster for this branchCode BranchMasterEntity branchEntity =
	 * branchMasterRepository.findBybranchCode(branchCode);
	 * 
	 * if (branchEntity != null) { if
	 * ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) { // branchType is
	 * already ZONE → use directly addMobileNumbers(mobileNumbers, branchEntity);
	 * 
	 * } else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) { //
	 * branchType is BRANCH → get parentCode and find parent in branchmaster String
	 * parentCode = branchEntity.getParentCode(); if (parentCode != null &&
	 * !parentCode.trim().isEmpty()) { BranchMasterEntity parentBranchEntity =
	 * branchMasterRepository.findBybranchCode(parentCode); if (parentBranchEntity
	 * != null) { addMobileNumbers(mobileNumbers, parentBranchEntity); } } } } }else
	 * {
	 * System.out.println("IT IS NOT BRANCH or NOT ZONAL PLEASE CHECK BRANCH TYPE");
	 * } }
	 * 
	 * // ✅ Remove duplicates mobileNumbers =
	 * mobileNumbers.stream().distinct().collect(Collectors.toList());
	 * 
	 * System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);
	 * 
	 * if (mobileNumbers.isEmpty()) {
	 * System.out.println("No mobile numbers found, skipping SMS for zone: " +
	 * activeZoneValue); continue; }
	 * 
	 * // ✅ Step 3: Fetch SMS template and send BranchMasterEntity branchEntity =
	 * branchMasterRepository.findBybranchCode(branchCode); List<Object[]>
	 * staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");
	 * 
	 * for (Object[] object : staticData) { String smsText =
	 * String.valueOf(object[2] == null ? "" : object[2]); smsText =
	 * smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
	 * 
	 * SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	 * SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	 * 
	 * String currentDate = dateFormat.format(new Date()); String currentTime =
	 * timeFormat.format(new Date());
	 * 
	 * smsText = smsText.replaceAll("DATE", currentDate); smsText =
	 * smsText.replaceAll("TIME", currentTime); smsText =
	 * smsText.replaceAll("YNAME", activeZoneValue); smsText =
	 * URLEncoder.encode(smsText);
	 * 
	 * SmsParamEntity smsParam = smsParamRepo.getById(1L); String smsURL =
	 * smsParam.getUrl();
	 * 
	 * String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
	 * StandardCharsets.UTF_8.toString()); String encodedBranchDesc =
	 * URLEncoder.encode(branchEntity.getBranchDesc(),
	 * StandardCharsets.UTF_8.toString()); String encodedAlertTypes =
	 * URLEncoder.encode(activeZoneValue, StandardCharsets.UTF_8.toString());
	 * 
	 * smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword()) .replace("XROUTE",
	 * "transactional") .replace("XSENDERID", smsParam.getSenderid())
	 * .replace("XMOBILE", String.join(",", mobileNumbers)) .replace("XV1",
	 * encodedDateTime) .replace("XV2", encodedBranchDesc) .replace("XV3",
	 * encodedAlertTypes);
	 * 
	 * String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6); String
	 * decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());
	 * 
	 * SmsLogEntity smsEntity = new SmsLogEntity(); smsEntity.setDatetime(new
	 * Date()); smsEntity.setMessage(decodedText);
	 * smsEntity.setSentTo(String.join(",", mobileNumbers)); smsEntity.setSmsId(1L);
	 * smsEntity.setStatus("N"); smsEntity.setType("ALERT");
	 * smsEntity.setMessageRef(smsURL); smsEntity.setBranchCode(branchCode);
	 * smsEntity.setEscalationLogId(escalationLogId);
	 * 
	 * BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() !=
	 * null) ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
	 * : null;
	 * 
	 * if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null)
	 * { smsEntity.setBranchName(branchEntity.getBranchDesc() + " - " +
	 * parentBranchEntity.getBranchDesc()); } else {
	 * smsEntity.setBranchName(branchEntity.getBranchDesc()); }
	 * 
	 * smsLogRepo.save(smsEntity); System.out.println(smsEntity);
	 * 
	 * smsEntityList.add(smsEntity); } } //Here! we call only once
	 * whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNew(branchCode,
	 * activeZone,escalationLogId,escalationLevel);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return smsEntityList; }
	 */

	/**
	 * processSmsForAlertNew
	 *
	 * activeZone format (NEW): "alertType|groupName,alertType|groupName,..."
	 *
	 * For SMS text substitution only the alertType part is used. groupName is
	 * parsed out here for the DB lookup but is NOT shown in the SMS body.
	 */
	@SuppressWarnings("deprecation")
	public List<SmsLogEntity> processSmsForAlertForAlarm(String branchCode, String activeZone, Long escalationLogId,
			String escalationLevel) {

		List<SmsLogEntity> smsEntityList = new ArrayList<>();

		try {
			String[] activeZoneArray = activeZone.split(",");

			for (String activeZoneToken : activeZoneArray) {
				activeZoneToken = activeZoneToken.trim();
				if (activeZoneToken.isBlank())
					continue;

				// Parse "alertType|groupName"
				String[] parts = activeZoneToken.split("\\|", 2);
				String alertTypeOnly = parts[0].trim(); // used in SMS text
				String groupName = parts.length > 1 ? parts[1].trim() : ""; // used for DB lookup

				// ── Step 1: Fetch escalation matrix ──────────────────────────────
				EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
						true);

				if (escalationMatrix == null) {
					System.out.println("No escalation matrix found for level: " + escalationLevel
							+ " — skipping token: " + activeZoneToken);
					continue;
				}

				if (!Boolean.TRUE.equals(escalationMatrix.getSms())) {
					System.out.println("SMS flag is OFF for level: " + escalationLevel + " — skipping.");
					continue;
				}

				System.out.println("SMS FLAG IN ESCALATION_MATRIX :: " + escalationMatrix.getSms());
				System.out.println("Escalation Matrix found for level: " + escalationLevel + " with recipient: "
						+ escalationMatrix.getRecipient());

				// ── Step 2: Resolve mobile numbers by recipient type ──────────────
				List<String> mobileNumbers = new ArrayList<>();
				String recipientType = escalationMatrix.getRecipient();

				if ("BRANCH".equalsIgnoreCase(recipientType)) {
					BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
					if (branchEntity != null) {
						addMobileNumbers(mobileNumbers, branchEntity);
					}

				} else if ("ZONE".equalsIgnoreCase(recipientType)) {
					BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
					if (branchEntity != null) {
						if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// Already a ZONE → use directly
							addMobileNumbers(mobileNumbers, branchEntity);

						} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a BRANCH → go ONE level up to ZONE only, HO nahi
							String parentCode = branchEntity.getParentCode();
							if (parentCode != null && !parentCode.trim().isEmpty()) {
								BranchMasterEntity parentBranchEntity = branchMasterRepository
										.findBybranchCode(parentCode);
								if (parentBranchEntity != null
										&& "ZONE".equalsIgnoreCase(parentBranchEntity.getBranchType())) {
									addMobileNumbers(mobileNumbers, parentBranchEntity);
								}
							}
						}
					}
				}

				// Remove duplicates
				mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());
				System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);

				if (mobileNumbers.isEmpty()) {
					System.out.println("No mobile numbers found, skipping SMS for token: " + activeZoneToken);
					continue;
				}

				// ── Step 3: Fetch SMS template and send ───────────────────────────
				BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
				List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

				for (Object[] object : staticData) {
					String smsText = String.valueOf(object[2] == null ? "" : object[2]);

					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
					String currentDate = dateFormat.format(new Date());
					String currentTime = timeFormat.format(new Date());

					smsText = smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
					smsText = smsText.replaceAll("DATE", currentDate);
					smsText = smsText.replaceAll("TIME", currentTime);
					// Use only alertTypeOnly (without groupName) in the SMS body
					smsText = smsText.replaceAll("YNAME", alertTypeOnly);
					smsText = URLEncoder.encode(smsText);

					SmsParamEntity smsParam = smsParamRepo.getById(1L);
					String smsURL = smsParam.getUrl();

					String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
							StandardCharsets.UTF_8.toString());
					String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
							StandardCharsets.UTF_8.toString());
					// Encode only alertTypeOnly for the SMS URL parameter
					String encodedAlertTypes = URLEncoder.encode(alertTypeOnly, StandardCharsets.UTF_8.toString());

					smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword()).replace("XROUTE", "transactional")
							.replace("XSENDERID", smsParam.getSenderid())
							.replace("XMOBILE", String.join(",", mobileNumbers)).replace("XV1", encodedDateTime)
							.replace("XV2", encodedBranchDesc).replace("XV3", encodedAlertTypes);

					String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
					String decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());

					SmsLogEntity smsEntity = new SmsLogEntity();
					smsEntity.setDatetime(new Date());
					smsEntity.setMessage(decodedText);
					smsEntity.setSentTo(String.join(",", mobileNumbers));
					smsEntity.setSmsId(1L);
					smsEntity.setStatus("N");
					smsEntity.setType("ALERT");
					smsEntity.setMessageRef(smsURL);
					smsEntity.setBranchCode(branchCode);
					smsEntity.setEscalationLogId(escalationLogId);

					BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() != null)
							? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
							: null;

					if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
						smsEntity.setBranchName(
								branchEntity.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
					} else {
						smsEntity.setBranchName(branchEntity.getBranchDesc());
					}

					smsLogRepo.save(smsEntity);
					System.out.println(smsEntity);
					smsEntityList.add(smsEntity);
				}
			}

			// ── WhatsApp trigger (once, after SMS loop) ───────────────────────────
			whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNewForAlarm(branchCode, activeZone,
					escalationLogId, escalationLevel);

			// ── CALL trigger (once, after SMS loop) ──────────────────────────────
			whatsappService.sendCallMessageForBranchCodeAndMessageNewForAlarm(branchCode, activeZone, escalationLogId,
					escalationLevel);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsEntityList;
	}

	@SuppressWarnings("deprecation")
	public List<SmsLogEntity> processSmsForAlertForAlert(String branchCode, String activeZone, Long escalationLogId,
			String escalationLevel) {

		List<SmsLogEntity> smsEntityList = new ArrayList<>();

		try {
			String[] activeZoneArray = activeZone.split(",");

			for (String activeZoneToken : activeZoneArray) {
				activeZoneToken = activeZoneToken.trim();
				if (activeZoneToken.isBlank())
					continue;

				// Parse "alertType|inputType"
				String[] parts = activeZoneToken.split("\\|", 2);
				String alertTypeOnly = parts[0].trim(); // used in SMS text
				String inputType = parts.length > 1 ? parts[1].trim() : ""; // used for DB lookup

				// ── Step 1: Fetch escalation matrix ──────────────────────────────
				EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
						false);

				if (escalationMatrix == null) {
					System.out.println("No escalation matrix found for level: " + escalationLevel
							+ " — skipping token: " + activeZoneToken);
					continue;
				}

				if (!Boolean.TRUE.equals(escalationMatrix.getSms())) {
					System.out.println("SMS flag is OFF for level: " + escalationLevel + " — skipping.");
					continue;
				}

				System.out.println("SMS FLAG IN ESCALATION_MATRIX :: " + escalationMatrix.getSms());
				System.out.println("Escalation Matrix found for level: " + escalationLevel + " with recipient: "
						+ escalationMatrix.getRecipient());

				// ── Step 2: Fetch zone details (needed for VENDOR lookup) ─────────
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
				DeviceZoneDetailsEntity deviceZoneDetailsEntity = null;
				if (device != null && device.getDeviceId() != null) {
					deviceZoneDetailsEntity = deviceZoneRepo
							.findByDeviceIdAndAlertTypeAndInputType(device.getDeviceId(), alertTypeOnly, inputType);
				}

				// ── Step 3: Resolve mobile numbers by recipient type ──────────────
				List<String> mobileNumbers = new ArrayList<>();
				String recipientType = escalationMatrix.getRecipient();
				BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);

				if ("VENDOR".equalsIgnoreCase(recipientType)) {
					// Get vendor name from zone details → look up vendor master → get mobile
					if (deviceZoneDetailsEntity != null && deviceZoneDetailsEntity.getVendors() != null
							&& !deviceZoneDetailsEntity.getVendors().trim().isEmpty()) {

						Optional<VendorMasterEntity> vendorMaster = vendorRepo
								.findByVendorName(deviceZoneDetailsEntity.getVendors());

						if (vendorMaster != null) {
							// Vendor has its own mobile fields — add them directly
							addVendorMobileNumbers(mobileNumbers, vendorMaster.get());
						} else {
							System.out.println("No vendor found for name: " + deviceZoneDetailsEntity.getVendors()
									+ " — skipping.");
						}
					} else {
						System.out.println("Zone details or vendor name is null/empty — skipping VENDOR SMS.");
					}

				} else if ("BRANCH".equalsIgnoreCase(recipientType)) {
					// Send to branch contacts directly
					if (branchEntity != null) {
						addMobileNumbers(mobileNumbers, branchEntity);
					}

				} else if ("ZONE".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {
						if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// Already a ZONE → use directly
							addMobileNumbers(mobileNumbers, branchEntity);

						} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a BRANCH → go ONE level up to ZONE only, not HO
							String parentCode = branchEntity.getParentCode();
							if (parentCode != null && !parentCode.trim().isEmpty()) {
								BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
								if (parentEntity != null && "ZONE".equalsIgnoreCase(parentEntity.getBranchType())) {
									addMobileNumbers(mobileNumbers, parentEntity);
								}
							}
						}
					}

				} else if ("HEAD OFFICE".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {

						if ("HEAD OFFICE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// Already at HEAD OFFICE → use directly
							addMobileNumbers(mobileNumbers, branchEntity);

						} else if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a ZONE → go ONE level up, check if parent is HEAD OFFICE
							String parentCode = branchEntity.getParentCode();
							if (parentCode != null && !parentCode.trim().isEmpty()) {
								BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
								if (parentEntity != null
										&& "HEAD OFFICE".equalsIgnoreCase(parentEntity.getBranchType())) {
									addMobileNumbers(mobileNumbers, parentEntity);
								} else {
									System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO SMS.");
								}
							}

						} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a BRANCH → go up to ZONE → then up to HEAD OFFICE (two levels)
							String zoneCode = branchEntity.getParentCode();
							if (zoneCode != null && !zoneCode.trim().isEmpty()) {
								BranchMasterEntity zoneEntity = branchMasterRepository.findBybranchCode(zoneCode);
								if (zoneEntity != null && "ZONE".equalsIgnoreCase(zoneEntity.getBranchType())) {
									String hoCode = zoneEntity.getParentCode();
									if (hoCode != null && !hoCode.trim().isEmpty()) {
										BranchMasterEntity hoEntity = branchMasterRepository.findBybranchCode(hoCode);
										if (hoEntity != null
												&& "HEAD OFFICE".equalsIgnoreCase(hoEntity.getBranchType())) {
											addMobileNumbers(mobileNumbers, hoEntity);
										} else {
											System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO SMS.");
										}
									}
								} else if (zoneEntity != null
										&& "HEAD OFFICE".equalsIgnoreCase(zoneEntity.getBranchType())) {
									addMobileNumbers(mobileNumbers, zoneEntity);
									System.out.println("Parent of BRANCH is HEAD OFFICE — added HO SMS.");
								}
							}
						}
					}
				}

				// ── Remove duplicates ─────────────────────────────────────────────
				mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());
				System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);

				if (mobileNumbers.isEmpty()) {
					System.out.println("No mobile numbers found, skipping SMS for token: " + activeZoneToken);
					continue;
				}

				// ── Step 4: Fetch SMS template and send ───────────────────────────
				List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

				for (Object[] object : staticData) {
					String smsText = String.valueOf(object[2] == null ? "" : object[2]);

					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
					String currentDate = dateFormat.format(new Date());
					String currentTime = timeFormat.format(new Date());

					smsText = smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
					smsText = smsText.replaceAll("DATE", currentDate);
					smsText = smsText.replaceAll("TIME", currentTime);
					// Use only alertTypeOnly (without inputType) in the SMS body
					smsText = smsText.replaceAll("YNAME", alertTypeOnly);
					smsText = URLEncoder.encode(smsText);

					SmsParamEntity smsParam = smsParamRepo.getById(1L);
					String smsURL = smsParam.getUrl();

					String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
							StandardCharsets.UTF_8.toString());
					String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
							StandardCharsets.UTF_8.toString());
					// Encode only alertTypeOnly for the URL parameter
					String encodedAlertTypes = URLEncoder.encode(alertTypeOnly, StandardCharsets.UTF_8.toString());

					smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword()).replace("XROUTE", "transactional")
							.replace("XSENDERID", smsParam.getSenderid())
							.replace("XMOBILE", String.join(",", mobileNumbers)).replace("XV1", encodedDateTime)
							.replace("XV2", encodedBranchDesc).replace("XV3", encodedAlertTypes);

					String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
					String decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());

					SmsLogEntity smsEntity = new SmsLogEntity();
					smsEntity.setDatetime(new Date());
					smsEntity.setMessage(decodedText);
					smsEntity.setSentTo(String.join(",", mobileNumbers));
					smsEntity.setSmsId(1L);
					smsEntity.setStatus("N");
					smsEntity.setType("ALERT");
					smsEntity.setMessageRef(smsURL);
					smsEntity.setBranchCode(branchCode);
					smsEntity.setEscalationLogId(escalationLogId);

					BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() != null)
							? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
							: null;

					if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
						smsEntity.setBranchName(
								branchEntity.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
					} else {
						smsEntity.setBranchName(branchEntity.getBranchDesc());
					}

					smsLogRepo.save(smsEntity);
					System.out.println(smsEntity);
					smsEntityList.add(smsEntity);
				}
			}

			// ── WhatsApp trigger (once, after SMS loop) ───────────────────────────
			whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNewForAlart(branchCode, activeZone,
					escalationLogId, escalationLevel);

			// ── CALL trigger (once, after SMS loop) ──────────────────────────────
			whatsappService.sendCallMessageForBranchCodeAndMessageNewForAlart(branchCode, activeZone, escalationLogId,
					escalationLevel);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsEntityList;
	}

	// =========================================================================
	// HELPER — VendorMasterEntity mobile numbers
	// Vendor has its own mobile field(s) — adjust field names to match your entity
	// =========================================================================
	private void addVendorMobileNumbers(List<String> mobileNumbers, VendorMasterEntity vendorMaster) {
		if (vendorMaster.getMobile() != null && !vendorMaster.getMobile().trim().isEmpty()) {
			mobileNumbers.add(vendorMaster.getMobile().replaceFirst("^91", ""));
		}

	}

	// 12March commented code for non - flag
	// @SuppressWarnings("deprecation")
	// public List<SmsLogEntity> processSmsForAlertNew(String branchCode, String
	// activeZone, Long escalationLogId, String escalationLevel) {
	// List<SmsLogEntity> smsEntityList = new ArrayList<>();
	// try {
	// String[] activeZoneArray = activeZone.split(",");
	//
	// for (String activeZoneValue : activeZoneArray) {
	// activeZoneValue = activeZoneValue.trim();
	//
	// // Step 1: Fetch escalation matrix by escalationLevel and critical_flag =
	// true
	// EscalationMatrix escalationMatrix =
	// escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel, true);
	// if(escalationMatrix.getSms().equals(true)) {
	// System.out.println("SMS FLAG IN ESCALATION_MATRIX ::"+
	// escalationMatrix.getSms());
	// System.out.println("Escalation Matrix found for level: " + escalationLevel +
	// " with recipient: " + escalationMatrix.getRecipient());
	// List<String> mobileNumbers = new ArrayList<>();
	//
	// if (escalationMatrix != null) {
	// String recipientType = escalationMatrix.getRecipient();
	//
	// if ("BRANCH".equalsIgnoreCase(recipientType)) {
	// BranchMasterEntity branchEntity =
	// branchMasterRepository.findBybranchCode(branchCode);
	// if (branchEntity != null) {
	// addMobileNumbers(mobileNumbers, branchEntity);
	// }
	//
	// } else if ("ZONE".equalsIgnoreCase(recipientType)) {
	// BranchMasterEntity branchEntity =
	// branchMasterRepository.findBybranchCode(branchCode);
	// if (branchEntity != null) {
	// if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
	// // Already a ZONE → use directly
	// addMobileNumbers(mobileNumbers, branchEntity);
	//
	// } else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
	// // It's a BRANCH → go ONE level up to ZONE only, HO nahi
	// String parentCode = branchEntity.getParentCode();
	// if (parentCode != null && !parentCode.trim().isEmpty()) {
	// BranchMasterEntity parentBranchEntity =
	// branchMasterRepository.findBybranchCode(parentCode);
	// if (parentBranchEntity != null
	// && "ZONE".equalsIgnoreCase(parentBranchEntity.getBranchType())) { // ✅ ZONE
	// tak hi
	// addMobileNumbers(mobileNumbers, parentBranchEntity);
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// // Remove duplicates
	// mobileNumbers =
	// mobileNumbers.stream().distinct().collect(Collectors.toList());
	//
	// System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);
	//
	// if (mobileNumbers.isEmpty()) {
	// System.out.println("No mobile numbers found, skipping SMS for zone: " +
	// activeZoneValue);
	// continue;
	// }
	//
	// // Step 2: Fetch SMS template and send
	// BranchMasterEntity branchEntity =
	// branchMasterRepository.findBybranchCode(branchCode);
	// List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE",
	// "ALERT");
	//
	// for (Object[] object : staticData) {
	// String smsText = String.valueOf(object[2] == null ? "" : object[2]);
	// smsText = smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
	//
	// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	// SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	//
	// String currentDate = dateFormat.format(new Date());
	// String currentTime = timeFormat.format(new Date());
	//
	// smsText = smsText.replaceAll("DATE", currentDate);
	// smsText = smsText.replaceAll("TIME", currentTime);
	// smsText = smsText.replaceAll("YNAME", activeZoneValue);
	// smsText = URLEncoder.encode(smsText);
	//
	// SmsParamEntity smsParam = smsParamRepo.getById(1L);
	// String smsURL = smsParam.getUrl();
	//
	// String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
	// StandardCharsets.UTF_8.toString());
	// String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
	// StandardCharsets.UTF_8.toString());
	// String encodedAlertTypes = URLEncoder.encode(activeZoneValue,
	// StandardCharsets.UTF_8.toString());
	//
	// smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword())
	// .replace("XROUTE", "transactional")
	// .replace("XSENDERID", smsParam.getSenderid())
	// .replace("XMOBILE", String.join(",", mobileNumbers))
	// .replace("XV1", encodedDateTime)
	// .replace("XV2", encodedBranchDesc)
	// .replace("XV3", encodedAlertTypes);
	//
	// String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
	// String decodedText = URLDecoder.decode(textPart,
	// StandardCharsets.UTF_8.toString());
	//
	// SmsLogEntity smsEntity = new SmsLogEntity();
	// smsEntity.setDatetime(new Date());
	// smsEntity.setMessage(decodedText);
	// smsEntity.setSentTo(String.join(",", mobileNumbers));
	// smsEntity.setSmsId(1L);
	// smsEntity.setStatus("N");
	// smsEntity.setType("ALERT");
	// smsEntity.setMessageRef(smsURL);
	// smsEntity.setBranchCode(branchCode);
	// smsEntity.setEscalationLogId(escalationLogId);
	//
	// BranchMasterEntity parentBranchEntity =
	// (branchEntity.getParentCode() != null)
	// ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
	// : null;
	//
	// if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null)
	// {
	// smsEntity.setBranchName(branchEntity.getBranchDesc() + " - " +
	// parentBranchEntity.getBranchDesc());
	// } else {
	// smsEntity.setBranchName(branchEntity.getBranchDesc());
	// }
	//
	// smsLogRepo.save(smsEntity);
	// System.out.println(smsEntity);
	// smsEntityList.add(smsEntity);
	// }
	// }
	// }
	//
	// // WhatsApp trigger (once, after SMS loop)
	// whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNewForAlarm(
	// branchCode, activeZone, escalationLogId, escalationLevel);
	// // CALL trigger (once, after SMS loop)
	// whatsappService.sendCallMessageForBranchCodeAndMessageNewForAlarm(
	// branchCode, activeZone, escalationLogId, escalationLevel);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return smsEntityList;
	// }

	/*
	 * Last working code in testing server
	 * 
	 * @SuppressWarnings("deprecation") public List<SmsLogEntity>
	 * processSmsForAlertNew(String branchCode, String activeZone, Long
	 * escalationLogId, String escalationLevel) { List<SmsLogEntity> smsEntityList =
	 * new ArrayList<>(); try { String[] activeZoneArray = activeZone.split(",");
	 * 
	 * for (String activeZoneValue : activeZoneArray) { activeZoneValue =
	 * activeZoneValue.trim();
	 * 
	 * // ✅ Step 1: Fetch escalation matrix by escalationLevel and critical_flag =
	 * true EscalationMatrix escalationMatrix =
	 * escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel, true);
	 * 
	 * List<String> mobileNumbers = new ArrayList<>();
	 * 
	 * if (escalationMatrix != null) { String recipientType =
	 * escalationMatrix.getRecipient();
	 * 
	 * if ("BRANCH".equalsIgnoreCase(recipientType)) { BranchMasterEntity
	 * branchEntity = branchMasterRepository.findBybranchCode(branchCode); if
	 * (branchEntity != null) { addMobileNumbers(mobileNumbers, branchEntity); }
	 * 
	 * } else if ("ZONE".equalsIgnoreCase(recipientType)) { BranchMasterEntity
	 * branchEntity = branchMasterRepository.findBybranchCode(branchCode); if
	 * (branchEntity != null) { if
	 * ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
	 * addMobileNumbers(mobileNumbers, branchEntity);
	 * 
	 * } else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) { String
	 * parentCode = branchEntity.getParentCode(); if (parentCode != null &&
	 * !parentCode.trim().isEmpty()) { BranchMasterEntity parentBranchEntity =
	 * branchMasterRepository.findBybranchCode(parentCode); if (parentBranchEntity
	 * != null) { addMobileNumbers(mobileNumbers, parentBranchEntity); } } } } } }
	 * 
	 * // ✅ Remove duplicates mobileNumbers =
	 * mobileNumbers.stream().distinct().collect(Collectors.toList());
	 * 
	 * System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);
	 * 
	 * if (mobileNumbers.isEmpty()) {
	 * System.out.println("No mobile numbers found, skipping SMS for zone: " +
	 * activeZoneValue); continue; }
	 * 
	 * // ✅ Step 2: Fetch SMS template and send BranchMasterEntity branchEntity =
	 * branchMasterRepository.findBybranchCode(branchCode); List<Object[]>
	 * staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");
	 * 
	 * for (Object[] object : staticData) { String smsText =
	 * String.valueOf(object[2] == null ? "" : object[2]); smsText =
	 * smsText.replaceAll("XNAME", branchEntity.getBranchDesc());
	 * 
	 * SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	 * SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	 * 
	 * String currentDate = dateFormat.format(new Date()); String currentTime =
	 * timeFormat.format(new Date());
	 * 
	 * smsText = smsText.replaceAll("DATE", currentDate); smsText =
	 * smsText.replaceAll("TIME", currentTime); smsText =
	 * smsText.replaceAll("YNAME", activeZoneValue); smsText =
	 * URLEncoder.encode(smsText);
	 * 
	 * SmsParamEntity smsParam = smsParamRepo.getById(1L); String smsURL =
	 * smsParam.getUrl();
	 * 
	 * String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
	 * StandardCharsets.UTF_8.toString()); String encodedBranchDesc =
	 * URLEncoder.encode(branchEntity.getBranchDesc(),
	 * StandardCharsets.UTF_8.toString()); String encodedAlertTypes =
	 * URLEncoder.encode(activeZoneValue, StandardCharsets.UTF_8.toString());
	 * 
	 * smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword()) .replace("XROUTE",
	 * "transactional") .replace("XSENDERID", smsParam.getSenderid())
	 * .replace("XMOBILE", String.join(",", mobileNumbers)) .replace("XV1",
	 * encodedDateTime) .replace("XV2", encodedBranchDesc) .replace("XV3",
	 * encodedAlertTypes);
	 * 
	 * String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6); String
	 * decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());
	 * 
	 * SmsLogEntity smsEntity = new SmsLogEntity(); smsEntity.setDatetime(new
	 * Date()); smsEntity.setMessage(decodedText);
	 * smsEntity.setSentTo(String.join(",", mobileNumbers)); smsEntity.setSmsId(1L);
	 * smsEntity.setStatus("N"); smsEntity.setType("ALERT");
	 * smsEntity.setMessageRef(smsURL); smsEntity.setBranchCode(branchCode);
	 * smsEntity.setEscalationLogId(escalationLogId);
	 * 
	 * BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() !=
	 * null) ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
	 * : null;
	 * 
	 * if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null)
	 * { smsEntity.setBranchName(branchEntity.getBranchDesc() + " - " +
	 * parentBranchEntity.getBranchDesc()); } else {
	 * smsEntity.setBranchName(branchEntity.getBranchDesc()); }
	 * 
	 * smsLogRepo.save(smsEntity); System.out.println(smsEntity);
	 * smsEntityList.add(smsEntity); } }
	 * whatsappService.sendWhatsAppMessageForBranchCodeAndMessageNew(branchCode,
	 * activeZone,escalationLogId,escalationLevel); } catch (Exception e) {
	 * e.printStackTrace(); } return smsEntityList; }
	 */
	// ✅ Helper method to extract and add mobile numbers from a BranchMasterEntity
	private void addMobileNumbers(List<String> mobileNumbers, BranchMasterEntity branchEntity) {
		if (branchEntity.getMobile() != null && !branchEntity.getMobile().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobile().replaceFirst("^91", ""));
		}
		if (branchEntity.getMobileSecondary() != null && !branchEntity.getMobileSecondary().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobileSecondary().replaceFirst("^91", ""));
		}
		if (branchEntity.getMobileTertiary() != null && !branchEntity.getMobileTertiary().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobileTertiary().replaceFirst("^91", ""));
		}
		if (branchEntity.getMobileQuaternary() != null && !branchEntity.getMobileQuaternary().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobileQuaternary().replaceFirst("^91", ""));
		}
		if (branchEntity.getMobileQuinary() != null && !branchEntity.getMobileQuinary().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobileQuinary().replaceFirst("^91", ""));
		}
		if (branchEntity.getMobileSenary() != null && !branchEntity.getMobileSenary().trim().isEmpty()) {
			mobileNumbers.add(branchEntity.getMobileSenary().replaceFirst("^91", ""));
		}
	}

	@SuppressWarnings("deprecation")
	public SmsLogEntity processSmsForAlert(String branchCode, String activeZone, Long escalationLogId) {
		SmsLogEntity smsEntity = null;
		try {
			String activeZoneList = activeZone;
			// System.out.println("ACTIVE ZONES --------------->>>>>>>>>>>>>>>>>>>>>>" +
			// activeZone);
			String[] activeZoneArray = activeZoneList.split("_");

			// System.out.println("Splitting activeZone by underscore and printing values
			// before colon:");
			String[] zoneNames = new String[activeZoneArray.length];
			StringBuilder concatenatedZones = new StringBuilder();
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

			// System.out.println("ZONE NAMES concatenated---->>>>>>>>>> " +
			// concatenatedZones.toString());

			String[] zoneValuesForValidation = concatenatedZones.toString().split(",\\s*");
			StringBuilder matchedAlertTypes = new StringBuilder();
			for (String activeZoneValue : zoneValuesForValidation) {
				activeZoneValue = activeZoneValue.trim();
				// System.out.println("Active Zone Value for Validation: " + activeZoneValue);

				BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
				// System.out.println("Branch Name: " + branchEntity.getBranchDesc());
				List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

				List<String> mobileNumbers = new ArrayList<>();
				if (branchEntity.getMobile() != null && !branchEntity.getMobile().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobile().replaceFirst("^91", ""));
				}
				if (branchEntity.getMobileSecondary() != null && !branchEntity.getMobileSecondary().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobileSecondary().replaceFirst("^91", ""));
				}
				if (branchEntity.getMobileTertiary() != null && !branchEntity.getMobileTertiary().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobileTertiary().replaceFirst("^91", ""));
				}
				if (branchEntity.getMobileQuaternary() != null
						&& !branchEntity.getMobileQuaternary().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobileQuaternary().replaceFirst("^91", ""));
				}
				if (branchEntity.getMobileQuinary() != null && !branchEntity.getMobileQuinary().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobileQuinary().replaceFirst("^91", ""));
				}
				if (branchEntity.getMobileSenary() != null && !branchEntity.getMobileSenary().trim().isEmpty()) {
					mobileNumbers.add(branchEntity.getMobileSenary().replaceFirst("^91", ""));
				}
				// *************START SOCIAL MEDIA REQUEST***************
				// org.springframework.http.ResponseEntity<List<SocialMediaDTO>> socialMediaResp
				// =
				// socialMediaService.getSocialMediaDetailsByBranchCode(branchCode);
				//
				// if (socialMediaResp == null ||
				// socialMediaResp.getStatusCode() != HttpStatus.OK ||
				// socialMediaResp.getBody() == null ||
				// socialMediaResp.getBody().isEmpty()) {
				//
				// logger.warn("No social media details found for branchCode: " + branchCode);
				// return null;
				// }
				//
				// List<SocialMediaDTO> responseList = socialMediaResp.getBody();
				//
				// if (responseList != null && !responseList.isEmpty()) {
				// for (SocialMediaDTO socialMediaDTO : responseList) {
				// String alertType = socialMediaDTO.getAlertType();
				// String sms = socialMediaDTO.getSms();
				//
				// if (activeZoneValue.equals(alertType) && "Y".equalsIgnoreCase(sms)) {
				// // System.out.println("Matched Alert Type: " + alertType + " and SMS: " +
				// sms);
				// matchedAlertTypes.append(alertType).append(", ");
				// }
				// }
				// }
				// ***************END SOCIAL MEDIA REQUEST**************

				// 🛑 Stop if no active zones with Y flag found
				// if (matchedAlertTypes.length() == 0) {
				// logger.info("No active zones with Y flag for branchCode: " + branchCode + ".
				// SMS not sent.");
				// return null;
				// }
				//

				for (Object[] object : staticData) {
					String smsText = String.valueOf(object[2] == null ? "" : object[2]);
					// System.out.println("THE COMPLETE SMS TEXT \n" + smsText);
					smsText = smsText.replaceAll("XNAME", branchEntity.getBranchDesc());

					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

					String currentDate = dateFormat.format(new Date());
					String currentTime = timeFormat.format(new Date());

					smsText = smsText.replaceAll("DATE", currentDate);
					// System.out.println(currentTime);
					smsText = smsText.replaceAll("TIME", currentTime);

					smsText = smsText.replaceAll("YNAME", matchedAlertTypes.toString());

					smsText = URLEncoder.encode(smsText);

					// System.out.println("FINAL READY SMS \n" + smsText);

					// SmsParamEntity smsParam = smsParamRepo.getById(1L);
					// String smsURL = smsParam.getUrl();
					// smsURL = smsURL.replace("XUSERID", smsParam.getUserid())
					// .replace("XPASSWORD", smsParam.getPassword()).replace("XSENDERID",
					// smsParam.getSenderid())
					// .replace("XSENDERMOBILE", String.join(",", mobileNumbers)).replace("XTEXT",
					// smsText);
					//// System.out.println("***********************************");
					//// System.out.println(smsURL);

					// Remove trailing comma from matchedAlertTypes
					if (matchedAlertTypes.length() > 0 && matchedAlertTypes.toString().endsWith(", ")) {
						matchedAlertTypes.setLength(matchedAlertTypes.length() - 2);
					}

					SmsParamEntity smsParam = smsParamRepo.getById(1L);

					String smsURL = smsParam.getUrl();

					// URL encode the dynamic values before replacing
					String encodedDateTime = URLEncoder.encode(currentDate + " " + currentTime,
							StandardCharsets.UTF_8.toString());
					String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
							StandardCharsets.UTF_8.toString());
					String encodedAlertTypes = URLEncoder.encode(matchedAlertTypes.toString(),
							StandardCharsets.UTF_8.toString());

					smsURL = smsURL.replace("XAPIKEY", smsParam.getPassword()).replace("XROUTE", "transactional")
							.replace("XSENDERID", smsParam.getSenderid())
							.replace("XMOBILE", String.join(",", mobileNumbers)).replace("XV1", encodedDateTime)
							.replace("XV2", encodedBranchDesc).replace("XV3", encodedAlertTypes);

					String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
					String decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());

					smsEntity = new SmsLogEntity();
					smsEntity.setDatetime(new Date());
					smsEntity.setMessage(decodedText);
					smsEntity.setSentTo(String.join(",", mobileNumbers));
					smsEntity.setSmsId(1L);
					smsEntity.setStatus("N");
					smsEntity.setType("ALERT");
					smsEntity.setMessageRef(smsURL);
					smsEntity.setBranchCode(branchCode);
					smsEntity.setEscalationLogId(escalationLogId);
					BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() != null)
							? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
							: null;

					if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
						smsEntity.setBranchName(
								branchEntity.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
					} else {
						smsEntity.setBranchName(branchEntity.getBranchDesc());
					}

					smsLogRepo.save(smsEntity);

					whatsappService.sendWhatsAppMessageForBranchCodeAndMessage(branchCode, activeZone, escalationLogId);
					System.out.println(smsEntity);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smsEntity;
	}

	// public ResponseEntity sendSmsByBranchCodeNew(String branchCode, String
	// activeZone, Long escalationLogId) {
	//
	// System.out.println("incoming activeZone: " + activeZone);
	//
	// ResponseEntity response = new ResponseEntity();
	//
	// try {
	//
	// if (activeZone == null || activeZone.isBlank()) {
	// response.setMessage("Active zone is empty.");
	// response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	// return response;
	// }
	//
	// // split alerts by comma
	// String[] activeZoneArray = activeZone.split(",");
	//
	// String deviceId =
	// deviceMasterRepository.findBybranchCode(branchCode).getDeviceId();
	//
	// if (deviceId != null) {
	//
	// List<String> criticalZones = new ArrayList<>();
	//
	// for (String zone : activeZoneArray) {
	//
	// if (zone == null || zone.isBlank()) continue;
	//
	// // remove extra spaces
	// String alertType = zone.trim();
	//
	// DeviceZoneDetailsEntity entity =
	// deviceZoneRepo.findByDeviceIdAndAlertType(deviceId, alertType);
	//
	// if (entity != null && "Y".equalsIgnoreCase(entity.getCriticalAlertFlag())) {
	// criticalZones.add(alertType);
	// }
	// }
	//
	// // no critical alerts
	// if (criticalZones.isEmpty()) {
	// response.setMessage("No critical-alert zones – SMS not sent.");
	// response.setStatusCode(HttpStatus.OK.value());
	// return response;
	// }
	//
	// // rebuild filtered alerts
	// activeZone = String.join(",", criticalZones);
	// }
	//
	// System.out.println("filtered activeZone: " + activeZone);
	//
	// List<SmsLogEntity> smsLogEntity = processSmsForAlertNew(branchCode,
	// activeZone, escalationLogId);
	//
	// if (smsLogEntity != null) {
	//
	// ResponseEntity responseEntity =
	// communicationService.sendCommunication(List.of(smsLogEntity));
	//
	// response.setPayload(responseEntity);
	// response.setMessage("SMS Sent Successfully.");
	// response.setStatusCode(HttpStatus.OK.value());
	//
	// } else {
	//
	// response.setMessage("No SMS sent.");
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// }
	//
	// } catch (Exception e) {
	//
	// response.setMessage("Error Occurred: " + e.getMessage());
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// }
	//
	// return response;
	// }

	/**
	 * sendSmsByBranchCodeNew
	 *
	 * activeZone format (NEW): "alertType|groupName,alertType|groupName,..." e.g.
	 * "FIRE|ZONE1,INTRUSION|ZONE2,FIRE|ZONE3"
	 *
	 * alertType alone can be duplicate across zones. groupName (from ticket) makes
	 * each entry unique so we can look up the exact DeviceZoneDetailsEntity via
	 * findByDeviceIdAndAlertTypeAndGroupName().
	 */
	public ResponseEntity sendSmsByBranchCodeForAlarm(String branchCode, String activeZone, Long escalationLogId,
			String escalationLevel) {

		System.out.println("incoming activeZone: " + activeZone);

		ResponseEntity response = new ResponseEntity();

		try {
			if (activeZone == null || activeZone.isBlank()) {
				response.setMessage("Active zone is empty.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			// ── Each token is "alertType|groupName" ──────────────────────────────
			String[] activeZoneArray = activeZone.split(",");

			DeviceMasterEntity deviceMaster = deviceMasterRepository.findBybranchCode(branchCode);

			if (deviceMaster != null) {
				String deviceId = deviceMaster.getDeviceId();

				if (deviceId != null) {
					List<String> criticalZones = new ArrayList<>();

					for (String zone : activeZoneArray) {
						if (zone == null || zone.isBlank())
							continue;

						// Split "alertType|groupName"
						String[] parts = zone.trim().split("\\|", 2);
						String alertType = parts[0].trim();
						String groupName = parts.length > 1 ? parts[1].trim() : "";

						// Precise lookup using BOTH alertType AND groupName
						DeviceZoneDetailsEntity entity = deviceZoneRepo.findByDeviceIdAndAlertTypeAndInputType(deviceId,
								alertType, groupName);

						if (entity != null && "Y".equalsIgnoreCase(entity.getCriticalAlertFlag())) {
							// Keep the full "alertType|groupName" token so downstream can still parse it
							criticalZones.add(zone.trim());
						} else {

							System.out.println("No matching DeviceZoneDetailsEntity for deviceId: " + deviceId
									+ ", alertType: " + alertType + ", groupName: " + groupName);
						}
					}

					if (criticalZones.isEmpty()) {
						response.setMessage("No critical-alert zones – SMS not sent.");
						response.setStatusCode(HttpStatus.OK.value());
						return response;
					}

					// Rebuild filtered zone string (still "alertType|groupName" format)
					activeZone = String.join(",", criticalZones);
				}
			}

			System.out.println("filtered activeZone (critical only): " + activeZone);

			List<SmsLogEntity> smsLogEntityList = processSmsForAlertForAlarm(branchCode, activeZone, escalationLogId,
					escalationLevel);

			if (smsLogEntityList != null && !smsLogEntityList.isEmpty()) {
				ResponseEntity responseEntity = communicationService.sendCommunication(smsLogEntityList);
				response.setPayload(responseEntity);
				response.setMessage("SMS Sent Successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No SMS sent.");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error Occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity sendSmsByBranchCodeForAlert(String branchCode, String activeZone, Long escalationLogId,
			String escalationLevel) {

		System.out.println("incoming activeZone: " + activeZone);

		ResponseEntity response = new ResponseEntity();

		try {
			if (activeZone == null || activeZone.isBlank()) {
				response.setMessage("Active zone is empty.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			// ── Each token is "alertType|groupName" ──────────────────────────────
			String[] activeZoneArray = activeZone.split(",");

			DeviceMasterEntity deviceMaster = deviceMasterRepository.findBybranchCode(branchCode);

			if (deviceMaster != null) {
				String deviceId = deviceMaster.getDeviceId();

				if (deviceId != null) {
					List<String> criticalZones = new ArrayList<>();

					for (String zone : activeZoneArray) {
						if (zone == null || zone.isBlank())
							continue;

						// Split "alertType|groupName"
						String[] parts = zone.trim().split("\\|", 2);
						String alertType = parts[0].trim();
						String groupName = parts.length > 1 ? parts[1].trim() : "";

						// Precise lookup using BOTH alertType AND groupName
						DeviceZoneDetailsEntity entity = deviceZoneRepo.findByDeviceIdAndAlertTypeAndInputType(deviceId,
								alertType, groupName);

						criticalZones.add(zone.trim());
					}

					if (criticalZones.isEmpty()) {
						response.setMessage("No critical-alert zones – SMS not sent.");
						response.setStatusCode(HttpStatus.OK.value());
						return response;
					}

					// Rebuild filtered zone string (still "alertType|groupName" format)
					activeZone = String.join(",", criticalZones);
				}
			}

			System.out.println("filtered activeZone (critical only): " + activeZone);

			List<SmsLogEntity> smsLogEntityList = processSmsForAlertForAlert(branchCode, activeZone, escalationLogId,
					escalationLevel);

			if (smsLogEntityList != null && !smsLogEntityList.isEmpty()) {
				ResponseEntity responseEntity = communicationService.sendCommunication(smsLogEntityList);
				response.setPayload(responseEntity);
				response.setMessage("SMS Sent Successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No SMS sent.");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error Occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}
	// public ResponseEntity sendSmsByBranchCodeNew(String branchCode, String
	// activeZone, Long escalationLogId,String escalationLevel) {
	//
	// System.out.println("incoming activeZone: " + activeZone);
	//
	// ResponseEntity response = new ResponseEntity();
	//
	// try {
	//
	// if (activeZone == null || activeZone.isBlank()) {
	// response.setMessage("Active zone is empty.");
	// response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	// return response;
	// }
	//
	// // split alerts by comma
	// String[] activeZoneArray = activeZone.split(",");
	//
	// DeviceMasterEntity deviceMaster =
	// deviceMasterRepository.findBybranchCode(branchCode);
	//
	// if (deviceMaster != null) {
	//
	// String deviceId = deviceMaster.getDeviceId();
	//
	// if (deviceId != null) {
	//
	// List<String> criticalZones = new ArrayList<>();
	//
	// for (String zone : activeZoneArray) {
	//
	// if (zone == null || zone.isBlank()) continue;
	//
	// String alertType = zone.trim();
	//
	// DeviceZoneDetailsEntity entity =
	// deviceZoneRepo.findByDeviceIdAndAlertType(deviceId, alertType);
	//
	// if (entity != null && "Y".equalsIgnoreCase(entity.getCriticalAlertFlag())) {
	// criticalZones.add(alertType);
	// }
	// }
	//
	// // no critical alerts found
	// if (criticalZones.isEmpty()) {
	// response.setMessage("No critical-alert zones – SMS not sent.");
	// response.setStatusCode(HttpStatus.OK.value());
	// return response;
	// }
	//
	// // rebuild filtered zone string
	// activeZone = String.join(",", criticalZones);
	// }
	// }
	//
	// System.out.println("filtered activeZone: " + activeZone);
	//
	// // returns List<SmsLogEntity>
	// List<SmsLogEntity> smsLogEntityList = processSmsForAlertNew(branchCode,
	// activeZone, escalationLogId,escalationLevel);
	//
	// // ✅ FIX: check for null AND empty, pass the list directly (not wrapped in
	// List.of())
	// if (smsLogEntityList != null && !smsLogEntityList.isEmpty()) {
	//
	// ResponseEntity responseEntity =
	// communicationService.sendCommunication(smsLogEntityList); // ✅ pass list
	// directly
	//
	// response.setPayload(responseEntity);
	// response.setMessage("SMS Sent Successfully.");
	// response.setStatusCode(HttpStatus.OK.value());
	//
	// } else {
	//
	// response.setMessage("No SMS sent.");
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// }
	//
	// } catch (Exception e) {
	//
	// e.printStackTrace(); // ✅ log full stack trace, not just message
	// response.setMessage("Error Occurred: " + e.getMessage());
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// }
	//
	// return response;
	// }

	public ResponseEntity sendSmsByBranchCode(String branchCode, String activeZone, Long escalationLogId) {

		System.out.println("incoming activeZone: " + activeZone);

		boolean hasLeadingUnderscore = activeZone.startsWith("_");
		String[] activeZoneArray = activeZone.split("_");

		String deviceId = deviceMasterRepository.findBybranchCode(branchCode).getDeviceId();

		if (deviceId != null) {
			List<String> criticalZones = new ArrayList<>();

			for (String zone : activeZoneArray) {
				if (zone == null || zone.isBlank())
					continue;

				String[] parts = zone.split(":");
				String alertType = parts[0].trim();

				DeviceZoneDetailsEntity entity = deviceZoneRepo.findByDeviceIdAndAlertType(deviceId, alertType);

				if (entity != null && "Y".equalsIgnoreCase(entity.getCriticalAlertFlag())) {
					criticalZones.add(zone);
				}
			}

			if (criticalZones.isEmpty()) {
				ResponseEntity resp = new ResponseEntity();
				resp.setMessage("No critical‑alert zones – SMS not sent.");
				resp.setStatusCode(HttpStatus.OK.value());
				return resp;
			}

			// rebuild activeZone, preserving any leading underscore
			activeZone = (hasLeadingUnderscore ? "_" : "") + String.join("_", criticalZones);
		}
		/* -------------------------------------------------------------------- */

		System.out.println("filtered activeZone: " + activeZone);

		ResponseEntity response = new ResponseEntity();
		try {
			SmsLogEntity smsLogEntity = processSmsForAlert(branchCode, activeZone, escalationLogId);
			if (smsLogEntity != null) {

				ResponseEntity responseEntity = communicationService.sendCommunication(List.of(smsLogEntity));
				response.setPayload(responseEntity);
				response.setMessage("SMS Sent Successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No SMS sent.");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} catch (Exception e) {
			response.setMessage("Error Occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusReport(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			// List<SmsDetailsEntity> sms=smsDetailsRepository.findlastRecordByBroadcast();
			// for(SmsDetailsEntity smsEntity=sms) {
			// System.out.println(sms+"sms");
			// }
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			List<Map<String, Object>> branchReports = new ArrayList<>();

			for (BranchMasterEntity branch : branchList) {
				String currentBranchCode = branch.getBranchCode();
				String branchName = branch.getBranchDesc();
				String mobile = branch.getMobile();
				String bank = branch.getBank();

				DeviceMasterEntity device = deviceRepo.findBybranchCode(currentBranchCode);
				if (device == null) {
					Map<String, Object> branchReport = new HashMap<>();
					branchReport.put("branchCode", currentBranchCode);
					branchReport.put("message", "Device not found");
					branchReport.put("statusCode", HttpStatus.NOT_FOUND.value());
					branchReports.add(branchReport);
				} else {
					SmsDetailsEntity smsDetail = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());
					if (smsDetail == null) {
						List<String> zoneValues = new ArrayList<>();
						// List<String> zoneGroup = new ArrayList<>();
						List<DeviceZoneDetailsEntity> zoneList = zoneRepo.findAllByDeviceId(device.getDeviceId());
						if (!zoneList.isEmpty()) {

							for (DeviceZoneDetailsEntity item : zoneList) {

								if (item.getPrimaryFlag().equals("Y")) {
									zoneValues.add(item.getZoneDesiredName() + ": " + "N");
								}
								// else {
								// zoneGroup.add(item.getZoneDesiredName() + ": " + "N");
								// }
								//

							}
						}

						Date currentDate = general.getCurrentTimeStamp();
						Map<String, Object> responseBody = new HashMap<>();
						responseBody.put("deviceName", device.getDeviceName());
						responseBody.put("zoneValues", zoneValues);
						// responseBody.put("zoneGroup", zoneGroup);
						zoneValues.add("DateTime: " + currentDate);

						Map<String, Object> branchReport = new HashMap<>();
						branchReport.put("branchCode", currentBranchCode);
						branchReport.put("branchName", branchName);
						branchReport.put("bankName", bank);
						branchReport.put("mobile", mobile);
						branchReport.put("report", responseBody);
						branchReports.add(branchReport);

						// Map<String, Object> branchReport = new HashMap<>();
						// branchReport.put("branchCode", currentBranchCode);
						// branchReport.put("message", "No record found");
						// branchReport.put("statusCode", HttpStatus.NOT_FOUND.value());
						// branchReports.add(branchReport);
					} else {
						String deviceName = device.getDeviceName();
						List<DeviceZoneDetailsEntity> zoneList = zoneRepo.findAllByDeviceId(device.getDeviceId());
						Map<String, String> zoneNameMap = new HashMap<>();
						Map<String, String> zoneGroupMap = new HashMap<>();
						zoneList.forEach(zone -> {
							zoneNameMap.put(zone.getZoneOriginalName(), zone.getZoneDesiredName());
							zoneGroupMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag());

						}

						);

						List<String> zoneValues = new ArrayList<>();
						// List<String> zoneGroup = new ArrayList<>();
						for (int i = 1; i <= zoneList.size(); i++) {
							String zoneValue = (String) smsDetail.getClass().getMethod("getZone" + i).invoke(smsDetail);

							if (zoneValue.equals("I")) {
								zoneValue = "A";
							}
							if (zoneValue != null && !zoneValue.isEmpty()) {
								String zoneName = zoneNameMap.get("ZONE" + i);
								String zoneFlag = zoneGroupMap.get("ZONE" + i);
								// System.out.println("---->>>>>"+zoneFlag);
								if (zoneName != null && zoneFlag.equals("Y")) {
									// System.out.println("ZONEVALUES--->>"+zoneName+"---"+zoneFlag);
									zoneValues.add(zoneName + ": " + zoneValue);

								}
								// else {
								// zoneGroup.add(zoneName + ": " + zoneValue);
								// }
							}
						}
						String dateTime = smsDetail.getDateTime().toString();
						zoneValues.add("DateTime: " + dateTime);

						Map<String, Object> responseBody = new HashMap<>();
						responseBody.put("deviceName", deviceName);
						responseBody.put("zoneValues", zoneValues);
						// responseBody.put("zoneGroup", zoneGroup);
						Map<String, Object> branchReport = new HashMap<>();
						branchReport.put("branchCode", currentBranchCode);
						branchReport.put("branchName", branchName);
						branchReport.put("bankName", bank);
						branchReport.put("mobile", mobile);
						branchReport.put("report", responseBody);
						branchReports.add(branchReport);
					}
				}
			}
			response.setMessage("Zone values retrieved for all branches");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(branchReports);

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("An error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity sendMailByBranchCode(String branchCode, String activeZone) {
		ResponseEntity response = new ResponseEntity();

		System.out.println("activeZone--------->>>" + activeZone);

		if (activeZone == null || activeZone.trim().isEmpty() || activeZone.replace("_", "").isBlank()) {
			response.setMessage("No active zones provided – email not sent.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		boolean hasLeadingUnderscore = activeZone.startsWith("_");
		String[] activeZoneArray = activeZone.split("_");

		String deviceId = deviceMasterRepository.findBybranchCode(branchCode).getDeviceId();

		/* ✅ Filter: Keep only critical alert zones */
		if (deviceId != null) {
			List<String> criticalZones = new ArrayList<>();

			for (String zone : activeZoneArray) {
				if (zone == null || zone.isBlank())
					continue;

				String[] parts = zone.split(":");
				String alertType = parts[0].trim();

				DeviceZoneDetailsEntity entity = deviceZoneRepo.findByDeviceIdAndAlertType(deviceId, alertType);

				if (entity != null && "Y".equalsIgnoreCase(entity.getCriticalAlertFlag())) {
					criticalZones.add(zone);
				}
			}

			if (criticalZones.isEmpty()) {
				ResponseEntity resp = new ResponseEntity();
				resp.setMessage("No critical-alert zones – email not sent.");
				resp.setStatusCode(HttpStatus.OK.value());
				return resp;
			}

			activeZone = (hasLeadingUnderscore ? "_" : "") + String.join("_", criticalZones);
		}

		try {
			EmailLogEntity emailLogEntity = processMessageByType(branchCode, activeZone);
			if (emailLogEntity != null) {
				List<EmailLogEntity> mailLogList = List.of(emailLogEntity);

				try {
					ResponseEntity emailResponse = communicationService.sendEmail(mailParamRepo.getById("1"),
							mailLogList);

					response.setPayload(emailResponse);
					response.setMessage("Email sent at " + LocalDateTime.now());
					response.setStatusCode(HttpStatus.OK.value());

				} catch (MailSendException ex) {
					logger.error("SMTP Authentication failed while sending email: {}", ex.getMessage());

					if (ex.getMessage().contains("Too many login attempts") || ex.getMessage().contains("4.7.0")) {

						response.setMessage(
								"Email not sent – Too many SMTP login attempts. Gmail has temporarily blocked email sending. Please try again later.");
					} else {
						response.setMessage("Email not sent – Authentication failed.");
					}

					response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
					return response;

				} catch (Exception ex) {
					logger.error("Failed to send email: {}", ex.getMessage());
					response.setMessage("Failed to send email: " + ex.getMessage());
					response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
					return response;
				}

			} else {
				response.setMessage("No Email sent.");
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}

		} catch (Exception e) {
			logger.error("Error Occurred: {}", e.getMessage());
			response.setMessage("Error Occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	private EmailLogEntity processMessageByType(String branchCode, String activeZone) throws Exception {

		EmailLogEntity emailEntity = new EmailLogEntity();

		try {
			String[] activeZoneArray = activeZone.split("_");
			StringBuilder concatenatedZones = new StringBuilder();

			// Extract alert types from activeZone
			for (String zone : activeZoneArray) {
				int colonIndex = zone.indexOf(':');
				if (colonIndex != -1) {
					concatenatedZones.append(zone.substring(0, colonIndex));
					concatenatedZones.append(", ");
				}
			}

			String zoneNames = concatenatedZones.toString();
			if (zoneNames.length() > 2) {
				zoneNames = zoneNames.substring(0, zoneNames.length() - 2);
			}

			String[] zoneValuesForValidation = concatenatedZones.toString().split(",\\s*");
			StringBuilder matchedAlertTypes = new StringBuilder();

			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

			// Collect branch email recipients
			List<String> emails = new ArrayList<>();
			emails.add(branchEntity.getEmail());
			if (branchEntity.getEmail2() != null)
				emails.add(branchEntity.getEmail2());
			if (branchEntity.getEmail3() != null)
				emails.add(branchEntity.getEmail3());
			if (branchEntity.getEmail4() != null)
				emails.add(branchEntity.getEmail4());
			if (branchEntity.getEmail5() != null)
				emails.add(branchEntity.getEmail5());
			if (branchEntity.getEmail6() != null)
				emails.add(branchEntity.getEmail6());

			// -------------------------------------------
			// ✅ FETCH SOCIAL MEDIA DETAILS SAFELY
			// -------------------------------------------
			org.springframework.http.ResponseEntity<List<SocialMediaDTO>> socialMediaResp = socialMediaService
					.getSocialMediaDetailsByBranchCode(branchCode);

			if (socialMediaResp == null || socialMediaResp.getStatusCode() != HttpStatus.OK
					|| socialMediaResp.getBody() == null || socialMediaResp.getBody().isEmpty()) {

				logger.warn("No social media details found for branchCode: " + branchCode);
				return null;
			}

			List<SocialMediaDTO> socialMediaList = socialMediaResp.getBody();
			// -------------------------------------------

			// Loop through alert zones
			for (String activeZoneValue : zoneValuesForValidation) {
				activeZoneValue = activeZoneValue.trim();

				for (SocialMediaDTO socialMediaDTO : socialMediaList) {
					String alertType = socialMediaDTO.getAlertType();
					String mail = socialMediaDTO.getMail();

					if (activeZoneValue.equals(alertType) && "Y".equalsIgnoreCase(mail)) {
						matchedAlertTypes.append(alertType).append(", ");
					}
				}

				// ✅ Stop if no alert condition matches
				if (matchedAlertTypes.length() == 0) {
					logger.info("No matching alertType with mail=Y for branchCode: " + branchCode);
					return null;
				}

				// Send email ================================================
				for (Object[] object : staticData) {
					String emailSubject = String.valueOf(object[0] == null ? "" : object[0]);
					String emailBodyTemplate = String.valueOf(object[1] == null ? "" : object[1]);

					String emailBody = emailBodyTemplate.replaceAll("XNAME", branchEntity.getBranchDesc())
							.replaceAll("DATE-TIME", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()))
							.replaceAll("YNAME", matchedAlertTypes.toString());

					MailParamEntity mailParam = mailParamRepo.getById("1");

					// SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					// String formattedDateTime = formatter.format(LocalDateTime.now());
					//
					// // Convert formatted string back to Date
					// LocalDateTime dateTime = formatter.parse(formattedDateTime);

					emailEntity.setBodyText(emailBody);
					emailEntity.setDatetime(LocalDateTime.now());
					emailEntity.setEmailIdTo(emails.get(0));
					if (emails.size() > 1) {
						emailEntity.setEmailIdCc(String.join(",", emails.subList(1, emails.size())));
					}
					emailEntity.setFromEmail(mailParam.getSmtpMailId());
					emailEntity.setMailId(1L);
					emailEntity.setStatus("N");
					emailEntity.setSubject(emailSubject);
					emailEntity.setBranchCode(branchCode);

					BranchMasterEntity parentBranchEntity = (branchEntity.getParentCode() != null)
							? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
							: null;

					if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
						emailEntity.setBranchName(
								branchEntity.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
					} else {
						emailEntity.setBranchName(branchEntity.getBranchDesc());
					}

					// ✅ Skip duplicate alerts for same branch & alert type on same day
					long existingCount = emailLogRepo.countTodayEmailByBranchAndAlert(branchCode, activeZoneValue);
					if (existingCount > 0) {
						logger.info("Skipping duplicate email for branchCode: " + branchCode + ", alertType: "
								+ activeZoneValue);
						return null;
					}

					emailLogRepo.save(emailEntity);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error occurred in processMessageByType: ", e);
		}
		return emailEntity;
	}

	public ResponseEntity changeFlag() {
		ResponseEntity response = new ResponseEntity();
		try {
			int id = smsDetailsRepository.getMaxId();
			System.out.println(id);
			smsDetailsRepository.updateFlag(id);
			if (id != 0) {
				smsDetailsRepository.updateFlag(id);
				System.out.println(id);
				response.setMessage("Flag changed successfully");

			} else {
				response.setMessage("Error occured while changing flag");
			}

		} catch (Exception e) {
		}
		return response;
	}

	public ResponseEntity encryptAndDecryptMessage() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<String> responseList = new ArrayList<String>();
			String message = "##2401030138 12.04.2024 02:40:33,10060,ANNNNNNN,DDDDDDDD,NNNNNNNNNNNNNNNNNNNNNNNN,#";
			String secretKey = generateSecretKey();
			responseList.add("Original message: " + message);
			String encryptedMessage = EncryptionDecryption.encrypt(message, secretKey);
			System.out.println("Encrypted message: " + encryptedMessage);
			responseList.add("Encrypted message: " + encryptedMessage);
			String decryptedMessage = EncryptionDecryption.decrypt(encryptedMessage, secretKey);
			System.out.println("Decrypted message: " + decryptedMessage);
			responseList.add("Decrypted message: " + decryptedMessage);
			response.setPayload(responseList);
			response.setMessage("Encryption and decryption successful");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	private static String generateSecretKey() {
		byte[] key = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(key);
		return Base64.getEncoder().encodeToString(key);
	}

	// public ResponseEntity getCountOfWorkingDevices(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	// try {
	// int workingDeviceCountString = 0, notWorkingCount = 0;
	//
	// ResponseEntity listEntity = branchService.getAllChildren(branchCode);
	// if (listEntity.getPayload() != null) {
	// @SuppressWarnings("unchecked")
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// listEntity.getPayload();
	// for (BranchMasterEntity x : branchList) {
	//
	// DeviceMasterEntity deviceMasterEntity =
	// deviceMasterRepository.findBybranchCode(x.getBranchCode());
	// if (deviceMasterEntity != null) {
	// SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
	// .getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
	//
	// if (smsDetailsEntity == null) {
	// workingDeviceCountString++;
	// } else {
	//
	// Boolean flagBoolean = false;
	// List<DeviceZoneDetailsEntity> zoneList = zoneRepo
	// .findAllByDeviceId(deviceMasterEntity.getDeviceId());
	// Map<String, String> zoneNameMap = new HashMap<>();
	// Map<String, String> zoneFlagMap = new HashMap<>();
	//
	// zoneList.forEach(
	// zone -> {zoneNameMap.put(zone.getZoneOriginalName(),
	// zone.getZoneDesiredName());
	//
	// zoneFlagMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag());
	// });
	//
	//
	//
	// for (String zoneKey : zoneNameMap.keySet()) {
	// // Convert "ZONE1" to "Zone1"
	// String formattedZoneKey = "Zone" + zoneKey.substring(4);
	//
	//
	// String zoneValue = (String) smsDetailsEntity.getClass().getMethod("get" +
	// formattedZoneKey)
	// .invoke(smsDetailsEntity);
	//
	// String primaryFlag = zoneFlagMap.get(zoneKey);
	//
	//
	//
	// if ("Y".equalsIgnoreCase(primaryFlag) &&
	// (zoneValue.equalsIgnoreCase("A") || zoneValue.equalsIgnoreCase("I") ||
	// zoneValue.equalsIgnoreCase("D"))) {
	//
	// flagBoolean = true;
	// break;
	// }
	//
	// }
	//
	// if (!flagBoolean) {
	//
	//
	// workingDeviceCountString++;
	// } else {
	//
	// notWorkingCount++;
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// Map<String, Object> responseBody = new HashMap<>();
	// responseBody.put("workingDeviceCount", workingDeviceCountString);
	// responseBody.put("notWorkingCount", notWorkingCount);
	//
	// response.setPayload(responseBody);
	//
	// }
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// return response;
	// }

	public ResponseEntity getCountOfWorkingDevices(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		try {

			int workingDeviceCount = 0, notWorkingCount = 0;

			ResponseEntity listEntity = branchService.getAllChildren(branchCode);

			// Default payload so null never comes
			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("workingDeviceCount", 0);
			responseBody.put("notWorkingCount", 0);

			if (listEntity.getPayload() != null) {

				@SuppressWarnings("unchecked")
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) listEntity.getPayload();

				for (BranchMasterEntity x : branchList) {

					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(x.getBranchCode());

					if (deviceMasterEntity != null) {

						SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
								.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());

						if (smsDetailsEntity == null) {
							continue;
						}

						Date recordTime = smsDetailsEntity.getDateTime();

						if (recordTime != null) {

							long sixHoursMillis = 12 * 60 * 60 * 1000L;

							if (System.currentTimeMillis() - recordTime.getTime() > sixHoursMillis) {
								continue;
							}
						}

						boolean notWorkingFlag = false;

						List<DeviceZoneDetailsEntity> zoneList = zoneRepo
								.findAllByDeviceId(deviceMasterEntity.getDeviceId());

						Map<String, String> zoneFlagMap = new HashMap<>();

						zoneList.forEach(zone -> zoneFlagMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag()));

						for (String zoneKey : zoneFlagMap.keySet()) {

							// Convert "ZONE1" -> "Zone1"
							String formattedZoneKey = "Zone" + zoneKey.substring(4);

							String zoneValue = (String) smsDetailsEntity.getClass().getMethod("get" + formattedZoneKey)
									.invoke(smsDetailsEntity);

							String primaryFlag = zoneFlagMap.get(zoneKey);

							if ("Y".equalsIgnoreCase(primaryFlag) && (zoneValue.equalsIgnoreCase("A")
									|| zoneValue.equalsIgnoreCase("I") || zoneValue.equalsIgnoreCase("D"))) {

								notWorkingFlag = true;
								break;
							}
						}

						if (!notWorkingFlag) {
							workingDeviceCount++;
						} else {
							notWorkingCount++;
						}
					}
				}

				responseBody.put("workingDeviceCount", workingDeviceCount);
				responseBody.put("notWorkingCount", notWorkingCount);
			}

			response.setPayload(responseBody);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Device count fetched successfully");

		} catch (Exception e) {

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("workingDeviceCount", 0);
			responseBody.put("notWorkingCount", 0);

			response.setPayload(responseBody);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			e.printStackTrace();
			response.setMessage("Error while processing devices: " + e.getMessage());
		}

		return response;
	}

	public ResponseEntity getAllNotWorkingDevices(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			// List<SmsDetailsEntity> sms=smsDetailsRepository.findlastRecordByBroadcast();
			// for(SmsDetailsEntity smsEntity=sms) {
			// System.out.println(sms+"sms");
			// }
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No data found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			List<Map<String, Object>> branchReports = new ArrayList<>();

			for (BranchMasterEntity branch : branchList) {
				String currentBranchCode = branch.getBranchCode();
				String branchName = branch.getBranchDesc();
				String mobile = branch.getMobile();
				String bank = branch.getBank();

				DeviceMasterEntity device = deviceRepo.findBybranchCode(currentBranchCode);
				if (device == null) {
					Map<String, Object> branchReport = new HashMap<>();
					branchReport.put("branchCode", currentBranchCode);
					branchReport.put("message", "Device not found");
					branchReport.put("statusCode", HttpStatus.NOT_FOUND.value());
					branchReports.add(branchReport);
				} else {
					SmsDetailsEntity smsDetail = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());
					if (smsDetail == null) {

						Map<String, Object> branchReport = new HashMap<>();
						branchReport.put("branchCode", currentBranchCode);
						branchReport.put("message", "No record found");
						branchReport.put("statusCode", HttpStatus.NOT_FOUND.value());
						branchReports.add(branchReport);
					} else {
						String deviceName = device.getDeviceName();
						List<DeviceZoneDetailsEntity> zoneList = zoneRepo.findAllByDeviceId(device.getDeviceId());
						Map<String, String> zoneNameMap = new HashMap<>();
						Map<String, String> zoneAlertMap = new HashMap<>();
						for (DeviceZoneDetailsEntity zone : zoneList) {
							zoneNameMap.put(zone.getZoneOriginalName(), zone.getZoneDesiredName());
							zoneAlertMap.put(zone.getZoneOriginalName(), zone.getAlertType());
						}
						List<String> zoneValues = new ArrayList<>();
						for (int i = 1; i <= 40; i++) {
							String zoneValue = (String) smsDetail.getClass().getMethod("getZone" + i).invoke(smsDetail);

							if (zoneValue.equals("A")) {

								if (zoneValue != null && !zoneValue.isEmpty()) {
									String zoneName = zoneNameMap.get("ZONE" + i);
									String zoneAlert = zoneAlertMap.get("ZONE" + i);
									if (zoneName != null) {
										zoneValues.add(zoneName + ": " + zoneAlert);
									}
								}
							}
						}
						String dateTime = smsDetail.getDateTime().toString();

						Map<String, Object> responseBody = new HashMap<>();
						responseBody.put("deviceName", deviceName);
						responseBody.put("DateTime", dateTime);
						responseBody.put("zoneValues", zoneValues);

						Map<String, Object> branchReport = new HashMap<>();
						branchReport.put("branchCode", currentBranchCode);
						branchReport.put("branchName", branchName);
						branchReport.put("bankName", bank);
						branchReport.put("mobile", mobile);
						branchReport.put("report", responseBody);
						branchReports.add(branchReport);
					}
				}
			}
			response.setMessage("Zone values retrieved for all branches");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(branchReports);

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("An error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	// public ResponseEntity getAllAlertsReport(String branchCode ,int page, int
	// size) {
	// ResponseEntity response = new ResponseEntity();
	// try {
	// ResponseEntity responsePayload = branchService.getAllChildren(branchCode);
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// responsePayload.getPayload();
	// List<AllAlarmsDTO> alarmList= new ArrayList<>();
	// Page<SmsDetailsEntity> smsRecord=null;
	// if(branchList!=null) {
	// Pageable pageable = PageRequest.of(page, size);
	// branchList.forEach(branch->{
	// DeviceMasterEntity deviceMasterEntity =
	// deviceMasterRepository.findBybranchCode(branch.getBranchCode());
	// if(deviceMasterEntity!=null) {
	// smsRecord =
	// smsDetailsRepository.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo(),
	// pageable);
	// List<SmsDetailsEntity> smsRecordPage = smsRecord.getContent();
	//
	//
	//// List<SmsDetailsEntity> smsRecord =
	// smsDetailsRepository.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo(),
	// pageable);
	// if(smsRecord.isEmpty()) {
	// response.setMessage("No record found for this device");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// }else {
	// smsRecord.forEach(alarmRecord->{
	// for (int i = 1; i <= 40; i++) {
	// String zoneValue = "";
	// try {
	// zoneValue = (String) alarmRecord.getClass().getMethod("getZone" +
	// i).invoke(alarmRecord);
	// } catch (IllegalAccessException | IllegalArgumentException
	// | InvocationTargetException | NoSuchMethodException | SecurityException e) {
	// e.printStackTrace();
	// }
	// if(zoneValue.equals("A")) {
	// String zoneOriginalName = "ZONE"+i;
	// DeviceZoneDetailsEntity deviceZoneDetailsEntity =
	// deviceZoneRepo.findByDeviceIdAndZoneName(deviceMasterEntity.getDeviceId(),
	// zoneOriginalName);
	// if(deviceZoneDetailsEntity!=null) {
	// AllAlarmsDTO alarmResponse = new AllAlarmsDTO();
	// alarmResponse.setBank(branch.getBank());
	// alarmResponse.setAddress(branch.getAddress1()+"," +branch.getAddress2()+" ,
	// "+branch.getAddress3()+" , "+branch.getAddress4());
	// alarmResponse.setBranchCode(branch.getBranchCode());
	// alarmResponse.setBranchName(branch.getBranchDesc());
	// alarmResponse.setIfsc(branch.getIfsc());
	// alarmResponse.setVendorName(deviceMasterEntity.getVendorName());
	// alarmResponse.setAlertType(deviceZoneDetailsEntity.getAlertType());
	// alarmResponse.setZoneDesiredName(deviceZoneDetailsEntity.getZoneDesiredName());
	// alarmResponse.setTimeStamp(alarmRecord.getDateTime().toString());
	// alarmList.add(alarmResponse);
	// }
	// }
	//
	// }
	//
	//
	// });
	// }
	// }else {
	// response.setMessage("Device not found for this branch");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// }
	// });
	// response.setPayload(alarmList);
	// response.setMessage("Alarms Fetched successfully");
	// response.setStatusCode(HttpStatus.OK.value());
	//
	// response.setTotalElements(smsRecord.getTotalElements());
	// response.setTotalPages(smsRecord.getTotalPages());
	// response.setCurrentPage(smsRecord.getNumber());
	//
	//
	// }
	//
	// }catch (Exception e) {
	// // TODO: handle exception
	// }
	// return response;
	// }

	// public ResponseEntity getAllAlertsReport(String branchCode, int page, int
	// size) {
	// ResponseEntity response = new ResponseEntity();
	// try {
	// ResponseEntity responsePayload = branchService.getAllChildren(branchCode);
	// @SuppressWarnings("unchecked")
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// responsePayload.getPayload();
	// List<AllAlarmsDTO> alarmList = new ArrayList<>();
	//
	// if (branchList != null) {
	// for (BranchMasterEntity branch : branchList) {
	// DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
	// .findBybranchCode(branch.getBranchCode());
	// if (deviceMasterEntity != null) {
	// List<SmsDetailsEntity> smsRecord = smsDetailsRepository
	// .findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo());
	// if (!smsRecord.isEmpty()) {
	// for (SmsDetailsEntity alarmRecord : smsRecord) {
	// for (int i = 1; i <= 40; i++) {
	// String zoneValue = "";
	// try {
	// zoneValue = (String) alarmRecord.getClass().getMethod("getZone" + i)
	// .invoke(alarmRecord);
	// } catch (IllegalAccessException | IllegalArgumentException
	// | InvocationTargetException | NoSuchMethodException | SecurityException e) {
	// e.printStackTrace();
	// }
	// if ("A".equals(zoneValue) || "I".equals(zoneValue)) {
	// String zoneOriginalName = "ZONE" + i;
	// DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneRepo
	// .findByDeviceIdAndZoneName(deviceMasterEntity.getDeviceId(),
	// zoneOriginalName);
	// if (deviceZoneDetailsEntity != null) {
	// AllAlarmsDTO alarmResponse = new AllAlarmsDTO();
	// alarmResponse.setBank(branch.getBank());
	// alarmResponse.setAddress(branch.getAddress1() + "," + branch.getAddress2()
	// + " , " + branch.getAddress3() + " , " + branch.getAddress4());
	// alarmResponse.setBranchCode(branch.getBranchCode());
	// alarmResponse.setBranchName(branch.getBranchDesc());
	// alarmResponse.setIfsc(branch.getIfsc());
	// if(!"GPRS".equalsIgnoreCase(alarmRecord.getSenderMobileNo())) {
	// alarmResponse.setCommunicationType("SMS");
	// }else {
	// alarmResponse.setCommunicationType("GPRS");
	// }
	// alarmResponse.setVendorName(deviceMasterEntity.getVendorName());
	// alarmResponse.setAlertType(deviceZoneDetailsEntity.getAlertType());
	// alarmResponse
	// .setZoneDesiredName(deviceZoneDetailsEntity.getZoneDesiredName());
	// alarmResponse.setTimeStamp(alarmRecord.getDateTime().toString());
	// alarmResponse.setDeviceType(deviceMasterEntity.getProtectionType());
	// alarmList.add(alarmResponse);
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	//
	//
	// alarmList.sort((a1, a2) -> {
	// return a2.getTimeStamp().compareTo(a1.getTimeStamp());
	// });
	//
	// // Correct pagination logic
	// int totalElements = alarmList.size();
	// int totalPages = (int) Math.ceil((double) totalElements / size) - 1;
	// int start = page * size;
	// int end = Math.min(start + size, totalElements);
	//
	// if (start < totalElements) {
	// List<AllAlarmsDTO> pagedAlarmList = alarmList.subList(start, end);
	// response.setPayload(pagedAlarmList);
	// response.setMessage("Alarms fetched successfully");
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setTotalElements(totalElements);
	// response.setTotalPages(totalPages);
	// response.setCurrentPage(page);
	// } else {
	// response.setPayload(Collections.emptyList());
	// response.setMessage("No records found for the requested page");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// }
	// } else {
	// response.setPayload(Collections.emptyList());
	// response.setMessage("No branches found");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// }
	// } catch (Exception e) {
	// response.setMessage("An error occurred");
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// e.printStackTrace();
	// }
	// return response;
	// }

	public ResponseEntity getAllAlertsReport(String branchCode, int page, int size, String zoneDesiredName,
			String alertType, String fromDate, String toDate) {
		ResponseEntity response = new ResponseEntity();
		try {
			// Get all branches first
			ResponseEntity responsePayload = branchService.getAllChildren(branchCode);

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responsePayload.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setPayload(Collections.emptyList());
				response.setMessage("No branches found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			// Extract branch codes
			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.collect(Collectors.toList());

			// Batch fetch devices
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);

			Map<String, DeviceMasterEntity> deviceByBranchCode = allDevices.stream().collect(Collectors
					.toMap(DeviceMasterEntity::getBranchCode, device -> device, (existing, replacement) -> existing));

			// Extract serial numbers
			List<String> deviceSerialNos = allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.collect(Collectors.toList());

			// Batch fetch SMS records (from both tables based on date range)
			List<SmsDetailsEntity> allSmsRecords = fetchSmsRecords(deviceSerialNos, fromDate, toDate);

			Map<String, List<SmsDetailsEntity>> smsByDeviceSerial = allSmsRecords.stream()
					.collect(Collectors.groupingBy(SmsDetailsEntity::getPanelSerialNo));

			// Extract device IDs for zone query
			List<String> deviceIds = allDevices.stream().map(DeviceMasterEntity::getDeviceId)
					.collect(Collectors.toList());

			// Batch fetch zone details
			List<DeviceZoneDetailsEntity> allZones = deviceZoneRepo.findByDeviceIdIn(deviceIds);

			Map<String, DeviceZoneDetailsEntity> zoneByDeviceAndZone = allZones.stream()
					.collect(Collectors.toMap(zone -> zone.getDeviceId() + "_" + zone.getZoneOriginalName(),
							zone -> zone, (existing, replacement) -> existing));

			Map<String, BranchMasterEntity> branchMap = branchList.stream()
					.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, branch -> branch));

			// Step 1: Build alarm list
			List<AllAlarmsDTO> alarmList = processAlarmsOptimized(branchMap, deviceByBranchCode, smsByDeviceSerial,
					zoneByDeviceAndZone);

			// Step 2: Apply filters here (before pagination)
			alarmList = alarmList.stream()
					.filter(dto -> (zoneDesiredName == null || zoneDesiredName.isBlank()
							|| dto.getZoneDesiredName().equalsIgnoreCase(zoneDesiredName)))
					.filter(dto -> (alertType == null || alertType.isBlank()
							|| dto.getAlertType().equalsIgnoreCase(alertType)))
					.filter(dto -> {
						if (fromDate == null || toDate == null || fromDate.isBlank() || toDate.isBlank())
							return true;

						// DB timestamp format
						DateTimeFormatter smsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

						// Parse SMS timestamp from DB
						LocalDateTime timestamp = LocalDateTime.parse(dto.getTimeStamp(), smsFormatter);

						// Parse only date from request (yyyy-MM-dd)
						LocalDate from = LocalDate.parse(fromDate);
						LocalDate to = LocalDate.parse(toDate);

						// Convert to time range (whole day inclusive)
						LocalDateTime fromDateTime = from.atStartOfDay();
						LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

						return !timestamp.isBefore(fromDateTime) && !timestamp.isAfter(toDateTime);
					}).collect(Collectors.toList());

			// Sort by latest timestamp
			alarmList.sort((a1, a2) -> a2.getTimeStamp().compareTo(a1.getTimeStamp()));

			// Apply pagination
			return paginateResults(alarmList, page, size, response);

		} catch (Exception e) {
			response.setMessage("An error occurred: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			e.printStackTrace();
			return response;
		}
	}

	/**
	 * Fetches SMS records from both sms_details and sms_details_history tables
	 * based on the date range provided
	 */
	private List<SmsDetailsEntity> fetchSmsRecords(List<String> deviceSerialNos, String fromDate, String toDate) {
		List<SmsDetailsEntity> allSmsRecords = new ArrayList<>();

		// Calculate the cutoff date (2 days ago from now)
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime twoDaysAgo = now.minusDays(2);
		Date twoDaysAgoDate = Date.from(twoDaysAgo.atZone(ZoneId.systemDefault()).toInstant());

		// If no date range is provided, fetch only from current table (last 2 days)
		if (fromDate == null || toDate == null || fromDate.isBlank() || toDate.isBlank()) {
			allSmsRecords = smsDetailsRepository.findByPanelSerialNoIn(deviceSerialNos);
			return allSmsRecords;
		}

		// Parse date range
		LocalDate from = LocalDate.parse(fromDate);
		LocalDate to = LocalDate.parse(toDate);
		LocalDateTime fromDateTime = from.atStartOfDay();
		LocalDateTime toDateTime = to.plusDays(1).atStartOfDay(); // Include end date

		// Convert to Date objects for repository queries
		Date fromDateObj = Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date toDateObj = Date.from(toDateTime.atZone(ZoneId.systemDefault()).toInstant());

		// Determine if we need to fetch from history table
		boolean needCurrentTable = !toDateTime.isBefore(twoDaysAgo);
		boolean needHistoryTable = fromDateTime.isBefore(twoDaysAgo);

		// Fetch from current table (sms_details) if date range overlaps with last 2
		// days
		if (needCurrentTable) {
			List<SmsDetailsEntity> currentRecords = smsDetailsRepository.findByPanelSerialNoIn(deviceSerialNos);
			allSmsRecords.addAll(currentRecords);
		}

		// Fetch from history table if date range goes beyond 2 days
		if (needHistoryTable) {
			List<SmsDetailsHistoryEntity> historyRecords = smsDetailsHistoryRepository
					.findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(deviceSerialNos,
							fromDateObj, toDateObj);

			// Convert history entities to regular entities
			List<SmsDetailsEntity> convertedHistoryRecords = historyRecords.stream().map(this::convertHistoryToEntity)
					.collect(Collectors.toList());

			allSmsRecords.addAll(convertedHistoryRecords);
		}

		// Remove duplicates if any (based on unique identifier)
		// Assuming there's a unique ID field in SmsDetailsEntity
		allSmsRecords = allSmsRecords.stream().distinct().collect(Collectors.toList());

		return allSmsRecords;
	}

	/**
	 * Converts SmsDetailsHistoryEntity to SmsDetailsEntity Adjust field mappings
	 * based on your actual entity structure
	 */
	private SmsDetailsEntity convertHistoryToEntity(SmsDetailsHistoryEntity historyEntity) {
		SmsDetailsEntity entity = new SmsDetailsEntity();

		// Map all fields from history to current entity
		entity.setPanelSerialNo(historyEntity.getPanelSerialNo());
		entity.setDateTime(historyEntity.getDateTime());
		entity.setSenderMobileNo(historyEntity.getSenderMobileNo());

		// Map all 40 zones
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

		// Map any other fields that exist in both entities
		// entity.setId(historyEntity.getId()); // Be careful with IDs

		return entity;
	}

	// Keep all other existing methods unchanged
	private List<AllAlarmsDTO> processAlarmsOptimized(Map<String, BranchMasterEntity> branchMap,
			Map<String, DeviceMasterEntity> deviceByBranchCode, Map<String, List<SmsDetailsEntity>> smsByDeviceSerial,
			Map<String, DeviceZoneDetailsEntity> zoneByDeviceAndZone) {

		List<AllAlarmsDTO> alarmList = new ArrayList<>();

		for (BranchMasterEntity branch : branchMap.values()) {
			DeviceMasterEntity device = deviceByBranchCode.get(branch.getBranchCode());
			if (device == null)
				continue;

			List<SmsDetailsEntity> smsRecords = smsByDeviceSerial.get(device.getDeviceSerialNo());
			if (smsRecords == null || smsRecords.isEmpty())
				continue;

			for (SmsDetailsEntity smsRecord : smsRecords) {
				Map<String, String> zoneValues = extractZoneValues(smsRecord);

				for (Map.Entry<String, String> zoneEntry : zoneValues.entrySet()) {
					String zoneName = zoneEntry.getKey();
					String zoneValue = zoneEntry.getValue();

					if ("A".equals(zoneValue) || "I".equals(zoneValue)) {
						String zoneKey = device.getDeviceId() + "_" + zoneName;
						DeviceZoneDetailsEntity zoneDetails = zoneByDeviceAndZone.get(zoneKey);

						if (zoneDetails != null) {
							AllAlarmsDTO alarm = createAlarmDTO(branch, device, smsRecord, zoneDetails);
							alarmList.add(alarm);
						}
					}
				}
			}
		}

		return alarmList;
	}

	private Map<String, String> extractZoneValues(SmsDetailsEntity smsRecord) {
		Map<String, String> zoneValues = new HashMap<>();
		Class<?> clazz = smsRecord.getClass();

		for (int i = 1; i <= 40; i++) {
			try {
				Method method = clazz.getMethod("getZone" + i);
				String zoneValue = (String) method.invoke(smsRecord);
				if (zoneValue != null && ("A".equals(zoneValue) || "I".equals(zoneValue))) {
					zoneValues.put("ZONE" + i, zoneValue);
				}
			} catch (Exception e) {
				System.err.println("Error accessing zone" + i + ": " + e.getMessage());
			}
		}

		return zoneValues;
	}

	private AllAlarmsDTO createAlarmDTO(BranchMasterEntity branch, DeviceMasterEntity device,
			SmsDetailsEntity smsRecord, DeviceZoneDetailsEntity zoneDetails) {
		AllAlarmsDTO alarm = new AllAlarmsDTO();

		BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
				? branchMasterRepository.findBybranchCode(branch.getParentCode())
				: null;

		alarm.setBank(branch.getBank());
		alarm.setAddress(String.join(", ", branch.getAddress1(), branch.getAddress2(), branch.getAddress3(),
				branch.getAddress4()));
		alarm.setBranchCode(branch.getBranchCode());
		if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
			alarm.setBranchName(branch.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
		} else {
			alarm.setBranchName(branch.getBranchDesc());
		}

		alarm.setIfsc(branch.getIfsc());
		alarm.setCommunicationType("GPRS".equalsIgnoreCase(smsRecord.getSenderMobileNo()) ? "GPRS" : "SMS");
		alarm.setVendorName(zoneDetails.getVendors());
		alarm.setAlertType(zoneDetails.getAlertType());
		alarm.setZoneDesiredName(zoneDetails.getZoneDesiredName());
		alarm.setTimeStamp(smsRecord.getDateTime().toString());
		alarm.setDeviceType(device.getProtectionType());

		return alarm;
	}

	private ResponseEntity paginateResults(List<AllAlarmsDTO> alarmList, int page, int size, ResponseEntity response) {
		int totalElements = alarmList.size();
		int totalPages = (int) Math.ceil((double) totalElements / size);
		int start = page * size;
		int end = Math.min(start + size, totalElements);

		if (start < totalElements) {
			List<AllAlarmsDTO> pagedAlarmList = alarmList.subList(start, end);
			response.setPayload(pagedAlarmList);
			response.setMessage("Alarms fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
			response.setTotalElements(totalElements);
			response.setTotalPages(totalPages);
			response.setCurrentPage(page);
		} else {
			response.setPayload(Collections.emptyList());
			response.setMessage("No records found for the requested page");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		}

		return response;
	}

	public ResponseEntity makeCall(@RequestBody CallDTO callDTO) {
		String url = "https://api.voicensms.in/OBDAPI/webresources/CreateOBDCampaignPost";

		ResponseEntity response = new ResponseEntity();

		String ukey = paramService.getParamByCode("UKEY");
		String serviceNo = paramService.getParamByCode("SERVICE_NO");
		String ivrTemplateId = paramService.getParamByCode("IVR_TEMPLATE_ID");
		String retryAtmpt = paramService.getParamByCode("RETRY_ATMPT");
		String retryduration = paramService.getParamByCode("RETRY_DURATION");

		callDTO.setUkey(ukey);
		callDTO.setServiceno(serviceNo);
		callDTO.setIvrtemplateid(ivrTemplateId);
		callDTO.setRetryatmpt(Integer.parseInt(retryAtmpt));
		callDTO.setRetryduration(Integer.parseInt(retryduration));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CallDTO> requestEntity = new HttpEntity<>(callDTO, headers);

		RestTemplate restTemplate = new RestTemplate();
		String responseBody = null;

		try {
			org.springframework.http.ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					requestEntity, String.class);
			System.out.println("RESPONSE FROM VOICENSMS: " + responseEntity.getBody());
			responseBody = responseEntity.getBody();

			// ✅ Save logs for each msisdn entry
			for (MsisdnList msisdn : callDTO.getMsisdnlist()) {
				CallLogEntity log = new CallLogEntity();

				BranchMasterEntity branchMasterEntity = branchMasterRepository.findByBranchDesc(msisdn.getBranchName());

				if (branchMasterEntity != null) {
					log.setBranchCode(branchMasterEntity.getBranchCode());
				}

				BranchMasterEntity parentBranchEntity = (branchMasterEntity.getParentCode() != null)
						? branchMasterRepository.findBybranchCode(branchMasterEntity.getParentCode())
						: null;

				if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
					log.setBranchName(msisdn.getBranchName() + " - " + parentBranchEntity.getBranchDesc());
				} else {
					log.setBranchName(msisdn.getBranchName());
				}

				log.setPhoneNo(msisdn.getPhoneno());
				log.setProductType(msisdn.getProductType());
				log.setReason(msisdn.getReason());
				log.setContactNo(msisdn.getContactNo());
				log.setRequestPayload(callDTO.toString()); // You may use ObjectMapper to save JSON
				log.setResponsePayload(responseBody);
				log.setCreatedAt(LocalDateTime.now());

				callLogRepository.save(log);
				response.setPayload(log);
				response.setMessage(responseBody);
				response.setStatusCode(HttpStatus.OK.value());
				;
			}

			return response;
		} catch (Exception e) {
			// Save failed attempt in logs also
			for (MsisdnList msisdn : callDTO.getMsisdnlist()) {
				CallLogEntity log = new CallLogEntity();
				log.setBranchName(msisdn.getBranchName());
				log.setPhoneNo(msisdn.getPhoneno());
				log.setProductType(msisdn.getProductType());
				log.setReason(msisdn.getReason());
				log.setContactNo(msisdn.getContactNo());
				log.setCreatedAt(LocalDateTime.now());
				log.setRequestPayload(callDTO.toString());
				log.setResponsePayload("ERROR: " + e.getMessage());

				callLogRepository.save(log);
			}

			return response;
		}
	}

	// public ResponseEntity getCountsByStatusOfAllDevices(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	// try {
	// List<Map<String, Object>> branchReports = new ArrayList<>();
	// ResponseEntity branchResponseList = branchService.getAllChildren(branchCode);
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseList.getPayload();
	//
	// if (branchList.isEmpty()) {
	// response.setMessage("Branches not found on this branch code :" + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// } else {
	// branchList.forEach(branch -> {
	// List<String> zoneStatusList = new ArrayList<>();
	// Map<String, Object> responseMap = new HashMap<>();
	//
	// String dateTime = general.getCurrentTimeStamp().toString();
	// String currentBranchCode = branch.getBranchCode();
	// String branchName = branch.getBranchDesc();
	// String mobile = branch.getMobile();
	// String bank = branch.getBank();
	//
	// DeviceMasterEntity device =
	// deviceMasterRepository.findBybranchCode(branch.getBranchCode());
	// if (device == null) {
	// response.setPayload("Device not found for this branchcode :" +
	// branch.getBranchCode());
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// } else {
	// List<DeviceZoneDetailsEntity> deviceZoneList =
	// deviceZoneRepo.findByDeviceId(device.getDeviceId());
	// if (deviceZoneList.isEmpty()) {
	// response.setMessage("Zones not found for this device :" +
	// device.getDeviceId());
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// } else {
	// SmsDetailsEntity smsLastRecord =
	// smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());
	// if (smsLastRecord == null) {
	// deviceZoneList.forEach(zone -> {
	// zoneStatusList.add(zone.getZoneOriginalName() + ":" +
	// zone.getZoneDesiredName() + ":" + zone.getAlertType() + ":" +
	// zone.getPrimaryFlag() + ":N");
	// });
	// } else {
	// dateTime = smsLastRecord.getDateTime().toString();
	// deviceZoneList.forEach(zone -> {
	// String zoneName = zone.getZoneOriginalName();
	// zoneName = StringUtils.capitalize(zoneName.toLowerCase());
	//
	// String methodName = "get" + zoneName;
	// try {
	// String zoneValue = (String)
	// smsLastRecord.getClass().getMethod(methodName).invoke(smsLastRecord);
	//
	// if("I".equals(zoneValue)||"D".equals(zoneValue)) {
	// zoneValue="A";
	// }
	// zoneStatusList.add(zone.getZoneOriginalName() + ":" +
	// zone.getZoneDesiredName() + ":" + zone.getAlertType() + ":" +
	// zone.getPrimaryFlag() + ":" + zoneValue);
	// } catch (IllegalAccessException | IllegalArgumentException |
	// InvocationTargetException | NoSuchMethodException | SecurityException e) {
	// e.printStackTrace();
	// }
	// });
	// }
	// }
	// }
	//
	// responseMap.put("branchCode", currentBranchCode);
	// responseMap.put("mobile", mobile);
	// responseMap.put("branchName", branchName);
	// responseMap.put("bank", bank);
	// responseMap.put("dateTime", dateTime);
	// responseMap.put("deviceName", device != null ? device.getDeviceName() :
	// "N/A");
	// responseMap.put("zoneValues", zoneStatusList);
	// branchReports.add(responseMap);
	// });
	// }
	// response.setPayload(branchReports);
	// response.setMessage("Data fetched successfully");
	// response.setStatusCode(HttpStatus.OK.value());
	// } catch (Exception e) {
	// response.setMessage("Error occurred while fetching data by branchCode :" +
	// branchCode);
	// response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// }
	// return response;
	// }
	public ResponseEntity getCountsByStatusOfAllDevices(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<Map<String, Object>> branchReports = new ArrayList<>();
			ResponseEntity branchResponseList = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseList.getPayload();

			if (branchList.isEmpty()) {
				response.setMessage("Branches not found on this branch code :" + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				branchList.forEach(branch -> {
					List<String> zoneStatusList = new ArrayList<>();
					Map<String, Object> responseMap = new HashMap<>();

					String dateTime = general.getCurrentTimeStamp().toString();
					String currentBranchCode = branch.getBranchCode();
					String branchName = branch.getBranchDesc();
					String mobile = branch.getMobile();
					String bank = branch.getBank();

					DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
					if (device == null) {
						response.setPayload("Device not found for this branchcode :" + branch.getBranchCode());
						response.setStatusCode(HttpStatus.NOT_FOUND.value());
					} else {
						List<DeviceZoneDetailsEntity> deviceZoneList = deviceZoneRepo
								.findByDeviceId(device.getDeviceId());
						if (deviceZoneList.isEmpty()) {
							response.setMessage("Zones not found for this device :" + device.getDeviceId());
							response.setStatusCode(HttpStatus.NOT_FOUND.value());
						} else {
							SmsDetailsEntity smsLastRecord = smsDetailsRepository
									.getLatestRecord(device.getDeviceSerialNo());
							if (smsLastRecord == null) {
								deviceZoneList.forEach(zone -> {
									zoneStatusList.add(zone.getZoneOriginalName() + ":" + zone.getZoneDesiredName()
											+ ":" + zone.getInputType() + ":" + zone.getAlertType() + ":"
											+ zone.getPrimaryFlag() + ":N");
								});
							} else {
								dateTime = smsLastRecord.getDateTime().toString();
								deviceZoneList.forEach(zone -> {
									String zoneName = zone.getZoneOriginalName();
									zoneName = StringUtils.capitalize(zoneName.toLowerCase());

									String methodName = "get" + zoneName;
									try {
										String zoneValue = (String) smsLastRecord.getClass().getMethod(methodName)
												.invoke(smsLastRecord);

										if ("I".equals(zoneValue) || "D".equals(zoneValue)) {
											zoneValue = "A";
										}
										zoneStatusList.add(zone.getZoneOriginalName() + ":" + zone.getZoneDesiredName()
												+ ":" + zone.getInputType() + ":" + zone.getAlertType() + ":"
												+ zone.getPrimaryFlag() + ":" + zoneValue);
									} catch (IllegalAccessException | IllegalArgumentException
											| InvocationTargetException | NoSuchMethodException | SecurityException e) {
										e.printStackTrace();
									}
								});
							}
						}
					}

					responseMap.put("branchCode", currentBranchCode);
					responseMap.put("mobile", mobile);
					responseMap.put("branchName", branchName);
					responseMap.put("bank", bank);
					responseMap.put("dateTime", dateTime);
					responseMap.put("deviceName", device != null ? device.getDeviceName() : "N/A");
					responseMap.put("zoneValues", zoneStatusList);
					branchReports.add(responseMap);
				});
			}
			response.setPayload(branchReports);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setMessage("Error occurred while fetching data by branchCode :" + branchCode);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllAlertsReportWithoutPagination(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity responsePayload = branchService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responsePayload.getPayload();
			List<AllAlarmsDTO> alarmList = new ArrayList<>();

			if (branchList != null) {
				for (BranchMasterEntity branch : branchList) {
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(branch.getBranchCode());
					if (deviceMasterEntity != null) {
						List<SmsDetailsEntity> smsRecord = smsDetailsRepository
								.findByPanelSerialNo(deviceMasterEntity.getDeviceSerialNo());
						if (!smsRecord.isEmpty()) {
							for (SmsDetailsEntity alarmRecord : smsRecord) {
								for (int i = 1; i <= 40; i++) {
									String zoneValue = "";
									try {
										zoneValue = (String) alarmRecord.getClass().getMethod("getZone" + i)
												.invoke(alarmRecord);
									} catch (IllegalAccessException | IllegalArgumentException
											| InvocationTargetException | NoSuchMethodException | SecurityException e) {
										e.printStackTrace();
									}
									if ("A".equals(zoneValue) || "I".equals(zoneValue)) {
										String zoneOriginalName = "ZONE" + i;
										DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneRepo
												.findByDeviceIdAndZoneName(deviceMasterEntity.getDeviceId(),
														zoneOriginalName);
										if (deviceZoneDetailsEntity != null) {
											AllAlarmsDTO alarmResponse = new AllAlarmsDTO();
											alarmResponse.setBank(branch.getBank());
											alarmResponse.setAddress(branch.getAddress1() + "," + branch.getAddress2()
													+ " , " + branch.getAddress3() + " , " + branch.getAddress4());
											alarmResponse.setBranchCode(branch.getBranchCode());
											alarmResponse.setBranchName(branch.getBranchDesc());
											alarmResponse.setIfsc(branch.getIfsc());
											alarmResponse.setVendorName(deviceMasterEntity.getVendorName());
											alarmResponse.setAlertType(deviceZoneDetailsEntity.getAlertType());
											alarmResponse
													.setZoneDesiredName(deviceZoneDetailsEntity.getZoneDesiredName());
											alarmResponse.setTimeStamp(alarmRecord.getDateTime().toString());
											alarmList.add(alarmResponse);
										}
									}
								}
							}
						}
					}
				}

				// Correct pagination logic

				alarmList.sort((a1, a2) -> {
					return a2.getTimeStamp().compareTo(a1.getTimeStamp());
				});

				response.setPayload(alarmList);
				response.setMessage("Alarms fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());

			} else {
				response.setPayload(Collections.emptyList());
				response.setMessage("No branches found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			response.setMessage("An error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * API START FOR REPORT (On the basis of MONTHLY , WEEKLY and DAILY basis)
	 */
	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForMonthlyAlertReports() {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		List<MailReportDTO> mailReportDTOList = new ArrayList<>();
		List<BranchMasterEntity> branchMasterList = branchMasterRepository.findAll();

		for (BranchMasterEntity branchMasterEntity : branchMasterList) {
			System.out.println("Branch Code :" + branchMasterEntity.getBranchCode());

			BranchMasterDTO branchMasterDTO = new BranchMasterDTO();
			BeanUtils.copyProperties(branchMasterEntity, branchMasterDTO);
			MailReportDTO mailReportDTO = new MailReportDTO();
			mailReportDTO.setBranchName(branchMasterDTO.getBranchDesc());

			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (deviceMasterEntity == null) {
				logger.error("DeviceMasterEntity not found for branch code: {}", branchMasterDTO.getBranchCode());
				continue;
			}

			List<DeviceZoneDetailsEntity> deviceZoneList = deviceZoneRepo
					.findByDeviceId(deviceMasterEntity.getDeviceId());
			if (deviceZoneList.isEmpty()) {
				logger.error("DeviceZoneDetailsEntity list is empty for device id: {}",
						deviceMasterEntity.getDeviceId());
				continue;
			}

			DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
			BeanUtils.copyProperties(deviceMasterEntity, deviceMasterDTO);
			deviceMasterDTO.setZoneList(deviceZoneList);
			mailReportDTO.setDeviceZoneEntity(deviceZoneList);

			List<String> zoneStatusList = new ArrayList<>();
			SmsDetailsEntity smsLastRecord = smsDetailsRepository
					.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
			if (smsLastRecord == null) {
				deviceZoneList.forEach(zone -> {
					zoneStatusList.add(zone.getZoneOriginalName() + ":N");
				});
			} else {
				String dateTime = general.getCurrentTimeStamp().toString();
				deviceZoneList.forEach(zone -> {
					String zoneName = zone.getZoneOriginalName();
					zoneName = StringUtils.capitalize(zoneName.toLowerCase());

					String methodName = "get" + zoneName;
					try {
						String zoneValue = (String) smsLastRecord.getClass().getMethod(methodName)
								.invoke(smsLastRecord);
						zoneStatusList.add(zone.getZoneOriginalName() + ":" + zoneValue);
						mailReportDTO.setStatus(zoneStatusList);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				});
			}
			Integer month = null;
			Integer year = null;
			ResponseEntity responseEntity = loadHistoricalSmsDetails(deviceMasterEntity.getDeviceSerialNo(), month,
					year);
			List<HashMap<String, String>> smsdtoList = (List<HashMap<String, String>>) responseEntity.getPayload();

			mailReportDTO.setSmsDto(smsdtoList);

			mailReportDTO.setVendorName(deviceMasterEntity.getVendorName());
			mailReportDTO.setVendorEmail(deviceMasterEntity.getVendorEmail());

			BranchMasterEntity branchMasterForParent = branchMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (branchMasterForParent != null) {
				branchMasterDTO.setParentName(branchMasterForParent.getBranchDesc());

				if ("ZONE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setZonalName("N/A");
				} else if ("HEAD OFFICE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setParentCode("N/A");
					branchMasterDTO.setZonalName("N/A");
				} else {
					branchMasterDTO.setZonalName(branchMasterForParent.getBranchDesc());
				}
				mailReportDTO.setZonalOffice(branchMasterDTO.getZonalName());
				mailReportDTO.setParentBranchName(branchMasterDTO.getParentName());
			} else {
				branchMasterDTO.setParentName("N/A");
				branchMasterDTO.setZonalName("N/A");
			}
			mailReportDTOList.add(mailReportDTO);
		}

		logger.info("List is: {}", mailReportDTOList);
		return org.springframework.http.ResponseEntity.ok(mailReportDTOList);
	}

	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForWeeklyAlertReports()
			throws Exception {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		List<MailReportDTO> mailReportDTOList = new ArrayList<>();
		List<BranchMasterEntity> branchMasterList = branchMasterRepository.findAll();

		for (BranchMasterEntity branchMasterEntity : branchMasterList) {
			System.out.println("Branch Code :" + branchMasterEntity.getBranchCode());

			BranchMasterDTO branchMasterDTO = new BranchMasterDTO();
			BeanUtils.copyProperties(branchMasterEntity, branchMasterDTO);
			MailReportDTO mailReportDTO = new MailReportDTO();
			mailReportDTO.setBranchName(branchMasterDTO.getBranchDesc());

			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (deviceMasterEntity == null) {
				logger.error("DeviceMasterEntity not found for branch code: {}", branchMasterDTO.getBranchCode());
				continue;
			}

			List<DeviceZoneDetailsEntity> deviceZoneList = deviceZoneRepo
					.findByDeviceId(deviceMasterEntity.getDeviceId());
			if (deviceZoneList.isEmpty()) {
				logger.error("DeviceZoneDetailsEntity list is empty for device id: {}",
						deviceMasterEntity.getDeviceId());
				continue;
			}

			DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
			BeanUtils.copyProperties(deviceMasterEntity, deviceMasterDTO);
			deviceMasterDTO.setZoneList(deviceZoneList);
			mailReportDTO.setDeviceZoneEntity(deviceZoneList);

			List<String> zoneStatusList = new ArrayList<>();
			SmsDetailsEntity smsLastRecord = smsDetailsRepository
					.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
			if (smsLastRecord == null) {
				deviceZoneList.forEach(zone -> {
					zoneStatusList.add(zone.getZoneOriginalName() + ":N");
				});
			} else {
				String dateTime = general.getCurrentTimeStamp().toString();
				deviceZoneList.forEach(zone -> {
					String zoneName = zone.getZoneOriginalName();
					zoneName = StringUtils.capitalize(zoneName.toLowerCase());

					String methodName = "get" + zoneName;
					try {
						String zoneValue = (String) smsLastRecord.getClass().getMethod(methodName)
								.invoke(smsLastRecord);
						zoneStatusList.add(zone.getZoneOriginalName() + ":" + zoneValue);
						mailReportDTO.setStatus(zoneStatusList);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				});
			}
			Integer month = null;
			Integer year = null;
			ResponseEntity responseEntity = fetchSmsDetailsForCurrentWeek(deviceMasterEntity.getDeviceSerialNo());
			List<HashMap<String, String>> smsdtoList = (List<HashMap<String, String>>) responseEntity.getPayload();

			mailReportDTO.setSmsDto(smsdtoList);

			mailReportDTO.setVendorName(deviceMasterEntity.getVendorName());
			mailReportDTO.setVendorEmail(deviceMasterEntity.getVendorEmail());

			BranchMasterEntity branchMasterForParent = branchMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (branchMasterForParent != null) {
				branchMasterDTO.setParentName(branchMasterForParent.getBranchDesc());

				if ("ZONE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setZonalName("N/A");
				} else if ("HEAD OFFICE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setParentCode("N/A");
					branchMasterDTO.setZonalName("N/A");
				} else {
					branchMasterDTO.setZonalName(branchMasterForParent.getBranchDesc());
				}
				mailReportDTO.setZonalOffice(branchMasterDTO.getZonalName());
				mailReportDTO.setParentBranchName(branchMasterDTO.getParentName());
			} else {
				branchMasterDTO.setParentName("N/A");
				branchMasterDTO.setZonalName("N/A");
			}
			mailReportDTOList.add(mailReportDTO);
		}

		logger.info("List is: {}", mailReportDTOList);
		return org.springframework.http.ResponseEntity.ok(mailReportDTOList);
	}

	public org.springframework.http.ResponseEntity<List<MailReportDTO>> sendReportForDailyAlertReports()
			throws Exception {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		List<MailReportDTO> mailReportDTOList = new ArrayList<>();
		List<BranchMasterEntity> branchMasterList = branchMasterRepository.findAll();

		for (BranchMasterEntity branchMasterEntity : branchMasterList) {
			System.out.println("Branch Code :" + branchMasterEntity.getBranchCode());

			BranchMasterDTO branchMasterDTO = new BranchMasterDTO();
			BeanUtils.copyProperties(branchMasterEntity, branchMasterDTO);
			MailReportDTO mailReportDTO = new MailReportDTO();
			mailReportDTO.setBranchName(branchMasterDTO.getBranchDesc());

			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (deviceMasterEntity == null) {
				logger.error("DeviceMasterEntity not found for branch code: {}", branchMasterDTO.getBranchCode());
				continue;
			}

			List<DeviceZoneDetailsEntity> deviceZoneList = deviceZoneRepo
					.findByDeviceId(deviceMasterEntity.getDeviceId());
			if (deviceZoneList.isEmpty()) {
				logger.error("DeviceZoneDetailsEntity list is empty for device id: {}",
						deviceMasterEntity.getDeviceId());
				continue;
			}

			DeviceMasterDTO deviceMasterDTO = new DeviceMasterDTO();
			BeanUtils.copyProperties(deviceMasterEntity, deviceMasterDTO);
			deviceMasterDTO.setZoneList(deviceZoneList);
			mailReportDTO.setDeviceZoneEntity(deviceZoneList);

			List<String> zoneStatusList = new ArrayList<>();
			SmsDetailsEntity smsLastRecord = smsDetailsRepository
					.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
			if (smsLastRecord == null) {
				deviceZoneList.forEach(zone -> {
					zoneStatusList.add(zone.getZoneOriginalName() + ":N");
				});
			} else {
				String dateTime = general.getCurrentTimeStamp().toString();
				deviceZoneList.forEach(zone -> {
					String zoneName = zone.getZoneOriginalName();
					zoneName = StringUtils.capitalize(zoneName.toLowerCase());

					String methodName = "get" + zoneName;
					try {
						String zoneValue = (String) smsLastRecord.getClass().getMethod(methodName)
								.invoke(smsLastRecord);
						zoneStatusList.add(zone.getZoneOriginalName() + ":" + zoneValue);
						mailReportDTO.setStatus(zoneStatusList);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				});
			}
			Integer month = null;
			Integer year = null;
			ResponseEntity responseEntity = fetchSmsDetailsForCurrentDay(deviceMasterEntity.getDeviceSerialNo());
			List<HashMap<String, String>> smsdtoList = (List<HashMap<String, String>>) responseEntity.getPayload();

			mailReportDTO.setSmsDto(smsdtoList);

			mailReportDTO.setVendorName(deviceMasterEntity.getVendorName());
			mailReportDTO.setVendorEmail(deviceMasterEntity.getVendorEmail());

			BranchMasterEntity branchMasterForParent = branchMasterRepository
					.findBybranchCode(branchMasterDTO.getBranchCode());
			if (branchMasterForParent != null) {
				branchMasterDTO.setParentName(branchMasterForParent.getBranchDesc());

				if ("ZONE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setZonalName("N/A");
				} else if ("HEAD OFFICE".equals(branchMasterDTO.getBranchType())) {
					branchMasterDTO.setParentCode("N/A");
					branchMasterDTO.setZonalName("N/A");
				} else {
					branchMasterDTO.setZonalName(branchMasterForParent.getBranchDesc());
				}
				mailReportDTO.setZonalOffice(branchMasterDTO.getZonalName());
				mailReportDTO.setParentBranchName(branchMasterDTO.getParentName());
			} else {
				branchMasterDTO.setParentName("N/A");
				branchMasterDTO.setZonalName("N/A");
			}
			mailReportDTOList.add(mailReportDTO);
		}

		logger.info("List is: {}", mailReportDTOList);
		return org.springframework.http.ResponseEntity.ok(mailReportDTOList);
	}

	// To export the monthly report in via excel sheet
	public void exportMonthlyAlertReportsToFile(String fileLocation, String branchCode)
			throws IOException, MessagingException {
		org.springframework.http.ResponseEntity<List<MailReportDTO>> response = sendReportForMonthlyAlertReports();

		// System.out.println("mailReportDTOs=====>>>>"+response.toString());
		if (response == null || !response.hasBody()) {
			throw new IOException("No data received for monthly alert reports.");
		}

		List<MailReportDTO> mailReportDTOs = response.getBody();
		if (mailReportDTOs == null || mailReportDTOs.isEmpty()) {
			throw new IOException("Received empty or null list of mail report DTOs.");
		}

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Monthly Report");

		setColumnWidths(sheet, mailReportDTOs);

		Row header = sheet.createRow(0);

		Row row2 = sheet.createRow(1);

		createHeaderRow(header, workbook, mailReportDTOs, row2);

		CellStyle workingStyle = createCellStyle(workbook, IndexedColors.BRIGHT_GREEN);
		CellStyle faultyStyle = createCellStyle(workbook, IndexedColors.RED);
		CellStyle isolatedStyle = createCellStyle(workbook, IndexedColors.ORANGE);
		CellStyle disableStyle = createCellStyle(workbook, IndexedColors.GREY_25_PERCENT);

		int rowNum = 2;
		boolean greyRow = true;
		int srNo = 1;

		for (MailReportDTO report : mailReportDTOs) {
			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = report.getDeviceZoneEntity();
			List<String> statusList = report.getStatus();
			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				continue;
			}

			int startRowNum = rowNum;
			for (DeviceZoneDetailsEntity deviceZoneDetailsEntity : deviceZoneDetailsEntities) {
				Row row = sheet.createRow(rowNum);
				if (greyRow) {
					setRowColor(row, IndexedColors.GREY_25_PERCENT);
				}
				populateRowWithData(row, report, deviceZoneDetailsEntity, statusList, srNo, workingStyle, faultyStyle,
						isolatedStyle, disableStyle);
				rowNum++;
			}

			mergeCells(sheet, startRowNum, rowNum - 1);

			greyRow = !greyRow;
			srNo++;
		}

		try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
			workbook.write(outputStream);
			System.out.println("Excel file exported successfully to: " + fileLocation);
		} finally {
			workbook.close();
		}

		BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
		if (branchEntity.getParentCode().equals(0)) {
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
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		} else {
			BranchMasterEntity parentBranch = branchMasterRepository.findBybranchCode(branchEntity.getParentCode());
			List<String> emails = new ArrayList<>();
			emails.add(parentBranch.getEmail());
			if (parentBranch.getEmail2() != null) {
				emails.add(parentBranch.getEmail2());
			}
			if (parentBranch.getEmail3() != null) {
				emails.add(parentBranch.getEmail3());
			}
			if (parentBranch.getEmail4() != null) {
				emails.add(parentBranch.getEmail4());
			}
			if (parentBranch.getEmail5() != null) {
				emails.add(parentBranch.getEmail5());
			}
			if (parentBranch.getEmail6() != null) {
				emails.add(parentBranch.getEmail6());
			}
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		}
	}

	private void setColumnWidths(Sheet sheet, List<MailReportDTO> mailReportDTOs) {
		int[] columnWidths = { 256 * 5, 256 * 25, 256 * 25, 256 * 25, 256 * 20, 256 * 25, 256 * 20, 256 * 30, 256 * 25,
				256 * 25, 256 * 25, 256 * 20 };

		Set<String> dateTimes = new LinkedHashSet<>();
		for (MailReportDTO report : mailReportDTOs) {

			System.out.println("report--->>>>>>>>>" + report.toString());
			if (report.getSmsDto() != null) {
				// for (SmsDetailsDTO sms : report.getSmsDto()) {
				// dateTimes.add(String.valueOf(sms.getDateTime()));
				// }

				report.getSmsDto().forEach(x -> {
					dateTimes.add(x.get("dateTime"));
				});
			}
		}

		int totalColumns = columnWidths.length + dateTimes.size();
		for (int i = 0; i < totalColumns; i++) {
			if (i < columnWidths.length) {
				sheet.setColumnWidth(i, columnWidths[i]);
			} else {
				sheet.setColumnWidth(i, 256 * 25);
			}
		}
	}

	private void createHeaderRow(Row header, Workbook workbook, List<MailReportDTO> mailReportDTOs, Row row2) {
		Set<String> dateTimes = new LinkedHashSet<>();
		for (MailReportDTO report : mailReportDTOs) {
			if (report.getSmsDto() != null) {
				report.getSmsDto().forEach(item -> {
					dateTimes.add(item.get("dateTime"));
				});
			}
		}

		Set<String> dateTimeSet = new LinkedHashSet<>();
		for (String dateTime : dateTimes) {
			String datePart = dateTime.substring(dateTime.indexOf(' ') + 1);
			dateTimeSet.add(datePart);
		}

		System.out.println("List of String[] Time:--> " + dateTimeSet);

		String[] headers = { "SR NO.", "BRANCH", "ZONAL OFFICE", "PARENT", "VENDOR NAME", "VENDOR EMAIL", "DEVICE",
				"ALERT TYPE", "CURRENT STATUS", "MAKE & MODEL", "DATE OF INSTALLATION", "LIFE CYCLE/ AGE" };
		CellStyle headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		headerStyle.setFont(font);

		int colIndex = 0;
		for (String headerText : headers) {
			Cell headerCell = header.createCell(colIndex++);
			headerCell.setCellValue(headerText);
			headerCell.setCellStyle(headerStyle);
		}

		int row2Index = colIndex;

		// Add values in row 1 (date parts)
		for (String dateTime : dateTimes) {
			String datePart = dateTime.substring(0, dateTime.indexOf(' '));

			Cell headerCell = header.createCell(colIndex++);
			headerCell.setCellValue(datePart);
			headerCell.setCellStyle(headerStyle);
		}

		// Add values in row 2 (time stamp values)
		for (String dateTime : dateTimeSet) {
			Cell cell = row2.createCell(row2Index++);
			cell.setCellValue(dateTime);
			// Optionally, apply cell style here if needed
		}
	}

	private void populateRowWithData(Row row, MailReportDTO report, DeviceZoneDetailsEntity deviceZoneDetailsEntity,
			List<String> statusList, int srNo, CellStyle workingStyle, CellStyle faultyStyle, CellStyle isolatedStyle,
			CellStyle disableStyle) {

		// Populate basic details
		Cell cell = row.createCell(0);
		cell.setCellValue(srNo);

		cell = row.createCell(1);
		cell.setCellValue(report.getBranchName());

		cell = row.createCell(2);
		cell.setCellValue(report.getZonalOffice());

		cell = row.createCell(3);
		cell.setCellValue(report.getParentBranchName());

		cell = row.createCell(4);
		cell.setCellValue(report.getVendorName());

		cell = row.createCell(5);
		cell.setCellValue(report.getVendorEmail());

		cell = row.createCell(6);
		cell.setCellValue(deviceZoneDetailsEntity.getZoneDesiredName());

		cell = row.createCell(7);
		cell.setCellValue(deviceZoneDetailsEntity.getAlertType());

		cell = row.createCell(8);
		cell.setCellValue("");
		if (statusList != null) {
			String zoneName = deviceZoneDetailsEntity.getZoneOriginalName();
			for (String status : statusList) {
				String[] parts = status.split(":");
				if (parts.length > 0 && parts[0].equals(zoneName)) {
					String statusValue = parts[1];
					switch (statusValue) {
					case "N":
						cell.setCellValue("WORKING");
						cell.setCellStyle(workingStyle);
						break;
					case "A":
						cell.setCellValue("FAULT");
						cell.setCellStyle(faultyStyle);
						break;
					case "I":
						cell.setCellValue("ISOLATED");
						cell.setCellStyle(isolatedStyle);
						break;
					case "D":
						cell.setCellValue("DISABLE");
						cell.setCellStyle(disableStyle);
						break;
					default:
						cell.setCellValue(statusValue);
						break;
					}
					break;
				}
			}
		}

		cell = row.createCell(9);
		cell.setCellValue(report.getMakeAndModel());

		cell = row.createCell(10);
		cell.setCellValue(report.getInstallation());

		cell = row.createCell(11);
		cell.setCellValue(report.getLifeCycle());

		// Add status for each zone vertically under respective date and time headers
		int colIndex = 12;
		if (report.getSmsDto() != null) {
			for (HashMap<String, String> sms : report.getSmsDto()) {
				String zoneOriginalName = deviceZoneDetailsEntity.getZoneOriginalName();
				Cell zoneCell = row.createCell(colIndex++);
				String zoneStatus = sms.get(zoneOriginalName);
				zoneCell.setCellValue(zoneStatus);
				switch (zoneStatus) {
				case "N":
					zoneCell.setCellValue("WORKING");
					zoneCell.setCellStyle(workingStyle);
					break;
				case "A":
					zoneCell.setCellValue("FAULT");
					zoneCell.setCellStyle(faultyStyle);
					break;
				case "I":
					zoneCell.setCellValue("ISOLATED");
					zoneCell.setCellStyle(isolatedStyle);
					break;
				case "D":
					zoneCell.setCellValue("DISABLE");
					zoneCell.setCellStyle(disableStyle);
					break;
				default:
					break;
				}
			}
		}
	}

	private void mergeCells(Sheet sheet, int startRow, int endRow) {
		if (startRow == endRow) {
			return;
		}

		for (int col = 0; col <= 5; col++) {
			sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, col, col));
		}
		for (int col = 9; col <= 11; col++) {
			sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, col, col));
		}
	}

	private void setRowColor(Row row, IndexedColors color) {
		CellStyle style = row.getSheet().getWorkbook().createCellStyle();
		style.setFillForegroundColor(color.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell == null) {
				cell = row.createCell(i);
			}
			cell.setCellStyle(style);
		}
	}

	private CellStyle createCellStyle(Workbook workbook, IndexedColors color) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(color.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	// To export the weekly report in via excel sheet
	public void exportWeeklyAlertReportsToFile(String fileLocation, String branchCode) throws Exception {
		org.springframework.http.ResponseEntity<List<MailReportDTO>> response = sendReportForWeeklyAlertReports();

		// System.out.println("mailReportDTOs=====>>>>"+response.toString());
		if (response == null || !response.hasBody()) {
			throw new IOException("No data received for monthly alert reports.");
		}

		List<MailReportDTO> mailReportDTOs = response.getBody();
		if (mailReportDTOs == null || mailReportDTOs.isEmpty()) {
			throw new IOException("Received empty or null list of mail report DTOs.");
		}

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Monthly Report");

		setColumnWidths(sheet, mailReportDTOs);

		Row header = sheet.createRow(0);

		Row row2 = sheet.createRow(1);

		createHeaderRow(header, workbook, mailReportDTOs, row2);

		CellStyle workingStyle = createCellStyle(workbook, IndexedColors.BRIGHT_GREEN);
		CellStyle faultyStyle = createCellStyle(workbook, IndexedColors.RED);
		CellStyle isolatedStyle = createCellStyle(workbook, IndexedColors.ORANGE);
		CellStyle disableStyle = createCellStyle(workbook, IndexedColors.GREY_25_PERCENT);

		int rowNum = 2;
		boolean greyRow = true;
		int srNo = 1;

		for (MailReportDTO report : mailReportDTOs) {
			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = report.getDeviceZoneEntity();
			List<String> statusList = report.getStatus();
			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				continue;
			}

			int startRowNum = rowNum;
			for (DeviceZoneDetailsEntity deviceZoneDetailsEntity : deviceZoneDetailsEntities) {
				Row row = sheet.createRow(rowNum);
				if (greyRow) {
					setRowColor(row, IndexedColors.GREY_25_PERCENT);
				}
				populateRowWithData(row, report, deviceZoneDetailsEntity, statusList, srNo, workingStyle, faultyStyle,
						isolatedStyle, disableStyle);
				rowNum++;
			}

			mergeCells(sheet, startRowNum, rowNum - 1);

			greyRow = !greyRow;
			srNo++;
		}

		try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
			workbook.write(outputStream);
			System.out.println("Excel file exported successfully to: " + fileLocation);
		} finally {
			workbook.close();
		}
		BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
		if (branchEntity.getParentCode().equals(0)) {
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
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		} else {
			BranchMasterEntity parentBranch = branchMasterRepository.findBybranchCode(branchEntity.getParentCode());
			List<String> emails = new ArrayList<>();
			emails.add(parentBranch.getEmail());
			if (parentBranch.getEmail2() != null) {
				emails.add(parentBranch.getEmail2());
			}
			if (parentBranch.getEmail3() != null) {
				emails.add(parentBranch.getEmail3());
			}
			if (parentBranch.getEmail4() != null) {
				emails.add(parentBranch.getEmail4());
			}
			if (parentBranch.getEmail5() != null) {
				emails.add(parentBranch.getEmail5());
			}
			if (parentBranch.getEmail6() != null) {
				emails.add(parentBranch.getEmail6());
			}
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		}
	}

	// To export the daily report in via excel sheet
	public void exportDailyAlertReportsToFile(String fileLocation, String branchCode) throws Exception {
		org.springframework.http.ResponseEntity<List<MailReportDTO>> response = sendReportForDailyAlertReports();

		// System.out.println("mailReportDTOs=====>>>>"+response.toString());
		if (response == null || !response.hasBody()) {
			throw new IOException("No data received for monthly alert reports.");
		}

		List<MailReportDTO> mailReportDTOs = response.getBody();
		if (mailReportDTOs == null || mailReportDTOs.isEmpty()) {
			throw new IOException("Received empty or null list of mail report DTOs.");
		}

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Monthly Report");

		setColumnWidths(sheet, mailReportDTOs);

		Row header = sheet.createRow(0);

		Row row2 = sheet.createRow(1);

		createHeaderRow(header, workbook, mailReportDTOs, row2);

		CellStyle workingStyle = createCellStyle(workbook, IndexedColors.BRIGHT_GREEN);
		CellStyle faultyStyle = createCellStyle(workbook, IndexedColors.RED);
		CellStyle isolatedStyle = createCellStyle(workbook, IndexedColors.ORANGE);
		CellStyle disableStyle = createCellStyle(workbook, IndexedColors.GREY_25_PERCENT);

		int rowNum = 2;
		boolean greyRow = true;
		int srNo = 1;

		for (MailReportDTO report : mailReportDTOs) {
			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = report.getDeviceZoneEntity();
			List<String> statusList = report.getStatus();
			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				continue;
			}

			int startRowNum = rowNum;
			for (DeviceZoneDetailsEntity deviceZoneDetailsEntity : deviceZoneDetailsEntities) {
				Row row = sheet.createRow(rowNum);
				if (greyRow) {
					setRowColor(row, IndexedColors.GREY_25_PERCENT);
				}
				populateRowWithData(row, report, deviceZoneDetailsEntity, statusList, srNo, workingStyle, faultyStyle,
						isolatedStyle, disableStyle);
				rowNum++;
			}

			mergeCells(sheet, startRowNum, rowNum - 1);

			greyRow = !greyRow;
			srNo++;
		}

		try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
			workbook.write(outputStream);
			System.out.println("Excel file exported successfully to: " + fileLocation);

		} finally {
			workbook.close();
		}

		BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
		if (branchEntity.getParentCode().equals(0)) {
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
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		} else {
			BranchMasterEntity parentBranch = branchMasterRepository.findBybranchCode(branchEntity.getParentCode());
			List<String> emails = new ArrayList<>();
			emails.add(parentBranch.getEmail());
			if (parentBranch.getEmail2() != null) {
				emails.add(parentBranch.getEmail2());
			}
			if (parentBranch.getEmail3() != null) {
				emails.add(parentBranch.getEmail3());
			}
			if (parentBranch.getEmail4() != null) {
				emails.add(parentBranch.getEmail4());
			}
			if (parentBranch.getEmail5() != null) {
				emails.add(parentBranch.getEmail5());
			}
			if (parentBranch.getEmail6() != null) {
				emails.add(parentBranch.getEmail6());
			}
			for (String email : emails) {
				System.out.println();
				mailService.sendEmailWithAttachment(fileLocation, email, "Daily Alert Report",
						"Please find the attached daily alert report.");
			}

		}
	}

	/*
	 * <<<=====================================================================>>
	 * This function is responsible for getting the sms details based on current
	 * date(fetchSmsDetailsForCurrentMonth) & based on month & year
	 * (getSmsDetailsForSpecificPeriod) which is provided by user
	 * 
	 * @author Aniket mishra
	 * <<<=====================================================================>>
	 */
	public ResponseEntity loadHistoricalSmsDetails(String panelSerialNumber, Integer month, Integer year) {
		ResponseEntity response = new ResponseEntity();
		try {
			if (panelSerialNumber != null && month == null && year == null) {
				response = fetchSmsDetailsForCurrentMonth(panelSerialNumber);
			} else if (panelSerialNumber != null && month != null && year != null) {
				response = getSmsDetailsForSpecificPeriod(panelSerialNumber, month, year);
			} else {
				response.setMessage("Invalid parameters provided.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(
					"Error occurred in fetching the data for the specific time period. Please contact the administrator.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/*
	 * <<<==========================================================================
	 * >> This function is used for getting the sms details based on current date &
	 * from current date is fecth the month & get all the data for same month
	 * 
	 * @author Aniket mishra
	 * <<<==========================================================================
	 * >>
	 */

	public ResponseEntity fetchSmsDetailsForCurrentMonth(String panelSerialNumber) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			LocalDate currentDate = LocalDate.now();
			int currentMonth = currentDate.getMonthValue();
			int currentYear = currentDate.getYear();

			DeviceMasterEntity deviceDetails = deviceMasterRepository.findByDeviceSerialNo(panelSerialNumber);

			List<DeviceZoneDetailsEntity> device_zone_details = deviceZoneRepo
					.findByDeviceId(deviceDetails.getDeviceId());
			System.out.println(device_zone_details);

			Set<String> relevantZones = new HashSet<>();
			for (DeviceZoneDetailsEntity zoneDetail : device_zone_details) {
				relevantZones.add(zoneDetail.getZoneOriginalName());
			}

			List<SmsDetailsEntity> smsDetailsEntity = smsDetailsRepository
					.findByPanelSerialNoAndMonth(panelSerialNumber, currentMonth, currentYear);

			List<LinkedHashMap<String, String>> smsDetailsDTOs = new LinkedList(); // Changed type to
																					// List<LinkedHashMap<String,
																					// String>>

			if (smsDetailsEntity != null && !smsDetailsEntity.isEmpty()) {
				for (SmsDetailsEntity smsEntity : smsDetailsEntity) {
					LinkedHashMap<String, String> smsDTO = new LinkedHashMap<>(); // Changed to use LinkedHashMap

					smsDTO.put("id", String.valueOf(smsEntity.getId()));
					// smsDTO.put("senderMobileNo", smsEntity.getSenderMobileNo());
					// smsDTO.put("panelSerialNo", smsEntity.getPanelSerialNo());
					smsDTO.put("dateTime", smsEntity.getDateTime().toString());
					// smsDTO.put("uniqueId", smsEntity.getUniqueId());
					// smsDTO.put("main", smsEntity.getMain());
					// smsDTO.put("solar", smsEntity.getSolar());
					// smsDTO.put("battery", smsEntity.getBattery());
					// smsDTO.put("signalFlag", smsEntity.getSignalFlag());
					// smsDTO.put("armed", smsEntity.getArmed());

					for (int i = 1; i <= 40; i++) {
						String zoneName = "ZONE" + i;
						String zoneValue = (String) SmsDetailsEntity.class.getMethod("getZone" + i).invoke(smsEntity);
						if (relevantZones.contains(zoneName) && zoneValue != null) {
							smsDTO.put("ZONE" + i, zoneValue);
						}
					}

					smsDetailsDTOs.add(smsDTO);
				}

				response.setPayload(smsDetailsDTOs);
				response.setMessage("Data fetched successfully!!");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the current month!!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from the database!!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity fetchSmsDetailsForCurrentWeek(String panelSerialNumber) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			LocalDate currentDate = LocalDate.now();
			int currentWeek = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
			int currentYear = currentDate.getYear();

			DeviceMasterEntity deviceDetails = deviceMasterRepository.findByDeviceSerialNo(panelSerialNumber);

			List<DeviceZoneDetailsEntity> device_zone_details = deviceZoneRepo
					.findByDeviceId(deviceDetails.getDeviceId());
			System.out.println(device_zone_details);

			Set<String> relevantZones = new HashSet<>();
			for (DeviceZoneDetailsEntity zoneDetail : device_zone_details) {
				relevantZones.add(zoneDetail.getZoneOriginalName());
			}

			List<SmsDetailsEntity> smsDetailsEntity = smsDetailsRepository.findByPanelSerialNoAndWeek(panelSerialNumber,
					currentWeek, currentYear);

			List<LinkedHashMap<String, String>> smsDetailsDTOs = new LinkedList<>();

			if (smsDetailsEntity != null && !smsDetailsEntity.isEmpty()) {
				for (SmsDetailsEntity smsEntity : smsDetailsEntity) {
					LinkedHashMap<String, String> smsDTO = new LinkedHashMap<>();

					smsDTO.put("id", String.valueOf(smsEntity.getId()));
					smsDTO.put("dateTime", smsEntity.getDateTime().toString());

					for (int i = 1; i <= 40; i++) {
						String zoneName = "ZONE" + i;
						String zoneValue = (String) SmsDetailsEntity.class.getMethod("getZone" + i).invoke(smsEntity);
						if (relevantZones.contains(zoneName) && zoneValue != null) {
							smsDTO.put("ZONE" + i, zoneValue);
						}
					}

					smsDetailsDTOs.add(smsDTO);
				}

				response.setPayload(smsDetailsDTOs);
				response.setMessage("Data fetched successfully!!");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the current week!!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from the database!!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/*
	 * <<<========================================================================>>
	 * This function is used for getting the sms details based on month & year which
	 * is provided by user
	 * 
	 * @author Aniket mishra
	 * <<<========================================================================>>
	 */
	public ResponseEntity getSmsDetailsForSpecificPeriod(String panelSerialNumber, int month, int year)
			throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity deviceDetails = deviceMasterRepository.findByDeviceSerialNo(panelSerialNumber);

			List<DeviceZoneDetailsEntity> deviceZoneDetails = deviceZoneRepo
					.findByDeviceId(deviceDetails.getDeviceId());
			Set<String> relevantZones = new HashSet<>();
			for (DeviceZoneDetailsEntity zoneDetail : deviceZoneDetails) {
				relevantZones.add(zoneDetail.getZoneOriginalName());
			}

			List<SmsDetailsEntity> smsDetailsEntity = smsDetailsRepository
					.findByPanelSerialNoAndMonth(panelSerialNumber, month, year);
			List<ObjectNode> smsDetailsDTOs = new LinkedList<>();

			ObjectMapper objectMapper = new ObjectMapper();

			if (smsDetailsEntity != null && !smsDetailsEntity.isEmpty()) {
				for (SmsDetailsEntity smsEntity : smsDetailsEntity) {
					ObjectNode smsDTO = objectMapper.createObjectNode();

					smsDTO.put("id", smsEntity.getId());
					smsDTO.put("senderMobileNo", smsEntity.getSenderMobileNo());
					smsDTO.put("panelSerialNo", smsEntity.getPanelSerialNo());
					smsDTO.put("dateTime", smsEntity.getDateTime().toString());
					smsDTO.put("uniqueId", smsEntity.getUniqueId());
					smsDTO.put("main", smsEntity.getMain());
					smsDTO.put("solar", smsEntity.getSolar());
					smsDTO.put("battery", smsEntity.getBattery());
					smsDTO.put("signalFlag", smsEntity.getSignalFlag());
					smsDTO.put("armed", smsEntity.getArmed());

					for (int i = 1; i <= 40; i++) {
						String zoneName = "ZONE" + i;
						String zoneValue = (String) SmsDetailsEntity.class.getMethod("getZone" + i).invoke(smsEntity);
						if (relevantZones.contains(zoneName) && zoneValue != null) {
							smsDTO.put("ZONE" + i, zoneValue);
						}
					}

					smsDTO.put("completeMessage", smsEntity.getCompleteMessage());
					smsDTO.put("entryDateTime", smsEntity.getEntryDateTime().toString());
					smsDTO.put("broadcastFlag", smsEntity.getBroadcastFlag());

					smsDetailsDTOs.add(smsDTO);
				}

				response.setPayload(smsDetailsDTOs);
				response.setMessage("Data fetched successfully!!");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the specified month and year!!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from the database!!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity fetchSmsDetailsForCurrentDay(String panelSerialNumber) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			LocalDate currentDate = LocalDate.now();

			DeviceMasterEntity deviceDetails = deviceMasterRepository.findByDeviceSerialNo(panelSerialNumber);

			List<DeviceZoneDetailsEntity> device_zone_details = deviceZoneRepo
					.findByDeviceId(deviceDetails.getDeviceId());
			System.out.println(device_zone_details);

			Set<String> relevantZones = new HashSet<>();
			for (DeviceZoneDetailsEntity zoneDetail : device_zone_details) {
				relevantZones.add(zoneDetail.getZoneOriginalName());
			}

			List<SmsDetailsEntity> smsDetailsEntity = smsDetailsRepository.findByPanelSerialNoAndDate(panelSerialNumber,
					currentDate);

			List<LinkedHashMap<String, String>> smsDetailsDTOs = new LinkedList<>();

			if (smsDetailsEntity != null && !smsDetailsEntity.isEmpty()) {
				for (SmsDetailsEntity smsEntity : smsDetailsEntity) {
					LinkedHashMap<String, String> smsDTO = new LinkedHashMap<>();

					smsDTO.put("id", String.valueOf(smsEntity.getId()));
					smsDTO.put("dateTime", smsEntity.getDateTime().toString());

					for (int i = 1; i <= 40; i++) {
						String zoneName = "ZONE" + i;
						String zoneValue = (String) SmsDetailsEntity.class.getMethod("getZone" + i).invoke(smsEntity);
						if (relevantZones.contains(zoneName) && zoneValue != null) {
							smsDTO.put("ZONE" + i, zoneValue);
						}
					}

					smsDetailsDTOs.add(smsDTO);
				}

				response.setPayload(smsDetailsDTOs);
				response.setMessage("Data fetched successfully!!");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the current day!!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data from the database!!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getLatestRecordWithAlertType(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			DeviceMasterEntity device = deviceRepo.findBybranchCode(branchCode);
			if (device != null) {
				List<SmsDetailsEntity> smsDetail = smsDetailsRepository.findByPanelSerialNo(device.getDeviceSerialNo());
				if (!smsDetail.isEmpty()) {
					Collections.sort(smsDetail, Comparator.comparing(SmsDetailsEntity::getDateTime).reversed());
					SmsDetailsEntity latestRecord = smsDetail.get(0);

					SmsDetailsDTO smsResponse = new SmsDetailsDTO();
					BeanUtils.copyProperties(latestRecord, smsResponse);
					smsResponse.setDateTimes(latestRecord.getDateTime().toString());
					smsResponse.setADC1(latestRecord.getADC1() + "," + device.getVoltage1());
					smsResponse.setADC2(latestRecord.getADC2() + "," + device.getVoltage2());
					smsResponse.setADC3(latestRecord.getADC3() + "," + device.getVoltage3());
					smsResponse.setBATTERY_VOLTAGE(latestRecord.getBATTERY_VOLTAGE());
					smsResponse.setGSM_SIGNAL(latestRecord.getGSM_SIGNAL());
					smsResponse.setEVENT_INFO(latestRecord.getEVENT_INFO());
					smsResponse.setEVENT_TYPE(latestRecord.getEVENT_TYPE());

					smsResponse.setPanelSerialNo(
							deviceRepo.findByDeviceSerialNo(latestRecord.getPanelSerialNo()).getDeviceName());

					@SuppressWarnings("unused")
					String deviceName = device.getDeviceName();
					List<DeviceZoneDetailsEntity> zoneList = zoneRepo.findAllByDeviceId(device.getDeviceId());
					Map<String, String> zoneNameMap = new HashMap<>();
					Map<String, String> zoneAlertMap = new HashMap<>();
					Map<String, String> zoneVendorMap = new HashMap<>();
					zoneList.forEach(zone -> zoneNameMap.put(zone.getZoneOriginalName(), zone.getZoneDesiredName()));
					zoneList.forEach(zone -> zoneAlertMap.put(zone.getZoneOriginalName(), zone.getAlertType()));
					zoneList.forEach(zone -> zoneVendorMap.put(zone.getZoneOriginalName(), zone.getVendors()));
					List<String> zoneValues = new ArrayList<>();
					for (int i = 1; i <= 40; i++) {
						String zoneValue = (String) latestRecord.getClass().getMethod("getZone" + i)
								.invoke(latestRecord);
						if (zoneValue != null && !zoneValue.isEmpty()) {
							String zoneName = zoneNameMap.get("ZONE" + i);
							String alertType = zoneAlertMap.get("ZONE" + i);
							String vendorName = zoneVendorMap.get("ZONE" + i);

							if (zoneName != null) {
								zoneValues.add(zoneName + ": " + alertType + ": " + zoneValue + ":" + vendorName);
							}
						}
					}

					Map<String, Object> responseBody = new HashMap<>();
					responseBody.put("latestRecord", smsResponse);
					responseBody.put("zoneValues", zoneValues);

					response.setPayload(responseBody);
					response.setMessage("Data fetched successfully");
					response.setStatusCode(HttpStatus.OK.value());
				} else {
					response.setMessage("No records found for the given branch code");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
				}
			} else {
				response.setMessage("Device not found for the given branch code");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			response.setMessage("Error occurred while fetching data: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/*
	 * public static void main(String[] args) { String message =
	 * "SMS-LEO\"#0112000000A01457(,NSN0061442,SB007,SI009,SB010,SN011,SI012,A-012,A-014,A-014,A-016,A-018,A-020,A-022,A-000,A-002,SB0111447,)-14:47:00,08-15-2024";
	 * String messageCodes = extractCodesFromMessage(message);
	 * System.out.println("messageCodes:-"+messageCodes); }
	 */

	public String extractCodesFromMessage(String message) {
		List<String> parsedCodes = new ArrayList<>();

		// Extract the part inside the brackets
		int bracketIndex = message.indexOf("(");
		String codesPart = message.substring(bracketIndex + 1, message.indexOf(")"));

		// Split the codes by commas
		String[] codes = codesPart.split(",");

		for (String code : codes) {
			if (!code.isEmpty()) {
				// Check if the code ends with a 4-digit time
				if (code.length() > 4 && code.matches(".*\\d{4}$")) {
					String baseCode = "";
					if (code.startsWith("N")) {
						baseCode = code.substring(1, code.length() - 4);
					} else {
						baseCode = code.substring(0, code.length() - 4);
					}
					parsedCodes.add("'" + baseCode + "'");
				} else {
					parsedCodes.add("'" + code + "'");
				}
			}
		}

		return String.join(",", parsedCodes);
	}

	public ResponseEntity getLeoStaticDataCodes(List<String> messageCode) {
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			List<LeoStaticDataEntity> entityDataEntities = leoStaticDataRepository.getByMessageCodes(messageCode);
			if (!entityDataEntities.isEmpty()) {
				responseEntity.setPayload(entityDataEntities);
				responseEntity.setStatusCode(HttpStatus.OK.value());
				responseEntity.setMessage("Data fetched successfully");
			} else {
				responseEntity.setMessage("No data found");
				responseEntity.setStatusCode(HttpStatus.NO_CONTENT.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("Error Occurred: " + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return responseEntity;
	}

	public ResponseEntity sendMailOnDailyBases() {
		String rootPath = servletContext.getRealPath("DailyReport/");
		String exportLocation = rootPath + "Daily_Status_Report.xlsx";
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			List<EmailSchedulerEntity> emailSchedulerEntities = emailSchedulerRepository.findAll();
			for (EmailSchedulerEntity emailSchedulerEntity : emailSchedulerEntities) {
				if ("Daily".equals(emailSchedulerEntity.getTimePeriod())) {
					exportDailyAlertReportsToFile(exportLocation, emailSchedulerEntity.getBranchId());
				}
			}
			responseEntity.setMessage("Emails sent successfully.");
			responseEntity.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("Error Occurred: " + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return responseEntity;
	}

	public ResponseEntity sendMailOnWeeklyBases() {
		String rootPath = servletContext.getRealPath("WeeklyReport/");
		String exportLocation = rootPath + "Weekly_Status_Report.xlsx";
		System.out.println("WEEKLY RUN");
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			List<EmailSchedulerEntity> emailSchedulerEntities = emailSchedulerRepository.findAll();
			for (EmailSchedulerEntity emailSchedulerEntity : emailSchedulerEntities) {
				// Use equals() for string comparison
				if ("Weekly".equals(emailSchedulerEntity.getTimePeriod())) {
					exportWeeklyAlertReportsToFile(exportLocation, emailSchedulerEntity.getBranchId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("Error Occurred: " + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return responseEntity;
	}

	public ResponseEntity sendMailOnMonthlyBases() {
		System.out.println("Monthly RUn");
		String rootPath = servletContext.getRealPath("MonthlyReport/");
		String exportLocation = rootPath + "Monthly_Status_Report.xlsx";
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			List<EmailSchedulerEntity> emailSchedulerEntities = emailSchedulerRepository.findAll();
			for (EmailSchedulerEntity emailSchedulerEntity : emailSchedulerEntities) {
				if ("Monthly".equals(emailSchedulerEntity.getTimePeriod())) {
					exportWeeklyAlertReportsToFile(exportLocation, emailSchedulerEntity.getBranchId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity.setMessage("Error Occurred: " + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return responseEntity;
	}

	public ResponseEntity getLastSyncScheduler() {
		ResponseEntity response = new ResponseEntity();
		try {
			// ResponseEntity branchResponseEntity =
			// branchMasterService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = branchMasterRepository.findAll();
			List<BranchMasterEntity> nonResponsiveDevices = new ArrayList<>();
			if (!branchList.isEmpty()) {
				LocalDateTime sixHoursAgo = LocalDateTime.now().minusHours(6);
				branchList.forEach(branch -> {
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(branch.getBranchCode());

					if (deviceMasterEntity != null) {

						SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
								.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
						LocalDateTime smsTime = null;
						if (smsDetailsEntity != null) {
							smsTime = smsDetailsEntity.getDateTime().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDateTime();

						}

						// Check if the latest SMS entry is older than 6 hours
						if (smsDetailsEntity == null || smsTime.isBefore(sixHoursAgo)) {

							nonResponsiveDevices.add(branch); // Add branch to non-responsive list
						}
					}
				});
			}

			response.setPayload(nonResponsiveDevices);
			response.setMessage("Non-responsive branches fetched successfully");

		} catch (Exception e) {
			response.setMessage("Error occurred while fetching non-responsive branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllNotWorkingPanelSerialNumbers(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			List<String> notWorkingPanelSerialNumbers = new ArrayList<>();

			for (BranchMasterEntity branch : branchList) {
				String currentBranchCode = branch.getBranchCode();

				DeviceMasterEntity device = deviceRepo.findBybranchCode(currentBranchCode);
				if (device == null) {
					// Skip branches without devices
					continue;
				}

				SmsDetailsEntity smsDetail = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());
				if (smsDetail == null) {
					// Skip devices with no SMS records
					continue;
				}

				boolean hasAInZones = false;
				for (int i = 1; i <= 40; i++) {
					String zoneValue = (String) smsDetail.getClass().getMethod("getZone" + i).invoke(smsDetail);

					if ("A".equals(zoneValue)) {
						hasAInZones = true;
						break; // Stop checking further zones for this device
					}
				}

				if (hasAInZones) {
					notWorkingPanelSerialNumbers.add(device.getDeviceSerialNo());
				}
			}

			response.setMessage("Not working panel serial numbers retrieved successfully");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(notWorkingPanelSerialNumbers);

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("An error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public org.springframework.http.ResponseEntity<Object> findAllAlertsMessage() {
		List<SmsDetailsEntity> smsDetailsEntities = smsDetailsRepository.getAllLatestRecord();
		List<DeviceMasterEntity> deviceMasterEntities = deviceMasterRepository.findAll();
		List<BranchMasterEntity> branchMasterEntities = branchMasterRepository.findAll();
		List<AlertApiResponse> alertApiResponses = smsDetailsEntities.stream()
				.flatMap(sms -> deviceMasterEntities.stream()
						.filter(device -> sms.getPanelSerialNo().equals(device.getDeviceSerialNo()))
						.flatMap(device -> branchMasterEntities.stream()
								.filter(branch -> branch.getBranchCode().equals(device.getBranchCode())).map(branch -> {
									AlertApiResponse response = new AlertApiResponse();
									response.setId(id);
									response.setBranchDesc(branch.getBranchDesc());
									response.setBranchCode(branch.getBranchCode());
									response.setBankName(branch.getBank());
									response.setDateTime(sms.getEntryDateTime());
									String extractMessage;
									try {
										extractMessage = extractCodesFromMessage(sms.getCompleteMessage());
									} catch (Exception e) {
										extractMessage = "Error extracting message: " + e.getMessage();
									}
									response.setAlertMessage(extractMessage);
									response.setPanelSerialNumber(sms.getPanelSerialNo());
									id++;
									return response;
								})))
				.collect(Collectors.toList());

		return org.springframework.http.ResponseEntity.ok(alertApiResponses);
	}

	private SmsDetailsEntity getLatestSmsForThresholdTime(String panelSerialNo, long thresholdHours) {

		Date cutoff = new Date(System.currentTimeMillis() - (thresholdHours * 60 * 60 * 1000));

		SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(panelSerialNo);
		System.out.println("Cutoff time: " + cutoff + " | Latest SMS time: "
				+ (smsDetailsEntity != null ? smsDetailsEntity.getDateTime() : "No SMS found"));

		if (smsDetailsEntity != null) {
			if (smsDetailsEntity.getDateTime() != null && smsDetailsEntity.getDateTime().before(cutoff)) {
				return null;
			}
			return smsDetailsEntity;
		}

		List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
				.findTopByPanelSerialNoOrderByDateTimeDesc(panelSerialNo, 1);

		if (historyList == null || historyList.isEmpty()) {
			return null;
		}

		SmsDetailsHistoryEntity historyEntity = historyList.get(0);

		if (historyEntity.getDateTime() != null && historyEntity.getDateTime().before(cutoff)) {
			return null;
		}

		SmsDetailsEntity fallbackSms = new SmsDetailsEntity();
		BeanUtils.copyProperties(historyEntity, fallbackSms);
		return fallbackSms;
	}

	private boolean checkNonSyncLog(String panelSerialNo) {
		List<NonSyncLogEntity> activeNonSync = nonSyncLogRepository.findActiveNonSyncByPanelSerialNo(panelSerialNo);

		if (activeNonSync == null || activeNonSync.isEmpty())
			return false;

		LocalDateTime cutoff = LocalDateTime.now().minusHours(getThresholdHours());
		NonSyncLogEntity latest = activeNonSync.get(0);

		return latest.getNonSyncStartDatetime() != null && latest.getNonSyncStartDatetime().isBefore(cutoff);
	}

	private SmsDetailsEntity getLatestSms(String panelSerialNo) {

		// 1️⃣ Try latest record from sms_details table
		SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(panelSerialNo);

		if (smsDetailsEntity != null) {
			return smsDetailsEntity;
		}

		// 2️⃣ Fallback to sms_details_history (latest record)
		List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
				.findTopByPanelSerialNoOrderByDateTimeDesc(panelSerialNo, 1);

		if (historyList == null || historyList.isEmpty()) {
			return null;
		}

		SmsDetailsHistoryEntity historyEntity = historyList.get(0);

		// 3️⃣ Copy properties from history → sms entity
		SmsDetailsEntity fallbackSms = new SmsDetailsEntity();

		BeanUtils.copyProperties(historyEntity, fallbackSms);

		return fallbackSms;
	}

	// public ResponseEntity countingZonesByStatus(String branchCode, String
	// zoneType, String status) {
	// ResponseEntity response = new ResponseEntity();
	// List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();
	//
	// ResponseEntity responseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
	// ? (List<BranchMasterEntity>) responseEntity.getPayload()
	// : null;
	//
	// if (branchList == null || branchList.isEmpty()) {
	// response.setPayload(zoneReportDTOs);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Data fetched successfully");
	// return response;
	// }
	//
	// long thresholdHours = getThresholdHours();
	//
	// for (BranchMasterEntity branch : branchList) {
	// DeviceMasterEntity deviceMasterEntity =
	// deviceMasterRepository.findBybranchCode(branch.getBranchCode());
	// if (deviceMasterEntity == null ||
	// "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
	// continue;
	// }
	//
	// List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = deviceZoneRepo
	// .findByZoneDesiredNameAndDeviceId(zoneType,
	// deviceMasterEntity.getDeviceId());
	//
	// if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty())
	// {
	// continue;
	// }
	//
	// // Get SMS with threshold check
	// SmsDetailsEntity smsDetailsEntity =
	// getLatestSmsForThresholdTime(deviceMasterEntity.getDeviceSerialNo(),
	// thresholdHours);
	//
	// // Handle "No Info" case
	// if (smsDetailsEntity == null) {
	// if ("No Info".equalsIgnoreCase(status)) {
	// // Check if device is in non-sync state beyond threshold
	// if (checkNonSyncLog(deviceMasterEntity.getDeviceSerialNo())) {
	// ZoneReportDTO zoneReportDTO = createZoneReportDTO(branch, deviceMasterEntity,
	// null);
	// zoneReportDTOs.add(zoneReportDTO);
	// }
	// }
	// continue;
	// }
	//
	// // Group zones by inputType
	// Map<String, List<DeviceZoneDetailsEntity>> groupedZones =
	// deviceZoneDetailsEntities.stream()
	// .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));
	//
	// for (List<DeviceZoneDetailsEntity> zones : groupedZones.values()) {
	// Map<String, String> groupStatusDetails =
	// determineGroupStatusWithDetails(zones, smsDetailsEntity);
	// String groupStatus = groupStatusDetails.get("groupStatus");
	//
	// // Filter by the status in the request
	// if (!groupStatus.equalsIgnoreCase(status)) {
	// continue;
	// }
	//
	// // Add zone data to DTO list
	// ZoneReportDTO zoneReportDTO = createZoneReportDTO(branch, deviceMasterEntity,
	// zones);
	// zoneReportDTO.setZoneDTOList(buildZoneDTOList(zones, groupStatusDetails));
	// zoneReportDTOs.add(zoneReportDTO);
	// break; // Add only once per branch for this zone type
	// }
	// }
	//
	// zoneReportDTOs.sort(Comparator
	// .comparing(ZoneReportDTO::getParentBranch,
	// Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
	// .thenComparing(ZoneReportDTO::getBranchName,
	// Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
	//
	// response.setPayload(zoneReportDTOs);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Data fetched successfully");
	// return response;
	// }
	//
	// // Helper method to create ZoneReportDTO
	// private ZoneReportDTO createZoneReportDTO(BranchMasterEntity branch,
	// DeviceMasterEntity device,
	// List<DeviceZoneDetailsEntity> zones) {
	// ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
	//
	// if (branch.getParentCode() != null) {
	// BranchMasterEntity parent =
	// branchMasterRepository.findBybranchCode(branch.getParentCode());
	// if (parent != null) {
	// zoneReportDTO.setParentBranch(parent.getBranchDesc());
	// }
	// }
	//
	// zoneReportDTO.setBranchCode(branch.getBranchCode());
	// zoneReportDTO.setBank(branch.getBank());
	// zoneReportDTO.setBranchName(branch.getBranchDesc());
	// zoneReportDTO.setMobile(branch.getMobile());
	// zoneReportDTO.setDeviceSerialNo(device.getDeviceSerialNo());
	//
	// // If zones are provided, create ZoneDTO list; otherwise empty list
	// if (zones != null) {
	// List<ZonesDTO> zonesDTOList = new ArrayList<>();
	// for (DeviceZoneDetailsEntity zone : zones) {
	// ZonesDTO zonesDTO = new ZonesDTO();
	// zonesDTO.setZone(zone.getZoneOriginalName());
	// zonesDTO.setZoneType(zone.getZoneDesiredName());
	// zonesDTO.setAlertType(zone.getAlertType());
	// zonesDTO.setStatus("No Info"); // Default status for No Info case
	// zonesDTOList.add(zonesDTO);
	// }
	// zoneReportDTO.setZoneDTOList(zonesDTOList);
	// } else {
	// zoneReportDTO.setZoneDTOList(new ArrayList<>());
	// }
	//
	// return zoneReportDTO;
	// }
	//
	// Helper method to build ZoneDTO list with proper statuses
	private List<ZonesDTO> buildZoneDTOList(List<DeviceZoneDetailsEntity> zones,
			Map<String, String> groupStatusDetails) {
		List<ZonesDTO> zonesDTOList = new ArrayList<>();
		for (DeviceZoneDetailsEntity zone : zones) {
			ZonesDTO zonesDTO = new ZonesDTO();
			zonesDTO.setZone(zone.getZoneOriginalName());
			zonesDTO.setZoneType(zone.getZoneDesiredName());
			zonesDTO.setAlertType(zone.getAlertType());
			zonesDTO.setStatus(groupStatusDetails.get(zone.getZoneOriginalName()));
			zonesDTOList.add(zonesDTO);
		}
		return zonesDTOList;
	}

	public ResponseEntity countingZonesByStatus(String branchCode, String zoneType, String status) {

		ResponseEntity response = new ResponseEntity();
		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();

		// Get all child branches under given branchCode
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);

		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: null;

		// If no branches found, return empty payload
		if (branchList == null || branchList.isEmpty()) {

			response.setPayload(zoneReportDTOs);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully");

			return response;
		}

		// Get threshold hours once
		long thresholdHours = getThresholdHours();

		// ============================================================
		// BATCH DATA FETCH
		// ============================================================

		// Collect branch codes
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Fetch all devices in one query
		List<DeviceMasterEntity> allDevices = deviceMasterRepository.findByBranchCodeIn(branchCodes);

		Map<String, DeviceMasterEntity> deviceByBranch = allDevices.stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, d -> d, (a, b) -> a));

		// Collect device IDs
		List<String> deviceIds = allDevices.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());

		// Collect serial numbers
		List<String> serialNos = allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// Fetch zones
		Map<String, List<DeviceZoneDetailsEntity>> zonesByDeviceId = deviceIds.isEmpty() ? Collections.emptyMap()
				: deviceZoneRepo.findByZoneDesiredNameAndDeviceIdIn(zoneType, deviceIds).stream()
						.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getDeviceId));

		// Latest SMS
		Map<String, SmsDetailsEntity> latestSmsBySerial = buildLatestSmsMap(serialNos);

		// Latest SMS history
		Map<String, SmsDetailsHistoryEntity> latestHistoryBySerial = buildLatestHistoryMap(serialNos);

		// Parent branches
		List<String> parentCodes = branchList.stream().map(BranchMasterEntity::getParentCode).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());

		Map<String, BranchMasterEntity> parentByCode = parentCodes.isEmpty() ? Collections.emptyMap()
				: branchMasterRepository.findByBranchCodeIn(parentCodes).stream()
						.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, b -> b, (a, b) -> a));

		// ============================================================
		// ⭐ CHANGED 1: FETCH BANK MASTER DATA
		// ============================================================

		Map<String, String> bankNameByCode = bankMasterRepository.findAll().stream()
				.filter(bank -> bank.getBankCode() != null)
				.collect(Collectors.toMap(BankMasterEntity::getBankCode, bank -> bank.getBankName(), (a, b) -> a));

		// ============================================================
		// Active non-sync logs
		// ============================================================

		Map<String, List<NonSyncLogEntity>> nonSyncBySerial = serialNos.isEmpty() ? Collections.emptyMap()
				: nonSyncLogRepository.findActiveNonSyncByPanelSerialNoIn(serialNos).stream()
						.collect(Collectors.groupingBy(NonSyncLogEntity::getPanelSerialNo));

		// ============================================================
		// MAIN LOOP
		// ============================================================

		for (BranchMasterEntity branch : branchList) {

			// Get device for branch
			DeviceMasterEntity deviceMasterEntity = deviceByBranch.get(branch.getBranchCode());

			// Skip if device not found
			if (deviceMasterEntity == null) {
				continue;
			}

			// Skip inactive device
			if ("inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
				continue;
			}

			// Get zones for device
			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = zonesByDeviceId
					.get(deviceMasterEntity.getDeviceId());

			// Skip if no zones
			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				continue;
			}

			// ========================================================
			// GET SMS
			// ========================================================

			SmsDetailsEntity smsDetailsEntity = resolveSmsForThresholdTime(deviceMasterEntity.getDeviceSerialNo(),
					thresholdHours, latestSmsBySerial, latestHistoryBySerial);

			// ========================================================
			// NO INFO CASE
			// ========================================================

			if (smsDetailsEntity == null) {

				if ("All".equalsIgnoreCase(status) || "No Info".equalsIgnoreCase(status)) {

					boolean nonSync = checkNonSyncLog(nonSyncBySerial.get(deviceMasterEntity.getDeviceSerialNo()),
							thresholdHours);

					if (nonSync) {

						// ⭐ CHANGED 2: PASS bankNameByCode
						ZoneReportDTO zoneReportDTO = createZoneReportDTO(branch, deviceMasterEntity, null,
								parentByCode, bankNameByCode);

						zoneReportDTOs.add(zoneReportDTO);
					}
				}

				continue;
			}

			// ========================================================
			// GROUP ZONES BY INPUT TYPE
			// ========================================================

			Map<String, List<DeviceZoneDetailsEntity>> groupedZones = deviceZoneDetailsEntities.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

			// ========================================================
			// DETERMINE STATUS
			// ========================================================

			for (List<DeviceZoneDetailsEntity> zones : groupedZones.values()) {

				Map<String, String> groupStatusDetails = determineGroupStatusWithDetails(zones, smsDetailsEntity);

				String groupStatus = groupStatusDetails.get("groupStatus");

				// ====================================================
				// STATUS FILTER
				// ====================================================

				if (!"All".equalsIgnoreCase(status) && !groupStatus.equalsIgnoreCase(status)) {

					continue;
				}

				// ====================================================
				// CREATE DTO
				// ====================================================

				// ⭐ CHANGED 3: PASS bankNameByCode
				ZoneReportDTO zoneReportDTO = createZoneReportDTO(branch, deviceMasterEntity, zones, parentByCode,
						bankNameByCode);

				zoneReportDTO.setZoneDTOList(buildZoneDTOList(zones, groupStatusDetails));

				zoneReportDTOs.add(zoneReportDTO);

				/*
				 * Keep original logic: Only one result per branch for this zone type.
				 */
				break;
			}
		}

		// ============================================================
		// SORT RESULT
		// ============================================================

		zoneReportDTOs.sort(Comparator
				.comparing(ZoneReportDTO::getParentBranch, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.thenComparing(ZoneReportDTO::getBranchName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

		// ============================================================
		// FINAL RESPONSE
		// ============================================================

		response.setPayload(zoneReportDTOs);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully");

		return response;
	}

	// UPDATED SIGNATURE: added parentByCode parameter (pre-fetched map of parent
	// branches)
	// SAME LOGIC AS BEFORE — only difference: parent branch lookup now uses the Map
	// instead of a DB call
	private ZoneReportDTO createZoneReportDTO(BranchMasterEntity branch, DeviceMasterEntity device,
			List<DeviceZoneDetailsEntity> zones, Map<String, BranchMasterEntity> parentByCode,

			// ⭐ CHANGED 1: Added bankNameByCode
			Map<String, String> bankNameByCode) {

		ZoneReportDTO zoneReportDTO = new ZoneReportDTO();

		// ORIGINAL LOGIC: set parent branch description if parentCode is present
		if (branch.getParentCode() != null) {

			BranchMasterEntity parent = parentByCode.get(branch.getParentCode());

			if (parent != null) {

				zoneReportDTO.setParentBranch(parent.getBranchDesc());
			}
		}

		// ORIGINAL LOGIC: set basic branch/device fields
		zoneReportDTO.setBranchCode(branch.getBranchCode());

		// ⭐ CHANGED 2:
		// OLD:
		// zoneReportDTO.setBank(branch.getBank());

		// NEW: Convert bank code -> bank name
		String bankCode = branch.getBank();

		String bankName = bankNameByCode.get(bankCode);

		zoneReportDTO.setBank(bankName);

		zoneReportDTO.setBranchName(branch.getBranchDesc());

		zoneReportDTO.setMobile(branch.getMobile());

		zoneReportDTO.setDeviceSerialNo(device.getDeviceSerialNo());

		// ORIGINAL LOGIC:
		// if zones are provided, build ZoneDTO list with default "No Info"
		// otherwise set an empty list

		if (zones != null) {

			List<ZonesDTO> zonesDTOList = new ArrayList<>();

			for (DeviceZoneDetailsEntity zone : zones) {

				ZonesDTO zonesDTO = new ZonesDTO();

				zonesDTO.setZone(zone.getZoneOriginalName());

				zonesDTO.setZoneType(zone.getZoneDesiredName());

				zonesDTO.setAlertType(zone.getAlertType());

				zonesDTO.setStatus("No Info");

				zonesDTOList.add(zonesDTO);
			}

			zoneReportDTO.setZoneDTOList(zonesDTOList);

		} else {

			zoneReportDTO.setZoneDTOList(new ArrayList<>());
		}

		return zoneReportDTO;
	}

	// RENAMED/UPDATED: was getLatestSmsForThresholdTime(String panelSerialNo, long
	// thresholdHours)
	// SAME LOGIC AS BEFORE — only difference: reads from pre-fetched Maps instead
	// of querying DB directly
	private SmsDetailsEntity resolveSmsForThresholdTime(String panelSerialNo, long thresholdHours,
			Map<String, SmsDetailsEntity> latestSmsBySerial,
			Map<String, SmsDetailsHistoryEntity> latestHistoryBySerial) {

		// ORIGINAL LOGIC: calculate cutoff time based on threshold hours
		Date cutoff = new Date(System.currentTimeMillis() - (thresholdHours * 60 * 60 * 1000));

		// ORIGINAL LOGIC: try to get latest SMS from primary table (now via pre-fetched
		// map)
		SmsDetailsEntity smsDetailsEntity = latestSmsBySerial.get(panelSerialNo);

		if (smsDetailsEntity != null) {
			// ORIGINAL LOGIC: if SMS is older than cutoff, treat as no valid SMS
			if (smsDetailsEntity.getDateTime() != null && smsDetailsEntity.getDateTime().before(cutoff)) {
				return null;
			}
			return smsDetailsEntity;
		}

		// ORIGINAL LOGIC: fallback to history table if no SMS found in primary table
		// (now via pre-fetched map)
		SmsDetailsHistoryEntity historyEntity = latestHistoryBySerial.get(panelSerialNo);
		if (historyEntity == null) {
			return null;
		}

		// ORIGINAL LOGIC: if history SMS is older than cutoff, treat as no valid SMS
		if (historyEntity.getDateTime() != null && historyEntity.getDateTime().before(cutoff)) {
			return null;
		}

		// ORIGINAL LOGIC: copy history record fields into a SmsDetailsEntity for
		// uniform downstream handling
		SmsDetailsEntity fallbackSms = new SmsDetailsEntity();
		BeanUtils.copyProperties(historyEntity, fallbackSms);
		return fallbackSms;
	}

	// NEW METHOD: Fetches latest SMS record per serial number in ONE DB query
	// (GROUP BY at DB level)
	// Replaces the old per-branch call to
	// smsDetailsRepository.getLatestRecord(serialNo)
	private Map<String, SmsDetailsEntity> buildLatestSmsMap(List<String> serialNos) {
		if (serialNos.isEmpty()) {
			return Collections.emptyMap();
		}
		// Single query fetches the latest row per PANELSERIALNO using DB-side GROUP BY
		// + JOIN
		List<SmsDetailsEntity> latestSmsList = smsDetailsRepository.getLatestRecordsForSerialNos(serialNos);

		// Convert list to Map<serialNo, SmsDetailsEntity> for O(1) lookup in the main
		// loop
		return latestSmsList.stream()
				.collect(Collectors.toMap(SmsDetailsEntity::getPanelSerialNo, s -> s, (a, b) -> a));
	}

	// NEW METHOD: Fetches latest SMS-history record per serial number in ONE DB
	// query
	// Replaces the old per-branch call to
	// smsDetailsHistoryRepository.findTopByPanelSerialNoOrderByDateTimeDesc(serialNo,
	// 1)
	private Map<String, SmsDetailsHistoryEntity> buildLatestHistoryMap(List<String> serialNos) {
		if (serialNos.isEmpty()) {
			return Collections.emptyMap();
		}
		// Single query fetches the latest row per PANELSERIALNO using DB-side GROUP BY
		// + JOIN
		List<SmsDetailsHistoryEntity> latestHistoryList = smsDetailsHistoryRepository
				.getLatestHistoryForSerialNos(serialNos);

		// Convert list to Map<serialNo, SmsDetailsHistoryEntity> for O(1) lookup in the
		// main loop
		return latestHistoryList.stream()
				.collect(Collectors.toMap(SmsDetailsHistoryEntity::getPanelSerialNo, h -> h, (a, b) -> a));
	}

	private boolean checkNonSyncLog(List<NonSyncLogEntity> activeNonSync, long thresholdHours) {
		if (activeNonSync == null || activeNonSync.isEmpty()) {
			return false;
		}
		LocalDateTime cutoff = LocalDateTime.now().minusHours(thresholdHours);
		NonSyncLogEntity latest = activeNonSync.get(0);
		return latest.getNonSyncStartDatetime() != null && latest.getNonSyncStartDatetime().isBefore(cutoff);
	}

	private Map<String, String> determineGroupStatusWithDetails(List<DeviceZoneDetailsEntity> zones,
			SmsDetailsEntity smsDetailsEntity) {
		Map<String, String> zoneStatusMap = new HashMap<>();
		boolean isPrimaryZoneWorking = true;
		boolean hasNonPrimaryA = false;

		for (DeviceZoneDetailsEntity zone : zones) {
			String zoneStatus = getZoneStatus(smsDetailsEntity, zone.getZoneOriginalName());
			zoneStatusMap.put(zone.getZoneOriginalName(), zoneStatus);

			if ("Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				zoneStatusMap.put("groupStatus", "Not Working");
				return zoneStatusMap; // No need to continue if primary is not working
			}
			if (!"Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				hasNonPrimaryA = true;
			}
			if ("Y".equals(zone.getPrimaryFlag()) && "N".equals(zoneStatus)) {
				isPrimaryZoneWorking = true;
			}
		}

		if (isPrimaryZoneWorking && hasNonPrimaryA) {
			zoneStatusMap.put("groupStatus", "Partially Working");
		} else if (isPrimaryZoneWorking) {
			zoneStatusMap.put("groupStatus", "Working");
		} else {
			zoneStatusMap.put("groupStatus", "Unknown");
		}

		return zoneStatusMap;
	}

	// Helper method to determine group status
	private String determineGroupStatus(List<DeviceZoneDetailsEntity> zones, SmsDetailsEntity smsDetailsEntity) {
		boolean isPrimaryZoneWorking = true;
		boolean hasNonPrimaryA = false;

		for (DeviceZoneDetailsEntity zone : zones) {
			String zoneStatus = getZoneStatus(smsDetailsEntity, zone.getZoneOriginalName());
			if ("Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				return "Not Working";
			}
			if (!"Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				hasNonPrimaryA = true;
			}
			if ("Y".equals(zone.getPrimaryFlag()) && "N".equals(zoneStatus)) {
				isPrimaryZoneWorking = true;
			}
		}

		if (isPrimaryZoneWorking && hasNonPrimaryA) {
			return "Partially Working";
		} else if (isPrimaryZoneWorking) {
			return "Working";
		}
		return "Unknown";
	}

	// Helper method to get the zone status from SmsDetailsEntity
	private String getZoneStatus(SmsDetailsEntity smsDetailsEntity, String zoneOriginalName) {
		try {
			Field field = SmsDetailsEntity.class.getDeclaredField(zoneOriginalName.toLowerCase());
			field.setAccessible(true);
			return (String) field.get(smsDetailsEntity);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	// public ResponseEntity getCountsByZoneStatusForToday(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	// Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();
	//
	// List<String> zoneTypes = Arrays.asList("CCTV", "LOCKER CCTV", "Security
	// Alarm", "Fire Alarm", "BACS", "ETL");
	//
	// // Initialize result map
	// for (String zoneType : zoneTypes) {
	// Map<String, Long> statusCounts = new HashMap<>();
	// statusCounts.put("Working", 0L);
	// statusCounts.put("Not Working", 0L);
	// statusCounts.put("Partially Working", 0L);
	// countsByZoneType.put(zoneType, statusCounts);
	// }
	//
	// // ── Step 1: Branch list fetch ──────────────────────────────────────────
	// ResponseEntity responseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
	// ? (List<BranchMasterEntity>) responseEntity.getPayload()
	// : null;
	//
	// if (branchList == null || branchList.isEmpty()) {
	// response.setPayload(countsByZoneType);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Counts fetched successfully");
	// return response;
	// }
	//
	// LocalDate today = LocalDate.now();
	//
	// // ── Step 2: Saare branch codes ek saath nikalo ────────────────────────
	// List<String> branchCodes = branchList.stream()
	// .map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	//
	// // ── Step 3: BULK fetch - DeviceMasterEntity for all branches ──────────
	// // Repository mein ye method add karna hoga (niche dekho)
	// List<DeviceMasterEntity> allDevices =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	//
	// // Sirf active devices rakho, aur branchCode -> device ka map banao
	// Map<String, DeviceMasterEntity> branchToDeviceMap = allDevices.stream()
	// .filter(d -> !"inactive".equalsIgnoreCase(d.getStatus()))
	// .collect(Collectors.toMap(
	// DeviceMasterEntity::getBranchCode,
	// d -> d,
	// (existing, duplicate) -> existing // duplicate branchCode pe pehla rakho
	// ));
	//
	// if (branchToDeviceMap.isEmpty()) {
	// response.setPayload(countsByZoneType);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Counts fetched successfully");
	// return response;
	// }
	//
	// // ── Step 4: BULK fetch - DeviceZoneDetails for all deviceIds + zoneTypes
	// List<String> activeDeviceIds = branchToDeviceMap.values().stream()
	// .map(DeviceMasterEntity::getDeviceId)
	// .collect(Collectors.toList());
	//
	// List<DeviceZoneDetailsEntity> allZones = deviceZoneRepo
	// .findByZoneDesiredNameInAndDeviceIdIn(zoneTypes, activeDeviceIds);
	//
	// // Map: deviceId -> zoneType -> List<DeviceZoneDetailsEntity>
	// Map<String, Map<String, List<DeviceZoneDetailsEntity>>> deviceZoneMap =
	// allZones.stream()
	// .collect(Collectors.groupingBy(
	// DeviceZoneDetailsEntity::getDeviceId,
	// Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName)
	// ));
	//
	// // ── Step 5: BULK fetch - Latest SMS for all serial numbers ────────────
	// List<String> serialNos = branchToDeviceMap.values().stream()
	// .map(DeviceMasterEntity::getDeviceSerialNo)
	// .collect(Collectors.toList());
	//
	// // Repository mein ye method add karna hoga (niche dekho)
	// List<SmsDetailsEntity> latestSmsList =
	// smsDetailsRepository.findLatestSmsForSerialNos(serialNos);
	//
	// // Map: serialNo -> SmsDetailsEntity (sirf aaj ke)
	// Map<String, SmsDetailsEntity> serialToSmsMap = latestSmsList.stream()
	// .filter(sms -> sms.getDateTime() != null)
	// .filter(sms -> {
	// LocalDate smsDate = sms.getDateTime().toInstant()
	// .atZone(ZoneId.systemDefault()).toLocalDate();
	// return smsDate.equals(today);
	// })
	// .collect(Collectors.toMap(
	// SmsDetailsEntity::getPanelSerialNo,
	// s -> s,
	// (existing, duplicate) -> existing
	// ));
	//
	// // ── Step 6: In-memory processing (no more DB calls) ───────────────────
	// branchToDeviceMap.forEach((bCode, device) -> {
	// SmsDetailsEntity sms = serialToSmsMap.get(device.getDeviceSerialNo());
	// if (sms == null) return; // aaj ka SMS nahi mila, skip
	//
	// Map<String, List<DeviceZoneDetailsEntity>> zonesByType =
	// deviceZoneMap.getOrDefault(device.getDeviceId(), Collections.emptyMap());
	//
	// zonesByType.forEach((zoneType, zones) -> {
	// if (!countsByZoneType.containsKey(zoneType)) return;
	//
	// Map<String, List<DeviceZoneDetailsEntity>> groupedByInputType =
	// zones.stream()
	// .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));
	//
	// groupedByInputType.forEach((inputType, groupedZones) -> {
	// String groupStatus = determineGroupStatus(groupedZones, sms);
	// Map<String, Long> statusCounts = countsByZoneType.get(zoneType);
	// statusCounts.put(groupStatus, statusCounts.get(groupStatus) + 1);
	// });
	// });
	// });
	//
	// response.setPayload(countsByZoneType);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Counts fetched successfully");
	// return response;
	// }
	public List<String> getZoneTypesFromDB() {
		List<ParamEntity> params = paramRepository.getParamDataByCodeAndSerial("ZONE_DESIRED_NAME",
				"ZONE_DESIRED_NAME");

		if (params == null || params.isEmpty()) {
			return Collections.emptyList();
		}

		return params.stream().map(ParamEntity::getDescp1) // change to correct field
				.collect(Collectors.toList());
	}

	public ResponseEntity getCountsByZoneStatus(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<String> zoneTypes = getZoneTypesFromDB();
		// List<String> zoneTypes = Arrays.asList("CCTV", "LOCKER CCTV", "Security
		// Alarm", "Fire Alarm", "BACS", "ETL");
		List<String> statuses = Arrays.asList("Working", "Partially Working", "Not Working");

		Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();

		for (String zoneType : zoneTypes) {
			Map<String, Long> statusCounts = new LinkedHashMap<>();

			for (String status : statuses) {
				// Reuse existing function
				ResponseEntity zoneResponse = countingZonesByStatus(branchCode, zoneType, status);

				List<ZoneReportDTO> dtoList = (zoneResponse.getPayload() != null)
						? (List<ZoneReportDTO>) zoneResponse.getPayload()
						: Collections.emptyList();

				// Count unique branches (not individual zones)
				long count = dtoList.stream().map(ZoneReportDTO::getBranchCode).distinct().count();

				statusCounts.put(status, count);
			}

			countsByZoneType.put(zoneType, statusCounts);
		}

		response.setPayload(countsByZoneType);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Counts fetched successfully");
		return response;
	}

	public ResponseEntity getCountsByZoneStatusBUG(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		// List<String> zoneTypes = Arrays.asList("CCTV", "LOCKER CCTV", "Security
		// Alarm", "Fire Alarm", "BACS", "ETL");
		List<String> zoneTypes = getZoneTypesFromDB();
		// 1 DB call — fetch all branches
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: Collections.emptyList();

		if (branchList.isEmpty()) {
			response.setPayload(buildEmptyResult(zoneTypes));
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Counts fetched successfully");
			return response;
		}

		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// 1 DB call — fetch all active devices for all branches at once
		List<DeviceMasterEntity> activeDevices = deviceMasterRepository.findActiveByBranchCodes(branchCodes);

		if (activeDevices.isEmpty()) {
			response.setPayload(buildEmptyResult(zoneTypes));
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Counts fetched successfully");
			return response;
		}

		// Build lookup maps
		Map<String, DeviceMasterEntity> deviceByBranchCode = activeDevices.stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, d -> d, (a, b) -> a));

		List<String> deviceIds = activeDevices.stream().map(DeviceMasterEntity::getDeviceId)
				.collect(Collectors.toList());

		List<String> serialNos = activeDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
				.collect(Collectors.toList());

		// 1 DB call — fetch ALL zone details for ALL devices and ALL zone types
		List<DeviceZoneDetailsEntity> allZones = deviceZoneRepo.findByDeviceIdsAndZoneTypes(deviceIds, zoneTypes);

		// Group: deviceId → zoneType → List<DeviceZoneDetailsEntity>
		Map<String, Map<String, List<DeviceZoneDetailsEntity>>> zonesByDeviceAndType = allZones.stream()
				.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getDeviceId,
						Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName)));

		// 1 DB call — fetch latest SMS for all serial numbers
		Map<String, SmsDetailsEntity> smsMap = fetchLatestSmsForAll(serialNos);

		// Now compute counts — pure in-memory, no DB calls
		Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();

		for (String zoneType : zoneTypes) {
			Map<String, Long> statusCounts = new HashMap<>();
			statusCounts.put("Working", 0L);
			statusCounts.put("Not Working", 0L);
			statusCounts.put("Partially Working", 0L);

			for (BranchMasterEntity branch : branchList) {
				DeviceMasterEntity device = deviceByBranchCode.get(branch.getBranchCode());
				if (device == null)
					continue;

				SmsDetailsEntity sms = smsMap.get(device.getDeviceSerialNo());
				if (sms == null)
					continue;

				Map<String, List<DeviceZoneDetailsEntity>> zonesByType = zonesByDeviceAndType
						.getOrDefault(device.getDeviceId(), Collections.emptyMap());

				List<DeviceZoneDetailsEntity> zones = zonesByType.get(zoneType);
				if (zones == null || zones.isEmpty())
					continue;

				// Group by inputType and determine status
				Map<String, List<DeviceZoneDetailsEntity>> groupedZones = zones.stream()
						.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

				groupedZones.forEach((inputType, groupZones) -> {
					String groupStatus = determineGroupStatus(groupZones, sms);
					statusCounts.put(groupStatus, statusCounts.get(groupStatus) + 1);
				});
			}

			countsByZoneType.put(zoneType, statusCounts);
		}

		response.setPayload(countsByZoneType);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Counts fetched successfully");
		return response;
	}

	private Map<String, SmsDetailsEntity> fetchLatestSmsForAll(List<String> serialNos) {
		Map<String, SmsDetailsEntity> smsMap = new HashMap<>();

		// 1 DB call for primary table
		List<SmsDetailsEntity> smsList = smsDetailsRepository.getLatestRecordsForSerialNos(serialNos);
		smsList.forEach(s -> smsMap.put(s.getPanelSerialNo(), s));

		// Find which serialNos are still missing
		List<String> missingSerialNos = serialNos.stream().filter(sn -> !smsMap.containsKey(sn))
				.collect(Collectors.toList());

		if (!missingSerialNos.isEmpty()) {
			// 1 DB call for history fallback (only for missing ones)
			List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
					.getLatestHistoryForSerialNos(missingSerialNos);

			historyList.forEach(h -> {
				SmsDetailsEntity fallback = new SmsDetailsEntity();
				BeanUtils.copyProperties(h, fallback);
				smsMap.put(h.getPanelSerialNo(), fallback);
			});
		}

		return smsMap;
	}

	private Map<String, Map<String, Long>> buildEmptyResult(List<String> zoneTypes) {
		Map<String, Map<String, Long>> result = new LinkedHashMap<>();
		for (String zoneType : zoneTypes) {
			Map<String, Long> counts = new HashMap<>();
			counts.put("Working", 0L);
			counts.put("Not Working", 0L);
			counts.put("Partially Working", 0L);
			result.put(zoneType, counts);
		}
		return result;
	}

	public ResponseEntity countingZonesForReportTable(String branchCode, String zoneType, String status) {
		ResponseEntity response = new ResponseEntity();
		Map<String, ZoneReportDTO> branchZoneMap = new HashMap<>();

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: null;

		branchList.forEach(branch -> {
			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
			if (deviceMasterEntity == null || "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
				return;
			}

			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = (zoneType == null || zoneType.isEmpty())
					? deviceZoneRepo.findByDeviceId(deviceMasterEntity.getDeviceId())
					: deviceZoneRepo.findByZoneDesiredNameAndDeviceId(zoneType, deviceMasterEntity.getDeviceId());

			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				return;
			}

			SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
					.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
			if (smsDetailsEntity == null) {
				return;
			}

			// Group zones by inputType
			Map<String, List<DeviceZoneDetailsEntity>> groupedZones = deviceZoneDetailsEntities.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

			groupedZones.forEach((inputType, zones) -> {
				String groupStatus = determineGroupStatus(zones, smsDetailsEntity);

				// If status is provided, filter zones based on the group status
				if (status != null && !status.isEmpty() && !groupStatus.equalsIgnoreCase(status)) {
					return;
				}

				// Get or create the ZoneReportDTO for the branch
				ZoneReportDTO zoneReportDTO = branchZoneMap.computeIfAbsent(branch.getBranchCode(), key -> {
					ZoneReportDTO newReportDTO = new ZoneReportDTO();

					newReportDTO.setDeviceSerialNo(deviceMasterEntity.getDeviceSerialNo());
					newReportDTO.setBranchCode(branch.getBranchCode());
					newReportDTO.setBank(branch.getBank());
					newReportDTO.setBranchName(branch.getBranchDesc());
					newReportDTO.setMobile(branch.getMobile());
					newReportDTO.setZoneDTOList(new ArrayList<>());
					return newReportDTO;
				});

				// Add zone data to the branch's zone list
				List<ZonesDTO> zonesDTOList = zoneReportDTO.getZoneDTOList();
				zones.forEach(zone -> {
					ZonesDTO zonesDTO = new ZonesDTO();
					zonesDTO.setZone(zone.getZoneOriginalName());
					zonesDTO.setZoneType(zone.getZoneDesiredName());
					zonesDTO.setAlertType(zone.getAlertType());

					// Get individual zone status
					String individualZoneStatus = getZoneStatus(smsDetailsEntity, zone.getZoneOriginalName());

					// Append individual status for partially working groups
					if ("Partially Working".equals(groupStatus)) {
						zonesDTO.setStatus(groupStatus + " (" + individualZoneStatus + ")");
					} else {
						zonesDTO.setStatus(groupStatus); // Keep original status for other cases
					}

					zonesDTOList.add(zonesDTO);
				});
			});
		});

		// Convert map values to a list
		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>(branchZoneMap.values());
		response.setPayload(zoneReportDTOs);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully");
		return response;
	}

	public org.springframework.http.ResponseEntity<Object> saveNewSMS(SmsDetailsDTO smsDetailsDTO) {
		if (smsDetailsDTO == null) {
			return org.springframework.http.ResponseEntity.status(HttpStatus.NO_CONTENT.value())
					.body("Record not present in request");
		} else {
			SmsDetailsEntity smsDetails = new SmsDetailsEntity();

			BeanUtils.copyProperties(smsDetailsDTO, smsDetails);
			smsDetailsRepository.save(smsDetails);
			return org.springframework.http.ResponseEntity.status(HttpStatus.OK.value()).body(smsDetails);
		}
	}

	public ResponseEntity getTATReport(String branchCode, String zoneType) {
		ResponseEntity response = new ResponseEntity();
		List<TATReportDTO> responseList = new ArrayList<>();

		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches are found by this branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		Map<String, BranchMasterEntity> branchMap = branchList.stream()
				.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for this branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Formatter for dd-MM-yyyy, HH:mm:ss
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");

		for (DeviceMasterEntity device : deviceList) {
			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneRepo
					.findByDeviceIdAndZoneDesiredName(device.getDeviceId(), zoneType);
			if (zoneDetails == null || zoneDetails.isEmpty()) {
				continue;
			}

			for (DeviceZoneDetailsEntity zoneDetail : zoneDetails) {
				SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());
				if (smsDetailsEntity == null) {
					continue;
				}

				LocalDateTime latestRecordTime = smsDetailsEntity.getDateTime().toInstant()
						.atZone(ZoneId.systemDefault()).toLocalDateTime();

				Duration age = Duration.between(latestRecordTime, LocalDateTime.now());
				if (age.toHours() > 6) {
					// Skip this device — data is stale
					continue;
				}

				String zoneOriginalName = zoneDetail.getZoneOriginalName();
				int zoneNumber = Integer.parseInt(zoneOriginalName.replaceAll("\\D+", ""));
				String methodName = "getZone" + zoneNumber;

				try {
					Method zoneMethod = smsDetailsEntity.getClass().getMethod(methodName);
					String zoneValue = (String) zoneMethod.invoke(smsDetailsEntity);

					if (!"N".equals(zoneValue)) {
						// Faulty, find when it became faulty
						List<SmsDetailsEntity> smsHistory = smsDetailsRepository
								.getByPanelSerialNoAndDateTime(device.getDeviceSerialNo());

						LocalDateTime faultySince = null;

						for (int i = 1; i < smsHistory.size(); i++) {
							SmsDetailsEntity historicalSms = smsHistory.get(i);
							String historicalZoneValue = (String) zoneMethod.invoke(historicalSms);

							if ("N".equals(historicalZoneValue)) {
								Date faultDate = smsHistory.get(i - 1).getDateTime();
								faultySince = faultDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
								break;
							}

							if (i == smsHistory.size() - 1) {
								Date oldestDate = historicalSms.getDateTime();
								faultySince = oldestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
							}
						}

						if (faultySince != null) {
							Duration duration = Duration.between(faultySince, LocalDateTime.now());

							long totalMinutes = duration.toMinutes();
							long days = totalMinutes / (24 * 60);
							long hours = (totalMinutes % (24 * 60)) / 60;
							long minutes = totalMinutes % 60;

							TATReportDTO tatReportDTO = new TATReportDTO();
							tatReportDTO.setBranchCode(device.getBranchCode());
							tatReportDTO.setDeviceType(zoneType);
							tatReportDTO.setZone(zoneDetail.getAlertType());

							BranchMasterEntity branch = branchMap.get(device.getBranchCode());
							tatReportDTO.setIfsc(branch != null ? branch.getIfsc() : "N/A");
							tatReportDTO.setBranchName(branch.getBranchDesc());

							// Format createdTime
							tatReportDTO.setCreatedTime(LocalDateTime.now().format(formatter));
							tatReportDTO.setTat(faultySince.format(formatter));
							tatReportDTO.setDowntime(days + "d " + hours + "h " + minutes + "m");

							responseList.add(tatReportDTO);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}

		if (responseList.isEmpty()) {
			response.setMessage("No active zones found for devices under branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			response.setPayload(responseList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("TAT Report generated successfully.");
		}

		return response;
	}

	public ResponseEntity getSystemWiseBranchReport(String branchCode, String zoneType, String status) {
		ResponseEntity response = new ResponseEntity();
		Map<String, ZoneReportDTO> branchZoneMap = new HashMap<>();

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: null;

		if (branchList == null || branchList.isEmpty()) {
			response.setPayload(Collections.emptyList());
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("No branches found");
			return response;
		}

		branchList.forEach(branch -> {
			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
			if (deviceMasterEntity == null || "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
				return;
			}

			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = (zoneType == null || zoneType.isEmpty())
					? deviceZoneRepo.findByDeviceId(deviceMasterEntity.getDeviceId())
					: deviceZoneRepo.findByZoneDesiredNameAndDeviceId(zoneType, deviceMasterEntity.getDeviceId());

			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				return;
			}

			SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
					.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());

			// ✅ Determine if branch is Out of Sync
			boolean isOutOfSync = (smsDetailsEntity == null || !isCurrentDayRecord(smsDetailsEntity.getDateTime()));

			// Group zones by zoneType (system-wise grouping)
			Map<String, List<DeviceZoneDetailsEntity>> systemWiseZones = deviceZoneDetailsEntities.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
			zoneReportDTO.setDeviceSerialNo(deviceMasterEntity.getDeviceSerialNo());
			zoneReportDTO.setBranchCode(branch.getBranchCode());
			zoneReportDTO.setBank(branch.getIfsc());
			BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
					? branchMasterRepository.findBybranchCode(branch.getParentCode())
					: null;

			if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
				// currentBranch.setCreatedBy(parentBranchEntity.getBranchDesc());
				zoneReportDTO.setBranchName(branch.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
			} else {
				zoneReportDTO.setBranchName(branch.getBranchDesc());

			}

			zoneReportDTO.setMobile(branch.getMobile());
			List<ZonesDTO> zonesDTOList = new ArrayList<>();

			systemWiseZones.forEach((systemType, zones) -> {
				// Group by inputType within system
				Map<String, List<DeviceZoneDetailsEntity>> inputGrouped = zones.stream()
						.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

				inputGrouped.forEach((inputType, inputZones) -> {
					String groupStatus;

					if (isOutOfSync) {
						// ✅ Mark as Out of Sync if no SMS or not today's SMS
						groupStatus = "Out of Sync";
					} else {
						// ✅ Normal status determination
						groupStatus = determineGroupStatusForReport(inputZones, smsDetailsEntity);
					}

					// Apply status filter if needed
					if (status != null && !status.isEmpty() && !groupStatus.equalsIgnoreCase(status)) {
						return;
					}

					ZonesDTO summaryDTO = new ZonesDTO();
					summaryDTO.setZoneType(systemType);

					// Combine alertTypes for display
					String combinedAlerts = inputZones.stream().map(DeviceZoneDetailsEntity::getAlertType)
							.filter(Objects::nonNull).distinct().collect(Collectors.joining(", "));
					summaryDTO.setAlertType(combinedAlerts);

					summaryDTO.setStatus(groupStatus);
					zonesDTOList.add(summaryDTO);
				});
			});

			// Always add branch even if zonesDTOList is empty (show Out of Sync)
			if (zonesDTOList.isEmpty() && isOutOfSync) {
				ZonesDTO summaryDTO = new ZonesDTO();
				summaryDTO.setZoneType(zoneType != null ? zoneType : "Unknown");
				summaryDTO.setAlertType("-");
				summaryDTO.setStatus("Out of Sync");
				zonesDTOList.add(summaryDTO);
			}

			zoneReportDTO.setZoneDTOList(zonesDTOList);
			branchZoneMap.put(branch.getBranchCode(), zoneReportDTO);
		});

		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>(branchZoneMap.values());

		zoneReportDTOs.sort(Comparator.comparing(ZoneReportDTO::getBranchName, String.CASE_INSENSITIVE_ORDER));

		response.setPayload(zoneReportDTOs);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully");
		return response;
	}

	// ✅ Helper method to check if record is from current day
	private boolean isCurrentDayRecord(Date recordDate) {
		if (recordDate == null)
			return false;
		LocalDate recordLocalDate = recordDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return recordLocalDate.equals(LocalDate.now());
	}

	// Helper method to determine group status
	private String determineGroupStatusForReport(List<DeviceZoneDetailsEntity> zones,
			SmsDetailsEntity smsDetailsEntity) {
		boolean isPrimaryZoneWorking = true;
		boolean hasNonPrimaryA = false;

		for (DeviceZoneDetailsEntity zone : zones) {
			String zoneStatus = getZoneStatusForReport(smsDetailsEntity, zone.getZoneOriginalName());

			if ("Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				return "Not Working";
			}
			if (!"Y".equals(zone.getPrimaryFlag()) && !"N".equals(zoneStatus)) {
				hasNonPrimaryA = true;
			}
			if ("Y".equals(zone.getPrimaryFlag()) && "N".equals(zoneStatus)) {
				isPrimaryZoneWorking = true;
			}
		}

		if (isPrimaryZoneWorking && hasNonPrimaryA) {
			return "Partially Working";
		} else if (isPrimaryZoneWorking) {
			return "Working";
		}
		return "Unknown";
	}

	// Helper method to get the zone status from SmsDetailsEntity
	private String getZoneStatusForReport(SmsDetailsEntity smsDetailsEntity, String zoneOriginalName) {
		try {
			Field field = SmsDetailsEntity.class.getDeclaredField(zoneOriginalName.toLowerCase());
			field.setAccessible(true);
			return (String) field.get(smsDetailsEntity);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResponseEntity getUpTimeReport(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<TATReportDTO> responseList = new ArrayList<>();

		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for this branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		Map<String, BranchMasterEntity> branchMap = branchList.stream()
				.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for the branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		for (DeviceMasterEntity device : deviceList) {
			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zoneDetails == null || zoneDetails.isEmpty()) {
				continue;
			}

			Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zoneDetails.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : zoneGroups.entrySet()) {
				String deviceType = entry.getKey();
				List<DeviceZoneDetailsEntity> zones = entry.getValue();

				// Fetch today's SMS logs
				LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
				List<SmsDetailsEntity> smsToday = smsDetailsRepository
						.getByPanelSerialNoAndDateTime(device.getDeviceSerialNo()).stream().filter(sms -> {
							LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDateTime();
							return !smsTime.isBefore(startOfToday);
						}).sorted(Comparator.comparing(SmsDetailsEntity::getDateTime)).collect(Collectors.toList());

				// NEW: Get the last SMS from yesterday to determine starting state
				List<SmsDetailsEntity> smsYesterday = smsDetailsRepository
						.getByPanelSerialNoAndDateTime(device.getDeviceSerialNo()).stream().filter(sms -> {
							LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDateTime();
							return smsTime.isBefore(startOfToday) && smsTime.isAfter(startOfToday.minusDays(7)); // Get
																													// last
																													// 7
																													// days
																													// data
						}).sorted(Comparator.comparing(SmsDetailsEntity::getDateTime).reversed()) // Latest first
						.collect(Collectors.toList());

				TATReportDTO dto = new TATReportDTO();
				dto.setBranchCode(device.getBranchCode());
				dto.setDeviceType(deviceType);
				dto.setCreatedTime(LocalDateTime.now().toString());

				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				dto.setIfsc(branch != null ? branch.getIfsc() : "N/A");
				dto.setBranchName(branch != null ? branch.getBranchDesc() : "Unknown");

				// Case 1: No logs for today → Check yesterday's status
				if (smsToday.isEmpty()) {
					// Check if device was faulty yesterday
					boolean wasYesterdayFaulty = checkIfDeviceFaulty(smsYesterday, zones);
					if (wasYesterdayFaulty) {
						dto.setStatus("Out of Sync - Already Faulty");
						dto.setUptime("0");
						dto.setDowntime("100");
						dto.setTat("Device faulty from previous day - 100% down today");
					} else {
						dto.setStatus("Out of Sync");
						dto.setUptime("Out of Sync");
						dto.setDowntime("Out of Sync");
						dto.setTat("Out of Sync");
					}
				} else {
					// Case 2: Logs found → calculate uptime/downtime

					// NEW: Determine initial state from yesterday
					boolean wasInitiallyFaulty = checkIfDeviceFaulty(smsYesterday, zones);

					Duration totalFaulty = Duration.ZERO;
					LocalDateTime faultStart = null;

					// If device was faulty yesterday, start counting from beginning of today
					if (wasInitiallyFaulty) {
						faultStart = startOfToday;
					}

					for (SmsDetailsEntity sms : smsToday) {
						LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();
						boolean isFaulty = isCurrentSmsStateForZones(sms, zones);

						if (isFaulty) {
							if (faultStart == null)
								faultStart = smsTime;
						} else {
							if (faultStart != null) {
								totalFaulty = totalFaulty.plus(Duration.between(faultStart, smsTime));
								faultStart = null;
							}
						}
					}

					// Still faulty till now
					if (faultStart != null) {
						// System.out.println(branch.getBranchDesc()+"--->>"+deviceType+"--->>"+faultStart+"===>>"+LocalDateTime.now());
						totalFaulty = totalFaulty.plus(Duration.between(faultStart, LocalDateTime.now()));
					}

					long totalMinutesToday = Duration.between(startOfToday, LocalDateTime.now()).toMinutes();
					long faultyMinutes = totalFaulty.toMinutes();
					// System.out.println(branch.getBranchDesc()+"--->>"+deviceType+"--->>"+faultyMinutes+"-->>"+totalMinutesToday);
					long faultyPercent = Math.round((double) faultyMinutes / totalMinutesToday * 100);
					long uptimePercent = 100 - faultyPercent;

					dto.setTat(String.format("Faulty %d%% today (%d min)", faultyPercent, faultyMinutes));
					dto.setDowntime(String.valueOf(faultyPercent));
					dto.setUptime(String.valueOf(uptimePercent));
					dto.setStatus("logs found");
				}

				responseList.add(dto);
			}
		}

		if (responseList.isEmpty()) {
			response.setMessage("No devices or logs found for the branch.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {

			responseList.sort(Comparator.comparing(TATReportDTO::getBranchName, String.CASE_INSENSITIVE_ORDER));

			response.setPayload(responseList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Uptime report generated successfully.");
		}

		return response;
	}

	// Helper method to check if device was faulty based on SMS logs
	private boolean checkIfDeviceFaulty(List<SmsDetailsEntity> smsList, List<DeviceZoneDetailsEntity> zones) {
		if (smsList.isEmpty()) {
			return false; // If no previous data, assume it was working
		}

		// Check the most recent SMS
		SmsDetailsEntity latestSms = smsList.get(0); // Already sorted in reverse order
		return isCurrentSmsStateForZones(latestSms, zones);
	}

	// Helper method to check if current SMS state is faulty for given zones
	private boolean isCurrentSmsStateForZones(SmsDetailsEntity sms, List<DeviceZoneDetailsEntity> zones) {
		for (DeviceZoneDetailsEntity zone : zones) {
			int zoneNum = Integer.parseInt(zone.getZoneOriginalName().replaceAll("\\D+", ""));
			String methodName = "getZone" + zoneNum;
			try {
				Method method = sms.getClass().getMethod(methodName);
				String value = (String) method.invoke(sms);
				if (!"N".equals(value)) {
					return true; // Faulty
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false; // Not faulty
	}

	public ResponseEntity getUptimeReportByTime(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<TATReportDTO> responseList = new ArrayList<>();

		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for this branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		Map<String, BranchMasterEntity> branchMap = branchList.stream()
				.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for the branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		for (DeviceMasterEntity device : deviceList) {
			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zoneDetails == null || zoneDetails.isEmpty())
				continue;

			Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zoneDetails.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : zoneGroups.entrySet()) {
				String deviceType = entry.getKey();
				List<DeviceZoneDetailsEntity> zones = entry.getValue();

				List<SmsDetailsEntity> allSms = smsDetailsRepository
						.getByPanelSerialNoAndDateTime(device.getDeviceSerialNo());
				List<SmsDetailsEntity> sortedSms = allSms.stream()
						.sorted(Comparator.comparing(SmsDetailsEntity::getDateTime)).collect(Collectors.toList());

				Map<String, String> tatMap = new LinkedHashMap<>();
				Map<String, Long> uptimeMap = new LinkedHashMap<>();
				Map<String, Long> downtimeMap = new LinkedHashMap<>();

				Map<String, LocalDateTime> timeRanges = new LinkedHashMap<>();
				timeRanges.put("day", LocalDateTime.now().minusDays(1));
				timeRanges.put("week", LocalDateTime.now().minusDays(7));
				timeRanges.put("month", LocalDateTime.now().minusDays(30));
				timeRanges.put("quarter", LocalDateTime.now().minusDays(90));

				for (Map.Entry<String, LocalDateTime> entryRange : timeRanges.entrySet()) {
					String rangeKey = entryRange.getKey();
					LocalDateTime startDate = entryRange.getValue();
					LocalDateTime now = LocalDateTime.now();

					List<SmsDetailsEntity> smsList = sortedSms.stream().filter(sms -> {
						LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();
						return !smsTime.isBefore(startDate);
					}).collect(Collectors.toList());

					// Special handling for "day" uptime
					if ("day".equals(rangeKey)) {
						if (smsList.isEmpty()) {
							// No record found today
							tatMap.put("day", "0% (not synced)");
							uptimeMap.put("day", 0L);
							downtimeMap.put("day", 100L);
							continue; // skip normal calculation
						}

						// Check latest SMS timestamp
						SmsDetailsEntity latestSms = smsList.get(smsList.size() - 1);
						LocalDateTime latestSmsTime = latestSms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();

						if (Duration.between(latestSmsTime, now).toHours() > 6) {
							// Last record older than 6 hours
							tatMap.put("day", "0% (not synced)");
							uptimeMap.put("day", 0L);
							downtimeMap.put("day", 100L);
							continue; // skip normal calculation
						}
					}

					// NEW: For day calculation, check previous day's status to determine initial
					// state
					boolean wasInitiallyFaulty = false;
					if ("day".equals(rangeKey)) {
						// Get SMS logs from before the start date to check initial state
						List<SmsDetailsEntity> previousSms = sortedSms.stream().filter(sms -> {
							LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDateTime();
							return smsTime.isBefore(startDate) && smsTime.isAfter(startDate.minusDays(7)); // Check last
																											// 7 days
						}).sorted(Comparator.comparing(SmsDetailsEntity::getDateTime).reversed()) // Latest first
								.collect(Collectors.toList());

						wasInitiallyFaulty = checkIfDeviceFaulty(previousSms, zones);
					}

					// ---------- Normal faulty time calculation (with initial state fix) ----------
					Duration totalFaulty = Duration.ZERO;
					LocalDateTime faultStart = null;

					// NEW: If device was faulty before the range started, begin counting from start
					if ("day".equals(rangeKey) && wasInitiallyFaulty) {
						faultStart = startDate;
					}

					for (SmsDetailsEntity sms : smsList) {
						LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();
						boolean isFaulty = isCurrentSmsStateForZones(sms, zones);

						if (isFaulty) {
							if (faultStart == null)
								faultStart = smsTime;
						} else {
							if (faultStart != null) {
								totalFaulty = totalFaulty.plus(Duration.between(faultStart, smsTime));
								faultStart = null;
							}
						}
					}

					if (faultStart != null) {
						totalFaulty = totalFaulty.plus(Duration.between(faultStart, now));
					}

					long totalMinutes = Duration.between(startDate, now).toMinutes();
					long faultyMinutes = totalFaulty.toMinutes();
					long faultyPercent = totalMinutes == 0 ? 0
							: Math.round((double) faultyMinutes / totalMinutes * 100);
					long uptimePercent = 100 - faultyPercent;

					tatMap.put(rangeKey, String.format("Faulty %d%% (%d min)", faultyPercent, faultyMinutes));
					uptimeMap.put(rangeKey, uptimePercent);
					downtimeMap.put(rangeKey, faultyPercent);
				}

				TATReportDTO dto = new TATReportDTO();
				dto.setBranchCode(device.getBranchCode());
				dto.setDeviceType(deviceType);
				dto.setStatus("logs found");
				dto.setCreatedTime(LocalDateTime.now().toString());

				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				dto.setIfsc(branch != null ? branch.getIfsc() : "N/A");

				BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
						? branchMasterRepository.findBybranchCode(branch.getParentCode())
						: null;

				if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
					dto.setBranchName(
							branch != null ? branch.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc()
									: "Unknown");
				} else {
					dto.setBranchName(branch != null ? branch.getBranchDesc() : "Unknown");

				}

				dto.setDayTat(tatMap.get("day"));

				// If marked as not synced → show "0% (not synced)" in dayUptime
				if ("0% (not synced)".equals(tatMap.get("day"))) {
					dto.setDayUptime("(not synced) 0");
					dto.setDayDowntime("N/A"); // or keep as "100" if you prefer
					dto.setDayTat("(not synced) 0");
				} else {
					dto.setDayUptime(String.valueOf(uptimeMap.get("day")) + "%");
					dto.setDayDowntime(String.valueOf(downtimeMap.get("day")) + "%");
				}

				dto.setWeekTat(tatMap.get("week"));
				dto.setMonthTat(tatMap.get("month"));
				dto.setQuarterTat(tatMap.get("quarter"));

				dto.setWeekUptime(String.valueOf(uptimeMap.get("week")));
				dto.setMonthUptime(String.valueOf(uptimeMap.get("month")));
				dto.setQuarterUptime(String.valueOf(uptimeMap.get("quarter")));

				dto.setWeekDowntime(String.valueOf(downtimeMap.get("week")));
				dto.setMonthDowntime(String.valueOf(downtimeMap.get("month")));
				dto.setQuarterDowntime(String.valueOf(downtimeMap.get("quarter")));

				responseList.add(dto);
			}
		}

		if (responseList.isEmpty()) {
			response.setMessage("No devices or logs found for the branch.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {

			responseList.sort(Comparator.comparing(TATReportDTO::getBranchName, String.CASE_INSENSITIVE_ORDER));

			response.setPayload(responseList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Uptime report generated successfully.");
		}

		return response;
	}

	// Helper method to check if device was faulty based on SMS logs
	// private boolean checkIfDeviceFaulty(List<SmsDetailsEntity> smsList,
	// List<DeviceZoneDetailsEntity> zones) {
	// if (smsList.isEmpty()) {
	// return false; // If no previous data, assume it was working
	// }
	//
	// // Check the most recent SMS (already sorted in reverse order)
	// SmsDetailsEntity latestSms = smsList.get(0);
	// return isCurrentSmsStateForZones(latestSms, zones);
	// }
	//
	// // Helper method to check if current SMS state is faulty for given zones
	// private boolean isCurrentSmsStateForZones(SmsDetailsEntity sms,
	// List<DeviceZoneDetailsEntity> zones) {
	// for (DeviceZoneDetailsEntity zone : zones) {
	// int zoneNum = Integer.parseInt(zone.getZoneOriginalName().replaceAll("\\D+",
	// ""));
	// String methodName = "getZone" + zoneNum;
	// try {
	// Method method = sms.getClass().getMethod(methodName);
	// String value = (String) method.invoke(sms);
	// if (!"N".equals(value)) {
	// return true; // Faulty
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return false; // Not faulty
	// }

	//

	// public ResponseEntity getCountOfNotSyncedDevices(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	// int notSyncedDeviceCount = 0;
	//
	// ResponseEntity listEntity = branchService.getAllChildren(branchCode);
	// if (listEntity.getPayload() != null) {
	// @SuppressWarnings("unchecked")
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// listEntity.getPayload();
	//
	// // Collect all branch codes
	// List<String> branchCodes = branchList.stream()
	// .map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	//
	// // ✅ 1. Fetch all device masters for these branches in one go
	// List<DeviceMasterEntity> devices =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	//
	// if (!devices.isEmpty()) {
	// // Collect all device serial numbers
	// List<String> deviceSerials = devices.stream()
	// .map(DeviceMasterEntity::getDeviceSerialNo)
	// .filter(Objects::nonNull)
	// .collect(Collectors.toList());
	//
	// // ✅ 2. Fetch latest SMS records for all devices at once
	// // (Assuming your repository can handle this)
	// Map<String, SmsDetailsEntity> latestSmsByDevice = smsDetailsRepository
	// .getLatestRecordsForDevices(deviceSerials)
	// .stream()
	// .collect(Collectors.toMap(SmsDetailsEntity::getPanelSerialNo, e -> e));
	//
	// // ✅ 3. Process in memory
	// Instant now = Instant.now();
	// for (DeviceMasterEntity device : devices) {
	// SmsDetailsEntity sms = latestSmsByDevice.get(device.getDeviceSerialNo());
	// if (sms != null && sms.getDateTime() != null) {
	// long hoursDifference =
	// ChronoUnit.HOURS.between(sms.getDateTime().toInstant(), now);
	// if (hoursDifference > 6) {
	// notSyncedDeviceCount++;
	// }
	// } else {
	// notSyncedDeviceCount++; // no record found = not synced
	// }
	// }
	// }
	//
	// Map<String, Object> responseBody = new HashMap<>();
	// responseBody.put("notSyncedDeviceCount", notSyncedDeviceCount);
	// response.setPayload(responseBody);
	// }
	//
	// return response;
	// }

	public ResponseEntity getCountOfNotSyncedDevices(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		int notSyncedDeviceCount = 0;
		int hours = getThresholdHours();
		System.out.println("ThreshHold Hour ::" + hours);
		ResponseEntity listEntity = branchService.getAllChildren(branchCode);
		if (listEntity.getPayload() != null) {
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) listEntity.getPayload();

			for (BranchMasterEntity branch : branchList) {

				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
				if (deviceMasterEntity != null) {
					SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
							.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());

					if (smsDetailsEntity != null && smsDetailsEntity.getDateTime() != null) {
						long hoursDifference = ChronoUnit.HOURS.between(smsDetailsEntity.getDateTime().toInstant(),
								Instant.now());

						if (hoursDifference >= hours) {
							notSyncedDeviceCount++;
						}
					} else {
						notSyncedDeviceCount++;
					}
				}
			}

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("notSyncedDeviceCount", notSyncedDeviceCount);
			response.setPayload(responseBody);
		}

		return response;
	}

	public ResponseEntity getCountOfNotSyncedDevicesNew(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		int notSyncedDeviceCount = 0;
		int hours = getThresholdHours();
		System.out.println("Enter");
		System.out.println("ThreshHold Hour ::" + hours);

		ResponseEntity listEntity = branchService.getAllChildren(branchCode);
		if (listEntity.getPayload() != null) {
			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) listEntity.getPayload();

			for (BranchMasterEntity branch : branchList) {

				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
				if (deviceMasterEntity != null) {
					String serialNo = deviceMasterEntity.getDeviceSerialNo();

					// SMS table se latest record
					SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(serialNo);

					// Heartbeat table se latest record (LIMIT 1)
					Heartbeat heartbeatEntity = heartbeatRepository.findLatestHeartbeat(serialNo);

					Instant smsInstant = null;
					Instant heartbeatInstant = null;

					if (smsDetailsEntity != null && smsDetailsEntity.getDateTime() != null) {
						smsInstant = smsDetailsEntity.getDateTime().toInstant();
					}

					if (heartbeatEntity != null && heartbeatEntity.getHeartbeatDatetime() != null) {
						heartbeatInstant = heartbeatEntity.getHeartbeatDatetime().atZone(ZoneId.systemDefault())
								.toInstant();
					}

					// Dono mein se jo latest ho wo lo
					Instant latestSyncTime = null;
					if (smsInstant != null && heartbeatInstant != null) {
						System.out.println("SMS INSTANT TIME :: " + smsInstant + " -- HEARTBEAT INSTANT TIME : "
								+ heartbeatInstant);
						latestSyncTime = smsInstant.isAfter(heartbeatInstant) ? smsInstant : heartbeatInstant;
						System.out.println("FINAL OUTPUT TIME ::" + latestSyncTime);
					} else if (smsInstant != null) {
						latestSyncTime = smsInstant;
					} else if (heartbeatInstant != null) {
						latestSyncTime = heartbeatInstant;
					}

					if (latestSyncTime != null) {
						long hoursDifference = ChronoUnit.HOURS.between(latestSyncTime, Instant.now());
						System.out.println("Diffrence b/w latest time and latestSyncTime ::" + latestSyncTime);
						if (hoursDifference >= hours) {
							System.out.println(hoursDifference + ">=" + hours);
							notSyncedDeviceCount++;
						}
					} else {
						// Dono mein koi record nahi = not synced
						notSyncedDeviceCount++;
					}
				}
			}

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("notSyncedDeviceCount", notSyncedDeviceCount);
			response.setPayload(responseBody);
		}

		return response;
	}

	// Helper: dono tables ke timestamps mein se jo latest ho wo return karo
	private Instant getLatestSyncTime(SmsDetailsEntity smsDetailsEntity, Heartbeat heartbeatEntity) {
		Instant smsInstant = null;
		Instant heartbeatInstant = null;

		if (smsDetailsEntity != null && smsDetailsEntity.getDateTime() != null) {
			smsInstant = smsDetailsEntity.getDateTime().toInstant();
		}

		if (heartbeatEntity != null && heartbeatEntity.getHeartbeatDatetime() != null) {
			heartbeatInstant = heartbeatEntity.getHeartbeatDatetime().atZone(ZoneId.systemDefault()).toInstant();
		}

		if (smsInstant != null && heartbeatInstant != null) {
			// Dono hain - jo latest ho wo lo
			return smsInstant.isAfter(heartbeatInstant) ? smsInstant : heartbeatInstant;
		} else if (smsInstant != null) {
			return smsInstant;
		} else if (heartbeatInstant != null) {
			return heartbeatInstant;
		}

		// Dono null hain
		return null;
	}

	private static final int DEFAULT_SYNC_THRESHOLD_HOURS = 36; // Default threshold

	/**
	 * Threshold hours - pehle ParamEntity se fetch karo, nahi mila ya error aaye
	 * toh DEFAULT use karo.
	 */
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

	/**
	 * Checks if a device is synced within the given threshold hours. Returns false
	 * (not synced) if: - smsDetailsEntity is null - dateTime is null - duration
	 * since last sync exceeds thresholdHours
	 */
	private boolean isSynced(SmsDetailsEntity smsDetailsEntity, int thresholdHours) {
		if (smsDetailsEntity == null || smsDetailsEntity.getDateTime() == null) {
			return false; // No record → Not Synced
		}

		Duration duration = Duration.between(smsDetailsEntity.getDateTime().toInstant(), Instant.now());
		return duration.compareTo(Duration.ofHours(thresholdHours)) <= 0;
	}

	// public ResponseEntity getCountOfNotSyncedDevices(String branchCode) {
	//
	// ResponseEntity response = new ResponseEntity();
	// int notSyncedDeviceCount = 0;
	//
	// ResponseEntity listEntity = branchService.getAllChildren(branchCode);
	// if (listEntity.getPayload() != null) {
	//
	// @SuppressWarnings("unchecked")
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// listEntity.getPayload();
	//
	// for (BranchMasterEntity branch : branchList) {
	//
	// DeviceMasterEntity deviceMasterEntity =
	// deviceMasterRepository.findBybranchCode(branch.getBranchCode());
	//
	// if (deviceMasterEntity != null) {
	//
	// SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
	// .getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
	//
	// if (smsDetailsEntity != null && smsDetailsEntity.getDateTime() != null) {
	//
	// Duration duration =
	// Duration.between(smsDetailsEntity.getDateTime().toInstant(), Instant.now());
	//
	// // 🔥 This correctly handles 12h + even 1 second
	// if (duration.compareTo(Duration.ofHours(12)) > 0) {
	// notSyncedDeviceCount++;
	// }
	//
	// } else {
	// // No SMS or datetime → Not Synced
	// notSyncedDeviceCount++;
	// }
	// }
	// }
	//
	// Map<String, Object> responseBody = new HashMap<>();
	// responseBody.put("notSyncedDeviceCount", notSyncedDeviceCount);
	// response.setPayload(responseBody);
	// }
	//
	// return response;
	// }

	/*
	 * public ResponseEntity getAlertWiseTATReport(String branchCode, LocalDateTime
	 * startDate, LocalDateTime endDate) {
	 * 
	 * System.out.println("=== getAlertWiseTATReport START ==="); System.out
	 * .println("Input -> branchCode: " + branchCode + ", startDate: " + startDate +
	 * ", endDate: " + endDate);
	 * 
	 * ResponseEntity response = new ResponseEntity(); List<AlertWiseTATReportDTO>
	 * responseList = new ArrayList<>();
	 * 
	 * // ✅ Step 1: Date validation if (startDate == null || endDate == null ||
	 * endDate.isBefore(startDate)) {
	 * System.out.println("ERROR: Invalid date range.");
	 * response.setMessage("Invalid date range provided.");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); return response; }
	 * 
	 * // ✅ Step 2: Get all child branches ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode); if
	 * (branchResponseEntity.getPayload() == null) {
	 * System.out.println("ERROR: No branches found for branchCode: " + branchCode);
	 * response.setMessage("No branches found for branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	 * branchResponseEntity.getPayload();
	 * System.out.println("Total branches fetched: " + branchList.size());
	 * 
	 * List<String> branchCodes =
	 * branchList.stream().map(BranchMasterEntity::getBranchCode)
	 * .collect(Collectors.toList()); System.out.println("Branch codes: " +
	 * branchCodes);
	 * 
	 * // ✅ Step 3: Get all devices for these branches List<DeviceMasterEntity>
	 * deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes); if
	 * (deviceList == null || deviceList.isEmpty()) {
	 * System.out.println("ERROR: No devices found for branchCode: " + branchCode);
	 * response.setMessage("No devices found for branch: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * System.out.println("Total devices fetched: " + deviceList.size());
	 * 
	 * 
	 * KEY LOGIC: Category = zoneDesiredName + " - " + alertType (e.g.
	 * "CCTV - HDD FAILURE")
	 *
	 * zone_original_name in DeviceZoneDetailsEntity = "ZONE1", "ZONE2" etc.
	 * ticket.zone = "1", "2" etc. Match condition: "ZONE" + ticket.getZone() ==
	 * deviceZoneDetail.getZoneOriginalName()
	 *
	 * So for each ticket, we find which DeviceZoneDetail it belongs to by: 1.
	 * ticket.panelSerialNo == device.deviceSerialNo (or panelSerialNo field) 2.
	 * "ZONE" + ticket.zone == deviceZoneDetail.zoneOriginalName 3. From that
	 * zoneDetail we get zoneDesiredName (device type like CCTV, BACS etc.)
	 *
	 * Category key = zoneDesiredName + " - " + ticket.alertType
	 * 
	 * 
	 * // ✅ Step 4: Build lookup map -> panelSerialNo ->
	 * List<DeviceZoneDetailsEntity> // Also build deviceId -> DeviceMasterEntity
	 * map Map<String, DeviceMasterEntity> serialNoToDevice = new HashMap<>();
	 * Map<String, List<DeviceZoneDetailsEntity>> deviceIdToZones = new HashMap<>();
	 * 
	 * for (DeviceMasterEntity device : deviceList) { if (device.getDeviceSerialNo()
	 * != null) { serialNoToDevice.put(device.getDeviceSerialNo(), device);
	 * System.out.println( "Mapped serialNo: " + device.getDeviceSerialNo() +
	 * " -> deviceId: " + device.getDeviceId()); }
	 * 
	 * List<DeviceZoneDetailsEntity> zones =
	 * deviceZoneRepo.findByDeviceId(device.getDeviceId()); if (zones != null &&
	 * !zones.isEmpty()) { deviceIdToZones.put(device.getDeviceId(), zones);
	 * System.out.println("deviceId: " + device.getDeviceId() + " -> zones count: "
	 * + zones.size()); for (DeviceZoneDetailsEntity z : zones) {
	 * System.out.println("   zoneOriginalName: " + z.getZoneOriginalName() +
	 * " | zoneDesiredName: " + z.getZoneDesiredName() + " | vendor: " +
	 * z.getVendors()); } } else {
	 * System.out.println("  SKIP: No zones for deviceId: " + device.getDeviceId());
	 * } }
	 * 
	 * // ✅ Step 5: Fetch all tickets for all devices in date range List<String>
	 * allSerialNos = new ArrayList<>(serialNoToDevice.keySet());
	 * System.out.println("\nFetching tickets for serialNos: " + allSerialNos);
	 * 
	 * List<Ticket> allTickets =
	 * ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(allSerialNos,
	 * startDate, endDate);
	 * 
	 * if (allTickets == null || allTickets.isEmpty()) {
	 * System.out.println("No tickets found in date range.");
	 * response.setMessage("No alert TAT data found for the given date range.");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * System.out.println("Total tickets fetched: " + allTickets.size());
	 * 
	 * // ✅ Step 6: Fetch all escalation logs for all tickets in one batch
	 * List<String> allTicketNos =
	 * allTickets.stream().map(Ticket::getTicketNo).collect(Collectors.toList());
	 * 
	 * List<EscalationLog> allEscalationLogs =
	 * escalationLogRepository.findByTicketNoIn(allTicketNos);
	 * System.out.println("Total escalation logs fetched: " +
	 * allEscalationLogs.size());
	 * 
	 * // Build map: ticketNo -> Set<escalationLevel> Map<String, Set<String>>
	 * ticketEscalationMap = new HashMap<>(); for (EscalationLog log :
	 * allEscalationLogs) { if (log.getTicketNo() != null && log.getEscLevel() !=
	 * null) { ticketEscalationMap.computeIfAbsent(log.getTicketNo(), k -> new
	 * HashSet<>()) .add(log.getEscLevel().toUpperCase()); } } System.out.
	 * println("ticketEscalationMap size (unique tickets with escalation): " +
	 * ticketEscalationMap.size());
	 * 
	 * 
	 * AlertStats per category key (zoneDesiredName + " - " + alertType) Tracks:
	 * sites, systems, total alerts, L1/L2/L3 buckets, resolved within 24hrs
	 * 
	 * // category -> AlertStats Map<String, AlertStats> categoryStatsMap = new
	 * LinkedHashMap<>();
	 * 
	 * // ✅ Step 7: Process each ticket for (Ticket ticket : allTickets) {
	 * 
	 * System.out.println("\n--- Processing ticket: " + ticket.getTicketNo() +
	 * " | panelSerialNo: " + ticket.getDeviceSerialNo() + " | zone: " +
	 * ticket.getZone() + " | alertType: " + ticket.getZone());
	 * 
	 * // 7a. Find the device by panelSerialNo DeviceMasterEntity device =
	 * serialNoToDevice.get(ticket.getDeviceSerialNo()); if (device == null) {
	 * System.out.println("  SKIP: No device found for panelSerialNo: " +
	 * ticket.getDeviceSerialNo()); continue; } System.out.println(
	 * "  Device found -> deviceId: " + device.getDeviceId() + " | branchCode: " +
	 * device.getBranchCode());
	 * 
	 * // 7b. Build zone key to match: "ZONE" + ticket.zone String ticketZoneKey =
	 * (ticket.getZone() != null && !ticket.getZone().isBlank()) ? "ZONE" +
	 * ticket.getZone().trim() : null;
	 * 
	 * System.out.println("  Constructed zoneKey from ticket: [" + ticketZoneKey +
	 * "]");
	 * 
	 * if (ticketZoneKey == null) {
	 * System.out.println("  SKIP: ticket zone is null/blank."); continue; }
	 * 
	 * // 7c. Find matching DeviceZoneDetail List<DeviceZoneDetailsEntity> zones =
	 * deviceIdToZones.get(device.getDeviceId()); if (zones == null ||
	 * zones.isEmpty()) {
	 * System.out.println("  SKIP: No zones loaded for deviceId: " +
	 * device.getDeviceId()); continue; }
	 * 
	 * DeviceZoneDetailsEntity matchedZone = null; for (DeviceZoneDetailsEntity z :
	 * zones) { System.out.println("  Comparing ticket zoneKey [" + ticketZoneKey +
	 * "] with zoneOriginalName [" + z.getZoneOriginalName() + "]");
	 * 
	 * if (ticketZoneKey.equalsIgnoreCase(z.getZoneOriginalName())) { matchedZone =
	 * z; System.out.println("  MATCH FOUND -> zoneDesiredName: " +
	 * z.getZoneDesiredName()); break; } }
	 * 
	 * if (matchedZone == null) {
	 * System.out.println("  SKIP: No matching zoneOriginalName [" + ticketZoneKey +
	 * "] found in deviceId: " + device.getDeviceId()); continue; }
	 * 
	 * // 7d. Build category key String zoneDesiredName =
	 * matchedZone.getZoneDesiredName() != null ?
	 * matchedZone.getZoneDesiredName().toUpperCase().trim() : "UNKNOWN"; String
	 * alertTypeOfDeviceZone = matchedZone.getAlertType() != null ?
	 * matchedZone.getAlertType().toUpperCase().trim() : "UNKNOWN"; String alertType
	 * = ticket.getZone() != null ? "ZONE" + ticket.getZone().toUpperCase().trim() :
	 * "UNKNOWN";
	 * 
	 * String categoryKey = alertTypeOfDeviceZone + "-" + zoneDesiredName + " - " +
	 * alertType; System.out.println("  Category key: [" + categoryKey + "]");
	 * 
	 * // 7e. Get or create AlertStats for this category AlertStats stats =
	 * categoryStatsMap.computeIfAbsent(categoryKey, k -> new AlertStats());
	 * 
	 * // 7f. Total Sites: unique branchCode per category if (device.getBranchCode()
	 * != null && !device.getBranchCode().isBlank()) {
	 * stats.getSites().add(device.getBranchCode()); System.out.println(
	 * "  Site added: " + device.getBranchCode() + " | Sites so far: " +
	 * stats.getSites().size()); }
	 * 
	 * // 7g. Total Systems: unique deviceId + zoneDesiredName per category String
	 * systemKey = device.getDeviceId() + "_" + zoneDesiredName;
	 * stats.getSystems().add(systemKey); System.out
	 * .println("  System key added: [" + systemKey + "] | Systems so far: " +
	 * stats.getSystems().size());
	 * 
	 * // 7h. Total alerts count stats.incrementTotalAlerts();
	 * System.out.println("  Total alerts so far: " + stats.getTotalAlerts());
	 * 
	 * // 7i. Resolved within 24 hrs check boolean isResolved =
	 * ticket.getResolvedAlertId() != null; if (isResolved && ticket.getUpdateAt()
	 * != null && ticket.getCreateAt() != null) { long hoursToResolve =
	 * ChronoUnit.HOURS.between(ticket.getCreateAt(), ticket.getUpdateAt());
	 * System.out.println("  Resolved ticket -> hours taken: " + hoursToResolve); if
	 * (hoursToResolve < 24) { stats.incrementResolvedWithin24hrs();
	 * System.out.println("  → RESOLVED within 24 hrs ✅"); } }
	 * 
	 * // 7j. Escalation bucket: highest level reached Set<String> levels =
	 * ticketEscalationMap.getOrDefault(ticket.getTicketNo(),
	 * Collections.emptySet());
	 * System.out.println("  Escalation levels for ticket [" + ticket.getTicketNo()
	 * + "]: " + levels);
	 * 
	 * boolean hasL1 = levels.contains("L1"); boolean hasL2 = levels.contains("L2");
	 * boolean hasL3 = levels.contains("L3");
	 * 
	 * if (hasL1 && hasL2 && hasL3) { stats.incrementAlerts72plus();
	 * System.out.println("  → L3 bucket (72+ hrs) ⛔"); } else if (hasL1 && hasL2) {
	 * stats.incrementAlerts48to72();
	 * System.out.println("  → L2 bucket (48-72 hrs) 🔶"); } else if (hasL1) {
	 * stats.incrementAlerts24to48();
	 * System.out.println("  → L1 bucket (24-48 hrs) 🟡"); } else { System.out.
	 * println("  → No escalation bucket (resolved within 24hrs or no escalation)");
	 * } }
	 * 
	 * // ✅ Step 8: Build DTOs
	 * System.out.println("\n=== Building DTOs from categoryStatsMap ===");
	 * System.out.println("Total categories found: " + categoryStatsMap.size());
	 * 
	 * for (Map.Entry<String, AlertStats> entry : categoryStatsMap.entrySet()) {
	 * 
	 * String category = entry.getKey(); AlertStats stats = entry.getValue();
	 * 
	 * System.out.println("\nCategory                       : " + category);
	 * System.out.println("  Sites                        : " +
	 * stats.getSites().size());
	 * System.out.println("  Systems                      : " +
	 * stats.getSystems().size());
	 * System.out.println("  Total Alerts                 : " +
	 * stats.getTotalAlerts());
	 * System.out.println("  24-48 hrs (L1)               : " +
	 * stats.getAlerts24to48());
	 * System.out.println("  48-72 hrs (L2)               : " +
	 * stats.getAlerts48to72());
	 * System.out.println("  72+ hrs   (L3)               : " +
	 * stats.getAlerts72plus());
	 * System.out.println("  Resolved within 24hrs        : " +
	 * stats.getResolvedWithin24hrs());
	 * 
	 * AlertWiseTATReportDTO dto = new AlertWiseTATReportDTO();
	 * dto.setCategoryWise(category); dto.setTotalSites(stats.getSites().size());
	 * dto.setTotalSystems(stats.getSystems().size());
	 * dto.setTotalAlerts(stats.getTotalAlerts());
	 * dto.setAlerts24to48Hrs(stats.getAlerts24to48());
	 * dto.setAlerts48to72Hrs(stats.getAlerts48to72());
	 * dto.setAlerts72plusHrs(stats.getAlerts72plus());
	 * dto.setResolvedWithin24hrs(stats.getResolvedWithin24hrs());
	 * 
	 * responseList.add(dto); }
	 * 
	 * if (responseList.isEmpty()) {
	 * System.out.println("RESULT: responseList is empty. Returning NOT_FOUND.");
	 * response.setMessage("No alert TAT data found for the given date range.");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); } else { responseList
	 * .sort(Comparator.comparing(AlertWiseTATReportDTO::getCategoryWise,
	 * String.CASE_INSENSITIVE_ORDER)); System.out.println("RESULT: " +
	 * responseList.size() + " category rows returned.");
	 * response.setPayload(responseList);
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setMessage("Alert-wise TAT report generated successfully."); }
	 * 
	 * System.out.println("=== getAlertWiseTATReport END ===\n"); return response; }
	 */

	public ResponseEntity getAlertWiseTATReport(String branchCode, LocalDateTime startDate, LocalDateTime endDate) {

		ResponseEntity response = new ResponseEntity();
		Logger logger = LoggerFactory.getLogger(getClass());

		logger.info("=== getAlertWiseTATReport START | branchCode={} | startDate={} | endDate={} ===", branchCode,
				startDate, endDate);

		// Step 1: Date validation
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			logger.warn("Step 1 FAILED: Invalid date range | startDate={} | endDate={}", startDate, endDate);
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		logger.info("Step 1 PASSED: Date validation successful");

		// Step 2: Get all child branches
		logger.info("Step 2: Fetching child branches for branchCode={}", branchCode);
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			logger.warn("Step 2 FAILED: No branches found for branchCode={}", branchCode);
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());
		logger.info("Step 2 DONE: Found {} branches -> {}", branchCodes.size(), branchCodes);

		// Step 3: Get all devices for these branches
		logger.info("Step 3: Fetching devices for {} branches", branchCodes.size());
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			logger.warn("Step 3 FAILED: No devices found for branchCode={}", branchCode);
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		logger.info("Step 3 DONE: Found {} devices", deviceList.size());

		// Step 4: Build lookup maps
		logger.info("Step 4: Building device lookup maps and fetching zones");
		Map<String, DeviceMasterEntity> deviceIdToDevice = new HashMap<>();
		Map<String, DeviceMasterEntity> serialNoToDevice = new HashMap<>();
		Map<String, List<DeviceZoneDetailsEntity>> deviceIdToZones = new HashMap<>();
		List<String> allSerialNos = new ArrayList<>();

		for (DeviceMasterEntity device : deviceList) {
			if (device.getDeviceId() != null) {
				deviceIdToDevice.put(device.getDeviceId(), device);
			}
			if (device.getDeviceSerialNo() != null) {
				serialNoToDevice.put(device.getDeviceSerialNo(), device);
				allSerialNos.add(device.getDeviceSerialNo());
			}
			List<DeviceZoneDetailsEntity> zones = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zones != null && !zones.isEmpty()) {
				deviceIdToZones.put(device.getDeviceId(), zones);
				logger.debug("Step 4: Device {} has {} zones", device.getDeviceId(), zones.size());
			}
		}
		logger.info("Step 4 DONE: deviceIdToDevice={} | serialNoToDevice={} | deviceIdToZones={} | allSerialNos={}",
				deviceIdToDevice.size(), serialNoToDevice.size(), deviceIdToZones.size(), allSerialNos.size());

		// Step 5: Build alertType lookup maps from device_zone_details
		logger.info("Step 5: Building alertType lookup maps from device_zone_details");
		Map<String, String> deviceZoneKeyToAlertType = new HashMap<>();
		Map<String, String> deviceZoneKeyToCritical = new HashMap<>();
		Map<String, Set<String>> alertTypeToAllZones = new HashMap<>();
		Map<String, Set<String>> alertTypeToAllBranchCodes = new HashMap<>();
		Map<String, Set<String>> alertTypeToAllDeviceIds = new HashMap<>();
		Map<String, Set<String>> alertTypeToAllDeviceTypes = new HashMap<>();
		Map<String, String> alertTypeToCriticalFlag = new HashMap<>();

		for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : deviceIdToZones.entrySet()) {
			String deviceId = entry.getKey();
			DeviceMasterEntity device = deviceIdToDevice.get(deviceId);
			if (device == null)
				continue;
			String deviceBranchCode = device.getBranchCode();
			String deviceType = device.getDeviceType();
			if (deviceType == null || deviceType.isBlank()) {
				deviceType = "UNKNOWN";
			}

			for (DeviceZoneDetailsEntity zone : entry.getValue()) {
				if (zone.getZoneOriginalName() != null && zone.getAlertType() != null) {
					String alertType = zone.getAlertType();
					String criticalFlag = zone.getCriticalAlertFlag();

					String key = deviceId + "_" + zone.getZoneOriginalName();
					deviceZoneKeyToAlertType.put(key, alertType);
					deviceZoneKeyToCritical.put(key, criticalFlag);

					alertTypeToAllZones.computeIfAbsent(alertType, k -> new HashSet<>())
							.add(zone.getZoneOriginalName());
					alertTypeToAllBranchCodes.computeIfAbsent(alertType, k -> new HashSet<>()).add(deviceBranchCode);
					alertTypeToAllDeviceIds.computeIfAbsent(alertType, k -> new HashSet<>()).add(deviceId);
					alertTypeToAllDeviceTypes.computeIfAbsent(alertType, k -> new HashSet<>()).add(deviceType);
					alertTypeToCriticalFlag.putIfAbsent(alertType, criticalFlag);
				}
			}
		}
		logger.info("Step 5 DONE: Found {} distinct alertTypes -> {}", alertTypeToAllZones.size(),
				alertTypeToAllZones.keySet());

		for (Map.Entry<String, Set<String>> entry : alertTypeToAllDeviceTypes.entrySet()) {
			logger.info("Step 5: AlertType={} has device types: {}", entry.getKey(), entry.getValue());
		}

		// Step 6: Load escalation matrix
		logger.info("Step 6: Loading escalation matrix");
		List<EscalationMatrix> allEscLevels = escalationMatrixRepo.findAll();

		List<EscalationMatrix> criticalLevels = allEscLevels.stream()
				.filter(e -> e.getCriticalFlag() != null && e.getCriticalFlag())
				.sorted(Comparator.comparingInt(EscalationMatrix::getEscTimeRange)).collect(Collectors.toList());

		List<EscalationMatrix> nonCriticalLevels = allEscLevels.stream()
				.filter(e -> e.getCriticalFlag() == null || !e.getCriticalFlag())
				.sorted(Comparator.comparingInt(EscalationMatrix::getEscTimeRange)).collect(Collectors.toList());

		logger.info("Step 6 DONE: criticalLevels={} | nonCriticalLevels={}", criticalLevels.size(),
				nonCriticalLevels.size());

		// Step 7: Fetch all tickets in date range
		logger.info("Step 7: Fetching tickets for {} serialNos between {} and {}", allSerialNos.size(), startDate,
				endDate);
		List<Ticket> tickets = ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(allSerialNos, startDate,
				endDate);

		int ticketCount = (tickets != null) ? tickets.size() : 0;
		logger.info("Step 7 DONE: Found {} tickets", ticketCount);

		LocalDateTime endOfDay = endDate.toLocalDate().atTime(23, 59, 59);
		logger.info("Step 7: endOfDay (for unresolved gap) = {}", endOfDay);

		// Per alertType accumulators
		Map<String, Integer> alertTypeToTicketCount = new HashMap<>();
		Map<String, Map<String, Integer>> alertTypeToEscCount = new HashMap<>();
		Map<String, Long> alertTypeToTotalGap = new HashMap<>();
		Map<String, Long> alertTypeToTotalTime = new HashMap<>();
		Map<String, Integer> alertTypeToResolvedCount = new HashMap<>();
		Map<String, Integer> alertTypeToUnresolvedCount = new HashMap<>(); // NEW: Unresolved count

		// Step 8: Process each ticket
		logger.info("Step 8: Processing {} tickets for TAT and escalation", ticketCount);
		int processedCount = 0;
		int skippedCount = 0;

		if (tickets != null && !tickets.isEmpty()) {
			for (Ticket ticket : tickets) {
				String serialNo = ticket.getDeviceSerialNo();
				String zoneNo = ticket.getZone();
				if (serialNo == null || zoneNo == null) {
					logger.debug("Step 8: Skipping ticket id={} — serialNo or zoneNo is null", ticket.getId());
					skippedCount++;
					continue;
				}

				String zoneOriginalName = "ZONE" + zoneNo;
				DeviceMasterEntity device = serialNoToDevice.get(serialNo);
				if (device == null) {
					logger.debug("Step 8: Skipping ticket id={} — device not found for serialNo={}", ticket.getId(),
							serialNo);
					skippedCount++;
					continue;
				}

				String deviceId = device.getDeviceId();
				String lookupKey = deviceId + "_" + zoneOriginalName;
				String alertType = deviceZoneKeyToAlertType.get(lookupKey);
				if (alertType == null) {
					logger.debug("Step 8: Skipping ticket id={} — alertType not found for key={}", ticket.getId(),
							lookupKey);
					skippedCount++;
					continue;
				}

				// Count ticket
				alertTypeToTicketCount.merge(alertType, 1, Integer::sum);

				// Get alert generated datetime
				logger.debug("Step 8: Ticket id={} | Fetching sms for alertGeneratedId={}", ticket.getId(),
						ticket.getAlertGeneratedId());
				Date alertGeneratedDate = getSmsDateTime(ticket.getAlertGeneratedId(), smsDetailsRepository,
						smsDetailsHistoryRepository);
				if (alertGeneratedDate == null) {
					logger.warn(
							"Step 8: Ticket id={} — alertGeneratedDate not found in sms_details or history | alertGeneratedId={}",
							ticket.getId(), ticket.getAlertGeneratedId());
					skippedCount++;
					continue;
				}

				// Get alert resolved datetime
				Date alertResolvedDate = null;
				boolean isResolved = false;
				if (ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0) {
					logger.debug("Step 8: Ticket id={} | Fetching sms for resolvedAlertId={}", ticket.getId(),
							ticket.getResolvedAlertId());
					alertResolvedDate = getSmsDateTime(ticket.getResolvedAlertId(), smsDetailsRepository,
							smsDetailsHistoryRepository);
					if (alertResolvedDate != null) {
						isResolved = true;
					}
				}

				// Calculate gap
				long gapMinutes;
				if (isResolved && alertResolvedDate != null) {
					gapMinutes = (alertResolvedDate.getTime() - alertGeneratedDate.getTime()) / (1000 * 60);

					// Only resolved tickets go into averageTat
					alertTypeToTotalGap.merge(alertType, gapMinutes, Long::sum);
					alertTypeToResolvedCount.merge(alertType, 1, Integer::sum);

					logger.debug("Step 8: Ticket id={} | RESOLVED | gap={}min | alertType={}", ticket.getId(),
							gapMinutes, alertType);
				} else {
					long endOfDayMillis = endOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
					gapMinutes = (endOfDayMillis - alertGeneratedDate.getTime()) / (1000 * 60);

					// Count unresolved tickets
					alertTypeToUnresolvedCount.merge(alertType, 1, Integer::sum);

					logger.debug("Step 8: Ticket id={} | UNRESOLVED | gap={}min (till endOfDay) | alertType={}",
							ticket.getId(), gapMinutes, alertType);
				}

				if (gapMinutes < 0) {
					logger.warn("Step 8: Ticket id={} — negative gap={}min, setting to 0", ticket.getId(), gapMinutes);
					gapMinutes = 0;
				}

				// Total time (all tickets including unresolved)
				alertTypeToTotalTime.merge(alertType, gapMinutes, Long::sum);

				// Determine escalation level
				String criticalFlag = alertTypeToCriticalFlag.getOrDefault(alertType, "N");
				List<EscalationMatrix> levels = "Y".equalsIgnoreCase(criticalFlag) ? criticalLevels : nonCriticalLevels;

				String escLevel = determineEscLevel(gapMinutes, levels);
				logger.debug("Step 8: Ticket id={} | criticalFlag={} | escLevel={}", ticket.getId(), criticalFlag,
						escLevel);

				alertTypeToEscCount.computeIfAbsent(alertType, k -> new LinkedHashMap<>()).merge(escLevel, 1,
						Integer::sum);

				processedCount++;
			}
		}

		logger.info("Step 8 DONE: processed={} | skipped={}", processedCount, skippedCount);

		// Step 9: Build final report rows
		logger.info("Step 9: Building final report rows for {} alertTypes", alertTypeToAllZones.size());
		List<Map<String, Object>> reportRows = new ArrayList<>();

		for (String alertType : alertTypeToAllZones.keySet()) {

			List<String> sortedZones = new ArrayList<>(
					alertTypeToAllZones.getOrDefault(alertType, Collections.emptySet()));
			sortedZones.sort(Comparator.comparingInt(z -> Integer.parseInt(z.replace("ZONE", ""))));

			Map<String, Integer> escSummary = alertTypeToEscCount.getOrDefault(alertType, Collections.emptyMap());
			int resolvedCount = alertTypeToResolvedCount.getOrDefault(alertType, 0);
			int unresolvedCount = alertTypeToUnresolvedCount.getOrDefault(alertType, 0); // NEW: Get unresolved count
			long totalGapForAvg = alertTypeToTotalGap.getOrDefault(alertType, 0L);
			long totalTimeAllTickets = alertTypeToTotalTime.getOrDefault(alertType, 0L);
			int totalAlerts = alertTypeToTicketCount.getOrDefault(alertType, 0);

			// Validation: resolved + unresolved should equal total alerts
			if (resolvedCount + unresolvedCount != totalAlerts) {
				logger.warn("Step 9: AlertType={} | Mismatch: resolved({}) + unresolved({}) = {} but totalAlerts={}",
						alertType, resolvedCount, unresolvedCount, resolvedCount + unresolvedCount, totalAlerts);
			}

			// Average TAT = sum of resolved gaps / resolved count
			String averageTat;
			if (resolvedCount > 0) {
				long avgMinutes = totalGapForAvg / resolvedCount;
				averageTat = formatMinutes(avgMinutes);
			} else {
				averageTat = "N/A";
			}

			// Total time = sum of all ticket gaps (resolved + unresolved)
			String totalTime = formatMinutes(totalTimeAllTickets);

			// Get sorted device types list
			Set<String> deviceTypesSet = alertTypeToAllDeviceTypes.getOrDefault(alertType, Collections.emptySet());
			List<String> sortedDeviceTypes = new ArrayList<>(deviceTypesSet);
			Collections.sort(sortedDeviceTypes);

			logger.info(
					"Step 9: alertType={} | totalSites={} | totalSystems={} | deviceTypes={} | totalAlerts={} | resolvedCount={} | unresolvedCount={} | averageTat={} | totalTime={} | escSummary={}",
					alertType, alertTypeToAllBranchCodes.getOrDefault(alertType, Collections.emptySet()).size(),
					alertTypeToAllDeviceIds.getOrDefault(alertType, Collections.emptySet()).size(), sortedDeviceTypes,
					totalAlerts, resolvedCount, unresolvedCount, averageTat, totalTime, escSummary);

			Map<String, Object> row = new LinkedHashMap<>();
			row.put("categoryWise", alertType);
			row.put("totalSites", alertTypeToAllBranchCodes.getOrDefault(alertType, Collections.emptySet()).size());
			row.put("totalSystems", alertTypeToAllDeviceIds.getOrDefault(alertType, Collections.emptySet()).size());
			row.put("deviceTypes", sortedDeviceTypes);
			row.put("totalAlerts", totalAlerts);
			row.put("resolvedCount", resolvedCount);
			row.put("unresolvedCount", unresolvedCount); // NEW: Add unresolved count
			row.put("zones", sortedZones);
			row.put("criticalFlag", alertTypeToCriticalFlag.getOrDefault(alertType, "N"));
			row.put("escalation", escSummary);
			row.put("averageTat", averageTat);
			row.put("totalTime", totalTime);

			reportRows.add(row);
		}

		reportRows.sort(Comparator.comparing(r -> r.get("categoryWise").toString()));

		logger.info("=== getAlertWiseTATReport END | totalRows={} ===", reportRows.size());

		response.setPayload(reportRows);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("TAT report fetched successfully.");
		return response;
	}

	// Helper: Get DATETIME from sms_details, fallback to sms_details_history
	private Date getSmsDateTime(Long smsId, SmsDetailsRepository smsRepo, SmsDetailsHistoryRepository smsHistoryRepo) {
		Logger logger = LoggerFactory.getLogger(getClass());

		if (smsId == null) {
			logger.debug("getSmsDateTime: smsId is null, returning null");
			return null;
		}

		// First: check sms_details by id
		Optional<SmsDetailsEntity> smsOpt = smsRepo.findById(smsId.intValue());
		if (smsOpt.isPresent()) {
			logger.debug("getSmsDateTime: Found in sms_details | id={} | dateTime={}", smsId,
					smsOpt.get().getDateTime());
			return smsOpt.get().getDateTime();
		}

		logger.debug("getSmsDateTime: Not found in sms_details | id={} | trying sms_details_history", smsId);

		// Fallback: check sms_details_history by original_id
		Optional<SmsDetailsHistoryEntity> histOpt = smsHistoryRepo.findByOriginalId(smsId.intValue());
		if (histOpt.isPresent()) {
			logger.debug("getSmsDateTime: Found in sms_details_history | originalId={} | dateTime={}", smsId,
					histOpt.get().getDateTime());
			return histOpt.get().getDateTime();
		}

		logger.warn("getSmsDateTime: NOT found in sms_details or sms_details_history | id={}", smsId);
		return null;
	}

	// // ─── Helper: Determine escalation level
	// ──────────────────────────────────────
	// // gap < L1 → L0 (no escalation)
	// // gap >= L1 → L1
	// // gap >= L2 → L2
	// // gap >= L3 → L3 (highest)
	// private String determineEscLevel(long gapMinutes, List<EscalationMatrix>
	// levels) {
	// Logger logger = LoggerFactory.getLogger(getClass());
	//
	// if (levels == null || levels.isEmpty()) {
	// logger.warn("determineEscLevel: No escalation levels configured, returning
	// N/A");
	// return "N/A";
	// }
	//
	// String currentLevel = "L0"; // default: within time, no escalation breached
	//
	// for (EscalationMatrix level : levels) {
	// if (gapMinutes >= level.getEscTimeRange()) {
	// currentLevel = level.getEscLevel();
	// logger.debug("determineEscLevel: gap={}min >= escTimeRange={}min → upgraded
	// to {}",
	// gapMinutes, level.getEscTimeRange(), currentLevel);
	// } else {
	// break;
	// }
	// }
	//
	// logger.debug("determineEscLevel: Final level={} for gap={}min", currentLevel,
	// gapMinutes);
	// return currentLevel;
	// }

	// ─── Helper: Format minutes to "Xd Yh Zm" ────────────────────────────────────
	private String formatMinutes(long totalMinutes) {
		if (totalMinutes <= 0)
			return "0d 0h 0m";
		long days = totalMinutes / (60 * 24);
		long hours = (totalMinutes % (60 * 24)) / 60;
		long minutes = totalMinutes % 60;
		return days + "d " + hours + "h " + minutes + "m";
	}

	// // ─── Helper: Get DATETIME from sms_details, fallback to sms_details_history
	// ──
	// private Date getSmsDateTime(Long smsId,
	// SmsDetailsRepository smsRepo,
	// SmsDetailsHistoryRepository smsHistoryRepo) {
	// if (smsId == null) return null;
	//
	// // First: check sms_details by id
	// Optional<SmsDetailsEntity> smsOpt = smsRepo.findById(smsId.intValue());
	// if (smsOpt.isPresent()) {
	// return smsOpt.get().getDateTime();
	// }
	//
	// // Fallback: check sms_details_history by original_id
	// Optional<SmsDetailsHistoryEntity> histOpt =
	// smsHistoryRepo.findByOriginalId(smsId.intValue());
	// if (histOpt.isPresent()) {
	// return histOpt.get().getDateTime();
	// }
	//
	// return null; // not found in either table
	// }
	//

	// ─── Helper: Determine escalation level from gap minutes ─────────────────────
	// Levels are sorted by esc_time_range ASC (L1 < L2 < L3)
	// Logic:
	// gap < L1.escTimeRange → "L0" (within time, no escalation)
	// gap >= L1.escTimeRange → "L1"
	// gap >= L2.escTimeRange → "L2"
	// gap >= L3.escTimeRange → "L3" (highest)
	private String determineEscLevel(long gapMinutes, List<EscalationMatrix> levels) {
		if (levels == null || levels.isEmpty())
			return "N/A";

		String currentLevel = "L0"; // default: within time, no escalation breached

		for (EscalationMatrix level : levels) {
			if (gapMinutes >= level.getEscTimeRange()) {
				currentLevel = level.getEscLevel(); // keep upgrading as gap grows
			} else {
				break; // gap is less than this level's time, stop here
			}
		}

		return currentLevel;
	}

	public ResponseEntity getVendorWiseTATReport(String branchCode, LocalDateTime startDate, LocalDateTime endDate) {

		logger.info("=== getVendorWiseTATReport START ===");
		logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}", branchCode, startDate, endDate);

		ResponseEntity response = new ResponseEntity();

		// Step 1: Date validation
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			logger.error("Step 1 FAILED: Invalid date range");
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		// Step 2: Get all child branches
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Step 3: Get all devices
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 4: Build vendor stats maps from device_zone_details
		logger.info("Step 4: Building vendor stats from device_zone_details");

		Map<String, Set<String>> vendorToBranchCodes = new LinkedHashMap<>();
		Map<String, Set<String>> vendorToSystemKeys = new LinkedHashMap<>();
		Map<String, Set<String>> vendorToSerialNos = new LinkedHashMap<>();
		Map<String, Set<String>> vendorToDeviceTypes = new LinkedHashMap<>();

		for (DeviceMasterEntity device : deviceList) {
			String deviceType = device.getDeviceType();
			if (deviceType == null || deviceType.isBlank()) {
				deviceType = "UNKNOWN";
			}

			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zoneDetails == null || zoneDetails.isEmpty()) {
				continue;
			}

			Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zoneDetails.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : zoneGroups.entrySet()) {
				String zoneDesiredName = entry.getKey();
				List<DeviceZoneDetailsEntity> zones = entry.getValue();

				String vendor = zones.stream().map(DeviceZoneDetailsEntity::getVendors)
						.filter(v -> v != null && !v.isBlank()).findFirst().orElse("UNKNOWN");

				vendorToDeviceTypes.computeIfAbsent(vendor, k -> new HashSet<>()).add(deviceType);

				if (device.getBranchCode() != null && !device.getBranchCode().isBlank()) {
					vendorToBranchCodes.computeIfAbsent(vendor, k -> new HashSet<>()).add(device.getBranchCode());
				}

				String systemKey = device.getDeviceId() + "_" + zoneDesiredName;
				vendorToSystemKeys.computeIfAbsent(vendor, k -> new HashSet<>()).add(systemKey);

				if (device.getDeviceSerialNo() != null && !device.getDeviceSerialNo().isBlank()) {
					vendorToSerialNos.computeIfAbsent(vendor, k -> new HashSet<>()).add(device.getDeviceSerialNo());
				}
			}
		}

		// Step 5: Process tickets per vendor
		logger.info("Step 5: Processing tickets per vendor");
		List<Map<String, Object>> reportRows = new ArrayList<>();

		for (String vendor : vendorToBranchCodes.keySet()) {
			logger.info("Processing vendor: {}", vendor);

			Set<String> serialNosSet = vendorToSerialNos.getOrDefault(vendor, Collections.emptySet());
			List<String> serialNos = new ArrayList<>(serialNosSet);

			// Get all raw tickets
			List<Ticket> rawTickets = ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(serialNos, startDate,
					endDate);

			int totalAlerts = (rawTickets == null) ? 0 : rawTickets.size();

			if (rawTickets == null || rawTickets.isEmpty()) {
				reportRows.add(buildVendorRowWithResolution(vendor, vendorToBranchCodes.get(vendor).size(),
						vendorToSystemKeys.getOrDefault(vendor, Collections.emptySet()).size(),
						vendorToDeviceTypes.getOrDefault(vendor, Collections.emptySet()), 0, 0, 0, 0, 0, 0, "N/A",
						"N/A"));
				continue;
			}

			// Get unique ticket numbers
			List<String> uniqueTicketNos = rawTickets.stream().map(Ticket::getTicketNo).distinct()
					.collect(Collectors.toList());

			// Fetch escalation logs
			List<EscalationLog> escalationLogs = escalationLogRepository.findByTicketNoIn(uniqueTicketNos);

			// Build map: ticketNo -> Set of escalation levels (L1, L2, L3)
			Map<String, Set<String>> ticketEscalationLevels = new HashMap<>();
			for (EscalationLog log : escalationLogs) {
				if (log.getTicketNo() != null && log.getEscLevel() != null) {
					ticketEscalationLevels.computeIfAbsent(log.getTicketNo(), k -> new HashSet<>())
							.add(log.getEscLevel().toUpperCase());
				}
			}

			// Get ticket status map (resolved or not)
			Map<String, Boolean> ticketResolvedStatus = new HashMap<>();
			Map<String, Date> ticketResolvedTimeMap = new HashMap<>();

			for (Ticket ticket : rawTickets) {
				String ticketNo = ticket.getTicketNo();
				if (ticketNo != null && !ticketResolvedStatus.containsKey(ticketNo)) {
					boolean isResolved = "C".equalsIgnoreCase(ticket.getTicketStatus())
							|| "O".equalsIgnoreCase(ticket.getTicketStatus())
									&& "C".equalsIgnoreCase(ticket.getZoneStatus());
					ticketResolvedStatus.put(ticketNo, isResolved);

					// Get resolved time if resolved
					if (isResolved && ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0) {
						int resolvedId = ticket.getResolvedAlertId().intValue();
						Optional<SmsDetailsEntity> sms = smsDetailsRepository.findById(resolvedId);
						if (sms.isPresent()) {
							ticketResolvedTimeMap.put(ticketNo, sms.get().getDateTime());
						} else {
							Optional<SmsDetailsHistoryEntity> history = smsDetailsHistoryRepository
									.findByOriginalId(resolvedId);
							history.ifPresent(h -> ticketResolvedTimeMap.put(ticketNo, h.getDateTime()));
						}
					}
				}
			}

			// Process EACH alert row for resolution categories
			int resolvedBeforeL1 = 0; // Resolved without any escalation
			int resolvedAtL1 = 0; // Resolved after L1 escalation (but not L2/L3)
			int resolvedAtL2 = 0; // Resolved after L2 escalation (but not L3)
			int resolvedAtL3 = 0; // Resolved after L3 escalation
			int unresolved = 0; // Not resolved yet

			// For TAT calculation
			long totalGapSeconds = 0;
			int resolvedCountForTat = 0;

			for (Ticket zoneRecord : rawTickets) {
				String ticketNo = zoneRecord.getTicketNo();

				// Get escalation levels for this ticket
				Set<String> levels = ticketEscalationLevels.getOrDefault(ticketNo, Collections.emptySet());
				boolean isResolved = ticketResolvedStatus.getOrDefault(ticketNo, false);

				// Determine resolution category for THIS ALERT ROW
				if (!isResolved) {
					unresolved++;
				} else {
					resolvedCountForTat++;

					// Check which level resolved the ticket
					if (levels.isEmpty()) {
						// No escalation at all -> Resolved before L1
						resolvedBeforeL1++;
					} else if (levels.contains("L3")) {
						// Has L3 escalation -> Resolved at L3
						resolvedAtL3++;
					} else if (levels.contains("L2")) {
						// Has L2 but not L3 -> Resolved at L2
						resolvedAtL2++;
					} else if (levels.contains("L1")) {
						// Has L1 but not L2/L3 -> Resolved at L1
						resolvedAtL1++;
					} else {
						// Fallback - should not happen
						resolvedBeforeL1++;
					}
				}

				// Calculate TAT (gap between generated and resolved time)
				Date alertGeneratedTime = getAlertGeneratedTime(zoneRecord);
				if (alertGeneratedTime == null) {
					continue;
				}

				Date resolvedAlertTime = null;
				if (isResolved) {
					resolvedAlertTime = ticketResolvedTimeMap.get(ticketNo);
				}

				// For unresolved, use endDate as fallback
				if (resolvedAlertTime == null) {
					resolvedAlertTime = java.sql.Timestamp.valueOf(endDate);
				}

				long gap = ChronoUnit.SECONDS.between(
						alertGeneratedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
						resolvedAlertTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

				if (gap < 0)
					gap = 0;

				if (isResolved) {
					totalGapSeconds += gap;
				}
			}

			// Calculate average TAT
			String averageTat;
			if (resolvedCountForTat > 0) {
				long avgSec = totalGapSeconds / resolvedCountForTat;
				averageTat = formatSeconds(avgSec);
			} else {
				averageTat = "N/A";
			}

			// Calculate total time (using all alerts)
			long totalTimeSeconds = 0;
			for (Ticket zoneRecord : rawTickets) {
				Date alertGeneratedTime = getAlertGeneratedTime(zoneRecord);
				if (alertGeneratedTime != null) {
					Date endDateTime = java.sql.Timestamp.valueOf(endDate);
					long gap = ChronoUnit.SECONDS.between(
							alertGeneratedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
							endDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
					if (gap < 0)
						gap = 0;
					totalTimeSeconds += gap;
				}
			}
			String totalTime = formatSeconds(totalTimeSeconds);

			logger.info("Vendor={} | Total={} | BeforeL1={} | L1={} | L2={} | L3={} | Unresolved={}", vendor,
					totalAlerts, resolvedBeforeL1, resolvedAtL1, resolvedAtL2, resolvedAtL3, unresolved);

			reportRows.add(buildVendorRowWithResolution(vendor, vendorToBranchCodes.get(vendor).size(),
					vendorToSystemKeys.getOrDefault(vendor, Collections.emptySet()).size(),
					vendorToDeviceTypes.getOrDefault(vendor, Collections.emptySet()), totalAlerts, resolvedBeforeL1,
					resolvedAtL1, resolvedAtL2, resolvedAtL3, unresolved, averageTat, totalTime));
		}

		// Step 6: Sort and return
		reportRows.sort(Comparator.comparing(r -> r.get("vendorName").toString().toLowerCase()));

		if (reportRows.isEmpty()) {
			response.setMessage("No vendor TAT data found.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			response.setPayload(reportRows);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Vendor-wise TAT report generated successfully.");
		}

		return response;
	}

	// Helper method to get alert generated time
	private Date getAlertGeneratedTime(Ticket ticket) {
		if (ticket.getAlertGeneratedId() == null || ticket.getAlertGeneratedId() == 0) {
			return null;
		}

		int alertGeneratedId = ticket.getAlertGeneratedId().intValue();
		Optional<SmsDetailsEntity> sms = smsDetailsRepository.findById(alertGeneratedId);
		if (sms.isPresent()) {
			return sms.get().getDateTime();
		}

		Optional<SmsDetailsHistoryEntity> smsHis = smsDetailsHistoryRepository.findByOriginalId(alertGeneratedId);
		if (smsHis.isPresent()) {
			return smsHis.get().getDateTime();
		}

		return null;
	}

	// Helper method to determine escalation level
	private String getEscalationLevel(Set<String> levels) {
		if (levels.contains("L3")) {
			return "L3";
		} else if (levels.contains("L2")) {
			return "L2";
		} else if (levels.contains("L1")) {
			return "L1";
		} else {
			return "L0";
		}
	}

	// Updated build method with resolution categories
	private Map<String, Object> buildVendorRowWithResolution(String vendor, int branchCount, int systemCount,
			Set<String> deviceTypes, int totalAlerts, int resolvedBeforeL1, int resolvedAtL1, int resolvedAtL2,
			int resolvedAtL3, int unresolved, String averageTat, String totalTime) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("vendorName", vendor);
		row.put("totalSites", branchCount);
		row.put("totalSystems", systemCount);
		row.put("deviceTypes", String.join(", ", deviceTypes));
		row.put("totalAlerts", totalAlerts);
		row.put("resolvedBeforeL1", resolvedBeforeL1); // Resolved without escalation
		row.put("resolvedAtL1", resolvedAtL1); // Resolved at L1 escalation
		row.put("resolvedAtL2", resolvedAtL2); // Resolved at L2 escalation
		row.put("resolvedAtL3", resolvedAtL3); // Resolved at L3 escalation
		row.put("unresolved", unresolved); // Not resolved yet
		row.put("averageTat", averageTat);
		row.put("totalTime", totalTime);
		return row;
	}

	// ─── Helper: Build vendor row map ────────────────────────────────────────────
	private Map<String, Object> buildVendorRow(String vendorName, int totalSites, int totalSystems, int totalAlerts,
			int l0Count, int l1Count, int l2Count, int l3Count, int resolvedCount, String averageTat,
			String totalTime) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("vendorName", vendorName);
		row.put("totalSites", totalSites);
		row.put("totalSystems", totalSystems);
		row.put("totalAlerts", totalAlerts);

		// Escalation buckets as map
		Map<String, Integer> escalation = new LinkedHashMap<>();
		escalation.put("L0", l0Count); // resolved within 24hrs
		escalation.put("L1", l1Count); // 24-48hrs
		escalation.put("L2", l2Count); // 48-72hrs
		escalation.put("L3", l3Count); // 72+hrs
		row.put("escalation", escalation);
		row.put("resolvedCount", resolvedCount);
		row.put("averageTat", averageTat); // only resolved tickets
		row.put("totalTime", totalTime); // all tickets combined
		return row;
	}

	// ─── Helper: Format seconds to "Xd Yh Zm Ws" ─────────────────────────────────
	private String formatSeconds(long totalSeconds) {
		if (totalSeconds <= 0)
			return "0d 0h 0m 0s";
		long days = totalSeconds / (60 * 60 * 24);
		long hours = (totalSeconds % (60 * 60 * 24)) / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}

	// comment this code on 01 - April-2026
	// public ResponseEntity getVendorWiseTATReport(String branchCode, LocalDateTime
	// startDate, LocalDateTime endDate) {
	//
	// logger.info("=== getVendorWiseTATReport START ===");
	// logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}",
	// branchCode, startDate, endDate);
	//
	// ResponseEntity response = new ResponseEntity();
	// List<VendorWiseTATReportDTO> responseList = new ArrayList<>();
	//
	// if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
	// logger.error("Invalid date range provided");
	// response.setMessage("Invalid date range provided.");
	// response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	// return response;
	// }
	//
	// ResponseEntity branchResponseEntity =
	// branchMasterService.getAllChildren(branchCode);
	//
	// if (branchResponseEntity.getPayload() == null) {
	// logger.error("No branches found for branchCode: {}", branchCode);
	// response.setMessage("No branches found");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseEntity.getPayload();
	// logger.info("Total branches fetched: {}", branchList.size());
	//
	// List<String> branchCodes =
	// branchList.stream().map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	//
	// List<DeviceMasterEntity> deviceList =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	//
	// if (deviceList == null || deviceList.isEmpty()) {
	// logger.error("No devices found for branchCode {}", branchCode);
	// response.setMessage("No devices found");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// logger.info("Total devices fetched {}", deviceList.size());
	//
	// Map<String, VendorStats> vendorStatsMap = new LinkedHashMap<>();
	// Map<String, Set<String>> vendorSerialNosMap = new LinkedHashMap<>();
	//
	// for (DeviceMasterEntity device : deviceList) {
	//
	// logger.debug("Processing device {}", device.getDeviceSerialNo());
	//
	// List<DeviceZoneDetailsEntity> zoneDetails =
	// deviceZoneRepo.findByDeviceId(device.getDeviceId());
	//
	// if (zoneDetails == null || zoneDetails.isEmpty())
	// continue;
	//
	// Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zoneDetails.stream()
	// .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));
	//
	// for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry :
	// zoneGroups.entrySet()) {
	//
	// String deviceType = entry.getKey();
	// List<DeviceZoneDetailsEntity> zones = entry.getValue();
	//
	// String vendor = zones.stream().map(DeviceZoneDetailsEntity::getVendors)
	// .filter(v -> v != null && !v.isBlank()).findFirst().orElse("UNKNOWN");
	//
	// VendorStats stats = vendorStatsMap.computeIfAbsent(vendor, k -> new
	// VendorStats());
	//
	// if (device.getBranchCode() != null && !device.getBranchCode().isBlank()) {
	// stats.getSites().add(device.getBranchCode());
	// }
	//
	// String systemKey = device.getDeviceId() + "_" + deviceType;
	// stats.getSystems().add(systemKey);
	//
	// if (device.getDeviceSerialNo() != null &&
	// !device.getDeviceSerialNo().isBlank()) {
	// vendorSerialNosMap.computeIfAbsent(vendor, k -> new
	// HashSet<>()).add(device.getDeviceSerialNo());
	// }
	// }
	// }
	//
	// logger.info("Fetching ticket data for vendors");
	//
	// for (Map.Entry<String, Set<String>> entry : vendorSerialNosMap.entrySet()) {
	//
	// String vendor = entry.getKey();
	// List<String> serialNos = new ArrayList<>(entry.getValue());
	//
	// logger.info("Processing vendor {}", vendor);
	//
	// List<Ticket> rawTickets =
	// ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(serialNos,
	// startDate,
	// endDate);
	//
	// VendorStats stats = vendorStatsMap.get(vendor);
	// if (stats == null)
	// continue;
	//
	// stats.setTotalAlerts(rawTickets == null ? 0 : rawTickets.size());
	//
	// if (rawTickets == null || rawTickets.isEmpty())
	// continue;
	//
	// // ✅ FIX: ticketNo ke basis pe deduplicate karo — ek ticket ek hi baar
	// process
	// // ho
	// Map<String, Ticket> uniqueTicketMap = new LinkedHashMap<>();
	// for (Ticket ticket : rawTickets) {
	// uniqueTicketMap.putIfAbsent(ticket.getTicketNo(), ticket);
	// }
	// List<Ticket> tickets = new ArrayList<>(uniqueTicketMap.values());
	//
	// logger.info("Vendor {} — total tickets: {}, unique tickets: {}", vendor,
	// rawTickets.size(), tickets.size());
	//
	// List<String> ticketNos =
	// tickets.stream().map(Ticket::getTicketNo).collect(Collectors.toList());
	//
	// // ✅ Escalation logs for all tickets of this vendor
	// List<EscalationLog> escalationLogs =
	// escalationLogRepository.findByTicketNoIn(ticketNos);
	// logger.info("Vendor {} — escalation logs found: {}", vendor,
	// escalationLogs.size());
	//
	// // ✅ ticketNo → Set of escalation levels (L1, L2, L3)
	// Map<String, Set<String>> ticketEscalationLevels = new HashMap<>();
	// for (EscalationLog log : escalationLogs) {
	// String ticketNo = log.getTicketNo();
	// String level = log.getEscLevel();
	// if (ticketNo != null && level != null) {
	// ticketEscalationLevels.computeIfAbsent(ticketNo, k -> new
	// HashSet<>()).add(level.toUpperCase());
	// }
	// }
	//
	// // ✅ Resolved within 24hrs
	// int resolvedWithin24hrs = 0;
	// for (Ticket ticket : tickets) {
	// boolean isResolved = ticket.getResolvedAlertId() != null;
	// if (isResolved && ticket.getUpdateAt() != null && ticket.getCreateAt() !=
	// null) {
	// long hoursToResolve = ChronoUnit.HOURS.between(ticket.getCreateAt(),
	// ticket.getUpdateAt());
	// logger.debug("Ticket {} hoursToResolve: {}", ticket.getTicketNo(),
	// hoursToResolve);
	// if (hoursToResolve < 24) {
	// resolvedWithin24hrs++;
	// }
	// }
	// }
	// logger.info("Vendor {} — resolvedWithin24hrs: {}", vendor,
	// resolvedWithin24hrs);
	// stats.setResolvedWithin24hrs(resolvedWithin24hrs);
	//
	// // ✅ Escalation bucket counts
	// int l1Count = 0;
	// int l2Count = 0;
	// int l3Count = 0;
	//
	// for (Map.Entry<String, Set<String>> ticketEntry :
	// ticketEscalationLevels.entrySet()) {
	// Set<String> levels = ticketEntry.getValue();
	//
	// boolean hasL1 = levels.contains("L1");
	// boolean hasL2 = levels.contains("L2");
	// boolean hasL3 = levels.contains("L3");
	//
	// if (hasL1 && hasL2 && hasL3) {
	// l3Count++;
	// } else if (hasL1 && hasL2) {
	// l2Count++;
	// } else if (hasL1) {
	// l1Count++;
	// }
	// }
	//
	// logger.info("Vendor {} — L1(24-48): {}, L2(48-72): {}, L3(72+): {}", vendor,
	// l1Count, l2Count, l3Count);
	//
	// stats.setAlerts24to48(l1Count);
	// stats.setAlerts48to72(l2Count);
	// stats.setAlerts72plus(l3Count);
	//
	// // ✅ Average TAT Calculation
	// long totalGapSeconds = 0;
	// int totalZoneCount = 0;
	//
	// for (Ticket ticket : tickets) { // ← ab unique tickets pe hi chalega
	//
	// String ticketNo = ticket.getTicketNo();
	//
	// List<Ticket> ticketZoneRecords = ticketRepository.findByTicketNo(ticketNo);
	//
	// if (ticketZoneRecords == null || ticketZoneRecords.isEmpty())
	// continue;
	//
	// long ticketGap = 0;
	//
	// for (Ticket record : ticketZoneRecords) {
	//
	// Date alertGeneratedTime = null;
	// Date resolvedAlertTime = null;
	//
	// // ── ALERT GENERATED TIME ──────────────────────────────
	// Long alertGeneratedIdLong = record.getAlertGeneratedId();
	//
	// if (alertGeneratedIdLong != null && alertGeneratedIdLong != 0) {
	//
	// int alertGeneratedId = Math.toIntExact(alertGeneratedIdLong);
	//
	// Optional<SmsDetailsEntity> sms =
	// smsDetailsRepository.findById(alertGeneratedId);
	//
	// if (sms.isPresent()) {
	// alertGeneratedTime = sms.get().getDateTime();
	// logger.debug("alertGeneratedId: {} → sms_details: {}", alertGeneratedId,
	// alertGeneratedTime);
	// } else {
	// Optional<SmsDetailsHistoryEntity> smsHis = smsDetailsHistoryRepository
	// .findByOriginalId(alertGeneratedId);
	// if (smsHis.isPresent()) {
	// alertGeneratedTime = smsHis.get().getDateTime();
	// logger.debug("alertGeneratedId: {} → sms_details_history: {}",
	// alertGeneratedId,
	// alertGeneratedTime);
	// } else {
	// logger.warn("alertGeneratedId: {} → NOT FOUND", alertGeneratedId);
	// }
	// }
	// }
	//
	// // ── RESOLVED ALERT TIME ───────────────────────────────
	// Long resolvedAlertIdLong = record.getResolvedAlertId();
	//
	// if (resolvedAlertIdLong != null && resolvedAlertIdLong != 0) {
	//
	// int resolvedAlertId = Math.toIntExact(resolvedAlertIdLong);
	//
	// Optional<SmsDetailsEntity> sms =
	// smsDetailsRepository.findById(resolvedAlertId);
	//
	// if (sms.isPresent()) {
	// resolvedAlertTime = sms.get().getDateTime();
	// logger.debug("resolvedAlertId: {} → sms_details: {}", resolvedAlertId,
	// resolvedAlertTime);
	// } else {
	// Optional<SmsDetailsHistoryEntity> smsHis = smsDetailsHistoryRepository
	// .findByOriginalId(resolvedAlertId);
	// if (smsHis.isPresent()) {
	// resolvedAlertTime = smsHis.get().getDateTime();
	// logger.debug("resolvedAlertId: {} → sms_details_history: {}",
	// resolvedAlertId,
	// resolvedAlertTime);
	// } else {
	// logger.warn("resolvedAlertId: {} → NOT FOUND", resolvedAlertId);
	// }
	// }
	//
	// } else {
	// // ✅ Not resolved — use endDate as fallback
	// resolvedAlertTime = java.sql.Timestamp.valueOf(endDate);
	// logger.info("Ticket {} record resolvedAlertId is null → using endDate {} as
	// fallback", ticketNo,
	// endDate);
	// }
	//
	// // ── GAP CALCULATION ───────────────────────────────────
	// if (alertGeneratedTime != null && resolvedAlertTime != null) {
	//
	// LocalDateTime alertTime =
	// alertGeneratedTime.toInstant().atZone(ZoneId.systemDefault())
	// .toLocalDateTime();
	//
	// LocalDateTime resolvedTime =
	// resolvedAlertTime.toInstant().atZone(ZoneId.systemDefault())
	// .toLocalDateTime();
	//
	// long gap = ChronoUnit.SECONDS.between(alertTime, resolvedTime);
	//
	// if (gap < 0) {
	// logger.warn("Negative gap ({} sec) for ticket {}, treating as 0", gap,
	// ticketNo);
	// gap = 0;
	// }
	//
	// logger.debug("Gap for row: {} seconds", gap);
	// ticketGap += gap;
	//
	// } else {
	// logger.warn("SKIP gap: alertGeneratedTime is null for ticket {}", ticketNo);
	// }
	// }
	//
	// totalGapSeconds += ticketGap;
	// totalZoneCount += ticketZoneRecords.size();
	// }
	//
	// // ✅ Final Average TAT
	// long avgTAT = 0;
	// String avgFormatted = "N/A";
	//
	// if (totalZoneCount > 0) {
	// avgTAT = totalGapSeconds / totalZoneCount;
	// long hrs = avgTAT / 3600;
	// long mins = (avgTAT % 3600) / 60;
	// long secs = avgTAT % 60;
	// avgFormatted = hrs + "h " + mins + "m " + secs + "s";
	// }
	//
	// logger.info("Vendor {} — Avg TAT: {} ({} seconds)", vendor, avgFormatted,
	// avgTAT);
	//
	// stats.setAverageTATSeconds(avgTAT);
	// stats.setAverageTATFormatted(avgFormatted);
	// }
	//
	// // ✅ Build DTOs
	// for (Map.Entry<String, VendorStats> entry : vendorStatsMap.entrySet()) {
	//
	// String vendorName = entry.getKey();
	// VendorStats stats = entry.getValue();
	//
	// VendorWiseTATReportDTO dto = new VendorWiseTATReportDTO();
	// dto.setVendorName(vendorName);
	// dto.setTotalSites(stats.getSites().size());
	// dto.setTotalSystems(stats.getSystems().size());
	// dto.setTotalAlerts(stats.getTotalAlerts());
	// dto.setAlerts24to48Hrs(stats.getAlerts24to48());
	// dto.setAlerts48to72Hrs(stats.getAlerts48to72());
	// dto.setAlerts72plusHrs(stats.getAlerts72plus());
	// dto.setResolvedWithin24hrs(stats.getResolvedWithin24hrs());
	// dto.setAverageTATSeconds(stats.getAverageTATSeconds());
	// dto.setAverageTATFormatted(stats.getAverageTATFormatted());
	//
	// responseList.add(dto);
	// }
	//
	// if (responseList.isEmpty()) {
	// response.setMessage("No vendor TAT data found.");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// } else {
	// responseList
	// .sort(Comparator.comparing(VendorWiseTATReportDTO::getVendorName,
	// String.CASE_INSENSITIVE_ORDER));
	// response.setPayload(responseList);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Vendor-wise TAT report generated successfully.");
	// }
	//
	// logger.info("=== getVendorWiseTATReport END ===");
	// return response;
	// }

	public ResponseEntity getUptimeReportByDateRange(String branchCode, LocalDateTime startDate,
			LocalDateTime endDate) {

		ResponseEntity response = new ResponseEntity();
		List<TATReportDTO> responseList = new ArrayList<>();

		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		// ✅ Get all child branches
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		Map<String, BranchMasterEntity> branchMap = branchList.stream()
				.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

		// ✅ Get devices of branches
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

		for (DeviceMasterEntity device : deviceList) {

			List<DeviceZoneDetailsEntity> zoneDetails = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zoneDetails == null || zoneDetails.isEmpty())
				continue;

			Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zoneDetails.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : zoneGroups.entrySet()) {

				String deviceType = entry.getKey();
				List<DeviceZoneDetailsEntity> zones = entry.getValue();

				List<SmsDetailsEntity> allSms = new ArrayList<>();

				// Fetch from LIVE table (last 2 days)
				if (!endDate.isBefore(twoDaysAgo)) {
					List<SmsDetailsEntity> recentSms = smsDetailsRepository
							.getByPanelSerialNoAndDateTime(device.getDeviceSerialNo());
					if (recentSms != null)
						allSms.addAll(recentSms);
				}

				// Fetch from HISTORY table
				if (startDate.isBefore(twoDaysAgo)) {
					List<SmsDetailsHistoryEntity> historySms = smsDetailsHistoryRepository
							.findByPanelSerialNoInAndDateTimeBetweenOrderByPanelSerialNoAscDateTimeAsc(
									Collections.singletonList(device.getDeviceSerialNo()),
									Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()),
									Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));

					if (historySms != null && !historySms.isEmpty()) {
						List<SmsDetailsEntity> converted = historySms.stream().map(this::convertHistoryToEntity)
								.collect(Collectors.toList());
						allSms.addAll(converted);
					}
				}

				if (allSms.isEmpty())
					continue;

				List<SmsDetailsEntity> sortedSms = allSms.stream()
						.sorted(Comparator.comparing(SmsDetailsEntity::getDateTime)).collect(Collectors.toList());

				// Filter SMS by date range
				List<SmsDetailsEntity> smsList = sortedSms.stream().filter(sms -> {
					LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();
					return !smsTime.isBefore(startDate) && !smsTime.isAfter(endDate);
				}).collect(Collectors.toList());

				if (smsList.isEmpty())
					continue;

				boolean wasInitiallyFaulty = checkIfDeviceFaulty(sortedSms, zones);
				Duration totalFaulty = Duration.ZERO;
				LocalDateTime faultStart = wasInitiallyFaulty ? startDate : null;

				for (SmsDetailsEntity sms : smsList) {
					LocalDateTime smsTime = sms.getDateTime().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();

					boolean isFaulty = isCurrentSmsStateForZones(sms, zones);

					if (isFaulty) {
						if (faultStart == null)
							faultStart = smsTime;
					} else {
						if (faultStart != null) {
							totalFaulty = totalFaulty.plus(Duration.between(faultStart, smsTime));
							faultStart = null;
						}
					}
				}

				if (faultStart != null) {
					totalFaulty = totalFaulty.plus(Duration.between(faultStart, endDate));
				}

				long totalMinutes = Duration.between(startDate, endDate).toMinutes();
				long faultyMinutes = totalFaulty.toMinutes();
				long faultyPercent = totalMinutes == 0 ? 0 : Math.round(((double) faultyMinutes / totalMinutes) * 100);
				long uptimePercent = 100 - faultyPercent;

				TATReportDTO dto = new TATReportDTO();
				dto.setBranchCode(device.getBranchCode());

				dto.setDeviceType(deviceType);
				dto.setStatus("logs found");
				dto.setCreatedTime(LocalDateTime.now().toString());

				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				dto.setIfsc(branch != null ? branch.getIfsc() : "N/A");
				// dto.setBranchName(branch != null ? branch.getBranchDesc() : "Unknown");

				BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
						? branchMasterRepository.findBybranchCode(branch.getParentCode())
						: null;

				if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
					dto.setBranchName(
							branch != null ? branch.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc()
									: "Unknown");
				} else {
					dto.setBranchName(branch != null ? branch.getBranchDesc() : "Unknown");

				}

				dto.setUptime(uptimePercent + "%");
				dto.setDowntime(faultyPercent + "%");
				dto.setTat(String.format("Faulty %d%% (%d min)", faultyPercent, faultyMinutes));

				responseList.add(dto);
			}
		}

		if (responseList.isEmpty()) {
			response.setMessage("No logs found for the given date range.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {

			responseList.sort(Comparator.comparing(TATReportDTO::getBranchName, String.CASE_INSENSITIVE_ORDER));

			response.setPayload(responseList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Uptime report generated successfully.");
		}

		return response;
	}

	// @Scheduled(cron = "0 0 8 * * *")
	// public int updateDateTimeColumn() {
	// return smsDetailsRepository.updateAllDateTimeToCurrent();
	// }

	public ResponseEntity getWorkingDevicesRecords(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		try {
			int workingDeviceCount = 0, notWorkingCount = 0;

			List<DeviceMasterDTO> workingDevices = new ArrayList<>();
			List<DeviceMasterDTO> notWorkingDevices = new ArrayList<>();

			ResponseEntity listEntity = branchService.getAllChildren(branchCode);

			if (listEntity.getPayload() != null) {

				@SuppressWarnings("unchecked")
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) listEntity.getPayload();

				for (BranchMasterEntity x : branchList) {

					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(x.getBranchCode());

					if (deviceMasterEntity == null)
						continue;

					if (x.getParentCode() != null) {
						BranchMasterEntity parent = branchMasterRepository.findBybranchCode(x.getParentCode());
						if (parent != null) {
							deviceMasterEntity.setDeviceLocation(parent.getBranchDesc());
						}
					}

					SmsDetailsEntity smsDetailsEntity = smsDetailsRepository
							.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());

					if (smsDetailsEntity == null)
						continue;

					Date recordTime = smsDetailsEntity.getDateTime();
					if (recordTime != null) {
						long sixHoursMillis = 6 * 60 * 60 * 1000L;
						if (System.currentTimeMillis() - recordTime.getTime() > sixHoursMillis) {
							continue;
						}
					}

					boolean notWorkingFlag = false;

					List<DeviceZoneDetailsEntity> zoneList = zoneRepo
							.findAllByDeviceId(deviceMasterEntity.getDeviceId());

					Map<String, String> zoneFlagMap = new HashMap<>();
					zoneList.forEach(zone -> zoneFlagMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag()));

					for (String zoneKey : zoneFlagMap.keySet()) {

						String formattedZoneKey = "Zone" + zoneKey.substring(4);

						String zoneValue = (String) smsDetailsEntity.getClass().getMethod("get" + formattedZoneKey)
								.invoke(smsDetailsEntity);

						String primaryFlag = zoneFlagMap.get(zoneKey);

						if ("Y".equalsIgnoreCase(primaryFlag) && ("A".equalsIgnoreCase(zoneValue)
								|| "I".equalsIgnoreCase(zoneValue) || "D".equalsIgnoreCase(zoneValue))) {

							notWorkingFlag = true;
							break;
						}
					}

					DeviceMasterDTO dto = new DeviceMasterDTO();
					BeanUtils.copyProperties(deviceMasterEntity, dto);
					dto.setBankName(x.getBank());

					if (!notWorkingFlag) {
						workingDeviceCount++;
						workingDevices.add(dto);
					} else {
						notWorkingCount++;
						notWorkingDevices.add(dto);
					}
				}

				Map<String, Object> responseBody = new HashMap<>();
				responseBody.put("workingDeviceCount", workingDeviceCount);
				responseBody.put("notWorkingCount", notWorkingCount);
				responseBody.put("workingDevices", workingDevices);
				responseBody.put("notWorkingDevices", notWorkingDevices);

				response.setPayload(responseBody);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error while processing devices: " + e.getMessage());
		}

		return response;
	}

	public ResponseEntity getRecordsOfNotSyncedDevices(String branchCode) {

		ResponseEntity response = new ResponseEntity();
		List<Map<String, Object>> notSyncedDevices = new ArrayList<>();

		ResponseEntity listEntity = branchService.getAllChildren(branchCode);

		if (listEntity.getPayload() != null) {

			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) listEntity.getPayload();

			for (BranchMasterEntity branch : branchList) {

				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());

				if (device == null)
					continue;

				// Parent branch location
				if (branch.getParentCode() != null) {
					BranchMasterEntity parent = branchMasterRepository.findBybranchCode(branch.getParentCode());
					if (parent != null) {
						device.setDeviceLocation(parent.getBranchDesc());
					}
				}

				Instant lastSmsTime = getLastSmsTime(device.getDeviceSerialNo());

				boolean isNotSynced = false;
				String notSyncedDuration;

				Instant now = Instant.now();

				if (lastSmsTime != null) {

					System.out.println(lastSmsTime + "----->>>>" + now);

					long hoursDiff = ChronoUnit.HOURS.between(lastSmsTime, now);

					System.out.println("differnce in hours --->>>" + hoursDiff);
					isNotSynced = hoursDiff >= 12;
					notSyncedDuration = formatDuration(lastSmsTime, now);
				} else {
					isNotSynced = true;
					notSyncedDuration = "Never Synced";
				}

				if (isNotSynced) {
					System.out.println("DEVICE NAME -> " + device.getDeviceName());
					Map<String, Object> deviceInfo = new HashMap<>();
					deviceInfo.put("deviceName", device.getDeviceName());
					deviceInfo.put("deviceSerialNo", device.getDeviceSerialNo());
					deviceInfo.put("parentBranch", device.getDeviceLocation());
					deviceInfo.put("lastSmsTime", DATE_TIME_FORMATTER.format(lastSmsTime));
					deviceInfo.put("bankName", branch.getBank());
					deviceInfo.put("notSyncedHours", notSyncedDuration);
					deviceInfo.put("notSyncedSince",
							lastSmsTime != null ? DATE_TIME_FORMATTER.format(lastSmsTime) : "Never Synced");

					notSyncedDevices.add(deviceInfo);
				}
			}
		}

		notSyncedDevices.sort(Comparator
				.comparing(m -> (String) ((Map<String, Object>) m).get("parentBranch"),
						Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.thenComparing(m -> (String) ((Map<String, Object>) m).get("deviceName"),
						Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("notSyncedDeviceCount", notSyncedDevices.size());
		responseBody.put("notSyncedDevices", notSyncedDevices);

		response.setPayload(responseBody);
		return response;
	}

	private String formatDuration(Instant from, Instant to) {

		if (from == null || to == null) {
			return "Never Synced";
		}

		long totalMinutes = ChronoUnit.MINUTES.between(from, to);

		long days = totalMinutes / (24 * 60);
		long hours = (totalMinutes % (24 * 60)) / 60;
		long minutes = totalMinutes % 60;

		StringBuilder result = new StringBuilder();

		if (days > 0)
			result.append(days).append(" day").append(days > 1 ? "s " : " ");
		if (hours > 0)
			result.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
		if (minutes > 0 || result.length() == 0)
			result.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");

		return result.toString().trim();
	}

	private Instant getLastSmsTime(String serialNo) {

		// 1️⃣ Check latest sms_details
		SmsDetailsEntity latestSms = smsDetailsRepository.getLatestRecord(serialNo);

		if (latestSms != null && latestSms.getDateTime() != null) {
			return latestSms.getDateTime().toInstant();
		}

		// 2️⃣ Fallback to sms_details_history
		List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
				.findByPanelSerialNoOrderByDateTimeDesc(serialNo);

		if (historyList != null && !historyList.isEmpty() && historyList.get(0).getDateTime() != null) {
			return historyList.get(0).getDateTime().toInstant();
		}

		// 3️⃣ No record at all
		return null;
	}

	/*
	 * public ResponseEntity getDataForExecutiveSummary(String branchCode, String
	 * zoneType) { ResponseEntity response = new ResponseEntity();
	 * List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();
	 * 
	 * ResponseEntity responseEntity =
	 * branchMasterService.getAllChildren(branchCode); List<BranchMasterEntity>
	 * branchList = (responseEntity.getPayload() != null) ?
	 * (List<BranchMasterEntity>) responseEntity.getPayload() : new ArrayList<>();
	 * 
	 * branchList.forEach(branch -> { DeviceMasterEntity deviceMasterEntity =
	 * deviceMasterRepository.findBybranchCode(branch.getBranchCode());
	 * 
	 * if (deviceMasterEntity == null ||
	 * "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) { return; }
	 * 
	 * List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = deviceZoneRepo
	 * .findByZoneDesiredNameAndDeviceId(zoneType,
	 * deviceMasterEntity.getDeviceId());
	 * 
	 * if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty())
	 * { return; }
	 * 
	 * SmsDetailsEntity smsDetailsEntity =
	 * getLatestSms(deviceMasterEntity.getDeviceSerialNo());
	 * 
	 * if (smsDetailsEntity == null) { return; }
	 * 
	 * // Group zones by inputType Map<String, List<DeviceZoneDetailsEntity>>
	 * groupedZones = deviceZoneDetailsEntities.stream()
	 * .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));
	 * 
	 * groupedZones.forEach((inputType, zones) -> {
	 * 
	 * Map<String, String> groupStatusDetails =
	 * determineGroupStatusWithDetails(zones, smsDetailsEntity);
	 * 
	 * String groupStatus = groupStatusDetails.get("groupStatus");
	 * 
	 * ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
	 * 
	 * if (branch.getParentCode() != null) { BranchMasterEntity parent =
	 * branchMasterRepository.findBybranchCode(branch.getParentCode()); if (parent
	 * != null) { zoneReportDTO.setParentBranch(parent.getBranchDesc()); } }
	 * zoneReportDTO.setBranchCode(branch.getBranchCode());
	 * zoneReportDTO.setBank(branch.getBank());
	 * zoneReportDTO.setBranchName(branch.getBranchDesc());
	 * zoneReportDTO.setMobile(branch.getMobile());
	 * zoneReportDTO.setDeviceSerialNo(deviceMasterEntity.getDeviceSerialNo());
	 * zoneReportDTO.setLatestSms(smsDetailsEntity); //
	 * zoneReportDTO.setParentBranch(branchCode) // ✅ optional but useful
	 * 
	 * List<ZonesDTO> zonesDTOList = new ArrayList<>(); zones.forEach(zone -> {
	 * ZonesDTO zonesDTO = new ZonesDTO();
	 * zonesDTO.setZone(zone.getZoneOriginalName());
	 * zonesDTO.setZoneType(zone.getZoneDesiredName());
	 * zonesDTO.setAlertType(zone.getAlertType() + "," +
	 * zone.getCriticalAlertFlag());
	 * zonesDTO.setStatus(groupStatusDetails.get(zone.getZoneOriginalName()));
	 * zonesDTOList.add(zonesDTO); });
	 * 
	 * zoneReportDTO.setZoneDTOList(zonesDTOList);
	 * zoneReportDTOs.add(zoneReportDTO); }); });
	 * 
	 * zoneReportDTOs.sort(Comparator .comparing(ZoneReportDTO::getParentBranch,
	 * Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
	 * .thenComparing(ZoneReportDTO::getBranchName,
	 * Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
	 * 
	 * response.setPayload(zoneReportDTOs);
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setMessage("Data fetched successfully"); return response; }
	 */

	public ResponseEntity getDataForExecutiveSummary(String branchCode, String zoneType, int page, int size,
			String branchName, boolean export) {

		ResponseEntity response = new ResponseEntity();
		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();

		// Step 1: Get all child branches
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);

		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: new ArrayList<>();

		// Optional branch name search
		// Matches from the START of branch name only
		if (branchName != null && !branchName.trim().isEmpty()) {

			String searchTerm = branchName.trim().toLowerCase();

			branchList = branchList.stream()
					.filter(b -> b.getBranchDesc() != null && b.getBranchDesc().toLowerCase().startsWith(searchTerm))
					.collect(Collectors.toList());
		}

		if (branchList.isEmpty()) {

			response.setPayload(Collections.emptyList());
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully");
			response.setTotalElements(0);
			response.setTotalPages(0);
			response.setCurrentPage(page);

			return response;
		}

		// Step 2: Collect branch codes
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// ============================================================
		// Step 2.1: Collect bank codes
		// ============================================================

		List<String> bankCodes = branchList.stream().map(BranchMasterEntity::getBank).filter(Objects::nonNull)
				.map(String::trim).filter(code -> !code.isEmpty()).distinct().collect(Collectors.toList());

		// ============================================================
		// Step 2.2: Fetch bank master data in ONE query
		// ============================================================

		List<BankMasterEntity> bankList = bankCodes.isEmpty() ? new ArrayList<>()
				: bankMasterRepository.findByBankCodeIn(bankCodes);

		// ============================================================
		// Step 2.3: Create bankCode -> bankName map
		// ============================================================

		Map<String, String> bankNameMap = bankList.stream().collect(Collectors.toMap(BankMasterEntity::getBankCode,
				BankMasterEntity::getBankName, (existing, replacement) -> existing));

		// Step 3: Batch fetch ONLY ACTIVE devices for all branches
		List<DeviceMasterEntity> activeDevices = deviceMasterRepository.findActiveByBranchCodes(branchCodes);

		// Map branchCode -> device
		Map<String, DeviceMasterEntity> deviceByBranch = activeDevices.stream().collect(Collectors
				.toMap(DeviceMasterEntity::getBranchCode, Function.identity(), (existing, replacement) -> existing));

		// Step 4: Batch fetch zones for all active devices
		List<String> deviceIds = activeDevices.stream().map(DeviceMasterEntity::getDeviceId).filter(Objects::nonNull)
				.collect(Collectors.toList());

		List<DeviceZoneDetailsEntity> allZones = deviceIds.isEmpty() ? new ArrayList<>()
				: deviceZoneRepo.findByZoneDesiredNameAndDeviceIdIn(zoneType, deviceIds);

		// Group zones by deviceId
		Map<String, List<DeviceZoneDetailsEntity>> zonesByDevice = allZones.stream()
				.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getDeviceId));

		// Step 5: Batch fetch latest SMS for all active devices
		List<String> serialNumbers = activeDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
				.filter(Objects::nonNull).collect(Collectors.toList());

		Map<String, SmsDetailsEntity> latestSmsMap = fetchLatestSmsBatch(serialNumbers);

		// Step 6: Batch fetch parent branches
		Set<String> parentCodes = branchList.stream().map(BranchMasterEntity::getParentCode).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Map<String, BranchMasterEntity> parentBranchMap = new HashMap<>();

		if (!parentCodes.isEmpty()) {

			List<BranchMasterEntity> parents = branchMasterRepository.findByBranchCodeIn(new ArrayList<>(parentCodes));

			parentBranchMap = parents.stream().collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
					Function.identity(), (existing, replacement) -> existing));
		}

		// Step 7: Build ZoneReportDTO list
		for (BranchMasterEntity branch : branchList) {

			DeviceMasterEntity device = deviceByBranch.get(branch.getBranchCode());

			if (device == null) {
				continue;
			}

			List<DeviceZoneDetailsEntity> zones = zonesByDevice.get(device.getDeviceId());

			if (zones == null || zones.isEmpty()) {
				continue;
			}

			SmsDetailsEntity sms = latestSmsMap.get(device.getDeviceSerialNo());

			if (sms == null) {
				continue;
			}

			// Group zones by inputType
			Map<String, List<DeviceZoneDetailsEntity>> groupedZones = zones.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : groupedZones.entrySet()) {

				List<DeviceZoneDetailsEntity> zoneGroup = entry.getValue();

				// Existing logic
				Map<String, String> groupStatusDetails = determineGroupStatusWithDetails(zoneGroup, sms);

				ZoneReportDTO zoneReportDTO = new ZoneReportDTO();

				// Parent branch
				if (branch.getParentCode() != null) {

					BranchMasterEntity parent = parentBranchMap.get(branch.getParentCode());

					if (parent != null) {
						zoneReportDTO.setParentBranch(parent.getBranchDesc());
					}
				}

				zoneReportDTO.setBranchCode(branch.getBranchCode());

				// ========================================================
				// BANK CODE -> BANK NAME
				// ========================================================

				String bankCode = branch.getBank();

				String bankName = bankNameMap.get(bankCode);

				// If bank name is available, use it.
				// Otherwise return original bank code.
				zoneReportDTO.setBank(bankName != null ? bankName : bankCode);

				zoneReportDTO.setBranchName(branch.getBranchDesc());
				zoneReportDTO.setMobile(branch.getMobile());
				zoneReportDTO.setDeviceSerialNo(device.getDeviceSerialNo());
				zoneReportDTO.setLatestSms(sms);

				List<ZonesDTO> zonesDTOList = new ArrayList<>();

				for (DeviceZoneDetailsEntity zone : zoneGroup) {

					ZonesDTO zonesDTO = new ZonesDTO();

					zonesDTO.setZone(zone.getZoneOriginalName());
					zonesDTO.setZoneType(zone.getZoneDesiredName());

					zonesDTO.setAlertType(zone.getAlertType() + "," + zone.getCriticalAlertFlag());

					zonesDTO.setStatus(groupStatusDetails.get(zone.getZoneOriginalName()));

					zonesDTOList.add(zonesDTO);
				}

				zoneReportDTO.setZoneDTOList(zonesDTOList);

				zoneReportDTOs.add(zoneReportDTO);
			}
		}

		// Step 8: Sort
		zoneReportDTOs.sort(Comparator
				.comparing(ZoneReportDTO::getParentBranch, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.thenComparing(ZoneReportDTO::getBranchName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

		// Step 9: Apply pagination — skip if export = true
		int totalElements = zoneReportDTOs.size();

		List<ZoneReportDTO> paginatedList;

		int totalPages;

		if (export) {

			// Excel/PDF export ke liye poora data bhejo
			paginatedList = zoneReportDTOs;
			totalPages = 1;

		} else {

			totalPages = (int) Math.ceil((double) totalElements / size);

			int fromIndex = page * size;

			int toIndex = Math.min(fromIndex + size, totalElements);

			if (fromIndex < totalElements) {

				paginatedList = zoneReportDTOs.subList(fromIndex, toIndex);

			} else {

				paginatedList = new ArrayList<>();
			}
		}

		// Step 10: Set response
		response.setPayload(paginatedList);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully");
		response.setTotalElements(totalElements);
		response.setTotalPages(totalPages);
		response.setCurrentPage(page);

		return response;
	}

	private Map<String, SmsDetailsEntity> fetchLatestSmsBatch(List<String> serialNumbers) {
		// 1. Try sms_details table
		List<SmsDetailsEntity> smsList = smsDetailsRepository.findLatestByPanelSerialNoIn(serialNumbers);
		Map<String, SmsDetailsEntity> result = smsList.stream()
				.collect(Collectors.toMap(SmsDetailsEntity::getPanelSerialNo, Function.identity(),
						(existing, replacement) -> existing.getId() > replacement.getId() ? existing : replacement));

		// 2. Fallback to sms_details_history for missing serials
		Set<String> foundSerials = result.keySet();
		List<String> missingSerials = serialNumbers.stream().filter(s -> !foundSerials.contains(s))
				.collect(Collectors.toList());

		if (!missingSerials.isEmpty()) {
			List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
					.findLatestByPanelSerialNoIn(missingSerials);

			// Resolve duplicates in history as well (keep highest ID)
			Map<String, SmsDetailsHistoryEntity> historyMap = historyList.stream().collect(Collectors.toMap(
					SmsDetailsHistoryEntity::getPanelSerialNo, Function.identity(),
					(existing, replacement) -> existing.getId() > replacement.getId() ? existing : replacement));

			for (SmsDetailsHistoryEntity history : historyMap.values()) {
				SmsDetailsEntity fallback = new SmsDetailsEntity();
				BeanUtils.copyProperties(history, fallback);
				result.put(history.getPanelSerialNo(), fallback);
			}
		}
		return result;
	}

	// public ResponseEntity getUptimeTatReport(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	//
	// // 1. Fetch all child branches
	// ResponseEntity branchResponseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// if (branchResponseEntity.getPayload() == null) {
	// System.out.println("ERROR: No branches found for branchCode: " + branchCode);
	// response.setMessage("No branches found for branch code: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseEntity.getPayload();
	// System.out.println("Total branches fetched: " + branchList.size());
	//
	// List<String> branchCodes = branchList.stream()
	// .map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	// System.out.println("Branch codes: " + branchCodes);
	//
	// Map<String, BranchMasterEntity> branchMap = branchList.stream()
	// .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	// Function.identity()));
	//
	// // 2. Fetch devices by branch codes
	// List<DeviceMasterEntity> deviceList =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	// if (deviceList == null || deviceList.isEmpty()) {
	// System.out.println("ERROR: No devices found for branchCode: " + branchCode);
	// response.setMessage("No devices found for branch: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	// System.out.println("Total devices fetched: " + deviceList.size());
	//
	// // 3. Extract device IDs from deviceList
	// List<String> deviceIds = deviceList.stream()
	// .map(DeviceMasterEntity::getDeviceId)
	// .collect(Collectors.toList());
	// System.out.println("Device IDs: " + deviceIds);
	//
	// // 4. Fetch device_zone_details for all device IDs in one query
	// List<DeviceZoneDetailsEntity> deviceZoneDetailsList =
	// zoneRepo.findByDeviceIdIn(deviceIds);
	// System.out.println("Total device zone details fetched: " +
	// deviceZoneDetailsList.size());
	//
	// // 5. Group zone details by device_id for easy lookup
	// Map<String, List<DeviceZoneDetailsEntity>> zoneDetailsByDeviceId =
	// deviceZoneDetailsList.stream()
	// .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getDeviceId));
	//
	// // 6. Print zone details per device
	// deviceList.forEach(device -> {
	// String deviceId = device.getDeviceId();
	// List<DeviceZoneDetailsEntity> zoneDetails =
	// zoneDetailsByDeviceId.getOrDefault(deviceId, Collections.emptyList());
	//
	// System.out.println("--------------------------------------------------");
	// System.out.println("Device ID : " + deviceId);
	// System.out.println("Branch Code : " + device.getBranchCode());
	// System.out.println("Zone Details Count: " + zoneDetails.size());
	//
	// zoneDetails.forEach(zone -> {
	// System.out.println(" >> Zone Detail: " + zone); // ya specific fields print
	// karo
	// // Example: System.out.println(" Zone: " + zone.getZoneName() + " | Status: "
	// + zone.getStatus());
	// });
	// });
	//
	// return null;
	// }

	/*
	 * public ResponseEntity getUptimeTatReport(String branchCode, String fromDate,
	 * String toDate) { ResponseEntity response = new ResponseEntity();
	 * 
	 * // 1. Fetch all child branches ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode); if
	 * (branchResponseEntity.getPayload() == null) {
	 * response.setMessage("No branches found for branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	 * branchResponseEntity.getPayload();
	 * 
	 * List<String> branchCodes = branchList.stream()
	 * .map(BranchMasterEntity::getBranchCode) .collect(Collectors.toList());
	 * 
	 * Map<String, BranchMasterEntity> branchMap = branchList.stream()
	 * .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	 * Function.identity()));
	 * 
	 * // 2. Fetch devices by branch codes List<DeviceMasterEntity> deviceList =
	 * deviceMasterRepository.findByBranchCodeIn(branchCodes); if (deviceList ==
	 * null || deviceList.isEmpty()) {
	 * response.setMessage("No devices found for branch: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * // 3. Extract device IDs List<String> deviceIds = deviceList.stream()
	 * .map(DeviceMasterEntity::getDeviceId) .collect(Collectors.toList());
	 * 
	 * // 4. Fetch device zone details List<DeviceZoneDetailsEntity>
	 * deviceZoneDetailsList = zoneRepo.findByDeviceIdIn(deviceIds);
	 * 
	 * // 5. Group zone details by deviceId Map<String,
	 * List<DeviceZoneDetailsEntity>> zoneDetailsByDeviceId =
	 * deviceZoneDetailsList.stream()
	 * .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getDeviceId));
	 * 
	 * // ---------------------------------------------------------------- // 6.
	 * Calculate Total Time in minutes from date range // fromDate 00:00:00 -->
	 * toDate 23:59:59 //
	 * ---------------------------------------------------------------- long
	 * totalTimeMinutes = 0; if (fromDate != null && toDate != null &&
	 * !fromDate.trim().isEmpty() && !toDate.trim().isEmpty()) {
	 * 
	 * LocalDateTime startDT = LocalDate .parse(fromDate.trim(),
	 * DateTimeFormatter.ofPattern("yyyy-MM-dd")) .atStartOfDay(); // 00:00:00
	 * 
	 * // // (wrong - 1 second kam) // LocalDateTime endDT = LocalDate //
	 * .parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) //
	 * .atTime(23, 59, 59); // 2879 minutes aata hai
	 * 
	 * // AFTER (correct - 2880 minutes) LocalDateTime endDT = LocalDate
	 * .parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) .plusDays(1)
	 * .atStartOfDay(); // next day 00:00:00 tak count karo // 23:59:59
	 * 
	 * totalTimeMinutes = ChronoUnit.MINUTES.between(startDT, endDT); // e.g. 2880
	 * for 2 days }
	 * 
	 * // ---------------------------------------------------------------- // 7.
	 * Call getResponseTimeReport to get all alert records // (null for
	 * systemIntegrator/product/status — we need raw data) //
	 * ----------------------------------------------------------------
	 * ResponseEntity rtrResponse =deviceMasterService.getResponseTimeReport(
	 * branchCode, null, null, null, fromDate, toDate);
	 * 
	 * // Build a map: panelSerialNo + "|" + alertType --> sum of timeDifference
	 * minutes // key = deviceSerialNo + "|" + alertType (both lowercase for safe
	 * matching) Map<String, Long> downtimeMap = new HashMap<>();
	 * 
	 * if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	 * Map<String, Object> rtrPayload = (Map<String, Object>)
	 * rtrResponse.getPayload(); List<InputStatusDetailsEntity> inputStatusDetails =
	 * (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	 * 
	 * if (inputStatusDetails != null) { for (InputStatusDetailsEntity entity :
	 * inputStatusDetails) { String serialNo = entity.getDeviceSerialNo(); String
	 * alertType = entity.getAlertType(); // e.g. "CCTV VIDEO LOSS" String timeDiff
	 * = entity.getTimeDifference(); // e.g. "8 minutes" or "0 minutes"
	 * 
	 * if (serialNo == null || alertType == null || timeDiff == null) continue;
	 * 
	 * // Parse "8 minutes" -> 8L long minutes = 0; try { String cleaned =
	 * timeDiff.trim().toLowerCase().replace("minutes", "").replace("minute",
	 * "").trim(); minutes = Long.parseLong(cleaned); } catch (NumberFormatException
	 * ignored) {}
	 * 
	 * String mapKey = serialNo.trim() + "|" + alertType.trim().toLowerCase();
	 * downtimeMap.merge(mapKey, minutes, Long::sum); } } }
	 * 
	 * // ---------------------------------------------------------------- // 8.
	 * Build final uptime TAT rows // One row per: branch + vendor + alertType //
	 * ---------------------------------------------------------------- // We need
	 * deviceSerialNo for key lookup, so map device -> branch Map<String,
	 * DeviceMasterEntity> deviceByBranchCode = deviceList.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getBranchCode,
	 * Function.identity(), (a, b) -> a)); // keep first if duplicate
	 * 
	 * // Collect unique (branchCode, vendor, alertType) combinations from zone
	 * details // Zone entity has: deviceId, zoneOriginalName (alertType), vendors
	 * // We need to link deviceId -> branchCode -> branchName
	 * 
	 * Map<String, String> deviceIdToBranchCode = deviceList.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
	 * DeviceMasterEntity::getBranchCode, (a, b) -> a));
	 * 
	 * Map<String, String> deviceIdToSerialNo = deviceList.stream()
	 * .collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
	 * DeviceMasterEntity::getDeviceSerialNo, (a, b) -> a));
	 * 
	 * // Collect unique alert types per (branchCode, vendor) from response time
	 * report // Use a LinkedHashMap to preserve insertion order // Key:
	 * branchCode|vendor|alertType Map<String, UptimeTatEntity> resultMap = new
	 * LinkedHashMap<>();
	 * 
	 * if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	 * Map<String, Object> rtrPayload = (Map<String, Object>)
	 * rtrResponse.getPayload(); List<InputStatusDetailsEntity> inputStatusDetails =
	 * (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	 * 
	 * if (inputStatusDetails != null) { for (InputStatusDetailsEntity entity :
	 * inputStatusDetails) { String serialNo = entity.getDeviceSerialNo(); String
	 * alertType = entity.getAlertType(); String vendorName =
	 * entity.getVendorName(); String branchName = entity.getBranchName();
	 * 
	 * if (serialNo == null || alertType == null) continue;
	 * 
	 * // Shorten vendor to a display label if needed (optional) String rowKey =
	 * branchName + "|" + vendorName + "|" + alertType;
	 * 
	 * if (!resultMap.containsKey(rowKey)) { // Downtime for this serial + alertType
	 * combo String downtimeKey = serialNo.trim() + "|" +
	 * alertType.trim().toLowerCase(); long downtime =
	 * downtimeMap.getOrDefault(downtimeKey, 0L); long uptime = Math.max(0,
	 * totalTimeMinutes - downtime);
	 * 
	 * UptimeTatEntity row = new UptimeTatEntity(); row.setBranchName(branchName);
	 * row.setVendorName(vendorName); row.setAlertType(alertType);
	 * row.setTotalTimeMinutes(totalTimeMinutes); row.setDowntimeMinutes(downtime);
	 * row.setNoInfoAvailableMinutes(0L); // future use
	 * row.setUptimeMinutes(uptime);
	 * 
	 * resultMap.put(rowKey, row); } // If key already exists, downtime is already
	 * summed in downtimeMap — no re-add needed } } }
	 * 
	 * List<UptimeTatEntity> uptimeList = new ArrayList<>(resultMap.values());
	 * 
	 * response.setPayload(uptimeList);
	 * response.setMessage("Uptime TAT report fetched successfully");
	 * response.setStatusCode(HttpStatus.OK.value()); return response; }
	 */

	// public ResponseEntity getUptimeTatReport(String branchCode, String fromDate,
	// String toDate) {
	// ResponseEntity response = new ResponseEntity();
	//
	// // 1. Fetch all child branches
	// ResponseEntity branchResponseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// if (branchResponseEntity.getPayload() == null) {
	// response.setMessage("No branches found for branch code: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseEntity.getPayload();
	//
	// List<String> branchCodes = branchList.stream()
	// .map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	//
	// Map<String, BranchMasterEntity> branchMap = branchList.stream()
	// .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	// Function.identity()));
	//
	// // 2. Fetch devices
	// List<DeviceMasterEntity> deviceList =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	// if (deviceList == null || deviceList.isEmpty()) {
	// response.setMessage("No devices found for branch: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// // 3. Maps for quick lookup
	// Map<String, DeviceMasterEntity> deviceByDeviceId = deviceList.stream()
	// .collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
	// Function.identity(), (a, b) -> a));
	//
	// Map<String, String> deviceIdToSerialNo = deviceList.stream()
	// .collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
	// DeviceMasterEntity::getDeviceSerialNo, (a, b) -> a));
	//
	// // 4. Fetch all zone details for all devices
	// List<String> deviceIds = deviceList.stream()
	// .map(DeviceMasterEntity::getDeviceId)
	// .collect(Collectors.toList());
	//
	// List<DeviceZoneDetailsEntity> deviceZoneDetailsList =
	// zoneRepo.findByDeviceIdIn(deviceIds);
	//
	// // ----------------------------------------------------------------
	// // 5. Calculate Total Time in minutes + noInfoMap
	// // ----------------------------------------------------------------
	// long totalTimeMinutes = 0;
	// Map<String, Long> noInfoMap = new HashMap<>(); // initialized empty,
	// populated below if dates present
	//
	// if (fromDate != null && toDate != null
	// && !fromDate.trim().isEmpty() && !toDate.trim().isEmpty()) {
	//
	// LocalDateTime startDT = LocalDate
	// .parse(fromDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	// .atStartOfDay();
	// System.out.println("StartDate ::"+startDT);
	//// LocalDateTime endDT = LocalDate
	//// .parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	//// .plusDays(1)
	//// .atStartOfDay();
	// LocalDateTime endDT = LocalDate
	// .parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	// .atStartOfDay();
	// System.out.println("EndDate ::"+endDT);
	// totalTimeMinutes = ChronoUnit.MINUTES.between(startDT, endDT);
	//
	// // Step 5 ke baad: noInfoMap calculate karo
	// List<String> allSerialNos = deviceList.stream()
	// .map(DeviceMasterEntity::getDeviceSerialNo)
	// .collect(Collectors.toList());
	//
	// noInfoMap = calculateNoInfoMinutes(allSerialNos, startDT, endDT);
	// }
	//
	// // ----------------------------------------------------------------
	// // 6. Call getResponseTimeReport → build downtimeMap
	// // Key: deviceSerialNo + "|" + alertType (lowercase)
	// // Value: sum of all timeDifference minutes
	// // ----------------------------------------------------------------
	// Map<String, Long> downtimeMap = new HashMap<>();
	//
	// ResponseEntity rtrResponse = deviceMasterService.getResponseTimeReport(
	// branchCode, null, null, null, fromDate, toDate);
	//
	// if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	// Map<String, Object> rtrPayload = (Map<String, Object>)
	// rtrResponse.getPayload();
	// List<InputStatusDetailsEntity> inputStatusDetails =
	// (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	//
	// if (inputStatusDetails != null) {
	// for (InputStatusDetailsEntity entity : inputStatusDetails) {
	// String serialNo = entity.getDeviceSerialNo();
	// String alertType = entity.getAlertType();
	// String timeDiff = entity.getTimeDifference();
	//
	// if (serialNo == null || alertType == null || timeDiff == null) continue;
	//
	// long minutes = 0;
	// try {
	// String cleaned = timeDiff.trim().toLowerCase()
	// .replace("minutes", "").replace("minute", "").trim();
	// minutes = Long.parseLong(cleaned);
	// } catch (NumberFormatException ignored) {}
	//
	// String mapKey = serialNo.trim() + "|" + alertType.trim().toLowerCase();
	// downtimeMap.merge(mapKey, minutes, Long::sum);
	// }
	// }
	// }
	//
	// // ----------------------------------------------------------------
	// // 7. Build vendor name lookup from inputStatusDetails
	// // Key: deviceSerialNo → vendorName
	// // ----------------------------------------------------------------
	// Map<String, String> serialToVendorMap = new HashMap<>();
	// if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	// Map<String, Object> rtrPayload = (Map<String, Object>)
	// rtrResponse.getPayload();
	// List<InputStatusDetailsEntity> inputStatusDetails =
	// (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	// if (inputStatusDetails != null) {
	// for (InputStatusDetailsEntity entity : inputStatusDetails) {
	// if (entity.getDeviceSerialNo() != null && entity.getVendorName() != null) {
	// serialToVendorMap.putIfAbsent(entity.getDeviceSerialNo(),
	// entity.getVendorName());
	// }
	// }
	// }
	// }
	//
	// // ----------------------------------------------------------------
	// // 8. Iterate over ALL zone details → one row per device+zone
	// // Downtime = 0 if no alert came for that zone
	// // ----------------------------------------------------------------
	// List<UptimeTatEntity> uptimeList = new ArrayList<>();
	//
	// // Deduplicate: same device+zone should appear only once
	// Set<String> processedKeys = new HashSet<>();
	//
	// for (DeviceZoneDetailsEntity zone : deviceZoneDetailsList) {
	// String deviceId = zone.getDeviceId();
	// String zoneAlertType = zone.getAlertType(); // e.g. "CCTV VIDEO LOSS"
	// String zoneNumber = zone.getZoneOriginalName();
	//
	// if (deviceId == null || zoneAlertType == null) continue;
	//
	// DeviceMasterEntity device = deviceByDeviceId.get(deviceId);
	// if (device == null) continue;
	//
	// BranchMasterEntity branch = branchMap.get(device.getBranchCode());
	// if (branch == null) continue;
	//
	// String serialNo = device.getDeviceSerialNo();
	// String rowKey = serialNo + "|" + zoneAlertType.trim().toLowerCase();
	//
	// // Skip duplicate zone entries for same device+alertType
	// if (!processedKeys.add(rowKey)) continue;
	//
	// // Vendor: prefer from zone entity, fallback to response time report map
	// String vendorName = zone.getVendors();
	// if (vendorName == null || vendorName.trim().isEmpty()) {
	// vendorName = serialToVendorMap.getOrDefault(serialNo, "N/A");
	// }
	//
	// long downtime = downtimeMap.getOrDefault(rowKey, 0L);
	// long noInfo = noInfoMap.getOrDefault(serialNo, 0L);
	// long uptime = Math.max(0, totalTimeMinutes - downtime - noInfo); // FIX:
	// noInfo subtracted
	//
	// UptimeTatEntity row = new UptimeTatEntity();
	// row.setBranchName(branch.getBranchDesc());
	// row.setVendorName(vendorName);
	// row.setAlertType(zoneAlertType);
	// row.setZone(zoneNumber);
	// row.setTotalTimeMinutes(totalTimeMinutes);
	// row.setDowntimeMinutes(downtime);
	// row.setNoInfoAvailableMinutes(noInfo); // FIX: was hardcoded 0L
	// row.setUptimeMinutes(uptime);
	//
	// uptimeList.add(row);
	// }
	//
	// // Optional: sort by branchName then alertType
	// uptimeList.sort(Comparator
	// .comparing(UptimeTatEntity::getBranchName)
	// .thenComparing(UptimeTatEntity::getAlertType));
	//
	// response.setPayload(uptimeList);
	// response.setMessage("Uptime TAT report fetched successfully");
	// response.setStatusCode(HttpStatus.OK.value());
	// return response;
	// }
	//
	//
	/**
	 * Sabhi panelSerialNo ke liye noInfoAvailableMinutes calculate karo Key:
	 * panelSerialNo → Value: total non-sync minutes in filter range
	 */
	private Map<String, Long> calculateNoInfoMinutes(List<String> deviceSerialNos, LocalDateTime filterStart,
			LocalDateTime filterEnd) {

		Map<String, Long> noInfoMap = new HashMap<>();

		// Sirf wahi records jo filter range ko overlap karte hain
		List<NonSyncLogEntity> nonSyncLogs = nonSyncLogRepository.findByPanelSerialNoInAndDateRange(deviceSerialNos,
				filterStart, filterEnd);

		// Group by panelSerialNo
		Map<String, List<NonSyncLogEntity>> groupedByPanel = nonSyncLogs.stream()
				.collect(Collectors.groupingBy(NonSyncLogEntity::getPanelSerialNo));

		for (Map.Entry<String, List<NonSyncLogEntity>> entry : groupedByPanel.entrySet()) {
			String serialNo = entry.getKey();
			List<NonSyncLogEntity> records = entry.getValue();

			long totalNoInfoMinutes = 0;

			for (NonSyncLogEntity log : records) {
				// Effective start: filter range ke andar clamp karo
				LocalDateTime effectiveStart = log.getNonSyncStartDatetime();
				if (effectiveStart.isBefore(filterStart)) {
					effectiveStart = filterStart;
				}

				// Effective end: NULL hai to filterEnd use karo, warna clamp karo
				LocalDateTime effectiveEnd;
				if (log.getNonSyncEndDatetime() == null) {
					effectiveEnd = filterEnd; // NULL = abhi bhi non-sync
				} else {
					effectiveEnd = log.getNonSyncEndDatetime();
					if (effectiveEnd.isAfter(filterEnd)) {
						effectiveEnd = filterEnd;
					}
				}

				// Safety check: agar start >= end to skip (invalid record)
				if (!effectiveStart.isBefore(effectiveEnd))
					continue;

				long minutes = ChronoUnit.MINUTES.between(effectiveStart, effectiveEnd);
				totalNoInfoMinutes += minutes;
			}

			noInfoMap.put(serialNo, totalNoInfoMinutes);
		}

		return noInfoMap;
	}

	public ResponseEntity getUptimeTatReport(String branchCode, String fromDate, String toDate, String filterBranchName,
			String filterVendorName, String filterZone, String filterAlertType, String filterZoneDesiredName, int page,
			int size) {
		ResponseEntity response = new ResponseEntity();

		// 1. Fetch all child branches
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		Map<String, BranchMasterEntity> branchMap = branchList.stream()
				.collect(Collectors.toMap(BranchMasterEntity::getBranchCode, Function.identity()));

		// 2. Fetch devices
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// 3. Maps for quick lookup
		Map<String, DeviceMasterEntity> deviceByDeviceId = deviceList.stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getDeviceId, Function.identity(), (a, b) -> a));

		Map<String, String> deviceIdToTypeMap = deviceList.stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
						device -> device.getDeviceType() != null ? device.getDeviceType() : "N/A", (a, b) -> a));

		// 4. Fetch all zone details
		List<String> deviceIds = deviceList.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());
		List<DeviceZoneDetailsEntity> deviceZoneDetailsList = zoneRepo.findByDeviceIdIn(deviceIds);

		// 5. Calculate Total Time in minutes
		long totalTimeMinutes = 0;
		Map<String, Long> noInfoMap = new HashMap<>();
		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;

		if (fromDate != null && toDate != null && !fromDate.trim().isEmpty() && !toDate.trim().isEmpty()) {
			startDateTime = LocalDate.parse(fromDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
			endDateTime = LocalDate.parse(toDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59, 59);

			totalTimeMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime) + 1;

			System.out.println("Total Time Range: " + startDateTime + " to " + endDateTime);
			System.out.println("Total Minutes: " + totalTimeMinutes);

			List<String> allSerialNos = deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.collect(Collectors.toList());
			noInfoMap = calculateNoInfoMinutes(allSerialNos, startDateTime, endDateTime);
		}

		// 6. Call getResponseTimeReport → build downtimeMap (FIXED WITH START DATE
		// HANDLING)
		Map<String, Long> downtimeMap = new HashMap<>();
		Map<String, Long> resolvedMinutesMap = new HashMap<>();

		// Track ongoing alerts per device+alertType
		Map<String, LocalDateTime> earliestOngoingAlertMap = new HashMap<>();
		Map<String, LocalDateTime> latestOngoingAlertMap = new HashMap<>();

		ResponseEntity rtrResponse = deviceMasterService.getResponseTimeReport(branchCode, null, null, null, fromDate,
				toDate);

		if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
			Map<String, Object> rtrPayload = (Map<String, Object>) rtrResponse.getPayload();
			List<InputStatusDetailsEntity> inputStatusDetails = (List<InputStatusDetailsEntity>) rtrPayload
					.get("inputStatusDetails");

			if (inputStatusDetails != null) {
				System.out.println("Total inputStatusDetails count: " + inputStatusDetails.size());

				for (InputStatusDetailsEntity entity : inputStatusDetails) {
					String serialNo = entity.getDeviceSerialNo();
					String alertType = entity.getAlertType();
					String timeDiff = entity.getTimeDifference();
					String alarmTime = entity.getAlarmTime();

					if (serialNo == null || alertType == null)
						continue;

					String mapKey = serialNo.trim() + "|" + alertType.trim().toLowerCase();

					// Handle Ongoing alerts
					if ("Ongoing".equalsIgnoreCase(timeDiff)) {
						if (alarmTime != null && !alarmTime.trim().isEmpty()) {
							LocalDateTime alarmDateTime = parseAlarmTime(alarmTime);
							if (alarmDateTime != null) {
								// Track earliest and latest ongoing alert
								LocalDateTime existingEarliest = earliestOngoingAlertMap.get(mapKey);
								LocalDateTime existingLatest = latestOngoingAlertMap.get(mapKey);

								if (existingEarliest == null || alarmDateTime.isBefore(existingEarliest)) {
									earliestOngoingAlertMap.put(mapKey, alarmDateTime);
								}
								if (existingLatest == null || alarmDateTime.isAfter(existingLatest)) {
									latestOngoingAlertMap.put(mapKey, alarmDateTime);
								}

								System.out.println("Ongoing alert for " + mapKey + " at: " + alarmDateTime);
							}
						}
					} else {
						// Handle resolved alerts - sum all resolved downtimes
						long minutes = parseTimeDifferenceToMinutes(timeDiff);
						if (minutes > 0) {
							resolvedMinutesMap.merge(mapKey, minutes, Long::sum);
							System.out.println("Resolved alert for " + mapKey + ": " + minutes + " minutes");
						}
					}
				}

				// Calculate ongoing downtime
				for (String mapKey : earliestOngoingAlertMap.keySet()) {
					LocalDateTime earliestAlarm = earliestOngoingAlertMap.get(mapKey);

					if (earliestAlarm != null && endDateTime != null) {
						long ongoingMinutes = 0;

						// IMPORTANT FIX: If alert started before report start date, use report start
						// date
						LocalDateTime effectiveStartDate = earliestAlarm;
						if (startDateTime != null && earliestAlarm.isBefore(startDateTime)) {
							effectiveStartDate = startDateTime;
							System.out.println("Alert for " + mapKey + " started before report range (" + earliestAlarm
									+ "). Using start date: " + startDateTime);
						}

						// Calculate from effective start date to end date
						ongoingMinutes = ChronoUnit.MINUTES.between(effectiveStartDate, endDateTime);

						// Add resolved minutes if any
						long totalMinutes = resolvedMinutesMap.getOrDefault(mapKey, 0L) + ongoingMinutes;
						downtimeMap.put(mapKey, totalMinutes);

						System.out.println("ONGOING CALCULATION for " + mapKey + ":");
						System.out.println("  Earliest Alarm: " + earliestAlarm);
						System.out.println("  Effective Start Date: " + effectiveStartDate);
						System.out.println("  End DateTime: " + endDateTime);
						System.out.println("  Ongoing Minutes: " + ongoingMinutes);
						System.out.println("  Resolved Minutes: " + resolvedMinutesMap.getOrDefault(mapKey, 0L));
						System.out.println("  TOTAL DOWNTIME: " + totalMinutes);
					}
				}
			}
		}

		// 7. Build vendor name lookup
		Map<String, String> serialToVendorMap = new HashMap<>();
		if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
			Map<String, Object> rtrPayload = (Map<String, Object>) rtrResponse.getPayload();
			List<InputStatusDetailsEntity> inputStatusDetails = (List<InputStatusDetailsEntity>) rtrPayload
					.get("inputStatusDetails");
			if (inputStatusDetails != null) {
				for (InputStatusDetailsEntity entity : inputStatusDetails) {
					if (entity.getDeviceSerialNo() != null && entity.getVendorName() != null) {
						serialToVendorMap.putIfAbsent(entity.getDeviceSerialNo(), entity.getVendorName());
					}
				}
			}
		}

		// 8. Filter values
		String fBranch = (filterBranchName != null && !filterBranchName.trim().isEmpty())
				? filterBranchName.trim().toLowerCase()
				: null;
		String fVendor = (filterVendorName != null && !filterVendorName.trim().isEmpty())
				? filterVendorName.trim().toLowerCase()
				: null;
		String fZone = (filterZone != null && !filterZone.trim().isEmpty()) ? filterZone.trim().toLowerCase() : null;
		String fAlertType = (filterAlertType != null && !filterAlertType.trim().isEmpty())
				? filterAlertType.trim().toLowerCase()
				: null;
		String fZoneDesiredName = (filterZoneDesiredName != null && !filterZoneDesiredName.trim().isEmpty())
				? filterZoneDesiredName.trim().toLowerCase()
				: null;

		// 9. Group mode
		boolean groupByBranchAndAlertType = (fVendor != null && fZone == null);

		// 10. Build rows
		List<UptimeTatEntity> uptimeList = new ArrayList<>();
		Set<String> processedKeys = new HashSet<>();

		if (!groupByBranchAndAlertType) {
			// Original behavior: one row per device+zone
			for (DeviceZoneDetailsEntity zone : deviceZoneDetailsList) {
				String deviceId = zone.getDeviceId();
				String zoneAlertType = zone.getAlertType();
				String zoneNumber = zone.getZoneOriginalName();
				String zoneDesiredName = zone.getZoneDesiredName();

				if (deviceId == null || zoneAlertType == null)
					continue;

				DeviceMasterEntity device = deviceByDeviceId.get(deviceId);
				if (device == null)
					continue;

				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				if (branch == null)
					continue;

				String serialNo = device.getDeviceSerialNo();
				String rowKey = serialNo + "|" + zoneAlertType.trim().toLowerCase();

				if (!processedKeys.add(rowKey))
					continue;

				String vendorName = zone.getVendors();
				if (vendorName == null || vendorName.trim().isEmpty()) {
					vendorName = serialToVendorMap.getOrDefault(serialNo, "N/A");
				}

				// Apply filters
				if (fBranch != null && !branch.getBranchDesc().trim().toLowerCase().contains(fBranch))
					continue;
				if (fVendor != null && !vendorName.trim().toLowerCase().contains(fVendor))
					continue;
				if (fZone != null && (zoneNumber == null || !zoneNumber.trim().toLowerCase().contains(fZone)))
					continue;
				if (fAlertType != null && !zoneAlertType.trim().toLowerCase().contains(fAlertType))
					continue;
				if (fZoneDesiredName != null && (zoneDesiredName == null
						|| !zoneDesiredName.trim().toLowerCase().contains(fZoneDesiredName)))
					continue;

				long downtime = downtimeMap.getOrDefault(rowKey, 0L);
				long noInfo = noInfoMap.getOrDefault(serialNo, 0L);
				long uptime = Math.max(0, totalTimeMinutes - downtime - noInfo);

				UptimeTatEntity row = new UptimeTatEntity();
				row.setBranchName(branch.getBranchDesc());
				row.setVendorName(vendorName);
				row.setAlertType(zoneAlertType);
				row.setZoneDesiredName(zoneDesiredName);
				row.setZone(zoneNumber);
				row.setDeviceType(deviceIdToTypeMap.getOrDefault(deviceId, "N/A"));
				row.setTotalTimeMinutes(totalTimeMinutes);
				row.setDowntimeMinutes(downtime);
				row.setNoInfoAvailableMinutes(noInfo);
				row.setUptimeMinutes(uptime);

				uptimeList.add(row);
			}
		} else {
			// Grouped behavior
			Map<String, UptimeTatEntity> groupedMap = new LinkedHashMap<>();
			Map<String, String> groupZoneMap = new HashMap<>();
			Map<String, String> groupZoneDesiredMap = new HashMap<>();
			Map<String, String> groupDeviceTypeMap = new HashMap<>();

			for (DeviceZoneDetailsEntity zone : deviceZoneDetailsList) {
				String deviceId = zone.getDeviceId();
				String zoneAlertType = zone.getAlertType();
				String zoneNumber = zone.getZoneOriginalName();
				String zoneDesiredName = zone.getZoneDesiredName();

				if (deviceId == null || zoneAlertType == null)
					continue;

				DeviceMasterEntity device = deviceByDeviceId.get(deviceId);
				if (device == null)
					continue;

				BranchMasterEntity branch = branchMap.get(device.getBranchCode());
				if (branch == null)
					continue;

				String serialNo = device.getDeviceSerialNo();
				String rowKey = serialNo + "|" + zoneAlertType.trim().toLowerCase();

				if (!processedKeys.add(rowKey))
					continue;

				String vendorName = zone.getVendors();
				if (vendorName == null || vendorName.trim().isEmpty()) {
					vendorName = serialToVendorMap.getOrDefault(serialNo, "N/A");
				}

				// Apply filters
				if (fVendor != null && !vendorName.trim().toLowerCase().contains(fVendor))
					continue;
				if (fBranch != null && !branch.getBranchDesc().trim().toLowerCase().contains(fBranch))
					continue;
				if (fAlertType != null && !zoneAlertType.trim().toLowerCase().contains(fAlertType))
					continue;
				if (fZoneDesiredName != null && (zoneDesiredName == null
						|| !zoneDesiredName.trim().toLowerCase().contains(fZoneDesiredName)))
					continue;

				long downtime = downtimeMap.getOrDefault(rowKey, 0L);
				long noInfo = noInfoMap.getOrDefault(serialNo, 0L);
				long uptime = Math.max(0, totalTimeMinutes - downtime - noInfo);

				String groupKey = branch.getBranchDesc().trim().toLowerCase() + "|"
						+ zoneAlertType.trim().toLowerCase();

				if (!groupZoneMap.containsKey(groupKey)) {
					groupZoneMap.put(groupKey, zoneNumber);
					groupZoneDesiredMap.put(groupKey, zoneDesiredName);
					groupDeviceTypeMap.put(groupKey, deviceIdToTypeMap.getOrDefault(deviceId, "N/A"));
				}

				if (groupedMap.containsKey(groupKey)) {
					UptimeTatEntity existing = groupedMap.get(groupKey);
					existing.setDowntimeMinutes(existing.getDowntimeMinutes() + downtime);
					existing.setNoInfoAvailableMinutes(existing.getNoInfoAvailableMinutes() + noInfo);
					existing.setUptimeMinutes(existing.getUptimeMinutes() + uptime);
				} else {
					UptimeTatEntity row = new UptimeTatEntity();
					row.setBranchName(branch.getBranchDesc());
					row.setVendorName(vendorName);
					row.setAlertType(zoneAlertType);
					row.setZone(groupZoneMap.get(groupKey));
					row.setZoneDesiredName(groupZoneDesiredMap.get(groupKey));
					row.setDeviceType(groupDeviceTypeMap.get(groupKey));
					row.setTotalTimeMinutes(totalTimeMinutes);
					row.setDowntimeMinutes(downtime);
					row.setNoInfoAvailableMinutes(noInfo);
					row.setUptimeMinutes(uptime);
					groupedMap.put(groupKey, row);
				}
			}
			uptimeList.addAll(groupedMap.values());
		}

		// Sort
		uptimeList.sort(
				Comparator.comparing(UptimeTatEntity::getBranchName).thenComparing(UptimeTatEntity::getAlertType));

		// Summary
		long sumTotalTime = 0L;
		long sumDowntime = 0L;
		long sumNoInfo = 0L;
		long sumUptime = 0L;

		for (UptimeTatEntity row : uptimeList) {
			sumTotalTime += row.getTotalTimeMinutes();
			sumDowntime += row.getDowntimeMinutes();
			sumNoInfo += row.getNoInfoAvailableMinutes();
			sumUptime += row.getUptimeMinutes();
		}

		// Convert to hours
		double totalTimeHours = Math.round((sumTotalTime / 60.0) * 100.0) / 100.0;
		double downtimeHours = Math.round((sumDowntime / 60.0) * 100.0) / 100.0;
		double noInfoHours = Math.round((sumNoInfo / 60.0) * 100.0) / 100.0;
		double uptimeHours = Math.round((sumUptime / 60.0) * 100.0) / 100.0;

		double downtimePct = (totalTimeHours > 0) ? Math.round((downtimeHours / totalTimeHours) * 100.0 * 100.0) / 100.0
				: 0.0;
		double noInfoPct = (totalTimeHours > 0) ? Math.round((noInfoHours / totalTimeHours) * 100.0 * 100.0) / 100.0
				: 0.0;
		double uptimePct = (totalTimeHours > 0) ? Math.round((uptimeHours / totalTimeHours) * 100.0 * 100.0) / 100.0
				: 0.0;

		Map<String, Object> summaryMap = new LinkedHashMap<>();
		summaryMap.put("totalTimeHours", totalTimeHours);
		summaryMap.put("downtimeHours", downtimeHours + " (" + downtimePct + "%)");
		summaryMap.put("noInfoAvailableHours", noInfoHours + " (" + noInfoPct + "%)");
		summaryMap.put("uptimeHours", uptimeHours + " (" + uptimePct + "%)");

		// Pagination
		int totalElements = uptimeList.size();
		int totalPages = (size > 0) ? (int) Math.ceil((double) totalElements / size) : 1;
		int safePage = Math.max(0, page);
		int fromIndex = safePage * size;
		int toIndex = Math.min(fromIndex + size, totalElements);
		List<UptimeTatEntity> pagedList = (fromIndex >= totalElements) ? new ArrayList<>()
				: uptimeList.subList(fromIndex, toIndex);

		Map<String, Object> finalPayload = new LinkedHashMap<>();
		finalPayload.put("uptimeList", pagedList);
		finalPayload.put("summary", summaryMap);

		response.setPayload(finalPayload);
		response.setTotalElements(totalElements);
		response.setTotalPages(totalPages);
		response.setCurrentPage(safePage);
		response.setMessage("Uptime TAT report fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	// ==================== HELPER METHODS ====================

	/**
	 * Parse alarm time string to LocalDateTime Format: "27/04/2026, 06:01:35"
	 */
	private LocalDateTime parseAlarmTime(String alarmTime) {
		if (alarmTime == null || alarmTime.trim().isEmpty()) {
			return null;
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
			return LocalDateTime.parse(alarmTime.trim(), formatter);
		} catch (Exception e) {
			System.err.println("Failed to parse alarm time: " + alarmTime);
			return null;
		}
	}

	/**
	 * Parse time difference string to minutes
	 */
	private long parseTimeDifferenceToMinutes(String timeDiff) {
		if (timeDiff == null || timeDiff.trim().isEmpty()) {
			return 0L;
		}

		String diff = timeDiff.trim().toLowerCase();
		long totalMinutes = 0;

		try {
			Pattern pattern = Pattern.compile(
					"(?:(\\d+)\\s*days?)?,?\\s*(?:(\\d+)\\s*hours?)?,?\\s*(?:and\\s+)?(?:(\\d+)\\s*minutes?)?");
			Matcher matcher = pattern.matcher(diff);

			if (matcher.find()) {
				if (matcher.group(1) != null) {
					totalMinutes += Long.parseLong(matcher.group(1)) * 24 * 60;
				}
				if (matcher.group(2) != null) {
					totalMinutes += Long.parseLong(matcher.group(2)) * 60;
				}
				if (matcher.group(3) != null) {
					totalMinutes += Long.parseLong(matcher.group(3));
				}
			}

			if (totalMinutes == 0) {
				Pattern simplePattern = Pattern.compile("(\\d+)\\s*minutes?");
				Matcher simpleMatcher = simplePattern.matcher(diff);
				if (simpleMatcher.find()) {
					totalMinutes = Long.parseLong(simpleMatcher.group(1));
				}
			}

		} catch (Exception e) {
			System.err.println("Failed to parse time difference: " + timeDiff);
		}

		return totalMinutes;
	}

	// public ResponseEntity getUptimeTatReport(String branchCode, String fromDate,
	// String toDate, String filterBranchName,
	// String filterVendorName, String filterZone, String filterAlertType, String
	// filterZoneDesiredName, int page, int size) {
	// ResponseEntity response = new ResponseEntity();
	//
	// // 1. Fetch all child branches
	// ResponseEntity branchResponseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// if (branchResponseEntity.getPayload() == null) {
	// response.setMessage("No branches found for branch code: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseEntity.getPayload();
	// List<String> branchCodes =
	// branchList.stream().map(BranchMasterEntity::getBranchCode).collect(Collectors.toList());
	// Map<String, BranchMasterEntity> branchMap = branchList.stream()
	// .collect(Collectors.toMap(BranchMasterEntity::getBranchCode,
	// Function.identity()));
	//
	// // 2. Fetch devices
	// List<DeviceMasterEntity> deviceList =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	// if (deviceList == null || deviceList.isEmpty()) {
	// response.setMessage("No devices found for branch: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// // 3. Maps for quick lookup
	// Map<String, DeviceMasterEntity> deviceByDeviceId = deviceList.stream()
	// .collect(Collectors.toMap(DeviceMasterEntity::getDeviceId,
	// Function.identity(), (a, b) -> a));
	//
	// // 4. Fetch all zone details for all devices
	// List<String> deviceIds =
	// deviceList.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());
	// List<DeviceZoneDetailsEntity> deviceZoneDetailsList =
	// zoneRepo.findByDeviceIdIn(deviceIds);
	//
	// // 5. Calculate Total Time in minutes + noInfoMap
	// long totalTimeMinutes = 0;
	// Map<String, Long> noInfoMap = new HashMap<>();
	//
	// if (fromDate != null && toDate != null && !fromDate.trim().isEmpty() &&
	// !toDate.trim().isEmpty()) {
	// LocalDateTime startDT = LocalDate.parse(fromDate.trim(),
	// DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
	// LocalDateTime endDT = LocalDate.parse(toDate.trim(),
	// DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
	// totalTimeMinutes = ChronoUnit.MINUTES.between(startDT, endDT);
	//
	// List<String> allSerialNos =
	// deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo).collect(Collectors.toList());
	// noInfoMap = calculateNoInfoMinutes(allSerialNos, startDT, endDT);
	// }
	//
	// // 6. Call getResponseTimeReport → build downtimeMap
	// Map<String, Long> downtimeMap = new HashMap<>();
	// ResponseEntity rtrResponse =
	// deviceMasterService.getResponseTimeReport(branchCode, null, null, null,
	// fromDate, toDate);
	//
	// if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	// Map<String, Object> rtrPayload = (Map<String, Object>)
	// rtrResponse.getPayload();
	// List<InputStatusDetailsEntity> inputStatusDetails =
	// (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	//
	// if (inputStatusDetails != null) {
	// for (InputStatusDetailsEntity entity : inputStatusDetails) {
	// String serialNo = entity.getDeviceSerialNo();
	// String alertType = entity.getAlertType();
	// String timeDiff = entity.getTimeDifference();
	//
	// if (serialNo == null || alertType == null || timeDiff == null) continue;
	//
	// long minutes = 0;
	// try {
	// String cleaned = timeDiff.trim().toLowerCase().replace("minutes",
	// "").replace("minute", "").trim();
	// minutes = Long.parseLong(cleaned);
	// } catch (NumberFormatException ignored) {}
	//
	// String mapKey = serialNo.trim() + "|" + alertType.trim().toLowerCase();
	// downtimeMap.merge(mapKey, minutes, Long::sum);
	// }
	// }
	// }
	//
	// // 7. Build vendor name lookup
	// Map<String, String> serialToVendorMap = new HashMap<>();
	// if (rtrResponse != null && rtrResponse.getPayload() instanceof Map) {
	// Map<String, Object> rtrPayload = (Map<String, Object>)
	// rtrResponse.getPayload();
	// List<InputStatusDetailsEntity> inputStatusDetails =
	// (List<InputStatusDetailsEntity>) rtrPayload.get("inputStatusDetails");
	// if (inputStatusDetails != null) {
	// for (InputStatusDetailsEntity entity : inputStatusDetails) {
	// if (entity.getDeviceSerialNo() != null && entity.getVendorName() != null) {
	// serialToVendorMap.putIfAbsent(entity.getDeviceSerialNo(),
	// entity.getVendorName());
	// }
	// }
	// }
	// }
	//
	// // 8. Prepare filter values
	// String fBranch = (filterBranchName != null &&
	// !filterBranchName.trim().isEmpty()) ? filterBranchName.trim().toLowerCase() :
	// null;
	// String fVendor = (filterVendorName != null &&
	// !filterVendorName.trim().isEmpty()) ? filterVendorName.trim().toLowerCase() :
	// null;
	// String fZone = (filterZone != null && !filterZone.trim().isEmpty()) ?
	// filterZone.trim().toLowerCase() : null;
	// String fAlertType = (filterAlertType != null &&
	// !filterAlertType.trim().isEmpty()) ? filterAlertType.trim().toLowerCase() :
	// null;
	// String fZoneDesiredName = (filterZoneDesiredName != null &&
	// !filterZoneDesiredName.trim().isEmpty()) ?
	// filterZoneDesiredName.trim().toLowerCase() : null;
	//
	// // 9. Decide grouping mode
	// boolean groupByBranchAndAlertType = (fVendor != null && fZone == null);
	//
	// // 10. Build raw rows
	// List<UptimeTatEntity> uptimeList = new ArrayList<>();
	// Set<String> processedKeys = new HashSet<>();
	//
	// if (!groupByBranchAndAlertType) {
	// // Original behavior: one row per device+zone
	// for (DeviceZoneDetailsEntity zone : deviceZoneDetailsList) {
	// String deviceId = zone.getDeviceId();
	// String zoneAlertType = zone.getAlertType();
	// String zoneNumber = zone.getZoneOriginalName();
	// String zoneDesiredName = zone.getZoneDesiredName();
	//
	// if (deviceId == null || zoneAlertType == null) continue;
	//
	// DeviceMasterEntity device = deviceByDeviceId.get(deviceId);
	// if (device == null) continue;
	//
	// BranchMasterEntity branch = branchMap.get(device.getBranchCode());
	// if (branch == null) continue;
	//
	// String serialNo = device.getDeviceSerialNo();
	// // ✅ CHANGE 1: device_type fetch karo device_master se
	// String deviceType = device.getDeviceType();
	//
	// String rowKey = serialNo + "|" + zoneAlertType.trim().toLowerCase();
	// if (!processedKeys.add(rowKey)) continue;
	//
	// String vendorName = zone.getVendors();
	// if (vendorName == null || vendorName.trim().isEmpty()) {
	// vendorName = serialToVendorMap.getOrDefault(serialNo, "N/A");
	// }
	//
	// // Apply filters
	// if (fBranch != null &&
	// !branch.getBranchDesc().trim().toLowerCase().contains(fBranch)) continue;
	// if (fVendor != null && !vendorName.trim().toLowerCase().contains(fVendor))
	// continue;
	// if (fZone != null && (zoneNumber == null ||
	// !zoneNumber.trim().toLowerCase().contains(fZone))) continue;
	// if (fAlertType != null &&
	// !zoneAlertType.trim().toLowerCase().contains(fAlertType)) continue;
	// if (fZoneDesiredName != null && (zoneDesiredName == null ||
	// !zoneDesiredName.trim().toLowerCase().contains(fZoneDesiredName))) continue;
	//
	// long downtime = downtimeMap.getOrDefault(rowKey, 0L);
	// long noInfo = noInfoMap.getOrDefault(serialNo, 0L);
	// // ✅ CHANGE 2: uptime = totalTime - noInfo (downtime hatao)
	// long uptime = Math.max(0, totalTimeMinutes - noInfo);
	//
	// UptimeTatEntity row = new UptimeTatEntity();
	// row.setBranchName(branch.getBranchDesc());
	// row.setVendorName(vendorName);
	// row.setAlertType(zoneAlertType);
	// row.setZoneDesiredName(zoneDesiredName);
	// row.setZone(zoneNumber);
	// row.setTotalTimeMinutes(totalTimeMinutes);
	// row.setDowntimeMinutes(downtime);
	// row.setNoInfoAvailableMinutes(noInfo);
	// row.setUptimeMinutes(uptime);
	// // ✅ CHANGE 1: deviceType set karo
	// row.setDeviceType(deviceType);
	//
	// uptimeList.add(row);
	// }
	//
	// } else {
	// // Grouped behavior: vendor filter applied → sum zones
	// Map<String, UptimeTatEntity> groupedMap = new LinkedHashMap<>();
	//
	// for (DeviceZoneDetailsEntity zone : deviceZoneDetailsList) {
	// String deviceId = zone.getDeviceId();
	// String zoneAlertType = zone.getAlertType();
	// String zoneNumber = zone.getZoneOriginalName();
	//
	// if (deviceId == null || zoneAlertType == null) continue;
	//
	// DeviceMasterEntity device = deviceByDeviceId.get(deviceId);
	// if (device == null) continue;
	//
	// BranchMasterEntity branch = branchMap.get(device.getBranchCode());
	// if (branch == null) continue;
	//
	// String serialNo = device.getDeviceSerialNo();
	// // ✅ CHANGE 1: device_type fetch karo device_master se
	// String deviceType = device.getDeviceType();
	//
	// String rowKey = serialNo + "|" + zoneAlertType.trim().toLowerCase();
	// if (!processedKeys.add(rowKey)) continue;
	//
	// String vendorName = zone.getVendors();
	// if (vendorName == null || vendorName.trim().isEmpty()) {
	// vendorName = serialToVendorMap.getOrDefault(serialNo, "N/A");
	// }
	//
	// // Apply filters
	// if (fVendor != null && !vendorName.trim().toLowerCase().contains(fVendor))
	// continue;
	// if (fBranch != null &&
	// !branch.getBranchDesc().trim().toLowerCase().contains(fBranch)) continue;
	// if (fAlertType != null &&
	// !zoneAlertType.trim().toLowerCase().contains(fAlertType)) continue;
	// String zoneDesiredNameG = zone.getZoneDesiredName();
	// if (fZoneDesiredName != null && (zoneDesiredNameG == null ||
	// !zoneDesiredNameG.trim().toLowerCase().contains(fZoneDesiredName))) continue;
	//
	// long downtime = downtimeMap.getOrDefault(rowKey, 0L);
	// long noInfo = noInfoMap.getOrDefault(serialNo, 0L);
	// // ✅ CHANGE 2: uptime = totalTime - noInfo (downtime hatao)
	// long uptime = Math.max(0, totalTimeMinutes - noInfo);
	//
	// String groupKey = branch.getBranchDesc().trim().toLowerCase() + "|" +
	// zoneAlertType.trim().toLowerCase();
	//
	// if (groupedMap.containsKey(groupKey)) {
	// UptimeTatEntity existing = groupedMap.get(groupKey);
	// existing.setDowntimeMinutes(existing.getDowntimeMinutes() + downtime);
	// existing.setNoInfoAvailableMinutes(existing.getNoInfoAvailableMinutes() +
	// noInfo);
	// existing.setUptimeMinutes(existing.getUptimeMinutes() + uptime);
	// existing.setTotalTimeMinutes(existing.getTotalTimeMinutes() +
	// totalTimeMinutes);
	// } else {
	// UptimeTatEntity row = new UptimeTatEntity();
	// row.setBranchName(branch.getBranchDesc());
	// row.setVendorName(vendorName);
	// row.setAlertType(zoneAlertType);
	// row.setZone(null);
	// row.setTotalTimeMinutes(totalTimeMinutes);
	// row.setDowntimeMinutes(downtime);
	// row.setNoInfoAvailableMinutes(noInfo);
	// row.setUptimeMinutes(uptime);
	// // ✅ CHANGE 1: deviceType set karo
	// row.setDeviceType(deviceType);
	// groupedMap.put(groupKey, row);
	// }
	// }
	//
	// uptimeList.addAll(groupedMap.values());
	// }
	//
	// // Sort by branchName then alertType
	// uptimeList.sort(Comparator.comparing(UptimeTatEntity::getBranchName).thenComparing(UptimeTatEntity::getAlertType));
	//
	// // 11. Summary
	// long sumTotalTime = 0L;
	// long sumDowntime = 0L;
	// long sumNoInfo = 0L;
	// long sumUptime = 0L;
	//
	// for (UptimeTatEntity row : uptimeList) {
	// sumTotalTime += row.getTotalTimeMinutes();
	// sumDowntime += row.getDowntimeMinutes();
	// sumNoInfo += row.getNoInfoAvailableMinutes();
	// sumUptime += row.getUptimeMinutes();
	// }
	//
	// // 12. Minutes → Hours
	// double totalTimeHours = Math.round((sumTotalTime / 60.0) * 100.0) / 100.0;
	// double downtimeHours = Math.round((sumDowntime / 60.0) * 100.0) / 100.0;
	// double noInfoHours = Math.round((sumNoInfo / 60.0) * 100.0) / 100.0;
	// double uptimeHours = Math.round((sumUptime / 60.0) * 100.0) / 100.0;
	//
	// // 13. Percentage (hours base pe)
	// double downtimePct = (totalTimeHours > 0) ? Math.round((downtimeHours /
	// totalTimeHours) * 100.0 * 100.0) / 100.0 : 0.0;
	// double noInfoPct = (totalTimeHours > 0) ? Math.round((noInfoHours /
	// totalTimeHours) * 100.0 * 100.0) / 100.0 : 0.0;
	// double uptimePct = (totalTimeHours > 0) ? Math.round((uptimeHours /
	// totalTimeHours) * 100.0 * 100.0) / 100.0 : 0.0;
	//
	// // 14. Summary map
	// Map<String, Object> summaryMap = new LinkedHashMap<>();
	// summaryMap.put("totalTimeMinutes", sumTotalTime);
	// // summaryMap.put("downtimeMinutes", sumDowntime + " (" + downtimePct +
	// "%)");
	// summaryMap.put("noInfoAvailableMinutes", sumNoInfo + " (" + noInfoPct +
	// "%)");
	// summaryMap.put("uptimeMinutes", sumUptime + " (" + uptimePct + "%)");
	//
	// // 15. Pagination
	// int totalElements = uptimeList.size();
	// int totalPages = (size > 0) ? (int) Math.ceil((double) totalElements / size)
	// : 1;
	// int safePage = Math.max(0, page);
	// int fromIndex = safePage * size;
	// int toIndex = Math.min(fromIndex + size, totalElements);
	//
	// List<UptimeTatEntity> pagedList = (fromIndex >= totalElements)
	// ? new ArrayList<>()
	// : uptimeList.subList(fromIndex, toIndex);
	//
	// // 16. Final payload
	// Map<String, Object> finalPayload = new LinkedHashMap<>();
	// finalPayload.put("uptimeList", pagedList);
	// finalPayload.put("summary", summaryMap);
	//
	// response.setPayload(finalPayload);
	// response.setTotalElements(totalElements);
	// response.setTotalPages(totalPages);
	// response.setCurrentPage(safePage);
	// response.setMessage("Uptime TAT report fetched successfully");
	// response.setStatusCode(HttpStatus.OK.value());
	// return response;
	// }
	//
	// public ResponseEntity getFinalTATReport(String branchCode, LocalDateTime
	// startDate, LocalDateTime endDate) {
	//
	// logger.info("=== getFinalTATReport START ===");
	// logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}",
	// branchCode, startDate, endDate);
	//
	// ResponseEntity response = new ResponseEntity();
	//
	// // Step 1: Date validation
	// if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
	// logger.error("Step 1 FAILED: Invalid date range | startDate={} | endDate={}",
	// startDate, endDate);
	// response.setMessage("Invalid date range provided.");
	// response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	// return response;
	// }
	// logger.info("Step 1 PASSED: Date validation successful");
	//
	// // Step 2: Get all child branches
	// logger.info("Step 2: Fetching child branches for branchCode={}", branchCode);
	// ResponseEntity branchResponseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// if (branchResponseEntity.getPayload() == null) {
	// logger.error("Step 2 FAILED: No branches found for branchCode={}",
	// branchCode);
	// response.setMessage("No branches found for branch code: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	// branchResponseEntity.getPayload();
	// List<String> branchCodes =
	// branchList.stream().map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	// logger.info("Step 2 DONE: Found {} branches -> {}", branchCodes.size(),
	// branchCodes);
	//
	// // Step 3: Get all devices for these branches
	// logger.info("Step 3: Fetching devices for {} branches", branchCodes.size());
	// List<DeviceMasterEntity> deviceList =
	// deviceMasterRepository.findByBranchCodeIn(branchCodes);
	// if (deviceList == null || deviceList.isEmpty()) {
	// logger.error("Step 3 FAILED: No devices found for branchCode={}",
	// branchCode);
	// response.setMessage("No devices found for branch: " + branchCode);
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	// logger.info("Step 3 DONE: Found {} devices", deviceList.size());
	//
	// // Step 4: Get all device_zone_details + build serialNo lookup
	// logger.info("Step 4: Fetching device_zone_details for all devices");
	//
	// Map<String, String> serialNoToDeviceId = new HashMap<>();
	// Map<String, String> deviceZoneKeyToAlertType = new HashMap<>();
	// List<String> allSerialNos = new ArrayList<>();
	//
	// for (DeviceMasterEntity device : deviceList) {
	// if (device.getDeviceSerialNo() != null) {
	// serialNoToDeviceId.put(device.getDeviceSerialNo(), device.getDeviceId());
	// allSerialNos.add(device.getDeviceSerialNo());
	// }
	//
	// List<DeviceZoneDetailsEntity> zones =
	// deviceZoneRepo.findByDeviceId(device.getDeviceId());
	// if (zones == null || zones.isEmpty())
	// continue;
	//
	// for (DeviceZoneDetailsEntity zone : zones) {
	// if (zone.getZoneOriginalName() != null && zone.getAlertType() != null) {
	// String key = device.getDeviceId() + "_" + zone.getZoneOriginalName();
	// deviceZoneKeyToAlertType.put(key, zone.getAlertType());
	// }
	// }
	// }
	// logger.info("Step 4 DONE: allSerialNos={} | allSerialNosSize={} |
	// deviceZoneKeys={}",
	// allSerialNos, allSerialNos.size(), deviceZoneKeyToAlertType.size());
	//
	// // Step 5: Fetch all tickets in date range
	// logger.info("Step 5: Fetching tickets for {} serialNos between {} and {}",
	// allSerialNos.size(), startDate, endDate);
	// List<Ticket> allTickets =
	// ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(allSerialNos,
	// startDate, endDate);
	//
	// int rawTicketCount = (allTickets == null) ? 0 : allTickets.size();
	// logger.info("Step 5 DONE: Found {} raw ticket zone records", rawTicketCount);
	//
	// if (allTickets == null || allTickets.isEmpty()) {
	// logger.warn("Step 5: No tickets found for given date range");
	// response.setMessage("No tickets found for given date range.");
	// response.setStatusCode(HttpStatus.NOT_FOUND.value());
	// return response;
	// }
	//
	// // Step 6: Calculate A1 - Total Alerts (filter by date range only)
	// logger.info("Step 6: Calculating A1 - Total Alerts");
	//
	// int totalAlerts = 0; // A1 - All alerts filtered by daterange
	// Set<String> allTicketNos = new HashSet<>();
	// List<Ticket> validTickets = new ArrayList<>();
	//
	// for (Ticket ticket : allTickets) {
	// String serialNo = ticket.getDeviceSerialNo();
	// String zoneNo = ticket.getZone();
	// if (serialNo == null || zoneNo == null)
	// continue;
	//
	// String deviceId = serialNoToDeviceId.get(serialNo);
	// if (deviceId == null)
	// continue;
	//
	// String lookupKey = deviceId + "_ZONE" + zoneNo;
	// String alertType = deviceZoneKeyToAlertType.get(lookupKey);
	// if (alertType == null)
	// continue;
	//
	// totalAlerts++; // A1 increment
	// validTickets.add(ticket);
	// if (ticket.getTicketNo() != null) {
	// allTicketNos.add(ticket.getTicketNo());
	// }
	// }
	// logger.info("Step 6 DONE: A1 totalAlerts={} | validTickets={} |
	// uniqueTicketNos={}",
	// totalAlerts, validTickets.size(), allTicketNos.size());
	//
	// // Step 7: Fetch escalation logs for all ticketNos
	// logger.info("Step 7: Fetching escalation logs for {} unique ticketNos",
	// allTicketNos.size());
	// List<EscalationLog> escalationLogs =
	// escalationLogRepository.findByTicketNoIn(new ArrayList<>(allTicketNos));
	//
	// // ticketNo -> escalation levels set
	// Map<String, Set<String>> ticketEscLevels = new HashMap<>();
	// // ticketNo -> latest escalation level (to check if escalated at all)
	// Map<String, Boolean> ticketHasAnyEscalation = new HashMap<>();
	//
	// for (EscalationLog log : escalationLogs) {
	// if (log.getTicketNo() != null && log.getEscLevel() != null) {
	// ticketEscLevels.computeIfAbsent(log.getTicketNo(), k -> new HashSet<>())
	// .add(log.getEscLevel().toUpperCase());
	// ticketHasAnyEscalation.put(log.getTicketNo(), true);
	// }
	// }
	// logger.info("Step 7 DONE: escalationLogs={} | ticketsWithEscalation={}",
	// escalationLogs.size(), ticketEscLevels.size());
	//
	// // Step 8: Fetch ticket statuses from TicketHistory or Ticket table
	// logger.info("Step 8: Fetching ticket statuses");
	// // Assuming you have a repository to get latest ticket status
	// // For now, we'll create a map from ticketNo to ticket object with status
	// Map<String, Ticket> ticketStatusMap = new HashMap<>();
	// for (Ticket ticket : validTickets) {
	// if (ticket.getTicketNo() != null) {
	// // If ticket already has status field, use it
	// // Otherwise fetch from database
	// ticketStatusMap.put(ticket.getTicketNo(), ticket);
	// }
	// }
	//
	// // Step 9: Calculate all metrics per ticket
	// logger.info("Step 9: Processing each ticket for all metrics");
	//
	// // A1 - Total Alerts (already calculated)
	//
	// // A1_L1 - TOTAL ALERTS (L1 ESCALATION) -> alerts jo escalation_log m L1 ho
	// chuke h
	// int totalAlertsL1Escalation = 0;
	//
	// // B1 - RESOLVED -> jo ticket ek bar escalation_log m escalate ho chuke h and
	// TICKET_STATUS ='C' h
	// int resolved = 0;
	//
	// // B2 - RESOLVED IN TIME -> ticket jo keval l1 escalation hua h l2 or l3 ni
	// AND ticket_status ="C"
	// int resolvedInTime = 0;
	//
	// // B3 - RESOLVED DELAYED -> jis ticket ka l2 or l3 escalation hua h or ab
	// uska ticket_status ="C" h
	// int resolvedDelayed = 0;
	//
	// // C1 - NOT RESOLVED -> ticket jinka zone_status = "O" h and ticket_status
	// bhi "O" h
	// int notResolved = 0;
	//
	// // C2 - TOTAL DELAY (IN SECONDS) -> saare ticket ke gap time ka sum
	// long totalDelaySeconds = 0;
	//
	// // D1 - Never Escalated + Not Resolved (optional)
	// int neverEscalatedNotResolved = 0;
	//
	// LocalDateTime endOfDay = endDate.toLocalDate().atTime(23, 59, 59);
	// logger.info("Step 9: endOfDay for unresolved fallback = {}", endOfDay);
	//
	// // Group tickets by ticketNo to process each unique ticket
	// Map<String, List<Ticket>> ticketsByTicketNo = validTickets.stream()
	// .filter(t -> t.getTicketNo() != null)
	// .collect(Collectors.groupingBy(Ticket::getTicketNo));
	//
	// logger.info("Step 9: Processing {} unique tickets",
	// ticketsByTicketNo.size());
	//
	// for (Map.Entry<String, List<Ticket>> entry : ticketsByTicketNo.entrySet()) {
	// String ticketNo = entry.getKey();
	// List<Ticket> ticketAlerts = entry.getValue();
	//
	// // Get escalation levels for this ticket
	// Set<String> escLevels = ticketEscLevels.getOrDefault(ticketNo,
	// Collections.emptySet());
	// boolean hasAnyEscalation = !escLevels.isEmpty();
	// boolean hasL1 = escLevels.contains("L1");
	// boolean hasL2OrL3 = escLevels.contains("L2") || escLevels.contains("L3");
	//
	// // Get ticket status from first alert (assuming all alerts for same ticket
	// have same status)
	// Ticket firstTicket = ticketAlerts.get(0);
	// String ticketStatus = firstTicket.getTicketStatus(); // Assuming Ticket has
	// ticketStatus field
	// String zoneStatus = firstTicket.getZoneStatus(); // Assuming Ticket has
	// zoneStatus field
	//
	// // A1_L1 calculation: alerts jo escalation_log m L1 ho chuke h
	// if (hasL1) {
	// // Count all alerts for this ticket in L1 escalation
	// totalAlertsL1Escalation += ticketAlerts.size();
	// logger.debug("Step 9: Ticket={} has L1 escalation, adding {} alerts to
	// A1_L1",
	// ticketNo, ticketAlerts.size());
	// }
	//
	// // B1: RESOLVED -> ticket escalated at least once AND ticket_status = 'C'
	// boolean isResolved = "C".equalsIgnoreCase(ticketStatus);
	//
	// if (isResolved && hasAnyEscalation) {
	// resolved++;
	// logger.debug("Step 9: Ticket={} → RESOLVED (escalated + status=C)",
	// ticketNo);
	//
	// // B2: RESOLVED IN TIME -> only L1 escalation, no L2/L3, and status='C'
	// if (hasL1 && !hasL2OrL3) {
	// resolvedInTime++;
	// logger.debug("Step 9: Ticket={} → RESOLVED IN TIME (only L1)", ticketNo);
	// }
	//
	// // B3: RESOLVED DELAYED -> has L2 or L3 escalation and status='C'
	// if (hasL2OrL3) {
	// resolvedDelayed++;
	// logger.debug("Step 9: Ticket={} → RESOLVED DELAYED (has L2/L3)", ticketNo);
	// }
	// }
	//
	// // C1: NOT RESOLVED -> zone_status='O' and ticket_status='O'
	// boolean isNotResolved = "O".equalsIgnoreCase(zoneStatus) &&
	// "O".equalsIgnoreCase(ticketStatus);
	// if (isNotResolved) {
	// notResolved++;
	// logger.debug("Step 9: Ticket={} → NOT RESOLVED (zone_status=O,
	// ticket_status=O)", ticketNo);
	//
	// // D1: Never escalated + not resolved
	// if (!hasAnyEscalation) {
	// neverEscalatedNotResolved++;
	// logger.debug("Step 9: Ticket={} → NEVER ESCALATED + NOT RESOLVED", ticketNo);
	// }
	// }
	//
	// // C2: Calculate delay for this ticket (using first and last alert of the
	// ticket)
	// // Get earliest alert_generated_id and latest resolved_alert_id for this
	// ticket
	// Long earliestAlertGeneratedId = ticketAlerts.stream()
	// .map(Ticket::getAlertGeneratedId)
	// .filter(id -> id != null && id != 0)
	// .min(Long::compareTo)
	// .orElse(null);
	//
	// Long latestResolvedAlertId = ticketAlerts.stream()
	// .map(Ticket::getResolvedAlertId)
	// .filter(id -> id != null && id != 0)
	// .max(Long::compareTo)
	// .orElse(null);
	//
	// // Calculate gap for this ticket
	// long ticketDelaySeconds = calculateGapSeconds(
	// earliestAlertGeneratedId,
	// latestResolvedAlertId,
	// endOfDay,
	// isResolved && latestResolvedAlertId != null
	// );
	//
	// totalDelaySeconds += ticketDelaySeconds;
	// logger.debug("Step 9: Ticket={} delay={} seconds", ticketNo,
	// ticketDelaySeconds);
	// }
	//
	// logger.info("Step 9 DONE: Processing complete");
	// logger.info("Step 9 RESULTS:");
	// logger.info(" A1 - Total Alerts : {}", totalAlerts);
	// logger.info(" A1_L1 - Total Alerts L1 Esc : {}", totalAlertsL1Escalation);
	// logger.info(" B1 - Resolved : {}", resolved);
	// logger.info(" B2 - Resolved In Time : {}", resolvedInTime);
	// logger.info(" B3 - Resolved Delayed : {}", resolvedDelayed);
	// logger.info(" C1 - Not Resolved : {}", notResolved);
	// logger.info(" C2 - Total Delay (sec) : {}", totalDelaySeconds);
	// logger.info(" D1 - Never Escalated + Not Resolved : {}",
	// neverEscalatedNotResolved);
	//
	// // Step 10: Format C2 total delay
	// String totalDelayFormatted = formatSecondsToHours(totalDelaySeconds);
	// logger.info("Step 10: C2 totalDelay formatted = {}", totalDelayFormatted);
	//
	// // Step 11: Build response map
	// logger.info("Step 11: Building final response map");
	//
	// Map<String, Object> result = new LinkedHashMap<>();
	//
	// // A1
	// Map<String, Object> a1 = new LinkedHashMap<>();
	// a1.put("label", "TOTAL ALERTS");
	// a1.put("value", totalAlerts);
	// result.put("A1_totalAlerts", a1);
	//
	// // A1 - L1 Escalation
	// Map<String, Object> a1L1 = new LinkedHashMap<>();
	// a1L1.put("label", "TOTAL ALERTS (L1 ESCALATION)");
	// a1L1.put("value", totalAlertsL1Escalation);
	// result.put("A1_totalAlertsL1Esc", a1L1);
	//
	// // B1 - Resolved
	// Map<String, Object> b1 = new LinkedHashMap<>();
	// b1.put("label", "RESOLVED");
	// b1.put("value", resolved);
	// result.put("B1_resolved", b1);
	//
	// // B2 - Resolved In Time
	// Map<String, Object> b2 = new LinkedHashMap<>();
	// b2.put("label", "RESOLVED - IN TIME");
	// b2.put("value", resolvedInTime);
	// result.put("B2_resolvedInTime", b2);
	//
	// // B3 - Resolved Delayed
	// Map<String, Object> b3 = new LinkedHashMap<>();
	// b3.put("label", "RESOLVED - DELAYED");
	// b3.put("value", resolvedDelayed);
	// result.put("B3_resolvedDelayed", b3);
	//
	// // C1 - Not Resolved
	// Map<String, Object> c1 = new LinkedHashMap<>();
	// c1.put("label", "NOT RESOLVED");
	// c1.put("value", notResolved);
	// result.put("C1_notResolved", c1);
	//
	// // C2 - Total Delay
	// Map<String, Object> c2 = new LinkedHashMap<>();
	// c2.put("label", "TOTAL DELAY");
	// c2.put("valueSeconds", totalDelaySeconds);
	// c2.put("valueFormatted", totalDelayFormatted);
	// result.put("C2_totalDelay", c2);
	//
	// // D1 - Never Escalated + Not Resolved
	// Map<String, Object> d1 = new LinkedHashMap<>();
	// d1.put("label", "PENDING - NEVER ESCALATED");
	// d1.put("value", neverEscalatedNotResolved);
	// result.put("D1_neverEscalatedNotResolved", d1);
	//
	// logger.info("=== getFinalTATReport END ===");
	//
	// response.setPayload(result);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Final TAT report fetched successfully.");
	// return response;
	// }

	public ResponseEntity getFinalTATReport(String branchCode, LocalDateTime startDate, LocalDateTime endDate) {

		logger.info("=== getFinalTATReport START ===");
		logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}", branchCode, startDate, endDate);

		ResponseEntity response = new ResponseEntity();

		// Step 1: Date validation
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			logger.error("Step 1 FAILED: Invalid date range");
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		// Step 2: Get all child branches
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		// Step 3: Get all devices
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 4: Build device mappings
		Map<String, String> serialNoToDeviceId = new HashMap<>();
		Map<String, String> deviceZoneKeyToAlertType = new HashMap<>();
		List<String> allSerialNos = new ArrayList<>();

		for (DeviceMasterEntity device : deviceList) {
			if (device.getDeviceSerialNo() != null) {
				serialNoToDeviceId.put(device.getDeviceSerialNo(), device.getDeviceId());
				allSerialNos.add(device.getDeviceSerialNo());
			}

			List<DeviceZoneDetailsEntity> zones = deviceZoneRepo.findByDeviceId(device.getDeviceId());
			if (zones == null || zones.isEmpty())
				continue;

			for (DeviceZoneDetailsEntity zone : zones) {
				if (zone.getZoneOriginalName() != null && zone.getAlertType() != null) {
					String key = device.getDeviceId() + "_" + zone.getZoneOriginalName();
					deviceZoneKeyToAlertType.put(key, zone.getAlertType());
				}
			}
		}

		// Step 5: Fetch all tickets
		List<Ticket> allTickets = ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(allSerialNos, startDate,
				endDate);

		if (allTickets == null || allTickets.isEmpty()) {
			response.setMessage("No tickets found for given date range.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 6: Filter valid tickets
		List<Ticket> validTickets = new ArrayList<>();

		for (Ticket ticket : allTickets) {
			String serialNo = ticket.getDeviceSerialNo();
			String zoneNo = ticket.getZone();
			if (serialNo == null || zoneNo == null)
				continue;

			String deviceId = serialNoToDeviceId.get(serialNo);
			if (deviceId == null)
				continue;

			String lookupKey = deviceId + "_ZONE" + zoneNo;
			String alertType = deviceZoneKeyToAlertType.get(lookupKey);
			if (alertType == null)
				continue;

			validTickets.add(ticket);
		}

		int totalAlerts = validTickets.size();
		logger.info("Total valid alerts: {}", totalAlerts);

		if (validTickets.isEmpty()) {
			response.setMessage("No valid tickets found.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		// Step 7: Collect alert IDs
		List<Integer> allAlertGeneratedIds = new ArrayList<>();
		List<Integer> allAlertResolvedIds = new ArrayList<>();

		for (Ticket ticket : validTickets) {
			if (ticket.getAlertGeneratedId() != null && ticket.getAlertGeneratedId() != 0) {
				allAlertGeneratedIds.add(ticket.getAlertGeneratedId().intValue());
			}
			if (ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0) {
				allAlertResolvedIds.add(ticket.getResolvedAlertId().intValue());
			}
		}

		// Step 8-11: Batch fetch data
		Map<Integer, SmsDetailsEntity> smsDetailsGeneratedMap = new HashMap<>();
		if (!allAlertGeneratedIds.isEmpty()) {
			List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository.findAllById(allAlertGeneratedIds);
			for (SmsDetailsEntity sms : smsDetailsList) {
				smsDetailsGeneratedMap.put(sms.getId(), sms);
			}
		}

		Map<Integer, SmsDetailsHistoryEntity> historyGeneratedMap = new HashMap<>();
		Set<Integer> missingGeneratedIds = new HashSet<>();
		for (Integer id : allAlertGeneratedIds) {
			if (!smsDetailsGeneratedMap.containsKey(id))
				missingGeneratedIds.add(id);
		}
		if (!missingGeneratedIds.isEmpty()) {
			List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
					.findByOriginalIdIn(new ArrayList<>(missingGeneratedIds));
			for (SmsDetailsHistoryEntity history : historyList) {
				historyGeneratedMap.put(history.getOriginalId(), history);
			}
		}

		Map<Integer, SmsDetailsEntity> smsDetailsResolvedMap = new HashMap<>();
		if (!allAlertResolvedIds.isEmpty()) {
			List<SmsDetailsEntity> smsDetailsList = smsDetailsRepository.findAllById(allAlertResolvedIds);
			for (SmsDetailsEntity sms : smsDetailsList) {
				smsDetailsResolvedMap.put(sms.getId(), sms);
			}
		}

		Map<Integer, SmsDetailsHistoryEntity> historyResolvedMap = new HashMap<>();
		Set<Integer> missingResolvedIds = new HashSet<>();
		for (Integer id : allAlertResolvedIds) {
			if (!smsDetailsResolvedMap.containsKey(id))
				missingResolvedIds.add(id);
		}
		if (!missingResolvedIds.isEmpty()) {
			List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
					.findByOriginalIdIn(new ArrayList<>(missingResolvedIds));
			for (SmsDetailsHistoryEntity history : historyList) {
				historyResolvedMap.put(history.getOriginalId(), history);
			}
		}

		// Step 12: Get escalation logs
		Set<String> allTicketNos = validTickets.stream().map(Ticket::getTicketNo).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<EscalationLog> escalationLogs = escalationLogRepository.findByTicketNoIn(new ArrayList<>(allTicketNos));
		Map<String, Set<String>> ticketEscLevels = new HashMap<>();
		Map<String, LocalDateTime> ticketFirstEscalationTime = new HashMap<>();

		for (EscalationLog log : escalationLogs) {
			if (log.getTicketNo() != null && log.getEscLevel() != null) {
				ticketEscLevels.computeIfAbsent(log.getTicketNo(), k -> new HashSet<>())
						.add(log.getEscLevel().toUpperCase());

				// Track first escalation time for L1
				if ("L1".equalsIgnoreCase(log.getEscLevel())) {
					if (!ticketFirstEscalationTime.containsKey(log.getTicketNo())) {
						ticketFirstEscalationTime.put(log.getTicketNo(), log.getSentAt());
					}
				}
			}
		}

		// Step 13: Group tickets and calculate times
		Map<String, List<Ticket>> ticketsByTicketNo = validTickets.stream().filter(t -> t.getTicketNo() != null)
				.collect(Collectors.groupingBy(Ticket::getTicketNo));

		Map<String, Date> ticketGeneratedTimeMap = new HashMap<>();
		Map<String, Date> ticketResolvedTimeMap = new HashMap<>();
		Map<String, String> ticketMainStatusMap = new HashMap<>();

		for (Map.Entry<String, List<Ticket>> entry : ticketsByTicketNo.entrySet()) {
			String ticketNo = entry.getKey();
			List<Ticket> tickets = entry.getValue();

			ticketMainStatusMap.put(ticketNo, tickets.get(0).getTicketStatus());

			// Oldest generated time
			Date oldestGeneratedTime = null;
			for (Ticket ticket : tickets) {
				if (ticket.getAlertGeneratedId() != null && ticket.getAlertGeneratedId() != 0) {
					Integer genId = ticket.getAlertGeneratedId().intValue();
					SmsDetailsEntity sms = smsDetailsGeneratedMap.get(genId);
					if (sms != null && sms.getDateTime() != null) {
						if (oldestGeneratedTime == null || sms.getDateTime().before(oldestGeneratedTime)) {
							oldestGeneratedTime = sms.getDateTime();
						}
					} else {
						SmsDetailsHistoryEntity history = historyGeneratedMap.get(genId);
						if (history != null && history.getDateTime() != null) {
							if (oldestGeneratedTime == null || history.getDateTime().before(oldestGeneratedTime)) {
								oldestGeneratedTime = history.getDateTime();
							}
						}
					}
				}
			}
			ticketGeneratedTimeMap.put(ticketNo, oldestGeneratedTime);

			// Latest resolved time
			Date latestResolvedTime = null;
			for (Ticket ticket : tickets) {
				if (ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0) {
					Integer resId = ticket.getResolvedAlertId().intValue();
					SmsDetailsEntity sms = smsDetailsResolvedMap.get(resId);
					if (sms != null && sms.getDateTime() != null) {
						if (latestResolvedTime == null || sms.getDateTime().after(latestResolvedTime)) {
							latestResolvedTime = sms.getDateTime();
						}
					} else {
						SmsDetailsHistoryEntity history = historyResolvedMap.get(resId);
						if (history != null && history.getDateTime() != null) {
							if (latestResolvedTime == null || history.getDateTime().after(latestResolvedTime)) {
								latestResolvedTime = history.getDateTime();
							}
						}
					}
				}
			}
			ticketResolvedTimeMap.put(ticketNo, latestResolvedTime);
		}

		// Step 14: Calculate metrics as per your requirements
		int totalAlertsReceived = totalAlerts; // A - Total Alerts Received

		int alertsResolved = 0; // B - Alerts - Resolved
		int alertsResolvedBeforeEscalation = 0; // C - Alerts Resolved before Escalation
		int alertsResolvedInTimeAtL1 = 0; // D - Alerts Resolved in Time (at L1)
		int alertsResolvedDelayed = 0; // E - Alerts Resolved - Delayed (after L1 escalation)
		int alertsNotResolved = 0; // F - Alerts - Not Resolved
		int alertsPendingNotEscalated = 0; // G - Alerts Pending but Not Escalated
		int alertsPendingAtL1 = 0; // H - Alerts Pending at L1 escalation
		int alertsPendingAtL1AfterHigherEsc = 0; // I - Alerts Pending at L1 even after escalation to higher authorities

		long totalDelaySeconds = 0;

		LocalDateTime now = LocalDateTime.now();
		Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		long slaSeconds = 24 * 60 * 60; // 24 hours SLA

		Map<String, Long> ticketDelayMap = new HashMap<>();

		// Calculate delay per ticket
		for (Map.Entry<String, List<Ticket>> entry : ticketsByTicketNo.entrySet()) {
			String ticketNo = entry.getKey();
			boolean isResolved = "C".equalsIgnoreCase(ticketMainStatusMap.get(ticketNo));
			Date generatedTime = ticketGeneratedTimeMap.get(ticketNo);
			Date resolvedTime = ticketResolvedTimeMap.get(ticketNo);

			long ticketDelaySeconds = 0;
			if (isResolved && resolvedTime != null && generatedTime != null) {
				ticketDelaySeconds = (resolvedTime.getTime() - generatedTime.getTime()) / 1000;
			} else if (!isResolved && generatedTime != null) {
				ticketDelaySeconds = (currentDate.getTime() - generatedTime.getTime()) / 1000;
			}

			if (ticketDelaySeconds > 0) {
				ticketDelayMap.put(ticketNo, ticketDelaySeconds);
				totalDelaySeconds += ticketDelaySeconds;
			}
		}

		// Count each row for all metrics
		for (Ticket ticket : validTickets) {
			String ticketNo = ticket.getTicketNo();
			if (ticketNo == null)
				continue;

			String ticketStatus = ticket.getTicketStatus();
			String zoneStatus = ticket.getZoneStatus();

			boolean isResolved = "C".equalsIgnoreCase(ticketStatus)
					|| "C".equalsIgnoreCase(zoneStatus) && "O".equalsIgnoreCase(ticketStatus);
			boolean isNotResolved = "O".equalsIgnoreCase(zoneStatus) && "O".equalsIgnoreCase(ticketStatus);

			Set<String> escLevels = ticketEscLevels.getOrDefault(ticketNo, Collections.emptySet());
			boolean hasAnyEscalation = !escLevels.isEmpty();
			boolean hasL1 = escLevels.contains("L1");
			boolean hasL2OrL3 = escLevels.contains("L2") || escLevels.contains("L3");

			long resolutionSeconds = ticketDelayMap.getOrDefault(ticketNo, 0L);

			// B - Alerts - Resolved (Total resolved alerts)
			if (isResolved) {
				alertsResolved++;
			}

			// C - Alerts Resolved before Escalation
			if (isResolved && !hasAnyEscalation) {
				alertsResolvedBeforeEscalation++;
			}

			// D - Alerts Resolved in Time (at L1) - Only L1, no L2/L3, within SLA
			if (isResolved && hasL1 && !hasL2OrL3 && resolutionSeconds <= slaSeconds) {
				alertsResolvedInTimeAtL1++;
			}

			// E - Alerts Resolved - Delayed (after L1 escalation) - Has L2/L3 OR beyond SLA
			if (isResolved && hasAnyEscalation && (hasL2OrL3 || resolutionSeconds > slaSeconds)) {
				alertsResolvedDelayed++;
			}

			// F - Alerts - Not Resolved
			if (isNotResolved) {
				alertsNotResolved++;
			}

			// G - Alerts Pending but Not Escalated
			if (isNotResolved && !hasAnyEscalation) {
				alertsPendingNotEscalated++;
			}

			// H - Alerts Pending at L1 escalation
			if (isNotResolved && hasL1 && !hasL2OrL3) {
				alertsPendingAtL1++;
			}

			// I - Alerts Pending at L1 even after escalation to higher authorities
			if (isNotResolved && hasL1 && hasL2OrL3) {
				alertsPendingAtL1AfterHigherEsc++;
			}
		}

		// Extra metrics (keep for reference)
		int totalAlertsL1Escalation = 0;
		for (Ticket ticket : validTickets) {
			String ticketNo = ticket.getTicketNo();
			if (ticketNo == null)
				continue;
			Set<String> escLevels = ticketEscLevels.getOrDefault(ticketNo, Collections.emptySet());
			if (escLevels.contains("L1")) {
				totalAlertsL1Escalation++;
			}
		}

		int resolvedWithEscalation = alertsResolved - alertsResolvedBeforeEscalation;
		int escalatedButNotResolved = alertsNotResolved - alertsPendingNotEscalated;

		// Format delay
		long hours = totalDelaySeconds / 3600;
		long minutes = (totalDelaySeconds % 3600) / 60;
		long seconds = totalDelaySeconds % 60;
		String totalDelayFormatted = String.format("%d hr %d min %d sec", hours, minutes, seconds);

		logger.info("=== FINAL METRICS ===");
		logger.info("A - Total Alerts Received: {}", totalAlertsReceived);
		logger.info("B - Alerts - Resolved: {}", alertsResolved);
		logger.info("C - Alerts Resolved before Escalation: {}", alertsResolvedBeforeEscalation);
		logger.info("D - Alerts Resolved in Time (at L1): {}", alertsResolvedInTimeAtL1);
		logger.info("E - Alerts Resolved - Delayed: {}", alertsResolvedDelayed);
		logger.info("F - Alerts - Not Resolved: {}", alertsNotResolved);
		logger.info("G - Alerts Pending but Not Escalated: {}", alertsPendingNotEscalated);
		logger.info("H - Alerts Pending at L1 escalation: {}", alertsPendingAtL1);
		logger.info("I - Alerts Pending at L1 even after higher escalation: {}", alertsPendingAtL1AfterHigherEsc);

		// Build response as per your requirements
		Map<String, Object> result = new LinkedHashMap<>();

		// A - Total Alerts Received
		Map<String, Object> a = new LinkedHashMap<>();
		a.put("label", "Total Alerts Received");
		a.put("value", totalAlertsReceived);
		result.put("A_totalAlertsReceived", a);

		// B - Alerts - Resolved
		Map<String, Object> b = new LinkedHashMap<>();
		b.put("label", "Alerts - Resolved");
		b.put("value", alertsResolved);
		result.put("B_alertsResolved", b);

		// C - Alerts Resolved before Escalation
		Map<String, Object> c = new LinkedHashMap<>();
		c.put("label", "Alerts Resolved before Escalation");
		c.put("value", alertsResolvedBeforeEscalation);
		result.put("C_alertsResolvedBeforeEscalation", c);

		// D - Alerts Resolved in Time (at L1)
		Map<String, Object> d = new LinkedHashMap<>();
		d.put("label", "Alerts Resolved in Time (at L1)");
		d.put("value", alertsResolvedInTimeAtL1);
		result.put("D_alertsResolvedInTimeAtL1", d);

		// E - Alerts Resolved - Delayed (after L1 escalation)
		Map<String, Object> e = new LinkedHashMap<>();
		e.put("label", "Alerts Resolved - Delayed (after L1 escalation)");
		e.put("value", alertsResolvedDelayed);
		result.put("E_alertsResolvedDelayed", e);

		// F - Alerts - Not Resolved
		Map<String, Object> f = new LinkedHashMap<>();
		f.put("label", "Alerts - Not Resolved");
		f.put("value", alertsNotResolved);
		result.put("F_alertsNotResolved", f);

		// G - Alerts Pending but Not Escalated
		Map<String, Object> g = new LinkedHashMap<>();
		g.put("label", "Alerts Pending but Not Escalated");
		g.put("value", alertsPendingNotEscalated);
		result.put("G_alertsPendingNotEscalated", g);

		// H - Alerts Pending at L1 escalation
		Map<String, Object> h = new LinkedHashMap<>();
		h.put("label", "Alerts Pending at L1 escalation");
		h.put("value", alertsPendingAtL1);
		result.put("H_alertsPendingAtL1", h);

		// I - Alerts Pending at L1 even after escalation to higher authorities
		Map<String, Object> i = new LinkedHashMap<>();
		i.put("label", "Alerts Pending at L1 even after escalation to higher authorities");
		i.put("value", alertsPendingAtL1AfterHigherEsc);
		result.put("I_alertsPendingAtL1AfterHigherEsc", i);

		// ========== EXTRA METRICS (Added at the end) ==========

		// // Total Alerts with L1 Escalation
		// Map<String, Object> extra1 = new LinkedHashMap<>();
		// extra1.put("label", "TOTAL ALERTS (L1 ESCALATION)");
		// extra1.put("value", totalAlertsL1Escalation);
		// result.put("EXTRA_totalAlertsL1Esc", extra1);
		//
		// // Total Delay
		// Map<String, Object> extra2 = new LinkedHashMap<>();
		// extra2.put("label", "TOTAL DELAY");
		// extra2.put("valueSeconds", totalDelaySeconds);
		// extra2.put("valueFormatted", totalDelayFormatted);
		// result.put("EXTRA_totalDelay", extra2);
		//
		// // Resolved with Escalation
		// Map<String, Object> extra3 = new LinkedHashMap<>();
		// extra3.put("label", "RESOLVED (WITH ESCALATION)");
		// extra3.put("value", resolvedWithEscalation);
		// result.put("EXTRA_resolvedWithEscalation", extra3);
		//
		// // Escalated But Not Resolved
		// Map<String, Object> extra4 = new LinkedHashMap<>();
		// extra4.put("label", "ESCALATED BUT NOT RESOLVED");
		// extra4.put("value", escalatedButNotResolved);
		// result.put("EXTRA_escalatedButNotResolved", extra4);

		response.setPayload(result);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("TAT report(s) fetched successfully | tatType=ALL");
		return response;
	}

	// Helper method to get escalation level priority
	private int getEscLevelPriority(String escLevel) {
		if (escLevel == null)
			return 0;
		switch (escLevel.toUpperCase()) {
		case "L1":
			return 1;
		case "L2":
			return 2;
		case "L3":
			return 3;
		case "L4":
			return 4;
		default:
			return 0;
		}
	}

	// Helper: Calculate gap in seconds
	private long calculateGapSeconds(Long alertGeneratedId, Long resolvedAlertId, LocalDateTime endOfDay,
			boolean isResolved) {
		logger.debug("calculateGapSeconds: alertGeneratedId={} | resolvedAlertId={} | isResolved={}", alertGeneratedId,
				resolvedAlertId, isResolved);

		if (alertGeneratedId == null || alertGeneratedId == 0) {
			logger.warn("calculateGapSeconds: alertGeneratedId is null or 0");
			return 0;
		}

		// Get alert generated time
		Date alertGeneratedTime = getSmsDateTime(alertGeneratedId, smsDetailsRepository, smsDetailsHistoryRepository);
		if (alertGeneratedTime == null) {
			logger.warn("calculateGapSeconds: alertGeneratedTime is null for id={}", alertGeneratedId);
			return 0;
		}

		// Get resolved time OR fallback to endOfDay
		Date resolvedTime;
		if (isResolved && resolvedAlertId != null && resolvedAlertId != 0) {
			resolvedTime = getSmsDateTime(resolvedAlertId, smsDetailsRepository, smsDetailsHistoryRepository);
			if (resolvedTime == null) {
				logger.warn("calculateGapSeconds: resolvedTime not found for id={}, using endOfDay", resolvedAlertId);
				resolvedTime = java.sql.Timestamp.valueOf(endOfDay);
			}
		} else {
			// Not resolved → use endOfDay
			resolvedTime = java.sql.Timestamp.valueOf(endOfDay);
			logger.debug("calculateGapSeconds: Not resolved → using endOfDay={}", endOfDay);
		}

		LocalDateTime alertTime = alertGeneratedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime resolvedDT = resolvedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		long gap = ChronoUnit.SECONDS.between(alertTime, resolvedDT);
		if (gap < 0) {
			logger.warn("calculateGapSeconds: Negative gap={}sec, setting to 0", gap);
			gap = 0;
		}

		logger.debug("calculateGapSeconds: gap={}sec | alertTime={} | resolvedTime={}", gap, alertTime, resolvedDT);
		return gap;
	}

	// Helper: Format seconds to hours/minutes/seconds
	private String formatSecondsToHours(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;

		if (hours > 0) {
			return String.format("%d hr %d min %d sec", hours, minutes, seconds);
		} else if (minutes > 0) {
			return String.format("%d min %d sec", minutes, seconds);
		} else {
			return String.format("%d sec", seconds);
		}
	}
	// // ─── Helper: Get DATETIME from sms_details → fallback sms_details_history
	// ────
	// private Date getSmsDateTime(Long smsId,
	// SmsDetailsRepository smsRepo,
	// SmsDetailsHistoryRepository smsHistoryRepo) {
	// if (smsId == null || smsId == 0) return null;
	//
	// Optional<SmsDetailsEntity> sms = smsRepo.findById(smsId.intValue());
	// if (sms.isPresent()) {
	// logger.debug("getSmsDateTime: Found in sms_details | id={}", smsId);
	// return sms.get().getDateTime();
	// }
	//
	// Optional<SmsDetailsHistoryEntity> history =
	// smsHistoryRepo.findByOriginalId(smsId.intValue());
	// if (history.isPresent()) {
	// logger.debug("getSmsDateTime: Found in sms_details_history | originalId={}",
	// smsId);
	// return history.get().getDateTime();
	// }
	//
	// logger.warn("getSmsDateTime: NOT FOUND in both tables | id={}", smsId);
	// return null;
	// }

	// ─── Helper: Format seconds → "Xd Yh Zm Ws" ─────────────────────────────────
	// private String formatSeconds(long totalSeconds) {
	// if (totalSeconds <= 0) return "0d 0h 0m 0s";
	// long days = totalSeconds / 86400;
	// long hours = (totalSeconds % 86400) / 3600;
	// long minutes = (totalSeconds % 3600) / 60;
	// long seconds = totalSeconds % 60;
	// return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	// }

	public ResponseEntity getCombinedTATReport(String branchCode, LocalDateTime startDate, LocalDateTime endDate,
			String category) {

		logger.info("=== getCombinedTATReport START ===");
		logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}, category: {}", branchCode, startDate, endDate,
				category);

		ResponseEntity response = new ResponseEntity();

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 1: Date validation
		// ─────────────────────────────────────────────────────────────────────────
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			logger.error("Step 1 FAILED: Invalid date range");
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		logger.info("Step 1 PASSED: Date validation successful");

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 2: Get all child branches
		// ─────────────────────────────────────────────────────────────────────────
		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());
		logger.info("Step 2 DONE: Found {} branches", branchCodes.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 3: Get all devices
		// ─────────────────────────────────────────────────────────────────────────
		List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
		if (deviceList == null || deviceList.isEmpty()) {
			response.setMessage("No devices found for branch: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		logger.info("Step 3 DONE: Found {} devices", deviceList.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 4: Build lookup maps from device_zone_details
		// ─────────────────────────────────────────────────────────────────────────
		Map<String, String> serialNoToDeviceId = new HashMap<>();
		Map<String, String> deviceZoneKeyToAlertType = new HashMap<>();
		List<String> allSerialNos = new ArrayList<>();

		// For vendor-wise: vendor → branchCodes / systemKeys / serialNos
		Map<String, Set<String>> vendorToBranchCodes = new LinkedHashMap<>();
		Map<String, Set<String>> vendorToSystemKeys = new LinkedHashMap<>();
		Map<String, Set<String>> vendorToSerialNos = new LinkedHashMap<>();

		// serialNo → vendor (needed for alert-wise grouping)
		Map<String, String> serialNoToVendor = new HashMap<>();

		for (DeviceMasterEntity device : deviceList) {
			String serialNo = device.getDeviceSerialNo();
			String deviceId = device.getDeviceId();

			if (serialNo != null) {
				serialNoToDeviceId.put(serialNo, deviceId);
				allSerialNos.add(serialNo);
			}

			List<DeviceZoneDetailsEntity> zones = deviceZoneRepo.findByDeviceId(deviceId);
			if (zones == null || zones.isEmpty())
				continue;

			// zone key → alertType (for Final TAT)
			for (DeviceZoneDetailsEntity zone : zones) {
				if (zone.getZoneOriginalName() != null && zone.getAlertType() != null) {
					String key = deviceId + "_" + zone.getZoneOriginalName();
					deviceZoneKeyToAlertType.put(key, zone.getAlertType());
				}
			}

			// vendor maps (for Vendor-wise TAT)
			Map<String, List<DeviceZoneDetailsEntity>> zoneGroups = zones.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getZoneDesiredName));

			for (Map.Entry<String, List<DeviceZoneDetailsEntity>> entry : zoneGroups.entrySet()) {
				String deviceType = entry.getKey();
				List<DeviceZoneDetailsEntity> zoneGroup = entry.getValue();

				String vendor = zoneGroup.stream().map(DeviceZoneDetailsEntity::getVendors)
						.filter(v -> v != null && !v.isBlank()).findFirst().orElse("UNKNOWN");

				if (serialNo != null)
					serialNoToVendor.put(serialNo, vendor);

				if (device.getBranchCode() != null) {
					vendorToBranchCodes.computeIfAbsent(vendor, k -> new HashSet<>()).add(device.getBranchCode());
				}
				vendorToSystemKeys.computeIfAbsent(vendor, k -> new HashSet<>()).add(deviceId + "_" + deviceType);
				if (serialNo != null) {
					vendorToSerialNos.computeIfAbsent(vendor, k -> new HashSet<>()).add(serialNo);
				}
			}
		}
		logger.info("Step 4 DONE: serialNos={} | vendors={}", allSerialNos.size(), vendorToBranchCodes.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 5: Fetch all tickets in date range
		// ─────────────────────────────────────────────────────────────────────────
		List<Ticket> allTickets = ticketRepository.findByDeviceSerialNoInAndCreateAtBetween(allSerialNos, startDate,
				endDate);

		if (allTickets == null || allTickets.isEmpty()) {
			response.setMessage("No tickets found for given date range.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		logger.info("Step 5 DONE: raw tickets={}", allTickets.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 6: Resolve alertType per ticket → build validTickets
		// ─────────────────────────────────────────────────────────────────────────
		Set<String> allTicketNos = new HashSet<>();
		List<Ticket> validTickets = new ArrayList<>();

		// Also tag each ticket with its resolved alertType (needed for alert-wise)
		Map<String, String> ticketToAlertType = new HashMap<>();

		for (Ticket ticket : allTickets) {
			String serialNo = ticket.getDeviceSerialNo();
			String zoneNo = ticket.getZone();
			if (serialNo == null || zoneNo == null)
				continue;

			String deviceId = serialNoToDeviceId.get(serialNo);
			if (deviceId == null)
				continue;

			String lookupKey = deviceId + "_ZONE" + zoneNo;
			String alertType = deviceZoneKeyToAlertType.get(lookupKey);
			if (alertType == null)
				continue;

			validTickets.add(ticket);
			if (ticket.getTicketNo() != null) {
				allTicketNos.add(ticket.getTicketNo());
				ticketToAlertType.put(ticket.getTicketNo(), alertType);
			}
		}
		logger.info("Step 6 DONE: validTickets={} | uniqueTicketNos={}", validTickets.size(), allTicketNos.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 7: Fetch escalation logs → ticketNo → Set<escLevel>
		// ─────────────────────────────────────────────────────────────────────────
		List<EscalationLog> escalationLogs = escalationLogRepository.findByTicketNoIn(new ArrayList<>(allTicketNos));

		Map<String, Set<String>> ticketEscLevels = new HashMap<>();
		for (EscalationLog log : escalationLogs) {
			if (log.getTicketNo() != null && log.getEscLevel() != null) {
				ticketEscLevels.computeIfAbsent(log.getTicketNo(), k -> new HashSet<>())
						.add(log.getEscLevel().toUpperCase());
			}
		}
		logger.info("Step 7 DONE: escalationLogs={}", escalationLogs.size());

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 8: FINAL TAT metrics (always computed)
		// ─────────────────────────────────────────────────────────────────────────
		int totalAlerts = 0;
		int totalAlertsL1Escalation = 0;
		int resolved = 0;
		int resolvedInTime = 0;
		int resolvedDelayed = 0;
		int notResolved = 0;
		long totalDelaySeconds = 0;

		LocalDateTime endOfDay = endDate.toLocalDate().atTime(23, 59, 59);

		for (Ticket ticket : validTickets) {
			String ticketNo = ticket.getTicketNo();
			Set<String> escLevels = ticketEscLevels.getOrDefault(ticketNo, Collections.emptySet());
			boolean hasEscalation = !escLevels.isEmpty();
			boolean hasL1 = escLevels.contains("L1");

			totalAlerts++;
			if (hasL1)
				totalAlertsL1Escalation++;

			boolean isResolved = ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0;

			if (isResolved) {
				resolved++;
				if (!hasEscalation) {
					resolvedInTime++;
				} else {
					resolvedDelayed++;
					totalDelaySeconds += calculateGapSeconds(ticket.getAlertGeneratedId(), ticket.getResolvedAlertId(),
							endOfDay, true);
				}
			} else {
				notResolved++;
				totalDelaySeconds += calculateGapSeconds(ticket.getAlertGeneratedId(), null, endOfDay, false);
			}
		}

		// Build Final TAT map
		Map<String, Object> finalTat = new LinkedHashMap<>();
		Map<String, Object> a1 = new LinkedHashMap<>();
		a1.put("label", "TOTAL ALERTS");
		a1.put("value", totalAlerts);
		finalTat.put("A1_totalAlerts", a1);

		Map<String, Object> a1L1 = new LinkedHashMap<>();
		a1L1.put("label", "TOTAL ALERTS (L1 ESCALATION)");
		a1L1.put("value", totalAlertsL1Escalation);
		finalTat.put("A1_totalAlertsL1Esc", a1L1);

		Map<String, Object> b1 = new LinkedHashMap<>();
		b1.put("label", "RESOLVED");
		b1.put("value", resolved);
		finalTat.put("B1_resolved", b1);

		Map<String, Object> b2 = new LinkedHashMap<>();
		b2.put("label", "RESOLVED - IN TIME");
		b2.put("value", resolvedInTime);
		finalTat.put("B2_resolvedInTime", b2);

		Map<String, Object> b3 = new LinkedHashMap<>();
		b3.put("label", "RESOLVED - DELAYED");
		b3.put("value", resolvedDelayed);
		finalTat.put("B3_resolvedDelayed", b3);

		Map<String, Object> c1 = new LinkedHashMap<>();
		c1.put("label", "NOT RESOLVED");
		c1.put("value", notResolved);
		finalTat.put("C1_notResolved", c1);

		Map<String, Object> c2 = new LinkedHashMap<>();
		c2.put("label", "TOTAL DELAY");
		c2.put("valueSeconds", totalDelaySeconds);
		c2.put("valueFormatted", formatSeconds(totalDelaySeconds));
		finalTat.put("C2_totalDelay", c2);

		logger.info("Step 8 DONE: Final TAT computed | totalAlerts={} | resolved={}", totalAlerts, resolved);

		// ─────────────────────────────────────────────────────────────────────────
		// STEP 9: Build final response payload
		// ─────────────────────────────────────────────────────────────────────────
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("finalTAT", finalTat); // always present

		boolean isVendorCategory = "vendor".equalsIgnoreCase(category);
		boolean isAlertCategory = "alert".equalsIgnoreCase(category);

		// ── VENDOR-WISE block ────────────────────────────────────────────────────
		if (isVendorCategory) {
			logger.info("Step 9: Building vendor-wise TAT data");
			List<Map<String, Object>> vendorRows = new ArrayList<>();

			// Deduplicate tickets by ticketNo per vendor
			// vendor → unique ticketNo → Ticket
			Map<String, Map<String, Ticket>> vendorTicketMap = new LinkedHashMap<>();
			for (Ticket ticket : validTickets) {
				String serialNo = ticket.getDeviceSerialNo();
				String vendor = serialNoToVendor.getOrDefault(serialNo, "UNKNOWN");
				vendorTicketMap.computeIfAbsent(vendor, k -> new LinkedHashMap<>()).putIfAbsent(ticket.getTicketNo(),
						ticket);
			}

			for (String vendor : vendorToBranchCodes.keySet()) {
				Map<String, Ticket> uniqueTickets = vendorTicketMap.getOrDefault(vendor, Collections.emptyMap());

				if (uniqueTickets.isEmpty()) {
					vendorRows.add(buildVendorRow(vendor, vendorToBranchCodes.get(vendor).size(),
							vendorToSystemKeys.getOrDefault(vendor, Collections.emptySet()).size(), 0, 0, 0, 0, 0, 0,
							"N/A", "N/A"));
					continue;
				}

				int vTotalAlerts = uniqueTickets.size();
				int l0Count = 0, l1Count = 0, l2Count = 0, l3Count = 0;
				long vTotalGapSec = 0;
				long vTotalTimeSec = 0;
				int vResolvedCount = 0;

				for (Ticket ticket : uniqueTickets.values()) {
					String ticketNo = ticket.getTicketNo();
					Set<String> levels = ticketEscLevels.getOrDefault(ticketNo, Collections.emptySet());

					boolean hasL1v = levels.contains("L1");
					boolean hasL2v = levels.contains("L2");
					boolean hasL3v = levels.contains("L3");

					if (hasL1v && hasL2v && hasL3v)
						l3Count++;
					else if (hasL1v && hasL2v)
						l2Count++;
					else if (hasL1v)
						l1Count++;
					else {
						boolean isRes = ticket.getResolvedAlertId() != null;
						if (isRes && ticket.getUpdateAt() != null && ticket.getCreateAt() != null) {
							long hrs = ChronoUnit.HOURS.between(ticket.getCreateAt(), ticket.getUpdateAt());
							if (hrs < 24)
								l0Count++;
						}
					}

					// gap calculation
					boolean isRes = ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0;
					long gap = calculateGapSeconds(ticket.getAlertGeneratedId(),
							isRes ? ticket.getResolvedAlertId() : null, endOfDay, isRes);
					vTotalTimeSec += gap;
					if (isRes) {
						vTotalGapSec += gap;
						vResolvedCount++;
					}
				}

				String averageTat = vResolvedCount > 0 ? formatSeconds(vTotalGapSec / vResolvedCount) : "N/A";
				String totalTime = formatSeconds(vTotalTimeSec);

				vendorRows.add(buildVendorRow(vendor, vendorToBranchCodes.get(vendor).size(),
						vendorToSystemKeys.getOrDefault(vendor, Collections.emptySet()).size(), vTotalAlerts, l0Count,
						l1Count, l2Count, l3Count, vResolvedCount, averageTat, totalTime));
			}

			vendorRows.sort(Comparator.comparing(r -> r.get("vendorName").toString().toLowerCase()));
			payload.put("vendorWiseTAT", vendorRows);
			logger.info("Step 9: vendorWiseTAT added | rows={}", vendorRows.size());
		}

		// ── ALERT-WISE block ─────────────────────────────────────────────────────
		if (isAlertCategory) {
			logger.info("Step 9: Building alert-wise TAT data");

			// alertType → metrics accumulators
			// Using a small inner class equivalent via Map of maps
			Map<String, int[]> alertCounters = new LinkedHashMap<>();
			// alertType → [totalAlerts, resolved, resolvedInTime, resolvedDelayed,
			// notResolved, L1Esc]
			Map<String, Long> alertDelayMap = new LinkedHashMap<>();

			for (Ticket ticket : validTickets) {
				String ticketNo = ticket.getTicketNo();
				String alertType = ticketToAlertType.getOrDefault(ticketNo, "UNKNOWN");

				int[] counters = alertCounters.computeIfAbsent(alertType, k -> new int[6]);
				// [0]=total [1]=resolved [2]=resolvedInTime [3]=resolvedDelayed [4]=notResolved
				// [5]=L1Esc

				counters[0]++; // totalAlerts

				Set<String> escLevels = ticketEscLevels.getOrDefault(ticketNo, Collections.emptySet());
				boolean hasEscalation = !escLevels.isEmpty();
				boolean hasL1a = escLevels.contains("L1");

				if (hasL1a)
					counters[5]++;

				boolean isRes = ticket.getResolvedAlertId() != null && ticket.getResolvedAlertId() != 0;

				if (isRes) {
					counters[1]++;
					if (!hasEscalation) {
						counters[2]++;
					} else {
						counters[3]++;
						long gap = calculateGapSeconds(ticket.getAlertGeneratedId(), ticket.getResolvedAlertId(),
								endOfDay, true);
						alertDelayMap.merge(alertType, gap, Long::sum);
					}
				} else {
					counters[4]++;
					long gap = calculateGapSeconds(ticket.getAlertGeneratedId(), null, endOfDay, false);
					alertDelayMap.merge(alertType, gap, Long::sum);
				}
			}

			List<Map<String, Object>> alertRows = new ArrayList<>();
			for (Map.Entry<String, int[]> entry : alertCounters.entrySet()) {
				String alertType = entry.getKey();
				int[] c = entry.getValue();
				long delayeSec = alertDelayMap.getOrDefault(alertType, 0L);

				Map<String, Object> row = new LinkedHashMap<>();
				row.put("alertType", alertType);
				row.put("totalAlerts", c[0]);
				row.put("totalAlertsL1Esc", c[5]);
				row.put("resolved", c[1]);
				row.put("resolvedInTime", c[2]);
				row.put("resolvedDelayed", c[3]);
				row.put("notResolved", c[4]);
				row.put("totalDelaySeconds", delayeSec);
				row.put("totalDelayFormatted", formatSeconds(delayeSec));
				alertRows.add(row);
			}

			alertRows.sort(Comparator.comparing(r -> r.get("alertType").toString().toLowerCase()));
			payload.put("alertWiseTAT", alertRows);
			logger.info("Step 9: alertWiseTAT added | alertTypes={}", alertRows.size());
		}

		logger.info("=== getCombinedTATReport END ===");
		response.setPayload(payload);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Combined TAT report fetched successfully.");
		return response;
	}

	public ResponseEntity getAllTATReports(String branchCode, LocalDateTime startDate, LocalDateTime endDate,
			String tatType) {

		logger.info("=== getAllTATReports START ===");
		logger.info("Input -> branchCode: {}, startDate: {}, endDate: {}, tatType: {}", branchCode, startDate, endDate,
				tatType);

		ResponseEntity response = new ResponseEntity();

		// ── Step 1: Date validation ───────────────────────────────────────────────
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			logger.error("Step 1 FAILED: Invalid date range | startDate={} | endDate={}", startDate, endDate);
			response.setMessage("Invalid date range provided.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		logger.info("Step 1 PASSED: Date validation OK");

		// ── Step 2: Normalize tatType ─────────────────────────────────────────────
		String type = (tatType == null || tatType.isBlank()) ? "ALL" : tatType.trim().toUpperCase();
		logger.info("Step 2: Normalized tatType={}", type);

		if (!type.equals("ALL") && !type.equals("VENDOR") && !type.equals("ALERT")) {
			logger.error("Step 2 FAILED: Invalid tatType={} | allowed values: ALL, VENDOR, ALERT", type);
			response.setMessage("Invalid tatType. Allowed values: ALL, VENDOR, ALERT.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		logger.info("Step 2 PASSED: tatType={} is valid", type);

		// ── Step 3: Launch futures based on tatType ───────────────────────────────
		logger.info("Step 3: Launching futures | tatType={}", type);
		long parallelStart = System.currentTimeMillis();

		// Final TAT — HAMESHA chalega
		logger.info("Step 3: Submitting getFinalTATReport (always runs)");
		CompletableFuture<ResponseEntity> finalFuture = CompletableFuture.supplyAsync(() -> {
			logger.info("[Async] getFinalTATReport -> START | branchCode={}", branchCode);
			long t = System.currentTimeMillis();
			ResponseEntity r = getFinalTATReport(branchCode, startDate, endDate);
			logger.info("[Async] getFinalTATReport -> DONE | statusCode={} | timeTaken={}ms", r.getStatusCode(),
					System.currentTimeMillis() - t);
			return r;
		});

		// Vendor — only if tatType = VENDOR or ALL
		CompletableFuture<ResponseEntity> vendorFuture = null;
		if (type.equals("VENDOR") || type.equals("ALL")) {
			logger.info("Step 3: Submitting getVendorWiseTATReport | tatType={}", type);
			vendorFuture = CompletableFuture.supplyAsync(() -> {
				logger.info("[Async] getVendorWiseTATReport -> START | branchCode={}", branchCode);
				long t = System.currentTimeMillis();
				ResponseEntity r = getVendorWiseTATReport(branchCode, startDate, endDate);
				logger.info("[Async] getVendorWiseTATReport -> DONE | statusCode={} | timeTaken={}ms",
						r.getStatusCode(), System.currentTimeMillis() - t);
				return r;
			});
		} else {
			logger.info("Step 3: Skipping getVendorWiseTATReport | tatType={}", type);
		}

		// Alert — only if tatType = ALERT or ALL
		CompletableFuture<ResponseEntity> alertFuture = null;
		if (type.equals("ALERT") || type.equals("ALL")) {
			logger.info("Step 3: Submitting getAlertWiseTATReport | tatType={}", type);
			alertFuture = CompletableFuture.supplyAsync(() -> {
				logger.info("[Async] getAlertWiseTATReport -> START | branchCode={}", branchCode);
				long t = System.currentTimeMillis();
				ResponseEntity r = getAlertWiseTATReport(branchCode, startDate, endDate);
				logger.info("[Async] getAlertWiseTATReport -> DONE | statusCode={} | timeTaken={}ms", r.getStatusCode(),
						System.currentTimeMillis() - t);
				return r;
			});
		} else {
			logger.info("Step 3: Skipping getAlertWiseTATReport | tatType={}", type);
		}

		// ── Step 4: Wait for all submitted futures ────────────────────────────────
		logger.info("Step 4: Waiting for all submitted futures to complete...");

		ResponseEntity finalResult = null;
		ResponseEntity vendorResult = null;
		ResponseEntity alertResult = null;

		try {
			// Collect only non-null futures
			List<CompletableFuture<ResponseEntity>> activeFutures = new ArrayList<>();
			activeFutures.add(finalFuture);
			if (vendorFuture != null)
				activeFutures.add(vendorFuture);
			if (alertFuture != null)
				activeFutures.add(alertFuture);

			logger.info("Step 4: Total active futures waiting={}", activeFutures.size());

			CompletableFuture
					.allOf(finalFuture, vendorFuture != null ? vendorFuture : CompletableFuture.completedFuture(null),
							alertFuture != null ? alertFuture : CompletableFuture.completedFuture(null))
					.join();

			finalResult = finalFuture.get();
			if (vendorFuture != null)
				vendorResult = vendorFuture.get();
			if (alertFuture != null)
				alertResult = alertFuture.get();

			logger.info("Step 4 DONE: All futures resolved | totalParallelTime={}ms",
					System.currentTimeMillis() - parallelStart);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Step 4 FAILED: Thread interrupted while waiting for TAT futures", e);
			response.setMessage("Report generation interrupted. Please try again.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		} catch (ExecutionException e) {
			logger.error("Step 4 FAILED: ExecutionException | cause={}",
					e.getCause() != null ? e.getCause().getMessage() : "unknown", e);
			response.setMessage("Error while generating one of the TAT reports.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		// ── Step 5: Log individual results ────────────────────────────────────────
		logger.info("Step 5: Individual result status check");
		logger.info("  getFinalTATReport      -> statusCode={} | hasPayload={}", finalResult.getStatusCode(),
				finalResult.getPayload() != null);
		if (vendorResult != null) {
			logger.info("  getVendorWiseTATReport -> statusCode={} | hasPayload={}", vendorResult.getStatusCode(),
					vendorResult.getPayload() != null);
		} else {
			logger.info("  getVendorWiseTATReport -> SKIPPED (tatType={})", type);
		}
		if (alertResult != null) {
			logger.info("  getAlertWiseTATReport  -> statusCode={} | hasPayload={}", alertResult.getStatusCode(),
					alertResult.getPayload() != null);
		} else {
			logger.info("  getAlertWiseTATReport  -> SKIPPED (tatType={})", type);
		}

		// ── Step 6: Build combined payload ────────────────────────────────────────
		logger.info("Step 6: Building combined payload | tatType={}", type);

		Map<String, Object> combinedPayload = new LinkedHashMap<>();

		// Final — hamesha
		combinedPayload.put("finalTATReport", finalResult.getPayload());
		logger.info("Step 6: finalTATReport added | payload={}", finalResult.getPayload() != null ? "present" : "null");

		// Vendor — only if requested
		if (vendorResult != null) {
			combinedPayload.put("vendorWiseTATReport", vendorResult.getPayload());
			logger.info("Step 6: vendorWiseTATReport added | size={}",
					vendorResult.getPayload() instanceof List ? ((List<?>) vendorResult.getPayload()).size() : "N/A");
		} else {
			combinedPayload.put("vendorWiseTATReport", null);
			logger.info("Step 6: vendorWiseTATReport -> null (not requested)");
		}

		// Alert — only if requested
		if (alertResult != null) {
			combinedPayload.put("alertWiseTATReport", alertResult.getPayload());
			logger.info("Step 6: alertWiseTATReport added | size={}",
					alertResult.getPayload() instanceof List ? ((List<?>) alertResult.getPayload()).size() : "N/A");
		} else {
			combinedPayload.put("alertWiseTATReport", null);
			logger.info("Step 6: alertWiseTATReport -> null (not requested)");
		}

		// ── Step 7: Meta block ────────────────────────────────────────────────────
		logger.info("Step 7: Building _meta block");

		Map<String, Object> meta = new LinkedHashMap<>();
		meta.put("tatType", type);
		meta.put("finalTATStatus", finalResult.getStatusCode());
		meta.put("finalTATMessage", finalResult.getMessage());
		meta.put("vendorWiseStatus", vendorResult != null ? vendorResult.getStatusCode() : "SKIPPED");
		meta.put("vendorWiseMessage", vendorResult != null ? vendorResult.getMessage() : "Not requested");
		meta.put("alertWiseStatus", alertResult != null ? alertResult.getStatusCode() : "SKIPPED");
		meta.put("alertWiseMessage", alertResult != null ? alertResult.getMessage() : "Not requested");
		meta.put("totalTimeTakenMs", System.currentTimeMillis() - parallelStart);

		combinedPayload.put("_meta", meta);
		logger.info("Step 7 DONE: _meta={}", meta);

		// ── Step 8: Determine overall status ──────────────────────────────────────
		logger.info("Step 8: Determining overall HTTP status");

		boolean finalOk = isOk(finalResult);
		boolean vendorOk = vendorResult == null || isOk(vendorResult); // skipped = OK
		boolean alertOk = alertResult == null || isOk(alertResult); // skipped = OK

		logger.info("Step 8: finalOk={} | vendorOk={} | alertOk={}", finalOk, vendorOk, alertOk);

		String overallMessage;
		int overallStatus;

		if (finalOk && vendorOk && alertOk) {
			overallStatus = HttpStatus.OK.value();
			overallMessage = "TAT report(s) fetched successfully | tatType=" + type;
			logger.info("Step 8: Overall -> 200 OK");
		} else if (finalOk || vendorOk || alertOk) {
			overallStatus = 206; // Partial Content
			overallMessage = "Some TAT reports could not be generated. Check _meta for details.";
			logger.warn("Step 8: Overall -> 206 Partial | finalOk={} | vendorOk={} | alertOk={}", finalOk, vendorOk,
					alertOk);
		} else {
			overallStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
			overallMessage = "All TAT reports failed. Check _meta for individual errors.";
			logger.error("Step 8: Overall -> 500 | All reports failed");
		}

		// ── Step 9: Return ────────────────────────────────────────────────────────
		logger.info("Step 9: Returning final response | overallStatus={} | message={}", overallStatus, overallMessage);

		response.setPayload(combinedPayload);
		response.setStatusCode(overallStatus);
		response.setMessage(overallMessage);

		logger.info("=== getAllTATReports END | totalTime={}ms ===", System.currentTimeMillis() - parallelStart);
		return response;
	}

	// ── Helper
	// ────────────────────────────────────────────────────────────────────
	private boolean isOk(ResponseEntity r) {
		return r != null && Integer.valueOf(200).equals(r.getStatusCode());
	}

	public List<NotWorkingDeviceDTO> getNotWorkingDevicesByZone(String branchCode, String zoneName) {
		List<NotWorkingDeviceDTO> result = new ArrayList<>();

		// Get all children branches for the given branchCode
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: null;

		if (branchList == null || branchList.isEmpty())
			return result;

		// Determine which zones to query
		List<String> zoneTypes = (zoneName != null && !zoneName.isBlank()) ? List.of(zoneName) : getZoneTypesFromDB();
		for (String zoneType : zoneTypes) {
			for (BranchMasterEntity branch : branchList) {

				DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());

				if (deviceMasterEntity == null || "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
					continue;
				}

				List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = deviceZoneRepo
						.findByZoneDesiredNameAndDeviceId(zoneType, deviceMasterEntity.getDeviceId());

				if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
					continue;
				}

				SmsDetailsEntity smsDetailsEntity = getLatestSms(deviceMasterEntity.getDeviceSerialNo());

				if (smsDetailsEntity == null)
					continue;

				// Group by inputType
				Map<String, List<DeviceZoneDetailsEntity>> groupedZones = deviceZoneDetailsEntities.stream()
						.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

				groupedZones.forEach((inputType, zones) -> {
					String groupStatus = determineGroupStatus(zones, smsDetailsEntity);

					if ("Not Working".equals(groupStatus)) {

						// ✅ Print only Not Working SMS details in console
						System.out.println("========== NOT WORKING DEVICE SMS DETAIL ==========");
						System.out.println("Branch Code     : " + branch.getBranchCode());
						System.out.println("Device Serial No: " + deviceMasterEntity.getDeviceSerialNo());
						System.out.println("Zone Type       : " + zoneType);
						System.out.println("Input Type      : " + inputType);
						System.out.println("SMS ID          : " + smsDetailsEntity.getId());
						System.out.println("SMS DateTime    : " + smsDetailsEntity.getDateTime());
						System.out.println("SMS       : " + smsDetailsEntity.toString());
						System.out.println("===================================================");

						NotWorkingDeviceDTO dto = new NotWorkingDeviceDTO();
						dto.setDeviceSerialNo(deviceMasterEntity.getDeviceSerialNo());
						dto.setZone(zoneType);
						dto.setStatus("Not Working");
						dto.setLastAlertTime(smsDetailsEntity.getDateTime());
						result.add(dto);
					}
				});
			}
		}

		return result;
	}

	public ResponseEntity getAllNotWorkingDevicesByZone(String branchCode, String zoneName) {
		ResponseEntity response = new ResponseEntity();

		List<NotWorkingDeviceDTO> notWorkingDevices = getNotWorkingDevicesByZone(branchCode, zoneName);

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("data", notWorkingDevices);
		payload.put("totalNotWorkingCount", notWorkingDevices.size());

		response.setPayload(payload);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Not working devices fetched successfully");
		return response;
	}

	// public ResponseEntity getCountsByZoneStatusFast(String branchCode) {
	// ResponseEntity response = new ResponseEntity();
	// List<String> zoneTypes = getZoneTypesFromDB();
	// // List<String> zoneTypes = Arrays.asList("CCTV", "LOCKER CCTV", "Security
	// // Alarm", "Fire Alarm", "BACS", "ETL");
	// List<String> statuses = Arrays.asList("Working", "Partially Working", "Not
	// Working", "No Info");
	//
	// Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();
	//
	// ResponseEntity responseEntity =
	// branchMasterService.getAllChildren(branchCode);
	// List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
	// ? (List<BranchMasterEntity>) responseEntity.getPayload()
	// : Collections.emptyList();
	//
	// if (branchList.isEmpty()) {
	// response.setPayload(buildEmptyResult(zoneTypes, statuses));
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Counts fetched successfully");
	// return response;
	// }
	//
	// List<String> branchCodes =
	// branchList.stream().map(BranchMasterEntity::getBranchCode)
	// .collect(Collectors.toList());
	//
	// List<DeviceMasterEntity> allDevices =
	// deviceMasterRepository.findActiveByBranchCodes(branchCodes);
	// Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
	// .collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, d -> d, (a, b)
	// -> a));
	//
	// List<String> deviceIds =
	// allDevices.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());
	//
	// List<DeviceZoneDetailsEntity> allZones =
	// deviceZoneRepo.findByDeviceIdsAndZoneTypes(deviceIds, zoneTypes);
	//
	// Map<String, Map<String, List<DeviceZoneDetailsEntity>>> zoneIndex = new
	// HashMap<>();
	// for (DeviceZoneDetailsEntity zone : allZones) {
	// zoneIndex.computeIfAbsent(zone.getDeviceId(), k -> new HashMap<>())
	// .computeIfAbsent(zone.getZoneDesiredName(), k -> new
	// ArrayList<>()).add(zone);
	// }
	//
	// long thresholdHours = getThresholdHours();
	//
	// for (String zoneType : zoneTypes) {
	// Map<String, Long> statusCounts = new LinkedHashMap<>();
	//
	// for (String status : statuses) {
	// List<ZoneReportDTO> dtoList = new ArrayList<>();
	//
	// for (BranchMasterEntity branch : branchList) {
	// DeviceMasterEntity device = deviceMap.get(branch.getBranchCode());
	// if (device == null || "inactive".equalsIgnoreCase(device.getStatus())) {
	// continue;
	// }
	//
	// Map<String, List<DeviceZoneDetailsEntity>> zonesByType =
	// zoneIndex.get(device.getDeviceId());
	// if (zonesByType == null)
	// continue;
	//
	// List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities =
	// zonesByType.get(zoneType);
	// if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty())
	// {
	// continue;
	// }
	//
	// SmsDetailsEntity smsDetailsEntity =
	// getLatestSmsForThresholdTime(device.getDeviceSerialNo(),
	// thresholdHours);
	//
	// if (smsDetailsEntity == null) {
	// if ("No Info".equalsIgnoreCase(status)) {
	// if (checkNonSyncLog(device.getDeviceSerialNo())) {
	// ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
	// zoneReportDTO.setBranchCode(branch.getBranchCode());
	// dtoList.add(zoneReportDTO);
	// }
	// }
	// continue;
	// }
	//
	// Map<String, List<DeviceZoneDetailsEntity>> groupedZones =
	// deviceZoneDetailsEntities.stream()
	// .collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));
	//
	// for (List<DeviceZoneDetailsEntity> zones : groupedZones.values()) {
	// Map<String, String> groupStatusDetails =
	// determineGroupStatusWithDetails(zones,
	// smsDetailsEntity);
	// String groupStatus = groupStatusDetails.get("groupStatus");
	//
	// if (groupStatus.equalsIgnoreCase(status)) {
	// ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
	// if (branch.getParentCode() != null) {
	// BranchMasterEntity parent = branchMasterRepository
	// .findBybranchCode(branch.getParentCode());
	// if (parent != null) {
	// zoneReportDTO.setParentBranch(parent.getBranchDesc());
	// }
	// }
	// zoneReportDTO.setBranchCode(branch.getBranchCode());
	// zoneReportDTO.setBank(branch.getBank());
	// zoneReportDTO.setBranchName(branch.getBranchDesc());
	// zoneReportDTO.setMobile(branch.getMobile());
	// zoneReportDTO.setDeviceSerialNo(device.getDeviceSerialNo());
	//
	// List<ZonesDTO> zonesDTOList = new ArrayList<>();
	// for (DeviceZoneDetailsEntity zone : zones) {
	// ZonesDTO zonesDTO = new ZonesDTO();
	// zonesDTO.setZone(zone.getZoneOriginalName());
	// zonesDTO.setZoneType(zone.getZoneDesiredName());
	// zonesDTO.setAlertType(zone.getAlertType());
	// zonesDTO.setStatus(groupStatusDetails.get(zone.getZoneOriginalName()));
	// zonesDTOList.add(zonesDTO);
	// }
	// zoneReportDTO.setZoneDTOList(zonesDTOList);
	// dtoList.add(zoneReportDTO);
	// break;
	// }
	// }
	// }
	//
	// long count =
	// dtoList.stream().map(ZoneReportDTO::getBranchCode).distinct().count();
	//
	// statusCounts.put(status, count);
	// }
	//
	// countsByZoneType.put(zoneType, statusCounts);
	// }
	//
	// response.setPayload(countsByZoneType);
	// response.setStatusCode(HttpStatus.OK.value());
	// response.setMessage("Counts fetched successfully");
	// return response;
	// }

	public ResponseEntity getCountsByZoneStatusFast(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<String> zoneTypes = getZoneTypesFromDB();
		List<String> statuses = Arrays.asList("Working", "Partially Working", "Not Working", "No Info");

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: Collections.emptyList();

		if (branchList.isEmpty()) {
			response.setPayload(buildEmptyResult(zoneTypes, statuses));
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Counts fetched successfully");
			return response;
		}

		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		List<DeviceMasterEntity> allDevices = deviceMasterRepository.findActiveByBranchCodes(branchCodes);
		Map<String, DeviceMasterEntity> deviceMap = allDevices.stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getBranchCode, d -> d, (a, b) -> a));

		List<String> deviceIds = allDevices.stream().map(DeviceMasterEntity::getDeviceId).collect(Collectors.toList());
		List<String> serialNos = allDevices.stream().map(DeviceMasterEntity::getDeviceSerialNo)
				.collect(Collectors.toList());

		List<DeviceZoneDetailsEntity> allZones = deviceZoneRepo.findByDeviceIdsAndZoneTypes(deviceIds, zoneTypes);
		Map<String, Map<String, List<DeviceZoneDetailsEntity>>> zoneIndex = new HashMap<>();
		for (DeviceZoneDetailsEntity zone : allZones) {
			zoneIndex.computeIfAbsent(zone.getDeviceId(), k -> new HashMap<>())
					.computeIfAbsent(zone.getZoneDesiredName(), k -> new ArrayList<>()).add(zone);
		}

		long thresholdHours = getThresholdHours();
		Date cutoff = new Date(System.currentTimeMillis() - thresholdHours * 60 * 60 * 1000);

		Map<String, SmsDetailsEntity> smsMap = buildSmsMap(serialNos, cutoff);
		Map<String, Boolean> nonSyncMap = buildNonSyncMap(serialNos, cutoff);

		Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();
		for (String zoneType : zoneTypes) {
			Map<String, Long> statusCounts = new LinkedHashMap<>();
			for (String status : statuses)
				statusCounts.put(status, 0L);

			for (BranchMasterEntity branch : branchList) {
				DeviceMasterEntity device = deviceMap.get(branch.getBranchCode());
				if (device == null || "inactive".equalsIgnoreCase(device.getStatus()))
					continue;

				Map<String, List<DeviceZoneDetailsEntity>> zonesByType = zoneIndex.get(device.getDeviceId());
				if (zonesByType == null)
					continue;

				List<DeviceZoneDetailsEntity> zonesForType = zonesByType.get(zoneType);
				if (zonesForType == null || zonesForType.isEmpty())
					continue;

				SmsDetailsEntity sms = smsMap.get(device.getDeviceSerialNo());
				boolean hasNonSync = nonSyncMap.getOrDefault(device.getDeviceSerialNo(), false);

				if (sms == null) {
					if (hasNonSync)
						statusCounts.merge("No Info", 1L, Long::sum);
					continue;
				}

				Map<String, List<DeviceZoneDetailsEntity>> grouped = zonesForType.stream()
						.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

				Set<String> distinctStatuses = new HashSet<>();
				for (List<DeviceZoneDetailsEntity> group : grouped.values()) {
					Map<String, String> statusDetails = determineGroupStatusWithDetails(group, sms);
					distinctStatuses.add(statusDetails.get("groupStatus"));
				}

				for (String st : distinctStatuses) {
					statusCounts.merge(st, 1L, Long::sum);
				}
			}

			countsByZoneType.put(zoneType, statusCounts);
		}

		response.setPayload(countsByZoneType);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Counts fetched successfully");
		return response;
	}

	private Map<String, SmsDetailsEntity> buildSmsMap(List<String> serialNos, Date cutoff) {
		Map<String, SmsDetailsEntity> map = new HashMap<>();
		if (serialNos.isEmpty())
			return map;

		List<SmsDetailsEntity> latestList = smsDetailsRepository.findLatestBySerialNos(serialNos);
		for (SmsDetailsEntity sms : latestList) {
			if (sms.getDateTime() == null || !sms.getDateTime().before(cutoff)) {
				map.put(sms.getPanelSerialNo(), sms);
			}
		}

		List<String> remaining = serialNos.stream().filter(s -> !map.containsKey(s)).collect(Collectors.toList());
		if (!remaining.isEmpty()) {
			List<SmsDetailsHistoryEntity> histList = smsDetailsHistoryRepository.findLatestBySerialNos(remaining);
			for (SmsDetailsHistoryEntity hist : histList) {
				if (hist.getDateTime() == null || !hist.getDateTime().before(cutoff)) {
					SmsDetailsEntity fallback = new SmsDetailsEntity();
					BeanUtils.copyProperties(hist, fallback);
					map.put(fallback.getPanelSerialNo(), fallback);
				}
			}
		}
		return map;
	}

	private Map<String, Boolean> buildNonSyncMap(List<String> serialNos, Date cutoff) {
		Map<String, Boolean> map = new HashMap<>();
		if (serialNos.isEmpty())
			return map;

		LocalDateTime ldtCutoff = cutoff.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		List<NonSyncLogEntity> logs = nonSyncLogRepository.findActiveNonSyncByPanelSerialNos(serialNos);
		for (NonSyncLogEntity log : logs) {
			if (log.getNonSyncStartDatetime() != null && log.getNonSyncStartDatetime().isBefore(ldtCutoff)) {
				map.put(log.getPanelSerialNo(), true);
			}
		}
		return map;
	}

	private Map<String, Map<String, Long>> buildEmptyResult(List<String> zoneTypes, List<String> statuses) {
		Map<String, Map<String, Long>> result = new LinkedHashMap<>();
		for (String zoneType : zoneTypes) {
			Map<String, Long> statusCounts = new LinkedHashMap<>();
			for (String status : statuses) {
				statusCounts.put(status, 0L);
			}
			result.put(zoneType, statusCounts);
		}
		return result;
	}

	public ResponseEntity getCountsByZoneStatusForToday(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<String> zoneTypes = getZoneTypesFromDB();
		// List<String> zoneTypes = Arrays.asList("CCTV", "LOCKER CCTV", "Security
		// Alarm", "Fire Alarm", "BACS", "ETL");
		List<String> statuses = Arrays.asList("Working", "Partially Working", "Not Working");

		Map<String, Map<String, Long>> countsByZoneType = new LinkedHashMap<>();

		// Fetch all children branches once
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: Collections.emptyList();

		for (String zoneType : zoneTypes) {
			Map<String, Long> statusCounts = new LinkedHashMap<>();

			// --- Working / Partially Working / Not Working ---
			for (String status : statuses) {
				ResponseEntity zoneResponse = countingZonesByStatusForToday(branchCode, zoneType, status);

				List<ZoneReportDTO> dtoList = (zoneResponse.getPayload() != null)
						? (List<ZoneReportDTO>) zoneResponse.getPayload()
						: Collections.emptyList();

				long count = dtoList.stream().map(ZoneReportDTO::getBranchCode).distinct().count();

				statusCounts.put(status, count);
			}

			// --- Non Sync count (merged into Not Working) ---
			long nonSyncCount = branchList.stream().filter(branch -> {
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());

				// Skip inactive or missing devices
				if (device == null || "inactive".equalsIgnoreCase(device.getStatus())) {
					return false;
				}

				// Skip if no zones of this type exist for the device
				List<DeviceZoneDetailsEntity> zones = deviceZoneRepo.findByZoneDesiredNameAndDeviceId(zoneType,
						device.getDeviceId());
				if (zones == null || zones.isEmpty()) {
					return false;
				}

				// Non Sync = today's SMS not found
				SmsDetailsEntity sms = getLatestSmsForToday(device.getDeviceSerialNo());
				return sms == null;
			}).map(BranchMasterEntity::getBranchCode).distinct().count();

			// Merge Non Sync into Not Working
			long existingNotWorking = statusCounts.getOrDefault("Not Working", 0L);
			statusCounts.put("Not Working", existingNotWorking + nonSyncCount);

			countsByZoneType.put(zoneType, statusCounts);
		}

		response.setPayload(countsByZoneType);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Counts fetched successfully");
		return response;
	}

	public ResponseEntity countingZonesByStatusForToday(String branchCode, String zoneType, String status) {
		ResponseEntity response = new ResponseEntity();
		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (responseEntity.getPayload() != null)
				? (List<BranchMasterEntity>) responseEntity.getPayload()
				: null;

		branchList.forEach(branch -> {
			DeviceMasterEntity deviceMasterEntity = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
			if (deviceMasterEntity == null || "inactive".equalsIgnoreCase(deviceMasterEntity.getStatus())) {
				return;
			}

			List<DeviceZoneDetailsEntity> deviceZoneDetailsEntities = deviceZoneRepo
					.findByZoneDesiredNameAndDeviceId(zoneType, deviceMasterEntity.getDeviceId());

			if (deviceZoneDetailsEntities == null || deviceZoneDetailsEntities.isEmpty()) {
				return;
			}

			// SmsDetailsEntity smsDetailsEntity =
			// smsDetailsRepository.getLatestRecord(deviceMasterEntity.getDeviceSerialNo());
			// if (smsDetailsEntity == null) {
			// return;
			// }

			SmsDetailsEntity smsDetailsEntity = getLatestSmsForToday(deviceMasterEntity.getDeviceSerialNo());

			if (smsDetailsEntity == null) {
				return;
			}

			// Group zones by inputType
			Map<String, List<DeviceZoneDetailsEntity>> groupedZones = deviceZoneDetailsEntities.stream()
					.collect(Collectors.groupingBy(DeviceZoneDetailsEntity::getInputType));

			groupedZones.forEach((inputType, zones) -> {
				Map<String, String> groupStatusDetails = determineGroupStatusWithDetails(zones, smsDetailsEntity);
				String groupStatus = groupStatusDetails.get("groupStatus");

				// Filter by the status in the request
				if (!groupStatus.equalsIgnoreCase(status)) {
					return;
				}

				// Add zone data to DTO list
				ZoneReportDTO zoneReportDTO = new ZoneReportDTO();
				if (branch.getParentCode() != null) {
					BranchMasterEntity parent = branchMasterRepository.findBybranchCode(branch.getParentCode());
					if (parent != null) {
						zoneReportDTO.setParentBranch(parent.getBranchDesc());
					}
				}
				zoneReportDTO.setBranchCode(branch.getBranchCode());
				zoneReportDTO.setBank(branch.getBank());
				zoneReportDTO.setBranchName(branch.getBranchDesc());
				zoneReportDTO.setMobile(branch.getMobile());
				zoneReportDTO.setDeviceSerialNo(deviceMasterEntity.getDeviceSerialNo());

				List<ZonesDTO> zonesDTOList = new ArrayList<>();
				zones.forEach(zone -> {
					ZonesDTO zonesDTO = new ZonesDTO();
					zonesDTO.setZone(zone.getZoneOriginalName());
					zonesDTO.setZoneType(zone.getZoneDesiredName());
					zonesDTO.setAlertType(zone.getAlertType());
					zonesDTO.setStatus(groupStatusDetails.get(zone.getZoneOriginalName())); // Set status for individual
																							// zones
					zonesDTOList.add(zonesDTO);
				});

				zoneReportDTO.setZoneDTOList(zonesDTOList);
				zoneReportDTOs.add(zoneReportDTO);
			});

		});

		zoneReportDTOs.sort(Comparator
				.comparing(ZoneReportDTO::getParentBranch, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.thenComparing(ZoneReportDTO::getBranchName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

		response.setPayload(zoneReportDTOs);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully");
		return response;
	}

	private SmsDetailsEntity getLatestSmsForToday(String panelSerialNo) {
		SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(panelSerialNo);

		if (smsDetailsEntity == null) {
			// No record at all → Non Sync
			return null;
		}

		// Check if the record belongs to today
		LocalDate today = LocalDate.now();
		LocalDate smsDate = null;

		if (smsDetailsEntity.getDateTime() != null) {
			// Adjust depending on your field type:
			// If it's java.util.Date:
			smsDate = smsDetailsEntity.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// If it's LocalDateTime, use this instead:
			// smsDate = smsDetailsEntity.getDateTime().toLocalDate();
		}

		if (smsDate == null || !smsDate.isEqual(today)) {
			// Record exists but not today's → Non Sync
			return null;
		}

		// Today's data found
		return smsDetailsEntity;
	}

	public Set<String> getNoInfoDeviceSerials(List<String> serialNos) {
		if (serialNos == null || serialNos.isEmpty()) {
			return Collections.emptySet();
		}

		long thresholdHours = getThresholdHours();
		Date cutoff = new Date(System.currentTimeMillis() - thresholdHours * 60 * 60 * 1000);

		Map<String, SmsDetailsEntity> smsMap = buildSmsMap(serialNos, cutoff);
		Map<String, Boolean> nonSyncMap = buildNonSyncMap(serialNos, cutoff);

		Set<String> noInfoSerials = new HashSet<>();
		for (String serial : serialNos) {
			boolean hasRecentSms = smsMap.containsKey(serial);
			boolean hasActiveOldNonSync = nonSyncMap.getOrDefault(serial, false);

			// Same condition as the overview count's "No Info" bucket
			if (!hasRecentSms && hasActiveOldNonSync) {
				noInfoSerials.add(serial);
			}
		}

		return noInfoSerials;
	}
}