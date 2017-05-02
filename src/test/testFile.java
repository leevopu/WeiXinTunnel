package test;

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

import com.weixin.corp.entity.message.CallMessage;

public class testFile {
	private static final String TEXT_MSG_TYPE = "text";
	private static final String IMAGE_MSG_TYPE = "image";
	private static final String MEDIA_MSG_TYPE = "media";
	private static final String FILE_MSG_TYPE = "file";

	public String sendFileUsePost(String url, CallMessage callMessage)
			throws IOException {
		String result = null;
		String msgType = callMessage.getMsgType();
		if (TEXT_MSG_TYPE != msgType && IMAGE_MSG_TYPE != msgType
				&& MEDIA_MSG_TYPE != msgType && FILE_MSG_TYPE != msgType) {
			return "发送的消息类型不正确，只允许text,image,media和file";
		}
		/**
		 * 第一部分
		 */
		URL urlObj = new URL(url);
		// 连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		/**
		 * 设置关键值
		 */
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
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
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
		sb.append(callMessage.getFromUser());
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
		sb.append(callMessage.getToUser());
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
		sb.append(callMessage.getSendTime());
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
			sb.append(callMessage.getText());
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);
			out.write(sb.toString().getBytes("utf-8"));
		} else {
			File media = new File(callMessage.getMediaPath());
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
			DataInputStream in = new DataInputStream(new FileInputStream(media));
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
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			throw new IOException("数据读取异常");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

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
	public String sendFileUsePost(String url, String type, String filePath)
			throws IOException {
		String result = null;
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("文件不存在");
		}
		/**
		 * 第一部分
		 */
		URL urlObj = new URL(url);
		// 连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		/**
		 * 设置关键值
		 */
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
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
		final String newLine = "\r\n";
		// 请求正文信息
		// 第一部分：
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // 必须多两道线
		sb.append(BOUNDARY);
		sb.append(newLine);
		sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
				+ file.getName() + "\"");
		sb.append(newLine);
		sb.append("Content-Type:application/octet-stream");
		sb.append(newLine);
		sb.append(newLine);
		byte[] head = sb.toString().getBytes("utf-8");
		// 获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
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
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
		// 定义最后数据分隔线
		out.write(foot);
		out.flush();
		out.close();

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
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			throw new IOException("数据读取异常");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		String filePath = "C:/Users/Administrator/Desktop/ggg.jpg";// 本地或服务器文件路径
		// String sendUrl =
		// "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=image";//ACCESS_TOKEN是获取到的access_token
		String sendUrl = "http://localhost/WeixinTest3/testServlet";
		testFile fileUpload = new testFile();
		CallMessage callMessage = new CallMessage("doubi", "13788888888运营部",
				"image", null, filePath, "2017-05-01");
		// String resultImage = fileUpload.sendFileUsePost(sendUrl, "image",
		// filePath);
		String result = fileUpload.sendFileUsePost(sendUrl, callMessage);

	}
}
