package com.weixin.corp.entity.message;

public class TextMessage extends CorpBaseMessage {
	/**
	 * �ظ�����Ϣ����
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
