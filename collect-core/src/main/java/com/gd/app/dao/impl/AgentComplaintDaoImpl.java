package com.gd.app.dao.impl;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;

import com.gd.app.dao.AgentComplaintDao;
@Repository("agentComplaintDao")
public class AgentComplaintDaoImpl implements AgentComplaintDao {

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Insert
	public Object addAgentComplaint(@SqlParameter("userName") String userName, @SqlParameter("phone") String phone,
			@SqlParameter("content") String content) {
		return "insert into agent_complaint(ID,USER_NAME,TELEPHONE,COMMIT_TIME,CONTENT)values(#{AOF.snowflake},#{userName},#{phone},sysdate(),#{content})";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Insert
	public Object addAgentComplaint(@SqlParameter("userName") String userName, @SqlParameter("phone") String phone,
			@SqlParameter("content") String content, @SqlParameter("deviceSys") String deviceSys,@SqlParameter("deviceModel") String deviceModel) {
		return "insert into agent_complaint(ID,USER_NAME,TELEPHONE,COMMIT_TIME,CONTENT,DEVICE_SYS,DEVICE_MODEL)values(#{AOF.snowflake},#{userName},#{phone},sysdate(),#{content},#{deviceSys},#{deviceModel})";
	}

}
