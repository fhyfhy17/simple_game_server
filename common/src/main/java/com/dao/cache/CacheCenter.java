package com.dao.cache;

import com.entry.BaseEntry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.util.CollectionUtil;
import com.util.StringUtil;
import com.util.support.Cat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class CacheCenter {

    @Getter
    public final Object lock = new Object();

    private final Map<String, BaseEntry> cacheMap = new HashMap<>();
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("saveDbThreadPool")
    private Executor saveDbThreadPool;

    public void add(BaseEntry baseEntry) {
        synchronized (lock) {
            cacheMap.put(makeKey(baseEntry), baseEntry);
        }
    }

    /**
     * 当delete发生时，清空队列里的同id数据，防止delete后落地了脏数据
     */
    public void clearWhenDelete(Class<BaseEntry> type, long id) {
        synchronized (lock) {
            cacheMap.remove(makeKey(type, id));
        }
    }

    /**
     * 清除一个Player所有缓存
     */
    public void clearWhenDeleteAllForPlayer(long id) {
        synchronized (lock) {
            for (String key : cacheMap.keySet()) {
                if (StringUtil.getSpliteSuffix(key, Cat.colon).equals(String.valueOf(id))) {
                    cacheMap.remove(key);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000 * 5)// 5秒执行一次
    public void batchSave() {

        synchronized (lock) {

            List<BaseEntry> list = Lists.newArrayList(cacheMap.values());
            cacheMap.clear();
            //按class分组，因为mongo的批量只支持单集合批量
            Map<Class, List<BaseEntry>> collect = list.stream()
                    .collect(Collectors.groupingBy(x -> x.getClass().asSubclass(BaseEntry.class)));
            if (collect.size() <= 0) {
                return;
            }
            log.info("共有 {} 个集合等待更新", collect.size());

            //开始保存
            for (Map.Entry<Class, List<BaseEntry>> classListEntry : collect.entrySet()) {
                List<BaseEntry> value = classListEntry.getValue();
                Class key = classListEntry.getKey();

                Map<Integer, List<BaseEntry>> integerListMap = CollectionUtil.spiltList(value, 50);//每次批量分成50个数据一份
                Collection<List<BaseEntry>> values = integerListMap.values();
                int count = 0;
                for (List<BaseEntry> baseEntries : values) {
                    int finalI = count++;
                    try {
                        saveDbThreadPool.execute(() -> {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();

                            String name = key.toString() + finalI;
                            log.info("开始执行 {}  一共有 {} 条", name, baseEntries.size());
                            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, key);

                            for (BaseEntry baseEntry : baseEntries) {
                                Map<String, Object> updateMap = null;
                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                                try {
                                    String json = objectMapper.writeValueAsString(baseEntry);
                                    updateMap = objectMapper.readValue(json, Map.class);
                                } catch (IOException e) {
                                    log.error("批量入库，转码报错 {} -> {}", name, baseEntry.getId(), e);
                                }
                                Update update = new Update();
                                if (Objects.isNull(updateMap)) {
                                    continue;
                                }
                                for (Map.Entry<String, Object> stringObjectEntry : updateMap.entrySet()) {
                                    if (stringObjectEntry.getKey().equals("id")) {
                                        continue;
                                    }
                                    update.set(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                                }

                                ops.upsert(new Query(Criteria.where("id").is(baseEntry.getId())), update);
                            }
                            ops.execute();
                            stopWatch.stop();
                            log.info("结束执行 {}  ，用时 {} 毫秒", name, stopWatch.getTime());

                        });

                    } catch (Exception e) {
                        log.error("入库报错", e);
                    }

                }
            }
        }
    }

    private String makeKey(BaseEntry baseEntry) {
        return baseEntry.getClass().asSubclass(BaseEntry.class).getSimpleName() + ":" + baseEntry.getId();
    }

    private String makeKey(Class<BaseEntry> baseEntryClass, long id) {
        return baseEntryClass.asSubclass(BaseEntry.class).getSimpleName() + ":" + id;
    }
}
