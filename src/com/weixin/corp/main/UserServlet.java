package com.weixin.corp.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.user.User;
import com.weixin.corp.service.UserService;

public class UserServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(UserServlet.class);
	private static final long serialVersionUID = -2195245096397807373L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("userServlet doGet");
//		if (request.getRemoteAddr().contains("192.168")) {
			// String userRequest = request.getRequestURI();

			// http://192.168.103.43/WeixinTunnel/userServlet?type=update&userid=wangwu&name=wangwu...封装

			// 模拟解析成实体对象
			// if(type.equals("create") 创建用户
			User newUser = new User();
			newUser.setUserid("wangwu");
			newUser.setName("wangwu");
			// department之后改成部门名称，目前断网测试用departmentId
			newUser.addDepartment(3);
			newUser.setPosition("高级经理");
			newUser.setMobile("13666666666");
			newUser.setGender("1");
			newUser.setEmail("wangwu@test.com");
			newUser.setWeixinid("wangwu888");
			newUser.setEnable(0); // 禁用，停职或离职
			int createUserResult = UserService.createUser(newUser);
			if (0 != createUserResult) {
				log.error("新增用户" + newUser.getName() + "成功");
			} else {
				log.info("新增用户" + newUser.getName() + "成功");
			}

			// if(type.equals("update") 更新用户
			User updateUser = new User();
			updateUser.setUserid("lisi");
			updateUser.setName("lisi");
			// department之后改成部门名称，目前断网测试用departmentId
			updateUser.addDepartment(1);
			updateUser.setPosition("工程师");
			updateUser.setMobile("15888888888");
			updateUser.setGender("1");
			updateUser.setEmail("lisi@qq.com");
			updateUser.setWeixinid("lisi");
			updateUser.setEnable(1); // 禁用，停职或离职
			int updateUserResult = UserService.updateUser(updateUser);
			if (0 != updateUserResult) {
				log.error("更新用户" + newUser.getName() + "成功");
			} else {
				log.info("更新用户" + newUser.getName() + "成功");
			}
//		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Post");
		doGet(request, response);
	}

}
