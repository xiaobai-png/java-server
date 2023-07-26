package com.beiyou.greedysnake.common.xml;

public class QbanKillTextSendXmlHelper {
	public QbanKillTextSendXmlHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * 字符串结束标志
	 */
	private static String lastStr = "</over>";
	
	
	/**
	 * 发送登录结果
	 * @param success
	 * @return String
	 */
	public static String buildLoginSuccessXml(int success){
		String xmlHead = "<LoginSuccess>";
		String xmlFoot = "</LoginSuccess>";
		StringBuffer result = new StringBuffer();
		result.append("<root>");
		result.append("<success>" + success+ "</success>");
		result.append("</root>");
		return xmlHead + result.toString() + xmlFoot+lastStr;
	}
	
	/** 
	 * 发送移动信息
	 * 
	 * */
	public static String buildPlayDirectionXml(String direct){
		String xmlHead = "<Direction>";
		String xmlFoot = "</Direction>";
		StringBuffer result = new StringBuffer();
		result.append("<root>");
		result.append("<direction>" + direct+ "</direction>");
		result.append("</root>");
		return xmlHead + result.toString() + xmlFoot+lastStr;
	}
	
	public static String buildPlaySpeedXml(String speed){
		String xmlHead = "<Speed>";
		String xmlFoot = "</Speed>";
		StringBuffer result = new StringBuffer();
		result.append("<root>");
		result.append("<speed>" + speed+ "</speed>");
		result.append("</root>");
		return xmlHead + result.toString() + xmlFoot+lastStr;
	}
	
}
