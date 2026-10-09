package com.doritech.api.DTO;

import java.util.List;

import com.doritech.api.Entity.CallLogEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.SmsLogEntity;

public class OpenAlertCommunicationDTO {

    private int smsDetailsId;

    private String deviceSerialNo;
    private String deviceName;
    private String bankName;
    private String branchName;
    private String vendorName;
    private String alarmType;
    private String alertType;
    private String alarmTime;
    private String status; // Open
    private String timeDiff;
	private EmailLogEntity emailLog;
    private SmsLogEntity smsLog;
    private CallLogEntity callLog;
    
    
	public int getSmsDetailsId() {
		return smsDetailsId;
	}
	public void setSmsDetailsId(int smsDetailsId) {
		this.smsDetailsId = smsDetailsId;
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
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimeDiff() {
		return timeDiff;
	}
	public void setTimeDiff(String timeDiff) {
		this.timeDiff = timeDiff;
	}
	public EmailLogEntity getEmailLog() {
		return emailLog;
	}
	public void setEmailLog(EmailLogEntity emailLog) {
		this.emailLog = emailLog;
	}
	public SmsLogEntity getSmsLog() {
		return smsLog;
	}
	public void setSmsLog(SmsLogEntity smsLog) {
		this.smsLog = smsLog;
	}
	public CallLogEntity getCallLog() {
		return callLog;
	}
	public void setCallLog(CallLogEntity callLog) {
		this.callLog = callLog;
	}
	
    
    
    
}
