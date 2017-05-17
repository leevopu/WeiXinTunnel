package com.weixin.corp.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
 * �ϲ�Ӧ���������� ��������
 * 
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5941583433272362854L;

	private static Log log = LogFactory.getLog(UploadServlet.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
		response.setContentType("text/html;charset=UTF-8");
		if (null != call.getErrorInfo()) {
			response.getWriter().write(call.getErrorInfo());
		} else {
			response.getWriter().write(UploadService.process(call));
		}
	}

	private RequestCall parseRequestCall(HttpServletRequest request)
			throws IOException {
		RequestCall call = new RequestCall();
		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
		final int FIELDDATA = 3;

		String contentType = request.getContentType();// ������Ϣ����
		String mediaName = ""; // �ļ���
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

		DataInputStream in = new DataInputStream(request.getInputStream());
		int totalBytes = request.getContentLength();
		byte[] mediaByte = new byte[totalBytes];
		in.readFully(mediaByte);
		in.close();
		String reqContent = new String(mediaByte, "UTF-8");
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
				if (pos == -1) {
					pos = line.indexOf("name=");
					pos += "name=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);
					fieldName = line;
					state = FIELDDATA;
				} else {
					String temp = line;
					pos = line.indexOf("filename=");
					pos += "filename=".length() + 1;
					line = line.substring(pos);
					int l = line.length();
					line = line.substring(0, l - 1);
					pos = line.lastIndexOf("\\");
					line = line.substring(pos + 1);
					mediaName = line;
					// ���ֽ�������ȡ���ļ�����
					pos = CommonUtil.byteIndexOf(mediaByte, temp, 0);
					mediaByte = CommonUtil.subBytes(mediaByte,
							pos + temp.getBytes().length + 2, mediaByte.length);// ȥ��ǰ��Ĳ���
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

						mediaByte = CommonUtil.subBytes(mediaByte,
								line.getBytes().length + 2, mediaByte.length);
					}
					pos = CommonUtil.byteIndexOf(mediaByte, boundary, 0);
					if (pos != -1)
						mediaByte = CommonUtil.subBytes(mediaByte, 0, pos - 1);
					state = FILEDATA;
				}
				break;
			case FIELDDATA:
				line = br.readLine();
				if ("text".equals(fieldName)) {
					StringBuffer textValue = new StringBuffer("");
					while (!line.startsWith(boundary)) {
						if (!"".equals(line)) {
							textValue.append(line);
							// if("text".equals(call.getMsgType())){
							textValue.append("\n");
						}
						// }
						line = br.readLine();
					}
					reflectFiledValue(call, fieldName, textValue.toString());
					state = DATAHEADER;
				} else {
					fieldValue = line;
					reflectFiledValue(call, fieldName, fieldValue);
					state = NONE;
				}
				break;
			case FILEDATA:
				while ((!line.startsWith(boundary))
						&& (!line.startsWith(lastBoundary))) {
					line = br.readLine();
					if (line.startsWith(lastBoundary)) {
						state = NONE;
						break;
					}
				}
				break;
			}
		}
		// ȥ���ı���html��ǩ֮��Ŀո�
		if (!CommonUtil.StringisEmpty(call.getText())) {
			String text = call.getText();
			String reg = ">\\s+([^\\s<]*)\\s+<";
			text = text.replaceAll(reg, ">$1<");
			call.setText(text);
		}

		// У�飺ͼ����Ϣ����ʱ
		if (MessageService.MPNEWS_MSG_TYPE.equals(call.getMsgType())) {
			if (CommonUtil.StringisEmpty(call.getTitle())
					|| CommonUtil.StringisEmpty(call.getText())
					|| CommonUtil.StringisEmpty(mediaName)) {
				call.setErrorInfo("ͼ����Ϣ���ͣ����⡢�ı����ļ��زı���!");
				System.out.println("ͼ����Ϣ���ͣ����⡢�ı����ļ��زı���!");
				return call;
			}
		}

		if (CommonUtil.StringisEmpty(mediaName)) {
			if (CommonUtil.StringisEmpty(call.getText())) {
				call.setErrorInfo("�ı����ݺ��ز��ļ�����ͬʱΪ��");
			} else {
				call.setMsgType(MessageService.TEXT_MSG_TYPE);
			}
		} else {
			call.setMediaByte(mediaByte);
			call.setMediaName(mediaName);
		}
		return call;
	}

	private RequestCall reflectFiledValue(RequestCall call, String fieldName,
			String fieldValue) {
		try {
			if ("submit".equals(fieldName)) {
				return call;
			}
			Field declaredField = RequestCall.class.getDeclaredField(fieldName);
			Method method = call.getClass().getMethod(
					"set" + StringUtils.capitalize(declaredField.getName()),
					declaredField.getType());
			method.invoke(call, fieldValue);
		} catch (Exception e) {
			log.error("RequestCall��ֵʧ��: " + e.getMessage());
		}
		return call;
	}

}
