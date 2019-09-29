package com.util;

import com.controller.ControllerHandler;
import com.controller.fun.Fun0;
import com.controller.fun.Fun1;
import com.controller.fun.Fun2;
import com.controller.fun.Fun3;
import com.controller.fun.Fun4;

public class ControllorUtil{

    public static Object handleMethod(ControllerHandler handler ,Object[] m){
        Object result = null;
        switch (handler.getFunType()) {
            case Fun0:
                result = (((Fun0) handler.getFun()).apply(handler.getAction()));
                break;
            case Fun1:
                result = (((Fun1) handler.getFun()).apply(handler.getAction(), m[0]));
                break;
            case Fun2:
                result = (((Fun2) handler.getFun()).apply(handler.getAction(), m[0], m[1]));
                break;
            case Fun3:
                result = (((Fun3) handler.getFun()).apply(handler.getAction(), m[0], m[1], m[2]));
                break;
            case Fun4:
                result = (((Fun4) handler.getFun()).apply(handler.getAction(), m[0], m[1], m[2], m[3]));
                break;
            default:
                System.out.println("default");
                break;
        }
        return result;
    }
}
