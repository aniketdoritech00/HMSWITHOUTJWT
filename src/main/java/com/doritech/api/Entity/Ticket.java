package com.doritech.api.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_gernated_id", nullable = false)
    private Long alertGeneratedId;

    @Column(name = "device_serial_no", length = 10)
    private String deviceSerialNo;

    @Column(name = "group_name", length = 10)
    private String groupName;

    @Column(name = "resolved_alert_id")
    private Long resolvedAlertId;

    @Column(name = "resolved_type", length = 10)
    private String resolvedType;

    @Column(name = "ticket_no", length = 50)
    private String ticketNo;

    @Column(name = "ticket_status", nullable = false, length = 10)
    private String ticketStatus;

    @Column(name = "zone", nullable = false, length = 2)
    private String zone;
    @Column(name = "critical_flag", nullable = false, length = 2)
    private String criticalFlag;
    @Column(name = "zone_status", nullable = false, length = 10)
    private String zoneStatus;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAlertGeneratedId() {
		return alertGeneratedId;
	}

	public void setAlertGeneratedId(Long alertGeneratedId) {
		this.alertGeneratedId = alertGeneratedId;
	}

	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}

	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getResolvedAlertId() {
		return resolvedAlertId;
	}

	public void setResolvedAlertId(Long resolvedAlertId) {
		this.resolvedAlertId = resolvedAlertId;
	}

	public String getResolvedType() {
		return resolvedType;
	}

	public void setResolvedType(String resolvedType) {
		this.resolvedType = resolvedType;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getZoneStatus() {
		return zoneStatus;
	}

	public void setZoneStatus(String zoneStatus) {
		this.zoneStatus = zoneStatus;
	}

	public String getCriticalFlag() {
		return criticalFlag;
	}

	public void setCriticalFlag(String criticalFlag) {
		this.criticalFlag = criticalFlag;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", alertGeneratedId=" + alertGeneratedId + ", deviceSerialNo=" + deviceSerialNo
				+ ", groupName=" + groupName + ", resolvedAlertId=" + resolvedAlertId + ", resolvedType=" + resolvedType
				+ ", ticketNo=" + ticketNo + ", ticketStatus=" + ticketStatus + ", zone=" + zone + ", criticalFlag="
				+ criticalFlag + ", zoneStatus=" + zoneStatus + ", createAt=" + createAt + ", updateAt=" + updateAt
				+ "]";
	}

	
    
    
    
}