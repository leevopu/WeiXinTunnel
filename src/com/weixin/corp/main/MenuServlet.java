package com.weixin.corp.main;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.weixin.corp.main.TimerTaskServlet.DailyUpdateUserTimerTask;
import com.weixin.corp.service.MenuService;
import com.weixin.corp.service.MessageService;

public class MenuServlet extends HttpServlet{

	private static final long serialVersionUID = -1707218526916889365L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Get...");
		doPost(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Post");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String uri = req.getRequestURI();
	    String path = uri.substring(uri.lastIndexOf("/"),uri.lastIndexOf("."));
	    String action = path.substring(1, path.length());
	    System.out.println("action:"+action);
	    
	    if("get".equals(action)){
	    	//测试数据项
	    	//String str = MenuService.get();
	    	//System.out.println(str);
			String str = "东证资管测试号-ecology15955126618,东证资管测试号-ecology15955126618|东证资管测试号-ecology15955126618";
			System.out.println(MessageService.convert(str));
	    	
	    	//调用微信接口返回数据项
	    	JSONObject menu = MenuService.getMenu();
	    	req.setAttribute("str",menu.toString()); 
	    	RequestDispatcher dispatcher=req.getRequestDispatcher("/WEB-INF/views/menuMng.jsp");
			dispatcher.forward(req, resp);
	    }
	    if("save".equals(action)){
	    	String menus = req.getParameter("result");
	    	int it = MenuService.createMenuIndex(menus);
	    	System.out.println(it);
	    	JSONObject menu = MenuService.getMenu();
	    	System.out.println(menu);
	    	req.setAttribute("str",menu.toString()); 
	    	RequestDispatcher dispatcher=req.getRequestDispatcher("/WEB-INF/views/menuMng.jsp");
	    	dispatcher.forward(req, resp);
	    }
	}
}
