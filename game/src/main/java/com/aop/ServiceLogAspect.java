package com.aop;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {
    public final static ThreadLocal<JSONObject> THREAD_LOCAL = new InheritableThreadLocal<>();

    @Pointcut("@annotation(com.annotation.ServiceLog)")
    public void logPoint() {
    }


    @AfterReturning("logPoint()")
    public void aftReturn(JoinPoint pjp) {
        try {
            JSONObject jsonObject = THREAD_LOCAL.get();
            //jsonObject.fluentPut();   这日志还是得一个一个拼，木啥意思啊~~

            //log.info(jsonObject.toJSONString());
        } catch (Throwable e) {
            log.error("", e);
        } finally {
            THREAD_LOCAL.remove();
        }
    }
}
