package com;

import com.pojo.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

        if (buffer.readableBytes() < 4) {
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
        int commandId = buffer.readInt();

        Packet pushMsg = new Packet();
        pushMsg.setId(commandId);

        // 结算消息长度(总长度 - (自身 + 4byte))
        int dataSize = length - 4;
        if (dataSize > 0) {
            byte[] data = new byte[dataSize];
            buffer.readBytes(data);
            pushMsg.setData(data);
        }

        out.add(pushMsg);
    }
}