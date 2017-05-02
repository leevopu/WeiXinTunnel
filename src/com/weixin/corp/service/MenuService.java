package com.weixin.corp.service;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.menu.Menu;
import com.weixin.corp.utils.WeixinUtil;

/**
 * �˵�����
 * 
 */
public class MenuService {

	private static Log log = LogFactory.getLog(MenuService.class);

	/**
	 * �˵�������POST��
	 */
	private static String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	/**
	 * �˵���ѯ
	 */
	private static String MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

	/**
	 * �����˵�
	 * 
	 * @param jsonMenu
	 *            json��ʽ
	 * @return ״̬ 0 ��ʾ�ɹ���������ʾʧ��
	 */
	private static int createMenu(String jsonMenu) {
		System.out.println("jsonMenu: " + jsonMenu);
		int result = 0;
		// ���ýӿڴ����˵�
		JSONObject jsonObject = WeixinUtil.httpsRequest(MENU_CREATE, "POST",
				jsonMenu);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("�����˵�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * �����˵�
	 * 
	 * @param menu
	 *            �˵�ʵ��
	 * @return 0��ʾ�ɹ�������ֵ��ʾʧ��
	 */
	public static int createMenu(Menu menu) {
		return createMenu(JSONObject.fromObject(menu).toString());
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

	/**
	 * ��ѯ�˵�
	 * 
	 * @return Menu �˵�����
	 */
	public static Menu testGetMenu() {
		String testgetMenuUrl = "{    \"menu\": {   \"button\":[       {           \"type\":\"click\",           \"name\":\"����x����\",           \"key\":\"V1001_TODAY_MUSIC\"       },       {           \"name\":\"�˵�\",           \"sub_button\":[               {                   \"type\":\"view\",                   \"name\":\"����\",                   \"url\":\"http://www.soso.com/\"               },               {                   \"type\":\"click\",                   \"name\":\"��һ������\",                   \"key\":\"V1001_GOOD\"               }           ]      }   ]}}";
		// String testgetMenuUrl =
		// "{    \"menu\": {        \"button\": [            {                \"name\": \"Ԥ��\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"�Ϻ�����\",                        \"key\": \"�����Ϻ�\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"view\",                        \"name\": \"��������\",                        \"url\": \"http://m.hao123.com/a/tianqi\",                        \"sub_button\": [ ]                    }                ]            },            {                \"name\": \"����������\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"��˾���\",                        \"key\": \"company\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"Ȥζ��Ϸ\",                        \"key\": \"��Ϸ\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"ddddd\",                        \"key\": \"ddddd\",                        \"sub_button\": [ ]                    }                ]            }        ]    }}";
		JSONObject json = JSONObject.fromObject(testgetMenuUrl).getJSONObject(
				"menu");
		System.out.println(json);
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		System.out.println(menu);
		return menu;
	}

	public static void main(String[] args) {
		// getMenu();
		// Button sb2 = new Button("΢�ͷ�", "click", "wchat_CustomerService_01",
		// null, null);
		// Button btn1 = new Button("΢����", "click", null, null, new Button[]
		// {sb2 });
		//
		// Button sb3 = new Button("��˾���", "click", "23", null, null);
		// Button sb4 = new Button("���ʱش�", "click", "45", null, null);
		//
		// Button btn2 = new Button("���Ǵ�", "click", null, null, new Button[] {
		// sb3, sb4 });
		//
		// Button sb6 = new Button("view����", "view", null, "http://m.baidu.com",
		// null);
		//
		// Button btn3 = new Button("���¶�̬", "click", null, null, new Button[] {
		// sb6 });
		//
		// Menu menu = new Menu(new Button[] { btn1, btn2, btn3 });
		// createMenu(menu);
		testGetMenu();
	}
}
