package com.exception.exceptionNeedSendToClient;


public class NoPlayerException extends ServerBusinessException {

    private static final long serialVersionUID = -4742832112872227456L;


    private String msg;

    public NoPlayerException() {
        super();
    }

    public NoPlayerException(Throwable t) {
        super(t);
    }

    public NoPlayerException(String msg) {
        super(msg);

        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
