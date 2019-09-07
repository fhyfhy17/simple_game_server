package com.dao;

import com.entry.BagEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface BagRepository extends MongoRepository<BagEntry, Long> {


}