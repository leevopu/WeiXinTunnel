package test.customer.info;

import java.io.Serializable;
import java.util.Date;

public class RequestInfo implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;
	/**
	 * �����ˣ�֮����ܻ���У���жϣ�
	 */
	private String fromUser;
	/**
	 * �����ˣ����ֻ��ţ�����������Ψһȷ���û���
	 * <br>
	 * ��|�ָ��������ˣ�ȷ����ʱ�����û���Ϣ
	 */
	private String toUser;
	/**
	 * ��Ϣ���ͣ��ı�text��ͼƬimage����Ƶvideo��
	 */
	private String infoType;
	/**
	 * ͼƬ��Ƶ·�������ı�����
	 */
	private String pathOrContent;
	/**
	 * ϣ�����͵�ʱ�䣨�ӳٷ��ͣ��ɲ����ã�
	 */
	private Date chooseTime;
	
	public RequestInfo(String fromUser, String toUser, String infoType,
			String pathOrContent, Date chooseTime) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.infoType = infoType;
		this.pathOrContent = pathOrContent;
		this.chooseTime = chooseTime;
	}
	
	public RequestInfo(){
		super();
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

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getPathOrContent() {
		return pathOrContent;
	}

	public void setPathOrContent(String pathOrContent) {
		this.pathOrContent = pathOrContent;
	}

	public Date getChooseTime() {
		return chooseTime;
	}

	public void setChooseTime(Date chooseTime) {
		this.chooseTime = chooseTime;
	}

}
