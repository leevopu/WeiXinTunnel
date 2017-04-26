package com.weixin.corp.utils;

import java.io.InputStream;
import java.io.Writer;
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
import com.weixin.corp.entity.data.Data;
import com.weixin.corp.entity.message.BaseMessage;
import com.weixin.corp.entity.message.CorpBaseMessage;
import com.weixin.corp.entity.message.Text;
import com.weixin.corp.entity.message.TextMessage;

/**
 * ��Ϣ������
 */
public class MessageUtil {
	private static Log log = LogFactory.getLog(MessageUtil.class);
	public static String GROUP_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

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
	public static String textMessageToXml(BaseMessage message) {
		xstream.alias("xml", message.getClass());
		// if(message instanceof NewsMessage){
		// xstream.alias("item", new Article().getClass());
		// }
		return xstream.toXML(message);
	}

	public static String textMessageToXml(CorpBaseMessage message) {
		xstream.alias("xml", message.getClass());
		// if(message instanceof NewsMessage){
		// xstream.alias("item", new Article().getClass());
		// }
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

	// public static void groupMessage(Map<String, String[]> messageMapConfig,
	// String... periods) throws Exception {
	// for (String period : periods) {
	// Iterator<Entry<String, String[]>> it = messageMapConfig.entrySet()
	// .iterator();
	// while (it.hasNext()) {
	// Map.Entry<String, String[]> entry = it.next();
	// if (entry.getKey().toLowerCase().contains(period)) {
	// // Method method = MessageService.class.getMethod(
	// // entry.getKey(), new Class[]{String[].class});
	// // method.invoke(MessageService.class, new Object[]{entry.getValue()});
	// sendMessageByNameAndPartyIds(entry.getKey(), entry.getValue());
	// }
	// }
	// }

	public static int groupMessage() {
		int result = 0;
		Set<Data> datas = WeixinUtil.getDatas();
		Set<Data> errordatas = new HashSet<Data>();
		for (Data data : datas) {
			TextMessage tm = new TextMessage();
			Text text = new Text();
			tm.setText(text);
			text.setContent(null == data.getContext() ? "" : data.getContext());
			tm.setAgentid(WeixinUtil.getAgentid());
			tm.setMsgtype("text");
			tm.setTouser(data.getToUser());

//			JSONObject jsonObject = WeixinUtil.httpsRequest(GROUP_MESSAGE_URL, "POST", JSONObject.fromObject(tm).toString());
//			if (null != jsonObject) {
//				if (0 != jsonObject.getInt("errcode")) {
//					result = jsonObject.getInt("errcode");
//					log.error("Ⱥ����Ϣ���� errcode:" + jsonObject.getInt("errcode")
//							+ "��errmsg:" + jsonObject.getString("errmsg") + "��invaliduser:" + jsonObject.getString("invaliduser"));
//					errordatas.add(data);
//				}
//			}
//			try {
//				Thread.sleep(5 * 1000);
//			} catch (InterruptedException e) {
//			}
		}
		datas.removeAll(errordatas);
		return result;
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
		WeixinUtil.test();
		groupMessage();
	}

}
