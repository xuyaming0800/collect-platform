package com.autonavi.collect.util;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.mq.RabbitMQMessageHandler;
import com.autonavi.audit.mq.RabbitMQUtils;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;

public class CollectAuditBackMonitor {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;
	
	private PropertiesConfig propertiesConfig;

	public CollectAuditBackMonitor() throws Exception {
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	@PostConstruct
	private void run()throws Exception{
		logger.info("启动审核回传消息队列开始");
		receiveCollectInfoToAuditQueue(syncTaskBusinessMananger.getTaskReceiveAudit().execute(null));
		logger.info("启动审核回传消息队列结束");
		
	}
	/**
	 * 接受审核回传的消息
	 * @param handler
	 * @throws BusinessException
	 */
	private void receiveCollectInfoToAuditQueue(final RabbitMQMessageHandler handler)throws BusinessException{
		try {
			final String host=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_IN_HOST).toString();
			final Integer port=new Integer(propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_IN_PORT).toString());
			final String queueName=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_IN_NAME).toString();
			final Long id=new Long(queueName.hashCode());
			Thread t = new Thread() {
				public void run() {
					try {
						RabbitMQUtils.receive(String.valueOf(id), host, port, null, null, queueName, ResultEntity.class, true, handler);
					} catch (Exception e) {
						e.printStackTrace();
//						throw new BusinessRunException(BusinessExceptionEnum.TASK_IN_QUEUE_ERROR);
					}
				}
			};
			t.start();
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.TASK_IN_QUEUE_ERROR);
		}
	}

}
