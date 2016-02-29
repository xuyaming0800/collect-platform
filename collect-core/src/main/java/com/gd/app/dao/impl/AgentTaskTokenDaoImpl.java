package com.gd.app.dao.impl;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Delete;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.gd.app.dao.AgentTaskTokenDao;
import com.gd.app.entity.AgentTaskToken;

@Repository("agentTaskTokenDao")
public class AgentTaskTokenDaoImpl implements AgentTaskTokenDao {

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Insert
	public Object addAgentTaskToken(@SqlParameter("m") AgentTaskToken token) {
		return "insert into agent_task_token(id,token_id,user_name,img_md5,header,x,y,create_time,status,ip) values(#{AOF.snowflake},#{m.tokenId},#{m.userName},#{m.imgMd5},#{m.header},#{m.x},#{m.y},SYSDATE(),0,#{m.ip})";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select(collectionType = CollectionType.beanList, resultType = AgentTaskToken.class)
	public Object getAgentTaskTokenByTokenId(
			@SqlParameter("tokenId") String tokenId,
			@SqlParameter("userName") String userName) {
		return "select token_id tokenId,USER_NAME userName,IMG_MD5 imgMd5,X x,Y y from agent_task_token t where t.token_id=#{tokenId} ";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Delete
	public Object deleteAgentTaskToken(@SqlParameter("tokenId") String tokenId,
			@SqlParameter("userName") String userName) {
		return "delete  from  agent_task_token  where token_id=#{tokenId} and fieid_one is null";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Insert
	public Object moveAgentTaskToken(@SqlParameter("tokenId") String tokenId,
			@SqlParameter("userName") String userName) {
		return "insert into agent_task_token_del select * from agent_task_token where token_id=#{tokenId} and fieid_one is null";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskTokenFieldOne(
			@SqlParameter("tokenId") String tokenId,
			@SqlParameter("userName") String userName) {
		return "update agent_task_token t set t.fieid_one='1' where t.token_id=#{tokenId} and t.status=0";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskToken(@SqlParameter("m") AgentTaskToken m) {
		return "update agent_task_token t set t.UPDATE_TIME=SYSDATE(),t.BASE_ID=#{m.baseId},t.img_id=#{m.imgId},t.status=#{m.status}  where t.token_id=#{m.tokenId}";
	}

}
