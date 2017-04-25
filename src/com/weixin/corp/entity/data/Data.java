package com.weixin.corp.entity.data;

public class Data {
	private String title;
	private String toUser;
	private String amount;

	public Data(String title, String toUser, String amount) {
		super();
		this.title = title;
		this.toUser = toUser;
		this.amount = amount;
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

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}
