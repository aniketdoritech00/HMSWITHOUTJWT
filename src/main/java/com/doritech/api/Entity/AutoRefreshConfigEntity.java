package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "auto_refresh_config_entity")
public class AutoRefreshConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_id", nullable = false, unique = true)
    private String empId;

    @Column(name = "status")
    private String status;

    public AutoRefreshConfigEntity() {
    }

    public AutoRefreshConfigEntity(String empId, String status) {
        this.empId = empId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}