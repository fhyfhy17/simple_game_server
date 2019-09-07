package com.util.monad;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName FunctorList
 * @Description TODO
 * @Author dafeng
 * @Date 2019/1/23 16:18
 **/
public class FunctorList<T> implements Functor<T> {
    private final ImmutableList<T> list;

    public FunctorList(Iterable<T> value) {
        this.list = ImmutableList.copyOf(value);
    }


    @Override
    public <R> FunctorList<R> map(Function<T, R> f) {
        List<R> collect = list.stream().map(f).collect(Collectors.toList());
        return new FunctorList<>(collect);
    }

    public ImmutableList<T> get() {
        return list;
    }
}
