package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Text;

public class TextJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Image
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

	@Override
	public String getMediaId() {
		return null;
	}
}

