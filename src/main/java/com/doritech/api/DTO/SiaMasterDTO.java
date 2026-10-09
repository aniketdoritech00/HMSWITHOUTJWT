package com.doritech.api.DTO;

import java.util.List;

public class SiaMasterDTO {

    private Long siaDeviceId;

    private String siaSerialNo;

    private String panelSerialNo;

    private String status;

    private String vendor;

    private List<SiaPhysicalZoneMasterDTO> zoneList;

    public Long getSiaDeviceId() {
        return siaDeviceId;
    }

    public void setSiaDeviceId(Long siaDeviceId) {
        this.siaDeviceId = siaDeviceId;
    }

    public String getSiaSerialNo() {
        return siaSerialNo;
    }

    public void setSiaSerialNo(String siaSerialNo) {
        this.siaSerialNo = siaSerialNo;
    }

    public String getPanelSerialNo() {
        return panelSerialNo;
    }

    public void setPanelSerialNo(String panelSerialNo) {
        this.panelSerialNo = panelSerialNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public List<SiaPhysicalZoneMasterDTO> getZoneList() {
        return zoneList;
    }

    public void setZoneList(List<SiaPhysicalZoneMasterDTO> zoneList) {
        this.zoneList = zoneList;
    }
}