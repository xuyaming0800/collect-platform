package com.gd.app.collect_core;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.geo.util.CoordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.JedisPool;
import autonavi.online.framework.sharding.uniqueid.support.IdWorkerFromSnowflake;

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.component.CollectUserCacheComponent;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectSendMessageErrorDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.manager.SyncTaskUtilBusinessManager;
import com.autonavi.collect.manager.TaskCollectUtilBusinessManager;
import com.autonavi.collect.service.CollectJobService;
import com.autonavi.collect.service.CollectMgrUtilService;
import com.autonavi.collect.service.CollectTaskClazzMgrService;
import com.autonavi.collect.service.CollectTaskClazzService;
import com.autonavi.collect.service.CollectTaskJobService;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskScoreService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class AppTest {
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
	private CollectJobService collectJobService;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private TaskClazzCacheComponent cache;
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	@Autowired
	private SyncTaskUtilBusinessManager syncTaskUtilBusinessManager;



	@Test
	public void test()throws Exception {
		
//		this.runNewTask("/Users/xuyaming/Downloads/out_test_1028.sql",0.005,"正式测试1-小区","659203797813305344","659187296729309184",1460082646L);
//		this.importTask("/Users/xuyaming/Downloads/写字楼.txt", "/Users/xuyaming/Downloads/xzl.sql", "拍建筑", "643695172407787520", "3", 1460082646L);
//		this.importTask("/Users/xuyaming/Downloads/商场.txt", "/Users/xuyaming/Downloads/sc.sql", "拍建筑", "643695219715342336", "3", 1460082646L);
//		this.importTask("/Users/xuyaming/Downloads/超市.txt", "/Users/xuyaming/Downloads/cs.sql", "拍建筑", "643695198416666624", "3", 1460082646L);
		
//		this.importResourceTask("/Users/xuyaming/Downloads/全国LED数据排重20151118.TXT", "/Users/xuyaming/Downloads/ym.sql", 657094810196246528L, 657092850172821504L);
//		CollectTaskBase obj=new CollectTaskBase();
//		obj.setId(662538011686207488L);
//		obj.setVerifyStatus(4);
//		obj.setOwnerId(643698924900581376L);
//		obj.setCollectUserId(600138819622141952L);
//		obj.setVerifyDataName("kengkengkeng");
//		syncTaskUtilBusinessManager.taskRelease(obj);
//		HttpPost httpPost = new HttpPost("http://localhost:8080/cc-web/openapi?serviceid=666005");  
//        HttpClient client = new DefaultHttpClient(); 
//        Map<String, String> params=new HashMap<String,String>();
//        params.put("ownerId", "3");
//        params.put("collectClassId", "607750605007582898");
//        List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());  
//        for(Map.Entry<String, String> entry : params.entrySet()){  
//            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));  
//            valuePairs.add(nameValuePair);  
//        }  
//        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");  
//        httpPost.setEntity(formEntity);
//        HttpResponse resp = client.execute(httpPost);  
//        
//        HttpEntity entity = resp.getEntity();  
//        String respContent = EntityUtils.toString(entity , "UTF-8").trim();  
//        httpPost.abort();  
//        client.getConnectionManager().shutdown();  
//        System.out.println(respContent);
		
//		CollectTaskClazz clazz=cache.getCollectTaskClazz(643695172407787520L);
//		System.out.println(clazz);
//		
//		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
//		File file = new File("/Users/xuyaming/Downloads/H5.txt");
//		File file_out = new File("/Users/xuyaming/Downloads/dongdan_out_h5.sql");
//		int workId=31;
//		int dbid=31;
//	    List<String> contents = FileUtils.readLines(file);
//	    for (String line : contents) {
//	    	String[] s=line.split(",");
//	    	double[] xy=new double[2];
//	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
//	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
//	    	
//	    	long a=ids.nextId(workId, dbid);
//			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,40,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1450082646,'某小区采集',120,120,96,0,null,607750605007582896,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1450082646,"+a+",null,0,607750605007582896,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//	    }
//		
//	    file = new File("/Users/xuyaming/Downloads/zs.txt");
//		file_out = new File("/Users/xuyaming/Downloads/dongdan_out_zs.sql");
//	    contents = FileUtils.readLines(file);
//	    for (String line : contents) {
//	    	String[] s=line.split(",");
//	    	double[] xy=new double[2];
//	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
//	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
//	    	
//	    	long a=ids.nextId(workId, dbid);
//			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,40,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1450082646,'某小区采集',120,120,96,0,null,607750605007582896,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1450082646,"+a+",null,0,607750605007582896,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//	    }
		
//	    file = new File("/Users/xuyaming/Downloads/迅雷下载/写字楼.txt");
//		file_out = new File("/Users/xuyaming/Downloads/dongdan_out_17.sql");
//	    contents = FileUtils.readLines(file);
//	    for (String line : contents) {
//	    	String[] s=line.split(",");
//	    	double[] xy=new double[2];
//	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
//	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
//	    	
//	    	long a=ids.nextId(workId, dbid);
//			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,40,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1450082646,'某写字楼采集',120,120,96,0,null,643695172407787520,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1450082646,"+a+",null,0,643695172407787520,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//	    }
//	    
//	    file = new File("/Users/xuyaming/Downloads/迅雷下载/商场.txt");
//		file_out = new File("/Users/xuyaming/Downloads/dongdan_out_18.sql");
//	    contents = FileUtils.readLines(file);
//	    for (String line : contents) {
//	    	String[] s=line.split(",");
//	    	double[] xy=new double[2];
//	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
//	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
//	    	
//	    	long a=ids.nextId(workId, dbid);
//			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,40,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1450082646,'某商场采集',120,120,96,0,null,643695219715342336,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1450082646,"+a+",null,0,643695219715342336,3);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//	    }
	    
//	    IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
//		File file = new File("/Users/xuyaming/Downloads/dongdan.csv");
//		File file_out = new File("/Users/xuyaming/Downloads/dongdan_out_5.sql");
//		int workId=31;
//		int dbid=31;
//	    List<String> contents = FileUtils.readLines(file);
//	    for (String line : contents) {
//	    	String[] s=line.split(",");
//	    	double[] xy=new double[2];
//	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
//	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
//	    	
//	    	long a=ids.nextId(workId, dbid);
//			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,10,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1446625786,'广告监测',120,120,96,0,null,607750605007582904,4);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1446625786,"+a+",null,0,607750605007582904,4);","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		    FileUtils.write(file_out,"\n","utf-8",true);
//	    }
		
//		
//		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
//		File file_out = new File("/Users/xuyaming/Downloads/out_test_1028.sql");
//		int workId=31;
//		int dbid=31;
//		double startX=116.253282;
//		double endX=116.3330982;
//		double startY=40.078854;
//		double endY=40.027454;
//		int i=0;
//		for(double x=startX;x<=endX;x=x+0.008){
//			for(double y=startY;y>=endY;y=y-0.008){
//				i++;
//	    	String adcode=taskCollectUtilService.fetchAdcode(x, y);
//				long a=ids.nextId(workId, dbid);
//				 FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'正式测试1-小区"+i+"','街道"+i+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1460082646,'正式测试1-小区',1200,1200,96,0,null,656751973487345664,656745633113178112);","utf-8", true);
//			     FileUtils.write(file_out,"\n","utf-8",true);
//			     FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'正式测试1-小区"+i+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1460082646,"+a+",null,0,656751973487345664,656745633113178112);","utf-8", true);
//			     FileUtils.write(file_out,"\n","utf-8",true);
//			     FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+x+"','"+y+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//			     FileUtils.write(file_out,"\n","utf-8",true);
//			}
//			
//		    
//		}
//		System.out.println(i);
//		FileUtil.validateImage("/Users/xuyaming/Downloads/201509_15_157_jYLUV6437132696551424001.jpg");
		
		
//	    BusinessRunException e=new BusinessRunException(BusinessExceptionEnum.IMAGE_INFO_ERROR);
//		e.getSqlExpEnum().setMessage(e.getSqlExpEnum().getMessage().replace("{0}", "1.jpg"));
//		CountDownLatch latch=new CountDownLatch(3);
//		for(int i=0;i<3;i++){
//			MyThread3 thread=new MyThread3(latch);
//			thread.start();
//		}
//		latch.await();
		
//		syncTaskBusinessMananger.getPassiveTaskSendToAudit().execute(null);
//		Jedis jedis=jedisPool.getResource();
//		jedis.del("xym1");
//		System.out.println(jedis.sadd("xym1", "1"));
//		jedis.expire("xym1", 10);
//		jedis.del("xym1");
//		System.out.println(jedis.sadd("xym1", "1"));
//		Thread.sleep(11000);
//		jedis.expire("xym1", 10);
//		System.out.println(jedis.sadd("xym1", "1"));
//		jedis.expire("xym1", 10);
//		List<CollectPassiveTask> list=(List<CollectPassiveTask>)passiveTaskBusinessManager.getTaskReleaseCheck().execute(null);
//		for(CollectPassiveTask task:list){
//			collectTaskJobService.releasePassiveTask(task);
//		}
//		CollectBasePackage collectBasePackage=new CollectBasePackage();
//		collectBasePackage.setCollectUserId(575946127350693888L);
//		collectBasePackage.setTaskPackageStatus(TASK_STATUS.FINISH.getCode());
//		List<CollectBasePackageEntity> l=(List<CollectBasePackageEntity>)collectBasePackageDao.getUserCollectBasePackageByStatus(collectBasePackage, 0, 10);
//		System.out.println(l);
//		CollectTaskClazz clazz=new CollectTaskClazz();
//		clazz.setId(607750605007482882L);
//		clazz.setClazzDistance(1000);
//		clazz.setClazzImgCount(100);
//		clazz.setClazzFarImgCount(50);
//		clazz.setClazzNearImgCount(50);
//		clazz.setClazzName("过路天桥2");
//		clazz.setClazzPay(0.1);
//		clazz.setTaskType(TASK_TYPE.INITIATIVE.getCode());
//		clazz.setClazzType(TASK_CLAZZ_TYPE.ITEM.getCode());
//		clazz.setClazzStatus(TASK_CLAZZ_STATUS.VAILD.getCode());
//		clazz.setClazzIndex(3);
//		clazz.setClazzDesc("dddd");
//        collectTaskClazzMgrService.recoveryCollectTaskClazz(607750605007482881L);
//        collectTaskClazzMgrService.reIndexCollectTaskClazz(607750605007482881L, true);
//        collectTaskClazzMgrService.refreshCollectTaskClazzCache();
		
//		Jedis jedis=redisUtilComponent.getRedisInstance();
//		System.out.println(redisUtilComponent.getRedisStringCache(CommonConstant.TASK_CLAZZ_CACHE_PREFIX+"607750605007482883", jedis));
//		CollectTaskClazz clazz=redisUtilComponent.getRedisJsonCache(jedis, CommonConstant.TASK_CLAZZ_CACHE_PREFIX+"607750605007482883", 
//				CollectTaskClazz.class, JsonBinder.buildNormalBinder(false));
//		System.out.println(clazz);
//		System.out.println(collectTaskClazzService.getCollectTaskInitiativeClazzTree("1"));
//		System.out.println(new Date());
//		collectMgrUtilService.Test("1");
//		System.out.println(new Date());
//		CountDownLatch latch=new CountDownLatch(10);
//		for(int i=0;i<10;i++){
//			MyThread2 thread2=new MyThread2(collectMgrUtilService,"T"+i,latch);
//			thread2.start();
//		}
//		latch.await();
//		redisUtilComponent.returnRedis(jedis);
//		redisUtilComponent.lockIdByRedis("xxx", "1", BusinessExceptionEnum.DUPLICATE_ACTIVE_TASK_SAVE_ERROR, 10, jedis);
//		try {
//			redisUtilComponent.lockIdByRedis("xxx", "1", BusinessExceptionEnum.DUPLICATE_ACTIVE_TASK_SAVE_ERROR, 10, jedis);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//		}
//		redisUtilComponent.releaseIdByRedis("xxx", "1", jedis);
//		jedisPool.returnBrokenResource(jedis);
//		redisUtilComponent.returnRedis(jedis);
		//重发任务到审核-开始
//		CollectTaskSubmitEntity _entity=new CollectTaskSubmitEntity();
//		CollectBasePackage pkg=new CollectBasePackage();
//		pkg.setId(613229361868308480L);
//		pkg.setCollectUserId(575946127350693888L);
//		_entity.setUserName(service.getUserNameCache(pkg.getCollectUserId()));
//		_entity.setCollectBasePackage(pkg);
//		_entity.setJedis(jedis);
//		List<ResultEntity> entitys=syncTaskBusinessMananger.getInitiativeTaskSendToAudit().execute(_entity);
//		for(ResultEntity entity:entitys){
//			syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(entity);
//		}
//		redisUtilComponent.returnRedis(jedis);
//		List<ResultEntity> l=new ArrayList<ResultEntity>();
//		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
//		
//		for(int i=0;i<1;i++){
//			for(ResultEntity entity:entitys){
////				System.out.println("发送至待审核消息队列--开始 id="+((CollectAudit)entity.getInfo()).getId()+" pid="+_entity.getCollectBasePackage().getId());
////				System.out.println("json:"+JsonBinder.buildNormalBinder().toJson(entity));
////				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(entity);
////				System.out.println("发送至待审核消息队列--结束 id="+((CollectAudit)entity.getInfo()).getId()+" pid="+_entity.getCollectBasePackage().getId());
//				ResultEntity _ent=new ResultEntity();
//				_ent.setCode(entity.getCode());
//				_ent.setDesc(entity.getDesc());
//				_ent.setSuccess(entity.isSuccess());
//				CollectAudit audit=new CollectAudit();
//				PropertyUtils.copyProperties(audit, entity.getInfo());
//				audit.setId(String.valueOf(ids.nextId(2,2)));
//				audit.setCollect_task_name("苗佳是猪啊啊啊啊啊");
//				_ent.setInfo(audit);
//				l.add(_ent);
//			}
//		}
//		CountDownLatch latch=new CountDownLatch(1);
//		Long time=new Date().getTime();
//		for(int i=0;i<1;i++){
//			MyThread thread=new MyThread(i+"",l,syncTaskBusinessMananger,latch);
//			thread.start();
//		}
//		latch.await();
//		System.out.println(new Date().getTime()-time);
//		///重发任务到审核-结束
//		collectUserCacheComponent.initAll();
//		service.getUserNameCache(616529286882394112L);
//		CountDownLatch latch=new CountDownLatch(1);
//		for(int i=0;i<1;i++){
//			MyThread1 thread=new MyThread1(service,"n_m_3",latch);
//			thread.start();
//		}
//		latch.await();
//		List<String> test=new ArrayList<String>();
//		test.add("http://www.dataup.cn/collect-img/img/201506/23/105/201506_23_105_XTFav6131484861578649602.JPG");
//		test.add("http://www.dataup.cn/collect-img/img/201506/23/105/201506_23_105_XTFav6131484861578649602.JPG");
//		service.notifyImageWather(1L,2L,test);
//		 CollectSendMessageError error=new CollectSendMessageError();
//		 error.setUserId(1L);
//		 error.setTaskId(2L);
//		 error.setErrorType(SEND_MESSAGE_ERROR_TYPE.AUDIT_IN.getCode());
//		 error.setErrorDesc("中文测试2222");
//		 collectSendMessageErrorDao.insertError(CommonConstant.getSingleDataSourceKey(), error);
//		 
//		 CollectSendMessageError error1=(CollectSendMessageError)collectSendMessageErrorDao.selectError(CommonConstant.getSingleDataSourceKey(), error);
		
//		collectJobService.taskPackageTimeoutProceed();
//		collectJobService.taskPackageTimeoutRelease();
		while(true){
	
		}
//		CollectBasePackage collectBasePackage=new CollectBasePackage();
//		collectBasePackage.setPassivePackageId(1426064213888L);
//		collectBasePackage.setAllotUserId(575946127350693888L);
//		collectTaskAllotUserDao.updateStatus(collectBasePackage, ALLOT_USER_STATUS.VALID.getCode());
//		System.out.println(id);
		//testTaskSubmit();
		
		
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
	private void runNewTask(String fileName,double split,String projectName,String collectCLassId,String ownerId,Long time)throws Exception{
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		File file_out = new File(fileName);
		int workId=31;
		int dbid=31;
		double startX=116.253282;
		double endX=116.3330982;
		double startY=40.078854;
		double endY=40.027454;
		int i=0;
		for(double x=startX;x<=endX;x=x+split){
			for(double y=startY;y>=endY;y=y-split){
				i++;
	    	String adcode=taskCollectUtilService.fetchAdcode(x, y);
				long a=ids.nextId(workId, dbid);
				 FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+projectName+i+"','街道"+i+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,"+time+",'"+projectName+"',1200,1200,96,0,null,"+collectCLassId+","+ownerId+");","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			     FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+projectName+i+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,"+time+","+a+",null,0,"+collectCLassId+","+ownerId+");","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			     FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+x+"','"+y+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			}
			
		    
		}
	}
	private void importTask(String fileSource,String fileOut,String projectName,String collectCLassId,String ownerId,Long time)throws Exception{
		int workId=31;
		int dbid=31;
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		File file = new File(fileSource);
		File file_out = new File(fileOut);
	    List<String> contents = FileUtils.readLines(file);
	    for (String line : contents) {
	    	String[] s=line.split(",");
	    	double[] xy=new double[2];
	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
	    	String adcode=taskCollectUtilService.fetchAdcode(xy[0], xy[1]);
	    	
	    	long a=ids.nextId(workId, dbid);
			FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[0]+"','"+s[0]+"',1,40,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,"+time+",'"+projectName+"',120,120,96,0,null,"+collectCLassId+","+ownerId+");","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,"+time+","+a+",null,0,"+collectCLassId+","+ownerId+");","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"','"+adcode+"','2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
	    }
	}
	private void importResourceTask(String fileSource,String fileOut,Long collectCLassId,Long ownerId)throws Exception{
		double ddd=Double.valueOf("117.2894023");
		int workId=31;
		int dbid=31;
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		File file = new File(fileSource);
		File file_out = new File(fileOut);
	    List<String> contents = FileUtils.readLines(file);
	    for (String line : contents) {
	    	String[] s=line.trim().split(",");
	    	double[] xy=new double[2];
	    	CoordUtils.gps2bd(Double.valueOf(s[0]), Double.valueOf(s[1]), xy);
	    	long a=ids.nextId(workId, dbid);
			FileUtils.write(file_out,"insert into collect_resource_data values("+a+",'"+xy[0]+"','"+xy[1]+"',"+collectCLassId+","+ownerId+",unix_timestamp(now()),unix_timestamp(now()),0);","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
	    }
	}
}
