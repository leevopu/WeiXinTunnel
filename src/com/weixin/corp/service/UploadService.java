package com.weixin.corp.service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.entity.message.json.CorpBaseJsonMessage;
import com.weixin.corp.utils.CommonUtil;
import com.weixin.corp.utils.WeixinUtil;

public class UploadService {
	private static Log log = LogFactory.getLog(UploadService.class);

	public boolean uploadWeixinByWebservice(RequestCall call) {

		byte[] imageByte = call.getMediaByte();
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				imageByte));
		try {
			in.readFully(imageByte);
			in.close();
			FileOutputStream fos = null;
			File mediaFile = new File("D:/temp/" + call.getMediaName());
			fos = new FileOutputStream(mediaFile);
			fos.write(imageByte, 0, imageByte.length);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String process(RequestCall call) {

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

		// �ж��Ƿ��ʽ����Ҫ���Ƿ���ȱʧ���ֶ�
		if (/* CommonUtil.StringisEmpty(call.getFromUser()) || */CommonUtil
				.StringisEmpty(call.getToUser())
				|| CommonUtil.StringisEmpty(call.getMsgType())
				|| (CommonUtil.StringisEmpty(call.getText()) && null == call
						.getMediaName())) {
			StringBuffer missFieldValue = new StringBuffer();
			missFieldValue.append("ȱ�ٱ�Ҫ����Ϣ���飬������:");
			missFieldValue.append(call.getFromUser());
			missFieldValue.append("��������:");
			missFieldValue
					.append(CommonUtil.StringisEmpty(call.getToUser()) ? "ȱʧ"
							: call.getToUser());
			missFieldValue.append("����Ϣ����:");
			missFieldValue
					.append(CommonUtil.StringisEmpty(call.getMsgType()) ? "ȱʧ"
							: call.getMsgType());
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
			return missFieldValue.toString();
		}
		// ���ͼƬ�ļ� ����ļ����������ѹ��
		if (MessageService.IMAGE_MSG_TYPE.equals(call.getMsgType())
				|| MessageService.MPNEWS_MSG_TYPE.equals(call.getMsgType())) {
			String[] imagType = { "jpg", "jepg", "png", "bmp", "gif" };
			List<String> imageTyepLists = Arrays.asList(imagType);
			String str = StringUtils.substringAfterLast(call.getMediaName(),
					".");
			if (!imageTyepLists.contains(str)) {
				return "�ϴ��ļ���ѡ���ز����Ͳ�ƥ��";
			}
			int width = 800;
			int height = 650;
			// ͼƬѹ��
			boolean flag = CommonUtil.compressPic(call.getMediaName(), height,
					width);
			if (!flag) {
				return "ͼƬѹ��ʧ�ܣ�����ͼƬ��С�����ͣ�";
			}
			System.out.println("ͼƬѹ����ɣ�");
		}

		// �������ʱ��ѡ�Ĳ��ԣ��ڵ�ǰϵͳʱ��2�����ڣ��Ǿ���գ�Ĭ�����̷��͡�
		if (!CommonUtil.StringisEmpty(call.getSendTime())
				&& CommonUtil.getStrDate(call.getSendTime(),
						"yyyy-MM-dd HH:mm:ss").before(
						new Date(System.currentTimeMillis() + 1000 * 60 * 2))) {
			call.setSendTime(null);
		}
		String msgType = call.getMsgType();
		// ��������ı������ϴ��زģ���ȡ�ز�id
		if (!MessageService.TEXT_MSG_TYPE.equals(msgType)) {
			JSONObject jsonObject = null;
			String mediaId = null;
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
								new Date(System.currentTimeMillis() + 1000 * 60
										* 60 * 24 * 3))) {
					// �����زĽӿ�
					jsonObject = MessageService.uploadPermanentMedia(call);
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
			// ������Ϣ���У���ʱ����
			WeixinUtil.getDelayJsonMessageQueue().offer(jsonMessage);
			return "������Ϣ���У��ȴ���ʱ����";
		}

	}

}
