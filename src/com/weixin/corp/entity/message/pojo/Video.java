package com.weixin.corp.entity.message.pojo;

public class Video {
	private String MediaId;

	private String title;
	
	private String description;
	
	public String getMediaId() {
		return MediaId;
	}
	
	//
	// public void setMediaId(String mediaId) {
	// MediaId = mediaId;
	// }

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Video(String mediaId) {
		this.MediaId = mediaId;
		this.title = "";
		this.description = "";
	}
	
	public Video(String mediaId, String title, String description) {
		this.MediaId = mediaId;
		this.title = title;
		this.description = description;
	}
}
