package cn.dataup.collect.util;

import com.autonavi.audit.mq.RabbitMQUtils;

import cn.dataup.collect.util.bean.QueueInfoEntity;

public class SendCollectInfoJsonToAuditQueue {
	public static void execute(QueueInfoEntity entity,Object messgae) {
		try {
			RabbitMQUtils.send(new Long(entity.getQueueName().hashCode()), entity.getHost(),
					entity.getPort(), null, null, entity.getQueueName(), messgae, true);
		} catch (Exception e) {
			System.out.println("Error["+messgae+"]");
		
		}
	}

}
