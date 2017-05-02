package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Image;


public class ImageJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Image
	 */
	private Image Image;

	public Image getImage() {
		return Image;
	}
	
	public ImageJsonMessage(String mediaId){
		this.Image = new Image(mediaId);
	}

}

