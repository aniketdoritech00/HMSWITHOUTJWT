package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Mail_Param")
public class MailParamEntity {

	@Id
	@Column(name = "SMTP_MAIL_ID")
	private String smtpMailId;
	@Column(name = "SMTP_ACCOUNT_NAME")
	private String smptAccountName;
	@Column(name = "SMTP_MAIL_PWD")
	private String smtpMailPwd;
	@Column(name = "SMTP_SERVER")
	private String smtpServer;
	@Column(name = "SMTP_PORT")
	private Long smtpPort;
	@Column(name = "SMTP_SSL")
	private String smtpSsl;
	@Column(name = "CONN_TIMEOUT")
	private Long connTimeout;
	@Column(name = "BOUNCE_MAIL_ID")
	private String bounceMailId;
	@Column(name = "BOUNCE_ACCOUNT_NAME")
	private String bounceAccountName;
	@Column(name = "BOUNCE_MAIL_PWD")
	private String bounceMailPwd;
	@Column(name = "POP3_SERVER")
	private String pop3Server;
	@Column(name = "POP3_PORT")
	private Long pop3Port;
	@Column(name = "POP3_SSL")
	private String pop3Ssl;
	@Column(name = "PARAM1")
	private String param1;
	@Column(name = "BRANCH_CODE")
	private String branchCode;

	public String getSmtpMailId() {
		return smtpMailId;
	}

	public void setSmtpMailId(String smtpMailId) {
		this.smtpMailId = smtpMailId;
	}

	public String getSmptAccountName() {
		return smptAccountName;
	}

	public void setSmptAccountName(String smptAccountName) {
		this.smptAccountName = smptAccountName;
	}

	public String getSmtpMailPwd() {
		return smtpMailPwd;
	}

	public void setSmtpMailPwd(String smtpMailPwd) {
		this.smtpMailPwd = smtpMailPwd;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public Long getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Long smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpSsl() {
		return smtpSsl;
	}

	public void setSmtpSsl(String smtpSsl) {
		this.smtpSsl = smtpSsl;
	}

	public Long getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(Long connTimeout) {
		this.connTimeout = connTimeout;
	}

	public String getBounceMailId() {
		return bounceMailId;
	}

	public void setBounceMailId(String bounceMailId) {
		this.bounceMailId = bounceMailId;
	}

	public String getBounceAccountName() {
		return bounceAccountName;
	}

	public void setBounceAccountName(String bounceAccountName) {
		this.bounceAccountName = bounceAccountName;
	}

	public String getBounceMailPwd() {
		return bounceMailPwd;
	}

	public void setBounceMailPwd(String bounceMailPwd) {
		this.bounceMailPwd = bounceMailPwd;
	}

	public String getPop3Server() {
		return pop3Server;
	}

	public void setPop3Server(String pop3Server) {
		this.pop3Server = pop3Server;
	}

	public Long getPop3Port() {
		return pop3Port;
	}

	public void setPop3Port(Long pop3Port) {
		this.pop3Port = pop3Port;
	}

	public String getPop3Ssl() {
		return pop3Ssl;
	}

	public void setPop3Ssl(String pop3Ssl) {
		this.pop3Ssl = pop3Ssl;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@Override
	public String toString() {
		return "MailParamEntity [smtpMailId=" + smtpMailId + ", smptAccountName=" + smptAccountName + ", smtpMailPwd="
				+ smtpMailPwd + ", smtpServer=" + smtpServer + ", smtpPort=" + smtpPort + ", smtpSsl=" + smtpSsl
				+ ", connTimeout=" + connTimeout + ", bounceMailId=" + bounceMailId + ", bounceAccountName="
				+ bounceAccountName + ", bounceMailPwd=" + bounceMailPwd + ", pop3Server=" + pop3Server + ", pop3Port="
				+ pop3Port + ", pop3Ssl=" + pop3Ssl + ", param1=" + param1 + ", branchCode=" + branchCode
				+ ", getSmtpMailId()=" + getSmtpMailId() + ", getSmptAccountName()=" + getSmptAccountName()
				+ ", getSmtpMailPwd()=" + getSmtpMailPwd() + ", getSmtpServer()=" + getSmtpServer() + ", getSmtpPort()="
				+ getSmtpPort() + ", getSmtpSsl()=" + getSmtpSsl() + ", getConnTimeout()=" + getConnTimeout()
				+ ", getBounceMailId()=" + getBounceMailId() + ", getBounceAccountName()=" + getBounceAccountName()
				+ ", getBounceMailPwd()=" + getBounceMailPwd() + ", getPop3Server()=" + getPop3Server()
				+ ", getPop3Port()=" + getPop3Port() + ", getPop3Ssl()=" + getPop3Ssl() + ", getParam1()=" + getParam1()
				+ ", getBranchCode()=" + getBranchCode() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
