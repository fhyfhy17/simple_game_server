package com;

import com.pojo.Packet;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class BaseReceiver {

    public abstract void onReceive(Packet message);

    public abstract void startup(AtomicInteger count);

}
