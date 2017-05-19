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
		/* String param=filterConfig.getInitParameter("count");//��ȡ��ʼ������
		   count=Integer.valueOf(param);//���ַ���ת��Ϊint
		 */		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("��url���ڹ��ˡ���������");
		/**
		 * 1,doFilter�ĵ�һ������ΪServletRequest���󡣴˶�����������ṩ�˶Խ������Ϣ������ ��
		 * �����ݡ�cookie��HTTP����ͷ������ȫ���ʡ��ڶ�������ΪServletResponse��ͨ���ڼ򵥵Ĺ� ��
		 * �����к��Դ˲��������һ������ΪFilterChain���˲�����������servlet��JSPҳ��
		 */
		// ��ServletRequestת����HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		/**
		 * �������HTTP���󣬲�����Ҫ��������getHeader��getCookies����ServletRequest�� ��
		 * �޷��õ��ķ�������Ҫ�Ѵ�request�������HttpServletRequest
		 */
		HttpServletResponse resp = (HttpServletResponse) response;
		String suffix = req.getQueryString();//��ȡ����url����ַ���[����.]
		
		if(!CommonUtil.StringisEmpty(suffix)&&suffix.endsWith("wsdl")){
			 String fullURL = req.getRequestURL().toString().toLowerCase();//������url�е�����תΪСд
			 System.out.println(fullURL);
			 String targetURL = fullURL+"?"+suffix;
			 if(targetURL.startsWith("http")){//�������wsdl����Ϊ����ʽΪhttp������תΪhttps����
				 resp.sendRedirect(targetURL.replace("http", "https")); 
			 }
		}
		// ����filter����������ִ��
		chain.doFilter(request, response);
		/**
		*����FilterChain�����doFilter������Filter�ӿڵ�doFilter����ȡһ��FilterChain������ ����Ϊ��
		* ��һ���������ڵ��ô˶����doFilter����ʱ��������һ����صĹ����������û����
		*��һ����������servlet��JSPҳ���������servlet��JSPҳ�汻���
	    */

	}

	
	@Override
	public void destroy() {
		super.destroy();
		log.info("����filter....");  
	}

	
}
