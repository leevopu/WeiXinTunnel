package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Image;

/**
 * 图片消息
 * 
 */
public class ImageJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Image
	 */
	private Image Image;

	public Image getImage() {
		return Image;
	}

	public ImageJsonMessage(String mediaId) {
		super();
		this.Image = new Image(mediaId);
		this.setMsgtype("image");
	}

	@Override
	public String getMediaId() {
		return this.Image.getMediaId();
	}
}
