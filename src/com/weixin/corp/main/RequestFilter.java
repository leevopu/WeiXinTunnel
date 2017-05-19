package com.weixin.corp.main;

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
		System.out.println(httpsPort);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		// 拼接获取请求的url和参数
		String requestUrl = req.getRequestURL().toString()
				+ (null == req.getQueryString() ? "" : "?"
						+ req.getQueryString());
		// 把除webservice请求外的访问转成https
		if (!requestUrl.toLowerCase().endsWith("wsdl")
				&& requestUrl.startsWith("http:")) {
			resp.sendRedirect(requestUrl.replace("http:", "https:").replace(
					req.getContextPath(),
					":" + httpsPort + req.getContextPath()));
			return;
		}
		// 加入filter链继续向下执行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
