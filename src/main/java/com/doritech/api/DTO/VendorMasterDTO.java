package com.doritech.api.DTO;

import java.util.Date;

public class VendorMasterDTO {
    private String vendorId;
    private String vendorName;
    private String contactPerson;
    private String email;
    private String mobile;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pin;
    private String status;
    private String createdBy;
    private Date createdAt;
    private String terminal;
    private String updatedBy;
    private Date updatedAt;

    // Constructors
    public VendorMasterDTO() {}

    public VendorMasterDTO(String vendorId, String vendorName, String contactPerson, String email, String mobile,
                           String address, String city, String state, String country, String pin, String status,
                           String createdBy, Date createdAt, String terminal, String updatedBy, Date updatedAt) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pin = pin;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.terminal = terminal;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "VendorMasterDTO [vendorId=" + vendorId + ", vendorName=" + vendorName + ", contactPerson=" 
                + contactPerson + ", email=" + email + ", mobile=" + mobile + ", address=" + address + ", city=" 
                + city + ", state=" + state + ", country=" + country + ", pin=" + pin + ", status=" + status 
                + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", terminal=" + terminal 
                + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + "]";
    }
}
