package com.exception;


public class NoEnumTypeException extends RuntimeException {

    private static final long serialVersionUID = -4742821342872227456L;


    private String msg;

    public NoEnumTypeException() {
        super();
    }

    public NoEnumTypeException(Throwable t) {
        super(t);
    }

    public NoEnumTypeException(String msg) {
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
