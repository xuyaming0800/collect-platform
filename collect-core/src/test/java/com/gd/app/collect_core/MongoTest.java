package com.gd.app.collect_core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.JedisPool;
import autonavi.online.framework.util.bean.PropertyUtils;

import com.autonavi.collect.component.CollectUserCacheComponent;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TokenUseCacheUtilComponent;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectSendMessageErrorDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.manager.TaskCollectUtilBusinessManager;
import com.autonavi.collect.service.CollectMgrUtilService;
import com.autonavi.collect.service.CollectTaskClazzMgrService;
import com.autonavi.collect.service.CollectTaskClazzService;
import com.autonavi.collect.service.CollectTaskJobService;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskScoreService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MongoTest {
	@Autowired
	public PassiveTaskService passiveTaskServiceImpl;
	@Autowired
	public SyncTaskBusinessMananger syncTaskBusinessMananger;
	@Autowired
	public PassiveTaskBusinessManager passiveTaskBusinessManager;
	@Autowired
	public TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;
	@Autowired
	public TaskScoreService TaskScoreService;
	@Autowired
	private TaskCollectUtilService service;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Resource
	public JedisPool jedisPool;
	@Autowired
	private CollectTaskJobService collectTaskJobService;
	@Autowired
	private CollectTaskAllotUserDao collectTaskAllotUserDao;
	@Autowired
	private CollectTaskClazzService collectTaskClazzService;
	@Autowired
	private CollectTaskClazzMgrService collectTaskClazzMgrService;
	@Autowired
	private CollectMgrUtilService collectMgrUtilService;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private CollectUserCacheComponent collectUserCacheComponent;
	@Autowired
	private CollectSendMessageErrorDao collectSendMessageErrorDao; 
	
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private TokenUseCacheUtilComponent tokenUseCacheUtilComponent;



	/**
	 * @throws Exception
	 */
	@Test
	public void test()throws Exception {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
//		insert
//		BasicDBObject insert = new BasicDBObject();
//		Map<String,Object> map=new HashMap<String,Object>();
//		map.put("taskId", "100000");
//		map.put("baseId", "100001");
//		Map<String,Object> map_1=new HashMap<String,Object>();
//		Map<String,Object> map_2=new HashMap<String,Object>();
//		map_2.put("name", "小区停车场");
//		map_2.put("location", "大恒科技大厦");
//		map_1.put("停车场1", map_2);
//		map.put("attrs", map_1);
//		insert.putAll(map);
//		dbcoll.insert(insert);
		//select
//		BasicDBObject query = new BasicDBObject();
//		query.put("taskId", "100000");
//		BasicDBObject projection = new BasicDBObject();
//		projection.put("_id", 0);
//		projection.put("attrs.停车场1", 1);
//		DBCursor dBCursor=dbcoll.find(query);
		
//		String json=dBCursor.next().toString();
//		JsonBinder binder=JsonBinder.buildNormalBinder(false);
//		JavaType type=binder.getCollectionType(Map.class, String.class,Object.class);
//		@SuppressWarnings("unchecked")
//		Map<String,Object> mm=binder.fromJson(json, Map.class, type);
//		System.out.println(mm);
		//update
//		BasicDBObject updateQuery = new BasicDBObject();
//		updateQuery.put("taskId", "100000");
//		BasicDBObject update = new BasicDBObject();
//		update.put("attrs.停车场2", "111");
//		dbcoll.update(updateQuery, new BasicDBObject("$set", update));
		//delete
//		BasicDBObject deleteQuery = new BasicDBObject();
//		deleteQuery.put("taskId", "100000");
//		BasicDBObject delete = new BasicDBObject();
//		delete.put("attrs.停车场2", new BasicDBObject("$exists", true));
//		dbcoll.update(deleteQuery, new BasicDBObject("$unset", delete));
//		Map<String,Object> updateQueryMap=new HashMap<String,Object>();
//		updateQueryMap.put("baseId", "624223939098312704");
//		updateQueryMap.put("attrs.楼栋1.imgs.index", "2");
//		Map<String,Object> mmm=new HashMap<String,Object>();
//		String selectlocate="attrList.$.status";
//		String updatejson="3";
//		mmm.put(selectlocate, updatejson);
//		mongoDBUtilComponent.updateArrayObject("collect_audit_attr", updateQueryMap, selectlocate, updatejson,true);
//		mongoDBUtilComponent.deleteCommonObject("collect_audit_attr", updateQueryMap, selectlocate);
//		mongoDBUtilComponent.updateCommonObject("collect_audit_attr", updateQueryMap, selectlocate, updatejson);
//		mongoDBUtilComponent.updateCommonObject("collect_audit_attr", updateQueryMap,mmm );
//		List<String> s=mongoDBUtilComponent.selectObject("collect_audit_attr", updateQueryMap, selectlocate, false);
//		System.out.println(s);
		
//		 List<Object> deleteList=mongoDBUtilComponent.selectObject("collect_audit_attr", updateQueryMap, "attrs.楼栋1.imgs.$", false);
//		for(Object s:deleteList){
//			
//			mongoDBUtilComponent.deleteArrayObject("collect_audit_attr", updateQueryMap,"attrs.楼栋1.imgs", PropertyUtils.getValue(s, "attrs.楼栋1.imgs"),false);
//		}
//	    System.out.println("ssss");
	//	System.out.println(tokenUseCacheUtilComponent.checkUseTokenFromCache(3L));
//		Map<String,Object> mmm=new HashMap<String,Object>();
//		mmm.put("baseId", "624510304071450624");
//		mmm.put("attrs.楼栋1.imgs.imgH5Id", "img1");
//		
//		Map<String,Object> mm1=new HashMap<String,Object>();
//		mm1.put("attrs.楼栋1.imgs.$.lon","100.1");
//		mm1.put("attrs.楼栋1.imgs.$.url","100.1");
//		mongoDBUtilComponent.batchUpdateCommonObject("collect_audit_attr", mmm, mm1);
		
//		Map<String,Object> mmm=new HashMap<String,Object>();
//		mmm.put("baseId", "625969381247025152");
//		List<Object> l=mongoDBUtilComponent.selectObject("collect_audit_attr", mmm, "attrList", false);
//		Object obj=null;
//		if(l.size()>0){
//			obj=l.get(0);
//		}
//		obj=PropertyUtils.getValue(obj, "attrList");
//		List<Object> result =(List<Object>)(obj);
//		System.out.println(result);
		
//		Map<String,Object> mmm=new HashMap<String,Object>();
//		mmm.put("attrList.path",new BasicDBObject("$exists", true));
//		List<Object> l=mongoDBUtilComponent.selectObject("collect_audit_attr", mmm, null, true);
//		for(Object obj:l){
//			List<Object> _obj=(List<Object>)PropertyUtils.getValue(obj, "attrList");
//			for(Object __obj:_obj){
//				String level=(String)PropertyUtils.getValue(__obj, "path");
//				Map<String,Object> mmmm=new HashMap<String,Object>();
//				mmmm.put("attrList.path", level);
//				if(level!=null)
//				mongoDBUtilComponent.updateCommonObject("collect_audit_attr", mmmm, "attrList.$.level", level);
//				mongoDBUtilComponent.deleteCommonObject("collect_audit_attr", mmmm, "attrList.$.path");
//				
//			}
//		}
		
		Map<String,Object> mmm=new HashMap<String,Object>();
		mmm.put("baseId","625969381247025152");
		List<Object> l=mongoDBUtilComponent.selectObject("collect_audit_attr", mmm, null, true);
		for(Object obj:l){
			Object _obj=PropertyUtils.getValue(obj, "attrs.楼栋1");
			Object ooo=mongoDBUtilComponent.transferMongoObjectoCommon(_obj);
			Object oooo=PropertyUtils.getValue(ooo, "imgs");
			
			System.out.println(ooo);
			
		}
		
//		
	}

	private void testTaskSave() {
		
//		CollectTaskBase collectTaskBase = new CollectTaskBase();
//		collectTaskBase.setPassiveId(1426064213585L);
//		collectTaskBase.setCollectUserId(1L);
//		collectTaskBase.setCollectDataName("jiang测试保存");		
//		try {
//			passiveTaskServiceImpl.taskSave(collectTaskBase);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private void testTaskSubmit() {
//		CollectPassiveTask collectPassiveTask = new CollectPassiveTask();
//		collectPassiveTask.setId(1426064213585L);
//		// collectPassiveTask.setAllotUserId(1L);
//		collectPassiveTask.setCollectUserId(1L);
//		collectPassiveTask.setCollectDataName("jiang测试提交");
//		// CollectTaskBase collectTaskBase = new CollectTaskBase();
//		List<Map<?, ?>> taskList = new ArrayList<Map<?, ?>>();
//		Map<String, Object> task = new HashMap<String, Object>();
//		task.put("task", collectPassiveTask);
//		taskList.add(task);
//		try {
//			passiveTaskServiceImpl.taskSubmit(taskList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
