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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.entity.message.json.FileJsonMessage;
import com.weixin.corp.entity.message.json.ImageJsonMessage;
import com.weixin.corp.entity.message.json.VideoJsonMessage;
import com.weixin.corp.entity.message.pojo.Article;
import com.weixin.corp.entity.message.xml.CorpBaseXMLMessage;
import com.weixin.corp.entity.message.xml.NewsXMLMessage;
import com.weixin.corp.entity.message.xml.TextXMLMessage;
import com.weixin.corp.entity.user.User;
import com.weixin.corp.main.TimerTaskServlet.DailyUpdateUserTimerTask;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.MyX509TrustManager;
import com.weixin.corp.utils.WeixinUtil;

public class MessageService {

	private static Log log = LogFactory.getLog(MessageService.class);

	public static String MESSAGE_SEND = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

	public static String MEDIA_TEMP_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	public static String MEDIA_PERMANENT_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/material/add_material?type=TYPE&access_token=ACCESS_TOKEN";

	public static String MEDIA_PERMANENT_COUNT_GET_URL = "https://qyapi.weixin.qq.com/cgi-bin/material/get_count?access_token=ACCESS_TOKEN";

	public static String MEDIA_PERMANENT_LIST_GET_URL = "https://qyapi.weixin.qq.com/cgi-bin/material/batchget?access_token=ACCESS_TOKEN";

	public static final String TEXT_MSG_TYPE = "text";
	public static final String IMAGE_MSG_TYPE = "image";
	public static final String VIDEO_MSG_TYPE = "video";
	public static final String FILE_MSG_TYPE = "file";

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
	private static XStream xstream = new XStream();

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
			respContent = "�����͵����ı�ff��Ϣ��";
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
		responseMsg = textMessageToXml(defaultMessage);
		return responseMsg;
	}

	public static boolean sendMessage(CorpBaseJsonMessage jsonMessage) {
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MESSAGE_SEND,
				WeixinUtil.POST_REQUEST_METHOD,
				JSONObject.fromObject(jsonMessage).toString()
						.replace("mediaId", "media_id"));
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
			jsonMessage = new ImageJsonMessage(call.getText());
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
		default:
			break;
		}
		if (null != call.getSendTime() && !"".equals(call.getSendTime())) {
			jsonMessage.setSendTime(CommonUtil.getStrDate(call.getSendTime(),
					"yyyy-MM-dd HH:mm:ss").getTime());
		}
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		// ת��
		// ת��toUser���Ż����߷ָ���б��userid���߷ָ���б�
		// jsonMessage.setTouser(call.getToUser());
		// String userId = convert(call.getToUser());
		// jsonMessage.setTouser(call.getToUser());
		jsonMessage.setTouser("leevo_pu");
		return jsonMessage;
	}

	/**
	 * �������toUser��΢�Ŷ���Ϣƥ�� ת��ΪuserID
	 * 
	 * @param str
	 * @return
	 */
	public static String convert(String toUser) {
		if ("".equals(toUser) || null == toUser) {// touserΪ��
			log.error("toUserΪ��");
			return null;
		}
		String userIds = "";
		//��΢�Ŷ�ȡͨѶ¼����  :���⣺���ݲ���id��΢�Ŷ�ȡ��Ա����  ���ɹ�  url����
		String depts[] = new String[] {};
		if (toUser.indexOf("|") != -1) {// ���� "," "|"�����зָ�
			// System.out.println("����");
			String users[] = toUser.split("|");
			for (int i = 0; i < users.length; i++) {
				String user = users[i];
				String dep = "";
				String ph = "";
				if (user.length() > 11) {
					// ȡ���11λ
					ph = user.substring(user.length() - 11, user.length());
					// �ж��Ƿ�ȫΪ���� flag�� TRUE FALSE
					boolean flag = isNum(ph);
					if (flag) {
						dep = user.substring(0, user.length() - 11);
					} else {
						dep = user;
					}
				} else {// �϶�û�����ֻ���
					dep = user;
				}
			}
		} else {// ֻ��һλ,���зָ�
				// ph = toUser.substring(toUser.length()-11, toUser.length());
		}
		// ��΢�Ŷ�ȡͨѶ¼���� :���⣺���ݲ���id��΢�Ŷ�ȡ��Ա���� ���ɹ� url����
		DailyUpdateUserTimerTask x = new DailyUpdateUserTimerTask();
		x.run();
		Map<String, HashMap<String, User>> maps = WeixinUtil.getUseridPool();
		System.out.println(maps.keySet().toString());
		Object[] strs = maps.keySet().toArray();
		if(toUser.indexOf(",")!=-1){//  ���� ","  �����зָ� 
			//System.out.println("����");
			String users[] = toUser.split(",");
			for (int i = 0; i < users.length; i++) {
				String user = users[i];
				String ph   = "";
				if(user.length()>11){
					//ȡ���11λ   ���û���ֻ�����������
					ph = user.substring(user.length()-11, user.length());
					String dep  = "";
					//�ж��Ƿ�ȫΪ����  flag�� TRUE FALSE
					if(isNum(ph)){
						//�õ���������
						dep = user.substring(0,user.length()-11); 
						//�����������ƣ�ƥ����Ϣ
						for (int j = 0; j < strs.length; j++) {
							//����ƥ��   
							if(strs[j].equals(dep)){
								HashMap<String, User> datas = maps.get(dep);
								User data = datas.get(ph);
								if(null!=data){
									userIds +=data.getUserid()+"|";
								}
							}
						}
					}
				}
			}
		}else{//ֻ��һ���������ַ���,���зָ�
			String ph   = toUser.substring(toUser.length()-11, toUser.length());
			if(isNum(ph)){
				String dep = toUser.substring(0,toUser.length()-11);
				for (int j = 0; j < strs.length; j++) {
					//����ƥ��   
					if(strs[j].equals(dep)){
						HashMap<String, User> datas = maps.get(dep);
						User data = datas.get(ph);
						if(null!=data){
							userIds = data.getUserid();
						}
					}
				}
			}
		}
		//�����ַ������һλ"|"
		String s = userIds.substring(userIds.length()-1, userIds.length());
		if("|".equals(s)){
			userIds = userIds.substring(0, userIds.length()-1);
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
	public String testUploadToServer(String requestUrl, RequestCall call) {
		String result = null;
		String msgType = call.getMsgType();
		if (TEXT_MSG_TYPE != msgType && IMAGE_MSG_TYPE != msgType
				&& VIDEO_MSG_TYPE != msgType && FILE_MSG_TYPE != msgType) {
			return "���͵���Ϣ���Ͳ���ȷ��ֻ����text,image,video��file";
		}
		URL url = null;
		HttpURLConnection con = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// ������SSLContext�����еõ�SSLSocketFactory����
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			con = (HttpURLConnection) url.openConnection();
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
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
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
			sb.append(call.getFromUser());
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
			sb.append(call.getToUser());
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
			sb.append(call.getSendTime());
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
					con.getInputStream()));
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

	public static void main(String[] args) {
		TextXMLMessage textXMLMessage = new TextXMLMessage("abc");
		System.out.println(textMessageToXml(textXMLMessage));
	}

}
