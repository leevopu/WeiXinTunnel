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
		// ��ȡweb.xml�����õĲ���
		// appid�������û�Ψһƾ֤
		String appid = getInitParameter("appid");
		// appsecret�������û�Ψһƾ֤��Կ
		String appsecret = getInitParameter("appsecret");
		// aeskey�������û�������Կ
		String aeskey = getInitParameter("aeskey");
		// agentid�������û�Ӧ��ID
		String agentid = getInitParameter("agentid");

		log.info("weixin api appid: " + appid);
		log.info("weixin api appsecret: " + appsecret);

		// δ����appid��appsecret��aeskeyʱ������ʾ
		if ("".equals(appid) || "".equals(appsecret) || "".equals(aeskey)
				|| aeskey.length() != 43 || "".equals(agentid)) {
			log.error("appid, appsecret, aeskey or agentid configuration error in web.xml, please check carefully.");
			System.exit(-1);
		} else {
			// token�������û���֤����
			String token = getInitParameter("token");
			if (null != token) {
				WeixinUtil.init(token, appid, appsecret, aeskey, agentid);
			}
			// ������ʱ��ȡaccess_token���̣߳�access_tokenÿ��2Сʱ��ʧЧ
			// new Thread(new TokenTimerTaskThread()).start();
			// ������ʱ��ȡ�������ݣ�ÿ��10�㴥��1�ν���Ⱥ��
			 MessageService.dailyGroupOnTimeTask();
		}
	}

	/**
	 * ��ʱ��ȡ΢��access_token���߳�
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
