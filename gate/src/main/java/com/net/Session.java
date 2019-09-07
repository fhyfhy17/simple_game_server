package com.net;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class Session {

    private String id; // 连接ID
    private long uid; // 用户唯一ID
    private Channel channel; // 连接通道
    private String gameId;// game服务器
    private int autoIncrease;//自增序列

    // 向客户端发送消息
    public void writeMsg(Object msg) {
        if (channel == null) {
            return;
        }
        if (!channel.isActive()) {
            return;
        }

        channel.writeAndFlush(msg);
    }
}
