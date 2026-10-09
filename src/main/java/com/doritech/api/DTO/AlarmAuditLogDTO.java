package com.doritech.api.DTO;

import javax.persistence.Column;

public class AlarmAuditLogDTO {

	private int id;

	private String deviceId;

	private String activeZone;

	private String remarks;

	private String remarks2;

	private String dateTime;

	private String user;

	private String userName;

	private String vendorName;

	private String branchCode;

	private String mobile;

	private String address;

	private String alarmedDevice;

	private String zoneName;
	
	private String alertDateTime;
	
	

	public String getAlertDateTime() {
		return alertDateTime;
	}

	public void setAlertDateTime(String alertDateTime) {
		this.alertDateTime = alertDateTime;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getAlarmedDevice() {
		return alarmedDevice;
	}

	public void setAlarmedDevice(String alarmedDevice) {
		this.alarmedDevice = alarmedDevice;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getActiveZone() {
		return activeZone;
	}

	public void setActiveZone(String activeZone) {
		this.activeZone = activeZone;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRemarks2() {
		return remarks2;
	}

	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}

}