package org.cola.nettywebsocket.core.executor;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.handler.DefaultServerHandler;
import org.cola.nettywebsocket.core.handler.ServerHandler;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;
import org.cola.nettywebsocket.core.handler.channel.DefaultChannelWrapper;
import org.cola.nettywebsocket.core.route.ProxyWebSocketHandle;
import org.cola.nettywebsocket.core.server.HttpServer;
import org.cola.nettywebsocket.core.server.NettyHttpServer;
import org.cola.nettywebsocket.core.server.NettyWebSocketServer;
import org.cola.nettywebsocket.core.server.WebSocketServer;

/**
 * @Author cola
 * @Date 2022/3/31
 * @Description websocket通道初始化器
 **/
public class NettyWebSocketExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyWebSocketExecutor.class);
    private int webSocketPort = 8001;
    private int httpPort = 8002;
    private ServerHandler serverHandler = new DefaultServerHandler();
    private ChannelWrapper channelWrapper = new DefaultChannelWrapper();
    private String baseUrl = "/ws";


    public NettyWebSocketExecutor(){

    }
    public NettyWebSocketExecutor(ServerHandler serverHandler){
        this.serverHandler = serverHandler;
    }
    public NettyWebSocketExecutor(ServerHandler serverHandler, ChannelWrapper channelWrapper){
        this.serverHandler = serverHandler;
        this.channelWrapper = channelWrapper;
    }
    public NettyWebSocketExecutor(int webSocketPort, int httpPort,
                                  ServerHandler serverHandler, ChannelWrapper channelWrapper,String baseUrl){
        this.webSocketPort = webSocketPort;
        this.httpPort = httpPort;
        this.serverHandler = serverHandler;
        this.channelWrapper = channelWrapper;
        this.baseUrl = baseUrl;
    }
    /**
     *
     * @throws Exception
     */
    private HttpServer httpServer = null;

    private WebSocketServer webSocketServer = null;

    public void start() throws Exception {
        // start http server
        httpServer = new NettyHttpServer(httpPort);
        httpServer.setChannelWrapper(channelWrapper);
        new Thread(()-> httpServer.start()).start();
        //start websocket server
        webSocketServer = new NettyWebSocketServer(webSocketPort);
        webSocketServer.setBaseUrl(baseUrl);
        webSocketServer.setServerHandler(serverHandler);
        webSocketServer.setChannelWrapper(channelWrapper);
        //set channel handle
        ProxyWebSocketHandle.getProxyWebSocketHandle().setChannelWrapper(channelWrapper);
        new Thread(()-> webSocketServer.start()).start();
    }

    private void stop() {
        // stop provider factory
        if (httpServer != null) {
            try {
                httpServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        // stop http factory
        if (webSocketServer != null) {
            try {
                webSocketServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
