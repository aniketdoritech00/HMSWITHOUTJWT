package com.doritech.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallDTO {
	
	  @JsonProperty("sourcetype")
	    private String sourcetype;

	    @JsonProperty("customivr")
	    private boolean customivr;

	    @JsonProperty("campaigntype")
	    private String campaigntype;

	    @JsonProperty("filetype")
	    private String filetype;

	    @JsonProperty("ukey")
	    private String ukey;

	    @JsonProperty("serviceno")
	    private String serviceno;

	    @JsonProperty("ivrtemplateid")
	    private String ivrtemplateid;

	    @JsonProperty("retryatmpt")
	    private int retryatmpt;

	    @JsonProperty("retryduration")
	    private int retryduration;

	    @JsonProperty("msisdnlist")
	    private MsisdnList[] msisdnlist;

		public String getSourcetype() {
			return sourcetype;
		}

		public void setSourcetype(String sourcetype) {
			this.sourcetype = sourcetype;
		}

		public boolean isCustomivr() {
			return customivr;
		}

		public void setCustomivr(boolean customivr) {
			this.customivr = customivr;
		}

		public String getCampaigntype() {
			return campaigntype;
		}

		public void setCampaigntype(String campaigntype) {
			this.campaigntype = campaigntype;
		}

		public String getFiletype() {
			return filetype;
		}

		public void setFiletype(String filetype) {
			this.filetype = filetype;
		}

		public String getUkey() {
			return ukey;
		}

		public void setUkey(String ukey) {
			this.ukey = ukey;
		}

		public String getServiceno() {
			return serviceno;
		}

		public void setServiceno(String serviceno) {
			this.serviceno = serviceno;
		}

		public String getIvrtemplateid() {
			return ivrtemplateid;
		}

		public void setIvrtemplateid(String ivrtemplateid) {
			this.ivrtemplateid = ivrtemplateid;
		}

		public int getRetryatmpt() {
			return retryatmpt;
		}

		public void setRetryatmpt(int retryatmpt) {
			this.retryatmpt = retryatmpt;
		}

		public int getRetryduration() {
			return retryduration;
		}

		public void setRetryduration(int retryduration) {
			this.retryduration = retryduration;
		}

		public MsisdnList[] getMsisdnlist() {
			return msisdnlist;
		}

		public void setMsisdnlist(MsisdnList[] msisdnlist) {
			this.msisdnlist = msisdnlist;
		}
	    
	    
	    
	    

}
