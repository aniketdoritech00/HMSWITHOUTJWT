package com.doritech.api.Framework;

public class ApiMessage {

	private String message;
	
	private Boolean exceptionFlag;
	
	private ApiMessageTypeEnum typeEnum;

	public ApiMessage(String message, Boolean exceptionFlag, ApiMessageTypeEnum typeEnum) {
		super();
		this.message = message;
		this.exceptionFlag = exceptionFlag;
		this.typeEnum = typeEnum;
	}

	public ApiMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getExceptionFlag() {
		return exceptionFlag;
	}

	public void setExceptionFlag(Boolean exceptionFlag) {
		this.exceptionFlag = exceptionFlag;
	}

	public ApiMessageTypeEnum getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(ApiMessageTypeEnum typeEnum) {
		this.typeEnum = typeEnum;
	}
	
	
	
}
