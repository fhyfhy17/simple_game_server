package com.rank;

import lombok.Value;

import java.util.List;


public class ZSetUtils {

    public static CommonComparator commonComparator = new CommonComparator(); //通用排序器，第一条件是分，分大的在前，分数相同，时间小的在前

    public static ScoreComparator<Long> longComparator = longComparator(false);
    public static ScoreComparator<Long> longDescComparator = longComparator(true);
    public static ScoreComparator<Integer> integerComparator = integerComparator(false);
    public static ScoreComparator<Integer> integerDescComparator = integerComparator(true);
    public static ScoreComparator<String> stringComparator = stringComparator(false);
    public static ScoreComparator<String> stringDescComparator = stringComparator(true);

    /**
     * @param desc 是否降序 ，true 降序为大的在前, false 正序小的在前
     * @return Long 类型的score排序器
     */
    private static ScoreComparator<Long> longComparator(boolean desc) {
        return desc ? (o1, o2) -> o2.compareTo(o1) : Long::compareTo;
    }

    /**
     * @param desc 是否降序 ，true 降序,大的在前; false 正序,小的在前
     * @return Integer 类型的score排序器
     */
    private static ScoreComparator<Integer> integerComparator(boolean desc) {
        return desc ? (o1, o2) -> o2.compareTo(o1) : Integer::compareTo;
    }

    /**
     * @param desc 是否降序
     * @return String 类型的score排序器
     */
    private static ScoreComparator<String> stringComparator(boolean desc) {
        return desc ? (o1, o2) -> o2.compareTo(o1) : String::compareTo;
    }

    public static void main(String[] args) {
        Zset<Long, Long> entries = Zset.newZSet(ZSetUtils.longComparator, ZSetUtils.longComparator);

        long start = System.currentTimeMillis();
        for (long i = 0; i < 10000; i++) {
            entries.zadd(i, i);
        }
        List<Entry<Long, Long>> entries1 = entries.zrangeByRank(1000, 1200);
        entries1.forEach(x -> System.out.println(x.getKey()));
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        System.out.println("==================================");
        List<Entry<Long, Long>> entries2 = entries.zrangeByScore(100L, 150L);
        entries.zremrangeByRank(1000, 9000);
        entries.iterator().forEachRemaining(x -> {
            System.out.println("被删过的" + x);
        });

        entries2.forEach(x -> System.out.println(x.getKey() + "_" + x.getScore()));

        System.out.println(entries.zrank(18L));

        Zset<String, Common> e3 = Zset
            .newZSet(ZSetUtils.stringComparator, ZSetUtils.commonComparator);

        for (long i = 0; i < 100; i++) {
            e3.zadd("a" + i, new Common(i, (int) (System.currentTimeMillis() / 1000)));
        }

        List<Entry<String, Common>> entries3 = e3.zrangeByRank(0, 40);
        entries3.forEach(System.out::println);

//		e3.iterator().forEachRemaining(x->{
//			System.out.println(x);
//		});

    }

}

/**
 * 常用比较器，分大的，时间小的在前
 */

class CommonComparator implements ScoreComparator<Common> {

    @Override
    public int compare(Common o1, Common o2) {
        int result = Long.compare(o2.getScore(), o1.getScore());
        if (result == 0) {
            result = Integer.compare(o1.getTime(), o2.getTime());
        }
        return result;
    }
}

/**
 * Score的实现, 不可变类，不允许修改
 */
@Value
class Common {

    private long score;
    private int time;
}