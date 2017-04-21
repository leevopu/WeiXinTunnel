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
 * 消息工具类
 */
public class MessageUtil {
	private static Log log = LogFactory.getLog(MessageUtil.class);

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(InputStream inputStream)
			throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		// 遍历所有子节点
		for (Element e : elementList){
			System.out.println("element name: " + e.getName());
			map.put(e.getName(), e.getText());
		}

		// 释放资源
		inputStream.close();

		return map;
	}

	/**
	 * 消息对象转换成xml <br>
	 * XStream是一个Java对象和XML相互转换的工具
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
	// * 音乐消息对象转换成xml
	// *
	// * @param musicMessage 音乐消息对象
	// * @return xml
	// */
	// public static String musicMessageToXml(MusicMessage musicMessage) {
	// xstream.alias("xml", musicMessage.getClass());
	// return xstream.toXML(musicMessage);
	// }
	//
	// /**
	// * 图文消息对象转换成xml
	// *
	// * @param newsMessage 图文消息对象
	// * @return xml
	// */
	// public static String newsMessageToXml(NewsMessage newsMessage) {
	// xstream.alias("xml", newsMessage.getClass());
	// xstream.alias("item", new Article().getClass());
	// return xstream.toXML(newsMessage);
	// }

	/**
	 * 扩展xstream，使其支持CDATA块
	 * 
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
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

//		// xml请求解析
//		Map<String, String> requestMap = null;
//		try {
//			requestMap = parseXml(request);
//		} catch (Exception e) {
//			e.printStackTrace();
//			respContent = "响应出错，请稍后再试";
//			paramMap = null;
//		}
		System.out.println(requestMap);
		// 发送方账号（用户OpenId）
		String fromUserName = requestMap.get("FromUserName");
		System.out.println("fromUserName : " + fromUserName);
		// 开发方账号（微信公众号）
		String toUserName = requestMap.get("ToUserName");
		System.out.println("toUserName : " + toUserName);
		// 消息类型
		String msgType = requestMap.get("MsgType");
		System.out.println("msgType :" + msgType);
		// 企业应用ID
		String agentID = requestMap.get("AgentID");
		System.out.println("agentID :" + msgType);
		// if ("image".equals(msgType)) {
		// mediaId = requestMap.get("MediaId");
		// System.out.println("MediaId : " + mediaId);
		// }

		// if(null != paramMap.get("UPLOAD_TYPE")){
		//
		// }

		// 文本消息
		if (msgType.equals(MessageUtil.MESSAGE_TYPE_TEXT)) {
			respContent = "您发送的是文本ff消息！";
		}
		// 图片消息
		else if (msgType.equals(MessageUtil.MESSAGE_TYPE_IMAGE)) {
			respContent = "您发送的是图片消息！";
		}
		// 事件推送
		else if (msgType.equals(MessageUtil.MESSAGE_TYPE_EVENT)) {
			// 事件类型
			String eventType = requestMap.get("Event");
			System.out.println("eventType :" + eventType);
			// 订阅
			if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
				respContent = "感谢您的关注！";
			}
			// 自定义菜单点击事件
			else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
				// 暂未封装-------------------------------测试阶段
				String eventKey = requestMap.get("EventKey");
				switch (eventKey) {
				case "天气北京":
					System.out.println("天气北京");
					respContent = "点击了" + eventKey + ",但目前无法响应";
					break;
				default:
					respContent = "点击了" + eventKey;
				}
			}
		}
		// 测试回图片消息，以后可根据事件响应需要回图片消息
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
			// 回复文本消息
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
	 * 消息类型：文本
	 */
	public static final String MESSAGE_TYPE_TEXT = "text";

	/**
	 * 消息类型：图片
	 */
	public static final String MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 消息类型：图文
	 */
	public static final String MESSAGE_TYPE_NEWS = "news";

	/**
	 * 消息类型：推送
	 */
	public static final String MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(订阅)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
}
