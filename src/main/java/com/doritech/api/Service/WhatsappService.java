package com.doritech.api.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.CallDTO;
import com.doritech.api.DTO.MsisdnList;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.CallLogEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.EscalationMatrix;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Entity.WhatsAppLogs;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.CallLogRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EscalationMatrixRepository;
import com.doritech.api.Repository.SmsDetailsRepository;
import com.doritech.api.Repository.StaticDataRepo;
import com.doritech.api.Repository.VendorMasterRepository;
import com.doritech.api.Repository.WhatsappLogsRepository;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class WhatsappService {

	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	DeviceZoneDetailsRepository deviceZoneRepo;
	@Autowired
	SmsDetailsService smsDetailsService;
	@Autowired
	SmsDetailsRepository smsDetailsRepository;
	@Autowired
	DeviceZoneDetailsRepository zoneRepository;
	@Autowired
	CallLogRepository callLogRepository;
	@Autowired
	EscalationMatrixRepository escalationMatrixRepo;
	@Value("${apiUrl}")
	private String apiUrl;

	@Value("${WhatsappKey}")
	private String WhatsappKey;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	StaticDataRepo staticDataRepo;
	@Autowired
	SocialMediaService socialMediaService;
	@Autowired
	WhatsappLogsRepository whatsAppLogsRepo;

	@Autowired
	VendorMasterRepository vendorRepo;

	public ResponseEntity getDeviceStatusCount(String branchCode)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ResponseEntity response = new ResponseEntity();
		int totalDeviceCount = 0;
		int workingDeviceCount = 0;
		int notWorkingCount = 0;

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
		branchList = branchList == null ? new ArrayList<>() : branchList;

		for (BranchMasterEntity branch : branchList) {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
			if (device != null && !"SIA".equalsIgnoreCase(device.getProductType())) { // 🚨 SIA exclude
				totalDeviceCount++;

				SmsDetailsEntity smsDetails = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());

				if (smsDetails == null) {
					workingDeviceCount++;
				} else {
					boolean isDeviceNotWorking = false;

					List<DeviceZoneDetailsEntity> zoneList = zoneRepository.findAllByDeviceId(device.getDeviceId());

					Map<String, String> zoneFlagMap = new HashMap<>();

					for (DeviceZoneDetailsEntity zone : zoneList) {
						zoneFlagMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag());
					}

					for (String zoneKey : zoneFlagMap.keySet()) {
						String formattedZoneKey = "Zone" + zoneKey.substring(4); // e.g., Zone01 → Zone1
						String zoneValue = (String) smsDetails.getClass().getMethod("get" + formattedZoneKey)
								.invoke(smsDetails);
						String primaryFlag = zoneFlagMap.get(zoneKey);

						if ("Y".equalsIgnoreCase(primaryFlag) && (zoneValue.equalsIgnoreCase("A")
								|| zoneValue.equalsIgnoreCase("I") || zoneValue.equalsIgnoreCase("D"))) {
							isDeviceNotWorking = true;
							break;
						}
					}

					if (isDeviceNotWorking) {
						notWorkingCount++;
					} else {
						workingDeviceCount++;
					}
				}
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("totalDeviceCount", totalDeviceCount);
		result.put("workingDeviceCount", workingDeviceCount);
		result.put("notWorkingCount", notWorkingCount);

		response.setPayload(result);
		response.setMessage("Device count status (excluding SIA) for branch: " + branchCode);
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	// ✅ New API only for SIA devices
	public ResponseEntity getSiaDeviceStatusCount(String branchCode)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ResponseEntity response = new ResponseEntity();
		int totalDeviceCount = 0;
		int workingDeviceCount = 0;
		int notWorkingCount = 0;

		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
		branchList = branchList == null ? new ArrayList<>() : branchList;

		for (BranchMasterEntity branch : branchList) {
			DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());
			if (device != null && "SIA".equalsIgnoreCase(device.getProductType())) { // 🚨 Only SIA
				totalDeviceCount++;

				SmsDetailsEntity smsDetails = smsDetailsRepository.getLatestRecord(device.getDeviceSerialNo());

				if (smsDetails == null) {
					workingDeviceCount++;
				} else {
					boolean isDeviceNotWorking = false;

					List<DeviceZoneDetailsEntity> zoneList = zoneRepository.findAllByDeviceId(device.getDeviceId());

					Map<String, String> zoneFlagMap = new HashMap<>();

					for (DeviceZoneDetailsEntity zone : zoneList) {
						zoneFlagMap.put(zone.getZoneOriginalName(), zone.getPrimaryFlag());
					}

					for (String zoneKey : zoneFlagMap.keySet()) {
						String formattedZoneKey = "Zone" + zoneKey.substring(4);
						String zoneValue = (String) smsDetails.getClass().getMethod("get" + formattedZoneKey)
								.invoke(smsDetails);
						String primaryFlag = zoneFlagMap.get(zoneKey);

						if ("Y".equalsIgnoreCase(primaryFlag) && (zoneValue.equalsIgnoreCase("A")
								|| zoneValue.equalsIgnoreCase("I") || zoneValue.equalsIgnoreCase("D"))) {
							isDeviceNotWorking = true;
							break;
						}
					}

					if (isDeviceNotWorking) {
						notWorkingCount++;
					} else {
						workingDeviceCount++;
					}
				}
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("totalSiaDeviceCount", totalDeviceCount);
		result.put("workingSiaDeviceCount", workingDeviceCount);
		result.put("notWorkingSiaDeviceCount", notWorkingCount);

		response.setPayload(result);
		response.setMessage("SIA Device count status for branch: " + branchCode);
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

//	    public void sendWhatsAppMessageForBranchCodeAndMessageNew(String branchCode, String activeZone, Long escalationLogId) {
//
//	        try {
//
//	            BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
//
//	            List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");
//
//	            // Split alerts
//	            String[] activeZoneArray = activeZone.split(",");
//	            List<String> alerts = new ArrayList<>();
//
//	            for (String zone : activeZoneArray) {
//	                if (zone != null && !zone.trim().isEmpty()) {
//	                    alerts.add(zone.trim());
//	                }
//	            }
//
//	            String alertText = String.join(", ", alerts);
//
//	            List<String> mobileNumbers = new ArrayList<>();
//
//	            if (branchEntity.getMobile() != null && !branchEntity.getMobile().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobile().replaceFirst("^91", ""));
//	            }
//	            if (branchEntity.getMobileSecondary() != null && !branchEntity.getMobileSecondary().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobileSecondary().replaceFirst("^91", ""));
//	            }
//	            if (branchEntity.getMobileTertiary() != null && !branchEntity.getMobileTertiary().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobileTertiary().replaceFirst("^91", ""));
//	            }
//	            if (branchEntity.getMobileQuaternary() != null && !branchEntity.getMobileQuaternary().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobileQuaternary().replaceFirst("^91", ""));
//	            }
//	            if (branchEntity.getMobileQuinary() != null && !branchEntity.getMobileQuinary().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobileQuinary().replaceFirst("^91", ""));
//	            }
//	            if (branchEntity.getMobileSenary() != null && !branchEntity.getMobileSenary().trim().isEmpty()) {
//	                mobileNumbers.add(branchEntity.getMobileSenary().replaceFirst("^91", ""));
//	            }
//
//	            System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);
//
//	            for (Object[] object : staticData) {
//
//	                String whatsappText = String.valueOf(object[2] == null ? "" : object[2]);
//
//	                whatsappText = whatsappText.replaceAll("XNAME", branchEntity.getBranchDesc());
//
//	                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//
//	                String currentDate = dateFormat.format(new Date());
//	                String currentTime = timeFormat.format(new Date());
//
//	                whatsappText = whatsappText.replaceAll("DATE", currentDate);
//	                whatsappText = whatsappText.replaceAll("TIME", currentTime);
//
//	                // replace alert placeholder
//	                whatsappText = whatsappText.replaceAll("YNAME", alertText);
//
//	                System.out.println("ALERTS : " + alertText);
//
//	                for (String whatsappNo : mobileNumbers) {
//	                    sendWhatsAppMessage(branchCode, whatsappNo, whatsappText, escalationLogId);
//	                }
//	            }
//
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    
	public void sendWhatsAppMessageForBranchCodeAndMessageNewForAlarm(String branchCode, String activeZone,
			Long escalationLogId, String escalationLevel) {
		try {
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

			// Split alerts
			String[] activeZoneArray = activeZone.split(",");
			List<String> alerts = new ArrayList<>();
			for (String zone : activeZoneArray) {
				if (zone != null && !zone.trim().isEmpty()) {
					alerts.add(zone.trim());
				}
			}
			String alertText = String.join(", ", alerts);

			// Step 1: Fetch escalation matrix
			EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
					true);
			if (escalationMatrix.getWhatsapp().equals(true)) {
				System.out.println("WHATSAPP escalationMatrix FLAG :: " + escalationMatrix.getWhatsapp());
				List<String> mobileNumbers = new ArrayList<>();
				BranchMasterEntity resolvedBranchEntity = null; // track resolved branch for CALL

				if (escalationMatrix != null) {
					String recipientType = escalationMatrix.getRecipient();

					if ("BRANCH".equalsIgnoreCase(recipientType)) {
						if (branchEntity != null) {
							addMobileNumbersForWhatsapp(mobileNumbers, branchEntity);
							resolvedBranchEntity = branchEntity;
						}

					} else if ("ZONE".equalsIgnoreCase(recipientType)) {
						if (branchEntity != null) {
							if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
								// Already a ZONE → use directly
								addMobileNumbersForWhatsapp(mobileNumbers, branchEntity);
								resolvedBranchEntity = branchEntity;

							} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
								// It's a BRANCH → go ONE level up to ZONE only, HO nahi
								String parentCode = branchEntity.getParentCode();
								if (parentCode != null && !parentCode.trim().isEmpty()) {
									BranchMasterEntity parentBranchEntity = branchMasterRepository
											.findBybranchCode(parentCode);
									if (parentBranchEntity != null
											&& "ZONE".equalsIgnoreCase(parentBranchEntity.getBranchType())) { // ZONE
																												// tak
																												// hi
																												// rok
																												// do
										addMobileNumbersForWhatsapp(mobileNumbers, parentBranchEntity);
										resolvedBranchEntity = parentBranchEntity;
									}
								}
							}
						}
					}
				}

				// Remove duplicates
				mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());

				System.out.println("WHATSAPP MOBILE NUMBERS ARE :: " + mobileNumbers);

				if (mobileNumbers.isEmpty()) {
					System.out.println("No mobile numbers found, skipping WhatsApp/Call for branchCode: " + branchCode);
					return;
				}

				// Step 2: Send WhatsApp
				for (Object[] object : staticData) {
					String whatsappText = String.valueOf(object[2] == null ? "" : object[2]);

					whatsappText = whatsappText.replaceAll("XNAME", branchEntity.getBranchDesc());

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

					String currentDate = dateFormat.format(new Date());
					String currentTime = timeFormat.format(new Date());

					whatsappText = whatsappText.replaceAll("DATE", currentDate);
					whatsappText = whatsappText.replaceAll("TIME", currentTime);
					whatsappText = whatsappText.replaceAll("YNAME", alertText);

					System.out.println("ALERTS : " + alertText);

					for (String whatsappNo : mobileNumbers) {
						sendWhatsAppMessage(branchCode, whatsappNo, whatsappText, escalationLogId);
					}
				}

			} else {
				System.out.println("Escalation level " + escalationLevel
						+ " is not configured for WhatsApp. Skipping WhatsApp for branchCode: " + branchCode);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendWhatsAppMessageForBranchCodeAndMessageNewForAlart(String branchCode, String activeZone,
			Long escalationLogId, String escalationLevel) {
		try {
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

			// ── Step 1: Fetch escalation matrix ──────────────────────────────────
			EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
					false);

			if (escalationMatrix == null) {
				System.out.println("No escalation matrix found for level: " + escalationLevel
						+ " — skipping WhatsApp for branchCode: " + branchCode);
				return;
			}

			if (!Boolean.TRUE.equals(escalationMatrix.getWhatsapp())) {
				System.out.println("WhatsApp flag is OFF for level: " + escalationLevel
						+ " — skipping WhatsApp for branchCode: " + branchCode);
				return;
			}

			System.out.println("WHATSAPP escalationMatrix FLAG :: " + escalationMatrix.getWhatsapp());

			String recipientType = escalationMatrix.getRecipient();

			// ── Step 2: Parse activeZone tokens ("alertType|inputType,...") ───────
			String[] activeZoneArray = activeZone.split(",");
			// Build clean alertText for message body (alertTypeOnly parts only)
			List<String> alertTextList = new ArrayList<>();
			for (String token : activeZoneArray) {
				if (token != null && !token.trim().isEmpty()) {
					String alertTypeOnly = token.trim().split("\\|")[0].trim();
					alertTextList.add(alertTypeOnly);
				}
			}
			String alertText = String.join(", ", alertTextList);

			// ── Step 3: Resolve mobile numbers by recipient type ──────────────────
			List<String> mobileNumbers = new ArrayList<>();

			if ("VENDOR".equalsIgnoreCase(recipientType)) {
				// For VENDOR: pick first token to get zone details → vendor → mobile
				// (vendor is zone-specific; iterate all tokens if you need all vendors)
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
				if (device != null && device.getDeviceId() != null) {
					for (String token : activeZoneArray) {
						if (token == null || token.trim().isEmpty())
							continue;
						String[] parts = token.trim().split("\\|", 2);
						String alertType = parts[0].trim();
						String inputType = parts.length > 1 ? parts[1].trim() : "";

						DeviceZoneDetailsEntity zoneDetails = deviceZoneRepo
								.findByDeviceIdAndAlertTypeAndInputType(device.getDeviceId(), alertType, inputType);

						if (zoneDetails != null && zoneDetails.getVendors() != null
								&& !zoneDetails.getVendors().trim().isEmpty()) {

							Optional<VendorMasterEntity> vendorMaster = vendorRepo
									.findByVendorName(zoneDetails.getVendors());
							if (vendorMaster != null) {
								addVendorMobileNumbersForWhatapp(mobileNumbers, vendorMaster.get());
							} else {
								System.out.println("No vendor found for name: " + zoneDetails.getVendors());
							}
						}
					}
				} else {
					System.out
							.println("No device found for branchCode: " + branchCode + " — skipping VENDOR WhatsApp.");
				}

			} else if ("BRANCH".equalsIgnoreCase(recipientType)) {
				// Send to branch contacts directly
				if (branchEntity != null) {
					addMobileNumbersForWhatsapp(mobileNumbers, branchEntity);
				}

			} else if ("ZONE".equalsIgnoreCase(recipientType)) {
				if (branchEntity != null) {
					if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// Already a ZONE → use directly
						addMobileNumbersForWhatsapp(mobileNumbers, branchEntity);

					} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
						// BRANCH → go ONE level up to ZONE only, not HO
						String parentCode = branchEntity.getParentCode();
						if (parentCode != null && !parentCode.trim().isEmpty()) {
							BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
							if (parentEntity != null && "ZONE".equalsIgnoreCase(parentEntity.getBranchType())) {
								addMobileNumbersForWhatsapp(mobileNumbers, parentEntity);
							}
						}
					}
				}

			} else if ("HEAD OFFICE".equalsIgnoreCase(recipientType)) {
				if (branchEntity != null) {

					if ("HEAD OFFICE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// Already at HEAD OFFICE → use directly
						addMobileNumbersForWhatsapp(mobileNumbers, branchEntity);

					} else if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// ZONE → go ONE level up, check if parent is HEAD OFFICE
						String parentCode = branchEntity.getParentCode();
						if (parentCode != null && !parentCode.trim().isEmpty()) {
							BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
							if (parentEntity != null && "HEAD OFFICE".equalsIgnoreCase(parentEntity.getBranchType())) {
								addMobileNumbersForWhatsapp(mobileNumbers, parentEntity);
							} else {
								System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO WhatsApp.");
							}
						}

					} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
						// BRANCH → go up to ZONE → then up to HEAD OFFICE (two levels)
						String zoneCode = branchEntity.getParentCode();
						if (zoneCode != null && !zoneCode.trim().isEmpty()) {
							BranchMasterEntity zoneEntity = branchMasterRepository.findBybranchCode(zoneCode);
							if (zoneEntity != null && "ZONE".equalsIgnoreCase(zoneEntity.getBranchType())) {
								String hoCode = zoneEntity.getParentCode();
								if (hoCode != null && !hoCode.trim().isEmpty()) {
									BranchMasterEntity hoEntity = branchMasterRepository.findBybranchCode(hoCode);
									if (hoEntity != null && "HEAD OFFICE".equalsIgnoreCase(hoEntity.getBranchType())) {
										addMobileNumbersForWhatsapp(mobileNumbers, hoEntity);
									} else {
										System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO WhatsApp.");
									}
								}
							} else if (zoneEntity != null
									&& "HEAD OFFICE".equalsIgnoreCase(zoneEntity.getBranchType())) {
								addMobileNumbersForWhatsapp(mobileNumbers, zoneEntity);
								System.out.println("Parent of BRANCH is HEAD OFFICE — added HO WHATSAPP.");
							}

						}
					}
				}
			}

			// ── Remove duplicates ─────────────────────────────────────────────────
			mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());
			System.out.println("WHATSAPP MOBILE NUMBERS ARE :: " + mobileNumbers);

			if (mobileNumbers.isEmpty()) {
				System.out.println("No mobile numbers found, skipping WhatsApp for branchCode: " + branchCode);
				return;
			}

			// ── Step 4: Send WhatsApp message ─────────────────────────────────────
			for (Object[] object : staticData) {
				String whatsappText = String.valueOf(object[2] == null ? "" : object[2]);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
				String currentDate = dateFormat.format(new Date());
				String currentTime = timeFormat.format(new Date());

				whatsappText = whatsappText.replaceAll("XNAME",
						branchEntity != null ? branchEntity.getBranchDesc() : "");
				whatsappText = whatsappText.replaceAll("DATE", currentDate);
				whatsappText = whatsappText.replaceAll("TIME", currentTime);
				// alertText already contains only alertTypeOnly parts (no inputType)
				whatsappText = whatsappText.replaceAll("YNAME", alertText);

				System.out.println("ALERTS : " + alertText);

				for (String whatsappNo : mobileNumbers) {
					sendWhatsAppMessage(branchCode, whatsappNo, whatsappText, escalationLogId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCallMessageForBranchCodeAndMessageNewForAlarm(String branchCode, String activeZone,
			Long escalationLogId, String escalationLevel) {
		try {
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

			// Split alerts
			String[] activeZoneArray = activeZone.split(",");
			List<String> alerts = new ArrayList<>();
			for (String zone : activeZoneArray) {
				if (zone != null && !zone.trim().isEmpty()) {
					alerts.add(zone.trim());
				}
			}
			String alertText = String.join(", ", alerts);

			// Step 1: Fetch escalation matrix
			EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
					true);

			List<String> mobileNumbers = new ArrayList<>();
			BranchMasterEntity resolvedBranchEntity = null; // track resolved branch for CALL

			if (escalationMatrix != null) {
				String recipientType = escalationMatrix.getRecipient();

				if ("BRANCH".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {
						addMobileNumbers(mobileNumbers, branchEntity);
						resolvedBranchEntity = branchEntity; //
					}

				} else if ("ZONE".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {
						if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// Already a ZONE → use directly
							addMobileNumbers(mobileNumbers, branchEntity);
							resolvedBranchEntity = branchEntity;

						} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a BRANCH → go ONE level up to ZONE only, HO nahi
							String parentCode = branchEntity.getParentCode();
							if (parentCode != null && !parentCode.trim().isEmpty()) {
								BranchMasterEntity parentBranchEntity = branchMasterRepository
										.findBybranchCode(parentCode);
								if (parentBranchEntity != null
										&& "ZONE".equalsIgnoreCase(parentBranchEntity.getBranchType())) { // ZONE tak hi
																											// rok do
									addMobileNumbers(mobileNumbers, parentBranchEntity);
									resolvedBranchEntity = parentBranchEntity;
								}
							}
						}
					}
				}
			}

			// Remove duplicates
			mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());

			System.out.println("Call MOBILE NUMBERS ARE :: " + mobileNumbers);

			if (mobileNumbers.isEmpty()) {
				System.out.println("No mobile numbers found, skipping call for branchCode: " + branchCode);
				return;
			}

			// Step 3: Send CALL — same resolved numbers, sirf ek baar, loop ke bahar
			try {
				if (escalationMatrix.getCallAlert().equals(true)) {
					System.out.println("CALL escalationMatrix FLAG :: " + escalationMatrix.getCallAlert());
					String resolvedBranchDesc = (resolvedBranchEntity != null) ? resolvedBranchEntity.getBranchDesc()
							: branchEntity.getBranchDesc();

					List<MsisdnList> msisdnListAll = new ArrayList<>();

					for (String number : mobileNumbers) {
						MsisdnList msisdn = new MsisdnList();
						msisdn.setPhoneno(number);
						msisdn.setBranchName(resolvedBranchDesc);
						msisdn.setProductType(resolvedBranchDesc);
						msisdn.setReason(alertText);
						msisdn.setContactNo(number);
						msisdnListAll.add(msisdn);
					}

					CallDTO callDTO = new CallDTO();
					callDTO.setSourcetype("0");
					callDTO.setCustomivr(true);
					callDTO.setCampaigntype("4");
					callDTO.setFiletype("2");
					callDTO.setMsisdnlist(msisdnListAll.toArray(new MsisdnList[0]));

					smsDetailsService.makeCall(callDTO); // sirf ek baar
					System.out.println("CALL dispatched for branchCode: " + branchCode + " | resolvedBranch: "
							+ resolvedBranchDesc + " | numbers: " + mobileNumbers);
				} else {
					System.out.println("Escalation level " + escalationLevel
							+ " is not configured for CALL. Skipping CALL for branchCode: " + branchCode);
				}
			} catch (Exception callEx) {
				System.out.println("CALL failed for branchCode: " + branchCode + " → " + callEx.getMessage());
				callEx.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCallMessageForBranchCodeAndMessageNewForAlart(String branchCode, String activeZone,
			Long escalationLogId, String escalationLevel) {
		try {
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);

			// ── Step 1: Fetch escalation matrix (non-critical → false) ───────────
			EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
					false);

			if (escalationMatrix == null) {
				System.out.println("No escalation matrix found for level: " + escalationLevel
						+ " — skipping CALL for branchCode: " + branchCode);
				return;
			}

			if (!Boolean.TRUE.equals(escalationMatrix.getCallAlert())) {
				System.out.println("CALL flag is OFF for level: " + escalationLevel
						+ " — skipping CALL for branchCode: " + branchCode);
				return;
			}

			System.out.println("CALL escalationMatrix FLAG :: " + escalationMatrix.getCallAlert());

			String recipientType = escalationMatrix.getRecipient();

			// ── Step 2: Parse activeZone tokens ("alertType|inputType,...") ───────
			String[] activeZoneArray = activeZone.split(",");
			List<String> alertTextList = new ArrayList<>();
			for (String token : activeZoneArray) {
				if (token != null && !token.trim().isEmpty()) {
					// ✅ Strip inputType — only alertTypeOnly in call reason text
					String alertTypeOnly = token.trim().split("\\|")[0].trim();
					alertTextList.add(alertTypeOnly);
				}
			}
			String alertText = String.join(", ", alertTextList);

			// ── Step 3: Resolve mobile numbers by recipient type ──────────────────
			List<String> mobileNumbers = new ArrayList<>();
			BranchMasterEntity resolvedBranchEntity = null;

			if ("VENDOR".equalsIgnoreCase(recipientType)) {
				// Look up vendor mobile via zone details for each token
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branchCode);
				if (device != null && device.getDeviceId() != null) {
					for (String token : activeZoneArray) {
						if (token == null || token.trim().isEmpty())
							continue;
						String[] parts = token.trim().split("\\|", 2);
						String alertType = parts[0].trim();
						String inputType = parts.length > 1 ? parts[1].trim() : "";

						DeviceZoneDetailsEntity zoneDetails = deviceZoneRepo
								.findByDeviceIdAndAlertTypeAndInputType(device.getDeviceId(), alertType, inputType);

						if (zoneDetails != null && zoneDetails.getVendors() != null
								&& !zoneDetails.getVendors().trim().isEmpty()) {

							Optional<VendorMasterEntity> vendorMaster = vendorRepo
									.findByVendorName(zoneDetails.getVendors());
							if (vendorMaster != null) {
								addVendorMobileNumbers(mobileNumbers, vendorMaster.get());
							} else {
								System.out.println("No vendor found for name: " + zoneDetails.getVendors());
							}
						}
					}
				} else {
					System.out.println("No device found for branchCode: " + branchCode + " — skipping VENDOR CALL.");
				}
				resolvedBranchEntity = branchEntity;

			} else if ("BRANCH".equalsIgnoreCase(recipientType)) {
				// Send to branch contacts directly
				if (branchEntity != null) {
					addMobileNumbers(mobileNumbers, branchEntity);
					resolvedBranchEntity = branchEntity;
				}

			} else if ("ZONE".equalsIgnoreCase(recipientType)) {
				if (branchEntity != null) {
					if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// Already a ZONE → use directly
						addMobileNumbers(mobileNumbers, branchEntity);
						resolvedBranchEntity = branchEntity;

					} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
						// BRANCH → go ONE level up to ZONE only, not HO
						String parentCode = branchEntity.getParentCode();
						if (parentCode != null && !parentCode.trim().isEmpty()) {
							BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
							if (parentEntity != null && "ZONE".equalsIgnoreCase(parentEntity.getBranchType())) {
								addMobileNumbers(mobileNumbers, parentEntity);
								resolvedBranchEntity = parentEntity;
							}
						}
					}
				}

			} else if ("HEAD OFFICE".equalsIgnoreCase(recipientType)) {
				if (branchEntity != null) {

					if ("HEAD OFFICE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// Already at HEAD OFFICE → use directly
						addMobileNumbers(mobileNumbers, branchEntity);
						resolvedBranchEntity = branchEntity;

					} else if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
						// ZONE → go ONE level up, check if parent is HEAD OFFICE
						String parentCode = branchEntity.getParentCode();
						if (parentCode != null && !parentCode.trim().isEmpty()) {
							BranchMasterEntity parentEntity = branchMasterRepository.findBybranchCode(parentCode);
							if (parentEntity != null && "HEAD OFFICE".equalsIgnoreCase(parentEntity.getBranchType())) {
								addMobileNumbers(mobileNumbers, parentEntity);
								resolvedBranchEntity = parentEntity;
							} else {
								System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO CALL.");
							}
						}

					} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
						// BRANCH → two levels up: BRANCH → ZONE → HEAD OFFICE
						String zoneCode = branchEntity.getParentCode();
						if (zoneCode != null && !zoneCode.trim().isEmpty()) {
							BranchMasterEntity zoneEntity = branchMasterRepository.findBybranchCode(zoneCode);
							if (zoneEntity != null && "ZONE".equalsIgnoreCase(zoneEntity.getBranchType())) {
								String hoCode = zoneEntity.getParentCode();
								if (hoCode != null && !hoCode.trim().isEmpty()) {
									BranchMasterEntity hoEntity = branchMasterRepository.findBybranchCode(hoCode);
									if (hoEntity != null && "HEAD OFFICE".equalsIgnoreCase(hoEntity.getBranchType())) {
										addMobileNumbers(mobileNumbers, hoEntity);
										resolvedBranchEntity = hoEntity;
									} else {
										System.out.println("Parent of ZONE is not HEAD OFFICE — skipping HO CALL.");
									}
								}
							} else if (zoneEntity != null
									&& "HEAD OFFICE".equalsIgnoreCase(zoneEntity.getBranchType())) {
								addMobileNumbers(mobileNumbers, zoneEntity);
								System.out.println("Parent of BRANCH is HEAD OFFICE — added HO CALL.");
							}
						}
					}
				}
			}

			// ── Remove duplicates ─────────────────────────────────────────────────
			mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());
			System.out.println("CALL MOBILE NUMBERS ARE :: " + mobileNumbers);

			if (mobileNumbers.isEmpty()) {
				System.out.println("No mobile numbers found, skipping CALL for branchCode: " + branchCode);
				return;
			}

			// ── Step 4: Dispatch CALL ─────────────────────────────────────────────
			try {
				String resolvedBranchDesc = (resolvedBranchEntity != null) ? resolvedBranchEntity.getBranchDesc()
						: (branchEntity != null ? branchEntity.getBranchDesc() : "");

				List<MsisdnList> msisdnListAll = new ArrayList<>();
				for (String number : mobileNumbers) {
					MsisdnList msisdn = new MsisdnList();
					msisdn.setPhoneno(number);
					msisdn.setBranchName(resolvedBranchDesc);
					msisdn.setProductType(resolvedBranchDesc);
					// alertText already has only alertTypeOnly parts (no inputType)
					msisdn.setReason(alertText);
					msisdn.setContactNo(number);
					msisdnListAll.add(msisdn);
				}

				CallDTO callDTO = new CallDTO();
				callDTO.setSourcetype("0");
				callDTO.setCustomivr(true);
				callDTO.setCampaigntype("4");
				callDTO.setFiletype("2");
				callDTO.setMsisdnlist(msisdnListAll.toArray(new MsisdnList[0]));

				smsDetailsService.makeCall(callDTO);
				System.out.println("CALL dispatched for branchCode: " + branchCode + " | resolvedBranch: "
						+ resolvedBranchDesc + " | numbers: " + mobileNumbers);

			} catch (Exception callEx) {
				System.out.println("CALL failed for branchCode: " + branchCode + " → " + callEx.getMessage());
				callEx.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendWhatsAppMessageForBranchCodeAndMessageNewForAlert(String branchCode, String activeZone,
			Long escalationLogId, String escalationLevel) {
		try {
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

			// Split alerts
			String[] activeZoneArray = activeZone.split(",");
			List<String> alerts = new ArrayList<>();
			for (String zone : activeZoneArray) {
				if (zone != null && !zone.trim().isEmpty()) {
					alerts.add(zone.trim());
				}
			}
			String alertText = String.join(", ", alerts);

			// Step 1: Fetch escalation matrix
			EscalationMatrix escalationMatrix = escalationMatrixRepo.findByEscLevelAndCriticalFlag(escalationLevel,
					true);

			List<String> mobileNumbers = new ArrayList<>();
			BranchMasterEntity resolvedBranchEntity = null; // track resolved branch for CALL

			if (escalationMatrix != null) {
				String recipientType = escalationMatrix.getRecipient();

				if ("BRANCH".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {
						addMobileNumbers(mobileNumbers, branchEntity);
						resolvedBranchEntity = branchEntity; //
					}

				} else if ("ZONE".equalsIgnoreCase(recipientType)) {
					if (branchEntity != null) {
						if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
							// Already a ZONE → use directly
							addMobileNumbers(mobileNumbers, branchEntity);
							resolvedBranchEntity = branchEntity;

						} else if ("BRANCH".equalsIgnoreCase(branchEntity.getBranchType())) {
							// It's a BRANCH → go ONE level up to ZONE only, HO nahi
							String parentCode = branchEntity.getParentCode();
							if (parentCode != null && !parentCode.trim().isEmpty()) {
								BranchMasterEntity parentBranchEntity = branchMasterRepository
										.findBybranchCode(parentCode);
								if (parentBranchEntity != null
										&& "ZONE".equalsIgnoreCase(parentBranchEntity.getBranchType())) { // ZONE tak hi
																											// rok do
									addMobileNumbers(mobileNumbers, parentBranchEntity);
									resolvedBranchEntity = parentBranchEntity;
								}
							}
						}
					}
				}
			}

			// Remove duplicates
			mobileNumbers = mobileNumbers.stream().distinct().collect(Collectors.toList());

			System.out.println("WHATSAPP MOBILE NUMBERS ARE :: " + mobileNumbers);

			if (mobileNumbers.isEmpty()) {
				System.out.println("No mobile numbers found, skipping WhatsApp/Call for branchCode: " + branchCode);
				return;
			}

			// Step 2: Send WhatsApp
			for (Object[] object : staticData) {
				String whatsappText = String.valueOf(object[2] == null ? "" : object[2]);

				whatsappText = whatsappText.replaceAll("XNAME", branchEntity.getBranchDesc());

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

				String currentDate = dateFormat.format(new Date());
				String currentTime = timeFormat.format(new Date());

				whatsappText = whatsappText.replaceAll("DATE", currentDate);
				whatsappText = whatsappText.replaceAll("TIME", currentTime);
				whatsappText = whatsappText.replaceAll("YNAME", alertText);

				System.out.println("ALERTS : " + alertText);

				for (String whatsappNo : mobileNumbers) {
					sendWhatsAppMessage(branchCode, whatsappNo, whatsappText, escalationLogId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private void addVendorMobileNumbersForWhatapp(List<String> mobileNumbers, VendorMasterEntity vendorMaster) {
		if (vendorMaster.getMobile() != null && !vendorMaster.getMobile().trim().isEmpty()) {

			String mobile = vendorMaster.getMobile().trim();

			if (mobile.length() == 10) {
				mobile = "91" + mobile;
			}

			mobileNumbers.add(mobile);
		}
	}

	/*
	 * 
	 * last working code in testing public void
	 * sendWhatsAppMessageForBranchCodeAndMessageNew(String branchCode, String
	 * activeZone, Long escalationLogId, String escalationLevel) { try {
	 * BranchMasterEntity branchEntity =
	 * branchMasterRepository.findBybranchCode(branchCode); List<Object[]>
	 * staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");
	 * 
	 * // Split alerts String[] activeZoneArray = activeZone.split(",");
	 * List<String> alerts = new ArrayList<>(); for (String zone : activeZoneArray)
	 * { if (zone != null && !zone.trim().isEmpty()) { alerts.add(zone.trim()); } }
	 * String alertText = String.join(", ", alerts);
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
	 * if ("BRANCH".equalsIgnoreCase(recipientType)) { if (branchEntity != null) {
	 * addMobileNumbers(mobileNumbers, branchEntity); }
	 * 
	 * } else if ("ZONE".equalsIgnoreCase(recipientType)) { if (branchEntity !=
	 * null) { if ("ZONE".equalsIgnoreCase(branchEntity.getBranchType())) {
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
	 * System.out.println("WHATSAPP MOBILE NUMBERS ARE :: " + mobileNumbers);
	 * 
	 * if (mobileNumbers.isEmpty()) { System.out.
	 * println("No mobile numbers found, skipping WhatsApp for branchCode: " +
	 * branchCode); return; }
	 * 
	 * // ✅ Step 2: Build message and send WhatsApp for (Object[] object :
	 * staticData) { String whatsappText = String.valueOf(object[2] == null ? "" :
	 * object[2]);
	 * 
	 * whatsappText = whatsappText.replaceAll("XNAME",
	 * branchEntity.getBranchDesc());
	 * 
	 * SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	 * SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	 * 
	 * String currentDate = dateFormat.format(new Date()); String currentTime =
	 * timeFormat.format(new Date());
	 * 
	 * whatsappText = whatsappText.replaceAll("DATE", currentDate); whatsappText =
	 * whatsappText.replaceAll("TIME", currentTime); whatsappText =
	 * whatsappText.replaceAll("YNAME", alertText);
	 * 
	 * System.out.println("ALERTS : " + alertText);
	 * 
	 * for (String whatsappNo : mobileNumbers) { sendWhatsAppMessage(branchCode,
	 * whatsappNo, whatsappText, escalationLogId); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
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

	private String formatMobileForWhatsapp(String mobile) {
		if (mobile == null)
			return null;

		mobile = mobile.trim();

		if (mobile.startsWith("91") && mobile.length() == 12) {
			return mobile; // already correct
		}

		if (mobile.length() == 10) {
			return "91" + mobile; // add country code
		}

		return mobile; // return as it is for other cases
	}

	private void addMobileNumbersForWhatsapp(List<String> mobileNumbers, BranchMasterEntity branchEntity) {

		if (branchEntity.getMobile() != null && !branchEntity.getMobile().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobile()));
		}
		if (branchEntity.getMobileSecondary() != null && !branchEntity.getMobileSecondary().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobileSecondary()));
		}
		if (branchEntity.getMobileTertiary() != null && !branchEntity.getMobileTertiary().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobileTertiary()));
		}
		if (branchEntity.getMobileQuaternary() != null && !branchEntity.getMobileQuaternary().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobileQuaternary()));
		}
		if (branchEntity.getMobileQuinary() != null && !branchEntity.getMobileQuinary().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobileQuinary()));
		}
		if (branchEntity.getMobileSenary() != null && !branchEntity.getMobileSenary().trim().isEmpty()) {
			mobileNumbers.add(formatMobileForWhatsapp(branchEntity.getMobileSenary()));
		}
	}

	public void sendWhatsAppMessageForBranchCodeAndMessage(String branchCode, String activeZone, Long escalationLogId) {
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
			System.out.println("MOBILE NUMBERS ARE :: " + mobileNumbers);

//            org.springframework.http.ResponseEntity<List<SocialMediaDTO>> socialMediaList = socialMediaService.getSocialMediaDetailsByBranchCode(branchCode);
//            if (socialMediaList == null || socialMediaList.getStatusCode() == HttpStatus.NO_CONTENT || socialMediaList.getBody() == null) {
//                System.out.println("Data not present in social media report with branchCode: " + branchCode);
//                return;
//            }
//
//            @SuppressWarnings("unchecked")
//            List<SocialMediaDTO> responseList = (List<SocialMediaDTO>) socialMediaList.getBody();
//            if (responseList != null && !responseList.isEmpty()) {
//                for (String activeZoneValue : zoneValuesForValidation) {
//                    activeZoneValue = activeZoneValue.trim();
//                    for (SocialMediaDTO socialMediaDTO : responseList) {
//                        String alertType = socialMediaDTO.getAlertType();
//                        String whatsapp = socialMediaDTO.getWhatsapp();
//                        if (activeZoneValue.equals(alertType) && "Y".equalsIgnoreCase(whatsapp)) {
//                            System.out.println("Active zone value: " + activeZoneValue + " WhatsApp status: " + whatsapp);
//                            matchedAlertTypes += alertType + ", ";
//                        }
//                    }
//                }
//
//                if (matchedAlertTypes.length() > 0) {
//                    matchedAlertTypes = matchedAlertTypes.substring(0, matchedAlertTypes.length() - 2);
//                }

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
				if (escalationLogId == 0L) {
					escalationLogId = 0L;
				}
				for (String whatsappNo : mobileNumbers) {
					sendWhatsAppMessage(branchCode, whatsappNo, whatsappText, escalationLogId);
				}
			}
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendWhatsAppMessage(String branchCode, String whatsappNo, String whatsappText, Long escalationLogId)
			throws IOException {
		System.out.println("NUMBER ::: " + whatsappNo + "  TEXT ::::: " + whatsappText);
		String encodedMessage = URLEncoder.encode(whatsappText, "UTF-8");
		String urlString = "https://wasmsapi.com/api/sendText?token=" + WhatsappKey + "&phone=" + whatsappNo
				+ "&message=" + encodedMessage;

		URL url = new URL(urlString);
		System.out.println("URL : " + url);
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

		String responseStr = response.toString();

		// ✅ Save log in DB
		WhatsAppLogs log = new WhatsAppLogs();
		log.setPhoneNo(whatsappNo);
		log.setWhatsappMessage(whatsappText); // readable message, not encoded
		log.setStatus(responseCode == 200 ? "S" : "F"); // S=Success, F=Fail
		log.setResponseMessage(responseStr);
		log.setUrl(urlString);
		log.setBranchCode(branchCode);
		if (escalationLogId != 0L) {

			log.setEscalationLogId(escalationLogId);
		}
		BranchMasterEntity branchMasterEntity = branchMasterRepository.findBybranchCode(branchCode);

		BranchMasterEntity parentBranchEntity = (branchMasterEntity.getParentCode() != null)
				? branchMasterRepository.findBybranchCode(branchMasterEntity.getParentCode())
				: null;

		if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
			log.setTicketId(branchMasterEntity.getBranchDesc() + " - " + parentBranchEntity.getBranchDesc());
		} else {
			log.setTicketId(branchMasterEntity.getBranchDesc());
		}

		whatsAppLogsRepo.save(log);

		System.out.println("Response Code : " + responseCode);
		System.out.println("Response : " + response.toString());
	}

	/*
	 * public ResponseEntity getWhatsappLogsByBranchCode(String branchCode) {
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * try { List<WhatsAppLogs> responseList = new ArrayList<>();
	 * 
	 * ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode);
	 * 
	 * if (branchResponseEntity == null || branchResponseEntity.getPayload() ==
	 * null) { response.setMessage("No branches found for this branch code: " +
	 * branchCode); response.setStatusCode(HttpStatus.NOT_FOUND.value()); return
	 * response; }
	 * 
	 * @SuppressWarnings("unchecked") List<BranchMasterEntity> branchList =
	 * (List<BranchMasterEntity>) branchResponseEntity.getPayload();
	 * 
	 * if (branchList == null || branchList.isEmpty()) {
	 * response.setMessage("No branches found for this branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * // ✅ Collect branch codes List<String> branchCodes = branchList.stream()
	 * .map(BranchMasterEntity::getBranchCode) .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // ✅ Fetch all logs in one query (better than loop) List<WhatsAppLogs>
	 * whatsAppLogs = whatsAppLogsRepo.findByBranchCodeIn(branchCodes);
	 * 
	 * if (whatsAppLogs == null || whatsAppLogs.isEmpty()) { response.
	 * setMessage("No WhatsApp log details found for the given branch code(s).");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(Collections.emptyList()); } else {
	 * responseList.sort(Comparator.comparing(WhatsAppLogs::getTimestamp).reversed()
	 * ); response.setMessage("WhatsApp log details found successfully!");
	 * response.setPayload(whatsAppLogs);
	 * response.setStatusCode(HttpStatus.OK.value()); }
	 * 
	 * } catch (Exception e) { response.setMessage("Error: " + e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); }
	 * 
	 * return response; }
	 */

	public ResponseEntity getWhatsappLogsByBranchCode(String branchCode, int page, int size, boolean downloadExcel) {

		ResponseEntity response = new ResponseEntity();

		try {

			ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);

			if (branchResponseEntity == null || branchResponseEntity.getPayload() == null) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.filter(Objects::nonNull).collect(Collectors.toList());
			if (downloadExcel) {
				List<WhatsAppLogs> whatsAppLogs = whatsAppLogsRepo.findByBranchCodeInOrderByTimestampDesc(branchCodes);
				if (whatsAppLogs.isEmpty()) {
					response.setMessage("No WhatsApp log details found for the given branch code(s).");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setPayload(null);
					response.setCurrentPage(0);
					response.setTotalElements(0);
					response.setTotalPages(0);
					return response;
				}

				response.setMessage("WhatsApp log details found successfully!");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(whatsAppLogs);
				response.setCurrentPage(0);
				response.setTotalElements(whatsAppLogs.size());
				response.setTotalPages(1);

				return response;
			} else {

				Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

				Page<WhatsAppLogs> whatsAppPage = whatsAppLogsRepo.findByBranchCodeIn(branchCodes, pageable);

				if (whatsAppPage.isEmpty()) {
					response.setMessage("No WhatsApp log details found for the given branch code(s).");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setPayload(Collections.emptyList());
					response.setCurrentPage(0);
					response.setTotalElements(0);
					response.setTotalPages(0);
					return response;
				}

				response.setMessage("WhatsApp log details found successfully!");
				response.setStatusCode(HttpStatus.OK.value());

				// Records
				response.setPayload(whatsAppPage.getContent());

				// Pagination Details
				response.setCurrentPage(whatsAppPage.getNumber());
				response.setTotalElements(whatsAppPage.getTotalElements());
				response.setTotalPages(whatsAppPage.getTotalPages());

			}
		} catch (Exception e) {

			response.setMessage("Error: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(Collections.emptyList());
			response.setCurrentPage(0);
			response.setTotalElements(0);
			response.setTotalPages(0);
		}

		return response;
	}

	/*
	 * public ResponseEntity getCallLogsByBranchCode(String branchCode) {
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * try { List<WhatsAppLogs> responseList = new ArrayList<>();
	 * 
	 * ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode);
	 * 
	 * if (branchResponseEntity == null || branchResponseEntity.getPayload() ==
	 * null) { response.setMessage("No branches found for this branch code: " +
	 * branchCode); response.setStatusCode(HttpStatus.NOT_FOUND.value()); return
	 * response; }
	 * 
	 * @SuppressWarnings("unchecked") List<BranchMasterEntity> branchList =
	 * (List<BranchMasterEntity>) branchResponseEntity.getPayload();
	 * 
	 * if (branchList == null || branchList.isEmpty()) {
	 * response.setMessage("No branches found for this branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * // ✅ Collect branch codes List<String> branchCodes = branchList.stream()
	 * .map(BranchMasterEntity::getBranchCode) .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // ✅ Fetch all logs in one query (better than loop) List<CallLogEntity>
	 * callLogs = callLogRepository.findByBranchCodeIn(branchCodes);
	 * 
	 * if (callLogs == null || callLogs.isEmpty()) {
	 * response.setMessage("No call log details found for the given branch code(s)."
	 * ); response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(Collections.emptyList()); } else {
	 * 
	 * callLogs.sort(Comparator.comparing(CallLogEntity::getCreatedAt).reversed());
	 * response.setMessage("call log details found successfully!");
	 * response.setPayload(callLogs); response.setStatusCode(HttpStatus.OK.value());
	 * }
	 * 
	 * } catch (Exception e) { response.setMessage("Error: " + e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); }
	 * 
	 * return response; }
	 */
	public ResponseEntity getCallLogsByBranchCode(String branchCode, int page, int size, boolean downloadExcel) {

		ResponseEntity response = new ResponseEntity();

		try {

			ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);

			if (branchResponseEntity == null || branchResponseEntity.getPayload() == null) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.filter(Objects::nonNull).collect(Collectors.toList());

			// Excel Download
			if (downloadExcel) {

				List<CallLogEntity> callLogs = callLogRepository.findByBranchCodeInOrderByCreatedAtDesc(branchCodes);

				if (callLogs.isEmpty()) {
					response.setMessage("No call log details found for the given branch code(s).");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setPayload(Collections.emptyList());
					response.setCurrentPage(0);
					response.setTotalElements(0);
					response.setTotalPages(0);
					return response;
				}

				response.setMessage("Call log details found successfully!");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(callLogs);
				response.setCurrentPage(0);
				response.setTotalElements(callLogs.size());
				response.setTotalPages(1);

				return response;
			}

			// Pagination
			Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

			Page<CallLogEntity> callLogPage = callLogRepository.findByBranchCodeIn(branchCodes, pageable);

			if (callLogPage.isEmpty()) {
				response.setMessage("No call log details found for the given branch code(s).");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			response.setMessage("Call log details found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(callLogPage.getContent());
			response.setCurrentPage(callLogPage.getNumber());
			response.setTotalElements(callLogPage.getTotalElements());
			response.setTotalPages(callLogPage.getTotalPages());

		} catch (Exception e) {

			response.setMessage("Error: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(Collections.emptyList());
			response.setCurrentPage(0);
			response.setTotalElements(0);
			response.setTotalPages(0);
		}

		return response;
	}

	public void sendPdfUsingOkHttp(String filePath) {
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

				// MediaType for PDF
				MediaType mediaType = MediaType.parse("application/pdf");

				RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
						.addFormDataPart("file", file.getName(), // send actual filename
								RequestBody.create(mediaType, file))
						.build();

				// WhatsApp API URL
				String url = "https://wasmsapi.com/api/sendFiles?token=" + WhatsappKey + "&phone=" + mobileNumber;

				Request request = new Request.Builder().url(url).post(body).build();

				try (Response response = client.newCall(request).execute()) {
					System.out.println("Response Code: " + response.code() + " for number: " + mobileNumber);
					if (response.body() != null) {
						System.out.println("Response Body: " + response.body().string());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
