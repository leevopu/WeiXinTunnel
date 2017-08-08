package com.weixin.corp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 1L;

	private static String httpsPort;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		httpsPort = filterConfig.getInitParameter("httpsPort");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		// ƴ�ӻ�ȡ�����url�Ͳ���
		String requestUrl = req.getRequestURL().toString()
				+ (null == req.getQueryString() ? "" : "?"
						+ req.getQueryString());
		// �ѳ�webservice������ķ���ת��https
		if (!requestUrl.toLowerCase().endsWith("wsdl")
				&& requestUrl.startsWith("http:")) {
			System.out.println(requestUrl);
			System.out.println("ContextPath:" + req.getContextPath());
			if(-1 == requestUrl.indexOf(":", 10)){
				System.out.println(requestUrl.replace("http:", "https:").replace(req.getContextPath(), ":" + httpsPort + req.getContextPath()));
				resp.sendRedirect(requestUrl.replace("http:", "https:").replace(req.getContextPath(), ":" + httpsPort + req.getContextPath()));
			}
			else{
				System.out.println(requestUrl.replace("http:", "https:").replace(requestUrl.substring(requestUrl.indexOf(":", 10), requestUrl.indexOf("/", requestUrl.indexOf(":", 10))), ":" + httpsPort));
				resp.sendRedirect(requestUrl.replace("http:", "https:").replace(requestUrl.substring(requestUrl.indexOf(":", 10), requestUrl.indexOf("/", requestUrl.indexOf(":", 10))), ":" + httpsPort));
			}
			return;
		}
		// ����filter����������ִ��
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
	public static void main(String[] args) {
		String requestUrl = "http://localhost:90/WeixinTunnel/uploadServlet";
		System.out.println(requestUrl.indexOf("/", requestUrl.indexOf(":", 10)));
		System.out.println(requestUrl.replace(requestUrl.substring(requestUrl.indexOf(":", 10), requestUrl.indexOf("/", requestUrl.indexOf(":", 10))), ":" + 9999));
//		requestUrl.replace("http:", "https:").replace(
//				requestUrl.substring(requestUrl.indexOf(":")), requestUrl.indexOf("/", requestUrl.indexOf(":")))
	}

}
