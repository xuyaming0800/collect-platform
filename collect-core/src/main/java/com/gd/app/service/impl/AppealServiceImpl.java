package com.gd.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gd.app.dao.AgentAppealBaseDao;
import com.gd.app.entity.Appeal;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AppealService;
import com.gd.app.util.FileUtil;

//@Service("appealService")
public class AppealServiceImpl implements AppealService {
	private Logger logger = LogManager.getLogger(AppealServiceImpl.class);

	@Resource
	private AgentAppealBaseDao agentAppealBaseDao;

	/**
	 * 按条件查询申诉
	 * 
	 * @param appeal
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Appeal> queryAppeals(Appeal appeal, int pageIndex, int pageSize) {
		List<Appeal> appealList = new ArrayList<Appeal>();
		List<?> list;
		final int start = (pageIndex - 1) * pageSize;

		try {

			list = (List<?>)agentAppealBaseDao.queryAppeals(appeal, start,pageSize);

			if (CollectionUtils.isEmpty(list)){
				return appealList;
			}
			//APPE_URL,ID,APPE_USER_NAME,APPE_NAME,APPE_CONTENT,APPE_TYPE,APPE_STATUS,APPE_REMARK,APPE_REASON,APPE_SUBMIT_TIME,APPE_BASEID	
			Iterator<?> lt = list.iterator();
			while (lt.hasNext()) {
				Appeal appealObj = new Appeal();
				Map<String, Object> map = (Map<String, Object>) lt.next();

				// 设值
				if (map.get("APPE_BASEID") != null){
					appealObj.setBaseId(new Long(map.get("APPE_BASEID").toString()));
				}
//				if (map.get("APPE_CONTENT") != null){
//					appealObj.setContent(map.get("APPE_CONTENT").toString());
//				}	
				if (map.get("ID") != null){
					appealObj.setId(new Long(map.get("ID").toString()));
				}
				if (map.get("APPE_NAME") != null){
					appealObj.setName(map.get("APPE_NAME").toString());
				}

				if (map.get("APPE_REASON") != null){
					appealObj.setReason(map.get("APPE_REASON").toString());
				}	
				if (map.get("APPE_REMARK") != null){
					appealObj.setRemark(map.get("APPE_REMARK").toString());
				}	
				if (map.get("APPE_STATUS") != null){
					appealObj.setStatus(new Integer(map.get("APPE_STATUS").toString()));
				}	
				if (map.get("APPE_SUBMIT_TIME") != null){
					appealObj.setSubmitTime(map.get("APPE_SUBMIT_TIME").toString());
				}	
				if (map.get("APPE_TYPE") != null){
					appealObj.setType(map.get("APPE_TYPE").toString());
				}	
				if (map.get("APPE_USER_NAME") != null){
					appealObj.setUserName(map.get("APPE_USER_NAME").toString());
				}	
				if (map.get("APPE_URL") != null){
					appealObj.setUrl(map.get("APPE_URL").toString());
				}	

				appealList.add(appealObj);
			}

		} catch (Exception e) {
			logger.error("查询申诉失败", e);
			throw new AppException(AppExceptionEnum.APPEAL_QUERY_ERROR);
		}
		return appealList;
	}

	/**
	 * 查询单条申诉
	 * 
	 * @param id
	 * @return
	 */
	public Appeal queryAppeal(final Long id,String userName) {
		Object result = agentAppealBaseDao.queryAppeal(id,userName);
		if(result==null){
			return null;
		}
		List<Appeal> appealList = (List<Appeal>)result;
		if(CollectionUtils.isEmpty(appealList)){
			return null;
		}
		return appealList.get(0);
	}

	/**
	 * 通过baseID查询单条申诉
	 * 
	 * @param baseID
	 * @return
	 */
	public Appeal queryAppealByBaseID(final Long baseID,String userName) {
		Object result = agentAppealBaseDao.queryAppealByBaseID(baseID,userName);
		if(result==null){
			return null;
		}
		List<Appeal> appealList = (List<Appeal>)result;
		if(CollectionUtils.isEmpty(appealList)){
			return null;
		}
		return appealList.get(0);
	}

	/**
	 * 按条件查询申诉数量
	 * 
	 * @param appeal
	 * @return
	 */
	public int queryAppealsCount(Appeal appeal) {
		int count = 0;
		try {
			List<?> list = (List<?>)agentAppealBaseDao.queryAppealsCount(appeal);
			if (CollectionUtils.isEmpty(list)){
				return 0;
			}     
			count = new Integer(((Map<String, Object>)list.get(0)).get("total").toString());
		} catch (Exception e) {
			logger.error("查询申诉总量失败", e);
			throw new AppException(AppExceptionEnum.APPEAL_QUERY_ERROR);
		}
		return count;
	}

	/**
	 * 新增申诉
	 * 
	 * @param appeal
	 * @return
	 */
	public int saveAppeal(Appeal appeal) {
		int count = 0;
		try {
			Object result = agentAppealBaseDao.saveAppeal(appeal);
			if(result==null){
				return count;
			}
			count = (Integer)result;
			if (appeal.getBaseId() != null) {
				count += updateAppealTask(appeal.getBaseId(),appeal.getUserName());
			}

		} catch (Exception e) {
			logger.error("提交申诉失败", e);
			throw new AppException(AppExceptionEnum.APPEAL_UPDATE_ERROR);
		}
		return count;
	}

	/**
	 * 修改申诉处理状态
	 * 
	 * @param appeal
	 * @return
	 */
	public int updateAppeal(Appeal appeal) {
		int count = 0;
		try {
			Object result = agentAppealBaseDao.updateAppeal(appeal);
			if(result==null){
				return count;
			}
			count = (Integer)result;
		} catch (Exception e) {
			logger.error("修改申诉失败", e);
			throw new AppException(AppExceptionEnum.APPEAL_UPDATE_ERROR);
		}

		return count;
	}

	private int updateAppealTask(Long baseId,String userName) {
		int count = 0;
		try {
			Object result = agentAppealBaseDao.updateAppealTask(baseId,userName);
			if(result==null){
				return count;
			}
			count = (Integer)result;
		} catch (Exception e) {
			logger.error("修改申诉任务状态失败", e);
			throw new AppException(AppExceptionEnum.APPEAL_TASK_UPDATE_ERROR);
		}
		return count;
	}

	/**
	 * 通过baseId查询任务
	 * 
	 * @param baseID
	 * @return
	 */
	public Map<String, String> queryAppealTask(long baseID,String userName) {
		Map<String, String> taskMap = new HashMap<String, String>();
		List<?> list;
		try {
			list = (List<?>)agentAppealBaseDao.queryAppealTask(baseID,userName);

			if (CollectionUtils.isEmpty(list)){
				return taskMap;
			}
			Iterator<?> lt = list.iterator();
			while (lt.hasNext()) {

				Map<String, Object> map = (Map<String, Object>) lt.next();
				String dataName = map.get("DATA_NAME").toString();
				String userNameStr = map.get("USER_NAME").toString();
				String submitTime = map.get("SUBMIT_TIME").toString();
				String comments = map.get("COMMENTS") == null ? "" : map.get("COMMENTS").toString();
				String imageID = map.get("IMAGE_ID") == null ? "" : map.get("IMAGE_ID").toString();
				String imageURL = FileUtil.getImageUrl(imageID);
				taskMap.put("name", dataName);
				taskMap.put("userName", userNameStr);
				taskMap.put("submitTime", submitTime);
				taskMap.put("comments", comments);
				taskMap.put("imageUrl", imageURL);
			}
		} catch (Exception e) {
			logger.error("申诉任务详情无法显示", e);
			throw new AppException(AppExceptionEnum.APPEAL_TASK_ERROR);
		}
		return taskMap;
	}

}
