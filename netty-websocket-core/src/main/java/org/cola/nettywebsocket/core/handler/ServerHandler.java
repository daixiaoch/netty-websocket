package org.cola.nettywebsocket.core.handler;

/**
 *  websocket listen：DefaultServerHandler is a demo
 * @author: cola
 * @date: 2022年06月01日 17:23
 */
public interface ServerHandler {
    /**
     * @param channelId
     */
    void onOpen(String channelId);
    /**
     * @param channelId
     */
    void onClose(String channelId) ;

    /**
     *
     * @param channelId
     * @param obj
     */
    void onMessage(String channelId, Object obj) ;

    /**
     *
     * @param channelId
     * @param throwable
     */
    void onError(String channelId, Throwable throwable) ;
}
