package com.dao;

import com.entry.PlayerUnionEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PlayerUnionRepository extends MongoRepository<PlayerUnionEntry, Long> {

}