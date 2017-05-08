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
			respContent = "您发送的是文本ff消息！";
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
				log.error("群发消息出错 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
				return false;
			}
			if (jsonObject.has("invaliduser")
					&& !"".equals(jsonObject.getString("invaliduser"))) {
				log.error("丢失接收人:" + jsonObject.getString("invaliduser")
						+ "，请确认用户更新情况");
				return false;
			}
		}
		return true;
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
			if (sendMessage(jsonMessage)) {
				successMessages.add(call);
			}
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
		// 转换
		// 转换toUser逗号或竖线分割的列表成userid竖线分割的列表
		// jsonMessage.setTouser(call.getToUser());
		// String userId = convert(call.getToUser());
		// jsonMessage.setTouser(call.getToUser());
		jsonMessage.setTouser("leevo_pu");
		return jsonMessage;
	}

	/**
	 * 将传入的toUser与微信端信息匹配 转化为userID
	 * 
	 * @param str
	 * @return
	 */
	public static String convert(String toUser) {
		if ("".equals(toUser) || null == toUser) {// touser为空
			log.error("toUser为空");
			return null;
		}
		String userIds = "";
		//从微信端取通讯录数据  :问题：根据部门id从微信端取人员详情  不成功  url正常
		String depts[] = new String[] {};
		if (toUser.indexOf("|") != -1) {// 根据 "," "|"来进行分割
			// System.out.println("包含");
			String users[] = toUser.split("|");
			for (int i = 0; i < users.length; i++) {
				String user = users[i];
				String dep = "";
				String ph = "";
				if (user.length() > 11) {
					// 取最后11位
					ph = user.substring(user.length() - 11, user.length());
					// 判断是否全为数字 flag： TRUE FALSE
					boolean flag = isNum(ph);
					if (flag) {
						dep = user.substring(0, user.length() - 11);
					} else {
						dep = user;
					}
				} else {// 肯定没有填手机号
					dep = user;
				}
			}
		} else {// 只有一位,进行分割
				// ph = toUser.substring(toUser.length()-11, toUser.length());
		}
		// 从微信端取通讯录数据 :问题：根据部门id从微信端取人员详情 不成功 url正常
		DailyUpdateUserTimerTask x = new DailyUpdateUserTimerTask();
		x.run();
		Map<String, HashMap<String, User>> maps = WeixinUtil.getUseridPool();
		System.out.println(maps.keySet().toString());
		Object[] strs = maps.keySet().toArray();
		if(toUser.indexOf(",")!=-1){//  根据 ","  来进行分割 
			//System.out.println("包含");
			String users[] = toUser.split(",");
			for (int i = 0; i < users.length; i++) {
				String user = users[i];
				String ph   = "";
				if(user.length()>11){
					//取最后11位   如果没填手机号则不做处理
					ph = user.substring(user.length()-11, user.length());
					String dep  = "";
					//判断是否全为数字  flag： TRUE FALSE
					if(isNum(ph)){
						//拿到部门名称
						dep = user.substring(0,user.length()-11); 
						//遍历部门名称，匹配信息
						for (int j = 0; j < strs.length; j++) {
							//部门匹配   
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
		}else{//只有一个单独的字符串,进行分割
			String ph   = toUser.substring(toUser.length()-11, toUser.length());
			if(isNum(ph)){
				String dep = toUser.substring(0,toUser.length()-11);
				for (int j = 0; j < strs.length; j++) {
					//部门匹配   
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
		//处理字符串最后一位"|"
		String s = userIds.substring(userIds.length()-1, userIds.length());
		if("|".equals(s)){
			userIds = userIds.substring(0, userIds.length()-1);
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

	/**
	 * 模拟上层应用调用请求
	 */
	public String testUploadToServer(String requestUrl, RequestCall call) {
		String result = null;
		String msgType = call.getMsgType();
		if (TEXT_MSG_TYPE != msgType && IMAGE_MSG_TYPE != msgType
				&& VIDEO_MSG_TYPE != msgType && FILE_MSG_TYPE != msgType) {
			return "发送的消息类型不正确，只允许text,image,video和file";
		}
		URL url = null;
		HttpURLConnection con = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String BOUNDARY = "---------------------------"
					+ System.currentTimeMillis();
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			final String newLine = "\r\n";

			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());

			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append(newLine);

			// 添加消息类型
			sb.append("Content-Disposition: form-data;name=\"msgType\"");
			sb.append(newLine);
			sb.append(newLine);
			sb.append(msgType);
			sb.append(newLine);
			sb.append("Content-Type:application/octet-stream");
			sb.append(newLine);
			sb.append(newLine);

			// 添加发送人
			sb.append("--"); // 必须多两道线
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

			// 添加接收人
			sb.append("--"); // 必须多两道线
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

			// 添加时间
			sb.append("--"); // 必须多两道线
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

			// 添加消息内容（文本或文件）
			sb.append("--"); // 必须多两道线
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
					return "选择的消息文件不存在";
				}
				sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
						+ media.getName() + "\"");
				sb.append(newLine);
				sb.append("Content-Type:application/octet-stream");
				sb.append(newLine);
				sb.append(newLine);
				out.write(sb.toString().getBytes("utf-8"));
				// 文件正文部分
				// 把文件已流文件的方式 推入到url中
				DataInputStream in = new DataInputStream(new FileInputStream(
						media));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
				out.write(newLine.getBytes());
				// 结尾部分
				byte[] foot = ("--" + BOUNDARY + "--").getBytes("utf-8");
				// 定义最后数据分隔线
				out.write(foot);
				out.write(newLine.getBytes());
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用消息接口请求失败");
			return "调用消息接口请求失败";
		}

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
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
			log.error("获取POST请求响应出现异常！" + e);
			e.printStackTrace();
			return "获取POST请求响应出现异常！";
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
