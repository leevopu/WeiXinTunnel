package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.Text;

/**
 * 图文消息
 * 
 */
public class TextXMLMessage extends CorpBaseXMLMessage {
	/**
	 * 只能在初始化时配置text
	 */
	private Text text;

	public Text getText() {
		return text;
	}

	public TextXMLMessage(String content) {
		super();
		this.text = new Text(content);
	}
	
}