package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sia_master")
public class SiaMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "sia_serial_no")
	private String siaSerialNo;

	@Column(name = "panel_serial_no")
	private String panelSerialNo;

	@Column(name = "status")
	private String status;

	@Column(name = "vendor_code")
	private String vendor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSiaSerialNo() {
		return siaSerialNo;
	}

	public void setSiaSerialNo(String siaSerialNo) {
		this.siaSerialNo = siaSerialNo;
	}

	public String getPanelSerialNo() {
		return panelSerialNo;
	}

	public void setPanelSerialNo(String panelSerialNo) {
		this.panelSerialNo = panelSerialNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
}