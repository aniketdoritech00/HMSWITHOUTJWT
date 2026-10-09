package com.doritech.api.DTO;
public class AlertWiseTATReportDTO {
    private String categoryWise;   // e.g. "CCTV - HDD FAILURE"
    private int totalSites;
    private int totalSystems;
    private int totalAlerts;
    private int alerts24to48Hrs;
    private int alerts48to72Hrs;
    private int alerts72plusHrs;
    private int resolvedWithin24hrs;
    // getters + setters
	public String getCategoryWise() {
		return categoryWise;
	}
	public void setCategoryWise(String categoryWise) {
		this.categoryWise = categoryWise;
	}
	public int getTotalSites() {
		return totalSites;
	}
	public void setTotalSites(int totalSites) {
		this.totalSites = totalSites;
	}
	public int getTotalSystems() {
		return totalSystems;
	}
	public void setTotalSystems(int totalSystems) {
		this.totalSystems = totalSystems;
	}
	public int getTotalAlerts() {
		return totalAlerts;
	}
	public void setTotalAlerts(int totalAlerts) {
		this.totalAlerts = totalAlerts;
	}
	public int getAlerts24to48Hrs() {
		return alerts24to48Hrs;
	}
	public void setAlerts24to48Hrs(int alerts24to48Hrs) {
		this.alerts24to48Hrs = alerts24to48Hrs;
	}
	public int getAlerts48to72Hrs() {
		return alerts48to72Hrs;
	}
	public void setAlerts48to72Hrs(int alerts48to72Hrs) {
		this.alerts48to72Hrs = alerts48to72Hrs;
	}
	public int getAlerts72plusHrs() {
		return alerts72plusHrs;
	}
	public void setAlerts72plusHrs(int alerts72plusHrs) {
		this.alerts72plusHrs = alerts72plusHrs;
	}
	public int getResolvedWithin24hrs() {
		return resolvedWithin24hrs;
	}
	public void setResolvedWithin24hrs(int resolvedWithin24hrs) {
		this.resolvedWithin24hrs = resolvedWithin24hrs;
	}
    
    
}