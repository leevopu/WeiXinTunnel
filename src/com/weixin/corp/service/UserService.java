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
	 * 用户创建（POST）
	 */
	private static String USER_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
	/**
	 * 用户更新（POST）
	 */
	private static String USER_UPDATE = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	/**
	 * 部门查询（GET）
	 */
	private static String DEPARTMENT_GET = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";

	/**
	 * 创建用户
	 * 
	 * @param jsonUser
	 *            json格式
	 * @return 状态 0 表示成功、其他表示失败
	 */
	private static int createUser(String jsonUser) {
		System.out.println("createUserJson: " + jsonUser);
		int result = 0;
		// 调用接口创建用户
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE, "POST",
				jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("创建用户失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * 创建用户
	 * 
	 * @param user
	 *            用户实例
	 * @return 0表示成功，其他值表示失败
	 */
	public static int createUser(User user) {
		// 传入的是部门名称，获取部门列表进行匹配，获取部门ID
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
	 * 更新用户
	 * 
	 * @return 用户结构json字符串
	 */
	private static int updateUserJson(String jsonUser) {
		System.out.println("updateUserJson: " + jsonUser);
		int result = 0;
		// 调用接口创建用户
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_UPDATE, "POST",
				jsonUser);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("更新用户失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * 更新用户
	 * 
	 * @return User 用户对象
	 */
	public static int updateUser(User user) {
		// 传入的是部门名称，获取部门列表进行匹配，获取部门ID
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
	 * 查询部门
	 * 
	 * @return 部门结构json字符串
	 */
	private static JSONObject getDepartmentJson() {
		JSONObject result = null;
		result = WeixinUtil.httpsRequest(DEPARTMENT_GET, "GET", null);
		return result;
	}

	/**
	 * 查询部门列表
	 * 
	 * @return Department 部门列表对象
	 */
	private static List<Department> getDepartment() {
		JSONObject departmentJson = getDepartmentJson();
		if (null == departmentJson) {
			log.error("获取部门列表失败");
			return null;
		}
		JSONObject json = departmentJson.getJSONObject("department");
		System.out.println(json);
		List<Department> departments = (ArrayList<Department>) JSONObject
				.toBean(json, Department.class);
		return departments;
	}

}
