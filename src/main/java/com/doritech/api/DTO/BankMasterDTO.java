package com.doritech.api.DTO;

public class BankMasterDTO {
    private String bankCode;
    private String bankName;
    
    // Constructors
    public BankMasterDTO() {}
    
    public BankMasterDTO(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }
    
    // Getters and Setters
    public String getBankCode() {
        return bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}