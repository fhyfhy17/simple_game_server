package com.controller.interceptor.handlerInterceptorImpl;

import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Order(5)
@Component
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）
public class ResultReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message,ControllerHandler handler,com.google.protobuf.Message result) {
        if (Objects.isNull(result)) {
            return;
        }

        Packet messageResult = buildMessage(result, message);
        ServerInfoManager.sendMessage(message.getFrom(), messageResult);

    }

    private Packet buildMessage(com.google.protobuf.Message resultMessage,Packet message) {
        Packet messageResult = new Packet();
        messageResult.setId(ProtoUtil.protoGetMessageId(resultMessage));
        messageResult.setUid(message.getUid());
        messageResult.setFrom(ContextUtil.id);
        messageResult.setGate(message.getGate());
        messageResult.setData(resultMessage.toByteArray());
        return messageResult;
    }
}
