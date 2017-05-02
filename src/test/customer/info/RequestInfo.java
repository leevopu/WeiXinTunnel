package test.customer.info;

import java.io.Serializable;

public class RequestInfo implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;
	/**
	 * �����ˣ�֮����ܻ���У���жϣ�
	 */
	private String fromUser;
	/**
	 * �����ˣ����ֻ��ţ�����������Ψһȷ���û���
	 * <br>
	 * <br>
	 * �ö��ŷָ��������ˣ�ȷ����ʱ�����û���Ϣ������ֻ����ƥ���ϵ��û��ܽ��յ���Ϣ
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
	private String mediaPath;
	/**
	 * ����ʱ�䣨����ʱ���ӳٷ��ͣ��������Ϳɲ����ã�
	 * <br>
	 * <br>
	 * ��ʽ yyyy-MM-dd HH:mm:ss  ���� 2020-10-10 10:00:00
	 */
	private String sendTime;
	
	public RequestInfo(){
		super();
	}

	public RequestInfo(String fromUser, String toUser, String msgType,
			String text, String mediaPath, String sendTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.msgType = msgType;
		this.text = text;
		this.mediaPath = mediaPath;
		this.sendTime = sendTime;
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
