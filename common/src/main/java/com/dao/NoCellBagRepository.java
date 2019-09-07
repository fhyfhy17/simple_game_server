package com.dao;

import com.entry.NoCellBagEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface NoCellBagRepository extends MongoRepository<NoCellBagEntry, Long> {


}