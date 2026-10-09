package com.doritech.api.DTO;

import java.util.List;

public class TicketDataDTO {

	private String deviceSerialNo;
	private String deviceName;
	private String deviceType;
	private String deviceLocation;
	private String mobileNo;
	private String product;
	private String ticketStatus;
	private String ticketTime;
	private String ticketResolvedTime;

	private String bankName;
	private String branchName;
	private String vendorName;

	private String timeDifference; // difference between ticketTime and ticketResolvedTime
	private String ticketId;

	private String escalationLevel;
	private String escalationColor;

	private String branchContactNo;
	// -------------------- GETTERS & SETTERS --------------------

	public String getBranchContactNo() {
		return branchContactNo;
	}

	public void setBranchContactNo(String branchContactNo) {
		this.branchContactNo = branchContactNo;
	}

	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getTicketTime() {
		return ticketTime;
	}

	public void setTicketTime(String ticketTime) {
		this.ticketTime = ticketTime;
	}

	public String getTicketResolvedTime() {
		return ticketResolvedTime;
	}

	public void setTicketResolvedTime(String ticketResolvedTime) {
		this.ticketResolvedTime = ticketResolvedTime;
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

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(String timeDifference) {
		this.timeDifference = timeDifference;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getEscalationLevel() {
		return escalationLevel;
	}

	public void setEscalationLevel(String escalationLevel) {
		this.escalationLevel = escalationLevel;
	}

	public String getEscalationColor() {
		return escalationColor;
	}

	public void setEscalationColor(String escalationColor) {
		this.escalationColor = escalationColor;
	}

}