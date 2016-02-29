package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.sharding.dao.DaoHelper;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_CLAZZ_STATUS;
import com.autonavi.collect.dao.CollectTaskCLazzDao;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.CollectTaskClazzMgrService;
@Service
public class CollectTaskClazzMgrServiceImpl implements
		CollectTaskClazzMgrService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectTaskCLazzDao collectTaskCLazzDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private TaskClazzCacheComponent TaskClazzCacheComponent;

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectTaskClazz> getCollectTaskCLazzListByParent(Long parentId)
			throws BusinessException {
		try {
			List<CollectTaskClazz> result=(List<CollectTaskClazz>)collectTaskCLazzDao.selectTaskClazzByPid(CommonConstant.getSingleDataSourceKey(), parentId);
			return result!=null?result:new ArrayList<CollectTaskClazz>();
		} 
		catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

	@Override
	public CollectTaskClazz getCollectTaskCLazz(Long id)
			throws BusinessException {
		try {
			CollectTaskClazz collectTaskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), id);
			if(collectTaskClazz==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			return collectTaskClazz;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

	@Override
	public CollectTaskClazz updateCollectTaskClazz(
			CollectTaskClazz collectTaskClazz) throws BusinessException {
		boolean lockOne=false;
		try {
			if(collectTaskClazz==null||collectTaskClazz.getId()==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			//锁定记录
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString(), 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockOne=true;
			//更新记录
			int count=(Integer)collectTaskCLazzDao.updateTaskClazzById(CommonConstant.getSingleDataSourceKey(), collectTaskClazz);
			//判断是否更新到记录
			if(count==0){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			//返回更新后结果
			return (CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), 
					collectTaskClazz.getId());
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}finally{
			if(lockOne){
				//释放锁定记录
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString());
			}
			
			
		}
	}

	@Override
	public CollectTaskClazz addCollectTaskClazz(
			CollectTaskClazz collectTaskClazz) throws BusinessException {
		try {
			//插入记录
			collectTaskCLazzDao.insertTaskClazz(CommonConstant.getSingleDataSourceKey(), collectTaskClazz);
			//获取主键并设置
			collectTaskClazz.setId(DaoHelper.getPrimaryKey());
			return collectTaskClazz;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

	@Override
	public CollectTaskClazz reIndexCollectTaskClazz(Long id, Boolean isUp)
			throws BusinessException {
		CollectTaskClazz collectTaskClazz=null;
		CollectTaskClazz nearByCollectTaskClazz=null;
		boolean lockOne=false;
		boolean lockTwo=false;
		boolean lockThree=false;
		try {
			//查询要更改顺序的分类
			collectTaskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), id);
			if(collectTaskClazz==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			//锁定自身
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString(), 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockOne=true;
			//锁定父级
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"", 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockTwo=true;
			//获取相邻的
			
			Integer index=null;
			if(isUp){
				//往前排序
				index=(Integer)collectTaskCLazzDao.selectPervTaskClazzByPidIndex(CommonConstant.getSingleDataSourceKey(), 
						collectTaskClazz.getParentId(), collectTaskClazz.getClazzIndex()); 
				
			}else{
				//往后排序
				index=(Integer)collectTaskCLazzDao.selectNextTaskClazzByPidIndex(CommonConstant.getSingleDataSourceKey(), 
						collectTaskClazz.getParentId(), collectTaskClazz.getClazzIndex());
			}
			if(index!=null)
			    nearByCollectTaskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzByPidIndex(CommonConstant.getSingleDataSourceKey(), 
					collectTaskClazz.getParentId(),index); 
			//设置新顺序
			if(nearByCollectTaskClazz!=null){
				//锁定邻居
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, nearByCollectTaskClazz.getId().toString(), 
						BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
				lockThree=true;
				Integer nearNewIndex=collectTaskClazz.getClazzIndex();
				collectTaskClazz.setClazzIndex(index);
				nearByCollectTaskClazz.setClazzIndex(nearNewIndex);
				collectTaskCLazzDao.updateTaskClazzIndex(CommonConstant.getSingleDataSourceKey(), collectTaskClazz.getId(), collectTaskClazz.getClazzIndex());
				collectTaskCLazzDao.updateTaskClazzIndex(CommonConstant.getSingleDataSourceKey(), nearByCollectTaskClazz.getId(), nearByCollectTaskClazz.getClazzIndex());
			}
			return collectTaskClazz;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		} finally{
			//释放锁定记录
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, 
						collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"");
			}
			if(lockThree){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, nearByCollectTaskClazz.getId().toString());
			}
			
		}
	}

	@Override
	public CollectTaskClazz deleteCollectTaskClazz(Long id)
			throws BusinessException {
		CollectTaskClazz collectTaskClazz=null;
		boolean lockOne=false;
		boolean lockTwo=false;
		try {
			//查询要更改顺序的分类
			collectTaskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), id);
			if(collectTaskClazz==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			if(!collectTaskClazz.getClazzStatus().equals(TASK_CLAZZ_STATUS.VAILD.getCode())){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_IS_INVALID);
			}
			//锁定自身
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString(), 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockOne=true;
			//锁定父级
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"", 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockTwo=true;
			//逻辑删除分类
			int count=(Integer)collectTaskCLazzDao.updateTaskClazzStatusById(CommonConstant.getSingleDataSourceKey(), 
					id, TASK_CLAZZ_STATUS.INVAILD.getCode(), collectTaskClazz.getClazzStatus(),0);
			collectTaskCLazzDao.updateTaskClazzAfterLogicDel(CommonConstant.getSingleDataSourceKey(), collectTaskClazz.getParentId(), collectTaskClazz.getClazzIndex());
		    if(count>0){
		    	collectTaskClazz.setClazzStatus(TASK_CLAZZ_STATUS.INVAILD.getCode());
		    }
			return collectTaskClazz;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		} finally{
			//释放锁定记录
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, 
						collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"");
			}
			
		}
	}

	@Override
	public CollectTaskClazz recoveryCollectTaskClazz(Long id)
			throws BusinessException {
		CollectTaskClazz collectTaskClazz=null;
		boolean lockOne=false;
		boolean lockTwo=false;
		try {
			//查询要更改顺序的分类
			collectTaskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), id);
			if(collectTaskClazz==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			if(!collectTaskClazz.getClazzStatus().equals(TASK_CLAZZ_STATUS.INVAILD.getCode())){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_IS_VALID);
			}
			//锁定自身
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString(), 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockOne=true;
			//锁定父级
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"", 
					BusinessExceptionEnum.TASK_CLAZZ_MODIFY_LOCKED, 10);
			lockTwo=true;
			//获取最大序列
			Integer index=(Integer)collectTaskCLazzDao.selectMaxIndexPid(CommonConstant.getSingleDataSourceKey(), collectTaskClazz.getParentId());
			//恢复删除分类
			int count=(Integer)collectTaskCLazzDao.updateTaskClazzStatusById(CommonConstant.getSingleDataSourceKey(), 
					id, TASK_CLAZZ_STATUS.VAILD.getCode(), collectTaskClazz.getClazzStatus(),index!=null?index+1:1);
			
		    if(count>0){
		    	collectTaskClazz.setClazzStatus(TASK_CLAZZ_STATUS.VAILD.getCode());
		    }
			return collectTaskClazz;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		} finally{
			//释放锁定记录
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, collectTaskClazz.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_CLAZZ_KEY, 
						collectTaskClazz.getParentId()!=null?collectTaskClazz.getParentId().toString():"");
			}
		}
	}

	@Override
	public void refreshCollectTaskClazzCache() {
		try {
			TaskClazzCacheComponent.refresh();
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

}
