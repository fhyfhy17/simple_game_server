package com.pojo;

import reactor.util.annotation.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 和Pair的区别的 ，这个可以为空，Pair不可以为空
 */
public class Tuple<T1, T2> implements Iterable<Object>, Serializable {

    private static final long serialVersionUID = -351808204523422L;


    T1 t1;
    T2 t2;

    public Tuple(T1 t1,T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public Tuple() {

    }


    public T1 getKey() {
        return t1;
    }


    public T2 getValue() {
        return t2;
    }

    public void setKey(T1 t1) {
        this.t1 = t1;
    }

    public void setValue(T2 t2) {
        this.t2 = t2;
    }

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    @Nullable
    public Object get(int index) {
        switch (index) {
            case 0:
                return t1;
            case 1:
                return t2;
            default:
                return null;
        }
    }

    /**
     * Turn this {@literal Tuples} into a plain Object list.
     *
     * @return A new Object list.
     */
    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    /**
     * Turn this {@literal Tuples} into a plain Object array.
     *
     * @return A new Object array.
     */
    public Object[] toArray() {
        return new Object[]{t1, t2};
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableList(toList()).iterator();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple<?, ?> pair = (Tuple<?, ?>) o;

        return t1.equals(pair.t1) && t2.equals(pair.t2);

    }

    @Override
    public int hashCode() {
        int result = size();
        result = 31 * result + t1.hashCode();
        result = 31 * result + t2.hashCode();
        return result;
    }

    public int size() {
        return 2;
    }


}