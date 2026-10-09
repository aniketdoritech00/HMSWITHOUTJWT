package com.doritech.api.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity to track scheduler execution history.
 * Used to detect missed scheduler runs after system restart.
 */
@Entity
@Table(name = "scheduler_run_log")
public class SchedulerRunLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Scheduler name: "DAILY_5PM" or "DAILY_8AM"
     */
    @Column(name = "scheduler_name", nullable = false, length = 50)
    private String schedulerName;

    /**
     * The date on which this scheduler was supposed to run
     */
    @Column(name = "run_date", nullable = false)
    private LocalDate runDate;

    /**
     * Exact datetime when it actually ran
     */
    @Column(name = "run_datetime")
    private LocalDateTime runDatetime;

    /**
     * SUCCESS / FAILED
     */
    @Column(name = "status", length = 20)
    private String status;

    /**
     * Additional info - success message or error message
     */
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    /**
     * "SCHEDULED" = cron se chala
     */
    @Column(name = "trigger_type", length = 30)
    private String triggerType;

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchedulerName() { return schedulerName; }
    public void setSchedulerName(String schedulerName) { this.schedulerName = schedulerName; }

    public LocalDate getRunDate() { return runDate; }
    public void setRunDate(LocalDate runDate) { this.runDate = runDate; }

    public LocalDateTime getRunDatetime() { return runDatetime; }
    public void setRunDatetime(LocalDateTime runDatetime) { this.runDatetime = runDatetime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
}