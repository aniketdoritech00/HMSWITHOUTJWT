package com.doritech.api.DTO;

import java.util.List;

public class DeviceMasterDO {
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
    private String branchName;
    private String branchAddress;
    private String bankName;
    private String ifsc;
    private String armedFlag;
    private List<String> lastSyncedTimes; // List to store the last 5 synced times

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceLocation() { return deviceLocation; }
    public void setDeviceLocation(String deviceLocation) { this.deviceLocation = deviceLocation; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProtectionType() { return protectionType; }
    public void setProtectionType(String protectionType) { this.protectionType = protectionType; }

    public String getUserNo() { return userNo; }
    public void setUserNo(String userNo) { this.userNo = userNo; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getEmail1() { return email1; }
    public void setEmail1(String email1) { this.email1 = email1; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getPasscode() { return passcode; }
    public void setPasscode(String passcode) { this.passcode = passcode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSupportNo() { return supportNo; }
    public void setSupportNo(String supportNo) { this.supportNo = supportNo; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getDeviceSerialNo() { return deviceSerialNo; }
    public void setDeviceSerialNo(String deviceSerialNo) { this.deviceSerialNo = deviceSerialNo; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getVendorEmail() { return vendorEmail; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }

    public String getVendorMobile() { return vendorMobile; }
    public void setVendorMobile(String vendorMobile) { this.vendorMobile = vendorMobile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchAddress() { return branchAddress; }
    public void setBranchAddress(String branchAddress) { this.branchAddress = branchAddress; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getIfsc() { return ifsc; }
    public void setIfsc(String ifsc) { this.ifsc = ifsc; }

    public String getArmedFlag() { return armedFlag; }
    public void setArmedFlag(String armedFlag) { this.armedFlag = armedFlag; }

    public List<String> getLastSyncedTimes() { return lastSyncedTimes; }
    public void setLastSyncedTimes(List<String> lastSyncedTimes) { this.lastSyncedTimes = lastSyncedTimes; }

    @Override
    public String toString() {
        return "DeviceMasterDO [deviceId=" + deviceId + ", deviceName=" + deviceName + ", deviceType=" + deviceType
                + ", deviceLocation=" + deviceLocation + ", phoneNumber=" + phoneNumber + ", protectionType=" + protectionType
                + ", userNo=" + userNo + ", userPassword=" + userPassword + ", email1=" + email1 + ", productType=" + productType
                + ", passcode=" + passcode + ", address=" + address + ", supportNo=" + supportNo + ", branchCode=" + branchCode
                + ", deviceSerialNo=" + deviceSerialNo + ", vendorName=" + vendorName + ", vendorEmail=" + vendorEmail
                + ", vendorMobile=" + vendorMobile + ", status=" + status + ", branchName=" + branchName + ", branchAddress=" + branchAddress
                + ", bankName=" + bankName + ", ifsc=" + ifsc + ", armedFlag=" + armedFlag + ", lastSyncedTimes=" + lastSyncedTimes + "]";
    }
}
