package com.util.monad;

import java.util.function.Function;

/**
 * @ClassName Functor
 * @Description 函子
 * @Author dafeng
 * @Date 2019/1/23 12:29
 **/
public interface Functor<T> {
    <R> Functor<R> map(Function<T, R> f);

}


