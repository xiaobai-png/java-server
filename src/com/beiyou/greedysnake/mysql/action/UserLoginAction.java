package com.beiyou.greedysnake.mysql.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.beiyou.greedysnake.mysql.facade.UserFacade;
import com.beiyou.greedysnake.mysql.ibatis.entity.User;
import com.pwl.framework.action.BaseAction;
import com.pwl.framework.exception.BaseException;


public class UserLoginAction extends BaseAction {

	@Override
	public ActionForward process(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3)
			throws BaseException {
		// 使用https更加安全，不使用websocket
		// 允许跨域
//		arg3.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
//	    arg3.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5306");
	    
//	    String schoolName = arg2.getParameter("schoolname");
//		System.out.println(schoolName);

		
		
		
//		UserFacade uFacade = new UserFacade();
		
//		User user = uFacade.getUserById();
		
//		String username = user.getUsername();
//		String password = user.getPassword();
		
		return null;
	}
	
	
}
