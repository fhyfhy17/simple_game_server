package com.thread.hash;

public class HashKeyRunnable implements Runnable {

    private long key;
    private Runnable runnable;

    public HashKeyRunnable(long key, Runnable runnable) {
        this.key = key;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    public Object getKey() {
        return this.key;
    }
}
