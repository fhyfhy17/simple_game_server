package com;


import com.pojo.Packet;

public class NettyMessage extends Packet
{
    private int autoIncrease;
    private long checkCode;

    public int getAutoIncrease() {
        return autoIncrease;
    }

    public void setAutoIncrease(int autoIncrease) {
        this.autoIncrease = autoIncrease;
    }

    public long getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(long checkCode) {
        this.checkCode = checkCode;
    }
}
