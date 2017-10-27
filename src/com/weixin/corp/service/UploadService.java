package com.weixin.corp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.WeixinUtil;

public class UploadService {
	private static Log log = LogFactory.getLog(UploadService.class);

	public static String UPLOAD_TEMP_URL = UploadService.class.getResource("/")
			.getPath().substring(0, 4)
			+ "temp/";
	
	public static String getTest() {
		return "abc";
	}

	public static String process(RequestCall call) {
		try {
			if(null != call.getMediaBase64()){
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] base64byte = decoder.decodeBuffer(call.getMediaBase64());
				call.setMediaByte(base64byte);
			}
			// �ж��Ƿ��ʽ����Ҫ���Ƿ���ȱʧ���ֶ�
			if (/* CommonUtil.StringisEmpty(call.getFromUser()) || */CommonUtil
					.StringisEmpty(call.getToUser())
					|| CommonUtil.StringisEmpty(call.getMsgType())
					|| (CommonUtil.StringisEmpty(call.getText()) && null == call
							.getMediaName())) {
				StringBuffer missFieldValue = new StringBuffer();
				missFieldValue.append("ȱ�ٱ�Ҫ����Ϣ����");
				missFieldValue.append("��������:");
				missFieldValue
						.append(CommonUtil.StringisEmpty(call.getToUser()) ? "ȱʧ"
								: call.getToUser());
				missFieldValue.append("����Ϣ����:");
				missFieldValue.append(CommonUtil.StringisEmpty(call
						.getMsgType()) ? "ȱʧ" : call.getMsgType());
				missFieldValue.append("���ı�����:");
				missFieldValue
						.append(CommonUtil.StringisEmpty(call.getText()) ? "��"
								: call.getText());
				missFieldValue.append("���ز��ļ�:");
				missFieldValue.append(null != call.getMediaByte() ? "��"
						: call.getMediaName()
								.substring(
										call.getMediaName().lastIndexOf(
												File.separator) + 1));
				log.error(missFieldValue.toString());
				return missFieldValue.toString();
			}
			
			if(MessageService.MPNEWS_MSG_TYPE.equals(call
					.getMsgType())){
				if (CommonUtil.StringisEmpty(call.getTitle())
						|| CommonUtil.StringisEmpty(call.getText())
						|| CommonUtil.StringisEmpty(call.getMediaName())) {
					log.error("ͼ����Ϣ����ʱ�����⡢�ı����ļ��زı���!");
					return "ͼ����Ϣ����ʱ�����⡢�ı����ļ��زı���!";
				}
			}

			if (null != call.getMediaByte() && null != call.getMediaName()) {
				// �ж��ļ�����
				long contentLength = call.getMediaByte().length;
				System.out.println("lenth: " + contentLength);
				String size = CommonUtil.convertFileSize(contentLength);
				System.out.println("�ļ���СΪ��" + size);

				// =================================================================
				// �ж��ļ���С ����20M������ʾ
				// =================================================================
				if (contentLength > 20 * 1024 * 1024) {
					return "�ļ���С����20M�������²�����������";
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
					// ���������
					FileOutputStream outStream = new FileOutputStream(media);
					// д������
					outStream.write(call.getMediaByte());
					// �ر������
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return "��������ת���ز��ļ�ʧ��";
				}
				// ���ͼƬ�ļ� ����ļ����������ѹ��
				if (MessageService.IMAGE_MSG_TYPE.equals(call.getMsgType())
						|| MessageService.MPNEWS_MSG_TYPE.equals(call
								.getMsgType())) {
					String[] imagType = { ".jpg", ".jepg", ".png", ".bmp",
							".gif" };
					List<String> imageTyepLists = Arrays.asList(imagType);
					String suffix = call.getMediaName().substring(
							call.getMediaName().lastIndexOf("."));
					if (!imageTyepLists.contains(suffix)) {
						String msg = "�ϴ��ļ���ѡ���ز����Ͳ�ƥ��";
						System.out.println(msg);
						return msg;
					}
					// ��ͼƬ����2M��ʱ�򣬶���Ƭ����ѹ��
					if (contentLength > 2 * 1024 * 1024) {
						int width = 640;
						int height = 380;
						// ͼƬѹ��
						boolean flag = CommonUtil.compressPic(media, height,
								width);
						if (!flag) {
							return "ͼƬѹ��ʧ�ܣ�����ͼƬ��С�����ͣ�";
						}
						// ѹ�����������call
						call.setMediaByte(FileUtils.readFileToByteArray(media));
						System.out.println("ͼƬѹ����ɣ�");
					}
				}
				if (MessageService.FILE_MSG_TYPE.equals(call.getMsgType())){
					System.out.println("==============fit==============");
					CommonUtil.selfAdapt(media);
				}
			}


			if (!CommonUtil.StringisEmpty(call.getSendTime())) {
				// yyyy-MM-dd ͳһת�� yyyy-MM-dd HH:mm:ss �������
				if (call.getSendTime().length() == 10) {
					call.setSendTime(call.getSendTime() + " 00:00:00");
				}
				// �������ʱ��ѡ�Ĳ��ԣ��ڵ�ǰϵͳʱ��2�����ڣ��Ǿ���գ�Ĭ�����̷��͡�
				if (CommonUtil.getStrDate(call.getSendTime(),
						"yyyy-MM-dd HH:mm:ss").before(
						new Date(System.currentTimeMillis() + 1000 * 60 * 2))) {
					call.setSendTime(null);
				}
			}

			String msgType = call.getMsgType();
			// ��������ı������ϴ��زģ���ȡ�ز�id
			if (!MessageService.TEXT_MSG_TYPE.equals(msgType)) {
				JSONObject jsonObject = null;
				String mediaId = null;
				if (MessageService.MPNEWS_MSG_TYPE.equals(msgType)) {// ͼ����Ϣ
					// �����زĽӿ�
					jsonObject = MessageService.uploadTempMedia(call);
					if (null != jsonObject && jsonObject.has("media_id")) {
						mediaId = jsonObject.getString("media_id");
						call.setMediaId(mediaId);
					} else {
						return "�ϴ��ز�ʧ�ܣ������ļ��Ƿ����Ҫ��";
					}
				} else
				// �޽��������ز����--ȥ����������ѡ��ͼ�ģ�ֻ��ͼ�������ÿ�ȴ�����
				if (CommonUtil.StringisEmpty(call.getToUser())) {
					// �����زĽӿڣ�����ҳ�Ĺ����زĿ��޷������ӿ��ϴ��ģ��ϴ������ʹ�ã�

					return "??";
				}
				// ����з���ʱ���ҷ���ʱ�䳬��ϵͳʱ��3�죬��Ϊ��ʱ�ز�ֻ�ܱ���3�죬�������3�죬���ϴ������ز�
				else {
					if (!CommonUtil.StringisEmpty(call.getSendTime())
							&& CommonUtil.getStrDate(call.getSendTime(),
									"yyyy-MM-dd HH:mm:ss").after(
									new Date(System.currentTimeMillis() + 1000
											* 60 * 60 * 24 * 3))) {
						// �����زĽӿ�
						/*
						        ���ýӿ��ѱ�΢�Źر�
						jsonObject = MessageService.uploadPermanentMedia(call);
						 * ������Ҫ�ɿ��ǽ��ļ��ȷ�������Ŀ¼��Ȼ��ȵ���ʱ���д���������װ��У�飬����
						 */
					} else {
						// ��ʱ�زĽӿ�
						jsonObject = MessageService.uploadTempMedia(call);
					}
					if (null != jsonObject && jsonObject.has("media_id")) {
						mediaId = jsonObject.getString("media_id");
						call.setMediaId(mediaId);
					} else {
						return "�ϴ��ز�ʧ�ܣ������ļ��Ƿ����Ҫ��";
					}
				}
			}
			CorpBaseJsonMessage jsonMessage = MessageService
					.changeMessageToJson(call);
			// ����������Ϣ
			if (CommonUtil.StringisEmpty(call.getSendTime())) {
				if (0 == MessageService.sendMessage(jsonMessage)) {
					// �ظ���ʾ���ͳɹ�
					return "���ͳɹ�";
				} else {
					// �ظ�����ʧ�� -1����ĳ������ֵ���������ӳ����ʾ
					return "����ʧ��";
				}
			} else {
				// ������Ϣ���У���ʱ���� 3������Ч
				WeixinUtil.getDelayJsonMessageQueue().offer(jsonMessage);
				return "������Ϣ���У��ȴ���ʱ����";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "��������ʧ��";
		}
	}
}
