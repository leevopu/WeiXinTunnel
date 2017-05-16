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

public class JDBCFactory {

	private static Log log = LogFactory.getLog(JDBCFactory.class);

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
			JDBCFactory.driver = driverClassName;
			JDBCFactory.url = url;
			JDBCFactory.user = username;
			JDBCFactory.password = getDes().decrypt(password);
//			getConn();
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

	private static DESUtil des = null;

	public static DESUtil getDes() {
		if (des == null) {
			try {
				des = new DESUtil("WEIXIN");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return des;
	}

	public static Map<String, Set<String>> getUserOaId() {
		Map<String, Set<String>> userOaIdMap = new HashMap<String, Set<String>>();
		// ResultSet result = execRead("select oaid, userid from xxxx");
		// try {
		// while (result.next()) {
		// if (null == userOaIdMap.get(result.getString(2))) {
		// Set<String> oaIdSet = new HashSet<String>();
		// userOaIdMap.put(result.getString(2), oaIdSet);
		// }
		// userOaIdMap.get(result.getString(2)).add(result.getString(1));
		// }
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		Set<String> oaIdSet = new HashSet<String>();
		oaIdSet.add("1");
		oaIdSet.add("2");
		Set<String> oaIdSet2 = new HashSet<String>();
		oaIdSet2.add("3");
		userOaIdMap.put("guanzhao", oaIdSet);
		userOaIdMap.put("leevo_pu", oaIdSet2);
		return userOaIdMap;
	}
}
