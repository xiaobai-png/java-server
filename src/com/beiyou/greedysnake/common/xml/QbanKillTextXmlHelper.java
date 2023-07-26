package com.beiyou.greedysnake.common.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.Element;

public class QbanKillTextXmlHelper {
	
	public QbanKillTextXmlHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 登录请求
	 * @param doc
	 * @return
	 */
	// 对于session的相应发送目前有三种方法    1、getID  2、session.setattribute可能不行  3、使用hashMap
	private static List<String> isWhichXml(Document doc, IoSession session){
		List<String> list = null;
		try{
            Element rootElement = doc.getRootElement();
            List children = rootElement.elements();
            int size = children.size();
            // 进行解析信息，可以知道此处是登陆的信息
            switch(rootElement.getName()) {
            	case "UserLogin":
            		list = new ArrayList<String>();
    				list.add("UserLogin");
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("userName").getText());
    					list.add(child.element("passWord").getText());
    				}
    				break;
            	case "UserPlay":
            		list = new ArrayList<String>();
    				list.add("UserPlay");
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("angle").getText());
    				}
    				break;
            	case "UserSpeed":
            		list = new ArrayList<String>();
    				list.add("UserSpeed");
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("speed").getText());
    				}
    				break;
            	case "UserGrade":
            		list = new ArrayList<String>();
    				list.add("UserGrade");
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("userName").getText());
    					list.add(child.element("userGrade").getText());
    				}
            		break;
            	case "GameTime":
            		list = new ArrayList<String>();
    				list.add("GameTime");
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("gameStatic").getText());
    					list.add(child.element("userName").getText());
    				}
            		break;
            	case "UserRank":
            		list = new ArrayList<String>();
    				list.add("UserRank");
    				
    				for (int i = 0; i < size; i++) {
    					Element child = (Element) children.get(i);
    					list.add(child.element("rank0").getText());
    					list.add(child.element("rank1").getText());
    					list.add(child.element("rank2").getText());
    				}
            		break;
            	
            	 default:
            		 System.out.println("XmlHelper接收信息解析失败");
            		 break;
            }
            
			
			
			
        } catch (Exception ex){
            list = null;
        }
		return list;
	}
	
	/**
	 * 数据解析处理
	 * @param doc
	 * @return List<String>
	 */
	public static List<String> gameDateHelper(Document doc, IoSession session){
		List<String> list = null;
		
		list = isWhichXml(doc, session);
		if(list != null && list.size() > 0){
			return list;
		}
		
		return null;
	}
	
}
