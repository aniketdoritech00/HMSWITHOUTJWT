package com.doritech.api.Entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "dvr")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DVR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

  private Long id;

  @Column(name = "incoming_id", nullable = true)
  private String incomingId;

    @Column(name = "dvr_id")
    private Long dvrId;

    @Column(name = "panel_serial", nullable = false)
    private String panelSerial;

    @Column(nullable = false)
    private String model;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "firmware_version", nullable = false)
    private String firmwareVersion;

    @Column(name = "time_mode", nullable = false)
    private String timeMode;

    @Column(name = "local_time", nullable = false)
    private LocalDateTime localTime;

    @Column(name = "hdd_count", nullable = false)
    private Integer hddCount;

    @Column(name = "channel_count", nullable = false)
    private Integer channelCount;


   
}