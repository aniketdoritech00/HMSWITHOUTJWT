package com.doritech.api.DTO;
public class AlertTypeDropdownDto {
    private String alertType;

    public AlertTypeDropdownDto(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
}