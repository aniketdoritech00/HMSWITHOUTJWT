package com.doritech.api.DTO;

import java.util.List;

public class BranchDTO {
    private String branchCode;
    private String branchType;
    private String branchDesc;
    private String deviceId;
    private String deviceSerialNo;
    private List<DeviceZoneDTO> deviceZones;  

    public List<DeviceZoneDTO> getDeviceZones() {
        return deviceZones;
    }

    public void setDeviceZones(List<DeviceZoneDTO> deviceZones) {
        this.deviceZones = deviceZones;
    }
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public String getBranchDesc() {
        return branchDesc;
    }

    public void setBranchDesc(String branchDesc) {
        this.branchDesc = branchDesc;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }
}
