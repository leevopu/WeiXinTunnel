package com.weixin.corp.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.aes.AesException;
import com.weixin.aes.WXBizMsgCrypt;
import com.weixin.corp.service.AgentService;
import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.SignUtil;

/**
 * 核心请求处理类
 * 
 */
public class CorpWeixinServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(CorpWeixinServlet.class);
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
		String signature = request.getParameter("msg_signature");
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

		String sEchoStr; // 需要返回的明文
		PrintWriter out = response.getWriter();
		WXBizMsgCrypt wxcpt;
		try {
			wxcpt = new WXBizMsgCrypt("weixin",
					"nh5DkevVih58uqmiDSWqnql6hamyMl7pBK6DiwLdjgR",
					"wx522a5f82e335b883");
			sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
			// 验证URL成功，将sEchoStr返回
			out.print(sEchoStr);
		} catch (AesException e1) {
			e1.printStackTrace();
		}

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
		System.out.println("start doPost Time = " + startDoPostTime);
		// 获得请求参数
		String signature = request.getParameter("msg_signature");
		System.out.println("signature: " + signature);
		if (requestCachePool.containsKey(signature)) {
			return;
		}
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");

		// 获得post提交的数据
		BufferedReader br = new BufferedReader(new InputStreamReader(
				request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while (null != (line = br.readLine())) {
			sb.append(line);
		}
		String requestMsg = sb.toString();
		System.out.println("requestCryptMsg: " + requestMsg);
		String requestDecryptMsg = null;
		String aesErrorInfo = null;
		WXBizMsgCrypt wxcpt = null;
		try {
			wxcpt = new WXBizMsgCrypt("weixin",
					"nh5DkevVih58uqmiDSWqnql6hamyMl7pBK6DiwLdjgR",
					"wx522a5f82e335b883");
			requestDecryptMsg = wxcpt.DecryptMsg(signature, timestamp, nonce,
					requestMsg);
			System.out.println("requestDecryptMsg: " + requestDecryptMsg);
		} catch (AesException e1) {
			aesErrorInfo = e1.getMessage();
			e1.printStackTrace();
		}
		if (null == requestDecryptMsg) {
			log.error("DecryptMsg Error: " + aesErrorInfo);
			return;
		}
		log.info("requestDecryptMsg: " + requestDecryptMsg);

		ByteArrayInputStream bais = new ByteArrayInputStream(
				requestDecryptMsg.getBytes("UTF-8"));

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
			requestMap = MessageUtil.parseXml(bais);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("ParseXml Error: " + e1.getMessage());
			return;
		}
		requestCachePool.put(signature, requestMap);
		try {
			Thread.sleep(1 * 1000); // 模拟请求超时
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String responseMsg = MessageUtil.processRequest(requestMap, paramMap);
		if (null == responseMsg) {
			return;
		}

		System.out.println("responseMsg before encrypt: " + responseMsg);
		log.info("responseMsg before encrypt: " + responseMsg);

		long endDoPostTime = System.currentTimeMillis();
		System.out.println("end doPost Time = " + endDoPostTime);
		System.out.println("本次Post响应耗时: " + (endDoPostTime - startDoPostTime)
				/ 1000f + "秒");
		// if (endDoPostTime - startDoPostTime > 5) {
		// // System.out.println("超时响应, 改为模板推送");
		// // MessageUtil.sendTemplateMessage(requestMap);
		// } else {
		// 响应消息
		try {
			responseMsg = wxcpt.EncryptMsg(responseMsg, timestamp, nonce);
		} catch (AesException e1) {
			aesErrorInfo = e1.getMessage();
			e1.printStackTrace();
		}
		if (null == responseMsg) {
			log.error("EncryptMsg Error: " + aesErrorInfo);
			return;
		}
		System.out.println("responseMsg after encrypt: " + responseMsg);
		log.info("responseMsg after encrypt: " + responseMsg);

		PrintWriter out = response.getWriter();
		out.print(responseMsg);
		out.close();

		try {
			requestCachePool.remove(request.getParameter("msg_signature"));
		} catch (Exception e) {
		}
	}
}