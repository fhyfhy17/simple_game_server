package com.lock;

import com.lock.redis.RedissonConfig;
import com.lock.zk.DistributedLock;
import com.lock.zk.ZkDistributedLock;
import com.util.ContextUtil;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LockUtil{
	
	private static RedissonConfig redissonConfig;
	private static LockType lockType =LockType.Redis; //zk的两个客户端，一个要创建不存在的路径，不知道怎么同步， 另一个curator速度太慢，应该都是实现的不对。。。先用redis
	
	
	enum LockType{
		Redis,
		Zk,
		;
	}
	
	/**
	 * 取得分布式锁
	 */
	public static DisLock lock(String key){
		DisLock disLock;
		if(lockType==LockType.Redis){
			RLock lock=redissonConfig.getClient().getLock(key);
			disLock =new DisLock(lock);
		}else {
			DistributedLock lock=new DistributedLock(key);
			disLock = new DisLock(lock);
		}
	
		return disLock;
	}
	
	@Bean
	public ZkDistributedLock getZkDistributedLock(){
		return new ZkDistributedLock(ContextUtil.zkIpPort, "testLock");
	}
	
	
	@Autowired
	public void setRedissonConfig(RedissonConfig redissonConfig){
		LockUtil.redissonConfig=redissonConfig;
	}
	
}
