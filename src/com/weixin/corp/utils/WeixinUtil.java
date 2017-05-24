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
	 * 群发消息缓存池 最外层key为日期，方便清理
	 */
	private static Map<String, HashSet<RequestCall>> groupMessagePool = new HashMap<String, HashSet<RequestCall>>();
	/**
	 * 定时消息发送队列
	 */
	private static DelayQueue<CorpBaseJsonMessage> delayJsonMessageQueue = new DelayQueue<CorpBaseJsonMessage>();
	/**
	 * 用户账号缓存池
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

	// 目前环境无数据库，模拟取数据
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
	// System.out.println("完成数据获取");
	// }

	/**
	 * 与微信服务器的验证口令 <br>
	 * <br>
	 * 不同于accessToken
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
	 * 添加定时群发消息，目前只群发数据库每日跑批 且日期大于等于系统日期，格式为yyyy-MM-dd8位
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
	// log.error("获取的数据: " + call.getTitle() + call.getToUser() + ", 日期: "
	// + call.getSendTime() + ", 不正确");
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

			// 在创建QName对象时，QName类的构造方法的第一个参数表示WSDL文件的命名空间名，也就是<wsdl:definitions>元素的targetNamespace属性值
			QName qName = new QName(httpsRequestQName, httpsRequestMethod);
			Object[] parameters = new Object[] { requestUrl, requestMethod,
					outputStr, null };
			// Object[] opAddEntryArgs = new Object[] { content, atta };
			// 返回参数类型，这个和axis1有点区别
			// invokeBlocking方法有三个参数，其中第一个参数的类型是QName对象，表示要调用的方法名；
			// 第二个参数表示要调用的WebService方法的参数值，参数类型为Object[]；
			// 第三个参数表示WebService方法的返回值类型的Class对象，参数类型为Class[]。
			// 当方法没有参数时，invokeBlocking方法的第二个参数值不能是null，而要使用new Object[]{}
			// 如果被调用的WebService方法没有返回值，应使用RPCServiceClient类的invokeRobust方法，
			// 该方法只有两个参数，它们的含义与invokeBlocking方法的前两个参数的含义相同
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

			// 在创建QName对象时，QName类的构造方法的第一个参数表示WSDL文件的命名空间名，也就是<wsdl:definitions>元素的targetNamespace属性值
			QName qName = new QName(httpsRequestQName, httpsRequestMethod);
			Object[] parameters = new Object[] { requestUrl, requestMethod, null, call };
			// Object[] opAddEntryArgs = new Object[] { content, atta };
			// 返回参数类型，这个和axis1有点区别
			// invokeBlocking方法有三个参数，其中第一个参数的类型是QName对象，表示要调用的方法名；
			// 第二个参数表示要调用的WebService方法的参数值，参数类型为Object[]；
			// 第三个参数表示WebService方法的返回值类型的Class对象，参数类型为Class[]。
			// 当方法没有参数时，invokeBlocking方法的第二个参数值不能是null，而要使用new Object[]{}
			// 如果被调用的WebService方法没有返回值，应使用RPCServiceClient类的invokeRobust方法，
			// 该方法只有两个参数，它们的含义与invokeBlocking方法的前两个参数的含义相同
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
	 * 封装成webservice 单独在一台能通外网的电脑上
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的字符串形式请求
	 * @param uploadMedia
	 *            提交的文件素材形式请求 <br>
	 * <br>
	 *            outputStr和uploadMedia二选一
	 * 
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
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
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 获得输出流
			OutputStream out = null;

			// 当字符串形式请求时，如json、xml
			if (null != outputStr) {
				out = new DataOutputStream(httpUrlConn.getOutputStream());
				out.write(outputStr.getBytes("UTF-8"));
				out.close();
			}
			// 当文件素材形式请求时
			else if (null != uploadMedia && null != uploadMedia.getMediaByte()
					&& null != uploadMedia.getMediaName()) {
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
				// 素材用中文名上传失败，随机改个名字
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ System.currentTimeMillis()
						+ uploadMedia.getMediaName().substring(
								uploadMedia.getMediaName().lastIndexOf("."))
						+ "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");

				byte[] head = sb.toString().getBytes("utf-8");
				out = new DataOutputStream(httpUrlConn.getOutputStream());
				// 输出表头
				out.write(head);
				out.write(uploadMedia.getMediaByte());
				// 结尾部分
				byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n")
						.getBytes("utf-8");// 定义最后数据分隔线
				out.write(foot);
				out.flush();
				out.close();
			}

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
			// 释放资源
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
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
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
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();

		httpUrlConn.setSSLSocketFactory(ssf);
	}

	/**
	 * 获取可用的access_token
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
	 * 获取新的access_token <br>
	 * <br>
	 * 调用access_token的接口地址（GET）
	 */
	public static AccessToken requestNewAccessToken() {
		String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace(
				"APPSECRET", appsecret);
		JSONObject jsonObject = httpsRequest(requestUrl, GET_REQUEST_METHOD,
				null);
		// 如果请求成功
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
