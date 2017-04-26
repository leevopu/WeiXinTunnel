package com.weixin.corp.utils;

import net.sf.json.JSONObject;

import com.weixin.corp.entity.menu.Button;
import com.weixin.corp.entity.menu.Menu;

public class MenuUtil {
	public MenuUtil() {
		System.out.println("初始化...");
	}
	
	public String get() {
		Button btn1 = new Button();
		btn1.setName("我的东方");
		btn1.setKey("MY_EAST");
		btn1.setType("click");
		btn1.setUrl("www.dfzq.com.cn");
		
		Button btn2 = new Button();
		btn2.setName("系统图片");
		btn2.setKey("SYS_PHOTOS");
		btn2.setType("pic_sysphoto");
		
		Button btn3 = new Button();
		btn3.setName("手机相册发图");
		btn3.setKey("CAMERY_PHOTOS");
		btn3.setType("pic_photo_or_album");
		
		Button btn4 = new Button();
		btn4.setName("微信相册发图");
		btn4.setKey("WeiXin_PHOTOS");
		btn4.setType("pic_weixin");
		
		
		Button btn5 = new Button();
		Button[] btns = {btn2,btn3,btn4};
		btn5.setName("美丽东方");
		btn5.setSub_button(btns);
		
		Button[] btna = {btn1,btn5};
		for (int i = 0; i < btna.length; i++) {
			Button button = btna[i];
			System.out.println(button.toString());
		}
		Menu menu = new Menu();
		menu.setButton(btna);
		String str = JSONObject.fromObject(menu).toString();
		return str;
	}
}
