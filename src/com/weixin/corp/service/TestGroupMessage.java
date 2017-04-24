package com.weixin.corp.service;

import net.sf.json.JSONObject;

import com.weixin.corp.utils.WeixinUtil;

public class TestGroupMessage {
	public String sendGroupMessage() {
		String groupUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";// ACCESS_TOKEN是获取到的access_token，根据分组id发群发消息地址
		String groupUrl1 = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=NkwaEYtrewLfkgMphjwUmnZ0l6vguB794RBhstvHUT58nLk2HMrQ3Ji9EJTD8aOyViteTyV6H1xrVLx2Myee1MUdwz0xEcw0gmlBpf6VkFoMQRfAAAQUV";// 根据openid发群发消息地址
		String group1data = "{\"filter\":{\"is_to_all\":false,\"group_id\":\"0\"},\"text\":{\"content\":\"群发消息测试\"},\"msgtype\":\"text\"}\";";
		String openid1data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"],\"msgtype\": \"text\",\"text\": {\"content\": \"测试文本消息\"}}";
		String openid2data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"voice\": {\"media_id\":\"UfMRvSiXAD5_iUS8u0Gc3JrKGWOABE9ivQbgrX6i-mVrKGBRL9KnKlioK1BxTPc3\"},\"msgtype\":\"voice\"}";
		String openid3data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"image\": {\"media_id\":\"fNUzGbYzTRui4N7-eyx9e3viP8uJuzztAvA32lIdjX4Cucj7mGN_1jpWjn7O80c8\"},\"msgtype\":\"image\"}";
		String openid4data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"mpnews\": {\"media_id\":\"6I8DOB-7rJsY_zdOCe6YJKJ59MwXWPb2iYBKVqb22cBHPtECYdRgiWIULfCW-hcF\"},\"msgtype\":\"mpnews\"}";
		String url = groupUrl.replace("ACCESS_TOKEN",
				WeixinUtil.getAvailableAccessToken());
		JSONObject json = WeixinUtil.httpsRequest(url, "POST", group1data);
		return json.toString();
	}
	//https://api.weixin.qq.com/cgi-bin/user/get?access_token=a1JPzpy2ytCY3iHk0palRP4fgB8i-eMpWQeyWRFMi6pCw2xJTyY4mRgiQEuIVpOPTTEao26qkEZXTs0hnJsNeO9mRU91INnl4Ykt5QH35Q44qMSDP4B0Tn6oFwuhbntwJWEbAGAANV&next_openid=oU8JkwOg2e3-m-C_UIkkSJrVmmno
	public String createGroup() {
		String createGroup = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
		String createGroupValue = "{\"group\":{\"name\":\"test3\"}}";
		String getGroup = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=ACCESS_TOKEN";
		JSONObject json = WeixinUtil.httpsRequest(
				getGroup.replace("ACCESS_TOKEN",
						WeixinUtil.getAvailableAccessToken()), "POST", null);
		return json.toString();
	}
	
	public String getGroup() {
		String createGroup = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
		String createGroupValue = "{\"group\":{\"name\":\"test3\"}}";
		String getGroup = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=ACCESS_TOKEN";
		JSONObject json = WeixinUtil.httpsRequest(
				getGroup.replace("ACCESS_TOKEN",
						WeixinUtil.getAvailableAccessToken()), "POST", null);
		return json.toString();
	}

	public static void main(String[] args) {
		System.out.println(new TestGroupMessage().sendGroupMessage());
	}
}
