package com.dao.cache;

import com.dao.PlayerRepository;
import com.entry.PlayerEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class PlayerDBStore implements CacheLoaderWriter<Long, PlayerEntry> {

    @Override
    public PlayerEntry load(Long key) throws CacheLoaderException {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        return playerRepository.findById(key).orElse(null);
    }

    @Override
    public void delete(Long key) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        playerRepository.deleteById(key);
    }

    @Override
    public void write(Long key, PlayerEntry playerEntry) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        playerRepository.save(playerEntry);
    }


}