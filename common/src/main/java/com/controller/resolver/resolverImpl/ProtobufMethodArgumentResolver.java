package com.controller.resolver.resolverImpl;

import com.controller.resolver.ActionMethodArgumentResolver;
import com.controller.resolver.MethodParameter;
import com.google.protobuf.Message;
import com.pojo.Packet;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class ProtobufMethodArgumentResolver implements ActionMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Message.class.isAssignableFrom(parameter.getClassType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Packet message) throws Exception {
        Class<? extends Message> messageProto = (Class<? extends Message>) parameter.getClassType();
        Constructor<? extends Message> cons = messageProto.getDeclaredConstructor();
        cons.setAccessible(true);
        return cons.newInstance().getParserForType().parseFrom(message.getData());

    }
}
