package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "employee_master")
public class EmployeeMasterEntity {
	@Id
	@Column(name = "emp_id")
	private String empId;
	@Column(name = "emp_role")
	private String empRole;
	@Column(name = "emp_name")
	private String empName;
//	@Column(name = "emp_pan")
//	private String empPan;
	@Column(name = "address")
	private String address;
	@Column(name = "city")
	private String city;
	@Column(name = "pin")
	private String pin;
	@Column(name = "state")
	private String state;
	@Column(name = "country")
	private String country;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "email")
	private String email;
	@Column(name = "status")
	private String status;
	@Column(name = "branch")
	private String branch;
	@Column(name = "login_flag")
	private String loginFlag;
	@Column(name = "password")
	private String password;
	@Column(name = "group_id")
	private String groupId;
	
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
	
	private String activeLoginFlag;
	
	

	public String getActiveLoginFlag() {
		return activeLoginFlag;
	}

	public void setActiveLoginFlag(String activeLoginFlag) {
		this.activeLoginFlag = activeLoginFlag;
	}

	public EmployeeMasterEntity() {
	}

	public EmployeeMasterEntity(String empId, String empRole, String empName, String address,
			String city, String pin, String state, String country, String mobile, String email, String status,
			String branch, String loginFlag, String password, String groupId) {
		this.empId = empId;
		this.empRole = empRole;
		this.empName = empName;
		//this.empPan = empPan;
		this.address = address;
		this.city = city;
		this.pin = pin;
		this.state = state;
		this.country = country;
		this.mobile = mobile;
		this.email = email;
		this.status = status;
		this.branch = branch;
		this.loginFlag = loginFlag;
		this.password = password;
		this.groupId = groupId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpRole() {
		return empRole;
	}

	public void setEmpRole(String empRole) {
		this.empRole = empRole;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

//	public String getEmpPan() {
//		return empPan;
//	}
//
//	public void setEmpPan(String empPan) {
//		this.empPan = empPan;
//	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	@Override
	public String toString() {
		return "EmployeeMasterEntity [empId=" + empId + ", empRole=" + empRole + ", empName=" + empName + ", address=" + address + ", city=" + city + ", pin=" + pin + ", state=" + state
				+ ", country=" + country + ", mobile=" + mobile + ", email=" + email + ", status=" + status
				+ ", branch=" + branch + ", loginFlag=" + loginFlag + ", password=" + password + ", groupId=" + groupId
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", updatedBy=" + updatedBy
				+ ", updatedDate=" + updatedDate + ", updationStatus=" + updationStatus + ", count=" + count + "]";
	}
}