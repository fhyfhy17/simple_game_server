package com.lock;

import com.lock.redis.RedissonConfig;
import com.lock.zk.ZkDistributedLock;
import com.util.ContextUtil;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LockUtil{
	
	private static RedissonConfig redissonConfig;
	private static ZkDistributedLock zkDistributedLock;
	private static LockType lockType =LockType.Redis;
	
	
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
			ZkDistributedLock.ZkLock lock=zkDistributedLock.getLock(key);
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
	
	@Autowired
	public void setZkDistributedLock(ZkDistributedLock zkDistributedLock){
		LockUtil.zkDistributedLock = zkDistributedLock;
	}
}
