package com.weixin.corp.entity;

public class TextMessage extends CorpBaseMessage {
	/**
	 * 回复的消息内容
	 */
	private String Content;

	public String getContent() {
		return Content;
	}

	public TextMessage(String content) {
		super();
		Content = content;
	}
	
}
