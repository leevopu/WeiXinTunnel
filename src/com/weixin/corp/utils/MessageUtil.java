package com.weixin.corp.utils;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.weixin.corp.entity.message.BaseMessage;
import com.weixin.corp.entity.message.CorpBaseMessage;
import com.weixin.corp.entity.message.ImageMessage;
import com.weixin.corp.entity.message.TemplateMessage;
import com.weixin.corp.entity.message.TemplateMessageData;
import com.weixin.corp.entity.message.TextMessage;

/**
 * ��Ϣ������
 */
public class MessageUtil {
	private static Log log = LogFactory.getLog(MessageUtil.class);

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
		for (Element e : elementList){
			System.out.println("element name: " + e.getName());
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
	public static String textMessageToXml(BaseMessage message) {
		xstream.alias("xml", message.getClass());
//		if(message instanceof NewsMessage){
//			xstream.alias("item", new Article().getClass());
//		}
		return xstream.toXML(message);
	}
	
	public static String textMessageToXml(CorpBaseMessage message) {
		xstream.alias("xml", message.getClass());
//		if(message instanceof NewsMessage){
//			xstream.alias("item", new Article().getClass());
//		}
		return xstream.toXML(message);
	}

	// /**
	// * ������Ϣ����ת����xml
	// *
	// * @param musicMessage ������Ϣ����
	// * @return xml
	// */
	// public static String musicMessageToXml(MusicMessage musicMessage) {
	// xstream.alias("xml", musicMessage.getClass());
	// return xstream.toXML(musicMessage);
	// }
	//
	// /**
	// * ͼ����Ϣ����ת����xml
	// *
	// * @param newsMessage ͼ����Ϣ����
	// * @return xml
	// */
	// public static String newsMessageToXml(NewsMessage newsMessage) {
	// xstream.alias("xml", newsMessage.getClass());
	// xstream.alias("item", new Article().getClass());
	// return xstream.toXML(newsMessage);
	// }

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

	public static String processRequest(Map<String, String> requestMap,
			Map<String, String> paramMap) {
		CorpBaseMessage baseMessage = null;
		String respMessage = null;
		String respContent = "";

//		// xml�������
//		Map<String, String> requestMap = null;
//		try {
//			requestMap = parseXml(request);
//		} catch (Exception e) {
//			e.printStackTrace();
//			respContent = "��Ӧ�������Ժ�����";
//			paramMap = null;
//		}
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
		// if ("image".equals(msgType)) {
		// mediaId = requestMap.get("MediaId");
		// System.out.println("MediaId : " + mediaId);
		// }

		// if(null != paramMap.get("UPLOAD_TYPE")){
		//
		// }

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
		// ���Ի�ͼƬ��Ϣ���Ժ�ɸ����¼���Ӧ��Ҫ��ͼƬ��Ϣ
		if (null != paramMap && null != paramMap.get("mediaId")) {
			ImageMessage imageMessage = new ImageMessage(
					paramMap.get("mediaId"));
			imageMessage.setToUserName(fromUserName);
			imageMessage.setFromUserName(toUserName);
			imageMessage.setCreateTime(System.currentTimeMillis());
			imageMessage.setMsgType(MessageUtil.MESSAGE_TYPE_IMAGE);
			imageMessage.setAgentID(agentID);
			baseMessage = imageMessage;
		} else {
			// �ظ��ı���Ϣ
			TextMessage textMessage = new TextMessage(respContent);
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(System.currentTimeMillis());
			textMessage.setMsgType(MessageUtil.MESSAGE_TYPE_TEXT);
			textMessage.setAgentID(agentID);
			baseMessage = textMessage;
		}

		respMessage = textMessageToXml(baseMessage);
		return respMessage;
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
}
