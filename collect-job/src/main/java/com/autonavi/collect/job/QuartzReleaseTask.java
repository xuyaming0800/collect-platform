package com.autonavi.collect.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.service.CollectTaskJobService;
/**
 * 被动任务释放JOB
 * @author xuyaming
 *
 */
public class QuartzReleaseTask {
	private Logger logger = LogManager.getLogger(this.getClass());
	private static Object synFlag = new Object();
	@Autowired 
	private CollectTaskJobService collectTaskJobService;
	@Autowired
	public PassiveTaskBusinessManager passiveTaskBusinessManager;
	public void process()throws Exception{
		synchronized(synFlag){
			logger.info("释放被动任务开始!");
			try {
				List<CollectPassiveTask> list=(List<CollectPassiveTask>)passiveTaskBusinessManager.getTaskReleaseCheck().execute(null);
				if(list!=null&&list.size()>0){
					logger.info("需要释放任务共"+list.size()+"条");
				}
				for(CollectPassiveTask task:list){
					try {
						collectTaskJobService.releasePassiveTask(task);
					} catch (Exception e) {
						logger.error("任务释放出现问题id=["+task.getId()+"]",e);
					}
				}
				logger.info("释放被动任务结束!");
			} catch (Exception e) {
				logger.error("释放任务查询异常",e);
				throw e;
			}
		}
		
	}

}
