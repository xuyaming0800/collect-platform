package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant.RESOURCE_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_VERIFY_STATUS;
import com.autonavi.collect.entity.ActiveTaskAroundSearchEntity;
import com.autonavi.collect.entity.ActiveTaskAroundSearchResultEntity;
import com.autonavi.collect.entity.SearchPassiveTaskFacetResult;
import com.autonavi.collect.entity.SearchPassiveTaskResultEntity;
import com.autonavi.collect.entity.SearchTaskQueryEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.manager.ActiveTaskSearchManager;
import com.autonavi.collect.manager.SearchPassiveTaskManager;
import com.autonavi.collect.service.TaskSearchService;

@Component
public class TaskSearchServiceImpl implements TaskSearchService {

	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private SearchPassiveTaskManager collectPassiveTaskManager;

	@Autowired
	private ActiveTaskSearchManager activeTaskSearchManager;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;
	
//	@Autowired
//	private JedisPool jedisPool;

	@Override
	public SearchPassiveTaskResultEntity aroundTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getAroundTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}

	@Override
	public SearchPassiveTaskResultEntity districtTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getDistrictTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}

	@Override
	public SearchPassiveTaskResultEntity aroundNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getAroundNoUserTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}
	
	@Override
	public SearchPassiveTaskResultEntity aroundNoUserAllTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getAroundNoUserTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}

	@Override
	public SearchPassiveTaskResultEntity districtNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getDistrictNoUserTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}

	/**
	 * 统计
	 * 
	 * @param searchTaskQueryEntity
	 * @return
	 */
	public List<SearchPassiveTaskFacetResult> districtFacetTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getDistrictFacetTask().execute(
					searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}
	}

	/**
	 * 统计
	 * 
	 * @param searchTaskQueryEntity
	 * @return
	 */
	public List<SearchPassiveTaskFacetResult> districtFacetNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity) {
		try {
			return collectPassiveTaskManager.getDistrictFacetNoUserTask()
					.execute(searchTaskQueryEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		}

	}

	@Override
	public List<ActiveTaskAroundSearchResultEntity> activeTaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		try {
			return activeTaskSearchManager.getActiveTaskAroundSearch().execute(
					activeTaskAroundSearchEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}
	
	@Override
	public List<ActiveTaskAroundSearchResultEntity> activeH5TaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		try {
			return activeTaskSearchManager.getActiveH5TaskAroundSearch().execute(
					activeTaskAroundSearchEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}
	
	@Override
	public List<ActiveTaskAroundSearchResultEntity> activeTaskResourceAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		try {
			return activeTaskSearchManager.getActiveTaskResourceAroundSearch().execute(
					activeTaskAroundSearchEntity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}

	@Override
	public boolean activeTaskAroundSearchCheck(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		boolean flag=false;
		try {
			CollectTaskClazz taskClazz=taskClazzCacheComponent.getCollectTaskClazz(activeTaskAroundSearchEntity.getClazzId());
			if(taskClazz.getInitClazzId()!=null){
				//归一同一调整价格的分类
				activeTaskAroundSearchEntity.setClazzId(taskClazz.getInitClazzId());
			}
			activeTaskAroundSearchEntity.setRadius(taskClazz.getClazzDistance());
			List<ActiveTaskAroundSearchResultEntity> l=this.activeTaskAroundSearch(activeTaskAroundSearchEntity);
			//恢复 如果需要
//			activeTaskAroundSearchEntity.setClazzId(taskClazz.getId());
			for(ActiveTaskAroundSearchResultEntity entity:l){
				if(entity.getTaskStatus()==TASK_STATUS.SAVE.getCode()||entity.getTaskStatus()==TASK_STATUS.SUBMIT.getCode()
						||entity.getTaskStatus()==TASK_STATUS.FIRST_AUDIT.getCode()){
					flag=true;
					break;
				}else if(entity.getTaskStatus()==TASK_STATUS.FINISH.getCode()){
					if(entity.getVerifyStatus()==TASK_VERIFY_STATUS.PASS.getCode()){
						flag=true;
						break;
					}
					
				}
			}
			return flag;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}

	@Override
	public List<ActiveTaskAroundSearchResultEntity> vaildActiveTaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		try {
			CollectTaskClazz taskClazz=taskClazzCacheComponent.getCollectTaskClazz(activeTaskAroundSearchEntity.getClazzId());
			if(taskClazz.getInitClazzId()!=null){
				//归一同一调整价格的分类
				activeTaskAroundSearchEntity.setClazzId(taskClazz.getInitClazzId());
			}
			activeTaskAroundSearchEntity=activeTaskSearchManager.getActiveTaskAroundSearchCount().execute(activeTaskAroundSearchEntity);
			List<ActiveTaskAroundSearchResultEntity> l=this.activeTaskAroundSearch(activeTaskAroundSearchEntity);
			//恢复 如果需要
//			activeTaskAroundSearchEntity.setClazzId(taskClazz.getId());
			List<ActiveTaskAroundSearchResultEntity> result=new ArrayList<ActiveTaskAroundSearchResultEntity>();
			Set<Long> tempSet=new HashSet<Long>();
			for(ActiveTaskAroundSearchResultEntity entity:l){
				if(!tempSet.contains(entity.getTaskId())){
					tempSet.add(entity.getTaskId());
					if(entity.getTaskStatus()==TASK_STATUS.SAVE.getCode()||entity.getTaskStatus()==TASK_STATUS.SUBMIT.getCode()
							||entity.getTaskStatus()==TASK_STATUS.FIRST_AUDIT.getCode()){
						result.add(entity);
					}else if(entity.getTaskStatus()==TASK_STATUS.FINISH.getCode()){
						if(entity.getVerifyStatus()==TASK_VERIFY_STATUS.PASS.getCode()){
							result.add(entity);
						}
						
					}
				}
				
			}
			tempSet.clear();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}

	@Override
	public boolean activeH5TaskAroundSearchCheck(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity) {
		boolean flag=false;
		try {
			//母库
			List<ActiveTaskAroundSearchResultEntity> l1=this.activeTaskResourceAroundSearch(activeTaskAroundSearchEntity);
			for(ActiveTaskAroundSearchResultEntity entity:l1){
				if(entity.getTaskStatus()==RESOURCE_STATUS.USE.getCode()){
					flag=true;
					return flag;
				}
			}
			//已采集库
			List<ActiveTaskAroundSearchResultEntity> l=this.activeH5TaskAroundSearch(activeTaskAroundSearchEntity);
			for(ActiveTaskAroundSearchResultEntity entity:l){
				if(entity.getImageStatus()==TASK_IMG_STATUS.USE.getCode()||entity.getImageStatus()==TASK_IMG_STATUS.PASS.getCode()){
					flag=true;
					return flag;
				}
			}
			return flag;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
		}
	}

}
