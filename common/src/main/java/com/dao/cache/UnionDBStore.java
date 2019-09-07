package com.dao.cache;

import com.dao.UnionRepository;
import com.entry.UnionEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;
import java.util.List;

@Repository
public class UnionDBStore implements CacheLoaderWriter<Long, UnionEntry> {

    @Override
    public UnionEntry load(Long key) throws CacheLoaderException {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        List<UnionEntry> all=unionRepository.findAll();
        //TODO 现在想把这个全部帮派，存到redis里，问题来了，
        // 1 由谁来加载，启动的第一个服吗？
        // 2 加载出来了怎么存到redis里， 循环 hash到 N个redis里吗
        
        
        return unionRepository.findById(key).orElse(null);
    }


    @Override
    public void delete(Long key) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.deleteById(key);
    }

    @Override
    public void write(Long key, UnionEntry unionEntry) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.save(unionEntry);
    }


}