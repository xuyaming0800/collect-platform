package com.autonavi.collect.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geo.util.CoordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;
import autonavi.online.framework.util.bean.PropertyUtils;

import com.autonavi.collect.bean.BossUserTable;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.component.CollectUserCacheComponent;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_EXTRA_OPERATION;
import com.autonavi.collect.constant.CommonConstant.USER_CACHE_TYPE;
import com.autonavi.collect.dao.BossUserDao;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectUserCacheEntity;
import com.autonavi.collect.entity.CollectUserTaskQueryEntity;
import com.autonavi.collect.entity.CoordinateTransferEntity;
import com.autonavi.collect.entity.TaskExtraInfoEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.util.CoordinateConvert;



/**
 * 采集任务相关工具
 * @author xuyaming
 *
 */
@Component
public class TaskCollectUtilBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private OffsetCoordinate OffsetCoordinate;
	@Autowired
	private FetchAdcode fetchAdcode;
	@Autowired
	private TransferCoordinate transferCoordinate;
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	
	@Autowired
	BossUserDao bossUserDao;
	
	@Autowired
	private CollectUserCacheComponent collectUserCacheComponent;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private AddUserIdCache addUserIdCache;
	@Autowired
	private ObtainUserIdCache obtainUserIdCache;
	@Autowired
	private ObtainUserNameCache obtainUserNameCache;
	@Autowired
	private UpdateTaskExtraInfo updateTaskExtraInfo;
	
	@Autowired
	private SubmitTaskExtraInfo submitTaskExtraInfo;
	
	@Autowired
	private InitTaskExtraInfo initTaskExtraInfo;
	@Autowired
	private QueryTaskExtraEntityCollect queryTaskExtraEntityCollect;
	@Autowired
	private QueryTaskExtraEntity queryTaskExtraEntity;
	@Autowired
	private DeleteWholeTaskExtra deleteWholeTaskExtra;
	
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;
	
	
	public ObtainUserIdCache getObtainUserIdCache() {
		return obtainUserIdCache;
	}
	public ObtainUserNameCache getObtainUserNameCache() {
		return obtainUserNameCache;
	}
	public AddUserIdCache getAddUserIdCache() {
		return addUserIdCache;
	}
	public TransferCoordinate getTransferCoordinate() {
		return transferCoordinate;
	}
	public OffsetCoordinate getOffsetCoordinate() {
		return OffsetCoordinate;
	}
	public FetchAdcode getFetchAdcode() {
		return fetchAdcode;
	}
	
	
	
	public InitTaskExtraInfo getInitTaskExtraInfo() {
		return initTaskExtraInfo;
	}
	public SubmitTaskExtraInfo getSubmitTaskExtraInfo() {
		return submitTaskExtraInfo;
	}
	public UpdateTaskExtraInfo getUpdateTaskExtraInfo() {
		return updateTaskExtraInfo;
	}
	public TaskCollectUtilBusinessManager() throws Exception{
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
	}
	
	
	public QueryTaskExtraEntityCollect getQueryTaskExtraEntityCollect() {
		return queryTaskExtraEntityCollect;
	}
	
	


	public QueryTaskExtraEntity getQueryTaskExtraEntity() {
		return queryTaskExtraEntity;
	}

	



	public DeleteWholeTaskExtra getDeleteWholeTaskExtra() {
		return deleteWholeTaskExtra;
	}





	/**
	 * 偏移坐标
	 * @author xuyaming
	 *
	 */
	@Component
	public class OffsetCoordinate implements CollectCore<String[],String[]> {
		@Autowired
		public OffsetCoordinate(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public String[] execute(String[] obj) throws BusinessException {
			if(obj==null||obj.length<2){
				throw new BusinessException(BusinessExceptionEnum.PARAM_X_RADIUS);
			}
			try {
				String offsetCorrdinate = CoordinateConvert.GPS2Deflexion(obj[0] + "," + obj[1]);
				String[] offsetList = offsetCorrdinate.split(",");
				return offsetList;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw  new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
		
	}
	@Component
	public class TransferCoordinate implements CollectCore<double[],CoordinateTransferEntity> {
		@Autowired
		public TransferCoordinate(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public double[] execute(CoordinateTransferEntity entity) throws BusinessException {
			try {
				double[] xy=new double[2];
				if(entity.getFromGpsSys().equals(CommonConstant.GPS_SYSTEM.BAIDU.getCode())&&entity.getToGpsSys().equals(CommonConstant.GPS_SYSTEM.DEFAULT.getCode())){
					CoordUtils.bd2gps(entity.getX(), entity.getY(), xy);
				}else if(entity.getFromGpsSys().equals(CommonConstant.GPS_SYSTEM.BAIDU.getCode())&&entity.getToGpsSys().equals(CommonConstant.GPS_SYSTEM.GCJ.getCode())){
					CoordUtils.bd2gcj(entity.getX(), entity.getY(), xy);
				}
				//其他情况慢慢实现
				return xy;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw  new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
		
	}
	/**
	 * 计算ADCODE
	 * @author xuyaming
	 *
	 */
	@Component
	public class FetchAdcode implements CollectCore<String,double[]> {
		@Autowired
		public FetchAdcode(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public String execute(double[] obj) throws BusinessException {
			if(obj==null||obj.length<2){
				throw new BusinessException(BusinessExceptionEnum.PARAM_X_RADIUS);
			}
			try {
				com.mapabc.spatial.RegionSearch rs = com.mapabc.spatial.RegionSearch.getInstance();
				String adCode = rs.getPointADCode(obj[0], obj[1]);
				return adCode;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
				throw  new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
		
	}
//	@Component
//	public class AddUserIdCache implements CollectCore<Long,String>{
//		@Autowired
//		public AddUserIdCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
//			
//		}
//
//		@Override
//		public Long execute(String userName) throws BusinessException {
//			try {
//				BossUserTable user=new BossUserTable();
//				user.setUserName(userName);
//				bossUserDao.addBossUser(user, CommonConstant.getSingleDataSourceKey());
//				Long id=DaoHelper.getPrimaryKey();
//				try {
//					collectUserCacheComponent.addUserIdCache(userName, id);
//					return id;
//				} catch (BusinessException e) {// 如果redis抛出异常，直接返回
//					//回退已经写入数据库的数据
//					bossUserDao.delBossUser(id, CommonConstant.getSingleDataSourceKey());
//					logger.error(e.getMessage(),e);
//					throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
//				} 
//				
//			} catch (Exception e) {
//				if(e instanceof BusinessException){
//					throw e;
//				}
//				logger.error(e.getMessage(),e);
//				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
//			}
//		}
//		
//	}
	
	@Component
	public class AddUserIdCache implements CollectCore<Long,CollectUserCacheEntity<?>>{
		@Autowired
		public AddUserIdCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Long execute(CollectUserCacheEntity<?> entity) throws BusinessException {
			boolean lockOne=false;
			try {
				BossUserTable user=new BossUserTable();
				user.setUserName(entity.getUserName());
				String userId=null;
				if(entity.getCacheType().equals(USER_CACHE_TYPE.REDIS.getCode())){
					try {
						redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_USER_FIRST_KEY, entity.getUserName(), BusinessExceptionEnum.USER_INSERT_LOCKED, 10);
						lockOne=true;
					} catch (BusinessException e) {
						if(!String.valueOf(e.getSqlExpEnum().getCode()).equals(BusinessExceptionEnum.USER_INSERT_LOCKED.getCode())){
							throw e;
						}
						logger.info("用户"+entity.getUserName()+"正在完成第一次入库，进入等待，最长5秒");
					}
					if(!lockOne){
						userId=collectUserCacheComponent.getUserIdFromCache(entity.getUserName(), entity.getCacheType());
						int count=0;
						while(userId==null){
							if(count==9){
								break;
							}
							Thread.sleep(500);
							userId=collectUserCacheComponent.getUserIdFromCache(entity.getUserName(),entity.getCacheType());
							count++;
						}
						if(userId!=null){
							logger.info("用户"+entity.getUserName()+" 找到ID="+userId);
							return new Long(userId);
						}
						logger.warn("用户"+entity.getUserName()+"还未获取到,请检查运行环境,进入插入程序");
					}
					
				}else{
					throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
				}
				bossUserDao.addBossUser(user, CommonConstant.getSingleDataSourceKey());
				Long id=DaoHelper.getPrimaryKey();
				try {
					if(entity.getCacheType().equals(USER_CACHE_TYPE.REDIS.getCode())){
						collectUserCacheComponent.addUserIdCache(entity.getUserName(), id,entity.getCacheType());
					}else{
						throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
					}
					return id;
				} catch (BusinessException e) {// 如果redis抛出异常，直接返回
					//回退已经写入数据库的数据
					//bossUserDao.delBossUser(id, CommonConstant.getSingleDataSourceKey());
					logger.error(e.getMessage(),e);
					throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
				} 
				
			} catch (Exception e) {
				if(e instanceof BusinessException){
					throw (BusinessException)e;
				}
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			} finally{
				if(lockOne){
					if(entity.getCacheType().equals(USER_CACHE_TYPE.REDIS.getCode())){
						redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_USER_FIRST_KEY, entity.getUserName());
					}else{
						throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
					}
					
				}
			}
		}
		
	}
	
//	@Component
//	public class ObtainUserIdCache implements CollectCore<Long,String>{
//		@Autowired
//		public ObtainUserIdCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
//			
//		}
//
//		@Override
//		public Long execute(String userName) throws BusinessException {
//			String userId=collectUserCacheComponent.getUserIdFromCache(userName);
//			if(userId!=null){
//				return new Long(userId);
//			}
//			return null;
//		}
//	}
	
	@Component
	public class ObtainUserIdCache implements CollectCore<Long,CollectUserCacheEntity<?>>{
		@Autowired
		public ObtainUserIdCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Long execute(CollectUserCacheEntity<?> entity) throws BusinessException {
			Long id=null;
			if(entity.getIsFromDb()){
				BossUserTable user=(BossUserTable)bossUserDao.getBossUserByName(CommonConstant.getSingleDataSourceKey(), entity.getUserName());
				if(user!=null){
					id=user.getId();
				}
			}
			if(entity.getCacheType().equals(USER_CACHE_TYPE.REDIS.getCode())){
				if(id==null){
					String userId=collectUserCacheComponent.getUserIdFromCache(entity.getUserName(),entity.getCacheType());
					if(userId!=null)
					   id=new Long(userId);
				}
				if(id!=null){
					collectUserCacheComponent.addUserIdCache(entity.getUserName(), id,entity.getCacheType());
				}
				
			}else{
				throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
			}
			return id;
		}
	}
	
	
//	@Component
//	public class ObtainUserNameCache implements CollectCore<String,Long>{
//		@Autowired
//		public ObtainUserNameCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
//			
//		}
//
//		@Override
//		public String execute(Long userId) throws BusinessException {
//			String userName=collectUserCacheComponent.getUserNameFromCache(userId);
//			if(userName!=null){
//				return userName;
//			}
//			return null;
//		}
//	}
	
	@Component
	public class ObtainUserNameCache implements CollectCore<String,CollectUserCacheEntity<?>>{
		@Autowired
		public ObtainUserNameCache(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public String execute(CollectUserCacheEntity<?> entity) throws BusinessException {
			String userName=null;
			if(entity.getIsFromDb()){
				BossUserTable user=(BossUserTable)bossUserDao.getBossUserById(CommonConstant.getSingleDataSourceKey(), entity.getUserId());
				if(user!=null){
					userName=user.getUserName();
				}
			}
			if(entity.getCacheType().equals(USER_CACHE_TYPE.REDIS.getCode())){
				if(userName==null){
					userName=collectUserCacheComponent.getUserNameFromCache(entity.getUserId(),entity.getCacheType());
				}
				if(userName!=null){
					collectUserCacheComponent.addUserIdCache(userName, entity.getUserId(),entity.getCacheType());
				}
			}else{
				throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
			}
			//可扩展其他缓存方式
			return userName;
		}
	}
	@Component
	public class InitTaskExtraInfo implements CollectCore<Long,CollectTaskBase> {
		@Autowired
		public InitTaskExtraInfo(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Long execute(CollectTaskBase collectTaskBase) throws BusinessException {
			String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
			Map<String,Object> insertMap=new HashMap<String,Object>();
			insertMap.put("baseId", collectTaskBase.getId().toString());
			insertMap.put("taskId", collectTaskBase.getPassiveId()==null?null:collectTaskBase.getPassiveId().toString());
			insertMap.put("attrList", new ArrayList<Object>());
			insertMap.put("collectClassId",collectTaskBase.getTaskClazzId().toString());
			CollectTaskClazz clazz=taskClazzCacheComponent.getCollectTaskClazz(collectTaskBase.getTaskClazzId());
			if(clazz==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			insertMap.put("collectClassName",clazz.getClazzName()) ;
			mongoDBUtilComponent.insertObject(tableName, insertMap);
			Map<String,Object> updateQueryMap=new HashMap<String,Object>();
			updateQueryMap.put("baseId", collectTaskBase.getId().toString());
			//检查一遍
			List<Object> ll=mongoDBUtilComponent.selectObject(tableName, updateQueryMap, null, true);
			if(ll.size()==0){
				throw new BusinessException(
						BusinessExceptionEnum.MONGONDB_INSERT_ERROR);
			}
			return null;
		}
	}
	
	@Component
	public class UpdateTaskExtraInfo implements CollectCore<List<Long>,CollectTaskSaveEntity> {
		@Autowired
		public UpdateTaskExtraInfo(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public List<Long> execute(CollectTaskSaveEntity entity) throws BusinessException {
			try {
				List<Long> result=new ArrayList<Long>();
				String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				Map<String,Object> updateQueryMap=new HashMap<String,Object>();
				CollectTaskBase collectTaskBase=entity.getCollectTaskBase();
				List<TaskExtraInfoEntity> extraList=entity.getTaskExtraInfoEntityList();
				updateQueryMap.put("baseId", collectTaskBase.getId().toString());
				if(entity.getIsNew()){
					initTaskExtraInfo.execute(collectTaskBase);
				}
				for(TaskExtraInfoEntity _ent:extraList){
					Map<String,Object> attrListItemQueryMap=new HashMap<String,Object>();
					attrListItemQueryMap.put("baseId", collectTaskBase.getId().toString());
					String temp="";
					if(_ent.getLevel().split(".").length<=2){
						temp="attrList";
					}else{
						temp=_ent.getLevel().substring(0,_ent.getLevel().lastIndexOf(".attrs"))+".attrList";
					}
					attrListItemQueryMap.put(temp+".batchId", _ent.getBatchId());
					//二次校验
					Map<String,Object> tempMap=new HashMap<String,Object>();
					Map<String,Object> tempMapNew=new HashMap<String,Object>();
					tempMap.put("baseId", collectTaskBase.getId().toString());
					if(mongoDBUtilComponent.checkPropertyIsExist(tableName, tempMap, _ent.getLevel())){
						tempMap.remove(_ent.getLevel());
						tempMapNew.remove(_ent.getLevel());
						tempMap.put( _ent.getLevel()+".batchId",_ent.getBatchId() );
						tempMap.put( _ent.getLevel()+".collectClassId",_ent.getCollectClassId() );
						tempMap.put( _ent.getLevel()+".money",_ent.getMoney() );
						
						tempMapNew.put( _ent.getLevel()+".batchId",_ent.getBatchId());
						tempMapNew.put( _ent.getLevel()+".collectClassId",_ent.getCollectClassId() );
						tempMapNew.put( _ent.getLevel()+".userMoney",_ent.getMoney());
						List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
						l.add(tempMap);
						l.add(tempMapNew);
						if(!mongoDBUtilComponent.checkObjectIsExist(tableName, l)){
							//batchId不对应
							logger.error("同一个路径["+_ent.getLevel()+"] batchId 不匹配 请不要重复初始保存步骤");
							throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
						}
						tempMap.remove( _ent.getLevel()+".batchId");
						tempMap.remove( _ent.getLevel()+".collectClassId");
						tempMap.remove( _ent.getLevel()+".money");
						
						tempMapNew.remove( _ent.getLevel()+".batchId");
						tempMapNew.remove( _ent.getLevel()+".collectClassId");
						tempMapNew.remove( _ent.getLevel()+".userMoney");
						tempMap.clear();
					}
					if(Integer.valueOf(_ent.getOperation()).equals(TASK_EXTRA_OPERATION.UPDATE.getCode())){
						 mongoDBUtilComponent.updateCommonObject(tableName,
								    updateQueryMap, _ent.getLevel(), _ent.getValue());
						 if(mongoDBUtilComponent.selectObject(tableName, attrListItemQueryMap, null, true).size()==0){
							 attrListItemQueryMap.remove(temp+".batchId");
							 Map<String,Object> _attrListItem=new HashMap<String,Object>();
							 _attrListItem.put("name",_ent.getLevel().substring(_ent.getLevel().lastIndexOf(".")+1));
							 _attrListItem.put("level", _ent.getLevel());
							 _attrListItem.put("batchId", _ent.getBatchId());
							 _attrListItem.put("collectClazzId", _ent.getCollectClassId());
							 _attrListItem.put("collectClazzName",  _ent.getCollectClassName());
							 _attrListItem.put("money", _ent.getMoney());
							 _attrListItem.put("versionNo", _ent.getVersionNo());
							 _attrListItem.put("userMoney", _ent.getUserMoney());
							 _attrListItem.put("customMoney", _ent.getCustomMoney());
							 mongoDBUtilComponent.updateArrayObject(tableName, attrListItemQueryMap, temp, _attrListItem, true,false);
						 }
					}
					    
					else if(Integer.valueOf(_ent.getOperation()).equals(TASK_EXTRA_OPERATION.DELETE.getCode())){
						 List<Object> deleteList=mongoDBUtilComponent.selectObject(tableName, attrListItemQueryMap, temp+".$", false);
						 mongoDBUtilComponent.deleteCommonObject(tableName,
								 updateQueryMap, _ent.getLevel());
						for(Object obj:deleteList){
							 mongoDBUtilComponent.deleteArrayObject(tableName, attrListItemQueryMap, temp, 
									 PropertyUtils.getValue(obj, temp),false);
						}
						result.add(Long.valueOf(_ent.getBatchId()));
					}
				}
				return result;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}
	/**
	 * 提交时候更新mongodb
	 * @author xuyaming
	 *
	 */
	@Component
	public class SubmitTaskExtraInfo implements CollectCore<List<Long>,CollectTaskSubmitEntity> {
		@Autowired
		public SubmitTaskExtraInfo(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public List<Long> execute(CollectTaskSubmitEntity entity) throws BusinessException {
			try {
				String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				CollectTaskBase collectTaskBase=entity.getCollectTaskBase();
				List<CollectTaskImg> imgList=entity.getCollectTaskImgList();
				for(CollectTaskImg img:imgList){
					if(img.getLevel()!=null&&img.getImageBatchId()!=null&&img.getImageH5Id()!=null){
						Map<String,Object> queryMap=new HashMap<String,Object>();
						Map<String,Object> createQueryMap=new HashMap<String,Object>();
						queryMap.put("baseId", collectTaskBase.getId().toString());
						createQueryMap.put("baseId", collectTaskBase.getId().toString());
						String createLocate=img.getLevel()+".imgs";
						Map<String,Object> _map=new HashMap<String,Object>();
						_map.put("imgH5Id", img.getImageH5Id());
						queryMap.put(img.getLevel()+".imgs.imgH5Id", img.getImageH5Id());
						String prefix=img.getLevel()+".imgs.$.";
					    Map<String,Object> updateMap=new HashMap<String,Object>();
					    updateMap.put(prefix+"lon", img.getCollectOffsetX()==null?null:img.getCollectOffsetX().toString());
					    updateMap.put(prefix+"lat", img.getCollectOffsetY()==null?null:img.getCollectOffsetY().toString());
					    updateMap.put(prefix+"imgType", img.getImageFlag()==null?null:img.getImageFlag().toString());
					    updateMap.put(prefix+"image_url", getCollectImgUrl(img.getImgName()));
					    updateMap.put(prefix+"thumbnai_url", getCollectImgUrl(img.getImgName()));
					    updateMap.put(prefix+"gps_time", img.getGpsTime()==null?null:img.getGpsTime().toString());
					    updateMap.put(prefix+"photograph_time", img.getPhotoTime()==null?null:img.getPhotoTime().toString());
					    updateMap.put(prefix+"point_accury", img.getGpsAccuracy()==null?null:img.getGpsAccuracy().toString());
					    updateMap.put(prefix+"point_level", img.getGpsCount()==null?null:img.getGpsCount().toString());
					    updateMap.put(prefix+"position", img.getPosition()==null?null:img.getPosition().toString());
					    updateMap.put(prefix+"index", img.getImageIndex()==null?null:img.getImageIndex().toString());
					    mongoDBUtilComponent.batchUpdateCommonObject(tableName, queryMap, updateMap,createLocate,createQueryMap,_map);
					}
					
				}
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}
	private String getCollectImgUrl(String imageName){
		if(imageName==null||imageName.equals("")){
			return null;
		}
		String commonUrl = this.propertiesConfig.getProperty(CommonConstant.COLLECT_IMG_URL).toString();
		String[] spiltOne = imageName.split("\\.");
		String[] splitTwo = spiltOne[0].split("_");
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < splitTwo.length - 1; i++) {
			sb.append(File.separator);
			sb.append(splitTwo[i]);
		}
		return commonUrl+sb.toString()+File.separator+imageName;
	}
	/**
	 * 获取额外信息列表
	 * @author xuyaming
	 *
	 */
	@Component
	public class QueryTaskExtraEntityCollect implements CollectCore<Object,CollectUserTaskQueryEntity> {
		@Autowired
		public QueryTaskExtraEntityCollect(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Object execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			try {
				String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				String level=entity.getLevel();
				if(level.length()>0)level=level+".";
				Map<String,Object> queryMap=new HashMap<String,Object>();
				queryMap.put("baseId", entity.getCollectTaskBase().getId().toString());
				List<Object> queryResult=mongoDBUtilComponent.selectObject(tableName, queryMap, level+"attrList", false);
				Object result=null;
				if(queryResult.size()>0){
					Object obj=	queryResult.get(0);
					Object _obj=PropertyUtils.getValue(obj, level+"attrList");
					result=mongoDBUtilComponent.transferMongoObjectoCommon(_obj);
				}
				return result;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}
	
	@Component
	public class QueryTaskExtraEntity implements CollectCore<Object,CollectUserTaskQueryEntity> {
		@Autowired
		public QueryTaskExtraEntity(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Object execute(CollectUserTaskQueryEntity entity)
				throws BusinessException {
			try {
				String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				String level=entity.getLevel();
				Map<String,Object> queryMap=new HashMap<String,Object>();
				queryMap.put("baseId", entity.getCollectTaskBase().getId().toString());
				queryMap.put(level+".batchId", entity.getBatchId().toString());
				List<Object> queryResult=mongoDBUtilComponent.selectObject(tableName, queryMap, level, false);
				Object result=null;
				if(queryResult.size()>0){
					Object obj=	queryResult.get(0);
					result=mongoDBUtilComponent.transferMongoObjectoCommon(PropertyUtils.getValue(obj, level));
					
				}
				return result;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}
	
	@Component
	public class DeleteWholeTaskExtra implements CollectCore<Object,CollectTaskBase> {
		@Autowired
		public DeleteWholeTaskExtra(TaskCollectUtilBusinessManager taskCollectUtilBusinessManager){
			
		}

		@Override
		public Object execute(CollectTaskBase entity)
				throws BusinessException {
			try {
				String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				Map<String,Object> queryMap=new HashMap<String,Object>();
				queryMap.put("baseId", entity.getId().toString());
				mongoDBUtilComponent.removeCommonObject(tableName, queryMap);
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

}
