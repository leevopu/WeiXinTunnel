package com.weixin.corp.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.weixin.corp.utils.JdbcUtil;
import com.weixin.corp.utils.WeixinUtil;

public class TimerTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WeixinUtil.class);

	public void init() throws ServletException {
		// ����ѭ����ȡaccess_token���̣߳�access_tokenÿ��2Сʱ��ʧЧ
		new Thread(new WeixinAccessTokenTimerTaskThread()).start();

		String driverClassName = getInitParameter("driverClassName");
		String url = getInitParameter("url");
		String username = getInitParameter("username");
		String password = getInitParameter("password");
		if (!JdbcUtil.initJDBC(driverClassName, url, username, password)) {
			log.error("connect database failed");
			System.exit(-1);
		}
		// �״γ�ʼ������������ȡaccess_token��jdbc֮��
		Runnable userPoolInit = new DailyUpdateUserTimerTask();
		userPoolInit.run();

		Runnable groupMessagePoolInit = new DailyGroupMessageTimerTask();
		groupMessagePoolInit.run();
		// JDBCFactory.execRead("select 123");
		// ������ʱ��ȡ�������ݣ�ÿ��10�㴥��1�ν���Ⱥ��
		// dailyFixOnTimeTask(10, new DailyGroupMessageTimerTask());
		// // ������ʱ�����û���Ϣ��ÿ��6�㴥��1�θ��»���
		// dailyFixOnTimeTask(6, new DailyUpdateUserTimerTask());
		// ����ѭ������û��Զ��巢��ʱ�����Ϣ
		new Thread(new DelayJsonMessageTimerTaskThread()).start();
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

	/**
	 * ��ʱ��ȡ�������ݣ�ÿ�촥��1�ν���Ⱥ��
	 */
	public static class DailyGroupMessageTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				log.info("��ʼִ��ÿ�ն�ʱȺ����Ϣ");
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

	/**
	 * ��ʱ�����û���Ϣ��ÿ�촥��1�θ��»���
	 */
	public static class DailyUpdateUserTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				Map<String, Set<String>> userOaIdMap = JdbcUtil.getUserOaId();
				while (null == WeixinUtil.getAvailableAccessToken()) {
					Thread.sleep(5 * 1000);
				}
				log.info("��ʼִ��ÿ�ն�ʱ�����û�");

				// ��ȡ΢��ȫ��������Ϣ
				List<Department> departmentList = UserService.getDepartment();
				if (null == departmentList) {
					log.error("δ��ȡ��������Ϣ");
					return;
				}
				// �������Ż�ȡ�û���Ϣ
				List<User> userList = null;
				Set<String> oaIdSet = null;
				// ���oaϵͳid��userid��ӳ���ϵ
				Map<String, User> oaUserIdPool = WeixinUtil
						.getOaUserIdPool();
				for (Department department : departmentList) {
					userList = UserService.getUserByDepartment(department
							.getId());
					if (null != userList) {
						// �����û�����
						for (User user : userList) {
							oaIdSet = userOaIdMap.get(user.getUserid());
							if (null != oaIdSet) {
								for (String oaId : oaIdSet) {
									// û�д�oaid��keyʱ��ʼ��
									if (null == oaUserIdPool.get(oaId)) {
										oaUserIdPool.put(oaId, user);
									}
									// ͬһuserid�жಿ��ʱ������user�Ĳ��źż���
									oaUserIdPool.get(oaId).getDepartment()
											.add(department.getId());
								}
							}
						}
					}
				}
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
					AccessToken accessToken = WeixinUtil
							.requestNewAccessToken();
					if (null != accessToken) {
						log.info(String.format(
								"��ȡaccess_token�ɹ�����Чʱ��%d�� token:%s",
								accessToken.getExpiresIn(),
								accessToken.getToken()));
						// ����1Сʱ��ȥȡ����ֹ����
						Thread.sleep(3600 * 1000);
					} else {
						// ���access_tokenΪnull��5����ٻ�ȡ
						Thread.sleep(5 * 1000);
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
	 * ����ʱ����json��Ϣ���߳�
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
}
