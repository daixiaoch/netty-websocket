package org.cola.nettywebsocket.core.server;

import org.cola.nettywebsocket.core.handler.ServerHandler;

/**
 * @author cola
 */
public interface WebSocketServer extends HttpServer {
    /**
     *
     * @param baseUrl
     */
    void setBaseUrl(String baseUrl);

    /**
     *
     * @param serverHandler
     */
    void setServerHandler(ServerHandler serverHandler);
}
