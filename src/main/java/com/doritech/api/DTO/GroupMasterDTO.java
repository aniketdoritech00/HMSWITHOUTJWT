package com.doritech.api.DTO;

import java.sql.Date;

public class GroupMasterDTO {
    private String groupId;

    private String groupName;

    private String groupDescription;

    private Date createdDate;

    private String empId;
    private String empName;

    public GroupMasterDTO() {
    }

    public GroupMasterDTO(String groupId, String groupName, String groupDescription, Date createdDate, String empId, String empName) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdDate = createdDate;
        this.empId = empId;
        this.empName = empName;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public String toString() {
        return "GroupMasterDTO{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", createdDate=" + createdDate +
                ", empId='" + empId + '\'' +
                ", empName='" + empName + '\'' +
                '}';
    }
    public void setCreatedDate(String createdDate2) {
		// TODO Auto-generated method stub
		
	}
}