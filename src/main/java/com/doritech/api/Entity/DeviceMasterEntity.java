package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "device_master")
public class DeviceMasterEntity {
	@Id
	@Column(name = "device_id")
	private String deviceId;
	@Column(name = "device_name")
	private String deviceName;
	@Column(name = "device_serial_no")
	private String deviceSerialNo;
	@Column(name = "device_type")
	private String deviceType;
	@Column(name = "device_location")
	private String deviceLocation;
	@Column(name = "phone_no")
	private String phoneNumber;
	@Column(name = "protection_type")
	private String protectionType;
	@Column(name = "user_no")
	private String userNo;
	@Column(name = "user_password")
	private String userPassword;
	@Column(name = "email1")
	private String email1;
	@Column(name = "product_type")
	private String productType;
	@Column(name = "passcode")
	private String passcode;
	@Column(name = "address")
	private String address;
	@Column(name = "support_no")
	private String supportNo;
	@Column(name = "branch_code")
	private String branchCode;
	@Column(name = "vendor_name")
	private String vendorName;
	@Column(name = "vendor_email")
	private String vendorEmail;
	@Column(name = "vendor_mobile")
	private String vendorMobile;

	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "created_date")
	private String createdDate;
	@Column(name = "updated_by")
	private String updatedBy;
	@Column(name = "updated_date")
	private String updatedDate;
	@Column(name = "updation_status")
	private String updationStatus;
	@Column(name = "count")
	private int count;

	private String status;

	private String voltage1;
	private String voltage2;
	private String voltage3;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}

	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdationStatus() {
		return updationStatus;
	}

	public void setUpdationStatus(String updationStatus) {
		this.updationStatus = updationStatus;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getVendorMobile() {
		return vendorMobile;
	}

	public void setVendorMobile(String vendorMobile) {
		this.vendorMobile = vendorMobile;
	}

	@Override
	public String toString() {
		return "DeviceMasterEntity [deviceId=" + deviceId + ", deviceName=" + deviceName + ", deviceSerialNo="
				+ deviceSerialNo + ", deviceType=" + deviceType + ", deviceLocation=" + deviceLocation
				+ ", phoneNumber=" + phoneNumber + ", protectionType=" + protectionType + ", userNo=" + userNo
				+ ", userPassword=" + userPassword + ", email1=" + email1 + ", productType=" + productType
				+ ", passcode=" + passcode + ", address=" + address + ", supportNo=" + supportNo + ", branchCode="
				+ branchCode + ", vendorName=" + vendorName + ", vendorEmail=" + vendorEmail + ", vendorMobile="
				+ vendorMobile + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", updatedBy="
				+ updatedBy + ", updatedDate=" + updatedDate + ", updationStatus=" + updationStatus + ", count=" + count
				+ ", status=" + status + "]";
	}

}
