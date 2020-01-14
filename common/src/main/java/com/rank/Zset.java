package com.rank;


import com.Constant;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * key和score均为泛型类型的sorted set - 参考redis的zset实现
 * <b>排序规则</b>
 * 有序集合里面的成员是不能重复的，都是唯一的，但是，不同成员间有可能有相同的分数。 当多个成员有相同的分数时，它们将按照键排序。 即：分数作为第一排序条件，键作为第二排序条件，当分数相同时，比较键的大小。
 * <p>
 * <b>NOTE</b>：
 * 1. ZSET中的排名从0开始（提供给用户的接口，排名都从0开始） 2. ZSET使用键的<b>compare</b>结果判断两个键是否相等，而不是equals方法，因此必须保证键不同时,compare结果一定不为0。
 * 3. 又由于key需要存放于{@link HashMap}中，因此“相同”的key必须有相同的hashCode，且equals方法返回true。
 * <b>手动加粗:key的关键属性最好是number或string</b>
 * <p>
 */
public class Zset<K, S> implements Iterable<Entry<K, S>> {

    /**
     * <key,score>
     */
    private final Map<K, S> map = new HashMap<>(Constant.ZSET_INIT_CAPACITY);
    private final SkipList<K, S> zsl;

    private Zset(Comparator<K> objComparator, ScoreComparator<S> scoreComparator) {
        this.zsl = new SkipList<>(objComparator, scoreComparator);
    }

    public static <K, S> Zset<K, S> newZSet(Comparator<K> keyComparator,
        ScoreComparator<S> scoreHandler) {
        return new Zset<>(keyComparator, scoreHandler);
    }

    /**
     * 新增一个成员。
     *
     * @param score 分
     * @param key   成员id
     */
    public void zadd(final K key, final S score) {
        final S oldScore = map.put(key, score);
        if (oldScore != null) {
            zsl.zslDelete(oldScore, key);
        }
        zsl.zslInsert(score, key);
    }

    /**
     * 新增一个成员。当且仅当该成员不在有序集合时才添加。
     *
     * @param score 数据的评分
     * @param key   成员id
     * @return 添加成功则返回true，否则返回false。
     */
    public boolean zaddnx(final K key, final S score) {
        final S oldScore = map.putIfAbsent(key, score);
        if (oldScore == null) {
            zsl.zslInsert(score, key);
            return true;
        }
        return false;
    }


    /**
     * 删除指定成员
     *
     * @param key 成员id
     * @return 如果成员存在，则返回对应的score，否则返回null。
     */
    public S zrem(K key) {
        final S oldScore = map.remove(key);
        if (oldScore != null) {
            zsl.zslDelete(oldScore, key);
            return oldScore;
        } else {
            return null;
        }
    }

    /**
     * 移除zset中所有score值介于start和end之间(包括等于start或end)的成员
     *
     * @param start 起始分数 inclusive
     * @param end   截止分数 inclusive
     * @return 删除的成员数目
     */
    public int zremrangeByScore(S start, S end) {
        return zremrangeByScore(new Range<>(start, end));
    }

    /**
     * 移除zset中所有score值在范围区间的成员
     *
     * @param range score范围区间
     * @return 删除的成员数目
     */
    private int zremrangeByScore(Range<S> range) {
        return zsl.zslDeleteRangeByScore(range, map);
    }

    /**
     * 删除并返回有序集合中的第一个成员。 - 不使用min和max，是因为score的比较方式是用户自定义的。
     *
     * @return 如果不存在，则返回null
     */
    @Nullable
    public Entry<K, S> zpopFirst() {
        return zremByRank(0);
    }

    /**
     * 删除并返回有序集合中的最后一个成员。 - 不使用min和max，是因为score的比较方式是用户自定义的。
     *
     * @return 如果不存在，则返回null
     */
    @Nullable
    public Entry<K, S> zpopLast() {
        return zremByRank(zsl.length() - 1);
    }

    /**
     * 删除指定排名的成员
     *
     * @param rank 排名 0-based
     * @return 删除成功则返回该排名对应的数据，否则返回null
     */
    @Nullable
    public Entry<K, S> zremByRank(int rank) {
        if (rank < 0 || rank >= zsl.length()) {
            return null;
        }
        final SkipListNode<K, S> delete = zsl.zslDeleteByRank(rank + 1, map);
        assert null != delete;
        return new Entry<>(delete.obj, delete.score);
    }

    /**
     * 删除指定排名范围的全部成员，start和end都是从0开始的。 排名0表示分数最小的成员。 start和end都可以是负数，此时它们表示从最高排名成员开始的偏移量，eg:
     * -1表示最高排名的成员， -2表示第二高分的成员，以此类推。
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set
     * and M the number of elements removed by the operation
     *
     * @param start 起始排名
     * @param end   截止排名
     * @return 删除的成员数目
     */
    public int zremrangeByRank(int start, int end) {
        final int zslLength = zsl.length();

        start = convertStartRank(start, zslLength);
        end = convertEndRank(end, zslLength);

        if (isRankRangeEmpty(start, end, zslLength)) {
            return 0;
        }

        return zsl.zslDeleteRangeByRank(start + 1, end + 1, map);
    }

    /**
     * 转换截止排名
     *
     * @param end       请求参数中的截止排名(0-based)
     * @param zslLength 跳表的长度
     * @return 有效截止排名
     */
    private static int convertEndRank(int end, int zslLength) {
        if (end < 0) {
            end += zslLength;
        }
        if (end >= zslLength) {
            end = zslLength - 1;
        }
        return end;
    }


    /**
     * 判断排名区间是否为空
     *
     * @param start     转换后的起始排名(0-based)
     * @param end       转换后的截止排名(0-based)
     * @param zslLength 跳表长度
     * @return true/false
     */
    public static boolean isRankRangeEmpty(final int start, final int end, final int zslLength) {
        /* Invariant: start >= 0, so this test will be true when end < 0.
         * The range is empty when start > end or start >= length. */
        return start > end || start >= zslLength;
    }

    /**
     * 删除zset中尾部多余的成员，将zset中的成员数量限制到count之内。 保留前面的count个数成员
     *
     * @param count 剩余数量限制
     * @return 删除的成员数量
     */
    public int zlimit(int count) {
        if (zsl.length() <= count) {
            return 0;
        }
        return zsl.zslDeleteRangeByRank(count + 1, zsl.length(), map);
    }

    /**
     * 删除zset中头部多余的成员，将zset中的成员数量限制到count之内。 - 保留后面的count个数成员
     *
     * @param count 剩余数量限制
     * @return 删除的成员数量
     */
    public int zrevlimit(int count) {
        if (zsl.length() <= count) {
            return 0;
        }
        return zsl.zslDeleteRangeByRank(1, zsl.length() - count, map);
    }

    /**
     * 返回有序集成员key的score值。 如果key成员不是有序集的成员，返回null - 这里返回任意的基础值都是不合理的，因此必须返回null。
     *
     * @param key 成员id
     * @return score
     */
    public S zscore(K key) {
        return map.get(key);
    }

    /**
     * 返回有序集中成员key的排名。
     * <p>
     * <b>Time complexity:</b> O(log(N))
     * <p>
     * <b>与redis的区别</b>：我们使用-1表示成员不存在，而不是返回null。
     *
     * @param key 成员id
     * @return 如果存在该成员，则返回该成员的排名(0-based)，否则返回-1
     */
    public int zrank(K key) {
        final S score = map.get(key);
        if (score == null) {
            return -1;
        }
        // 0 < zslGetRank <= size
        return zsl.zslGetRank(score, key) - 1;
    }

//    /**
//     * 返回有序集中成员member的逆序排名。
//     * <p>
//     * <b>Time complexity:</b> O(log(N))
//     * <p>
//     * <b>与redis的区别</b>：我们使用-1表示成员不存在，而不是返回null。
//     *
//     * @param member 成员id
//     * @return 如果存在该成员，则返回该成员的排名(0-based)，否则返回-1
//     */
//    public int zrevrank(K member) {
//        final S score = map.get(member);
//        if (score == null) {
//            return -1;
//        }
//        // 0 < zslGetRank <= size
//        return zsl.length() - zsl.zslGetRank(score, member);
//    }

    /**
     * 获取指定排名的成员数据。
     *
     * @param rank 排名 0-based
     * @return memver，如果不存在，则返回null
     */
    public Entry<K, S> zmemberByRank(int rank) {
        if (rank < 0 || rank >= zsl.length()) {
            return null;
        }
        final SkipListNode<K, S> node = zsl.zslGetElementByRank(rank + 1);
        assert null != node;
        return new Entry<>(node.obj, node.score);
    }

//    /**
//     * 获取指定逆序排名的成员数据。
//     *
//     * @param rank 排名 0-based
//     * @return memver，如果不存在，则返回null
//     */
//    public Entry<K, S> zrevmemberByRank(int rank) {
//        if (rank < 0 || rank >= zsl.length()) {
//            return null;
//        }
//        final SkipListNode<K, S> node = zsl.zslGetElementByRank(zsl.length() - rank);
//        assert null != node;
//        return new Entry<>(node.obj, node.score);
//    }


    /**
     * 返回有序集合中的分数在start和end之间的所有成员（包括分数等于start或者end的成员）。
     *
     * @param start 起始分数 inclusive
     * @param end   截止分数 inclusive
     * @return memberInfo
     */
    public List<Entry<K, S>> zrangeByScore(S start, S end) {
        return zrangeByScore(start, end, false);
    }


    public List<Entry<K, S>> zrangeByScore(S start, S end, boolean reverse) {
        if (reverse) {
            return zrangeByScoreWithOptions(new Range<>(end, start), 0, -1, true);
        } else {
            return zrangeByScoreWithOptions(new Range<>(start, end), 0, -1, false);
        }
    }

    /**
     * 返回zset中指定分数区间内的成员，并按照指定顺序返回
     *
     * @param range   score范围描述信息
     * @param offset  偏移量(用于分页)  大于等于0
     * @param limit   返回的成员数量(用于分页) 小于0表示不限制
     * @param reverse 是否逆序
     * @return memberInfo
     */
    private List<Entry<K, S>> zrangeByScoreWithOptions(final Range<S> range, int offset, int limit,
        boolean reverse) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset" + ": " + offset + " (expected: >= 0)");
        }

        SkipListNode<K, S> listNode;
        /* If reversed, get the last node in range as starting point. */
        if (reverse) {
            listNode = zsl.zslLastInRange(range);
        } else {
            listNode = zsl.zslFirstInRange(range);
        }

        /* No "first" element in the specified interval. */
        if (listNode == null) {
            return new ArrayList<>();
        }

        /* If there is an offset, just traverse the number of elements without
         * checking the score because that is done in the next loop. */
        while (listNode != null && offset-- != 0) {
            if (reverse) {
                listNode = listNode.backward;
            } else {
                listNode = listNode.levelInfo[0].forward;
            }
        }

        final List<Entry<K, S>> result = new ArrayList<>();

        /* 这里使用 != 0 判断，当limit小于0时，表示不限制 */
        while (listNode != null && limit-- != 0) {
            /* Abort when the node is no longer in range. */
            if (reverse) {
                if (!zsl.zslValueGteMin(listNode.score, range)) {
                    break;
                }
            } else {
                if (!zsl.zslValueLteMax(listNode.score, range)) {
                    break;
                }
            }

            result.add(new Entry<>(listNode.obj, listNode.score));

            /* Move to next node */
            if (reverse) {
                listNode = listNode.backward;
            } else {
                listNode = listNode.levelInfo[0].forward;
            }
        }
        return result;
    }

    /**
     * 查询指定排名区间的成员信息
     *
     * @param start 起始排名(0-based) inclusive
     * @param end   截止排名(0-based) inclusive
     * @return memberInfo
     */
    public List<Entry<K, S>> zrangeByRank(int start, int end) {
        return zrangeByRankInternal(start, end, false);
    }

//    /**
//     * 查询指定逆序排名区间的成员信息
//     *
//     * @param start 起始排名(0-based) inclusive
//     * @param end   截止排名(0-based) inclusive
//     * @return memberInfo
//     */
//    public List<Entry<K, S>> zrevrangeByRank(int start,int end) {
//        return zrangeByRankInternal(start, end, true);
//    }

    /**
     * 查询指定排名区间的成员id和分数，start和end都是从0开始的。
     *
     * @param start   起始排名(0-based) inclusive
     * @param end     截止排名(0-based) inclusive
     * @param reverse 是否逆序返回
     * @return memberInfo
     */
    private List<Entry<K, S>> zrangeByRankInternal(int start, int end, boolean reverse) {
        final int zslLength = zsl.length();

        start = convertStartRank(start, zslLength);
        end = convertEndRank(end, zslLength);

        if (isRankRangeEmpty(start, end, zslLength)) {
            return new ArrayList<>();
        }

        int rangeLen = end - start + 1;

        SkipListNode<K, S> listNode;

        /* start >= 0，大于0表示需要进行调整 */
        /* Check if starting point is trivial, before doing log(N) lookup. */
        if (reverse) {
            listNode = start > 0 ? zsl.zslGetElementByRank(zslLength - start) : zsl.getTail();
        } else {
            listNode = start > 0 ? zsl.zslGetElementByRank(start + 1)
                : zsl.getHeader().levelInfo[0].forward;
        }

        final List<Entry<K, S>> result = new ArrayList<>(rangeLen);
        while (rangeLen-- > 0 && listNode != null) {
            result.add(new Entry<>(listNode.obj, listNode.score));
            listNode = reverse ? listNode.backward : listNode.levelInfo[0].forward;
        }
        return result;
    }

    /**
     * 转换起始排名
     *
     * @param start     请求参数中的起始排名(0-based)
     * @param zslLength 跳表的长度
     * @return 有效起始排名
     */
    public static int convertStartRank(int start, int zslLength) {
        if (start < 0) {
            start += zslLength;
        }
        if (start < 0) {
            start = 0;
        }
        return start;
    }


    /**
     * 返回有序集key中，score值在指定区间(包括score值等于start或end)的成员
     *
     * @param start 起始分数
     * @param end   截止分数
     * @return 分数区间段内的成员数量
     */
    public int zcount(S start, S end) {
        return zcountInternal(new Range<>(start, end));
    }

    /**
     * 返回有序集key中，score值在指定区间的成员
     *
     * @param range score区间描述信息
     * @return 分数区间段内的成员数量
     */
    private int zcountInternal(final Range<S> range) {
        final SkipListNode<K, S> firstNodeInRange = zsl.zslFirstInRange(range);
        if (firstNodeInRange != null) {
            final int firstNodeRank = zsl.zslGetRank(firstNodeInRange.score, firstNodeInRange.obj);

            /* 如果firstNodeInRange不为null，那么lastNode也一定不为null(最坏的情况下firstNode就是lastNode) */
            final SkipListNode<K, S> lastNodeInRange = zsl.zslLastInRange(range);
            assert lastNodeInRange != null;
            final int lastNodeRank = zsl.zslGetRank(lastNodeInRange.score, lastNodeInRange.obj);

            return lastNodeRank - firstNodeRank + 1;
        }
        return 0;
    }

    /**
     * @return zset中的成员数量
     */
    public int zcard() {
        return zsl.length();
    }


    /**
     * 迭代有序集中的所有元素
     *
     * @return iterator
     */
    public Iterator<Entry<K, S>> zscan() {
        return zscan(0);
    }

    /**
     * 从指定偏移量开始迭代有序集中的元素
     *
     * @param offset 偏移量，如果小于等于0，则等价于{@link #zscan()}
     * @return iterator
     */
    public Iterator<Entry<K, S>> zscan(int offset) {
        if (offset <= 0) {
            return new ZSetIterator(zsl.getHeader().directForward());
        }

        if (offset >= zsl.length()) {
            return new ZSetIterator(null);
        }

        return new ZSetIterator(zsl.zslGetElementByRank(offset + 1));
    }

    @Override
    public Iterator<Entry<K, S>> iterator() {
        return zscan(0);
    }

    /**
     * @return zset中当前的成员信息，用于debug
     */
    public String dump() {
        return zsl.dump();
    }

    private class ZSetIterator implements Iterator<Entry<K, S>> {

        private SkipListNode<K, S> lastReturned;
        private SkipListNode<K, S> next;
        int expectedModCount = zsl.getModCount();

        ZSetIterator(SkipListNode<K, S> next) {
            this.next = next;
        }

        public boolean hasNext() {
            return next != null;
        }

        public Entry<K, S> next() {
            checkModification();

            if (next == null) {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.directForward();

            return new Entry<>(lastReturned.obj, lastReturned.score);
        }

        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            checkModification();

            // remove lastReturned
            map.remove(lastReturned.obj);
            zsl.zslDelete(lastReturned.score, lastReturned.obj);

            // reset lastReturned
            lastReturned = null;
            expectedModCount = zsl.getModCount();
        }

        final void checkModification() {
            if (zsl.getModCount() != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
