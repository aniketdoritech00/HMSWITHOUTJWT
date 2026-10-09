package com.doritech.api.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "heartbeat")
public class Heartbeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "panel_serial_number", nullable = false, length = 50)
	private String panelSerialNumber;

	@CreationTimestamp
	@Column(name = "heartbeat_datetime", nullable = false)
	private LocalDateTime heartbeatDatetime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPanelSerialNumber() {
		return panelSerialNumber;
	}

	public void setPanelSerialNumber(String panelSerialNumber) {
		this.panelSerialNumber = panelSerialNumber;
	}

	public LocalDateTime getHeartbeatDatetime() {
		return heartbeatDatetime;
	}

	public void setHeartbeatDatetime(LocalDateTime heartbeatDatetime) {
		this.heartbeatDatetime = heartbeatDatetime;
	}

	
}