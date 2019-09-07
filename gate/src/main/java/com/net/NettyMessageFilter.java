package com.net;

import com.pojo.NettyMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.zip.CRC32;

@Component
public class NettyMessageFilter {
    private CRC32 crc32 = new CRC32();

    public boolean checkAutoIncrease(Session session, NettyMessage message) {
        if (session.getAutoIncrease() == message.getAutoIncrease()) {
            return false;
        }
        if (session.getAutoIncrease() != Integer.MAX_VALUE && session.getAutoIncrease() > message.getAutoIncrease()) {
            return false;
        }
        session.setAutoIncrease(message.getAutoIncrease());
        return true;
    }

    public boolean checkCode(Session session, NettyMessage message) {
        byte[] data = message.getData();
        if (Objects.isNull(data) && message.getCheckCode() == 0) {
            return true;
        }
        crc32.reset();
        crc32.update(data);
        return crc32.getValue() == message.getCheckCode();
    }

}
