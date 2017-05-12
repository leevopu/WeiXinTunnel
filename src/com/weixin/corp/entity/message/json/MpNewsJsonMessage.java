package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.MpArticle;
import com.weixin.corp.entity.message.pojo.MpNews;

public class MpNewsJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Mpnews
	 */
	private MpNews mpnews;

	public MpNews getMpnews() {
		return mpnews;
	}

	public MpNewsJsonMessage(String title,String thumb_media_id,String content,String digest){
		super();
		MpArticle[] articles = {new MpArticle(title,thumb_media_id,content,digest)};
		this.mpnews = new MpNews("", articles);
		this.setMsgtype("mpnews");
		
	}

	public MpNewsJsonMessage(String mediaId) {
		super();
		this.mpnews = new MpNews(mediaId, new MpArticle[0]);
		this.setMsgtype("mpnews");
	}
	
	@Override
	public String getMediaId() {
		return this.mpnews.getMediaId();
	}

}

