package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "device_zone_details")
public class DeviceZoneDetailsEntity {

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "device_id")
	private String deviceId;
	@Column(name = "zone_original_name")
	private String zoneOriginalName;
	@Column(name = "zone_desired_name")
	private String zoneDesiredName;
	@Column(name = "alert_code")
	private String alertCode;
	@Column(name = "alert_type")
	private String alertType;
	@Column(name = "input_type")
	private String inputType;
	@Column(name = "primary_flag")
	private String primaryFlag;
	@Column(name = "critical_alert_flag")
	private String criticalAlertFlag;
	@Column(name = "vendor_id")
	private String vendorId;
	@Column(name = "vendors")
	private String vendors;

	public String getVendors() {
		return vendors;
	}

	public void setVendors(String vendors) {
		this.vendors = vendors;
	}

	public String getCriticalAlertFlag() {
		return criticalAlertFlag;
	}

	public void setCriticalAlertFlag(String criticalAlertFlag) {
		this.criticalAlertFlag = criticalAlertFlag;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getZoneOriginalName() {
		return zoneOriginalName;
	}

	public void setZoneOriginalName(String zoneOriginalName) {
		this.zoneOriginalName = zoneOriginalName;
	}

	public String getZoneDesiredName() {
		return zoneDesiredName;
	}

	public void setZoneDesiredName(String zoneDesiredName) {
		this.zoneDesiredName = zoneDesiredName;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getPrimaryFlag() {
		return primaryFlag;
	}

	public void setPrimaryFlag(String primaryFlag) {
		this.primaryFlag = primaryFlag;
	}

	public String getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(String alertCode) {
		this.alertCode = alertCode;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}



}
