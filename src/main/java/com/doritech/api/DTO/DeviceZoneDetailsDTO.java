package com.doritech.api.DTO;

import java.util.List;

public class DeviceZoneDetailsDTO {
	private String id;
	private String deviceId;
	private String zoneOriginalName;
	private String zoneDesiredName;
	private String alertCode;
	private String alertType;
	private String inputType;
	private String primaryFlag;
	private String message;
	private String alertFlag;
	private String criticalAlertFlag;
	private String vendorId;
	private String vendors;

	private List<NonSiaZoneCameraMappingRequestDto> cameraMappings;

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAlertFlag() {
		return alertFlag;
	}

	public void setAlertFlag(String alertFlag) {
		this.alertFlag = alertFlag;
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

	public List<NonSiaZoneCameraMappingRequestDto> getCameraMappings() {
		return cameraMappings;
	}

	public void setCameraMappings(List<NonSiaZoneCameraMappingRequestDto> cameraMappings) {
		this.cameraMappings = cameraMappings;
	}

}
