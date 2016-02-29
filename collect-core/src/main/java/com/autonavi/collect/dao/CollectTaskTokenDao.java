package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskToken;

@Repository
public class CollectTaskTokenDao {
	/**
	 * 插入Token
	 * @param token
	 * @return
	 */
	@Author("yaming.xu")
	@Shard(indexColumn = "info.userId,info.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object saveTaskToken(@SqlParameter("info") CollectTaskToken token) {
		String sql = "insert into COLLECT_TASK_TOKEN(ID,BASE_ID,CREATE_TIME,UPDATE_TIME,TOKEN,USER_ID,ATTACHMENT_MD5,HEADER,TOKEN_X,TOKEN_Y,TOKEN_STATUS,OWNER_ID)"
				+ "values(#{AOF.snowflake},#{info.baseId},UNIX_TIMESTAMP(now()),UNIX_TIMESTAMP(now()),"
				+ "#{info.token},#{info.userId},#{info.attachmentMd5},#{info.header},#{info.tokenX},"
				+ "#{info.tokenY},0,#{info.ownerId})";
		return sql;
	}
	/**
	 * 获取Token 根据Token值
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	
	@Author("yaming.xu")
	@Shard(indexName = "USER_OWNER_ID_INDEX", indexColumn = "userId,ownerId")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskToken.class)
	public Object getTaskTokenByToken(
			@SqlParameter("token") String token,
			@SqlParameter("userId") Long userId,
			@SqlParameter("ownerId") Long ownerId) {
		return "select id,base_id baseId,token,user_id userId,ATTACHMENT_MD5 attachmentMd5,token_x tokenX,TOKEN_Y tokenY,TOKEN_STATUS tokenStatus,OWNER_ID ownerId from COLLECT_TASK_TOKEN t where t.token=#{token} and t.user_id=#{userId} ";
	}
	
	/**
	 * 获取Token 根据Md5值
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	
	@Author("yaming.xu")
	@Shard(indexName = "USER_OWNER_ID_INDEX", indexColumn = "userId,ownerId")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskToken.class)
	public Object getTaskTokenByMd5(
			@SqlParameter("md5") String md5,
			@SqlParameter("userId") Long userId,
			@SqlParameter("ownerId") Long ownerId) {
		return "select id,base_id baseId,token,user_id userId,ATTACHMENT_MD5 attachmentMd5,token_x tokenX,TOKEN_Y tokenY,TOKEN_STATUS tokenStatus,OWNER_ID ownerId from COLLECT_TASK_TOKEN t where t.ATTACHMENT_MD5=#{md5} and t.user_id=#{userId} ";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "info.userId,info.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateTaskTokenStatus(@SqlParameter("info") CollectTaskToken token) {
		return "update COLLECT_TASK_TOKEN set token_status=#{info.tokenStatus} where token=#{info.token} and user_id=#{info.userId}";
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "info.userId,info.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateTaskTokenBaseId(@SqlParameter("info") CollectTaskToken token,@SqlParameter("base") CollectTaskBase base) {
		return "update COLLECT_TASK_TOKEN set base_id=#{base.id} where token=#{info.token} and user_id=#{info.userId} and base_id is null ";
	}
	


}
