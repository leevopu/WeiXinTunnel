package com.weixin.corp.entity.message.xml;

import com.weixin.corp.entity.message.pojo.MpArticle;
import com.weixin.corp.entity.message.pojo.MpNews;


/**
 * 图文消息
 * 
 */
public class MpNewsXMLMessage extends CorpBaseXMLMessage {
	/**
	 * 多条图文消息信息
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