package com.controller;

import com.annotation.Controllor;
import com.annotation.Rpc;
import com.controller.fun.Fun0;
import com.controller.fun.Fun1;
import com.controller.fun.Fun2;
import com.controller.fun.Fun3;
import com.controller.fun.Fun4;
import com.controller.fun.FunType;
import com.google.common.collect.Maps;
import com.google.common.reflect.Reflection;
import com.google.protobuf.Message;
import com.util.Pair;
import com.util.ProtoUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class ControllerFactory {

    private static Map<Integer, ControllerHandler> controllerMap = Maps.newHashMap();
    private static Map<String, ControllerHandler> rpcControllerMap = Maps.newHashMap();

    public static void init() {
        Map<String, BaseController> allControllers = SpringUtils.getBeansOfType(BaseController.class);
        allControllers.values().forEach(controller -> {
            Method[] declaredMethods = controller.getClass().getDeclaredMethods();

            for (Method method : declaredMethods) {
//                if (method.getName().startsWith("CGLIB")) {
//                    continue;
//                }
                if(Objects.isNull(method.getAnnotation(Controllor.class))){
                    continue;
                }
                
                if(!Objects.isNull(method.getAnnotation(Rpc.class))){
                    MethodAccessor methodAccessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);

                    Pair<Object, FunType> add = addFunType(controller, method);

                    rpcControllerMap.put(method.getDeclaringClass().getGenericInterfaces()[0].getTypeName()+"_"+method.getName(), new ControllerHandler(controller, method, -1, methodAccessor, add.getValue(), add.getKey()));
                }else{
                    for (Class<?> parameterClass : method.getParameterTypes()) {
                        if (!Message.class.isAssignableFrom(parameterClass)) {
                            continue;
                        }
                        try {
                            Class cl = Class.forName(parameterClass.getName());
                            Method methodB = cl.getMethod("newBuilder");
                            Object obj = methodB.invoke(null, null);
                            Message.Builder msgBuilder = (Message.Builder) obj;
                            int msgId =ProtoUtil.protoGetMessageId(msgBuilder);
                            if (controllerMap.containsKey(msgId)) {
                                log.error("重复的msgid ={} controllerName ={} methodName ={}", msgId, controller.getClass().getSimpleName(), method.getName());
                                continue;
                            }

                            MethodAccessor methodAccessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);
            
                            Pair<Object, FunType> add = addFunType(controller, method);
            
                            controllerMap.put(msgId, new ControllerHandler(controller, method, msgId, methodAccessor, add.getValue(), add.getKey()));
            
                        } catch (IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
                            log.error("", e);
                        }
                        break;
                    }
                }
            }
        });
    }


    public static Pair<Object, FunType> addFunType(BaseController controller, Method method) {
        Pair<Object, FunType> pair = null;
        try {
            Object fun = null;
            FunType f = null;
            boolean isVoid = void.class.isAssignableFrom(method.getReturnType());
            switch (method.getParameters().length) {
                case 0:
                    fun = getFun0Obj(method, controller, isVoid);
                    f = FunType.Fun0;
                    break;
                case 1:
                    fun = getFun1Obj(method, controller, isVoid);
                    f = FunType.Fun1;
                    break;
                case 2:
                    fun = getFun2Obj(method, controller, isVoid);
                    f = FunType.Fun2;
                    break;
                case 3:
                    fun = getFun3Obj(method, controller, isVoid);
                    f = FunType.Fun3;
                    break;
                case 4:
                    fun = getFun4Obj(method, controller, isVoid);
                    f = FunType.Fun4;
                    break;
                default:
                    log.error("还没开那么多");
                    break;
            }
            pair = new Pair<>(fun, f);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return pair;
    }
    
    private static Object getFun0Obj(Method method, BaseController controller, boolean isVoid) {
     
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site2 = null;
        try {
            site2 = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Fun0.class),
                    MethodType.methodType(isVoid ? void.class : Object.class, Object.class),
                    lookup.findVirtual(controller.getClass(), method.getName(), MethodType.methodType(method.getReturnType())),
                    MethodType.methodType(method.getReturnType(), controller.getClass())
            );
            
        } catch (LambdaConversionException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Fun0) site2.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    
    
    private static Object getFun1Obj(Method method, BaseController controller, boolean isVoid) {
        Class<?>[] types = method.getParameterTypes();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site2 = null;
        try {
            site2 = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Fun1.class),
                    MethodType.methodType(isVoid ? void.class : Object.class, Object.class, Object.class),
                    lookup.findVirtual(controller.getClass(), method.getName(), MethodType.methodType(method.getReturnType(), types[0])),
                    MethodType.methodType(method.getReturnType(), controller.getClass(), types[0])
            );

        } catch (LambdaConversionException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Fun1) site2.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    private static Object getFun2Obj(Method method, BaseController controller, boolean isVoid) {
        Class<?>[] types = method.getParameterTypes();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site2 = null;
        try {
            site2 = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Fun2.class),
                    MethodType.methodType(isVoid ? void.class : Object.class, Object.class, Object.class, Object.class),
                    lookup.findVirtual(controller.getClass(), method.getName(), MethodType.methodType(method.getReturnType(), types[0], types[1])),
                    MethodType.methodType(method.getReturnType(), controller.getClass(), types[0], types[1])
            );

        } catch (LambdaConversionException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Fun2) site2.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    private static Object getFun3Obj(Method method, BaseController controller, boolean isVoid) {
        Class<?>[] types = method.getParameterTypes();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site2 = null;
        try {
            site2 = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Fun3.class),
                    MethodType.methodType(isVoid ? void.class : Object.class, Object.class, Object.class, Object.class, Object.class),
                    lookup.findVirtual(controller.getClass(), method.getName(), MethodType.methodType(method.getReturnType(), types[0], types[1], types[2])),
                    MethodType.methodType(method.getReturnType(), controller.getClass(), types[0], types[1], types[2])
            );

        } catch (LambdaConversionException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Fun3) site2.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private static Object getFun4Obj(Method method, BaseController controller, boolean isVoid) {
        Class<?>[] types = method.getParameterTypes();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site2 = null;
        try {
            site2 = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Fun4.class),
                    MethodType.methodType(isVoid ? void.class : Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
                    lookup.findVirtual(controller.getClass(), method.getName(), MethodType.methodType(method.getReturnType(), types[0], types[1], types[2], types[3])),
                    MethodType.methodType(method.getReturnType(), controller.getClass(), types[0], types[1], types[2], types[3])
            );

        } catch (LambdaConversionException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Fun4) site2.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, ControllerHandler> getControllerMap() {
        return controllerMap;
    }
    public static Map<String, ControllerHandler> getRpcControllerMap() {
        return rpcControllerMap;
    }
}
