package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "non_sia_zone_camera_mapping")
public class NonSiaZoneCameraMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "zone_id", length = 200)
	private String zoneId;

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
}