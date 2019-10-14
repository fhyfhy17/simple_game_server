package com.exception;

import lombok.ToString;

@ToString
public class WrapException extends RuntimeException {


    public WrapException(String msg, Throwable t) {
        super(msg, t);
    }
}
