package com.config;

import com.dao.CustomMongoRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.dao",repositoryBaseClass = CustomMongoRepository.class)
public class MongoConfig{
}