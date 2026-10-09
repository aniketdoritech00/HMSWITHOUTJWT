package com.doritech.api.DTO;

public class CameraDto {
	private String cameraName;
	private String cameraType;
    private String cameraPort;
	private String ipAddress;
	private Integer channelNo;
    private String pixelResolution;
	private String model;
	private String cameraSerialNo;
	private String status;

	public String getCameraSerialNo() {
		return cameraSerialNo;
	}

	public void setCameraSerialNo(String cameraSerialNo) {
		this.cameraSerialNo = cameraSerialNo;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public String getCameraType() {
		return cameraType;
	}

	public void setCameraType(String cameraType) {
		this.cameraType = cameraType;
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

	public String getCameraPort() {
		return cameraPort;
	}

	public void setCameraPort(String cameraPort) {
		this.cameraPort = cameraPort;
	}

	public String getPixelResolution() {
		return pixelResolution;
	}

	public void setPixelResolution(String pixelResolution) {
		this.pixelResolution = pixelResolution;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}