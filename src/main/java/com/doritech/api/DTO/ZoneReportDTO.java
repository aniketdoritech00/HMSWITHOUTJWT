package com.doritech.api.DTO;

import java.util.List;

import com.doritech.api.Entity.SmsDetailsEntity;

public class ZoneReportDTO {
	
	private String branchCode;
	private String branchName;
	private String bank;
	private String mobile;
	private String deviceSerialNo;
	private String parentBranch;
	
	private SmsDetailsEntity latestSms;
	
	private List<ZonesDTO> zoneDTOList;
	
	
	
	
	public SmsDetailsEntity getLatestSms() {
		return latestSms;
	}
	public void setLatestSms(SmsDetailsEntity latestSms) {
		this.latestSms = latestSms;
	}
	public String getParentBranch() {
		return parentBranch;
	}
	public void setParentBranch(String parentBranch) {
		this.parentBranch = parentBranch;
	}
	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}
	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}
	public List<ZonesDTO> getZoneDTOList() {
		return zoneDTOList;
	}
	public void setZoneDTOList(List<ZonesDTO> zoneDTOList) {
		this.zoneDTOList = zoneDTOList;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
