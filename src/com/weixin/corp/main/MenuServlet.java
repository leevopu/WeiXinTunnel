package com.weixin.corp.main;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MenuServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1707218526916889365L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Get");
		String str = "[{\"name\":\"今日歌曲\",\"key\":\"V1001_TODAY_MUSIC\",\"type\":\"click\"},"
				+ "{\"name\": \"发图\",\"sub_button\":["
				+ "{\"type\": \"pic_sysphoto\", \"name\": \"系统拍照发图\", \"key\": \"rselfmenu_1_0\","
				+ "\"sub_button\": []}, "
				+ "{\"type\": \"pic_photo_or_album\", \"name\": \"拍照或者相册发图\", "
				+ "\"key\": \"rselfmenu_1_1\", "
				+ "\"sub_button\": []}, "
				+ "{"
				+ "\"typ\":\"pic_weixin\", "
				+ "\"name\": \"微信相册发图\", "
				+ "\"key\": \"rselfmenu_1_2\", "
				+ "\"sub_button\": []}]},"
				+ "{\"name\": \"你猜\",\"sub_button\":[{\"type\": \"pic_sysphoto\", \"name\": \"系统拍照发图\", \"key\": "
				+ "\"66666\",\"sub_button\": []},{\"type\": \"pic_photo_or_album\", \"name\": \"拍照或者相册发图\","
				+ "\"key\": \"88888\",\"sub_button\": []}]}"
				+ "]";
		req.setAttribute("str",str); 
		RequestDispatcher dispatcher=req.getRequestDispatcher("/WEB-INF/views/menuMng.jsp");
		dispatcher.forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Post");
		doGet(req,resp);
	}
}
