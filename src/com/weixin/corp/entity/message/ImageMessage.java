package com.weixin.corp.entity.message;


public class ImageMessage extends CorpBaseMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����Image
	 */
	private Image Image;

	public Image getImage() {
		return Image;
	}
	
	public ImageMessage(String mediaId){
		this.Image = new Image(mediaId);
	}

}

