package com.doritech.api.DTO;

public class DeviceMasterLogResponse {
	
	private String deviceName;
	private String panelSerialNo;
	private String lastUpdatedDateTime;
	private String notSyncedFrom;
	private String branchName;
	private String bankName;
	private String vendorName;
	private String mobile;
	
	
	
	
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}
	public void setLastUpdatedDateTime(String lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getPanelSerialNo() {
		return panelSerialNo;
	}
	public void setPanelSerialNo(String panelSerialNo) {
		this.panelSerialNo = panelSerialNo;
	}
	
	public String getNotSyncedFrom() {
		return notSyncedFrom;
	}
	public void setNotSyncedFrom(String notSyncedFrom) {
		this.notSyncedFrom = notSyncedFrom;
	}
	@Override
	public String toString() {
		return "deviceMasterLogResponse [deviceName=" + deviceName + ", panelSerialNo=" + panelSerialNo
				+ ", lastUpdatedDateTime=" + lastUpdatedDateTime + ", notSyncedFrom=" + notSyncedFrom
				+ "]";
	}
}
