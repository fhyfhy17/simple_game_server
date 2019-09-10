package com.dao.cache;

import com.entry.BaseEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
@EnableScheduling
public class MapCacheCenter{
	private ConcurrentHashMap<String,BaseEntry> timingSave= new ConcurrentHashMap<>(1024);
	
	@Data
	@AllArgsConstructor
	public static class SaveEntryWrapper{
		private
	}
	enum Type{
		INSERT,
		UPDATE,
		DELETE,
		;
	}
	
	public void batchSave(){
		
		timingSave.values()
	}
}
