package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "escalation_matrix")
public class EscalationMatrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "esc_level", nullable = false, length = 10)
    private String escLevel;                  // L1, L2, L3

    @Column(name = "esc_time_range")
    private Integer escTimeRange;             // in minutes (e.g., 1440 = 24hrs)

    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;                 // Vendor, Zonal, etc.

    @Column(name = "whatsapp")
    private Boolean whatsapp = false;

    @Column(name = "sms")
    private Boolean sms = false;

    @Column(name = "call_alert")
    private Boolean callAlert = false;

    @Column(name = "mail")
    private Boolean mail = false;
    @Column(name = "critical_flag")
    private Boolean criticalFlag = false;

    @Column(name = "cc_mail_1", length = 100)
    private String ccMail1;

    @Column(name = "cc_mail_2", length = 100)
    private String ccMail2;

    @Column(name = "cc_mail_3", length = 100)
    private String ccMail3;

    @Column(name = "cc_mail_4", length = 100)
    private String ccMail4;

    @Column(name = "no_of_reminder")
    private Integer noOfReminder = 0;

    @Column(name = "reminder_time")
    private Integer reminderTime;             // in minutes

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEscLevel() {
		return escLevel;
	}

	public void setEscLevel(String escLevel) {
		this.escLevel = escLevel;
	}

	public Integer getEscTimeRange() {
		return escTimeRange;
	}

	public void setEscTimeRange(Integer escTimeRange) {
		this.escTimeRange = escTimeRange;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Boolean getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(Boolean whatsapp) {
		this.whatsapp = whatsapp;
	}

	public Boolean getSms() {
		return sms;
	}

	public void setSms(Boolean sms) {
		this.sms = sms;
	}

	public Boolean getCallAlert() {
		return callAlert;
	}

	public void setCallAlert(Boolean callAlert) {
		this.callAlert = callAlert;
	}

	public Boolean getMail() {
		return mail;
	}

	public void setMail(Boolean mail) {
		this.mail = mail;
	}

	public String getCcMail1() {
		return ccMail1;
	}

	public void setCcMail1(String ccMail1) {
		this.ccMail1 = ccMail1;
	}

	public String getCcMail2() {
		return ccMail2;
	}

	public void setCcMail2(String ccMail2) {
		this.ccMail2 = ccMail2;
	}

	public String getCcMail3() {
		return ccMail3;
	}

	public void setCcMail3(String ccMail3) {
		this.ccMail3 = ccMail3;
	}

	public String getCcMail4() {
		return ccMail4;
	}

	public void setCcMail4(String ccMail4) {
		this.ccMail4 = ccMail4;
	}

	public Integer getNoOfReminder() {
		return noOfReminder;
	}

	public void setNoOfReminder(Integer noOfReminder) {
		this.noOfReminder = noOfReminder;
	}

	public Integer getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(Integer reminderTime) {
		this.reminderTime = reminderTime;
	}

	public Boolean getCriticalFlag() {
		return criticalFlag;
	}

	public void setCriticalFlag(Boolean criticalFlag) {
		this.criticalFlag = criticalFlag;
	}

	@Override
	public String toString() {
		return "EscalationMatrix [id=" + id + ", escLevel=" + escLevel + ", escTimeRange=" + escTimeRange
				+ ", recipient=" + recipient + ", whatsapp=" + whatsapp + ", sms=" + sms + ", callAlert=" + callAlert
				+ ", mail=" + mail + ", criticalFlag=" + criticalFlag + ", ccMail1=" + ccMail1 + ", ccMail2=" + ccMail2
				+ ", ccMail3=" + ccMail3 + ", ccMail4=" + ccMail4 + ", noOfReminder=" + noOfReminder + ", reminderTime="
				+ reminderTime + "]";
	}
    
    
    
}