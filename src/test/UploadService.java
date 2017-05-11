package test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.utils.WeixinUtil;

public class UploadService {
	private static Log log = LogFactory.getLog(UploadService.class);

	public boolean uploadImageWithByte(byte[] imageByte, String str1) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				imageByte));
		try {
			in.readFully(imageByte);
			in.close();
			FileOutputStream fos = null;

			fos = new FileOutputStream("D:/temp/" + str1);
			fos.write(imageByte, 0, imageByte.length);
			fos.close();
			log.info("123");
		} catch (Exception e) {

		}
		System.out.println("str1: " + str1);
		return true;
	}
	
	public boolean uploadWeixin(RequestCall call) {
		System.out.println("aaa");
		System.out.println(WeixinUtil.getAeskey());
		
		byte[] imageByte = call.getFileByte();
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				imageByte));
		try {
			in.readFully(imageByte);
			in.close();
			FileOutputStream fos = null;

			fos = new FileOutputStream("D:/temp/" + call.getMsgType());
			fos.write(imageByte, 0, imageByte.length);
			fos.close();
		} catch (Exception e) {

		}
		System.out.println("str1: " + call.getMsgType());
		return true;
	}

}
