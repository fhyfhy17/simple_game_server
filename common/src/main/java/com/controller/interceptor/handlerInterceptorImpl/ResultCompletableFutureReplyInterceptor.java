package com.controller.interceptor.handlerInterceptorImpl;

import com.controller.interceptor.HandlerExecutionChain;
import com.controller.interceptor.HandlerInterceptor;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Order(5)
@Component
@Slf4j
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）  CompletableFuture
public class ResultCompletableFutureReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message, Object result) {
        if (!CompletableFuture.class.isAssignableFrom(result.getClass())) {
            return;
        }

        CompletableFuture completableFuture = (CompletableFuture) result;
        try {
            Object result1 = completableFuture.get();
            if (!Objects.isNull(result1)) {
                HandlerExecutionChain.applyPostHandle(message, result1);
            }
        } catch (Exception e) {
            log.error("", e);
        }


    }

}
