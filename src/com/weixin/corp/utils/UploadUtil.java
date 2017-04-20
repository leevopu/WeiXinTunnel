package com.weixin.corp.utils;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

public class UploadUtil {

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
	public static String send(String url, String filePath) throws IOException {

		String result = null;

		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("�ļ�������");
		}

		/**
		 * ��һ����
		 */
		URL urlObj = new URL(url);
		// ����
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		/**
		 * ���ùؼ�ֵ
		 */
		con.setRequestMethod("POST"); // ��Post��ʽ�ύ����Ĭ��get��ʽ
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); // post��ʽ����ʹ�û���

		// ��������ͷ��Ϣ
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		// ���ñ߽�
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
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
		OutputStream out = new DataOutputStream(con.getOutputStream());
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

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// ����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			System.out.println("����POST��������쳣��" + e);
			e.printStackTrace();
			throw new IOException("���ݶ�ȡ�쳣");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		System.out.println("upload result : " + result);
		JSONObject jsonObj = JSONObject.fromObject(result);
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
