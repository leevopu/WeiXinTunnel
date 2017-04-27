package com.weixin.corp.entity.user;

import java.io.Serializable;

/**
 * 微信用户信息
 */
public class User implements Serializable {

	private static final long serialVersionUID = 3349073660021645866L;
	private String userid;
	private String name;
	private String department;
	private String position;
	private String mobile;
	private String gender;
	private String email;
	private String weixinid;
	private Integer enable = 1; //默认启用， 0为禁用


	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
	public void setDepartment(int departmentId) {
		this.department = departmentId + "";
	} 

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeixinid() {
		return weixinid;
	}

	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

}
