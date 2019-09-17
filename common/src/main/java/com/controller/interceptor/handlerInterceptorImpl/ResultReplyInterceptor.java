package com.controller.interceptor.handlerInterceptorImpl;

import com.controller.interceptor.HandlerInterceptor;
import com.google.protobuf.Message;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(5)
@Component
//结果拦截器 （根据执行完消息返回的结果，执行回消息操作）
public class ResultReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message, Object result) {

        if(!Message.class.isAssignableFrom(result.getClass())){
            return;
        }

        Packet messageResult = buildMessage((Message) result, message);
        ServerInfoManager.sendMessage(message.getFrom(), messageResult);

    }

    private Packet buildMessage(Message resultMessage,Packet message) {
        Packet  messageResult = new Packet();
        messageResult.setId(ProtoUtil.protoGetMessageId(resultMessage));
        messageResult.setUid(message.getUid());
        messageResult.setFrom(ContextUtil.id);
        messageResult.setGate(message.getGate());
        messageResult.setData(resultMessage.toByteArray());
        return messageResult;
    }
}
