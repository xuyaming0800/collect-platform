package com.autonavi.collect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;
import autonavi.online.framework.util.bean.PropertyUtils;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_VERIFY_STATUS;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.dao.CollectTaskCLazzDao;
import com.autonavi.collect.dao.CollectTaskImageDao;
import com.autonavi.collect.entity.CollectBasePackageEntity;
import com.autonavi.collect.entity.CollectTaskBaseEntity;
import com.autonavi.collect.entity.CollectUserTaskQueryEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

/**
 * @author ang.ji
 *	我的任务管理类
 */
@Component
public class MyTaskBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private MyTaskQuery myTaskQuery;
	@Autowired
	private MyTaskPackageQuery myTaskPackageQuery;
	@Autowired
	private MyPackageTasksQuery myPackageTasksQuery;
	@Autowired
	private MyTaskQueryCheck myTaskQueryCheck;
	@Autowired
	private MyTaskPackageQueryCheck myTaskPackageQueryCheck;
	@Autowired
	private MyPackageTasksQueryCheck myPackageTasksQueryCheck;
	@Autowired
	private MyTaskImgsQueryCheck myTaskImgsQueryCheck;
	@Autowired
	private MyTaskImgsQuery myTaskImgsQuery;
	@Autowired
	private MyTaskClazzCacheDetail myTaskClazzCacheDetail;
	@Autowired
	private MyTaskClazzCacheJson myTaskClazzCacheJson;
	@Autowired
	public RedisUtilComponent redisUtilComponent;
	
	@Autowired
	public CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	public CollectBasePackageDao collectBasePackageDao;
	@Autowired
	public CollectTaskImageDao collectTaskImageDao;
	@Autowired
	public CollectTaskCLazzDao collectTaskCLazzDao;
	@Autowired
	public TaskClazzCacheComponent taskClazzCacheComponent;
	@Autowired
	private TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;

	
	
	public MyTaskClazzCacheDetail getMyTaskClazzCacheDetail() {
		return myTaskClazzCacheDetail;
	}
	public MyTaskImgsQueryCheck getMyTaskImgsQueryCheck() {
		return myTaskImgsQueryCheck;
	}
	public MyTaskImgsQuery getMyTaskImgsQuery() {
		return myTaskImgsQuery;
	}
	public MyTaskQuery getMyTaskQuery() {
		return myTaskQuery;
	}
	public MyTaskPackageQuery getMyTaskPackageQuery() {
		return myTaskPackageQuery;
	}
	public MyPackageTasksQuery getMyPackageTasksQuery() {
		return myPackageTasksQuery;
	}
	public MyTaskQueryCheck getMyTaskQueryCheck() {
		return myTaskQueryCheck;
	}
	public MyTaskPackageQueryCheck getMyTaskPackageQueryCheck() {
		return myTaskPackageQueryCheck;
	}
	public MyPackageTasksQueryCheck getMyPackageTasksQueryCheck() {
		return myPackageTasksQueryCheck;
	}
	
	public MyTaskClazzCacheJson getMyTaskClazzCacheJson() {
		return myTaskClazzCacheJson;
	}
	public PropertiesConfig getPropertiesConfig() {
		return propertiesConfig;
	}
	private PropertiesConfig propertiesConfig;

	public MyTaskBusinessManager() throws Exception {
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}

	@Component
	public class MyTaskQueryCheck implements CollectCore<Object, CollectTaskBase> {

		@Autowired
		public MyTaskQueryCheck(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public Object execute(CollectTaskBase collectTaskBase) throws BusinessException {
			return null;
		}
	}
	
	@Component
	public class MyTaskPackageQueryCheck implements CollectCore<Object, CollectBasePackage> {

		@Autowired
		public MyTaskPackageQueryCheck(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public Object execute(CollectBasePackage collectTaskBase) throws BusinessException {
			return null;
		}
	}
	
	@Component
	public class MyPackageTasksQueryCheck implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyPackageTasksQueryCheck(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			//校验父级是否是用户的和业主的 兼容问题 暂时不做业主校验
			CollectBasePackage collectBasePackageDb=(CollectBasePackage)collectBasePackageDao.getCollectBasePackageById(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getAllotUserId(), entity.getCollectBasePackage().getOwnerId());
			if(collectBasePackageDb==null){
				throw new BusinessException(
						BusinessExceptionEnum.TASK_NOT_EXIST);
			}
			if(!collectBasePackageDb.getAllotUserId().equals(entity.getCollectBasePackage().getAllotUserId())){
				throw new BusinessException(
						BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
			}
			entity.setCollectBasePackage(collectBasePackageDb);
			return entity;
		}
	}
	@Component
	public class MyTaskImgsQueryCheck implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyTaskImgsQueryCheck(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			CollectTaskBase base=(CollectTaskBase)collectTaskBaseDao.selectTaskDataForCollect(entity.getCollectTaskBase());
			if(base==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
			}
			if(!base.getAllotUserId().equals(entity.getCollectTaskBase().getCollectUserId())){
				throw new BusinessException(BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
			}
			entity.setCollectTaskBase(base);
			return entity;
		}
	}

	@Component
	public class MyTaskQuery implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyTaskQuery(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			List<CollectTaskBase> list=(List<CollectTaskBase>)collectTaskBaseDao.select(entity.getCollectTaskBase(),entity.getStart(),entity.getLimit());
			CollectUserTaskQueryEntity result=new CollectUserTaskQueryEntity();
			result.setTotal(DaoHelper.getCount());
			List<CollectTaskBaseEntity> listExtras=new ArrayList<CollectTaskBaseEntity>();
			for(CollectTaskBase base:list){
				CollectTaskBaseEntity baseEntity=new CollectTaskBaseEntity();
				PropertyUtils.copy(base, baseEntity);
				entity.setCollectTaskBase(base);
				baseEntity.setExtras(taskCollectUtilBusinessManager.getQueryTaskExtraEntityCollect().execute(entity));
				entity.setCollectTaskBase(null);
				listExtras.add(baseEntity);
			}
			list.clear();
			entity.setCollectTaskBaseList(listExtras);
			return result;
		}
	}
	/**
	 * 获取我的任务包
	 * @author xuyaming
	 *
	 */
	@Component
	public class MyTaskPackageQuery implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyTaskPackageQuery(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			List<CollectBasePackageEntity> list=null;
			CollectUserTaskQueryEntity result=new CollectUserTaskQueryEntity();
			if(entity.getIsPassive()==null){
				list=(List<CollectBasePackageEntity>)collectBasePackageDao.getUserCollectBasePackageByStatus(entity.getCollectBasePackage(),entity.getStart(),entity.getLimit());
				result.setTotal((Long)collectBasePackageDao.getUserCollectBasePackageCountByStatus(entity.getCollectBasePackage()));
			}
		    else if(entity.getIsPassive()){
		    	list=(List<CollectBasePackageEntity>)collectBasePackageDao.getUserPassiveCollectBasePackageByStatus(entity.getCollectBasePackage(),entity.getStart(),entity.getLimit());
		    	result.setTotal((Long)collectBasePackageDao.getUserPassiveCollectBasePackageCountByStatus(entity.getCollectBasePackage()));
			}else{
				list=(List<CollectBasePackageEntity>)collectBasePackageDao.getUserInitiativeCollectBasePackageByStatus(entity.getCollectBasePackage(),entity.getStart(),entity.getLimit());
				result.setTotal((Long)collectBasePackageDao.getUserInitiativeCollectBasePackageCountByStatus(entity.getCollectBasePackage()));
			}
			result.setCollectBasePackageList(list);
			if(entity.getCollectBasePackage().getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
				result.setVerifyPassCount((Long)collectBasePackageDao.getUserCollectBasePackageVerifyCount(entity.getCollectBasePackage(), TASK_VERIFY_STATUS.PASS.getCode(),entity.getIsPassive()));
				result.setVerifyFailCount((Long)collectBasePackageDao.getUserCollectBasePackageVerifyCount(entity.getCollectBasePackage(), TASK_VERIFY_STATUS.FAIL.getCode(),entity.getIsPassive()));
			}
			if(entity.getCollectBasePackage().getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
				result.setVerifyPassCount((Long)collectBasePackageDao.getUserCollectBasePackageVerifyCount(entity.getCollectBasePackage(), TASK_VERIFY_STATUS.FIRST_PASS.getCode(),entity.getIsPassive()));
				result.setVerifyFailCount((Long)collectBasePackageDao.getUserCollectBasePackageVerifyCount(entity.getCollectBasePackage(), TASK_VERIFY_STATUS.FIRST_FAIL.getCode(),entity.getIsPassive()));
			}
			return result;
		}
	}
	/**
	 * 获取我的任务包下的步骤
	 * @author xuyaming
	 *
	 */
	@Component
	public class MyPackageTasksQuery implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyPackageTasksQuery(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			List<CollectTaskBase> list=(List<CollectTaskBase>)collectTaskBaseDao.selectByPackage(entity.getCollectTaskBase(),entity.getStart(),entity.getLimit());
			entity.setTotal(DaoHelper.getCount());
			List<CollectTaskBaseEntity> listExtras=new ArrayList<CollectTaskBaseEntity>();
			for(CollectTaskBase base:list){
				CollectTaskBaseEntity baseEntity=new CollectTaskBaseEntity();
				PropertyUtils.copy(base, baseEntity);
				entity.setCollectTaskBase(base);
				baseEntity.setExtras(taskCollectUtilBusinessManager.getQueryTaskExtraEntityCollect().execute(entity));
				entity.setCollectTaskBase(null);
				listExtras.add(baseEntity);
			}
			list.clear();
			entity.setCollectTaskBaseList(listExtras);
			return entity;
		}
	}
	
	/**
	 * 获取我的任务的图片
	 * @author xuyaming
	 *
	 */
	@Component
	public class MyTaskImgsQuery implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyTaskImgsQuery(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			if(entity.getLevel()==null||entity.getLevel().equals("")
					||entity.getBatchId()==null||entity.getBatchId().equals(0L)){
				//直接读图模式
				List<CollectTaskImg> list=(List<CollectTaskImg>)collectTaskImageDao.selectByBaseId(entity.getCollectTaskBase());
				Long userId=entity.getCollectTaskBase().getCollectUserId();
				Long ownerId=entity.getCollectTaskBase().getOwnerId();
				entity.setTotal(DaoHelper.getCount());
				entity.setCollectTaskImgList(list);
				CollectBasePackage base=(CollectBasePackage)collectBasePackageDao.getCollectBasePackageById(entity.getCollectTaskBase().getTaskPackageId(), userId,ownerId);
				entity.setCollectBasePackage(base);
			}else{
				//读mongodb
				Long userId=entity.getCollectTaskBase().getAllotUserId();
				Long ownerId=entity.getCollectTaskBase().getOwnerId();
				CollectBasePackage base=(CollectBasePackage)collectBasePackageDao.getCollectBasePackageById(entity.getCollectTaskBase().getTaskPackageId(), userId,ownerId);
				entity.setCollectBasePackage(base);
				CollectTaskBaseEntity baseEntity=new CollectTaskBaseEntity();
				PropertyUtils.copy(entity.getCollectTaskBase(), baseEntity);
				baseEntity.setExtras(taskCollectUtilBusinessManager.getQueryTaskExtraEntity().execute(entity));
				entity.setCollectTaskBaseEntity(baseEntity);
			}
			
			return entity;
		}
	}
	
	@Component
	public class MyTaskClazzCacheDetail implements CollectCore<CollectUserTaskQueryEntity, CollectUserTaskQueryEntity> {

		@Autowired
		public MyTaskClazzCacheDetail(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public CollectUserTaskQueryEntity execute(CollectUserTaskQueryEntity entity) throws BusinessException {
			if(entity.getCollectBasePackageList()!=null){
				for(CollectBasePackageEntity pkg:entity.getCollectBasePackageList()){
//					CollectTaskClazz clazz=redisUtilComponent.getRedisJsonCache(entity.getJedis(), CommonConstant.TASK_CLAZZ_CACHE_PREFIX+pkg.getCollectClassId(),
//							CollectTaskClazz.class,JsonBinder.buildNormalBinder());
					String json=entity.getClazzTaskClazzJsonMap().get(Long.valueOf(pkg.getCollectClassId()));
					if(json==null){
						logger.error("clazzId=["+pkg.getCollectClassId()+"] Cache is null ");
						throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
					}
					CollectTaskClazz clazz=JsonBinder.buildNormalBinder(false).fromJson
							(json, CollectTaskClazz.class);
					pkg.setCollectClassName(clazz.getClazzName());
					pkg.setCollectClassPayType(clazz.getClazzPayType());
				}
			}
			if(entity.getCollectTaskBaseList()!=null){
				for(CollectTaskBase base:entity.getCollectTaskBaseList()){
					if(base.getTaskClazzId()!=null){
//						entity.getCollectTaskClazzMap().put(base.getTaskClazzId(),redisUtilComponent.getRedisJsonCache(entity.getJedis(), CommonConstant.TASK_CLAZZ_CACHE_PREFIX+base.getTaskClazzId(),
//								CollectTaskClazz.class,JsonBinder.buildNormalBinder()));
						String json=entity.getClazzTaskClazzJsonMap().get(Long.valueOf(base.getTaskClazzId()));
						if(json==null){
							logger.error("clazzId=["+base.getTaskClazzId()+"] Cache is null ");
							throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
						}else{
							entity.getCollectTaskClazzMap().put(base.getTaskClazzId(),JsonBinder.buildNormalBinder(false).fromJson
									(json, CollectTaskClazz.class));
						}
					}
				}
			}
			
			return entity;
		}
	}
	
	@Component
	public class MyTaskClazzCacheJson implements CollectCore<Map<Long,String>, List<Long>> {

		@Autowired
		public MyTaskClazzCacheJson(MyTaskBusinessManager myTaskBusinessManager) {

		}

		@Override
		public Map<Long,String> execute(List<Long> list) throws BusinessException {
			Map<Long,String> result=new HashMap<Long,String>();
			for(Long id:list){
				String json=taskClazzCacheComponent.getCollectTaskClazzJson(id);
				result.put(id, json);
			}
			return result;
			
		}
	}
}
