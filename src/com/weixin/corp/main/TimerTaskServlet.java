package com.weixin.corp.main;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.weixin.corp.utils.JDBCFactory;
import com.weixin.corp.utils.WeixinUtil;

public class TimerTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WeixinUtil.class);

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
			// 启动循环获取access_token的线程，access_token每隔2小时会失效
			new Thread(new WeixinAccessTokenTimerTaskThread()).start();

			String driverClassName = getInitParameter("driverClassName");
			String url = getInitParameter("url");
			String username = getInitParameter("username");
			String password = getInitParameter("password");
			if (!JDBCFactory.initJDBC(driverClassName, url, username, password)) {
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
				System.out.println("开始执行每日定时群发消息");
				// 模拟定时取数据，真实的需用户定时调用接口
				WeixinUtil.testFetchData();
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

	public static class DailyUpdateUserTimerTask implements Runnable {
		@Override
		public void run() {
			try {
				Map<String, Set<String>> userOaIdMap = JDBCFactory
						.getUserOaId();
				while (null == WeixinUtil.getAvailableAccessToken()) {
					Thread.sleep(5 * 1000);
				}
				System.out.println("开始执行每日定时更新用户");

				// 获取微信全部部门信息
				List<Department> departmentList = UserService.getDepartment();
				if (null == departmentList) {
					log.error("未获取到部门信息");
					return;
				}
				// 遍历部门获取用户信息
				List<User> userList = null;
				Set<String> oaIdSet = null;
				for (Department department : departmentList) {
					HashMap<String, User> oaUserIdPool = WeixinUtil
							.getOaUserIdPool();
					// 是否递归获取子部门下面的成员 1/0
					String feachChild = "1";
					// 0获取全部员工，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加
					String status = "0";
					userList = UserService.getUserByDepartment(
							department.getId(), feachChild, status);
					if (null != userList) {
						// 放入用户缓存
						for (User user : userList) {
							oaIdSet = userOaIdMap.get(user.getUserid());
							if (null != oaIdSet) {
								for (String oaId : oaIdSet) {
									// 没有此oaid的key
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
				System.out.println("用户信息缓存更新完成");
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

	/**
	 * 定时发送json消息的线程
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

	public static void main(String[] args) {
		DailyGroupMessageTimerTask x = new DailyGroupMessageTimerTask();
		x.run();
	}
}
