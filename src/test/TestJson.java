package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

public class TestJson {

	/**
	 * ����ע����Ϣ����õĲ������ύget���󣬻��accessTkoen
	 * 
	 * @author lpe234
	 * @time 2014-5-21 00:52:15
	 */
	String appID = "wx168dbadda799a989";
	String appsecret = "0cb2de6f5e25137e0ffe03e32d05ff04";// ΢�ŷ���Ż�����������˺ŵĶ��ĺŲ��С�����
	String preUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	String tempUrl = String.format(preUrl, appID, appsecret);

	public static void main(String[] args) throws Exception {

		String keyWord = URLDecoder.decode("%E5%A4%B4%E5%83%8F", "UTF-8");

		System.out.println(keyWord);

		String urlStr = URLEncoder.encode("��֤�ʹܲ��Ժ�", "UTF-8");

		System.out.println(urlStr);

//		TestJson tj = new TestJson();
//		System.out.println(tj.getUserInfo("id"));
	}

	public String getUserInfo(String key) {
		User user = new User("baidu", "�ٶ�", "www.baidu.com");
		JSONObject jsonObject = JSONObject.fromObject(user);
		System.out.println(jsonObject.toString());
		return jsonObject.getString(key);
	}

	// ����String����access_token
	public String get() {
		String temp = null;
		temp = getJSON();
		JSONObject j = JSONObject.fromObject(temp);
		temp = j.getString("access_token");
		// System.out.println(temp);
		return temp;
	}

	// ��ȡwx����������JSON����,private�ڲ�����
	private String getJSON() {
		String temp = null;
		try {
			URL url = new URL(tempUrl);
			URLConnection conn = url.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			temp = br.readLine();
			System.out.println("readline : " + temp);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(temp);
		return temp;
	}
}