// DeviceDropdownDTO.java
package com.doritech.api.DTO;

public class DeviceDropdownDTO {
    
    private String deviceSerialNo;
    private String branchDesc;
    
    public DeviceDropdownDTO(String deviceSerialNo, String branchDesc) {
        this.deviceSerialNo = deviceSerialNo;
        this.branchDesc = branchDesc;
    }
    
    // Getters and Setters
    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }
    
    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }
    
    public String getBranchDesc() {
        return branchDesc;
    }
    
    public void setBranchDesc(String branchDesc) {
        this.branchDesc = branchDesc;
    }
}