package com.doritech.api.DTO;

import java.util.Date;

public class AlertApiResponse {
	
	
	private Long id;
	private String branchDesc;
	private String branchCode;
	private String bankName;
	private String panelSerialNumber;
	private String alertMessage;
	private Date dateTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBranchDesc() {
		return branchDesc;
	}
	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPanelSerialNumber() {
		return panelSerialNumber;
	}
	public void setPanelSerialNumber(String panelSerialNumber) {
		this.panelSerialNumber = panelSerialNumber;
	}
	public String getAlertMessage() {
		return alertMessage;
	}
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	
}