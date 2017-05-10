package com.weixin.corp.entity.message;

import java.io.File;
import java.io.Serializable;

import com.weixin.corp.entity.message.pojo.MpNews;

/**
 * ���ݿ���Ⱥ�����û��������õķ�װ��Ϣ
 * 
 */
public class RequestCall implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;

	/**
	 * ��Ϣ���⣨�Ǳ���)
	 */
	private String title;
	/**
	 * �����ˣ�"database"|user=��������+�ֻ��ţ� <br>
	 * <br>
	 * ��������user��ֵʱУ�����
	 */
	private String fromUser;
	/**
	 * �����ˣ��ò�������+�ֻ�����Ψһȷ���û��� <br>
	 * <br>
	 * �ö��Ż����߷ָ��������ˣ�ȷ����ʱ�����û���Ϣ������ֻ����ƥ���ϵ��û��ܽ��յ���Ϣ
	 */
	private String toUser;
	/**
	 * ��Ϣ���ͣ��ı�text��ͼƬimage����Ƶvideo���ļ�file��ͼ��mpnnews��
	 */
	private String msgType;
	/**
	 * �ı����ݣ���msgTypeΪtext������ֵ)
	 */
	private String text;
	/**
	 * ��msgType��Ϊtext������ֵ
	 */
	private File media;
	/**
	 * msgTypeΪmpnews
	 */
	private MpNews mpnews;
	/**
	 * ����ʱ�䣨����ʱ���ӳٷ��ͣ��������Ϳɲ����ã� <br>
	 * <br>
	 * ��ʽ yyyy-MM-dd HH:mm:ss ���� 2020-10-10 10:00:00
	 */
	private String sendTime;
	/**
	 * ��Ӧ��Ĵ�����Ϣ��������
	 */
	private String errorInfo;

	/**
	 * �ز��ϴ����õ�ID
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
	 * ͼ����Ϣ
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
	 * ���ݿ���������Ĺ��췽����fromUserĬ��ֵdatabase��msgTypeĬ��text
	 * 
	 * @param title
	 *            ��Ϣ����
	 * @param toUser
	 *            ������
	 * @param sendTime
	 *            ����ʱ��
	 * @param text
	 *            ��������
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
