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
			return "���͵���Ϣ���Ͳ���ȷ��ֻ����text,image,media��file";
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
		String BOUNDARY = "---------------------------"
				+ System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
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
		sb.append(callMessage.getFromUser());
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
		sb.append(callMessage.getToUser());
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
		sb.append(callMessage.getSendTime());
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
			sb.append(callMessage.getText());
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);
			out.write(sb.toString().getBytes("utf-8"));
		} else {
			File media = new File(callMessage.getMediaPath());
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
			DataInputStream in = new DataInputStream(new FileInputStream(media));
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
			System.out.println("����POST��������쳣��" + e);
			e.printStackTrace();
			throw new IOException("���ݶ�ȡ�쳣");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

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
	public String sendFileUsePost(String url, String type, String filePath)
			throws IOException {
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
		String BOUNDARY = "---------------------------"
				+ System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
		final String newLine = "\r\n";
		// ����������Ϣ
		// ��һ���֣�
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // �����������
		sb.append(BOUNDARY);
		sb.append(newLine);
		sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
				+ file.getName() + "\"");
		sb.append(newLine);
		sb.append("Content-Type:application/octet-stream");
		sb.append(newLine);
		sb.append(newLine);
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
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
		// ����������ݷָ���
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
		return result;
	}

	public static void main(String[] args) throws IOException {
		String filePath = "C:/Users/Administrator/Desktop/ggg.jpg";// ���ػ�������ļ�·��
		// String sendUrl =
		// "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=image";//ACCESS_TOKEN�ǻ�ȡ����access_token
		String sendUrl = "http://localhost/WeixinTest3/testServlet";
		testFile fileUpload = new testFile();
		CallMessage callMessage = new CallMessage("doubi", "13788888888��Ӫ��",
				"image", null, filePath, "2017-05-01");
		// String resultImage = fileUpload.sendFileUsePost(sendUrl, "image",
		// filePath);
		String result = fileUpload.sendFileUsePost(sendUrl, callMessage);

	}
}
