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

	// �������û�Ψһƾ֤
	public static String appid = "";
	// �������û�Ψһƾ֤��Կ
	public static String appsecret = "";

	public static String aeskey = "";

	public static AccessToken accessToken = null;

	public void init() throws ServletException {
		// ��ȡweb.xml�����õĲ���
		appid = getInitParameter("appid");
		appsecret = getInitParameter("appsecret");
		aeskey = getInitParameter("aeskey");

		log.info("weixin api appid: " + appid);
		log.info("weixin api appsecret: " + appsecret);

		// δ����appid��appsecret��aeskeyʱ������ʾ
		if ("".equals(appid) || "".equals(appsecret) || "".equals(aeskey)) {
			log.error("appid, appsecret or aeskey configuration error in web.xml, please check carefully.");
		} else {
			String token = getInitParameter("token");
			if (null != token) {
				WeixinUtil.setToken(token);
			}
			// ������ʱ��ȡaccess_token���߳�
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
	 * ��ʱ��ȡ΢��access_token���߳�
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
								"��ȡaccess_token�ɹ�����Чʱ��%d�� token:%s",
								accessToken.getExpiresIn(),
								accessToken.getToken()));
						// ���ߵ�����ǰ200����ȥ��ȡ�µ�accessToken
						Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
					} else {
						// ���access_tokenΪnull��60����ٻ�ȡ
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
