package com.doritech.api.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "param_entity")
@IdClass(ParamEntityPk.class) // Specify the composite primary key class
public class ParamEntity {
    @Id
    private int id;
    @Id
    private String code;
    @Id
    private String serial;
    private String descp1;
    private String descp2;
    private String descp3;
    private String descp4;
    private String descp5;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;

    public ParamEntity() {
    }

    public ParamEntity(int id, String code, String serial, String descp1, String descp2, String descp3, String descp4, String descp5, String param1, String param2, String param3, String param4, String param5) {
        this.id = id;
        this.code = code;
        this.serial = serial;
        this.descp1 = descp1;
        this.descp2 = descp2;
        this.descp3 = descp3;
        this.descp4 = descp4;
        this.descp5 = descp5;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
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

    public String getDescp1() {
        return descp1;
    }

    public void setDescp1(String descp1) {
        this.descp1 = descp1;
    }

    public String getDescp2() {
        return descp2;
    }

    public void setDescp2(String descp2) {
        this.descp2 = descp2;
    }

    public String getDescp3() {
        return descp3;
    }

    public void setDescp3(String descp3) {
        this.descp3 = descp3;
    }

    public String getDescp4() {
        return descp4;
    }

    public void setDescp4(String descp4) {
        this.descp4 = descp4;
    }

    public String getDescp5() {
        return descp5;
    }

    public void setDescp5(String descp5) {
        this.descp5 = descp5;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public String getParam5() {
        return param5;
    }

    public void setParam5(String param5) {
        this.param5 = param5;
    }

    @Override
    public String toString() {
        return "ParamEntity{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", serial='" + serial + '\'' +
                ", descp1='" + descp1 + '\'' +
                ", descp2='" + descp2 + '\'' +
                ", descp3='" + descp3 + '\'' +
                ", descp4='" + descp4 + '\'' +
                ", descp5='" + descp5 + '\'' +
                ", param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                ", param3='" + param3 + '\'' +
                ", param4='" + param4 + '\'' +
                ", param5='" + param5 + '\'' +
                '}';
    }
    
    
}
