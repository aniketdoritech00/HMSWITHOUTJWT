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
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "HDD")
//public class HDD {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String status;
//
//    @Column(nullable = false)
//    private Integer capacity;
//
//    @Column(name = "free_space", nullable = false)
//    private Integer freeSpace; // MB
//
//    @ManyToOne
//    @JoinColumn(name = "dvr_id")
//    @JsonBackReference
//    private DVR dvr;
//
//}