package com.weixin.corp.entity.message.pojo;

public class MpNews {
	/**
	 * 图文消息名称
	 */
	private MpArticle[] articles;
	
	public MpArticle[] getArticles() {
		return articles;
	}
	

	public MpNews(MpArticle[] artricle) {
		super();
		//当前处理均为一条记录
		articles=artricle;
	}
	
}
