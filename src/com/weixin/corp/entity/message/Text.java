package com.weixin.corp.entity.message;

public class Text {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = (null == content ? "" : content);
	}

}
