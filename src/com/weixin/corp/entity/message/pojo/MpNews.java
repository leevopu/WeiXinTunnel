package com.weixin.corp.entity.message.pojo;

public class MpNews {
	/**
	 * ͼ����Ϣ����
	 */
	private MpArticle[] articles;
	
	public MpArticle[] getArticles() {
		return articles;
	}
	

	public MpNews(MpArticle[] artricle) {
		super();
		//��ǰ�����Ϊһ����¼
		articles=artricle;
	}
	
}
