package com.doritech.api.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "nvr_config")
public class NvrConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoFormat;
    private String recordingMode;
    private String storageType;

    @OneToOne
    @JoinColumn(name = "nvr_id", unique = true)
    private NvrMaster nvr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVideoFormat() {
		return videoFormat;
	}

	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}

	public String getRecordingMode() {
		return recordingMode;
	}

	public void setRecordingMode(String recordingMode) {
		this.recordingMode = recordingMode;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public NvrMaster getNvr() {
		return nvr;
	}

	public void setNvr(NvrMaster nvr) {
		this.nvr = nvr;
	}
    
}