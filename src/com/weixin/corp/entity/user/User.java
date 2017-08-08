package com.weixin.corp.entity.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	private String avatar;
	private List<Attr> extattr = new ArrayList<Attr>();
	private List<String> order = new ArrayList<String>();
	private int status;
	private int enable = 1; // 默认启用， 0为禁用
	private String oaid; // 内部系统相互调用的用户id

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

	public void addDepartment(Integer departmentId) {
		this.department.add(departmentId);
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getOaid() {
		return oaid;
	}

	public void setOaid(String oaid) {
		this.oaid = oaid;
	}
	
//	public void addExtAttr(String name, String value) {
//	    this.extattrs.add(new Attr(name, value));
//	  }

	public List<Attr> getExtattr() {
		return this.extattr;
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(List<String> order) {
		this.order = order;
	}

	public static class Attr {

		private String name;
		private String value;

		public Attr(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
