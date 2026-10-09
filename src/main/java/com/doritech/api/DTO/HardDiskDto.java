package com.doritech.api.DTO;

import java.math.BigDecimal;

public class HardDiskDto {
	private String hardDiskName;
	private BigDecimal capacityTb;
	private BigDecimal freeSpace;
	private String diskType;
	private String hardDiskSerialNo;
	private String healthStatus;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	public String getHardDiskSerialNo() {
		return hardDiskSerialNo;
	}

	public void setHardDiskSerialNo(String hardDiskSerialNo) {
		this.hardDiskSerialNo = hardDiskSerialNo;
	}

	public String getHardDiskName() {
		return hardDiskName;
	}

	public void setHardDiskName(String hardDiskName) {
		this.hardDiskName = hardDiskName;
	}

	public BigDecimal getCapacityTb() {
		return capacityTb;
	}

	public void setCapacityTb(BigDecimal capacityTb) {
		this.capacityTb = capacityTb;
	}

	public String getDiskType() {
		return diskType;
	}

	public void setDiskType(String diskType) {
		this.diskType = diskType;
	}

	public BigDecimal getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(BigDecimal freeSpace) {
		this.freeSpace = freeSpace;
	}

}