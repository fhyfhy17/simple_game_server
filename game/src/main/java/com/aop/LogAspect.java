package com.aop;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.Message;
import com.pojo.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@Slf4j
/**
 * 用于切入Controller，记录请求的方法，参数，返回值。
 * */
public class LogAspect {

    private StopWatch stopWatch = new StopWatch();

    @Pointcut("execution(* com.controller.BaseController+.*(..))")
    public void logPoint() {
    }


    @Around("logPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("[controller=" + pjp.getTarget().getClass().getSimpleName())
                    .append("][method=" + pjp.getSignature().getName())
                    .append("][params=" + parseParam(pjp.getArgs()));
            stopWatch.start();

            Object result = pjp.proceed(pjp.getArgs());

            stopWatch.stop();

            sb.append("][result=" + parseObject(result));
            sb.append("][time=" + (stopWatch.getTime()));
            sb.append("]");

            log.info(sb.toString());


            return result;
        } catch (Throwable e) {
            sb.append("][error=" + e.getMessage());
            sb.append("][time=" + stopWatch.getTime());
            sb.append("]");

            log.info(sb.toString());
            throw e;
        } finally {
            stopWatch.reset();
        }
    }

    public static String parseParam(Object[] params) {
        if (Objects.isNull(params)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            sb.append(parseObject(param));
            sb.append(";");
        }
        return sb.toString();
    }

    public static String parseObject(Object data) {

        if (data instanceof Message) {
            return data.toString().replace('\n', ',');
        } else if (data instanceof Player) {
            return "playerId:" + ((Player) data).getPlayerId();
        } else {
            return JSON.toJSONString(data);
        }
    }
}
