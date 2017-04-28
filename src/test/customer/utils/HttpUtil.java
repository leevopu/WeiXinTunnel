package test.customer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

	// ��ʾ�������˵�url
	private static String PATH = "http://localhost/WeixinTest3/testServlet";
	private static URL url;

	static {
		try {
			url = new URL(PATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * params ��д��URL�Ĳ��� encode �ֽڱ���
	 */
	public static String sendPostMessage(Map<String, String> params,
			String encode) {

		StringBuffer stringBuffer = new StringBuffer();

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					stringBuffer
							.append(entry.getKey())
							.append("=")
							.append(URLEncoder.encode(entry.getValue(), encode))
							.append("&");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// ɾ�����һ�� & �ַ�
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
			System.out.println("-->>" + stringBuffer.toString());

			try {
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setConnectTimeout(3000);
				httpURLConnection.setDoInput(true);// �ӷ�������ȡ����
				httpURLConnection.setDoOutput(true);// �������д������

				// ����ϴ���Ϣ���ֽڴ�С������
				byte[] mydata = stringBuffer.toString().getBytes();
				// ���������������
				httpURLConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				httpURLConnection.setRequestProperty("Content-Lenth",
						String.valueOf(mydata.length));

				// ������������������������
				OutputStream outputStream = (OutputStream) httpURLConnection
						.getOutputStream();
				outputStream.write(mydata);

				// ��÷�������Ӧ�Ľ����״̬��
				int responseCode = httpURLConnection.getResponseCode();
				if (responseCode == 200) {

					// ������������ӷ������˻������
					InputStream inputStream = (InputStream) httpURLConnection
							.getInputStream();
					return (changeInputStream(inputStream, encode));

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "";
	}

	/*
	 * // �Ѵ�������InputStream��ָ�������ʽencode����ַ���String
	 */
	public static String changeInputStream(InputStream inputStream,
			String encode) {

		// ByteArrayOutputStream һ������ڴ���
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {

			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);

				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return result;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "admin");
		params.put("password", "123");
		String result = sendPostMessage(params, "utf-8");
		System.out.println("-result->>" + result);

	}

}