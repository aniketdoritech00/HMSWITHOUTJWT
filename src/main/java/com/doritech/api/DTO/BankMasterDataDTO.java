package com.doritech.api.DTO;

import java.util.Objects;

public class BankMasterDataDTO {
    private String branchCode;
    private String branchName;
    private String fgmo;
    private String zoneName;

    public BankMasterDataDTO() {}

    public BankMasterDataDTO(String branchCode, String branchName, String fgmo, String zoneName) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.fgmo = fgmo;
        this.zoneName = zoneName;
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

    public String getFgmo() {
        return fgmo;
    }

    public void setFgmo(String fgmo) {
        this.fgmo = fgmo;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankMasterDataDTO)) return false;
        BankMasterDataDTO that = (BankMasterDataDTO) o;
        return Objects.equals(branchCode, that.branchCode) &&
               Objects.equals(branchName, that.branchName) &&
               Objects.equals(fgmo, that.fgmo) &&
               Objects.equals(zoneName, that.zoneName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchCode, branchName, fgmo, zoneName);
    }

    @Override
    public String toString() {
        return "BankMasterDataDTO{" +
                "branchCode='" + branchCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", fgmo='" + fgmo + '\'' +
                ", zoneName='" + zoneName + '\'' +
                '}';
    }
}
