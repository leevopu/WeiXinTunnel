package com.weixin.corp.entity.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 微信用户信息
 */
public class User implements Serializable {

	private static final long serialVersionUID = 3349073660021645866L;
	private String userid;
	private String name;
	private Set<Integer> department = new HashSet<Integer>();
	private String position;
	private String mobile;
	private String gender;
	private String email;
	private String weixinid;
	private Integer enable = 1; //默认启用， 0为禁用
	private String oaid; //内部系统相互调用的用户id

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
	
	public Set<Integer> getDepartment() {
		return department;
	}

	public void setDepartment(Set<Integer> department) {
		this.department = department;
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

	public String getOaid() {
		return oaid;
	}

	public void setOaid(String oaid) {
		this.oaid = oaid;
	}

}
