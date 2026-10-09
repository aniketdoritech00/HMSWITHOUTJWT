package com.doritech.api.DTO;

public class EscalationMatrixDTO {

    private String escLevel;          
    private Integer escTimeRange;     

    private String recipient;         

    private Boolean whatsapp;
    private Boolean sms;
    private Boolean callAlert;
    private Boolean mail;

    private Boolean criticalFlag;

    private String ccMail1;
    private String ccMail2;
    private String ccMail3;
    private String ccMail4;

    private Integer noOfReminder;
    private Integer reminderTime;

    // getters & setters

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

    public Boolean getCriticalFlag() {
        return criticalFlag;
    }

    public void setCriticalFlag(Boolean criticalFlag) {
        this.criticalFlag = criticalFlag;
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
}