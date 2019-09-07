package com.dao.cache;

import com.dao.NoCellBagRepository;
import com.entry.NoCellBagEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class NoCellBagDBStore implements CacheLoaderWriter<Long, NoCellBagEntry> {

    @Override
    public NoCellBagEntry load(Long key) throws CacheLoaderException {
        NoCellBagRepository noCellBagRepository = SpringUtils.getBean(NoCellBagRepository.class);
        return noCellBagRepository.findById(key).orElse(null);
    }

    @Override
    public void delete(Long key) {
        NoCellBagRepository noCellBagRepository = SpringUtils.getBean(NoCellBagRepository.class);
        noCellBagRepository.deleteById(key);
    }

    @Override
    public void write(Long key, NoCellBagEntry noCellBagEntry) {
        NoCellBagRepository noCellBagRepository = SpringUtils.getBean(NoCellBagRepository.class);
        noCellBagRepository.save(noCellBagEntry);
    }


}