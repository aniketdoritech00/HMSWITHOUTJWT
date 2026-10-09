package com.doritech.api.Entity;

import java.sql.Blob;
import java.time.LocalDateTime;
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
@Table(name="Email_Log")
public class EmailLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ID")
	
	@Column(name="EMAIL_LOG_ID") private Long emailLogId;
	@Column(name="MAIL_ID") private Long mailId;
	@Column(name="EMAIL_ID_TO") private String emailIdTo;
	@Column(name="EMAIL_ID_CC") private String emailIdCc;
	@Column(name="EMAIL_ID_BCC") private String emailIdBcc;
	@Column(name="PRODUCT_CODE") private String producCode;
	@Column(name="DOC_TYPE") private Long docType;
	@Column(name="DOC_DATE") private Date docDate;
	@Column(name="DOC_EXT") private String docExt;
	@Column(name="DOC_REF") private String docRef;
	@Column(name="PRIORITY") private String priority;
	@Column(name="SUBJECT") private String subject;
	@Column(name="BODY_TEXT") private String bodyText;
	@Column(name="PASSWORD") private String password;
	@Column(name="SIGN_FLAG") private String signFlag;
	@Column(name="SIGN_DATE") private Date signDate;
	@Column(name="SIGNED_BY") private String signedBy;
	@Column(name="STATUS") private String status;
	@Column(name="FROM_EMAIL") private String fromEmail;
	@Column(name="MAIL_DOC") private byte[] mailDoc;
	@Column(name="DATETIME") private LocalDateTime datetime;
	@Column(name="USER_ID") private String userId;
	@Column(name="TERMINAL") private String terminal;
	@Column(name="TYPE") private String type;
	@Column(name="emp_id") private String empId;
	@Column(name="branch_code") private String branchCode;
	@Column(name="branch_name") private String branchName;
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
	public Long getEmailLogId() {
		return emailLogId;
	}
	public void setEmailLogId(Long emailLogId) {
		this.emailLogId = emailLogId;
	}
	public Long getMailId() {
		return mailId;
	}
	public void setMailId(Long mailId) {
		this.mailId = mailId;
	}
	public String getEmailIdTo() {
		return emailIdTo;
	}
	public void setEmailIdTo(String emailIdTo) {
		this.emailIdTo = emailIdTo;
	}
	public String getEmailIdCc() {
		return emailIdCc;
	}
	public void setEmailIdCc(String emailIdCc) {
		this.emailIdCc = emailIdCc;
	}
	public String getEmailIdBcc() {
		return emailIdBcc;
	}
	public void setEmailIdBcc(String emailIdBcc) {
		this.emailIdBcc = emailIdBcc;
	}
	public String getProducCode() {
		return producCode;
	}
	public void setProducCode(String producCode) {
		this.producCode = producCode;
	}
	public Long getDocType() {
		return docType;
	}
	public void setDocType(Long docType) {
		this.docType = docType;
	}
	public Date getDocDate() {
		return docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}
	public String getDocExt() {
		return docExt;
	}
	public void setDocExt(String docExt) {
		this.docExt = docExt;
	}
	public String getDocRef() {
		return docRef;
	}
	public void setDocRef(String docRef) {
		this.docRef = docRef;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBodyText() {
		return bodyText;
	}
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public String getSignedBy() {
		return signedBy;
	}
	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public byte[] getMailDoc() {
		return mailDoc;
	}
	public void setMailDoc(byte[] mailDoc) {
		this.mailDoc = mailDoc;
	}
	public LocalDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(LocalDateTime datetime) {
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public Long getEscalationLogId() {
		return escalationLogId;
	}
	public void setEscalationLogId(Long escalationLogId) {
		this.escalationLogId = escalationLogId;
	}
	
}
