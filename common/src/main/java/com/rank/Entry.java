package com.rank;


import lombok.Data;

@Data
public class Entry<K, S> {

    private final K key;
    private final S score;

    Entry(K key, S score) {
        this.key = key;
        this.score = score;
    }
}
