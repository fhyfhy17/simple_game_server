package com.annotation;

import com.enums.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMethod {
    EventType value();

    String executor() default "selfExecutor";//self就是在本线程执行，同步的，其它Executor是异步的。
}
