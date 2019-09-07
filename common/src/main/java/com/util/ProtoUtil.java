package com.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.net.msg.Options;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

@Slf4j
public class ProtoUtil {

    public static Message.Builder createBuilerByClassName(String name) {
        Message.Builder msgBuilder = null;
        try {
            Class cl = Class.forName(name);
            Method methodB = cl.getMethod("newBuilder");
            Object obj = methodB.invoke(null, null);
            msgBuilder = (Message.Builder) obj;
        } catch (Exception e) {
            log.error("", e);
        }

        return msgBuilder;
    }


    public static Message.Builder setFieldByName(Message.Builder builder, String name, Object value) {
        Descriptors.FieldDescriptor fieldDescriptor = builder.getDescriptorForType().findFieldByName(name);
        if (fieldDescriptor == null) {
            log.error("统一回复 StatusException 时,没有找到  result 字段 ");
        }
        if (value == null) {
            builder.clearField(fieldDescriptor);
        } else {
            builder.setField(fieldDescriptor, value);
        }
        return builder;
    }

    public static Packet buildMessage(Message proto,long uid,@Nullable String from) {
        Packet messageResult = new Packet();
        messageResult.setId(protoGetMessageId(proto));
        messageResult.setUid(uid);
        messageResult.setFrom(null == from ? ContextUtil.id : from);
        messageResult.setData(proto.toByteArray());
        return messageResult;
    }
    
    public static Packet buildRpcRequstMessage(byte[] data,long uid,@Nullable String from) {
        Packet messageResult = new Packet();
        messageResult.setId(-1);
        messageResult.setUid(uid);
        messageResult.setFrom(null == from ? ContextUtil.id : from);
        messageResult.setData(data);
        return messageResult;
    }
    
    public static Packet buildRpcResponseMessage(byte[] data,long uid,@Nullable String from) {
        Packet messageResult = new Packet();
        messageResult.setId(-2);
        messageResult.setUid(uid);
        messageResult.setFrom(null == from ? ContextUtil.id : from);
        messageResult.setData(data);
        return messageResult;
    }
    
    public static  int protoGetMessageId(Message.Builder builder){
        return  builder.getDescriptorForType().getOptions().getExtension(Options.messageId);
    }
    public static  int protoGetMessageId(Message message){
        return message.getDescriptorForType().getOptions().getExtension(Options.messageId);
    }
}
