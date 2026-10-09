package com.doritech.api.DTO;

public class CommandRequestDTO {
    private String deviceSerialNumber;
    private String panelMobileNo;
    private String messageType;
    private String userId;
    private String name;
    private String passcode;
    private String password;
    private String protectionType;
    private String zoneNo;
    private String newLabel;
    private String dial;
    private String retry;
    private String userGroup;
    private String receiverGroup;
    
    private String loggedInUser;
    private String branchCode;
    
    
    
    
    
    

    public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	

	// Getters and Setters
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getPanelMobileNo() {
        return panelMobileNo;
    }

    public void setPanelMobileNo(String panelMobileNo) {
        this.panelMobileNo = panelMobileNo;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtectionType() {
        return protectionType;
    }

    public void setProtectionType(String protectionType) {
        this.protectionType = protectionType;
    }

    public String getZoneNo() {
        return zoneNo;
    }

    public void setZoneNo(String zoneNo) {
        this.zoneNo = zoneNo;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public String getDial() {
        return dial;
    }

    public void setDial(String dial) {
        this.dial = dial;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getReceiverGroup() {
        return receiverGroup;
    }

    public void setReceiverGroup(String receiverGroup) {
        this.receiverGroup = receiverGroup;
    }

	@Override
	public String toString() {
		return "CommandRequestDTO [deviceSerialNumber=" + deviceSerialNumber + ", panelMobileNo=" + panelMobileNo
				+ ", messageType=" + messageType + ", userId=" + userId + ", name=" + name + ", passcode=" + passcode
				+ ", password=" + password + ", protectionType=" + protectionType + ", zoneNo=" + zoneNo + ", newLabel="
				+ newLabel + ", dial=" + dial + ", retry=" + retry + ", userGroup=" + userGroup + ", receiverGroup="
				+ receiverGroup + ", loggedInUser=" + loggedInUser + "]";
	}

	

   
}
