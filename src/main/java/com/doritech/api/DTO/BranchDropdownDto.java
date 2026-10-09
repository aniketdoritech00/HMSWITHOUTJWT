package com.doritech.api.DTO;
public class BranchDropdownDto {
    private String branchCode;
    private String branchDesc;

    public BranchDropdownDto(String branchCode, String branchDesc) {
        this.branchCode = branchCode;
        this.branchDesc = branchDesc;
    }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchDesc() { return branchDesc; }
    public void setBranchDesc(String branchDesc) { this.branchDesc = branchDesc; }
}