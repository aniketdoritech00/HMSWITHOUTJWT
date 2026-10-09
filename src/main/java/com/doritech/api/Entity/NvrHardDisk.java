package com.doritech.api.Entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "nvr_hard_disk")
public class NvrHardDisk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long hardDiskId;

	@Column(length = 100)
	private String hardDiskName;

	@Column(length = 100)
	private String hardDiskSerialNo;

	private BigDecimal capacityTb;
	private BigDecimal freeSpace;

	@Column(length = 100)
	private String diskType;

	@Column(length = 50)
	private String healthStatus;

	@Column(length = 20)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nvr_id", nullable = false)
	@JsonIgnore
	private NvrMaster nvr;

	public Long getHardDiskId() {
		return hardDiskId;
	}

	public void setHardDiskId(Long hardDiskId) {
		this.hardDiskId = hardDiskId;
	}

	public String getHardDiskName() {
		return hardDiskName;
	}

	public void setHardDiskName(String hardDiskName) {
		this.hardDiskName = hardDiskName;
	}

	public String getHardDiskSerialNo() {
		return hardDiskSerialNo;
	}

	public void setHardDiskSerialNo(String hardDiskSerialNo) {
		this.hardDiskSerialNo = hardDiskSerialNo;
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

	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public NvrMaster getNvr() {
		return nvr;
	}

	public void setNvr(NvrMaster nvr) {
		this.nvr = nvr;
	}

	public BigDecimal getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(BigDecimal freeSpace) {
		this.freeSpace = freeSpace;
	}
}