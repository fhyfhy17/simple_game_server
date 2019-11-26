package com.service.map;

import com.alibaba.fastjson.JSON;
import com.service.BaseService;
import com.service.map.core.astar.MapDataTemplate;
import com.template.TemplateManager;
import com.template.templates.ItemTemplate;
import com.template.templates.ItemTemplateCache;
import com.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BattleMapService extends BaseService{
	
	@Autowired
	private TemplateManager templateManager;
	
	private Map<String,MapDataTemplate> allDataMap = new HashMap<>();		//地图网格数据
	
	@Value("${map.mapData}")
	private String mapDataPath;
	
	@Override
	public void onStart(){
        ItemTemplate template = ItemTemplateCache.get(1);
		//template.get
		//TODO 做个地图template ， 启动 Scene, 做Scene的 Tick
		
		//加载地图网格数据 寻路用
		loadMapData();
	}
	
	
	@Override
	public void onClose(){
	
	}
	
	/**
	 * 加载地图网格数据
	 */
	private void loadMapData(){
		try{
			ClassPathResource res=new ClassPathResource(mapDataPath);
			File file=res.getFile();
			for(File fileData : FileUtil.getFiles(file.getAbsolutePath(),".json")){
				try{
					MapDataTemplate template=JSON.parseObject(new FileInputStream(fileData),MapDataTemplate.class);
					this.allDataMap.put(template.getMapId(),template);
				} catch(IOException e){
					log.error("加载地图 name ={} 报错",fileData.getName(),e);
				}
			}
		} catch(Exception e){
			log.error("加载地图报错");
		}
	}
}
