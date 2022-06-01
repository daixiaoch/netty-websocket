package org.cola.nettywebsocket.core.handler;

import org.cola.nettywebsocket.core.route.ProxyWebSocketHandle;

/**
 * @Description: 默认回调
 * @author: cola
 * @date: 2022年06月01日 15:50
 */
public class DefaultServerHandler implements ServerHandler {
    @Override
    public void onOpen(String channelId) {

    }

    @Override
    public void onClose(String channelId) {

    }

    @Override
    public void onMessage(String channelId, Object obj) {
        ProxyWebSocketHandle.getProxyWebSocketHandle().sendText(channelId,"来了hello");
    }

    @Override
    public void onError(String channelId, Throwable throwable) {

    }
}