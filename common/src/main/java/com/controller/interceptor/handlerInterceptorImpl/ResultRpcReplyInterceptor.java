package com.controller.interceptor.handlerInterceptorImpl;

import com.Constant;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.rpc.RpcResponse;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import com.util.StringUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Order(5)
@Component
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）  RPC
public class ResultRpcReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message, Object result) {
        if(!StringUtil.contains(message.getRpc(),Constant.RPC_REQUEST)){
            return;
        }

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(message.getRpc());
        rpcResponse.setData(result);
        ServerInfoManager.sendMessage(message.getFrom(), ProtoUtil.buildRpcResponseMessage(ProtostuffUtil.serializeObject(rpcResponse, RpcResponse.class), message.getUid(), ContextUtil.id));
        //log.info("响应 发回去 的  "+ System.currentTimeMillis());
    }

}
