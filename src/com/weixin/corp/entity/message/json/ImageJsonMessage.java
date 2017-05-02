package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Image;

public class ImageJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Image
	 */
	private Image image;

	public Image getImage() {
		return image;
	}
	
	public ImageJsonMessage(String mediaId){
		super();
		this.image = new Image(mediaId);
		this.setMsgType("image");
	}

}

