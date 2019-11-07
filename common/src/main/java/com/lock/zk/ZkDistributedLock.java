package com.lock.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

@Slf4j
public class ZkDistributedLock {
    
    private CuratorFramework cf;
    private String nameSpace;
    public ZkDistributedLock(String zkAddr, String nameSpace) {
        this.nameSpace = nameSpace;
        cf = CuratorFrameworkFactory.builder()
                .connectString(zkAddr)
                .sessionTimeoutMs(1000)
                .retryPolicy(new ExponentialBackoffRetry(0, 10))
                .namespace(nameSpace)
                .build();
        cf.start();
    }
    
    public ZkLock getLock(String key){
        InterProcessMutex lock = new InterProcessMutex(cf, "/"+nameSpace +"/"+ key);
        return new ZkLock(lock,key);
    }

    public static class ZkLock{
        private InterProcessMutex lock ;
        private String lockKey;
        ZkLock(InterProcessMutex lock,String lockKey){
            this.lock=lock;
            this.lockKey=lockKey;
        }
        public void lock() {
            try {
                lock.acquire();
            } catch (Exception e) {
                log.error(" 获取zk锁异常,lockKey:{},e:", lockKey, e);
            }
        }
        
        public void unlock(){
            try {
                lock.release();
            } catch (Exception e) {
                log.error("释放zk分布式锁【ERROR】 key={}",lockKey, e);
            }
        }
    }
}