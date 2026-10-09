package com.doritech.api.DTO;

public class BulkSmsRequestDTO {
    private String branchCode;
    private String activeZone;
    private Long escalationLogId;
    private String mobileNumber;   // single number jispe sms jayega
    private int count;             // kitne sms bhejne hain
    private int waitMinutes;       // har sms ke beech kitne minute ka gap
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getActiveZone() {
		return activeZone;
	}
	public void setActiveZone(String activeZone) {
		this.activeZone = activeZone;
	}
	public Long getEscalationLogId() {
		return escalationLogId;
	}
	public void setEscalationLogId(Long escalationLogId) {
		this.escalationLogId = escalationLogId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getWaitMinutes() {
		return waitMinutes;
	}
	public void setWaitMinutes(int waitMinutes) {
		this.waitMinutes = waitMinutes;
	}
    
    
    
}