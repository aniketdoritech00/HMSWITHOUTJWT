package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "nvr_connection_details")
public class NvrConnectionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nvr_ip", nullable = false, length = 50)
    private String nvrIp;

    @Column(name = "nvr_port")
    private Integer nvrPort;

    @Column(name = "nvr_url", length = 255)
    private String nvrUrl;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "panel_serial", length = 50)
    private String panelSerial;

    @Column(name = "rtsp_port")
    private Integer rtspPort;

    @Column(name = "rtsp_url", length = 500)
    private String rtspUrl;


    @Column(name = "nvr_id")
    private String nvr;

    public NvrConnectionDetails() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNvrIp() {
        return nvrIp;
    }

    public void setNvrIp(String nvrIp) {
        this.nvrIp = nvrIp;
    }

    public Integer getNvrPort() {
        return nvrPort;
    }

    public void setNvrPort(Integer nvrPort) {
        this.nvrPort = nvrPort;
    }

    public String getNvrUrl() {
        return nvrUrl;
    }

    public void setNvrUrl(String nvrUrl) {
        this.nvrUrl = nvrUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPanelSerial() {
        return panelSerial;
    }

    public void setPanelSerial(String panelSerial) {
        this.panelSerial = panelSerial;
    }

    public Integer getRtspPort() {
        return rtspPort;
    }

    public void setRtspPort(Integer rtspPort) {
        this.rtspPort = rtspPort;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getNvr() {
        return nvr;
    }

    public void setNvr(String nvr) {
        this.nvr = nvr;
    }
}