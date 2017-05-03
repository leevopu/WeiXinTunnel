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
 * 上层应用主动调用 请求处理类
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
		// final int MXA_SEGSIZE = 1024 * 1024 * 20;// 设置每批最大的数据量
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
		System.out.println("lenth: " + request.getContentLength()); // 判断文件长度
		// 超过20M返回提示
		RequestCall call = parseRequestCall(request);

		// 解析失败
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
			return;
		}
		
		Map<String, Object> uploadMap = parseUpload(request);
		// 上传了文件
		Iterator<Entry<String, Object>> it = uploadMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) it
					.next();
			System.out.println("====================================================");
			System.out.println(entry.getKey()+" , "+entry.getValue());
			System.out.println("====================================================");
		}
		
		RequestCall call = parseRequestCall(request);

		// 解析失败
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
			return;
		}
		// 判断是否格式符合要求，是否有缺失的字段
		if (null == call.getFromUser() || null == call.getToUser()
				|| null == call.getMsgType()
				|| (null == call.getText() && null == call.getMedia())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("缺少必要的信息请检查，fromUser:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("，toUser:");
			missFieldValue.append(call.getToUser());
			missFieldValue.append("，msgType:");
			missFieldValue.append(call.getMsgType());
			missFieldValue.append("，text:");
			missFieldValue.append(call.getText());
			missFieldValue.append("，media:");
			if (null != call.getMedia()) {
				missFieldValue.append(call.getMedia().getName());
			}
			response.getWriter().write(missFieldValue.toString());
			return;
		}
		
		
		// 判断是否格式符合要求，是否有缺失的字段
		if (null == call.getFromUser() || null == call.getToUser()
				|| null == call.getMsgType()
				|| (null == call.getText() && null == call.getMedia())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("缺少必要的信息请检查，fromUser:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("，toUser:");
			missFieldValue.append(call.getToUser());
			missFieldValue.append("，msgType:");
			missFieldValue.append(call.getMsgType());
			missFieldValue.append("，text:");
			missFieldValue.append(call.getText());
			missFieldValue.append("，media:");
			if (null != call.getMedia()) {
				missFieldValue.append(call.getMedia().getName());
			}
			response.getWriter().write(missFieldValue.toString());
			return;
		}
		String msgType = call.getMsgType();
		// 如果不是文本，先上传临时素材，获取素材id
		if (TEXT_MSG_TYPE != (msgType)) {
			// 判断设定的时间是否超过3天，因为临时素材只能保留3天，如果超过3天，则上传永久素材
			if(null != call.getSendTime()){
				CommonUtil.shiftDay(call.getSendTime(), "yyyy-MM-dd", -3)
				if()
			}
		}
		File media = call.getMedia();
		// 判断文件是否上传成功
		System.out.println(media.exists());
		// 最好再建个UploadServlet，下面的代码是响应手机端请求的。
		long contentLength = request.getContentLength();
		System.out.println("lenth: " + contentLength); // 判断文件长度
		// 超过20M返回提示
		String size = CommonUtil.convertFileSize(contentLength);
		System.out.println("====================" + size);
		// =================================================================
		// 判断文件大小
		// =================================================================
		String x = StringUtils.substringBefore(size, " ");
		System.out.println(x);
		System.out.println(+Float.parseFloat(x) > 10);
		/*
		 * if(Float.parseFloat(x)>10){
		 * System.out.println("文件大小超过20M，请重新操作！！！！"); return ; }
		 */

	}

	private RequestCall parseRequestCall(HttpServletRequest request)
			throws IOException {
		RequestCall call = new RequestCall();
		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
		final int FIELDDATA = 3; // 不需要表单上传

		// final int MXA_SEGSIZE = 1000 * 1024 * 20;//
		// 设置每批最大的数据量，放到外面判断，否则不方便给返回值

		String contentType = request.getContentType();// 请求消息类型
		String fileName = ""; // 文件名
		String boundary = ""; // 分界符
		String lastBoundary = ""; // 结束符
		String fieldName = "";
		String fieldValue = "";

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) { // 取得分界符和结束符
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastBoundary = boundary + "--";
		}
		int state = NONE;
		// 得到数据输入流
		DataInputStream in = new DataInputStream(request.getInputStream());

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
				if (pos == -1) { // 将表单域的名字解析出来， 不需要
					pos = line.indexOf("name=");
					pos += "name=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);
					fieldName = line;
					state = FIELDDATA;
				} else { // 将文件名解析出来
					// if (pos != -1) {
					String temp = line;
					pos = line.indexOf("filename=");
					pos += "filename=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);// 去掉最后那个引号”
					pos = line.lastIndexOf("\\");
					line = line.substring(pos + 1);
					fileName = line;
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
			case FIELDDATA: // 表单字段
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
		// 创建输出流
		FileOutputStream outStream = new FileOutputStream(media);
		// 写入数据
		outStream.write(b, 0, b.length - 1);
		// 关闭输出流
		outStream.close();
		call.setMedia(media);
		return call;
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
			log.error("解析用户消息请求失败: " + e.getMessage());
		}
		return call;
	}

}