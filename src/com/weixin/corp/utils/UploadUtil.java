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
	 * 模拟上层应用调用请求
	 */
	public String testUploadToServer(String requestUrl, RequestCall call) {
		String result = null;
		String msgType = call.getMsgType();
		if (TEXT_MSG_TYPE != msgType && IMAGE_MSG_TYPE != msgType
				&& MEDIA_MSG_TYPE != msgType && FILE_MSG_TYPE != msgType) {
			return "发送的消息类型不正确，只允许text,image,media和file";
		}
		URL url = null;
		HttpURLConnection con = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String BOUNDARY = "---------------------------"
					+ System.currentTimeMillis();
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			final String newLine = "\r\n";

			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());

			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append(newLine);

			// 添加消息类型
			sb.append("Content-Disposition: form-data;name=\"msgType\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(msgType);
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// 添加发送人
			sb.append("--"); // 必须多两道线
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

			// 添加接收人
			sb.append("--"); // 必须多两道线
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

			// 添加时间
			sb.append("--"); // 必须多两道线
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

			// 添加消息内容（文本或文件）
			sb.append("--"); // 必须多两道线
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
					return "选择的消息文件不存在";
				}
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ media.getName() + "\"");
				sb.append(newLine);
				sb.append("Content-Type:application/octet-stream");
				sb.append(newLine);
				sb.append(newLine);
				out.write(sb.toString().getBytes("utf-8"));
				// 文件正文部分
				// 把文件已流文件的方式 推入到url中
				DataInputStream in = new DataInputStream(new FileInputStream(
						media));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
				out.write(newLine.getBytes());
				// 结尾部分
				byte[] foot = ("--" + BOUNDARY + "--").getBytes("utf-8");
				// 定义最后数据分隔线
				out.write(foot);
				out.write(newLine.getBytes());
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用消息接口请求失败");
			return "调用消息接口请求失败";
		}

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
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
			log.error("获取POST请求响应出现异常！" + e);
			e.printStackTrace();
			return "获取POST请求响应出现异常！";
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
//			// 从上述SSLContext对象中得到SSLSocketFactory对象
//			SSLSocketFactory ssf = sslContext.getSocketFactory();
//
//			URL url = new URL(requestUrl);
//			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
//					.openConnection();
//			httpUrlConn.setSSLSocketFactory(ssf);
//
//			/**
//			 * 设置关键值
//			 */
//			httpUrlConn.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
//			httpUrlConn.setDoInput(true);
//			httpUrlConn.setDoOutput(true);
//			httpUrlConn.setUseCaches(false); // post方式不能使用缓存
//
//			// 设置请求头信息
//			httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
//			httpUrlConn.setRequestProperty("Charset", "UTF-8");
//
//			// 设置边界
//			String BOUNDARY = "----------" + System.currentTimeMillis();
//			httpUrlConn.setRequestProperty("Content-Type",
//					"multipart/form-data; boundary=" + BOUNDARY);
//
//			// 请求正文信息
//
//			// 第一部分：
//			StringBuilder sb = new StringBuilder();
//			sb.append("--"); // 必须多两道线
//			sb.append(BOUNDARY);
//			sb.append("\r\n");
//			sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
//					+ file.getName() + "\"\r\n");
//			sb.append("Content-Type:application/octet-stream\r\n\r\n");
//
//			byte[] head = sb.toString().getBytes("utf-8");
//
//			// 获得输出流
//			OutputStream out = new DataOutputStream(
//					httpUrlConn.getOutputStream());
//			// 输出表头
//			out.write(head);
//
//			// 文件正文部分
//			// 把文件已流文件的方式 推入到url中
//			DataInputStream in = new DataInputStream(new FileInputStream(file));
//			int bytes = 0;
//			byte[] bufferOut = new byte[1024];
//			while ((bytes = in.read(bufferOut)) != -1) {
//				out.write(bufferOut, 0, bytes);
//			}
//			in.close();
//
//			// 结尾部分
//			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
//
//			out.write(foot);
//
//			out.flush();
//			out.close();
//
//			// ---------------------
//
//			// 将返回的输入流转换成字符串
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
//			inputStreamReader.close(); // 释放资源
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
	 * 模拟form表单的形式 ，上传文件 以输出流的形式把文件写入到url中，然后用输入流来获取url的响应
	 * 
	 * @param url
	 *            请求地址 form表单url地址
	 * @param filePath
	 *            文件在服务器保存路径
	 * @return String url的响应信息返回值
	 * @throws IOException
	 */
	public static String send(String requestUrl, String filePath)
			throws IOException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("文件不存在");
		}
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom()); //
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			/**
			 * 设置关键值
			 */
			httpUrlConn.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			httpUrlConn.setDoInput(true);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setUseCaches(false); // post方式不能使用缓存

			// 设置请求头信息
			httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConn.setRequestProperty("Charset", "UTF-8");

			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			httpUrlConn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			// 请求正文信息

			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
					+ file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] head = sb.toString().getBytes("utf-8");

			// 获得输出流
			OutputStream out = new DataOutputStream(
					httpUrlConn.getOutputStream());
			// 输出表头
			out.write(head);

			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();

			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

			out.write(foot);

			out.flush();
			out.close();

			// ---------------------

			// 将返回的输入流转换成字符串
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
			inputStreamReader.close(); // 释放资源
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
