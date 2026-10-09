package com.doritech.api.DTO;

import java.time.LocalDateTime;
import java.util.Date;

public class NotWorkingDeviceDTO {
    private String deviceSerialNo;
    private String alertType;
    private String zone;
    private String status;
    private Date lastAlertTime;
    
	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}
	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLastAlertTime() {
		return lastAlertTime;
	}
	public void setLastAlertTime(Date lastAlertTime) {
		this.lastAlertTime = lastAlertTime;
	}
    
    
    
    
}