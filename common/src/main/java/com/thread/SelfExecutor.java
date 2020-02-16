package com.thread;

import java.util.concurrent.Executor;

/**
 * 还是本线程处理
 */
public class SelfExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
