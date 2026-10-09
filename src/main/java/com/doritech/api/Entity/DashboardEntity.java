package com.doritech.api.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DashboardEntity {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private int id;
	private String empId;
	private String dashboardNo;
	private String timeStamp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getDashboardNo() {
		return dashboardNo;
	}
	public void setDashboardNo(String dashboardNo) {
		this.dashboardNo = dashboardNo;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
}
