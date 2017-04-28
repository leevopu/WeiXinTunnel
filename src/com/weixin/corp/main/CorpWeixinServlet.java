package com.weixin.corp.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;
import java.util.Enumeration;
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
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.MessageUtil;
import com.weixin.corp.utils.UploadUtil;
import com.weixin.corp.utils.WeixinUtil;

/**
 * 核心请求处理类
 * 
 */
public class CorpWeixinServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(CorpWeixinServlet.class);
	private static final long serialVersionUID = -5021188348833856475L;
	private static ConcurrentMap<String, Map<String, String>> requestCachePool = new ConcurrentHashMap<>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 微信加密签名
		String signature = request.getParameter("msg_signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");

		if (null == signature && null == timestamp && null == nonce) {

			System.out.println("signatures all null");
			return;
		}

		String sEchoStr; // 需要返回的明文
		PrintWriter out = response.getWriter();
		WXBizMsgCrypt wxcpt;
		try {
			wxcpt = new WXBizMsgCrypt(WeixinUtil.getToken(),
					WeixinUtil.getAppsecret(), WeixinUtil.getAppid());
			sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
			// 验证URL成功，将sEchoStr返回
			out.print(sEchoStr);
		} catch (AesException e1) {
			e1.printStackTrace();
		}

		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
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
		System.out.println("ContentType: " + request.getContentType());
		request.getContentLength();
		System.out.println(request.getHeader("Content-Disposition"));
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			System.out.println(headerNames.nextElement());
		}
		System.out.println("lenth: " + request.getContentLength()); //判断文件长度
		// 超过20M返回提示
		File uploadFile = parseUpload(request);
		// 判断文件是否上传成功
		System.out.println(uploadFile.exists());
		// 最好再建个UploadServlet，下面的代码是响应手机端请求的。
		// 获得请求参数
		String signature = request.getParameter("msg_signature");
		System.out.println("signature: " + signature);
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");

		String requestId = signature + timestamp + nonce;

		if (requestCachePool.containsKey(requestId)) {
			return;
		}

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
		// 调用核心业务类接收消息、处理消息

		// 不一定用,后期如果不用就去掉
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

		requestCachePool.remove(requestId);
	}

	private static byte[] readInputStream(InputStream inStream)
			throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		// 使用一个输入流从buffer里把数据读取出来
		while ((len = inStream.read(buffer)) != -1) {
			// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		// 关闭输入流
		inStream.close();
		// 把outStream里的数据写入内存
		return outStream.toByteArray();
	}

	private File parseUpload(HttpServletRequest request) throws IOException {

		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
//		final int FIELDDATA = 3; // 不需要表单上传

//		final int MXA_SEGSIZE = 1000 * 1024 * 20;// 设置每批最大的数据量，放到外面判断，否则不方便给返回值

		String contentType = request.getContentType();// 请求消息类型
		String filename = ""; // 文件名
		String boundary = ""; // 分界符
		String lastboundary = ""; // 结束符

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) { // 取得分界符和结束符
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastboundary = boundary + "--";
		}
		int state = NONE;
		// 得到数据输入流
		DataInputStream in = new DataInputStream(request.getInputStream());

		// if (totalBytes > MXA_SEGSIZE) {//每批大于20m时
		// message = "Each batch of data can not be larger than " + MXA_SEGSIZE
		// / (2000 * 1024)
		// + "M";
		// return null;
		// } 放到外面判断
		
		// 将请求消息的实体送到b变量中
		int totalBytes = request.getContentLength();
		byte[] b = new byte[totalBytes];
		in.readFully(b);
		in.close();
		String reqContent = new String(b, "UTF-8");
		BufferedReader br = new BufferedReader(new StringReader(reqContent));

		String line = null;
		while (null != (line = br.readLine())) {
			switch (state) {
			case NONE:
				if (line.startsWith(boundary)) {
					state = DATAHEADER;
				}
				break;
			case DATAHEADER:
				pos = line.indexOf("filename=");
				// if (pos == -1) { // 将表单域的名字解析出来， 不需要
				// pos = s.indexOf("name=");
				// pos += "name=".length() + 1;
				// s = s.substring(pos);
				// int l = s.length();
				// s = s.substring(0, l - 1);
				// fieldname = s;
				// state = FIELDDATA;
				// } else { // 将文件名解析出来
				if (pos != -1) {
					String temp = line;
					pos = line.indexOf("filename=");
					pos += "filename=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);// 去掉最后那个引号”
					pos = line.lastIndexOf("\\");
					line = line.substring(pos + 1);
					filename = line;
					// 从字节数组中取出文件数组
					pos = byteIndexOf(b, temp, 0);
					b = subBytes(b, pos + temp.getBytes().length + 2, b.length);// 去掉前面的部分
					int n = 0;
					/**
					 * 过滤boundary下形如 Content-Disposition: form-data; name="bin";
					 * filename="12.pdf" Content-Type: application/octet-stream
					 * Content-Transfer-Encoding: binary 的字符串
					 */
					while ((line = br.readLine()) != null) {
						if (n == 1)
							break;
						if (line.equals(""))
							n++;

						b = subBytes(b, line.getBytes().length + 2, b.length);
					}
					pos = byteIndexOf(b, boundary, 0);
					if (pos != -1)
						b = subBytes(b, 0, pos - 1);
					state = FILEDATA;
				}
				break;
			// case FIELDDATA: // 表单字段，不需要
			// s = reqbuf.readLine();
			// fieldvalue = s;
			// formfields.put(fieldname, fieldvalue);
			// state = NONE;
			// break;
			case FILEDATA:
				while ((!line.startsWith(boundary))
						&& (!line.startsWith(lastboundary))) {
					line = br.readLine();
					if (line.startsWith(boundary)) {
						state = DATAHEADER;
						break;
					}
				}
				break;
			}
		}
		File uploadFolder = new File(UploadUtil.TEMP_URL + CommonUtil.getDateStr(new Date(), "yyyy-MM-dd"));
		if(!uploadFolder.exists()){
			uploadFolder.mkdir();
		}
		System.out.println(uploadFolder.getAbsolutePath());
		System.out.println(uploadFolder.getAbsolutePath() + File.separator +  filename);
		File uploadFile = new File(uploadFolder.getAbsolutePath() + File.separator +  filename);
		// 创建输出流
		FileOutputStream outStream = new FileOutputStream(uploadFile);
		// 写入数据
		outStream.write(b, 0, b.length - 1);
		// 关闭输出流
		outStream.close();
		return uploadFile;
	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	public static int byteIndexOf(byte[] b, String s, int start) {
		return byteIndexOf(b, s.getBytes(), start);
	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	public static int byteIndexOf(byte[] b, byte[] s, int start) {
		int i;
		if (s.length == 0) {
			return 0;
		}
		int max = b.length - s.length;
		if (max < 0)
			return -1;
		if (start > max)
			return -1;
		if (start < 0)
			start = 0;
		search: for (i = start; i <= max; i++) {
			if (b[i] == s[0]) {
				int k = 1;
				while (k < s.length) {
					if (b[k + i] != s[k]) {
						continue search;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	// 用于从一个字节数组中提取一个字节数组
	public static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	// 用于从一个字节数组中提取一个字符串
	public static String subBytesString(byte[] b, int from, int end) {
		return new String(subBytes(b, from, end));
	}

}