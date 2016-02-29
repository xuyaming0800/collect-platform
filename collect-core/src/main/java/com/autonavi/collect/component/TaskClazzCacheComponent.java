package com.autonavi.collect.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.dao.CollectTaskCLazzDao;
import com.autonavi.collect.entity.TaskClazzMenuEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

@Component
public class TaskClazzCacheComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectTaskCLazzDao collectTaskCLazzDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	
	private PropertiesConfig propertiesConfig;
	
	private TaskClazzCacheComponent()throws Exception{
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init(){
		Jedis jedis=null;
		List<CollectTaskClazz> list=null;
		List<CollectTaskClazz> listAll=null;
		//主被动任务按照业主ID排列树
		Map<Long,List<TaskClazzMenuEntity>> activeOwnerMap=null;
		Map<Long,List<TaskClazzMenuEntity>> passiveOwnerMap=null;
		
		Map<Long,TaskClazzMenuEntity> activeEntitys=null;
		List<TaskClazzMenuEntity> tempActiveEntitysList=null;
		Map<Long,TaskClazzMenuEntity> passiveEntitys=null;
		List<TaskClazzMenuEntity> tempPassiveEntitysList=null;
		try {
			
			logger.info("开始初始化任务分类缓存");
			//获取全部任务缓存
			list=(List<CollectTaskClazz>)collectTaskCLazzDao.selectAllVaildTaskClazz(CommonConstant.getSingleDataSourceKey());
			listAll=(List<CollectTaskClazz>)collectTaskCLazzDao.selectAllTaskClazz(CommonConstant.getSingleDataSourceKey());
			if(list!=null){
				logger.info("共读取到使用中的分类信息"+list.size()+"条");
				logger.info("开始缓存使用中的每条分类信息");
				
				activeOwnerMap=new HashMap<Long,List<TaskClazzMenuEntity>>();
				passiveOwnerMap=new HashMap<Long,List<TaskClazzMenuEntity>>();
				
				activeEntitys=new HashMap<Long,TaskClazzMenuEntity>();
				tempActiveEntitysList=new ArrayList<TaskClazzMenuEntity>();
				passiveEntitys=new HashMap<Long,TaskClazzMenuEntity>();
				tempPassiveEntitysList=new ArrayList<TaskClazzMenuEntity>();
				for(CollectTaskClazz collectTaskClazz:list){
					if(collectTaskClazz.getTaskType().equals(CommonConstant.TASK_TYPE.INITIATIVE.getCode())){
						if(collectTaskClazz.getParentId()==null&&collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.MENU.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(false);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							activeEntitys.put(collectTaskClazz.getId(),entity);
							if(activeOwnerMap.get(collectTaskClazz.getOwnerId())==null){
								List<TaskClazzMenuEntity> activeEntitysList=new ArrayList<TaskClazzMenuEntity>();
								activeOwnerMap.put(collectTaskClazz.getOwnerId(), activeEntitysList);
							}
							activeOwnerMap.get(collectTaskClazz.getOwnerId()).add(entity);
						}
						else if(collectTaskClazz.getParentId()!=null&&collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.MENU.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(false);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setPid(collectTaskClazz.getParentId());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							activeEntitys.put(collectTaskClazz.getId(),entity);
							TaskClazzMenuEntity _entity=activeEntitys.get(collectTaskClazz.getParentId());
							if(_entity!=null){
								_entity.getCollectClasses().add(entity);
							}else{
								//万一没有找到父级暂时缓存
								tempActiveEntitysList.add(entity);
							}
						}else if(collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.ITEM.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(true);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setCollectClassCount(collectTaskClazz.getClazzImgCount());
							entity.setCollectClassPay(collectTaskClazz.getClazzPay());
							entity.setCollectClassPayType(collectTaskClazz.getClazzPayType());
							entity.setCollectClassFarCount(collectTaskClazz.getClazzFarImgCount());
							entity.setCollectClassNearCount(collectTaskClazz.getClazzNearImgCount());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setPid(collectTaskClazz.getParentId());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							TaskClazzMenuEntity _entity=activeEntitys.get(collectTaskClazz.getParentId());
							if(_entity!=null){
								_entity.getCollectClasses().add(entity);
							}else{
								//万一没有找到父级暂时缓存
								tempActiveEntitysList.add(entity);
							}
						}
					}else{
						if(collectTaskClazz.getParentId()==null&&collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.MENU.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(false);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							passiveEntitys.put(collectTaskClazz.getId(),entity);
							if(passiveOwnerMap.get(collectTaskClazz.getOwnerId())==null){
								List<TaskClazzMenuEntity> passiveEntitysList=new ArrayList<TaskClazzMenuEntity>();
								passiveOwnerMap.put(collectTaskClazz.getOwnerId(), passiveEntitysList);
							}
							passiveOwnerMap.get(collectTaskClazz.getOwnerId()).add(entity);
						}
						else if(collectTaskClazz.getParentId()!=null&&collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.MENU.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(false);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setPid(collectTaskClazz.getParentId());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							passiveEntitys.put(collectTaskClazz.getId(),entity);
							TaskClazzMenuEntity _entity=passiveEntitys.get(collectTaskClazz.getParentId());
							if(_entity!=null){
								_entity.getCollectClasses().add(entity);
							}else{
								//万一没有找到父级暂时缓存
								tempPassiveEntitysList.add(entity);
							}
						}else if(collectTaskClazz.getClazzType().equals(CommonConstant.TASK_CLAZZ_TYPE.ITEM.getCode())){
							TaskClazzMenuEntity entity=new TaskClazzMenuEntity();
							entity.setIsItem(true);
							entity.setCollectClassId(collectTaskClazz.getId());
							entity.setCollectClassName(collectTaskClazz.getClazzName());
							entity.setCollectClassCount(collectTaskClazz.getClazzImgCount());
							entity.setCollectClassPay(collectTaskClazz.getClazzPay());
							entity.setCollectClassPayType(collectTaskClazz.getClazzPayType());
							entity.setIndex(collectTaskClazz.getClazzIndex());
							entity.setPid(collectTaskClazz.getParentId());
							entity.setOwnerId(collectTaskClazz.getOwnerId());
							TaskClazzMenuEntity _entity=passiveEntitys.get(collectTaskClazz.getParentId());
							if(_entity!=null){
								_entity.getCollectClasses().add(entity);
							}else{
								//万一没有找到父级暂时缓存
								tempPassiveEntitysList.add(entity);
							}
						}
					}
				}
				//整理暂时缓存的数据
				for(TaskClazzMenuEntity entity:tempActiveEntitysList){
					TaskClazzMenuEntity _entity=activeEntitys.get(entity.getPid());
					if(_entity!=null){
						_entity.getCollectClasses().add(entity);
					}else{
						if(entity.getPid()==null){
							activeOwnerMap.get(entity.getOwnerId()).add(entity);
						}
					}
				}
				for(TaskClazzMenuEntity entity:tempPassiveEntitysList){
					TaskClazzMenuEntity _entity=passiveEntitys.get(entity.getPid());
					if(_entity!=null){
						_entity.getCollectClasses().add(entity);
					}else{
						if(entity.getPid()==null){
							passiveOwnerMap.get(entity.getOwnerId()).add(entity);
						}
					}
				}
			}
			
			jedis=redisUtilComponent.getRedisInstance();
			logger.info("整合给客户端的主动任务分类信息");
			//平台化后缓存需要按照不同的业主分类
			for(Long ownerId:activeOwnerMap.keySet()){
				redisUtilComponent.setRedisJsonCache(jedis, CommonConstant.TASK_CLAZZ_INITIATIVE_MENU_CACHE_PREFIX+ownerId, activeOwnerMap.get(ownerId), JsonBinder.buildNonNullBinder(false),0);
			}
			logger.info("整合给客户端的被动任务分类信息");
			//平台化后缓存需要按照不同的业主分类
			for(Long ownerId:passiveOwnerMap.keySet()){
				redisUtilComponent.setRedisJsonCache(jedis, CommonConstant.TASK_CLAZZ_PASSIVE_MENU_CACHE_PREFIX+ownerId, passiveOwnerMap.get(ownerId), JsonBinder.buildNonNullBinder(false),0);
			}
			if(listAll!=null){
				logger.info("共读取到分类信息"+listAll.size()+"条");
				logger.info("开始缓存每条分类信息");
				for(CollectTaskClazz collectTaskClazz:listAll){
					redisUtilComponent.setRedisJsonCache(jedis, CommonConstant.TASK_CLAZZ_CACHE_PREFIX+collectTaskClazz.getId(), collectTaskClazz, JsonBinder.buildNormalBinder(false),0);
				}
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if (jedis != null) {
				redisUtilComponent.returnRedis(jedis);
			}
			if (list!=null){
				logger.info("清理缓存垃圾信息");
				list.clear();
				list=null;
			}
			if(activeEntitys!=null){
				activeEntitys.clear();
				activeEntitys=null;
			}
			if(activeOwnerMap!=null){
				activeOwnerMap.clear();
				activeOwnerMap=null;
			}
			if(tempActiveEntitysList!=null){
				tempActiveEntitysList.clear();
				tempActiveEntitysList=null;
			}
			if(passiveEntitys!=null){
				passiveEntitys.clear();
				passiveEntitys=null;
			}
			if(passiveOwnerMap!=null){
				passiveOwnerMap.clear();
				passiveOwnerMap=null;
			}
			if(tempPassiveEntitysList!=null){
				tempPassiveEntitysList.clear();
				tempPassiveEntitysList=null;
			}
		}
	}
	public void refresh(){
		this.init();
	}
	public CollectTaskClazz getCollectTaskClazz(Long clazzId)throws BusinessException{
		JsonBinder jb=JsonBinder.buildNormalBinder(false);
		CollectTaskClazz taskClazz=redisUtilComponent.getRedisJsonCache(CommonConstant.TASK_CLAZZ_CACHE_PREFIX+clazzId, 
				CollectTaskClazz.class, jb);
		if(taskClazz==null){
			logger.warn("clazzId["+clazzId+"] not found from cache will get from db ");
			taskClazz=(CollectTaskClazz)collectTaskCLazzDao.selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), clazzId);
			if(taskClazz==null){
				logger.error("clazzId=["+clazzId+"] db is null ");
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}else{
				redisUtilComponent.setRedisJsonCache(CommonConstant.TASK_CLAZZ_CACHE_PREFIX+clazzId
						, taskClazz, jb, 0);
			}
			
		}
		return taskClazz;
	}
	public String getCollectTaskClazzJson(Long clazzId)throws BusinessException{
		String taskClazzJson=redisUtilComponent.getRedisStringCache(CommonConstant.TASK_CLAZZ_CACHE_PREFIX+clazzId);
		if(taskClazzJson==null){
			JsonBinder jb=JsonBinder.buildNormalBinder(false);
			logger.warn("clazzId["+clazzId+"] not found from cache will get from db ");
			CollectTaskClazz taskClazz=(CollectTaskClazz)collectTaskCLazzDao.
					selectTaskClazzById(CommonConstant.getSingleDataSourceKey(), clazzId);
			if(taskClazz==null){
				logger.error("clazzId=["+clazzId+"] db is null ");
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
			taskClazzJson=jb.toJson(taskClazz);
			redisUtilComponent.setRedisJsonCache(CommonConstant.TASK_CLAZZ_CACHE_PREFIX+clazzId
					, taskClazz, jb, 0);
					
		}
		return taskClazzJson;
	}
	/**
	 * 远程获取机制 防止redis出问题 以后再使用 需要改造上面的get方法 增加ownerId入参
	 * @param ownerId
	 * @param clazzId
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private String getRemoteCollectTaskClazzJson(Long ownerId,Long clazzId)throws BusinessException{
		HttpClient client=null;
		try {
			HttpPost httpPost = new HttpPost(propertiesConfig.getProperty(CommonConstant.REMOTE_H5_URL)+"/openapi?serviceid=666005");  
			client= new DefaultHttpClient(); 
			Map<String, String> params=new HashMap<String,String>();
			params.put("ownerId", String.valueOf(ownerId));
			params.put("collectClassId",String.valueOf(clazzId));
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());  
			for(Map.Entry<String, String> entry : params.entrySet()){  
			    NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));  
			    valuePairs.add(nameValuePair);  
			}  
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");  
			httpPost.setEntity(formEntity);
			HttpResponse resp = client.execute(httpPost);  
			
			HttpEntity entity = resp.getEntity();  
			String respContent = EntityUtils.toString(entity , "UTF-8").trim();  
			httpPost.abort();
			JsonBinder binder=JsonBinder.buildNormalBinder(false);
			Map<String,Object> result=binder.fromJson(respContent, Map.class, binder.getCollectionType(Map.class, String.class,Object.class));
			if(result.containsKey("success")&&Boolean.valueOf(result.get("success").toString())
					&&result.containsKey("info")&&result.get("info")!=null){
				return result.get("info").toString();
			}else{
				logger.error("远程返回结果错误["+respContent+"]");
				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_NOT_FOUND);
		}finally{
			if(client!=null&&client.getConnectionManager()!=null){
				client.getConnectionManager().shutdown();
			}
		}
	}

}
