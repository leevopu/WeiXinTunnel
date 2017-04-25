package com.weixin.corp.utils;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.weixin.corp.service.MessageService;

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
	public static String processRequest(Map<String, String> requestMap,
			Map<String, String> paramMap) {
		CorpBaseMessage baseMessage = null;
		String respMessage = null;
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

		respMessage = textMessageToXml(baseMessage);
		return respMessage;
	}
	
	public static void itWarnMessage(String warn) {
		System.out.println(warn);
	}
	
	public static void sendMessageByNameAndPartyIds(String messageName, String[] partyIds) {
//		String result = testDao.test();
		// ȡ��������
		for(String partyId : partyIds)
		System.out.println(partyId);
	}
	
	public static void groupMessage(Map<String, String[]> messageMapConfig,
			String... periods) throws Exception {
		for (String period : periods) {
			Iterator<Entry<String, String[]>> it = messageMapConfig.entrySet()
					.iterator();
 			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = it.next();
				if (entry.getKey().toLowerCase().contains(period)) {
					Method method = MessageService.class.getMethod(
							entry.getKey(), new Class[]{String[].class});
					method.invoke(MessageService.class, new Object[]{entry.getValue()});
				}
			}
		}
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
