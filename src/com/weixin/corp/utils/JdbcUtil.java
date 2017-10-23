package com.weixin.corp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JdbcUtil {

	private static Log log = LogFactory.getLog(JdbcUtil.class);

	private static String driver = null;
	private static String url = null;
	private static String user = null;
	private static String password = null;
	private static Connection CONNECTION = null;
	private static Statement ST = null;

	public synchronized static ResultSet execRead(String sql) {
		ResultSet rs = null;
		try {
			if (CONNECTION != null && !CONNECTION.isClosed() && ST != null) {
				rs = ST.executeQuery(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static boolean initJDBC(String driverClassName, String url,
			String username, String password) {
		try {
			JdbcUtil.driver = driverClassName;
			JdbcUtil.url = url;
			JdbcUtil.user = username;
			JdbcUtil.password = getDes().decrypt(password);
//			JdbcUtil.password = password;
			getConn();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("≥ı ºªØjdbc ß∞‹");
			return false;
		}
		return true;
	}

	private static void getConn() throws Exception {
		Class.forName(driver);
		CONNECTION = DriverManager.getConnection(url, user, password);
		ST = CONNECTION.createStatement();
	}

	private static DesUtil des = null;

	public static DesUtil getDes() {
		if (des == null) {
			try {
				des = new DesUtil("WEIXIN");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return des;
	}

	public static Map<String, Set<String>> getUserOaId() {
		Map<String, Set<String>> userOaIdMap = new HashMap<String, Set<String>>();
		ResultSet result = execRead("select t.id, t.loginid from dc_down.oa_hrmresource t where t.loginid is not null");
		try {
			while (result.next()) {
				if (null == userOaIdMap.get(result.getString(2))) {
					Set<String> oaIdSet = new HashSet<String>();
					userOaIdMap.put(result.getString(2), oaIdSet);
				}
				userOaIdMap.get(result.getString(2)).add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
//		Set<String> oaIdSet = new HashSet<String>();
//		oaIdSet.add("1");
//		Set<String> oaIdSet2 = new HashSet<String>();
//		oaIdSet2.add("3");
//		Set<String> oaIdSet4 = new HashSet<String>();
//		oaIdSet4.add("4");
//		Set<String> oaIdSet5 = new HashSet<String>();
//		oaIdSet5.add("5");
//		userOaIdMap.put("guanzhao", oaIdSet);
//		userOaIdMap.put("leevo_pu", oaIdSet2);
//		userOaIdMap.put("sunliqing", oaIdSet4);
//		userOaIdMap.put("yangziling", oaIdSet5);
		return userOaIdMap;
	}
	
	public static void main(String[] args) throws Exception {
		String url = "jdbc:oracle:thin:@192.168.56.92:1521:hsfkjx";
		boolean flag = initJDBC("oracle.jdbc.driver.OracleDriver", url, "dc_eiif", "123qweasdzxc");
//		getUserOaId();
	}
}
