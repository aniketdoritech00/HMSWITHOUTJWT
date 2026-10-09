package com.doritech.api.DTO;

import java.util.HashSet;
import java.util.Set;

public class VendorStats {

    private Set<String> sites = new HashSet<>();       // unique branch codes
    private Set<String> systems = new HashSet<>();     // unique device serial nos
    private int totalAlerts = 0;
    private long totalFaultHours = 0;
    private int alerts24to48 = 0;
    private int alerts48to72 = 0;
    private int alerts72plus = 0;
    private int alerts96plus = 0;
    private int resolvedWithin24hrs;
    private int pending24hrsNoEscalation;
    private long averageTATSeconds;
    private String averageTATFormatted;

    // + getters and setters for both
    // Getters & Setters
    public int getResolvedWithin24hrs() { return resolvedWithin24hrs; }
    public void setResolvedWithin24hrs(int resolvedWithin24hrs) { this.resolvedWithin24hrs = resolvedWithin24hrs; }

    public int getPending24hrsNoEscalation() { return pending24hrsNoEscalation; }
    public void setPending24hrsNoEscalation(int pending24hrsNoEscalation) { this.pending24hrsNoEscalation = pending24hrsNoEscalation; }
    public Set<String> getSites() { return sites; }
    public Set<String> getSystems() { return systems; }

    public int getTotalAlerts() { return totalAlerts; }
    public void setTotalAlerts(int totalAlerts) { this.totalAlerts = totalAlerts; }

    public long getTotalFaultHours() { return totalFaultHours; }
    public void setTotalFaultHours(long totalFaultHours) { this.totalFaultHours = totalFaultHours; }

    public int getAlerts24to48() { return alerts24to48; }
    public void setAlerts24to48(int alerts24to48) { this.alerts24to48 = alerts24to48; }

    public int getAlerts48to72() { return alerts48to72; }
    public void setAlerts48to72(int alerts48to72) { this.alerts48to72 = alerts48to72; }

    public int getAlerts72plus() { return alerts72plus; }
    public void setAlerts72plus(int alerts72plus) { this.alerts72plus = alerts72plus; }

    public int getAlerts96plus() { return alerts96plus; }
    public void setAlerts96plus(int alerts96plus) { this.alerts96plus = alerts96plus; }
	public long getAverageTATSeconds() {
		return averageTATSeconds;
	}
	public void setAverageTATSeconds(long averageTATSeconds) {
		this.averageTATSeconds = averageTATSeconds;
	}
	public String getAverageTATFormatted() {
		return averageTATFormatted;
	}
	public void setAverageTATFormatted(String averageTATFormatted) {
		this.averageTATFormatted = averageTATFormatted;
	}
	public void setSites(Set<String> sites) {
		this.sites = sites;
	}
	public void setSystems(Set<String> systems) {
		this.systems = systems;
	}
    
    
}