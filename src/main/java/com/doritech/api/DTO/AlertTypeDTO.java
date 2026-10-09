package com.doritech.api.DTO;

public class AlertTypeDTO {

    private String alertType;
    private String whatsapp;
    private String sms;
    private String calling;
    private String mail;

    // Default constructor
    public AlertTypeDTO() {
    }

    // Parameterized constructor
    public AlertTypeDTO(String alertType, String whatsapp, String sms, String calling, String mail) {
        this.alertType = alertType;
        this.whatsapp = whatsapp;
        this.sms = sms;
        this.calling = calling;
        this.mail = mail;
    }

    // Getters and Setters
    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCalling() {
        return calling;
    }

    public void setCalling(String calling) {
        this.calling = calling;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "AlertTypeDTO{" +
                "alertType='" + alertType + '\'' +
                ", whatsapp='" + whatsapp + '\'' +
                ", sms='" + sms + '\'' +
                ", calling='" + calling + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
