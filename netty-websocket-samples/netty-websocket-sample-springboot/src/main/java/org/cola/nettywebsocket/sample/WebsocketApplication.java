package org.cola.nettywebsocket.sample;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.executor.NettyWebSocketExecutor;
import org.cola.nettywebsocket.core.handler.DefaultServerHandler;
import org.cola.nettywebsocket.core.handler.ServerHandler;
import org.cola.nettywebsocket.core.handler.channel.BroadcastChannelWrapper;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author WDYin
 * @Date 2021/8/10
 * @Description websocket程序
 **/
@Component
public class WebsocketApplication {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebsocketApplication.class);

    //@Resource
    //private WebsocketInitialization websocketInitialization;

    @PostConstruct
    public void start() {
        try {
            logger.info(Thread.currentThread().getName() + ":websocket启动中......");
           // ServerConfig serverConfig = new ServerConfig(8003,"/ws",8004);
            ServerHandler serverHandler = new DefaultServerHandler();
            List<String> lists = new ArrayList<>();
            lists.add("127.0.0.1:8002");
            ChannelWrapper channelWrapper = new BroadcastChannelWrapper(lists);
            NettyWebSocketExecutor nettyWebSocketExecutor = new NettyWebSocketExecutor(serverHandler, channelWrapper);
            nettyWebSocketExecutor.start();

            //ProxyWebSocketHandle.getProxyWebSocketHandle().sendText("","");
            logger.info(Thread.currentThread().getName() + ":websocket启动成功！！！");
        } catch (Exception e) {
            logger.error("websocket发生错误：",e);
        }
    }
}