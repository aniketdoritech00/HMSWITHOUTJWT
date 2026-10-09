package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Static_Data")
public class StaticDataEntity {
	
	@Id
	@Column(name="ID") 
	private Long id;
	
	@Column(name="CODE") private String code;
	
	@Column(name="SERIAL") private String serial;
	
	@Column(name="DESCP1") private String descp1;
	
	@Column(name="DESCP2") private String descp2;
	
	@Column(name="DESCP3") private String descp3;
	
	@Column(name="DESCP4") private String descp4;
	
	@Column(name="DESCP5") private String descp5;
	
	@Column(name="DESCP6") private String descp6;
	
	@Column(name="DESCP7") private String descp7;
	
	@Column(name="DESCP8") private String descp8;
	
	@Column(name="DESCP9") private String descp9;
	
	@Column(name="DESCP10") private String descp10;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getDescp6() {
		return descp6;
	}

	public void setDescp6(String descp6) {
		this.descp6 = descp6;
	}

	public String getDescp7() {
		return descp7;
	}

	public void setDescp7(String descp7) {
		this.descp7 = descp7;
	}

	public String getDescp8() {
		return descp8;
	}

	public void setDescp8(String descp8) {
		this.descp8 = descp8;
	}

	public String getDescp9() {
		return descp9;
	}

	public void setDescp9(String descp9) {
		this.descp9 = descp9;
	}

	public String getDescp10() {
		return descp10;
	}

	public void setDescp10(String descp10) {
		this.descp10 = descp10;
	}

}
