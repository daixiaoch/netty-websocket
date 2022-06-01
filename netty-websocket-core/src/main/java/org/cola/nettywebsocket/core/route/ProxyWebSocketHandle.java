package org.cola.nettywebsocket.core.route;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.cola.nettywebsocket.core.handler.channel.BroadcastChannelWrapper;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;
import org.cola.nettywebsocket.core.handler.channel.DefaultChannelWrapper;
import org.cola.nettywebsocket.core.message.MessageRequest;

import java.util.List;

/**
 * @author: cola
 * @date: 2022年06月01日 16:15
 */
public class ProxyWebSocketHandle implements ProxyHandle {
    private static ProxyWebSocketHandle proxyWebSocketHandle = new ProxyWebSocketHandle();

    private ChannelWrapper channelWrapper = null;

    private ProxyWebSocketHandle(){

    }


    public static ProxyWebSocketHandle getProxyWebSocketHandle(){
        return proxyWebSocketHandle;
    }

    public void setChannelWrapper(ChannelWrapper channelWrapper){
        this.channelWrapper = channelWrapper;
    }

    @Override
    public ChannelFuture sendText(String channelId , String message ) {
        // 如果是默认单节点
        if(channelWrapper instanceof DefaultChannelWrapper){
            Channel channel = channelWrapper.getChannel(channelId);
            if(null != channel){
                return channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }else if(channelWrapper instanceof BroadcastChannelWrapper){
            List<String> services = channelWrapper.getServices();
            for (String service:services) {
                MessageRequest<String> request = new MessageRequest<>();
                request.setChannelId(channelId);
                request.setIsBroadcast(0);
                request.setData(message);
                HttpClient.sendPost(service,request);
            }
        }

        return null;
    }
    @Override
    public void broadcastSendText(String message ) {
        // 如果是默认单节点
        if(channelWrapper instanceof DefaultChannelWrapper){
            channelWrapper.getChannels().forEach((k, v)->{
                sendText(v.id().asLongText(),message);
            });
        }else if(channelWrapper instanceof BroadcastChannelWrapper){
            List<String> services = channelWrapper.getServices();
            for (String service:services) {
                MessageRequest<String> request = new MessageRequest<>();
                request.setIsBroadcast(1);
                request.setData(message);
                HttpClient.sendPost(service,request);
            }
        }

    }

}