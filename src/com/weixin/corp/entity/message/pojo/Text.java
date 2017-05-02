package com.weixin.corp.entity.message.pojo;

public class Text {

	private String Content;

	public String getContent() {
		return Content;
	}

//	public void setContent(String content) {
//		this.content = (null == content ? "" : content);
//	}
	
	public Text(String content) {
		this.Content = (null == content ? "" : content);
	}

}
