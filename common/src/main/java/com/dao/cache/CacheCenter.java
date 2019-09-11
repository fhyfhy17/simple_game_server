package com.dao.cache;

import com.entry.BaseEntry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class CacheCenter{
	
	@Autowired
	private MongoTemplate mongoTemplate;

	private final BlockingQueue<BaseEntry> queue = new LinkedBlockingQueue<>();
	
	@Getter
	public final Object lock = new Object();
	
	public void add(BaseEntry baseEntry){
		synchronized(lock){
			queue.offer(baseEntry);
		}
	}
	
	/**
	 * 当delete发生时，清空队列里的同id数据，防止delete后落地了脏数据
	 */
	public void clearWhenDelete(long id){
		synchronized(lock){
			queue.removeIf(next->id==next.getId());
		}
	}
	
	
	
	@Scheduled(fixedDelay=1000*5)// 5秒执行一次
	public void batchSave(){
		
		List<BaseEntry> list =Lists.newArrayList();
		synchronized(lock){
			queue.drainTo(list);
		
			
			//按class分组，因为mongo的批量只支持单集合批量
			Map<Class,List<BaseEntry>> collect=list.stream()
					.collect(Collectors.groupingBy(x->x.getClass().asSubclass(BaseEntry.class)));
			//过滤合并，如果list里，有多个同一类型entity，那么用后面那个，前面的可以抛弃
			for(List<BaseEntry> entryList : collect.values()){
				Set<Long> tempSet = new HashSet<>();
				
				for(int i=entryList.size() - 1;i >= 0;i--){
					BaseEntry baseEntry=entryList.get(i);
					if(tempSet.contains(baseEntry.getId())){
						list.remove(i);
						continue;
					}
					tempSet.add(baseEntry.getId());
				}
			}
			
			
			log.info("共有 {} 个集合等待更新",collect.size());
			StopWatch stopWatch = new StopWatch();
			for(Map.Entry<Class,List<BaseEntry>> entry : collect.entrySet()){
				stopWatch.reset();
				stopWatch.start();
				log.info("开始执行 {}  一共有 {} 条",entry.getKey(),entry.getValue().size());
				BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, entry.getKey());
				
				for(BaseEntry baseEntry : entry.getValue()){
					Map<String, Object> updateMap=null;
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
					
					try{
						String	json=objectMapper.writeValueAsString(baseEntry);
						updateMap = objectMapper.readValue(json, Map.class);
					} catch(IOException e){
						log.error("批量入库，转码报错 {} -> {}",entry.getKey(),baseEntry.getId(),e);
					}
					Update update = new Update();
					for(Map.Entry<String,Object> stringObjectEntry : updateMap.entrySet()){
						if(stringObjectEntry.getKey().equals("id")){
							continue;
						}
						update.set(stringObjectEntry.getKey(),stringObjectEntry.getValue());
					}
					
					ops.upsert(new Query( Criteria.where("id").is(baseEntry.getId())),update);
				}
				ops.execute();
				stopWatch.stop();
				log.info("结束执行 {}  ，用时 {} 毫秒",entry.getKey(),stopWatch.getTime());
			}
			
		}
		
	}
	
}
