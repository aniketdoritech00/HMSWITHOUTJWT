package com.doritech.api.DTO;

import java.util.List;

public  class EscalationLevelDTO {
        private String level;           // L1 / L2 / L3
        private String recipient;       // VENDOR / ZONE / HEAD_OFFICE
        private List<ChannelLogDTO> email;
        private List<ChannelLogDTO> sms;
        private List<ChannelLogDTO> call;
        private List<ChannelLogDTO> whatsapp;

        // Getters & Setters
        public String getLevel()                          { return level; }
        public void   setLevel(String v)                  { this.level = v; }
        public String getRecipient()                      { return recipient; }
        public void   setRecipient(String v)              { this.recipient = v; }
        public List<ChannelLogDTO> getEmail()             { return email; }
        public void   setEmail(List<ChannelLogDTO> v)     { this.email = v; }
        public List<ChannelLogDTO> getSms()               { return sms; }
        public void   setSms(List<ChannelLogDTO> v)       { this.sms = v; }
        public List<ChannelLogDTO> getCall()              { return call; }
        public void   setCall(List<ChannelLogDTO> v)      { this.call = v; }
        public List<ChannelLogDTO> getWhatsapp()          { return whatsapp; }
        public void   setWhatsapp(List<ChannelLogDTO> v)  { this.whatsapp = v; }
    }
