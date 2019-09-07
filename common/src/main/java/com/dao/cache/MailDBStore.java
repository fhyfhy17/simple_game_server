package com.dao.cache;

import com.dao.MailRepository;
import com.entry.MailEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class MailDBStore implements CacheLoaderWriter<Long, MailEntry> {

    @Autowired
    private MailRepository mailRepository;
    private static Logger logger = LoggerFactory.getLogger(MailDBStore.class);

    @Override
    public MailEntry load(Long key) throws CacheLoaderException {

        return mailRepository.findById(key).orElse(null);
    }


    @Override
    public void delete(Long key) {
        MailRepository mailRepository = SpringUtils.getBean(MailRepository.class);
        mailRepository.deleteById(key);
    }

    @Override
    public void write(Long key, MailEntry mailEntry) {
        MailRepository mailRepository = SpringUtils.getBean(MailRepository.class);
        mailRepository.save(mailEntry);
    }


}