package com.dao.cache;

import com.dao.UserRepository;
import com.entry.UserEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class UserDBStore implements CacheLoaderWriter<Long, UserEntry> {

    @Autowired
    private UserRepository userRepo;
    private static Logger logger = LoggerFactory.getLogger(UserDBStore.class);

    @Override
    public UserEntry load(Long key) throws CacheLoaderException {

        logger.info(String.valueOf(userRepo));
        return userRepo.findById(key).orElse(null);
    }


    @Override
    public void delete(Long key) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        userRepository.deleteById(key);
    }

    @Override
    public void write(Long key, UserEntry userEntry) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        userRepository.save(userEntry);
    }


}