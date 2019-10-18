package com.controller.interceptor.handlerInterceptorImpl;

import com.Constant;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.rpc.RpcResponse;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import com.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Order(5)
@Component
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）  RPC
public class ResultRpcReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(ControllerHandler handler, Packet message, Object result) {
        if (!StringUtil.contains(message.getRpc(), Constant.RPC_REQUEST)) {
            return;
        }
        
        if(result instanceof Throwable){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(message.getRpc());
            rpcResponse.setThrowable((Throwable)result);
            send(message,rpcResponse);
            return;
        }
        
        if (!CompletableFuture.class.isAssignableFrom(result.getClass())) {
            log.error("rpc返回结果必须为 CompletableFuture<> !" );
            return;
        }
        CompletableFuture<Object> completableFuture = (CompletableFuture<Object>) result;
    
        completableFuture.whenComplete((back, throwable) -> {
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(message.getRpc());
            if(throwable!=null){
                rpcResponse.setThrowable(throwable);
            }else {
                rpcResponse.setData(back);
            }
            send(message,rpcResponse);
           });
       
    }
    private void send(Packet packet,RpcResponse rpcResponse){
        ServerInfoManager.sendMessage(packet.getFrom(),
                ProtoUtil.buildRpcResponseMessage(
                        ProtostuffUtil.serializeObject(rpcResponse, RpcResponse.class),
                        packet.getUid(),
                        ContextUtil.id));
    }

}
