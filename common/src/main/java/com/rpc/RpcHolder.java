package com.rpc;

import com.enums.TypeEnum;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RpcHolder {
    private ConcurrentHashMap<String, FutureContext> requestContext = new ConcurrentHashMap<>();

    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest,Object hashKey,TypeEnum.ServerTypeEnum serverType,long uid,boolean needResponse) {
        String requestId = rpcRequest.getId();
        CompletableFuture<RpcResponse> future = null;
        if (needResponse) {
            future = new CompletableFuture<>();
            requestContext.put(requestId, new FutureContext(requestId, future, System.currentTimeMillis()));
        }

        Assert.notNull(requestId, "requestId 不能为空");
        Packet packet = ProtoUtil.buildRpcRequestMessage(ProtostuffUtil.serializeObject(rpcRequest, RpcRequest.class), uid, ContextUtil.id, requestId);

        if (Objects.isNull(hashKey)) {
            ServerInfoManager.sendMessageForTypeAll(serverType, packet);
        } else {
            ServerInfoManager.sendMessage(ServerInfoManager.hashChooseServer(hashKey, serverType), packet);
        }
        return future;
    }


    public void sendRequestGroupSend(RpcRequest rpcRequest, TypeEnum.ServerTypeEnum serverType, long uid) {
        String requestId = rpcRequest.getId();
        Assert.notNull(requestId, "requestId 不能为空");
        Packet packet = ProtoUtil.buildRpcRequestMessage(ProtostuffUtil.serializeObject(rpcRequest, RpcRequest.class), uid, ContextUtil.id, requestId);
        ServerInfoManager.sendMessageForTypeAll(serverType, packet);

    }


    public void receiveResponse(RpcResponse rpcResponse) {
        String requestId = rpcResponse.getRequestId();
        FutureContext futureContext = requestContext.remove(requestId);

        if (Objects.isNull(futureContext)) {
            log.error("rpcResponseFuture,原因是收到了多条同requestID 回复 requestId = {}，也有可能是超时被删", requestId);
            return;
        }

        futureContext.future.complete(rpcResponse);

    }

    //@Scheduled(fixedDelay = 1000)
    ////检查超时
    ////重试并没有意义，如果5秒不返回，直接超时处理
    //public void checkOvertime() {
    //    Iterator<FutureContext> it = requestContext.values().iterator();
    //    while (it.hasNext()) {
    //        FutureContext next = it.next();
    //        if (System.currentTimeMillis() - next.startTime > overtime) {
    //            it.remove();
    //            next.future.completeExceptionally(new StatusException(TipType.VisitOverTime));
    //        }
    //    }
    //}

    @AllArgsConstructor
    private static class FutureContext {
        private String requestId;
        private CompletableFuture<RpcResponse> future;
        private long startTime;
    }
}
