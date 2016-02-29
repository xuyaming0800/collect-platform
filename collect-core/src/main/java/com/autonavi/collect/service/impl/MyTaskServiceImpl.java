package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.entity.CollectUserTaskQueryEntity;
import com.autonavi.collect.manager.MyTaskBusinessManager;
import com.autonavi.collect.manager.TaskCollectUtilBusinessManager;
import com.autonavi.collect.service.MyTaskService;

@Service
public class MyTaskServiceImpl implements MyTaskService {

	@Resource
	public MyTaskBusinessManager myTaskBusinessManager;
	@Autowired
	public RedisUtilComponent redisUtilComponent;
	@Autowired
	public TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;

	@Override
	public CollectUserTaskQueryEntity getTask(CollectUserTaskQueryEntity collectUserTaskQueryEntity) throws Exception {
		try {
			myTaskBusinessManager.getMyTaskQueryCheck().execute(collectUserTaskQueryEntity.getCollectTaskBase());
			return myTaskBusinessManager.getMyTaskQuery().execute(collectUserTaskQueryEntity);
		} finally {

		}
	}

	@Override
	public CollectUserTaskQueryEntity getTaskPackage(
			CollectUserTaskQueryEntity collectUserTaskQueryEntity)
			throws Exception {
		try {
			myTaskBusinessManager.getMyTaskPackageQueryCheck().execute(collectUserTaskQueryEntity.getCollectBasePackage());
			return myTaskBusinessManager.getMyTaskPackageQuery().execute(collectUserTaskQueryEntity);
		} finally {

		}
	}

	@Override
	public CollectUserTaskQueryEntity getTasksByPackage(
			CollectUserTaskQueryEntity collectUserTaskQueryEntity)
			throws Exception {
		try {
			//需要实现check
			collectUserTaskQueryEntity=myTaskBusinessManager.getMyPackageTasksQueryCheck().execute(collectUserTaskQueryEntity);
			return myTaskBusinessManager.getMyPackageTasksQuery().execute(collectUserTaskQueryEntity);
		} finally {
//			redisUtilComponent.returnRedis(jedis);
//			collectUserTaskQueryEntity.setJedis(null);

		}
	}

	@Override
	public CollectUserTaskQueryEntity getTaskImage(
			CollectUserTaskQueryEntity collectUserTaskQueryEntity)
			throws Exception {
		try {
			//需要实现check
			collectUserTaskQueryEntity=myTaskBusinessManager.getMyTaskImgsQueryCheck().execute(collectUserTaskQueryEntity);
			return myTaskBusinessManager.getMyTaskImgsQuery().execute(collectUserTaskQueryEntity);
		} finally {

		}
	}

	@Override
	public CollectUserTaskQueryEntity transferClazzNamefromCache(
			CollectUserTaskQueryEntity entity,Map<Long,String> clazzJsonMap) throws Exception {
		try{
			entity.setClazzTaskClazzJsonMap(clazzJsonMap);
			return myTaskBusinessManager.getMyTaskClazzCacheDetail().execute(entity);
		}finally{
			
		}
	}

	@Override
	public Map<Long, String> getTaskClazzCacheJson(List<Long> taskClazzIdList)
			throws Exception {
		//暂时在此处处理类型 以后平台化后类型名字通过平台的服务在controller中获取
		return myTaskBusinessManager.getMyTaskClazzCacheJson().execute(taskClazzIdList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CollectUserTaskQueryEntity getTaskClazzCount(CollectUserTaskQueryEntity collectUserTaskQueryEntity)
			throws Exception {
		try {
			//需要实现check
			myTaskBusinessManager.getMyTaskImgsQueryCheck().execute(collectUserTaskQueryEntity);
			Object obj=taskCollectUtilBusinessManager.getQueryTaskExtraEntityCollect().execute(collectUserTaskQueryEntity);
			List<Map<String,Object>> list=(List<Map<String,Object>>)obj;
			Long count=0L;
			for(Map<String,Object> _m:list){
				if(_m.get("collectClazzId").toString().equals(collectUserTaskQueryEntity.getCollectClassId().toString())){
					count=count+1;
				}
			}
			CollectUserTaskQueryEntity entity=new CollectUserTaskQueryEntity();
			entity.setCollectClassId(collectUserTaskQueryEntity.getCollectClassId());
			entity.setCollectClassCount(String.valueOf(count));
//			return myTaskBusinessManager.getMyTaskImgsQuery().execute(collectUserTaskQueryEntity);
		    return entity;
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectUserTaskQueryEntity> getTaskClazzCounts(
			CollectUserTaskQueryEntity collectUserTaskQueryEntity) throws Exception {
		try {
			//需要实现check
			myTaskBusinessManager.getMyTaskImgsQueryCheck().execute(collectUserTaskQueryEntity);
			Object obj=taskCollectUtilBusinessManager.getQueryTaskExtraEntityCollect().execute(collectUserTaskQueryEntity);
			List<Map<String,Object>> list=(List<Map<String,Object>>)obj;
			Map<String,String> temp=new HashMap<String,String>();
			List<CollectUserTaskQueryEntity> result=new ArrayList<CollectUserTaskQueryEntity>();
			for(Map<String,Object> _m:list){
				CollectUserTaskQueryEntity entity=new CollectUserTaskQueryEntity();
				entity.setCollectClassId(_m.get("collectClazzId").toString());
				if(temp.containsKey(entity.getCollectClassId())){
					temp.put(entity.getCollectClassId(), String.valueOf(new Long(temp.get(entity.getCollectClassId())).longValue()+1));
				}else{
					temp.put(entity.getCollectClassId(),"1");
				}
			}
			for(String key:temp.keySet()){
				CollectUserTaskQueryEntity entity=new CollectUserTaskQueryEntity();
				entity.setCollectClassId(key);
				entity.setCollectClassCount(temp.get(key));
				result.add(entity);
			}
			
		    return result;
		} finally {

		}
	}

}
