package com.doritech.api.DTO;

import java.time.LocalDate;
import java.util.List;

public class NvrRequest {

	private String nvrName;
	private String nvrModel;

	private String ipAddress;
	private Integer portNo;

	private String location;
	private String vendorId;
	private String email;
	private String firmwareVersion;
	private LocalDate installationDate;
	private String macAddress;
	private String nvrSerialNo;
	private String panelSerialNo;
	private Integer totalChannels;
	private Integer totalHardDisks;
	private String status;

	private List<CameraDto> cameras;
	private List<HardDiskDto> hardDisks;

	public String getNvrName() {
		return nvrName;
	}

	public void setNvrName(String nvrName) {
		this.nvrName = nvrName;
	}

	public String getNvrModel() {
		return nvrModel;
	}

	public void setNvrModel(String nvrModel) {
		this.nvrModel = nvrModel;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public LocalDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(LocalDate installationDate) {
		this.installationDate = installationDate;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getNvrSerialNo() {
		return nvrSerialNo;
	}

	public void setNvrSerialNo(String nvrSerialNo) {
		this.nvrSerialNo = nvrSerialNo;
	}

	public String getPanelSerialNo() {
		return panelSerialNo;
	}

	public void setPanelSerialNo(String panelSerialNo) {
		this.panelSerialNo = panelSerialNo;
	}

	public Integer getTotalChannels() {
		return totalChannels;
	}

	public void setTotalChannels(Integer totalChannels) {
		this.totalChannels = totalChannels;
	}

	public Integer getTotalHardDisks() {
		return totalHardDisks;
	}

	public void setTotalHardDisks(Integer totalHardDisks) {
		this.totalHardDisks = totalHardDisks;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getPortNo() {
		return portNo;
	}

	public void setPortNo(Integer portNo) {
		this.portNo = portNo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<CameraDto> getCameras() {
		return cameras;
	}

	public void setCameras(List<CameraDto> cameras) {
		this.cameras = cameras;
	}

	public List<HardDiskDto> getHardDisks() {
		return hardDisks;
	}

	public void setHardDisks(List<HardDiskDto> hardDisks) {
		this.hardDisks = hardDisks;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
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

}