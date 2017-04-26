package com.weixin.corp.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.aes.AesException;
import com.weixin.aes.WXBizMsgCrypt;
import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.WeixinUtil;

/**
 * ������������
 * 
 */
public class CorpWeixinServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(CorpWeixinServlet.class);
	private static final long serialVersionUID = -5021188348833856475L;
	private static ConcurrentMap<String, Map<String, String>> requestCachePool = new ConcurrentHashMap<>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// ΢�ż���ǩ��
		String signature = request.getParameter("msg_signature");
		// ʱ���
		String timestamp = request.getParameter("timestamp");
		// �����
		String nonce = request.getParameter("nonce");
		// ����ַ���
		String echostr = request.getParameter("echostr");

		if (null == signature && null == timestamp && null == nonce) {

			System.out.println("signatures all null");
			return;
		}

		String sEchoStr; // ��Ҫ���ص�����
		PrintWriter out = response.getWriter();
		WXBizMsgCrypt wxcpt;
		try {
			wxcpt = new WXBizMsgCrypt(WeixinUtil.getToken(),
					WeixinUtil.getAppsecret(), WeixinUtil.getAppid());
			sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
			// ��֤URL�ɹ�����sEchoStr����
			out.print(sEchoStr);
		} catch (AesException e1) {
			e1.printStackTrace();
		}

		// ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��
		if (WeixinUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startDoPostTime = System.currentTimeMillis();
		System.out.println("doPost");
		System.out.println("start doPost Time = " + startDoPostTime);
		// ����������
		String signature = request.getParameter("msg_signature");
		System.out.println("signature: " + signature);
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");

		String requestId = signature + timestamp + nonce;

		if (requestCachePool.containsKey(requestId)) {
			return;
		}

		// ���post�ύ������
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
			wxcpt = new WXBizMsgCrypt(WeixinUtil.getToken(),
					WeixinUtil.getAppsecret(), WeixinUtil.getAppid());
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
		// ���ú���ҵ���������Ϣ��������Ϣ

		// ��һ����,����������þ�ȥ��
		Map<String, String> paramMap = new HashMap<>();
		Map<String, String> requestMap = null;
		try {
			requestMap = MessageUtil.parseXml(bais);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("ParseXml Error: " + e1.getMessage());
			return;
		}
		requestCachePool.put(requestId, requestMap);
		try {
			Thread.sleep(1 * 1000); // ģ������ʱ
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
		System.out.println("����Post��Ӧ��ʱ: " + (endDoPostTime - startDoPostTime)
				/ 1000f + "��");
		// if (endDoPostTime - startDoPostTime > 5) {
		// // System.out.println("��ʱ��Ӧ, ��Ϊģ������");
		// // MessageUtil.sendTemplateMessage(requestMap);
		// } else {
		// ��Ӧ��Ϣ
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

		requestCachePool.remove(requestId);
	}
}