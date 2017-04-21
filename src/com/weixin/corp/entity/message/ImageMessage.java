package com.weixin.corp.entity.message;


public class ImageMessage extends CorpBaseMessage {
	/**
	 * 只能在初始化时配置Image
	 */
	private Image Image;

	public Image getImage() {
		return Image;
	}
	
	public ImageMessage(String mediaId){
		this.Image = new Image(mediaId);
	}

}

