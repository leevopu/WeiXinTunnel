package com.weixin.corp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCFactory {

	private static Log log = LogFactory.getLog(JDBCFactory.class);

	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private static Connection CONNECTION = null;
	private static Statement ST = null;
	public static JDBCFactory JDBCFACTORY = null;

	/**
	 * 单例模式的得到对数据库操作的类
	 * 
	 * @return 返回数据访问对象的实例
	 * @throws Exception
	 */
	public static JDBCFactory getinstance() throws Exception {
		if (null == JDBCFACTORY || CONNECTION.isClosed()) {
			JDBCFACTORY = new JDBCFactory();
		}
		return JDBCFACTORY;
	}

	public synchronized static ResultSet execRead(String sql)
			throws SQLException {
		ResultSet rs = null;
		if (CONNECTION != null && !CONNECTION.isClosed() && ST != null) {
			try {
				rs = ST.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	public JDBCFactory() throws Exception {
		getConn();
	}

	public JDBCFactory(String driverClassName, String url, String username,
			String password) throws Exception {
		this.driver = driverClassName;
		this.url = url;
		this.user = username;
		this.password = getDes().decrypt(password);
		super();
	}

	private void getConn() throws Exception {
		Class.forName(driver);
		CONNECTION = DriverManager.getConnection(url, user, password);
		ST = CONNECTION.createStatement();
	}

	private DESUtil des = null;

	public DESUtil getDes() {
		if (des == null) {
			try {
				des = new DESUtil("WEIXIN");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return des;
	}

	public static Map<String, String> getOaUserId() {
		Map<String, String> oaUserId = new HashMap<String, String>();
		oaUserId.put("1", "guanzhao");
		oaUserId.put("2", "leevo_pu");
		return oaUserId;
	}

}
