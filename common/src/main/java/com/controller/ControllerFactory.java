package com.controller;

import com.annotation.Controllor;
import com.annotation.Rpc;
import com.controller.fun.*;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.pojo.Pair;
import com.util.ProtoUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;


@Slf4j
public class ControllerFactory {

    private static Map<Integer, ControllerHandler> controllerMap = Maps.newHashMap();
    private static Map<String, ControllerHandler> rpcControllerMap = Maps.newHashMap();

    public static void init() {
        Map<String, BaseController> allControllers = SpringUtils.getBeansOfType(BaseController.class);
        allControllers.values().forEach(controller -> {
            Method[] declaredMethods = controller.getClass().getDeclaredMethods();
    
           
            
            for (Method method : declaredMethods) {
    
                Controllor annotation=AnnotationUtils.findAnnotation(method,Controllor.class);
                if(Objects.isNull(annotation)){
                    continue;
                }
                
                if(!Objects.isNull(AnnotationUtils.findAnnotation(method,Rpc.class))){
                    //MethodAccessor methodAccessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);
                    Pair<Object, FunType> add = addFunType(controller, method);
                    Method rawMethod = SpringUtils.getRawMethod(method);
                    rpcControllerMap.put(rawMethod.getDeclaringClass().getGenericInterfaces()[0].getTypeName() + "_" + rawMethod.getName(), new ControllerHandler(controller, rawMethod, -1, add.getValue(), add.getKey()));
                } else {
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

                            //MethodAccessor methodAccessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);
            
                            Pair<Object, FunType> add = addFunType(controller, method);
            
                            controllerMap.put(msgId, new ControllerHandler(controller, method, msgId, add.getValue(), add.getKey()));
            
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
                    MethodType.methodType(isVoid?void.class:Object.class,Object.class),
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
                    MethodType.methodType(isVoid?void.class:Object.class,Object.class,Object.class),
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
					MethodType.methodType(isVoid?void.class:Object.class,Object.class,Object.class,Object.class),
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
					MethodType.methodType(isVoid?void.class:Object.class,Object.class,Object.class,Object.class,Object.class),
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
					MethodType.methodType(isVoid?void.class:Object.class,Object.class,Object.class,Object.class,Object.class,Object.class),
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
    
    
    /**
     * jdk的修改后的处理方法，还未看明白
     * */
    @SuppressWarnings("unchecked")
    public static BiConsumer createSetter(final MethodHandles.Lookup lookup,
                                          final MethodHandle setter,
                                          final Class<?> valueType) throws Exception {
        try {
            if (valueType.isPrimitive()) {
                if (valueType == double.class) {
                    ObjDoubleConsumer consumer = (ObjDoubleConsumer) createSetterCallSite(
                            lookup, setter, ObjDoubleConsumer.class, double.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (double) b);
                } else if (valueType == int.class) {
                    ObjIntConsumer consumer = (ObjIntConsumer) createSetterCallSite(
                            lookup, setter, ObjIntConsumer.class, int.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (int) b);
                } else if (valueType == long.class) {
                    ObjLongConsumer consumer = (ObjLongConsumer) createSetterCallSite(
                            lookup, setter, ObjLongConsumer.class, long.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (long) b);
                } else {
                    // Real code needs to support short, char, boolean, byte, and float according to pattern above
                    throw new RuntimeException("Type is not supported yet: " + valueType.getName());
                }
            } else {
                return (BiConsumer) createSetterCallSite(lookup, setter, BiConsumer.class, Object.class)
                        .getTarget().invokeExact();
            }
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Error(e);
        }
    }
    private static CallSite createSetterCallSite(MethodHandles.Lookup lookup, MethodHandle setter, Class<?> interfaceType, Class<?> valueType) throws LambdaConversionException {
        return LambdaMetafactory.metafactory(lookup,
                "accept",
                MethodType.methodType(interfaceType),
                MethodType.methodType(void.class, Object.class, valueType), //signature of method SomeConsumer.accept after type erasure
                setter,
                setter.type());
    }
    

    public static Map<Integer, ControllerHandler> getControllerMap() {
        return controllerMap;
    }
    public static Map<String, ControllerHandler> getRpcControllerMap() {
        return rpcControllerMap;
    }
}
