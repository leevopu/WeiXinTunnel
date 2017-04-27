package com.weixin.corp.service;

import java.util.ArrayList;
import java.util.List;

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
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE, "POST",
				jsonUser);

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
//		List<Department> departments = getDepartment();
//		if (null == departments) {
//			return -1;
//		}
//		for (Department department : departments) {
//			if (user.getDepartment().equals(department.getName())) {
//				user.setDepartment(department.getId());
//			}
//		}
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
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_UPDATE, "POST",
				jsonUser);

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
//		List<Department> departments = getDepartment();
//		if (null == departments) {
//			return -1;
//		}
//		for (Department department : departments) {
//			if (user.getDepartment().equals(department.getName())) {
//				user.setDepartment(department.getId());
//			}
//		}
		return updateUserJson(JSONObject.fromObject(user).toString());
	}

	/**
	 * ��ѯ����
	 * 
	 * @return ���Žṹjson�ַ���
	 */
	private static JSONObject getDepartmentJson() {
		JSONObject result = null;
		result = WeixinUtil.httpsRequest(DEPARTMENT_GET, "GET", null);
		return result;
	}

	/**
	 * ��ѯ�����б�
	 * 
	 * @return Department �����б����
	 */
	private static List<Department> getDepartment() {
		JSONObject departmentJson = getDepartmentJson();
		if (null == departmentJson) {
			log.error("��ȡ�����б�ʧ��");
			return null;
		}
		JSONObject json = departmentJson.getJSONObject("department");
		System.out.println(json);
		List<Department> departments = (ArrayList<Department>) JSONObject
				.toBean(json, Department.class);
		return departments;
	}

}
