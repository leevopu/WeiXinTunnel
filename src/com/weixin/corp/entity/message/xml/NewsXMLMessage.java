package com.weixin.corp.entity.message.xml;

import java.util.List;

import com.weixin.corp.entity.message.pojo.Article;

/**
 * ͼ����Ϣ
 * 
 */
public class NewsXMLMessage extends CorpBaseXMLMessage {
	/**
	 * ͼ����Ϣ����������Ϊ10������
	 */
	private int ArticleCount;
	/**
	 * ����ͼ����Ϣ��Ϣ��Ĭ�ϵ�һ��itemΪ��ͼ
	 */
	private List<Article> Articles;

	public int getArticleCount() {
		return ArticleCount;
	}


	public List<Article> getArticles() {
		return Articles;
	}

	public NewsXMLMessage(List<Article> articles) {
		super();
		if (null != articles) {
			ArticleCount = articles.size();
		}
		Articles = articles;
	}

}