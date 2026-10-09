package com.doritech.api.DTO;

import java.util.List;

public class VendorBranchMappedDTO {

    private int id;
    private String branchCode;
    private String branchName;
    private String branchContact;
    private String systemIntegrator;
    private String productType;
    private List<ProductChecklistDTO> products;

    public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	// Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

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

    public String getBranchContact() {
        return branchContact;
    }
    public void setBranchContact(String branchContact) {
        this.branchContact = branchContact;
    }

    public String getSystemIntegrator() {
        return systemIntegrator;
    }
    public void setSystemIntegrator(String systemIntegrator) {
        this.systemIntegrator = systemIntegrator;
    }

    public List<ProductChecklistDTO> getProducts() {
        return products;
    }
    public void setProducts(List<ProductChecklistDTO> products) {
        this.products = products;
    }
}
