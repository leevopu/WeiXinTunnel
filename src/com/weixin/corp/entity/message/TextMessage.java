package com.weixin.corp.entity.message;

public class TextMessage extends CorpBaseMessage {
	/**
	 * �ظ�����Ϣ����
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
