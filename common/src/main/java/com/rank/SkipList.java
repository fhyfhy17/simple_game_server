package com.rank;


import com.Constant;
import com.rank.Range.RangeOpt;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;


@Data
public class SkipList<K, S> {

    /**
     * 更新节点使用的缓存 - 避免频繁的申请空间
     */
    @SuppressWarnings("unchecked")
    private final SkipListNode<K, S>[] updateCache = new SkipListNode[Constant.ZSET_MAX_LEVEL];
    private final int[] rankCache = new int[Constant.ZSET_MAX_LEVEL];

    private final Comparator<K> objComparator;
    private final ScoreComparator<S> scoreComparator;

    /**
     * 修改次数 - 防止错误的迭代
     */
    private int modCount = 0;

    /**
     * 跳表头结点 - 哨兵 1. 可以简化判定逻辑 2. 恰好可以使得rank从1开始
     */
    private final SkipListNode<K, S> header;

    /**
     * 跳表尾节点
     */
    private SkipListNode<K, S> tail;

    /**
     * 跳表成员个数 注意：head头指针不包含在length计数中。
     */
    private int length = 0;

    /**
     * level表示SkipList的总层数，即所有节点层数的最大值。
     */
    private int level = 1;

    SkipList(Comparator<K> objComparator, ScoreComparator<S> scoreComparator) {
        this.objComparator = objComparator;
        this.scoreComparator = scoreComparator;
        this.header = zslCreateNode(Constant.ZSET_MAX_LEVEL, null, null);
    }

    /**
     * 插入一个新的节点到跳表。 这里假定成员已经不存在（直到调用方执行该方法）。
     * <p>
     * zslInsert a new node in the skiplist. Assumes the element does not already exist (up to the
     * caller to enforce that).
     * <pre>
     *             header                    newNode
     *               _                                                 _
     * level - 1    |_| pre                                           |_|
     *  |           |_| pre                    _                      |_|
     *  |           |_| pre  _                |_|                     |_|
     *  |           |_|  ↓  |_| pre  _        |_|      _              |_|
     *  |           |_|     |_|  ↓  |_| pre   |_|     |_|             |_|
     *  |           |_|     |_|     |_| pre   |_|     |_|      _      |_|
     *  |           |_|     |_|     |_| pre   |_|     |_|     |_|     |_|
     *  0           |0|     |1|     |2| pre   |_|     |3|     |4|     |5|
     * </pre>
     *
     * @param score 分数
     * @param obj   obj 分数对应的成员id
     */
    @SuppressWarnings("UnusedReturnValue")
    SkipListNode zslInsert(S score, K obj) {
        // 新节点的level
        final int level = zslRandomLevel();

        // update - 需要更新后继节点的Node，新节点各层的前驱节点
        // 1. 分数小的节点
        // 2. 分数相同但id小的节点（分数相同时根据数据排序）
        // rank - 新节点各层前驱的当前排名
        // 这里不必创建一个ZSKIPLIST_MAXLEVEL长度的数组，它取决于插入节点后的新高度，你在别处看见的代码会造成大量的空间浪费，增加GC压力。
        // 如果创建的都是ZSKIPLIST_MAXLEVEL长度的数组，那么应该实现缓存

        final SkipListNode<K, S>[] update = updateCache;
        final int[] rank = rankCache;
        final int realLength = Math.max(level, this.level);
        try {
            // preNode - 新插入节点的前驱节点
            SkipListNode<K, S> preNode = header;
            for (int i = this.level - 1; i >= 0; i--) {
                /* store rank that is crossed to reach the insert position */
                if (i == (this.level - 1)) {
                    // 起始点，也就是head，它的排名就是0
                    rank[i] = 0;
                } else {
                    // 由于是回溯降级继续遍历，因此其初始排名是前一次遍历的排名
                    rank[i] = rank[i + 1];
                }

                while (preNode.levelInfo[i].forward != null &&
                    compareScoreAndObj(preNode.levelInfo[i].forward, score, obj) < 0) {
                    // preNode的后继节点仍然小于要插入的节点，需要继续前进，同时累计排名
                    rank[i] += preNode.levelInfo[i].span;
                    preNode = preNode.levelInfo[i].forward;
                }

                // 这是要插入节点的第i层的前驱节点，此时触发降级
                update[i] = preNode;
            }

            if (level > this.level) {
                /* 新节点的层级大于当前层级，那么高出来的层级导致需要更新head，且排名和跨度是固定的 */
                for (int i = this.level; i < level; i++) {
                    rank[i] = 0;
                    update[i] = this.header;
                    update[i].levelInfo[i].span = this.length;
                }
                this.level = level;
            }

            /* 由于我们允许的重复score，并且zslInsert(该方法)的调用者在插入前必须测试要插入的member是否已经在hash表中。
             * 因此我们假设key（obj）尚未被插入，并且重复插入score的情况永远不会发生。*/
            /* we assume the key is not already inside, since we allow duplicated
             * scores, and the re-insertion of score and redis object should never
             * happen since the caller of zslInsert() should test in the hash table
             * if the element is already inside or not.*/
            final SkipListNode<K, S> newNode = zslCreateNode(level, score, obj);

            /* 这些节点的高度小于等于新插入的节点的高度，需要更新指针。此外它们当前的跨度被拆分了两部分，需要重新计算。 */
            for (int i = 0; i < level; i++) {
                /* 链接新插入的节点 */
                newNode.levelInfo[i].forward = update[i].levelInfo[i].forward;
                update[i].levelInfo[i].forward = newNode;

                /* rank[0] 是新节点的直接前驱的排名，每一层都有一个前驱，可以通过彼此的排名计算跨度 */
                /* 计算新插入节点的跨度 和 重新计算所有前驱节点的跨度，之前的跨度被拆分为了两份*/
                /* update span covered by update[i] as newNode is inserted here */
                newNode.levelInfo[i].span = update[i].levelInfo[i].span - (rank[0] - rank[i]);
                update[i].levelInfo[i].span = (rank[0] - rank[i]) + 1;
            }

            /*  这些节点高于新插入的节点，它们的跨度可以简单的+1 */
            /* increment span for untouched levels */
            for (int i = level; i < this.level; i++) {
                update[i].levelInfo[i].span++;
            }

            /* 设置新节点的前向节点(回溯节点) - 这里不包含header，一定注意 */
            newNode.backward = (update[0] == this.header) ? null : update[0];

            /* 设置新节点的后向节点 */
            if (newNode.levelInfo[0].forward != null) {
                newNode.levelInfo[0].forward.backward = newNode;
            } else {
                this.tail = newNode;
            }

            this.length++;
            this.modCount++;

            return newNode;
        } finally {
            releaseUpdate(update, realLength);

            for (int index = 0; index < realLength; index++) {
                rank[index] = 0;
            }

        }
    }

    /**
     * 返回一个随机的层级分配给即将插入的节点。 返回的层级值在 1 和 ZSKIPLIST_MAXLEVEL 之间（包含两者）。 具有类似幂次定律的分布，越高level返回的可能性更小。
     * <p>
     * Returns a random level for the new skiplist node we are going to create. The return value of
     * this function is between 1 and ZSKIPLIST_MAXLEVEL (both inclusive), with a powerlaw-alike
     * distribution where higher levels are less likely to be returned.
     *
     * @return level
     */
    public static int zslRandomLevel() {
        int level = 1;
        while (level < Constant.ZSET_MAX_LEVEL
            && ThreadLocalRandom.current().nextFloat() < Constant.ZSET_SKIPLIST_P) {
            level++;
        }
        return level;
    }

    /**
     * 释放update引用的对象
     */
    public static void releaseUpdate(Object[] update, int realLength) {
        for (int index = 0; index < realLength; index++) {
            update[index] = null;
        }
    }

    /**
     * Delete an element with matching score/object from the skiplist.
     *
     * @param score 分数用于快速定位节点
     * @param obj   用于确定节点是否是对应的数据节点
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean zslDelete(S score, K obj) {
        // update - 需要更新后继节点的Node
        // 1. 分数小的节点
        // 2. 分数相同但id小的节点（分数相同时根据数据排序）
        final SkipListNode<K, S>[] update = updateCache;
        final int realLength = this.level;
        try {
            SkipListNode<K, S> preNode = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (preNode.levelInfo[i].forward != null &&
                    compareScoreAndObj(preNode.levelInfo[i].forward, score, obj) < 0) {
                    // preNode的后继节点仍然小于要删除的节点，需要继续前进
                    preNode = preNode.levelInfo[i].forward;
                }
                // 这是目标节点第i层的可能前驱节点
                update[i] = preNode;
            }

            /* 由于可能多个节点拥有相同的分数，因此必须同时比较score和object */
            /* We may have multiple elements with the same score, what we need
             * is to find the element with both the right score and object. */
            final SkipListNode<K, S> targetNode = preNode.levelInfo[0].forward;
            if (targetNode != null && scoreEquals(targetNode.score, score) && objEquals(
                targetNode.obj, obj)) {
                zslDeleteNode(targetNode, update);
                return true;
            }

            /* not found */
            return false;
        } finally {
            releaseUpdate(update, realLength);
        }
    }

    /**
     * Internal function used by zslDelete, zslDeleteByScore and zslDeleteByRank
     *
     * @param deleteNode 要删除的节点
     * @param update     可能要更新的节点们
     */
    private void zslDeleteNode(final SkipListNode<K, S> deleteNode, final SkipListNode[] update) {
        for (int i = 0; i < this.level; i++) {
            if (update[i].levelInfo[i].forward == deleteNode) {
                // 这些节点的高度小于等于要删除的节点，需要合并两个跨度
                update[i].levelInfo[i].span += deleteNode.levelInfo[i].span - 1;
                update[i].levelInfo[i].forward = deleteNode.levelInfo[i].forward;
            } else {
                // 这些节点的高度高于要删除的节点，它们的跨度可以简单的 -1
                update[i].levelInfo[i].span--;
            }
        }

        if (deleteNode.levelInfo[0].forward != null) {
            // 要删除的节点有后继节点
            deleteNode.levelInfo[0].forward.backward = deleteNode.backward;
        } else {
            // 要删除的节点是tail节点
            this.tail = deleteNode.backward;
        }

        // 如果删除的节点是最高等级的节点，则检查是否需要降级
        if (deleteNode.levelInfo.length == this.level) {
            while (this.level > 1 && this.header.levelInfo[this.level - 1].forward == null) {
                // 如果最高层没有后继节点，则降级
                this.level--;
            }
        }

        this.length--;
        this.modCount++;
    }

    /**
     * 判断zset中的数据所属的范围是否和指定range存在交集(intersection)。 它不代表zset存在指定范围内的数据。 Returns if there is a part
     * of the zset is in range.
     * <pre>
     *                         ZSet
     *              min ____________________ max
     *                 |____________________|
     *   min ______________ max  min _____________
     *      |______________|        |_____________|
     *          Range                   Range
     * </pre>
     *
     * @param range 范围描述信息
     * @return true/false
     */
    boolean inRange(Range<S> range) {
        if (isScoreRangeEmpty(range)) {
            // 传进来的范围为空
            return false;
        }

        if (this.tail == null || !zslValueGteMin(this.tail.score, range)) {
            // 列表有序，按照从score小到大，如果尾部节点数据小于最小值，那么一定不在区间范围内
            return false;
        }

        final SkipListNode<K, S> firstNode = this.header.levelInfo[0].forward;
        if (firstNode == null || !zslValueLteMax(firstNode.score, range)) {
            // 列表有序，按照从score小到大，如果首部节点数据大于最大值，那么一定不在范围内
            return false;
        }
        return true;
    }

    /**
     * 测试score范围信息是否为空(无效)
     *
     * @param range 范围描述信息
     * @return true/false
     */
    private boolean isScoreRangeEmpty(Range<S> range) {
        // 这里和redis有所区别，这里min一定小于等于max
        return scoreEquals(range.getStart(), range.getEnd())
            && range.getRangeOpt() != RangeOpt.CLOSE_CLOSE;
    }

    /**
     * 找出第一个在指定范围内的节点。如果没有符合的节点，则返回null。
     * <p>
     * Find the first node that is contained in the specified range. Returns NULL when no element is
     * contained in the range.
     *
     * @param range 范围描述符
     * @return 不存在返回null
     */
    SkipListNode<K, S> zslFirstInRange(Range<S> range) {
        /* zset数据范围与指定范围没有交集，可提前返回，减少不必要的遍历 */
        /* If everything is out of range, return early. */
        if (!inRange(range)) {
            return null;
        }

        SkipListNode<K, S> lastNodeLtMin = this.header;
        for (int i = this.level - 1; i >= 0; i--) {
            /* 前进直到出现后继节点大于等于指定最小值的节点 */
            /* Go forward while *OUT* of range. */
            while (lastNodeLtMin.levelInfo[i].forward != null &&
                !zslValueGteMin(lastNodeLtMin.levelInfo[i].forward.score, range)) {
                // 如果当前节点的后继节点仍然小于指定范围的最小值，则继续前进
                lastNodeLtMin = lastNodeLtMin.levelInfo[i].forward;
            }
        }

        /* 这里的上下文表明了，一定存在一个节点的值大于等于指定范围的最小值，因此下一个节点一定不为null */
        /* This is an inner range, so the next node cannot be NULL. */
        final SkipListNode<K, S> firstNodeGteMin = lastNodeLtMin.levelInfo[0].forward;
        assert firstNodeGteMin != null;

        /* 如果该节点的数据大于max，则不存在再范围内的节点 */
        /* Check if score <= max. */
        if (!zslValueLteMax(firstNodeGteMin.score, range)) {
            return null;
        }
        return firstNodeGteMin;
    }

    /**
     * 找出最后一个在指定范围内的节点。如果没有符合的节点，则返回null。
     * <p>
     * Find the last node that is contained in the specified range. Returns NULL when no element is
     * contained in the range.
     *
     * @param range 范围描述信息
     * @return 不存在返回null
     */
    SkipListNode<K, S> zslLastInRange(Range<S> range) {
        /* zset数据范围与指定范围没有交集，可提前返回，减少不必要的遍历 */
        /* If everything is out of range, return early. */
        if (!inRange(range)) {
            return null;
        }

        SkipListNode<K, S> lastNodeLteMax = this.header;
        for (int i = this.level - 1; i >= 0; i--) {
            /* Go forward while *IN* range. */
            while (lastNodeLteMax.levelInfo[i].forward != null &&
                zslValueLteMax(lastNodeLteMax.levelInfo[i].forward.score, range)) {
                // 如果当前节点的后继节点仍然小于最大值，则继续前进
                lastNodeLteMax = lastNodeLteMax.levelInfo[i].forward;
            }
        }

        /* 这里的上下文表明一定存在一个节点的值小于指定范围的最大值，因此当前节点一定不为null */
        /* This is an inner range, so this node cannot be NULL. */
        assert lastNodeLteMax != null;

        /* Check if score >= min. */
        if (!zslValueGteMin(lastNodeLteMax.score, range)) {
            return null;
        }
        return lastNodeLteMax;
    }

    /**
     * 删除指定分数区间的所有节点。
     * <b>Note</b>: 该方法引用了ZSet的哈希表视图，以便从哈希表中删除成员。
     * <p>
     * Delete all the elements with score between min and max from the skiplist. Min and max are
     * inclusive, so a score >= min || score <= max is deleted. Note that this function takes the
     * reference to the hash table view of the sorted set, in order to remove the elements from the
     * hash table too.
     *
     * @param range 范围描述符
     * @param dict  对象id到score的映射
     * @return 删除的节点数量
     */
    int zslDeleteRangeByScore(Range<S> range, Map<K, S> dict) {
        final SkipListNode<K, S>[] update = updateCache;
        final int realLength = this.level;
        try {
            int removed = 0;

            SkipListNode<K, S> lastNodeLtMin = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (lastNodeLtMin.levelInfo[i].forward != null &&
                    !zslValueGteMin(lastNodeLtMin.levelInfo[i].forward.score, range)) {
                    lastNodeLtMin = lastNodeLtMin.levelInfo[i].forward;
                }
                update[i] = lastNodeLtMin;
            }

            /* 当前节点是小于目标范围最小值的最后一个节点，它的下一个节点可能为null，或大于等于最小值 */
            /* Current node is the last with score < or <= min. */
            SkipListNode<K, S> firstNodeGteMin = lastNodeLtMin.levelInfo[0].forward;

            /* 删除在范围内的节点(小于等于最大值的节点) */
            /* Delete nodes while in range. */
            while (firstNodeGteMin != null
                && zslValueLteMax(firstNodeGteMin.score, range)) {
                final SkipListNode<K, S> next = firstNodeGteMin.levelInfo[0].forward;
                zslDeleteNode(firstNodeGteMin, update);
                dict.remove(firstNodeGteMin.obj);
                removed++;
                firstNodeGteMin = next;
            }
            return removed;
        } finally {
            releaseUpdate(update, realLength);
        }
    }

    /**
     * 删除指定排名区间的所有成员。包括start和end。
     * <b>Note</b>: start和end基于从1开始
     * <p>
     * Delete all the elements with rank between start and end from the skiplist. Start and end are
     * inclusive. Note that start and end need to be 1-based
     *
     * @param start 起始排名 inclusive
     * @param end   截止排名 inclusive
     * @param dict  member -> score的字典
     * @return 删除的成员数量
     */
    int zslDeleteRangeByRank(int start, int end, Map<K, S> dict) {
        final SkipListNode<K, S>[] update = updateCache;
        final int realLength = this.level;
        try {
            /* 已遍历的真实成员数量，表示成员的真实排名 */
            int traversed = 0;
            int removed = 0;

            SkipListNode<K, S> lastNodeLtStart = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (lastNodeLtStart.levelInfo[i].forward != null &&
                    (traversed + lastNodeLtStart.levelInfo[i].span) < start) {
                    // 下一个节点的排名还未到范围内，继续前进
                    traversed += lastNodeLtStart.levelInfo[i].span;
                    lastNodeLtStart = lastNodeLtStart.levelInfo[i].forward;
                }
                update[i] = lastNodeLtStart;
            }

            traversed++;

            /* levelInfo[0] 最下面一层就是要删除节点的直接前驱 */
            SkipListNode<K, S> firstNodeGteStart = lastNodeLtStart.levelInfo[0].forward;
            while (firstNodeGteStart != null && traversed <= end) {
                final SkipListNode<K, S> next = firstNodeGteStart.levelInfo[0].forward;
                zslDeleteNode(firstNodeGteStart, update);
                dict.remove(firstNodeGteStart.obj);
                removed++;
                traversed++;
                firstNodeGteStart = next;
            }
            return removed;
        } finally {
            releaseUpdate(update, realLength);
        }
    }

    /**
     * 删除指定排名的成员 - 批量删除比单个删除更快捷 (该方法非原生方法)
     *
     * @param rank 排名 1-based
     * @param dict member -> score的字典
     * @return 删除的节点
     */
    SkipListNode<K, S> zslDeleteByRank(int rank, Map<K, S> dict) {
        final SkipListNode<K, S>[] update = updateCache;
        final int realLength = this.level;
        try {
            int traversed = 0;
            SkipListNode<K, S> lastNodeLtStart = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (lastNodeLtStart.levelInfo[i].forward != null &&
                    (traversed + lastNodeLtStart.levelInfo[i].span) < rank) {
                    // 下一个节点的排名还未到范围内，继续前进
                    traversed += lastNodeLtStart.levelInfo[i].span;
                    lastNodeLtStart = lastNodeLtStart.levelInfo[i].forward;
                }
                update[i] = lastNodeLtStart;
            }

            /* levelInfo[0] 最下面一层就是要删除节点的直接前驱 */
            final SkipListNode<K, S> targetRankNode = lastNodeLtStart.levelInfo[0].forward;
            if (null != targetRankNode) {
                zslDeleteNode(targetRankNode, update);
                dict.remove(targetRankNode.obj);
                return targetRankNode;
            } else {
                return null;
            }
        } finally {
            releaseUpdate(update, realLength);
        }
    }

    /**
     * 通过score和key查找成员所属的排名。 如果找不到对应的成员，则返回0。
     * <b>Note</b>：排名从1开始
     * <p>
     * Find the rank for an element by both score and key. Returns 0 when the element cannot be
     * found, rank otherwise. Note that the rank is 1-based due to the span of zsl->header to the
     * first element.
     *
     * @param score 节点分数
     * @param obj   节点对应的数据id
     * @return 排名，从1开始
     */
    int zslGetRank(S score, K obj) {
        int rank = 0;
        SkipListNode<K, S> firstNodeGteScore = this.header;
        for (int i = this.level - 1; i >= 0; i--) {
            while (firstNodeGteScore.levelInfo[i].forward != null &&
                compareScoreAndObj(firstNodeGteScore.levelInfo[i].forward, score, obj) <= 0) {
                // <= 也继续前进，也就是我们期望在目标节点停下来，这样rank也不必特殊处理
                rank += firstNodeGteScore.levelInfo[i].span;
                firstNodeGteScore = firstNodeGteScore.levelInfo[i].forward;
            }

            /* firstNodeGteScore might be equal to zsl->header, so test if firstNodeGteScore is header */
            if (firstNodeGteScore != this.header && objEquals(firstNodeGteScore.obj, obj)) {
                // 可能在任意层找到
                return rank;
            }
        }
        return 0;
    }

    /**
     * 查找指定排名的成员数据，如果不存在，则返回Null。 注意：排名从1开始
     * <p>
     * Finds an element by its rank. The rank argument needs to be 1-based.
     *
     * @param rank 排名，1开始
     * @return element
     */
    SkipListNode<K, S> zslGetElementByRank(int rank) {
        int traversed = 0;
        SkipListNode<K, S> firstNodeGteRank = this.header;
        for (int i = this.level - 1; i >= 0; i--) {
            while (firstNodeGteRank.levelInfo[i].forward != null &&
                (traversed + firstNodeGteRank.levelInfo[i].span) <= rank) {
                // <= rank 表示我们期望在目标节点停下来
                traversed += firstNodeGteRank.levelInfo[i].span;
                firstNodeGteRank = firstNodeGteRank.levelInfo[i].forward;
            }

            if (traversed == rank) {
                // 可能在任意层找到该排名的数据
                return firstNodeGteRank;
            }
        }
        return null;
    }

    /**
     * @return 跳表中的成员数量
     */
    int length() {
        return length;
    }

    /**
     * 创建一个skipList的节点
     *
     * @param level 节点的高度
     * @param score 成员分数
     * @param obj   成员id
     * @return node
     */
    static <K, S> SkipListNode<K, S> zslCreateNode(int level, S score, K obj) {
        final SkipListNode<K, S> node = new SkipListNode<>(obj, score, new SkipListLevel[level]);
        for (int index = 0; index < level; index++) {
            node.levelInfo[index] = new SkipListLevel<>();
        }
        return node;
    }

    /**
     * 值是否大于等于下限
     *
     * @param value 要比较的score
     * @param spec  范围描述信息
     * @return true/false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean zslValueGteMin(S value, Range<S> spec) {
        return !spec.getRangeOpt().isLeftClose() ? compareScore(value, spec.getStart()) > 0
            : compareScore(value, spec.getStart()) >= 0;
    }

    /**
     * 值是否小于等于上限
     *
     * @param value 要比较的score
     * @param spec  范围描述信息
     * @return true/false
     */
    boolean zslValueLteMax(S value, Range<S> spec) {
        return !spec.getRangeOpt().isRightClose() ? compareScore(value, spec.getEnd()) < 0
            : compareScore(value, spec.getEnd()) <= 0;
    }


    /**
     * 比较score和key的大小，分数作为第一排序条件，然后，相同分数的成员按照字典规则相对排序
     *
     * @param forward 后继节点
     * @param score   分数
     * @param obj     成员的键
     * @return 0 表示equals
     */
    int compareScoreAndObj(SkipListNode<K, S> forward, S score, K obj) {
        final int scoreCompareR = compareScore(forward.score, score);
        if (scoreCompareR != 0) {
            return scoreCompareR;
        }
        return compareObj(forward.obj, obj);
    }

    /**
     * 比较两个成员的key，<b>必须保证当且仅当两个键相等的时候返回0</b>
     *
     * @return 0表示相等
     */
    int compareObj(K objA, K objB) {
        return objComparator.compare(objA, objB);
    }

    /**
     * 判断两个对象是否相等
     *
     * @return true/false
     * @apiNote 使用compare == 0判断相等
     */
    boolean objEquals(K objA, K objB) {
        // 不使用equals，而是使用compare
        return compareObj(objA, objB) == 0;
    }

    /**
     * 比较两个分数的大小
     *
     * @return 0表示相等
     */
    int compareScore(S score1, S score2) {
        return scoreComparator.compare(score1, score2);
    }

    /**
     * 判断第一个分数是否和第二个分数相等
     *
     * @return true/false
     * @apiNote 使用compare == 0判断相等
     */
    boolean scoreEquals(S score1, S score2) {
        return compareScore(score1, score2) == 0;
    }

    /**
     * 获取跳表的堆内存视图
     *
     * @return string
     */
    String dump() {
        final StringBuilder sb = new StringBuilder("{level = 0, nodeArray:[\n");
        SkipListNode<K, S> curNode = this.header.directForward();
        int rank = 0;
        while (curNode != null) {
            sb.append("{rank:").append(rank++)
                .append(",obj:").append(curNode.obj)
                .append(",score:").append(curNode.score);

            curNode = curNode.directForward();

            if (curNode != null) {
                sb.append("},\n");
            } else {
                sb.append("}\n");
            }
        }
        return sb.append("]}").toString();
    }
}