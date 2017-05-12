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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.service.MessageService;
import com.weixin.corp.service.UploadService;
import com.weixin.corp.utils.CommonUtil;

/**
 * 上层应用主动调用 请求处理类
 * 
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5941583433272362854L;

	private static Log log = LogFactory.getLog(UploadServlet.class);

	public static final String UPLOAD_TEMP_URL = "D:/temp/";

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
		long startDoPostTime = System.currentTimeMillis();

		System.out.println("doPost");
		System.out.println("start doPost Time = " + startDoPostTime);
		RequestCall call = parseRequestCall(request);
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
		}
		response.getWriter().write(UploadService.process(call));
	}

	private RequestCall parseRequestCall(HttpServletRequest request)
			throws IOException {
		RequestCall call = new RequestCall();
		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
		final int FIELDDATA = 3; // 不需要表单上传

		// final int MXA_SEGSIZE = 1024 * 1024 * 20;//
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
			System.out.println(line);
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
		
		//校验：图文消息类型时
		if(MessageService.MPNEWS_MSG_TYPE.equals(call.getMsgType())){
			if(CommonUtil.StringisEmpty(call.getTitle())||CommonUtil.StringisEmpty(call.getText())){
				call.setErrorInfo("图文消息类型，标题、文本必填!");
				System.out.println("图文消息类型，标题、文本必填!");
				return call;
			}else if(CommonUtil.StringisEmpty(call.getDigest())&&CommonUtil.StringisEmpty(fileName)){
				call.setErrorInfo("图文消息类型，模板与素材文件必选其一");
				System.out.println("图文消息类型，模板与素材文件必选其一");
				return call;
			}
		}
		if (CommonUtil.StringisEmpty(fileName)) {
			if (CommonUtil.StringisEmpty(call.getText())) {
				call.setErrorInfo("文本内容和素材文件不能同时为空");
			} else {
				call.setMsgType(MessageService.TEXT_MSG_TYPE);
			}
			return call;
		}
		// 校验：图文消息类型时
		if (MessageService.MPNEWS_MSG_TYPE.equals(call.getMsgType())) {
			if ("" == call.getTitle() || "" == call.getDigest()) {
				call.setErrorInfo("图文类型消息，标题与模板必填");
				System.out.println("图文类型消息，标题与模板必填");
				return call;
			}
		}

		File uploadRootFolder = new File(UPLOAD_TEMP_URL);
		if (!uploadRootFolder.exists()) {
			uploadRootFolder.mkdir();
		}
		File uploadDailyFolder = new File(UPLOAD_TEMP_URL
				+ CommonUtil.getDateStr(new Date(), "yyyy-MM-dd"));
		if (!uploadDailyFolder.exists()) {
			uploadDailyFolder.mkdir();
		}
		File media = new File(uploadDailyFolder.getAbsolutePath()
				+ File.separator + fileName);
		// 创建输出流
		FileOutputStream outStream = new FileOutputStream(media);
		// 写入数据
		outStream.write(b, 0, b.length - 1);
		// 关闭输出流
		outStream.close();
		call.setMediaByte(b);
		call.setMediaName(media.getCanonicalPath());
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
			Method method = call.getClass().getMethod(
					"set" + StringUtils.capitalize(declaredField.getName()),
					declaredField.getType());
			method.invoke(call, fieldValue);
		} catch (Exception e) {
			call.setErrorInfo(e.getClass().getName() + ":" + e.getMessage());
			log.error("解析用户消息请求失败: " + e.getMessage());
		}
		return call;
	}

}
