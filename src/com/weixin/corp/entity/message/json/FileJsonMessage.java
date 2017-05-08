package com.weixin.corp.entity.message.json;

import com.weixin.corp.entity.message.pojo.File;

public class FileJsonMessage extends CorpBaseJsonMessage {
	/**
	 * 只能在初始化时配置File
	 */
	private File file;

	public File getFile() {
		return file;
	}
	
	public FileJsonMessage(String mediaId){
		super();
		this.file = new File(mediaId);
		this.setMsgtype("file");
	}

	@Override
	public String getMediaId() {
		return this.file.getMediaId();
	}
}

