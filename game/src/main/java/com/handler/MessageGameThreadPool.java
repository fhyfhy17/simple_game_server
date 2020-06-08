package com.handler;

import com.controller.ControllerFactory;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.exception.StatusException;
import com.pojo.Packet;
import com.pojo.Player;
import com.util.ControllorUtil;
import com.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MessageGameThreadPool {
    private static final MessageGameThreadPool MESSAGE_GAME_THREAD_POOL = new MessageGameThreadPool();
    public List<ExecutorService> handlerList = new ArrayList<>();
    protected int handlerCount = 8; // 执行器数量

    private MessageGameThreadPool() {
        initHandlers();
    }

    public static MessageGameThreadPool getInstance() {
        return MESSAGE_GAME_THREAD_POOL;
    }

    public void initHandlers() {
        for (int i = 0; i < this.handlerCount; i++) {
            int finalI = i;
            handlerList.add(Executors.newSingleThreadExecutor(r -> new Thread("game-thread-" + finalI)));
        }
    }

    public void execute(Player player, Runnable runnable) {
        choose(player.getUid()).execute(runnable);
    }

    public ExecutorService choose(long key) {
        int index = (int) (key % handlerCount);
        return handlerList.get(index);
    }

    public void messageReceived(Packet packet) {

        // 分配执行器执行
        int index = (int) (packet.getUid() % handlerCount);

        handlerList.get(index).execute(() -> {

            final int cmdId = packet.getId();
            ControllerHandler handler = ControllerFactory.getControllerMap().get(cmdId);
            if (handler == null) {
                log.error("收到不存在的消息，消息ID={}", cmdId);
                return;
            }
            try {
                Object[] m = handler.getMethodArgumentValues(packet);

                //拦截器前
                if (!HandlerExecutionChain.applyPreHandle(packet, handler, m)) {
                    return;
                }

                //执行方法
                Object result = ControllorUtil.handleMethod(handler, m);

                ////针对method的每个参数进行处理， 处理多参数,返回result（这是老的invoke执行controller 暂时废弃）
                //Message result = (Message) com.handler.invokeForController(packet);

                ////拦截器后
                if (!Objects.isNull(result)) {
                    HandlerExecutionChain.applyPostHandle(handler, packet, result);
                }

            } catch (StatusException se) {
                try {
                    //报错推到前端
                    ExceptionUtil.sendStatusExceptionToClient(handler.getMethod().getReturnType(), packet, se);
                } catch (Exception e) {
                    log.error("这不允许报错，哪个消息少加了条件？ 检查下rpc gate from哪些没加。", e);
                }
            } catch (Throwable e) {
                // 系统报错
                log.error("", e);
            }
        });
    }
}
