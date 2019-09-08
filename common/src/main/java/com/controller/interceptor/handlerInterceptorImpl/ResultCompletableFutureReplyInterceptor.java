package com.controller.interceptor.handlerInterceptorImpl;

import com.Constant;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.controller.interceptor.HandlerInterceptor;
import com.google.protobuf.Message;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.rpc.RpcRequest;
import com.rpc.RpcResponse;
import com.template.templates.type.TipType;
import com.util.ExceptionUtil;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import com.util.TipStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Order(5)
@Component
@Slf4j
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）  CompletableFuture
public class ResultCompletableFutureReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message, ControllerHandler handler, Object result, String rpcRequestId) {
        if (!CompletableFuture.class.isAssignableFrom(result.getClass())) {
            return;
        }

        CompletableFuture completableFuture = (CompletableFuture) result;
        try {
            Object result1 = completableFuture.get();
            if (!Objects.isNull(result1)) {
                if (message.getId() == Constant.RPC_REQUEST_ID) {
                    HandlerExecutionChain.applyPostHandle(message, result1, handler, ProtostuffUtil.deserializeObject(message.getData(), RpcRequest.class).getId());
                } else {
                    HandlerExecutionChain.applyPostHandle(message, result1, handler, null);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }


    }

}
