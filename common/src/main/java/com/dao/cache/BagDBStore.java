package com.dao.cache;

import com.dao.BagRepository;
import com.entry.BagEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class BagDBStore implements CacheLoaderWriter<Long, BagEntry> {

    @Override
    public BagEntry load(Long key) throws CacheLoaderException {
        BagRepository bagRepository = SpringUtils.getBean(BagRepository.class);
        return bagRepository.findById(key).orElse(null);
    }

    @Override
    public void delete(Long key) {
        BagRepository bagRepository = SpringUtils.getBean(BagRepository.class);
        bagRepository.deleteById(key);
    }

    @Override
    public void write(Long key, BagEntry bagEntry) {
        BagRepository bagRepository = SpringUtils.getBean(BagRepository.class);
        bagRepository.save(bagEntry);
    }


}