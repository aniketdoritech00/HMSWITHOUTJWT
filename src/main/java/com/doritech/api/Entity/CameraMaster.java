package com.doritech.api.Entity;

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
@Table(name = "camera_master")
public class CameraMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cameraId;

	@Column(length = 100)
	private String cameraName;

	@Column(length = 100)
	private String cameraSerialNo;

	@Column(length = 100)
	private String cameraType;

	@Column(length = 20)
	private String cameraPort;

	@Column(length = 100)
	private String pixelResolution;

	@Column(length = 100)
	private String ipAddress;
	
	@Column(length = 100)
	private String model;
	private Integer channelNo;

	@Column(length = 20)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nvr_id")
	@JsonIgnore
	private NvrMaster nvr;

	public Long getCameraId() {
		return cameraId;
	}

	public void setCameraId(Long cameraId) {
		this.cameraId = cameraId;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public String getCameraSerialNo() {
		return cameraSerialNo;
	}

	public void setCameraSerialNo(String cameraSerialNo) {
		this.cameraSerialNo = cameraSerialNo;
	}

	public String getCameraType() {
		return cameraType;
	}

	public void setCameraType(String cameraType) {
		this.cameraType = cameraType;
	}

	public String getPixelResolution() {
		return pixelResolution;
	}

	public void setPixelResolution(String pixelResolution) {
		this.pixelResolution = pixelResolution;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(Integer channelNo) {
		this.channelNo = channelNo;
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

	public String getCameraPort() {
		return cameraPort;
	}

	public void setCameraPort(String cameraPort) {
		this.cameraPort = cameraPort;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}