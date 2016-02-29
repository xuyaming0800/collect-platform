package com.autonavi.collect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.TaskClazzQueryEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.manager.OtherTaskBusinessManager;
import com.autonavi.collect.service.CollectTaskClazzService;
@Service
public class CollectTaskClazzServiceImpl implements CollectTaskClazzService {
	@Autowired
	private OtherTaskBusinessManager otherTaskBusinessManager;
	
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;

	@Override
	public String getCollectTaskInitiativeClazzTree(String uuid)throws BusinessException  {
		TaskClazzQueryEntity entity=new TaskClazzQueryEntity();
		if(uuid!=null)entity.setId(uuid);
		entity.setPrefix(CommonConstant.TASK_CLAZZ_INITIATIVE_MENU_CACHE_PREFIX);
		String json=otherTaskBusinessManager.getTaskClazzTreeQuery().execute(entity);
		if(json==null){
			taskClazzCacheComponent.refresh();
			json=otherTaskBusinessManager.getTaskClazzTreeQuery().execute(entity);
		}
		return json;
	}

	@Override
	public String getCollectTaskPassiveClazzTree(String uuid)throws BusinessException {
		TaskClazzQueryEntity entity=new TaskClazzQueryEntity();
		if(uuid!=null)entity.setId(uuid);
		entity.setPrefix(CommonConstant.TASK_CLAZZ_PASSIVE_MENU_CACHE_PREFIX);
		String json=otherTaskBusinessManager.getTaskClazzTreeQuery().execute(entity);
		if(json==null){
			taskClazzCacheComponent.refresh();
			json=otherTaskBusinessManager.getTaskClazzTreeQuery().execute(entity);
		}
		return json;
	}

}
