package com.util;

public class RouteUtil {

    public static Route route(int msgId) {
        if (msgId == 10001) {
            return Route.LOGIN;
        } else if (msgId >= 30000) {
            return Route.X;
        } else if (msgId > 10001) {
            return Route.GAME;
        } else {
            return Route.X;
        }

    }

    public enum Route {
        LOGIN,
        GAME,
        X,
        ;
    }
}
