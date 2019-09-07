package com.util.consistentHashing;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO 要考虑下，新加入节点的迁移成本 ，下面的
// 权重解决了机器性能问题， TRANSFORM 解决了分布不均问题， 迁移成本不知道如何考虑
// 如果一开始就分配 4096个节点，会如何变化呢，这个思考下
// 如何实现最小程度的迁移
@Slf4j
public class ConsistentHashing<T> {


    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private TreeMap<Integer, Pair<Integer, Node<T>>> nodeMap = new TreeMap<>();

    private List<Node<T>> nodeList = new ArrayList<>();

    private final static int TRANSFORM = 100;//对应个数   ； 虚拟节点数=对应个数 * 权重

    public ConsistentHashing() {
    }


    public int getVirtualNodeNum() {
        return nodeMap.size();
    }

    public static void main(String[] args) {
        IpNode ipNode1 = new IpNode("ipTest1", "192.168.0.1", 1);
        IpNode ipNode2 = new IpNode("ipTest2", "192.168.0.2", 1);
        IpNode ipNode3 = new IpNode("ipTest3", "192.168.0.3", 1);


        ConsistentHashing<String> consistentHashing = new ConsistentHashing<>();
        consistentHashing.addNode(ipNode1);
        consistentHashing.addNode(ipNode2);
        consistentHashing.addNode(ipNode3);

        Pair<Integer, Node<String>> abc = consistentHashing.getNodeByKey("abcde");

        System.out.println(abc.getValue().getResource() + " ");
        System.out.println(abc.getKey());
        System.out.println(consistentHashing.getVirtualNodeNum());
    }

    public void addNode(Node<T> node) {
        lock.writeLock().lock();
        int num = node.getWeight() * TRANSFORM;

        try {
            for (int i = 0; i < num; i++) {
                nodeMap.put(hashcode(node.getVirtualNodeName(i)), new Pair<>(i, node));
            }
            nodeList.add(node);
        } finally {
            lock.writeLock().unlock();
        }

    }

    public Pair<Integer, Node<T>> getNodeByKey(String key) {

        lock.readLock().lock();
        try {

            if (nodeMap.isEmpty()) {
                return null;
            }
            int hash = hashcode(key);
            if (!nodeMap.containsKey(hash)) {
                SortedMap<Integer, Pair<Integer, Node<T>>> tailMap = nodeMap.tailMap(hash);
                hash = tailMap.isEmpty() ? nodeMap.firstKey() : tailMap.firstKey();
            }
            return nodeMap.get(hash);
        } finally {
            lock.readLock().unlock();
        }
    }

    private int hashcode(String key) {

        HashFunction hashFunction = Hashing.murmur3_32(14556);
        HashCode hashCode = hashFunction.hashString(key, Charset.forName("utf-8"));
        return hashCode.asInt();
    }

    public void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }
        lock.writeLock().lock();

        try {
            int num = node.getWeight() * TRANSFORM;
            for (int i = 0; i < num; i++) {
                String virtualNodeName = node.getVirtualNodeName(i);
                nodeMap.remove(hashcode(virtualNodeName));
            }
            nodeList.remove(node);

        } finally {
            lock.writeLock().unlock();
        }
    }

}
