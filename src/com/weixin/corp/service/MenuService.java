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
 */
public class MenuService {

	private static Log log = LogFactory.getLog(MenuService.class);

	/**
	 * �˵�������POST��
	 */
	private static String MENU_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN&agentid=AGENTID";

	/**
	 * �˵���ѯ
	 */
	private static String MENU_GET = "https://qyapi.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN&agentid=AGENTID";

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
		JSONObject jsonObject = WeixinUtil.httpsRequest(MENU_CREATE, WeixinUtil.POST_REQUEST_METHOD,
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
	 * @return Menu �˵�����
	 */
	public static Menu getMenu() {
		// ���ýӿڲ�ѯ�˵�
		JSONObject jsonObject = WeixinUtil.httpsRequest(MENU_GET, "GET", null);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("�����˵�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
				return null;
			}
		} else {
			return null;
		}
		JSONObject menuJson = jsonObject.getJSONObject("menu");
		System.out.println(menuJson);
		Menu menu = (Menu) JSONObject.toBean(menuJson, Menu.class);
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
					String xx =	  JSONObject.fromObject(testgetMenuUrl).getString("menu");
					System.out.println(xx);
		JSONObject json = JSONObject.fromObject(testgetMenuUrl).getJSONObject(
				"menu");
		System.out.println(json);
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		System.out.println(menu);
		return menu;
	}
	
	public static String get() {
		Button btn1 = new Button();
		btn1.setName("�ҵĶ���");
		btn1.setKey("MY_EAST");
		btn1.setType("click");
		btn1.setUrl("www.dfzq.com.cn");
		
		Button btn2 = new Button();
		btn2.setName("ϵͳͼƬ");
		btn2.setKey("SYS_PHOTOS");
		btn2.setType("pic_sysphoto");
		
		Button btn3 = new Button();
		btn3.setName("�ֻ���ᷢͼ");
		btn3.setKey("CAMERY_PHOTOS");
		btn3.setType("pic_photo_or_album");
		
		Button btn4 = new Button();
		btn4.setName("΢����ᷢͼ");
		btn4.setKey("WeiXin_PHOTOS");
		btn4.setType("pic_weixin");
		
		Button btn5 = new Button();
		Button[] btns = {btn2,btn3,btn4};
		btn5.setName("��������");
		btn5.setSub_button(btns);
		
		Button[] btna = {btn1,btn5};
		Menu menu = new Menu();
		menu.setButton(btna);
		
		String str = JSONObject.fromObject(menu).toString();
		return str;
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
