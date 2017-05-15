package com.weixin.corp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.WeixinUtil;

public class UploadService {
	private static Log log = LogFactory.getLog(UploadService.class);

	public static final String UPLOAD_TEMP_URL = "D:/temp/";

	public static String process(RequestCall call) {
		try{
		// 判断是否格式符合要求，是否有缺失的字段
		if (/* CommonUtil.StringisEmpty(call.getFromUser()) || */CommonUtil
				.StringisEmpty(call.getToUser())
				|| CommonUtil.StringisEmpty(call.getMsgType())
				|| (CommonUtil.StringisEmpty(call.getText()) && null == call
						.getMediaName())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("缺少必要的信息请检查，发送人:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("，接收人:");
			missFieldValue
					.append(CommonUtil.StringisEmpty(call.getToUser()) ? "缺失"
							: call.getToUser());
			missFieldValue.append("，消息类型:");
			missFieldValue
					.append(CommonUtil.StringisEmpty(call.getMsgType()) ? "缺失"
							: call.getMsgType());
			missFieldValue.append("，文本内容:");
			missFieldValue
					.append(CommonUtil.StringisEmpty(call.getText()) ? "无"
							: call.getText());
			missFieldValue.append("，素材文件:");
			missFieldValue.append(null != call.getMediaByte() ? "无"
					: call.getMediaName()
							.substring(
									call.getMediaName().lastIndexOf(
											File.separator) + 1));
			log.error(missFieldValue.toString());
			return missFieldValue.toString();
		}

		if (null != call.getMediaByte() && null != call.getMediaName()) {
			// 判断文件长度
			long contentLength = call.getMediaByte().length;
			System.out.println("lenth: " + contentLength);
			String size = CommonUtil.convertFileSize(contentLength);
			System.out.println("文件大小为：" + size);

			// =================================================================
			// 判断文件大小 超过20M返回提示
			// =================================================================
			if (contentLength > 20 * 1024 * 1024) {
				return "文件大小超过20M，请重新操作！！！！";
			}
			File uploadRootFolder = new File(UPLOAD_TEMP_URL);
			if (!uploadRootFolder.exists()) {
				uploadRootFolder.mkdir();
			}
			File uploadDailyFolder = new File(UPLOAD_TEMP_URL
					+ CommonUtil.getDateStr(new Date(), "yyyy-MM-dd"));
			if (!uploadDailyFolder.exists()) {
				uploadDailyFolder.mkdir();
			}
			File media = new File(uploadDailyFolder.getAbsolutePath()
					+ File.separator + call.getMediaName());
			try {
				// 创建输出流
				FileOutputStream outStream = new FileOutputStream(media);
				// 写入数据
				outStream.write(call.getMediaByte());
				// 关闭输出流
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return "二进制流转成素材文件失败";
			}

			// 针对图片文件 如果文件过大，则进行压缩
			if (MessageService.IMAGE_MSG_TYPE.equals(call.getMsgType())
					|| MessageService.MPNEWS_MSG_TYPE.equals(call.getMsgType())) {
				String[] imagType = { "jpg", "jepg", "png", "bmp", "gif" };
				List<String> imageTyepLists = Arrays.asList(imagType);
				String str = StringUtils.substringAfterLast(
						call.getMediaName(), ".");
				if (!imageTyepLists.contains(str)) {
					String msg = "上传文件与选择素材类型不匹配";
					System.out.println(msg);
					return msg;
				}
				//当图片大于2M的时候，对批片进行压缩
				if(contentLength > 2 * 1024 * 1024){
					int width = 800;
					int height = 650;
					// 图片压缩
					boolean flag = CommonUtil.compressPic(media, height, width);
					if (!flag) {
						return "图片压缩失败，请检查图片大小及类型！";
					}
					// 压缩后的流传回call
					call.setMediaByte(FileUtils.readFileToByteArray(media));
					System.out.println("图片压缩完成！");
				}
			}
		}

		// 如果发送时间选的不对，在当前系统时间2分钟内，那就清空，默认立刻发送。
		if (!CommonUtil.StringisEmpty(call.getSendTime())
				&& CommonUtil.getStrDate(call.getSendTime(),
						"yyyy-MM-dd HH:mm:ss").before(
						new Date(System.currentTimeMillis() + 1000 * 60 * 2))) {
			call.setSendTime(null);
		}
		
		String msgType = call.getMsgType();
		// 如果不是文本，先上传素材，获取素材id
		if (!MessageService.TEXT_MSG_TYPE.equals(msgType)) {
			JSONObject jsonObject = null;
			String mediaId = null;
			if(MessageService.MPNEWS_MSG_TYPE.equals(msgType)){//图文消息
				// 永久素材接口
				jsonObject = MessageService.uploadPermanentMedia(call);
				if (null != jsonObject && jsonObject.has("media_id")) {
					mediaId = jsonObject.getString("media_id");
					call.setMediaId(mediaId);
				} else {
					return "上传素材失败，请检查文件是否符合要求";
				}
			}else
			// 无接收人则素材入库--去掉，入库必须选择图文，只有图文入永久库等待调用
			if (CommonUtil.StringisEmpty(call.getToUser())) {
				// 永久素材接口？因网页的公共素材库无法看到接口上传的，上传后如何使用？
				
				return "??";
			}
			// 如果有发送时间且发送时间超过系统时间3天，因为临时素材只能保留3天，如果超过3天，则上传永久素材
			else {
				if (!CommonUtil.StringisEmpty(call.getSendTime())
						&& CommonUtil.getStrDate(call.getSendTime(),
								"yyyy-MM-dd HH:mm:ss").after(
								new Date(System.currentTimeMillis() + 1000 * 60
										* 60 * 24 * 3))) {
					// 永久素材接口
					jsonObject = MessageService.uploadPermanentMedia(call);
				}else {
					// 临时素材接口
					jsonObject = MessageService.uploadTempMedia(call);
				}
				if (null != jsonObject && jsonObject.has("media_id")) {
					mediaId = jsonObject.getString("media_id");
					call.setMediaId(mediaId);
				} else {
					return "上传素材失败，请检查文件是否符合要求";
				}
			}
		}
		CorpBaseJsonMessage jsonMessage = MessageService
				.changeMessageToJson(call);
		// 立即发送消息
		if (CommonUtil.StringisEmpty(call.getSendTime())) {
			if (0 == MessageService.sendMessage(jsonMessage)) {
				// 回复提示发送成功
				return "发送成功";
			} else {
				// 回复发送失败 -1？？某个错误值，配个常亮映射显示
				return "发送失败";
			}
		} else {
			// 放入消息队列，定时触发
			WeixinUtil.getDelayJsonMessageQueue().offer(jsonMessage);
			return "放入消息队列，等待定时触发";
		}
		}catch(Exception e){
			e.printStackTrace();
			return "解析请求失败";
		}
	}
}
