package com.weixin.corp.entity.message;

import java.io.File;
import java.io.Serializable;

import com.weixin.corp.entity.message.pojo.MpNews;

/**
 * 数据库结果群发和用户主动调用的封装消息
 * 
 */
public class RequestCall implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;

	/**
	 * 消息标题（非必填)
	 */
	private String title;
	/**
	 * 发送人（"database"|user=部门名称+手机号） <br>
	 * <br>
	 * 主动调用user赋值时校验身份
	 */
	private String fromUser;
	/**
	 * 接收人（用部门名称+手机号来唯一确定用户） <br>
	 * <br>
	 * 用逗号或竖线分割多个接收人，确保及时更新用户信息，否则只有能匹配上的用户能接收到消息
	 */
	private String toUser;
	/**
	 * 消息类型（文本text，图片image，视频video，文件file，图文mpnnews）
	 */
	private String msgType;
	/**
	 * 文本内容（若msgType为text则需有值)
	 */
	private String text;
	/**
	 * 若msgType不为text则需有值
	 */
	private File media;
	/**
	 * msgType为mpnews
	 */
	private MpNews mpnews;
	/**
	 * 发送时间（控制时间延迟发送，立即发送可不配置） <br>
	 * <br>
	 * 格式 yyyy-MM-dd HH:mm:ss 样例 2020-10-10 10:00:00
	 */
	private String sendTime;
	/**
	 * 响应后的错误消息用于提醒
	 */
	private String errorInfo;

	/**
	 * 素材上传后获得的ID
	 */
	private String mediaId;

	public RequestCall() {
		super();
	}

	public RequestCall(String fromUser, String toUser, String msgType,
			String text, File media, String sendTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.msgType = msgType;
		this.text = text;
		this.media = media;
		this.sendTime = sendTime;
	}

	public RequestCall(String fromUser, String toUser, String msgType,
			String text, String mediaPath, String sendTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.msgType = msgType;
		this.text = text;
		this.media = new File(mediaPath);
		this.sendTime = sendTime;
	}
	/**
	 * 图文消息
	 * @param fromUser
	 * @param toUser
	 * @param msgType
	 * @param text
	 * @param media
	 * @param sendTime
	 */
	public RequestCall(String fromUser, String toUser, String msgType,
			String text, MpNews media, String sendTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.msgType = msgType;
		this.text = text;
		this.mpnews = media;
		this.sendTime = sendTime;
	}

	/**
	 * 数据库跑批结果的构造方法，fromUser默认值database，msgType默认text
	 * 
	 * @param title
	 *            消息标题
	 * @param toUser
	 *            接收人
	 * @param sendTime
	 *            发送时间
	 * @param text
	 *            主体内容
	 */
	public RequestCall(String title, String toUser, String sendTime, String text) {
		super();
		this.title = title;
		this.toUser = toUser;
		this.sendTime = sendTime;
		this.text = text;
		this.fromUser = "database";
		this.msgType = "text";
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

	public File getMedia() {
		return media;
	}

	public void setMedia(File media) {
		this.media = media;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public MpNews getMpnews() {
		return mpnews;
	}

	public void setMpnews(MpNews mpnews) {
		this.mpnews = mpnews;
	}
}
