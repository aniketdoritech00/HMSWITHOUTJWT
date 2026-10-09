package com.doritech.api.Entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "ticket_details") 
public class TicketDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id") 
    private Long id;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private String ticketId;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "alert_type", nullable = false)
    private String alertType;
    private String panelSerialNumber;
	private String dateTime;
	private String remarks;
    private String completeSMS;
    private String whatsAppStatus;
	private String mailStatus;
	private String mailTime;
    private String status;
    @UpdateTimestamp
    private Date timstamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

	public Date getTimstamp() {
		return timstamp;
	}

	public void setTimstamp(Date timstamp) {
		this.timstamp = timstamp;
	}

	public String getCompleteSMS() {
		return completeSMS;
	}

	public void setCompleteSMS(String completeSMS) {
		this.completeSMS = completeSMS;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPanelSerialNumber() {
		return panelSerialNumber;
	}

	public void setPanelSerialNumber(String panelSerialNumber) {
		this.panelSerialNumber = panelSerialNumber;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getWhatsAppStatus() {
		return whatsAppStatus;
	}

	public void setWhatsAppStatus(String whatsAppStatus) {
		this.whatsAppStatus = whatsAppStatus;
	}

	public String getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}

	public String getMailTime() {
		return mailTime;
	}

	public void setMailTime(String mailTime) {
		this.mailTime = mailTime;
	}
	
    
}
