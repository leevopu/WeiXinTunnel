package com.weixin.corp.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.entity.message.json.FileJsonMessage;
import com.weixin.corp.entity.message.json.ImageJsonMessage;
import com.weixin.corp.entity.message.json.MpNewsJsonMessage;
import com.weixin.corp.entity.message.json.TextJsonMessage;
import com.weixin.corp.entity.message.json.VideoJsonMessage;
import com.weixin.corp.entity.message.pojo.Article;
import com.weixin.corp.entity.message.xml.CorpBaseXMLMessage;
import com.weixin.corp.entity.message.xml.NewsXMLMessage;
import com.weixin.corp.entity.message.xml.TextXMLMessage;
import com.weixin.corp.entity.user.User;
import com.weixin.corp.main.TimerTaskServlet.DailyUpdateUserTimerTask;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.WeixinUtil;

public class MessageService {

	private static Log log = LogFactory.getLog(MessageService.class);

	public static String MESSAGE_SEND = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

	public static String MEDIA_TEMP_UPLOAD = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	public static String MEDIA_PERMANENT_UPLOAD = "https://qyapi.weixin.qq.com/cgi-bin/material/add_material?type=TYPE&access_token=ACCESS_TOKEN";
	
	public static String MPNEWS_UPLOAD = "https://qyapi.weixin.qq.com/cgi-bin/material/add_mpnews?access_token=ACCESS_TOKEN";

	public static String MEDIA_PERMANENT_COUNT_GET = "https://qyapi.weixin.qq.com/cgi-bin/material/get_count?access_token=ACCESS_TOKEN";

	public static String MEDIA_PERMANENT_LIST_GET = "https://qyapi.weixin.qq.com/cgi-bin/material/batchget?access_token=ACCESS_TOKEN";

	public static String MEDIA_PERMANENT_DELETE = "https://qyapi.weixin.qq.com/cgi-bin/material/del?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";

	public static final String TEXT_MSG_TYPE = "text";
	public static final String IMAGE_MSG_TYPE = "image";
	public static final String VIDEO_MSG_TYPE = "video";
	public static final String FILE_MSG_TYPE = "file";
	public static final String MPNEWS_MSG_TYPE = "mpnews";

	/**
	 * ����΢�ŷ���������XML��
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(InputStream inputStream)
			throws Exception {
		// ����������洢��HashMap��
		Map<String, String> map = new HashMap<String, String>();

		// ��ȡ������
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// �õ�xml��Ԫ��
		Element root = document.getRootElement();
		// �õ���Ԫ�ص������ӽڵ�
		List<Element> elementList = root.elements();

		// ���������ӽڵ�
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}

		// �ͷ���Դ
		inputStream.close();

		return map;
	}

	/**
	 * ��Ϣ����ת����xml <br>
	 * XStream��һ��Java�����XML�໥ת���Ĺ���
	 * 
	 * @param message
	 * 
	 * @return xml
	 */
	public static String textMessageToXml(CorpBaseXMLMessage message) {
		xstream.alias("xml", message.getClass());
		if (message instanceof NewsXMLMessage) {
			xstream.alias("item", new Article().getClass());
		}
		return xstream.toXML(message);
	}

	/**
	 * ��չxstream��ʹ��֧��CDATA��
	 * 
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// ������xml�ڵ��ת��������CDATA���
				boolean cdata = true;

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});

	/**
	 * 
	 * @param requestMap
	 * @param paramMap
	 * @return
	 */
	public static String processRequest(Map<String, String> requestMap) {
		TextXMLMessage defaultMessage = null;
		String responseMsg = null;
		String respContent = "";

		System.out.println(requestMap);
		// ���ͷ��˺ţ��û�OpenId��
		String fromUserName = requestMap.get("FromUserName");
		System.out.println("fromUserName : " + fromUserName);
		// �������˺ţ�΢�Ź��ںţ�
		String toUserName = requestMap.get("ToUserName");
		System.out.println("toUserName : " + toUserName);
		// ��Ϣ����
		String msgType = requestMap.get("MsgType");
		System.out.println("msgType :" + msgType);
		// ��ҵӦ��ID
		String agentID = requestMap.get("AgentID");
		System.out.println("agentID :" + msgType);

		// �ı���Ϣ
		if (MESSAGE_TYPE_TEXT == msgType) {
			respContent = "�����͵����ı���Ϣ��";
		}
		// ͼƬ��Ϣ
		else if (MESSAGE_TYPE_IMAGE == msgType) {
			respContent = "�����͵���ͼƬ��Ϣ��";
		}
		// �¼�����
		else if (MESSAGE_TYPE_EVENT == msgType) {
			// �¼�����
			String eventType = requestMap.get("Event");
			System.out.println("eventType :" + eventType);
			// ����
			if (EVENT_TYPE_SUBSCRIBE == msgType) {
				respContent = "��л���Ĺ�ע��";
			}
			// �Զ���˵�����¼�
			else if (EVENT_TYPE_CLICK == msgType) {
				// ��δ��װ-------------------------------���Խ׶�
				String eventKey = requestMap.get("EventKey");
				switch (eventKey) {
				case "��������":
					System.out.println("��������");
					respContent = "�����" + eventKey + ",��Ŀǰ�޷���Ӧ";
					break;
				default:
					respContent = "�����" + eventKey;
				}
			}
		}
		// test
//		String mediaId = "2U3efl32gH-nXPgi30kLBdVjbI5IuwrizqblJift-Okdkpw3AT1FJi779H0HFOEnM0bcZv_qEadmPvyw5fkJDOg";
//		ImageXMLMessage x = new ImageXMLMessage(mediaId);
//		x.setAgentID(9);
//		x.setCreateTime(new Date().getTime());
//		x.setFromUserName("wx522a5f82e335b883");
//		x.setToUserName("leevo_pu");
		responseMsg = textMessageToXml(defaultMessage);
		return responseMsg;
	}

	public static boolean sendMessage(CorpBaseJsonMessage jsonMessage) {
		JSONObject outputStr = JSONObject.fromObject(jsonMessage);
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MESSAGE_SEND,
				WeixinUtil.POST_REQUEST_METHOD,
				outputStr.toString().replace("mediaId", "media_id"));
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("Ⱥ����Ϣ���� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
				return false;
			}
			if (jsonObject.has("invaliduser")
					&& !"".equals(jsonObject.getString("invaliduser"))) {
				log.error("��ʧ������:" + jsonObject.getString("invaliduser")
						+ "����ȷ���û��������");
				return false;
			}
		}
		return true;
	}
	
	public static JSONObject uploadMPNews(RequestCall call) {
		//����ͼ����Ϣ��
		MpNewsJsonMessage jsonMessage = 
		 new MpNewsJsonMessage(call.getTitle(),call.getMediaId(),call.getText(),call.getDigest());
		
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MessageService.MPNEWS_UPLOAD.replace("TYPE",MPNEWS_MSG_TYPE), WeixinUtil.POST_REQUEST_METHOD,
				JSONObject.fromObject(jsonMessage).toString().replace("mediaId", "media_id"));
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("����ͼ�������ز��ϴ��ӿ�ʧ�� errcode:"
						+ jsonObject.getInt("errcode") + "��errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}
	
	public static JSONObject uploadPermanentMedia(RequestCall call) {
		String msgType = call.getMsgType();
		//�����ͼ�������زģ��޸���ͼƬ�����ϴ�
		if(MPNEWS_MSG_TYPE.equals(msgType)){
			msgType=IMAGE_MSG_TYPE;
		}
		JSONObject jsonObject = WeixinUtil.httpsRequestMedia(
				MessageService.MEDIA_PERMANENT_UPLOAD.replace("TYPE",
						msgType), WeixinUtil.POST_REQUEST_METHOD,
				call.getMedia());
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("���������ز��ϴ��ӿ�ʧ�� errcode:"
						+ jsonObject.getInt("errcode") + "��errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	public static JSONObject uploadTempMedia(RequestCall call) {
		String msgType = call.getMsgType();
		JSONObject jsonObject = WeixinUtil.httpsRequestMedia(
				MessageService.MEDIA_TEMP_UPLOAD.replace("TYPE",msgType), WeixinUtil.POST_REQUEST_METHOD,
				call.getMedia());
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("������ʱ�ز��ϴ��ӿ�ʧ�� errcode:"
						+ jsonObject.getInt("errcode") + "��errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	public static JSONObject getPermanentMediaList(String type) {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("offset", 0);
		map.put("count", 50);

		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MessageService.MEDIA_PERMANENT_LIST_GET,
				WeixinUtil.GET_REQUEST_METHOD, JSONObject.fromObject(map)
						.toString());
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("��ȡ�����ز��б�ӿ�ʧ�� errcode:"
						+ jsonObject.getInt("errcode") + "��errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	public static boolean deletePermanentMedia(String media_id) {
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MEDIA_PERMANENT_DELETE.replace("MEDIA_ID", media_id),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("Ⱥ����Ϣ���� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return �ɹ�Ϊ0��ʧ����Ϊerrcode
	 */
	public static int groupMessage() {
		String todayStr = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		int result = 0;
		Set<RequestCall> successMessages = new HashSet<RequestCall>();

		for (RequestCall call : WeixinUtil.getGroupMessagePool().get(todayStr)) {
			CorpBaseJsonMessage jsonMessage = changeMessageToJson(call);
			if (sendMessage(jsonMessage)) {
				successMessages.add(call);
			}
			try {
				// ������ͣ����͵���΢�ŷ�����ѹ��
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
			}
		}
		// �Ƴ��ɹ����͵���Ϣ
		WeixinUtil.getGroupMessagePool().get(todayStr)
				.removeAll(successMessages);
		log.info("���ڣ�" + todayStr + "��Ⱥ����Ϣ���");
		return result;
	}

	public static int warnFailureMessage() {
		String todayStr = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		int result = 0;
		for (RequestCall call : WeixinUtil.getGroupMessagePool().get(todayStr)) {
			call.setToUser("����Ա");
			CorpBaseJsonMessage jsonMessage = changeMessageToJson(call);
			sendMessage(jsonMessage);
			// JSONObject jsonObject = WeixinUtil.httpsRequest(MESSAGE_SEND,
			// WeixinUtil.POST_REQUEST_METHOD,
			// JSONObject.fromObject(jsonMessage).toString());
			// if (null != jsonObject) {
			// if (0 != jsonObject.getInt("errcode")) {
			// result = jsonObject.getInt("errcode");
			// log.error("������Ϣ���� errcode:" + jsonObject.getInt("errcode")
			// + "��errmsg:" + jsonObject.getString("errmsg"));
			// }
			// }
			// try {
			// // ������ͣ�����ѹ��
			// Thread.sleep(2 * 1000);
			// } catch (InterruptedException e) {
			// }
		}
		WeixinUtil.getGroupMessagePool().get(todayStr).clear();
		log.info("������Ϣ�������");
		return result;
	}

	public static CorpBaseJsonMessage changeMessageToJson(RequestCall call) {
		CorpBaseJsonMessage jsonMessage = null;
		switch (call.getMsgType()) {
		case TEXT_MSG_TYPE:
			jsonMessage = new TextJsonMessage(call.getText());
			break;
		case IMAGE_MSG_TYPE:
			jsonMessage = new ImageJsonMessage(call.getMediaId());
			break;
		case VIDEO_MSG_TYPE:
			jsonMessage = new VideoJsonMessage(call.getMediaId());
			break;
		case FILE_MSG_TYPE:
			jsonMessage = new FileJsonMessage(call.getMediaId());
			break;
		case MPNEWS_MSG_TYPE:
			jsonMessage = new MpNewsJsonMessage(call.getMediaId());
			break;
		default:
			break;
		}
		if (!CommonUtil.StringisEmpty(call.getSendTime())) {
			jsonMessage.setSendTime(CommonUtil.getStrDate(call.getSendTime(),
					"yyyy-MM-dd HH:mm:ss").getTime());
			if (!TEXT_MSG_TYPE.equals(jsonMessage.getMsgtype())
					&& CommonUtil.getStrDate(call.getSendTime(),
							"yyyy-MM-dd HH:mm:ss").after(
							new Date(System.currentTimeMillis() + 1000 * 60
									* 60 * 24 * 3))) {
				// ˵�����ϴ������ÿ���ز���Ϣ
				jsonMessage.setPermanent(true);
			}
		}
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		// ת��
		// ת��toUser���Ż����߷ָ���б��userid���߷ָ���б�
		// jsonMessage.setTouser(call.getToUser());
		String userId = convert(call.getToUser());
		jsonMessage.setTouser(userId);
		// jsonMessage.setTouser("leevo_pu");
		return jsonMessage;
	}

	/**
	 * �������toUser��΢�Ŷ���Ϣƥ�� ת��ΪuserID
	 * 
	 * @param str
	 * @return
	 */
	private static String convert(String toUser) {
		String userIds = "";
		Map<String, HashMap<String, User>> maps = WeixinUtil.getUseridPool();
		System.out.println(maps.keySet().toString());
		Object[] strs = maps.keySet().toArray();
		if (toUser.indexOf(",") != -1) {// ���� "," �����зָ�
			userIds = splitToUser(toUser, userIds, maps, strs, ",");
		} else if (toUser.indexOf("|") != -1) {// ���� "|" �����зָ�
			userIds = splitToUser(toUser, userIds, maps, strs, "\\|");
		} else if (toUser.length() > 11) {// ֻ��һ���������ַ���,���зָ�
			String ph = toUser.substring(toUser.length() - 11, toUser.length());
			if (isNum(ph)) {
				String dep = toUser.substring(0, toUser.length() - 11);
				for (int j = 0; j < strs.length; j++) {
					// ����ƥ��
					if (strs[j].equals(dep)) {
						HashMap<String, User> datas = maps.get(dep);
						User data = datas.get(ph);
						if (null != data) {
							userIds = data.getUserid();
						}
					}
				}
			}
		}
		if (!("".equals(userIds))) {
			// �����ַ������һλ"|"
			String s = userIds
					.substring(userIds.length() - 1, userIds.length());
			if ("|".equals(s)) {// ȥ�����һ��"|"
				userIds = userIds.substring(0, userIds.length() - 1);
			}
		}
		return userIds;
	}
	
	private static String splitToUser(String toUser, String userIds,
			Map<String, HashMap<String, User>> maps, Object[] strs,
			String signal) {
		String users[] = toUser.split(signal);
		for (int i = 0; i < users.length; i++) {
			String user = users[i];
			String ph = "";
			if (user.length() > 11) {
				// ȡ���11λ ���û���ֻ�����������
				ph = user.substring(user.length() - 11, user.length());
				String dep = "";
				// �ж��Ƿ�ȫΪ���� flag�� TRUE FALSE
				if (isNum(ph)) {
					// �õ���������
					dep = user.substring(0, user.length() - 11);
					// �����������ƣ�ƥ����Ϣ
					for (int j = 0; j < strs.length; j++) {
						// ����ƥ�� ƴ��userId
						if (strs[j].equals(dep)) {
							HashMap<String, User> datas = maps.get(dep);
							User data = datas.get(ph);
							if (null != data) {
								userIds += data.getUserid() + "|";
							}
						}
					}
				}
			}
		}
		return userIds;
	}

	private static boolean isNum(String ph) {
		// �����ж�
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(ph);
		if (!isNum.matches()) {
			// ��ȫ�����֣�����û�����ֻ���
			return false;
		}
		return true;
	}

	/**
	 * ��Ϣ���ͣ��ı�
	 */
	public static final String MESSAGE_TYPE_TEXT = "text";

	/**
	 * ��Ϣ���ͣ�ͼƬ
	 */
	public static final String MESSAGE_TYPE_IMAGE = "image";

	/**
	 * ��Ϣ���ͣ�ͼ��
	 */
	public static final String MESSAGE_TYPE_NEWS = "news";
	/**
	 * ��Ϣ���ͣ�ͼ��11
	 */
	public static final String MESSAGE_TYPE_MPNEWS = "mpnews";

	/**
	 * ��Ϣ���ͣ�����
	 */
	public static final String MESSAGE_TYPE_EVENT = "event";

	/**
	 * �¼����ͣ�subscribe(����)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * �¼����ͣ�CLICK(�Զ���˵�����¼�)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";

	/**
	 * ģ���ϲ�Ӧ�õ�������
	 */
	public static String testUploadToServer(String requestUrl, RequestCall call) {
		String result = null;
		String msgType = call.getMsgType();
		switch (msgType) {
		case TEXT_MSG_TYPE:
			System.out.println("123");
		case IMAGE_MSG_TYPE:
			System.out.println(234);
		case VIDEO_MSG_TYPE:
			System.out.println(345);
		case FILE_MSG_TYPE:
			break;
		default:
			return "���͵���Ϣ���Ͳ���ȷ��ֻ����text,image,video��file";
		}
		HttpURLConnection httpUrlConn = null;
		try {
//			TrustManager[] tm = { new MyX509TrustManager() };
//			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//			sslContext.init(null, tm, new java.security.SecureRandom());
//			// ������SSLContext�����еõ�SSLSocketFactory����
//			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url
					.openConnection();
//			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setRequestMethod("POST"); // ��Post��ʽ�ύ����Ĭ��get��ʽ
			httpUrlConn.setDoInput(true);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setUseCaches(false); // post��ʽ����ʹ�û���
			// ��������ͷ��Ϣ
			httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConn.setRequestProperty("Charset", "UTF-8");
			// ���ñ߽�
			String BOUNDARY = "---------------------------"
					+ System.currentTimeMillis();
			httpUrlConn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			final String newLine = "\r\n";

			// ��������
			OutputStream out = new DataOutputStream(httpUrlConn.getOutputStream());

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

			if(null != call.getFromUser()){
			// ��ӷ�����
			sb.append("--"); // �����������
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
			}

			// ��ӽ�����
			sb.append("--"); // �����������
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

			if(null != call.getSendTime()){
			// ���ʱ��
			sb.append("--"); // �����������
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
			}

			// �����Ϣ���ݣ��ı����ļ���
			sb.append("--"); // �����������
			sb.append(BOUNDARY);
			sb.append(newLine);
			if (TEXT_MSG_TYPE.equals(msgType)) {
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
				DataInputStream in = new DataInputStream(new FileInputStream(
						media));
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
		} catch (Exception e) {
			e.printStackTrace();
			log.error("������Ϣ�ӿ�����ʧ��");
			return "������Ϣ�ӿ�����ʧ��";
		}

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// ����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(
					httpUrlConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			log.error("��ȡPOST������Ӧ�����쳣��" + e);
			e.printStackTrace();
			return "��ȡPOST������Ӧ�����쳣��";
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

	public static void main(String[] args) throws Exception {
		RequestCall call = new RequestCall();
		call.setMsgType("image");
		call.setToUser("888");
		call.setMedia(new File("C:/Users/Administrator/Desktop/abc.png"));
		MessageService.testUploadToServer("http://localhost/WeixinTest3/uploadServlet", call);
	}

}
