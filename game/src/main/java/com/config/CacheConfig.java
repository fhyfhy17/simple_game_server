package com.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Builder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;

@Configuration
@EnableCaching
@Deprecated
public class CacheConfig {
    public enum Caches {
        PlayerEntryCache(() -> {
            return CacheParam.builder().maxNum(50000).build();
        }),
        UserEntryCache(() -> {
            return CacheParam.builder().maxNum(50000).build();
        }),
        ;
        private CacheBuilderInterface in;

        Caches(CacheBuilderInterface in) {
            this.in = in;
        }

    }

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        ArrayList<CaffeineCache> caches = new ArrayList<>();
        for (Caches c : Caches.values()) {
            caches.add(new CaffeineCache(c.name(),
                    Caffeine.newBuilder().recordStats()
                            .maximumSize(c.in.build().maxNum)
                            .build())
            );
        }

        cacheManager.setCaches(caches);

        return cacheManager;
    }

    @Builder
    private static class CacheParam {
        private int maxNum;
    }

    interface CacheBuilderInterface {
        CacheParam build();
    }
}

