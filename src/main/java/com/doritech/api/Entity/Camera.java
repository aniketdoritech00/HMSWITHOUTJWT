//package com.doritech.api.Entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "Camera")
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Camera {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = true)
//    private String name;
//
//    @Column(nullable = true)
//    private String model;
//
//    @Column(name = "serial_number", nullable = true)
//    private String serialNumber;
//
//    @ManyToOne
//    @JoinColumn(name = "dvr_id")
//    @JsonBackReference
//    private DVR dvr;
//
//
//
//
//
//}