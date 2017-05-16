package com.weixin.corp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.user.Department;
import com.weixin.corp.entity.user.User;
import com.weixin.corp.utils.WeixinUtil;

public class UserService {

	private static Log log = LogFactory.getLog(UserService.class);

	/**
	 * �û�������POST��
	 */
	private static String USER_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
	/**
	 * �û����£�POST��
	 */
	private static String USER_UPDATE = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	/**
	 * �û������źŻ�ȡ�б�GET��ָ��departmentId 
	 */
	private static String USER_GET_BY_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENTID&fetch_child=FETCHCHILD&status=STATUS";
	/**
	 * �û�ɾ�� ��GET��ָ��userId
	 */
	private static String USER_DELETE = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=ACCESS_TOKEN&userid=USERID";
	/**
	 * ���Ų�ѯ��GET��
	 */
	private static String DEPARTMENT_GET = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";

	/**
	 * �����û�
	 * 
	 * @param jsonUser
	 *            json��ʽ
	 * @return ״̬ 0 ��ʾ�ɹ���������ʾʧ��
	 */
	private static int createUser(String jsonUser) {
		System.out.println("createUserJson: " + jsonUser);
		int result = 0;
		// ���ýӿڴ����û�
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE,
				WeixinUtil.POST_REQUEST_METHOD, jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("�����û�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * �����û�
	 * 
	 * @param user
	 *            �û�ʵ��
	 * @return 0��ʾ�ɹ�������ֵ��ʾʧ��
	 */
	public static int createUser(User user) {
		// ������ǲ������ƣ���ȡ�����б����ƥ�䣬��ȡ����ID
		// List<Department> departments = getDepartment();
		// if (null == departments) {
		// return -1;
		// }
		// for (Department department : departments) {
		// if (user.getDepartment().equals(department.getName())) {
		// user.setDepartment(department.getId());
		// }
		// }
		return createUser(JSONObject.fromObject(user).toString());
	}

	/**
	 * �����û�
	 * 
	 * @return �û��ṹjson�ַ���
	 */
	private static int updateUserJson(String jsonUser) {
		System.out.println("updateUserJson: " + jsonUser);
		int result = 0;
		// ���ýӿڴ����û�
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_UPDATE,
				WeixinUtil.POST_REQUEST_METHOD, jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("�����û�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * �����û�
	 * 
	 * @return User �û�����
	 */
	public static int updateUser(User user) {
		// ������ǲ������ƣ���ȡ�����б����ƥ�䣬��ȡ����ID
		// List<Department> departments = getDepartment();
		// if (null == departments) {
		// return -1;
		// }
		// for (Department department : departments) {
		// if (user.getDepartment().equals(department.getName())) {
		// user.setDepartment(department.getId());
		// }
		// }
		return updateUserJson(JSONObject.fromObject(user).toString());
	}

	/**
	 * ɾ���û�
	 * 
	 * @param userId
	 * @return
	 */
	public static int deleteUser(String userId) {
		int result = 0;
		// ���ýӿ�ɾ���û�
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				USER_DELETE.replace("USERID", userId),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("ɾ���û�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * �����źŻ�ȡ�û��б�
	 * 
	 * @param departmentId
	 * 
	 */
	public static List<User> getUserByDepartment(Integer departmentId,String fetchChild,String status) {
		List<User> userList = new ArrayList<User>();
		// ���ýӿڻ�ȡ�û��б�
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				USER_GET_BY_DEPARTMENT.replace("DEPARTMENTID", String.valueOf(departmentId)).replace("FETCHCHILD", fetchChild).replace("STATUS", status),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("�����źŻ�ȡ�û��б�ʧ�� errcode:"
						+ jsonObject.getInt("errcode") + "��errmsg:"
						+ jsonObject.getString("errmsg"));
				return null;
			}
		} else {
			return null;
		}
		//JSONArray jsonArray = jsonObject.getJSONArray("userlist");
		//String testUserStr = "{				 \"errcode\": 0,				 \"errmsg\": \"ok\",				 \"userlist\": [				 {				 \"userid\": \"zhangsan\",				 \"name\": \"����\",	\"department\":[1]			 }				 ]				 }			";
		JSONArray jsonArray = JSONObject.fromObject(jsonObject).getJSONArray("userlist");
		Collection collection = jsonArray.toCollection(jsonArray, User.class);
		userList = (List<User>) collection;
		return userList;
	}

	/**
	 * ��ѯ�����б�
	 * 
	 */
	public static List<Department> getDepartment() {
		List<Department> departmentList = null;
//		// ���ýӿڻ�ȡ�����б�
		JSONObject jsonObject = WeixinUtil.httpsRequest(DEPARTMENT_GET,WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("��ȡ�����б�ʧ�� errcode:" + jsonObject.getInt("errcode")
						+ "��errmsg:" + jsonObject.getString("errmsg"));
				return null;
			}
		} else {
			return null;
		}
		
		// JSONArray jsonArray = jsonObject.getJSONArray("department");
		//String testDepartStr = "{  \"errcode\": 0, \"errmsg\": \"ok\", \"department\": [       {           \"id\": 2,   \"idx\": 2,           \"name\": \"�����з�����\",               \"order\": 10       },       {           \"id\": 3,      \"ifx\": \"abc\",           \"name\": \"�����Ʒ��\",           \"parentid\": 2,           \"order\": 40       }   ]}";
		JSONArray jsonArray = JSONObject.fromObject(jsonObject).getJSONArray("department");
		Collection collection = JSONArray.toCollection(jsonArray,Department.class);
		departmentList = (List<Department>) collection;
		return departmentList;
	}

	public static void main(String[] args) {
		UserService.getDepartment();
	}

}
