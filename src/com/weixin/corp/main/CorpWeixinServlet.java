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
		System.out.println("ContentType: " + request.getContentType());
		request.getContentLength();
		System.out.println(request.getHeader("Content-Disposition"));
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			System.out.println(headerNames.nextElement());
		}
		System.out.println("lenth: " + request.getContentLength()); //�ж��ļ�����
		// ����20M������ʾ
		File uploadFile = parseUpload(request);
		// �ж��ļ��Ƿ��ϴ��ɹ�
		System.out.println(uploadFile.exists());
		// ����ٽ���UploadServlet������Ĵ�������Ӧ�ֻ�������ġ�
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

	private static byte[] readInputStream(InputStream inStream)
			throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// ����һ��Buffer�ַ���
		byte[] buffer = new byte[1024];
		// ÿ�ζ�ȡ���ַ������ȣ����Ϊ-1������ȫ����ȡ���
		int len = 0;
		// ʹ��һ����������buffer������ݶ�ȡ����
		while ((len = inStream.read(buffer)) != -1) {
			// ���������buffer��д�����ݣ��м����������ĸ�λ�ÿ�ʼ����len�����ȡ�ĳ���
			outStream.write(buffer, 0, len);
		}
		// �ر�������
		inStream.close();
		// ��outStream�������д���ڴ�
		return outStream.toByteArray();
	}

	private File parseUpload(HttpServletRequest request) throws IOException {

		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
//		final int FIELDDATA = 3; // ����Ҫ���ϴ�

//		final int MXA_SEGSIZE = 1000 * 1024 * 20;// ����ÿ���������������ŵ������жϣ����򲻷��������ֵ

		String contentType = request.getContentType();// ������Ϣ����
		String filename = ""; // �ļ���
		String boundary = ""; // �ֽ��
		String lastboundary = ""; // ������

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) { // ȡ�÷ֽ���ͽ�����
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastboundary = boundary + "--";
		}
		int state = NONE;
		// �õ�����������
		DataInputStream in = new DataInputStream(request.getInputStream());

		// if (totalBytes > MXA_SEGSIZE) {//ÿ������20mʱ
		// message = "Each batch of data can not be larger than " + MXA_SEGSIZE
		// / (2000 * 1024)
		// + "M";
		// return null;
		// } �ŵ������ж�
		
		// ��������Ϣ��ʵ���͵�b������
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
				// if (pos == -1) { // ����������ֽ��������� ����Ҫ
				// pos = s.indexOf("name=");
				// pos += "name=".length() + 1;
				// s = s.substring(pos);
				// int l = s.length();
				// s = s.substring(0, l - 1);
				// fieldname = s;
				// state = FIELDDATA;
				// } else { // ���ļ�����������
				if (pos != -1) {
					String temp = line;
					pos = line.indexOf("filename=");
					pos += "filename=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);// ȥ������Ǹ����š�
					pos = line.lastIndexOf("\\");
					line = line.substring(pos + 1);
					filename = line;
					// ���ֽ�������ȡ���ļ�����
					pos = byteIndexOf(b, temp, 0);
					b = subBytes(b, pos + temp.getBytes().length + 2, b.length);// ȥ��ǰ��Ĳ���
					int n = 0;
					/**
					 * ����boundary������ Content-Disposition: form-data; name="bin";
					 * filename="12.pdf" Content-Type: application/octet-stream
					 * Content-Transfer-Encoding: binary ���ַ���
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
			// case FIELDDATA: // ���ֶΣ�����Ҫ
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
		// ���������
		FileOutputStream outStream = new FileOutputStream(uploadFile);
		// д������
		outStream.write(b, 0, b.length - 1);
		// �ر������
		outStream.close();
		return uploadFile;
	}

	// �ֽ������е�INDEXOF��������STRING���е�INDEXOF����
	public static int byteIndexOf(byte[] b, String s, int start) {
		return byteIndexOf(b, s.getBytes(), start);
	}

	// �ֽ������е�INDEXOF��������STRING���е�INDEXOF����
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

	// ���ڴ�һ���ֽ���������ȡһ���ֽ�����
	public static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	// ���ڴ�һ���ֽ���������ȡһ���ַ���
	public static String subBytesString(byte[] b, int from, int end) {
		return new String(subBytes(b, from, end));
	}

}