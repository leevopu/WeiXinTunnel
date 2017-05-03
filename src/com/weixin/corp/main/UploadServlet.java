package com.weixin.corp.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.UploadUtil;

/**
 * �ϲ�Ӧ���������� ��������
 * 
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 5941583433272362854L;
	private static Log log = LogFactory.getLog(UploadServlet.class);

	private static final String TEXT_MSG_TYPE = "text";
	private static final String IMAGE_MSG_TYPE = "image";
	private static final String MEDIA_MSG_TYPE = "media";
	private static final String FILE_MSG_TYPE = "file";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet..............................");
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/WEB-INF/views/FileMng.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// final int MXA_SEGSIZE = 1024 * 1024 * 20;// ����ÿ������������
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
		System.out.println("lenth: " + request.getContentLength()); // �ж��ļ�����
		// ����20M������ʾ
		RequestCall call = parseRequestCall(request);

		// ����ʧ��
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
			return;
		}
		
		Map<String, Object> uploadMap = parseUpload(request);
		// �ϴ����ļ�
		Iterator<Entry<String, Object>> it = uploadMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) it
					.next();
			System.out.println("====================================================");
			System.out.println(entry.getKey()+" , "+entry.getValue());
			System.out.println("====================================================");
		}
		
		RequestCall call = parseRequestCall(request);

		// ����ʧ��
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
			return;
		}
		// �ж��Ƿ��ʽ����Ҫ���Ƿ���ȱʧ���ֶ�
		if (null == call.getFromUser() || null == call.getToUser()
				|| null == call.getMsgType()
				|| (null == call.getText() && null == call.getMedia())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("ȱ�ٱ�Ҫ����Ϣ���飬fromUser:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("��toUser:");
			missFieldValue.append(call.getToUser());
			missFieldValue.append("��msgType:");
			missFieldValue.append(call.getMsgType());
			missFieldValue.append("��text:");
			missFieldValue.append(call.getText());
			missFieldValue.append("��media:");
			if (null != call.getMedia()) {
				missFieldValue.append(call.getMedia().getName());
			}
			response.getWriter().write(missFieldValue.toString());
			return;
		}
		
		
		// �ж��Ƿ��ʽ����Ҫ���Ƿ���ȱʧ���ֶ�
		if (null == call.getFromUser() || null == call.getToUser()
				|| null == call.getMsgType()
				|| (null == call.getText() && null == call.getMedia())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("ȱ�ٱ�Ҫ����Ϣ���飬fromUser:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("��toUser:");
			missFieldValue.append(call.getToUser());
			missFieldValue.append("��msgType:");
			missFieldValue.append(call.getMsgType());
			missFieldValue.append("��text:");
			missFieldValue.append(call.getText());
			missFieldValue.append("��media:");
			if (null != call.getMedia()) {
				missFieldValue.append(call.getMedia().getName());
			}
			response.getWriter().write(missFieldValue.toString());
			return;
		}
		String msgType = call.getMsgType();
		// ��������ı������ϴ���ʱ�زģ���ȡ�ز�id
		if (TEXT_MSG_TYPE != (msgType)) {
			// �ж��趨��ʱ���Ƿ񳬹�3�죬��Ϊ��ʱ�ز�ֻ�ܱ���3�죬�������3�죬���ϴ������ز�
			if(null != call.getSendTime()){
				CommonUtil.shiftDay(call.getSendTime(), "yyyy-MM-dd", -3)
				if()
			}
		}
		File media = call.getMedia();
		// �ж��ļ��Ƿ��ϴ��ɹ�
		System.out.println(media.exists());
		// ����ٽ���UploadServlet������Ĵ�������Ӧ�ֻ�������ġ�
		long contentLength = request.getContentLength();
		System.out.println("lenth: " + contentLength); // �ж��ļ�����
		// ����20M������ʾ
		String size = CommonUtil.convertFileSize(contentLength);
		System.out.println("====================" + size);
		// =================================================================
		// �ж��ļ���С
		// =================================================================
		String x = StringUtils.substringBefore(size, " ");
		System.out.println(x);
		System.out.println(+Float.parseFloat(x) > 10);
		/*
		 * if(Float.parseFloat(x)>10){
		 * System.out.println("�ļ���С����20M�������²�����������"); return ; }
		 */

	}

	private RequestCall parseRequestCall(HttpServletRequest request)
			throws IOException {
		RequestCall call = new RequestCall();
		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
		final int FIELDDATA = 3; // ����Ҫ���ϴ�

		// final int MXA_SEGSIZE = 1000 * 1024 * 20;//
		// ����ÿ���������������ŵ������жϣ����򲻷��������ֵ

		String contentType = request.getContentType();// ������Ϣ����
		String fileName = ""; // �ļ���
		String boundary = ""; // �ֽ��
		String lastBoundary = ""; // ������
		String fieldName = "";
		String fieldValue = "";

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) { // ȡ�÷ֽ���ͽ�����
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastBoundary = boundary + "--";
		}
		int state = NONE;
		// �õ�����������
		DataInputStream in = new DataInputStream(request.getInputStream());

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
				if (pos == -1) { // ����������ֽ��������� ����Ҫ
					pos = line.indexOf("name=");
					pos += "name=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);
					fieldName = line;
					state = FIELDDATA;
				} else { // ���ļ�����������
					// if (pos != -1) {
					String temp = line;
					pos = line.indexOf("filename=");
					pos += "filename=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);// ȥ������Ǹ����š�
					pos = line.lastIndexOf("\\");
					line = line.substring(pos + 1);
					fileName = line;
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
			case FIELDDATA: // ���ֶ�
				line = br.readLine();
				fieldValue = line;
				reflectFiledValue(call, fieldName, fieldValue);
				state = NONE;
				break;
			case FILEDATA:
				while ((!line.startsWith(boundary))
						&& (!line.startsWith(lastBoundary))) {
					line = br.readLine();
					// if (line.startsWith(boundary)) {
					// state = DATAHEADER;
					// break;
					// }
					if (line.startsWith(lastBoundary)) {
						state = NONE;
						break;
					}
				}
				break;
			}
		}
		File uploadFolder = new File(UploadUtil.TEMP_URL
				+ CommonUtil.getDateStr(new Date(), "yyyy-MM-dd"));
		if (!uploadFolder.exists()) {
			uploadFolder.mkdir();
		}
		File media = new File(uploadFolder.getAbsolutePath() + File.separator
				+ fileName);
		// ���������
		FileOutputStream outStream = new FileOutputStream(media);
		// д������
		outStream.write(b, 0, b.length - 1);
		// �ر������
		outStream.close();
		call.setMedia(media);
		return call;
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

	private RequestCall reflectFiledValue(RequestCall call, String fieldName,
			String fieldValue) {
		try {
			Field declaredField = RequestCall.class.getDeclaredField(fieldName);
			System.out.println(declaredField.getName());
			Method method = call.getClass().getMethod(
					"set" + StringUtils.capitalize("sendTime"),
					declaredField.getType());
			method.invoke(call, fieldName);
		} catch (Exception e) {
			e.printStackTrace();
			call.setErrorInfo(e.getMessage());
			log.error("�����û���Ϣ����ʧ��: " + e.getMessage());
		}
		return call;
	}

}