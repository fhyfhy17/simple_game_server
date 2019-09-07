package com.handler;

import com.Constant;
import com.controller.ControllerFactory;
import com.controller.ControllerHandler;
import com.controller.fun.Fun0;
import com.controller.fun.Fun1;
import com.controller.fun.Fun2;
import com.controller.fun.Fun3;
import com.controller.fun.Fun4;
import com.controller.interceptor.HandlerExecutionChain;
import com.exception.StatusException;
import com.exception.exceptionNeedSendToClient.ServerBusinessException;
import com.google.protobuf.Message;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.rpc.RpcHolder;
import com.rpc.RpcRequest;
import com.rpc.RpcResponse;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import com.util.SpringUtils;
import com.util.TipStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class MessageThreadHandler implements Runnable {
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

            // 执行心跳
            pulse();

            stopWatch.stop();

            try {
                if (stopWatch.getTime() < interval) {
                    Thread.sleep(interval - stopWatch.getTime());
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
            } finally {
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
            String gate = null;
            boolean isRpc;
            RpcRequest rpcRequest = null;
            try {
                packet = pulseQueues.poll();
                final int cmdId = packet.getId();
                gate = packet.getGate();
                if(packet.getId() == Constant.RPC_RESPONSE_ID){
                    //log.info("收到 RPC 返回时间  "+ System.currentTimeMillis());
                    SpringUtils.getBean(RpcHolder.class).receiveResponse(ProtostuffUtil.deserializeObject(packet.getData(),RpcResponse.class));
                    return;
                }
                isRpc = packet.getId() == Constant.RPC_REQUEST_ID;
    
                if(!isRpc){
                    handler = ControllerFactory.getControllerMap().get(cmdId);
                    if (handler == null) {
                        throw new IllegalStateException("收到不存在的消息，消息ID=" + cmdId);
                    }
                }else {
                    rpcRequest=ProtostuffUtil.deserializeObject(packet.getData(),RpcRequest.class);
                    String key = rpcRequest.getClassName()+"_"+rpcRequest.getMethodName();
                    handler=ControllerFactory.getRpcControllerMap().get(key);
                    //log.info("收到 RPC 请求时间  "+ System.currentTimeMillis());
                    if (handler == null) {
                        throw new IllegalStateException("收到不存在的Rpc消息，消息KEY=" + key );
                    }
                }

                //拦截器前
                if (!HandlerExecutionChain.applyPreHandle(packet, handler)) {
                    continue;
                }
                Object result = null;
                Object[] m ;
                if(!isRpc){
                    m = handler.getMethodArgumentValues(packet);
                }else {
                    m = rpcRequest.getParameters();
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
                //com.google.protobuf.Message result = (com.google.protobuf.Message) com.handler.invokeForController(packet);
                ////拦截器后
                if (result!=null&&Message.class.isAssignableFrom(result.getClass())) {

                    HandlerExecutionChain.applyPostHandle(packet, (com.google.protobuf.Message) result, handler);
                }
                
                if(isRpc&& result!=null){
                    RpcResponse rpcResponse = new RpcResponse();
                    rpcResponse.setRequestId(rpcRequest.getId());
                    rpcResponse.setData(result);
                    ServerInfoManager.sendMessage(packet.getFrom(),ProtoUtil.buildRpcResponseMessage(ProtostuffUtil.serializeObject(rpcResponse,RpcResponse.class),packet.getUid(),null));
                    //log.info("响应 发回去 的  "+ System.currentTimeMillis());
                }

            }
            //服务之间的错误，只在本地打印，  如果是RPC发回去一个错误类型也就够了
            catch (StatusException se) {
                // Status报错， 执行方法时，抛出主动定义的错误，方便多层调用时无法中断方法，这里主动回复给有result参数的协议
                Class<?> returnType = handler.getMethod().getReturnType();
                if (returnType.isAssignableFrom(Message.class)) {
                    Message.Builder builder = ProtoUtil.setFieldByName(ProtoUtil.createBuilerByClassName(returnType.getName()), "result", TipStatus.fail(se.getTip()));
                    Packet message1 = ProtoUtil.buildMessage(builder.build(), packet.getUid(), null);
                    ServerInfoManager.sendMessage(gate, message1);
                }
            } catch (ServerBusinessException sbe) {
                // 业务报错，
                log.error("", sbe);
                //TODO  也发给前端，方便调试
            } catch (Exception e) {
                // 系统报错
                log.error("", e);
                HandlerExecutionChain.applyPostHandle(packet, Constant.DEFAULT_ERROR_REPLY, handler);

            }
        }
    }


    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }


}
