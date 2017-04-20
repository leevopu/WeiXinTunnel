package com.weixin.corp.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
	
	private static Properties templateProps = new Properties();

	static {
		try {
			templateProps.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("config/template.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getTemplate(String key) {
		return templateProps.getProperty(key);
	}

}
