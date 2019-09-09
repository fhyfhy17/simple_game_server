package com.thread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * 有序线程池，可以包装在任何线程池外层，通过对相同的key进行队列执行，效果是相同key的进行队列执行进真正的线程池
 */
public class OrderingExecutor implements Executor {

    private final Executor delegate;
    private final Map<Object, Queue<Runnable>> keyedTasks = new HashMap<>();

    public OrderingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable task) {
        delegate.execute(task);
    }

    public void execute(Runnable task, Object key) {
        if (key == null) {
            execute(task);
            return;
        }

        boolean first;
        Runnable wrappedTask;
        synchronized (keyedTasks) {
            Queue<Runnable> dependencyQueue = keyedTasks.get(key);
            first = (dependencyQueue == null);
            if (dependencyQueue == null) {
                dependencyQueue = new LinkedList<>();
                keyedTasks.put(key, dependencyQueue);
            }

            wrappedTask = wrap(task, dependencyQueue, key);
            if (!first)
                dependencyQueue.add(wrappedTask);
        }

        if (first)
            delegate.execute(wrappedTask);

    }

    private Runnable wrap(Runnable task, Queue<Runnable> dependencyQueue, Object key) {
        return new OrderedTask(task, dependencyQueue, key);
    }

    class OrderedTask implements Runnable {

        private final Queue<Runnable> dependencyQueue;
        private final Runnable task;
        private final Object key;

        public OrderedTask(Runnable task, Queue<Runnable> dependencyQueue, Object key) {
            this.task = task;
            this.dependencyQueue = dependencyQueue;
            this.key = key;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName()+"  在执行run");
                task.run();
            } finally {
                Runnable nextTask = null;
                synchronized (keyedTasks) {
                    if (dependencyQueue.isEmpty()) {
                        keyedTasks.remove(key);
                    } else {
                        nextTask = dependencyQueue.poll();
                    }
                }
                //try
                //{
                //    Thread.sleep(2000);
                //}
                //catch(InterruptedException e)
                //{
                //    e.printStackTrace();
                //}
                if (nextTask != null)
                    delegate.execute(nextTask);
            }
        }
    }
}