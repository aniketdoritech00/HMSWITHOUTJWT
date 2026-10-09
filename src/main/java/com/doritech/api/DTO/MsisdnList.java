package com.doritech.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsisdnList {

	 @JsonProperty("phoneno")
     private String phoneno;

     @JsonProperty("BranchName")
     private String branchName;

     @JsonProperty("ProductType")
     private String productType;

     @JsonProperty("Reason")
     private String reason;

     @JsonProperty("ContactNo")
     private String contactNo;

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
     
     
}
