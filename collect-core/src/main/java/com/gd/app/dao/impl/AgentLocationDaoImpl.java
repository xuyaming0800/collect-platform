package com.gd.app.dao.impl;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;

import com.gd.app.dao.AgentLocationDao;
@Repository("agentLocationDao")
public class AgentLocationDaoImpl implements AgentLocationDao {

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getAgentLocationByAdcode(@SqlParameter("adCode") String adCode) {
		// TODO Auto-generated method stub
		return "select province,city,county  from agent_location  where ad_code=#{adCode}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getSystemTime() {
		// TODO Auto-generated method stub
		return "select sysdate() time";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Insert
	public Object addAppErrorInfo(@SqlParameter("userName") String userName, @SqlParameter("type") String type, @SqlParameter("version") String version,
			@SqlParameter("deviceInfo") String deviceInfo, @SqlParameter("erCode") String erCode, @SqlParameter("erInfo") String erInfo) {
		// TODO Auto-generated method stub
		return "insert into APP_ERROR_INFO (ID,USERNAME,TYPE,VERSION,DEVICEINFO,ERCODE,ERINFO,CREATETIME) VALUES(#{AOF.snowflake},#{userName},#{type},#{version},#{deviceInfo},#{erCode},#{erInfo},sysdate())";
	}

}
