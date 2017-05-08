package com.weixin.corp.entity.message.json;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class CorpBaseJsonMessage implements Delayed {

	private String msgtype;

	private String agentid;

	private String touser;

	private String toparty;

	private String totag;

	private int safe = 0;

	private long sendTime = 0;


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

	@Override
	public int compareTo(Delayed o) {
		CorpBaseJsonMessage another = (CorpBaseJsonMessage) o;
		// o���û��sendTime��ǰ�ţ�����ִ��
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

}
