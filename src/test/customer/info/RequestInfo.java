package test.customer.info;

import java.io.Serializable;
import java.util.Date;

public class RequestInfo implements Serializable {

	private static final long serialVersionUID = 5570817032197190881L;
	/**
	 * 发送人（之后可能会有校验判断）
	 */
	private String fromUser;
	/**
	 * 接收人（用手机号，部门名称来唯一确定用户）
	 * <br>
	 * 用|分割多个接收人，确保及时更新用户信息
	 */
	private String toUser;
	/**
	 * 消息类型（文本text，图片image，视频video）
	 */
	private String infoType;
	/**
	 * 图片视频路径或者文本内容
	 */
	private String pathOrContent;
	/**
	 * 希望发送的时间（延迟发送，可不配置）
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
