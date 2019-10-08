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
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Componentß
@Slf4j
public class RpcHolder {
    private static final int overtime = 5 * 1000; //超时时间
    private ConcurrentHashMap<String, FutureContext> requestContext = new ConcurrentHashMap<>();

    public SettableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest, Object hashKey, TypeEnum.ServerTypeEnum serverType, long uid, boolean needResponse, boolean self) {
        String requestId = rpcRequest.getId();
        SettableFuture<RpcResponse> future = null;
        if (needResponse) {
            future = new SettableFuture<>();
            requestContext.put(requestId, new FutureContext(requestId, future, System.currentTimeMillis()));
        }

//        else{
        // 无返回的，也存，
        // 这个再考虑吧，存起来是为了处理异常
        // 或者是，如果加上重发，一直等待被调用服务器启动？  这样会不会调用方被憋爆了啊， 这还得以后根据业务慢慢完善
        // 而且有返回的，如果一直重发，就会导致一直hang住，设计越复杂， 越是不稳定啊 ， 现在就这样吧，消息丢了就丢了，先在业务上适应，不要在一大堆修改后面加个有修改属性的rpc,
        // 不过貌似也很难限制，自己操作一堆修改角色属性，最后再发个加工会贡献值的 ，这种需求也挺常见的，所以规定rpc修改属性必须要用返回值还是有必要，
        // 另外考虑，如果bus挂了，那么我个人的操作全无法执行，这样合理不合理，还是合理的，因为bus没了，实际确实也干不了啥了。所以纠结的点就是挂的当时，个人的操作算不算完成
        // 如果算完成，就可以 发无需返回值的修改属性的rpc. 如果不算完成，就不允许发无返回值的修改属性的rpc.
        // 个人倾向于两种都行，毕竟服挂了错了就错了，一种是工会信息错了，一种是个人信息错了。
        // 方案还是针对实际情况实际处理，比如工会的贡献可以由成员等级和捐献记录重新算出来，  再比如如果个人可以因工会等级而产生buff，那么工会升级那一刻有个game挂了，等它再启动也可以修复数据
//            requestContext.put(requestId,new FutureContext(requestId,null,System.currentTimeMillis()));
//        }

        Assert.notNull(requestId, "requestId 不能为空");
        Packet packet = ProtoUtil.buildRpcRequestMessage(ProtostuffUtil.serializeObject(rpcRequest, RpcRequest.class), uid, ContextUtil.id, requestId);
        if (self) {
            ServerInfoManager.sendMessage(ContextUtil.id, packet);
            return null;
        }

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

        futureContext.future.set(rpcResponse);

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
