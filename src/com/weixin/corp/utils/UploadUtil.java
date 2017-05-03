package com.weixin.corp.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;

public class UploadUtil {

	private static Log log = LogFactory.getLog(UploadUtil.class);

	public static final String TEMP_URL = "D:/temp/";

	public static String MEDIA_TEMP_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	public static final String TEXT_MSG_TYPE = "text";
	public static final String IMAGE_MSG_TYPE = "image";
	public static final String MEDIA_MSG_TYPE = "media";
	public static final String FILE_MSG_TYPE = "file";

	/**
	 * ģ���ϲ�Ӧ�õ�������
	 */
	public String testUploadToServer(String requestUrl, RequestCall call) {
		String result = null;
		String msgType = call.getMsgType();
		if (TEXT_MSG_TYPE != msgType && IMAGE_MSG_TYPE != msgType
				&& MEDIA_MSG_TYPE != msgType && FILE_MSG_TYPE != msgType) {
			return "���͵���Ϣ���Ͳ���ȷ��ֻ����text,image,media��file";
		}
		URL url = null;
		HttpURLConnection con = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// ������SSLContext�����еõ�SSLSocketFactory����
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // ��Post��ʽ�ύ����Ĭ��get��ʽ
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post��ʽ����ʹ�û���
			// ��������ͷ��Ϣ
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			// ���ñ߽�
			String BOUNDARY = "---------------------------"
					+ System.currentTimeMillis();
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			final String newLine = "\r\n";

			// ��������
			OutputStream out = new DataOutputStream(con.getOutputStream());

			// ����������Ϣ
			// ��һ���֣�
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);

			// �����Ϣ����
			sb.append("Content-Disposition: form-data;name=\"msgType\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(msgType);
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// ��ӷ�����
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);
			sb.append("Content-Disposition: form-data;name=\"fromUser\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(call.getFromUser());
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// ��ӽ�����
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);
			sb.append("Content-Disposition: form-data;name=\"toUser\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(call.getToUser());
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// ���ʱ��
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);
			sb.append("Content-Disposition: form-data;name=\"sendTime\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(call.getSendTime());
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// �����Ϣ���ݣ��ı����ļ���
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);
			if (TEXT_MSG_TYPE == msgType) {
				sb.append("Content-Disposition: form-data;name=\"text\"");
				sb.append(newLine);
				sb.append(newLine);
				sb.append(call.getText());
				sb.append(newLine);
				sb.append("Content-Type:application/octet-stream");
				sb.append(newLine);
				sb.append(newLine);
				out.write(sb.toString().getBytes("utf-8"));
			} else {
				File media = call.getMedia();
				if (!media.exists()) {
					return "ѡ�����Ϣ�ļ�������";
				}
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ media.getName() + "\"");
				sb.append(newLine);
				sb.append("Content-Type:application/octet-stream");
				sb.append(newLine);
				sb.append(newLine);
				out.write(sb.toString().getBytes("utf-8"));
				// �ļ����Ĳ���
				// ���ļ������ļ��ķ�ʽ ���뵽url��
				DataInputStream in = new DataInputStream(new FileInputStream(
						media));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
				out.write(newLine.getBytes());
				// ��β����
				byte[] foot = ("--" + BOUNDARY + "--").getBytes("utf-8");
				// ����������ݷָ���
				out.write(foot);
				out.write(newLine.getBytes());
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("������Ϣ�ӿ�����ʧ��");
			return "������Ϣ�ӿ�����ʧ��";
		}

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// ����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			log.error("��ȡPOST������Ӧ�����쳣��" + e);
			e.printStackTrace();
			return "��ȡPOST������Ӧ�����쳣��";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

//	public static String uploadToWeixin(String requestUrl, File file) {
//		if (!file.exists() || !file.isFile()) {
//			log.error("is not a file:{}");
//		}
//		StringBuffer buffer = new StringBuffer();
//		try {
//			TrustManager[] tm = { new MyX509TrustManager() };
//			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//			sslContext.init(null, tm, new java.security.SecureRandom()); //
//			// ������SSLContext�����еõ�SSLSocketFactory����
//			SSLSocketFactory ssf = sslContext.getSocketFactory();
//
//			URL url = new URL(requestUrl);
//			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
//					.openConnection();
//			httpUrlConn.setSSLSocketFactory(ssf);
//
//			/**
//			 * ���ùؼ�ֵ
//			 */
//			httpUrlConn.setRequestMethod("POST"); // ��Post��ʽ�ύ����Ĭ��get��ʽ
//			httpUrlConn.setDoInput(true);
//			httpUrlConn.setDoOutput(true);
//			httpUrlConn.setUseCaches(false); // post��ʽ����ʹ�û���
//
//			// ��������ͷ��Ϣ
//			httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
//			httpUrlConn.setRequestProperty("Charset", "UTF-8");
//
//			// ���ñ߽�
//			String BOUNDARY = "----------" + System.currentTimeMillis();
//			httpUrlConn.setRequestProperty("Content-Type",
//					"multipart/form-data; boundary=" + BOUNDARY);
//
//			// ����������Ϣ
//
//			// ��һ���֣�
//			StringBuilder sb = new StringBuilder();
//			sb.append("--"); // �����������
//			sb.append(BOUNDARY);
//			sb.append("\r\n");
//			sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
//					+ file.getName() + "\"\r\n");
//			sb.append("Content-Type:application/octet-stream\r\n\r\n");
//
//			byte[] head = sb.toString().getBytes("utf-8");
//
//			// ��������
//			OutputStream out = new DataOutputStream(
//					httpUrlConn.getOutputStream());
//			// �����ͷ
//			out.write(head);
//
//			// �ļ����Ĳ���
//			// ���ļ������ļ��ķ�ʽ ���뵽url��
//			DataInputStream in = new DataInputStream(new FileInputStream(file));
//			int bytes = 0;
//			byte[] bufferOut = new byte[1024];
//			while ((bytes = in.read(bufferOut)) != -1) {
//				out.write(bufferOut, 0, bytes);
//			}
//			in.close();
//
//			// ��β����
//			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// ����������ݷָ���
//
//			out.write(foot);
//
//			out.flush();
//			out.close();
//
//			// ---------------------
//
//			// �����ص�������ת�����ַ���
//			InputStream inputStream = httpUrlConn.getInputStream();
//			InputStreamReader inputStreamReader = new InputStreamReader(
//					inputStream, "utf-8");
//			BufferedReader bufferedReader = new BufferedReader(
//					inputStreamReader);
//
//			String str = null;
//			while ((str = bufferedReader.readLine()) != null) {
//				buffer.append(str);
//			}
//			bufferedReader.close();
//			inputStreamReader.close(); // �ͷ���Դ
//			inputStream.close();
//			inputStream = null;
//			httpUrlConn.disconnect();
//			return JSONObject.fromObject(buffer.toString()).getString(
//					"media_id");
//		} catch (ConnectException ce) {
//			log.error("Weixin server connection timed out.", ce);
//		} catch (Exception e) {
//			log.error("https request error:{}", e);
//		}
//		return null;
//	}

	/**
	 * ģ��form������ʽ ���ϴ��ļ� �����������ʽ���ļ�д�뵽url�У�Ȼ��������������ȡurl����Ӧ
	 * 
	 * @param url
	 *            �����ַ form��url��ַ
	 * @param filePath
	 *            �ļ��ڷ���������·��
	 * @return String url����Ӧ��Ϣ����ֵ
	 * @throws IOException
	 */
	public static String send(String requestUrl, String filePath)
			throws IOException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("�ļ�������");
		}
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom()); //
			// ������SSLContext�����еõ�SSLSocketFactory����
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			/**
			 * ���ùؼ�ֵ
			 */
			httpUrlConn.setRequestMethod("POST"); // ��Post��ʽ�ύ����Ĭ��get��ʽ
			httpUrlConn.setDoInput(true);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setUseCaches(false); // post��ʽ����ʹ�û���

			// ��������ͷ��Ϣ
			httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConn.setRequestProperty("Charset", "UTF-8");

			// ���ñ߽�
			String BOUNDARY = "----------" + System.currentTimeMillis();
			httpUrlConn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			// ����������Ϣ

			// ��һ���֣�
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
					+ file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] head = sb.toString().getBytes("utf-8");

			// ��������
			OutputStream out = new DataOutputStream(
					httpUrlConn.getOutputStream());
			// �����ͷ
			out.write(head);

			// �ļ����Ĳ���
			// ���ļ������ļ��ķ�ʽ ���뵽url��
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();

			// ��β����
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// ����������ݷָ���

			out.write(foot);

			out.flush();
			out.close();

			// ---------------------

			// �����ص�������ת�����ַ���
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close(); // �ͷ���Դ
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
			return jsonObject.getString("media_id");
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.", ce);
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		JSONObject.fromObject("abc");
	}
}
