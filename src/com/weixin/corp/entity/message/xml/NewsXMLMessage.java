package com.weixin.corp.entity.message.xml;

import java.util.List;

import com.weixin.corp.entity.message.pojo.Article;

/**
 * 图文消息
 * 
 */
public class NewsXMLMessage extends CorpBaseXMLMessage {
	/**
	 * 图文消息个数，限制为10条以内
	 */
	private int ArticleCount;
	/**
	 * 多条图文消息信息，默认第一个item为大图
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