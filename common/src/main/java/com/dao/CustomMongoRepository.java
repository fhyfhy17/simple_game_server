package com.dao;

import com.dao.cache.MapCacheCenter;
import com.entry.BaseEntry;
import com.google.common.collect.Lists;
import com.util.ContextUtil;
import com.util.SpringUtils;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CustomMongoRepository <T, ID extends Serializable> extends SimpleMongoRepository<T, ID>
{
    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;


    public CustomMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }
    
    @Override
    public <S extends T> S save(S entity){
		Assert.notNull(entity, "Entity must not be null!");
	
		
		//if(ContextUtil.id.equals(1)){//TODO 此处应该是缓存是否开启
		String key=entity.getClass().getSimpleName() + entityInformation.getId(entity).toString();
		
		ConcurrentHashMap<String,BaseEntry> timingSave=SpringUtils.getBean(MapCacheCenter.class).getTimingSave();
		System.out.println("是否是new"+entityInformation.isNew(entity));
		if(timingSave.containsKey(key)){
			System.out.println("存在key"+key);
		
		}
		timingSave.put(key,(BaseEntry)entity);
		
		return entity;
		
		//	return entity;
		//}else{
		//	return mongoOperations.save(entity, entityInformation.getCollectionName());
		//}
    }
	
	@Override
	public <S extends T> List<S> saveAll(Iterable<S> entities){
		List<S> list =Lists.newArrayList();
		entities.forEach(
				s->
				   list.add(this.save(s))
		);
		
		BulkOperations ops = SpringUtils.getBean(MongoTemplate.class).bulkOps(BulkOperations.BulkMode.UNORDERED, "test");
		ops.upsert()
		ops.execute();
		
		return list;
	}
	
	
	
	
}