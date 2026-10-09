package com.doritech.api.Entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class VendorBranchMappedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String branchCode;
    private String branchName;
    private String branchContact;
    private String systemIntegrator;
    private String productType;
    @OneToMany(mappedBy = "vendorBranch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductChecklistEntity> product;

    public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
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

    public List<ProductChecklistEntity> getProduct() {
        return product;
    }
    public void setProduct(List<ProductChecklistEntity> product) {
        this.product = product;
    }
}
