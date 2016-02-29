package com.gd.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;

import com.gd.app.appenum.TaskStatusEnum;
import com.gd.app.dao.AgentTaskBaseDao;
import com.gd.app.dao.AgentTaskTokenDao;
import com.gd.app.entity.AgentTaskBase;
import com.gd.app.entity.AgentTaskMz;
import com.gd.app.entity.AgentTaskToken;
import com.gd.app.entity.AgentTaskUploadEntity;
import com.gd.app.entity.Task;
import com.gd.app.entity.TaskLockResultEntity;
import com.gd.app.entity.ViewUserScore;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.exception.SearchException;
import com.gd.app.service.TaskHandlerService;
import com.gd.app.util.CoordinateConvert;
import com.gd.app.util.DateUtil;
import com.gd.app.util.StringUtil;
import com.gd.app.util.SysProps;



//@Service("taskHandlerService")
public class TaskHandlerServiceImpl implements TaskHandlerService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private AgentTaskTokenDao agentTaskTokenDao;
	@Resource
	private AgentTaskBaseDao agentTaskBaseDao;
	@Resource
	private JedisPool jedisPool;
	@Resource
	private SearchEngineServiceImp searchEngineService;

	@Override
	public AgentTaskToken addAgentTaskToken(AgentTaskToken token) {
		int expire = 0;
		try {
			expire = new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_TOKEN_GET_MIN_SPLIT).toString());
			this.checkDuplicateOperation(token.getUserName() + "_TG",
					"TOKEN_GET", AppExceptionEnum.TASK_TOKEN_IN_EXPIRE, expire);
		} catch (AppException e) {
			throw e;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		try {
			UUID uuid = UUID.randomUUID();
			token.setTokenId(uuid.toString());
			agentTaskTokenDao.addAgentTaskToken(token);
			logger.info("[插入token信息]用户=" + token.getUserName() + "，uuid="
					+ uuid.toString() + ",token_id=" + token.getId());
			return token;
		} catch (Exception e) {
			logger.error("用户[" + token.getUserName() + "]插入token信息出现异常", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}
	}

	private void checkDuplicateOperation(String key, String value,
			AppExceptionEnum appExceptionEnum, int expire) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = jedisPool.getResource();
			result = jedis.sadd(key, value);// 如果有两个相同的key，返回值是-1
			jedis.expire(key, expire);// 1秒过期
		} catch (Exception e) {// 如果redis抛出异常，直接返回
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e);
			return;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		if (result != 1) {// 如果返回值是-1，驳回此保存或者提交请求
			throw new AppException(appExceptionEnum);
		}
	}

	@Override
	public AgentTaskToken getAgentTaskToken(String id, String userName) {
		// TODO Auto-generated method stub
		try {
			List<?> l = (List<?>) agentTaskTokenDao.getAgentTaskTokenByTokenId(
					id, userName);
			if (l == null || l.size() == 0) {
				return null;
			} else {
				return (AgentTaskToken) l.get(0);
			}
		} catch (Exception e) {
			logger.info("[" + id + "]查询TOKEN异常", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}

	}

	@Override
	public void deleteAgentTaskTokenForSave(String tokenId, String userName) {
		try {
			agentTaskTokenDao.moveAgentTaskToken(tokenId, userName);
			int count = (Integer) agentTaskTokenDao.deleteAgentTaskToken(
					tokenId, userName);
			if (count == 0)
				logger.info("[删除TOKEN信息] =ID[" + tokenId + "] USER_NAME=["
						+ userName + "] FIEID_ONE不为空 不能删除");
			else
				logger.info("[删除TOKEN信息] =ID[" + tokenId + "] USER_NAME=["
						+ userName + "] 删除成功");
		} catch (Exception e) {
			logger.error("[" + tokenId + "]删除Token异常", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}

	}

	@Override
	public TaskLockResultEntity saveLockTask(String taskId, String userName,
			String dataName, String x, String y, String adCode,
			String deviceInfo, String scoreId,String tokenId) {
		logger.info("任务保存入口,执行任务保存 deviceInfo=[" + deviceInfo + "],username=["
				+ userName + "],dataname=[" + dataName + "],adcode=[" + adCode
				+ "]");
		try {
			TaskLockResultEntity entity = this.saveLockTaskInSearchEngine(
					taskId, userName, dataName, x, y, adCode, deviceInfo,scoreId);
			//暂时没有运营支撑的的表 本方法暂时不实现
			this.updateUserInfomation(userName, entity.getBaseId()+"");
			logger.info("保存返回的数据信息 entity========dataname=["
					+ entity.getDataName() + "],baseid=[" + entity.getBaseId()
					+ "],endtime=[" + entity.getEndTime() + "],submittime=["
					+ entity.getSubmitTime() + "]");
			
			if(tokenId!=null&&!tokenId.equals("")){
				updateAgentTaskTokenForSave(tokenId,userName);
			}
			return entity;
		} catch (AppException e) {
			throw e;
		}
	}
	
	@Override
	public TaskLockResultEntity saveLockUserTask(String taskId,
			String userName, String dataName, String x, String y,
			String adCode, String deviceInfo, String scoreId,String tokenId) {
		logger.info("任务保存入口,执行定向任务保存 deviceInfo=[" + deviceInfo
				+ "],username=[" + userName + "],dataname=[" + dataName
				+ "],adcode=[" + adCode + "]");
		try {
			TaskLockResultEntity entity = this.saveLockTaskInUser(taskId,
					userName, dataName, x, y, adCode, deviceInfo,scoreId);
			logger.info("保存返回的数据信息 entity========dataname=["
					+ entity.getDataName() + "],baseid=[" + entity.getBaseId()
					+ "],endtime=[" + entity.getEndTime() + "],submittime=["
					+ entity.getSubmitTime() + "]");
			//暂时没有运营支撑的的表 本方法暂时不实现
			this.updateUserInfomation(userName, entity.getBaseId()+"");
			if(tokenId!=null&&!tokenId.equals("")){
				updateAgentTaskTokenForSave(tokenId,userName);
			}
			return entity;
		} catch (AppException e) {
			throw e;
		}
	}
	
	private TaskLockResultEntity saveLockTaskInUser(String taskId,
			String userName, String dataName, String x, String y,
			String adCode, String deviceInfo,String scoreId) throws AppException {

		logger.info("进入保存定向任务  taskId=[" + taskId + "]," + "userName=["
				+ userName + "],dataName=[" + dataName + "],adcode=[" + adCode
				+ "]");

		//String subAdCode = adCode.substring(0, 4);

		/** 格式化用户传入的数据名称 */
		String regDataName = StringUtil.formatTaskName(dataName);
		logger.info("[门址保存][" + userName + "]门址=["
				+ dataName + "]格式化后门址=[" + regDataName + "]");

		/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
		boolean activeTask = taskId == null || taskId.isEmpty();
		// 主动任务报错
		if (activeTask) {
			logger.info("定向任务必须是被动任务");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);

		} else {

			logger.info("保存的是被动任务 userName=[" + userName + "],dataName=["
					+ dataName + "],adcode=[" + adCode + "]");

			/** 防止同一个用户频繁操作同一个接口 */
			this.checkDuplicateOperation(taskId + "," + userName,
					"passive-task",
					AppExceptionEnum.DUPLICATE_PASSIVE_TASK_SAVE_ERROR);

			/** 检查该被动任务是否可以保存（是否被别人保存/提交） */
			try {
				this.passiveUserTaskCheckInSave(taskId, userName);
			} catch (AppException e) {
				throw e;
			}
		}

		logger.info("保存的定向任务开始写入到数据库中 [" + userName
				+ "], dataName=[" + dataName + "],taskId=" + taskId);

		/** 保存的任务入库 */
		Map<String, Object> saveMap = new HashMap<String, Object>();
		try {
			saveMap = this.saveUserTaskData2DB(taskId, userName, dataName,
					regDataName, x, y, adCode, deviceInfo,scoreId);
		} catch (AppException e) {
			throw e;
		}

		logger.info("任务保存成功 [" + userName
				+ "], dataName=[" + dataName + "],taskId=" + taskId);

		logger.info("任务保存完毕，把保存的任务基本信息封装返给客户端: " + saveMap);
		TaskLockResultEntity entity = null;
		try {
			entity = saveAfterEntity(adCode, regDataName, userName, taskId,
					saveMap);
			logger.info("我返回了=====================");
		} catch (Exception e) {
			logger.info("保存后的任务，返回给客户端时报错", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		return entity;
	}
	
	private void passiveUserTaskCheckInSave(String taskId, String userName) {
		logger.info("保存的是被动任务，首先从数据库中加载被动任务状态信息");
		String[] result = this.getTaskAllUserStatus(taskId);

		if (result == null) {
			/** 无效的被动任务 */
			logger.info("被动任务task_id=[" + taskId + "]，在数据库task_all中未查到相关记录");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}
		if (!result[1].equals(userName)) {
			logger.info("被动任务taskid=[" + taskId + "]没有被定向到此用户" + userName
					+ ",保存失败");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}
		switch (Integer.parseInt(result[0])) {
		case 1:
			logger.info("被动任务taskid=[" + taskId + "]审核通过的被动任务,不能再次采集,保存失败");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		case 2:
			logger.info("被动任务taskid=[" + taskId + "]已经被他人保存,保存失败");
			throw new AppException(AppExceptionEnum.TASK_HAD_LOCKED);
		case 3:
			logger.info("被动任务taskid=[" + taskId + "]已经被他人提交,保存失败");
			throw new AppException(AppExceptionEnum.TASK_HAD_COMMITED);
		case 4:
			logger.info("被动任务taskid=[" + taskId + "]被一个主动任务关联,保存失败");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		default:
			logger.info("被动任务taskid=[" + taskId + "]可以保存");
		}
	}
	
	private String[] getTaskAllUserStatus(String taskId) {
		List<?> list =(List<?>)agentTaskBaseDao.getAllotTaskById(taskId);
		if (list == null || list.size() == 0) {
			return null;
		}
		String[] result = new String[2];
		result[0] = ((TaskLockResultEntity)list.get(0)).getStatus().toString();
		result[1] = ((TaskLockResultEntity)list.get(0)).getUserName().toString();
		return result;
	}
	private Map<String, Object> saveUserTaskData2DB(String taskId,
			String userName, String dataName, String regDataName, String x,
			String y, String adCode, String deviceInfo,String scoreId) {
		
		Map<String, Object> paraMap=this.saveTaskData2DB(taskId, userName, dataName, regDataName, x, y, adCode, deviceInfo, scoreId);
        try{
        	this.updateBeidongTaskUserStatus(TaskStatusEnum.SAVING.getCode(),
					taskId,userName);

		} catch (Exception e) {
			logger.error("任务保存的时候,数据库操作异常", e);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
		return paraMap;
	}

	/**
	 * 在搜索引擎中锁定任务
	 * 
	 * @param taskId
	 * @param userName
	 * @param dataName
	 * @param x
	 * @param y
	 * @param adCode
	 * @return
	 */
	private TaskLockResultEntity saveLockTaskInSearchEngine(String taskId,
			String userName, String dataName, String x, String y,
			String adCode, String deviceInfo, String scoreId)
			throws AppException {
		logger.info("进入基于搜索引擎保存任务 taskId=[" + taskId + "]," + "userName=["
				+ userName + "],dataName=[" + dataName + "],adcode=[" + adCode
				+ "]");

		String subAdCode = adCode.substring(0, 4);

		/** 格式化用户传入的数据名称 */
		String regDataName = StringUtil.formatTaskName(dataName);
		logger.info("[门址保存][" + userName + "]门址=[" + dataName + "]格式化后门址=["
				+ regDataName + "]");
		/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
		boolean activeTask = taskId == null || taskId.isEmpty();
		if (activeTask) {
			logger.info("保存的是主动任务 userName=[" + userName + "],dataName=["
					+ dataName + "],adcode=[" + adCode + "]");
			/** 防止同一个用户频繁操作 */
			this.checkDuplicateOperation(userName + "," + dataName,
					"active-task",
					AppExceptionEnum.DUPLICATE_ACTIVE_TASK_SAVE_ERROR);
			try {
				/** 主动任务保存前进行排重 */
				this.activeTaskCheckDuplicateInSave(subAdCode, regDataName,
						null, userName, x, y);
			} catch (AppException e) {
				throw e;
			}
		}else{
			logger.info("保存的是被动任务 userName=[" + userName + "],dataName=["
					+ dataName + "],adcode=[" + adCode + "]");

//			/** 防止同一个用户频繁操作同一个接口 */
			this.checkDuplicateOperation(taskId + "," + userName,
					"passive-task",
					AppExceptionEnum.DUPLICATE_PASSIVE_TASK_SAVE_ERROR);
			/** 防止被动任务同ID并发数据库读脏问题*/
			this.checkPassiveLock(taskId, "passive-task", AppExceptionEnum.TASK_HAD_LOCKED, 5);
			/** 获得数据库行锁**/
			agentTaskBaseDao.getPassiveTaskLineLock(taskId);

			/** 检查该被动任务是否可以保存（是否被别人保存/提交） */
			try {
				this.passiveTaskCheckInSave(taskId);
			} catch (AppException e) {
				throw e;
			}
		}

		logger.info("保存的任务开始写入到数据库中 [" + userName
				+ "], dataName=[" + dataName + "],taskId=" + taskId);

		/** 保存的任务入库 */
		Map<String, Object> saveMap = new HashMap<String, Object>();
		try {
			saveMap = this.saveTaskData2DB(taskId, userName, dataName,
					regDataName, x, y, adCode, deviceInfo,scoreId);
		} catch (AppException e) {
			throw e;
		}

		logger.info("任务保存成功 [" + userName
				+ "], dataName=[" + dataName + "],taskId=" + taskId);

		logger.info("任务保存完毕，把保存的任务基本信息封装返给客户端: " + saveMap);
		TaskLockResultEntity entity = null;
		try {
			entity = saveAfterEntity(adCode, regDataName, userName, taskId,
					saveMap);
			logger.info("我返回了=====================");
		} catch (Exception e) {
			logger.info("保存后的任务，返回给客户端时报错", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		return entity;
	}

	// 使用redis来防止当前用户连续保存，连续提交的情况
	private void checkDuplicateOperation(String key, String value,
			AppExceptionEnum appExceptionEnum) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = jedisPool.getResource();
			result = jedis.sadd(key, value);// 如果有两个相同的key，返回值是-1
			jedis.expire(key, 1);// 1秒过期
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e);
			return;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		if (result != 1) {// 如果返回值是-1，驳回此保存或者提交请求
			throw new AppException(appExceptionEnum);
		}
	}
	// 使用redis来防止当前用户连续保存，连续提交的情况
		private void checkPassiveLock(String key, String value,
				AppExceptionEnum appExceptionEnum,int expireTime) {
			Jedis jedis = null;
			Long result = null;
			key="PASSIVE_LOCK_"+key;
			try {
				jedis = jedisPool.getResource();
				result = jedis.sadd(key, value);// 如果有两个相同的key，返回值是-1
				jedis.expire(key, expireTime);// 1秒过期
			} catch (Exception e) {// 如果redis抛出异常，直接返回

				/*
				 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
				 * java.lang.Long的问题
				 */
				if (jedis != null) {
					jedisPool.returnBrokenResource(jedis);
				}
				logger.error(e);
				return;
			} finally {
				if (jedis != null) {
					jedisPool.returnResource(jedis);
				}
			}
			if (result != 1) {// 如果返回值是-1，驳回此保存或者提交请求
				throw new AppException(appExceptionEnum);
			}
		}
	private int activeTaskCheckDuplicateInSave(String subAdCode,
			String regDataName, String baseId, String userName, String x,
			String y) throws AppException {

		/** 先走搜索引擎排重，如果搜索引擎报错则走基于数据库排重机制 */

		logger.info("开始进行保存前的数据排重，userName=[" + userName + "]，regDataName=["
				+ regDataName + "]，baseId=[" + baseId + "]，adcode=["
				+ subAdCode + "]");
		try {
			return activeTaskCheckDuplicateInSaveBySearch(subAdCode,
					regDataName, baseId, userName, x, y);
		} catch (SearchException se) {
			logger.info("基于搜索引擎对主动任务排重时异常", se);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR) ;
		}
	}
	private int activeTaskCheckDuplicateInSaveBySearch(String subAdCode,
			String regDataName, String baseId, String userName, String x,
			String y) {

		/** baseId不为空，表示是对已存在的数据进行操作(修改) */
		if (StringUtils.isNotBlank(baseId)) {
			Map<String, String> map = this.getAgentTaskBaseSaveInfo(baseId,userName);
			if(map==null){
				/** 无效的任务 */
				logger.info("任务baseid=[" + baseId
						+ "]，在数据库agent_task_base中未查到相关记录");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			String status = map.get("status");
			x = map.get("x");
			y = map.get("y");

			if (status == null) {
				/** 无效的任务 */
				logger.info("任务baseid=[" + baseId
						+ "]，在数据库agent_task_base中未查到相关记录");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			if ("0".equals(status)) {
				logger.error("[" + userName + "]门址=["
						+ regDataName + "] 待审核中不能修改");
				throw new AppException(AppExceptionEnum.TASK_CANNOT_MODIFY);
			}
		}

		/** 在高德已有门址库和 在已经采集的数据中进行排重 */
		logger.info("开始在高德已有门址库和已经采集门址库中进行排重");
		try {
			this.existsAutoNaviDataInSearchEngineInSave(subAdCode, regDataName,
					x, y);
		} catch (SearchException e) {
			logger.info("主动任务排重，基于搜索引擎排重的时候异常",e);
			throw e;
		}
		return -1;
	}
	private Map<String, String> getAgentTaskBaseSaveInfo(String baseId,String userName) {
		

		List<?> list = (List<?>)agentTaskBaseDao.getAgentInfoByBaseId(baseId, userName);
		if (list == null || list.size() == 0) {
			return null;
		}
		TaskLockResultEntity entity=(TaskLockResultEntity)list.get(0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("status",entity.getStatus());
		map.put("x", entity.getX());
		map.put("y", entity.getY());
		return map;
	}
	private void existsAutoNaviDataInSearchEngineInSave(String subAdCode,
			String dataName, String x, String y) throws AppException {

		Map<String, Object> paraMap = new HashMap<String, Object>();

		paraMap.put("keyword", dataName);
		paraMap.put("adcode", subAdCode);

		String offsets = this.offsetCorrdinate(x, y);
		String[] offsetXY = offsets.split(",");
		String offsetX = offsetXY[0];
		String offsetY = offsetXY[1];
		paraMap.put("x", offsetX);
		paraMap.put("y", offsetY);

		Map<String, Object> rsMap = new HashMap<String, Object>();

		/** 在高德母库中对要保存的数据进行排重 */
		try {
			if (searchEngineService == null) {
				logger.error("保存/修改任务 排重时，搜索引擎实例，实例化失败.... ");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}
			rsMap = searchEngineService
					.matchGDBaseDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("保存/修改任务,基于搜索引擎 在高德母库中进行排重 的时候异常: " + e);
			throw e;
		}
		logger.info("保存任务时dataname=[" + dataName + "]在高德母库中排重结果：" + rsMap);
		if (rsMap != null && rsMap.size() > 0) {
			logger.error("要保存的门址=[" + dataName
					+ "]在母库中已经存在");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}

		if (rsMap != null) {
			rsMap.clear();
		}

		/** 在审核回传表中排重 */
		try {
			if (searchEngineService == null) {
				logger.error("保存/修改任务 排重时，搜索引擎实例，实例化失败.... ");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}
			rsMap = searchEngineService
					.matchAuditbackDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("保存/修改任务,基于搜索引擎 在审核回传中进行排重 的时候异常: " + e);
			throw e;
		}
		logger.info("保存任务时dataname=[" + dataName + "]在审核回传中排重结果：" + rsMap);
		if (rsMap != null && rsMap.size() > 0) {
			logger.error("要保存的门址=[" + dataName
					+ "]在审核回传中已经存在");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}

		/** 在高德采集库中对要保存的任务进行排重 */
		if (rsMap != null) {
			rsMap.clear();
		}
		HashMap<String, Object[]> tmpMap = new HashMap<String, Object[]>();
		tmpMap.put("status", new Object[] { "0", "1", "3" });
		paraMap.put("customMap", tmpMap);

		try {
			if (searchEngineService == null) {
				logger.error("保存/修改任务 排重时，搜索引擎实例，实例化失败.... ");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}

			rsMap = searchEngineService
					.matchAgentBaseDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("基于搜索引擎 在采集库中进行排重 的时候异常: " + e);
			throw e;
		}
		logger.info("保存任务时dataname=[" + dataName + "]在高德采集库中排重结果：" + rsMap);
		if (rsMap != null && rsMap.size() > 0) {
			logger.error("要保存的门址=[" + dataName
					+ "]在采集库中已经存在");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}
	}
	private String offsetCorrdinate(String x, String y) {
		String offsetCorrdinate = CoordinateConvert.GPS2Deflexion(x + "," + y);
		String[] offsetList = offsetCorrdinate.split(",");
		if (offsetList.length == 2) {
			x = offsetList[0];
			y = offsetList[1];
		}
		return x + "," + y;
	}
	private void passiveTaskCheckInSave(String taskId) {
		logger.info("保存的是被动任务，首先从数据库中加载被动任务状态信息");
		String status = this.getTaskAllStatus(taskId);

		if (status == null) {
			/** 无效的被动任务 */
			logger.info("被动任务task_id=[" + taskId + "]，在数据库task_all中未查到相关记录");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}

		switch (Integer.parseInt(status)) {
		case 1:
			logger.info("被动任务taskid=[" + taskId + "]审核通过的被动任务,不能再次采集,保存失败");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		case 2:
			logger.info("被动任务taskid=[" + taskId + "]已经被他人保存,保存失败");
			throw new AppException(AppExceptionEnum.TASK_HAD_LOCKED);
		case 3:
			logger.info("被动任务taskid=[" + taskId + "]已经被他人提交,保存失败");
			throw new AppException(AppExceptionEnum.TASK_HAD_COMMITED);
		case 4:
			logger.info("被动任务taskid=[" + taskId + "]被一个主动任务关联,保存失败");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		default:
			logger.info("被动任务taskid=[" + taskId + "]可以保存");
		}
	}
	private String getTaskAllStatus(String taskId) {


		List<?> list =(List<?>)agentTaskBaseDao.getTaskPassiveById(taskId);
		if (list == null || list.size() == 0) {
			return null;
		}
		return ((TaskLockResultEntity)list.get(0)).getStatus().toString();
	}
	private Map<String, Object> saveTaskData2DB(String taskId, String userName,
			String dataName, String regDataName, String x, String y,
			String adCode, String deviceInfo,String scoreId) {
		long taskBaseSeq = 0;
		long taskBaseMzSeq = 0;
		String offsets = this.offsetCorrdinate(x, y);
		String[] offsetXY = offsets.split(",");
		String offsetX = offsetXY[0];
		String offsetY = offsetXY[1];

		Map<String, Object> paraMap = new HashMap<String, Object>();
		try {
			String currentDateTime = DateUtil.getCurrentDateTime();
			String endTimeStr = DateUtil.rollDateByStr(currentDateTime,
					DateUtil.SIMPLE_DATE_FORT, 5);
			AgentTaskBase base=new AgentTaskBase();
			if(taskId!=null&&!taskId.isEmpty()){
				base.setTaskId(taskId);	
			}
			base.setUserName(userName);
			base.setDataName(dataName);
			base.setSubmitTime(currentDateTime);
			base.setEndTime(endTimeStr);
			base.setAdCode(adCode);
			base.setRegDataname(regDataName);
			base.setDeviceInfo(deviceInfo);
			if(scoreId!=null){
				base.setScoreId(new Long(scoreId));	
			}
			base.setStatus(SysProps.BASE_STATUS_LOCK);
			agentTaskBaseDao.addAgentTaskBase(base);
			taskBaseSeq = DaoHelper.getPrimaryKey();
		    AgentTaskMz mz=new AgentTaskMz();
		    mz.setBaseId(taskBaseSeq);
		    mz.setX(x);
		    mz.setY(y);
		    mz.setOffsetX(offsetX);
		    mz.setOffsetY(offsetY);
		    mz.setUserName(userName);
		    agentTaskBaseDao.addAgentTaskMz(mz);
			taskBaseMzSeq =DaoHelper.getPrimaryKey();
			logger.info("保存的新任务 baseid=[" + taskBaseSeq + "]; mz id=["
					+ taskBaseMzSeq + "]");

			/** 如果是被动任务，需要更新task_all的status */
			if (!"".equals(taskId)) {
				this.updateBeidongTaskStatus(TaskStatusEnum.SAVING.getCode(),
						taskId,userName);

				/** 更新task_all 时间戳字段，用于标记该记录有变动 */
				// String updateSql =
				// "update task_all set timestamps=? where id=?";
				// this.baseDao.update(updateSql, new Object[] {new
				// Date().getTime(),taskId});
			}

			/** 设置agent_task_base数据 */
			paraMap.put("id", taskBaseSeq);
			paraMap.put("submit_time", currentDateTime);
			paraMap.put("end_time", endTimeStr);

		} catch (Exception e) {
			logger.error("任务保存的时候,数据库操作异常", e);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
		return paraMap;
	}
	/**
	 * 方法描述：更新被动任务状态
	 * 
	 * @param status
	 * @param taskId
	 */
	private void updateBeidongTaskStatus(String status, String taskId,String userName) {

		int rs = (Integer)agentTaskBaseDao.updateTaskPassive(new Integer(status), new Long(taskId), new Date().getTime(),userName);
		if(rs==0){
			logger.error("被动任务不存在,taskId = "
					+ taskId);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
		logger.info("保存的被动任务,task_all更新结果: " + rs);

	}
	private void updateBeidongTaskUserStatus(String status, String taskId,String userName) {

		int rs = (Integer)agentTaskBaseDao.updateAllotTask(new Integer(status), new Long(taskId), new Date().getTime());
		if(rs==0){
			logger.error("被动任务不存在,taskId = "
					+ taskId);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
		logger.info("保存的被动任务,task_all更新结果: " + rs);

	}
	private TaskLockResultEntity saveAfterEntity(String adcode,
			String dataName, String userName, String taskId,
			Map<String, Object> saveMap) {

		TaskLockResultEntity entity = null;
		try {
			logger.info("返回给客户端，需要封装的数据adcode=[" + adcode + "],dataname=["
					+ dataName + "],taskId=[" + taskId + "]=============");
			String baseId = saveMap.get("id").toString();
			String endTime = saveMap.get("end_time").toString();
			String submitTime = saveMap.get("submit_time").toString();

			entity = new TaskLockResultEntity();
			entity.setAdCode(adcode);
			entity.setDataName(dataName);
			entity.setEndTime(endTime);
			entity.setSubmitTime(submitTime);
			entity.setUserName(userName);
			entity.setTaskId(taskId);
			entity.setBaseId(Long.parseLong(baseId));
		} catch (Exception e) {
			logger.info("保存成功的任务，封装数据返回给客户端时异常", e);
		}
		return entity;
	}
	public void updateUserInfomation(String userName,String baseId){
		/** 加载用户的相关信息 */
		logger.info("更新agent_task_base表的agent_type字段信息，需要根据用户名 username=["
				+ userName + "]从view_user_score_new中获取");
		List<?> l=(List<?>)agentTaskBaseDao.getViewUserScore(userName);
		if(l!=null&&l.size()>0){
			ViewUserScore user=(ViewUserScore)l.get(0);
			logger.info("加载用户 username=[" + userName
					+ "],的agenttype结果：" + user);
			user.setBaseId(baseId);
			/** 更新agent_task_base表的 ename、agenttype */
			agentTaskBaseDao.updateAgentTaskBase(user);
		}
//		String sql = SQLFactory.getSql("query_user_score_new");
//		List<ListOrderedMap> queryReslut = this.baseDao.find(sql,
//				new Object[] { userName });
//
//		logger.info("加载用户 username=[" + userName
//				+ "],的agenttype结果：" + queryReslut);
//
//		if (queryReslut != null && queryReslut.size() > 0) {
//			ListOrderedMap map = queryReslut.get(0);
//
//			
//			String updateSql = "update agent_task_base set ename=?,agenttype=?,timestamps=? where id=?";
//			this.baseDao.update(updateSql,
//					new Object[] { map.get("ename"), map.get("agenttype"),
//							new Date().getTime(), baseId });
//		}
	}
	public void updateAgentTaskTokenForSave(String tokenId,String userName) {
		try {
			logger.info("[更新TOKEN信息] =ID[" + tokenId
					+ "]");
			agentTaskTokenDao.updateAgentTaskTokenFieldOne(tokenId, userName);
		} catch (Exception e) {
			logger.error(
					"[" + tokenId + "]更新Token异常", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}
	}

	@Override
	public void modifyTask(String baseId, String adCode, String dataName,
			String userName, String scoreId,String tokenId) {
		logger.info("[修改任务名称][" + userName + "]区域=["
				+ adCode + "]门址=[" + dataName + "]");
		String subAdCode = adCode.substring(0, 4);
		// 门址格式化
		String regDataName = StringUtil.formatTaskName(dataName);
		int reFlag = this.activeTaskCheckDuplicateInSave(subAdCode,
				regDataName, baseId, userName, "", "");
		logger.info("[修改任务名称][" + userName + "]门址=["
				+ dataName + "]格式化后门址=[" + regDataName + "]判重状态码：" + reFlag);
		if (reFlag > 0) {
			if (reFlag == 1) {
				logger.error("[" + userName
						+ "]门址baseId=[" + baseId + "]待审核中不能修改");
				throw new AppException(AppExceptionEnum.TASK_CANNOT_MODIFY);
			}
			if (reFlag == 2) {
				logger.error("[" + userName
						+ "]门址baseId=[" + baseId + "]用户保存过");
				throw new AppException(AppExceptionEnum.TASK_CANNOT_SAVE);
			}
			if (reFlag == 3) {
				logger.error("[" + userName + "]门址=["
						+ dataName + "]已被用户提交或保存");
				throw new AppException(AppExceptionEnum.TASK_HAD_LOCKED);
			}
			if (reFlag == 5) {
				logger.error("[" + userName + "]门址=["
						+ dataName + "]存在于基础门址中");
				throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
			}
		}

		logger.info("保存前数据排重完毕，开始把保存或者更新到数据库 ");
		/** 更新agent_task_base的时间戳 合并在一个sql 中*/
		AgentTaskBase base=new AgentTaskBase();
		base.setId(Long.valueOf(baseId));
		base.setDataName(dataName);
		base.setRegDataname(regDataName);
		base.setUserName(userName);
		if(scoreId!=null&&!scoreId.equals("")){
			base.setScoreId(Long.valueOf(scoreId));
		}
		int updateRs=(Integer)agentTaskBaseDao.modifyAgentTaskName(base);

		logger.info("在数据库中更新任务完毕,更新结果：[" + updateRs + "],basId=[" + baseId
				+ "]，dataName=[" + dataName + "]，regDataName=[" + regDataName
				+ "]");
		if(tokenId!=null&&!tokenId.equals("")){
			updateAgentTaskTokenForSave(tokenId,userName);
		}
		
	}

	@Override
	public boolean checkCountIsOut(String userName, String pack) {
		// TODO Auto-generated method stub
		Jedis jedis = null;
		try {
			int max=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_MAX_SUBMIT_TASK_BATCH).toString());
			jedis = jedisPool.getResource();
			String str=jedis.get("BATCH_"+userName+"_"+pack);
			if(str==null){
				return false;
			}
			int count=new Integer(str);
			if(count<=max){
				return true;
			}
			jedis.del("BATCH_"+userName+"_"+pack);
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	@Override
	public boolean checkUserIsTimeOut(String userName) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str=jedis.get("BLACK_"+userName);
			if(str!=null){
				return false;
			}
			str=jedis.get("SUBMIT_TASK_"+userName);
			if(str!=null){
				jedis.set("BLACK_"+userName, "");
				return false;
			}
			
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return true;
	}

	@Override
	public void relaseCheatTask(String baseId, String taskId,
			Boolean isUpdateSearchIndex,String userName,String recommend) {
		logger.info("释放作弊的任务 baseid=[" + baseId + "], taskId=[" + taskId + "]");
		/** 把作弊任务的状态置为2(标记为审核不通过) */
		try {
			AgentTaskBase base=new AgentTaskBase();
			base.setStatus(SysProps.BASE_STATUS_NOPASS);
			base.setId(new Long(baseId));
			base.setUserName(userName);
			agentTaskBaseDao.updateAgentTaskBaseStatus(base);
		} catch (Exception e) {
			logger.error("在数据库中释放作弊的任务失败 baseId=[" + baseId + "]", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}
		if (StringUtils.isNotBlank(taskId)) {
			/** 更新被动任务的状态为0，释放该被动任务 */
			boolean isRecommend=false;
			if(recommend!=null&&recommend.equals("1")){
				isRecommend=true;
			}
			try {
				this.releasePassiveTask(new Long(taskId), isRecommend);
			} catch (Exception e) {
				logger.error("在数据库中释放作弊的被动任务失败 task_id=[" + taskId + "]", e);
				throw new AppException(AppExceptionEnum.DB_ERROR);
			}
		}
		
	}
	private void releasePassiveTask(Long id,boolean isRecommend){
		if(isRecommend){
			agentTaskBaseDao.updateTaskPassive(new Integer(TaskStatusEnum.RECOMMEND.getCode()), id, new Long(0),null);
			agentTaskBaseDao.updateAllotTask(new Integer(TaskStatusEnum.INIT.getCode()), id, new Long(0));
		}else{
			agentTaskBaseDao.updateTaskPassive(new Integer(TaskStatusEnum.INIT.getCode()), id, new Long(0),null);
		}
		
	}

	@Override
	public void updateAgentTaskToken(AgentTaskToken token) {
		try {
			agentTaskTokenDao.updateAgentTaskToken(token);
			logger.info("[更新TOKEN信息] =ID["
					+ token.getId() + "]");
			agentTaskTokenDao.updateAgentTaskToken(token);
		} catch (Exception e) {
			logger.error("[" + token.getId()
					+ "]更新Token异常", e);
			throw new AppException(AppExceptionEnum.DB_ERROR);
		}
		
		
	}

	@Override
	public String saveSubmitUploadTask(AgentTaskUploadEntity agentTaskUpload,AgentTaskToken token)
			throws AppException {
		logger.info("开始提交任务 deviceInfo=[" + agentTaskUpload.getDeviceInfo()
				+ "],userName=[" + agentTaskUpload.getUserName()
				+ "], dataName=[" + agentTaskUpload.getDataName()
				+ "],taskId=[" + agentTaskUpload.getTaskId() + "]");
		try {
			String baseId=this.saveSubmitUploadTaskInSearchEngine(agentTaskUpload);
			this.updateUserInfomation(agentTaskUpload.getUserName(), baseId);
			if (token.getStatus() != 3) {
				token.setStatus(1);
			}
			token.setBaseId(new Long(baseId));
			logger.info("开始更新TOKEN相关 ["+token.getTokenId()+"]");
			updateAgentTaskToken(token);
			
			logger.info("开始记录验证码弹出标记");
			if(agentTaskUpload.getIsBatch()!=null&&agentTaskUpload.getIsBatch().equals("0")){
				setCountFlag(agentTaskUpload.getUserName(), agentTaskUpload.getPack());
			}else{
				setSubMitTaskFlag(agentTaskUpload.getUserName());
			}
			return baseId;
		} catch (AppException ae) {
			throw ae;
		}
	}
	/**
	 * 任务提交(基于搜索引擎)
	 * 
	 * @param uploadEntity
	 */
	private String saveSubmitUploadTaskInSearchEngine(
			AgentTaskUploadEntity agentTaskUpload) {

		String taskId = agentTaskUpload.getTaskId();
		String loginName = agentTaskUpload.getUserName();
		String dataName = agentTaskUpload.getDataName();
		String adCode = agentTaskUpload.getAdCode();
		String baseId = agentTaskUpload.getBaseId();
		String regDataName = StringUtil.formatTaskName(dataName);
		String subAdCode = adCode.substring(0, 4);

		// logger.info("基于搜索引擎，对提交前的数据进行排重 taskid=[" + taskId + "],userName=["+
		// loginName + "],dataName=[" + dataName + "],adcode=[" + adCode+
		// "],baseId=[" + baseId + "],regDataName=[" + regDataName + "]");

		// 不可以提交纯数字组成的门址
		if (regDataName.matches("[0-9]+")) {
			logger.error("["
					+ agentTaskUpload.getUserName() + "]不能提交纯数字组成的门址["
					+ agentTaskUpload.getDataName() + "]");
			throw new AppException(AppExceptionEnum.ONLY_NUMERIC_ERROR);
		}

		logger.info("[门址提交]["
				+ agentTaskUpload.getUserName() + "]区域=" + adCode + ",门址=["
				+ dataName + "],格式化后门址=[" + regDataName + "]taskId=" + taskId
				+ " baseId=" + baseId);

		/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
		boolean activeTask = (taskId == null || taskId.isEmpty());

		// 如果是被动任务
		if (!activeTask) {
			if(agentTaskUpload.getRecommend()!=null&&agentTaskUpload.getRecommend().equals("1")){
				agentTaskUpload.setComments("定向任务");
			}
			logger.info("提交的是被动任务，判断该任务是被其他人保存或者提交 taskid=[" + taskId + "]");
			/** 防止同一个用户频繁操作同一个接口 */
			this.checkDuplicateOperation(agentTaskUpload.getTaskId() + ","
					+ agentTaskUpload.getUserName(), "passive-task",
					AppExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR);
			/** 防止被动任务同ID并发数据库读脏问题*/
			this.checkPassiveLock(taskId, "passive-task", AppExceptionEnum.TASK_HAD_LOCKED, 5);
			/** 获得数据库行锁**/
			agentTaskBaseDao.getPassiveTaskLineLock(taskId);
			/** 在此主要判断，该被动任务是否已经被其他人采集 */
			try {
				String tempBaseId = this.passiveTaskCheckInSubmit(taskId,
						loginName,agentTaskUpload);
				if (StringUtils.isNotBlank(tempBaseId)) {
					baseId = tempBaseId;
					agentTaskUpload.setBaseId(tempBaseId);
				}
			} catch (AppException e) {
				throw e;
			}
			

			boolean taskIsSave = agentTaskUpload.getBaseId() != null
					&& !"".equals(agentTaskUpload.getBaseId());

			/** 被自己保存后再提交的任务 */
			if (taskIsSave) {
				logger.info("用户" + loginName
						+ "保存了任务 taskid=" + taskId + ", baseid=[" + baseId
						+ "]开始更新提交数据!");

				try {
					this.updateAgentTaskInDB(agentTaskUpload, baseId,DateUtil.getCurrentDateTime(), true);
				} catch (AppException e) {
					throw e;
				}
				this.updateTaskAdressTypeInDB(taskId,
						agentTaskUpload.getAddressType(), true,agentTaskUpload.getUserName(),agentTaskUpload.getRecommend());
			} else {

				/** 采集后直接提交的任务 */
				logger.info("["
						+ agentTaskUpload.getUserName() + "]任务" + taskId
						+ "未被任务人保存过! 开始插入用户提交数据");
				String id = this.addAgentTaskInDB(agentTaskUpload, regDataName,
						null, null);

				if (baseId == null || baseId.equals("")) {
					logger.info("["
							+ agentTaskUpload.getUserName()
							+ "]baseId是空的，从addAgentTask方法获得Id");
					baseId = id;
				}
				this.updateTaskAdressTypeInDB(taskId,
						agentTaskUpload.getAddressType(), true,agentTaskUpload.getUserName(),agentTaskUpload.getRecommend());
			}
		} else {
			if(agentTaskUpload.getRecommend()!=null&&agentTaskUpload.getRecommend().equals("1")){
				logger.info("定向任务必须是被动任务");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			logger.info("用户提交的是主动任务 username=["
					+ agentTaskUpload.getUserName() + "],dataname=["
					+ agentTaskUpload.getDataName() + "]");
			agentTaskUpload.setScoreId(null);

			/** 首先基于搜索引擎，对提交的主动任务进行排重 */
			int reFlag = -1;
			try {
				reFlag = this.activeTaskCheckDuplicateInSubmit(subAdCode,
						regDataName, baseId, loginName, agentTaskUpload);
			} catch (AppException e) {
				throw e;
			}

			/** 主被动关联（如果关联的被动任务状态非0，则该主动任务不能提交） */
			String relatedLostId = null;
			String relatedTaskId = null;
			// Map<String, String> rsMap =
			// this.checkRelationTaskNew(regDataName, subAdCode);
			// if(rsMap!=null&&rsMap.size()>0){
			// String status = rsMap.get("status");
			// if(!"0".equals(status)){
			// /**被动任务的状态不为0，表示该被动任务被保存或者已经被提交了，相应的主动任务则不能提交*/
			//
			// throw new
			// AppException(AppExceptionEnum.TASK_HAD_LOCKED_OR_COMMIT);
			// }
			//
			// /**把关联上的被动任务lostid（也就是task_all的task_id字段赋给
			// agent_task_base的related_lost_id）*/
			// relatedLostId = rsMap.get("lostId");
			// relatedTaskId = rsMap.get("caijiId");
			// }

			logger.info("提交的主动任务,username=["
					+ agentTaskUpload.getUserName() + "],dataname=["
					+ agentTaskUpload.getDataName() + "] 排重结果 =" + reFlag);
			Task task = new Task();
			task.setTasktype(0);
			task.setDataname(agentTaskUpload.getDataName());
			task.setAddressType(agentTaskUpload.getAddressType());
			task.setX(agentTaskUpload.getX());
			task.setY(agentTaskUpload.getY());

			boolean taskIsSave = agentTaskUpload.getBaseId() != null
					&& !"".equals(agentTaskUpload.getBaseId());
			if (taskIsSave) {

				logger.info("该主动任务是保存以后再提交的,baseId=["
						+ agentTaskUpload.getBaseId() + "] userName=["
						+ agentTaskUpload.getUserName() + "]");
				
				this.saveTaskByAuto(task, agentTaskUpload, regDataName, true,
						relatedLostId, relatedTaskId);
			} else {
				logger.info("该主动任务是未保存，采集后直接提交的，,baseId=["
						+ agentTaskUpload.getBaseId() + "] userName=["
						+ agentTaskUpload.getUserName() + "]");
				String id = this.saveTaskByAuto(task, agentTaskUpload,
						regDataName, false, relatedLostId, relatedTaskId);
				if (baseId == null || baseId.equals("")) {
					baseId = id;
				}
			}
		}
		return baseId;
	}
	private String passiveTaskCheckInSubmit(String taskId, String srcUserName,AgentTaskUploadEntity agentTaskUpload) {
		/** 判断要提交的任务是否已经被其他人保存或者提交了 */
		//先判断被动任务状态 释放任务--被保存--被提交
		List<?> l=(List<?>)agentTaskBaseDao.getTaskPassiveById(taskId);
		if(l==null||l.size()==0){
			logger.error("[" + srcUserName
					+ "]任务" + taskId + "已经不存在或者无效!");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}
		TaskLockResultEntity obj=(TaskLockResultEntity)l.get(0);
		agentTaskUpload.setAddressType(obj.getAddressType());
		if(obj.getStatus().equals(TaskStatusEnum.SAVING.getCode())){
			//保存的状况 需要判定是不是自己保存的
			if(!obj.getUserName().equals(srcUserName)){
				//不是自己保存的
				logger.error("[" + srcUserName
						+ "]任务" + taskId + "已经被用户" + obj.getUserName() + "锁定!");
				throw new AppException(AppExceptionEnum.TASK_HAD_COMMITED);
			}else{
				l=(List<?>)agentTaskBaseDao.getUserSavePassiveTaskByTaskId(taskId, srcUserName);
				if(l==null||l.size()==0){
					logger.error("[" + srcUserName
							+ "]任务" + taskId + "已经不存在或者无效!");
					throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
				}
				AgentTaskBase base=(AgentTaskBase)l.get(0);
				return base.getId().toString();
			}
		}else if(obj.getStatus().equals(TaskStatusEnum.COMMITED.getCode())
				||obj.getStatus().equals(TaskStatusEnum.FINISHED.getCode())){
			//被提交或者已经通过 直接返回错误
			logger.error("[" + srcUserName
					+ "]任务" + taskId + "已被用户提交!");
			throw new AppException(AppExceptionEnum.TASK_HAD_COMMITED);
		}else if(obj.getStatus().equals(TaskStatusEnum.DEL.getCode())){
			//逻辑删除 直接提示错误
			logger.error("[" + srcUserName
					+ "]任务" + taskId + "已经不存在或者无效!");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}else if(obj.getStatus().equals(TaskStatusEnum.RECOMMEND.getCode())){
			//定向任务 判断用户是否具有权限
			l=(List<?>)agentTaskBaseDao.getAllotTaskById(taskId);
			if(l==null||l.size()==0){
				logger.error("[" + srcUserName
						+ "]定向任务" + taskId + "已经不存在或者无效!");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			TaskLockResultEntity obj1=(TaskLockResultEntity)l.get(0);
			if(!obj1.getUserName().equals(srcUserName)){
				logger.error("[" + srcUserName
						+ "]定向任务" + taskId + "无权限!");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			return null;
			
		}else if(obj.getStatus().equals(TaskStatusEnum.INIT.getCode())){
			//初始状态
			return null;
		}else{
			//其他状态
			logger.error("[" + srcUserName
					+ "]任务" + taskId + "已经不存在或者无效!");
			throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
		}
	}
	private void updateAgentTaskInDB(AgentTaskUploadEntity agentTaskUpload,
			String baseId, String submitTime, Boolean isUpdateSearchIndex) {
		agentTaskUpload.setBaseId(baseId);
		try {
			int brs=(Integer)agentTaskBaseDao.updateAgentTaskBase(agentTaskUpload);
			logger.info("提交保存的agent_task_base数据baseid=[" + baseId + "]，提交结果: "+ brs);
			
			/**防止重复提交*/
			if (brs == 0) {
				throw new AppException(AppExceptionEnum.TASK_HAVE_SUBMIT);
			}
			int mzrs =(Integer)agentTaskBaseDao.updateAgentTaskMz(agentTaskUpload); 
			logger.info("提交保存的agent_task_mz数据baseid=[" + baseId + "]，提交结果: "
					+ mzrs);

		} catch (Exception e) {
			logger.error("提交任务的时候系统异常,taskId = "
					+ agentTaskUpload.getTaskId(), e);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}

//		/** 加载用户的相关信息 */
//		logger.info("更新agent_task_base表的agent_type字段信息，需要根据用户名 username=["
//				+ agentTaskUpload.getUserName() + "]从view_user_score_new中获取");
//		String sql = SQLFactory.getSql("query_user_score_new");
//		List<ListOrderedMap> queryReslut = this.baseDao.find(sql,
//				new Object[] { agentTaskUpload.getUserName() });
//
//		logger.info("加载用户 username=[" + agentTaskUpload.getUserName()
//				+ "],的agenttype结果：" + queryReslut);
//
//		if (queryReslut != null && queryReslut.size() > 0) {
//			ListOrderedMap map = queryReslut.get(0);
//
//			/** 更新agent_task_base表的 ename、agenttype、timestamps */
//			String updateSql = "update agent_task_base set ename=?,agenttype=?,timestamps=? where id=?";
//			this.baseDao.update(updateSql,
//					new Object[] { map.get("ename"), map.get("agenttype"),
//							new Date().getTime(), baseId });
//		}
		//
		// /**更新搜索引擎的索引数据*/
		// if(isUpdateSearchIndex){
		// updateAgentTaskInSearchEngine(agentTaskUpload, agentId, submitTime);
		// }
	}
	private void updateTaskAdressTypeInDB(String cjId, String addressType,
			Boolean isUpdateSearchIndex,String userName,String recommend) {
		try {
			int i=(Integer)agentTaskBaseDao.updateTaskPassive(new Integer(TaskStatusEnum.COMMITED.getCode()), new Long(cjId), userName);
			if(i==0){
				logger.error("被动任务不存在,taskId = "
						+ cjId);
				throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
			}
			//定向任务状态更新
			if(recommend!=null&&recommend.equals("1")){
				agentTaskBaseDao.updateAllotTask(new Integer(TaskStatusEnum.COMMITED.getCode()), new Long(cjId), new Long(0));
			}
		} catch (Exception e) {
			logger.error("系统异常,请重新提交,taskId = "
					+ cjId, e);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}

		/*	*//** 更新task_all的address_type成功，开始更新搜索引擎中的相关数据 */
		/*
		 * if(isUpdateSearchIndex){
		 * this.updateTaskAdressTypeInSearchEngine(cjId, addressType); }
		 */

	}
	private String addAgentTaskInDB(AgentTaskUploadEntity agentTaskUpload,
			String regDataName, String relatedLostId, String relatedTaskId) {

		logger.info("直接提交的任务，提交前 先把门址数据保存到数据库中");

		Map<String, Object> saveMap = new HashMap<String, Object>();
		try {
			saveMap = saveSubmitInDB(agentTaskUpload, regDataName,
					relatedLostId, relatedTaskId);
		} catch (AppException e) {
			throw e;
		}

		// this.saveSubmitInEngine(saveMap);

		return saveMap.get("id").toString();
	}
	private Map<String, Object> saveSubmitInDB(
			AgentTaskUploadEntity agentTaskUpload, String regDataName,
			String relatedLostId, String relatedTaskId) {

		long taskBaseSeq = 0;
		long taskBaseMzSeq = 0;
		String offsets = this.offsetCorrdinate(agentTaskUpload.getX(),
				agentTaskUpload.getY());
		String[] offsetXY = offsets.split(",");
		String offsetX = offsetXY[0];
		String offsetY = offsetXY[1];
		agentTaskUpload.setRegDataName(regDataName);
		agentTaskUpload.setRelatedLostId(relatedLostId);

		Map<String, Object> paraMap = new HashMap<String, Object>();
		try {

			String currentDateTime = DateUtil.getCurrentDateTime();

			agentTaskBaseDao.addAgentTaskBase(agentTaskUpload);
			taskBaseSeq=DaoHelper.getPrimaryKey();

			agentTaskBaseDao.addAgentTaskMz(agentTaskUpload);
			taskBaseMzSeq=DaoHelper.getPrimaryKey();

			/** 设置主被动关联 */
			if (relatedLostId != null) {

				/** 更新task_all中的关联的被动任务状态为已经提交 */
				agentTaskBaseDao.updateTaskPassive(new Integer(TaskStatusEnum.COMMITED.getCode()), new Long(relatedTaskId), new Long(0), agentTaskUpload.getUserName());
			}

			/** 设置agent_task_base数据 */
			paraMap.put("id", taskBaseSeq);
			paraMap.put("task_id", agentTaskUpload.getTaskId());
			paraMap.put("user_name", agentTaskUpload.getUserName());
			paraMap.put("task_type", agentTaskUpload.getTaskType());
			paraMap.put("data_name", agentTaskUpload.getDataName());
			paraMap.put("enterprise_code", agentTaskUpload.getEnterPriseCode());
			paraMap.put("photo_time", DateUtil.parseString(
					agentTaskUpload.getPhotoTime(), DateUtil.SIMPLE_DATE_FORT));
			paraMap.put("getgps_time", DateUtil.parseString(
					agentTaskUpload.getGpsTime(), DateUtil.SIMPLE_DATE_FORT));
			paraMap.put("adcode", agentTaskUpload.getAdCode());
			paraMap.put("reg_dataname", regDataName);
			paraMap.put("status", "0");
			paraMap.put("submit_time", currentDateTime);

			/** 设置agent_task_mz数据 */
			paraMap.put("detail_id", taskBaseMzSeq);
			paraMap.put("base_id", taskBaseSeq);
			paraMap.put("x", agentTaskUpload.getX());
			paraMap.put("y", agentTaskUpload.getY());
			paraMap.put("offset_x", offsetX);
			paraMap.put("offset_y", offsetY);
			paraMap.put("point_type", agentTaskUpload.getPointType());
			paraMap.put("point_level", agentTaskUpload.getPointLevel());
			paraMap.put("image_id", agentTaskUpload.getImageId());
			paraMap.put("user_name", agentTaskUpload.getUserName());
			paraMap.put("point_accury", agentTaskUpload.getPointAccury());
			paraMap.put("position", agentTaskUpload.getPosition());

		} catch (Exception e) {
			logger.error("系统异常,请重新提交", e);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
		return paraMap;
	}
	private int activeTaskCheckDuplicateInSubmit(String subAdCode,
			String regDataName, String baseId, String userName,
			AgentTaskUploadEntity agentTaskUpload) {

		logger.info("开始基于搜索引擎，对要提交的任务进行排重。baseId=[" + baseId + "],userName=["
				+ userName + "],regName=[" + regDataName + "]");

		try {
			return this.activeTaskCheckDuplicateInSubmitBySearch(subAdCode,
					regDataName, baseId, userName, agentTaskUpload);
		} catch (SearchException e) {
			logger.error("提交任务时，基于搜索引擎排重异常", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		} catch (AppException ae) {
			throw ae;
		}
	}

	/**
	 * 方法描述：checkCanSubmit 完成时间： 2013-8-30 下午5:28:20 维护原因: 当前版本： v1.0
	 * 
	 * @param baseId
	 * @param adCode
	 * @param dataName
	 */
	private int activeTaskCheckDuplicateInSubmitBySearch(String subAdCode,
			String regDataName, String baseId, String userName,
			AgentTaskUploadEntity agentTaskUpload) {

		/** 从数据库中把要提交的任务查询出来，判断该任务是否已经提交 */
		if (StringUtils.isNotBlank(baseId)) {
			/** 说明是保存以后再提交的主动任务，如果baseid为空，表示直接提交的主动任务 */
            Map<String,String> m=this.getAgentTaskBaseSaveInfo(baseId, userName);
			if (m == null) {
				/** 无效的任务 */
				logger.info("任务baseid=[" + baseId
						+ "]，在数据库agent_task_base中未查到相关记录");
				throw new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			}
			if ("0".equals(m.get("status")) || "1".equals(m.get("status"))) {
				logger.error("[" + agentTaskUpload.getUserName() + "]门址baseId["
						+ baseId + "] 已经提交，不能重复提交");
				throw new AppException(AppExceptionEnum.TASK_HAVE_SUBMIT);
			}
		}

		/** 判断要提交的数据，是否已经在高德门址库或者已经采集过 */
		logger.info("提交的数据开始在高德已有门址库和已经采集门址库中进行排重");

		String rx = agentTaskUpload.getX();
		String ry = agentTaskUpload.getY();
		try {
			this.existsAutoNaviDataInSearchEngineInSubmit(userName, baseId,
					subAdCode, regDataName, rx, ry);
		} catch (SearchException e) {
			throw e;
		} catch (AppException ae) {
			// TODO: handle exception
			throw ae;
		}
		return -1;
	}
	/**
	 * 方法描述： 查询基础库里名称是否存在 完成时间： 2013-8-30 下午5:04:36 维护时间： 2013-8-30 下午5:04:36
	 * 维护原因: 当前版本： v1.0
	 * 
	 * @param subAdCode
	 * @param dataName
	 * @return
	 */
	private void existsAutoNaviDataInSearchEngineInSubmit(String userName,
			String baseId, String subAdCode, String dataName, String x, String y)
			throws AppException {

		Map<String, Object> paraMap = new HashMap<String, Object>();

		paraMap.put("keyword", dataName);
		paraMap.put("adcode", subAdCode);

		String offsets = this.offsetCorrdinate(x, y);
		String[] offsetXY = offsets.split(",");
		String offsetX = offsetXY[0];
		String offsetY = offsetXY[1];
		paraMap.put("x", offsetX);
		paraMap.put("y", offsetY);

		Map<String, Object> rsMap = new HashMap<String, Object>();

		/** 在高德母库中进行排重 */
		try {
			if (searchEngineService == null) {
				logger.error("提交数据，基于搜索引擎在高德母库中进行排重的时候，搜索引擎实例化失败");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}

			rsMap = searchEngineService
					.matchGDBaseDataFromSearchEngine(paraMap);
			// logger.info("子索引[agent_task_base]");

		} catch (SearchException e) {
			logger.error("提交数据时，基于搜索引擎 在高德母库中进行排重 的时候异常: " + e);
			throw e;
		}
		if (rsMap != null && rsMap.size() > 0) {
			logger.info("在高德母库中发现重复的数据 dataname=[" + dataName + "],adcode=["
					+ subAdCode + "]");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}
		if (rsMap != null) {
			rsMap.clear();
		}

		/** 在审核回传表中排重 */
		HashMap<String, Object[]> tmpMap = new HashMap<String, Object[]>();
		tmpMap.put("status", new Object[] { "1" });
		paraMap.put("customMap", tmpMap);
		try {
			if (searchEngineService == null) {
				logger.error("保存/修改任务 排重时，搜索引擎实例，实例化失败.... ");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}
			rsMap = searchEngineService
					.matchAuditbackDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("保存/修改任务,基于搜索引擎 在审核回传中进行排重 的时候异常: " + e);
			throw e;
		}
		logger.info("提交的任务时dataname=[" + dataName + "]在审核回传中排重结果：" + rsMap);
		if (rsMap != null && rsMap.size() > 0) {
			logger.error("要保存的门址=[" + dataName
					+ "]在审核回传中已经存在");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}

		/** 在采集库中进行排重 */
		if (rsMap != null) {
			rsMap.clear();
		}
		// HashMap<String, Object[]> tmpMap = new HashMap<String, Object[]>();
		tmpMap.put("status", new Object[] { "0", "1", "3" });
		paraMap.put("customMap", tmpMap);

		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		try {
			if (searchEngineService == null) {
				logger.error("提交数据，基于搜索引擎在采集库中进行排重的时候，搜索引擎实例化失败");
				throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			}

			// paraMap.remove("x");
			// paraMap.remove("y");
			rsList = searchEngineService
					.findMatchAgentBaseDataFromSearchEngine(paraMap);

		} catch (SearchException e) {
			logger.error("提交数据时，基于搜索引擎 在采集库中进行排重 的时候异常 " + e);
			throw e;
		}
		logger.info("提交的数据,dataname=[" + dataName + "],在采集表中find 排重结果:"
				+ rsList);
		if (rsList != null && rsList.size() > 0) {

			if (StringUtils.isBlank(baseId)) {
				/** 直接提交的主动任务 */
				logger.info("直接提交的任务 dataname=[" + dataName + "],username=["
						+ userName + "] 已经存在");
				throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
			}

			boolean cansubmit = true;
			for (Map<String, Object> m : rsList) {

				String id = m.get("id").toString();
				String status = m.get("status").toString();
				String taskId = m.get("task_id") == null ? "" : m
						.get("task_id").toString();
				String un = m.get("user_name").toString();

				/** 提交的主动任务是否已经被别人提交 */
				if ((status.equals("0") || status.equals("1"))
						&& !un.equals(userName)) {
					logger.error("主动任务提交的时候,门址=["
							+ dataName + "]已经被username=[" + un + "] 提交");
					// throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
					cansubmit = false;
					break;
				}

				logger.info("status=" + status + "   un=[" + un
						+ "]   username=[" + userName + "]");
				/** 提交的主动任务，是否已经被自己提交过 */
				if ((status.equals("0") || status.equals("1"))
						&& un.equals(userName)) {
					logger.error("username=[" + un
							+ "] 已经提交了一个同名 dataname=[" + dataName + "]的任务");
					// throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
					cansubmit = false;
					break;
				}

				if (id.equals(baseId) && status.equals("3")
						&& "".equals(taskId) && un.equals(userName)) {
					/** 提交的是自己保存的任务 */
					logger.info("要提交的任务 dataname=[" + dataName + "],username=["
							+ userName + "],taskId=[" + taskId + "]");
					// return;
				}
			}
			if (cansubmit) {
				return;
			}
			logger.error("主动任务提交的时候,门址=[" + dataName
					+ "]已经存在");
			throw new AppException(AppExceptionEnum.TASK_IS_EXISTS);
		}
	}
	private String saveTaskByAuto(Task task,
			AgentTaskUploadEntity agentTaskUpload, String regDataName,
			boolean update, String relatedLostId, String relatedTaskId) {
		try {
			/** 作废TASK_ALL**/

			if (update) {
				try {
					this.updateAgentTaskInDB(agentTaskUpload, agentTaskUpload.getBaseId(), null, false);
				} catch (AppException e) {
					logger.error("baseid=["+agentTaskUpload.getBaseId()+"] 已经提交不能重复提交", e);
					throw e;
				}
				
				
				
				return agentTaskUpload.getBaseId();
			} else {
				String id = this.addAgentTaskInDB(agentTaskUpload, regDataName,relatedLostId, relatedTaskId);
				return id;
			}
		} catch (AppException e) {
			throw e;
		}catch(Exception ex){
			logger.error("系统异常,请重新提交", ex);
			throw new AppException(AppExceptionEnum.INSERT_AGENT_TASK_ERROR);
		}
	}
	public void setCountFlag(String userName, String pack) {
		// TODO Auto-generated method stub
		
		Jedis jedis = null;
		try {
			int submit_time=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_MAX_SUBMIT_TIME).toString());
			jedis = jedisPool.getResource();
			String str=jedis.get("BATCH_"+userName+"_"+pack);
			if(str==null){
				str="0";
			}
			int count=new Integer(str)+1;
			jedis.set("BATCH_"+userName+"_"+pack, count+"");
			jedis.expire("BATCH_"+userName+"_"+pack, submit_time);// 如果有两个相同的key，返回值是-1
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	@Override
	public void setSubMitTaskFlag(String userName) {
		// TODO Auto-generated method stub
		
		Jedis jedis = null;
		try {
			int expire=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_MAX_SUBMIT_TASK_SPLIT).toString());
			jedis = jedisPool.getResource();
			jedis.set("SUBMIT_TASK_"+userName, "");// 如果有两个相同的key，返回值是-1
			jedis.expire("SUBMIT_TASK_"+userName, expire);// 1秒过期
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	@Override
	public void saveSecureCode(String userName, String code) {
		
		Jedis jedis = null;
		try {
			int expire=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_SECURE_CODE_TIME).toString());
			jedis = jedisPool.getResource();
			jedis.set("SECURE_CODE_"+userName, code);// 如果有两个相同的key，返回值是-1
			jedis.expire("SECURE_CODE_"+userName, expire);// 1秒过期
			//临时记录弹出过验证码的用户
			jedis.set("BLACK_"+userName, "");
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		
	}

	@Override
	public void saveSecureCodeForBatch(String userName, String code) {
		
		Jedis jedis = null;
		try {
			int expire=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_SECURE_CODE_TIME).toString());
			jedis = jedisPool.getResource();
			jedis.set("SECURE_CODE_"+userName, code);// 如果有两个相同的key，返回值是-1
			jedis.expire("SECURE_CODE_"+userName, expire);// 1秒过期
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		
	}

	@Override
	public boolean checkSecureCode(String userName, String code) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str=jedis.get("SECURE_CODE_"+userName);
			if(str==null){
				jedis.del("SECURE_CODE_"+userName);
				return false;
			}
			if(!str.equalsIgnoreCase(code)){
				jedis.del("SECURE_CODE_"+userName);
				return false;
			}
			jedis.del("SECURE_CODE_"+userName);
			jedis.del("BLACK_"+userName);
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return true;
	}

	@Override
	public String checkSecureCodeForBatch(String userName, String code) {
		Jedis jedis = null;
		String pack="";
		try {
			int submit_time=new Integer(PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_MAX_SUBMIT_TIME).toString());
			jedis = jedisPool.getResource();
			String str=jedis.get("SECURE_CODE_"+userName);
			if(str==null){
				jedis.del("SECURE_CODE_"+userName);
				return "";
			}
			if(!str.equalsIgnoreCase(code)){
				jedis.del("SECURE_CODE_"+userName);
				return "";
			}
			jedis.del("SECURE_CODE_"+userName);
			//jedis.del("BLACK_"+userName);
			pack=java.util.UUID.randomUUID().toString().replace("-", "");
			jedis.set("BATCH_"+userName+"_"+pack, "0");
			jedis.expire("BATCH_"+userName+"_"+pack,submit_time);
			
			
		} catch (Exception e) {// 如果redis抛出异常，直接返回

			/*
			 * 尝试解决jedis出现java.lang.ClassCastException: [B cannot be cast to
			 * java.lang.Long的问题
			 */
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			logger.error(e);
			throw new AppException(AppExceptionEnum.SECURECODE_GEN_ERROR);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return pack;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTaskByUnLock(String taskId, String userName,
			String dataName) {
		logger.info("客户端从本地删除保存的数据。taksid=[" + taskId + "],username=["
				+ userName + "],dataname=[" + dataName + "]");
		long agentTaskId = 0;
		try {
			List<?> l=(List<?>)agentTaskBaseDao.getUserTaskByTaskId(userName, dataName, taskId);
			if(l!=null&&l.size()>0){
				Map<String,Object> m=(Map<String,Object>)l.get(0);
				agentTaskId=(Long)m.get("id");
			}
			// 删除数据;
			int oper=0;
			if (agentTaskId > 0) {

//				logger.info("服务端从库中搜索到要删除的任务baseid=[" + agentTaskId + "]");
				oper=(Integer)agentTaskBaseDao.deleteTaskById(userName, agentTaskId+"");

				/** 从agent_task_base的索引中删除相关数据 */
//				List<String> tmpList = new ArrayList<String>();
//				tmpList.add(String.valueOf(agentTaskId));
//
//				logger.info("开始从搜索引擎索引中删除相关数据 baseid=[" + agentTaskId + "]");
//				try {
//				searchEngineService.removeDataFromSearchIndex(
//						"agent_task_base", tmpList);
//				} catch (SearchException ae) {
//					logger.error("删除保存的任务时，更新搜索引擎中agent_task_base的索引数据异常", ae);
//
//					/** 把更新失败的数据保存到redis中 */
//
//				}
				// 修改被动任务状态
				if (!"".equals(taskId)&&oper==1) {
					oper=0;

					logger.info("删除的是被动任务 baseid=[" + agentTaskId+ "], taskid=[" + taskId+ "],现在数据库中更新task_all的状态信息");
					oper=(Integer)agentTaskBaseDao.initAllotTask(taskId, userName);
					if(oper==1){
						agentTaskBaseDao.initPassiveTask(taskId, true);
					}else{
						agentTaskBaseDao.initPassiveTask(taskId, false);
					}

//					logger.info("删除的是被动任务，需要从搜索引擎索引中释放该任务baseid=["
//							+ agentTaskId + "],taskid=[" + taskId + "]");
//					/** 更新搜索引擎索引中该被动任务的状态(释放该被动任务) */
//					Map<String, Object> paraMap = new HashMap<String, Object>();
//					paraMap.put("data_type", "task_all");
//					paraMap.put("id", taskId);
//					paraMap.put("status", TaskStatusEnum.INIT.getCode());
//
//					try {
//					searchEngineService.updateOrInsertSearchIndex(paraMap,true);
//					} catch (SearchException e) {
//						logger.error("删除保存的任务时，更新搜索引擎中agent_task_base的索引数据异常",
//								e);
//
//						/** 把更新失败的数据保存到redis中 */
//					}
				}
			}
			
			// 如果是被动任务，更新task_all数据的状态信息

		} catch (Exception e) {
			logger.warn("删除任务失败 taskId=" + taskId + ",userName=" + userName + ",dataName=" + dataName);
			throw new AppException(AppExceptionEnum.UNLOCK_TASK_ERROR);
		}
		
	}

	
}
