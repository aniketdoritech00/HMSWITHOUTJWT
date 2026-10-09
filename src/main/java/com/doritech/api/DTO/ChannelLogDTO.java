package com.doritech.api.DTO;
  // ── Inner: one row in channel tab ────────────────────────────────────────
    public class ChannelLogDTO{
        private String recipient;    // email/phone number
        private String timestamp;    // "13 FEB 26, 10:23 AM"
        private String status;       // DELIVERED / FAILED / PENDING

        public String getRecipient()         { return recipient; }
        public void   setRecipient(String v) { this.recipient = v; }
        public String getTimestamp()         { return timestamp; }
        public void   setTimestamp(String v) { this.timestamp = v; }
        public String getStatus()            { return status; }
        public void   setStatus(String v)    { this.status = v; }
    }