package com.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;

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
    public <S extends T> S save(S entity)
    {
        System.out.println(123);
        return super.save(entity);
    }
}