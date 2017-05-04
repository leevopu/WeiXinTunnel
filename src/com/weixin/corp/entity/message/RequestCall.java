package com.weixin.corp.entity.message;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.weixin.corp.utils.CommonUtil;

/**
 * ���ݿ���Ⱥ�����û��������õķ�װ��Ϣ
 * 
 */
public class RequestCall implements Serializable, Delayed {

	private static final long serialVersionUID = 5570817032197190881L;

	/**
	 * ��Ϣ���⣨�Ǳ���)
	 */
	private String title;
	/**
	 * �����ˣ�database, user��
	 */
	private String fromUser;
	/**
	 * �����ˣ��ò�������+�ֻ�����Ψһȷ���û��� <br>
	 * <br>
	 * �ö��Ż����߷ָ��������ˣ�ȷ����ʱ�����û���Ϣ������ֻ����ƥ���ϵ��û��ܽ��յ���Ϣ
	 */
	private String toUser;
	/**
	 * ��Ϣ���ͣ��ı�text��ͼƬimage����Ƶvideo���ļ�file��
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

	//
	@Override
	public int compareTo(Delayed o) {
		RequestCall another = (RequestCall) o;
		// o���û��sendTime��ǰ�ţ�����ִ��
		if (null == another.getSendTime()) {
			return -1;
		}
		if (CommonUtil.getStrDate(this.getSendTime(), "yyyy-MM-dd HH:mm:ss")
				.getTime() > CommonUtil.getStrDate(another.getSendTime(),
				"yyyy-MM-dd HH:mm:ss").getTime()) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {

		return unit.convert(
				CommonUtil
						.getStrDate(this.getSendTime(), "yyyy-MM-dd HH:mm:ss")
						.getTime()
						- System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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

}
