package com.doritech.api.DTO;

import org.springframework.web.multipart.MultipartFile;


public class IntegratorPenalMasterDTO {


    private String productType;
    private String panelId;
    private String panelSerialNo;
    private MultipartFile qcReportImage;
    
  

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

}
