package com.doritech.api.Framework;

import java.util.List;

public class ResponseDTO<T> {
	
	Boolean valid;
	
	private List<T> objectDto;
	
	List<ApiMessage> message;

	public ResponseDTO(Boolean valid, List<T> objectDto, List<ApiMessage> message) {
		super();
		this.valid = valid;
		this.objectDto = objectDto;
		this.message = message;
	}

	public ResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public List<T> getObjectDto() {
		return objectDto;
	}

	public void setObjectDto(List<T> objectDto) {
		this.objectDto = objectDto;
	}

	public List<ApiMessage> getMessage() {
		return message;
	}

	public void setMessage(List<ApiMessage> message) {
		this.message = message;
	}
	
	
}
