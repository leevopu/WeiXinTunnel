package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.MpArticle;
import com.weixin.corp.entity.message.pojo.MpNews;


/**
 * ͼ����Ϣ
 * 
 */
public class MpNewsXMLMessage extends CorpBaseXMLMessage {
	/**
	 * ����ͼ����Ϣ��Ϣ
	 */
	private MpNews mpnews;

	public MpNews getMpnews() {
		return mpnews;
	}

	public MpNewsXMLMessage(String mediaId, String title,String thumb_media_id,String content,String digest) {
		super();
		MpArticle[] articles = {new MpArticle(title,thumb_media_id,content,digest)};
		this.mpnews = new MpNews(mediaId, articles);
		this.setMsgType("mpnews");
	}

}