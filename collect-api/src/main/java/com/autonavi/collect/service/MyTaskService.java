package com.autonavi.collect.service;

import java.util.List;
import java.util.Map;

import com.autonavi.collect.entity.CollectUserTaskQueryEntity;

public interface MyTaskService {
	CollectUserTaskQueryEntity getTask(CollectUserTaskQueryEntity collectUserTaskQueryEntity) throws Exception;
	CollectUserTaskQueryEntity getTaskPackage(CollectUserTaskQueryEntity collectUserTaskQueryEntity) throws Exception;
	CollectUserTaskQueryEntity getTasksByPackage(CollectUserTaskQueryEntity collectUserTaskQueryEntity)throws Exception;
	CollectUserTaskQueryEntity getTaskClazzCount(CollectUserTaskQueryEntity entity)throws Exception;
	List<CollectUserTaskQueryEntity> getTaskClazzCounts(CollectUserTaskQueryEntity entity)throws Exception;
	CollectUserTaskQueryEntity getTaskImage(CollectUserTaskQueryEntity collectUserTaskQueryEntity)throws Exception;
	CollectUserTaskQueryEntity transferClazzNamefromCache(CollectUserTaskQueryEntity entity,Map<Long,String> clazzJsonMap ) throws Exception;
	Map<Long,String> getTaskClazzCacheJson(List<Long> taskClazzIdList) throws Exception;
}
