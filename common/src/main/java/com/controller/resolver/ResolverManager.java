package com.controller.resolver;

import com.pojo.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResolverManager {

    private static List<ActionMethodArgumentResolver> actionMethodArgumentResolvers;

    public static Object resolve(MethodParameter parameter, Packet message) throws Exception {
        for (ActionMethodArgumentResolver actionMethodArgumentResolver : actionMethodArgumentResolvers) {
            if (actionMethodArgumentResolver.supportsParameter(parameter)) {
                return actionMethodArgumentResolver.resolveArgument(parameter, message);
            }
        }
        return null;
    }

    @Autowired
    public void setActionMethodArgumentResolvers(List<ActionMethodArgumentResolver> actionMethodArgumentResolvers) {
        ResolverManager.actionMethodArgumentResolvers = actionMethodArgumentResolvers;
    }
}