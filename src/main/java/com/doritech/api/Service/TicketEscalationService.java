
package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Entity.EscalationMatrix;
import com.doritech.api.Entity.SmsDetailsEntity;
import com.doritech.api.Entity.SmsDetailsHistoryEntity;
import com.doritech.api.Entity.Ticket;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.EscalationMailLogRepository;
import com.doritech.api.Repository.EscalationMatrixRepository;
import com.doritech.api.Repository.SmsDetailsHistoryRepository;
import com.doritech.api.Repository.SmsDetailsRepository;
import com.doritech.api.Repository.TicketRepository;

@Service
public class TicketEscalationService {

	private static final Logger log =
			LoggerFactory.getLogger(TicketEscalationService.class);

	private static final String LOG_LINE =
			"##########################################################################";

	private final TicketRepository ticketRepository;
	private final EscalationMatrixRepository escalationMatrixRepository;
	private final SmsDetailsRepository smsDetailsRepository;
	private final SmsDetailsHistoryRepository smsDetailsHistoryRepository;
	private final DeviceMasterRepository deviceMasterRepository;
	private final DeviceZoneDetailsRepository deviceZoneDetailRepository;
	private final BranchMasterRepository branchMasterRepository;
	private final EscalationMailLogRepository escalationLogRepository;
	private final EmailLogRepo emailLogRepository;

	private final EscalationMailService escalationMailService;
	private final EscalationSmsService escalationSmsService;
	private final WhatsappService whatsAppService;
	private final EscalationCallService escalationCallService;

	private final SmsDetailsService smsDetailsService;
	private final DeviceMasterService deviceMasterService;
	private final BranchMasterService branchMasterService;

	@Autowired
	EscalationMatrixRepository escalationMatrixRepo;

	public TicketEscalationService(
			TicketRepository ticketRepository,
			EscalationMatrixRepository escalationMatrixRepository,
			SmsDetailsService smsDetailsService,
			SmsDetailsRepository smsDetailsRepository,
			SmsDetailsHistoryRepository smsDetailsHistoryRepository,
			DeviceMasterService deviceMasterService,
			DeviceMasterRepository deviceMasterRepository,
			DeviceZoneDetailsRepository deviceZoneDetailRepository,
			BranchMasterService branchMasterService,
			BranchMasterRepository branchMasterRepository,
			EscalationMailLogRepository escalationLogRepository,
			EmailLogRepo emailLogRepository,
			EscalationMailService escalationMailService,
			EscalationSmsService escalationSmsService,
			WhatsappService whatsAppService,
			EscalationCallService escalationCallService) {

		this.ticketRepository = ticketRepository;
		this.escalationMatrixRepository = escalationMatrixRepository;
		this.smsDetailsService = smsDetailsService;
		this.smsDetailsRepository = smsDetailsRepository;
		this.smsDetailsHistoryRepository = smsDetailsHistoryRepository;
		this.deviceMasterService = deviceMasterService;
		this.deviceMasterRepository = deviceMasterRepository;
		this.deviceZoneDetailRepository = deviceZoneDetailRepository;
		this.branchMasterService = branchMasterService;
		this.branchMasterRepository = branchMasterRepository;
		this.escalationLogRepository = escalationLogRepository;
		this.emailLogRepository = emailLogRepository;
		this.escalationMailService = escalationMailService;
		this.escalationSmsService = escalationSmsService;
		this.whatsAppService = whatsAppService;
		this.escalationCallService = escalationCallService;
	}

	// =========================================================================
	// HIGHLIGHTED LOG HELPER
	// =========================================================================

	private void logEscalationBanner(String title) {
		log.info("");
		log.info(LOG_LINE);
		log.info("                 {}", title);
		log.info(LOG_LINE);
	}

	// =========================================================================
	// MAIN ENTRY POINT
	// =========================================================================

	@Transactional
	public void processOpenTicketEscalations() {

		logEscalationBanner("TICKET ESCALATION PROCESS STARTED");

		log.info("[START] Service method: processOpenTicketEscalations()");
		log.info("[START] Current time: {}", LocalDateTime.now());

		try {
			List<String> openTicketNos = ticketRepository.findOpenTicketNos();

			log.info("[STEP 1] Open tickets fetched: {}",
					openTicketNos != null ? openTicketNos.size() : 0);

			if (openTicketNos == null || openTicketNos.isEmpty()) {
				log.info("[STEP 1] No open tickets found. Exiting.");
				return;
			}

			log.info("[STEP 1] Open ticket numbers: {}", openTicketNos);

			List<EscalationMatrix> allLevels =
					escalationMatrixRepository.findAllByOrderByEscTimeRangeAsc();

			if (allLevels == null || allLevels.isEmpty()) {
				log.warn("[ESCALATION] Escalation matrix is empty. Aborting.");
				return;
			}

			log.info("[ESCALATION] Matrix levels loaded: {}", allLevels.size());

			for (String ticketNo : openTicketNos) {
				log.info("[TICKET LOOP] Starting ticket processing: {}", ticketNo);

				try {
					processTicket(ticketNo, allLevels);
				} catch (Exception e) {
					log.error("[TICKET LOOP] Ticket '{}' processing failed: {}",
							ticketNo, e.getMessage(), e);
				}

				log.info("[TICKET LOOP] Finished ticket processing: {}", ticketNo);
			}

		} catch (Exception e) {
			log.error("[ESCALATION] Process failed: {}", e.getMessage(), e);
		} finally {
			log.info("[END] Escalation service finished at {}", LocalDateTime.now());
			logEscalationBanner("TICKET ESCALATION PROCESS COMPLETED");
		}
	}

	// =========================================================================
	// PER-TICKET PROCESSING
	// =========================================================================

	private void processTicket(
			String ticketNo,
			List<EscalationMatrix> allLevels) {

		logEscalationBanner("TICKET PROCESSING STARTED: " + ticketNo);

		log.info("[TICKET='{}'] Loading ticket rows.", ticketNo);

		try {
			List<Ticket> allRows = ticketRepository.findByTicketNo(ticketNo);

			if (allRows == null || allRows.isEmpty()) {
				log.warn("[TICKET='{}'] No rows found. Skipping.", ticketNo);
				return;
			}

			List<Ticket> openRows = allRows.stream()
					.filter(r -> "O".equalsIgnoreCase(r.getZoneStatus()))
					.filter(r -> r.getResolvedType() == null
							|| r.getResolvedType().isBlank())
					.collect(Collectors.toList());

			if (openRows.isEmpty()) {
				log.info("[TICKET='{}'] All zones resolved. Skipping.", ticketNo);
				return;
			}

			log.info("[TICKET='{}'] Open zones count: {}", ticketNo, openRows.size());

			Ticket firstOpenRow = openRows.get(0);
			Long alertGeneratedId = firstOpenRow.getAlertGeneratedId();

			SmsDetailsEntity smsDetail = getSmsDetail(alertGeneratedId);

			if (smsDetail == null) {
				log.warn(
						"[TICKET='{}'] No SMS detail found for alert ID={}. Skipping.",
						ticketNo, alertGeneratedId);
				return;
			}

			if (smsDetail.getDateTime() == null) {
				log.warn("[TICKET='{}'] SMS date/time is null. Skipping.", ticketNo);
				return;
			}

			LocalDateTime alertDateTime = smsDetail.getDateTime()
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();

			long elapsedMinutes = ChronoUnit.MINUTES.between(
					alertDateTime, LocalDateTime.now());

			String dtFirstInfo = alertDateTime.format(
					DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

			log.info("[TICKET='{}'] Alert date/time: {}", ticketNo, alertDateTime);
			log.info("[TICKET='{}'] Elapsed time: {} minutes ({} hours {} minutes)",
					ticketNo,
					elapsedMinutes,
					elapsedMinutes / 60,
					elapsedMinutes % 60);

			String panelSerialNo = smsDetail.getPanelSerialNo();
			DeviceMasterEntity device = getDevice(panelSerialNo);

			if (device == null) {
				log.warn("[TICKET='{}'] Device not found for panel serial '{}'.",
						ticketNo, panelSerialNo);
				return;
			}

			String productType = device.getProductType() != null
					? device.getProductType()
					: "N/A";

			List<String> allZoneNames = new ArrayList<>();
			List<String> allAlertTypes = new ArrayList<>();
			List<String> allVendorNames = new ArrayList<>();
			List<String> alertTypeWithGroupList = new ArrayList<>();

			boolean isCritical = openRows.stream()
					.anyMatch(r -> "Y".equalsIgnoreCase(r.getCriticalFlag()));

			for (Ticket openRow : openRows) {

				String zoneNumber = "ZONE" + openRow.getZone();

				String groupName = openRow.getGroupName() != null
						? openRow.getGroupName().trim()
						: "";

				DeviceZoneDetailsEntity zoneDetail =
						getZoneDetail(device.getDeviceId(), zoneNumber);

				String zoneDesiredName = zoneDetail != null
						? zoneDetail.getZoneDesiredName()
						: zoneNumber;

				String alertType = zoneDetail != null
						? zoneDetail.getAlertType()
						: "N/A";

				String vendorName = zoneDetail != null
						? zoneDetail.getVendors()
						: "N/A";

				allZoneNames.add(zoneDesiredName);
				allAlertTypes.add(alertType);
				alertTypeWithGroupList.add(alertType + "|" + groupName);

				if (vendorName != null
						&& !"N/A".equals(vendorName)
						&& !allVendorNames.contains(vendorName)) {
					allVendorNames.add(vendorName);
				}

				log.info(
						"[TICKET='{}'][ZONE] zone='{}', alertType='{}', group='{}', vendor='{}', criticalFlag='{}'",
						ticketNo,
						zoneNumber,
						alertType,
						groupName,
						vendorName,
						openRow.getCriticalFlag());
			}

			String combinedAlertTypes = String.join("<br/>", allAlertTypes);
			String combinedZoneNames = String.join("<br/>", allZoneNames);
			String combinedVendors = String.join("<br/>", allVendorNames);
			String combinedAlertTypesForWhatsapp =
					String.join(",", alertTypeWithGroupList);

			log.info("[TICKET='{}'] Combined zones: {}", ticketNo, combinedZoneNames);
			log.info("[TICKET='{}'] Combined alert types: {}", ticketNo, combinedAlertTypes);
			log.info("[TICKET='{}'] Combined vendors: {}", ticketNo, combinedVendors);

			BranchMasterEntity branch = getBranch(device.getBranchCode());

			if (branch == null) {
				log.warn("[TICKET='{}'] Branch not found: {}",
						ticketNo, device.getBranchCode());
				return;
			}

			Set<String> alreadyDoneLevels = getAlreadyDoneLevels(ticketNo, isCritical);

			// =============================================================
			// HIGHLIGHT: ESCALATION START
			// =============================================================

			log.info("");
			log.info("==========================================================================");
			log.info("                    TICKET ESCALATION STARTED");
			log.info("  Ticket No        : {}", ticketNo);
			log.info("  Alert ID         : {}", alertGeneratedId);
			log.info("  Critical         : {}", isCritical);
			log.info("  Elapsed Minutes  : {}", elapsedMinutes);
			log.info("  Branch Code      : {}", branch.getBranchCode());
			log.info("  Product Type     : {}", productType);
			log.info("  Open Zones       : {}", openRows.size());
			log.info("  Completed Levels : {}", alreadyDoneLevels);
			log.info("==========================================================================");

			if (isCritical) {

				logEscalationBanner("CRITICAL ESCALATION EVALUATION: " + ticketNo);

				List<EscalationMatrix> criticalLevels =
						escalationMatrixRepository.findByCriticalFlag(1);

				if (criticalLevels == null || criticalLevels.isEmpty()) {
					log.warn("[TICKET='{}'][CRITICAL] No critical matrix levels.", ticketNo);
					return;
				}

				List<EscalationMatrix> matchedLevels =
						determineLevelForCritical(elapsedMinutes, criticalLevels);

				if (matchedLevels == null || matchedLevels.isEmpty()) {
					log.info(
							"[TICKET='{}'][CRITICAL] No threshold reached. Elapsed={} min.",
							ticketNo, elapsedMinutes);
					return;
				}

				log.info("[TICKET='{}'][CRITICAL] Matched levels: {}",
						ticketNo,
						matchedLevels.stream()
								.map(EscalationMatrix::getEscLevel)
								.collect(Collectors.joining(", ")));

				List<EscalationMatrix> pendingFreshLevels = matchedLevels.stream()
						.filter(l -> !alreadyDoneLevels.contains(l.getEscLevel()))
						.collect(Collectors.toList());

				if (!pendingFreshLevels.isEmpty()) {

					for (EscalationMatrix matrixLevel : pendingFreshLevels) {

						logEscalationBanner(
								"CRITICAL ESCALATION LEVEL STARTED: "
										+ matrixLevel.getEscLevel());

						log.info("  Ticket No        : {}", ticketNo);
						log.info("  Escalation Level : {}", matrixLevel.getEscLevel());
						log.info("  Threshold        : {} minutes",
								matrixLevel.getEscTimeRange());
						log.info("  Elapsed Minutes  : {}", elapsedMinutes);
						log.info("  Recipient        : {}", matrixLevel.getRecipient());

						try {
							processSingleLevelForAlarm(
									ticketNo,
									matrixLevel,
									alertGeneratedId,
									branch,
									elapsedMinutes,
									dtFirstInfo,
									productType,
									combinedZoneNames,
									combinedAlertTypes,
									combinedVendors,
									combinedAlertTypesForWhatsapp);
						} catch (Exception e) {
							log.error(
									"[TICKET='{}'][CRITICAL][{}] Level processing failed.",
									ticketNo,
									matrixLevel.getEscLevel(),
									e);
						}
					}

				} else {

					List<EscalationMatrix> priorLevels =
							matchedLevels.subList(0, matchedLevels.size() - 1);

					EscalationMatrix currentLevel =
							matchedLevels.get(matchedLevels.size() - 1);

					for (EscalationMatrix priorLevel : priorLevels) {

						if (isReminderDue(ticketNo, priorLevel, true)) {

							logEscalationBanner(
									"CRITICAL PRIOR-LEVEL REMINDER: "
											+ priorLevel.getEscLevel());

							try {
								processSingleLevelForAlarm(
										ticketNo,
										priorLevel,
										alertGeneratedId,
										branch,
										elapsedMinutes,
										dtFirstInfo,
										productType,
										combinedZoneNames,
										combinedAlertTypes,
										combinedVendors,
										combinedAlertTypesForWhatsapp);
							} catch (Exception e) {
								log.error(
										"[TICKET='{}'][CRITICAL][{}] Reminder failed.",
										ticketNo,
										priorLevel.getEscLevel(),
										e);
							}
						}
					}

					logEscalationBanner(
							"CRITICAL CURRENT-LEVEL REMINDER: "
									+ currentLevel.getEscLevel());

					try {
						processSingleLevelForAlarm(
								ticketNo,
								currentLevel,
								alertGeneratedId,
								branch,
								elapsedMinutes,
								dtFirstInfo,
								productType,
								combinedZoneNames,
								combinedAlertTypes,
								combinedVendors,
								combinedAlertTypesForWhatsapp);
					} catch (Exception e) {
						log.error(
								"[TICKET='{}'][CRITICAL][{}] Current reminder failed.",
								ticketNo,
								currentLevel.getEscLevel(),
								e);
					}
				}

			} else {

				logEscalationBanner("NORMAL ESCALATION EVALUATION: " + ticketNo);

				List<EscalationMatrix> normalLevels =
						escalationMatrixRepository.findByCriticalFlag(0);

				if (normalLevels == null || normalLevels.isEmpty()) {
					log.warn("[TICKET='{}'][NORMAL] No normal matrix levels.", ticketNo);
					return;
				}

				log.info("[TICKET='{}'][NORMAL] Matrix rows: {}",
						ticketNo, normalLevels.size());

				EscalationMatrix matchedLevel =
						determineLevelForNonCritical(elapsedMinutes, normalLevels);

				if (matchedLevel == null) {
					log.info(
							"[TICKET='{}'][NORMAL] No threshold reached. Elapsed={} min.",
							ticketNo, elapsedMinutes);
					return;
				}

				log.info(
						"[TICKET='{}'][NORMAL] Highest matched level='{}', threshold={} minutes",
						ticketNo,
						matchedLevel.getEscLevel(),
						matchedLevel.getEscTimeRange());

				List<EscalationMatrix> levelsUpToMatched = normalLevels.stream()
						.filter(l -> l.getEscTimeRange() <= matchedLevel.getEscTimeRange())
						.collect(Collectors.toList());

				List<EscalationMatrix> pendingFreshLevels = levelsUpToMatched.stream()
						.filter(l -> !alreadyDoneLevels.contains(l.getEscLevel()))
						.collect(Collectors.toList());

				if (!pendingFreshLevels.isEmpty()) {

					for (EscalationMatrix level : pendingFreshLevels) {

						logEscalationBanner(
								"NORMAL ESCALATION LEVEL STARTED: "
										+ level.getEscLevel());

						log.info("  Ticket No        : {}", ticketNo);
						log.info("  Escalation Level : {}", level.getEscLevel());
						log.info("  Threshold        : {} minutes",
								level.getEscTimeRange());
						log.info("  Elapsed Minutes  : {}", elapsedMinutes);
						log.info("  Recipient        : {}", level.getRecipient());

						try {
							processSingleLevel(
									ticketNo,
									level,
									alertGeneratedId,
									branch,
									elapsedMinutes,
									dtFirstInfo,
									productType,
									combinedZoneNames,
									combinedAlertTypes,
									combinedVendors,
									combinedAlertTypesForWhatsapp);
						} catch (Exception e) {
							log.error(
									"[TICKET='{}'][NORMAL][{}] Level processing failed.",
									ticketNo,
									level.getEscLevel(),
									e);
						}
					}

				} else {

					List<EscalationMatrix> priorLevels =
							levelsUpToMatched.subList(
									0, levelsUpToMatched.size() - 1);

					for (EscalationMatrix priorLevel : priorLevels) {

						if (isReminderDue(ticketNo, priorLevel, false)) {

							logEscalationBanner(
									"NORMAL PRIOR-LEVEL REMINDER: "
											+ priorLevel.getEscLevel());

							try {
								processSingleLevel(
										ticketNo,
										priorLevel,
										alertGeneratedId,
										branch,
										elapsedMinutes,
										dtFirstInfo,
										productType,
										combinedZoneNames,
										combinedAlertTypes,
										combinedVendors,
										combinedAlertTypesForWhatsapp);
							} catch (Exception e) {
								log.error(
										"[TICKET='{}'][NORMAL][{}] Reminder failed.",
										ticketNo,
										priorLevel.getEscLevel(),
										e);
							}
						}
					}

					logEscalationBanner(
							"NORMAL CURRENT-LEVEL REMINDER: "
									+ matchedLevel.getEscLevel());

					try {
						processSingleLevel(
								ticketNo,
								matchedLevel,
								alertGeneratedId,
								branch,
								elapsedMinutes,
								dtFirstInfo,
								productType,
								combinedZoneNames,
								combinedAlertTypes,
								combinedVendors,
								combinedAlertTypesForWhatsapp);
					} catch (Exception e) {
						log.error(
								"[TICKET='{}'][NORMAL][{}] Current reminder failed.",
								ticketNo,
								matchedLevel.getEscLevel(),
								e);
					}
				}
			}

			log.info("[TICKET='{}'] Processing completed. Zones: {}",
					ticketNo, allZoneNames);

			logEscalationBanner("TICKET PROCESSING COMPLETED: " + ticketNo);

		} catch (Exception e) {
			log.error("[TICKET='{}'] Ticket processing failed: {}",
					ticketNo, e.getMessage(), e);
			throw e;
		}
	}

	// =========================================================================
	// REMINDER DUE CHECK
	// =========================================================================

	private boolean isReminderDue(
			String ticketNo,
			EscalationMatrix matrixLevel,
			boolean criticalFlag) {

		try {
			String escLevel = matrixLevel.getEscLevel();
			int noOfReminders = matrixLevel.getNoOfReminder();
			int reminderGap = matrixLevel.getReminderTime();

			log.info(
					"[REMINDER CHECK START] ticket={}, level={}, critical={}, maxReminders={}, gap={}min",
					ticketNo, escLevel, criticalFlag, noOfReminders, reminderGap);

			List<EscalationLog> logs =
					escalationLogRepository
							.findByTicketNoAndEscLevelAndCriticalFlagOrderByReminderIndexAsc(
									ticketNo, escLevel, criticalFlag);

			if (logs.isEmpty()) {
				log.info(
						"[REMINDER CHECK] No previous logs. Reminder is not due for this level.");
				return false;
			}

			long alreadySentCount = logs.size();

			if (alreadySentCount >= noOfReminders) {
				log.info(
						"[REMINDER CHECK] Limit reached. Sent={}, allowed={}",
						alreadySentCount, noOfReminders);
				return false;
			}

			int maxReminderIndex = logs.stream()
					.mapToInt(EscalationLog::getReminderIndex)
					.max()
					.orElse(0);

			int nextReminderIndex = maxReminderIndex + 1;

			if (nextReminderIndex > noOfReminders) {
				log.info(
						"[REMINDER CHECK] Next index {} exceeds limit {}.",
						nextReminderIndex, noOfReminders);
				return false;
			}

			if (reminderGap > 0) {
				EscalationLog lastLog = logs.get(logs.size() - 1);

				if (lastLog.getSentAt() != null) {
					long gapMinutes = ChronoUnit.MINUTES.between(
							lastLog.getSentAt(), LocalDateTime.now());

					log.info(
							"[REMINDER CHECK] Time since previous log={}min, required={}min",
							gapMinutes, reminderGap);

					if (gapMinutes < reminderGap) {
						log.info("[REMINDER CHECK] Reminder not due yet.");
						return false;
					}
				}
			}

			log.info(
					"[REMINDER CHECK] Reminder is due. Ticket={}, level={}, nextIndex={}",
					ticketNo, escLevel, nextReminderIndex);

			return true;

		} catch (Exception e) {
			log.error(
					"[REMINDER CHECK] Failed for ticket={}, level={}",
					ticketNo, matrixLevel.getEscLevel(), e);
			return false;
		}
	}

	// =========================================================================
	// ALREADY DONE LEVELS
	// =========================================================================

	private Set<String> getAlreadyDoneLevels(
			String ticketNo,
			boolean criticalFlag) {

		try {
			List<EscalationLog> logs =
					escalationLogRepository.findByTicketNoAndCriticalFlag(
							ticketNo, criticalFlag);

			Set<String> levels = logs.stream()
					.map(EscalationLog::getEscLevel)
					.collect(Collectors.toSet());

			log.info(
					"[ALREADY DONE] ticket={}, critical={}, levels={}",
					ticketNo, criticalFlag, levels);

			return levels;

		} catch (Exception e) {
			log.error(
					"[ALREADY DONE] Failed for ticket={}, critical={}",
					ticketNo, criticalFlag, e);

			return Collections.emptySet();
		}
	}

	// =========================================================================
	// SINGLE LEVEL — CRITICAL
	// =========================================================================

	private void processSingleLevelForAlarm(
			String ticketNo,
			EscalationMatrix matrixLevel,
			Long alertGeneratedId,
			BranchMasterEntity branch,
			long elapsedMinutes,
			String dtFirstInfo,
			String productType,
			String combinedZoneNames,
			String combinedAlertTypes,
			String combinedVendors,
			String combinedAlertTypesForWhatsapp) {

		String escalationLevel = matrixLevel.getEscLevel();
		String recipientType = matrixLevel.getRecipient();
		int noOfReminders = matrixLevel.getNoOfReminder();
		int reminderGap = matrixLevel.getReminderTime();

		logEscalationBanner(
				"CRITICAL LEVEL EXECUTION STARTED: " + escalationLevel);

		log.info(
				"[CRITICAL] ticket={}, level={}, recipient={}, elapsed={}min",
				ticketNo, escalationLevel, recipientType, elapsedMinutes);

		int nextReminderIndex =
				getNextReminderIndex(ticketNo, escalationLevel, true);

		List<EscalationLog> existingLogs =
				escalationLogRepository
						.findByTicketNoAndEscLevelAndCriticalFlagOrderByReminderIndexAsc(
								ticketNo, escalationLevel, true);

		long alreadySentCount = existingLogs.size();

		log.info(
				"[CRITICAL PRE-CHECK] ticket={}, level={}, sentCount={}, maxReminders={}, nextIndex={}",
				ticketNo, escalationLevel, alreadySentCount,
				noOfReminders, nextReminderIndex);

		if (alreadySentCount >= noOfReminders) {
			log.info("[CRITICAL PRE-CHECK] Reminder limit reached. Skipping.");
			return;
		}

		if (reminderGap > 0 && !existingLogs.isEmpty()) {
			EscalationLog lastLog = existingLogs.get(existingLogs.size() - 1);

			if (lastLog.getSentAt() != null) {
				long gapMinutes = ChronoUnit.MINUTES.between(
						lastLog.getSentAt(), LocalDateTime.now());

				log.info(
						"[CRITICAL PRE-CHECK] Previous gap={}min, required={}min",
						gapMinutes, reminderGap);

				if (gapMinutes < reminderGap) {
					log.info("[CRITICAL PRE-CHECK] Too soon. Skipping.");
					return;
				}
			}
		}

		if (nextReminderIndex > noOfReminders) {
			log.info("[CRITICAL PRE-CHECK] Reminder index exceeds limit. Skipping.");
			return;
		}

		String forwardedMailHistory = null;
		boolean isForwardedMail = false;

		if (nextReminderIndex > 0 && !existingLogs.isEmpty()) {
			forwardedMailHistory = buildForwardedMailHistory(existingLogs);
			isForwardedMail =
					forwardedMailHistory != null && !forwardedMailHistory.isBlank();
		}

		EscalationLog escLog = new EscalationLog();
		escLog.setTicketNo(ticketNo);
		escLog.setEscLevel(escalationLevel);
		escLog.setReminderIndex(nextReminderIndex);
		escLog.setSentAt(LocalDateTime.now());
		escLog.setCriticalFlag(true);

		EscalationLog savedEscLog = escalationLogRepository.save(escLog);
		Long escalationLogId = savedEscLog.getId();

		log.info(
				"[CRITICAL] Escalation log saved. id={}, ticket={}, level={}, reminderIndex={}",
				escalationLogId, ticketNo, escalationLevel, nextReminderIndex);

		if (Boolean.TRUE.equals(matrixLevel.getMail())) {
			try {
				logEscalationBanner("CRITICAL EMAIL DISPATCH STARTED");

				log.info("Ticket No         : {}", ticketNo);
				log.info("Escalation Level  : {}", escalationLevel);
				log.info("Escalation Log ID : {}", escalationLogId);
				log.info("Reminder Index    : {}", nextReminderIndex);
				log.info("Recipient Type    : {}", recipientType);

				escalationMailService.sendIfEligible(
						ticketNo,
						escalationLevel,
						alertGeneratedId,
						escalationLogId,
						noOfReminders,
						reminderGap,
						branch.getBranchCode(),
						branch.getBranchDesc(),
						branch.getAddress1(),
						branch.getContractPerson(),
						elapsedMinutes,
						combinedZoneNames,
						combinedAlertTypes,
						nextReminderIndex,
						recipientType,
						productType,
						dtFirstInfo,
						combinedVendors,
						isForwardedMail,
						forwardedMailHistory,
						matrixLevel.getCcMail1(),
						matrixLevel.getCcMail2(),
						matrixLevel.getCcMail3(),
						matrixLevel.getCcMail4());

				log.info(
						"[CRITICAL MAIL] sendIfEligible() returned. ticket={}, level={}, logId={}",
						ticketNo, escalationLevel, escalationLogId);

			} catch (Exception e) {
				log.error(
						"[CRITICAL MAIL] Failed. ticket={}, level={}, logId={}",
						ticketNo, escalationLevel, escalationLogId, e);
			}
		} else {
			log.info("[CRITICAL MAIL] Mail disabled for level={}", escalationLevel);
		}

		try {
			log.info("[CRITICAL SMS] Dispatch starting. ticket={}, level={}",
					ticketNo, escalationLevel);

			smsDetailsService.sendSmsByBranchCodeForAlarm(
					branch.getBranchCode(),
					combinedAlertTypesForWhatsapp,
					escalationLogId,
					escalationLevel);

			log.info("[CRITICAL SMS] Dispatch method returned. ticket={}, level={}",
					ticketNo, escalationLevel);

		} catch (Exception e) {
			log.error(
					"[CRITICAL SMS] Failed. ticket={}, level={}",
					ticketNo, escalationLevel, e);
		}

		try {
			EscalationMatrix escalationMatrix =
					escalationMatrixRepo.findByEscLevelAndCriticalFlag(
							escalationLevel, true);

			if (escalationMatrix == null) {
				log.warn("[CRITICAL CALL] No matrix found for level={}",
						escalationLevel);
			} else {
				log.info(
						"[CRITICAL CALL] Matrix found for level={}. Call dispatch is not implemented.",
						escalationLevel);
			}

		} catch (Exception e) {
			log.error("[CRITICAL CALL] Failed for level={}", escalationLevel, e);
		}

		logEscalationBanner(
				"CRITICAL LEVEL EXECUTION COMPLETED: " + escalationLevel);
	}

	// =========================================================================
	// SINGLE LEVEL — NORMAL
	// =========================================================================

	private void processSingleLevel(
			String ticketNo,
			EscalationMatrix matrixLevel,
			Long alertGeneratedId,
			BranchMasterEntity branch,
			long elapsedMinutes,
			String dtFirstInfo,
			String productType,
			String combinedZoneNames,
			String combinedAlertTypes,
			String combinedVendors,
			String combinedAlertTypesForWhatsapp) {

		String escalationLevel = matrixLevel.getEscLevel();
		String recipientType = matrixLevel.getRecipient();
		int noOfReminders = matrixLevel.getNoOfReminder();
		int reminderGap = matrixLevel.getReminderTime();

		logEscalationBanner(
				"NORMAL LEVEL EXECUTION STARTED: " + escalationLevel);

		log.info(
				"[NORMAL] ticket={}, level={}, recipient={}, elapsed={}min",
				ticketNo, escalationLevel, recipientType, elapsedMinutes);

		int nextReminderIndex =
				getNextReminderIndex(ticketNo, escalationLevel, false);

		List<EscalationLog> existingLogs =
				escalationLogRepository
						.findByTicketNoAndEscLevelAndCriticalFlagOrderByReminderIndexAsc(
								ticketNo, escalationLevel, false);

		long alreadySentCount = existingLogs.size();

		log.info(
				"[NORMAL PRE-CHECK] ticket={}, level={}, sentCount={}, maxReminders={}, nextIndex={}",
				ticketNo, escalationLevel, alreadySentCount,
				noOfReminders, nextReminderIndex);

		if (alreadySentCount >= noOfReminders) {
			log.info("[NORMAL PRE-CHECK] Reminder limit reached. Skipping.");
			return;
		}

		if (reminderGap > 0 && !existingLogs.isEmpty()) {
			EscalationLog lastLog = existingLogs.get(existingLogs.size() - 1);

			if (lastLog.getSentAt() != null) {
				long gapMinutes = ChronoUnit.MINUTES.between(
						lastLog.getSentAt(), LocalDateTime.now());

				log.info(
						"[NORMAL PRE-CHECK] Previous gap={}min, required={}min",
						gapMinutes, reminderGap);

				if (gapMinutes < reminderGap) {
					log.info("[NORMAL PRE-CHECK] Too soon. Skipping.");
					return;
				}
			}
		}

		if (nextReminderIndex > noOfReminders) {
			log.info("[NORMAL PRE-CHECK] Reminder index exceeds limit. Skipping.");
			return;
		}

		String forwardedMailHistory = null;
		boolean isForwardedMail = false;

		if (nextReminderIndex > 0 && !existingLogs.isEmpty()) {
			forwardedMailHistory = buildForwardedMailHistory(existingLogs);
			isForwardedMail =
					forwardedMailHistory != null && !forwardedMailHistory.isBlank();
		}

		EscalationLog escLog = new EscalationLog();
		escLog.setTicketNo(ticketNo);
		escLog.setEscLevel(escalationLevel);
		escLog.setReminderIndex(nextReminderIndex);
		escLog.setSentAt(LocalDateTime.now());
		escLog.setCriticalFlag(false);

		EscalationLog savedEscLog = escalationLogRepository.save(escLog);
		Long escalationLogId = savedEscLog.getId();

		log.info(
				"[NORMAL] Escalation log saved. id={}, ticket={}, level={}, reminderIndex={}",
				escalationLogId, ticketNo, escalationLevel, nextReminderIndex);

		if (Boolean.TRUE.equals(matrixLevel.getMail())) {
			try {
				logEscalationBanner("NORMAL EMAIL DISPATCH STARTED");

				log.info("Ticket No         : {}", ticketNo);
				log.info("Escalation Level  : {}", escalationLevel);
				log.info("Escalation Log ID : {}", escalationLogId);
				log.info("Reminder Index    : {}", nextReminderIndex);
				log.info("Recipient Type    : {}", recipientType);

				escalationMailService.sendIfEligible(
						ticketNo,
						escalationLevel,
						alertGeneratedId,
						escalationLogId,
						noOfReminders,
						reminderGap,
						branch.getBranchCode(),
						branch.getBranchDesc(),
						branch.getAddress1(),
						branch.getContractPerson(),
						elapsedMinutes,
						combinedZoneNames,
						combinedAlertTypes,
						nextReminderIndex,
						recipientType,
						productType,
						dtFirstInfo,
						combinedVendors,
						isForwardedMail,
						forwardedMailHistory,
						matrixLevel.getCcMail1(),
						matrixLevel.getCcMail2(),
						matrixLevel.getCcMail3(),
						matrixLevel.getCcMail4());

				log.info(
						"[NORMAL MAIL] sendIfEligible() returned. ticket={}, level={}, logId={}",
						ticketNo, escalationLevel, escalationLogId);

			} catch (Exception e) {
				log.error(
						"[NORMAL MAIL] Failed. ticket={}, level={}, logId={}",
						ticketNo, escalationLevel, escalationLogId, e);
			}
		} else {
			log.info("[NORMAL MAIL] Mail disabled for level={}", escalationLevel);
		}

		try {
			log.info("[NORMAL SMS] Dispatch starting. ticket={}, level={}",
					ticketNo, escalationLevel);

			smsDetailsService.sendSmsByBranchCodeForAlert(
					branch.getBranchCode(),
					combinedAlertTypesForWhatsapp,
					escalationLogId,
					escalationLevel);

			log.info("[NORMAL SMS] Dispatch method returned. ticket={}, level={}",
					ticketNo, escalationLevel);

		} catch (Exception e) {
			log.error(
					"[NORMAL SMS] Failed. ticket={}, level={}",
					ticketNo, escalationLevel, e);
		}

		try {
			EscalationMatrix escalationMatrix =
					escalationMatrixRepo.findByEscLevelAndCriticalFlag(
							escalationLevel, true);

			if (escalationMatrix == null) {
				log.warn("[NORMAL CALL] No matrix found for level={}",
						escalationLevel);
			} else {
				log.info(
						"[NORMAL CALL] Matrix found for level={}. Call dispatch is not implemented.",
						escalationLevel);
			}

		} catch (Exception e) {
			log.error("[NORMAL CALL] Failed for level={}", escalationLevel, e);
		}

		logEscalationBanner(
				"NORMAL LEVEL EXECUTION COMPLETED: " + escalationLevel);
	}

	// =========================================================================
	// BUILD FORWARDED MAIL HISTORY
	// =========================================================================

	private String buildForwardedMailHistory(
			List<EscalationLog> existingLogs) {

		EscalationLog lastLog = existingLogs.stream()
				.reduce((a, b) -> b)
				.orElse(null);

		if (lastLog == null) {
			log.warn("[FWD] No last escalation log found.");
			return null;
		}

		StringBuilder history = new StringBuilder();

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

		try {
			Long escLogId = lastLog.getId();

			if (escLogId == null) {
				log.warn("[FWD] Last escalation log has null ID.");
				return null;
			}

			List<EmailLogEntity> emailLogs =
					emailLogRepository.findByEscalationLogId(escLogId);

			if (emailLogs == null || emailLogs.isEmpty()) {
				log.info("[FWD] No email history found for escalationLogId={}",
						escLogId);
				return null;
			}

			for (EmailLogEntity emailLog : emailLogs) {

				String sentAtStr = lastLog.getSentAt() != null
						? lastLog.getSentAt().format(formatter)
						: "N/A";

				history.append("\n\n");
				history.append("-------- Forwarded Message")
						.append(" (Reminder #")
						.append(lastLog.getReminderIndex())
						.append(" | Level: ")
						.append(lastLog.getEscLevel())
						.append(" | Sent: ")
						.append(sentAtStr)
						.append(") --------\n");

				history.append("From    : ")
						.append(nullSafe(emailLog.getFromEmail()))
						.append("\n");

				history.append("To      : ")
						.append(nullSafe(emailLog.getEmailIdTo()))
						.append("\n");

				if (emailLog.getEmailIdCc() != null
						&& !emailLog.getEmailIdCc().isBlank()) {
					history.append("CC      : ")
							.append(emailLog.getEmailIdCc())
							.append("\n");
				}

				history.append("Subject : ")
						.append(nullSafe(emailLog.getSubject()))
						.append("\n");

				history.append("Date    : ")
						.append(emailLog.getDatetime() != null
								? emailLog.getDatetime().format(formatter)
								: "N/A")
						.append("\n");

				history.append("Body    :\n")
						.append(nullSafe(emailLog.getBodyText()))
						.append("\n");

				history.append(
						"------------------------------------------------------------------------------------");
			}

		} catch (Exception e) {
			log.error("[FWD] Error building forwarded history.", e);
		}

		String result = history.length() > 0 ? history.toString() : null;

		log.info("[FWD] History built. Characters={}",
				result != null ? result.length() : 0);

		return result;
	}

	private String nullSafe(String val) {
		return val != null ? val : "";
	}

	// =========================================================================
	// SMS DETAIL LOOKUP WITH HISTORY FALLBACK
	// =========================================================================

	private SmsDetailsEntity getSmsDetail(Long alertId) {

		if (alertId == null) {
			log.warn("[HELPER] getSmsDetail called with null alertId.");
			return null;
		}

		int id = Math.toIntExact(alertId);

		try {
			Optional<SmsDetailsEntity> primary =
					smsDetailsRepository.findById(id);

			if (primary.isPresent()) {
				log.debug("[HELPER] SMS detail found in primary table. id={}", id);
				return primary.get();
			}

		} catch (Exception e) {
			log.warn("[HELPER] Primary SMS lookup failed for id={}: {}",
					id, e.getMessage());
		}

		log.info("[HELPER] SMS id={} not in primary table. Checking history.", id);

		try {
			Optional<SmsDetailsHistoryEntity> historyOpt =
					smsDetailsHistoryRepository.findByOriginalId(id);

			if (historyOpt.isPresent()) {
				SmsDetailsHistoryEntity h = historyOpt.get();

				log.info("[HELPER] SMS found in history for ORIGINAL_ID={}", id);

				SmsDetailsEntity mapped = new SmsDetailsEntity();
				mapped.setId(h.getOriginalId());
				mapped.setPanelSerialNo(h.getPanelSerialNo());
				mapped.setDateTime(h.getDateTime());
				mapped.setSenderMobileNo(h.getSenderMobileNo());
				mapped.setCompleteMessage(h.getCompleteMessage());
				mapped.setBroadcastFlag(h.getBroadcastFlag());

				return mapped;
			}

		} catch (Exception e) {
			log.error("[HELPER] History lookup failed for id={}: {}",
					id, e.getMessage(), e);
		}

		log.warn("[HELPER] SMS id={} not found in primary or history table.", id);
		return null;
	}

	// =========================================================================
	// DEVICE LOOKUP
	// =========================================================================

	private DeviceMasterEntity getDevice(String serialNo) {
		try {
			DeviceMasterEntity device =
					deviceMasterRepository.getByDeviceSerialNo(serialNo);

			log.info("[HELPER] Device lookup. serial={}, found={}",
					serialNo, device != null);

			return device;

		} catch (Exception e) {
			log.error("[HELPER] Device lookup failed for serial={}",
					serialNo, e);
			return null;
		}
	}

	// =========================================================================
	// BRANCH LOOKUP
	// =========================================================================

	private BranchMasterEntity getBranch(String branchCode) {
		try {
			BranchMasterEntity branch =
					branchMasterRepository.findBybranchCode(branchCode);

			log.info("[HELPER] Branch lookup. code={}, found={}",
					branchCode, branch != null);

			return branch;

		} catch (Exception e) {
			log.error("[HELPER] Branch lookup failed for code={}",
					branchCode, e);
			return null;
		}
	}

	// =========================================================================
	// ZONE DETAIL LOOKUP
	// =========================================================================

	private DeviceZoneDetailsEntity getZoneDetail(
			String deviceId,
			String zoneName) {

		try {
			DeviceZoneDetailsEntity detail =
					deviceZoneDetailRepository.findByDeviceIdAndZoneOriginalName(
							deviceId, zoneName);

			log.debug(
					"[HELPER] Zone lookup. deviceId={}, zone={}, found={}",
					deviceId, zoneName, detail != null);

			return detail;

		} catch (Exception e) {
			log.error(
					"[HELPER] Zone lookup failed. deviceId={}, zone={}",
					deviceId, zoneName, e);
			return null;
		}
	}

	// =========================================================================
	// NEXT REMINDER INDEX
	// =========================================================================

	private int getNextReminderIndex(
			String ticketNo,
			String escLevel,
			boolean criticalFlag) {

		try {
			List<EscalationLog> existing =
					escalationLogRepository
							.findByTicketNoAndEscLevelAndCriticalFlagOrderByReminderIndexAsc(
									ticketNo, escLevel, criticalFlag);

			if (existing.isEmpty()) {
				log.info(
						"[REMINDER INDEX] No existing logs. ticket={}, level={}, index=0",
						ticketNo, escLevel);
				return 0;
			}

			int maxReminderIndex = existing.stream()
					.mapToInt(EscalationLog::getReminderIndex)
					.max()
					.orElse(0);

			int nextIndex = maxReminderIndex + 1;

			log.info(
					"[REMINDER INDEX] ticket={}, level={}, max={}, next={}",
					ticketNo, escLevel, maxReminderIndex, nextIndex);

			return nextIndex;

		} catch (Exception e) {
			log.error(
					"[REMINDER INDEX] Lookup failed. ticket={}, level={}, critical={}",
					ticketNo, escLevel, criticalFlag, e);
			return 0;
		}
	}

	// =========================================================================
	// NORMAL LEVEL MATCHING
	// =========================================================================

	private EscalationMatrix determineLevelForNonCritical(
			long elapsedMin,
			List<EscalationMatrix> levels) {

		try {
			EscalationMatrix matched = null;

			for (EscalationMatrix level : levels) {
				log.debug(
						"[NORMAL MATCH] elapsed={}min, level={}, threshold={}min",
						elapsedMin, level.getEscLevel(), level.getEscTimeRange());

				if (elapsedMin >= level.getEscTimeRange()) {
					matched = level;
				}
			}

			log.info("[NORMAL MATCH] Matched level={}",
					matched != null ? matched.getEscLevel() : "NONE");

			return matched;

		} catch (Exception e) {
			log.error("[NORMAL MATCH] Failed to determine escalation level.", e);
			return null;
		}
	}

	// =========================================================================
	// CRITICAL LEVEL MATCHING
	// =========================================================================

	private List<EscalationMatrix> determineLevelForCritical(
			long elapsedMin,
			List<EscalationMatrix> levels) {

		try {
			List<EscalationMatrix> matched = new ArrayList<>();

			for (EscalationMatrix level : levels) {
				log.debug(
						"[CRITICAL MATCH] elapsed={}min, level={}, threshold={}min",
						elapsedMin, level.getEscLevel(), level.getEscTimeRange());

				if (elapsedMin >= level.getEscTimeRange()) {
					matched.add(level);
				}
			}

			log.info("[CRITICAL MATCH] Matched levels={}",
					matched.stream()
							.map(EscalationMatrix::getEscLevel)
							.collect(Collectors.joining(", ")));

			return matched.isEmpty() ? null : matched;

		} catch (Exception e) {
			log.error("[CRITICAL MATCH] Failed to determine escalation levels.", e);
			return null;
		}
	}
}
