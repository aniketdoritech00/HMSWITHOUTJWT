package com.doritech.api.DTO;
public class VendorWiseTATReportDTO {

    private String vendorName;        // DIGITALS - NOIDA
    private int totalSites;           // unique branch count
    private int totalSystems;         // unique device count
    private int totalAlerts;          // total faulty events
    private int alerts24to48Hrs;      // faults resolved in 24-48 hrs
    private int alerts48to72Hrs;
    private int alerts72plusHrs;
    private int alerts96plusHrs;
    private String averageTat;        // "28 HOURS"
    private int resolvedWithin24hrs;
    private int pending24hrsNoEscalation;
    private long averageTATSeconds;
    private String averageTATFormatted;  // e.g. "1h 23m 45s" or "0h 0m 9s"

    // Getters & Setters
    public int getResolvedWithin24hrs() { return resolvedWithin24hrs; }
    public void setResolvedWithin24hrs(int resolvedWithin24hrs) { this.resolvedWithin24hrs = resolvedWithin24hrs; }

    public int getPending24hrsNoEscalation() { return pending24hrsNoEscalation; }
    public void setPending24hrsNoEscalation(int pending24hrsNoEscalation) { this.pending24hrsNoEscalation = pending24hrsNoEscalation; }
    // Getters & Setters

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public int getTotalSites() { return totalSites; }
    public void setTotalSites(int totalSites) { this.totalSites = totalSites; }

    public int getTotalSystems() { return totalSystems; }
    public void setTotalSystems(int totalSystems) { this.totalSystems = totalSystems; }

    public int getTotalAlerts() { return totalAlerts; }
    public void setTotalAlerts(int totalAlerts) { this.totalAlerts = totalAlerts; }

    public int getAlerts24to48Hrs() { return alerts24to48Hrs; }
    public void setAlerts24to48Hrs(int alerts24to48Hrs) { this.alerts24to48Hrs = alerts24to48Hrs; }

    public int getAlerts48to72Hrs() { return alerts48to72Hrs; }
    public void setAlerts48to72Hrs(int alerts48to72Hrs) { this.alerts48to72Hrs = alerts48to72Hrs; }

    public int getAlerts72plusHrs() { return alerts72plusHrs; }
    public void setAlerts72plusHrs(int alerts72plusHrs) { this.alerts72plusHrs = alerts72plusHrs; }

    public int getAlerts96plusHrs() { return alerts96plusHrs; }
    public void setAlerts96plusHrs(int alerts96plusHrs) { this.alerts96plusHrs = alerts96plusHrs; }

    public String getAverageTat() { return averageTat; }
    public void setAverageTat(String averageTat) { this.averageTat = averageTat; }
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
    
    
}