package com.net.coder;

import com.pojo.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx,Packet msg,ByteBuf buff) throws Exception {

        try {

            // 写入总包长占位置
            buff.writeInt(0);
            buff.writeInt(msg.getId());
            // 可能并没有消息体
            byte[] data = msg.getData();
            if (data != null && data.length > 0) {
                buff.writeBytes(msg.getData());
            }
            // 回写包头
            int length = buff.writerIndex();
            buff.setInt(0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
