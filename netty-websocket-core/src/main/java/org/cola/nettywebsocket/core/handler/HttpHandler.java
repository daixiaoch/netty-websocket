package org.cola.nettywebsocket.core.handler;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.handler.channel.ChannelWrapper;
import org.cola.nettywebsocket.core.message.MessageRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 自定义的路由 既可以实现http又可以实现socket
 * 
 * @author daixiaoch
 *
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpHandler.class);

    private ChannelWrapper channelWrapper = null;

    public HttpHandler(ChannelWrapper channelWrapper) {
        this.channelWrapper = channelWrapper;
    }
    
    /**
     * 路由
     * 对http，websocket单独处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //每个channel都有id，asLongText是全局channel唯一id
        String key = ctx.channel().id().asLongText();
        sendHttpResponse(ctx,msg ,  new DefaultFullHttpResponse(HTTP_1_1, OK));
        // 如果是HTTP请求，则说明是集群情况下发送消息
        if (msg instanceof FullHttpRequest) {
           MessageRequest<String> messageRequest =  getPostParamsFromChannel(msg);
           // 不是广播消息
           if(messageRequest.getIsBroadcast() == 0){
               Channel channel = channelWrapper.getChannel(messageRequest.getChannelId());
               if(channel != null){
                   channel.writeAndFlush(new TextWebSocketFrame(messageRequest.getData()));
               }
           }else{
               Map<String,Channel> map = channelWrapper.getChannels();
               map.forEach((k, v)->{
                   v.writeAndFlush(new TextWebSocketFrame(messageRequest.getData()));
               });
           }
        }

    }
    /**
     * 获取客户端的channle，添加到ChannelGroup中
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 每个channel都有id，asLongText是全局channel唯一id
        String key = ctx.channel().id().asLongText();
        //存储channel的id和用户的主键
        logger.info(">>>>>>>>>>> new http client is successfully connected. " + ctx.channel().remoteAddress() + Thread.currentThread().getName());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 异常捕获
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>>  http exception." + cause.getMessage());
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        int statusCode = res.status().code();
        if (statusCode != OK.code() && res.content().readableBytes() == 0) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        HttpUtil.setContentLength(res, res.content().readableBytes());

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || statusCode != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    private MessageRequest<String> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {
        MessageRequest<String> params = null;
        if(fullHttpRequest.method() == HttpMethod.POST){
            // 处理post 请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if(StringUtil.isNullOrEmpty(strContentType)){
                return null;
            }
            if(strContentType.contains("x-www-form-urlencoded")){
                // params = getFormParams(fullHttpRequest);
            }else if(strContentType.contains("application/json")){
                params = getJSONParams(fullHttpRequest);
            }else {
                return null;
            }
        }
        return params;
    }
    private MessageRequest<String> getJSONParams(FullHttpRequest fullHttpRequest) {
       // Map<String, Object> params = new HashMap<>();

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = null;
        try {
            strContent = new String(reqContent, "UTF-8");
            MessageRequest messageRequest = new Gson().fromJson(strContent, MessageRequest.class);
            return messageRequest;
        } catch (UnsupportedEncodingException e) {
            logger.error(">>>>>>>>>>> new http client is successfully connected. " ,e);

        }

        return null;

    }
    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }

        return params;
    }

}