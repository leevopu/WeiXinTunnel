package com.weixin.corp.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;

public class WeixinUtil {

	public static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=APPID&corpsecret=APPSECRET";

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static final String TEMP_URL = "D:/temp/";

	public static final String POST_REQUEST_METHOD = "POST";
	public static final String GET_REQUEST_METHOD = "GET";

	private static Log log = LogFactory.getLog(WeixinUtil.class);

	public static AccessToken accessToken = null;

	/**
	 * ֻ���fromUserΪdatabase��Ⱥ����Ϣ �����keyΪ���ڣ������������
	 */
	private static Map<String, HashSet<RequestCall>> groupMessagePool = new HashMap<String, HashSet<RequestCall>>();

	private static DelayQueue<CorpBaseJsonMessage> delayJsonMessageQueue = new DelayQueue<CorpBaseJsonMessage>();

	private static Map<String, HashMap<String, String>> useridPool = new HashMap<String, HashMap<String, String>>();

	private static String token = "weixin";
	private static String appid;
	private static String appsecret;
	private static String aeskey;
	private static String agentid;

	public static void init(String token, String appid, String appsecret,
			String aeskey, String agentid) {
		WeixinUtil.token = token;
		WeixinUtil.appid = appid;
		WeixinUtil.appsecret = appsecret;
		WeixinUtil.aeskey = aeskey;
		WeixinUtil.agentid = agentid;
	}

	// Ŀǰ���������ݿ⣬ģ��ȡ����
	public static void testFetchData() {
		RequestCall data1 = new RequestCall("monthlyStockReport", "wangli",
				"2017-04-26", "300");
		RequestCall data2 = new RequestCall("monthlyBondReport", "dawei",
				"2017-04-26", null);
		// test1
		// List<Data> dataList = new ArrayList<Data>();
		// dataList.add(data1);
		// dataList.add(data2);
		// for (Data data : dataList) {
		// dataCachePool.put(data.getTitle() + data.getTouser(), data1);
		// }

		// test2
		List<RequestCall> testCalls = new ArrayList<RequestCall>();
		testCalls.add(data1);
		testCalls.add(data2);
		for (RequestCall call : testCalls) {
			addTimerGroupMessage(call);
		}
		System.out.println("������ݻ�ȡ");
	}

	/**
	 * ��΢�ŷ���������֤���� <br>
	 * <br>
	 * ��ͬ��accessToken
	 */
	public static String getToken() {
		return token;
	}

	public static String getAppid() {
		return appid;
	}

	public static String getAgentid() {
		return agentid;
	}

	public static String getAppsecret() {
		return appsecret;
	}

	public static String getAeskey() {
		return aeskey;
	}

	public static Map<String, HashSet<RequestCall>> getGroupMessagePool() {
		return groupMessagePool;
	}

	public static Map<String, HashMap<String, String>> getUseridPool() {
		return useridPool;
	}

	public static JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr) {
		return httpsRequest(requestUrl, requestMethod, outputStr, null);
	}

	public static JSONObject httpsRequestMedia(String requestUrl,
			String requestMethod, File uploadMedia) {
		return httpsRequest(requestUrl, requestMethod, null, uploadMedia);
	}

	public static DelayQueue<CorpBaseJsonMessage> getDelayJsonMessageQueue() {
		return delayJsonMessageQueue;
	}

	/**
	 * ��Ӷ�ʱȺ����Ϣ��ĿǰֻȺ�����ݿ�ÿ������ �����ڴ��ڵ���ϵͳ���ڣ���ʽΪyyyy-MM-dd8λ
	 * 
	 */
	public static boolean addTimerGroupMessage(RequestCall call) {
		try {
			Date today = sdf.parse(sdf.format(new Date()));
			String sendTime = call.getSendTime();
			if (null == sendTime || sendTime.length() != 8) {
				return false;
			}
			Date sendTimeDate = sdf.parse(sendTime);
			if ("database".equals(call.getFromUser())
					&& !sendTimeDate.before(today)) {
				if (null == groupMessagePool.get(sendTime)) {
					groupMessagePool.put(sendTime, new HashSet<RequestCall>());
				}
				groupMessagePool.get(sendTime).add(call);
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("��ȡ������: " + call.getTitle() + call.getToUser() + ", ����: "
					+ call.getSendTime() + ", ����ȷ");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return false;
	}

	/**
	 * ����https���󲢻�ȡ���
	 * 
	 * @param requestUrl
	 *            �����ַ
	 * @param requestMethod
	 *            ����ʽ��GET��POST��
	 * @param outputStr
	 *            �ύ���ַ�����ʽ����
	 * @param uploadMedia
	 *            �ύ���ļ��ز���ʽ����
	 * @return JSONObject(ͨ��JSONObject.get(key)�ķ�ʽ��ȡjson���������ֵ)
	 */
	private static JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr, File uploadMedia) {
		// return null;
		if (null != WeixinUtil.accessToken) {
			requestUrl = requestUrl.replace("ACCESS_TOKEN",
					WeixinUtil.accessToken.getToken());
		}
		requestUrl = requestUrl.replace("AGENTID", WeixinUtil.agentid);
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try { // ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// ������SSLContext�����еõ�SSLSocketFactory����
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// ��������ʽ��GET/POST��
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// ��������
			OutputStream out = new DataOutputStream(
					httpUrlConn.getOutputStream());

			// ���ַ�����ʽ����ʱ����json��xml
			if (null != outputStr) {
				out.write(outputStr.getBytes("UTF-8"));
				out.close();
			}
			// ���ļ��ز���ʽ����ʱ
			else if (null != uploadMedia) {
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
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ uploadMedia.getName() + "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");

				byte[] head = sb.toString().getBytes("utf-8");

				// �����ͷ
				out.write(head);
				// �ļ����Ĳ���
				// ���ļ������ļ��ķ�ʽ ���뵽url��
				DataInputStream in = new DataInputStream(new FileInputStream(
						uploadMedia));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
				// ��β����
				byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n")
						.getBytes("utf-8");// ����������ݷָ���
				out.write(foot);
				out.flush();
				out.close();
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
			// �ͷ���Դ
			bufferedReader.close();
			inputStreamReader.close();
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
	}

	/**
	 * ��ȡ���õ�access_token
	 * 
	 * @return access_token
	 */
	public static String getAvailableAccessToken() {
		return accessToken.getToken();
	}

	/**
	 * ��ȡ�µ�access_token
	 * <br>
	 * <br>
	 * ����access_token�Ľӿڵ�ַ��GET��
	 */
	public static AccessToken getNewAccessToken() {
		String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace(
				"APPSECRET", appsecret);
		JSONObject jsonObject = httpsRequest(requestUrl, GET_REQUEST_METHOD,
				null, null);
		// �������ɹ�
		if (null != jsonObject) {
			System.out.println("access_token init success");
			try {
				accessToken = new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
			} catch (Exception e) {
				System.out.println("access_token init failure");
				e.printStackTrace();
				accessToken = null;
			}
		}
		return accessToken;
	}

	/**
	 * ��֤ǩ��
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp,
			String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// ��token��timestamp��nonce�������������ֵ�������
		// Arrays.sort(arr);
		sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}

		System.out.println("content.toString : " + content.toString());

		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// �����������ַ���ƴ�ӳ�һ���ַ�������sha1����
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// ��sha1���ܺ���ַ�������signature�Աȣ���ʶ��������Դ��΢��
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}

	/**
	 * ���ֽ�����ת��Ϊʮ�������ַ���
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * ���ֽ�ת��Ϊʮ�������ַ���
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}

	private static void sort(String a[]) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[j].compareTo(a[i]) < 0) {
					String temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String x = "{\"errcode\": 0,   \"errmsg\": \"ok\",  \"invaliduser\": \"\",  \"invalidparty\":\"PartyID1\",   \"invalidtag\":\"TagID1\"}";
		JSONObject jsonObject = JSONObject.fromObject(x);
		System.out.println("".equals(jsonObject.getString("Invaliduser")));
	}
}
