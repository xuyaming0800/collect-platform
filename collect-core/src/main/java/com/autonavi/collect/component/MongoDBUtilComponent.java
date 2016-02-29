package com.autonavi.collect.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * MongoDB封装方法
 * 
 * @author xuyaming
 *
 */
@Component
public class MongoDBUtilComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private MongoTemplate mongoTemplate;

	private enum JSON_STYLE {
		MAP(0), ARRAY(1), STRING(2);
		private int code;

		public int getCode() {
			return code;
		}

		private JSON_STYLE(int code) {
			this.code = code;
		}
	}

	/**
	 * 插入mongodb
	 * 
	 * @param tableName
	 * @param insertMap
	 */
	@SuppressWarnings("unchecked")
	public void insertObject(String tableName, String json)
			throws BusinessException {
		try {
			Integer style = this.verifyJsonStyle(json);
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			Map<String, Object> insertMap = null;
			if (style.equals(JSON_STYLE.MAP.getCode())) {
				insertMap = binder.fromJson(json, Map.class, binder
						.getCollectionType(Map.class, String.class,
								Object.class));
			} else if (style.equals(JSON_STYLE.ARRAY.getCode())) {
				logger.error("插入时候的JSON串必须是JSON格式");
				throw new BusinessException(
						BusinessExceptionEnum.MONGONDB_INSERT_ERROR);
			}
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject insert = new BasicDBObject();
			if (insertMap != null)
				insert.putAll(insertMap);
			dbcoll.insert(insert);
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[INSERT] " + json);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_INSERT_ERROR);

		}
	}

	/**
	 * 插入mongodb
	 * 
	 * @param tableName
	 * @param insertMap
	 * @throws BusinessException
	 */
	public void insertObject(String tableName, Map<String, Object> insertMap)
			throws BusinessException {
		try {
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject insert = new BasicDBObject();
			if (insertMap != null)
				insert.putAll(insertMap);
			dbcoll.insert(insert);
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[INSERT] " + binder.toJson(insertMap));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_INSERT_ERROR);

		}
	}
	/**
	 * 查询mongodb
	 * @param tableName
	 * @param selectQueryMap
	 * @param selectlocates
	 * @param isReturnId
	 * @return
	 * @throws BusinessException
	 */
	public List<Object> selectObjectMultiProjection(String tableName,
			Map<String, Object> selectQueryMap, String[] selectlocates,
			boolean isReturnId) throws BusinessException {
		try {
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject query = new BasicDBObject();
			query.putAll(selectQueryMap);
			DBCursor dBCursor = null;
			List<Object> result = new ArrayList<Object>();
			if (selectlocates != null&&selectlocates.length>0) {
				BasicDBObject projection = new BasicDBObject();
				for(String selectlocate:selectlocates){
					projection.put(selectlocate, 1);
				}
				if (!isReturnId) {
					projection.put("_id", 0);
				}
				dBCursor = dbcoll.find(query, projection);
			} else {
				dBCursor = dbcoll.find(query);
			}
			for (DBObject _obj : dBCursor) {
				if(_obj.toMap().size()>0)
				result.add(_obj);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_QUERY_ERROR);

		}
	}

	/**
	 * 查询mongodb
	 * 
	 * @param tableName
	 * @param selectQueryMap
	 * @param selectlocate
	 * @param isReturnId
	 * @return
	 * @throws BusinessException
	 */
	public List<Object> selectObject(String tableName,
			Map<String, Object> selectQueryMap, String selectlocate,
			boolean isReturnId) throws BusinessException {
		try {
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject query = new BasicDBObject();
			query.putAll(selectQueryMap);
			DBCursor dBCursor = null;
			List<Object> result = new ArrayList<Object>();
			if (selectlocate != null) {
				BasicDBObject projection = new BasicDBObject();
				projection.put(selectlocate, 1);
				if (!isReturnId) {
					projection.put("_id", 0);
				}
				dBCursor = dbcoll.find(query, projection);
			} else {
				dBCursor = dbcoll.find(query);
			}
			for (DBObject _obj : dBCursor) {
				if(_obj.toMap().size()>0)
				result.add(_obj);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_QUERY_ERROR);

		}
	}
	/**
	 * 
	 * @param tableName
	 * @param updateQueryMap
	 * @param updatelocate
	 * @param updatejsonObject
	 * @param isDlc
	 * @param isNewListItem 是否是新数组元素 仅对数组元素对象起作用
	 * @throws BusinessException
	 */
	public void updateArrayObject(String tableName,
			Map<String, Object> updateQueryMap, String updatelocate,
			Object updatejsonObject, boolean isDlc,Boolean isNewListItem) throws BusinessException {
		try {
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			String queryJson = binder.toJson(updateQueryMap);
			String updateJson = binder.toJson(updatejsonObject);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(updateQueryMap);
			BasicDBObject update = new BasicDBObject();
			if (!isDlc) {
				// 非追加模式 先清除
				update.put(updatelocate, new BasicDBObject("$exists", true));
				dbcoll.updateMulti(updateQuery, new BasicDBObject("$unset",
						update));
			}
			update.put(updatelocate, updatejsonObject);
			if(!(updatejsonObject instanceof List)){
				dbcoll.updateMulti(updateQuery, new BasicDBObject("$push",
						update));
			}else{
				if(isNewListItem){
					dbcoll.updateMulti(updateQuery, new BasicDBObject("$push",
							update));
				}else{
					dbcoll.updateMulti(updateQuery, new BasicDBObject("$pushAll",
							update));
				}
				
			}
			
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[UPDATE_QUERY] " + queryJson + " [UPDATE_LOCATE] "
					+ updatelocate + " [UPDATE] " + updateJson);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_UPDATE_ERROR);
		}
	}

	public void updateCommonObject(String tableName,
			Map<String, Object> updateQueryMap, String updatelocate,
			Object updateMap) throws BusinessException {

		try {
			String updatejson = "";
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			updatejson = binder.toJson(updateMap);
			String queryJson = binder.toJson(updateQueryMap);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(updateQueryMap);
			BasicDBObject update = new BasicDBObject();
			// JSON形式 直接set
			update.put(updatelocate, updateMap);
			dbcoll.updateMulti(updateQuery, new BasicDBObject("$set", update));

			// 记录日志 供万一写数据失败 追回使用
			logger.info("[UPDATE_QUERY] " + queryJson + " [UPDATE_LOCATE] "
					+ updatelocate + " [UPDATE] " + updatejson);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_UPDATE_ERROR);
		}

	}

	public void batchUpdateCommonObject(String tableName,
			Map<String, Object> updateQueryMap, Map<String, Object> updateMap)
			throws BusinessException {

		try {
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(updateQueryMap);
			BasicDBObject update = new BasicDBObject();
			
			// JSON形式 直接set
			update.putAll(updateMap);
			dbcoll.updateMulti(updateQuery, new BasicDBObject("$set", update));

			// 记录日志 供万一写数据失败 追回使用
			for (String key : updateMap.keySet()) {
				String updatejson = "";
				JsonBinder binder = JsonBinder.buildNormalBinder(false);
				updatejson = binder.toJson(updateMap.get(key));
				String queryJson = binder.toJson(updateQueryMap);
				logger.info("[UPDATE_QUERY] " + queryJson + " [UPDATE_LOCATE] "
						+ key + " [UPDATE] " + updatejson);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_UPDATE_ERROR);
		}

	}
	
	public void batchUpdateCommonObject(String tableName,
			Map<String, Object> updateQueryMap, Map<String, Object> updateMap,String createLocate,Map<String, Object> creatQueryMap,Object obj)
			throws BusinessException {

		try {
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(updateQueryMap);
			BasicDBObject update = new BasicDBObject();
			
			if(createLocate!=null){
				//没有则直接创建路径
				if(!this.checkObjectIsExist(tableName, updateQueryMap)){
					this.updateArrayObject(tableName, creatQueryMap, createLocate, obj, true, false);
				}
			}
			
			// JSON形式 直接set
			update.putAll(updateMap);
			dbcoll.updateMulti(updateQuery, new BasicDBObject("$set", update));

			// 记录日志 供万一写数据失败 追回使用
			for (String key : updateMap.keySet()) {
				String updatejson = "";
				JsonBinder binder = JsonBinder.buildNormalBinder(false);
				updatejson = binder.toJson(updateMap.get(key));
				String queryJson = binder.toJson(updateQueryMap);
				logger.info("[UPDATE_QUERY] " + queryJson + " [UPDATE_LOCATE] "
						+ key + " [UPDATE] " + updatejson);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_UPDATE_ERROR);
		}

	}

	public void deleteCommonObject(String tableName,
			Map<String, Object> deleteQueryMap, String deletelocate)
			throws BusinessException {

		try {
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			String queryJson = binder.toJson(deleteQueryMap);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(deleteQueryMap);
			BasicDBObject delete = new BasicDBObject();
			delete.put(deletelocate, new BasicDBObject("$exists", true));
			dbcoll.updateMulti(updateQuery, new BasicDBObject("$unset", delete));
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[DELETE_QUERY] " + queryJson + " [DELETE_LOCATE] "
					+ deletelocate);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_DELETE_ERROR);
		}

	}
	
	public void removeCommonObject(String tableName,
			Map<String, Object> deleteQueryMap)
			throws BusinessException {

		try {
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			String queryJson = binder.toJson(deleteQueryMap);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(deleteQueryMap);
			dbcoll.remove(updateQuery);
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[REMOVE_QUERY] " + queryJson );
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_DELETE_ERROR);
		}

	}
	public Boolean checkPropertyIsExist(String tableName,Map<String, Object> queryMap,String checkLocate){
		DBCollection dbcoll = mongoTemplate.getDb()
				.getCollection(tableName);
		BasicDBObject selectQuery = new BasicDBObject();
		queryMap.put(checkLocate, new BasicDBObject("$exists", true));
		selectQuery.putAll(queryMap);
		if(dbcoll.findOne(selectQuery)!=null){
			return true;
		}else{
			return false;
		}
		
	}
	
	public Boolean checkObjectIsExist(String tableName,Map<String, Object> queryMap){
		DBCollection dbcoll = mongoTemplate.getDb()
				.getCollection(tableName);
		BasicDBObject selectQuery = new BasicDBObject();
		selectQuery.putAll(queryMap);
		if(dbcoll.findOne(selectQuery)!=null){
			return true;
		}else{
			return false;
		}
		
	}
	public Boolean checkObjectIsExist(String tableName,List<Map<String, Object>> queryMapList){
		DBCollection dbcoll = mongoTemplate.getDb()
				.getCollection(tableName);
		BasicDBObject selectQuery = new BasicDBObject();
		selectQuery.put("$or",queryMapList);
		if(dbcoll.findOne(selectQuery)!=null){
			return true;
		}else{
			return false;
		}
		
	}
	
	
    /**
     * 
     * @param tableName
     * @param deleteQueryMap
     * @param deletelocate
     * @param deleteArray
     * @param isNewListItem 是否是新数组元素 仅对数组元素对象起作用
     * @throws BusinessException
     */
	public void deleteArrayObject(String tableName,
			Map<String, Object> deleteQueryMap, String deletelocate,
			Object deleteArray,Boolean isNewListItem) throws BusinessException {

		try {
			JsonBinder binder = JsonBinder.buildNormalBinder(false);
			String queryJson = binder.toJson(deleteQueryMap);
			DBCollection dbcoll = mongoTemplate.getDb()
					.getCollection(tableName);
			BasicDBObject updateQuery = new BasicDBObject();
			updateQuery.putAll(deleteQueryMap);
			BasicDBObject delete = new BasicDBObject();
			delete.put(deletelocate, deleteArray);
			if(!(deleteArray instanceof List)){
				dbcoll.updateMulti(updateQuery, new BasicDBObject("$pull",
						delete));

			}
			else{
				if(isNewListItem){
					dbcoll.updateMulti(updateQuery, new BasicDBObject("$pull",
							delete));
				}else{
					dbcoll.updateMulti(updateQuery, new BasicDBObject("$pullAll",
							delete));
				}
				
			}
			// 记录日志 供万一写数据失败 追回使用
			logger.info("[DELETE_QUERY] " + queryJson + " [DELETE_LOCATE] "
					+ deletelocate + " [DELETE_ARRAY] " + deleteArray);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessExceptionEnum.MONGONDB_DELETE_ERROR);
		}

	}

	private Integer verifyJsonStyle(String json) {
		if (json.indexOf("{") == 0) {
			return JSON_STYLE.MAP.getCode();
		} else if (json.indexOf("[") == 0) {
			return JSON_STYLE.ARRAY.getCode();
		} else {
			return JSON_STYLE.STRING.getCode();
		}
	}
	/**
	 * 将mongodb的对象转为通用对象
	 * @param obj
	 * @return
	 */
//	@SuppressWarnings("unchecked")
	public Object transferMongoObjectoCommon(Object obj){
//		if(obj instanceof BasicDBObject){
//			Map<Object,Object> map=((BasicDBObject)obj).toMap();
//			for(Object key:map.keySet()){
//				Object value=map.get(key);
//				value=transferMongoObjectoCommon(value);
//				map.put(key, value);
//			}
//			return map;
//		}else if(obj instanceof BasicDBList){
//			Object[] arrays=((BasicDBList)obj).toArray();
//			int i=0;
//			for(Object _obj:arrays){
//				_obj=transferMongoObjectoCommon(_obj);
//				arrays[i++]=_obj;
//			}
//			return arrays;
//		}
		return obj;
	}

}
