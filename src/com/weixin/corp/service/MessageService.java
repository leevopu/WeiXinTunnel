package com.weixin.corp.service;

import java.io.InputStream;
import java.io.Writer;
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
import com.weixin.corp.constant.ErrorCode;
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
	public static String textMessageToXml(CorpBaseXMLMessage message) {
		xstream.alias("xml", message.getClass());
		if (message instanceof NewsXMLMessage) {
			xstream.alias("item", new Article().getClass());
		}
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
	public static String processRequest(Map<String, String> requestMap) {
		TextXMLMessage defaultMessage = null;
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
		if (MESSAGE_TYPE_TEXT == msgType) {
			respContent = "您发送的是文本消息！";
		}
		// 图片消息
		else if (MESSAGE_TYPE_IMAGE == msgType) {
			respContent = "您发送的是图片消息！";
		}
		// 事件推送
		else if (MESSAGE_TYPE_EVENT == msgType) {
			// 事件类型
			String eventType = requestMap.get("Event");
			System.out.println("eventType :" + eventType);
			// 订阅
			if (EVENT_TYPE_SUBSCRIBE == msgType) {
				respContent = "感谢您的关注！";
			}
			// 自定义菜单点击事件
			else if (EVENT_TYPE_CLICK == msgType) {
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

	public static int sendMessage(CorpBaseJsonMessage jsonMessage) {
		JSONObject outputStr = JSONObject.fromObject(jsonMessage);
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		JSONObject jsonObject = WeixinUtil.httpsRequest(MESSAGE_SEND,
				WeixinUtil.POST_REQUEST_METHOD,
				outputStr.toString().replace("mediaId", "media_id"));
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("群发消息出错 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
				return jsonObject.getInt("errcode");
			}
			if (jsonObject.has("invaliduser")
					&& !"".equals(jsonObject.getString("invaliduser"))) {
				log.error("丢失接收人:" + jsonObject.getString("invaliduser")
						+ "，请确认用户更新情况");
				return ErrorCode.MESSAGE_LOST_USER;
			}
			return 0;
		}
		return ErrorCode.MESSAGE_NO_RETURN;
	}
	
	public static JSONObject uploadMPNews(RequestCall call) {
		//生成图文消息体
		MpNewsJsonMessage jsonMessage = 
		 new MpNewsJsonMessage(call.getTitle(),call.getMediaId(),call.getText(),call.getDigest());
		
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MessageService.MPNEWS_UPLOAD.replace("TYPE",MPNEWS_MSG_TYPE), WeixinUtil.POST_REQUEST_METHOD,
				JSONObject.fromObject(jsonMessage).toString().replace("mediaId", "media_id"));
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("请求图文永久素材上传接口失败 errcode:"
						+ jsonObject.getInt("errcode") + "，errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}
	
	public static JSONObject uploadPermanentMedia(RequestCall call) {
		String msgType = call.getMsgType();
		//如果是图文类型素材，修改以图片类型上传
		if(MPNEWS_MSG_TYPE.equals(msgType)){
			msgType=IMAGE_MSG_TYPE;
		}
		JSONObject jsonObject = WeixinUtil.httpsRequestMedia(
				MessageService.MEDIA_PERMANENT_UPLOAD.replace("TYPE", msgType),
				WeixinUtil.POST_REQUEST_METHOD, call);
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("请求永久素材上传接口失败 errcode:"
						+ jsonObject.getInt("errcode") + "，errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	public static JSONObject uploadTempMedia(RequestCall call) {
		String msgType = call.getMsgType();
		JSONObject jsonObject = WeixinUtil.httpsRequestMedia(
				MessageService.MEDIA_TEMP_UPLOAD.replace("TYPE", msgType),
				WeixinUtil.POST_REQUEST_METHOD, call);
		if (null != jsonObject) {
			if (jsonObject.has("errcode") && 0 != jsonObject.getInt("errcode")) {
				log.error("请求临时素材上传接口失败 errcode:"
						+ jsonObject.getInt("errcode") + "，errmsg:"
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
				log.error("获取永久素材列表接口失败 errcode:"
						+ jsonObject.getInt("errcode") + "，errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	public static int deletePermanentMedia(String media_id) {
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				MEDIA_PERMANENT_DELETE.replace("MEDIA_ID", media_id),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("群发消息出错 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
				return jsonObject.getInt("errcode");
			}
			return 0;
		}
		return ErrorCode.MESSAGE_NO_RETURN;
	}

	/**
	 * 
	 * @return 成功为0，失败则为errcode
	 */
	public static int groupMessage() {
		String todayStr = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		int result = 0;
		Set<RequestCall> successMessages = new HashSet<RequestCall>();

		for (RequestCall call : WeixinUtil.getGroupMessagePool().get(todayStr)) {
			CorpBaseJsonMessage jsonMessage = changeMessageToJson(call);
			if (0 == sendMessage(jsonMessage)) {
				successMessages.add(call);
			}
			// else 失败？？
			try {
				// 间隔发送，降低调用微信服务器压力
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
			}
		}
		// 移除成功发送的消息
		WeixinUtil.getGroupMessagePool().get(todayStr)
				.removeAll(successMessages);
		log.info("日期：" + todayStr + "，群发消息完成");
		return result;
	}

	public static int warnFailureMessage() {
		String todayStr = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		int result = 0;
		for (RequestCall call : WeixinUtil.getGroupMessagePool().get(todayStr)) {
			call.setToUser("管理员");
			CorpBaseJsonMessage jsonMessage = changeMessageToJson(call);
			sendMessage(jsonMessage);
			// JSONObject jsonObject = WeixinUtil.httpsRequest(MESSAGE_SEND,
			// WeixinUtil.POST_REQUEST_METHOD,
			// JSONObject.fromObject(jsonMessage).toString());
			// if (null != jsonObject) {
			// if (0 != jsonObject.getInt("errcode")) {
			// result = jsonObject.getInt("errcode");
			// log.error("警告消息出错 errcode:" + jsonObject.getInt("errcode")
			// + "，errmsg:" + jsonObject.getString("errmsg"));
			// }
			// }
			// try {
			// // 间隔发送，降低压力
			// Thread.sleep(2 * 1000);
			// } catch (InterruptedException e) {
			// }
		}
		WeixinUtil.getGroupMessagePool().get(todayStr).clear();
		log.info("警告消息发送完成");
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
				// 说明是上传到永久库的素材消息
				jsonMessage.setPermanent(true);
			}
		}
		jsonMessage.setAgentid(WeixinUtil.getAgentid());
		// 转换
		// 转换toUser逗号或竖线分割的列表成userid竖线分割的列表
		// jsonMessage.setTouser(call.getToUser());
		String userId = convert(call.getToUser());
		jsonMessage.setTouser(userId);
		// jsonMessage.setTouser("leevo_pu");
		return jsonMessage;
	}

	/**
	 * 将传入的toUser与微信端信息匹配 转化为userID
	 * 
	 * @param str
	 * @return
	 */
	private static String convert(String toUser) {
		String userIds = "";
		Map<String, HashMap<String, User>> maps = WeixinUtil.getUseridPool();
		System.out.println(maps.keySet().toString());
		Object[] strs = maps.keySet().toArray();
		if (toUser.indexOf(",") != -1) {// 根据 "," 来进行分割
			userIds = splitToUser(toUser, userIds, maps, strs, ",");
		} else if (toUser.indexOf("|") != -1) {// 根据 "|" 来进行分割
			userIds = splitToUser(toUser, userIds, maps, strs, "\\|");
		} else if (toUser.length() > 11) {// 只有一个单独的字符串,进行分割
			String ph = toUser.substring(toUser.length() - 11, toUser.length());
			if (isNum(ph)) {
				String dep = toUser.substring(0, toUser.length() - 11);
				for (int j = 0; j < strs.length; j++) {
					// 部门匹配
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
			// 处理字符串最后一位"|"
			String s = userIds
					.substring(userIds.length() - 1, userIds.length());
			if ("|".equals(s)) {// 去除最后一个"|"
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
				// 取最后11位 如果没填手机号则不做处理
				ph = user.substring(user.length() - 11, user.length());
				String dep = "";
				// 判断是否全为数字 flag： TRUE FALSE
				if (isNum(ph)) {
					// 拿到部门名称
					dep = user.substring(0, user.length() - 11);
					// 遍历部门名称，匹配信息
					for (int j = 0; j < strs.length; j++) {
						// 部门匹配 拼接userId
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
		// 正则判断
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(ph);
		if (!isNum.matches()) {
			// 不全是数字，可能没有填手机号
			return false;
		}
		return true;
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
	 * 消息类型：图文11
	 */
	public static final String MESSAGE_TYPE_MPNEWS = "mpnews";

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
