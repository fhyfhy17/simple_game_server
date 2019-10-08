package com.controller.interceptor.handlerInterceptorImpl;

import cn.hutool.core.util.TypeUtil;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.controller.interceptor.HandlerInterceptor;
import com.exception.StatusException;
import com.pojo.Packet;
import com.util.ExceptionUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Order(5)
@Component
@Slf4j
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）  CompletableFuture
public class ResultCompletableFutureReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(ControllerHandler handler,Packet message,Object o) {
        if (!CompletableFuture.class.isAssignableFrom(o.getClass())) {
            return;
        }

        CompletableFuture<Object> completableFuture = (CompletableFuture<Object>) o;
	 
		Type actualTypeArgument =TypeUtil.getTypeArgument(TypeUtil.getReturnType(SpringUtils.getRawMethod(handler.getMethod())));
		completableFuture.whenComplete((result,throwable) -> {
            if(throwable!=null){
                if(throwable.getCause() instanceof StatusException){
                    StatusException se = (StatusException)throwable.getCause();
                    try{
						
						ExceptionUtil.sendStatusExceptionToClient(actualTypeArgument.getClass(),message,se);
	
						log.error("异步status",throwable);
	
					}catch(Throwable e){
						log.error("这不允许报错，看看忘添加的条件。 ",e);
					}
                }else {
                    log.error("异步报错", throwable);
                }
            }
            
            if (!Objects.isNull(result)) {
                HandlerExecutionChain.applyPostHandle(handler,message, result);
            }
        }) ;
    }
}
