package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.Text;

/**
 * ͼ����Ϣ
 * 
 */
public class TextXMLMessage extends CorpBaseXMLMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����text
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