package org.cola.nettywebsocket.core.handler.channel;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: redis模式包装类
 * @author: cola
 * @date: 2022年06月01日 17:24
 */
public class RedisChannelWrapper implements ChannelWrapper {
    public Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public void setChannel(String channelId, Channel channel) {
        //set to redis
    }

    @Override
    public void removeChannel(String channelId) {

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
        return null;
    }
}