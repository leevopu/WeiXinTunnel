package com.weixin.corp.entity.menu;


/**s
 * ��ť
 * 
 * @author caspar.chen
 * @version 1.0
 * 
 */
public class Button {

	/**
	 * ��ť����
	 */
	private String name;

	/**
	 * ��ť����
	 */
	private String type;

	/**
	 * ��ťkeyֵ
	 */
	private String key;

	/**
	 * ��ťurl
	 */
	private String url;

	/**
	 * �Ӱ�ť�б�
	 */
	private Button[] sub_button;

	public Button[] getSub_button() {
		return sub_button;
	}

	public void setSub_button(Button[] sub_button) {
		this.sub_button = sub_button;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Button(String name, String type, String key, String url,
			Button[] sub_button) {
		super();
		this.name = name;
		this.type = type;
		this.key = key;
		this.url = url;
		this.sub_button = sub_button;
	}

	public Button() {
		super();
	}

}
