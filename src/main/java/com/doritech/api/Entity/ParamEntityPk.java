package com.doritech.api.Entity;

import java.io.Serializable;

public class ParamEntityPk implements Serializable {
    private int id;
    private String code;
    private String serial;

    // Constructors
    public ParamEntityPk() {
    }

    public ParamEntityPk(int id, String code, String serial) {
        this.id = id;
        this.code = code;
        this.serial = serial;

    }

    // Getters and Setters

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

}
