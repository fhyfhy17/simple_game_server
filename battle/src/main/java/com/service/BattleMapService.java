package com.service;

import com.template.TemplateManager;
import com.template.templates.ItemTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BattleMapService extends BaseService{
	
	@Autowired
	private TemplateManager templateManager;
	
	
	
	
	@Override
	public void onStart(){
		ItemTemplate template=templateManager.getTemplate(ItemTemplate.class,1);
		//template.get
		//TODO 做个地图template ， 启动 Scene, 做Scene的 Tick
	}
	
	
	@Override
	public void onClose(){
	
	}
}
