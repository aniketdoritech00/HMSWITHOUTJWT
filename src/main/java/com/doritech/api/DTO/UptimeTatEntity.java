package com.doritech.api.DTO;
public class UptimeTatEntity {

    private String branchName;
    private String vendorName;
    private String zone;
    private String zoneDesiredName;
    private String alertType;
    private long totalTimeMinutes;
    private long downtimeMinutes;
    private long noInfoAvailableMinutes;
    private long uptimeMinutes;
    private String deviceType;
 // getter + setter
    // Getters & Setters
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public long getTotalTimeMinutes() { return totalTimeMinutes; }
    public void setTotalTimeMinutes(long totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }

    public long getDowntimeMinutes() { return downtimeMinutes; }
    public void setDowntimeMinutes(long downtimeMinutes) { this.downtimeMinutes = downtimeMinutes; }

    public long getNoInfoAvailableMinutes() { return noInfoAvailableMinutes; }
    public void setNoInfoAvailableMinutes(long v) { this.noInfoAvailableMinutes = v; }

    public long getUptimeMinutes() { return uptimeMinutes; }
    public void setUptimeMinutes(long uptimeMinutes) { this.uptimeMinutes = uptimeMinutes; }
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getZoneDesiredName() {
		return zoneDesiredName;
	}
	public void setZoneDesiredName(String zoneDesiredName) {
		this.zoneDesiredName = zoneDesiredName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
    
    
}