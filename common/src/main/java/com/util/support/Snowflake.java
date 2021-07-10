package com.util.support;

import com.entry.BaseEntry;
import com.util.ReflectionUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 - 0000000000000 - 0000000000
 * </pre>
 * <p>
 * 第一位为未使用，接下来的40位为毫秒级时间(40位的长度可以使用34年)<br>
 * 然后是13位workerIdBits(10位的长度最多支持部署8192个节点）<br>
 * 最后12位是毫秒内的计数（10位的计数顺序号支持每个节点每毫秒产生1024个ID序号）
 */
public class Snowflake {

    private final long twepoch = 1625898730942L;
    private final long workerIdBits = 13L;//2^13 8192
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final long sequenceBits = 10L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private ConcurrentHashMap<Class<? extends BaseEntry>, Object> lockMap = new ConcurrentHashMap<>();


    /**
     * 构造
     *
     * @param workerId 终端ID
     */
    public Snowflake(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
        for (Class<? extends BaseEntry> entryClass : ReflectionUtil.getEntryClasses()) {
            lockMap.put(entryClass, new Object());
        }
    }


    public long nextId(Class<? extends BaseEntry> clazz) {
        synchronized (lockMap.get(clazz)) {
            long timestamp = System.currentTimeMillis();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        }
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
