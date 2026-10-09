package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sia_physical_zone_master")
public class SiaPhysicalZoneMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sia_device_id")
	private Long siaDeviceId;

	@Column(name = "zone")
	private String zone;

	@Column(name = "zone_name")
	private String zoneName;

	@Column(name = "status")
	private String status;

	// Getters & Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiaDeviceId() {
		return siaDeviceId;
	}

	public void setSiaDeviceId(Long siaDeviceId) {
		this.siaDeviceId = siaDeviceId;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}