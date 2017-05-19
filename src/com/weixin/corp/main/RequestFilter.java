package com.weixin.corp.main;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.utils.CommonUtil;


public class RequestFilter extends HttpServlet implements Filter{

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(RequestFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		/* String param=filterConfig.getInitParameter("count");//获取初始化参数
		   count=Integer.valueOf(param);//将字符串转换为int
		 */		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("该url正在过滤。。。。。");
		/**
		 * 1,doFilter的第一个参数为ServletRequest对象。此对象给过滤器提供了对进入的信息（包括 　
		 * 表单数据、cookie和HTTP请求头）的完全访问。第二个参数为ServletResponse，通常在简单的过 　
		 * 滤器中忽略此参数。最后一个参数为FilterChain，此参数用来调用servlet或JSP页。
		 */
		// 将ServletRequest转换成HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		/**
		 * 如果处理HTTP请求，并且需要访问诸如getHeader或getCookies等在ServletRequest中 　
		 * 无法得到的方法，就要把此request对象构造成HttpServletRequest
		 */
		HttpServletResponse resp = (HttpServletResponse) response;
		String suffix = req.getQueryString();//获取请求url后的字符串[参数.]
		
		if(!CommonUtil.StringisEmpty(suffix)&&suffix.endsWith("wsdl")){
			 String fullURL = req.getRequestURL().toString().toLowerCase();//将请求url中的所有转为小写
			 System.out.println(fullURL);
			 String targetURL = fullURL+"?"+suffix;
			 if(targetURL.startsWith("http")){//如果访问wsdl并且为请求方式为http，则跳转为https请求
				 resp.sendRedirect(targetURL.replace("http", "https")); 
			 }
		}
		// 加入filter链继续向下执行
		chain.doFilter(request, response);
		/**
		*调用FilterChain对象的doFilter方法。Filter接口的doFilter方法取一个FilterChain对象作 　　为它
		* 的一个参数。在调用此对象的doFilter方法时，激活下一个相关的过滤器。如果没有另
		*　一个过滤器与servlet或JSP页面关联，则servlet或JSP页面被激活。
	    */

	}

	
	@Override
	public void destroy() {
		super.destroy();
		log.info("销毁filter....");  
	}

	
}
