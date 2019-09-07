package com.lock.zk;

import com.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ZkDistributedLock {

    private final ThreadLocal<Pair<InterProcessMutex, String>> threadLocal = new ThreadLocal<>();
    /**
     * zookeeper地址
     */
    private String zkAddr;
    /**
     * session超时时间
     */
    private int sessionTimeOutMs;
    /**
     * zk名称空间
     */
    private String nameSpace;
    private CuratorFramework cf;

    public ZkDistributedLock(String zkAddr, int sessionTimeOutMs, String nameSpace) {

        this.zkAddr = zkAddr;
        this.sessionTimeOutMs = sessionTimeOutMs;
        this.nameSpace = nameSpace;

        //1 重试策略：重试时间为0s 重试10次  [默认重试策略:无需等待一直抢，抢10次］
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(0, 10);

        //2 通过工厂创建连接
        cf = CuratorFrameworkFactory.builder()
                .connectString(this.zkAddr)
                .sessionTimeoutMs(this.sessionTimeOutMs)
                .retryPolicy(retryPolicy)
                .namespace(this.nameSpace)
                .build();
        //3 开启连接
        cf.start();
    }

    /**
     * 获取分布式锁  默认自旋 直到锁可用
     *
     * @param lockKey
     * @return
     */
    public boolean lock(String lockKey) {
        try {
            InterProcessMutex lock = new InterProcessMutex(cf, "/" + lockKey);
            lock.acquire();

            threadLocal.set(new Pair<>(lock, lockKey));

            return true;
        } catch (Exception e) {
            log.error(">>> 获取zk锁异常,lockKey:{},e:", lockKey, e);
            return false;
        }
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁key
     * @param time    等待时间
     * @param unit    时间单位
     * @return
     */
    public boolean lock(String lockKey, long time, TimeUnit unit) {
        try {
            InterProcessMutex lock = new InterProcessMutex(cf, "/" + lockKey);

            if (lock.acquire(time, unit)) {
                threadLocal.set(new Pair<>(lock, lockKey));
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error(">>> 获取zk锁异常,lockKey:{},e:", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock() {
        String lockKey = null;
        try {

            /**
             * 当前线程中获取到pair   如果没有获取到锁 没有必要做释放
             */
            Pair<InterProcessMutex, String> pair = threadLocal.get();
            if (pair == null) {
                return;
            }

            InterProcessMutex lock = pair.getKey();
            lockKey = pair.getValue();
            if (lock == null) {
                return;
            }

            if (!lock.isAcquiredInThisProcess()) {
                return;
            }

            lock.release();

        } catch (Exception e) {

            log.error(">>>释放zk分布式锁【ERROR】,lockKey:{},e:", lockKey, e);

        } finally {

            threadLocal.remove();
        }
    }


}