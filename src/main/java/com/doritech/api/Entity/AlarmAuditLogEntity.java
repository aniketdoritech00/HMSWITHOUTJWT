package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alarm_audit_log")
public class AlarmAuditLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="device_id")
	private String deviceId;
	
	@Column(name="active_zone")
	private String activeZone;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="remarks2")
	private String remarks2;
	
	@Column(name="date_time")
	private String dateTime;
	
	@Column(name="user")
	private String user;

	@Column(name="vendor_name")
	private String vendorName;
	
	@Column(name="branch_code")
	private String branchCode;
	
	@Column(name="mobile")
	private String mobile;
	
	@Column(name="address")
	private String address;
	
	@Column(name="zone_name")
	private String zoneName;
	
	@Column(name="alert_date_time")
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
