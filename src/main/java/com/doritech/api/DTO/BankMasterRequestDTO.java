package com.doritech.api.DTO;

import javax.validation.constraints.NotBlank;

public class BankMasterRequestDTO {
    
    @NotBlank(message = "Bank name is required")
    private String bankName;    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}