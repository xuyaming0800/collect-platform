package com.autonavi.collect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import autonavi.online.framework.property.PropertiesConfigUtil;

import com.autonavi.collect.bean.CollectAllotBaseUser;
import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.entity.SearchPassiveTaskEntity;
import com.autonavi.collect.entity.SearchPassiveTaskFacetResult;
import com.autonavi.collect.entity.SearchPassiveTaskResultEntity;
import com.autonavi.collect.entity.SearchTaskQueryEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.search.declare.SearchResult;
import com.search.implement.MySearchServiceImp;
import com.search.model.MyRequest;
import com.search.util.NodeTile;

@Component
public class SearchPassiveTaskManager {

	@Autowired
	private MySearchServiceImp searchServiceImp;

	@Autowired
	private AroundTask aroundTask;

	@Autowired
	private AroundNoUserTask aroundNoUserTask;

	@Autowired
	private DistrictTask districtTask;

	@Autowired
	private DistrictNoUserTask districtNoUserTask;

	@Autowired
	private DistrictFacetTask districtFacetTask;

	@Autowired
	private DistrictFacetNoUserTask districtFacetNoUserTask;

	/**
	 * 获取圈内的所有信息
	 * 
	 * @author chunsheng.zhang
	 *
	 */
	@Component
	public class AroundTask implements
			CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
		@Autowired
		public AroundTask(SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public SearchPassiveTaskResultEntity execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();
			String indexName = null;
			String indexType = null;
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME);
				indexType = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME_TYPE);
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexNameArray(new String[] { indexType });

			requestInfo.setX(searchTaskQueryEntity.getX());
			requestInfo.setY(searchTaskQueryEntity.getY());
			requestInfo.setRadius(searchTaskQueryEntity.getRange());
			requestInfo.setNumber(searchTaskQueryEntity.getNumber());
			requestInfo.setPage(searchTaskQueryEntity.getPage());

			Map<String, String> sortMap = new HashMap<String, String>(1);
			sortMap.put("location", "asc");
			requestInfo.setSortMap(sortMap);

			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] { taskStatus });
			}
		
			customMap.put("b_status", new Object[] { "0" });

			if (null != adcode) {
				customMap.put("adcode", new Object[] { adcode });
			}

			if (null != searchTaskQueryEntity.getCustomMap()) {
				customMap.putAll(searchTaskQueryEntity.getCustomMap());
			}

			if (null != searchTaskQueryEntity.getUserId()) {
				customMap.put("b_allot_user_id",
						new Object[] { searchTaskQueryEntity.getUserId() });
			}

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}

			return init(searchResult, searchTaskQueryEntity);
		}

	}

	/**
	 * 获取圈内的所有信息
	 * 
	 * @author chunsheng.zhang
	 *
	 */
	@Component
	public class AroundNoUserTask implements
			CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
		@Autowired
		public AroundNoUserTask(
				SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public SearchPassiveTaskResultEntity execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();
			Long ownerId=searchTaskQueryEntity.getOwnerId();
			Long clazzId=searchTaskQueryEntity.getCollectCLazzId();
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			String indexName = null;
			String indexType = null;
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME);
				indexType = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME_TYPE);
				
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexTypeArray(new String[] { indexType });

			requestInfo.setX(searchTaskQueryEntity.getX());
			requestInfo.setY(searchTaskQueryEntity.getY());
			requestInfo.setRadius(searchTaskQueryEntity.getRange());
			requestInfo.setNumber(searchTaskQueryEntity.getNumber());
			requestInfo.setPage(searchTaskQueryEntity.getPage());

			Map<String, String> sortMap = new HashMap<String, String>(1);
			sortMap.put("location", "asc");
			requestInfo.setSortMap(sortMap);

			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] { taskStatus });
			}
			
			
			if(ownerId!=null){
				customMap.put("owner_id", new Object[] { ownerId });
			}
			if(clazzId!=null){
				customMap.put("task_clazz_id", new Object[] { clazzId });
			}

			if (null != adcode) {
				customMap.put("adcode", new Object[] { adcode });
			}

			if (null != searchTaskQueryEntity.getCustomMap()) {
				customMap.putAll(searchTaskQueryEntity.getCustomMap());
			}

			/*
			 * if(null != searchTaskQueryEntity.getUserId()) {
			 * customMap.put("b_allot_user_id", new
			 * Object[]{searchTaskQueryEntity.getUserId()}); }
			 */

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}

			return init(searchResult, searchTaskQueryEntity);
		}

	}

	@Component
	public class DistrictTask implements
			CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
		@Autowired
		public DistrictTask(SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@Override
		public SearchPassiveTaskResultEntity execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();

			String indexName = null;
			String indexType = null;
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME);
				indexType = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME_TYPE);
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}

			requestInfo.setNumber(searchTaskQueryEntity.getNumber());
			requestInfo.setPage(searchTaskQueryEntity.getPage());
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexTypeArray(new String[] { indexType });

			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] {taskStatus });
			}
			customMap.put("b_status", new Object[] { "0" });

			if (null != adcode) {
				customMap.put("adcode", new Object[] { adcode });
			}

			if (null != searchTaskQueryEntity.getUserId()) {
				customMap.put("b_allot_user_id",
						new Object[] { searchTaskQueryEntity.getUserId() });
			}

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}
			// List<Map<String, Object>> resultList = searchResult.getList();
			return init(searchResult, searchTaskQueryEntity);
		}

	}

	@Component
	public class DistrictNoUserTask implements
			CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
		@Autowired
		public DistrictNoUserTask(
				SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@Override
		public SearchPassiveTaskResultEntity execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();
			Long ownerId=searchTaskQueryEntity.getOwnerId();
			Long clazzId=searchTaskQueryEntity.getCollectCLazzId();

			String indexName = null;
			String indexType = null;
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME);
				indexType=(String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME_TYPE);
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}

			requestInfo.setNumber(searchTaskQueryEntity.getNumber());
			requestInfo.setPage(searchTaskQueryEntity.getPage());
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexTypeArray(new String[] { indexType });

			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] {taskStatus });
			}
			
			
			if(ownerId!=null){
				customMap.put("owner_id", new Object[] { ownerId });
			}
			if(clazzId!=null){
				customMap.put("task_clazz_id", new Object[] { clazzId });
			}
			if (null != adcode) {
				customMap.put("adcode", new Object[] { adcode });
			}

			/*
			 * if(null != searchTaskQueryEntity.getUserId()) {
			 * customMap.put("b_allot_user_id", new
			 * Object[]{searchTaskQueryEntity.getUserId()}); }
			 */

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}
			// List<Map<String, Object>> resultList = searchResult.getList();
			return init(searchResult, searchTaskQueryEntity);
		}

	}

	/***
	 * 统计
	 * 
	 * @author chunsheng.zhang
	 *
	 */
	@Component
	public class DistrictFacetTask
			implements
			CollectCore<List<SearchPassiveTaskFacetResult>, SearchTaskQueryEntity> {
		@Autowired
		public DistrictFacetTask(
				SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@Override
		public List<SearchPassiveTaskFacetResult> execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();

			String indexName = null;
			String indexType =null;
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME);
				indexType = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_INDEX_NAME_TYPE);
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}

			if (null == searchTaskQueryEntity.getPage()) {
				searchTaskQueryEntity.setPage(1);
			}
			if (null == searchTaskQueryEntity.getNumber()) {
				searchTaskQueryEntity.setNumber(20);
			}

			requestInfo.setNumber(searchTaskQueryEntity.getNumber());

			requestInfo.setPage(searchTaskQueryEntity.getPage());
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexTypeArray(new String[] { indexType});

			// requestInfo.setAdcode(adcode);
			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] {taskStatus });
			}
			if (null != searchTaskQueryEntity.getUserId()) {
				customMap.put("b_allot_user_id",
						new Object[] { searchTaskQueryEntity.getUserId() });
			}

			if (null == adcode || "".equals(adcode)) {
				requestInfo.setGroupFieldArray(new String[] { "padcode" });
			} else if (adcode.length() == 2) {
				customMap.put("padcode", new String[] { adcode });
				requestInfo.setGroupFieldArray(new String[] { "cadcode" });
			} else if (adcode.length() == 4) {
				customMap.put("cadcode", new String[] { adcode });
				requestInfo.setGroupFieldArray(new String[] { "adcode" });
			}

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}

			Map<String, Long> groupMap = searchResult.getGroupMap();

			List<SearchPassiveTaskFacetResult> searchPassiveTaskFacetResults = new ArrayList<SearchPassiveTaskFacetResult>();

			SearchPassiveTaskFacetResult searchPassiveTaskFacetResult = null;
			Set<Map.Entry<String, Long>> st = groupMap.entrySet();
			Iterator<Map.Entry<String, Long>> itr = st.iterator();
			while (itr.hasNext()) {
				searchPassiveTaskFacetResult = new SearchPassiveTaskFacetResult();

				Map.Entry<String, Long> me = itr.next();
				searchPassiveTaskFacetResult.setAdcode(me.getKey());
				searchPassiveTaskFacetResult.setCount(me.getValue());

				searchPassiveTaskFacetResults.add(searchPassiveTaskFacetResult);
			}

			return searchPassiveTaskFacetResults;
		}

	}

	/***
	 * 统计
	 * 
	 * @author chunsheng.zhang
	 *
	 */
	@Component
	public class DistrictFacetNoUserTask
			implements
			CollectCore<List<SearchPassiveTaskFacetResult>, SearchTaskQueryEntity> {
		@Autowired
		public DistrictFacetNoUserTask(
				SearchPassiveTaskManager collectPassiveTaskManager) {

		}

		@Override
		public List<SearchPassiveTaskFacetResult> execute(
				SearchTaskQueryEntity searchTaskQueryEntity)
				throws BusinessException {
			MyRequest requestInfo = new MyRequest();
			String adcode = searchTaskQueryEntity.getAdcode();
			Long ownerId=searchTaskQueryEntity.getOwnerId();
			Long clazzId=searchTaskQueryEntity.getCollectCLazzId();

			String indexName = null;
			String indexType = null;
			try {
				indexName = (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME);
				indexType= (String) PropertiesConfigUtil
						.getPropertiesConfigInstance().getProperty(
								CommonConstant.COLLECT_NOUSER_INDEX_NAME_TYPE);
			} catch (BusinessException e) {
				throw new BusinessRunException(e);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
			}

			if (null == searchTaskQueryEntity.getPage()) {
				searchTaskQueryEntity.setPage(1);
			}
			if (null == searchTaskQueryEntity.getNumber()) {
				searchTaskQueryEntity.setNumber(20);
			}

			requestInfo.setNumber(searchTaskQueryEntity.getNumber());

			requestInfo.setPage(searchTaskQueryEntity.getPage());
			requestInfo.setIndexNameArray(new String[] { indexName });
			requestInfo.setIndexTypeArray(new String[] { indexType });

			// requestInfo.setAdcode(adcode);
			HashMap<String, Object[]> customMap = requestInfo.getMustMap();
			Integer taskStatus=searchTaskQueryEntity.getTaskStatus();
			if(taskStatus!=null){
				customMap.put("task_package_status",
						new Object[] {taskStatus });
			}
			/*
			 * if(null != searchTaskQueryEntity.getUserId()) {
			 * customMap.put("b_allot_user_id", new
			 * Object[]{searchTaskQueryEntity.getUserId()}); }
			 */
			
            if(ownerId!=null){
            	customMap.put("owner_id", new Object[] { ownerId });
			}
            
            if(clazzId!=null){
				customMap.put("task_clazz_id", new Object[] { clazzId });
			}

			if (null == adcode || "".equals(adcode)) {
				requestInfo.setGroupFieldArray(new String[] { "padcode" });
			} else if (adcode.length() == 2) {
				customMap.put("padcode", new String[] { adcode });
				requestInfo.setGroupFieldArray(new String[] { "cadcode" });
			} else if (adcode.length() == 4) {
				customMap.put("cadcode", new String[] { adcode });
				requestInfo.setGroupFieldArray(new String[] { "adcode" });
			}
			
			

			SearchResult searchResult = new SearchResult(0);
			try {
				searchResult = searchServiceImp.search(requestInfo);
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.SEARCH_QUERY_ERROR);
			}

			Map<String, Long> groupMap = searchResult.getGroupMap();

			List<SearchPassiveTaskFacetResult> searchPassiveTaskFacetResults = new ArrayList<SearchPassiveTaskFacetResult>();

			SearchPassiveTaskFacetResult searchPassiveTaskFacetResult = null;
			Set<Map.Entry<String, Long>> st = groupMap.entrySet();
			Iterator<Map.Entry<String, Long>> itr = st.iterator();
			while (itr.hasNext()) {
				searchPassiveTaskFacetResult = new SearchPassiveTaskFacetResult();

				Map.Entry<String, Long> me = itr.next();
				searchPassiveTaskFacetResult.setAdcode(me.getKey());
				searchPassiveTaskFacetResult.setCount(me.getValue());

				searchPassiveTaskFacetResults.add(searchPassiveTaskFacetResult);
			}

			return searchPassiveTaskFacetResults;
		}

	}

	/**
	 * 构建CollectPassiveTaskEntity实体
	 * 
	 * @param resultList
	 * @return
	 */
	private SearchPassiveTaskResultEntity init(SearchResult searchResult,
			SearchTaskQueryEntity searchTaskQueryEntity) {
		double x = searchTaskQueryEntity.getX();
		double y = searchTaskQueryEntity.getY();

		List<Map<String, Object>> resultList = searchResult.getList();
		List<SearchPassiveTaskEntity> collectPassiveTaskEntitys = new ArrayList<SearchPassiveTaskEntity>();
		for (Map<String, Object> m : resultList) {
			SearchPassiveTaskEntity collectPassiveTaskEntity = new SearchPassiveTaskEntity();

			Double distance = null;

			// collect_task_base 表处理
			Long id = Long.parseLong(m.get("aid").toString());
			Long passive_package_id = convertLong(m.get("passive_package_id"));
			String task_package_name = (String) m.get("task_package_name");
			String task_package_desc = (String) m.get("task_package_desc");
			Integer task_package_count = convertInteger(m
					.get("task_package_count"));
			Double task_package_pay = convertDouble(m.get("task_package_pay"));
			Integer task_package_status = convertInteger(m
					.get("task_package_status"));
			Integer task_package_type = convertInteger(m
					.get("task_package_type"));
			Long create_time = convertLong(m.get("create_time"));
			Long update_time = convertLong(m.get("update_time"));
			Long a_allot_user_id = convertLong(m.get("a_allot_user_id"));
			Long collect_user_id = convertLong(m.get("collect_user_id"));
			Long allot_end_time = convertLong(m.get("allot_end_time"));
			Long taskClazzId=m.get("task_clazz_id")==null?null:convertLong(m.get("task_clazz_id"));
			String task_package_cate = (String) m.get("task_package_cate");

			CollectBasePackage collectBasePackage = new CollectBasePackage();
			collectBasePackage.setId(id);
			collectBasePackage.setPassivePackageId(passive_package_id);
			collectBasePackage.setTaskPackageName(task_package_name);
			collectBasePackage.setTaskPackageDesc(task_package_desc);
			collectBasePackage.setTaskPackageCount(task_package_count);
			collectBasePackage.setTaskPackagePay(task_package_pay);
			collectBasePackage.setTaskPackageStatus(task_package_status);
			collectBasePackage.setTaskPackageType(task_package_type);
			collectBasePackage.setCreateTime(create_time);
			collectBasePackage.setUpdateTime(update_time);
			collectBasePackage.setAllotUserId(a_allot_user_id);
			collectBasePackage.setCollectUserId(collect_user_id);
			collectBasePackage.setAllotEndTime(allot_end_time);
			collectBasePackage.setTaskPackageCate(task_package_cate);
			collectBasePackage.setTaskClazzId(taskClazzId);

			// collect_allot_base_user 表处理
			CollectAllotBaseUser collectAllotBaseUser = new CollectAllotBaseUser();
			Long bid = convertLong(m.get("bid"));
			Long b_package_id = convertLong(m.get("b_package_id"));
			Long b_allot_user_id = convertLong(m.get("b_allot_user_id"));

			collectAllotBaseUser.setId(bid);
			collectAllotBaseUser.setPackageId(b_package_id);
			collectAllotBaseUser.setAllotUserId(b_allot_user_id);
			collectAllotBaseUser.setStatus(convertInteger(m.get("b_status")));

			// collect_base_original_coordinate 表处理

			Long cid = convertLong(m.get("cid"));
			Long passive_id = convertLong(m.get("passive_id"));
			Double original_x = (Double) m.get("x");
			Double original_y = (Double) m.get("y");
			String adcode = (String) m.get("adcode");
			String task_sample_img = (String) m.get("task_sample_img");
			Integer coordinate_status = convertInteger(m
					.get("coordinate_status"));
			Long c_package_id = convertLong(m.get("c_package_id"));

			CollectOriginalCoordinate collectOriginalCoordinate = new CollectOriginalCoordinate();
			collectOriginalCoordinate.setId(cid);
			collectOriginalCoordinate.setPassiveId(passive_id);
			collectOriginalCoordinate.setOriginalX(original_x);
			collectOriginalCoordinate.setOriginalY(original_y);
			collectOriginalCoordinate.setOriginalAdcode(Integer
					.parseInt(adcode));
			collectOriginalCoordinate.setTaskSampleImg(task_sample_img);
			collectOriginalCoordinate.setCoordinateStatus(coordinate_status);
			collectOriginalCoordinate.setPackageId(c_package_id);

			if (StringUtils.hasText(x + "") && StringUtils.hasText(y + "")) {
				distance = NodeTile.distance(x, y, original_x, original_y);
				collectPassiveTaskEntity.setDistance(distance);
			}

			collectPassiveTaskEntity.setCollectBasePackage(collectBasePackage);
			collectPassiveTaskEntity
					.setCollectOriginalCoordinate(collectOriginalCoordinate);
			collectPassiveTaskEntity
					.setCollectAllotBaseUser(collectAllotBaseUser);

			collectPassiveTaskEntitys.add(collectPassiveTaskEntity);
		}

		SearchPassiveTaskResultEntity searchPassiveTaskResultEntity = new SearchPassiveTaskResultEntity();
		searchPassiveTaskResultEntity
				.setCollectPassiveTaskEntity(collectPassiveTaskEntitys);
		searchPassiveTaskResultEntity.setCount(searchResult.getCount());
		return searchPassiveTaskResultEntity;
	}

	private Integer convertInteger(Object value) {
		if (null != value) {
			return Integer.parseInt(value.toString());
		}
		return null;
	}

	private Long convertLong(Object value) {
		if (null != value) {
			return Long.parseLong(value.toString());
		}
		return null;
	}

	private Double convertDouble(Object value) {
		if (null != value) {
			return Double.parseDouble(value.toString());
		}
		return null;
	}

	public AroundTask getAroundTask() {
		return aroundTask;
	}

	public void setAroundTask(AroundTask aroundTask) {
		this.aroundTask = aroundTask;
	}

	public DistrictTask getDistrictTask() {
		return districtTask;
	}

	public void setDistrictTask(DistrictTask districtTask) {
		this.districtTask = districtTask;
	}

	public DistrictFacetTask getDistrictFacetTask() {
		return districtFacetTask;
	}

	public void setDistrictFacetTask(DistrictFacetTask districtFacetTask) {
		this.districtFacetTask = districtFacetTask;
	}

	public AroundNoUserTask getAroundNoUserTask() {
		return aroundNoUserTask;
	}

	public void setAroundNoUserTask(AroundNoUserTask aroundNoUserTask) {
		this.aroundNoUserTask = aroundNoUserTask;
	}

	public DistrictNoUserTask getDistrictNoUserTask() {
		return districtNoUserTask;
	}

	public void setDistrictNoUserTask(DistrictNoUserTask districtNoUserTask) {
		this.districtNoUserTask = districtNoUserTask;
	}

	public DistrictFacetNoUserTask getDistrictFacetNoUserTask() {
		return districtFacetNoUserTask;
	}

	public void setDistrictFacetNoUserTask(
			DistrictFacetNoUserTask districtFacetNoUserTask) {
		this.districtFacetNoUserTask = districtFacetNoUserTask;
	}

}
