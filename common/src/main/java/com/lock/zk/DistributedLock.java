package com.lock.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



@Slf4j
public class DistributedLock {

	/** 锁的根节点 */
	private String lockRootNode;
	
	private static final String ROOT = "/Locks";
	
	/** 竞争锁的完整节点 */
	private String fullNode;
	
	public DistributedLock(String lockRootNode) {
		this.lockRootNode = lockRootNode;
	}

	public void lock()  {
		try{
			lock(0,null);
		} catch(KeeperException | InterruptedException | TimeoutException e){
			log.error("",e);
		}
		
	}
	private static final Object o = new Object();
	public void lock(int timeout, TimeUnit unit) throws KeeperException, InterruptedException, TimeoutException{
		//注册锁竞争
		ZooKeeper zk = ZkManager.zk;
		String path = ROOT+lockRootNode;
		
		synchronized(o){
			Stat stat0=zk.exists(ROOT,false);
			if(stat0==null)
			{
				zk.create(ROOT,new byte[0],ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
			//检查锁的路径是否存在
			Stat stat=zk.exists(path,false);
			if(stat==null)
			{
				zk.create(path,new byte[0],ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
		}
		
		fullNode = zk.create(path + "/", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		
		boolean isTimeout = false;
		while(true){
			//获得锁竞争者列表
			List<String> rivals = zk.getChildren(path, false);
			Collections.sort(rivals);
			
			int index = rivals.indexOf(fullNode.substring(path.length() + 1));
			if(index == -1){
				throw new RuntimeException("临时顺序节点丢失");
			}
			else if(index == 0){
				//获得锁直接返回
				return;
			}
			
			if(isTimeout){
				throw new TimeoutException();
			}
			
			//锁是别人的，监听第一个排在自己前面的竞争者是否被移除，被移除表示轮到自己获得锁
			final CountDownLatch countDownLatch = new CountDownLatch(1);
			Stat exists = zk.exists(path + "/" + rivals.get(index - 1),event->{
				if(event.getType() == EventType.NodeDeleted){
					countDownLatch.countDown();
				}
			});
			if(exists == null){
				continue;
			}
			if(timeout == 0){
				countDownLatch.await();
			}else{
				countDownLatch.await(timeout, unit);
				isTimeout = true;
			}
		}
	}
	
	public void unlock() {
		ZooKeeper zk = ZkManager.zk;
		try{
			zk.delete(fullNode, -1);
		} catch(InterruptedException | KeeperException e){
			e.printStackTrace();
		}
	}
	
}
