package org.cola.nettywebsocket.core.handler.channel;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;


/**
 *
 * @author cola

 */
public interface ChannelWrapper {
    /**
     *
     * @param channelId
     * @param channel
     */
    void setChannel(String channelId, Channel channel);

    /**
     * @param channelId
     */
    void removeChannel(String channelId);

    /**
     *返回当前节点所有Channel
     * @return
     */
    Map<String, Channel> getChannels();

    /**
     * 根据channelId获取当前节点Channel，若不存在则返回null
     * @param channelId
     * @return
     */
    Channel getChannel(String channelId);

    /**
     * 获取channelId所在的服务
     * @param channelId
     * @return
     */
    String getService(String channelId);

    /**
     * 获取所有的服务信息
     * @return
     */
    List<String> getServices();
}
