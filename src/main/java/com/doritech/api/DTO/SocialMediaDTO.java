package com.doritech.api.DTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


public class SocialMediaDTO {

	private Long id;
    @NotEmpty(message = "Bank name is required")
	private String bankName;
    @NotEmpty(message = "Alert Type is required")
    private String alertType;
    private String branchCode;
	private String whatsapp;
	private String sms;
	private String calling;
	private String mail;
	private String timestamp;
	private String terminal;

	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public String getWhatsapp() {
		return whatsapp;
	}
	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}
	public String getSms() {
		return sms;
	}
	public void setSms(String sms) {
		this.sms = sms;
	}
	public String getCalling() {
		return calling;
	}
	public void setCalling(String calling) {
		this.calling = calling;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	@Override
	public String toString() {
		return "SocialMediaDTO [id=" + id + ", bankName=" + bankName + ", alertType=" + alertType + ", branchCode="
				+ branchCode + ", whatsapp=" + whatsapp + ", sms=" + sms + ", calling=" + calling + ", mail=" + mail
				+ ", timestamp=" + timestamp + ", terminal=" + terminal + "]";
	}
	
	
	
	
}
