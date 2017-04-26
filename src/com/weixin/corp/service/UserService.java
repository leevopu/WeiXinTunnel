package com.weixin.corp.service;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.menu.Menu;
import com.weixin.corp.entity.user.User;
import com.weixin.corp.utils.WeixinUtil;

public class UserService {
	
	private static Log log = LogFactory.getLog(UserService.class);
	
	/**
	 * 用户创建（POST）
	 */
	private static String USER_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
	/**
	 * 用户更新（POST）
	 */
	private static String USER_UPDATE = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	/**
	 * 部门查询（GET）
	 */
	private static String DEPARTMENT_GET = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";
	
	/**
	 * 创建用户
	 * 
	 * @param jsonUser
	 *            json格式
	 * @return 状态 0 表示成功、其他表示失败
	 */
	private static int createUser(String jsonUser) {
		System.out.println("jsonUser: " + jsonUser);
		int result = 0;
		// 调用接口创建用户
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE, "POST",
				jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("创建用户失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * 创建用户
	 * 
	 * @param user
	 *            用户实例
	 * @return 0表示成功，其他值表示失败
	 */
	public static int createUser(User user) {
		return createUser(JSONObject.fromObject(user).toString());
	}
	
	/**
	 * 查询菜单
	 * 
	 * @return 菜单结构json字符串
	 */
	private static JSONObject getMenuJson() {
		JSONObject result = null;
		result = WeixinUtil.httpsRequest(MENU_GET, "GET", null);
		return result;
	}

	/**
	 * 查询菜单
	 * 
	 * @return Menu 菜单对象
	 */
	public static Menu getMenu() {
		JSONObject json = getMenuJson().getJSONObject("menu");
		System.out.println(json);
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		return menu;
	}
}
