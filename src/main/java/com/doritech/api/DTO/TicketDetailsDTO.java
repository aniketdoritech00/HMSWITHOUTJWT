package com.doritech.api.DTO;

import java.util.Date;

public class TicketDetailsDTO {
 
	private String ticketId;
    private String branchCode;
	private String branchName;
	private String bankName;
	private String vendorName;
	private String alertType;
	private String panelSerialNumber;
	private String dateTime;
	private String remarks;
	private String completeSMS;
	private String status;
	private String whatsAppStatus;
	private String mailStatus;
	private String mailTime;
	
	public String getWhatsAppStatus() {
		return whatsAppStatus;
	}
	public void setWhatsAppStatus(String whatsAppStatus) {
		this.whatsAppStatus = whatsAppStatus;
	}
	public String getMailStatus() {
		return mailStatus;
	}
	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}
	public String getMailTime() {
		return mailTime;
	}
	public void setMailTime(String mailTime) {
		this.mailTime = mailTime;
	}
	public String getTicketId() {
		return ticketId;
	}
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getCompleteSMS() {
		return completeSMS;
	}
	public void setCompleteSMS(String completeSMS) {
		this.completeSMS = completeSMS;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPanelSerialNumber() {
		return panelSerialNumber;
	}
	public void setPanelSerialNumber(String panelSerialNumber) {
		this.panelSerialNumber = panelSerialNumber;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "TicketDetailsDTO [ticketId=" + ticketId + ", branchCode=" + branchCode + ", branchName=" + branchName
				+ ", bankName=" + bankName + ", vendorName=" + vendorName + ", alertType=" + alertType
				+ ", panelSerialNumber=" + panelSerialNumber + ", dateTime=" + dateTime + ", remarks=" + remarks
				+ ", completeSMS=" + completeSMS + ", status=" + status + ", whatsAppStatus=" + whatsAppStatus
				+ ", mailStatus=" + mailStatus + ", mailTime=" + mailTime + "]";
	}
	
	

}
