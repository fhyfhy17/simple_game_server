package com.handler;

import com.Constant;
import com.controller.ControllerFactory;
import com.controller.ControllerHandler;
import com.controller.fun.*;
import com.controller.interceptor.HandlerExecutionChain;
import com.exception.StatusException;
import com.pojo.Packet;
import com.rpc.RpcHolder;
import com.rpc.RpcRequest;
import com.rpc.RpcResponse;
import com.thread.schedule.ScheduleAble;
import com.thread.schedule.ScheduleTask;
import com.util.ExceptionUtil;
import com.util.ProtostuffUtil;
import com.util.SpringUtils;
import com.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.quartz.SchedulerException;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class MessageThreadHandler extends ScheduleAble implements Runnable {
    // 执行器ID
    protected String handlerId;
    // 心跳频率10毫秒
    private int interval = 10;

    private StopWatch stopWatch = new StopWatch();

    protected final ConcurrentLinkedQueue<Packet> pulseQueues = new ConcurrentLinkedQueue<>();


    @Override
    public void run() {
        for (; ; ) {
            stopWatch.start();
            ContextHolder.setScheduleAble(this);
            // 执行心跳
            pulse();
            // 执行任务调度心跳
            pulseSchedule();
            stopWatch.stop();
            try {
                if (stopWatch.getTime() < interval) {
                    Thread.sleep(interval - stopWatch.getTime());
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                log.error("线程中断", e);
            } finally {
                ContextHolder.clear();
                stopWatch.reset();
            }
        }
    }

    public void messageReceived(Packet msg) {
        pulseQueues.add(msg);
    }


    public void pulse() {
        while (!pulseQueues.isEmpty()) {

            ControllerHandler handler = null;
            Packet packet = null;
            RpcRequest rpcRequest = null;
            try {
                packet = pulseQueues.poll();

                if (Constant.RPC_RESPONSE.equals(packet.getRpc())) {
                    SpringUtils.getBean(RpcHolder.class).receiveResponse(ProtostuffUtil.deserializeObject(packet.getData(), RpcResponse.class));
                    continue;
                }
                boolean isRpc = StringUtil.contains(packet.getRpc(), Constant.RPC_REQUEST);

                if (!isRpc) {
                    final int cmdId = packet.getId();
                    handler = ControllerFactory.getControllerMap().get(cmdId);
                    if (handler == null) {
                        throw new IllegalStateException("收到不存在的消息，消息ID=" + cmdId);
                    }
                } else {
                    rpcRequest = ProtostuffUtil.deserializeObject(packet.getData(), RpcRequest.class);
                    String key = rpcRequest.getClassName() + "_" + rpcRequest.getMethodName();
                    handler = ControllerFactory.getRpcControllerMap().get(key);
                    if (handler == null) {
                        throw new IllegalStateException("收到不存在的Rpc消息，消息KEY=" + key);
                    }
                }

                Object result = null;
                Object[] m;
                if (!isRpc) {
                    m = handler.getMethodArgumentValues(packet);
                } else {
                    m = rpcRequest.getParameters();
                }
                //拦截器前
                if (!HandlerExecutionChain.applyPreHandle(packet, handler, m)) {
                    continue;
                }
                //
                switch (handler.getFunType()) {
                    case Fun0:
                        result = (((Fun0) handler.getFun()).apply(handler.getAction()));
                        break;
                    case Fun1:
                        result = (((Fun1) handler.getFun()).apply(handler.getAction(), m[0]));
                        break;
                    case Fun2:
                        result = (((Fun2) handler.getFun()).apply(handler.getAction(), m[0], m[1]));
                        break;
                    case Fun3:
                        result = (((Fun3) handler.getFun()).apply(handler.getAction(), m[0], m[1], m[2]));
                        break;
                    case Fun4:
                        result = (((Fun4) handler.getFun()).apply(handler.getAction(), m[0], m[1], m[2], m[3]));
                        break;
                    default:
                        System.out.println("default");
                        break;
                }


                ////针对method的每个参数进行处理， 处理多参数,返回result（这是老的invoke执行controller 暂时废弃）
                //Message result = (Message) com.handler.invokeForController(packet);

                ////拦截器后
                if (!Objects.isNull(result)) {
                    HandlerExecutionChain.applyPostHandle(handler, packet, result);
                }

            } catch (StatusException se) {
                try {
                    //rpcRequest是不会报错的，因为每个rpcRequest都全部try好，包装错误到消息里了

                    //报错推到前端
                    if (handler != null)
                        ExceptionUtil.sendStatusExceptionToClient(handler.getMethod().getReturnType(), packet, se);

                } catch (Exception e) {
                    log.error("这不允许报错，哪个消息少加了条件？ 检查下rpc gate from哪些没加。", e);
                }
            } catch (Throwable e) {
                // 系统报错
                log.error("", e);
            }
        }

    }


    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }


    @Override
    public void pulseSchedule() {
        while (!schedulerList.isEmpty()) {
            ScheduleTask poll = schedulerList.poll();
            try {
                poll.execute();
            } catch (Throwable t) {
                log.error("pulseSchedule报错", t);
                try {
                    this.scheduler.deleteJob(poll.jobKey);
                } catch (SchedulerException e) {
                    log.error("删除schedule报错", e);
                }
            }
        }
    }
}
