package org.cola.nettywebsocket.core.route;

import io.netty.channel.ChannelFuture;

/**
 * @Description: send message
 * @author: cola
 * @date: 2022年06月01日 22:41
 */
public interface ProxyHandle {
    /**
     *
     * @param channelId
     * @param message
     * @return
     */
    ChannelFuture sendText(String channelId , String message);

    /**
     *
     * @param message
     */
    void broadcastSendText(String message);
}
