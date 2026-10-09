package com.doritech.api.DTO;

import org.springframework.web.multipart.MultipartFile;


public class IntegratorDeviceMasterDTO {

	private int id;
    private String productType;
    private String panelId;
    private String panelSerialNo;
    private String m2mSimNumber;
    private MultipartFile qcReportImage;
    private MultipartFile m2mSimImage;
    private String m2mSimSerialNumber;
    private String opertaor;
    
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
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getPanelId() {
		return panelId;
	}
	public void setPanelId(String panelId) {
		this.panelId = panelId;
	}
	public String getPanelSerialNo() {
		return panelSerialNo;
	}
	public void setPanelSerialNo(String panelSerialNo) {
		this.panelSerialNo = panelSerialNo;
	}
	public MultipartFile getQcReportImage() {
		return qcReportImage;
	}
	public void setQcReportImage(MultipartFile qcReportImage) {
		this.qcReportImage = qcReportImage;
	}
	public MultipartFile getM2mSimImage() {
		return m2mSimImage;
	}
	public void setM2mSimImage(MultipartFile m2mSimImage) {
		this.m2mSimImage = m2mSimImage;
	}
    
    
}
