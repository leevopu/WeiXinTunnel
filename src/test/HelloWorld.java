package test;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.utils.WeixinUtil;

public class HelloWorld {
	public String sayHello(String name) {
		return "Hello£¬ " + name + ".";
	}

	public String saySorry(RequestCall call) {
		System.out.println(123);
		return WeixinUtil.getAvailableAccessToken() + "Sorry,"
				+ call.getMsgType() + ".";
	}

	public String getWorld() {
		return "Hello,World";
	}

}