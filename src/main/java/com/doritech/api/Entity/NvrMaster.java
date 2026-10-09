package com.doritech.api.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "nvr_master")
public class NvrMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nvrId;

    @Column(length = 100)
    private String nvrName;

    @Column(length = 100)
    private String panelSerialNo;

    @Column(length = 100)
    private String nvrSerialNo;

    @Column(length = 50)
    private String vendorId;

    @Column(length = 100)
    private String nvrModel;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 100)
    private String macAddress;

    private Integer portNo;

    @Column(length = 100)
    private String firmwareVersion;

    private Integer totalChannels;

    private Integer totalHardDisks;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String location;

    private LocalDate installationDate;

    @Column(length = 20)
    private String status;

    @OneToMany(mappedBy = "nvr", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CameraMaster> cameras = new ArrayList<>();

    @OneToMany(mappedBy = "nvr", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NvrHardDisk> hardDisks = new ArrayList<>();

    public Long getNvrId() {
        return nvrId;
    }

    public void setNvrId(Long nvrId) {
        this.nvrId = nvrId;
    }

    public String getNvrName() {
        return nvrName;
    }

    public void setNvrName(String nvrName) {
        this.nvrName = nvrName;
    }

    public String getPanelSerialNo() {
        return panelSerialNo;
    }

    public void setPanelSerialNo(String panelSerialNo) {
        this.panelSerialNo = panelSerialNo;
    }

    public String getNvrSerialNo() {
        return nvrSerialNo;
    }

    public void setNvrSerialNo(String nvrSerialNo) {
        this.nvrSerialNo = nvrSerialNo;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getNvrModel() {
        return nvrModel;
    }

    public void setNvrModel(String nvrModel) {
        this.nvrModel = nvrModel;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getPortNo() {
        return portNo;
    }

    public void setPortNo(Integer portNo) {
        this.portNo = portNo;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Integer getTotalChannels() {
        return totalChannels;
    }

    public void setTotalChannels(Integer totalChannels) {
        this.totalChannels = totalChannels;
    }

    public Integer getTotalHardDisks() {
        return totalHardDisks;
    }

    public void setTotalHardDisks(Integer totalHardDisks) {
        this.totalHardDisks = totalHardDisks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CameraMaster> getCameras() {
        return cameras;
    }

    public void setCameras(List<CameraMaster> cameras) {
        this.cameras = cameras;
    }

    public List<NvrHardDisk> getHardDisks() {
        return hardDisks;
    }

    public void setHardDisks(List<NvrHardDisk> hardDisks) {
        this.hardDisks = hardDisks;
    }
}