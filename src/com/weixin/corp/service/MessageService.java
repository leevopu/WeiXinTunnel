package com.weixin.corp.service;

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
		// String mediaId =
		// "2U3efl32gH-nXPgi30kLBdVjbI5IuwrizqblJift-Okdkpw3AT1FJi779H0HFOEnM0bcZv_qEadmPvyw5fkJDOg";
		// ImageXMLMessage x = new ImageXMLMessage(mediaId);
		// x.setAgentID(9);
		// x.setCreateTime(new Date().getTime());
		// x.setFromUserName("wx522a5f82e335b883");
		// x.setToUserName("leevo_pu");
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
				MessageService.MEDIA_PERMANENT_UPLOAD.replace("TYPE",
						msgType), WeixinUtil.POST_REQUEST_METHOD,
				call);
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
		String userId = CommonUtil.convert(call.getToUser());
		jsonMessage.setTouser(userId);
		// jsonMessage.setTouser("leevo_pu");
		return jsonMessage;
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

	// /**
	// * 模拟上层应用Servlet调用请求
	// */
	// public static String testUploadToServer(String requestUrl, RequestCall
	// call) {
	// String result = null;
	// String msgType = call.getMsgType();
	// switch (msgType) {
	// case TEXT_MSG_TYPE:
	// System.out.println("123");
	// case IMAGE_MSG_TYPE:
	// System.out.println(234);
	// case VIDEO_MSG_TYPE:
	// System.out.println(345);
	// case FILE_MSG_TYPE:
	// break;
	// default:
	// return "发送的消息类型不正确，只允许text,image,video和file";
	// }
	// HttpURLConnection httpUrlConn = null;
	// try {
	// // TrustManager[] tm = { new MyX509TrustManager() };
	// // SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
	// // sslContext.init(null, tm, new java.security.SecureRandom());
	// // // 从上述SSLContext对象中得到SSLSocketFactory对象
	// // SSLSocketFactory ssf = sslContext.getSocketFactory();
	//
	// URL url = new URL(requestUrl);
	// httpUrlConn = (HttpURLConnection) url
	// .openConnection();
	// // httpUrlConn.setSSLSocketFactory(ssf);
	// httpUrlConn = (HttpURLConnection) url.openConnection();
	// httpUrlConn.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
	// httpUrlConn.setDoInput(true);
	// httpUrlConn.setDoOutput(true);
	// httpUrlConn.setUseCaches(false); // post方式不能使用缓存
	// // 设置请求头信息
	// httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
	// httpUrlConn.setRequestProperty("Charset", "UTF-8");
	// // 设置边界
	// String BOUNDARY = "---------------------------"
	// + System.currentTimeMillis();
	// httpUrlConn.setRequestProperty("Content-Type",
	// "multipart/form-data; boundary=" + BOUNDARY);
	// final String newLine = "\r\n";
	//
	// // 获得输出流
	// OutputStream out = new DataOutputStream(httpUrlConn.getOutputStream());
	//
	// // 请求正文信息
	// // 第一部分：
	// StringBuilder sb = new StringBuilder();
	// sb.append("--"); // 必须多两道线
	// sb.append(BOUNDARY);
	// sb.append(newLine);
	//
	// // 添加消息类型
	// sb.append("Content-Disposition: form-data;name=\"msgType\"");
	// sb.append(newLine);
	// sb.append(newLine);
	// sb.append(msgType);
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	//
	// if(null != call.getFromUser()){
	// // 添加发送人
	// sb.append("--"); // 必须多两道线
	// sb.append(BOUNDARY);
	// sb.append(newLine);
	// sb.append("Content-Disposition: form-data;name=\"fromUser\"");
	// sb.append(newLine);
	// sb.append(newLine);
	// sb.append(call.getFromUser());
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	// }
	//
	// // 添加接收人
	// sb.append("--"); // 必须多两道线
	// sb.append(BOUNDARY);
	// sb.append(newLine);
	// sb.append("Content-Disposition: form-data;name=\"toUser\"");
	// sb.append(newLine);
	// sb.append(newLine);
	// sb.append(call.getToUser());
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	//
	// if(null != call.getSendTime()){
	// // 添加时间
	// sb.append("--"); // 必须多两道线
	// sb.append(BOUNDARY);
	// sb.append(newLine);
	// sb.append("Content-Disposition: form-data;name=\"sendTime\"");
	// sb.append(newLine);
	// sb.append(newLine);
	// sb.append(call.getSendTime());
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	// }
	//
	// // 添加消息内容（文本或文件）
	// sb.append("--"); // 必须多两道线
	// sb.append(BOUNDARY);
	// sb.append(newLine);
	// if (TEXT_MSG_TYPE.equals(msgType)) {
	// sb.append("Content-Disposition: form-data;name=\"text\"");
	// sb.append(newLine);
	// sb.append(newLine);
	// sb.append(call.getText());
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	// out.write(sb.toString().getBytes("utf-8"));
	// } else {
	// File media = call.getMedia();
	// if (!media.exists()) {
	// return "选择的消息文件不存在";
	// }
	// sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
	// + media.getName() + "\"");
	// sb.append(newLine);
	// sb.append("Content-Type:application/octet-stream");
	// sb.append(newLine);
	// sb.append(newLine);
	// out.write(sb.toString().getBytes("utf-8"));
	// // // 文件正文部分
	// // // 把文件已流文件的方式 推入到url中
	// // DataInputStream in = new DataInputStream(new FileInputStream(
	// // media));
	// // int bytes = 0;
	// // byte[] bufferOut = new byte[1024];
	// // while ((bytes = in.read(bufferOut)) != -1) {
	// // out.write(bufferOut, 0, bytes);
	// // }
	// // in.close();
	// out.write(newLine.getBytes());
	// // 结尾部分
	// byte[] foot = ("--" + BOUNDARY + "--").getBytes("utf-8");
	// // 定义最后数据分隔线
	// out.write(foot);
	// out.write(newLine.getBytes());
	// out.flush();
	// }
	// out.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error("调用消息接口请求失败");
	// return "调用消息接口请求失败";
	// }
	//
	// StringBuffer buffer = new StringBuffer();
	// BufferedReader reader = null;
	// try {
	// // 定义BufferedReader输入流来读取URL的响应
	// reader = new BufferedReader(new InputStreamReader(
	// httpUrlConn.getInputStream()));
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// buffer.append(line);
	// }
	// if (result == null) {
	// result = buffer.toString();
	// }
	// } catch (IOException e) {
	// log.error("获取POST请求响应出现异常！" + e);
	// e.printStackTrace();
	// return "获取POST请求响应出现异常！";
	// } finally {
	// if (reader != null) {
	// try {
	// reader.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return result;
	// }

	public static void main(String[] args) throws Exception {
		RequestCall call = new RequestCall();
		call.setMsgType("image");
		call.setToUser("888");
		call.setMediaName("C:/Users/Administrator/Desktop/abc.png");
		// MessageService.testUploadToServer("http://localhost/WeixinTest3/uploadServlet",
		// call);
	}

}
