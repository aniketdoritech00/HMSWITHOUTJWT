package com.doritech.api.DTO;

import java.time.LocalDateTime;

public class GuardNgLogDTO {
	
	
	private Long id;
    private String deviceSerialNumber;
    private String apiUrl;
    private String requestPayload;
    private int responseCode;
    private String responseMessage;
    private LocalDateTime requestTimestamp;
    private LocalDateTime responseTimestamp;
    private String status; 
    
    private String userName;
    private String command;
    private String branchCode;
    private String branchName;
    
    
    
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getRequestPayload() {
		return requestPayload;
	}
	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public LocalDateTime getRequestTimestamp() {
		return requestTimestamp;
	}
	public void setRequestTimestamp(LocalDateTime requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}
	public LocalDateTime getResponseTimestamp() {
		return responseTimestamp;
	}
	public void setResponseTimestamp(LocalDateTime responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
    
    

}
