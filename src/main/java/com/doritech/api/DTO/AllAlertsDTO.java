package com.doritech.api.DTO;

import java.util.List;

public class AllAlertsDTO {
	
	private String branchCode;
	private String branchName;
	private String branchType;
	private String bankName;
	
	
	private List<String> alertTypes;


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


	public String getBranchType() {
		return branchType;
	}


	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public List<String> getAlertTypes() {
		return alertTypes;
	}


	public void setAlertTypes(List<String> alertTypes) {
		this.alertTypes = alertTypes;
	}
	

}
