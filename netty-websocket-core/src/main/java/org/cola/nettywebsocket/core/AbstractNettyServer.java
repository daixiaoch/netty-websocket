package org.cola.nettywebsocket.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * @author: cola
 * @date: 2022年06月01日 14:31
 */
public abstract class AbstractNettyServer {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNettyServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private int port;

    protected AbstractNettyServer(int port){
        this.port = port;
    }

    /**
     *newChannelInitializer
     * @return
     */
    protected abstract ChannelInitializer newChannelInitializer();

    //protected void setConfig


    private void init(){
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(newChannelInitializer());
    }


    public void start(){
        init();
        try{
            //启动服务器,本质是Java程序发起系统调用，然后内核底层起了一个处于监听状态的服务，生成一个文件描述符FD
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //异步
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                logger.info(">>>>>>>>>>> http remoting server stop.");
            } else {
                logger.error(">>>>>>>>>>> http remoting server error.", e);
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info(">>>>>>>>>>> http remoting server destroy success.");
    }

}