package com.weixin.corp.entity.message.xml;

public class CorpBaseXMLMessage {
	/**
	 * 接收方账号 <br>
	 * <br>
	 * 接收方账号与发送方账号 客户方OpenId与微信开发号相互配置
	 */
	private String ToUserName;
	/**
	 * 发送方账号 <br>
	 * <br>
	 * 发送方账号与接收方账号 客户方OpenId与微信开发号相互配置
	 */
	private String FromUserName;
	/**
	 * 消息创建时间 （整型）
	 */
	private Long CreateTime;
	/**
	 * 消息类型（text/image/...）
	 */
	private String MsgType;
	/**
	 * 消息id (64位整型)
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
