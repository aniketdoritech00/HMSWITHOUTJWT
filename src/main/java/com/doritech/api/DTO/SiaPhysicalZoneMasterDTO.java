package com.doritech.api.DTO;

import java.util.List;


public class SiaPhysicalZoneMasterDTO {

    private Long id;

    private String zone;

    private String zoneName;

    private String status;
    
  
    
    private List<SiaZoneCameraMappingDto> cameraList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public List<SiaZoneCameraMappingDto> getCameraList() {
		return cameraList;
	}

	public void setCameraList(List<SiaZoneCameraMappingDto> cameraList) {
		this.cameraList = cameraList;
	}

	 
    
}