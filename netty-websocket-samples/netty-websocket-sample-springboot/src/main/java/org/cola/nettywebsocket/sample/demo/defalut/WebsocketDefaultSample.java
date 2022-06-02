package org.cola.nettywebsocket.sample.demo.defalut;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.executor.NettyWebSocketExecutor;
import org.cola.nettywebsocket.core.handler.DefaultServerHandler;
import org.cola.nettywebsocket.core.handler.ServerHandler;
import org.springframework.stereotype.Component;

/**
 * @Author 单节点模式
 * @Date 2021/8/10
 * @Description websocket程序
 **/
@Component
public class WebsocketDefaultSample {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebsocketDefaultSample.class);

    public void start() {
        try {
            logger.info(Thread.currentThread().getName() + ":websocket启动中......");
            // 自定义serverHandler,可执行实现ServerHandler 接口
            ServerHandler serverHandler = new DefaultServerHandler();
            NettyWebSocketExecutor nettyWebSocketExecutor = new NettyWebSocketExecutor(serverHandler);
            nettyWebSocketExecutor.start();
            // 向客户端发送消息
            // ProxyWebSocketHandle.getProxyWebSocketHandle().sendText("","");
            logger.info(Thread.currentThread().getName() + ":websocket启动成功！！！");
        } catch (Exception e) {
            logger.error("websocket发生错误：",e);
        }
    }
}