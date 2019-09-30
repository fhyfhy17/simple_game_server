package com.util;

import com.exception.StatusException;
import com.google.protobuf.Message;
import com.manager.ServerInfoManager;
import com.pojo.Packet;
import com.util.support.TipStatus;

import java.util.Objects;

public class ExceptionUtil {
    /**
     * 在lambda里Throw异常
     *
     * @param e
     * @param <E>
     * @throws E
     */
    public static <E extends Throwable> void doThrow(Throwable e) throws E {
        if (!Objects.isNull(e))
            throw (E) e;
    }

    /**
     * 给前端返回提示性异常
     *
     */
    public static void sendStatusExceptionToClient(Class<?> returnType, Packet packet, StatusException se) {
        // Status报错， 执行方法时，抛出主动定义的错误，方便多层调用时无法中断方法，这里主动回复给有result参数的协议
        if (!Message.class.isAssignableFrom(returnType)) {
           return;
        }
      
        Message.Builder builder = ProtoUtil.setFieldByName(ProtoUtil.createBuilerByClassName(returnType.getName()), "result", TipStatus.fail(se.getTip()));
        Packet message1 = ProtoUtil.buildMessage(builder.build(), packet.getUid(), null);
        ServerInfoManager.sendMessage(packet.getGate(), message1);
        
    }
}
