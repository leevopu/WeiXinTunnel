package com.weixin.corp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
	
	public static String getDateStr(Date date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
}
