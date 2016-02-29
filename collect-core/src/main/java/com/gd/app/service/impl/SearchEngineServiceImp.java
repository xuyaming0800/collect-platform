package com.gd.app.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.entity.TaskLockResultEntity;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.exception.SearchException;
import com.gd.app.util.SysProps;
import com.mapabc.search.declare.SearchResult;
import com.mapabc.search.implement.SearchServiceImp;
import com.mapabc.search.model.RequestInfo;
@Service
public class SearchEngineServiceImp {
	private Logger logger = LogManager.getLogger(this.getClass());
    @Resource
	private SearchServiceImp searchService = null;
	private double repeated_fazhi = 0.8; // 排重结果得分伐值
	private String defaultSearchIndexName = "addresscollect_new"; // 默认搜索引擎 索引名称
	private int defaultSearchRange = 500; // 周边搜索默认搜索范围
	private String auditbackSearchIndexName = "auditback_new";
	
	public SearchEngineServiceImp()throws Exception{
		this.repeated_fazhi=new Double(PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.REPEATED_FAZHI).toString());
		this.defaultSearchIndexName=PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.SEARCHINDEX_NAME).toString();
		this.defaultSearchRange=new Integer(PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.DEFAULT_SEARCH_RANGE).toString());
		this.auditbackSearchIndexName=PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.AUDITBACK_SEARCH_INDEXNAME).toString();
	}
	
	public void setAuditbackSearchIndexName(String auditbackSearchIndexName) {
		this.auditbackSearchIndexName = auditbackSearchIndexName;
	}

	public void setRepeated_fazhi(double repeated_fazhi) {
		this.repeated_fazhi = repeated_fazhi;
	}

	public void setDefaultSearchIndexName(String defaultSearchIndexName) {
		this.defaultSearchIndexName = defaultSearchIndexName;
	}

	public void setDefaultSearchRange(int defaultSearchRange) {
		this.defaultSearchRange = defaultSearchRange;
	}

	public void setSearchService(SearchServiceImp searchService) {
		this.searchService = searchService;
	}

	public static void main(String[] args) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", 1L);
		map.put("search_time", 22L);
		map.put("status", 1);

		List<String> l = new ArrayList<String>();
		l.add("hsp");
		map.put("list", l);

		SearchResult rs = map2SearchResult(map);

		System.out.println(rs.getCount() + "  " + rs.getSearch_time() + "  "
				+ rs.getList() + "  " + rs.getStatus());
	}

	/***
	 * 内部封装调用搜索引擎,主要用于完整查询记录（被同名方法调用）
	 * 
	 * @param requestInfo
	 * @return
	 */
	List<Map<String, Object>> findMatchDataFromSearchEngine(
			RequestInfo requestInfo) {

		SearchResult searchResult = null;
		try {
			requestInfo.setRange(defaultSearchRange);
			String[] prefixField = { "adcode" };
			requestInfo.setPrefix_fields(prefixField);
			searchResult = searchService.searchData(requestInfo);
		} catch (Exception e) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
		logger.info("子索引["+requestInfo.getDataType()+"],find match 返回的状态码："+(searchResult==null?"异常":searchResult.getStatus()));
		if (searchResult == null || searchResult.getStatus() != 0) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}

		List<Map<String, Object>> list = searchResult.getList();
		logger.info("子索引["+requestInfo.getDataType()+"],通过find match方法返回的结果："+list);
		if(list==null||list.size()==0){
			return list;
		}
		List<Map<String, Object>> tmpList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> m : list){
			if(getSearchDataScore(m)>=repeated_fazhi){
				tmpList.add(m);
			}
		}
		list.clear();
		logger.info("子索引["+requestInfo.getDataType()+"],通过find match过滤后返回的结果："+tmpList);
		return tmpList;
	}	

	/***
	 * 内部封装调用搜索引擎,主要用于完整查询记录（被同名方法调用）
	 * 
	 * @param requestInfo
	 * @return
	 */
	List<Map<String, Object>> findDataFromSearchEngine(
			RequestInfo requestInfo) {

		SearchResult searchResult = null;
		Map<String,Object> m=new HashMap<String,Object>();
		try {
			String[] prefixField = { "adcode" };
			requestInfo.setPrefix_fields(prefixField);
			long st = new Date().getTime();
			searchResult = searchService.searchData(requestInfo);
			m.put("total", searchResult.getCount());
			logger.info("查询时间="+(new Date().getTime()-st)+"毫秒!"+searchResult.getCount());
		} catch (Exception e) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
		logger.info("子索引["+requestInfo.getDataType()+"],find 返回的状态码："+(searchResult==null?"异常":searchResult.getStatus()));
		if (searchResult == null || searchResult.getStatus() != 0) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}

		List<Map<String, Object>> list = searchResult.getList();
		logger.info("子索引["+requestInfo.getDataType()+"],通过find方法返回的结果数："+(list==null?0:list.size()));
		if(m.size()>0){
			list.add(m);
		}
		return list;
	}

	/***
	 * 在搜索引擎中根据dataname(regDataname) 分词匹配记录。内部封装调用搜索引擎（被同名方法调用）
	 * 
	 * @param requestInfo
	 * @return
	 */
	Map<String, Object> matchDataFromSearchEngine(
			RequestInfo requestInfo) {
		logger.info("子索引["+requestInfo.getDataType()+"],参与搜索引擎的参数 ，adcode=[" + requestInfo.getAdcode()
				+ "]，customMap=[" + requestInfo.getCustomMap() + "]，uneqMap=["
				+ requestInfo.getUnequalMap() + "]，keyword=["
				+ requestInfo.getKeyword() + "],indexName=["+requestInfo.getIndexName()+"]");

		SearchResult searchResult = null;
		try {
			String[] prefixField = { "adcode" };
			requestInfo.setPrefix_fields(prefixField);
			
			/**临时去掉使用经纬度周边搜索*/
			requestInfo.setRange(defaultSearchRange);
//			requestInfo.setX(null);
//			requestInfo.setY(null);
			
			long st = new Date().getTime();
			searchResult = searchService.searchData(requestInfo);
			logger.info("子索引["+requestInfo.getDataType()+"],本次搜索耗时  " + (new Date().getTime() - st) + " 毫秒.结果状态: "+(searchResult==null?"异常":searchResult.getStatus()));
		} catch (Exception e) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
//		logger.info("搜索引擎返回状态码："+searchResult.getStatus());
		if (searchResult == null || searchResult.getStatus() != 0) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}

		List<Map<String, Object>> rsList = searchResult.getList();
		logger.info("子索引["+requestInfo.getDataType()+"],搜索引擎返回的数据===" + rsList);
		if (rsList == null || rsList.size() == 0) {
			// throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
			return null;
		}

		Map<String, Object> rsMap = rsList.get(0);

		/** 判断返回数据的分值，小于指定的值，则认为没有查询到数据 */
		double score = getSearchDataScore(rsMap);
		if (score >= repeated_fazhi) {
			return rsList.get(0);
		}

		return null;
	}
	
	/***
	 * 基于搜索引擎，搜索agent_task_base 得分大于指定值的采集数据
	 * 
	 * @param paraMap
	 * @return
	 * @throws SearchException
	 */
	public List<Map<String, Object>> findMatchAgentBaseDataFromSearchEngine(
			Map<String, Object> paraMap)throws SearchException {

		logger.info("子索引[agent_task_base],参与find match 搜索的参数:"+paraMap);
		return this.findMatchDataFromSearchEngine(paraMap, "agent_task_base");
	}

	/***
	 * 基于搜索引擎，搜索agent_task_base 所有符合条件的数据
	 * 
	 * @param paraMap
	 * @return
	 * @throws SearchException
	 */
	public List<Map<String, Object>> findTaskAllDataFromSearchEngine(
			Map<String, Object> paraMap)throws SearchException {

		logger.info("子索引[task_all],参与find 查询的参数:"+paraMap);

		return this.findDataFromSearchEngine(paraMap, "task_all");
	}
	public List<Map<String, Object>> findMainDataFromSearchEngine(
			Map<String, Object> paraMap)throws SearchException {

		logger.info("子索引[agent_main_address],参与find 查询的参数:"+paraMap);

		return this.findDataFromSearchEngine(paraMap, "streetno");
	}
	
	public List<Map<String, Object>> findAutoTaskDataFromSearchEngine(
			Map<String, Object> paraMap)throws SearchException {
		String dataType = "agent_task_base_new";
		String adcode = paraMap.get("adcode").toString();
		String subIndexName = getPartitionIndexName(dataType, adcode);

		logger.info("子索引["+subIndexName+"],参与find 查询的参数:"+paraMap);

		return this.findDataFromSearchEngine(paraMap, subIndexName);
	}
	
	
	/***
	 * 基于搜索引擎，搜索task_all 得分大于指定值的数据
	 * 
	 * @param paraMap
	 * @return
	 * @throws SearchException
	 */
	public List<Map<String, Object>> findMatchTaskAllDataFromSearchEngine(
			Map<String, Object> paraMap)throws SearchException {

		logger.info("子索引[task_all],参与find match 搜索的参数:"+paraMap);
		return this.findMatchDataFromSearchEngine(paraMap, "task_all");
	}

	/**
	 * 基于搜索引擎，搜索高德已有的门址得分大于指定值的数据
	 * 
	 * @param paraMap
	 * @return
	 * @throws SearchException
	 */
	public List<Map<String, Object>> findMatchGDBaseDataFromSearchEngine(
			Map<String, Object> paraMap)
 throws SearchException {

		logger.info("子索引[agent_main_address],参与find match 搜索的参数:"+paraMap);
		return this.findMatchDataFromSearchEngine(paraMap, "agent_main_address");
	}

	/**
	 * 对接搜索引擎入口方法
	 * 
	 * @param paraMap
	 *            需要查询的字段信息
	 * @param dataType
	 *            数据类型（主要定义需要查询那些表中数据，数据值为表名）
	 * @throws SearchException
	 */
	List<Map<String, Object>> findMatchDataFromSearchEngine(
			Map<String, Object> paraMap, String dataType)
			throws SearchException {

		try {		
			RequestInfo requestInfo = map2RequestInfo(paraMap);
			requestInfo.setIndexName(defaultSearchIndexName);

			String subIndexname = getPartitionIndexName(dataType,paraMap.get("adcode").toString());
			requestInfo.setDataType(subIndexname);

			return this.findMatchDataFromSearchEngine(requestInfo);
		} catch (SearchException e) {
			throw e;
		}
	}
	
	/**
	 * 对接搜索引擎入口方法
	 * 
	 * @param paraMap
	 *            需要查询的字段信息
	 * @param dataType
	 *            数据类型（主要定义需要查询那些表中数据，数据值为表名）
	 * @throws SearchException
	 */
	List<Map<String, Object>> findDataFromSearchEngine(
			Map<String, Object> paraMap, String dataType)
			throws SearchException {

		if(!paraMap.containsKey("range")){
			paraMap.put("range", defaultSearchRange);
		}
		
		RequestInfo requestInfo = map2RequestInfo(paraMap);
		requestInfo.setIndexName(defaultSearchIndexName);
	    requestInfo.setDataType(dataType);
		try {
			return this.findDataFromSearchEngine(requestInfo);
		} catch (SearchException e) {
			throw e;
		}
	}
	
	
	Map<String, Object> matchDataFromSearchEngine(Map<String, Object> paraMap,
			String dataType) throws SearchException {

		RequestInfo requestInfo = map2RequestInfo(paraMap);
		requestInfo.setIndexName(defaultSearchIndexName);
		// String dataName = paraMap.containsKey("data_name") ? paraMap.get(
		// "data_name").toString() : "";
		// try {
		// requestInfo.setKeyword(new String(dataName.getBytes(), "GBK"));
		// } catch (UnsupportedEncodingException e1) {
		// // TODO Auto-generated catch block
		// throw new SearchException(SearchExceptionEnum.DATA_ENCODE_ERROR);
		// }
		requestInfo.setDataType(dataType);

		try {
			return this.matchDataFromSearchEngine(requestInfo);
		} catch (SearchException e) {
			throw e;
		}
	}

	public Map<String, Object> matchAgentBaseDataFromSearchEngine(
			Map<String, Object> paraMap) {
		String dataType = "agent_task_base";
		String adcode = paraMap.get("adcode").toString();
		String subIndexName = getPartitionIndexName(dataType, adcode);
		return this.matchDataFromSearchEngine(paraMap, subIndexName);
	}
	
	String getPartitionIndexName(String dataType,String adcode){
		
		return dataType+"_"+adcode.substring(0, 2);
		
	}
	
	public Map<String, Object> matchTempDataFromSearchEngine(
			Map<String, Object> paraMap) {

		return this.matchDataFromSearchEngine(paraMap, "temp");
	}

	public Map<String, Object> matchExistDataFromSearchEngine(
			Map<String, Object> paraMap) {

		return this.matchDataFromSearchEngine(paraMap,
				"agent_task_base,agent_main_address");
	}

	public Map<String, Object> matchGDBaseDataFromSearchEngine(
			Map<String, Object> paraMap) {

		return this.matchDataFromSearchEngine(paraMap, "agent_main_address");
	}

	public Map<String, Object> matchTaskAllDataFromSearchEngine(
			Map<String, Object> paraMap) throws SearchException {

		return this.matchDataFromSearchEngine(paraMap, "task_all_new");
	}

	/***
	 * 更新搜索引擎索引数据
	 * 
	 * @param map
	 */
	public Boolean updateOrInsertSearchIndex(Map<String, Object> map,boolean isUpdate)throws SearchException {

		Boolean rs = false;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		map.put("_id", map.containsKey("id") ? map.get("id").toString() : "");
		
		Map<String, Object>rsMap = new HashMap<String, Object>();
		/*如果是更新，需要先从搜索引擎中加载出其他的属性信息*/
		if(isUpdate){
			RequestInfo requestInfo = new RequestInfo();
			
			HashMap<String, Object[]> customMap = new HashMap<String, Object[]>();
			customMap.put("_id", new Object[]{map.get("_id")});
			requestInfo.setCustomMap(customMap);
			requestInfo.setDataType(map.get("data_type").toString());
			requestInfo.setIndexName(defaultSearchIndexName);
			
			rsMap = this.matchDataFromSearchEngine(requestInfo);
			if(rsMap==null||rsMap.size()==0){
				logger.info("子索引[ "+requestInfo.getDataType()+" ]搜索引擎中未发现 主键：["+map.get("_id")+"]的记录 ，更新的数据["+map+"]，更新失败");
				return false;
			}
		}
		rsMap.putAll(map);
		if(!rsMap.containsKey("location")){
			if(rsMap.get("offset_x")!=null){
				rsMap.put("location", rsMap.get("offset_y")+","+rsMap.get("offset_x"));
			}
		}
		logger.info("子索引["+map.get("data_type").toString()+"] 更新索引数据,参数信息：" + rsMap);
		list.add(rsMap);
		
		try{
			rs = searchService.bulkUpdate(defaultSearchIndexName,map.get("data_type").toString(), list);
			logger.info("子索引["+map.get("data_type").toString()+"] +数据：["+list+"]，搜索引擎索引数据更新结果：" + rs);
		} catch (Exception e) {
			logger.error("子索引["+map.get("data_type").toString()+"],更新搜索引擎索引数据时异常 ", e);
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
		return rs;
	}

	/***
	 * 从搜索引擎索引中删除相关数据
	 * 
	 * @param dataType
	 * @param list
	 * @return
	 */
	public Boolean removeDataFromSearchIndex(String dataType, List<String> list) {

		Boolean rs = false;
		try {
			rs = searchService.bulkDelete(defaultSearchIndexName, dataType,
					list);
			logger.info("搜索引擎索引数据删除结果：" + rs);
		} catch (Exception e) {
			logger.error("删除搜索引擎索引数据时异常 ", e);
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
		return rs;
	}

	/***
	 * 获取搜索引擎返回的 数据中，匹配结果得分
	 * 
	 * @param map
	 *            搜索引擎返回的结果数据（map只有一条数据，默认只取搜索引擎返回的第一条数据）
	 * @return
	 */
	public static double getSearchDataScore(Map<String, Object> map) {

		double score = -0.0;
		if (map == null || map.size() == 0) {
			return score;
		}
		score = map.get("sim_score") == null ? score : Double.parseDouble(map.get(
				"sim_score").toString());
		return score;
	}

	/**
	 * 动态把map的值，反射为RequestInfo对象的值
	 * 
	 * @param map
	 * @return
	 */
	public static RequestInfo map2RequestInfo(Map<String, Object> map) {

		RequestInfo rs = new RequestInfo();
		if (map == null || map.size() == 0) {
			return null;
		}

		Class<?> rsClass = rs.getClass();
		Field field = null;

		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();

			try {
				field = rsClass.getDeclaredField(entry.getKey());
				field.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				throw new SearchException(
						AppExceptionEnum.ANALYZ_SEARCH_RESULT_ERROR);
			}

			try {
				field.set(rs, entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				throw new SearchException(
						AppExceptionEnum.ANALYZ_SEARCH_RESULT_ERROR);
			}
		}
		return rs;
	}
	/**
	 * 动态把map的值，反射为SearchResult对象的值
	 * 
	 * @param map
	 * @return
	 */
	public static SearchResult map2SearchResult(Map<String, Object> map) {

		SearchResult rs = new SearchResult();
		if (map == null || map.size() == 0) {
			return null;
		}

		Class<?> rsClass = rs.getClass();
		Field field = null;

		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();

			try {
				field = rsClass.getDeclaredField(entry.getKey());
				field.setAccessible(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new SearchException(
						AppExceptionEnum.ANALYZ_SEARCH_RESULT_ERROR);
			}

			try {
				field.set(rs, entry.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new SearchException(
						AppExceptionEnum.ANALYZ_SEARCH_RESULT_ERROR);
			}
		}
		return rs;
	}

	public static TaskLockResultEntity searchRs2TaskLockResult(
			Map<String, Object> rsMap) {

		return null;
	}
	/**
	 * 从审核回传中匹配数据
	 * 
	 * @param paraMap
	 * @return
	 */
	public Map<String, Object> matchAuditbackDataFromSearchEngine(
			Map<String, Object> paraMap) {

		String dataType = "auditback_new";
		String adcode = paraMap.get("adcode").toString();
		String subIndexName = getPartitionIndexName(dataType, adcode);
		RequestInfo requestInfo = map2RequestInfo(paraMap);
		requestInfo.setDataType(subIndexName);
		requestInfo.setIndexName(auditbackSearchIndexName);
		
		try {
			return this.matchAuditbackDataFromSearchEngine(requestInfo);
		} catch (SearchException e) {
			throw e;
		}
	}
	
	/***
	 * 在搜索引擎中根据dataname(regDataname) 分词匹配记录。内部封装调用搜索引擎（被同名方法调用）
	 * 
	 * @param requestInfo
	 * @return
	 */
	Map<String, Object> matchAuditbackDataFromSearchEngine(
			RequestInfo requestInfo) {
		logger.info("子索引["+requestInfo.getDataType()+"],参与搜索引擎计算的参数 ，adcode=[" + requestInfo.getAdcode()+ "],customMap=[" + requestInfo.getCustomMap() + "],"
				+ "uneqMap=["+ requestInfo.getUnequalMap() + "],keyword=["+ requestInfo.getKeyword() + "]");

		SearchResult searchResult = null;
		try {
			String[] prefixField = { "adcode" };
			requestInfo.setPrefix_fields(prefixField);
			requestInfo.setRange(defaultSearchRange);
			
			long st = new Date().getTime();
			searchResult = searchService.searchData(requestInfo);
			logger.info("子索引["+requestInfo.getDataType()+"],本次搜索耗时  " + (new Date().getTime() - st) + " 毫秒 . 结果状态:"+(searchResult==null?"异常":searchResult.getStatus()));
		} catch (Exception e) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}
		
		if (searchResult == null || searchResult.getStatus() != 0) {
			throw new SearchException(AppExceptionEnum.SEARCH_ENGINE_ERROR);
		}

		List<Map<String, Object>> rsList = searchResult.getList();
		logger.info("子索引[auditback],搜索引擎返回的数据===" + rsList);
		if (rsList == null || rsList.size() == 0) {
			return null;
		}

		Map<String, Object> rsMap = rsList.get(0);

		/** 判断返回数据的分值，小于指定的值，则认为没有查询到数据 */
		double score = getSearchDataScore(rsMap);
		logger.info("keyword=[" + requestInfo.getKeyword() + "],adcode=["
				+ requestInfo.getAdcode() + "]。匹配结果得分: " + score);
		if (score >= repeated_fazhi) {
			return rsList.get(0);
		}

		return null;
	}
}
