package com.doritech.api.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.TicketTrackingDTO;
import com.doritech.api.DTO.ZoneReportDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.EscalationMatrix;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;
import com.doritech.api.Entity.Ticket;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.EscalationMatrixRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.SmsDetailsHistoryRepository;
import com.doritech.api.Repository.SmsDetailsRepository;
import com.doritech.api.Repository.TicketRepository;
import com.doritech.api.Repository.VendorMasterRepository;

@Service
public class TicketTrackingService {

	private static final Logger log = LoggerFactory.getLogger(TicketTrackingService.class);
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

	private final TicketRepository ticketRepository;
	private final SmsDetailsRepository smsDetailsRepository;
	private final SmsDetailsHistoryRepository smsDetailsHistoryRepository;
	private final DeviceMasterRepository deviceMasterRepository;
	private final DeviceZoneDetailsRepository deviceZoneDetailsRepository;
	private final BranchMasterRepository branchMasterRepository;
	private final EscalationMailLogRepository escalationLogRepository;
	private final EscalationMatrixRepository escalationMatrixRepository;
	private final EmailLogRepo emailLogRepository;
	private final BranchMasterService branchMasterService;
	private final EscalationMailService escalationMailService;
	private final SmsDetailsService smsDetailsService;
	private final VendorMasterRepository vendorMasterRepository; // ← New repository for vendor_master
	private final ParamRepository paramRepository;

	public TicketTrackingService(TicketRepository ticketRepository, SmsDetailsRepository smsDetailsRepository,
			SmsDetailsHistoryRepository smsDetailsHistoryRepository, DeviceMasterRepository deviceMasterRepository,
			DeviceZoneDetailsRepository deviceZoneDetailsRepository, BranchMasterRepository branchMasterRepository,
			EscalationMailLogRepository escalationLogRepository, EscalationMatrixRepository escalationMatrixRepository,
			EmailLogRepo emailLogRepository, BranchMasterService branchMasterService,
			EscalationMailService escalationMailService, SmsDetailsService smsDetailsService,
			VendorMasterRepository vendorMasterRepository,ParamRepository paramRepository) {

		this.ticketRepository = ticketRepository;
		this.smsDetailsRepository = smsDetailsRepository;
		this.smsDetailsHistoryRepository = smsDetailsHistoryRepository;
		this.deviceMasterRepository = deviceMasterRepository;
		this.deviceZoneDetailsRepository = deviceZoneDetailsRepository;
		this.branchMasterRepository = branchMasterRepository;
		this.escalationLogRepository = escalationLogRepository;
		this.escalationMatrixRepository = escalationMatrixRepository;
		this.emailLogRepository = emailLogRepository;
		this.branchMasterService = branchMasterService;
		this.escalationMailService = escalationMailService;
		this.smsDetailsService = smsDetailsService;
		this.vendorMasterRepository = vendorMasterRepository; // ← Initialize the new repository
		this.paramRepository = paramRepository;		}

	// =========================================================================
	// RESOLVE SMS DETAIL — primary first, then history fallback
	// =========================================================================
	private SmsDetailsEntity resolveSmsDetail(Long alertGeneratedId) {
		if (alertGeneratedId == null) {
			log.warn("[SMS-RESOLVE] alertGeneratedId is null.");
			return null;
		}

		int id = Math.toIntExact(alertGeneratedId);

		// ── Step 1: Primary table ─────────────────────────────────────────────
		try {
			Optional<SmsDetailsEntity> primaryOpt = smsDetailsRepository.findById(id);
			// findById returns empty Optional if not found — NO exception thrown
			if (primaryOpt.isPresent()) {
				log.debug("[SMS-RESOLVE] Found in sms_details for id={}", id);
				return primaryOpt.get();
			}
			log.info("[SMS-RESOLVE] id={} not in sms_details. Trying history...", id);
		} catch (Exception e) {
			log.warn("[SMS-RESOLVE] Primary lookup failed for id={}: {}", id, e.getMessage());
			// Still fall through to history
		}

		// ── Step 2: Fallback — history table ─────────────────────────────────
		try {
			Optional<SmsDetailsHistoryEntity> historyOpt = smsDetailsHistoryRepository.findByOriginalId(id);

			if (historyOpt.isPresent()) {
				SmsDetailsHistoryEntity h = historyOpt.get();
				log.info("[SMS-RESOLVE] Found in sms_details_history for ORIGINAL_ID={}", id);

				SmsDetailsEntity mapped = new SmsDetailsEntity();
				mapped.setId(h.getOriginalId());
				mapped.setPanelSerialNo(h.getPanelSerialNo());
				mapped.setDateTime(h.getDateTime());
				mapped.setSenderMobileNo(h.getSenderMobileNo());
				mapped.setCompleteMessage(h.getCompleteMessage());
				mapped.setBroadcastFlag(h.getBroadcastFlag());
				return mapped;
			}

			log.warn("[SMS-RESOLVE] id={} not found in sms_details OR sms_details_history.", id);

		} catch (Exception e) {
			log.error("[SMS-RESOLVE] History lookup failed for id={}: {}", id, e.getMessage());
		}

		return null;
	}

	// =========================================================================
	// PUBLIC — Get all open ticket tracking data
	// =========================================================================
	public List<TicketTrackingDTO> getOpenTicketTracking(String zoneDesiredName, String loggedInBranch) {

		// ========================================================================
		// STEP 1: Get ALL branches (instead of hierarchy based)
		// ========================================================================
		List<String> allBranchCodes = new ArrayList<>();

		try {
			// Option 1: Fetch all branches from branch_master
			List<BranchMasterEntity> allBranches = branchMasterRepository.findAll();
			allBranchCodes = allBranches.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
					.collect(Collectors.toList());

			log.info("[TRACKING] Total branches found: {}", allBranchCodes.size());

		} catch (Exception e) {
			log.error("[TRACKING] Failed to fetch all branches: {}", e.getMessage(), e);
			return new ArrayList<>(); // Return empty if no branches found
		}

		// ========================================================================
		// STEP 2: Get ALL panel serial numbers from ALL devices
		// ========================================================================
		Set<String> allPanelSerialNos = new HashSet<>();

		try {
			// Fetch all devices
			List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAll();

			for (DeviceMasterEntity device : allDevices) {
				if (device.getDeviceSerialNo() != null && !device.getDeviceSerialNo().isBlank()) {
					allPanelSerialNos.add(device.getDeviceSerialNo());
				}
			}

			log.info("[TRACKING] Total unique panel serial numbers found: {}", allPanelSerialNos.size());

		} catch (Exception e) {
			log.error("[TRACKING] Failed to fetch devices: {}", e.getMessage(), e);
			return new ArrayList<>();
		}

		// ========================================================================
		// STEP 3: Get ALL open tickets for these panel serial numbers
		// ========================================================================
		List<Ticket> allOpenTickets = new ArrayList<>();

		if (!allPanelSerialNos.isEmpty()) {
			try {
				// Query to find tickets with status 'O' and device_serial_no in the list
				allOpenTickets = ticketRepository.findOpenTicketsBydeviceSerialNos(allPanelSerialNos);

				log.info("[TRACKING] Total open tickets found across all branches: {}", allOpenTickets.size());

			} catch (Exception e) {
				log.error("[TRACKING] Failed to fetch open tickets: {}", e.getMessage(), e);
				return new ArrayList<>();
			}
		}

		// ========================================================================
		// STEP 4: For backward compatibility, create a set of allowed branch codes
		// containing ALL branches (so no filtering happens)
		// ========================================================================
		Set<String> allowedBranchCodes = new HashSet<>(allBranchCodes);

		log.info("[TRACKING] Processing tickets for ALL branches (total: {})", allowedBranchCodes.size());

		// ========================================================================
		// STEP 5: Build DTO for each open ticket
		// ========================================================================
		List<TicketTrackingDTO> result = new ArrayList<>();

		for (Ticket ticket : allOpenTickets) {
			try {
				String ticketNo = ticket.getTicketNo();
				TicketTrackingDTO dto = buildDTO(ticketNo, zoneDesiredName, allowedBranchCodes);
				if (dto != null) {
					result.add(dto);
				}
			} catch (Exception e) {
				log.error("[TRACKING] Failed for ticket '{}': {}", ticket.getTicketNo(), e.getMessage(), e);
			}
		}

		log.info("[TRACKING] Final result count: {}", result.size());
		return result;
	}

	// =========================================================================
	// EXTRACT ALL BRANCH CODES FROM HIERARCHY (RECURSIVE)
	// =========================================================================
	@SuppressWarnings("unchecked")
	private Set<String> extractAllBranchCodes(List<?> branches) {
		Set<String> codes = new HashSet<>();
		if (branches == null)
			return codes;

		for (Object item : branches) {
			if (item instanceof Map) {
				Map<String, Object> branch = (Map<String, Object>) item;
				Object code = branch.get("branchCode");
				if (code != null && !code.toString().isBlank())
					codes.add(code.toString());
				Object children = branch.get("children");
				if (children instanceof List<?>)
					codes.addAll(extractAllBranchCodes((List<?>) children));
			} else {
				try {
					java.lang.reflect.Method getCode = item.getClass().getMethod("getBranchCode");
					java.lang.reflect.Method getChildren = item.getClass().getMethod("getChildren");
					Object code = getCode.invoke(item);
					if (code != null && !code.toString().isBlank())
						codes.add(code.toString());
					Object children = getChildren.invoke(item);
					if (children instanceof List<?>)
						codes.addAll(extractAllBranchCodes((List<?>) children));
				} catch (Exception e) {
					log.warn("[TRACKING] Could not extract branchCode from: {}", item.getClass().getName());
				}
			}
		}
		return codes;
	}

	// =========================================================================
	// BUILD DTO
	// =========================================================================
	private TicketTrackingDTO buildDTO(String ticketNo, String zoneDesiredName, Set<String> allowedBranchCodes) {

		List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
		if (allRows.isEmpty())
			return null;

		Ticket openRow = allRows.stream().filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
				.filter(r -> r.getResolvedType() == null || r.getResolvedType().isBlank()).findFirst()
				.orElse(allRows.get(0));

		Long alertGeneratedId = openRow.getAlertGeneratedId();

// ── sms_details with fallback to history ──────────────────────────────
		SmsDetailsEntity smsDetail = null;
		String dtFirstInfo = "N/A";
		String panelSerialNo = null;
		LocalDateTime alertDateTime = null;

		try {
			smsDetail = resolveSmsDetail(alertGeneratedId);
			if (smsDetail != null) {
				panelSerialNo = smsDetail.getPanelSerialNo();
				if (smsDetail.getDateTime() != null) {
					alertDateTime = smsDetail.getDateTime().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();
					dtFirstInfo = alertDateTime.format(DATE_FMT);
				}
			} else {
				log.warn("[TRACKING] sms_detail not found for alertId={} in primary or history", alertGeneratedId);
			}
		} catch (Exception e) {
			log.warn("[TRACKING] sms_detail lookup error for alertId={}: {}", alertGeneratedId, e.getMessage());
		}

// ── device_master ─────────────────────────────────────────────────────
		DeviceMasterEntity device = null;
		String branchCode = null;
		String productType = "N/A";

		if (panelSerialNo != null) {
			try {
				device = deviceMasterRepository.getByDeviceSerialNo(panelSerialNo);
				if (device != null) {
					branchCode = device.getBranchCode();
					productType = device.getProductType() != null ? device.getProductType() : "N/A";
				}
			} catch (Exception e) {
				log.warn("[TRACKING] device not found for panel='{}'", panelSerialNo);
			}
		}

// ── Hierarchy check ───────────────────────────────────────────────────
		if (!allowedBranchCodes.isEmpty()) {
			if (branchCode == null || !allowedBranchCodes.contains(branchCode)) {
				log.info("[TRACKING] Skipping '{}' — branch '{}' not in hierarchy", ticketNo, branchCode);
				return null;
			}
		}

// ── branch_master ─────────────────────────────────────────────────────
		BranchMasterEntity branch = null;
		String branchName = "N/A";
		String branchAddress = "N/A";

		if (branchCode != null) {
			try {
				branch = branchMasterRepository.findBybranchCode(branchCode);
				if (branch != null) {
					branchName = branch.getBranchDesc() != null ? branch.getBranchDesc() : "N/A";
					branchAddress = branch.getAddress1() != null ? branch.getAddress1() : "N/A";
				}
			} catch (Exception e) {
				log.warn("[TRACKING] branch not found for code='{}'", branchCode);
			}
		}

// ── zone_details ──────────────────────────────────────────────────────
		String alertType = "N/A";
		String zoneDesireNameFromDB = null;
		String vendors = null;
		String vendorId = null; // ← New: Store vendor ID from zone_details

		if (device != null) {
			try {
				String zoneNumber = "ZONE" + openRow.getZone();
				DeviceZoneDetailsEntity zoneDetail = deviceZoneDetailsRepository
						.findByDeviceIdAndZoneOriginalName(device.getDeviceId(), zoneNumber);
				if (zoneDetail != null) {
					alertType = zoneDetail.getAlertType() != null ? zoneDetail.getAlertType() : "N/A";
					zoneDesireNameFromDB = zoneDetail.getZoneDesiredName();
					vendors = zoneDetail.getVendors();
					vendorId = zoneDetail.getVendorId(); // ← Get vendor ID from zone_details
				}
			} catch (Exception e) {
				log.warn("[TRACKING] zone detail lookup failed for ticket='{}'", ticketNo);
			}
		}

// ── Zone filter ───────────────────────────────────────────────────────
		if (zoneDesiredName != null && !zoneDesiredName.isBlank()) {
			if (zoneDesireNameFromDB == null || zoneDesireNameFromDB.isBlank()
					|| !zoneDesiredName.trim().equalsIgnoreCase(zoneDesireNameFromDB.trim())) {
				log.info("[TRACKING] Skipping '{}' — zone mismatch. param='{}', db='{}'", ticketNo, zoneDesiredName,
						zoneDesireNameFromDB);
				return null;
			}
			alertType = zoneDesireNameFromDB;
		}

// ── Vendor name from vendor_master using vendorId ─────────────────────
		String vendorName = "N/A";
		if (vendorId != null) {
			try {
// Assuming you have a VendorMasterRepository
				VendorMasterEntity vendor = vendorMasterRepository.findById(vendorId).orElse(null);
				if (vendor != null) {
					vendorName = vendor.getVendorName() != null ? vendor.getVendorName() : "N/A";
					log.info("[TRACKING] Vendor found: id={}, name='{}' for ticket='{}'", vendorId, vendorName,
							ticketNo);
				} else {
					log.warn("[TRACKING] No vendor found for vendorId={} for ticket='{}'", vendorId, ticketNo);
				}
			} catch (Exception e) {
				log.warn("[TRACKING] Vendor lookup failed for vendorId={} for ticket='{}': {}", vendorId, ticketNo,
						e.getMessage());
			}
		}

// ── Escalation logs ───────────────────────────────────────────────────
		// ── Escalation logs ───────────────────────────────────────────────────
		String currentEscLevel = null;
		int totalRemindersSent = 0;
		int maxReminders = 0;
		List<String> reminderSentOn = new ArrayList<>();

		long lastEscLogId = 0L;
		boolean isForwardedMail = false;
		String forwardedHistory = null;

		try {
			List<EscalationLog> escLogs = escalationLogRepository.findByTicketNoOrderBySentAtDesc(ticketNo);

			List<EscalationLog> escLogsAsc = new ArrayList<>(escLogs);
			Collections.reverse(escLogsAsc);

			if (!escLogs.isEmpty()) {
				currentEscLevel = escLogs.get(0).getEscLevel();
				lastEscLogId = escLogs.get(0).getId();

				// Count total reminders (all levels)
				totalRemindersSent = escLogs.size();

				// ✅ FIX: ReminderSentOn populate karo agar koi bhi L1 reminder exists
				// currentEscLevel L1 hona zaroori nahi hai
				int reminderSerial = 1;
				for (EscalationLog el : escLogsAsc) {
					// Only L1 entries with criticalFlag false and reminderIndex > 0
					if ("L1".equalsIgnoreCase(el.getEscLevel()) && !el.isCriticalFlag() // criticalFlag == false (0)
							&& el.getReminderIndex() > 0) {

						if (el.getSentAt() != null) {
							String formatted = el.getSentAt().format(DateTimeFormatter.ofPattern("dd MMM yy, hh:mm a"))
									.toUpperCase();
							reminderSentOn.add(reminderSerial + ". " + formatted);
							reminderSerial++;
						}
					}
				}

				EscalationMatrix matrix = escalationMatrixRepository.findByEscLevel(currentEscLevel);
				if (matrix != null)
					maxReminders = matrix.getNoOfReminder();

				if (totalRemindersSent > 0) {
					isForwardedMail = true;
					forwardedHistory = buildForwardedHistory(escLogs, currentEscLevel);
					log.info("[TRACKING][FWD] ticket='{}' — forwarded history built, isForwarded={}", ticketNo,
							forwardedHistory != null);
				}
			}

		} catch (Exception e) {
			log.warn("[TRACKING] escalation log lookup failed for '{}'", ticketNo);
		}

// ── Current status ────────────────────────────────────────────────────
		String currentStatus = resolveStatus(openRow, currentEscLevel, branch);

// ── Duration ─────────────────────────────────────────────────────────
		String alertTimeFormatted = "N/A";
		String durationStr = "N/A";

		if (alertDateTime != null) {
			alertTimeFormatted = alertDateTime.format(DATE_FMT);
			Duration duration = Duration.between(alertDateTime, LocalDateTime.now());
			long totalMinutes = duration.toMinutes();
			long days = totalMinutes / (24 * 60);
			long hours = (totalMinutes % (24 * 60)) / 60;
			long minutes = totalMinutes % 60;

			if (days > 0)
				durationStr = days + " Days " + hours + " Hours " + minutes + " Min";
			else if (hours > 0)
				durationStr = hours + " Hours " + minutes + " Min";
			else
				durationStr = minutes + " Min";
		}

// ── Build DTO ─────────────────────────────────────────────────────────
		TicketTrackingDTO dto = new TicketTrackingDTO();
		dto.setTicketNo(ticketNo);
		dto.setBranchName(branchName);
		dto.setBranchCode(branchCode != null ? branchCode : "N/A");
		dto.setProduct(productType);
		dto.setAlertType(alertType);
		dto.setVendorName(vendorName); // ← Now using vendor name from vendor_master
		dto.setDtOfFirstInformation(dtFirstInfo);
		dto.setCurrentStatus(currentStatus);
		dto.setEscalationLevel(currentEscLevel);
		dto.setTotalRemindersSent(totalRemindersSent);
		dto.setMaxRemindersAllowed(maxReminders);
		dto.setRemindersRemaining(Math.max(0, maxReminders - totalRemindersSent));
		dto.setReminderSentOn(reminderSentOn.isEmpty() ? null : reminderSentOn);
		dto.setAlertTime(alertTimeFormatted);
		dto.setDuration(durationStr);

// ── Send mail ─────────────────────────────────────────────────────────
		if (currentEscLevel != null && !currentEscLevel.isBlank()) {
			try {
				EscalationMatrix matrix = escalationMatrixRepository.findByEscLevel(currentEscLevel);

				int noOfReminders = matrix != null ? matrix.getNoOfReminder() : 0;
				int reminderGap = matrix != null ? matrix.getReminderTime() : 0;
				String recipient = matrix != null ? matrix.getRecipient() : "VENDOR";

				String ccMail1 = matrix != null ? matrix.getCcMail1() : null;
				String ccMail2 = matrix != null ? matrix.getCcMail2() : null;
				String ccMail3 = matrix != null ? matrix.getCcMail3() : null;
				String ccMail4 = matrix != null ? matrix.getCcMail4() : null;

				escalationMailService.sendIfEligible(ticketNo, currentEscLevel, alertGeneratedId, lastEscLogId,
						noOfReminders, reminderGap, branchCode != null ? branchCode : "N/A", branchName, branchAddress,
						vendorName, // ← Now passing the correct vendor name
						0L, zoneDesireNameFromDB != null ? zoneDesireNameFromDB : alertType, alertType,
						totalRemindersSent + 1, recipient, productType, dtFirstInfo, vendors, isForwardedMail,
						forwardedHistory, ccMail1, ccMail2, ccMail3, ccMail4);
				log.info("[TRACKING] Mail triggered for ticket='{}', level='{}', isFwd={}", ticketNo, currentEscLevel,
						isForwardedMail);

			} catch (Exception e) {
				log.warn("[TRACKING] Mail send failed for ticket='{}': {}", ticketNo, e.getMessage(), e);
			}
		}

		log.info("[TRACKING] DTO built for '{}': status='{}', reminders={}/{}", ticketNo, currentStatus,
				totalRemindersSent, maxReminders);

		return dto;
	}

	// =========================================================================
	// FORWARDED HISTORY BUILDER
	// =========================================================================
	private String buildForwardedHistory(List<EscalationLog> escLogsDesc, String currentEscLevel) {
		try {
			EscalationLog lastLog = escLogsDesc.stream().filter(l -> currentEscLevel.equalsIgnoreCase(l.getEscLevel()))
					.findFirst().orElse(null);

			if (lastLog == null) {
				log.warn("[TRACKING][FWD] No last log found for level='{}'", currentEscLevel);
				return null;
			}

			List<EmailLogEntity> emailLogs = emailLogRepository
					.findByEscalationLogIdOrderByDatetimeAsc(lastLog.getId());

			if (emailLogs == null || emailLogs.isEmpty()) {
				log.info("[TRACKING][FWD] No Email_Log for last esc_log_id={}", lastLog.getId());
				return null;
			}

			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
			StringBuilder history = new StringBuilder();

			for (EmailLogEntity emailLog : emailLogs) {
				String sentAtStr = lastLog.getSentAt() != null ? lastLog.getSentAt().format(fmt) : "N/A";

				history.append("\n\n");
				history.append("-------- Forwarded Message").append(" (Reminder #").append(lastLog.getReminderIndex())
						.append(" | Level: ").append(lastLog.getEscLevel()).append(" | Sent: ").append(sentAtStr)
						.append(") --------\n");

				history.append("From    : ").append(nullSafe(emailLog.getFromEmail())).append("\n");
				history.append("To      : ").append(nullSafe(emailLog.getEmailIdTo())).append("\n");

				if (emailLog.getEmailIdCc() != null && !emailLog.getEmailIdCc().isBlank()) {
					history.append("CC      : ").append(emailLog.getEmailIdCc()).append("\n");
				}

				history.append("Subject : ").append(nullSafe(emailLog.getSubject())).append("\n");
				history.append("Date    : ")
						.append(emailLog.getDatetime() != null ? emailLog.getDatetime().format(fmt) : "N/A")
						.append("\n");
				history.append("Body    :\n").append(nullSafe(emailLog.getBodyText())).append("\n");
				history.append("------------------------------------------------------------------------------------");
			}

			return history.length() > 0 ? history.toString() : null;

		} catch (Exception e) {
			log.error("[TRACKING][FWD] buildForwardedHistory error: {}", e.getMessage(), e);
			return null;
		}
	}

	private String nullSafe(String val) {
		return val != null ? val : "";
	}

	// =========================================================================
	// STATUS RESOLUTION
	// =========================================================================
	private String resolveStatus(Ticket ticket, String escLevel, BranchMasterEntity branch) {
		if ("R".equalsIgnoreCase(ticket.getTicketStatus()))
			return "RESOLVED";
		if (escLevel != null && !escLevel.isBlank())
			return escLevel + " ESCALATED";
		if (branch != null && branch.getContractPerson() != null && !branch.getContractPerson().isBlank())
			return "VENDOR ASSIGNED";
		return "NEW TICKET";
	}

	// =========================================================================
	// PUBLIC — Get all distinct zone names
	// =========================================================================
	public List<String> getAllZoneDesiredNames() {
		try {
			return deviceZoneDetailsRepository.findAllDistinctZoneDesiredNames();
		} catch (Exception e) {
			log.error("[TRACKING] getAllZoneDesiredNames failed: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	// =========================================================================
	// PUBLIC — Zone-wise open ticket count
	// =========================================================================
	public List<Map<String, Object>> getOpenTicketCountByZoneDesiredName() {
		log.info("[TRACKING] Zone-wise count started");

		List<String> openTicketNos = ticketRepository.findOpenTicketNos();
		Map<String, Integer> zoneCountMap = new LinkedHashMap<>();

		for (String ticketNo : openTicketNos) {
			try {
				List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
				if (allRows.isEmpty())
					continue;

				Ticket openRow = allRows.stream().filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
						.filter(r -> r.getResolvedType() == null || r.getResolvedType().isBlank()).findFirst()
						.orElse(null);
				if (openRow == null)
					continue;

				// ── Resolve with fallback ─────────────────────────────────────
				SmsDetailsEntity smsDetail = resolveSmsDetail(openRow.getAlertGeneratedId());
				if (smsDetail == null || smsDetail.getPanelSerialNo() == null)
					continue;

				DeviceMasterEntity device = null;
				try {
					device = deviceMasterRepository.getByDeviceSerialNo(smsDetail.getPanelSerialNo());
				} catch (Exception e) {
					continue;
				}
				if (device == null)
					continue;

				String zoneName = null;
				try {
					String zoneNumber = "ZONE" + openRow.getZone();
					DeviceZoneDetailsEntity zoneDetail = deviceZoneDetailsRepository
							.findByDeviceIdAndZoneOriginalName(device.getDeviceId(), zoneNumber);
					if (zoneDetail != null && zoneDetail.getZoneDesiredName() != null
							&& !zoneDetail.getZoneDesiredName().isBlank()) {
						zoneName = zoneDetail.getZoneDesiredName().trim();
					}
				} catch (Exception e) {
					continue;
				}
				if (zoneName == null)
					continue;

				zoneCountMap.merge(zoneName, 1, Integer::sum);

			} catch (Exception e) {
				log.error("[TRACKING] Zone count failed for '{}': {}", ticketNo, e.getMessage());
			}
		}

		List<Map<String, Object>> result = new ArrayList<>();
		zoneCountMap.forEach((zone, count) -> {
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("zoneDesiredName", zone);
			entry.put("count", count);
			result.add(entry);
		});

		result.sort((a, b) -> Integer.compare((int) b.get("count"), (int) a.get("count")));
		log.info("[TRACKING] Zone-wise count done: {} zones", result.size());
		return result;
	}

	public List<Map<String, Object>> getOpenTicketSummaryForToday(List<String> deviceSerials) {

		log.info("[SUMMARY] Open ticket summary started for {} devices",
				deviceSerials != null ? deviceSerials.size() : 0);

		if (deviceSerials == null || deviceSerials.isEmpty()) {
			log.warn("[SUMMARY] No device serials provided, returning empty list");
			return new ArrayList<>();
		}

		long thresholdHours = getThresholdHours();
		LocalDateTime cutoff = LocalDateTime.now().minusHours(thresholdHours);
		log.info("[SUMMARY] Using threshold={} hours, cutoff={}", thresholdHours, cutoff);

		// Get open ticket numbers for the given devices
		List<String> openTicketNos = ticketRepository.findOpenTicketNosByDeviceSerials(deviceSerials);
		log.info("[SUMMARY] Found {} open tickets for given devices", openTicketNos.size());

		if (openTicketNos.isEmpty()) {
			return new ArrayList<>();
		}

		// Fetch all rows for these ticket numbers in a single query
		List<Ticket> allTicketRows = ticketRepository.findTicketsByTicketNos(openTicketNos);

		Map<String, List<Ticket>> rowsByTicketNo = allTicketRows.stream()
				.collect(Collectors.groupingBy(Ticket::getTicketNo));

		Map<String, List<Ticket>> openRowsByTicket = new LinkedHashMap<>();
		Map<String, Ticket> baseRowByTicket = new LinkedHashMap<>();

		for (String ticketNo : openTicketNos) {
			List<Ticket> allRows = rowsByTicketNo.get(ticketNo);
			if (allRows == null || allRows.isEmpty())
				continue;

			List<Ticket> openRows = allRows.stream().filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
					.filter(r -> "O".equalsIgnoreCase(r.getZoneStatus())).collect(Collectors.toList());
			if (openRows.isEmpty())
				continue;

			Ticket baseRow = openRows.stream().filter(r -> r.getCreateAt() != null)
					.min(Comparator.comparing(Ticket::getCreateAt)).orElse(openRows.get(0));

			openRowsByTicket.put(ticketNo, openRows);
			baseRowByTicket.put(ticketNo, baseRow);
		}

		if (baseRowByTicket.isEmpty()) {
			return new ArrayList<>();
		}

		// ─────────────────────────────────────────────────────────────
		// NEW: exclude tickets belonging to devices currently classified
		// as "No Info" in the overview count (no recent SMS + active
		// stale non-sync log). This keeps this API's numbers aligned
		// with the overview count API's Not Working / Partially Working buckets.
		// ─────────────────────────────────────────────────────────────
		Set<String> allDeviceSerials = baseRowByTicket.values().stream().map(Ticket::getDeviceSerialNo)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		Set<String> noInfoSerials = smsDetailsService.getNoInfoDeviceSerials(new ArrayList<>(allDeviceSerials));

		if (!noInfoSerials.isEmpty()) {
			log.info("[SUMMARY] Excluding {} tickets belonging to 'No Info' devices: {}", noInfoSerials.size(),
					noInfoSerials);

			baseRowByTicket.entrySet().removeIf(e -> noInfoSerials.contains(e.getValue().getDeviceSerialNo()));
			openRowsByTicket.keySet().retainAll(baseRowByTicket.keySet());
		}

		if (baseRowByTicket.isEmpty()) {
			return new ArrayList<>();
		}
		// ─────────────────────────────────────────────────────────────

		// Fetch all related devices in a single query
		Set<String> panelSerials = baseRowByTicket.values().stream().map(Ticket::getDeviceSerialNo)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		Map<String, DeviceMasterEntity> deviceBySerial = deviceMasterRepository
				.findByDeviceSerialNoIn(new ArrayList<>(panelSerials)).stream()
				.collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, d -> d, (a, b) -> a));

		// Fetch all zone details for these devices in a single query
		Set<String> deviceIds = deviceBySerial.values().stream().map(DeviceMasterEntity::getDeviceId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		List<DeviceZoneDetailsEntity> allZoneDetails = deviceIds.isEmpty() ? Collections.emptyList()
				: deviceZoneDetailsRepository.findByDeviceIdIn(new ArrayList<>(deviceIds));

		Map<String, DeviceZoneDetailsEntity> zoneDetailIndex = allZoneDetails.stream()
				.collect(Collectors.toMap(z -> z.getDeviceId() + "|" + z.getZoneOriginalName(), z -> z, (a, b) -> a));

		// Resolve SMS/alert details for all alert IDs in bulk (sms_details, fallback
		// sms_details_history)
		Set<Integer> alertIds = baseRowByTicket.values().stream().map(Ticket::getAlertGeneratedId)
				.filter(Objects::nonNull).map(Math::toIntExact).collect(Collectors.toSet());

		Map<Integer, SmsDetailsEntity> resolvedSmsById = resolveSmsDetailsBatch(alertIds);

		// Fetch all escalation logs in one query, then sort each ticket's list by
		// sentAt desc in Java
		Map<String, List<EscalationLog>> escLogsByTicket = escalationLogRepository
				.findByTicketNoIn(new ArrayList<>(baseRowByTicket.keySet())).stream()
				.collect(Collectors.groupingBy(EscalationLog::getTicketNo));

		for (List<EscalationLog> logs : escLogsByTicket.values()) {
			logs.sort((a, b) -> {
				if (a.getSentAt() == null)
					return 1;
				if (b.getSentAt() == null)
					return -1;
				return b.getSentAt().compareTo(a.getSentAt()); // descending
			});
		}

		// Build final response
		List<Map<String, Object>> result = new ArrayList<>();

		for (String ticketNo : baseRowByTicket.keySet()) {
			try {
				Ticket baseRow = baseRowByTicket.get(ticketNo);
				List<Ticket> openRows = openRowsByTicket.get(ticketNo);

				DeviceMasterEntity device = deviceBySerial.get(baseRow.getDeviceSerialNo());
				if (device == null) {
					log.warn("[SUMMARY] Device not found for serial='{}'", baseRow.getDeviceSerialNo());
					continue;
				}
				String deviceId = device.getDeviceId();

				String productName = "N/A";
				String siName = "N/A";
				DeviceZoneDetailsEntity firstZoneDetail = zoneDetailIndex.get(deviceId + "|ZONE" + baseRow.getZone());
				if (firstZoneDetail != null && firstZoneDetail.getZoneDesiredName() != null) {
					productName = firstZoneDetail.getZoneDesiredName();
					siName = firstZoneDetail.getVendors();
				}

				List<String> alertTypes = new ArrayList<>();
				for (Ticket row : openRows) {
					DeviceZoneDetailsEntity zoneDetail = zoneDetailIndex.get(deviceId + "|ZONE" + row.getZone());
					if (zoneDetail != null && zoneDetail.getAlertType() != null) {
						alertTypes.add(zoneDetail.getAlertType());
					}
				}

				String dtFirstInfo = "N/A";
				LocalDateTime alertDateTime = null;
				Long alertGeneratedId = baseRow.getAlertGeneratedId();
				if (alertGeneratedId != null) {
					SmsDetailsEntity smsDetail = resolvedSmsById.get(Math.toIntExact(alertGeneratedId));
					if (smsDetail != null && smsDetail.getDateTime() != null) {
						alertDateTime = smsDetail.getDateTime().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();
						dtFirstInfo = alertDateTime.format(DATE_FMT);
					} else {
						log.warn("[SUMMARY] No SMS/alert record found for alertGeneratedId={} (ticket={})",
								alertGeneratedId, ticketNo);
					}
				}

				String currentEscLevel = "N/A";
				List<Map<String, Object>> reminderSentOn = new ArrayList<>();
				List<EscalationLog> escLogs = escLogsByTicket.get(ticketNo);
				if (escLogs != null && !escLogs.isEmpty()) {
					currentEscLevel = escLogs.get(0).getEscLevel();
					final String latestLevel = currentEscLevel;
					for (EscalationLog el : escLogs) {
						if (el.getSentAt() != null && latestLevel.equalsIgnoreCase(el.getEscLevel())) {
							Map<String, Object> reminder = new LinkedHashMap<>();
							reminder.put("reminderIndex", el.getReminderIndex());
							reminder.put("sentAt", el.getSentAt().format(DATE_FMT));
							reminder.put("esc_level", el.getEscLevel());
							reminderSentOn.add(reminder);
						}
					}
				}

				Map<String, Object> entry = new LinkedHashMap<>();
				entry.put("ticketNo", ticketNo);
				entry.put("deviceName", device.getDeviceName() != null ? device.getDeviceName() : "N/A");
				entry.put("product", productName);
				entry.put("SiName", siName);
				entry.put("alerts", alertTypes);
				entry.put("dateOfFirstInformation", dtFirstInfo);
				entry.put("reminderSentOn", reminderSentOn);
				entry.put("currentStatus", currentEscLevel);

				result.add(entry);

			} catch (Exception e) {
				log.error("[SUMMARY] Failed for ticket='{}': {}", ticketNo, e.getMessage(), e);
			}
		}

		log.info("[SUMMARY] Done. Total open tickets returned: {}", result.size());
		return result;
	}

	// Resolves sms/alert details for multiple alert IDs at once.
	// Step 1: look up sms_details table first (fast path — recent records live
	// here)
	// Step 2: for any IDs not found in sms_details, fall back to
	// sms_details_history
//	         table, since older records get archived there after some days
	private Map<Integer, SmsDetailsEntity> resolveSmsDetailsBatch(Set<Integer> alertIds) {
		Map<Integer, SmsDetailsEntity> resultMap = new HashMap<>();

		if (alertIds == null || alertIds.isEmpty()) {
			return resultMap;
		}

		// Step 1: check sms_details table
		try {
			for (SmsDetailsEntity sms : smsDetailsRepository.findAllById(alertIds)) {
				resultMap.put(sms.getId(), sms);
			}
			log.debug("[SMS-RESOLVE] Found {} of {} in sms_details", resultMap.size(), alertIds.size());
		} catch (Exception e) {
			log.warn("[SMS-RESOLVE] Primary bulk lookup failed: {}", e.getMessage());
		}

		// Step 2: whatever ids are still missing, check sms_details_history table
		Set<Integer> missingIds = alertIds.stream().filter(id -> !resultMap.containsKey(id))
				.collect(Collectors.toSet());

		if (!missingIds.isEmpty()) {
			try {
				List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
						.findByOriginalIdIn(new ArrayList<>(missingIds));

				for (SmsDetailsHistoryEntity h : historyList) {
					SmsDetailsEntity mapped = new SmsDetailsEntity();
					// ORIGINAL_ID in history table maps back to the original sms_details.id,
					// so we use it as the key to keep lookups consistent regardless of source table
					mapped.setId(h.getOriginalId());
					mapped.setPanelSerialNo(h.getPanelSerialNo());
					mapped.setDateTime(h.getDateTime());
					mapped.setSenderMobileNo(h.getSenderMobileNo());
					mapped.setCompleteMessage(h.getCompleteMessage());
					mapped.setBroadcastFlag(h.getBroadcastFlag());
					resultMap.put(h.getOriginalId(), mapped);
				}
				log.info("[SMS-RESOLVE] Found {} more in sms_details_history", historyList.size());
			} catch (Exception e) {
				log.error("[SMS-RESOLVE] History bulk lookup failed: {}", e.getMessage());
			}
		}

		return resultMap;
	}

	private static final int DEFAULT_SYNC_THRESHOLD_HOURS = 36; // Default threshold

	private int getThresholdHours() {
		try {
			ParamEntity paramEntity = paramRepository.findByCodeAndSerial("SCHEDULAR", "NON_SYNC");
			if (paramEntity != null && paramEntity.getDescp3() != null) {
				return Integer.parseInt(paramEntity.getDescp3());
			}
		} catch (NumberFormatException e) {
			log.warn("SYNC_THRESHOLD_HOURS param invalid, using default: {}", DEFAULT_SYNC_THRESHOLD_HOURS);
		}
		return DEFAULT_SYNC_THRESHOLD_HOURS;
	}

	// =========================================================================
	// PUBLIC — Open ticket summary
	// =========================================================================
	public List<Map<String, Object>> getOpenTicketSummary() {
		log.info("[SUMMARY] Open ticket summary started");

		List<String> openTicketNos = ticketRepository.findOpenTicketNos();
		List<Map<String, Object>> result = new ArrayList<>();

		for (String ticketNo : openTicketNos) {
			try {
				List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
				if (allRows.isEmpty())
					continue;

				List<Ticket> openRows = allRows.stream().filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
						.filter(r -> "O".equalsIgnoreCase(r.getZoneStatus()))
						.collect(java.util.stream.Collectors.toList());
				if (openRows.isEmpty())
					continue;

				Ticket baseRow = openRows.stream().filter(r -> r.getCreateAt() != null)
						.min(Comparator.comparing(Ticket::getCreateAt)).orElse(openRows.get(0));

				String panelSerialNo = baseRow.getDeviceSerialNo();
				DeviceMasterEntity device = null;
				try {
					device = deviceMasterRepository.getByDeviceSerialNo(panelSerialNo);
				} catch (Exception e) {
					log.warn("[SUMMARY] Device not found for serial='{}'", panelSerialNo);
				}
				if (device == null)
					continue;

				String deviceId = device.getDeviceId();
				String deviceDesc = device.getDeviceName();

				String productName = "N/A";
				String siName = "N/A";
				try {
					String firstZoneNo = "ZONE" + baseRow.getZone();
					DeviceZoneDetailsEntity firstZoneDetail = deviceZoneDetailsRepository
							.findByDeviceIdAndZoneOriginalName(deviceId, firstZoneNo);
					if (firstZoneDetail != null && firstZoneDetail.getZoneDesiredName() != null) {
						productName = firstZoneDetail.getZoneDesiredName();
						siName = firstZoneDetail.getVendors();
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] Zone desired name lookup failed for ticket='{}'", ticketNo);
				}

				List<String> alertTypes = new ArrayList<>();
				for (Ticket row : openRows) {
					try {
						String zoneNo = "ZONE" + row.getZone();
						DeviceZoneDetailsEntity zoneDetail = deviceZoneDetailsRepository
								.findByDeviceIdAndZoneOriginalName(deviceId, zoneNo);
						if (zoneDetail != null && zoneDetail.getAlertType() != null) {
							alertTypes.add(zoneDetail.getAlertType());
						}
					} catch (Exception e) {
						log.warn("[SUMMARY] AlertType lookup failed for ticket='{}', zone='{}'", ticketNo,
								row.getZone());
					}
				}

				// ── Date of first info — resolve with fallback ─────────────────
				String dtFirstInfo = "N/A";
				try {
					Long alertGeneratedId = baseRow.getAlertGeneratedId();
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + alertGeneratedId);
					if (alertGeneratedId != null) {
						SmsDetailsEntity smsDetail = resolveSmsDetail(alertGeneratedId);
						if (smsDetail != null && smsDetail.getDateTime() != null) {
							LocalDateTime alertDateTime = smsDetail.getDateTime().toInstant()
									.atZone(ZoneId.systemDefault()).toLocalDateTime();
							dtFirstInfo = alertDateTime.format(DATE_FMT);
						}
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] SMS detail not found for ticket='{}'", ticketNo);
				}

				String currentEscLevel = "N/A";
				List<Map<String, Object>> reminderSentOn = new ArrayList<>();
				try {
					List<EscalationLog> escLogs = escalationLogRepository.findByTicketNoOrderBySentAtDesc(ticketNo);

					if (!escLogs.isEmpty()) {
						currentEscLevel = escLogs.get(0).getEscLevel();

						final String latestLevel = currentEscLevel;
						for (EscalationLog el : escLogs) {
							if (el.getSentAt() != null && latestLevel.equalsIgnoreCase(el.getEscLevel())) {
								Map<String, Object> reminder = new LinkedHashMap<>();
								reminder.put("reminderIndex", el.getReminderIndex());
								reminder.put("sentAt",
										el.getSentAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
								reminder.put("esc_level", el.getEscLevel());
								reminderSentOn.add(reminder);
							}
						}
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] Escalation log lookup failed for ticket='{}'", ticketNo);
				}

				Map<String, Object> entry = new LinkedHashMap<>();
				entry.put("ticketNo", ticketNo);
				entry.put("deviceName", deviceDesc != null ? deviceDesc : "N/A");
				entry.put("product", productName);
				entry.put("SiName", siName);
				entry.put("alerts", alertTypes);
				entry.put("dateOfFirstInformation", dtFirstInfo);
				entry.put("reminderSentOn", reminderSentOn);
				entry.put("currentStatus", currentEscLevel);

				result.add(entry);

			} catch (Exception e) {
				log.error("[SUMMARY] Failed for ticket='{}': {}", ticketNo, e.getMessage(), e);
			}
		}

		log.info("[SUMMARY] Done. Total open tickets: {}", result.size());
		return result;
	}

	// =========================================================================
	// PUBLIC — Get open ticket tracking based on ZoneReport filtered data
	// =========================================================================
	public Map<String, Object> getOpenTicketTrackingFromZoneReport(String branchCode, String zoneType, String status) {

		log.info("[ZONE-TICKET] Starting process | branchCode={}, zoneType={}, status={}", branchCode, zoneType,
				status);

		// =====================================================================
		// STEP 1: Call countingZonesByStatus to get filtered ZoneReportDTO list
		// =====================================================================
		ResponseEntity zoneResponse = smsDetailsService.countingZonesByStatus(branchCode, zoneType, status);

		List<ZoneReportDTO> zoneReportDTOs = new ArrayList<>();
		if (zoneResponse.getPayload() != null) {
			zoneReportDTOs = (List<ZoneReportDTO>) zoneResponse.getPayload();
		}

		log.info("[ZONE-TICKET] STEP 1 — ZoneReport entries received: {}", zoneReportDTOs.size());

		// =====================================================================
		// STEP 2: Extract all panelSerialNos from the ZoneReportDTO list
		// =====================================================================
		// panelSerialNo → ZoneReportDTO (for later reference / no-ticket tracking)
		Map<String, ZoneReportDTO> serialNoToDtoMap = new LinkedHashMap<>();

		for (ZoneReportDTO dto : zoneReportDTOs) {
			String serial = dto.getDeviceSerialNo();
			if (serial != null && !serial.isBlank()) {
				serialNoToDtoMap.put(serial, dto);
			} else {
				log.warn("[ZONE-TICKET] STEP 2 — Skipping ZoneReportDTO with null/blank deviceSerialNo | branch={}",
						dto.getBranchCode());
			}
		}

		log.info("[ZONE-TICKET] STEP 2 — Unique panelSerialNos extracted: {} panelSerialNos size {}", serialNoToDtoMap,
				serialNoToDtoMap.size());

		// =====================================================================
		// STEP 3: Query ticket table for open tickets by these panelSerialNos
		// =====================================================================
		Set<String> allSerialNos = serialNoToDtoMap.keySet();
		List<Ticket> openTickets = new ArrayList<>();

		if (!allSerialNos.isEmpty()) {
			try {
				openTickets = ticketRepository.findOpenTicketsBydeviceSerialNos(allSerialNos);
				log.info("[ZONE-TICKET] STEP 3 — Open tickets fetched from DB: {}", openTickets.toString());
				log.info("[ZONE-TICKET] STEP 3 — Open tickets found in DB: {}", openTickets.size());
			} catch (Exception e) {
				log.error("[ZONE-TICKET] STEP 3 — Failed to fetch open tickets: {}", e.getMessage(), e);
			}
		} else {
			log.warn("[ZONE-TICKET] STEP 3 — No serial numbers to query, skipping ticket fetch");
		}

		// =====================================================================
		// STEP 4: Track which serialNos GOT a ticket vs which did NOT
		// =====================================================================
		// serialNos that have at least one open ticket
		Set<String> serialNosWithTicket = new HashSet<>();

		for (Ticket ticket : openTickets) {
			// We need panelSerialNo from sms_details via alertGeneratedId
			// Doing a lightweight lookup just to map ticket → serialNo
			try {
				SmsDetailsEntity sms = resolveSmsDetail(ticket.getAlertGeneratedId());
				if (sms != null && sms.getPanelSerialNo() != null) {
					serialNosWithTicket.add(sms.getPanelSerialNo());
					log.info("[ZONE-TICKET] STEP 4 — Ticket '{}' mapped to serialNo='{}'", ticket.getTicketNo(),
							sms.getPanelSerialNo());
				}
			} catch (Exception e) {
				log.warn("[ZONE-TICKET] STEP 4 — Could not resolve sms for ticketNo={}: {}", ticket.getTicketNo(),
						e.getMessage());
			}
		}

		// serialNos with NO open ticket found
		Map<String, ZoneReportDTO> noTicketMap = new LinkedHashMap<>();
		for (Map.Entry<String, ZoneReportDTO> entry : serialNoToDtoMap.entrySet()) {
			if (!serialNosWithTicket.contains(entry.getKey())) {
				noTicketMap.put(entry.getKey(), entry.getValue());
				log.info("[ZONE-TICKET] STEP 4 — No open ticket found for serialNo={}, branch={}", entry.getKey(),
						entry.getValue().getBranchCode());
			}
		}

		log.info("[ZONE-TICKET] STEP 4 — SerialNos WITH ticket: {} | WITHOUT ticket: {}", serialNosWithTicket.size(),
				noTicketMap.size());

		// =====================================================================
		// STEP 5: Build allowed branch codes set (from zoneReport branches)
		// Reusing existing buildDTO hierarchy check logic
		// =====================================================================
		Set<String> allowedBranchCodes = zoneReportDTOs.stream().map(ZoneReportDTO::getBranchCode)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		log.info("[ZONE-TICKET] STEP 5 — Allowed branch codes set size: {}", allowedBranchCodes.size());

		// =====================================================================
		// STEP 6: Build TicketTrackingDTO for each open ticket
		// =====================================================================
		List<TicketTrackingDTO> ticketTrackingDTOs = new ArrayList<>();

		for (Ticket ticket : openTickets) {
			String ticketNo = ticket.getTicketNo();
			log.info("[ZONE-TICKET] STEP 6 — Processing ticketNo={}", ticketNo);

			try {
				TicketTrackingDTO trackingDTO = buildDTO(ticketNo, zoneType, allowedBranchCodes);
				if (trackingDTO != null) {
					// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> trackingDTO: " +
					// trackingDTO.toString());
					ticketTrackingDTOs.add(trackingDTO);
					log.info("[ZONE-TICKET] STEP 6 — DTO built successfully for ticketNo={}", ticketNo);
				} else {
					log.warn(
							"[ZONE-TICKET] STEP 6 — buildDTO returned null for ticketNo={} (zone mismatch or branch not allowed)",
							ticketNo);
				}
			} catch (Exception e) {
				log.error("[ZONE-TICKET] STEP 6 — Exception while building DTO for ticketNo={}: {}", ticketNo,
						e.getMessage(), e);
			}
		}

		log.info("[ZONE-TICKET] STEP 6 — Total TicketTrackingDTOs built (before dedup): {}", ticketTrackingDTOs.size());

		// =====================================================================
		// STEP 6.5: Deduplicate based on ticketNo + alertType
		// =====================================================================
		List<TicketTrackingDTO> deduplicatedDTOs = new ArrayList<>();
		Set<String> uniqueKeys = new HashSet<>();

		for (TicketTrackingDTO dto : ticketTrackingDTOs) {
			String uniqueKey = dto.getTicketNo(); // Use only ticketNo if you want to merge

			if (!uniqueKeys.contains(uniqueKey)) {
				uniqueKeys.add(uniqueKey);
				deduplicatedDTOs.add(dto);
			} else {
				log.info("[ZONE-TICKET] STEP 6.5 — Skipping duplicate ticketNo={}, alertType={}", dto.getTicketNo(),
						dto.getAlertType());
			}
		}

		log.info("[ZONE-TICKET] STEP 6.5 — After deduplication: {} entries (from {})", deduplicatedDTOs.size(),
				ticketTrackingDTOs.size());

		// Update for final result
		ticketTrackingDTOs = deduplicatedDTOs; // Replace with deduplicated list
		// =====================================================================
		// STEP 7: Build "no ticket" summary list for branches with no open ticket
		// =====================================================================
		List<Map<String, String>> noTicketSummary = new ArrayList<>();

		for (Map.Entry<String, ZoneReportDTO> entry : noTicketMap.entrySet()) {
			ZoneReportDTO zDto = entry.getValue();
			Map<String, String> info = new LinkedHashMap<>();
			info.put("deviceSerialNo", entry.getKey());
			info.put("branchCode", zDto.getBranchCode() != null ? zDto.getBranchCode() : "N/A");
			info.put("branchName", zDto.getBranchName() != null ? zDto.getBranchName() : "N/A");
			info.put("parentBranch", zDto.getParentBranch() != null ? zDto.getParentBranch() : "N/A");
			info.put("mobile", zDto.getMobile() != null ? zDto.getMobile() : "N/A");
			info.put("remark", "No open ticket found for this device");
			noTicketSummary.add(info);

			log.info("[ZONE-TICKET] STEP 7 — No-ticket entry: serialNo={}, branch={}", entry.getKey(),
					zDto.getBranchCode());
		}

		log.info("[ZONE-TICKET] STEP 7 — No-ticket summary entries: {}", noTicketSummary.size());

		// =====================================================================
		// STEP 8: Compose and return final result
		// =====================================================================
		Map<String, Object> finalResult = new LinkedHashMap<>();
		finalResult.put("totalZoneReportEntries", zoneReportDTOs.size());
		finalResult.put("totalWithOpenTicket", ticketTrackingDTOs.size()); // Already deduplicated
		finalResult.put("totalWithoutOpenTicket", noTicketSummary.size());
		finalResult.put("ticketTrackingList", ticketTrackingDTOs); // Deduplicated list
		finalResult.put("noTicketDevices", noTicketSummary);
		finalResult.put("deduplicationApplied", true); // Optional flag

		log.info("[ZONE-TICKET] STEP 8 — Final result ready | withTicket={}, withoutTicket={}",
				ticketTrackingDTOs.size(), noTicketSummary.size());

		return finalResult;
	}

	public List<Map<String, Object>> getOpenTicketSummaryForToday() {
		log.info("[SUMMARY] Open ticket summary started");

		// ── Aaj ki date prefix (filter ke liye) ───────────────────────────
		String todayPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

		List<String> openTicketNos = ticketRepository.findOpenTicketNos();
		List<Map<String, Object>> result = new ArrayList<>();

		for (String ticketNo : openTicketNos) {
			try {
				List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);
				if (allRows.isEmpty())
					continue;

				List<Ticket> openRows = allRows.stream().filter(r -> "O".equalsIgnoreCase(r.getTicketStatus()))
						.filter(r -> "O".equalsIgnoreCase(r.getZoneStatus()))
						.collect(java.util.stream.Collectors.toList());
				if (openRows.isEmpty())
					continue;

				Ticket baseRow = openRows.stream().filter(r -> r.getCreateAt() != null)
						.min(Comparator.comparing(Ticket::getCreateAt)).orElse(openRows.get(0));

				String panelSerialNo = baseRow.getDeviceSerialNo();
				DeviceMasterEntity device = null;
				try {
					device = deviceMasterRepository.getByDeviceSerialNo(panelSerialNo);
				} catch (Exception e) {
					log.warn("[SUMMARY] Device not found for serial='{}'", panelSerialNo);
				}
				if (device == null)
					continue;

				String deviceId = device.getDeviceId();
				String deviceDesc = device.getDeviceName();

				String productName = "N/A";
				String siName = "N/A";
				try {
					String firstZoneNo = "ZONE" + baseRow.getZone();
					DeviceZoneDetailsEntity firstZoneDetail = deviceZoneDetailsRepository
							.findByDeviceIdAndZoneOriginalName(deviceId, firstZoneNo);
					if (firstZoneDetail != null && firstZoneDetail.getZoneDesiredName() != null) {
						productName = firstZoneDetail.getZoneDesiredName();
						siName = firstZoneDetail.getVendors();
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] Zone desired name lookup failed for ticket='{}'", ticketNo);
				}

				List<String> alertTypes = new ArrayList<>();
				for (Ticket row : openRows) {
					try {
						String zoneNo = "ZONE" + row.getZone();
						DeviceZoneDetailsEntity zoneDetail = deviceZoneDetailsRepository
								.findByDeviceIdAndZoneOriginalName(deviceId, zoneNo);
						if (zoneDetail != null && zoneDetail.getAlertType() != null) {
							alertTypes.add(zoneDetail.getAlertType());
						}
					} catch (Exception e) {
						log.warn("[SUMMARY] AlertType lookup failed for ticket='{}', zone='{}'", ticketNo,
								row.getZone());
					}
				}

				// ── Date of first info — resolve with fallback ─────────────
				String dtFirstInfo = "N/A";
				try {
					Long alertGeneratedId = baseRow.getAlertGeneratedId();
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + alertGeneratedId);
					if (alertGeneratedId != null) {
						SmsDetailsEntity smsDetail = resolveSmsDetail(alertGeneratedId);
						if (smsDetail != null && smsDetail.getDateTime() != null) {
							LocalDateTime alertDateTime = smsDetail.getDateTime().toInstant()
									.atZone(ZoneId.systemDefault()).toLocalDateTime();
							dtFirstInfo = alertDateTime.format(DATE_FMT);
						}
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] SMS detail not found for ticket='{}'", ticketNo);
				}

				// ── Aaj ki date ka filter ─────────────────────────────────
				if (dtFirstInfo.equals("N/A") || !dtFirstInfo.startsWith(todayPrefix)) {
					log.info("[SUMMARY] Skipping ticket='{}' | date='{}' | today='{}'", ticketNo, dtFirstInfo,
							todayPrefix);
					continue; // aaj ka ticket nahi, skip
				}
				// ─────────────────────────────────────────────────────────

				String currentEscLevel = "N/A";
				List<Map<String, Object>> reminderSentOn = new ArrayList<>();
				try {
					List<EscalationLog> escLogs = escalationLogRepository.findByTicketNoOrderBySentAtDesc(ticketNo);

					if (!escLogs.isEmpty()) {
						currentEscLevel = escLogs.get(0).getEscLevel();

						final String latestLevel = currentEscLevel;
						for (EscalationLog el : escLogs) {
							if (el.getSentAt() != null && latestLevel.equalsIgnoreCase(el.getEscLevel())) {
								Map<String, Object> reminder = new LinkedHashMap<>();
								reminder.put("reminderIndex", el.getReminderIndex());
								reminder.put("sentAt",
										el.getSentAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
								reminder.put("esc_level", el.getEscLevel());
								reminderSentOn.add(reminder);
							}
						}
					}
				} catch (Exception e) {
					log.warn("[SUMMARY] Escalation log lookup failed for ticket='{}'", ticketNo);
				}

				Map<String, Object> entry = new LinkedHashMap<>();
				entry.put("ticketNo", ticketNo);
				entry.put("deviceName", deviceDesc != null ? deviceDesc : "N/A");
				entry.put("product", productName);
				entry.put("SiName", siName);
				entry.put("alerts", alertTypes);
				entry.put("dateOfFirstInformation", dtFirstInfo);
				entry.put("reminderSentOn", reminderSentOn);
				entry.put("currentStatus", currentEscLevel);

				result.add(entry);

			} catch (Exception e) {
				log.error("[SUMMARY] Failed for ticket='{}': {}", ticketNo, e.getMessage(), e);
			}
		}

		log.info("[SUMMARY] Done. Today's open tickets: {}", result.size());
		return result;
	}
}