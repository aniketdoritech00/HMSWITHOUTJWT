package com.doritech.api.Entity;

import javax.mail.Address;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Read_Mail_Log")
public class ReadMailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
    private String mailSender;
    private String mailReceiver;
	private String mailPassowrd;
	private String mailContent;
	private String mailAttachmentPath;
	private String timeStamp;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMailSender() {
		return mailSender;
	}
	public void setMailSender(String mailSender) {
		this.mailSender = mailSender;
	}
	public String getMailReceiver() {
		return mailReceiver;
	}
	public void setMailReceiver(String mailReceiver) {
		this.mailReceiver = mailReceiver;
	}
	public String getMailPassowrd() {
		return mailPassowrd;
	}
	public void setMailPassowrd(String mailPassowrd) {
		this.mailPassowrd = mailPassowrd;
	}
	public String getMailContent() {
		return mailContent;
	}
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}
	public String getMailAttachmentPath() {
		return mailAttachmentPath;
	}
	public void setMailAttachmentPath(String mailAttachmentPath) {
		this.mailAttachmentPath = mailAttachmentPath;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return "ReadMailEntity [id=" + id + ", mailSender=" + mailSender + ", mailReceiver=" + mailReceiver
				+ ", mailPassowrd=" + mailPassowrd + ", mailContent=" + mailContent + ", mailAttachmentPath="
				+ mailAttachmentPath + ", timeStamp=" + timeStamp + "]";
	}
	
	
}
