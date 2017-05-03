package com.weixin.corp.utils;

import java.awt.Image;
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
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadUtil {
	
	private static Log log = LogFactory.getLog(UploadUtil.class);
	
	public static final String TEMP_URL = "D:/temp/";
	
	public static String MEDIA_TEMP_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		
	/**
	 * ģ��form������ʽ ���ϴ��ļ� �����������ʽ���ļ�д�뵽url�У�Ȼ��������������ȡurl����Ӧ
	 * @param url
	 *            �����ַ form��url��ַ
	 * @param filePath
	 *            �ļ��ڷ���������·��
	 * @return String url����Ӧ��Ϣ����ֵ
	 * @throws IOException
	 */
	public static String send(String requestUrl, String filePath) throws IOException {

		String result = null;

		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("�ļ�������");
		}
		try{
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
		httpUrlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);

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
		OutputStream out = new DataOutputStream(httpUrlConn.getOutputStream());
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
		
		
		
		
		
		---------------------
		try { // ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom()); //
			// ������SSLContext�����еõ�SSLSocketFactory����
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false); // ��������ʽ��GET/POST��
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// ����������Ҫ�ύʱ
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// ע������ʽ����ֹ��������
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

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
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.", ce);
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return jsonObject;
		
		
		
		
		String mediaId = jsonObj.getString("media_id");
		return mediaId;
	}

	public static void main(String[] args) throws IOException {
		// String a = "abc";
		// System.out.println(a);
		// String access_token =
		// WeixinUtil.getNewAccessToken("wx168dbadda799a989",
		// "0cb2de6f5e25137e0ffe03e32d05ff04").getToken();
		String filePath = "C:/Users/Administrator/Desktop/weixin_guanzhu.zzz";
		// System.out.println(System.currentTimeMillis());
		// if(filePath.endsWith("jpg") || filePath.endsWith("gif") ||
		// filePath.endsWith("png")){
		// System.out.println(System.currentTimeMillis());
		// System.out.println("is a image");
		// }
		// System.out.println(System.currentTimeMillis());
		// 1492135573844
		// 1492135573987
		// String sendUrl =
		// "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN";
		// String mediaId = UploadUtil.send(sendUrl.replace("ACCESS_TOKEN",
		// access_token),
		// filePath);
		// System.out.println(mediaId);
		boolean valid = false;
		Image image = null;
		System.out.println(System.currentTimeMillis());
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException ex) {
			valid = false;
			System.out.println("The file" + filePath
					+ "could not be opened , an error occurred.");
		}
		System.out.println(System.currentTimeMillis());
		if (image == null) {
			valid = false;
			System.out.println("The file" + filePath
					+ "could not be opened , it is not an image");
		} else {
			System.out.println(image);
		}
	}
}
