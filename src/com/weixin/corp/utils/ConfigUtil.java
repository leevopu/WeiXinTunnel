package com.weixin.corp.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigUtil {
	
	private static Log log = LogFactory.getLog(ConfigUtil.class);
	
	private static Map<String, String[]> messagePartyIdsConfig = null;

	static {
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("config/message.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取message.properties失败");
		}
		messagePartyIdsConfig = new HashMap<String, String[]>();
		Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			System.out.println("key   :" + key);
			System.out.println("value :" + value);
			System.out.println("---------------");
			try{
				Long.parseLong(value.replaceAll(",", ""));
			}catch(NumberFormatException e){
				e.printStackTrace();
				log.error("message.properties配置不正确，等号右边请把部门的数字ID用逗号隔开");
				continue;
			}
			messagePartyIdsConfig.put(key, value.split(","));
		}
	}

	public static Map<String, String[]> getGroupMessageConfig() {
		return messagePartyIdsConfig;
	}

	public static void main(String[] args) {
		System.out.println(Long.parseLong("xxx"));
	}
}
