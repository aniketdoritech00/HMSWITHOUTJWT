package com.doritech.api.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_email_log")
public class AlarmEmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;
    private String deviceName;
    private String alarmTime;
    private LocalDateTime mailSentTime;
    
    private String alarmKey;

    
	public String getAlarmKey() {
		return alarmKey;
	}
	public void setAlarmKey(String alarmKey) {
		this.alarmKey = alarmKey;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public LocalDateTime getMailSentTime() {
		return mailSentTime;
	}
	public void setMailSentTime(LocalDateTime mailSentTime) {
		this.mailSentTime = mailSentTime;
	}

    // getters and setters
}
