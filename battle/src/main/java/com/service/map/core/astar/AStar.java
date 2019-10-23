package com.service.map.core.astar;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class AStar {

    public static final float ParallelRange = 2.0f;
    private AtomicInteger count = new AtomicInteger(0);

    private PriorityQueue<Node> openList;

    // 已完成路径的list
    private LinkedList<Node> closedList;


    private Grid grid;
    private Node targetNode;
    private Node startNode;

    private Lock lock = new ReentrantLock();

    /**
     * 构造方法
     */
    public AStar(Grid grid) {
        this.grid = grid;
        openList = new PriorityQueue<>(256, Node::compareTo);
        closedList = new LinkedList<>();
    }


    /**
     * 找寻路径
     *
     * @return true为找到了路径，调用getPath获取路径节点列表
     */
    public LinkedList<Node> findPath(Node startNode, Node targetNode) {
        try {
            this.startNode = startNode;
            this.targetNode = targetNode;
            this.openList.clear();
            this.closedList.clear();
            lock.lock();
            return search(startNode, targetNode);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 寻路算法
     *
     * @return 寻路列表
     */
    private LinkedList<Node> search(Node startNode, Node targetNode) {
        startNode.setCostFromStart(0);
        startNode.setCostToTarget(startNode.getCost(targetNode));
        startNode.parentNode = null;
        openList.offer(startNode);
        while (!openList.isEmpty()) {
            Node firstNode = openList.poll();
            if (firstNode.equals(targetNode)) {
                return makePath(firstNode);
            } else {
                closedList.add(firstNode);
                List<Node> _limit = grid.getNearByNodes(firstNode.getX(), firstNode.getY());
                for(Node neighborNode : _limit){
                    boolean isOpen=openList.contains(neighborNode);
                    boolean isClosed=closedList.contains(neighborNode);
                    boolean canMove=canMove(neighborNode.getX(),neighborNode.getY());
                    if(!isOpen && !isClosed && canMove){
                        neighborNode.setCostFromStart(firstNode.getCostFromStart() + 1);
                        neighborNode.setCostToTarget(neighborNode.getCost(targetNode));
                        neighborNode.parentNode=firstNode;
                        openList.offer(neighborNode);
                    }
                }
            }

        }
        closedList.clear();
        return null;
    }


    /**
     * 判定是否为可通行区域
     *
     * @param x x
     * @param y y
     * @return 能否移动
     */
    private boolean canMove(int x, int y) {
        System.out.println(count.incrementAndGet());
        if (x < 0 || y < 0) {
            return false;
        }
        Node node = grid.getNode(x, y);
        if (node == null) {
            return false;
        }
        return grid.getNode(x, y).isWalkable();
    }


    /**
     * 通过Node制造行走路径
     *
     * @param node 终点
     * @return 经过路径
     */
    private LinkedList<Node> makePath(Node node) {
        LinkedList<Node> path = new LinkedList<>();
        while (node.parentNode != null) {
            path.addFirst(node);
            node = node.parentNode;
        }
        path.addFirst(node);
        return path;
    }
}
