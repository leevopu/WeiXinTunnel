package com.weixin.corp.entity.message.pojo;

public class MpArticle {
	/**
	 * ͼ����Ϣ����
	 */
	private String Title;
	/**
	 * ͼ����Ϣ����ͼ��media_id, �������ϴ���ý���ļ��ӿ��л�á��˴�thumb_media_id���ϴ��ӿڷ��ص�media_id  ����
	 */
	private String Thumb_media_id;
	/**
	 * ͼ����Ϣ�����ߣ�������64���ֽ�  �Ǳ���
	 */
	private String Author;
	/**
	 * ͼ����Ϣ������Ķ�ԭ�ġ�֮���ҳ������ �Ǳ���
	 */
	private String Content_source_url;
	/**
	 * ͼ����Ϣ�����ݣ�֧��html��ǩ��������666 K���ֽ� ����
	 */
	private String Content;
	/**
	 * ͼ����Ϣ��������������512���ֽڣ��������Զ��ض� �Ǳ���
	 */
	private String Digest;
	/**
	 * �Ƿ���ʾ���棬1Ϊ��ʾ��0Ϊ����ʾ �Ǳ���
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