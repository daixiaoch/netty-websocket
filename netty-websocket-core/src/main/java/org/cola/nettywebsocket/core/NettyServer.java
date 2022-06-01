package org.cola.nettywebsocket.core;
/**
 * @author: cola
 * @date: 2022年06月01日 14:31
 */
public interface NettyServer {
    /**
     * start netty server
     */
    void start();

    /**
     * stop netty server
     */
    void stop();
}
