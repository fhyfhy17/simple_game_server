package com.rpc;

import co.paralleluniverse.strands.SettableFuture;
import com.enums.TypeEnum;
import com.manager.ServerInfoManager;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RpcHolder{
	private ConcurrentHashMap<String,SettableFuture<RpcResponse>> requestContext = new ConcurrentHashMap<>();
	
	public SettableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest,Object hashkey,TypeEnum.ServerTypeEnum serverType,long uid,boolean needResponse) {
		String requestId = rpcRequest.getId();
		SettableFuture<RpcResponse> future = null;
		if(needResponse){
			future = new SettableFuture<>();
			requestContext.put(requestId, future);
		}
		Assert.notNull(requestId, "requestId 不能为空");
		String s=ServerInfoManager.hashChooseServer(hashkey,serverType);
		ServerInfoManager.sendMessage(s,ProtoUtil.buildRpcRequstMessage(ProtostuffUtil.serializeObject(rpcRequest,RpcRequest.class),uid,null));
		return future;
	}
	
	public void receiveResponse(RpcResponse rpcResponse){
		String requestId = rpcResponse.getRequestId();
		SettableFuture<RpcResponse> rpcResponseFuture = requestContext.remove(requestId);
		rpcResponseFuture.set(rpcResponse);
	}
}
