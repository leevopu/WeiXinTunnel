package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.Video;

public class VideoJsonMessage extends CorpBaseJsonMessage {
	/**
	 * ֻ���ڳ�ʼ��ʱ����Video
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

