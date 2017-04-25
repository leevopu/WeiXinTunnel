package com.weixin.corp.utils;

import java.lang.reflect.Array;

import net.sf.json.JSONObject;

import com.weixin.corp.entity.menu.Button;
import com.weixin.corp.entity.menu.Menu;

public class MenuUtil {
	public MenuUtil() {
		System.out.println("初始化...");
	}
	
	public String get() {
		Button btn1 = new Button();
		btn1.setName("今日歌曲");
		btn1.setKey("V1001_TODAY_MUSIC");
		btn1.setType("click");
		btn1.setUrl("123");
		
		Button btn2 = new Button();
		btn2.setName("系统拍照发图");
		btn2.setKey("rselfmenu_1_0");
		btn2.setType("pic_sysphoto");
		
		Button btn3 = new Button();
		btn3.setName("拍照或者相册发图");
		btn3.setKey("rselfmenu_1_0");
		btn3.setType("pic_photo_or_album");
		
		Button btn4 = new Button();
		btn4.setName("微信相册发图");
		btn4.setKey("rselfmenu_1_2");
		btn4.setType("pic_weixin");
		
		
		Button btn5 = new Button();
		Button[] btns = {btn2,btn3,btn4};
		btn5.setName("发图");
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
	
	public String add() {
		
		String str = "[{\"name\":\"今日歌曲\",\"key\":\"V1001_TODAY_MUSIC\",\"type\":\"click\"},"
				+ "{\"name\": \"发图\",\"sub_button\":["
				+ "{\"type\": \"pic_sysphoto\", \"name\": \"系统拍照发图\", \"key\": \"rselfmenu_1_0\","
				+ "\"sub_button\": []}, "
				+ "{\"type\": \"pic_photo_or_album\", \"name\": \"拍照或者相册发图\", "
				+ "\"key\": \"rselfmenu_1_1\", "
				+ "\"sub_button\": []}, "
				+ "{"
				+ "\"type\":\"pic_weixin\", "
				+ "\"name\": \"微信相册发图\", "
				+ "\"key\": \"rselfmenu_1_2\", "
				+ "\"sub_button\": []}]},"
				+ "{\"name\": \"你猜\",\"sub_button\":[{\"type\": \"pic_sysphoto\", \"name\": \"系统拍照发图\", \"key\": "
				+ "\"66666\",\"sub_button\": []},{\"type\": \"pic_photo_or_album\", \"name\": \"拍照或者相册发图\","
				+ "\"key\": \"88888\",\"sub_button\": []}]}"
				+ "]";
		return str;
	}
}
