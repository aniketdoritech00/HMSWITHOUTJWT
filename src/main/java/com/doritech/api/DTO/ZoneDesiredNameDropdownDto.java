package com.doritech.api.DTO;
public class ZoneDesiredNameDropdownDto {
    private String zoneDesiredName;

    public ZoneDesiredNameDropdownDto(String zoneDesiredName) {
        this.zoneDesiredName = zoneDesiredName;
    }

    public String getZoneDesiredName() { return zoneDesiredName; }
    public void setZoneDesiredName(String zoneDesiredName) { this.zoneDesiredName = zoneDesiredName; }
}