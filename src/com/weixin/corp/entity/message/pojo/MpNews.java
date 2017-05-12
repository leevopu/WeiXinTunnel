package com.weixin.corp.entity.message.pojo;

public class MpNews {
	private MpArticle[] articles;

	private String MediaId;

	public MpArticle[] getArticles() {
		return articles;
	}

	public String getMediaId() {
		return MediaId;
	}

	public MpNews(String mediaId, MpArticle[] artricle) {
		super();
		// 当前处理均为一条记录
		this.MediaId = mediaId;
		this.articles = artricle;
	}

}
