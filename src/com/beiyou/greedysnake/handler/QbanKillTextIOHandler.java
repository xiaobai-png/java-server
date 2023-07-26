package com.beiyou.greedysnake.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.beiyou.greedysnake.common.xml.QbanKillTextSendXmlHelper;
import com.beiyou.greedysnake.common.xml.QbanKillTextXmlHelper;
import com.beiyou.greedysnake.mysql.facade.UserFacade;
import com.beiyou.greedysnake.mysql.facade.UserGameRecordFacade;
import com.beiyou.greedysnake.mysql.facade.UserGameScoreFacade;
import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameScore;
import com.beiyou.greedysnake.mysql.util.DAONameLink;
import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameRecord;
import com.beiyou.greedysnake.mysql.ibatis.entity.User;
import com.beiyou.greedysnake.websocket.WebSocketCodecPacket;

public class QbanKillTextIOHandler extends IoHandlerAdapter{
	public Timestamp gametime = null;
	
	public QbanKillTextIOHandler() {
		
	}
	
	// 接收请求信息
	public void messageReceived(IoSession session, Object message) throws Exception {	
		// 测试：WebSocket
		boolean isRemoteWebSocket = session.containsAttribute("isWEB") && (true==(Boolean)session.getAttribute("isWEB"));
		// 解析接收消息
		String str = null;
        if(isRemoteWebSocket == false){// 普通socket，则可以直接读取数据
        	str = message.toString();
        	
		}else{// 是websocket，则从IoBuffer中读取信息
			IoBuffer buffer = (IoBuffer)message;
			int bufferLength = buffer.limit();
	        byte[] b = new byte[bufferLength];
	        buffer.get(b);
	        str = new String(b, "UTF-8");
		}
//        System.out.println("str="+str);
        
		// 解析接收消息，需要排查结束字符
//        String str = message.toString();
		if (str == null || "".equals(str.trim()) == true || str.indexOf("</over>") == -1) {
			System.out.println("信息为空");
			return;
		}
		
		if(str.indexOf("</over>") != -1){
			str = str.replaceAll("\n", "");
			String[] temp = str.split("</over>");
			for(int t = 0; t < temp.length; t++){
				gameDate(readHelp(temp[t]), session);
			}
		}else{
			gameDate(readHelp(str), session);
		}
		
	}
	
	//发送信息后调用的函数
	public void messageSent(IoSession session, Object message) throws Exception {
		
	}
	
	// 数据分析
	public void gameDate(Document doc, IoSession session){
		List<String> list = QbanKillTextXmlHelper.gameDateHelper(doc, session);
		
		if(list != null){// 解析完毕，分发处理
			gameDateStreamHelper(list ,session);
		}else{
//			logger.info("丢弃包str="+str);
		}
	}
	
	// 获取DOM内容
	public Document readHelp(String str){
		Document doc = null;
		try {
			SAXReader saxReader = new SAXReader(false);
			 
        	saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        	saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        	saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            InputStream inputStream = new ByteArrayInputStream(str.trim().getBytes("UTF-8"));
			doc = saxReader.read(inputStream);
			
			return doc;
		} catch (Exception ex) {
			System.out.println("文件解析失败");
			ex.printStackTrace();
			return null;
		}
	}
	
	
	// 向另一个客户发送消息
	private boolean sendMessageToAnotherClient(String xml, IoSession session) {
		// 向另外的客户端发送消息 只有一个
//		IoSession otherSession = session.getService().getManagedSessions().values()
//				.stream().filter(s -> !s.equals(session)).findFirst().orElse(null);
		IoSession otherSession = null;
		if(DAONameLink.userMap.containsKey("GamePlay")){
			otherSession = DAONameLink.userMap.get("GamePlay");
		}
		// 广播消息给所有客户端
//        for (IoSession clientSession : session.getService().getManagedSessions().values()) {
//            if (clientSession != session) {
//                WriteFuture future = clientSession.write("Client " + session.getId() + ": " + msg);
//                future.awaitUninterruptibly();
//                if (!future.isWritten()) {
//                    System.err.println("Failed to deliver message to: " + clientSession.getRemoteAddress());
//                }
//            }
//        }
		
		if (otherSession != null) {
			IoBuffer Buffer = null;
			try {
				Buffer = IoBuffer.wrap((xml.toString()).getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			WriteFuture future = otherSession.write(WebSocketCodecPacket.buildPacket(Buffer));
			future.addListener(new IoFutureListener<WriteFuture>() {
                @Override
                public void operationComplete(WriteFuture future) {
                    if (future.isWritten()) {
                        System.out.println("消息发送成功");
                    } else {
                        System.out.println("消息发送失败：" + future.getException());
                    }
                }
            });
			
			return true;
		} else {
			// 如果没有向别的客户端发出消息，则认为客户端没有上线
			return false;
		}
	}

	//处理未抛出的异常
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		try{
			session.close(true);
		}catch(Throwable ex){
			ex.printStackTrace();
		}
	}
	
	//连接超时
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		try{
			session.close(true);
		}catch(Throwable ex){
			ex.printStackTrace();
		}
	}
	
	
	//创建会话回调
	// 。对于TCP 连接来说，连接被接受的时候调用，但要注意此时TCP 连接并未建立，此方法仅代表字面含义，也就是连接的对象IoSession 被创建完毕的时候，回调这个方法。对于UDP 来说，当有数据包收到的时候回调这个方法，因为 UDP 是无连接的。
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("=================创建会话====================");
//		session.setAttribute("userId", "0");
//		String ipAndPort = session.getRemoteAddress().toString();
//		session.setAttribute("ip",ipAndPort.subSequence(1,ipAndPort.indexOf(":")));
//		session.setAttribute("sessionId", "");
	}

	//连接打开调用的函数
	public void sessionOpened(IoSession session) throws Exception {
		
	}

	//连接关闭调用的函数, 需要调用IoService 的dispose()方法停止Server 端、Client 端
	public void sessionClosed(IoSession session) throws Exception {
		// 强制更新
		// 结束
		// 断开重连 我不行
		System.out.println("测试游戏断开");
		if(DAONameLink.userMap.get("GamePlay") == session){
			System.out.println("游戏断开");
			UserGameRecordFacade ugrFacade = new UserGameRecordFacade();
			
			long t1=System.currentTimeMillis();
			Timestamp timeend = new Timestamp(t1);
			
			for(int i = 1; i < 4; i++){
				UserGameRecord time = new UserGameRecord(i);
				ugrFacade.updateTime(time.getUid(), gametime, timeend);
			}
			DAONameLink.userMap.remove("GamePlay");
		}
		
	}
		
	// 登陆操作
	private void getUserLoginInfo(String userName, String passWord, IoSession session){
		int success = -1;
		String tempXml = "";
		
		// 连接数据库
		
		UserFacade uFacade = new UserFacade();
		
		User user = uFacade.getUserByName(userName);
		// 可以进行注册功能
		if(user==null){
			System.out.println("账户错误");
			success = 0;
		}else{
			String username = user.getUsername();
			String password = user.getPassword();
			if(passWord.equals(password)){
				success = 1;
			}else{
				success = 0;
			}
		}
		
		// 进行发送数据
		tempXml = QbanKillTextSendXmlHelper.buildLoginSuccessXml(success);
		
		IoBuffer buffer = null;
		try {
			buffer = IoBuffer.wrap((tempXml.toString()).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		session.write(WebSocketCodecPacket.buildPacket(buffer));
	}
	
	
	// 游戏操作， 直接传参
	private void UserPlay(String angle, IoSession session){
		String tempXml = "";
		// 进行发送数据
		tempXml = QbanKillTextSendXmlHelper.buildPlayDirectionXml(angle);
		
		if(sendMessageToAnotherClient(tempXml, session)){
//			System.out.println("可以");
		}else{
			System.out.println("发送失败");
		}
		
	}
	
	// 游戏操作， 直接传参
	private void UserSpeed(String speed, IoSession session){
		String tempXml = "";
		// 进行发送数据
		tempXml = QbanKillTextSendXmlHelper.buildPlaySpeedXml(speed);
		
		if(sendMessageToAnotherClient(tempXml, session)){
//			System.out.println("可以");
		}else{
			System.out.println("发送失败");
		}
		
	}

	private void UserGrade(String userName, String userGrade, IoSession session){
		// 对数据库进行修改
		// 连接数据库
		UserFacade uFacade = new UserFacade();
		
		// 应该进行一次判断，判断是否有userName的信息，有则改变，无则插入
		//需要去user表里面找id
//		System.out.println("测试");
		// 需要把uid改成int
		User user = uFacade.getUserByNickName(userName);
		
		UserGameScoreFacade ugsFacade = new UserGameScoreFacade();
//		System.out.println("第几个" + user.getId());
		UserGameScore temp = ugsFacade.getUserGrade(user.getId());
		
		if(temp == null){
			ugsFacade.insertUserGrade(user.getId(), userGrade);
			System.out.println("成绩初始化 " + userName);
		}else{
			System.out.println("成绩更新 " + userName);
			ugsFacade.updateUserGrade(temp.getUid(), userGrade);
		}
		
	}
	
	private void GameRank(int rank1, int rank2, int rank3){
		System.out.println("排名测试1");
		UserGameRecordFacade ugrFacade = new UserGameRecordFacade();
		ugrFacade.updateRank(rank1 + 1, 1, gametime);
		ugrFacade.updateRank(rank2 + 1, 2, gametime);
		ugrFacade.updateRank(rank3 + 1, 3, gametime);
		System.out.println("排名测试2");
		
	}
	
	private void GameTime(Integer stat, String userName){
		if(stat == 1){
			// 开始
			System.out.println("开始测试1");
			UserFacade uFacade = new UserFacade();
			UserGameRecordFacade ugrFacade = new UserGameRecordFacade();
			long t1=System.currentTimeMillis();
			Timestamp timebegin = new Timestamp(t1);

			
			User user = uFacade.getUserByNickName(userName);
			UserGameRecord time = new UserGameRecord(user.getId());
			
			System.out.println("开始测试3");
			ugrFacade.beginTime(time.getUid(), timebegin);
			gametime = timebegin;
			System.out.println("开始测试2");
		}else{
			// 结束
			System.out.println("结束测试1");
			UserFacade uFacade = new UserFacade();
			long t1=System.currentTimeMillis();
			Timestamp timeend = new Timestamp(t1);
			
			UserGameRecordFacade ugrFacade = new UserGameRecordFacade();
			
			User user = uFacade.getUserByNickName(userName);
			System.out.println(userName);
			UserGameRecord time = new UserGameRecord(user.getId());
			ugrFacade.updateTime(time.getUid(), gametime, timeend);
			System.out.println("结束测试2");
		}
	}
	
	
	/**
	 * 游戏数据解析器
	 * 如果是本类处理的数据,则本类处理,如果不是则转交相应类处理
	 */
	private void gameDateStreamHelper(List<String> list, IoSession session){
		if(list != null && list.size() > 0){
			String tempStr = String.valueOf(list.get(0));
			
			switch(tempStr){
				case "UserLogin":
					getUserLoginInfo(list.get(1), list.get(2), session);
					DAONameLink.userMap.put("UserLogin", session); // 此处考虑联机使用 username
					break;
				case "UserPlay":
					UserPlay(list.get(1), session);
					break;
				case "UserSpeed":
					UserSpeed(list.get(1), session);
					break;
				case "UserGrade":
					UserGrade(list.get(1), list.get(2), session);
					break;
				case "GameTime":
					GameTime(Integer.parseInt(list.get(1)), list.get(2));
					DAONameLink.userMap.put("GamePlay", session); // 此处为昵称， 可以使用昵称查找username 但是可以直接使用username 变成昵称
					break;
				case "UserRank":
					GameRank(Integer.parseInt(list.get(1)),Integer.parseInt(list.get(2)),Integer.parseInt(list.get(3)));
					break;
				default:
					System.out.println("Handler解析失败");
					break;
			}
			
		}
	}
	
	
}
