package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Dynamic_Configuration")
public class DynamicConfigurationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "DEVICE_ID")
	private String deviceId;
	@Column(name = "ZONE")
	private String zone;
	@Column(name = "MESSAGE") 
	private String message;
	@Column(name = "ALERTTYPE")
	private String alertType;
	@Column(name = "DEVICE_ZONE_ID")
	private String deviceZoneId;
	@Column(name = "alert_flag")
	private String alertFlag;
	
	
	
	public String getDeviceZoneId() {
		return deviceZoneId;
	}
	public void setDeviceZoneId(String deviceZoneId) {
		this.deviceZoneId = deviceZoneId;
	}
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
	public String getAlertFlag() {
		return alertFlag;
	}
	public void setAlertFlag(String alertFlag) {
		this.alertFlag = alertFlag;
	}
	
	
	
	

}