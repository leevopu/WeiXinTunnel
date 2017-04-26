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
	 * �û�������POST��
	 */
	private static String USER_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
	/**
	 * �û����£�POST��
	 */
	private static String USER_UPDATE = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	/**
	 * ���Ų�ѯ��GET��
	 */
	private static String DEPARTMENT_GET = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";
	
	/**
	 * �����û�
	 * 
	 * @param jsonUser
	 *            json��ʽ
	 * @return ״̬ 0 ��ʾ�ɹ���������ʾʧ��
	 */
	private static int createUser(String jsonUser) {
		System.out.println("jsonUser: " + jsonUser);
		int result = 0;
		// ���ýӿڴ����û�
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE, "POST",
				jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("�����û�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * �����û�
	 * 
	 * @param user
	 *            �û�ʵ��
	 * @return 0��ʾ�ɹ�������ֵ��ʾʧ��
	 */
	public static int createUser(User user) {
		return createUser(JSONObject.fromObject(user).toString());
	}
	
	/**
	 * ��ѯ�˵�
	 * 
	 * @return �˵��ṹjson�ַ���
	 */
	private static JSONObject getMenuJson() {
		JSONObject result = null;
		result = WeixinUtil.httpsRequest(MENU_GET, "GET", null);
		return result;
	}

	/**
	 * ��ѯ�˵�
	 * 
	 * @return Menu �˵�����
	 */
	public static Menu getMenu() {
		JSONObject json = getMenuJson().getJSONObject("menu");
		System.out.println(json);
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		return menu;
	}
}
