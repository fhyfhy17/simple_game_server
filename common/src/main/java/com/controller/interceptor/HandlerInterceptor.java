package com.controller.interceptor;

import com.controller.ControllerHandler;
import com.pojo.Packet;

//TODO 拦截器可以实现 白名单黑名单功能，类似于多少级以下的不能访问某些协议之类的
// 好像实现的不怎么样啊,这里取不到别的参数，只能拿到 消息ID，这样基本的数据。。。要不要考虑把转换参数放在它前面。
// 这里的操作可以有很多,有实际项目了，还是好好改改。开启关闭某些模块 ，果然还是有个Module的比较好，可以过不去preHandle 不执行post
// 但还是要把消息错误提示返回去。
public interface HandlerInterceptor {
    default boolean preHandle(Packet message,ControllerHandler handler) {
        return true;
    }

    default void postHandle(Packet message,ControllerHandler handler,com.google.protobuf.Message result) {
    }
}
