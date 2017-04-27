package com.weixin.corp.entity.data;

import java.util.Date;

public class Data {
	private String title;
	private String touser;
	private Object sysdate;
	private String context;

	public Data(String title, String touser, Object sysdate, String context) {
		super();
		this.title = title;
		this.touser = touser;
		this.sysdate = sysdate;
		this.context = context;
	}

	public Data() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public Object getSysdate() {
		return sysdate;
	}

	public void setSysdate(Date sysdate) {
		this.sysdate = sysdate;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
