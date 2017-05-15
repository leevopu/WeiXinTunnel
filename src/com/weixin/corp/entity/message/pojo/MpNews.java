package com.weixin.corp.entity.message.pojo;

public class MpNews {
	private MpArticle[] articles;

	private String mediaId;

	public MpArticle[] getArticles() {
		return articles;
	}
	
	public String getMediaId() {
		return mediaId;
	}

	public MpNews(String mediaId, MpArticle[] artricle) {
		super();
		// 当前处理均为一条记录
		this.mediaId = mediaId;
		this.articles = artricle;
	}
	public MpNews() {
		super();
	}
}
