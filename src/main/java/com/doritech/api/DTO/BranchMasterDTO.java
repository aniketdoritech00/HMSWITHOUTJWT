package com.doritech.api.DTO;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

public class BranchMasterDTO {
	
	private String branchCode;

    private String parentCode;

    private String branchType;

    private String branchDesc;

    private String contractPerson;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String mobile;

    private String telephone;

    private String faxNo;

    private String status;

    private String email;
    
    private String bank;
    
    private String vendorName;
    
    private String vendorEmail;
    
    private List<BranchMasterDTO> children;
    
    private String ifsc;
    

    private String mobileSecondary;
    private String mobileTertiary;
    private String mobileQuaternary;
    private String mobileQuinary;
    private String mobileSenary;
    
    private String latitude;
    
    private String longitude;
    
    private String geoLocation; 
    
    private String email2;
    private String email3;
    private String email4;
    private String email5;
    private String email6;
    private String ZonalName;
    private String ParentName;
    private String systemIntegrator;
    
    private String parentBranchName;
    
    
    
    public String getParentBranchName() {
		return parentBranchName;
	}

	public void setParentBranchName(String parentBranchName) {
		this.parentBranchName = parentBranchName;
	}

	public String getVendorEmail() {
		return vendorEmail;
	}

	public void setVendorEmail(String vendorEmail) {
		this.vendorEmail = vendorEmail;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public BranchMasterDTO() {
        this.children = new ArrayList<>();
    }


    public List<BranchMasterDTO> getChildren() {
        return children;
    }

    public void setChildren(List<BranchMasterDTO> children) {
        this.children = children;
    }

    public void addChild(BranchMasterDTO child) {
        this.children.add(child);
    }
	
	
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
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

	public String getContractPerson() {
		return contractPerson;
	}

	public void setContractPerson(String contractPerson) {
		this.contractPerson = contractPerson;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getMobileSecondary() {
		return mobileSecondary;
	}

	public void setMobileSecondary(String mobileSecondary) {
		this.mobileSecondary = mobileSecondary;
	}

	public String getMobileTertiary() {
		return mobileTertiary;
	}

	public void setMobileTertiary(String mobileTertiary) {
		this.mobileTertiary = mobileTertiary;
	}

	public String getMobileQuaternary() {
		return mobileQuaternary;
	}

	public void setMobileQuaternary(String mobileQuaternary) {
		this.mobileQuaternary = mobileQuaternary;
	}

	public String getMobileQuinary() {
		return mobileQuinary;
	}

	public void setMobileQuinary(String mobileQuinary) {
		this.mobileQuinary = mobileQuinary;
	}

	public String getMobileSenary() {
		return mobileSenary;
	}

	public void setMobileSenary(String mobileSenary) {
		this.mobileSenary = mobileSenary;
	}


	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public String getEmail4() {
		return email4;
	}

	public void setEmail4(String email4) {
		this.email4 = email4;
	}

	public String getEmail5() {
		return email5;
	}

	public void setEmail5(String email5) {
		this.email5 = email5;
	}

	public String getEmail6() {
		return email6;
	}

	public void setEmail6(String email6) {
		this.email6 = email6;
	}

	public String getZonalName() {
		return ZonalName;
	}

	public void setZonalName(String zonalName) {
		this.ZonalName = zonalName;
	}

	public String getParentName() {
		return ParentName;
	}

	

	public void setParentName(String parentName) {
		this.ParentName = parentName;
	}

	public String getSystemIntegrator() {
		return systemIntegrator;
	}

	public void setSystemIntegrator(String systemIntegrator) {
		this.systemIntegrator = systemIntegrator;
	}

	@Override
	public String toString() {
		return "BranchMasterDTO [branchCode=" + branchCode + ", parentCode=" + parentCode + ", branchType=" + branchType
				+ ", branchDesc=" + branchDesc + ", contractPerson=" + contractPerson + ", address1=" + address1
				+ ", address2=" + address2 + ", address3=" + address3 + ", address4=" + address4 + ", mobile=" + mobile
				+ ", telephone=" + telephone + ", faxNo=" + faxNo + ", status=" + status + ", email=" + email
				+ ", bank=" + bank + ", vendorName=" + vendorName + ", vendorEmail=" + vendorEmail + ", children="
				+ children + ", ifsc=" + ifsc + ", mobileSecondary=" + mobileSecondary + ", mobileTertiary="
				+ mobileTertiary + ", mobileQuaternary=" + mobileQuaternary + ", mobileQuinary=" + mobileQuinary
				+ ", mobileSenary=" + mobileSenary + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", geoLocation=" + geoLocation + ", email2=" + email2 + ", email3=" + email3 + ", email4=" + email4
				+ ", email5=" + email5 + ", email6=" + email6 + ", ZonalName=" + ZonalName + ", ParentName="
				+ ParentName + ", systemIntegrator=" + systemIntegrator + "]";
	}

	
	
	    
}
