package org.cola.nettywebsocket.core.message;

import java.io.Serializable;

/**
 * @Author cola
 * @Date 2022/3/31
 * @Description websocket request param
 **/
public class MessageRequest<T> implements Serializable {
    /**
     * 用户唯一Id
     */
    private String channelId;
    /**
     * 是否广播消息
     */
    private int isBroadcast;
    /**
     * 需要发送的数据
     */
    private T data;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(int isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}