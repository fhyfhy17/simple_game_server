package com.util.monad;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @ClassName FunctorOptional
 * @Description TODO
 * @Author dafeng
 * @Date 2019/1/23 15:48
 **/
public class FunctorOptional<T> implements Functor<T> {

    private final T value;

    public FunctorOptional(T value) {
        this.value = value;
    }

    public static <T> FunctorOptional<T> empty() {
        return new FunctorOptional<>(null);
    }

    public static <T> FunctorOptional<T> of(T value) {
        return new FunctorOptional<>(value);
    }

    @Override
    public <R> FunctorOptional<R> map(Function<T, R> f) {
        if (value == null) {
            return empty();
        }
        R apply = f.apply(value);
        return new FunctorOptional<>(apply);
    }

    public <R> FunctorOptional<R> flatMap(Function<? super T, FunctorOptional<R>> mapper) {
        if (!isPresent())
            return empty();
        else {
            return mapper.apply(value);
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    public T get() {
        if (value == null)
            throw new RuntimeException("不能取得");
        return value;
    }

}
