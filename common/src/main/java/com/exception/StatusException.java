package com.exception;

public class StatusException extends Exception {

    int tip;

    public StatusException(Throwable t, int tip) {
        super(t);
        this.tip = tip;
    }

    public StatusException(int tip) {
        super();
        this.tip = tip;
    }

    public StatusException(String msg, int tip) {
        super(msg);
        this.tip = tip;
    }

    public int getTip() {
        return tip;
    }
}
