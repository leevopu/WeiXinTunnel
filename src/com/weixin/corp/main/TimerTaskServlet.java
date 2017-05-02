package com.weixin.corp.main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.service.MessageService;
import com.weixin.corp.utils.WeixinUtil;

public class TimerTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WeixinUtil.class);

	public static AccessToken accessToken = null;

	public void init() throws ServletException {
		// 获取web.xml中配置的参数
		// appid第三方用户唯一凭证
		String appid = getInitParameter("appid");
		// appsecret第三方用户唯一凭证密钥
		String appsecret = getInitParameter("appsecret");
		// aeskey第三方用户加密密钥
		String aeskey = getInitParameter("aeskey");
		// agentid第三方用户应用ID
		String agentid = getInitParameter("agentid");

		log.info("weixin api appid: " + appid);
		log.info("weixin api appsecret: " + appsecret);

		// 未配置appid、appsecret、aeskey时给出提示
		if ("".equals(appid) || "".equals(appsecret) || "".equals(aeskey)
				|| aeskey.length() != 43 || "".equals(agentid)) {
			log.error("appid, appsecret, aeskey or agentid configuration error in web.xml, please check carefully.");
			System.exit(-1);
		} else {
			// token第三方用户验证口令
			String token = getInitParameter("token");
			if (null != token) {
				WeixinUtil.init(token, appid, appsecret, aeskey, agentid);
			}
			// 启动定时获取access_token的线程，access_token每隔2小时会失效
			// new Thread(new TokenTimerTaskThread()).start();
			// 启动定时获取跑批数据，每天10点触发1次进行群发
			 MessageService.dailyGroupOnTimeTask();
		}
	}

	/**
	 * 定时获取微信access_token的线程
	 * 
	 */
	public static class TokenTimerTaskThread implements Runnable {

		public void run() {
			while (true) {
				try {
					System.out.println(WeixinUtil.getToken());
					accessToken = WeixinUtil.getNewAccessToken();
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
