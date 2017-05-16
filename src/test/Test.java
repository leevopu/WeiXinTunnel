package test;


import net.sf.json.JSONObject;

import com.weixin.aes.AesException;
import com.weixin.aes.WXBizMsgCrypt;
import com.weixin.corp.entity.user.User;

public class Test {
    
	    /**
	    * ≤‚ ‘¿‡
	    * @param args
	    * 
	    */
	//{"department":[1,2],"email":"","enable":1,"gender":"","mobile":"","name":"abc","oaid":"","position":"","userid":"123","weixinid":""}
	//{"department":[1,2],"email":"","enable":1,"gender":"","mobile":"","name":"abc","oaid":"","position":"","userid":"123","weixinid":""}
	    public static void main(String[] args) {
	    	User user = new User();
	    	user.setUserid("123");
	    	user.setName("abc");
	    	user.getDepartment().add(1);
	    	user.getDepartment().add(2);
	    	System.out.println(JSONObject.fromObject(user).toString());
	    }
}
