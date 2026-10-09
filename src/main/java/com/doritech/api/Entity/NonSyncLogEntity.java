package com.doritech.api.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "non_sync_log")
public class NonSyncLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nonsync_id")
    private Long nonSyncId;

    @Column(name = "panel_serial_no", nullable = false)
    private String panelSerialNo;

    @Column(name = "nonsync_start_datetime")
    private LocalDateTime nonSyncStartDatetime;

    @Column(name = "nonsync_end_datetime")
    private LocalDateTime nonSyncEndDatetime;

    public NonSyncLogEntity() {}

    public NonSyncLogEntity(String panelSerialNo,
                             LocalDateTime nonSyncStartDatetime,
                             LocalDateTime nonSyncEndDatetime) {
        this.panelSerialNo = panelSerialNo;
        this.nonSyncStartDatetime = nonSyncStartDatetime;
        this.nonSyncEndDatetime = nonSyncEndDatetime;
    }

    // Getters & Setters
    public Long getNonSyncId() { return nonSyncId; }
    public void setNonSyncId(Long nonSyncId) { this.nonSyncId = nonSyncId; }

    public String getPanelSerialNo() { return panelSerialNo; }
    public void setPanelSerialNo(String panelSerialNo) { this.panelSerialNo = panelSerialNo; }

    public LocalDateTime getNonSyncStartDatetime() { return nonSyncStartDatetime; }
    public void setNonSyncStartDatetime(LocalDateTime nonSyncStartDatetime) {
        this.nonSyncStartDatetime = nonSyncStartDatetime;
    }

    public LocalDateTime getNonSyncEndDatetime() { return nonSyncEndDatetime; }
    public void setNonSyncEndDatetime(LocalDateTime nonSyncEndDatetime) {
        this.nonSyncEndDatetime = nonSyncEndDatetime;
    }
}