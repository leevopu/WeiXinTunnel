package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.Image;

/**
 * ͼƬ��Ϣ
 * 
 */
public class ImageXMLMessage extends CorpBaseXMLMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����Image
	 */
	private Image Image;

	public Image getImage() {
		return Image;
	}
	
	public ImageXMLMessage(String mediaId) {
		super();
		this.Image = new Image(mediaId);
		this.setMsgType("text");
	}
}
