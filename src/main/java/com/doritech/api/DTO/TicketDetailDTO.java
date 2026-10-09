package com.doritech.api.DTO;

import java.util.List;

public class TicketDetailDTO {

	// ── Ticket Context ───────────────────────────────────────────────────────
	private String ticketNo;
	private String branchName;
	private String branchCode;
	private String product;
	private String currentStatus; // L2 ESCALATED / VENDOR ASSIGNED / OPEN
	// ── Alert Details ────────────────────────────────────────────────────────
	private String alertType;
	private String alertTime;
	private String duration;

	// ── Vendor Info ──────────────────────────────────────────────────────────
	private String vendorName;
	private String lastReminderDatetime;
	private int totalRemindersSent;
	private String firstInfoDatetime; // "12 FEB 26, 10:23 AM"
	private List<String> reminderSentOn;
	// ── Escalation Levels (L1, L2, L3) ──────────────────────────────────────
	private List<EscalationLevelDTO> escalationLevels;

	// ── Root Getters & Setters ───────────────────────────────────────────────
	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String v) {
		this.ticketNo = v;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String v) {
		this.branchName = v;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String v) {
		this.branchCode = v;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String v) {
		this.product = v;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String v) {
		this.alertType = v;
	}

	public String getFirstInfoDatetime() {
		return firstInfoDatetime;
	}

	public void setFirstInfoDatetime(String v) {
		this.firstInfoDatetime = v;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String v) {
		this.currentStatus = v;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String v) {
		this.vendorName = v;
	}

	public String getLastReminderDatetime() {
		return lastReminderDatetime;
	}

	public void setLastReminderDatetime(String v) {
		this.lastReminderDatetime = v;
	}

	public int getTotalRemindersSent() {
		return totalRemindersSent;
	}

	public void setTotalRemindersSent(int v) {
		this.totalRemindersSent = v;
	}
	
	public List<String> getReminderSentOn() {
		return reminderSentOn;
	}

	public void setReminderSentOn(List<String> reminderSentOn) {
		this.reminderSentOn = reminderSentOn;
	}

	public List<EscalationLevelDTO> getEscalationLevels() {
		return escalationLevels;
	}

	public void setEscalationLevels(List<EscalationLevelDTO> v) {
		this.escalationLevels = v;
	}

	public String getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(String alertTime) {
		this.alertTime = alertTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}