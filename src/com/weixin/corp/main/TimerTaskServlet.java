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
		// 启动循环获取access_token的线程，access_token每隔2小时会失效
		new Thread(new WeixinAccessTokenTimerTaskThread()).start();

		String driverClassName = getInitParameter("driverClassName");
		String url = getInitParameter("url");
		String username = getInitParameter("username");
		String password = getInitParameter("password");
		if (!JdbcUtil.initJDBC(driverClassName, url, username, password)) {
			log.error("connect database failed");
			System.exit(-1);
		}
		// 首次初始化缓存必须放在取access_token和jdbc之后
		Runnable userPoolInit = new DailyUpdateUserTimerTask();
		userPoolInit.run();

		Runnable groupMessagePoolInit = new DailyGroupMessageTimerTask();
		groupMessagePoolInit.run();
		// JDBCFactory.execRead("select 123");
		// 启动定时获取跑批数据，每天10点触发1次进行群发
		// dailyFixOnTimeTask(10, new DailyGroupMessageTimerTask());
		// // 启动定时更新用户信息，每天6点触发1次更新缓存
		// dailyFixOnTimeTask(6, new DailyUpdateUserTimerTask());
		// 启动循环监控用户自定义发送时间的消息
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
	 * 定时获取跑批数据，每天触发1次进行群发
	 */
	public static class DailyGroupMessageTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				log.info("开始执行每日定时群发消息");
				// 群发消息
				MessageService.groupMessage();
				// 未成功发送的记录会保留，可以进一步处理
				// 之前失败的消息通知管理员
				// MessageUtil.warnFailureMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 定时更新用户信息，每天触发1次更新缓存
	 */
	public static class DailyUpdateUserTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				Map<String, Set<String>> userOaIdMap = JdbcUtil.getUserOaId();
				while (null == WeixinUtil.getAvailableAccessToken()) {
					Thread.sleep(5 * 1000);
				}
				log.info("开始执行每日定时更新用户");

				// 获取微信全部部门信息
				List<Department> departmentList = UserService.getDepartment();
				if (null == departmentList) {
					log.error("未获取到部门信息");
					return;
				}
				// 遍历部门获取用户信息
				List<User> userList = null;
				Set<String> oaIdSet = null;
				// 获得oa系统id和userid的映射关系
				Map<String, User> oaUserIdPool = WeixinUtil
						.getOaUserIdPool();
				for (Department department : departmentList) {
					userList = UserService.getUserByDepartment(department
							.getId());
					if (null != userList) {
						// 放入用户缓存
						for (User user : userList) {
							oaIdSet = userOaIdMap.get(user.getUserid());
							if (null != oaIdSet) {
								for (String oaId : oaIdSet) {
									// 没有此oaid的key时初始化
									if (null == oaUserIdPool.get(oaId)) {
										oaUserIdPool.put(oaId, user);
									}
									// 同一userid有多部门时，设置user的部门号集合
									oaUserIdPool.get(oaId).getDepartment()
											.add(department.getId());
								}
							}
						}
					}
				}
				log.info("用户信息缓存更新完成");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 定时获取微信access_token的线程
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
								"获取access_token成功，有效时长%d秒 token:%s",
								accessToken.getExpiresIn(),
								accessToken.getToken()));
						// 休眠1小时再去取，防止过期
						Thread.sleep(3600 * 1000);
					} else {
						// 如果access_token为null，5秒后再获取
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
	 * 倒计时发送json消息的线程
	 * 
	 */
	public static class DelayJsonMessageTimerTaskThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					CorpBaseJsonMessage jsonMessage = WeixinUtil
							.getDelayJsonMessageQueue().take();
					// 定时触发响应，不论是否成功
					MessageService.sendMessage(jsonMessage);
					if (jsonMessage.isPermanent()) {
						// 删除永久库素材消息
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
