package com.doritech.api.DTO;

import java.util.List;
import java.util.Map;

public class SiaHealthResponseDTO {

    private String branchDesc;
    private String bank;
    private String controllingOffice;
    private List<Map<String, Object>> healthData;

    // Getters & Setters
    public String getBranchDesc() { return branchDesc; }
    public void setBranchDesc(String branchDesc) { this.branchDesc = branchDesc; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getControllingOffice() { return controllingOffice; }
    public void setControllingOffice(String controllingOffice) { this.controllingOffice = controllingOffice; }

    public List<Map<String, Object>> getHealthData() { return healthData; }
    public void setHealthData(List<Map<String, Object>> healthData) { this.healthData = healthData; }
}