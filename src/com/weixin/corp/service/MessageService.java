package com.weixin.corp.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.dao.TestDao;
import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.WeixinUtil;

public class MessageService {
	private TestDao testDao = new TestDao();
	

	private static Log log = LogFactory.getLog(MessageService.class);

	public static void itWarnMessage(String warn) {
		System.out.println(warn);
	}

	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
			Date d = null;
			Object o = new String("2011-01-01");
			if(o instanceof String){
				try {
					d = new SimpleDateFormat("yyyy-MM-dd").parse((String)o);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				d = (Date)o;
			}
			System.out.println(d.compareTo(new Date()));
			
			
		
		/*
		try {
			MessageService messageService = new MessageService();
			messageService.groupMessage(ConfigUtil.getGroupMessageConfig(),
					"monthly", "yearly");
		} catch (NoSuchMethodException ne) {
			ne.printStackTrace();
			log.error("MessageService.groupMessage error NoSuchMethodException: "
					+ ne.getMessage());
			MessageService
					.itWarnMessage("MessageService.groupMessage error NoSuchMethodException: "
							+ ne.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("MessageService.groupMessage error: " + e.getMessage());
			MessageService.itWarnMessage("MessageService.groupMessage error: "
					+ e.getMessage());
		}
	*/}

	public static void groupMessage(Map<String, String[]> messageMapConfig,
			String... periods) throws Exception {
		for (String period : periods) {
			Iterator<Entry<String, String[]>> it = messageMapConfig.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = it.next();
				if (entry.getKey().toLowerCase().contains(period)) {
					Method method = MessageService.class.getMethod(
							entry.getKey(), new Class[] { String[].class });
					method.invoke(MessageService.class,
							new Object[] { entry.getValue() });
				}
			}
		}
	}

	public static void dailyGroupOnTimeTask() {

		long oneDay = 24 * 60 * 60 * 1000;
		//"10:00:15"
		long initDelay = getTimeMillis("14:20:00") - System.currentTimeMillis();
		System.out.println("initDelay = " + initDelay);
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;

		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

		exec.scheduleAtFixedRate(new dailyGroupTimerTask(), initDelay, oneDay,
				TimeUnit.MILLISECONDS);
	}

	private static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println(dayFormat.format(new Date()) + " "
					+ time);
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " "
					+ time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

class dailyGroupTimerTask implements Runnable {
	@Override
	public void run() {
		try{
		System.out.println("开始执行每日定时群发消息");
		// 模拟定时取数据，真实环境需连接数据库
		WeixinUtil.testFetchData();
		// 群发消息
		MessageUtil.groupMessage();
		
		// 之前失败的消息通知管理员
		MessageUtil.warnFailureMessage();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
