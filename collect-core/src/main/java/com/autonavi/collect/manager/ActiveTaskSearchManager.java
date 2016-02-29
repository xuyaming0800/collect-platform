package com.autonavi.collect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.RESOURCE_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.entity.ActiveTaskAroundSearchEntity;
import com.autonavi.collect.entity.ActiveTaskAroundSearchResultEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.search.declare.SearchResult;
import com.search.implement.MySearchServiceImp;
import com.search.model.MyRequest;

@Component
public class ActiveTaskSearchManager {

	@Autowired
	private MySearchServiceImp searchServiceImp;

	@Autowired
	private ActiveTaskAroundSearch activeTaskAroundSearch;
	
	@Autowired
    private ActiveH5TaskAroundSearch activeH5TaskAroundSearch;
	
	@Autowired
	private ActiveTaskAroundSearchCount activeTaskAroundSearchCount;
	
	@Autowired
	private ActiveTaskResourceAroundSearch activeTaskResourceAroundSearch;

	// @Autowired
	// private AroundNoUserTask aroundNoUserTask;
	//
	// @Autowired
	// private DistrictTask districtTask;
	//
	// @Autowired
	// private DistrictNoUserTask districtNoUserTask;
	//
	// @Autowired
	// private DistrictFacetTask districtFacetTask;
	//
	// @Autowired
	// private DistrictFacetNoUserTask districtFacetNoUserTask;

	public ActiveTaskResourceAroundSearch getActiveTaskResourceAroundSearch() {
		return activeTaskResourceAroundSearch;
	}

	public ActiveTaskAroundSearch getActiveTaskAroundSearch() {
		return activeTaskAroundSearch;
	}
	
	public ActiveH5TaskAroundSearch getActiveH5TaskAroundSearch() {
		return activeH5TaskAroundSearch;
	}
	

	public void setActiveTaskAroundSearch(
			ActiveTaskAroundSearch activeTaskAroundSearch) {
		this.activeTaskAroundSearch = activeTaskAroundSearch;
	}
	
	

	public ActiveTaskAroundSearchCount getActiveTaskAroundSearchCount() {
		return activeTaskAroundSearchCount;
	}



	private PropertiesConfig propertiesConfig;

	public ActiveTaskSearchManager() throws Exception {
		if (propertiesConfig == null) {
			propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
		}
	}
	/**
	 * 获取圈内点计数
	 * @author xuyaming
	 *
	 */
	@Component
	public class ActiveTaskAroundSearchCount
			implements
			CollectCore<ActiveTaskAroundSearchEntity, ActiveTaskAroundSearchEntity> {
		@Autowired
		public ActiveTaskAroundSearchCount(
				ActiveTaskSearchManager searchActiveTaskManager) {

		}

		@Override
		public ActiveTaskAroundSearchEntity execute(
				ActiveTaskAroundSearchEntity activeTaskSearchEntity) throws BusinessException {
			try {
				MyRequest myRequest=getActiveTaskAroundSearchRequest(activeTaskSearchEntity);
				Long count=searchServiceImp.searchCount(myRequest);
				if(count>1){
					activeTaskSearchEntity.setNumber(count.intValue());
				}
				return activeTaskSearchEntity;
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
			}
		}
	}
	
	@Component
	public class ActiveTaskResourceAroundSearch
			implements
			CollectCore<List<ActiveTaskAroundSearchResultEntity>, ActiveTaskAroundSearchEntity> {
		@Autowired
		public ActiveTaskResourceAroundSearch(
				ActiveTaskSearchManager searchActiveTaskManager) {

		}

		@Override
		public List<ActiveTaskAroundSearchResultEntity> execute(
				ActiveTaskAroundSearchEntity activeTaskSearchEntity) throws BusinessException {
			try {
				MyRequest myRequest=getActiveTaskResourceAroundSearchRequest(activeTaskSearchEntity);
				SearchResult searchResult = searchServiceImp.search(myRequest);
				List<Map<String, Object>> searchResultList = searchResult
						.getList();
				List<ActiveTaskAroundSearchResultEntity> resultList = new ArrayList<ActiveTaskAroundSearchResultEntity>();
				for (Map<String, Object> itemMap : searchResultList) {
					ActiveTaskAroundSearchResultEntity activeTaskAroundSearchResultEntity = new ActiveTaskAroundSearchResultEntity();
					activeTaskAroundSearchResultEntity.setUpdateTime(Long
							.parseLong(itemMap.get("update_time").toString()));
					activeTaskAroundSearchResultEntity
							.setClazzId(Long.parseLong(itemMap.get(
									"task_clazz_id").toString()));
					activeTaskAroundSearchResultEntity.setTaskStatus(Integer
							.parseInt(itemMap.get("status").toString()));
					activeTaskAroundSearchResultEntity.setLocation(itemMap.get(
							"location").toString());
					resultList.add(activeTaskAroundSearchResultEntity);
				}
				return resultList;
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
			}
		}
	}
	
	@Component
	public class ActiveH5TaskAroundSearch
			implements
			CollectCore<List<ActiveTaskAroundSearchResultEntity>, ActiveTaskAroundSearchEntity> {
		@Autowired
		public ActiveH5TaskAroundSearch(
				ActiveTaskSearchManager searchActiveTaskManager) {

		}
		public List<ActiveTaskAroundSearchResultEntity> execute(
				ActiveTaskAroundSearchEntity activeTaskSearchEntity)
				throws BusinessException {
			try {
				MyRequest myRequest=getActiveH5TaskAroundSearchRequest(activeTaskSearchEntity);
				SearchResult searchResult = searchServiceImp.search(myRequest);
				List<Map<String, Object>> searchResultList = searchResult
						.getList();
				List<ActiveTaskAroundSearchResultEntity> resultList = new ArrayList<ActiveTaskAroundSearchResultEntity>();
				for (Map<String, Object> itemMap : searchResultList) {
					ActiveTaskAroundSearchResultEntity activeTaskAroundSearchResultEntity = new ActiveTaskAroundSearchResultEntity();
					activeTaskAroundSearchResultEntity
							.setCollectDataName(itemMap
									.get("collect_data_name").toString());
					if (itemMap.get("verify_status") != null) {
						activeTaskAroundSearchResultEntity
								.setVerifyStatus(Integer.parseInt(itemMap.get(
										"verify_status").toString()));
					}
					activeTaskAroundSearchResultEntity.setUpdateTime(Long
							.parseLong(itemMap.get("update_time").toString()));
					if(itemMap.get("image_status")!=null){
						activeTaskAroundSearchResultEntity.setImageStatus(Integer.parseInt(itemMap.get("image_status").toString()));
					}
					activeTaskAroundSearchResultEntity
							.setReleaseFreezeTime(Long.parseLong(itemMap.get(
									"release_freeze_time").toString()));
					activeTaskAroundSearchResultEntity
							.setClazzId(Long.parseLong(itemMap.get(
									"task_clazz_id").toString()));
					activeTaskAroundSearchResultEntity
					        .setTaskId(Long.parseLong(itemMap.get(
							        "task_id").toString()));
					activeTaskAroundSearchResultEntity.setUserId(Long
							.parseLong(itemMap.get("collect_user_id")
									.toString()));
					activeTaskAroundSearchResultEntity.setTaskStatus(Integer
							.parseInt(itemMap.get("task_status").toString()));
					activeTaskAroundSearchResultEntity.setLocation(itemMap.get(
							"location").toString());
					resultList.add(activeTaskAroundSearchResultEntity);
				}
				return resultList;
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
			}
		}
	}

	/**
	 * 获取圈内的所有信息
	 * 
	 * @author ang.ji
	 *
	 */
	@Component
	public class ActiveTaskAroundSearch
			implements
			CollectCore<List<ActiveTaskAroundSearchResultEntity>, ActiveTaskAroundSearchEntity> {
		@Autowired
		public ActiveTaskAroundSearch(
				ActiveTaskSearchManager searchActiveTaskManager) {

		}

		// @SuppressWarnings("unchecked")
		// @Override
		// public SearchPassiveTaskResultEntity execute(
		// SearchTaskQueryEntity searchTaskQueryEntity)
		// throws BusinessException {
		// MyRequest requestInfo = new MyRequest();
		// String adcode = searchTaskQueryEntity.getAdcode();
		// String indexName = null;
		// try {
		// indexName = (String) PropertiesConfigUtil
		// .getPropertiesConfigInstance().getProperty(
		// CommonConstant.COLLECT_INDEX_NAME);
		// } catch (BusinessException e) {
		// throw new BusinessRunException(e);
		// } catch (Exception e) {
		// throw new BusinessRunException(
		// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
		// }
		// requestInfo.setIndexNameArray(new String[] { indexName });
		//
		// requestInfo.setX(searchTaskQueryEntity.getX());
		// requestInfo.setY(searchTaskQueryEntity.getY());
		// requestInfo.setRange(searchTaskQueryEntity.getRange());
		// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
		// requestInfo.setPage(searchTaskQueryEntity.getPage());
		//
		// Map<String, String> sortMap = new HashMap<String, String>(1);
		// sortMap.put("location", "asc");
		// requestInfo.setSortMap(sortMap);
		//
		// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
		// customMap.put("task_package_status",
		// new Object[] { TASK_STATUS.ALLOT.getCode() });
		// customMap.put("b_status", new Object[] { "0" });
		//
		// if (null != adcode) {
		// customMap.put("adcode", new Object[] { adcode });
		// }
		//
		// if (null != searchTaskQueryEntity.getCustomMap()) {
		// customMap.putAll(searchTaskQueryEntity.getCustomMap());
		// }
		//
		// if (null != searchTaskQueryEntity.getUserId()) {
		// customMap.put("b_allot_user_id",
		// new Object[] { searchTaskQueryEntity.getUserId() });
		// }
		//
		// SearchResult searchResult = new SearchResult(0);
		// try {
		// searchResult = searchServiceImp.search(requestInfo);
		// } catch (Exception e) {
		// throw new BusinessRunException(
		// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
		// }
		//
		// return init(searchResult, searchTaskQueryEntity);
		// }

		public List<ActiveTaskAroundSearchResultEntity> execute(
				ActiveTaskAroundSearchEntity activeTaskSearchEntity)
				throws BusinessException {

			try {
				MyRequest myRequest=getActiveTaskAroundSearchRequest(activeTaskSearchEntity);
				SearchResult searchResult = searchServiceImp.search(myRequest);
				List<Map<String, Object>> searchResultList = searchResult
						.getList();
				List<ActiveTaskAroundSearchResultEntity> resultList = new ArrayList<ActiveTaskAroundSearchResultEntity>();
				for (Map<String, Object> itemMap : searchResultList) {
					// {update_time=1434015647,
					// verify_status=null,
					// _id=608541175216865280,608541207332651008,
					// location=116.30505,39.9821, task_status=4,
					// task_clazz_id=607750605007482885,
					// collect_data_name=苏州街1,
					// release_freeze_time=0}
					ActiveTaskAroundSearchResultEntity activeTaskAroundSearchResultEntity = new ActiveTaskAroundSearchResultEntity();
					activeTaskAroundSearchResultEntity
							.setCollectDataName(itemMap
									.get("collect_data_name").toString());
					if (itemMap.get("verify_status") != null) {
						activeTaskAroundSearchResultEntity
								.setVerifyStatus(Integer.parseInt(itemMap.get(
										"verify_status").toString()));
					}
					activeTaskAroundSearchResultEntity.setUpdateTime(Long
							.parseLong(itemMap.get("update_time").toString()));
					activeTaskAroundSearchResultEntity
							.setReleaseFreezeTime(Long.parseLong(itemMap.get(
									"release_freeze_time").toString()));
					activeTaskAroundSearchResultEntity
							.setClazzId(Long.parseLong(itemMap.get(
									"task_clazz_id").toString()));
					activeTaskAroundSearchResultEntity
					        .setTaskId(Long.parseLong(itemMap.get(
							        "task_id").toString()));
					activeTaskAroundSearchResultEntity.setUserId(Long
							.parseLong(itemMap.get("collect_user_id")
									.toString()));
					activeTaskAroundSearchResultEntity.setTaskStatus(Integer
							.parseInt(itemMap.get("task_status").toString()));
					activeTaskAroundSearchResultEntity.setLocation(itemMap.get(
							"location").toString());
					if (itemMap.get("verify_status") != null
							&& !itemMap.get("verify_status").equals(""))
						activeTaskAroundSearchResultEntity
								.setVerifyStatus(Integer.parseInt(itemMap.get(
										"verify_status").toString()));
					resultList.add(activeTaskAroundSearchResultEntity);
				}
				return resultList;
			} catch (Exception e) {
				throw new BusinessRunException(
						BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR);
			}
		}

	}
	
	private MyRequest getActiveTaskAroundSearchRequest(ActiveTaskAroundSearchEntity activeTaskSearchEntity)throws Exception{
		MyRequest myRequest = new MyRequest();
		myRequest
				.setIndexNameArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME)
						.toString() });
		myRequest
				.setIndexTypeArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME_TYPE)
						.toString() });
		myRequest.setX(activeTaskSearchEntity.getX());
		myRequest.setY(activeTaskSearchEntity.getY());
		if(activeTaskSearchEntity.getNumber()!=null){
			myRequest.setNumber(activeTaskSearchEntity.getNumber());
		}
		myRequest.setRadius(activeTaskSearchEntity.getRadius());
		HashMap<String, Object[]> mustMap = new HashMap<String, Object[]>();
		HashMap<String, Object[]> mustNotMap = new HashMap<String, Object[]>();
		if (activeTaskSearchEntity.getClazzId() > 0) {
			mustMap.put("init_clazz_id",
					new Object[] { activeTaskSearchEntity.getClazzId() });
		}
		if (activeTaskSearchEntity.getUserId() != null
				&& activeTaskSearchEntity.getUserId() > 0) {
			mustMap.put("collect_user_id",
					new Object[] { activeTaskSearchEntity.getUserId() });
		}
		
		if(activeTaskSearchEntity.getOwnerId()!=null){
			mustMap.put("owner_id",
					new Object[] { activeTaskSearchEntity.getOwnerId() });
		}
		
		mustMap.put("image_status",
				new Object[] { new Integer(TASK_IMG_STATUS.USE.getCode()) });

		myRequest.setMustMap(mustMap);
		if (activeTaskSearchEntity.getBaseId() != null
				&& activeTaskSearchEntity.getBaseId() > 0) {
			mustNotMap.put("task_id",
					new Object[] { activeTaskSearchEntity.getBaseId() });
			
			myRequest.setMustNotMap(mustNotMap);
		}
		return myRequest;
	}
	
	private MyRequest getActiveTaskResourceAroundSearchRequest(ActiveTaskAroundSearchEntity activeTaskSearchEntity)throws Exception{
		MyRequest myRequest = new MyRequest();
		myRequest
				.setIndexNameArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME)
						.toString() });
		myRequest
				.setIndexTypeArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME_TYPE)
						.toString() });
		myRequest.setX(activeTaskSearchEntity.getX());
		myRequest.setY(activeTaskSearchEntity.getY());
		if(activeTaskSearchEntity.getNumber()!=null){
			myRequest.setNumber(activeTaskSearchEntity.getNumber());
		}
		myRequest.setRadius(activeTaskSearchEntity.getRadius());
		HashMap<String, Object[]> mustMap = new HashMap<String, Object[]>();
		HashMap<String, Object[]> mustNotMap = new HashMap<String, Object[]>();
		if (activeTaskSearchEntity.getClazzId() > 0) {
			mustMap.put("task_clazz_id",
					new Object[] { activeTaskSearchEntity.getClazzId() });
		}
		
		if(activeTaskSearchEntity.getOwnerId()!=null){
			mustMap.put("owner_id",
					new Object[] { activeTaskSearchEntity.getOwnerId() });
		}
		mustNotMap.put("status", new Object[] { RESOURCE_STATUS.UNUSE.getCode()});

		myRequest.setMustMap(mustMap);
		myRequest.setMustNotMap(mustNotMap);
		return myRequest;
	}
	
	private MyRequest getActiveH5TaskAroundSearchRequest(ActiveTaskAroundSearchEntity activeTaskSearchEntity)throws Exception{
		MyRequest myRequest = new MyRequest();
		myRequest
				.setIndexNameArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME)
						.toString() });
		myRequest
				.setIndexTypeArray(new String[] { propertiesConfig
						.getProperty(
								CommonConstant.COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME_TYPE)
						.toString() });
		myRequest.setX(activeTaskSearchEntity.getX());
		myRequest.setY(activeTaskSearchEntity.getY());
		if(activeTaskSearchEntity.getNumber()!=null){
			myRequest.setNumber(activeTaskSearchEntity.getNumber());
		}
		myRequest.setRadius(activeTaskSearchEntity.getRadius());
		HashMap<String, Object[]> mustMap = new HashMap<String, Object[]>();
		HashMap<String, Object[]> mustNotMap = new HashMap<String, Object[]>();
		if (activeTaskSearchEntity.getClazzId() > 0) {
			mustMap.put("task_clazz_id",
					new Object[] { activeTaskSearchEntity.getClazzId() });
		}
		if (activeTaskSearchEntity.getUserId() != null
				&& activeTaskSearchEntity.getUserId() > 0) {
			mustMap.put("collect_user_id",
					new Object[] { activeTaskSearchEntity.getUserId() });
		}
		
		if(activeTaskSearchEntity.getOwnerId()!=null){
			mustMap.put("owner_id",
					new Object[] { activeTaskSearchEntity.getOwnerId() });
		}
		if(activeTaskSearchEntity.getImageFlag()!=null&&!activeTaskSearchEntity.getImageFlag().equals("")){
			mustMap.put("image_flag",
					new Object[] { activeTaskSearchEntity.getImageFlag() });
		}
		
		if (activeTaskSearchEntity.getBatchId() != null
				&& activeTaskSearchEntity.getBatchId() > 0) {
			mustNotMap.put("image_batch_id",
					new Object[] { activeTaskSearchEntity.getBatchId() });
			
			myRequest.setMustNotMap(mustNotMap);
		}
//		if (activeTaskSearchEntity.getImageStatus() != null) {
//			mustNotMap.put("image_status",
//					new Object[] { TASK_IMG_STATUS.UNUSE.getCode() });
//			
//			myRequest.setMustNotMap(mustNotMap);
//		}
		mustNotMap.put("image_status",
				new Object[] { TASK_IMG_STATUS.UNUSE.getCode(),TASK_IMG_STATUS.FAIL.getCode(),TASK_IMG_STATUS.STATION.getCode() });
		myRequest.setMustMap(mustMap);
		return myRequest;
	}

	// /**
	// * 获取圈内的所有信息
	// *
	// * @author chunsheng.zhang
	// *
	// */
	// @Component
	// public class AroundNoUserTask implements
	// CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
	// @Autowired
	// public AroundNoUserTask(
	// ActiveTaskSearchManager collectPassiveTaskManager) {
	//
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public SearchPassiveTaskResultEntity execute(
	// SearchTaskQueryEntity searchTaskQueryEntity)
	// throws BusinessException {
	// MyRequest requestInfo = new MyRequest();
	// String adcode = searchTaskQueryEntity.getAdcode();
	// String indexName = null;
	// try {
	// indexName = (String) PropertiesConfigUtil
	// .getPropertiesConfigInstance().getProperty(
	// CommonConstant.COLLECT_NOUSER_INDEX_NAME);
	// } catch (BusinessException e) {
	// throw new BusinessRunException(e);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
	// }
	// requestInfo.setIndexNameArray(new String[] { indexName });
	//
	// requestInfo.setX(searchTaskQueryEntity.getX());
	// requestInfo.setY(searchTaskQueryEntity.getY());
	// requestInfo.setRange(searchTaskQueryEntity.getRange());
	// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
	// requestInfo.setPage(searchTaskQueryEntity.getPage());
	//
	// Map<String, String> sortMap = new HashMap<String, String>(1);
	// sortMap.put("location", "asc");
	// requestInfo.setSortMap(sortMap);
	//
	// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
	// customMap.put("task_package_status",
	// new Object[] { TASK_STATUS.UNALLOT.getCode() });
	//
	// if (null != adcode) {
	// customMap.put("adcode", new Object[] { adcode });
	// }
	//
	// if (null != searchTaskQueryEntity.getCustomMap()) {
	// customMap.putAll(searchTaskQueryEntity.getCustomMap());
	// }
	//
	// /*
	// * if(null != searchTaskQueryEntity.getUserId()) {
	// * customMap.put("b_allot_user_id", new
	// * Object[]{searchTaskQueryEntity.getUserId()}); }
	// */
	//
	// SearchResult searchResult = new SearchResult(0);
	// try {
	// searchResult = searchServiceImp.search(requestInfo);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
	// }
	//
	// return init(searchResult, searchTaskQueryEntity);
	// }
	//
	// }
	//
	// @Component
	// public class DistrictTask implements
	// CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
	// @Autowired
	// public DistrictTask(ActiveTaskSearchManager collectPassiveTaskManager) {
	//
	// }
	//
	// @Override
	// public SearchPassiveTaskResultEntity execute(
	// SearchTaskQueryEntity searchTaskQueryEntity)
	// throws BusinessException {
	// MyRequest requestInfo = new MyRequest();
	// String adcode = searchTaskQueryEntity.getAdcode();
	//
	// String indexName = null;
	// try {
	// indexName = (String) PropertiesConfigUtil
	// .getPropertiesConfigInstance().getProperty(
	// CommonConstant.COLLECT_INDEX_NAME);
	// } catch (BusinessException e) {
	// throw new BusinessRunException(e);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
	// }
	//
	// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
	// requestInfo.setPage(searchTaskQueryEntity.getPage());
	// requestInfo.setIndexNameArray(new String[] { indexName });
	//
	// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
	// customMap.put("task_package_status",
	// new Object[] { TASK_STATUS.ALLOT.getCode() });
	// customMap.put("b_status", new Object[] { "0" });
	//
	// if (null != adcode) {
	// customMap.put("adcode", new Object[] { adcode });
	// }
	//
	// if (null != searchTaskQueryEntity.getUserId()) {
	// customMap.put("b_allot_user_id",
	// new Object[] { searchTaskQueryEntity.getUserId() });
	// }
	//
	// SearchResult searchResult = new SearchResult(0);
	// try {
	// searchResult = searchServiceImp.search(requestInfo);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
	// }
	// // List<Map<String, Object>> resultList = searchResult.getList();
	// return init(searchResult, searchTaskQueryEntity);
	// }
	//
	// }
	//
	// @Component
	// public class DistrictNoUserTask implements
	// CollectCore<SearchPassiveTaskResultEntity, SearchTaskQueryEntity> {
	// @Autowired
	// public DistrictNoUserTask(
	// ActiveTaskSearchManager collectPassiveTaskManager) {
	//
	// }
	//
	// @Override
	// public SearchPassiveTaskResultEntity execute(
	// SearchTaskQueryEntity searchTaskQueryEntity)
	// throws BusinessException {
	// MyRequest requestInfo = new MyRequest();
	// String adcode = searchTaskQueryEntity.getAdcode();
	//
	// String indexName = null;
	// try {
	// indexName = (String) PropertiesConfigUtil
	// .getPropertiesConfigInstance().getProperty(
	// CommonConstant.COLLECT_NOUSER_INDEX_NAME);
	// } catch (BusinessException e) {
	// throw new BusinessRunException(e);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
	// }
	//
	// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
	// requestInfo.setPage(searchTaskQueryEntity.getPage());
	// requestInfo.setIndexNameArray(new String[] { indexName });
	//
	// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
	// customMap.put("task_package_status",
	// new Object[] { TASK_STATUS.UNALLOT.getCode() });
	// if (null != adcode) {
	// customMap.put("adcode", new Object[] { adcode });
	// }
	//
	// /*
	// * if(null != searchTaskQueryEntity.getUserId()) {
	// * customMap.put("b_allot_user_id", new
	// * Object[]{searchTaskQueryEntity.getUserId()}); }
	// */
	//
	// SearchResult searchResult = new SearchResult(0);
	// try {
	// searchResult = searchServiceImp.search(requestInfo);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
	// }
	// // List<Map<String, Object>> resultList = searchResult.getList();
	// return init(searchResult, searchTaskQueryEntity);
	// }
	//
	// }
	//
	// /***
	// * 统计
	// *
	// * @author chunsheng.zhang
	// *
	// */
	// @Component
	// public class DistrictFacetTask
	// implements
	// CollectCore<List<SearchPassiveTaskFacetResult>, SearchTaskQueryEntity> {
	// @Autowired
	// public DistrictFacetTask(
	// ActiveTaskSearchManager collectPassiveTaskManager) {
	//
	// }
	//
	// @Override
	// public List<SearchPassiveTaskFacetResult> execute(
	// SearchTaskQueryEntity searchTaskQueryEntity)
	// throws BusinessException {
	// MyRequest requestInfo = new MyRequest();
	// String adcode = searchTaskQueryEntity.getAdcode();
	//
	// String indexName = null;
	// try {
	// indexName = (String) PropertiesConfigUtil
	// .getPropertiesConfigInstance().getProperty(
	// CommonConstant.COLLECT_INDEX_NAME);
	// } catch (BusinessException e) {
	// throw new BusinessRunException(e);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
	// }
	//
	// if (null == searchTaskQueryEntity.getPage()) {
	// searchTaskQueryEntity.setPage(1);
	// }
	// if (null == searchTaskQueryEntity.getNumber()) {
	// searchTaskQueryEntity.setNumber(9999999);
	// }
	//
	// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
	//
	// requestInfo.setPage(searchTaskQueryEntity.getPage());
	// requestInfo.setIndexNameArray(new String[] { indexName });
	//
	// // requestInfo.setAdcode(adcode);
	// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
	// customMap.put("task_package_status",
	// new Object[] { TASK_STATUS.ALLOT.getCode() });
	// if (null != searchTaskQueryEntity.getUserId()) {
	// customMap.put("b_allot_user_id",
	// new Object[] { searchTaskQueryEntity.getUserId() });
	// }
	//
	// if (null == adcode || "".equals(adcode)) {
	// requestInfo.setGroupFieldArray(new String[] { "padcode" });
	// } else if (adcode.length() == 2) {
	// customMap.put("padcode", new String[] { adcode });
	// requestInfo.setGroupFieldArray(new String[] { "cadcode" });
	// } else if (adcode.length() == 4) {
	// customMap.put("cadcode", new String[] { adcode });
	// requestInfo.setGroupFieldArray(new String[] { "adcode" });
	// }
	//
	// SearchResult searchResult = new SearchResult(0);
	// try {
	// searchResult = searchServiceImp.search(requestInfo);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
	// }
	//
	// Map<String, Long> groupMap = searchResult.getGroupMap();
	//
	// List<SearchPassiveTaskFacetResult> searchPassiveTaskFacetResults = new
	// ArrayList<SearchPassiveTaskFacetResult>();
	//
	// SearchPassiveTaskFacetResult searchPassiveTaskFacetResult = null;
	// Set<Map.Entry<String, Long>> st = groupMap.entrySet();
	// Iterator<Map.Entry<String, Long>> itr = st.iterator();
	// while (itr.hasNext()) {
	// searchPassiveTaskFacetResult = new SearchPassiveTaskFacetResult();
	//
	// Map.Entry<String, Long> me = itr.next();
	// searchPassiveTaskFacetResult.setAdcode(me.getKey());
	// searchPassiveTaskFacetResult.setCount(me.getValue());
	//
	// searchPassiveTaskFacetResults.add(searchPassiveTaskFacetResult);
	// }
	//
	// return searchPassiveTaskFacetResults;
	// }
	//
	// }
	//
	// /***
	// * 统计
	// *
	// * @author chunsheng.zhang
	// *
	// */
	// @Component
	// public class DistrictFacetNoUserTask
	// implements
	// CollectCore<List<SearchPassiveTaskFacetResult>, SearchTaskQueryEntity> {
	// @Autowired
	// public DistrictFacetNoUserTask(
	// ActiveTaskSearchManager collectPassiveTaskManager) {
	//
	// }
	//
	// @Override
	// public List<SearchPassiveTaskFacetResult> execute(
	// SearchTaskQueryEntity searchTaskQueryEntity)
	// throws BusinessException {
	// MyRequest requestInfo = new MyRequest();
	// String adcode = searchTaskQueryEntity.getAdcode();
	//
	// String indexName = null;
	// try {
	// indexName = (String) PropertiesConfigUtil
	// .getPropertiesConfigInstance().getProperty(
	// CommonConstant.COLLECT_NOUSER_INDEX_NAME);
	// } catch (BusinessException e) {
	// throw new BusinessRunException(e);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_READ_PARAM_ERROR);
	// }
	//
	// if (null == searchTaskQueryEntity.getPage()) {
	// searchTaskQueryEntity.setPage(1);
	// }
	// if (null == searchTaskQueryEntity.getNumber()) {
	// searchTaskQueryEntity.setNumber(9999999);
	// }
	//
	// requestInfo.setNumber(searchTaskQueryEntity.getNumber());
	//
	// requestInfo.setPage(searchTaskQueryEntity.getPage());
	// requestInfo.setIndexNameArray(new String[] { indexName });
	//
	// // requestInfo.setAdcode(adcode);
	// HashMap<String, Object[]> customMap = requestInfo.getMustMap();
	// customMap.put("task_package_status",
	// new Object[] { TASK_STATUS.UNALLOT.getCode() });
	// /*
	// * if(null != searchTaskQueryEntity.getUserId()) {
	// * customMap.put("b_allot_user_id", new
	// * Object[]{searchTaskQueryEntity.getUserId()}); }
	// */
	//
	// if (null == adcode || "".equals(adcode)) {
	// requestInfo.setGroupFieldArray(new String[] { "padcode" });
	// } else if (adcode.length() == 2) {
	// customMap.put("padcode", new String[] { adcode });
	// requestInfo.setGroupFieldArray(new String[] { "cadcode" });
	// } else if (adcode.length() == 4) {
	// customMap.put("cadcode", new String[] { adcode });
	// requestInfo.setGroupFieldArray(new String[] { "adcode" });
	// }
	//
	// SearchResult searchResult = new SearchResult(0);
	// try {
	// searchResult = searchServiceImp.search(requestInfo);
	// } catch (Exception e) {
	// throw new BusinessRunException(
	// BusinessExceptionEnum.SEARCH_QUERY_ERROR);
	// }
	//
	// Map<String, Long> groupMap = searchResult.getGroupMap();
	//
	// List<SearchPassiveTaskFacetResult> searchPassiveTaskFacetResults = new
	// ArrayList<SearchPassiveTaskFacetResult>();
	//
	// SearchPassiveTaskFacetResult searchPassiveTaskFacetResult = null;
	// Set<Map.Entry<String, Long>> st = groupMap.entrySet();
	// Iterator<Map.Entry<String, Long>> itr = st.iterator();
	// while (itr.hasNext()) {
	// searchPassiveTaskFacetResult = new SearchPassiveTaskFacetResult();
	//
	// Map.Entry<String, Long> me = itr.next();
	// searchPassiveTaskFacetResult.setAdcode(me.getKey());
	// searchPassiveTaskFacetResult.setCount(me.getValue());
	//
	// searchPassiveTaskFacetResults.add(searchPassiveTaskFacetResult);
	// }
	//
	// return searchPassiveTaskFacetResults;
	// }
	//
	// }
	//
	// /**
	// * 构建CollectPassiveTaskEntity实体
	// *
	// * @param resultList
	// * @return
	// */
	// private SearchPassiveTaskResultEntity init(SearchResult searchResult,
	// SearchTaskQueryEntity searchTaskQueryEntity) {
	// String x = searchTaskQueryEntity.getX();
	// String y = searchTaskQueryEntity.getY();
	//
	// List<Map<String, Object>> resultList = searchResult.getList();
	// List<SearchPassiveTaskEntity> collectPassiveTaskEntitys = new
	// ArrayList<SearchPassiveTaskEntity>();
	// for (Map<String, Object> m : resultList) {
	// SearchPassiveTaskEntity collectPassiveTaskEntity = new
	// SearchPassiveTaskEntity();
	//
	// Double distance = null;
	//
	// // collect_task_base 表处理
	// Long id = Long.parseLong(m.get("aid").toString());
	// Long passive_package_id = convertLong(m.get("passive_package_id"));
	// String task_package_name = (String) m.get("task_package_name");
	// String task_package_desc = (String) m.get("task_package_desc");
	// Integer task_package_count = convertInteger(m
	// .get("task_package_count"));
	// Double task_package_pay = convertDouble(m.get("task_package_pay"));
	// Integer task_package_status = convertInteger(m
	// .get("task_package_status"));
	// Integer task_package_type = convertInteger(m
	// .get("task_package_type"));
	// Long create_time = convertLong(m.get("create_time"));
	// Long update_time = convertLong(m.get("update_time"));
	// Long a_allot_user_id = convertLong(m.get("a_allot_user_id"));
	// Long collect_user_id = convertLong(m.get("collect_user_id"));
	// Long allot_end_time = convertLong(m.get("allot_end_time"));
	// String task_package_cate = (String) m.get("task_package_cate");
	//
	// CollectBasePackage collectBasePackage = new CollectBasePackage();
	// collectBasePackage.setId(id);
	// collectBasePackage.setPassivePackageId(passive_package_id);
	// collectBasePackage.setTaskPackageName(task_package_name);
	// collectBasePackage.setTaskPackageDesc(task_package_desc);
	// collectBasePackage.setTaskPackageCount(task_package_count);
	// collectBasePackage.setTaskPackagePay(task_package_pay);
	// collectBasePackage.setTaskPackageStatus(task_package_status);
	// collectBasePackage.setTaskPackageType(task_package_type);
	// collectBasePackage.setCreateTime(create_time);
	// collectBasePackage.setUpdateTime(update_time);
	// collectBasePackage.setAllotUserId(a_allot_user_id);
	// collectBasePackage.setCollectUserId(collect_user_id);
	// collectBasePackage.setAllotEndTime(allot_end_time);
	// collectBasePackage.setTaskPackageCate(task_package_cate);
	//
	// // collect_allot_base_user 表处理
	// CollectAllotBaseUser collectAllotBaseUser = new CollectAllotBaseUser();
	// Long bid = convertLong(m.get("bid"));
	// Long b_package_id = convertLong(m.get("b_package_id"));
	// Long b_allot_user_id = convertLong(m.get("b_allot_user_id"));
	//
	// collectAllotBaseUser.setId(bid);
	// collectAllotBaseUser.setPackageId(b_package_id);
	// collectAllotBaseUser.setAllotUserId(b_allot_user_id);
	// collectAllotBaseUser.setStatus(convertInteger(m.get("b_status")));
	//
	// // collect_base_original_coordinate 表处理
	//
	// Long cid = convertLong(m.get("cid"));
	// Long passive_id = convertLong(m.get("passive_id"));
	// Double original_x = (Double) m.get("x");
	// Double original_y = (Double) m.get("y");
	// String adcode = (String) m.get("adcode");
	// String task_sample_img = (String) m.get("task_sample_img");
	// Integer coordinate_status = convertInteger(m
	// .get("coordinate_status"));
	// Long c_package_id = convertLong(m.get("c_package_id"));
	//
	// CollectOriginalCoordinate collectOriginalCoordinate = new
	// CollectOriginalCoordinate();
	// collectOriginalCoordinate.setId(cid);
	// collectOriginalCoordinate.setPassiveId(passive_id);
	// collectOriginalCoordinate.setOriginalX(original_x);
	// collectOriginalCoordinate.setOriginalY(original_y);
	// collectOriginalCoordinate.setOriginalAdcode(Integer
	// .parseInt(adcode));
	// collectOriginalCoordinate.setTaskSampleImg(task_sample_img);
	// collectOriginalCoordinate.setCoordinateStatus(coordinate_status);
	// collectOriginalCoordinate.setPackageId(c_package_id);
	//
	// if (StringUtils.hasText(x) && StringUtils.hasText(y)) {
	// distance = NodeTile.distance(Double.parseDouble(x),
	// Double.parseDouble(y), original_x, original_y);
	// collectPassiveTaskEntity.setDistance(distance);
	// }
	//
	// collectPassiveTaskEntity.setCollectBasePackage(collectBasePackage);
	// collectPassiveTaskEntity
	// .setCollectOriginalCoordinate(collectOriginalCoordinate);
	// collectPassiveTaskEntity
	// .setCollectAllotBaseUser(collectAllotBaseUser);
	//
	// collectPassiveTaskEntitys.add(collectPassiveTaskEntity);
	// }
	//
	// SearchPassiveTaskResultEntity searchPassiveTaskResultEntity = new
	// SearchPassiveTaskResultEntity();
	// searchPassiveTaskResultEntity
	// .setCollectPassiveTaskEntity(collectPassiveTaskEntitys);
	// searchPassiveTaskResultEntity.setCount(searchResult.getCount());
	// return searchPassiveTaskResultEntity;
	// }
	//
	// private Integer convertInteger(Object value) {
	// if (null != value) {
	// return Integer.parseInt(value.toString());
	// }
	// return null;
	// }
	//
	// private Long convertLong(Object value) {
	// if (null != value) {
	// return Long.parseLong(value.toString());
	// }
	// return null;
	// }
	//
	// private Double convertDouble(Object value) {
	// if (null != value) {
	// return Double.parseDouble(value.toString());
	// }
	// return null;
	// }

	// public DistrictTask getDistrictTask() {
	// return districtTask;
	// }
	//
	// public void setDistrictTask(DistrictTask districtTask) {
	// this.districtTask = districtTask;
	// }
	//
	// public DistrictFacetTask getDistrictFacetTask() {
	// return districtFacetTask;
	// }
	//
	// public void setDistrictFacetTask(DistrictFacetTask districtFacetTask) {
	// this.districtFacetTask = districtFacetTask;
	// }
	//
	// public AroundNoUserTask getAroundNoUserTask() {
	// return aroundNoUserTask;
	// }
	//
	// public void setAroundNoUserTask(AroundNoUserTask aroundNoUserTask) {
	// this.aroundNoUserTask = aroundNoUserTask;
	// }
	//
	// public DistrictNoUserTask getDistrictNoUserTask() {
	// return districtNoUserTask;
	// }
	//
	// public void setDistrictNoUserTask(DistrictNoUserTask districtNoUserTask)
	// {
	// this.districtNoUserTask = districtNoUserTask;
	// }
	//
	// public DistrictFacetNoUserTask getDistrictFacetNoUserTask() {
	// return districtFacetNoUserTask;
	// }
	//
	// public void setDistrictFacetNoUserTask(
	// DistrictFacetNoUserTask districtFacetNoUserTask) {
	// this.districtFacetNoUserTask = districtFacetNoUserTask;
	// }

}
