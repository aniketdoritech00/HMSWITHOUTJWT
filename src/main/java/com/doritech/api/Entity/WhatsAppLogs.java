package com.doritech.api.Entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "whatsapp_logs")
public class WhatsAppLogs {

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	private String phoneNo;
	 @Column(name = "whatsapp_message", columnDefinition = "TEXT")
	private String whatsappMessage;
	private String status;
	private String responseMessage;
	 @Column(name = "url", columnDefinition = "TEXT")
	private String url;
	@UpdateTimestamp
	private Date timestamp;
	private String ticketId;
	private String branchCode;
	
	  @Column(name = "escalation_log_id")
	    private Long escalationLogId;
	
	public String getBranchCode() {
		return branchCode;
	}


	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}


	public String getTicketId() {
		return ticketId;
	}


	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getPhoneNo() {
		return phoneNo;
	}


	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}


	public String getWhatsappMessage() {
		return whatsappMessage;
	}


	public void setWhatsappMessage(String whatsappMessage) {
		this.whatsappMessage = whatsappMessage;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getResponseMessage() {
		return responseMessage;
	}


	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public Long getEscalationLogId() {
		return escalationLogId;
	}


	public void setEscalationLogId(Long escalationLogId) {
		this.escalationLogId = escalationLogId;
	}


	@Override
	public String toString() {
		return "WhatsAppLogs [id=" + id + ", phoneNo=" + phoneNo + ", whatsappMessage=" + whatsappMessage + ", status="
				+ status + ", responseMessage=" + responseMessage + ", url=" + url + ", timestamp=" + timestamp + "]";
	}
	
	
}