package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
public class IntegratorDeviceMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productType;
    private String panelId;
    private String panelSerialNo;
    private String m2mSimNumber;
    private String qcReportImagePath;
    private String m2mSimImagePath;
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
	public String getQcReportImagePath() {
		return qcReportImagePath;
	}
	public void setQcReportImagePath(String qcReportImagePath) {
		this.qcReportImagePath = qcReportImagePath;
	}
	public String getM2mSimImagePath() {
		return m2mSimImagePath;
	}
	public void setM2mSimImagePath(String m2mSimImagePath) {
		this.m2mSimImagePath = m2mSimImagePath;
	}
    
    
}
