package com.doritech.api.DTO;

public class DynamicConfigrationDTO {

	private Long id;
	private String deviceId;
	private String zone;
	private String message;
	private String alertType;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
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
	@Override
	public String toString() {
		return "DynamicConfigrationDTO [id=" + id + ", deviceId=" + deviceId + ", zone=" + zone + ", message=" + message
				+ ", alertType=" + alertType + "]";
	}
	
}
