package com.controller.interceptor;

import com.controller.ControllerHandler;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Component
public class HandlerExecutionChain {

    private static List<HandlerInterceptor> interceptorList;

    public static boolean applyPreHandle(Packet message,ControllerHandler handler) {
        if (!ObjectUtils.isEmpty(interceptorList)) {
            for (int i = 0; i < interceptorList.size(); i++) {
                HandlerInterceptor interceptor = interceptorList.get(i);
                if (!interceptor.preHandle(message, handler)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void applyPostHandle(Packet message,com.google.protobuf.Message result,ControllerHandler handler) {
        if (!ObjectUtils.isEmpty(interceptorList)) {
            for (int i = 0; i < interceptorList.size(); i++) {
                HandlerInterceptor interceptor = interceptorList.get(i);
                interceptor.postHandle(message, handler, result);
            }
        }
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        HandlerExecutionChain.interceptorList.add(interceptor);
    }

    public void addInterceptors(HandlerInterceptor... interceptors) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            CollectionUtils.mergeArrayIntoCollection(interceptors, HandlerExecutionChain.interceptorList);
        }

    }

    public void addInterceptors(List<HandlerInterceptor> interceptors) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            HandlerExecutionChain.interceptorList.addAll(interceptors);
        }

    }

    public List<HandlerInterceptor> getInterceptors() {
        return HandlerExecutionChain.interceptorList;
    }

    @Autowired
    public void setInterceptorList(List<HandlerInterceptor> interceptorList) {
        HandlerExecutionChain.interceptorList = interceptorList;
    }

}