package com.weixin.corp.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.SignUtil;

/**
 * 核心请求处理类
 * 
 */
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = -5021188348833856475L;
	private static HashMap<String, Map<String, String>> requestCachePool = new HashMap<>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet");
		System.out.println("-------------");
		System.out.println(request.getRequestURL().toString());
		System.out.println("appid: " + TokenServlet.appid);
		System.out.println("appsecret: " + TokenServlet.appsecret);
		System.out.println("accessToken: " + TokenServlet.accessToken);
		System.out.println("-------------");
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);

		if (null == signature && null == timestamp && null == nonce) {

			System.out.println("signatures all null");
			return;
		}
		PrintWriter out = response.getWriter();
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		System.out.println("doGet close");
		out = null;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startDoPostTime = System.currentTimeMillis();
		System.out.println("doPost");
		System.out.println(request.getRequestURL().toString());
		System.out.println("start doPost Time = " + startDoPostTime);
		String signature = request.getParameter("signature");
		System.out.println(signature);
		if (requestCachePool.containsKey(signature)) {
			return;
		}

		String appId = request.getParameter("appId");
		String appSecret = request.getParameter("appSecret");
		String openId = request.getParameter("openId");
		System.out.println("app : " + appId + appSecret + openId);
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 调用核心业务类接收消息、处理消息

		Map<String, String> paramMap = new HashMap<>(); // 不一定用
		// String uploadFilePath =
		// "C:/Users/Administrator/Desktop/weixin_guanzhu.png";
		// String sendUrl =
		// "http://file.api.weixin.qq.com/cgi-bin/media/upload?type=UPLOAD_TYPE&access_token=ACCESS_TOKEN";
		// Image image = ImageIO.read(new File(uploadFilePath));
		// if (null != image) {
		// sendUrl = sendUrl.replace("UPLOAD_TYPE", "image");
		// }
		// String mediaId = UploadUtil.send(
		// sendUrl.replace("ACCESS_TOKEN",
		// WeixinUtil.getAvailableAccessToken()), uploadFilePath);
		// System.out.println("mediaId : " + mediaId);
		//
		// paramMap.put("mediaId", mediaId);
		Map<String, String> requestMap = null;
		try {
			requestMap = MessageUtil.parseXml(request.getInputStream());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		requestCachePool.put(signature, requestMap);
		try {
			Thread.sleep(1 * 1000); // 模拟请求超时
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String respMessage = MessageUtil.processRequest(requestMap, paramMap);
		if (null == respMessage) {
			return;
		}

		long endDoPostTime = System.currentTimeMillis();
		System.out.println("end doPost Time = " + endDoPostTime);
		System.out.println("本次Post响应耗时: " + (endDoPostTime - startDoPostTime)
				/ 1000f + "秒");
		if (endDoPostTime - startDoPostTime > 5) {
			System.out.println("超时响应, 改为模板推送");
			MessageUtil.sendTemplateMessage(requestMap);
		} else {
			// 响应消息
			PrintWriter out = response.getWriter();
			System.out.println(0 + respMessage);
			out.print(respMessage);
			out.close();
		}
		try {
			requestCachePool.remove(request.getParameter("signature"));
		} catch (Exception e) {
		}
	}
}