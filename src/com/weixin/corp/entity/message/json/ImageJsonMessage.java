package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Image;

/**
 * ͼƬ��Ϣ
 * 
 */
public class ImageJsonMessage extends CorpBaseJsonMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����Image
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
