package com.doritech.api.DTO;

public class CommunicationMappingDTO {
    
    private Long id;
    private String mail;
    private String calls;
    private String sms;
    private String whatsapp;
    private String panelSerialNo;
    private Long smsDetailsId;
   
    
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
    
    public void setCall(String calls) {
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
    
    public Long getSmsDetailsId() {
        return smsDetailsId;
    }
    
    public void setSmsDetailsId(Long smsDetailsId) {
        this.smsDetailsId = smsDetailsId;
    }
}