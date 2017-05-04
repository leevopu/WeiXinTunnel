package com.weixin.corp.utils;

import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.weixin.corp.entity.message.json.VideoJsonMessage;
import com.weixin.corp.entity.message.pojo.Article;
import com.weixin.corp.entity.message.xml.CorpBaseXMLMessage;
import com.weixin.corp.entity.message.xml.NewsXMLMessage;
import com.weixin.corp.entity.message.xml.TextXMLMessage;

/**
 * ��Ϣ������
 */
public class MessageUtil {
	private static Log log = LogFactory.getLog(MessageUtil.class);

	public static String MESSAGE_SEND = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

	public static String MEDIA_TEMP_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

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
		if (msgType.equals(MessageUtil.MESSAGE_TYPE_TEXT)) {
			respContent = "�����͵����ı�ff��Ϣ��";
		}
		// ͼƬ��Ϣ
		else if (msgType.equals(MessageUtil.MESSAGE_TYPE_IMAGE)) {
			respContent = "�����͵���ͼƬ��Ϣ��";
		}
		// �¼�����
		else if (msgType.equals(MessageUtil.MESSAGE_TYPE_EVENT)) {
			// �¼�����
			String eventType = requestMap.get("Event");
			System.out.println("eventType :" + eventType);
			// ����
			if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
				respContent = "��л���Ĺ�ע��";
			}
			// �Զ���˵�����¼�
			else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
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
		log.info("Ⱥ����Ϣ���");
		return result;
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
			if (!"".equals(jsonObject.getString("invaliduser"))) {
				log.error("��ʧ������:" + jsonObject.getString("invaliduser")
						+ "����ȷ���û��������");
				return false;
			}
		}
		return true;
	}

	public static int warnFailureMessage() {
		String todayStr = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		int result = 0;
		for (RequestCall call : WeixinUtil.getGroupMessagePool().get(todayStr)) {
			CorpBaseJsonMessage jsonMessage = changeMessageToJson(call);
			jsonMessage.setTouser("����Ա");
			JSONObject jsonObject = WeixinUtil.httpsRequest(MESSAGE_SEND,
					WeixinUtil.POST_REQUEST_METHOD,
					JSONObject.fromObject(jsonMessage).toString());
			if (null != jsonObject) {
				if (0 != jsonObject.getInt("errcode")) {
					result = jsonObject.getInt("errcode");
					log.error("������Ϣ���� errcode:" + jsonObject.getInt("errcode")
							+ "��errmsg:" + jsonObject.getString("errmsg"));
				}
			}
			try {
				// ������ͣ�����ѹ��
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
			}
		}
		log.info("������Ϣ�������");
		return result;
	}

	public static CorpBaseJsonMessage changeMessageToJson(RequestCall call) {
		CorpBaseJsonMessage jsonMessage = null;
		switch (call.getMsgType()) {
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
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		// ת��
		// ת��toUser���Ż����߷ָ���б��userid���߷ָ���б�
		// jsonMessage.setTouser(call.getToUser());
		jsonMessage.setTouser(call.getToUser());
		return jsonMessage;
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

	public static void main(String[] args) {
		TextXMLMessage textXMLMessage = new TextXMLMessage("abc");
		System.out.println(textMessageToXml(textXMLMessage));
	}
}