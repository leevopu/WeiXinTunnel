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
	 * 用户创建（POST）
	 */
	private static String USER_CREATE = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
	/**
	 * 用户更新（POST）
	 */
	private static String USER_UPDATE = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	/**
	 * 用户按部门号获取列表（GET）指定departmentId 
	 */
	private static String USER_GET_BY_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENTID&fetch_child=FETCHCHILD&status=STATUS";
	/**
	 * 用户删除 （GET）指定userId
	 */
	private static String USER_DELETE = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=ACCESS_TOKEN&userid=USERID";
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
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_CREATE,
				WeixinUtil.POST_REQUEST_METHOD, jsonUser);

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
	 * 更新用户
	 * 
	 * @return 用户结构json字符串
	 */
	private static int updateUserJson(String jsonUser) {
		System.out.println("updateUserJson: " + jsonUser);
		int result = 0;
		// 调用接口创建用户
		JSONObject jsonObject = WeixinUtil.httpsRequest(USER_UPDATE,
				WeixinUtil.POST_REQUEST_METHOD, jsonUser);

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
	 * 删除用户
	 * 
	 * @param userId
	 * @return
	 */
	public static int deleteUser(String userId) {
		int result = 0;
		// 调用接口删除用户
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				USER_DELETE.replace("USERID", userId),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("删除用户失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
			}
		}
		return result;
	}

	/**
	 * 按部门号获取用户列表
	 * 
	 * @param departmentId
	 * 
	 */
	public static List<User> getUserByDepartment(Integer departmentId,String fetchChild,String status) {
		List<User> userList = new ArrayList<User>();
		// 调用接口获取用户列表
		JSONObject jsonObject = WeixinUtil.httpsRequest(
				USER_GET_BY_DEPARTMENT.replace("DEPARTMENTID", String.valueOf(departmentId)).replace("FETCHCHILD", fetchChild).replace("STATUS", status),
				WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("按部门号获取用户列表失败 errcode:"
						+ jsonObject.getInt("errcode") + "，errmsg:"
						+ jsonObject.getString("errmsg"));
				return null;
			}
		} else {
			return null;
		}
		//JSONArray jsonArray = jsonObject.getJSONArray("userlist");
		//String testUserStr = "{				 \"errcode\": 0,				 \"errmsg\": \"ok\",				 \"userlist\": [				 {				 \"userid\": \"zhangsan\",				 \"name\": \"李四\",	\"department\":[1]			 }				 ]				 }			";
		JSONArray jsonArray = JSONObject.fromObject(jsonObject).getJSONArray("userlist");
		Collection collection = jsonArray.toCollection(jsonArray, User.class);
		userList = (List<User>) collection;
		return userList;
	}

	/**
	 * 查询部门列表
	 * 
	 */
	public static List<Department> getDepartment() {
		List<Department> departmentList = null;
//		// 调用接口获取部门列表
		JSONObject jsonObject = WeixinUtil.httpsRequest(DEPARTMENT_GET,WeixinUtil.GET_REQUEST_METHOD, null);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				log.error("获取部门列表失败 errcode:" + jsonObject.getInt("errcode")
						+ "，errmsg:" + jsonObject.getString("errmsg"));
				return null;
			}
		} else {
			return null;
		}
		
		// JSONArray jsonArray = jsonObject.getJSONArray("department");
		//String testDepartStr = "{  \"errcode\": 0, \"errmsg\": \"ok\", \"department\": [       {           \"id\": 2,   \"idx\": 2,           \"name\": \"广州研发中心\",               \"order\": 10       },       {           \"id\": 3,      \"ifx\": \"abc\",           \"name\": \"邮箱产品部\",           \"parentid\": 2,           \"order\": 40       }   ]}";
		JSONArray jsonArray = JSONObject.fromObject(jsonObject).getJSONArray("department");
		Collection collection = JSONArray.toCollection(jsonArray,Department.class);
		departmentList = (List<Department>) collection;
		return departmentList;
	}

	public static void main(String[] args) {
		UserService.getDepartment();
	}

}
