package com.util.monad;

import java.util.function.Function;

/**
 * @ClassName FunctorImpl
 * @Description 函子具体实现
 * @Author dafeng
 * @Date 2019/1/23 15:39
 **/
public class FunctorImpl<T> implements Functor<T> {

    private final T value;

    public FunctorImpl(T value) {
        this.value = value;
    }

    @Override
    public <R> FunctorImpl<R> map(Function<T, R> f) {
        R apply = f.apply(value);
        return new FunctorImpl<>(apply);
    }

    public <R> FunctorImpl<R> flatmap(Function<T, FunctorImpl<R>> mapper) {
        return mapper.apply(value);
    }

    public T get() {
        return value;
    }
}
