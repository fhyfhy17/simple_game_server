package com.rank;

import java.util.Comparator;

/**
 * 分数排序器
 */
public interface ScoreComparator<T> extends Comparator<T> {

    @Override
    int compare(T o1, T o2);
}
