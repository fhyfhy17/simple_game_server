package com.controller.resolver;

import com.pojo.Packet;

//TODO Player参数实现
public interface ActionMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter parameter, Packet message) throws Exception;
}
