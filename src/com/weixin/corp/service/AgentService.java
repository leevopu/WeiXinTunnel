package com.weixin.corp.service;

import net.sf.json.JSONObject;

import com.weixin.corp.utils.WeixinUtil;

public class AgentService {
	public static final String AGENT_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=ACCESS_TOKEN";

	public static String getAgentList(){
		JSONObject json = WeixinUtil.httpRequest(
				AGENT_LIST_URL.replace("ACCESS_TOKEN",
						WeixinUtil.getAvailableAccessToken()), "POST", null);
		return json.toString();
	}
}
