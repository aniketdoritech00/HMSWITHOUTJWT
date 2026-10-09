package com.doritech.api.DTO;

public class AlertDetailsDTO {

    private String alertType;
    private int alertGeneratedId;
    private int alertResovedId;
    private String alertGeneratedTime;
    private String alertResovedTime;
    private String timeDifference; // difference between alertGeneratedTime and alertResovedTime
    private String zoneStatus;
    // -------------------- GETTERS & SETTERS --------------------

    public String getAlertType() {
        return alertType;
    }

    public String getZoneStatus() {
		return zoneStatus;
	}

	public void setZoneStatus(String zoneStatus) {
		this.zoneStatus = zoneStatus;
	}

	public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public int getAlertGeneratedId() {
        return alertGeneratedId;
    }

    public void setAlertGeneratedId(int alertGeneratedId) {
        this.alertGeneratedId = alertGeneratedId;
    }

    public int getAlertResovedId() {
        return alertResovedId;
    }

    public void setAlertResovedId(int alertResovedId) {
        this.alertResovedId = alertResovedId;
    }

    public String getAlertGeneratedTime() {
        return alertGeneratedTime;
    }

    public void setAlertGeneratedTime(String alertGeneratedTime) {
        this.alertGeneratedTime = alertGeneratedTime;
    }

    public String getAlertResovedTime() {
        return alertResovedTime;
    }

    public void setAlertResovedTime(String alertResovedTime) {
        this.alertResovedTime = alertResovedTime;
    }

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }
}