package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "communication_mapping")
public class CommunicationMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "mail")
	private String mail;

	@Column(name = "calls")
	private String calls;

	@Column(name = "sms")
	private String sms;

	@Column(name = "whatsapp")
	private String whatsapp;

	@Column(name = "panel_serial_no")
	private String panelSerialNo;

	@Column(name = "sms_details_id")
	private int smsDetailsId;

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCalls() {
		return calls;
	}

	public void setCalls(String calls) {
		this.calls = calls;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}

	public String getPanelSerialNo() {
		return panelSerialNo;
	}

	public void setPanelSerialNo(String panelSerialNo) {
		this.panelSerialNo = panelSerialNo;
	}

	public int getSmsDetailsId() {
		return smsDetailsId;
	}

	public void setSmsDetailsId(int smsDetailsId) {
		this.smsDetailsId = smsDetailsId;
	}
}