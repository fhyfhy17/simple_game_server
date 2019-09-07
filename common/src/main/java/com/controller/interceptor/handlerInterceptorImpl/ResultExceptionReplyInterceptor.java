package com.controller.interceptor.handlerInterceptorImpl;

import com.Constant;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.manager.ServerInfoManager;
import com.net.msg.LOGIN_MSG;
import com.pojo.Packet;
import com.util.ContextUtil;
import com.util.ProtoUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(4)
@Component
//结果拦截器 （如果报错了，给客户端返回的消息）
//只针对前端消息，服务之间暂时不需要
public class ResultExceptionReplyInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(Packet message,ControllerHandler handler,com.google.protobuf.Message result) {
        if (!Constant.DEFAULT_ERROR_REPLY.equals(result)) {
            return;
        }

        Packet messageResult = new Packet();
        LOGIN_MSG.GTC_UNIFIED_EXCEPTION.Builder builder = LOGIN_MSG.GTC_UNIFIED_EXCEPTION.newBuilder();
        builder.setMsg("服务器报错!");

        messageResult.setId(ProtoUtil.protoGetMessageId(builder));
        messageResult.setUid(message.getUid());
        messageResult.setFrom(ContextUtil.id);
        messageResult.setData(builder.build().toByteArray());

        ServerInfoManager.sendMessage(message.getGate(), messageResult);
    }


}
