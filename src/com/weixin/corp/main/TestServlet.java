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
 * ������������
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
		// ΢�ż���ǩ��
		String signature = request.getParameter("signature");
		// ʱ���
		String timestamp = request.getParameter("timestamp");
		// �����
		String nonce = request.getParameter("nonce");
		// ����ַ���
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
		// ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��
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
		// ��������Ӧ�ı��������ΪUTF-8����ֹ�������룩
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// ���ú���ҵ���������Ϣ��������Ϣ

		Map<String, String> paramMap = new HashMap<>(); // ��һ����
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
			Thread.sleep(1 * 1000); // ģ������ʱ
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String respMessage = MessageUtil.processRequest(requestMap, paramMap);
		if (null == respMessage) {
			return;
		}

		long endDoPostTime = System.currentTimeMillis();
		System.out.println("end doPost Time = " + endDoPostTime);
		System.out.println("����Post��Ӧ��ʱ: " + (endDoPostTime - startDoPostTime)
				/ 1000f + "��");
		if (endDoPostTime - startDoPostTime > 5) {
			System.out.println("��ʱ��Ӧ, ��Ϊģ������");
			MessageUtil.sendTemplateMessage(requestMap);
		} else {
			// ��Ӧ��Ϣ
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