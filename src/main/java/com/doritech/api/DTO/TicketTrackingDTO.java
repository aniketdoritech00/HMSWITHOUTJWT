package com.doritech.api.DTO;

import java.util.List;

public class TicketTrackingDTO {

    private String ticketNo;
    private String branchName;
    private String branchCode;
    private String product;          // e.g., CCTV, FIRE
    private String alertType;        // e.g., Camera Offline, No Video Feed
    private String vendorName;
    private String dtOfFirstInformation;  // yyyy-MM-dd
    private String currentStatus;    // VENDOR ASSIGNED / L1 ESCALATED / L2 ESCALATED / RESOLVED
    private String escalationLevel;  // L1 / L2 / L3 / null
    private int    totalRemindersSent;
    private int    maxRemindersAllowed;
    private int    remindersRemaining;
    private String alertTime;
    private String duration;
 // TicketTrackingDTO mein add karo
    private List<String> reminderSentOn;   // ["1. 13 FEB 26, 10.23 A.M.", "2. 14 FEB 26, ..."]

    public List<String> getReminderSentOn()              { return reminderSentOn; }
    public void setReminderSentOn(List<String> v)        { this.reminderSentOn = v; }
    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getTicketNo()                        { return ticketNo; }
    public void   setTicketNo(String v)                { this.ticketNo = v; }

    public String getBranchName()                      { return branchName; }
    public void   setBranchName(String v)              { this.branchName = v; }

    public String getBranchCode()                      { return branchCode; }
    public void   setBranchCode(String v)              { this.branchCode = v; }

    public String getProduct()                         { return product; }
    public void   setProduct(String v)                 { this.product = v; }

    public String getAlertType()                       { return alertType; }
    public void   setAlertType(String v)               { this.alertType = v; }

    public String getVendorName()                      { return vendorName; }
    public void   setVendorName(String v)              { this.vendorName = v; }

    public String getDtOfFirstInformation()            { return dtOfFirstInformation; }
    public void   setDtOfFirstInformation(String v)    { this.dtOfFirstInformation = v; }

    public String getCurrentStatus()                   { return currentStatus; }
    public void   setCurrentStatus(String v)           { this.currentStatus = v; }

    public String getEscalationLevel()                 { return escalationLevel; }
    public void   setEscalationLevel(String v)         { this.escalationLevel = v; }

    public int  getTotalRemindersSent()                { return totalRemindersSent; }
    public void setTotalRemindersSent(int v)           { this.totalRemindersSent = v; }

    public int  getMaxRemindersAllowed()               { return maxRemindersAllowed; }
    public void setMaxRemindersAllowed(int v)          { this.maxRemindersAllowed = v; }

    public int  getRemindersRemaining()                { return remindersRemaining; }
    public void setRemindersRemaining(int v)           { this.remindersRemaining = v; }
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
	@Override
	public String toString() {
		return "TicketTrackingDTO [ticketNo=" + ticketNo + ", branchName=" + branchName + ", branchCode=" + branchCode
				+ ", product=" + product + ", alertType=" + alertType + ", vendorName=" + vendorName
				+ ", dtOfFirstInformation=" + dtOfFirstInformation + ", currentStatus=" + currentStatus
				+ ", escalationLevel=" + escalationLevel + ", totalRemindersSent=" + totalRemindersSent
				+ ", maxRemindersAllowed=" + maxRemindersAllowed + ", remindersRemaining=" + remindersRemaining
				+ ", alertTime=" + alertTime + ", duration=" + duration + ", reminderSentOn=" + reminderSentOn + "]";
	}
    
    
    
}