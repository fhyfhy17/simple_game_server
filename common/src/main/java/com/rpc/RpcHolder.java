package com.rpc;

import co.paralleluniverse.strands.SettableFuture;
import com.enums.TypeEnum;
import com.exception.StatusException;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.template.templates.type.TipType;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RpcHolder {
    private static final int overtime = 5 * 1000; //超时时间
    private ConcurrentHashMap<String, FutureContext> requestContext = new ConcurrentHashMap<>();

    public SettableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest, Object hashKey, TypeEnum.ServerTypeEnum serverType, long uid, boolean needResponse) {
        String requestId = rpcRequest.getId();
        SettableFuture<RpcResponse> future = null;
        if (needResponse) {
            future = new SettableFuture<>();
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

    public void receiveResponse(RpcResponse rpcResponse) {
        String requestId = rpcResponse.getRequestId();
        int code = rpcResponse.getCode();
        FutureContext futureContext = requestContext.remove(requestId);

        if (Objects.isNull(futureContext)) {
            log.error("rpcResponseFuture,原因是收到了多条同requestID 回复 requestId = {}，也有可能是超时被删", requestId);
            return;
        }

        if (code > 0) {
            futureContext.future.setException(new StatusException(code));
        } else {
            futureContext.future.set(rpcResponse);
        }

    }

    @Scheduled(fixedDelay = 1000)
    //检查超时
    //重试并没有意义，如果5秒不返回，直接超时处理
    public void checkOvertime() {
        Iterator<FutureContext> it = requestContext.values().iterator();
        while (it.hasNext()) {
            FutureContext next = it.next();
            if (System.currentTimeMillis() - next.startTime > overtime) {
                it.remove();
                next.future.setException(new StatusException(TipType.VisitOverTime));
            }
        }
    }

    @AllArgsConstructor
    private static class FutureContext {
        private String requestId;
        private SettableFuture<RpcResponse> future;
        private long startTime;
    }
}
