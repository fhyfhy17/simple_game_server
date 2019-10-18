package com.handler;

import com.Constant;
import com.controller.ControllerFactory;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.exception.StatusException;
import com.pojo.Packet;
import com.rpc.RpcHolder;
import com.rpc.RpcRequest;
import com.rpc.RpcResponse;
import com.thread.schedule.ScheduleAble;
import com.thread.schedule.ScheduleTask;
import com.util.ControllorUtil;
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
    private String handlerId;
    // 心跳频率10毫秒
    private int interval = 10;

    private StopWatch stopWatch = new StopWatch();

    protected final ConcurrentLinkedQueue<Packet> tickQueues= new ConcurrentLinkedQueue<>();
    
    protected final ConcurrentLinkedQueue<Packet> rpcRequestQueues= new ConcurrentLinkedQueue<>();
    
    protected final ConcurrentLinkedQueue<Packet> rpcResponseQueues= new ConcurrentLinkedQueue<>();
    
    @Override
    public void run() {
        for (; ; ) {
            stopWatch.start();
            ContextHolder.setScheduleAble(this);
            tick();
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

    protected void tick(){
        // 执行心跳
        tickPacket();
        // 执行rpcRequest
        tickRpcRequest();
        // 执行rpcResponse
        tickRpcResponse();
        // 执行任务调度心跳
        tickSchedule();
    }
    
    
    public void messageReceived(Packet packet) {
        if (Constant.RPC_RESPONSE.equals(packet.getRpc())){
            rpcResponseQueues.add(packet);
        }else if(StringUtil.contains(packet.getRpc(), Constant.RPC_REQUEST)){
            rpcRequestQueues.add(packet);
        }else{
            tickQueues.add(packet);
        }
        
    }
  
    protected void tickRpcResponse(){
        while(!rpcResponseQueues.isEmpty()){
            Packet packet = rpcResponseQueues.poll();
            SpringUtils.getBean(RpcHolder.class).receiveResponse(ProtostuffUtil.deserializeObject(packet.getData(), RpcResponse.class));
        }
    }
    protected void tickRpcRequest(){
        while(!rpcRequestQueues.isEmpty()){
            Packet packet = null;
            ControllerHandler handler = null;
            try{
                packet = rpcRequestQueues.poll();
                handler = ControllerFactory.getControllerMap().get(packet.getId());
                RpcRequest rpcRequest = ProtostuffUtil.deserializeObject(packet.getData(), RpcRequest.class);
                String key = rpcRequest.getClassName() + "_" + rpcRequest.getMethodName();
                handler = ControllerFactory.getRpcControllerMap().get(key);
                if (handler == null) {
                    log.error("收到不存在的Rpc消息，消息KEY={}" , key);
                    continue;
                }
                Object[] m = rpcRequest.getParameters();
                //拦截器前
                if (!HandlerExecutionChain.applyPreHandle(packet, handler, m)) {
                    continue;
                }
                //执行方法
                Object result = ControllorUtil.handleMethod(handler, m);
                ////拦截器后
                if (!Objects.isNull(result)) {
                    HandlerExecutionChain.applyPostHandle(handler, packet, result);
                }
            }catch(Throwable t){
                HandlerExecutionChain.applyPostHandle(handler, packet, t);
            }
          
        }
    }

    protected void tickPacket() {
        while (!tickQueues.isEmpty()) {

            ControllerHandler handler = null;
            Packet packet = null;
            try {
                packet = tickQueues.poll();
                
                final int cmdId = packet.getId();
                handler = ControllerFactory.getControllerMap().get(cmdId);
                if (handler == null) {
                    log.error("收到不存在的消息，消息ID={}" , cmdId);
                    continue;
                }
                Object[] m = handler.getMethodArgumentValues(packet);
              
                //拦截器前
                if (!HandlerExecutionChain.applyPreHandle(packet, handler, m)) {
                    continue;
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
    public void tickSchedule() {
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
