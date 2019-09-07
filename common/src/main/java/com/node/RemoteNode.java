package com.node;

import lombok.extern.slf4j.Slf4j;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.Serializable;

@Slf4j
public final class RemoteNode implements Serializable {

    /* ip:port */
    private String remoteAddr;
    private ZContext zmqContext;
    private ZMQ.Socket zmqPush;

    public RemoteNode(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void startup() {
        try {
            log.info("[启动Remote]节点={} ", remoteAddr);
            zmqContext = new ZContext(1);
            zmqPush = zmqContext.createSocket(ZMQ.PUSH);
            zmqPush.setLinger(3000);
            zmqPush.setSndHWM(0);
            zmqPush.connect("tcp://" + remoteAddr);

            log.info("连接建立完毕 ={}", remoteAddr);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void sendReqMsg(byte[] message) {
        synchronized (this) {
            zmqPush.send(message);
        }
    }

    public void stop() {
        try {
            zmqContext.destroy();
        } catch (Exception e) {
            log.info("{} 节点close", this.remoteAddr, e);
        }
    }
}
