package com.weixin.corp.entity.message;

import java.io.Serializable;
/**
 * ��ʱȺ�����������õ���Ϣ��װ
 * 
 */
public class RequestCall implements Serializable {
	/**
	 * ��Ϣ���⣨�Ǳ����Ҫͼ��ʹ�ã�
	 */
	private String title;
	/**
	 * �ݲ�ʹ�ã����õ�ϵͳ����caller��
	 */
	private String fromUser;
	/**
	 * �����ˣ���OAϵͳ���û�id�� <br>
	 * <br>
	 * �ö��Ż����߷ָ���������
	 * ����ֻ����ƥ���ϵ��û��ܽ��յ���Ϣ
	 */
	private String toUser;
	/**
	 * ��Ϣ���ͣ��ı�text��ͼƬimage����Ƶvideo���ļ�file��ͼ��mpnews��
	 */
	private String msgType;
	/**
	 * �ı����ݣ���msgTypeΪtext������ֵ��
	 * ��ʶ��\rֻʶ��\n
	 */
	private String text;
	/**
	 * ͼƬ��Ƶ�ļ��Ķ�������
	 */
	private byte[] mediaByte;
	/**
	 * ͼƬ��Ƶ�ļ����ļ���
	 */
	private String mediaName;
	/**
	 * ����ʱ�䣨����ʱ���ӳٷ��ͣ��������Ϳɲ����ã� <br>
	 * <br>
	 * ��ʽ yyyy-MM-dd HH:mm:ss ���� 2020-10-10 10:10:00
	 * <br>
	 * �� yyyy-MM-dd ���� 2020-10-10����ÿ��Ⱥ��ʹ�ã�
	 */
	private String sendTime;
	/**
	 * �ز��ϴ����õ�ID���ڲ��ṩ�����������ṩ
	 */
	private String mediaId;

	private static final long serialVersionUID = 5570817032197190881L;
	
	public RequestCall() {
		super();
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

	public byte[] getMediaByte() {
		return mediaByte;
	}

	public void setMediaByte(byte[] mediaByte) {
		this.mediaByte = mediaByte;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

}
