package com.weixin.corp.entity.message.xml;


/**
 * 文本消息
 * 
 */
public class TextXMLMessage extends CorpBaseXMLMessage {
	/**
	 * 只能在初始化时配置content
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