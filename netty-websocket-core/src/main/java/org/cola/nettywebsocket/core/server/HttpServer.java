package org.cola.nettywebsocket.core.server;

import org.cola.nettywebsocket.core.NettyServer;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;
/**
 * @author: cola
 * @date: 2022年06月01日 16:15
 */
public interface HttpServer extends NettyServer {
    /**
     *
     * @param channelWrapper
     */
    void setChannelWrapper(ChannelWrapper channelWrapper);
}
