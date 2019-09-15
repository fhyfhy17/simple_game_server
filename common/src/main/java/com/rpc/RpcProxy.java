package com.rpc;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SettableFuture;
import com.Constant;
import com.annotation.Rpc;
import com.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class RpcProxy {
    @Autowired
    private RpcHolder rpcHolder;

    //rpc使用限制
    // 1 群发必须是不能改变属性的，无返回的。如果有类似于，帮派升级了给每个人加钱这种，即改变属性又群发的，必须改成发邮件
    // 2 无需返回的消息，必须不能改变属性，如果改变属性，必须为有返回的消息，因为改变属性说明是强需求，不要求返回如果有超时错误，则无法处理
    // * 以上说所改变属性，基本上是针对个人、组队、工会这种。 如果是bus通知各服务器改变状态，不属于上述规则
    // * rpc还不成熟，待完善，并且还无法处理类似 ，如果bus服挂了，game做了一个类似于个人升级了，给工会涨经验这种功能。这样工会涨经验的这次机会就失去了，也就是还没有做到一致性
    public <T> T proxy(Class<T> serviceInterface, Object hashKey, TypeEnum.ServerTypeEnum serverType, long uid) {
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            @Suspendable
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws ExecutionException, InterruptedException {
                Object o = exclude(proxy, method, args);
                if (!Objects.isNull(o)) {
                    return o;
                }
                RpcRequest rpcRequest = makeRequest(method, args);
                Rpc rpc = method.getAnnotation(Rpc.class);
                if (!rpc.needResponse()) {
                    rpcHolder.sendRequest(rpcRequest, hashKey, serverType, uid, false);
                    return null;
                }

                SettableFuture<RpcResponse> rpcResponseSettableFuture = rpcHolder.sendRequest(rpcRequest, hashKey, serverType, uid, true);
                RpcResponse rpcResponse = rpcResponseSettableFuture.get();
                return rpcResponse.getData();
            }
        });
        return (T) proxyInstance;
    }

    //群发，无返回值
    public <T> T proxyGroupSend(Class<T> serviceInterface, TypeEnum.ServerTypeEnum serverType, long uid) {
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            @Suspendable
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                Object o = exclude(proxy, method, args);
                if (!Objects.isNull(o)) {
                    return o;
                }
                RpcRequest rpcRequest = makeRequest(method, args);
                rpcHolder.sendRequestGroupSend(rpcRequest, serverType, uid);
                return null;
            }
        });
        return (T) proxyInstance;
    }

    //排除方法
    private Object exclude(Object proxy, Method method, Object[] args) {
        if (Object.class.equals(method.getDeclaringClass())) {
            switch (method.getName()) {
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return proxy.getClass().getName() + "@" +
                            Integer.toHexString(System.identityHashCode(proxy)) +
                            ", with InvocationHandler " + this;
                default:
                    throw new IllegalStateException(String.valueOf(method));
            }
        }
        return null;
    }

    private RpcRequest makeRequest(Method method, Object[] args) {
        String name = method.getDeclaringClass().getName();
        String methodName = method.getName();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setId(Constant.RPC_REQUEST + UUID.randomUUID().toString());
        rpcRequest.setClassName(name);
        rpcRequest.setMethodName(methodName);
        rpcRequest.setParameters(args);
        return rpcRequest;
    }
}
