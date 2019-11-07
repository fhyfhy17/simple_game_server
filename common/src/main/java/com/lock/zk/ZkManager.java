package com.lock.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

@Slf4j
public class ZkManager{
    
    public static ZooKeeper zk;
    
    public static final String LOCK_ROOT = "/Locks";
    /**
     * 初始化
     */
    public static void initial(String connectString) throws Exception{
        initial(connectString, null);
    }
    
    public static void create(String path) throws KeeperException, InterruptedException {
        
        if (null == ZkManager.zk.exists(path, false)) {
            synchronized (ZkManager.class) {
                try {
                    ZkManager.zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    
    /**
     * 初始化
     */
    public static void initial(String connectString, Runnable successTask) throws Exception{
        zk = new ZooKeeper( connectString, 1000, event -> {
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
