package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Text;

public class TextJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 回复的消息内容
	 */
	private Text text;

	public Text getText() {
		return text;
	}
	
	public TextJsonMessage(String content) {
		super();
		this.text = new Text(content);
		this.setMsgtype("text");
	}

}
