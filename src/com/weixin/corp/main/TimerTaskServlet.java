package com.weixin.corp.main;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.entity.user.Department;
import com.weixin.corp.entity.user.User;
import com.weixin.corp.service.MessageService;
import com.weixin.corp.service.UserService;
import com.weixin.corp.utils.CommonUtil;
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
			// ����ѭ����ȡaccess_token���̣߳�access_tokenÿ��2Сʱ��ʧЧ
			new Thread(new WeixinAccessTokenTimerTaskThread()).start();
			// �״γ�ʼ������������ȡaccess_token֮��
			Runnable userPoolInit = new DailyUpdateUserTimerTask();
			userPoolInit.run();
			// ������ʱ��ȡ�������ݣ�ÿ��10�㴥��1�ν���Ⱥ��
			// dailyFixOnTimeTask(10, new DailyGroupMessageTimerTask());
			// // ������ʱ�����û���Ϣ��ÿ��6�㴥��1�θ��»���
			// dailyFixOnTimeTask(6, new DailyUpdateUserTimerTask());
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
				MessageService.groupMessage();
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
				while(null == WeixinUtil.getAvailableAccessToken()){
					Thread.sleep(5 * 1000);
				}
				System.out.println("��ʼִ��ÿ�ն�ʱ�����û�");

				// ��ȡ΢��ȫ��������Ϣ
				List<Department> departmentList = UserService.getDepartment();
				if (null == departmentList) {
					log.error("δ��ȡ��������Ϣ");
					return;
				}
				// �������Ż�ȡ�û���Ϣ
				List<User> userList = null;
				for (Department department : departmentList) {
					System.out.println(department.getId() + ":"
							+ department.getName());
					Map<String, HashMap<String, User>> maps = WeixinUtil
							.getUseridPool();
					// ���������ţ����뻺��
					if (null == maps.get(department.getName())) {
						maps.put(department.getName(),
								new HashMap<String, User>());
					}
					// �Ƿ�ݹ��ȡ�Ӳ�������ĳ�Ա 1/0
					String feachChild = "1";
					// 0��ȡȫ��Ա����1��ȡ�ѹ�ע��Ա�б�2��ȡ���ó�Ա�б�4��ȡδ��ע��Ա�б�status�ɵ���
					String status = "0";
					userList = UserService.getUserByDepartment(
							department.getId(), feachChild, status);
					if (null != userList) {
						// ����û�����
						maps.get(department.getName()).clear();
						HashMap<String, User> datas = maps.get(department
								.getName());
						// �����û�����
						for (User user : userList) {
							// user.getDepartment()��һ��object����
							if (!CommonUtil.StringisEmpty(user.getMobile())) {
								// WeixinUtil.getUseridPool().get(department.getName()).put(user.getMobile(),
								// user);
								datas.put(user.getMobile(), user);
							}
						}
					}
				}
				System.out.println("�û���Ϣ����������");
				log.info("�û���Ϣ����������");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ʱ��ȡ΢��access_token���߳�
	 * 
	 */
	public static class WeixinAccessTokenTimerTaskThread implements Runnable {

		public void run() {
			while (true) {
				try {
					AccessToken accessToken = WeixinUtil.requestNewAccessToken();
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
					MessageService.sendMessage(jsonMessage);
					if (jsonMessage.isPermanent()) {
						// ɾ�����ÿ��ز���Ϣ
						MessageService.deletePermanentMedia(jsonMessage
								.getMediaId());
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static void main(String[] args) {
		DailyUpdateUserTimerTask x = new DailyUpdateUserTimerTask();
		x.run();
	}
}
