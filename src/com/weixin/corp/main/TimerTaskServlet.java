package com.weixin.corp.main;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.service.UserService;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.WeixinUtil;

public class TimerTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WeixinUtil.class);

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
			
			
			
			
			
			
			//??
			Runnable x = new DailyGroupMessageTimerTask();
			x.run();
			// ������ʱ��ȡ�������ݣ�ÿ��10�㴥��1�ν���Ⱥ��
			dailyFixOnTimeTask(10, new DailyGroupMessageTimerTask());
			// ������ʱ�����û���Ϣ��ÿ��6�㴥��1�θ��»���
			// dailyUpdateUserOnTimeTask();
			// ����ѭ����ȡaccess_token���̣߳�access_tokenÿ��2Сʱ��ʧЧ
			new Thread(new TokenTimerTaskThread()).start();
			// ����ѭ������û��Զ��巢��ʱ�����Ϣ
			new Thread(new DelayJsonMessageTimerTaskThread()).start();
		}
	}

	/**
	 * 
	 * @param fixHour
	 *            0-23
	 * @param runnable
	 *            task
	 */
	public static void dailyFixOnTimeTask(int fixHour, Runnable runnable) {
		long oneDay = 24 * 60 * 60 * 1000;
		Calendar fixTime = Calendar.getInstance();
		fixTime.setTime(new Date());
		fixTime.set(Calendar.HOUR_OF_DAY, fixHour);
		fixTime.set(Calendar.MINUTE, 0);
		fixTime.set(Calendar.SECOND, 0);
		long initDelay = fixTime.getTimeInMillis() - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(runnable, initDelay, oneDay,
				TimeUnit.MILLISECONDS);
	}

	public static class DailyGroupMessageTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				System.out.println("��ʼִ��ÿ�ն�ʱȺ����Ϣ");
				// ģ�ⶨʱȡ���ݣ���ʵ�������������ݿ� groupMessagePool
				WeixinUtil.testFetchData();
				// Ⱥ����Ϣ
				MessageUtil.groupMessage();
				// δ�ɹ����͵ļ�¼�ᱣ�������Խ�һ������
				// ֮ǰʧ�ܵ���Ϣ֪ͨ����Ա
				// MessageUtil.warnFailureMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class DailyUpdateUserTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				System.out.println("��ʼִ��ÿ�ն�ʱ�����û�");
				// ����û�����<��������<�ֻ���,userid>>
				WeixinUtil.getUseridPool().clear();
				// ��ȡ΢��ȫ��������Ϣ

				// �������Ż�ȡ�û���Ϣ

				// �����û�����
			} catch (Exception e) {
				e.printStackTrace();
			}
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
					AccessToken accessToken = WeixinUtil.getNewAccessToken();
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

	/**
	 * ��ʱ����json��Ϣ���߳�
	 * 
	 */
	public static class DelayJsonMessageTimerTaskThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					CorpBaseJsonMessage jsonMessage = WeixinUtil
							.getDelayJsonMessageQueue().take();
					// ��ʱ������Ӧ�������Ƿ�ɹ�
					MessageUtil.sendMessage(jsonMessage);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static void main(String[] args) {
		String a = "1970-01-01 8:00:00";
		Date x = CommonUtil.getStrDate(a, "yyyy-MM-dd HH:mm:ss");
		System.out.println(x.getTime());
		Calendar fixTime = Calendar.getInstance();
		fixTime.setTime(x);
		// fixTime.set(Calendar.HOUR_OF_DAY, 8);
		// fixTime.set(Calendar.MINUTE, 0);
		// fixTime.set(Calendar.SECOND, 0);
		System.out.println(fixTime.getTimeInMillis());
		System.out.println(fixTime.getTime().getTime());

	}
}
