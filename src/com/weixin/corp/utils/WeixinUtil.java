package com.weixin.corp.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;

import net.sf.json.JSONObject;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.AccessToken;
import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.entity.user.User;

public class WeixinUtil {

	public static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=APPID&corpsecret=APPSECRET";

	public static final String POST_REQUEST_METHOD = "POST";
	public static final String GET_REQUEST_METHOD = "GET";

	private static Log log = LogFactory.getLog(WeixinUtil.class);

	public static AccessToken accessToken = null;
	/**
	 * Ⱥ����Ϣ����� �����keyΪ���ڣ���������
	 */
	private static Map<String, HashSet<RequestCall>> groupMessagePool = new HashMap<String, HashSet<RequestCall>>();
	/**
	 * ��ʱ��Ϣ���Ͷ���
	 */
	private static DelayQueue<CorpBaseJsonMessage> delayJsonMessageQueue = new DelayQueue<CorpBaseJsonMessage>();
	/**
	 * �û��˺Ż����
	 */
	private static HashMap<String, User> oaUserIdPool = new HashMap<String, User>();

	private static String httpsRequestHostUrl;
	private static String httpsRequestMethod;
	private static String httpsRequestQName;

	private static String token = "weixin";
	private static String appid;
	private static String appsecret;
	private static String aeskey;
	private static String agentid;

	public static void init(String token, String appid, String appsecret,
			String aeskey, String agentid, String httpsRequestHostUrl,
			String httpsRequestMethod, String httpsRequestQName) {
		WeixinUtil.token = token;
		WeixinUtil.appid = appid;
		WeixinUtil.appsecret = appsecret;
		WeixinUtil.aeskey = aeskey;
		WeixinUtil.agentid = agentid;
		WeixinUtil.httpsRequestHostUrl = httpsRequestHostUrl;
		WeixinUtil.httpsRequestMethod = httpsRequestMethod;
		WeixinUtil.httpsRequestQName = httpsRequestQName;
	}

	// Ŀǰ���������ݿ⣬ģ��ȡ����
	// public static void testFetchData() {
	// RequestCall data1 = new RequestCall("monthlyStockReport", "3",
	// "2017-05-16", "300");
	// RequestCall data2 = new RequestCall("monthlyBondReport", "1",
	// "2017-05-16", null);
	// // test1
	// // List<Data> dataList = new ArrayList<Data>();
	// // dataList.add(data1);
	// // dataList.add(data2);
	// // for (Data data : dataList) {
	// // dataCachePool.put(data.getTitle() + data.getTouser(), data1);
	// // }
	//
	// // test2
	// List<RequestCall> testCalls = new ArrayList<RequestCall>();
	// testCalls.add(data1);
	// testCalls.add(data2);
	// for (RequestCall call : testCalls) {
	// addTimerGroupMessage(call);
	// }
	// System.out.println("������ݻ�ȡ");
	// }

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

	public static HashMap<String, User> getOaUserIdPool() {
		return oaUserIdPool;
	}

	public static DelayQueue<CorpBaseJsonMessage> getDelayJsonMessageQueue() {
		return delayJsonMessageQueue;
	}

	/**
	 * ��Ӷ�ʱȺ����Ϣ��ĿǰֻȺ�����ݿ�ÿ������ �����ڴ��ڵ���ϵͳ���ڣ���ʽΪyyyy-MM-dd8λ
	 * 
	 */
	// public static boolean addTimerGroupMessage(RequestCall call) {
	// try {
	// Date today = sdf.parse(sdf.format(new Date()));
	// String sendTime = call.getSendTime();
	// Date sendTimeDate = sdf.parse(sendTime);
	// if ("database".equals(call.getFromUser())
	// && !sendTimeDate.before(today)) {
	// if (null == groupMessagePool.get(sendTime)) {
	// groupMessagePool.put(sendTime, new HashSet<RequestCall>());
	// }
	// call.setSendTime(sendTime + " 00:00:00");
	// groupMessagePool.get(sendTime).add(call);
	// return true;
	// }
	// } catch (ParseException e) {
	// e.printStackTrace();
	// log.error("��ȡ������: " + call.getTitle() + call.getToUser() + ", ����: "
	// + call.getSendTime() + ", ����ȷ");
	// } catch (Exception e2) {
	// e2.printStackTrace();
	// }
	// return false;
	// }

	public static JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr) {
		if (null != WeixinUtil.accessToken) {
			requestUrl = requestUrl.replace("ACCESS_TOKEN",
					WeixinUtil.accessToken.getToken());
		}
		requestUrl = requestUrl.replace("AGENTID", WeixinUtil.agentid);
		try {
			RPCServiceClient serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference(
					httpsRequestHostUrl);
			options.setTo(targetEPR);

			// �ڴ���QName����ʱ��QName��Ĺ��췽���ĵ�һ��������ʾWSDL�ļ��������ռ�����Ҳ����<wsdl:definitions>Ԫ�ص�targetNamespace����ֵ
			QName qName = new QName(httpsRequestQName, httpsRequestMethod);
			Object[] parameters = new Object[] { requestUrl, requestMethod,
					outputStr, null };
			// Object[] opAddEntryArgs = new Object[] { content, atta };
			// ���ز������ͣ������axis1�е�����
			// invokeBlocking�������������������е�һ��������������QName���󣬱�ʾҪ���õķ�������
			// �ڶ���������ʾҪ���õ�WebService�����Ĳ���ֵ����������ΪObject[]��
			// ������������ʾWebService�����ķ���ֵ���͵�Class���󣬲�������ΪClass[]��
			// ������û�в���ʱ��invokeBlocking�����ĵڶ�������ֵ������null����Ҫʹ��new Object[]{}
			// ��������õ�WebService����û�з���ֵ��Ӧʹ��RPCServiceClient���invokeRobust������
			// �÷���ֻ���������������ǵĺ�����invokeBlocking������ǰ���������ĺ�����ͬ
			Class[] returnClass = new Class[] { String.class };
			String response = (String) serviceClient.invokeBlocking(
					qName, parameters, returnClass)[0];
			System.out.println(response);

			// return httpsRequest(requestUrl, requestMethod, outputStr, null);
			return JSONObject.fromObject(response);
		} catch (AxisFault x) {
			x.printStackTrace();
			log.error(x.getMessage());
		}
		return null;
	}

	public static JSONObject httpsRequestMedia(String requestUrl,
			String requestMethod, RequestCall call) {

		if (null != WeixinUtil.accessToken) {
			requestUrl = requestUrl.replace("ACCESS_TOKEN",
					WeixinUtil.accessToken.getToken());
		}
		requestUrl = requestUrl.replace("AGENTID", WeixinUtil.agentid);
		try {
			RPCServiceClient serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference(
					httpsRequestHostUrl);
			options.setTo(targetEPR);

			// �ڴ���QName����ʱ��QName��Ĺ��췽���ĵ�һ��������ʾWSDL�ļ��������ռ�����Ҳ����<wsdl:definitions>Ԫ�ص�targetNamespace����ֵ
			QName qName = new QName(httpsRequestQName, httpsRequestMethod);
			Object[] parameters = new Object[] { requestUrl, requestMethod, null, call };
			// Object[] opAddEntryArgs = new Object[] { content, atta };
			// ���ز������ͣ������axis1�е�����
			// invokeBlocking�������������������е�һ��������������QName���󣬱�ʾҪ���õķ�������
			// �ڶ���������ʾҪ���õ�WebService�����Ĳ���ֵ����������ΪObject[]��
			// ������������ʾWebService�����ķ���ֵ���͵�Class���󣬲�������ΪClass[]��
			// ������û�в���ʱ��invokeBlocking�����ĵڶ�������ֵ������null����Ҫʹ��new Object[]{}
			// ��������õ�WebService����û�з���ֵ��Ӧʹ��RPCServiceClient���invokeRobust������
			// �÷���ֻ���������������ǵĺ�����invokeBlocking������ǰ���������ĺ�����ͬ
			Class[] returnClass = new Class[] { String.class };
			String response = (String) serviceClient.invokeBlocking(
					qName, parameters, returnClass)[0];
			System.out.println(response);

			// return httpsRequest(requestUrl, requestMethod, outputStr, null);
			return JSONObject.fromObject(response);
		} catch (AxisFault x) {
			x.printStackTrace();
			log.error(x.getMessage());
		}
		return null;
	
//		return httpsRequest(requestUrl, requestMethod, null, call);
	}

	/**
	 * ��װ��webservice ������һ̨��ͨ�����ĵ�����
	 * 
	 * @param requestUrl
	 *            �����ַ
	 * @param requestMethod
	 *            ����ʽ��GET��POST��
	 * @param outputStr
	 *            �ύ���ַ�����ʽ����
	 * @param uploadMedia
	 *            �ύ���ļ��ز���ʽ���� <br>
	 * <br>
	 *            outputStr��uploadMedia��ѡһ
	 * 
	 * @return JSONObject(ͨ��JSONObject.get(key)�ķ�ʽ��ȡjson���������ֵ)
	 */
	public static JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr, RequestCall uploadMedia) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			setSSL(httpUrlConn);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// ��������ʽ��GET/POST��
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// ��������
			OutputStream out = null;

			// ���ַ�����ʽ����ʱ����json��xml
			if (null != outputStr) {
				out = new DataOutputStream(httpUrlConn.getOutputStream());
				out.write(outputStr.getBytes("UTF-8"));
				out.close();
			}
			// ���ļ��ز���ʽ����ʱ
			else if (null != uploadMedia && null != uploadMedia.getMediaByte()
					&& null != uploadMedia.getMediaName()) {
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
				// �ز����������ϴ�ʧ�ܣ�����ĸ�����
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ System.currentTimeMillis()
						+ uploadMedia.getMediaName().substring(
								uploadMedia.getMediaName().lastIndexOf("."))
						+ "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");

				byte[] head = sb.toString().getBytes("utf-8");
				out = new DataOutputStream(httpUrlConn.getOutputStream());
				// �����ͷ
				out.write(head);
				out.write(uploadMedia.getMediaByte());
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
			e.printStackTrace();
			log.error("https request error:{}", e);
		}
		return jsonObject;
	}

	private static void setSSL(HttpsURLConnection httpUrlConn) throws Exception {
		// ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
		TrustManager[] tm = { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		} };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// ������SSLContext�����еõ�SSLSocketFactory����
		SSLSocketFactory ssf = sslContext.getSocketFactory();

		httpUrlConn.setSSLSocketFactory(ssf);
	}

	/**
	 * ��ȡ���õ�access_token
	 * 
	 * @return access_token
	 */
	public static String getAvailableAccessToken() {
		if (null == accessToken) {
			return null;
		}
		return accessToken.getToken();
	}

	/**
	 * ��ȡ�µ�access_token <br>
	 * <br>
	 * ����access_token�Ľӿڵ�ַ��GET��
	 */
	public static AccessToken requestNewAccessToken() {
		String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace(
				"APPSECRET", appsecret);
		JSONObject jsonObject = httpsRequest(requestUrl, GET_REQUEST_METHOD,
				null);
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

}
