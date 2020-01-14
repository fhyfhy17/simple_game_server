package com.rank;

/**
 * 跳表层级
 */
public class SkipListLevel<K, S> {

    /**
     * 每层对应1个后向指针 (后继节点)
     */
    SkipListNode<K, S> forward;
    /**
     * 到后继节点之间的跨度 它表示当前的指针跨越了多少个节点。span用于计算成员排名(rank)，这是Redis对于SkipList做的一个扩展。
     */
    int span;
}