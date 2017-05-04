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
			
			
			
			
			
			
			//??
			Runnable x = new DailyGroupMessageTimerTask();
			x.run();
			// 启动定时获取跑批数据，每天10点触发1次进行群发
			dailyFixOnTimeTask(10, new DailyGroupMessageTimerTask());
			// 启动定时更新用户信息，每天6点触发1次更新缓存
			// dailyUpdateUserOnTimeTask();
			// 启动循环获取access_token的线程，access_token每隔2小时会失效
			new Thread(new TokenTimerTaskThread()).start();
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
				// 模拟定时取数据，真实环境需连接数据库 groupMessagePool
				WeixinUtil.testFetchData();
				// 群发消息
				MessageUtil.groupMessage();
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
				System.out.println("开始执行每日定时更新用户");
				// 清空用户缓存<部门名称<手机号,userid>>
				WeixinUtil.getUseridPool().clear();
				// 获取微信全部部门信息

				// 遍历部门获取用户信息

				// 放入用户缓存
			} catch (Exception e) {
				e.printStackTrace();
			}
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
					AccessToken accessToken = WeixinUtil.getNewAccessToken();
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
