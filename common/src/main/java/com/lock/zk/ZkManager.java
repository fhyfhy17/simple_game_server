package com.lock.zk;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZkManager{

    private static final Logger log = LoggerFactory.getLogger(ZkManager.class);

    public static ZooKeeper zk;

    /**
     * 初始化
     */
    public static void initial(String connectString) throws Exception{
        initial(connectString, null);
    }

    /**
     * 初始化
     */
    public static void initial(String connectString, Runnable successTask) throws Exception{
        zk = new ZooKeeper("127.0.0.1:2181", 1000, event -> {
            log.info("event=" + event.getType() + "，state=" + event.getState());
            if(event.getState() == Watcher.Event.KeeperState.Expired){
                try{
                    initial(connectString, successTask);
                }catch(Exception e){
                    log.error("zk重连失败", e);
                }
            }
        });

        //检查是否连接上zk服务器
        int count = 0;
        while(zk.getSessionId() == 0){
            Thread.sleep(100);
            if(++count == 40){
                throw new RuntimeException("连接不上zookeeper服务器，zk_connectString=" + connectString);
            }
        }
        log.warn("zookeeper连接成功");

        if(successTask != null){
            successTask.run();
        }
    }


}
