package com.dao;

import com.entry.UnionEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UnionRepository extends MongoRepository<UnionEntry, Long>
{

}