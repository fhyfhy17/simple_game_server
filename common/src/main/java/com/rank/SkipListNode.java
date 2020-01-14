package com.rank;

/**
 * 跳表节点
 */
public class SkipListNode<K, S> {

    /**
     * 节点对应的数据id
     */
    final K key;
    /**
     * 该节点数据对应的评分 - 如果要通用的话，这里将来将是一个泛型对象，需要实现{@link Comparable}。
     */
    final S score;
    /**
     * 该节点的层级信息 level[]存放指向各层链表后一个节点的指针（后向指针）。
     */
    final SkipListLevel<K, S>[] levelInfo;
    /**
     * 该节点的前向指针
     * <b>NOTE:</b>(不包含header)
     * backward字段是指向链表前一个节点的指针（前向指针）。 节点只有1个前向指针，所以只有第1层链表是一个双向链表。
     */
    SkipListNode<K, S> backward;

    SkipListNode(K key, S score, SkipListLevel[] levelInfo) {
        this.key = key;
        this.score = score;
        // noinspection unchecked
        this.levelInfo = levelInfo;
    }

    /**
     * @return 该节点的直接后继节点
     */
    SkipListNode<K, S> directForward() {
        return levelInfo[0].forward;
    }
}
