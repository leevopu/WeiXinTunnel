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

public class testFile {

	/**
	 * ģ��form������ʽ ���ϴ��ļ� �����������ʽ���ļ�д�뵽url�У�Ȼ��������������ȡurl����Ӧ
	 * @param url
	 *            �����ַ form��url��ַ
	 * @param filePath
	 *            �ļ��ڷ���������·��
	 * @return String url����Ӧ��Ϣ����ֵ
	 * @throws IOException
	 */
	public String send(String url, String type, String filePath) throws IOException {
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
		String BOUNDARY = "---------------------------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
		// ����������Ϣ
		// ��һ���֣�
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // �����������
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
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
		String filePath = "C:/Users/Administrator/Desktop/media.mp4";//���ػ�������ļ�·��
//		String sendUrl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=image";//ACCESS_TOKEN�ǻ�ȡ����access_token
		String sendUrl = "http://localhost/WeixinTest3/testServlet";
		testFile fileUpload = new testFile();
		String result = fileUpload.send(sendUrl, "image", filePath);
		System.out.println(result);

	}
}