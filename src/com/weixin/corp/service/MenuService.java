package com.weixin.corp.service;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.constant.ErrorCode;
import com.weixin.corp.entity.menu.Button;
import com.weixin.corp.entity.menu.Menu;
import com.weixin.corp.utils.WeixinUtil;

/**
 * 菜单管理
 * 
 */
public class MenuService {

	private static Log log = LogFactory.getLog(MenuService.class);

	/**
	 * 菜单创建（POST）
	 */
	private static String MENU_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN&agentid=AGENTID";

	/**
	 * 菜单查询
	 */
	private static String MENU_GET = "https://qyapi.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN&agentid=AGENTID";

	/**
	 * 创建菜单
	 * 
	 * @param jsonMenu
	 *            json格式
	 * @return 状态 0 表示成功、其他表示失败
	 */
	private static int createMenu(String jsonMenu) {
		System.out.println("jsonMenu: " + jsonMenu);
		int result = ErrorCode.SUCCESS_RETURN;
		// 调用接口创建菜单
		JSONObject jsonObject = WeixinUtil.httpsRequest(MENU_CREATE, WeixinUtil.POST_REQUEST_METHOD,
				jsonMenu);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("创建菜单失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * 创建菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @return 0表示成功，其他值表示失败
	 */
	public static int createMenuIndex(String menu) {
		//JSONObject.fromObject(menu).toString()
		return createMenu(menu);
	}

	/**
	 * 查询菜单
	 * 
	 * @return Menu 菜单对象
	 */
	public static JSONObject getMenu() {
		// 调用接口查询菜单
		JSONObject jsonObject = WeixinUtil.httpsRequest(MENU_GET, "GET", null);
		JSONObject menuJson = jsonObject.getJSONObject("menu");
		System.out.println(menuJson);
		Menu menu = (Menu) JSONObject.toBean(menuJson, Menu.class);
		return menuJson;
	}

	/**
	 * 查询菜单
	 * 
	 * @return Menu 菜单对象
	 */
//	public static Menu testGetMenu() {
//		String testgetMenuUrl = "{    \"menu\": {   \"button\":[       {           \"type\":\"click\",           \"name\":\"今日x歌曲\",           \"key\":\"V1001_TODAY_MUSIC\"       },       {           \"name\":\"菜单\",           \"sub_button\":[               {                   \"type\":\"view\",                   \"name\":\"搜索\",                   \"url\":\"http://www.soso.com/\"               },               {                   \"type\":\"click\",                   \"name\":\"赞一下我们\",                   \"key\":\"V1001_GOOD\"               }           ]      }   ]}}";
//		// String testgetMenuUrl =
//		// "{    \"menu\": {        \"button\": [            {                \"name\": \"预报\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"北京天气\",                        \"key\": \"天气北京\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"上海天气\",                        \"key\": \"天气上海\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"广州天气\",                        \"key\": \"天气广州\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"深圳天气\",                        \"key\": \"天气深圳\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"view\",                        \"name\": \"本地天气\",                        \"url\": \"http://m.hao123.com/a/tianqi\",                        \"sub_button\": [ ]                    }                ]            },            {                \"name\": \"方倍工作室\",                \"sub_button\": [                    {                        \"type\": \"click\",                        \"name\": \"公司简介\",                        \"key\": \"company\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"趣味游戏\",                        \"key\": \"游戏\",                        \"sub_button\": [ ]                    },                    {                        \"type\": \"click\",                        \"name\": \"ddddd\",                        \"key\": \"ddddd\",                        \"sub_button\": [ ]                    }                ]            }        ]    }}";
//					String xx =	  JSONObject.fromObject(testgetMenuUrl).getString("menu");
//					System.out.println(xx);
//		JSONObject json = JSONObject.fromObject(testgetMenuUrl).getJSONObject("menu");
//		System.out.println(json);
//		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
//		System.out.println(menu);
//		return menu;
//	}
	
//	public static String get() {
//		Button btn1 = new Button();
//		btn1.setName("我的东方");
//		btn1.setKey("MY_EAST");
//		btn1.setType("click");
//		btn1.setUrl("www.dfzq.com.cn");
//		
//		Button btn2 = new Button();
//		btn2.setName("系统图片");
//		btn2.setKey("SYS_PHOTOS");
//		btn2.setType("pic_sysphoto");
//		
//		Button btn3 = new Button();
//		btn3.setName("手机相册发图");
//		btn3.setKey("CAMERY_PHOTOS");
//		btn3.setType("pic_photo_or_album");
//		
//		Button btn4 = new Button();
//		btn4.setName("微信相册发图");
//		btn4.setKey("WeiXin_PHOTOS");
//		btn4.setType("pic_weixin");
//		
//		Button btn5 = new Button();
//		Button[] btns = {btn2,btn3,btn4};
//		btn5.setName("美丽东方");
//		btn5.setSub_button(btns);
//		
//		Button[] btna = {btn1,btn5};
//		Menu menu = new Menu();
//		menu.setButton(btna);
//		
//		String str = JSONObject.fromObject(menu).toString();
//		return str;
//	}

}
