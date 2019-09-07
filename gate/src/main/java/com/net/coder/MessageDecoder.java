package com.net.coder;

import com.pojo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

        if (buffer.readableBytes() < 16) {
            return;
        }

        buffer.markReaderIndex();

        // 读取包长(减自身)
        int length = buffer.readInt() - 4;
        if (length <= 0 || buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        // 解析成为消息格式
        int msgId = buffer.readInt();
        int autoIncrease = buffer.readInt();
        long checkCode = buffer.readLong();
        NettyMessage pushMsg = new NettyMessage();
        pushMsg.setId(msgId);
        pushMsg.setAutoIncrease(autoIncrease);
        pushMsg.setCheckCode(checkCode);

        int dataSize = length - 16;
        if (dataSize > 0) {
            byte[] data = new byte[dataSize];
            buffer.readBytes(data);
            pushMsg.setData(data);
        }

        out.add(pushMsg);
    }
}