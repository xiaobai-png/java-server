/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beiyou.greedysnake.websocket;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;

/**
 * Decodes incoming buffers in a manner that makes the sender transparent to the 
 * decoders further up in the filter chain. If the sender is a native client then
 * the buffer is simply passed through. If the sender is a websocket, it will extract
 * the content out from the dataframe and parse it before passing it along the filter
 * chain.
 * 
 * @author DHRUV CHOPRA
 */
public class WebSocketDecoder extends CumulativeProtocolDecoder{
	private final TextLineDecoder decoder = new TextLineDecoder(Charset.forName("UTF-8"), LineDelimiter.AUTO);// 普通socket用的数据解码器。固定编码UTF-8，如变更可改动
	
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        IoBuffer resultBuffer = null;
        if(!session.containsAttribute(WebSocketUtils.SessionAttribute)){// session中还没有设置过是否是websocket标志，则是连接后收到的第一个消息
            if(tryWebSockeHandShake(session, in, out)){// 尝试进行握手协议，如果是握手协议并且握手成功，则认为是websocket，以websocket数据进行处理
                in.position(in.limit());
                return true;
            }
            else{// 第一个消息不是握手协议，则是普通的socket信息，则设置为不是websocket，并使用普通的socket方式解析数据
                session.setAttribute(WebSocketUtils.SessionAttribute, false);
            	decoder.decode(session, in, out);
            }
        }
        else if(session.containsAttribute(WebSocketUtils.SessionAttribute) && true==(Boolean)session.getAttribute(WebSocketUtils.SessionAttribute)){// 是websocket传来的数据
            // there is incoming data from the websocket. Decode and send to handler or next filter.     
            int startPos = in.position();
            resultBuffer = WebSocketDecoder.buildWSDataBuffer(in, session);
            if(resultBuffer == null){
                // There was not enough data in the buffer to parse. Reset the in buffer
                // position and wait for more data before trying again.
                in.position(startPos);
                return false;
            }
        }
        else{// 是普通socket的数据信息，则使用普通的socket方式解析数据
        	decoder.decode(session, in, out);
        }
        if(resultBuffer != null){
        	out.write(resultBuffer);
        }
                
        return true;
    }

    /**
    *   Try parsing the message as a websocket handshake request. If it is such
    *   a request, then send the corresponding handshake response (as in Section 4.2.2 RFC 6455).
    */
    private boolean tryWebSockeHandShake(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        
        try{
            String payLoadMsg = new String(in.array());
            String socketKey = WebSocketUtils.getClientWSRequestKey(payLoadMsg);
            if(socketKey.length() <= 0){
                return false;
            }
            String challengeAccept = WebSocketUtils.getWebSocketKeyChallengeResponse(socketKey);            
            WebSocketHandShakeResponse wsResponse = WebSocketUtils.buildWSHandshakeResponse(challengeAccept);
            session.setAttribute(WebSocketUtils.SessionAttribute, true);
            session.write(wsResponse);
            return true;
        }
        catch(Exception e){
            // input is not a websocket handshake request.
            return false;
        }        
    }
    
    // Decode the in buffer according to the Section 5.2. RFC 6455
    // If there are multiple websocket dataframes in the buffer, this will parse
    // all and return one complete decoded buffer.
    private static IoBuffer buildWSDataBuffer(IoBuffer in, IoSession session) {

        IoBuffer resultBuffer = null;
        
        do{
            byte frameInfo = in.get();            
            byte opCode = (byte) (frameInfo & 0x0f);
            if (opCode == 8) {
                // opCode 8 means close. See RFC 6455 Section 5.2
                // return what ever is parsed till now.
                session.close(true);
                return resultBuffer;
            }        
            int frameLen = (in.get() & (byte) 0x7F);
            if(frameLen == 126){
                frameLen = in.getShort();
            }
            
            // Validate if we have enough data in the buffer to completely
            // parse the WebSocket DataFrame. If not return null.
            if(frameLen+4 > in.remaining()){                
                return null;
            }
            byte mask[] = new byte[4];
            for (int i = 0; i < 4; i++) {
                mask[i] = in.get();
            }

            /*  now un-mask frameLen bytes as per Section 5.3 RFC 6455
                Octet i of the transformed data ("transformed-octet-i") is the XOR of
                octet i of the original data ("original-octet-i") with octet at index
                i modulo 4 of the masking key ("masking-key-octet-j"):

                j                   = i MOD 4
                transformed-octet-i = original-octet-i XOR masking-key-octet-j
            * 
            */
             
            byte[] unMaskedPayLoad = new byte[frameLen];
            for (int i = 0; i < frameLen; i++) {
                byte maskedByte = in.get();
                unMaskedPayLoad[i] = (byte) (maskedByte ^ mask[i % 4]);
            }
            
            if(resultBuffer == null){
                resultBuffer = IoBuffer.wrap(unMaskedPayLoad);
                resultBuffer.position(resultBuffer.limit());
                resultBuffer.setAutoExpand(true);
            }
            else{
                resultBuffer.put(unMaskedPayLoad);
            }
        }
        while(in.hasRemaining());
        
        resultBuffer.flip();
        return resultBuffer;

    }    
}
