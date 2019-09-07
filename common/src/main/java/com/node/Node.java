package com.node;

import com.BaseReceiver;
import com.util.ContextUtil;
import com.util.IpUtil;
import com.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

@Slf4j
@Component
public class Node {

    // 消息处理逻辑
    private BaseReceiver baseReceiver;

    private ZContext zmqContext;

    private ZMQ.Socket zmqPull;

    private volatile boolean running = false;

    private int interval = 10;


    public void start() {
        if (running) {
            return;
        }
        running = true;

        // 消息监听服务,此服务为阻塞服务最后调用
        this.init();
    }

    public void stop() {
        running = false;
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zmqContext.destroy();
    }

    // 初始化消息监听服务
    private void init() {

        String host = IpUtil.getHostIp() + ":" + ContextUtil.tcpPort;
        try {

            log.info("[启动Node]节点 ={}", host);

            zmqContext = new ZContext();
            zmqContext.setIoThreads(1);

            zmqPull = zmqContext.createSocket(ZMQ.PULL);
            zmqPull.setLinger(3000);
            zmqPull.setRcvHWM(0);
            zmqPull.bind("tcp://" + host);

            log.info("启动节点 ={} 完毕", host);
//            CountUtil.start();
            while (running) {
                try {
                    long startTime = System.currentTimeMillis();

                    pulse();

                    long timeRunning = System.currentTimeMillis() - startTime;
                    if (timeRunning < interval) {
                        Thread.sleep(interval - timeRunning);
                    } else {
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    log.error("加入队列异常", e);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void pulse() {
        while (running) {
            byte[] buf = null;
            try {
                buf = zmqPull.recv();
            } catch (Exception e) {
                log.error(" zmqPull 已停止");
            }
            if (buf == null || buf.length <= 0) {
                break;
            }

//            CountUtil.count();
            baseReceiver.onReceive(SerializeUtil.stm(buf));
        }
    }

    public void setBaseReceiver(BaseReceiver baseReceiver) {
        this.baseReceiver = baseReceiver;
    }
}
