package com.weixin.corp.service;

import net.sf.json.JSONObject;

import com.weixin.corp.utils.WeixinUtil;

public class AgentService {
	public static final String AGENT_GET = "https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=ACCESS_TOKEN";

	public static String getAgentList(){
		JSONObject json = WeixinUtil.httpsRequest(
				AGENT_GET, WeixinUtil.GET_REQUEST_METHOD, null);
		return json.toString();
	}
}
