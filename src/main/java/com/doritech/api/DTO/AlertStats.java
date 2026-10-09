package com.doritech.api.DTO;

import java.util.HashSet;
import java.util.Set;

// Inner class or separate file
public class AlertStats {

    private Set<String> sites = new HashSet<>();
    private Set<String> systems = new HashSet<>();
    private int totalAlerts = 0;
    private int alerts24to48 = 0;
    private int alerts48to72 = 0;
    private int alerts72plus = 0;
    private int resolvedWithin24hrs = 0;

    public Set<String> getSites() { return sites; }
    public Set<String> getSystems() { return systems; }
    public int getTotalAlerts() { return totalAlerts; }
    public int getAlerts24to48() { return alerts24to48; }
    public int getAlerts48to72() { return alerts48to72; }
    public int getAlerts72plus() { return alerts72plus; }
    public int getResolvedWithin24hrs() { return resolvedWithin24hrs; }

    public void incrementTotalAlerts() { this.totalAlerts++; }
    public void incrementAlerts24to48() { this.alerts24to48++; }
    public void incrementAlerts48to72() { this.alerts48to72++; }
    public void incrementAlerts72plus() { this.alerts72plus++; }
    public void incrementResolvedWithin24hrs() { this.resolvedWithin24hrs++; }
}