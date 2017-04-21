package com.weixin.corp.main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.utils.WeixinUtil;

public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(TokenServlet.class);

	// �������û�Ψһƾ֤
	public static String appid = "";
	// �������û�Ψһƾ֤��Կ
	public static String appsecret = "";
	// �������û�������Կ
	public static String aeskey = "";
	
	public static AccessToken accessToken = null;

	public void init() throws ServletException {
		// ��ȡweb.xml�����õĲ���
		appid = getInitParameter("appid");
		appsecret = getInitParameter("appsecret");
		aeskey = getInitParameter("aeskey");

		// δ����appid��appsecret��aeskeyʱ������ʾ
		if ("".equals(appid) || "".equals(appsecret) || "".equals(aeskey)) {
			log.error("appid, appsecret or aeskey configuration error in web.xml, please check carefully.");
		} else {
			// ������ʱ��ȡaccesstoken���߳�
			new Thread(new TokenThread()).start();
		}
	}

	/**
	 * ��ʱ��ȡ΢��accesstoken���߳�
	 * 
	 */
	public static class TokenThread implements Runnable {

		public void run() {
			while (true) {
				try {
					accessToken = WeixinUtil.getNewAccessToken(appid,
							appsecret, aeskey);
					if (null != accessToken) {
						log.info(String.format(
								"��ȡaccesstoken�ɹ�����Чʱ��%d�� token:%s",
								accessToken.getExpiresIn(),
								accessToken.getToken()));
						// ���ߵ�����ǰ200����ȥ��ȡ�µ�accessToken
						Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
					} else {
						// ���accesstokenΪnull��60����ٻ�ȡ
						Thread.sleep(60 * 1000);
					}
				} catch (InterruptedException e) {
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e1) {
						log.error(e1.getMessage());
					}
					log.error(e.getMessage());
				}
			}
		}
	}
}
