package com.weixin.corp.entity.message.pojo;

public class MpArticle {
	/**
	 * 图文消息名称
	 */
	private String Title;
	/**
	 * 图文消息缩略图的media_id, 可以在上传多媒体文件接口中获得。此处thumb_media_id即上传接口返回的media_id  必填
	 */
	private String Thumb_media_id;
	/**
	 * 图文消息的作者，不超过64个字节  非必填
	 */
	private String Author;
	/**
	 * 图文消息点击“阅读原文”之后的页面链接 非必填
	 */
	private String Content_source_url;
	/**
	 * 图文消息的内容，支持html标签，不超过666 K个字节 必填
	 */
	private String Content;
	/**
	 * 图文消息的描述，不超过512个字节，超过会自动截断 非必填
	 */
	private String Digest;
	/**
	 * 是否显示封面，1为显示，0为不显示 非必填
	 */
	private int Show_cover_pic;
	
	public String getTitle() {
		return Title;
	}
	public String getThumb_media_id() {
		return Thumb_media_id;
	}
	public String getAuthor() {
		return Author;
	}
	public String getContent_source_url() {
		return Content_source_url;
	}
	public String getContent() {
		return Content;
	}
	public String getDigest() {
		return Digest;
	}
	public int getShow_cover_pic() {
		return Show_cover_pic;
	}
	public MpArticle(String title, String thumb_media_id, String content, String digest) {
		super();
		Title = title;
		Thumb_media_id = thumb_media_id;
		Content = content;
		Digest = digest;
		Show_cover_pic = 1;
	}
	
}