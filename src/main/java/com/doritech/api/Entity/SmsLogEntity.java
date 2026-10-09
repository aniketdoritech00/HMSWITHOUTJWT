package com.doritech.api.Entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;



@Entity
@Table(name="Sms_Log")
public class SmsLogEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ID")
	//@SequenceGenerator(name = "ID", sequenceName = "HIBERNATE_SEQUENCE")
	@Column(name="SMS_LOG_ID") private Long smsLogId;
	@Column(name="SMS_ID") private Long smsId;
	@Column(name="SENT_TO") private String sentTo;
	@Column(name="MESSAGE") private String message;
	@Column(name="MESSAGE_DATE") private Date messageDate;
	@Column(name="PRODUCT_CODE") private String productCode;
	@Column(name="DOC_TYPE") private Long docType;
	@Column(name="PRIORITY") private String priority;
	@Column(name="STATUS") private String status;
	@Column(name="DATETIME") private Date datetime;
	@Column(name="USER_ID") private String userId;
	@Column(name="TERMINAL") private String terminal;
	@Column(name="MESSAGE_REF") private String messageRef;
	@Column(name="REJ_REASON") private String rejReason;
	@Column(name="TYPE") private String type;
	@Column(name="BRANCH_CODE") private String branchCode;

	@Column(name="BRANCH_NAME") private String branchName;
	
	@Transient
	private String datetimeFormatted;

	  @Column(name = "escalation_log_id")
	    private Long escalationLogId;
	
	
	
	public String getDatetimeFormatted() {
		return datetimeFormatted;
	}
	public void setDatetimeFormatted(String datetimeFormatted) {
		this.datetimeFormatted = datetimeFormatted;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public Long getSmsLogId() {
		return smsLogId;
	}
	public void setSmsLogId(Long smsLogId) {
		this.smsLogId = smsLogId;
	}
	public Long getSmsId() {
		return smsId;
	}
	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}
	public String getSentTo() {
		return sentTo;
	}
	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Long getDocType() {
		return docType;
	}
	public void setDocType(Long docType) {
		this.docType = docType;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getMessageRef() {
		return messageRef;
	}
	public void setMessageRef(String messageRef) {
		this.messageRef = messageRef;
	}
	public String getRejReason() {
		return rejReason;
	}
	public void setRejReason(String rejReason) {
		this.rejReason = rejReason;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public Long getEscalationLogId() {
		return escalationLogId;
	}
	public void setEscalationLogId(Long escalationLogId) {
		this.escalationLogId = escalationLogId;
	}
	@Override
	public String toString() {
		return "SmsLogEntity [smsLogId=" + smsLogId + ", smsId=" + smsId + ", sentTo=" + sentTo + ", message=" + message
				+ ", messageDate=" + messageDate + ", productCode=" + productCode + ", docType=" + docType
				+ ", priority=" + priority + ", status=" + status + ", datetime=" + datetime + ", userId=" + userId
				+ ", terminal=" + terminal + ", messageRef=" + messageRef + ", rejReason=" + rejReason + ", type="
				+ type + "]";
	}

	
}
