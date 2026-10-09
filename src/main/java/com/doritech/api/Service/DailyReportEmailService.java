package com.doritech.api.Service;

import com.doritech.api.Entity.*;
import com.doritech.api.Repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyReportEmailService {

	@Autowired
	DeviceMasterService deviceMasterService;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	
	@Autowired
	private ExceptionLogRepository exceptionLogRepository;

	// ─────────────────────────────────────────────
	// Scheduler Names (Constants)
	// ─────────────────────────────────────────────
	public static final String SCHEDULER_5PM = "DAILY_5PM";
	public static final String SCHEDULER_8AM = "DAILY_8AM";

	private static final int HOUR_5PM = 17;
	private static final int HOUR_8AM = 8;

	// ─────────────────────────────────────────────
	// Injected Dependencies
	// ─────────────────────────────────────────────
	Logger log = LoggerFactory.getLogger(DailyReportEmailService.class);

	@Autowired
	private BranchMasterService branchMasterService;

	@Autowired
	private DeviceMasterRepository deviceMasterRepository;

	@Autowired
	private DeviceZoneDetailsRepository deviceZoneRepo;

	@Autowired
	private SmsDetailsRepository smsDetailsRepository;

	@Autowired
	private SmsDetailsHistoryRepository smsDetailsHistoryRepository;

	@Autowired
	SmsDetailsService smsDetailsService;

	@Autowired
	private EmailTemplateRepository emailTemplateRepository;

	@Autowired
	private EmailTemplateLoader emailTemplateLoader;

	@Autowired
	private EmailSchedularService emailSchedulerService;

	@Autowired
	TicketTrackingService ticketService;

	@Autowired
	private EmailLogRepo emailLogRepository;

	@Autowired
	private SchedulerRunLogRepo schedulerRunLogRepo;

	// =====================================================================
	// CRON SCHEDULERS
	// =====================================================================

	/**
	 * Daily 5 PM scheduler — Cron: 0 0 17 * * ?
	 */
	@Scheduled(cron = "0 0 17 * * ?")
	public void sendDailyBranchReports5PM() {
		log.info("=== [CRON] 5PM Scheduler triggered ===");
		runScheduler(SCHEDULER_5PM, "SCHEDULED");
	}
	 

	/**
	 * Daily 8 AM scheduler — Cron: 0 0 8 * * ?
	 */
	@Scheduled(cron = "0 0 8 * * ?")
	public void sendDailyBranchReports8AM() {
		log.info("=== [CRON] 8AM Scheduler triggered ===");
		runScheduler(SCHEDULER_8AM, "SCHEDULED");
	}

	// =====================================================================
	// STARTUP RECOVERY (ApplicationRunner)
	// =====================================================================

	@Component
	public class SchedulerStartupRecovery implements ApplicationRunner {

		@Autowired
		private DailyReportEmailService schedulerService;

		@Autowired
		private SchedulerRunLogRepo schedulerRunLogRepo;

		@Override
		public void run(ApplicationArguments args) {
			log.info("=== [STARTUP] Checking for missed schedulers ===");

			LocalDateTime now = LocalDateTime.now();
			LocalDate today = now.toLocalDate();
			int currentHour = now.getHour();

			log.info("Current time: {} | Date: {}", now, today);

			checkAndRecover(SCHEDULER_5PM, HOUR_5PM, today, currentHour);
			checkAndRecover(SCHEDULER_8AM, HOUR_8AM, today, currentHour);

			log.info("=== [STARTUP] Missed scheduler check complete ===");
		}

		private void checkAndRecover(String schedulerName, int scheduledHour, LocalDate today, int currentHour) {
			if (currentHour >= scheduledHour) {
				boolean alreadyRanSuccessfully = schedulerRunLogRepo
						.findTopBySchedulerNameAndRunDateAndStatus(schedulerName, today, "SUCCESS").isPresent();
				if (alreadyRanSuccessfully) {
					log.info("[STARTUP] {} already ran successfully today. Skipping.", schedulerName);
				} else {
					log.warn("[STARTUP] {} was MISSED today! Running now as recovery...", schedulerName);
					schedulerService.runScheduler(schedulerName, "STARTUP_RECOVERY");
				}
			} else {
				log.info("[STARTUP] {} not due yet today (scheduled at hour {}, current hour: {}). Skipping.",
						schedulerName, scheduledHour, currentHour);
			}
		}
	}

	// =====================================================================
	// CORE SCHEDULER RUNNER
	// =====================================================================

	public String runScheduler(String schedulerName, String triggerType) {
		log.info("[{}] Starting... TriggerType: {}", schedulerName, triggerType);

		String result = sendBranchReports();

		String status = result.startsWith("Error") || result.startsWith("Failed") ? "FAILED" : "SUCCESS";

		saveSchedulerRunLog(schedulerName, status, result, triggerType);

		log.info("[{}] Completed with status: {}", schedulerName, status);
		return result;
	}

	private void saveSchedulerRunLog(String schedulerName, String status, String remarks, String triggerType) {
		try {
			SchedulerRunLog log2 = new SchedulerRunLog();
			log2.setSchedulerName(schedulerName);
			log2.setRunDate(LocalDate.now());
			log2.setRunDatetime(LocalDateTime.now());
			log2.setStatus(status);
			log2.setRemarks(remarks);
			log2.setTriggerType(triggerType);

			schedulerRunLogRepo.save(log2);
			log.info("[LOG] Scheduler run log saved | Name: {} | Status: {} | Trigger: {}", schedulerName, status,
					triggerType);

		} catch (Exception e) {
			log.error("[LOG ERROR] Failed to save scheduler run log: {}", e.getMessage());
		}
	}

	// ─────────────────────────────────────────────
	// Main Entry — Send to all SuperHO / Zones
	// ─────────────────────────────────────────────

	public String sendBranchReports() {
		javax.mail.Transport transport = null;
		try {
			transport = emailSchedulerService.openTransport();
			com.doritech.api.Entity.ResponseEntity responseEntity = branchMasterService.getAllSuperHOAndZones();

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				log.warn("[DAILY-MAIL] No branches found. Aborting.");
				return "No branches found.";
			}

			EmailTemplateEntity template = resolveTemplate("Daily", 0);
			if (template == null) {
				log.error("[DAILY-MAIL] No template found for level=Daily, reminder=0. Aborting.");
				return "Template not found.";
			}

			String rawHtml = emailTemplateLoader.loadTemplate(template.getEmailBody());
			if (rawHtml == null || rawHtml.isBlank()) {
				log.error("[DAILY-MAIL] HTML body empty/null. Aborting.");
				return "Template HTML empty.";
			}

			int successCount = 0;
			int skipCount = 0;
			int failCount = 0;

			String reportDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			for (BranchMasterEntity branch : branchList) {
				String branchCode = branch.getBranchCode();
				String branchName = branch.getBranchDesc();

				List<String> connectedDeviceSerials = getConnectedDeviceSerials(branchCode);
				if (connectedDeviceSerials.isEmpty()) {
					log.info("[DAILY-MAIL] Branch {} ({}) has 0 active devices. Skipping email.", branchCode,
							branchName);
					skipCount++;
					continue;
				}

				List<String> emailList = collectEmails(branch);
				if (emailList.isEmpty()) {
					log.warn("[DAILY-MAIL] No emails for branch: {}", branchCode);
					saveEmailLog(null, branchCode, branchName, "No email configured for this branch", "N", "N/A");
					skipCount++;
					continue;
				}

				String htmlContent = buildFinalHtml(rawHtml, branchCode, branchName, reportDate,
						connectedDeviceSerials);
				if (htmlContent == null) {
					log.error("[DAILY-MAIL] HTML generation failed for branch: {}. Skipping.", branchCode);
					skipCount++;
					continue;
				}

				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);
				String formattedDateTime = now.format(formatter);

				String subject = (template.getEmailSubject() != null ? template.getEmailSubject() : "Daily Report")
						+ " (" + formattedDateTime + ")";

				for (String email : emailList) {
					try {
						emailSchedulerService.sendHtmlMailUsingTransport(transport, email, subject, htmlContent); // ⬅
																													// CHANGED
						log.info("[DAILY-MAIL] Sent to {} for branch {}", email, branchCode);
						saveEmailLog(email, branchCode, branchName, subject, "Y", htmlContent);
						successCount++;
					} catch (Exception e) {
						log.error("[DAILY-MAIL] Failed to send to {} for branch {}: {}", email, branchCode,
								e.getMessage());
						saveEmailLog(email, branchCode, branchName, subject, "N", "Error: " + e.getMessage());
						failCount++;
					}
				}
			}

			String result = String.format("Daily report job completed. Success=%d | Skip=%d | Fail=%d", successCount,
					skipCount, failCount);
			log.info("[DAILY-MAIL] {}", result);
			return result;

		} catch (Exception e) {
			log.error("[DAILY-MAIL] sendBranchReports fatal error: {}", e.getMessage(), e);
			return "Error: " + e.getMessage();
		} finally {
			emailSchedulerService.closeTransport(transport); // ⬅ NEW — end me connection band
		}
	}
	// ─────────────────────────────────────────────
	// Overload — Single branch (for testing)
	// ─────────────────────────────────────────────

	public String sendBranchReports(String singleBranchCode, String overrideEmail) {
		javax.mail.Transport transport = null;
		try {
			com.doritech.api.Entity.ResponseEntity responseEntity = branchMasterService.getAllHOAndZones();

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
			System.out.println("Branch List :" + branchList);
			if (branchList == null || branchList.isEmpty()) {
				return "No branches found.";
			}

			EmailTemplateEntity template = resolveTemplate("Daily", 0);
			if (template == null)
				return "Template not found.";

			String rawHtml = emailTemplateLoader.loadTemplate(template.getEmailBody());
			if (rawHtml == null || rawHtml.isBlank())
				return "Template HTML empty.";

			String reportDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			for (BranchMasterEntity branch : branchList) {
				if (!branch.getBranchCode().equals(singleBranchCode))
					continue;

				String branchCode = branch.getBranchCode();
				String branchName = branch.getBranchDesc();

				// SKIP CHECK — 0 active devices wali branch skip (test mode mein bhi)
				List<String> connectedDeviceSerials = getConnectedDeviceSerials(branchCode);
				if (connectedDeviceSerials.isEmpty()) {
					log.info("[DAILY-MAIL][TEST] Branch {} has 0 active devices. Skipping.", branchCode);
					return "Skipped: Branch " + branchCode + " has 0 active devices. No email sent.";
				}

				List<String> emailList = new ArrayList<>();
				if (overrideEmail != null && !overrideEmail.isBlank()) {
					emailList.add(overrideEmail);
				} else {
					emailList = collectEmails(branch);
				}

				if (emailList.isEmpty())
					return "No email found for branch: " + branchCode;

				// Pass already-fetched connectedDeviceSerials to avoid double DB call
				String htmlContent = buildFinalHtml(rawHtml, branchCode, branchName, reportDate,
						connectedDeviceSerials);
				if (htmlContent == null)
					return "HTML generation failed for: " + branchCode;

				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);
				String formattedDateTime = now.format(formatter);

				String subject = (template.getEmailSubject() != null ? template.getEmailSubject() : "Daily Report")
						+ " (" + formattedDateTime + ")";

				transport = emailSchedulerService.openTransport(); // ⬅ NEW — ek hi baar login

				for (String email : emailList) {
					emailSchedulerService.sendHtmlMailUsingTransport(transport, email, subject, htmlContent); // ⬅
																												// CHANGED
					log.info("[DAILY-MAIL][TEST] Sent to {} for branch {}", email, branchCode);
				}

				return "Sent successfully to: " + String.join(", ", emailList);
			}

			return "Branch not found: " + singleBranchCode;

		} catch (Exception e) {
			log.error("[DAILY-MAIL] Test send error: {}", e.getMessage(), e);
			return "Error: " + e.getMessage();
		} finally {
			emailSchedulerService.closeTransport(transport); // ⬅ NEW — connection band
		}
	}

	// ─────────────────────────────────────────────
	// Get all ACTIVE device serials under a branch/zone
	// ─────────────────────────────────────────────

	private List<String> getConnectedDeviceSerials(String branchCode) {
		List<String> deviceSerials = new ArrayList<>();

		try {
			com.doritech.api.Entity.ResponseEntity branchResponse = branchMasterService.getAllChildren(branchCode);

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> children = branchResponse.getPayload() != null
					? (List<BranchMasterEntity>) branchResponse.getPayload()
					: new ArrayList<>();

			for (BranchMasterEntity branch : children) {
				DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(branch.getBranchCode());

				if (device != null && "active".equalsIgnoreCase(device.getStatus())) {
					deviceSerials.add(device.getDeviceSerialNo());
					log.debug("[DEVICE] Found active device: {} for branch: {}", device.getDeviceSerialNo(),
							branch.getBranchCode());
				}
			}

			log.info("[DEVICE] Total active devices for branch {}: {}", branchCode, deviceSerials.size());

		} catch (Exception e) {
			log.error("[DEVICE] Error fetching devices for branch {}: {}", branchCode, e.getMessage());
		}

		return deviceSerials;
	}

	// ─────────────────────────────────────────────
	// HTML Builder — accepts pre-fetched connectedDeviceSerials
	// ─────────────────────────────────────────────

	public String buildFinalHtml(String rawHtml, String branchCode, String branchName, String reportDate,
			List<String> connectedDeviceSerials) {
		try {
			// Fetch branch details to check type
			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);

			if (branch != null && "HEAD OFFICE".equalsIgnoreCase(branch.getBranchType())) {
				return buildHeadOfficeFinalHtml(rawHtml, branchCode, branchName, reportDate, connectedDeviceSerials);
			} else {
				return buildZonalFinalHtml(rawHtml, branchCode, branchName, reportDate, connectedDeviceSerials);
			}

		} catch (Exception e) {
			log.error("[DAILY-MAIL] buildFinalHtml error for {}: {}", branchCode, e.getMessage(), e);
			return null;
		}
	}

	private String buildZonalFinalHtml(String rawHtml, String branchCode, String branchName, String reportDate,
			List<String> connectedDeviceSerials) {
		try {
			ResponseEntity response = smsDetailsService.getCountsByZoneStatusFast(branchCode);
			if (response == null || response.getPayload() == null) {
				log.warn("[DAILY-MAIL] No counts data for branchCode={}", branchCode);
				return null;
			}

			@SuppressWarnings("unchecked")
			Map<String, Map<String, Long>> payload = (Map<String, Map<String, Long>>) response.getPayload();

			long totalBranches = connectedDeviceSerials.size();

			log.info("[DAILY-MAIL] branchCode={} | totalBranches={}", branchCode, totalBranches);

			String overviewRows = buildOverviewRows(payload, totalBranches, branchCode);
			String pendingRows = buildPendingRows(connectedDeviceSerials);
			// ─────────────────────────────────────────────
			// Fix 1: buildZonalFinalHtml — add zone_sections replacement
			// ─────────────────────────────────────────────
			return rawHtml.replace("{{zonal_office}}", safe(branchName != null ? branchName : branchCode))
					.replace("{{recipient_name}}", safe(branchName != null ? branchName : branchCode))
					.replace("{{report_date}}", reportDate).replace("{{overview_rows}}", overviewRows)
					.replace("{{pending_rows}}", pendingRows).replace("{{zone_sections}}", "")
					.replace("{{phone}}", "+91-XXXXXXXXXX");

		} catch (Exception e) {
			log.error("[DAILY-MAIL] buildZonalFinalHtml error for {}: {}", branchCode, e.getMessage(), e);
			return null;
		}
	}

	private String buildHeadOfficeFinalHtml(String rawHtml, String branchCode, String branchName, String reportDate,
			List<String> connectedDeviceSerials) {
		try {
			// Fetch all direct children (ZONEs) of this HEAD OFFICE
			ResponseEntity childResponse = branchMasterService.getAllChildren(branchCode);
			List<BranchMasterEntity> allChildren = (childResponse.getPayload() != null)
					? (List<BranchMasterEntity>) childResponse.getPayload()
					: Collections.emptyList();

			// Filter only ZONE type children
			List<BranchMasterEntity> zones = allChildren.stream()
					.filter(b -> "ZONE".equalsIgnoreCase(b.getBranchType()) && branchCode.equals(b.getParentCode()))
					.collect(Collectors.toList());

			if (zones.isEmpty()) {
				log.warn("[DAILY-MAIL] No ZONEs found under HEAD OFFICE branchCode={}", branchCode);
				return null;
			}

			// Build one section per Zone
			StringBuilder allZoneSections = new StringBuilder();

			for (BranchMasterEntity zone : zones) {
				String zoneName = zone.getBranchDesc();
				String zoneCode = zone.getBranchCode();

				ResponseEntity zoneCountResponse = smsDetailsService.getCountsByZoneStatusFast(zoneCode);
				if (zoneCountResponse == null || zoneCountResponse.getPayload() == null) {
					log.warn("[DAILY-MAIL] No counts for zone={}", zoneCode);
					continue;
				}

				@SuppressWarnings("unchecked")
				Map<String, Map<String, Long>> payload = (Map<String, Map<String, Long>>) zoneCountResponse
						.getPayload();

				// Get connected serials for this zone's branches
				List<String> zoneSerials = allChildren.stream()
						.filter(b -> "BRANCH".equalsIgnoreCase(b.getBranchType()) && zoneCode.equals(b.getParentCode()))
						.map(b -> {
							DeviceMasterEntity device = deviceMasterRepository.findBybranchCode(b.getBranchCode());
							return (device != null && !"inactive".equalsIgnoreCase(device.getStatus()))
									? device.getDeviceSerialNo()
									: null;
						}).filter(Objects::nonNull).collect(Collectors.toList());

				long totalBranches = zoneSerials.size();
				String overviewRows = buildOverviewRows(payload, totalBranches, zoneCode);
				String pendingRows = buildPendingRows(zoneSerials);

				// Build one zone section block
				allZoneSections.append(buildZoneSectionHtml(zoneName, overviewRows, pendingRows));
			}

			// ─────────────────────────────────────────────
			// Fix 2: buildHeadOfficeFinalHtml — strip {{#if_zone}}...{{/if_zone}} block
			// ─────────────────────────────────────────────

			// Strip the entire {{#if_zone}} block (including its inner content)
			// before doing any other replacements
			String cleanedHtml = rawHtml
					.replaceAll("(?s)<!--\\s*\\{\\{#if_zone\\}\\}\\s*-->.*?<!--\\s*\\{\\{/if_zone\\}\\}\\s*-->", "");
			return cleanedHtml.replace("{{zonal_office}}", safe(branchName != null ? branchName : branchCode))
					.replace("{{recipient_name}}", safe(branchName != null ? branchName : branchCode))
					.replace("{{report_date}}", reportDate).replace("{{zone_sections}}", allZoneSections.toString())
					.replace("{{phone}}", "+91-XXXXXXXXXX");

		} catch (Exception e) {
			log.error("[DAILY-MAIL] buildHeadOfficeFinalHtml error for {}: {}", branchCode, e.getMessage(), e);
			return null;
		}
	}

	private String buildZoneSectionHtml(String zoneName, String overviewRows, String pendingRows) {
		return
		"<div style='margin-top:28px;'>"
				+ "  <div style='font-family:Arial,sans-serif;font-size:11px;font-weight:700;letter-spacing:1.2px;"
				+ "       text-transform:uppercase;background:#e3f2fd;border-left:4px solid #028BD7;"
				+ "       padding:8px 14px;color:#0267a0;margin-bottom:10px;'>" + "    🏢 " + safe(zoneName)
				+ "  </div>" +

				"  <div style='font-size:10px;font-weight:700;letter-spacing:1.2px;text-transform:uppercase;"
				+ "       color:#028BD7;margin-bottom:9px;text-decoration:underline;'>Overview</div>" +

				// Overview Table — class attributes add kiye (drh-scroll-wrap, drh-table, drh-table-ov)
				"  <div class='drh-scroll-wrap' style='border-radius:8px;overflow:hidden;border:1.5px solid #d0e8f5;margin-bottom:18px;'>"
				+ "    <table class='drh-table drh-table-ov' width='100%' cellpadding='0' cellspacing='0' style='width:100%;table-layout:fixed;border-collapse:collapse;font-size:11px;'>"
				+ "      <thead><tr>"
				+ "        <th style='width:16%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Product</th>"
				+ "        <th style='width:12%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Total<br/>Branches</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>System<br/>Installed</th>"
				+ "        <th style='width:12%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Working</th>"
				+ "        <th style='width:16%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Partially<br/>Working</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Not<br/>Working</th>"
				+ "        <th style='width:12%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>No Info</th>"
				+ "      </tr></thead>" + "      <tbody>" + overviewRows + "</tbody>" + "    </table>" + "  </div>" +

				"  <div style='font-size:10px;font-weight:700;letter-spacing:1.2px;text-transform:uppercase;"
				+ "       color:#028BD7;margin-bottom:9px;text-decoration:underline;'>Pending Issues</div>" +

				// Pending Table — class attributes add kiye (drh-scroll-wrap, drh-table, drh-table-pd)
				"  <div class='drh-scroll-wrap' style='border-radius:8px;overflow:hidden;border:1.5px solid #d0e8f5;margin-bottom:10px;'>"
				+ "    <table class='drh-table drh-table-pd' width='100%' cellpadding='0' cellspacing='0' style='width:100%;table-layout:fixed;border-collapse:collapse;font-size:11px;'>"
				+ "      <thead><tr>"
				+ "        <th style='width:10%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Ticket<br/>No.</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Branch<br/>Name</th>"
				+ "        <th style='width:10%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Product</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Alert</th>"
				+ "        <th style='width:12%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>S.I.<br/>Name</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Dt. of<br/>First Info</th>"
				+ "        <th style='width:14%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Reminder<br/>Sent On</th>"
				+ "        <th style='width:12%;background:#028BD7;color:#fff;padding:9px 6px;text-align:left;font-size:8.5px;letter-spacing:0.4px;text-transform:uppercase;word-wrap:break-word;'>Current<br/>Status</th>"
				+ "      </tr></thead>" + "      <tbody>" + pendingRows + "</tbody>" + "    </table>" + "  </div>"
				+ "</div>";
	}
		/**
	 * Backward-compatible overload — fetches connectedDeviceSerials internally. Use
	 * this only if calling buildFinalHtml directly without pre-fetched serials.
	 */
	public String buildFinalHtml(String rawHtml, String branchCode, String branchName, String reportDate) {
		List<String> connectedDeviceSerials = getConnectedDeviceSerials(branchCode);
		return buildFinalHtml(rawHtml, branchCode, branchName, reportDate, connectedDeviceSerials);
	}

	// ─────────────────────────────────────────────
	// Overview Table Rows Builder
	// ─────────────────────────────────────────────
	private String buildOverviewRows(Map<String, Map<String, Long>> payload, long totalBranches, String branchCode) {
	    StringBuilder sb = new StringBuilder();

	    // Fetch device wise total count — this is now the source of truth for
	    // which products exist, instead of a hardcoded array.
	    ResponseEntity deviceCountResponse = deviceMasterService.getDeviceWiseTotalCount(branchCode);
	    Map<String, Long> deviceWiseCounts = (deviceCountResponse.getPayload() != null)
	            ? (Map<String, Long>) deviceCountResponse.getPayload()
	            : Collections.emptyMap();

	    if (deviceWiseCounts.isEmpty()) {
	        log.warn("[buildOverviewRows] No products found in deviceWiseCounts for branchCode={}", branchCode);
	        return sb.toString();
	    }

	    // Build the product list dynamically from the API response, sorted
	    // alphabetically for stable/consistent ordering in the report.
	    List<String> products = deviceWiseCounts.keySet().stream()
	            .sorted()
	            .collect(Collectors.toList());

	    for (String product : products) {
	        Map<String, Long> counts = payload.getOrDefault(product, new HashMap<>());

	        long working = counts.getOrDefault("Working", 0L);
	        long partiallyWorking = counts.getOrDefault("Partially Working", 0L);
	        long notWorking = counts.getOrDefault("Not Working", 0L);
	        long noInfo = counts.getOrDefault("No Info", 0L);

	        long sum = working + partiallyWorking + notWorking + noInfo;

	        long deviceCount = deviceWiseCounts.getOrDefault(product, -1L);

	        // Record mismatches in ExceptionLog for later audit/tracking,
	        // but the row is still included in the report regardless — it is never skipped.
	        if (deviceCount == -1L) {
	            String msg = "Product not found in deviceWiseCounts: " + product + " | branchCode=" + branchCode;
	            log.warn("[buildOverviewRows] {}", msg);
	            saveMismatchLog("PRODUCT_NOT_FOUND", msg, branchCode);
	        } else if (sum != deviceCount) {
	            String msg = "Count mismatch for product: " + product
	                    + " | Working+Partial+NotWorking+NoInfo=" + sum
	                    + " | DeviceWiseCount=" + deviceCount
	                    + " | Difference=" + Math.abs(sum - deviceCount)
	                    + " | branchCode=" + branchCode;
	            log.warn("[buildOverviewRows] {}", msg);
	            saveMismatchLog("COUNT_MISMATCH", msg, branchCode);
	        }
                                                                                                                                                                                                                                                                                        
	        sb.append("<tr style=\"border-top:1px solid #eef5fb;\">")
	                .append("<td data-label=\"Product\" style=\"padding:10px 13px;color:#2a3a4a;\">").append(product)
	                .append("</td>")
	                .append("<td data-label=\"Total Branches\" style=\"padding:10px 13px;color:#2a3a4a;\">")
	                .append(totalBranches).append("</td>")
	                .append("<td data-label=\"System Installed\" style=\"padding:10px 13px;color:#2a3a4a;\">")
	                .append(sum).append("</td>")
	                .append("<td data-label=\"Working\" style=\"padding:10px 13px;color:#27ae60;font-weight:700;\">")
	                .append(working).append("</td>")
	                .append("<td data-label=\"Partially Working\" style=\"padding:10px 13px;color:#f39c12;font-weight:700;\">")
	                .append(partiallyWorking).append("</td>")
	                .append("<td data-label=\"Not Working\" style=\"padding:10px 13px;color:#e74c3c;font-weight:700;\">")
	                .append(notWorking).append("</td>")
	                .append("<td data-label=\"No Info\" style=\"padding:10px 13px;color:#7f8c8d;font-weight:700;\">")
	                .append(noInfo).append("</td>").append("</tr>\n");
	    }

	    return sb.toString();
	}

	/**
	 * Persists a mismatch/anomaly detected while building the overview rows
	 * into the ExceptionLog table, so it can be audited later without
	 * blocking or skipping the actual report row.
	 */
	private void saveMismatchLog(String exceptionType, String message, String branchCode) {
	    try {
	        ExceptionLog exLog = new ExceptionLog();
	        exLog.setExceptionType(exceptionType);
	        exLog.setExceptionMessage(message);
	        exLog.setClassName(this.getClass().getName());
	        exLog.setMethodName("buildOverviewRows");
	        exLog.setRequestUrl(branchCode);
	        exLog.setRequestMethod("DAILY SCHEDULER");
	        exceptionLogRepository.save(exLog);
	    } catch (Exception e) {
	        log.error("[buildOverviewRows] Failed to save mismatch to ExceptionLog: {}", e.getMessage());
	    }
	}
	// ─────────────────────────────────────────────
	// Pending Issues Table Rows
	// ─────────────────────────────────────────────

	private String buildPendingRows(List<String> deviceSerials) {
		try {
			List<Map<String, Object>> tickets = ticketService.getOpenTicketSummaryForToday(deviceSerials);

			if (tickets == null || tickets.isEmpty()) {
				return "<tr><td colspana=\"8\" style=\"text-align:center;color:#888;padding:16px;font-size:12px;\">No Pending Issues</td></tr>";
			}

			StringBuilder sb = new StringBuilder();
			for (Map<String, Object> t : tickets) {

				List<String> alertList = (List<String>) t.get("alerts");
				String alertStr = (alertList != null && !alertList.isEmpty()) ? String.join("<br>", alertList) : "N/A";

				List<Map<String, Object>> reminderList = (List<Map<String, Object>>) t.get("reminderSentOn");
				String reminderDates = "N/A";
				if (reminderList != null && !reminderList.isEmpty()) {
					List<Map<String, Object>> sortedReminders = new ArrayList<>(reminderList);
					sortedReminders.sort(Comparator.comparingInt(r -> (int) r.get("reminderIndex")));
					reminderDates = sortedReminders.stream().map(r -> r.get("reminderIndex") + ". " + r.get("sentAt"))
							.collect(java.util.stream.Collectors.joining("<br>"));
				}

				String status = safe((String) t.get("currentStatus"));
				String escBg = "#f3e5f5";
				String escColor = "#7b1fa2";
				if (status.contains("L1")) {
					escBg = "#fff3e0";
					escColor = "#e65100";
				} else if (status.contains("L2")) {
					escBg = "#fce4ec";
					escColor = "#c62828";
				} else if (status.contains("L3")) {
					escBg = "#f3e5f5";
					escColor = "#6a1b9a";
				} else if (status.equals("NEW TICKET")) {
					escBg = "#e8f5e9";
					escColor = "#2e7d32";
				} else if (status.equals("VENDOR ASSIGNED")) {
					escBg = "#e3f2fd";
					escColor = "#1565c0";
				}

				sb.append("<tr style=\"border-top:1px solid #eef5fb;\">").append(
						"<td data-label=\"Ticket No.\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append("<span style=\"font-family:Montserrat,sans-serif;font-weight:700;font-size:10px;")
						.append("background:#e8f4fc;color:#028BD7;padding:2px 7px;border-radius:4px;\">")
						.append(safe((String) t.get("ticketNo"))).append("</span></td>")
						.append("<td data-label=\"Branch Name\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append(safe((String) t.get("deviceName"))).append("</td>")
						.append("<td data-label=\"Product\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append(safe((String) t.get("product"))).append("</td>")
						.append("<td data-label=\"Alert\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append(safe(alertStr)).append("</td>")
						.append("<td data-label=\"S.I. Name\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append(safe((String) t.get("SiName"))).append("</td>")
						.append("<td data-label=\"Dt. of First Info\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;\">")
						.append(safe((String) t.get("dateOfFirstInformation"))).append("</td>")
						.append("<td data-label=\"Reminder Sent On\" style=\"padding:10px 13px;color:#2a3a4a;vertical-align:middle;font-size:11px;\">")
						.append(safe(reminderDates)).append("</td>")
						.append("<td data-label=\"Current Status\" style=\"padding:10px 13px;vertical-align:middle;\">")
						.append("<span style=\"font-size:10px;background:").append(escBg).append(";color:")
						.append(escColor).append(";padding:3px 10px;border-radius:10px;font-weight:700;")
						.append("font-family:Montserrat,sans-serif;\">").append(status).append("</span></td>")
						.append("</tr>\n");
			}
			return sb.toString();

		} catch (Exception e) {
			log.error("[DAILY-MAIL] buildPendingRows error: {}", e.getMessage());
			return "<tr><td colspan=\"8\" style=\"text-align:center;color:#e74c3c;padding:14px;\">Error loading pending issues</td></tr>";
		}
	}

	// ─────────────────────────────────────────────
	// Template Resolver
	// ─────────────────────────────────────────────

	private EmailTemplateEntity resolveTemplate(String level, int reminderIndex) {
		try {
			Optional<EmailTemplateEntity> exact = emailTemplateRepository
					.findByEscalationLevelAndEscalationReminderAndIsActiveTrue(level, reminderIndex);
			if (exact.isPresent())
				return exact.get();

			Optional<EmailTemplateEntity> fallback = emailTemplateRepository
					.findByEscalationLevelAndEscalationReminderAndIsActiveTrue(level, 0);
			if (fallback.isPresent())
				return fallback.get();

			log.warn("[DAILY-MAIL] No template: level='{}', reminder={}", level, reminderIndex);
			return null;

		} catch (Exception e) {
			log.error("[DAILY-MAIL] resolveTemplate error: {}", e.getMessage(), e);
			return null;
		}
	}

	// ─────────────────────────────────────────────
	// Latest SMS — sms_details first, fallback history
	// ─────────────────────────────────────────────

	private SmsDetailsEntity getLatestSms(String panelSerialNo) {
		SmsDetailsEntity smsDetailsEntity = smsDetailsRepository.getLatestRecord(panelSerialNo);

		if (smsDetailsEntity != null)
			return smsDetailsEntity;

		List<SmsDetailsHistoryEntity> historyList = smsDetailsHistoryRepository
				.findTopByPanelSerialNoOrderByDateTimeDesc(panelSerialNo, 1);

		if (historyList == null || historyList.isEmpty())
			return null;

		SmsDetailsEntity fallbackSms = new SmsDetailsEntity();
		BeanUtils.copyProperties(historyList.get(0), fallbackSms);
		return fallbackSms;
	}

	// ─────────────────────────────────────────────
	// determineGroupStatus
	// ─────────────────────────────────────────────

	private String determineGroupStatus(List<DeviceZoneDetailsEntity> zones, SmsDetailsEntity sms) {
		long total = zones.size();
		long working = zones.stream().filter(z -> isZoneWorking(z, sms)).count();

		if (working == total)
			return "Working";
		if (working == 0)
			return "Not Working";
		return "Partially Working";
	}

	private boolean isZoneWorking(DeviceZoneDetailsEntity zone, SmsDetailsEntity sms) {
		return true; // placeholder — apna existing logic yahan paste karo
	}

	// ─────────────────────────────────────────────
	// Email Log Save
	// ─────────────────────────────────────────────

	private void saveEmailLog(String emailTo, String branchCode, String branchName, String subject, String status,
			String bodyText) {
		try {
			EmailLogEntity logEntry = new EmailLogEntity();
			logEntry.setEmailIdTo(emailTo);
			logEntry.setBranchCode(branchCode);
			logEntry.setBranchName(branchName);
			logEntry.setSubject(subject);
			logEntry.setStatus(status);
			logEntry.setBodyText(bodyText);
			logEntry.setDatetime(LocalDateTime.now());
			logEntry.setType("SCHEDULER");
			logEntry.setPriority("N");

			emailLogRepository.save(logEntry);
			log.info("[EMAIL LOG] Saved | Branch: {} | Status: {}", branchCode, status);

		} catch (Exception e) {
			log.error("[EMAIL LOG ERROR] Failed to save log for branch: {} | Error: {}", branchCode, e.getMessage());
		}
	}

	// ─────────────────────────────────────────────
	// Email Collection
	// ─────────────────────────────────────────────

	private List<String> collectEmails(BranchMasterEntity branch) {
		List<String> list = new ArrayList<>();
		addIfNotEmpty(list, branch.getEmail());
		addIfNotEmpty(list, branch.getEmail2());
		addIfNotEmpty(list, branch.getEmail3());
		addIfNotEmpty(list, branch.getEmail4());
		addIfNotEmpty(list, branch.getEmail5());
		addIfNotEmpty(list, branch.getEmail6());
		return list;
	}

	private void addIfNotEmpty(List<String> list, String val) {
		if (val != null && !val.isBlank())
			list.add(val.trim());
	}

	// ─────────────────────────────────────────────
	// Null-safe helper
	// ─────────────────────────────────────────────

	private String safe(Object val) {
		return val != null ? val.toString().trim() : "-";
	}
}