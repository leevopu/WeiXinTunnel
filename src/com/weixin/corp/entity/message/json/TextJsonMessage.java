package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Text;

public class TextJsonMessage extends CorpBaseJsonMessage {
	/**
	 * �ظ�����Ϣ����
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
