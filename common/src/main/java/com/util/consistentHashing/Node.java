package com.util.consistentHashing;


public interface Node<T> {


    String getVirtualNodeName(int index);


    int getWeight();


    T getResource();


}