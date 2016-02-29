package com.gd.app.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select.Paging;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.gd.app.dao.AgentAppealBaseDao;
import com.gd.app.entity.Appeal;

@Repository("agentAppealBaseDao")
public class AgentAppealBaseDaoImpl implements AgentAppealBaseDao {

	long primaryKey = -1;

	public long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(long primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object queryAppeals(@SqlParameter("m") Appeal appeal,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		StringBuffer sqlBuffer = new StringBuffer(
				"select APPE_URL,ID,APPE_USER_NAME,APPE_NAME,APPE_CONTENT,APPE_TYPE,APPE_STATUS,APPE_REMARK,APPE_REASON,date_format(appe_submit_time,'%Y-%m-%d %H:%i:%S') APPE_SUBMIT_TIME,APPE_BASEID from agent_appeal where 1=1");
		if (StringUtils.isNotEmpty(appeal.getUserName())) {
			sqlBuffer.append(" and appe_user_name=#{m.userName}");
		}
		if (StringUtils.isNotEmpty(appeal.getSubmitTime())) {
			String startTime = appeal.getSubmitTime() + " 00:00:00";
			String endTime = appeal.getSubmitTime() + " 23:59:59";
			sqlBuffer.append(" and (appe_submit_time >= '").append(startTime)
					.append("' and appe_submit_time <= '").append(endTime)
					.append("')");
		} else {
			if (StringUtils.isNotEmpty(appeal.getBeginTime())) {
				String startTime = appeal.getBeginTime() + " 00:00:00";
				sqlBuffer.append(" and (appe_submit_time  >= '")
						.append(startTime).append("') ");
			}
			if (StringUtils.isNotEmpty(appeal.getEndTime())) {
				String endTime = appeal.getEndTime() + " 23:59:59";
				sqlBuffer.append(" and (appe_submit_time <= '").append(endTime)
						.append("') ");
			}
		}
		if (appeal.getType() != null) {
			sqlBuffer.append(" and appe_type=#{m.type}");
		}
		sqlBuffer.append(" order by appe_submit_time desc");

		return sqlBuffer.toString();
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Select(collectionType = CollectionType.beanList, resultType = Appeal.class)
	public Object queryAppeal(@SqlParameter("id") Long id,
			@SqlParameter("userName") String userName) {
		return "select id,appe_user_name  userName,appe_name name,appe_content content,appe_type type,appe_remark remark,appe_reason reason,APPE_BASEID baseId,appe_url url,appe_submit_time submitTime from  agent_appeal  where id=#{id}";
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Select(collectionType = CollectionType.beanList, resultType = Appeal.class)
	public Object queryAppealByBaseID(@SqlParameter("baseId") Long baseId,
			@SqlParameter("userName") String userName) {
		return "select id,appe_user_name  userName,appe_name name,appe_content content,appe_type type,appe_remark remark,appe_reason reason,APPE_BASEID baseId,appe_url url,appe_submit_time submitTime from  agent_appeal  where appe_baseId=#{baseId}";
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Select
	public Object queryAppealsCount(@SqlParameter("m") Appeal appeal) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("select count(0) as total from agent_appeal where 1=1");
		if (StringUtils.isNotEmpty(appeal.getUserName())) {
			sqlBuffer.append(" and appe_user_name=#{m.userName}");
		}
		if (StringUtils.isNotEmpty(appeal.getSubmitTime())) {
			String startTime = appeal.getSubmitTime() + " 00:00:00";
			String endTime = appeal.getSubmitTime() + " 23:59:59";
			sqlBuffer.append(" and (appe_submit_time >= '").append(startTime)
					.append("' and appe_submit_time <= '").append(endTime)
					.append("')");
		}
		if (appeal.getType() != null) {
			sqlBuffer.append(" and appe_type=#{m.type}");
		}
		if (appeal.getStatus() != null) {
			sqlBuffer.append(" and appe_status=#{m.status}");
		}
		return sqlBuffer.toString();
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Insert
	public Object saveAppeal(@SqlParameter("m") Appeal appeal) {
		return "insert into agent_appeal"
				+ "(id,appe_user_name,appe_name,appe_content,appe_type,"
				+ "appe_remark,appe_reason,appe_baseid,appe_url,appe_submit_time) "
				+ "values(#{AOF.snowflake},#{m.userName},#{m.name},"
				+ "#{m.content},#{m.type},#{m.remark},#{m.reason},"
				+ "#{m.baseId},#{m.url},#{m.submitTime})";
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Update
	public Object updateAppeal(@SqlParameter("m") Appeal appeal) {
		return "update agent_appeal set appe_status=#{m.status},appe_remark=#{m.remark}  where id=#{m.id}";
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Update
	public Object updateAppealTask(@SqlParameter("baseId") Long baseId,
			@SqlParameter("userName") String userName) {
		return "update agent_task_base set can_appeal=0  where id=#{baseId}";
	}

	@Override
	@Author("wenfeng.gao")
	@SingleDataSource(1)
	@Select
	public Object queryAppealTask(@SqlParameter("baseId") Long baseId,
			@SqlParameter("userName") String userName) {
		return "select a.data_name DATA_NAME,a.user_name USER_NAME,date_format(a.submit_time,'%Y-%m-%d %H:%i:%S') SUBMIT_TIME,a.comments COMMENTS,b.image_id IMAGE_ID from agent_task_base a inner join agent_task_mz b on a.id=b.base_id where a.id=#{baseId}";
	}

}
