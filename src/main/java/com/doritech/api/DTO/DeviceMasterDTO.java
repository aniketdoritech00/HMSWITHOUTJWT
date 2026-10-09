package com.doritech.api.DTO;

import java.util.List;

import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.DynamicConfigurationEntity;
import com.doritech.api.Entity.SiaProtocalEntity;
import com.doritech.api.Entity.SmsDetailsEntity;

public class DeviceMasterDTO {
	private String deviceId;
	private String deviceName;
	private String deviceType;
	private String deviceLocation;
	private String phoneNumber;
	private String protectionType;
	private String userNo;
	private String userPassword;
	private String email1;
	private String productType;
	private String passcode;
	private String address;
	private String supportNo;
	private String branchCode;
	private String deviceSerialNo;
	private String vendorName;
	private String vendorEmail;
	private String vendorMobile;
	private String status;
	private List<SiaProtocalEntity> siaProtocol;
	
	private String voltage1;
	private String voltage2;
	private String voltage3;
	
	private List<String> lastSyncedTimes;
	
	
	
	

	public List<String> getLastSyncedTimes() {
		return lastSyncedTimes;
	}

	public void setLastSyncedTimes(List<String> lastSyncedTimes) {
		this.lastSyncedTimes = lastSyncedTimes;
	}

	public String getVoltage1() {
		return voltage1;
	}

	public void setVoltage1(String voltage1) {
		this.voltage1 = voltage1;
	}

	public String getVoltage2() {
		return voltage2;
	}

	public void setVoltage2(String voltage2) {
		this.voltage2 = voltage2;
	}

	public String getVoltage3() {
		return voltage3;
	}

	public void setVoltage3(String voltage3) {
		this.voltage3 = voltage3;
	}

	public List<SiaProtocalEntity> getSiaProtocol() {
		return siaProtocol;
	}

	public void setSiaProtocol(List<SiaProtocalEntity> siaProtocol) {
		this.siaProtocol = siaProtocol;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVendorMobile() {
		return vendorMobile;
	}

	public void setVendorMobile(String vendorMobile) {
		this.vendorMobile = vendorMobile;
	}

	private String branchName;
	private String branchAddress;
	private String bankName;
	private String lastSyncedTime;
	private String ifsc;
	private String armedFlag;
	
	
	
	
	
	public String getArmedFlag() {
		return armedFlag;
	}

	public void setArmedFlag(String armedFlag) {
		this.armedFlag = armedFlag;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	private List<DeviceZoneDetailsEntity> zoneList;
	private List<DynamicConfigurationEntity> dynamicConfigurationList;
	private SmsDetailsEntity smsDetails;
	
	
	
	


	public List<DynamicConfigurationEntity> getDynamicConfigurationList() {
		return dynamicConfigurationList;
	}

	public void setDynamicConfigurationList(List<DynamicConfigurationEntity> dynamicConfigurationList) {
		this.dynamicConfigurationList = dynamicConfigurationList;
	}

	public String getLastSyncedTime() {
		return lastSyncedTime;
	}

	public void setLastSyncedTime(String lastSyncedTime) {
		this.lastSyncedTime = lastSyncedTime;
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

	public String getBranchAddress() {
		return branchAddress;
	}

	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
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

	public SmsDetailsEntity getSmsDetails() {
		return smsDetails;
	}

	public void setSmsDetails(SmsDetailsEntity smsDetails) {
		this.smsDetails = smsDetails;
	}

	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}

	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}

	public List<DeviceZoneDetailsEntity> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<DeviceZoneDetailsEntity> zoneList) {
		this.zoneList = zoneList;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceLocation() {
		return deviceLocation;
	}

	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getProtectionType() {
		return protectionType;
	}

	public void setProtectionType(String protectionType) {
		this.protectionType = protectionType;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSupportNo() {
		return supportNo;
	}

	public void setSupportNo(String supportNo) {
		this.supportNo = supportNo;
	}

	@Override
	public String toString() {
		return "DeviceMasterDTO [deviceId=" + deviceId + ", deviceName=" + deviceName + ", deviceType=" + deviceType
				+ ", deviceLocation=" + deviceLocation + ", phoneNumber=" + phoneNumber + ", protectionType="
				+ protectionType + ", userNo=" + userNo + ", userPassword=" + userPassword + ", email1=" + email1
				+ ", productType=" + productType + ", passcode=" + passcode + ", address=" + address + ", supportNo="
				+ supportNo + ", branchCode=" + branchCode + ", deviceSerialNo=" + deviceSerialNo + ", vendorName="
				+ vendorName + ", vendorEmail=" + vendorEmail + ", vendorMobile=" + vendorMobile + ", status=" + status
				+ ", branchName=" + branchName + ", branchAddress=" + branchAddress + ", bankName=" + bankName
				+ ", lastSyncedTime=" + lastSyncedTime + ", ifsc=" + ifsc + ", armedFlag=" + armedFlag + ", zoneList="
				+ zoneList + ", dynamicConfigurationList=" + dynamicConfigurationList + ", smsDetails=" + smsDetails
				+ "]";
	}

	
	public DeviceMasterDTO(String deviceId, String deviceName, String deviceSerialNo, String deviceType,
            String deviceLocation, String phoneNumber, String protectionType, String userNo,
            String userPassword, String email1, String productType, String passcode, String address,
            String supportNo, String branchCode, String vendorName, String vendorEmail,
            String vendorMobile, String status) {
this.deviceId = deviceId;
this.deviceName = deviceName;
this.deviceSerialNo = deviceSerialNo;
this.deviceType = deviceType;
this.deviceLocation = deviceLocation;
this.phoneNumber = phoneNumber;
this.protectionType = protectionType;
this.userNo = userNo;
this.userPassword = userPassword;
this.email1 = email1;
this.productType = productType;
this.passcode = passcode;
this.address = address;
this.supportNo = supportNo;
this.branchCode = branchCode;
this.vendorName = vendorName;
this.vendorEmail = vendorEmail;
this.vendorMobile = vendorMobile;
this.status = status;
}

	public DeviceMasterDTO() {
		super();
		// TODO Auto-generated constructor stub
	}


	
}
