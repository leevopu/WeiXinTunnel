package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Video;

public class VideoJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置Video
	 */
	private Video video;

	public Video getVideo() {
		return video;
	}
	
	public VideoJsonMessage(String mediaId){
		super();
		this.video = new Video(mediaId);
		this.setMsgtype("video");
	}

	@Override
	public String getMediaId() {
		return this.video.getMediaId();
	}
}

