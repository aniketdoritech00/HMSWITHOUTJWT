package com.doritech.api.DTO;

import java.util.HashMap;
import java.util.List;

import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.SmsDetailsEntity;

public class MailReportDTO {
	
	private String branchName;
	private String zonalOffice;
	private String parentBranchName;
	private String vendorName;
	private String vendorEmail;
	private List<DeviceZoneDetailsEntity> deviceZoneEntity;
	private List<String> status;
	private String makeAndModel;
	private String installation;
	private String lifeCycle;
	private List<HashMap<String, String>> smsDto;
	
	
	public  List<HashMap<String, String>> getSmsDto() {
		return smsDto;
	}
	public void setSmsDto( List<HashMap<String, String>> smsDto) {
		this.smsDto = smsDto;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getZonalOffice() {
		return zonalOffice;
	}
	public void setZonalOffice(String zonalOffice) {
		this.zonalOffice = zonalOffice;
	}
	public String getParentBranchName() {
		return parentBranchName;
	}
	public void setParentBranchName(String parentBranchName) {
		this.parentBranchName = parentBranchName;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getVendorEmail() {
		return vendorEmail;
	}
	public void setVendorEmail(String vendorEmail) {
		this.vendorEmail = vendorEmail;
	}
	
	
	public List<String> getStatus() {
		return status;
	}
	public void setStatus(List<String> status) {
		this.status = status;
	}
	public String getMakeAndModel() {
		return makeAndModel;
	}
	public void setMakeAndModel(String makeAndModel) {
		this.makeAndModel = makeAndModel;
	}
	public String getLifeCycle() {
		return lifeCycle;
	}
	public void setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
	}
	
	
	public String getInstallation() {
		return installation;
	}
	public void setInstallation(String installation) {
		this.installation = installation;
	}
	public List<DeviceZoneDetailsEntity> getDeviceZoneEntity() {
		return deviceZoneEntity;
	}
	public void setDeviceZoneEntity(List<DeviceZoneDetailsEntity> deviceZoneEntity) {
		this.deviceZoneEntity = deviceZoneEntity;
	}
	
	@Override
	public String toString() {
		return "MailReportDTO [branchName=" + branchName + ", zonalOffice=" + zonalOffice + ", parentBranchName="
				+ parentBranchName + ", vendorName=" + vendorName + ", vendorEmail=" + vendorEmail
				+ ", deviceZoneEntity=" + deviceZoneEntity + ", status=" + status + ", makeAndModel=" + makeAndModel
				+ ", installation=" + installation + ", lifeCycle=" + lifeCycle + ", smsDto=" + smsDto + "]";
	}
	
	
	
	

}