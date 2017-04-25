package com.weixin.corp.entity.message;

public class TextMessage extends CorpBaseMessage {
	/**
	 * 回复的消息内容
	 */
	private Text text;

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}
	
	public TextMessage() {
		super();
		this.text = new Text();
	}

}
