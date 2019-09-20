package com.controller.interceptor.handlerInterceptorImpl;

import com.Constant;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.controller.interceptor.HandlerInterceptor;
import com.exception.StatusException;
import com.pojo.Packet;
import com.util.ExceptionUtil;
import com.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
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
        completableFuture.whenComplete((result,throwable) -> {
            if(throwable!=null){
                if(throwable.getCause() instanceof StatusException){
                    StatusException se = (StatusException)throwable.getCause();
                    try{
						//收到rpcRequest报的错
						if(StringUtil.contains(message.getRpc(),Constant.RPC_REQUEST)){
							message.setId(se.getTip());
							HandlerExecutionChain.applyPostHandle(handler,message, null);
							return;
						}
	
						Type actualTypeArgument=((ParameterizedType)handler.getMethod().getGenericReturnType()).getActualTypeArguments()[0];
	
						//其它报错
						ExceptionUtil.sendStatusExceptionToClient((Class)actualTypeArgument,message,se);
	
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
