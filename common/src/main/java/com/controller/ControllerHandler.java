package com.controller;

import com.controller.fun.FunType;
import com.controller.resolver.MethodParameter;
import com.controller.resolver.ResolverManager;
import com.pojo.Packet;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.MethodAccessor;

import java.lang.reflect.Method;
import java.util.Objects;

@Data
public class ControllerHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);
    private BaseController action;
    private Method method;
    private MethodParameter[] parameters;
    private int msgId;
    private MethodAccessor methodAccessor;
    private FunType funType;
    private Object fun;

    public ControllerHandler(BaseController action, Method method, int msgId, MethodAccessor methodAccessor, FunType funType, Object fun) {

        this.action = action;
        this.method = method;
        this.msgId = msgId;
        this.methodAccessor = methodAccessor;
        this.funType = funType;
        this.fun = fun;
        Class<?>[] parameterTypes = this.method.getParameterTypes();
        MethodParameter[] parameters = new MethodParameter[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = new MethodParameter(method, i, this.method.getParameterTypes()[i]);
        }
        this.parameters = parameters;
    }

    public Object invokeForController(Packet message) throws Exception {
        Object[] args = getMethodArgumentValues(message);
        return this.methodAccessor.invoke(this.action, args);

    }

    public Object[] getMethodArgumentValues(Packet message) throws Exception {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParameter = this.parameters[i];
            Object o = ResolverManager.resolve(methodParameter, message);
            if (Objects.isNull(o)) {
                throw new IllegalArgumentException("出现了不支持的参数 = " + methodParameter);
            }
            args[i] = o;
        }
        return args;
    }

    public int getMsgId() {
        return msgId;
    }
}
