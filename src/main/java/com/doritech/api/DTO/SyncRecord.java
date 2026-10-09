package com.doritech.api.DTO;

import java.time.LocalDateTime;

public class SyncRecord {
    private LocalDateTime datetime;
    private String medium;

    public SyncRecord(LocalDateTime datetime, String medium) {
        this.datetime = datetime;
        this.medium = medium;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String getMedium() {
        return medium;
    }
}