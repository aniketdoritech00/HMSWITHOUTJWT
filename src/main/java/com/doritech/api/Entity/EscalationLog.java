package com.doritech.api.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Table to track which escalation level has already been sent
 * for a given ticket — prevents duplicate data at same level.
 *
 * CREATE TABLE hms.escalation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    ticket_no VARCHAR(100) NOT NULL,
    
    esc_level VARCHAR(10) NOT NULL,
    
    reminder_index INT NOT NULL,
    
    sent_at DATETIME NOT NULL,
        
    critical_flag boolean    
 *);
 */
@Entity
@Table(name = "escalation_log")
public class EscalationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_no", nullable = false, length = 100)
    private String ticketNo;

    @Column(name = "esc_level", nullable = false, length = 10)
    private String escLevel;

    /** 1-based index of this reminder within its escalation level */
    @Column(name = "reminder_index", nullable = false)
    private int reminderIndex;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;


    @Column(name = "critical_flag", nullable = false)
    private boolean criticalFlag = false;
	    
	   
	    // ── Getters & Setters ─────────────────────────────────────────────────────

	    public Long getId()                       { return id; }
	    public void setId(Long id)                { this.id = id; }

	    public String getTicketNo()               { return ticketNo; }
	    public void setTicketNo(String ticketNo)  { this.ticketNo = ticketNo; }

	    public String getEscLevel()               { return escLevel; }
	    public void setEscLevel(String escLevel)  { this.escLevel = escLevel; }

	    public int getReminderIndex()             { return reminderIndex; }
	    public void setReminderIndex(int i)       { this.reminderIndex = i; }

	    public LocalDateTime getSentAt()          { return sentAt; }
	    public void setSentAt(LocalDateTime t)    { this.sentAt = t; }
	    
	    
	    
		public boolean isCriticalFlag() {
			return criticalFlag;
		}
		public void setCriticalFlag(boolean criticalFlag) {
			this.criticalFlag = criticalFlag;
		}
		@Override
		public String toString() {
			return "EscalationLog [id=" + id + ", ticketNo=" + ticketNo + ", escLevel=" + escLevel + ", reminderIndex="
					+ reminderIndex + ", sentAt=" + sentAt + ", criticalFlag=" + criticalFlag + "]";
		}
	
		
		
	  
	}