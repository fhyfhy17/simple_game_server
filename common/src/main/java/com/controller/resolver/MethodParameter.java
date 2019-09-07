package com.controller.resolver;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class MethodParameter {
    private final Method method;
    private final int index;
    private Class<?> classType;
}
