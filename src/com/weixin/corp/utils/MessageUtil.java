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
 * 消息工具类
 */
public class MessageUtil {
	private static Log log = LogFactory.getLog(MessageUtil.class);
	
	public static String GROUP_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

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
		for (Element e : elementList) {
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

	/**
	 * 
	 * @param requestMap
	 * @param paramMap
	 * @return
	 */
	public static String processRequest(Map<String, String> requestMap,
			Map<String, String> paramMap) {
		BaseMessage baseMessage = null;
		String responseMsg = null;
		String respContent = "";

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

		responseMsg = textMessageToXml(baseMessage);
		return responseMsg;
	}

	public static int groupMessage() {
		int result = 0;
		Set<Data> datas = WeixinUtil.getDatas();
		Set<Data> successDatas = new HashSet<Data>();
		for (Data data : datas) {
			TextMessage tm = changeDataToTm(data);
			JSONObject jsonObject = WeixinUtil.httpsRequest(GROUP_MESSAGE_URL, "POST", JSONObject.fromObject(tm).toString());
			if (null != jsonObject) {
				if (0 != jsonObject.getInt("errcode")) {
					result = jsonObject.getInt("errcode");
					log.error("群发消息出错 errcode:" + jsonObject.getInt("errcode")
							+ "，errmsg:" + jsonObject.getString("errmsg") + "，invaliduser:" + jsonObject.getString("invaliduser"));
				}
				else {
					successDatas.add(data);
				}
			}
			try {
				// 间隔发送，降低压力
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
			}
		}
		// 移除成功发送的消息
		datas.removeAll(successDatas);
		log.info("群发消息完成");
		return result;
	}
	
	public static int warnFailureMessage() {
		int result = 0;
		Set<Data> datas = WeixinUtil.getDatas();
		for (Data data : datas) {
			TextMessage tm = changeDataToTm(data);
			tm.setTouser("管理员");
			JSONObject jsonObject = WeixinUtil.httpsRequest(GROUP_MESSAGE_URL, "POST", JSONObject.fromObject(tm).toString());
			if (null != jsonObject) {
				if (0 != jsonObject.getInt("errcode")) {
					result = jsonObject.getInt("errcode");
					log.error("警告消息出错 errcode:" + jsonObject.getInt("errcode")
							+ "，errmsg:" + jsonObject.getString("errmsg"));
				}
			}
			try {
				// 间隔发送，降低压力
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
			}
		}
		log.info("警告消息发送完成");
		return result;
	}

	private static TextMessage changeDataToTm(Data data) {
		TextMessage tm = new TextMessage();
		Text text = new Text();
		tm.setText(text);
		text.setContent(data.getContext());
		tm.setAgentid(WeixinUtil.getAgentid());
		tm.setMsgtype("text");
		tm.setTouser(data.getTouser());
		return tm;
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
