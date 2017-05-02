package com.weixin.corp.entity.message;

import java.io.Serializable;

/**
 * 数据库结果群发和用户主动调用的封装消息
 * 
 */
public class CallMessage implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;

	/**
	 * 消息标题（非必填)
	 */
	private String title;
	/**
	 * 发送人（database, user）
	 */
	private String fromUser;
	/**
	 * 接收人（用手机号，部门名称来唯一确定用户） <br>
	 * <br>
	 * 用逗号分割多个接收人，确保及时更新用户信息，否则只有能匹配上的用户能接收到消息
	 */
	private String toUser;
	/**
	 * 消息类型（文本text，图片image，视频video，文件file）
	 */
	private String msgType;
	/**
	 * 文本内容（若msgType为text则需有值)
	 */
	private String text;
	/**
	 * 若msgType不为text则需有值
	 */
	private String mediaPath;
	/**
	 * 发送时间（控制时间延迟发送，立即发送可不配置） <br>
	 * <br>
	 * 格式 yyyy-MM-dd HH:mm:ss 样例 2020-10-10 10:00:00
	 */
	private String sendTime;

	public CallMessage() {
		super();
	}

	public CallMessage(String fromUser, String toUser, String msgType,
			String text, String mediaPath, String sendTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.msgType = msgType;
		this.text = text;
		this.mediaPath = mediaPath;
		this.sendTime = sendTime;
	}

	public CallMessage(String title, String toUser, String sendTime, String text) {
		super();
		this.title = title;
		this.toUser = toUser;
		this.sendTime = sendTime;
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

}
