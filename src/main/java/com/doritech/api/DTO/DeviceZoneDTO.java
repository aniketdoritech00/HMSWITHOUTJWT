package com.doritech.api.DTO;
public class DeviceZoneDTO {
	private String deviceId;
	private String zoneOriginalName;
	private String zoneDesiredName;
	private String alertType;
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
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getZoneDesiredName() {
		return zoneDesiredName;
	}
	public void setZoneDesiredName(String zoneDesiredName) {
		this.zoneDesiredName = zoneDesiredName;
	}
	
}
