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

			// http://192.168.103.43/WeixinTunnel/userServlet?type=update&userid=wangwu&name=wangwu...��װ

			// ģ�������ʵ�����
			// if(type.equals("create") �����û�
			User newUser = new User();
			newUser.setUserid("wangwu");
			newUser.setName("wangwu");
			// department֮��ĳɲ������ƣ�Ŀǰ����������departmentId
			newUser.addDepartment(3);
			newUser.setPosition("�߼�����");
			newUser.setMobile("13666666666");
			newUser.setGender("1");
			newUser.setEmail("wangwu@test.com");
			newUser.setWeixinid("wangwu888");
			newUser.setEnable(0); // ���ã�ְͣ����ְ
			int createUserResult = UserService.createUser(newUser);
			if (0 != createUserResult) {
				log.error("�����û�" + newUser.getName() + "�ɹ�");
			} else {
				log.info("�����û�" + newUser.getName() + "�ɹ�");
			}

			// if(type.equals("update") �����û�
			User updateUser = new User();
			updateUser.setUserid("lisi");
			updateUser.setName("lisi");
			// department֮��ĳɲ������ƣ�Ŀǰ����������departmentId
			updateUser.addDepartment(1);
			updateUser.setPosition("����ʦ");
			updateUser.setMobile("15888888888");
			updateUser.setGender("1");
			updateUser.setEmail("lisi@qq.com");
			updateUser.setWeixinid("lisi");
			updateUser.setEnable(1); // ���ã�ְͣ����ְ
			int updateUserResult = UserService.updateUser(updateUser);
			if (0 != updateUserResult) {
				log.error("�����û�" + newUser.getName() + "�ɹ�");
			} else {
				log.info("�����û�" + newUser.getName() + "�ɹ�");
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
