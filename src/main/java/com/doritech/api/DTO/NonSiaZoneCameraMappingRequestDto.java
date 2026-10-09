package com.doritech.api.DTO;

public class NonSiaZoneCameraMappingRequestDto {

	private Long Id;

	private String zoneId;

	private String cameraChannel;

	private Long nvrId;

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getCameraChannel() {
		return cameraChannel;
	}

	public void setCameraChannel(String cameraChannel) {
		this.cameraChannel = cameraChannel;
	}

	public Long getNvrId() {
		return nvrId;
	}

	public void setNvrId(Long nvrId) {
		this.nvrId = nvrId;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
}