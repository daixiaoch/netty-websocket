package org.cola.nettywebsocket.core.handler.channel;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 广播模式包装类
 * @author: cola
 * @date: 2022年06月01日 17:23
 */
public class BroadcastChannelWrapper implements ChannelWrapper {
    public Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private List<String> services = null;

    public BroadcastChannelWrapper(List<String> services) {
        this.services = services;
    }

    @Override
    public void setChannel(String channelId, Channel channel) {
        channelMap.put(channelId, channel);
    }

    @Override
    public void removeChannel(String channelId) {
        channelMap.remove(channelId);
    }

    @Override
    public Map<String, Channel> getChannels() {
        return channelMap;
    }

    @Override
    public Channel getChannel(String channelId) {
        return channelMap.get(channelId);
    }

    @Override
    public String getService(String channelId) {
        return null;
    }

    @Override
    public List<String> getServices() {
        return services;
    }
}