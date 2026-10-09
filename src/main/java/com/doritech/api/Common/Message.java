package com.doritech.api.Common;

public class Message {

	private String type;
	private String message;

	public Message(String message) {
		super();
		this.message = message;
		// TODO Auto-generated constructor stub
	}

	public Message(String type, String message) {
		super();
		this.type = type;
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
