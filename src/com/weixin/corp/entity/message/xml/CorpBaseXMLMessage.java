package com.weixin.corp.entity.message.xml;

public class CorpBaseXMLMessage {
	/**
	 * ���շ��˺� <br>
	 * <br>
	 * ���շ��˺��뷢�ͷ��˺� �ͻ���OpenId��΢�ſ������໥����
	 */
	private String ToUserName;
	/**
	 * ���ͷ��˺� <br>
	 * <br>
	 * ���ͷ��˺�����շ��˺� �ͻ���OpenId��΢�ſ������໥����
	 */
	private String FromUserName;
	/**
	 * ��Ϣ����ʱ�� �����ͣ�
	 */
	private Long CreateTime;
	/**
	 * ��Ϣ���ͣ�text/image/...��
	 */
	private String MsgType;
	/**
	 * ��Ϣid (64λ����)
	 */
	private Long MsgID;
	
	private int AgentID;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Long getMsgID() {
		return MsgID;
	}

	public void setMsgID(Long msgID) {
		MsgID = msgID;
	}

	public int getAgentID() {
		return AgentID;
	}

	public void setAgentID(int agentID) {
		AgentID = agentID;
	}
	

}
