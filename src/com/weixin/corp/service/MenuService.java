package com.weixin.corp.service;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.menu.Button;
import com.weixin.corp.entity.menu.Menu;
import com.weixin.corp.utils.WeixinUtil;

/**
 * �˵�����
 * 
 * @author caspar.chen
 * @version 1.1
 * 
 */
public class MenuService {

	private static Log log = LogFactory.getLog(WeixinUtil.class);

	/**
	 * �˵�������POST�� ��100����/�죩
	 */
	public static String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	/**
	 * �˵���ѯ
	 */
	public static String MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

	/**
	 * �����˵�
	 * 
	 * @param jsonMenu
	 *            json��ʽ
	 * @return ״̬ 0 ��ʾ�ɹ���������ʾʧ��
	 */
	private static Integer createMenu(String jsonMenu) {
		System.out.println("jsonMenu: " + jsonMenu);
		int result = 0;
		String token = WeixinUtil.getToken();
		if (token != null) {
			// ƴװ�����˵���url
			String url = MENU_CREATE.replace("ACCESS_TOKEN", token);
			// ���ýӿڴ����˵�
			JSONObject jsonObject = WeixinUtil.httpsRequest(url, "POST", jsonMenu);

			if (null != jsonObject) {
				if (0 != jsonObject.getInt("errcode")) {
					result = jsonObject.getInt("errcode");
					log.error("�����˵�ʧ�� errcode:" + jsonObject.getInt("errcode")
							+ "��errmsg:" + jsonObject.getString("errmsg"));
				}
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
	public static Integer createMenu(Menu menu) {
		return createMenu(JSONObject.fromObject(menu).toString());
	}


	/**
	 * ��ѯ�˵�
	 * 
	 * @return �˵��ṹjson�ַ���
	 */
	private static JSONObject getMenuJson() {
		JSONObject result = null;
		String token = WeixinUtil.getToken();
		if (token != null) {
			String url = MENU_GET.replace("ACCESS_TOKEN", token);
			result = WeixinUtil.httpsRequest(url, "GET", null);
		}
		return result;
	}

	/**
	 * ��ѯ�˵�
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
	 * @return Menu �˵�����
	 */
	public static Menu testGetMenu() {
		String testgetMenuUrl = "{    \"menu\": {   \"button\":[       {           \"type\":\"click\",           \"name\":\"���ո���\",           \"key\":\"V1001_TODAY_MUSIC\"       },       {           \"name\":\"�˵�\",           \"sub_button\":[               {                   \"type\":\"view\",                   \"name\":\"����\",                   \"url\":\"http://www.soso.com/\"               },               {                   \"type\":\"click\",                   \"name\":\"��һ������\",                   \"key\":\"V1001_GOOD\"               }           ]      }   ]}}";
//		String testgetMenuUrl = "{    \"menu\": {        \"button\": [            {                \"name\": \"Ԥ��\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"�Ϻ�����\",                        \"key\": \"�����Ϻ�\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"��������\",                        \"key\": \"��������\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"view\",                        \"name\": \"��������\",                        \"url\": \"http://m.hao123.com/a/tianqi\",                        \"sub_button\": [ ]                    }                ]            },            {                \"name\": \"����������\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"��˾���\",                        \"key\": \"company\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"Ȥζ��Ϸ\",                        \"key\": \"��Ϸ\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"ddddd\",                        \"key\": \"ddddd\",                        \"sub_button\": [ ]                    }                ]            }        ]    }}";
		JSONObject json = JSONObject.fromObject(testgetMenuUrl).getJSONObject("menu");
		System.out.println(json);
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		System.out.println(menu);
		return menu;
	}

	public static void main(String[] args) {
//		getMenu();
//		Button sb2 = new Button("΢�ͷ�", "click", "wchat_CustomerService_01", null, null);
//		Button btn1 = new Button("΢����", "click", null, null, new Button[] {sb2 });
//
//		Button sb3 = new Button("��˾���", "click", "23", null, null);
//		Button sb4 = new Button("���ʱش�", "click", "45", null, null);
//		
//		Button btn2 = new Button("���Ǵ�", "click", null, null, new Button[] {
//				sb3, sb4 });
//
//		Button sb6 = new Button("view����", "view", null, "http://m.baidu.com",
//				null);
//		
//		Button btn3 = new Button("���¶�̬", "click", null, null, new Button[] {
//				sb6 });
//
//		Menu menu = new Menu(new Button[] { btn1, btn2, btn3 });
//		createMenu(menu);
		testGetMenu();
	}
}
