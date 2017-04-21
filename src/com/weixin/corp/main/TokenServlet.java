package com.weixin.corp.main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.utils.WeixinUtil;

public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WeixinUtil.class);

	// 第三方用户唯一凭证
	public static String appid = "";
	// 第三方用户唯一凭证密钥
	public static String appsecret = "";

	public static String aeskey = "";

	public static AccessToken accessToken = null;

	public void init() throws ServletException {
		// 获取web.xml中配置的参数
		appid = getInitParameter("appid");
		appsecret = getInitParameter("appsecret");
		aeskey = getInitParameter("aeskey");

		log.info("weixin api appid: " + appid);
		log.info("weixin api appsecret: " + appsecret);

		// 未配置appid、appsecret、aeskey时给出提示
		if ("".equals(appid) || "".equals(appsecret) || "".equals(aeskey)) {
			log.error("appid, appsecret or aeskey configuration error in web.xml, please check carefully.");
		} else {
			String token = getInitParameter("token");
			if (null != token) {
				WeixinUtil.setToken(token);
			}
			// 启动定时获取access_token的线程
			new Thread(new TokenThread()).start();
		}
		// while (sendTemplateMsgTime > 0) {
		// System.out.println(321);
		// if (null != accessToken) {
		// int result =
		// SendTemplateMsg.sendTemplateMessage(accessToken.getToken(),
		// openId);
		// if(0 == result){
		// sendTemplateMsgTime--;
		// }
		// }
		//
		// }
	}

	/**
	 * 定时获取微信access_token的线程
	 * 
	 */
	public static class TokenThread implements Runnable {

		public void run() {
			while (true) {
				try {
					System.out.println(WeixinUtil.getToken());
					accessToken = WeixinUtil.getNewAccessToken(appid,
							appsecret, aeskey);
					if (null != accessToken) {
						log.info(String.format(
								"获取access_token成功，有效时长%d秒 token:%s",
								accessToken.getExpiresIn(),
								accessToken.getToken()));
						// 休眠到过期前200秒再去获取新的accessToken
						Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
					} else {
						// 如果access_token为null，60秒后再获取
						Thread.sleep(60 * 1000);
					}
				} catch (InterruptedException e) {
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e1) {
						log.error("{}", e1);
					}
					log.error("{}", e);
				}
			}
		}
	}
}
