package com.util.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 泛型二分查找
 * */
public class BinarySearch {

    /**
     * @param list 有序列表
     * @param lo 查找开始位置
     * @param hi 查找的结束位置
     * @param value 查找的元素
     * @param comparator 比较器
     * @return 如果找到 返回元素value的索引，否则返回 < 0
     * */
    public static <T> int binarySearch (List<T> list,int lo,int hi,T value, Comparator<? super T> comparator){

        if(comparator == null){
            throw new IllegalArgumentException("comparable can not be null!");
        }

        if(!checkList(list)){
            return 0;
        }

        checkBinarySearchBounds(lo, hi, list.size());

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final T midVal = list.get(mid);

            if (comparator.compare(midVal,value) < 0 ) {
                lo = mid + 1;
            } else if (comparator.compare(midVal,value) > 0) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present

    }

    /**
     * @param list 有序列表
     * @param value 查找的元素
     * @param comparator 比较器
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     * */
    public static  <T> int binarySearch (List<T> list,T value,Comparator<? super T> comparator){
        if(!checkList(list)){ return 0; }
        return binarySearch(list,0, list.size() - 1 ,value,comparator);
    }

    /**
     * @param list 有序列表，元素必须实现了Comparable接口
     * @param lo 查找开始位置
     * @param hi 查找的结束位置
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     * */
    public static  <T extends Comparable<T>> int binarySearch (List<T> list,int lo,int hi, T value){

        if(!checkList(list)){
            return 0;
        }
        checkBinarySearchBounds(lo,hi, list.size());

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final T midVal = list.get(mid);

            if (midVal.compareTo(value) < 0 ) {
                lo = mid + 1;
            } else if (midVal.compareTo(value) > 0) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present

    }

    /**
     * @param list 有序列表 元素必须实现了Comparable接口
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     * */
    public static <T extends Comparable<T>> int binarySearch (List<T> list, T value){
        if(!checkList(list)){ return 0; }
        return binarySearch(list,0, list.size() - 1 ,value);
    }

    /**
     * @param list true代表list非空，否则为false
     * */
    private static boolean checkList(List list){
        return list != null && !list.isEmpty();
    }

    private static void checkBinarySearchBounds(int startIndex, int endIndex, int length) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException();
        }
        if (startIndex < 0 || endIndex > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }




    public static void main(String[] args) {
        List<CCC> list = new ArrayList<>();
        for(int i = 1 ; i< 21000 ; ++i){
            list.add(new CCC(i));
        }

        List<CCC> list2 = list;
        long start = System.currentTimeMillis();
        for(int i=0;i<10000000;i++){
            BinarySearch.binarySearch(list2,new CCC(111));
        }
        System.out.println(System.currentTimeMillis()-start);

    }
    @AllArgsConstructor
    @Data
    public static class CCC implements Comparable<CCC>{
        private int a;

        @Override
        public boolean equals(Object o)
        {
            if(this==o)
            {
                return true;
            }
            if(o==null || getClass()!=o.getClass())
            {
                return false;
            }
            CCC common=(CCC)o;
            return a==common.a;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(a);
        }

        @Override
        public int compareTo(CCC o)
        {
            return Integer.compare(this.a,o.a);
        }
    }
}