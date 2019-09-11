package com.util;

import com.dao.cache.CacheCenter;
import com.entry.BaseEntry;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBUtil{
	
	public static MongoTemplate mongoTemplate;
	public static CacheCenter cacheCenter;
	
	
	
	public static void forceEntrySave(List<BaseEntry> list,boolean clearPlayerDBCache ){
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		if( clearPlayerDBCache ){
			long playerId = list.get(0).getId();
			synchronized(cacheCenter.lock){
				cacheCenter.clearWhenDelete(playerId);
			}
		}

		for(BaseEntry baseEntry : list){
			mongoTemplate.save(baseEntry);
		}
	}

	
	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		DBUtil.mongoTemplate = mongoTemplate;
	}
	
	@Autowired
	public void setCacheCenter(CacheCenter cacheCenter) {
		DBUtil.cacheCenter = cacheCenter;
	}
}
