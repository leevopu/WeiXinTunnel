package com.weixin.corp.entity.message.xml;


/**
 * �ı���Ϣ
 * 
 */
public class TextXMLMessage extends CorpBaseXMLMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����content
	 */
	private String Content;

	public String getContent() {
		return Content;
	}

	public TextXMLMessage(String content) {
		super();
		this.Content = content;
		this.setMsgType("text");
	}
	
}