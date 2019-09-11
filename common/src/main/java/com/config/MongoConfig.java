package com.config;

import com.dao.CacheMongoRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.dao",repositoryBaseClass = CacheMongoRepository.class)
public class MongoConfig{
}