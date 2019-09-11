package com.dao;

import com.dao.cache.CacheCenter;

import com.entry.BaseEntry;
import com.google.common.collect.Lists;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class CacheMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID>{
    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;


    public CacheMongoRepository(MongoEntityInformation<T, ID> metadata,MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }
    
    @Override
    public <S extends T> S save(S entity){
		Assert.notNull(entity, "Entity must not be null!");
		SpringUtils.getBean(CacheCenter.class).add((BaseEntry)entity);
		return entity;
    }
	
	
	@Override
	public <S extends T> S insert(S entity){
		return this.save(entity);
	}
	
	@Override
	public <S extends T> List<S> insert(Iterable<S> entities){
		return this.saveAll(entities);
	}
	
	@Override
	public <S extends T> List<S> saveAll(Iterable<S> entities){
		List<S> list =Lists.newArrayList();
		entities.forEach(s -> list.add(this.save(s)));
		return list;
	}
	
}