package com.doritech.api.DTO;

import org.springframework.web.multipart.MultipartFile;


public class IntegratorSimMasterDTO {

	private int id; 
	private String m2mSimNumber;
    private MultipartFile m2mSimImage;
    private String m2mSimSerialNumber;
    private String opertaor;
    private String branchName;

    private String branchCode;
    
    
    
    
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getM2mSimSerialNumber() {
		return m2mSimSerialNumber;
	}
	public void setM2mSimSerialNumber(String m2mSimSerialNumber) {
		this.m2mSimSerialNumber = m2mSimSerialNumber;
	}
	public String getOpertaor() {
		return opertaor;
	}
	public void setOpertaor(String opertaor) {
		this.opertaor = opertaor;
	}
    
	public String getM2mSimNumber() {
		return m2mSimNumber;
	}
	public void setM2mSimNumber(String m2mSimNumber) {
		this.m2mSimNumber = m2mSimNumber;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public MultipartFile getM2mSimImage() {
		return m2mSimImage;
	}
	public void setM2mSimImage(MultipartFile m2mSimImage) {
		this.m2mSimImage = m2mSimImage;
	}
    
    
}
