package org.cola.nettywebsocket.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.AbstractNettyServer;
import org.cola.nettywebsocket.core.handler.DefaultServerHandler;
import org.cola.nettywebsocket.core.handler.ServerHandler;
import org.cola.nettywebsocket.core.handler.WebSocketHandler;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;

/**
 * @author: cola
 * @date: 2022年06月01日 14:41
 */
public class NettyWebSocketServer extends AbstractNettyServer implements WebSocketServer {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyWebSocketServer.class);

    private String baseUrl = "/ws";

    public NettyWebSocketServer(int port) {
        super(port);
    }

    private ServerHandler serverHandler = null;
    private ChannelWrapper channelWrapper = null;

    @Override
    public void setServerHandler(ServerHandler serverHandler){
        this.serverHandler = serverHandler;
        if (null == serverHandler) {
            this.serverHandler = new DefaultServerHandler();
        }
    }

    @Override
    public void setChannelWrapper(ChannelWrapper channelWrapper) {
        this.channelWrapper = channelWrapper;
    }

    @Override
    protected ChannelInitializer newChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        //因为基于http协议，使用http的编码和解码器
                        .addLast(new HttpServerCodec())
                        //是以块方式写，添加ChunkedWriteHandler处理器
                        .addLast(new ChunkedWriteHandler())
                        /*
                         说明
                         1. http数据在传输过程中是分段, HttpObjectAggregator ，就是可以将多个段聚合
                         2. 这就就是为什么，当浏览器发送大量数据时，就会发出多次http请求
                       */
                        .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                        /* 说明
                           1. 对应websocket ，它的数据是以 帧(frame) 形式传递
                           2. 可以看到WebSocketFrame 下面有六个子类
                           3. 浏览器请求时 ws://localhost:7000/msg 表示请求的uri
                           4. WebSocketServerProtocolHandler 核心功能是将 http协议升级为 ws协议 , 保持长连接
                           5. 是通过一个 状态码 101
                          */
                        .addLast(new WebSocketServerProtocolHandler(baseUrl))
                        //自定义的handler ，websocket处理业务逻辑
                        //.addLast(new WebSocketHandler())
                        //http 处理器
                        .addLast(new WebSocketHandler(serverHandler, channelWrapper));
            }
        };
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void start() {
        if (StringUtil.isNullOrEmpty(baseUrl)) {
            logger.error(">>>>>>>>>>>  baseUrl is empty.");
            return;
        }
        super.start();
    }
}