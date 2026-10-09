package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "sia_zone_camera_mapping")
public class SiaZoneCameraMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sia_zone_id", nullable = false)
	private Long siaZoneId;

	@Column(name = "camera_channel", length = 20)
	private String cameraChannel;

	@Column(name = "nvr_id")
	private Long nvrId;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public Long getNvrId() {
		return nvrId;
	}

	public void setNvrId(Long nvrId) {
		this.nvrId = nvrId;
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

}