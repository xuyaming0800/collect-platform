package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import autonavi.online.framework.constant.Miscellaneous;
import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.uniqueid.UniqueIDHolder;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.USER_CACHE_TYPE;
import com.autonavi.collect.entity.CollectUserCacheEntity;
import com.autonavi.collect.entity.CoordinateTransferEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.manager.TaskCollectUtilBusinessManager;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.util.AdCodeToNameConvert;
import com.gd.app.entity.WaterImgMessageEntity;
@Service
public class TaskCollectUtilServiceImpl implements TaskCollectUtilService {
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;
	
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;
	private PropertiesConfig propertiesConfig;
	
	public TaskCollectUtilServiceImpl()throws Exception{
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
	}
	

	@Override
	public String[] offsetXY(String x, String y) {
		try {
			//默认传入为百度坐标 不在做偏移处理 保留方法 最大适应性
			String[] coordinate=new String[]{x,y};
			//String[] result=taskCollectUtilBusinessManager.getOffsetCoordinate().execute(coordinate);
			return coordinate;
//		} catch (BusinessException e) {
//			throw new BusinessRunException(e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			throw  new BusinessRunException(e.getMessage(),e);
		}
	}

	@Override
	public String fetchAdcode(double corrX, double corrY) {
		try {
			//默认传入为百度坐标 需要转换为测绘局坐标进行ADCODE计算 不同的坐标系这个方法会有所不同
			CoordinateTransferEntity entity=new CoordinateTransferEntity();
			entity.setFromGpsSys(CommonConstant.GPS_SYSTEM.BAIDU.getCode());
			entity.setToGpsSys(CommonConstant.GPS_SYSTEM.GCJ.getCode());
			entity.setX(corrX);
			entity.setY(corrY);
			double[] coordinate=taskCollectUtilBusinessManager.getTransferCoordinate().execute(entity);
			String result=taskCollectUtilBusinessManager.getFetchAdcode().execute(coordinate);
			return result;
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			throw  new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
		
	}

	@Override
	public Long getUserIdCache(String userName) {
		try {
			CollectUserCacheEntity<Jedis> entity=new CollectUserCacheEntity<Jedis>();
			entity.setUserName(userName);
			entity.setCacheType(USER_CACHE_TYPE.REDIS.getCode());
			Long id=taskCollectUtilBusinessManager.getObtainUserIdCache().execute(entity);
			if(id==null){
				entity.setIsFromDb(true);
				id=taskCollectUtilBusinessManager.getObtainUserIdCache().execute(entity);
			}
			if(id==null){
				entity.setIsFromDb(false);
				id=taskCollectUtilBusinessManager.getAddUserIdCache().execute(entity);
			}
			return id;
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			throw  new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}


	@Override
	public String fetchAdcodeToName(String adcode) {
		try {
			return AdCodeToNameConvert.getInstance().fromAdCodeToName(adcode);
		} catch (Exception e) {
			if(e instanceof BusinessRunException){
				throw (BusinessRunException)e;
			}
			logger.error(e.getMessage(),e);
			throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public double[] transferCoordinate(Integer fromSys, Integer toSys, double x,
			double y) {
		try {
			CoordinateTransferEntity entity=new CoordinateTransferEntity();
			entity.setFromGpsSys(fromSys);
			entity.setToGpsSys(toSys);
			entity.setX(x);
			entity.setY(y);
			return taskCollectUtilBusinessManager.getTransferCoordinate().execute(entity);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			throw  new BusinessRunException(e.getMessage(),e);
		}
		
	}

	@Override
	public String getUserNameCache(Long userId) {
		try {
			CollectUserCacheEntity<Jedis> entity=new CollectUserCacheEntity<Jedis>();
			entity.setUserId(userId);
			entity.setCacheType(USER_CACHE_TYPE.REDIS.getCode());
			String userName=taskCollectUtilBusinessManager.getObtainUserNameCache().execute(entity);
			if(userName==null){
				entity.setIsFromDb(true);
				userName=taskCollectUtilBusinessManager.getObtainUserNameCache().execute(entity);
			}
			return userName;
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			throw  new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public void notifyImageWather(Long userId,Long taskId,List<String> imagePath)throws BusinessException {
		try {
			WaterImgMessageEntity entity=new WaterImgMessageEntity();
			entity.setUserId(userId.toString());
			entity.setTaskId(taskId.toString());
			entity.setImgUrls(imagePath);
			logger.info("发送打水印消息 task_id="+taskId+" 开始");
			logger.info(JsonBinder.buildNonNullBinder(false).toJson(entity));
			syncTaskBusinessMananger.getSendImagePathToWaterQueue().execute(entity);
			logger.info("发送打水印消息 task_id="+taskId+" 结束");
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw  new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public Long getPlatFromUniqueId() throws BusinessException {
		return UniqueIDHolder.getIdWorker().nextId(Miscellaneous.getMyid());
	}


	@Override
	public Boolean checkTaskLevelExist(Long baseId, String level)
			throws BusinessException {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
		queryMap.put("baseId", baseId.toString());
		return mongoDBUtilComponent.checkPropertyIsExist(tableName, queryMap, level);
	}


	@Override
	public Boolean checkTaskLevelBatchInfo(Long baseId, String level,
			String value,String clazzId) throws BusinessException {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
		queryMap.put("baseId", baseId.toString());
		queryMap.put(level+".batchId", value);
		queryMap.put(level+".collectClassId", clazzId);
		return mongoDBUtilComponent.checkObjectIsExist(tableName, queryMap);
	}
	
	@Override
	public Boolean checkTaskClazzInfo(Long baseId, String level,
			String clazzId) throws BusinessException {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
		queryMap.put("baseId", baseId.toString());
		queryMap.put(level+".collectClassId", clazzId);
		return mongoDBUtilComponent.checkObjectIsExist(tableName, queryMap);
	}
	
	@Override
	public Boolean checkTaskLevelBatchInfo(Long baseId, String level,
			String value) throws BusinessException {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
		queryMap.put("baseId", baseId.toString());
		queryMap.put(level+".batchId", value);
		return mongoDBUtilComponent.checkObjectIsExist(tableName, queryMap);
	}
	
	@Override
	public Boolean checkTaskLevelBatchInfo(Long baseId, String level,
			String value,String clazzId,String money) throws BusinessException {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
		queryMap.put("baseId", baseId.toString());
		queryMap.put(level+".batchId", value);
		queryMap.put(level+".collectClassId", clazzId);
		queryMap.put(level+".money", money);
		
		Map<String,Object> queryMapNew=new HashMap<String,Object>();
		queryMapNew.put("baseId", baseId.toString());
		queryMapNew.put(level+".batchId", value);
		queryMapNew.put(level+".collectClassId", clazzId);
		queryMapNew.put(level+".userMoney", money);
		
		List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
		l.add(queryMap);
		l.add(queryMapNew);
		return mongoDBUtilComponent.checkObjectIsExist(tableName, l);
	}


	@Override
	public CollectTaskClazz getCollectTaskClazzByIdFromCache(Long collectClazzId)
			throws BusinessException {
		CollectTaskClazz clazz= taskClazzCacheComponent.getCollectTaskClazz(collectClazzId); 
		if(clazz==null){
			throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
		}
		return clazz;
	}


	@Override
	public Long getPlatFromBatchId(Long userId) throws BusinessException {
		//获取用户取得BatchId的最小间隔
		Integer split=Integer.parseInt(propertiesConfig.getProperty(CommonConstant.MIN_BATCHID_SPLIT_TIME).toString());
		redisUtilComponent.lockIdByRedis(CommonConstant.BATCHID_USER_PREFIX, String.valueOf(userId), BusinessExceptionEnum.BATCHID_CACHE_IS_LOCKED, split);
		Long batchId=UniqueIDHolder.getIdWorker().nextId(Miscellaneous.getMyid());
		//存储batchId到缓存，方便以后校验
		Integer expire=Integer.parseInt(propertiesConfig.getProperty(CommonConstant.MAX_BATCHID_VALID_TIME).toString());
		redisUtilComponent.lockIdByRedis(CommonConstant.BATCHID_CACHE_PREFIX, String.valueOf(batchId)+"_"+String.valueOf(userId), BusinessExceptionEnum.BATCHID_CACHE_IS_LOCKED, expire);
		return batchId;
	}


	@Override
	public Boolean checkPlatFromBatchId(Long batchId,Long userId) throws BusinessException {
		// TODO Auto-generated method stub
		return redisUtilComponent.checkIdIsLockedByRedis(CommonConstant.BATCHID_CACHE_PREFIX, String.valueOf(batchId)+"_"+String.valueOf(userId));
	}


	@Override
	public void clearPlatFromBatchId(Long batchId,Long userId) throws BusinessException {
		// TODO Auto-generated method stub
		redisUtilComponent.releaseIdByRedis(CommonConstant.BATCHID_CACHE_PREFIX, String.valueOf(batchId)+"_"+String.valueOf(userId));
	}
	
	
	

}
