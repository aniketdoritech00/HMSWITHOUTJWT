package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "email_templates")
public class EmailTemplateEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "email_type")
    private String emailType;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "escalation_level")
    private String escalationLevel;

    @Column(name = "escalation_reminder")
    private Integer escalationReminder;

    @Column(name = "email_body", columnDefinition = "LONGTEXT")
    private String emailBody;

    @Column(name = "is_active")
    private Boolean isActive;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmailType() { return emailType; }
    public void setEmailType(String emailType) { this.emailType = emailType; }

    public String getEmailSubject() { return emailSubject; }
    public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }

    public String getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(String escalationLevel) { this.escalationLevel = escalationLevel; }

    public Integer getEscalationReminder() { return escalationReminder; }
    public void setEscalationReminder(Integer escalationReminder) { this.escalationReminder = escalationReminder; }

    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}