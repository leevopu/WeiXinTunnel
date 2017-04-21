package com.weixin.corp.entity.message;

import java.util.Map;

public class TemplateMessage {
	/**
	 * ģ����Ϣid
	 */
	private String template_id;
	/**
	 * �û�openId
	 */
	private String touser;
	/**
	 * URL�ÿգ����ڷ��ͺ󣬵��ģ����Ϣ�����һ���հ�ҳ�棨ios�������޷������android��
	 */
	private String url;
	/**
	 * ������ɫ
	 */
	private String topcolor;
	/**
	 * ��ϸ����
	 */
	private Map<String, TemplateMessageData> data;

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}

	public Map<String, TemplateMessageData> getData() {
		return data;
	}

	public void setData(Map<String, TemplateMessageData> data) {
		this.data = data;
	}
}
