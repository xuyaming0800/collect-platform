package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectSendMessageError;
import com.autonavi.collect.constant.CommonConstant.SEND_MESSAGE_ERROR_STATUS;

@Repository
public class CollectSendMessageErrorDao {
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dsKey")
	@Insert
	public Object insertError(@SqlParameter("dsKey") Integer dsKey,
			@SqlParameter("error") CollectSendMessageError error){
		StringBuffer sb=new StringBuffer("");
		sb.append(" insert into collect_send_message_error ");
		sb.append(" (ID,CREATE_TIME,TASK_ID,PACKAGE_ID,USER_ID,ERROR_TYPE,ERROR_DESC,STATUS) ");
		sb.append(" values( ");
		sb.append(" #{" + ReservedWord.snowflake+ "}, ");
		sb.append(" UNIX_TIMESTAMP(), ");
		sb.append(" #{error.taskId}, ");
		sb.append(" #{error.packageId}, ");
		sb.append(" #{error.userId}, ");
		sb.append(" #{error.errorType}, ");
		sb.append(" #{error.errorDesc}, ");
		sb.append(SEND_MESSAGE_ERROR_STATUS.NEW.getCode());
		sb.append(" ) ");
		return sb.toString();
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dsKey")
	@Select(collectionType = CollectionType.bean, resultType = CollectSendMessageError.class)
	public Object selectError(@SqlParameter("dsKey") Integer dsKey,
			@SqlParameter("error") CollectSendMessageError error){
		StringBuffer sb=new StringBuffer("");
		sb.append(" select ERROR_DESC errorDesc from collect_send_message_error ");
		sb.append(" where id=616940517401296896 ");
		return sb.toString();
	}
	

}
