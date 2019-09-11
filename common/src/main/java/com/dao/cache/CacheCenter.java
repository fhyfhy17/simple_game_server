package com.dao.cache;

import com.entry.BaseEntry;
import com.entry.PlayerEntry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.bson.Document;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class CacheCenter{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final BlockingQueue<BaseEntry> queue = new LinkedBlockingQueue<>(1024);
	
	

	enum Type{
		INSERT,
		UPDATE,
		DELETE,
		;
	}
	
	
	public void add(BaseEntry entry){
		queue.offer(entry);
	}
	
	@Scheduled(fixedDelay=1000*5)// 5秒执行一次
	public void batchSave(){
		
		Document doc2 = new Document(); // org.bson.Document
		PlayerEntry playerEntry=new PlayerEntry(2323);
		playerEntry.setName("bbb");
		mongoTemplate.getConverter().write(playerEntry, doc2);
		Update update = Update.fromDocument(doc2,"_id");
		//mongoTemplate.insert(playerEntry);
		mongoTemplate.upsert(new Query( Criteria.where("_id").is(playerEntry.getId())), update, PlayerEntry.class);
		
		
		
		synchronized(queue){
			
			List<BaseEntry> list =Lists.newArrayList();
			queue.drainTo(list);
			Map<Class,List<BaseEntry>> collect=list.stream().collect(Collectors.groupingBy(x->x.getClass().asSubclass(BaseEntry.class)));
			
			log.info("共有 {} 个集合等待更新",collect.size());
			StopWatch stopWatch = new StopWatch();
			for(Map.Entry<Class,List<BaseEntry>> entry : collect.entrySet()){
				stopWatch.reset();
				stopWatch.start();
				log.info("开始执行 {}",entry.getKey());
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
					Update update1 = new Update();
					for(Map.Entry<String,Object> stringObjectEntry : updateMap.entrySet()){
						if(stringObjectEntry.getKey().equals("id")){
							continue;
						}
						update1.set(stringObjectEntry.getKey(),stringObjectEntry.getValue());
					}
					
					ops.upsert(new Query( Criteria.where("id").is(baseEntry.getId())),update1);
				}
				ops.execute();
				stopWatch.stop();
				log.info("结束执行 {}  ，用时 {} 毫秒",entry.getKey(),stopWatch.getTime());
			}
		}
	}
}
