/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beiyou.greedysnake.websocket;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

/**
 * Encodes incoming buffers in a manner that makes the receiving client type transparent to the 
 * encoders further up in the filter chain. If the receiving client is a native client then
 * the buffer contents are simply passed through. If the receiving client is a websocket, it will encode
 * the buffer contents in to WebSocket DataFrame before passing it along the filter chain.
 * 
 * Note: you must wrap the IoBuffer you want to send around a WebSocketCodecPacket instance.
 * 
 * @author DHRUV CHOPRA
 */
public class WebSocketEncoder extends ProtocolEncoderAdapter{
	private final TextLineEncoder encoder = new TextLineEncoder(Charset.forName("UTF-8"), LineDelimiter.UNIX);// 普通socket用的数据编码器。固定编码UTF-8，如变更可改动
	
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        boolean isHandshakeResponse = message instanceof WebSocketHandShakeResponse;
        boolean isDataFramePacket = message instanceof WebSocketCodecPacket;
        boolean isRemoteWebSocket = session.containsAttribute(WebSocketUtils.SessionAttribute) && (true==(Boolean)session.getAttribute(WebSocketUtils.SessionAttribute));
        
        IoBuffer resultBuffer = null;
        if(isHandshakeResponse){// 是握手协议，websocket才需要握手协议
            WebSocketHandShakeResponse response = (WebSocketHandShakeResponse)message;
            resultBuffer = WebSocketEncoder.buildWSResponseBuffer(response);
        }
        else if(isDataFramePacket){// 是WebSocket数据编码包
        	WebSocketCodecPacket packet = (WebSocketCodecPacket)message;
//            resultBuffer = isRemoteWebSocket ? WebSocketEncoder.buildWSDataFrameBuffer(packet.getPacket()) : packet.getPacket();
            if(isRemoteWebSocket == true){// session中记录为WebSocket
            	resultBuffer = WebSocketEncoder.buildWSDataFrameBuffer(packet.getPacket());
            }else{// 否则不是WebSocket却发送的是WebSocket数据编码包，则不能发送
            	throw (new Exception("session is socket,  message is a websocket type"));
            }
        }
        else{// 是普通Socket信息数据，则使用TextLineEncoder编码发送数据
        	encoder.encode(session, message, out);
            //throw (new Exception("message not a websocket type"));
        }
        
        if(resultBuffer != null){
        	out.write(resultBuffer);
        }
    }
    
    // Web Socket handshake response go as a plain string.
    public static IoBuffer buildWSResponseBuffer(WebSocketHandShakeResponse response) {                
        IoBuffer buffer = IoBuffer.allocate(response.getResponse().getBytes().length, false);
        buffer.setAutoExpand(true);
        buffer.put(response.getResponse().getBytes());
        buffer.flip();
        return buffer;
    }
    
    // Encode the in buffer according to the Section 5.2. RFC 6455
    private static IoBuffer buildWSDataFrameBuffer(IoBuffer buf) {
        
        IoBuffer buffer = IoBuffer.allocate(buf.limit() + 2, false);
        buffer.setAutoExpand(true);
        buffer.put((byte) 0x81); // 0x81: 发送string数据  0x82:发送的是ArrayBuffer二进制数据
        if(buf.capacity() <= 125){
            byte capacity = (byte) (buf.limit());
            buffer.put(capacity);
        }
        else if(buf.capacity() >125 && buf.capacity()<65536){
            buffer.put((byte)126);
            buffer.putShort((short)buf.limit());
        }
        else {
        	buffer.put((byte)127);
        	buffer.putInt((int)buf.limit());
        }
        buffer.put(buf);
        buffer.flip();
        return buffer;
    }
    
}
