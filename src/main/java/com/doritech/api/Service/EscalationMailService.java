package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.EmailTemplateEntity;
import com.doritech.api.Entity.EscalationLog;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.EmailTemplateRepository;
import com.doritech.api.Repository.EscalationMailLogRepository;

/**
 * EscalationMailService
 *
 * RECIPIENT LOGIC (FIXED): escalationMatrix.getRecipient() mein comma-separated
 * types hote hain (e.g. "VENDOR,ZONE,HO") Har type ke liye alag
 * resolveRecipients() call karo → sab merge karo TO mein daalo. Agar
 * critical=true hai → same logic (matrix se jo bhi types aaye) applies.
 * Hard-code kuch nahi — matrix hi decide karta hai.
 *
 * CC LOGIC: L1 → no CC L2 → VENDOR in CC L3 → VENDOR + ZONE in CC (CC mein wahi
 * types jo TO mein already nahi hain)
 *
 * FORWARDED MAIL LOGIC: isForwardedMail=true → Subject mein "FWD: " prefix
 * forwardedHistory → previous mail HTML inject hoti hai body mein
 */
@Service
public class EscalationMailService {

	private static final Logger log = LoggerFactory.getLogger(EscalationMailService.class);
	private static final String FROM_MAIL = "alerts@doritech.in";

	private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
	private static final DateTimeFormatter REMINDER_FMT = DateTimeFormatter.ofPattern("dd MMM yy , hh.mm a");

	private final EscalationMailLogRepository escalationLogRepository;
	private final EmailLogRepo emailLogRepository;
	private final JavaMailSender mailSender;
	private final EmailRecipientResolver recipientResolver;
	private final EmailTemplateRepository emailTemplateRepository;
	private final EmailTemplateLoader emailTemplateLoader;

	public EscalationMailService(EscalationMailLogRepository escalationLogRepository, EmailLogRepo emailLogRepository,
			JavaMailSender mailSender, EmailRecipientResolver recipientResolver,
			EmailTemplateRepository emailTemplateRepository, EmailTemplateLoader emailTemplateLoader) {

		this.escalationLogRepository = escalationLogRepository;
		this.emailLogRepository = emailLogRepository;
		this.mailSender = mailSender;
		this.recipientResolver = recipientResolver;
		this.emailTemplateRepository = emailTemplateRepository;
		this.emailTemplateLoader = emailTemplateLoader;
	}

	// =========================================================================
	// MAIN METHOD
	// =========================================================================
	public void sendIfEligible(String ticketNo, String escalationLevel, Long alertGeneratedId, Long escalationLogId,
			int noOfReminders, int reminderGapMin, String branchCode, String branchName, String branchAddress,
			String contactPerson, long elapsedMinutes, String zoneDesiredName, String alertType, int reminderIndex,
			String recipientType, // comma-separated: "VENDOR,ZONE,HO"
			String productType, String dtFirstInfo, String vendors, boolean isForwardedMail, String forwardedHistory,
			String ccMail1, String ccMail2, String ccMail3, String ccMail4) {
		log.info("╔══════════════════════════════════════════════════════════════");
		log.info("║ [MAIL] START → ticket='{}' level='{}' reminderIdx={} escLogId={}", ticketNo, escalationLevel,
				reminderIndex, escalationLogId);
		log.info("║ [MAIL] isFwdMail={} | recipientType='{}' | branchCode='{}'", isForwardedMail, recipientType,
				branchCode);
		log.info("╚══════════════════════════════════════════════════════════════");

		try {
			// ─────────────────────────────────────────────────────────────────
			// STEP 1: Resolve ALL TO recipients from matrix recipientType
			// recipientType = comma-separated e.g. "VENDOR,ZONE,HO"
			// Har type ke liye resolve karo → sab merge karo
			// ─────────────────────────────────────────────────────────────────
			String toEmails = resolveAllRecipients(recipientType, branchCode, vendors, ticketNo, escalationLevel);

			if (toEmails == null) {
				log.warn("[MAIL] ✘ No TO recipients resolved for ticket='{}'. Skipping.", ticketNo);
				return;
			}
			log.info("[MAIL] ✔ Resolved TO: '{}'", toEmails);

			// ─────────────────────────────────────────────────────────────────
			// STEP 2: Resolve CC based on level
			// ─────────────────────────────────────────────────────────────────
			String ccEmails = resolveCcEmails(escalationLevel, branchCode, vendors, toEmails, ccMail1, ccMail2, ccMail3,
					ccMail4);
			log.info("[MAIL] CC: '{}'", ccEmails != null ? ccEmails : "none");

			// ─────────────────────────────────────────────────────────────────
			// STEP 3: Reminder times string (for email body)
			// ─────────────────────────────────────────────────────────────────
			List<EscalationLog> allLogs = escalationLogRepository
					.findByTicketNoAndEscLevelOrderByReminderIndexAsc(ticketNo, escalationLevel);
			String reminderTimes = buildReminderTimesString(allLogs);
			log.info("[MAIL] ReminderTimes built: '{}'", reminderTimes.isEmpty() ? "(none)" : reminderTimes);

			// ─────────────────────────────────────────────────────────────────
			// STEP 4: Guard — mail count & gap (same as before, kept for safety)
			// ─────────────────────────────────────────────────────────────────
			long mailSentCount = countMailSent(allLogs);
			log.info("[MAIL] Guard check → mailSentCount={} noOfReminders={}", mailSentCount, noOfReminders);

			if (mailSentCount > noOfReminders) {
				log.info("[MAIL] ✘ Limit reached ({} > {}). Skipping ticket='{}'.", mailSentCount, noOfReminders,
						ticketNo);
				return;
			}
			if (reminderGapMin > 0 && mailSentCount > 0) {
				EscalationLog lastLog = getLastMailLog(allLogs);
				if (lastLog != null) {
					long gap = ChronoUnit.MINUTES.between(lastLog.getSentAt(), LocalDateTime.now());
					log.info("[MAIL] Gap check → gap={}min required={}min", gap, reminderGapMin);
					if (gap < reminderGapMin) {
						log.info("[MAIL] ✘ Too soon ({} < {} min). Skipping ticket='{}'.", gap, reminderGapMin,
								ticketNo);
						return;
					}
				}
			}
			log.info("[MAIL] ✔ Guards passed.");

			// ─────────────────────────────────────────────────────────────────
			// STEP 5: Load template
			// ─────────────────────────────────────────────────────────────────
			EmailTemplateEntity template = resolveTemplate(escalationLevel, reminderIndex);
			if (template == null) {
				log.error("[MAIL] ✘ No template found for level='{}' reminderIdx={}. Skipping.", escalationLevel,
						reminderIndex);
				return;
			}
			log.info("[MAIL] ✔ Template found: id={} subject='{}'", template.getId(), template.getEmailSubject());

			// ─────────────────────────────────────────────────────────────────
			// STEP 6: Load HTML body from template
			// ─────────────────────────────────────────────────────────────────
			String rawHtml = emailTemplateLoader.loadTemplate(template.getEmailBody());
			if (rawHtml == null ) {
				log.error("[MAIL] ✘ HTML body load failed for template id={}. Skipping.", template.getId());
				return;
			}
			log.info("[MAIL] ✔ HTML template loaded. Length={} chars", rawHtml.length());

			// ─────────────────────────────────────────────────────────────────
			// STEP 7: Build subject (FWD: prefix agar isForwardedMail=true)
			// ─────────────────────────────────────────────────────────────────
			String subject = buildSubject(template.getEmailSubject(), ticketNo, escalationLevel, reminderIndex,
					isForwardedMail);
			log.info("[MAIL] Subject: '{}'", subject);

			// ─────────────────────────────────────────────────────────────────
			// STEP 8: Replace placeholders
			// ─────────────────────────────────────────────────────────────────
			String body = replacePlaceholders(rawHtml, ticketNo, escalationLevel, alertGeneratedId, branchCode,
					branchName, branchAddress, contactPerson, elapsedMinutes, reminderIndex, zoneDesiredName, alertType,
					productType, dtFirstInfo, reminderTimes, vendors);
			log.info("[MAIL] ✔ Placeholders replaced. Body length={} chars", body.length());

			// ─────────────────────────────────────────────────────────────────
			// STEP 9: Inject forwarded history (agar FWD mail hai)
			// ─────────────────────────────────────────────────────────────────
			if (isForwardedMail && forwardedHistory != null ) {
				log.info("[MAIL][FWD] Injecting forwarded history. History length={} chars", forwardedHistory.length());
				body = appendForwardedHistory(body, forwardedHistory);
				log.info("[MAIL][FWD] ✔ History injected. New body length={} chars", body.length());
			} else {
				log.info("[MAIL] Not a forwarded mail — no history injection.");
			}

			// ─────────────────────────────────────────────────────────────────
			// STEP 10: Send mail via SMTP
			// ─────────────────────────────────────────────────────────────────
			log.info("[MAIL] Dispatching → TO:'{}' CC:'{}' Subject:'{}'", toEmails,
					ccEmails != null ? ccEmails : "none", subject);

			if (!dispatchMail(toEmails, ccEmails, subject, body, ticketNo)) {
				log.error("[MAIL] ✘ SMTP dispatch failed for ticket='{}'.", ticketNo);
				return;
			}
			log.info("[MAIL] ✔ SMTP dispatch SUCCESS for ticket='{}'.", ticketNo);

			// ─────────────────────────────────────────────────────────────────
			// STEP 11: Save Email_Log record
			// ─────────────────────────────────────────────────────────────────
			saveEmailLog(escalationLogId, ticketNo, toEmails, ccEmails, subject, body, branchCode, branchName);

			log.info("╔══════════════════════════════════════════════════════════════");
			log.info("║ [MAIL] ✔ COMPLETE → ticket='{}' level='{}' escLogId={}", ticketNo, escalationLevel,
					escalationLogId);
			log.info("╚══════════════════════════════════════════════════════════════");

		} catch (Exception e) {
			log.error("[MAIL] ✘ FATAL ERROR ticket='{}' level='{}': {}", ticketNo, escalationLevel, e.getMessage(), e);
		}
	}

	// =========================================================================
	// CORE FIX: resolveAllRecipients()
	//
	// PEHLE (GALAT):
	// recipientResolver.resolveRecipients(recipientType, ...)
	// → recipientType = "VENDOR,ZONE,HO" → sirf puri string ek type samajh li
	// → sirf ek ya koi bhi mail nahi gayi
	//
	// AB (SAHI):
	// recipientType ko comma se split karo → ["VENDOR", "ZONE", "HO"]
	// → har ek ke liye alag resolve karo
	// → sab merge karo deduplicate karke
	// → matrix se jo bhi aaya sab cover ho gaya — kuch hardcode nahi
	// =========================================================================
	private String resolveAllRecipients(String recipientType, String branchCode, String vendors, String ticketNo,
			String escalationLevel) {
		log.info("[MAIL][RECIPIENT] Resolving all recipients → rawType='{}' branchCode='{}' vendors='{}'",
				recipientType, branchCode, vendors);

		if (recipientType == null ) {
			log.warn("[MAIL][RECIPIENT] recipientType is null/blank for ticket='{}' level='{}'.", ticketNo,
					escalationLevel);
			return null;
		}

		String[] types = recipientType.split(",");
		List<String> resolvedEmails = new ArrayList<>();

		for (String rawType : types) {
			String type = rawType.trim();
			if (type.isEmpty())
				continue;

			try {
				String emails = recipientResolver.resolveRecipients(type, branchCode, vendors);
				log.info("[MAIL][RECIPIENT] type='{}' → resolved='{}'", type, emails);

				if (emails != null ) {
					// Multiple emails ho sakti hain comma-separated — sab add karo
					for (String email : emails.split(",")) {
						String e = email.trim();
						if (!e.isEmpty() && !resolvedEmails.contains(e)) {
							resolvedEmails.add(e);
							log.info("[MAIL][RECIPIENT]   + Added: '{}'", e);
						} else if (!e.isEmpty()) {
							log.info("[MAIL][RECIPIENT]   ~ Duplicate skipped: '{}'", e);
						}
					}
				} else {
					log.warn("[MAIL][RECIPIENT] type='{}' → No emails found.", type);
				}
			} catch (Exception e) {
				log.error("[MAIL][RECIPIENT] type='{}' resolve failed: {}", type, e.getMessage(), e);
			}
		}

		if (resolvedEmails.isEmpty()) {
			log.warn("[MAIL][RECIPIENT] No emails resolved at all for recipientType='{}'", recipientType);
			return null;
		}

		String merged = String.join(",", resolvedEmails);
		log.info("[MAIL][RECIPIENT] ✔ Final TO ({}): '{}'", resolvedEmails.size(), merged);
		return merged;
	}

	// =========================================================================
	// FORWARDED HISTORY SECTION
	// =========================================================================

	/**
	 * Forwarded mail history ko styled HTML section ke roop mein inject karta hai.
	 *
	 * FIX: bodySection (HTML) ko DIRECTLY embed karo — zero escaping Sirf meta
	 * fields (From/To/Subject/Date) escape karo (plain text hain)
	 */
	private String appendForwardedHistory(String currentBody, String forwardedHistory) {
		try {
			log.info("[MAIL][FWD] Building forwarded history cards...");

			String[] blocks = forwardedHistory.split("-------- Forwarded Message");
			StringBuilder historyCards = new StringBuilder();
			int cardCount = 0;

			for (String block : blocks) {
				if (block == null || block.trim().isEmpty())
					continue;

				// ── Header extract ─────────────────────────────────────────
				String header = "";
				int headerEnd = block.indexOf("--------");
				if (headerEnd >= 0) {
					header = block.substring(0, headerEnd).replace("(", "").replace(")", "").trim();
					block = block.substring(headerEnd + 8);
				}

				// ── Meta vs Body split ─────────────────────────────────────
				String metaSection = "";
				String bodySection = "";

				int bodyMarker = block.indexOf("Body    :");
				if (bodyMarker >= 0) {
					metaSection = block.substring(0, bodyMarker).trim();
					bodySection = block.substring(bodyMarker + "Body    :".length()).trim();

					// Trailing "---" separator hatao
					int sep = bodySection.lastIndexOf("---");
					if (sep >= 0 && bodySection.substring(sep + 3).trim().isEmpty()) {
						bodySection = bodySection.substring(0, sep).trim();
					}
				} else {
					metaSection = block.trim();
				}

				log.info("[MAIL][FWD] Card #{} → header='{}' metaLen={} bodyLen={}", cardCount + 1, header,
						metaSection.length(), bodySection.length());

				// metaSection = plain text → HTML escape + <br/> for newlines
				String metaHtml = escapeForHtml(metaSection).replace("\n", "<br/>");

				// bodySection = already HTML → embed as-is
				historyCards.append(buildForwardedMailCard(header, metaHtml, bodySection));
				cardCount++;
			}

			if (historyCards.length() == 0) {
				log.warn("[MAIL][FWD] No cards built from forwardedHistory.");
				return currentBody;
			}

			log.info("[MAIL][FWD] ✔ {} card(s) built.", cardCount);

			// ── Outer wrapper ──────────────────────────────────────────────
			String fwdSection = "<br/><br/>"
					+ "<div style='margin-top:24px;font-family:Arial,sans-serif;font-size:13px;'>"

					// Header bar
					+ "<table width='100%' cellpadding='0' cellspacing='0' border='0'"
					+ "  style='margin-bottom:12px;'><tr>"
					+ "  <td style='background-color:#fdf3f2;border-left:5px solid #c0392b;"
					+ "             padding:10px 16px;border-radius:3px;'>"
					+ "    <span style='font-size:14px;font-weight:bold;color:#c0392b;'>"
					+ "      &#128231; Forwarded Mail History" + "    </span>"
					+ "    <span style='font-size:11px;color:#888;margin-left:10px;'>"
					+ "      (Previous escalation mails sent for this ticket)" + "    </span>" + "   </td>"
					+ " </tr>"
					+ "</table>"

					+ historyCards.toString() + "</div>";

			// Inject before </body>
			if (currentBody != null && currentBody.toLowerCase().contains("</body>")) {
				log.info("[MAIL][FWD] Injecting before </body> tag.");
				return currentBody.replace("</body>", fwdSection + "</body>");
			}

			log.info("[MAIL][FWD] No </body> found — appending at end.");
			return (currentBody != null ? currentBody : "") + fwdSection;

		} catch (Exception e) {
			log.error("[MAIL][FWD] appendForwardedHistory failed: {}", e.getMessage(), e);
			return currentBody;
		}
	}

	/**
	 * Single forwarded mail card (styled HTML)
	 */
	private String buildForwardedMailCard(String header, String metaHtml, String bodySection) {
		return "<div style='margin-bottom:20px;border:1px solid #dddddd;" + "border-radius:5px;overflow:hidden;"
				+ "box-shadow:0 1px 4px rgba(0,0,0,0.08);'>"

				// Blue header bar
				+ "<div style='background-color:#2980b9;color:#ffffff;"
				+ "padding:8px 14px;font-size:12px;font-weight:bold;'>" + "  &#x21BA;&nbsp;"
				+ (header.isEmpty() ? "Previous Mail" : escapeForHtml(header)) + "</div>"

				// Grey meta table
				+ "<div style='background-color:#f7f7f7;padding:10px 14px;" + "border-bottom:1px solid #e0e0e0;'>"
				+ "<table cellpadding='3' cellspacing='0' border='0' width='100%'"
				+ "style='font-size:12px;color:#555;'>" + buildMetaTableRows(metaHtml) + "</table></div>"

				// White body area — bodySection is RAW HTML, no escaping
				+ "<div style='padding:14px;background-color:#ffffff;'>"
				+ "<div style='border-left:3px solid #cccccc;padding-left:12px;color:#444;'>" + bodySection // ← DIRECT
																											// EMBED —
																											// zero
																											// escaping
				+ "</div></div>"

				+ "</div>";
	}

	/** Meta string → HTML table rows (label | value). */
	private String buildMetaTableRows(String metaHtml) {
		if (metaHtml == null)
			return "";
		StringBuilder rows = new StringBuilder();
		for (String line : metaHtml.split("<br/>")) {
			if (line == null || line.trim().isEmpty())
				continue;
			int colon = line.indexOf(":");
			if (colon > 0) {
				String label = line.substring(0, colon).trim();
				String value = line.substring(colon + 1).trim();
				rows.append("<tr>")
						.append("<td style='font-weight:bold;color:#333;white-space:nowrap;"
								+ "padding-right:12px;vertical-align:top;width:70px;'>")
						.append(label).append("</td>").append("<td style='color:#555;'>").append(value).append("</td>")
						.append("</tr>");
			} else {
				rows.append("<tr><td colspan='2'>").append(line).append("</td></tr>");
			}
		}
		return rows.toString();
	}

	/**
	 * ONLY for plain-text fields (From/To/Subject/Date). NEVER call this on HTML
	 * body content.
	 */
	private String escapeForHtml(String text) {
		if (text == null)
			return "";
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}

	// =========================================================================
	// CC RESOLUTION
	// CC mein wahi types jo TO mein already nahi hain (duplication avoid)
	// =========================================================================
	private String resolveCcEmails(String level, String branchCode, String vendors, String toEmails, String ccMail1,
			String ccMail2, String ccMail3, String ccMail4) {
		if (level == null)
			return null;

		log.info("[MAIL][CC] Resolving CC for level='{}' branchCode='{}'", level, branchCode);

		try {
			String levelBasedCc = null;

			switch (level.toUpperCase()) {
			case "L1":
				log.info("[MAIL][CC] L1 → No level-based CC.");
				break;

			case "L2":
				String v2 = recipientResolver.resolveRecipients("VENDOR", branchCode, vendors);
				levelBasedCc = filterOutDuplicates(v2, toEmails);
				log.info("[MAIL][CC] L2 → VENDOR='{}' after dedup='{}'", v2, levelBasedCc);
				break;

			case "L3":
				String vHO = recipientResolver.resolveRecipients("VENDOR", branchCode, vendors);
				String zHO = recipientResolver.resolveRecipients("ZONE", branchCode, vendors);
				String merged = mergeEmails(vHO, zHO);
				levelBasedCc = filterOutDuplicates(merged, toEmails);
				log.info("[MAIL][CC] L3 → VENDOR='{}' ZONE='{}' merged='{}' dedup='{}'", vHO, zHO, merged,
						levelBasedCc);
				break;

			default:
				log.warn("[MAIL][CC] Unknown level '{}' → No level-based CC.", level);
				break;
			}

// ── Matrix se CC mails collect karo ──────────────────────────────
			List<String> matrixCcs = new ArrayList<>();
			for (String m : new String[] { ccMail1, ccMail2, ccMail3, ccMail4 }) {
				if (m != null && !m.trim().isEmpty()) {
					matrixCcs.add(m.trim());
				}
			}
			log.info("[MAIL][CC] Matrix DB CC mails: {}", matrixCcs);

// ── Level-based + Matrix CC merge karo ───────────────────────────
			String matrixCcStr = matrixCcs.isEmpty() ? null : String.join(",", matrixCcs);
			String combined = mergeEmails(levelBasedCc, matrixCcStr);

// ── TO ke duplicates filter out karo ─────────────────────────────
			String finalCc = filterOutDuplicates(combined, toEmails);
			log.info("[MAIL][CC] ✔ Final CC: '{}'", finalCc);
			return (finalCc != null && !finalCc.isEmpty()) ? finalCc : null;

		} catch (Exception e) {
			log.error("[MAIL][CC] Error resolving CC: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * toAdd se woh emails filter karo jo alreadyIn mein already hain. Duplicate
	 * mails avoid karne ke liye (same email TO aur CC dono mein na jaye).
	 */
	private String filterOutDuplicates(String toAdd, String alreadyIn) {
		if (toAdd == null || toAdd.trim().isEmpty())
			return null;
		if (alreadyIn == null || alreadyIn.trim().isEmpty())
			return toAdd;

		List<String> existing = List.of(alreadyIn.split(","));
		List<String> filtered = new ArrayList<>();

		for (String email : toAdd.split(",")) {
			String e = email.trim();
			if (!e.isEmpty() && !existing.contains(e)) {
				filtered.add(e);
			}
		}
		return filtered.isEmpty() ? null : String.join(",", filtered);
	}

	private String mergeEmails(String... lists) {
		StringBuilder sb = new StringBuilder();
		for (String s : lists) {
			if (s != null && !s.trim().isEmpty()) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(s.trim());
			}
		}
		return sb.length() > 0 ? sb.toString() : null;
	}

	// =========================================================================
	// REMINDER TIMES STRING
	// =========================================================================
	private String buildReminderTimesString(List<EscalationLog> allLogs) {
		if (allLogs == null || allLogs.isEmpty())
			return "";
		List<String> list = new ArrayList<>();
		int count = 1;
		for (EscalationLog l : allLogs) {
			if (l.getReminderIndex() > 0 && l.getSentAt() != null) {
				String fmt = l.getSentAt().format(REMINDER_FMT).toUpperCase().replace("AM", "A.M.").replace("PM",
						"P.M.");
				list.add("(" + count++ + ") " + fmt);
			}
		}
		if (list.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder("REMINDER(S) SENT ON\n\n");
		list.forEach(r -> sb.append(r).append("\n"));
		return sb.toString().trim();
	}

	// =========================================================================
	// TEMPLATE RESOLUTION
	// =========================================================================
	private EmailTemplateEntity resolveTemplate(String level, int reminderIndex) {
		try {
			log.info("[MAIL][TEMPLATE] Looking for level='{}' reminderIdx={}", level, reminderIndex);

			Optional<EmailTemplateEntity> exact = emailTemplateRepository
					.findByEscalationLevelAndEscalationReminderAndIsActiveTrue(level, reminderIndex);
			if (exact.isPresent()) {
				log.info("[MAIL][TEMPLATE] ✔ Exact match found id={}", exact.get().getId());
				return exact.get();
			}

			log.info("[MAIL][TEMPLATE] No exact match. Trying fallback reminderIdx=0...");
			Optional<EmailTemplateEntity> fallback = emailTemplateRepository
					.findByEscalationLevelAndEscalationReminderAndIsActiveTrue(level, 0);
			if (fallback.isPresent()) {
				log.info("[MAIL][TEMPLATE] ✔ Fallback found id={}", fallback.get().getId());
				return fallback.get();
			}

			log.warn("[MAIL][TEMPLATE] ✘ No template found for level='{}' reminderIdx={}", level, reminderIndex);
			return null;

		} catch (Exception e) {
			log.error("[MAIL][TEMPLATE] Error: {}", e.getMessage(), e);
			return null;
		}
	}

	// =========================================================================
	// SUBJECT BUILDER
	// =========================================================================
	private String buildSubject(String template, String ticketNo, String level, int reminderIndex,
			boolean isForwardedMail) {
		String base = (template != null ? template : "Escalation Alert - {{ticket_no}}")
				.replace("{{ticket_no}}", nullSafe(ticketNo)).replace("{{escalation_level}}", nullSafe(level))
				.replace("{{reminder_index}}", String.valueOf(reminderIndex));
		return isForwardedMail ? "FWD: " + base : base;
	}

	// =========================================================================
	// PLACEHOLDER REPLACEMENT (FIXED: Replace "Security Gadget" with "{{product}} gadget")
	// =========================================================================
	private String replacePlaceholders(String rawHtml, String ticketNo, String escalationLevel, Long alertGeneratedId,
			String branchCode, String branchName, String branchAddress, String contactPerson, long elapsedMinutes,
			int reminderIndex, String zoneDesiredName, String alertType, String productType, String dtFirstInfo,
			String reminderTimes, String vendors) {
		if (rawHtml == null)
			return "<p>Template not available.</p>";

		long hours = elapsedMinutes / 60, mins = elapsedMinutes % 60;
		String firstInfoValue = nullSafe(dtFirstInfo)
				+ (reminderTimes != null && !reminderTimes.isEmpty() ? "\n" + reminderTimes : "");

		String result = rawHtml
				.replace("{{ticket_no}}", nullSafe(ticketNo))
				.replace("{{escalation_level}}", nullSafe(escalationLevel))
				.replace("{{alert_id}}", alertGeneratedId != null ? String.valueOf(alertGeneratedId) : "N/A")
				.replace("{{branch_code}}", nullSafe(branchCode))
				.replace("{{branch_name}}", nullSafe(branchName))
				.replace("{{branch_address}}", nullSafe(branchAddress))
				.replace("{{contact_person}}", nullSafe(vendors))
				.replace("{{si_name}}", nullSafe(vendors))
				.replace("{{si_company}}", nullSafe(vendors))
				.replace("{{elapsed_time}}", hours + "h " + mins + "m")
				.replace("{{elapsed_hours}}", String.valueOf(hours))
				.replace("{{elapsed_minutes}}", String.valueOf(mins))
				.replace("{{reminder_index}}", String.valueOf(reminderIndex))
				.replace("{{zone_name}}", nullSafe(zoneDesiredName))
				.replace("{{alert_type}}", nullSafe(alertType))
				.replace("{{alert}}", nullSafe(alertType))
				.replace("{{product}}", nullSafe(productType))
				.replace("{{first_info_dt}}", firstInfoValue)
				.replace("{{phone}}", "");

		
		String zoneDesiredNameValue = nullSafe(zoneDesiredName);
		
		result = result.replace("⚠ Security Gadget Issue", "⚠ " + zoneDesiredNameValue + " Gadget Issue");
		result = result.replace("⚠ SECURITY GADGET ISSUE", "⚠ " + zoneDesiredNameValue.toUpperCase() + " GADGET ISSUE");
		
		result = result.replace("issues in the Security Gadget(s)", "issues in the " + zoneDesiredNameValue + " gadget(s)");
		result = result.replace("issues in the Security Gadget", "issues in the " + zoneDesiredNameValue + " gadget");
		result = result.replace("issues in Security Gadget(s)", "issues in " + zoneDesiredNameValue + " gadget(s)");
		result = result.replace("issues in Security Gadget", "issues in " + zoneDesiredNameValue + " gadget");
		
		result = result.replaceAll("(?i)Security\\s+Gadget\\(s\\)", zoneDesiredNameValue + " gadget(s)");
		result = result.replaceAll("(?i)Security\\s+Gadget", zoneDesiredNameValue + " gadget");

		log.info("[MAIL][PLACEHOLDER] Replaced 'Security Gadget' with '{} gadget' in template.", zoneDesiredNameValue);
		
		return result;
	}

	// =========================================================================
	// DISPATCH MAIL
	// =========================================================================
	private boolean dispatchMail(String toEmails, String ccEmails, String subject, String body, String ticketNo) {
		try {
			log.info("[MAIL][SMTP] Creating MIME message...");
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper h = new MimeMessageHelper(msg, false, "UTF-8");
			h.setTo(toEmails.split(","));
			h.setFrom(FROM_MAIL);
			h.setSubject(subject + " (Ticket: " + ticketNo + ")");
			h.setText(body, true);

			if (ccEmails != null && !ccEmails.trim().isEmpty()) {
				h.setCc(ccEmails.split(","));
				log.info("[MAIL][SMTP] CC set: '{}'", ccEmails);
			}

			mailSender.send(msg);
			log.info("[MAIL][SMTP] ✔ Mail sent → TO:'{}' CC:'{}' Ticket:'{}'", toEmails,
					ccEmails != null ? ccEmails : "none", ticketNo);
			return true;

		} catch (Exception e) {
			log.error("[MAIL][SMTP] ✘ Send failed for ticket='{}': {}", ticketNo, e.getMessage(), e);
			return false;
		}
	}

	// =========================================================================
	// SAVE EMAIL LOG
	// =========================================================================
	private void saveEmailLog(Long escalationLogId, String ticketNo, String toEmails, String ccEmails, String subject,
			String body, String branchCode, String branchName) {
		try {
			EmailLogEntity el = new EmailLogEntity();
			el.setEscalationLogId(escalationLogId);
			el.setFromEmail(FROM_MAIL);
			el.setEmailIdTo(toEmails);
			el.setEmailIdCc(ccEmails);
			el.setSubject(subject);
			el.setBodyText(body);
			el.setDatetime(LocalDateTime.now());
			el.setStatus("S");
			el.setBranchCode(branchCode);
			el.setBranchName(branchName);
			el.setType("ESCALATION");
			emailLogRepository.save(el);
			log.info("[MAIL][LOG] ✔ Email_Log saved → escLogId={} ticket='{}'", escalationLogId, ticketNo);
		} catch (Exception e) {
			log.error("[MAIL][LOG] ✘ Email_Log save failed → escLogId={}: {}", escalationLogId, e.getMessage(), e);
		}
	}

	// =========================================================================
	// HELPERS
	// =========================================================================
	private long countMailSent(List<EscalationLog> logs) {
		try {
			return logs.stream().filter(l -> hasEmailLog(l.getId())).count();
		} catch (Exception e) {
			log.error("[MAIL] countMailSent error: {}", e.getMessage(), e);
			return 0;
		}
	}

	private boolean hasEmailLog(Long id) {
		try {
			return emailLogRepository.countByEscalationLogId(id) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	private EscalationLog getLastMailLog(List<EscalationLog> logs) {
		try {
			return logs.stream().filter(l -> hasEmailLog(l.getId())).reduce((a, b) -> b).orElse(null);
		} catch (Exception e) {
			log.error("[MAIL] getLastMailLog error: {}", e.getMessage(), e);
			return null;
		}
	}

	private String nullSafe(String v) {
		return v != null ? v : "N/A";
	}
}