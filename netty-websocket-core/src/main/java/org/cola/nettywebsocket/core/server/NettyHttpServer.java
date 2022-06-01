package org.cola.nettywebsocket.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.cola.nettywebsocket.core.AbstractNettyServer;
import org.cola.nettywebsocket.core.handler.HttpHandler;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;

/**
 * @author: cola
 * @date: 2022年06月01日 15:38
 */
public class NettyHttpServer extends AbstractNettyServer implements HttpServer{

    public NettyHttpServer(int port) {
        super(port);
    }
    private ChannelWrapper channelWrapper = null;

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
                        //自定义的handler ，websocket处理业务逻辑
                        //.addLast(new WebSocketHandler())
                        //http 处理器
                        .addLast(new HttpHandler(channelWrapper));
            }
        };
    }

    @Override
    public void setChannelWrapper(ChannelWrapper channelWrapper) {
        this.channelWrapper = channelWrapper;
    }
}