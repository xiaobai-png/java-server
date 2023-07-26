package com.beiyou.greedysnake.mysql.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

public class DAONameLink{

	public DAONameLink(){
		super();
	}
	
	public static Map<String, IoSession> userMap = new HashMap<>();
	public static Map<Long, String> clientName = new HashMap<>();
	
	public final static String USER_DAO = "User";
	public final static String USERGAMERECORD_DAO = "UserGameRecord";
	public final static String USERGAMESCORE_DAO = "UserGameScore";
	
}