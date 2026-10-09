package com.doritech.api.Entity;

public class ResponseEntity {
	
	private String message;
	
	private Object statusCode;
	
	private Object payload;
	
	 private long totalElements;
	    private int totalPages;
	    private int currentPage;
	    
	    

	public long getTotalElements() {
			return totalElements;
		}

		public void setTotalElements(long totalElements) {
			this.totalElements = totalElements;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}

		public int getCurrentPage() {
			return currentPage;
		}

		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Object statusCode) {
		this.statusCode = statusCode;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public ResponseEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseEntity(String message, Object statusCode, Object payload) {
		super();
		this.message = message;
		this.statusCode = statusCode;
		this.payload = payload;
	}
	
	

}
