package org.cola.nettywebsocket.core.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;

/**
 * 自定义的路由 既可以实现http又可以实现socket
 * 
 * @author daixiaoch
 *
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketHandler.class);

    private ServerHandler serverHandler = null;

    private ChannelWrapper channelWrapper = null;

    public WebSocketHandler(ServerHandler serverHandler, ChannelWrapper channelWrapper) {
        this.serverHandler = serverHandler;
        this.channelWrapper = channelWrapper;
    }

    /**
     * 路由
     * 对http，websocket单独处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        //每个channel都有id，asLongText是全局channel唯一id
        handleWebSocketFrame(ctx,msg);

    }
    /**
     * 获取客户端的channle，添加到ChannelGroup中
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 每个channel都有id，asLongText是全局channel唯一id
        String key = ctx.channel().id().asLongText();
        channelWrapper.setChannel(key,ctx.channel());
        serverHandler.onOpen(ctx.channel().id().asLongText());
        //存储channel的id和用户的主键
        logger.info(">>>>>>>>>>> new http client is successfully connected. " + ctx.channel().remoteAddress() + Thread.currentThread().getName());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //每个channel都有id，asLongText是全局channel唯一id
        String key = ctx.channel().id().asLongText();
        channelWrapper.removeChannel(key);

        super.channelInactive(ctx);
        serverHandler.onClose(ctx.channel().id().asLongText());
    }

    /**
     * 异常捕获
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        serverHandler.onError(ctx.channel().id().asLongText(),cause);
        logger.error(">>>>>>>>>>>  http exception." + cause.getMessage());
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            serverHandler.onMessage(ctx.channel().id().asLongText(), frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
           // abstractWebSocketServer.doOnBinary(ctx.channel(), frame);
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            return;
        }
    }
}