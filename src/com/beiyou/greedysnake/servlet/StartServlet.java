package com.beiyou.greedysnake.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.beiyou.greedysnake.main.QbanKillTextServerMain;



public class StartServlet extends HttpServlet{
	public StartServlet() {
		super();
	}
	
	/**
	 * 初始化方法
	 */
	public void init() throws ServletException {
		QbanKillTextServerMain.main(null);
	}
	
 	public void destroy(){
 		super.destroy();
 	}
}
