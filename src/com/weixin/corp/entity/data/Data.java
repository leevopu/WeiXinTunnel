package com.weixin.corp.entity.data;

public class Data {
	private String title;
	private String toUser;
	private String context;

	public Data(String title, String toUser, String context) {
		super();
		this.title = title;
		this.toUser = toUser;
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

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
