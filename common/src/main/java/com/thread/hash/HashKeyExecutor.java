package com.thread.hash;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * HashKey线程池，根据hashkey进行区分
 */
public class HashKeyExecutor implements Executor {
    private static final int nThreads = 8;

    private HashKeyThread[] threads = new HashKeyThread[nThreads];

    public void init() {
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new HashKeyThread();
            threads[i].start();
        }
    }

    @Override
    public void execute(Runnable task) {
        HashKeyRunnable hashKeyRunnable = (HashKeyRunnable) task;
        threads[hashKeyRunnable.getKey().hashCode() % nThreads].add(hashKeyRunnable);
    }
}

@Slf4j
class HashKeyThread extends Thread {
    private ConcurrentLinkedQueue<HashKeyRunnable> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        for (; ; ) {
            stopWatch.start();
            if (!queue.isEmpty()) {
                Runnable poll = queue.poll();
                poll.run();
            }
            stopWatch.stop();
            try {
                // 心跳频率5毫秒
                int interval = 5;
                if (stopWatch.getTime() < interval) {
                    Thread.sleep(interval - stopWatch.getTime());
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                log.error("线程中断", e);
            } finally {
                stopWatch.reset();
            }
        }
    }

    public void add(HashKeyRunnable hashKeyRunnable) {
        this.queue.add(hashKeyRunnable);
    }
}