package com.doritech.api.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_notifications")
public class AlertNotificationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_name", nullable = false)
    private String deviceName;
    
    @Column(name = "branch_name", nullable = false)
    private String branchName;
    
    @Column(name = "alert_type", nullable = false)
    private String alertType;
    
    @Column(name = "alarm_time", nullable = false)
    private String alarmTime;
    
    @Column(name = "notified_at", nullable = false)
    private LocalDateTime notifiedAt;
    
    @Column(name = "alarm_status", nullable = false)
    private String alarmStatus; // "OPEN" or "CLOSED"
    
    // Constructors
    public AlertNotificationEntity() {}
    
    public AlertNotificationEntity(String deviceName, String branchName, String alertType, 
                                   String alarmTime, LocalDateTime notifiedAt, String alarmStatus) {
        this.deviceName = deviceName;
        this.branchName = branchName;
        this.alertType = alertType;
        this.alarmTime = alarmTime;
        this.notifiedAt = notifiedAt;
        this.alarmStatus = alarmStatus;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getAlarmTime() {
        return alarmTime;
    }
    
    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }
    
    public LocalDateTime getNotifiedAt() {
        return notifiedAt;
    }
    
    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }
    
    public String getAlarmStatus() {
        return alarmStatus;
    }
    
    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}