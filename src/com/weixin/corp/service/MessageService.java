package com.weixin.corp.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.dao.TestDao;
import com.weixin.corp.utils.ConfigUtil;

public class MessageService {
	private TestDao testDao = new TestDao();
	
	private static Log log = LogFactory.getLog(MessageService.class);

	public static void itWarnMessage(String warn) {
		System.out.println(warn);
	}

	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		try {
			MessageService messageService = new MessageService();
			messageService.groupMessage(ConfigUtil.getGroupMessageConfig(), "monthly", "yearly");
		}catch (NoSuchMethodException ne){
			ne.printStackTrace();
			log.error("MessageService.groupMessage error NoSuchMethodException: " + ne.getMessage());
			MessageService.itWarnMessage("MessageService.groupMessage error NoSuchMethodException: " + ne.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("MessageService.groupMessage error: " + e.getMessage());
			MessageService.itWarnMessage("MessageService.groupMessage error: " + e.getMessage());
		}
	}

	public static void groupMessage(Map<String, String[]> messageMapConfig,
			String... periods) throws Exception {
		for (String period : periods) {
			Iterator<Entry<String, String[]>> it = messageMapConfig.entrySet()
					.iterator();
 			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = it.next();
				if (entry.getKey().toLowerCase().contains(period)) {
					Method method = MessageService.class.getMethod(
							entry.getKey(), new Class[]{String[].class});
					method.invoke(MessageService.class, new Object[]{entry.getValue()});
				}
			}
		}
	}
}
