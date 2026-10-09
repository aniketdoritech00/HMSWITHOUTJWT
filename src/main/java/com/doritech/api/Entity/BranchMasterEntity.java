package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "branch_master")
public class BranchMasterEntity {
	@Id
	@Column(name = "branch_code")
	private String branchCode;

	@Column(name = "bank")
	private String bank;

	@Column(name = "parent_code")
	private String parentCode;

	@Column(name = "branch_type")
	private String branchType;

	@Column(name = "branch_desc")
	private String branchDesc;

	@Column(name = "contract_person")
	private String contractPerson;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "address3")
	private String address3;

	@Column(name = "address4")
	private String address4;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "telephone")
	private String telephone;

	@Column(name = "fax_no")
	private String faxNo;

	@Column(name = "status")
	private String status;

	@Column(name = "email")
	private String email;

	@Column(name = "ifsc")
	private String ifsc;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "mobile_secondary")
	private String mobileSecondary;

	@Column(name = "mobile_tertiary")
	private String mobileTertiary;

	@Column(name = "mobile_quaternary")
	private String mobileQuaternary;

	@Column(name = "mobile_quinary")
	private String mobileQuinary;

	@Column(name = "mobile_senary")
	private String mobileSenary;

	@Column(name = "email2")
	private String email2;
	@Column(name = "email3")
	private String email3;
	@Column(name = "email4")
	private String email4;
	@Column(name = "email5")
	private String email5;
	@Column(name = "email6")
	private String email6;

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
	@Transient
	private String excelStatus;

	@Column(name = "system_integrator")
	private String systemIntegrator;

	public String getExcelStatus() {
		return excelStatus;
	}

	public void setExcelStatus(String excelStatus) {
		this.excelStatus = excelStatus;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
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

	public String getSystemIntegrator() {
		return systemIntegrator;
	}

	public void setSystemIntegrator(String systemIntegrator) {
		this.systemIntegrator = systemIntegrator;
	}

	@Override
	public String toString() {
		return "BranchMasterEntity [branchCode=" + branchCode + ", bank=" + bank + ", parentCode=" + parentCode
				+ ", branchType=" + branchType + ", branchDesc=" + branchDesc + ", contractPerson=" + contractPerson
				+ ", address1=" + address1 + ", address2=" + address2 + ", address3=" + address3 + ", address4="
				+ address4 + ", mobile=" + mobile + ", telephone=" + telephone + ", faxNo=" + faxNo + ", status="
				+ status + ", email=" + email + ", ifsc=" + ifsc + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", mobileSecondary=" + mobileSecondary + ", mobileTertiary=" + mobileTertiary + ", mobileQuaternary="
				+ mobileQuaternary + ", mobileQuinary=" + mobileQuinary + ", mobileSenary=" + mobileSenary + ", email2="
				+ email2 + ", email3=" + email3 + ", email4=" + email4 + ", email5=" + email5 + ", email6=" + email6
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", updatedBy=" + updatedBy
				+ ", updatedDate=" + updatedDate + ", updationStatus=" + updationStatus + ", count=" + count
				+ ", excelStatus=" + excelStatus + ", systemIntegrator=" + systemIntegrator + "]";
	}

}
