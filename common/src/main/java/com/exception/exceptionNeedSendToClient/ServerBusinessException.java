package com.exception.exceptionNeedSendToClient;

public class ServerBusinessException extends Exception {

    public ServerBusinessException(Throwable t) {
        super(t);
    }

    public ServerBusinessException() {
        super();
    }

    public ServerBusinessException(String msg) {
        super(msg);
    }
}
