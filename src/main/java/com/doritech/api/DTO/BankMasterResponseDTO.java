package com.doritech.api.DTO;

import java.time.LocalDateTime;

public class BankMasterResponseDTO {
    private String bankCode;
    private String bankName;
    private String message;
    private LocalDateTime timestamp;
    
    // Constructors
    public BankMasterResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    public BankMasterResponseDTO(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.timestamp = LocalDateTime.now();
    }
    
    public BankMasterResponseDTO(String bankCode, String bankName, String message) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.message = message;
        this.timestamp = LocalDateTime.now();
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
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}