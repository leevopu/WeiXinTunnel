package com.weixin.corp.entity.message.json;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class CorpBaseJsonMessage implements Delayed {

	private String msgtype;

	private String agentid;

	private String touser;

	private String toparty;

	private String totag;

	private int safe = 0;
	/**
	 * 给定时发送用作倒计时
	 */
	private long sendTime = 0;
	/**
	 * 是否为永久库素材消息，是则发送完后主动删除
	 */
	private boolean isPermanent = false;

	public abstract String getMediaId();

	@Override
	public int compareTo(Delayed o) {
		CorpBaseJsonMessage another = (CorpBaseJsonMessage) o;
		// o如果没有sendTime则靠前放，优先执行
		if (0 == another.getSendTime()) {
			return -1;
		}
		if (this.getSendTime() > another.getSendTime()) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(this.getSendTime() - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getToparty() {
		return toparty;
	}

	public void setToparty(String toparty) {
		this.toparty = toparty;
	}

	public String getTotag() {
		return totag;
	}

	public void setTotag(String totag) {
		this.totag = totag;
	}

	public int getSafe() {
		return safe;
	}

	public void setSafe(int safe) {
		this.safe = safe;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public boolean isPermanent() {
		return isPermanent;
	}

	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

}
