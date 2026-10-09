package com.doritech.api.DTO;

public class EmailSchedularDTO {

	private Long id;
	private String branchId;
	private String branchName;
	private String timePeriod;
	private String timestamp;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "EmailSchedulerEntity [id=" + id + ", branchId=" + branchId + ", branchName=" + branchName
				+ ", timePeriod=" + timePeriod + ", timestamp=" + timestamp + "]";
	}

    

}
