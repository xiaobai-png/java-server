package com.beiyou.greedysnake.main;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.beiyou.greedysnake.handler.QbanKillTextIOHandler;
import com.beiyou.greedysnake.websocket.WebSocketCodecFactory;
import com.pwl.framework.configure.GameCoreConfig;


public class QbanKillTextServerMain {
	
	private static QbanKillTextIOHandler killIOHandler = null;

	
	public QbanKillTextServerMain() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int tcp_port = 8205;
		int udp_port = 8026;
		// 添加数据库配置文件
		try{
			String databaseFile = "E:\\Application\\Serve\\GreedySnake\\src\\database.properties";
			String daoFile = "E:\\Application\\Serve\\GreedySnake\\src\\dao.xml";
			String logFile = "E:\\Application\\Serve\\GreedySnake\\src\\log4j.xml";
			GameCoreConfig.configGame(databaseFile, daoFile, logFile);
		}catch(Exception e){
//			logger.error(e);
			e.printStackTrace();
		}
		
		// 两个接收器
		IoAcceptor tcpacceptor = new NioSocketAcceptor(); // 这是tcp
		IoAcceptor udpacceptor = new NioDatagramAcceptor();
//		IoAcceptor udpacceptor = new NioDatagramAcceptor(); // 这是udp

		tcpacceptor.getFilterChain().addLast("logger", new LoggingFilter());
//		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		tcpacceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new WebSocketCodecFactory()));// 测试：websocket的编码和解码

		udpacceptor.getFilterChain().addLast("logger", new LoggingFilter());
		udpacceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new WebSocketCodecFactory()));// 测试：websocket的编码和解码
		
		killIOHandler = new QbanKillTextIOHandler();
		
		
		tcpacceptor.setHandler(killIOHandler);
		tcpacceptor.getSessionConfig().setReadBufferSize(1024);
		tcpacceptor.getSessionConfig().setWriteTimeout(120);//设置写超时为120秒
		tcpacceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15*60);//设置15分钟后超时
		
		udpacceptor.setHandler(killIOHandler);
		udpacceptor.getSessionConfig().setReadBufferSize(1024);
		udpacceptor.getSessionConfig().setWriteTimeout(120);//设置写超时为120秒
		udpacceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15*60);//设置15分钟后超时
		
		try{
			tcpacceptor.bind(new InetSocketAddress(tcp_port));
			udpacceptor.bind(new InetSocketAddress(udp_port));
		}catch(Exception ex){
//			logger.error(ex);
			ex.printStackTrace();
		}

	}

}
