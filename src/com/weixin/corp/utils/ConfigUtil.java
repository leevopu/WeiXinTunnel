package com.weixin.corp.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

	private static Properties newsProps = new Properties();

	static {
		try {
			newsProps.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("config/news.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getNews(String key) {
		return newsProps.getProperty(key);
	}

}
