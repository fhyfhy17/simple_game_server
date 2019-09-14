package com.rpc;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SettableFuture;
import com.Constant;
import com.annotation.Rpc;
import com.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class RpcProxy
{
	@Autowired
	private RpcHolder rpcHolder;

	//如果 hashKey为空，则为群发
	public <T> T proxy(Class<T> serviceInterface, @Nullable Object hashKey, TypeEnum.ServerTypeEnum serverType, long uid) {
		Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
			@Suspendable
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws ExecutionException, InterruptedException {
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
				String name = method.getDeclaringClass().getName();
				String methodName = method.getName();
				RpcRequest rpcRequest = new RpcRequest();
				rpcRequest.setId(Constant.RPC_REQUEST+UUID.randomUUID().toString());
				rpcRequest.setClassName(name);
				rpcRequest.setMethodName(methodName);
				rpcRequest.setParameters(args);
				Rpc rpc=method.getAnnotation(Rpc.class);
				if(!rpc.needResponse()){
					rpcHolder.sendRequest(rpcRequest, hashKey, serverType, uid, false);
					return null;
				}

				SettableFuture<RpcResponse> rpcResponseSettableFuture = rpcHolder.sendRequest(rpcRequest, hashKey, serverType, uid, true);
				RpcResponse rpcResponse = rpcResponseSettableFuture.get();
				return rpcResponse.getData();
			}
		});
		return (T)proxyInstance;
	}
}
