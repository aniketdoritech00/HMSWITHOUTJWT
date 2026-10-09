package com.doritech.api.DTO;

public class SiaZoneCameraMappingDto {

    private Long id;
    private Long siaZoneId;
    private String cameraChannel;
	private Long nvrId;

    public SiaZoneCameraMappingDto() {
    }

    public SiaZoneCameraMappingDto(Long id, Long siaZoneId, String cameraChannel) {
        this.id = id;
        this.siaZoneId = siaZoneId;
        this.cameraChannel = cameraChannel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiaZoneId() {
        return siaZoneId;
    }

    public void setSiaZoneId(Long siaZoneId) {
        this.siaZoneId = siaZoneId;
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
}