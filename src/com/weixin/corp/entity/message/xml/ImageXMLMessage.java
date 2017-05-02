package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.Image;

/**
 * 图片消息
 * 
 */
public class ImageXMLMessage extends CorpBaseXMLMessage {
	/**
	 * 只能在初始化时配置Image
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
