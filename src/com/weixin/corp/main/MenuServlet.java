package com.weixin.corp.main;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weixin.corp.service.MenuService;

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
	    	//Menu menu = new Menu();
	    	//menu = MenuService.getMenu();
	    	//String str = JSONObject.fromObject(menu).toString();
	    	
	    	String str = MenuService.get();
	    	System.out.println(str);
	    	req.setAttribute("str",str); 
	    	RequestDispatcher dispatcher=req.getRequestDispatcher("/WEB-INF/views/menuMng.jsp");
			dispatcher.forward(req, resp);
	    }
	    if("save".equals(action)){
	    	String menus = req.getParameter("result");
	    	System.out.println(menus);
//	    	String strNew = menuUtil.get();
//	    	System.out.println(strNew);
	    	req.setAttribute("str",menus); 
	    	RequestDispatcher dispatcher=req.getRequestDispatcher("/WEB-INF/views/menuMng.jsp");
	    	dispatcher.forward(req, resp);
	    }
	}
}
