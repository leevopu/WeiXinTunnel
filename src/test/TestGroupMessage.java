package test;

import net.sf.json.JSONObject;

import com.weixin.corp.utils.WeixinUtil;

public class TestGroupMessage {
	public String sendGroupMessage() {
		String groupUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";// ACCESS_TOKEN是获取到的access_token，根据分组id发群发消息地址
		String groupUrl1 = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=NkwaEYtrewLfkgMphjwUmnZ0l6vguB794RBhstvHUT58nLk2HMrQ3Ji9EJTD8aOyViteTyV6H1xrVLx2Myee1MUdwz0xEcw0gmlBpf6VkFoMQRfAAAQUV";// 根据openid发群发消息地址
		String group1data = "{\"filter\":{\"is_to_all\":false,\"group_id\":\"2\"},\"text\":{\"content\":\"群发消息测试\"},\"msgtype\":\"text\"}\";";
		String openid1data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"],\"msgtype\": \"text\",\"text\": {\"content\": \"测试文本消息\"}}";
		String openid2data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"voice\": {\"media_id\":\"UfMRvSiXAD5_iUS8u0Gc3JrKGWOABE9ivQbgrX6i-mVrKGBRL9KnKlioK1BxTPc3\"},\"msgtype\":\"voice\"}";
		String openid3data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"image\": {\"media_id\":\"fNUzGbYzTRui4N7-eyx9e3viP8uJuzztAvA32lIdjX4Cucj7mGN_1jpWjn7O80c8\"},\"msgtype\":\"image\"}";
		String openid4data = "{\"touser\":[\"obGXiwHTGN_4HkR2WToFj_3uaEKY\",\"obGXiwNu0z2o_RRWaODvaZctdWEM\"], \"mpnews\": {\"media_id\":\"6I8DOB-7rJsY_zdOCe6YJKJ59MwXWPb2iYBKVqb22cBHPtECYdRgiWIULfCW-hcF\"},\"msgtype\":\"mpnews\"}";
		String url = groupUrl.replace("ACCESS_TOKEN", WeixinUtil.getAvailableAccessToken());
		JSONObject json = WeixinUtil
				.httpRequest(url, "POST", group1data);
		return json.toString();
	}

	public static void main(String[] args) {
		System.out.println(new TestGroupMessage().sendGroupMessage());
	}
}
