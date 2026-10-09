package com.doritech.api.Entity;

public class InputStatusDetailsEntity {

	
	private String deviceSerialNo;
	private String deviceName;
	private String deviceType;
	private String deviceLocation;
	private String mobileNo;
	private String alertType;
	private String alarmtype;
	private String alarmDescription;
	private String alarmTime;
	private String issueResolvedTime;
	private String bankName;
	private String branchName;
	private String vendorName;
	public String timeDifference;
	public String ticketId;
	private int smsId;
	
	private String ifsc;

	
	private String escalationLevel;
	private String escalationColor;

	public String getEscalationLevel() {
	    return escalationLevel;
	}
	public void setEscalationLevel(String escalationLevel) {
	    this.escalationLevel = escalationLevel;
	}
	
	
	public String getEscalationColor() {
		return escalationColor;
	}
	public void setEscalationColor(String escalationColor) {
		this.escalationColor = escalationColor;
	}
	public int getSmsId() {
		return smsId;
	}
	public void setSmsId(int smsId) {
		this.smsId = smsId;
	}
	public String getIfsc() {
		return ifsc;
	}
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	public String getTicketId() {
		return ticketId;
	}
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	
	
	
	public String getTimeDifference() {
		return timeDifference;
	}
	public void setTimeDifference(String timeDifference) {
		this.timeDifference = timeDifference;
	}
	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}
	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceLocation() {
		return deviceLocation;
	}
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getAlarmtype() {
		return alarmtype;
	}
	public void setAlarmtype(String alarmtype) {
		this.alarmtype = alarmtype;
	}
	public String getAlarmDescription() {
		return alarmDescription;
	}
	public void setAlarmDescription(String alarmDescription) {
		this.alarmDescription = alarmDescription;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getIssueResolvedTime() {
		return issueResolvedTime;
	}
	public void setIssueResolvedTime(String issueResolvedTime) {
		this.issueResolvedTime = issueResolvedTime;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Override
	public String toString() {
		return "InputStatusDetailsEntity [deviceSerialNo=" + deviceSerialNo + ", deviceName=" + deviceName
				+ ", deviceType=" + deviceType + ", deviceLocation=" + deviceLocation + ", mobileNo=" + mobileNo
				+ ", alertType=" + alertType + ", alarmtype=" + alarmtype + ", alarmDescription=" + alarmDescription
				+ ", alarmTime=" + alarmTime + ", issueResolvedTime=" + issueResolvedTime + ", bankName=" + bankName
				+ ", branchName=" + branchName + ", vendorName=" + vendorName + ", timeDifference=" + timeDifference
				+ ", ticketId=" + ticketId + "]";
	}
	
	
}
