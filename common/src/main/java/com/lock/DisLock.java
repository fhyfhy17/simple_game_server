package com.lock;

import com.lock.zk.ZkDistributedLock;
import org.redisson.api.RLock;

public class DisLock{
	
	private RLock rLock;
	private ZkDistributedLock.ZkLock zkLock;
	private LockUtil.LockType lockType;
	
	public DisLock(RLock rLock){
		this.rLock=rLock;
		this.lockType=LockUtil.LockType.Redis;
	}
	
	public DisLock(ZkDistributedLock.ZkLock lock){
		this.zkLock=lock;
		this.lockType=LockUtil.LockType.Zk;
	}
	
	
	public DisLock lock(){
		if(lockType==LockUtil.LockType.Redis){
			rLock.lock();
		}else {
			zkLock.lock();
		}
		return this;
	}
	
	public void unLock(){
		if(lockType==LockUtil.LockType.Redis){
			rLock.unlock();
		}else {
			zkLock.unlock();
		}
	}
}
