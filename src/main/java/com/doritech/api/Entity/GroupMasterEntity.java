package com.doritech.api.Entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "group_master")
public class GroupMasterEntity {
    @Id
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_description")
    private String groupDescription;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "emp_id")
    private String empId;
    
    @Column(name = "created_by")
	private String createdBy;
    
	@Column(name = "updated_by")
	private String updatedBy;
	@Column(name = "updated_date")
	private String updatedDate;
	@Column(name = "updation_status")
	private String updationStatus;
	@Column(name = "count")
	private int count;

    public GroupMasterEntity() {
    }

    

    public GroupMasterEntity(String groupId, String groupName, String groupDescription, String createdDate, String empId,
			String createdBy, String updatedBy, String updatedDate, String updationStatus, int count) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupDescription = groupDescription;
		this.createdDate = createdDate;
		this.empId = empId;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.updatedDate = updatedDate;
		this.updationStatus = updationStatus;
		this.count = count;
	}
    
    


	
    public String getGroupId() {
		return groupId;
	}



	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}



	public String getGroupName() {
		return groupName;
	}



	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}



	public String getGroupDescription() {
		return groupDescription;
	}



	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}



	public String getCreatedDate() {
		return createdDate;
	}



	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}



	public String getEmpId() {
		return empId;
	}



	public void setEmpId(String empId) {
		this.empId = empId;
	}



	public String getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
		return "GroupMasterEntity [groupId=" + groupId + ", groupName=" + groupName + ", groupDescription="
				+ groupDescription + ", createdDate=" + createdDate + ", empId=" + empId + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + ", updatedDate=" + updatedDate + ", updationStatus=" + updationStatus
				+ ", count=" + count + "]";
	}

}