package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Delete;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.BossUserTable;

@Repository
public class BossUserDao {
	/**
	 * 查询所有用户的名称ID对应关系
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = BossUserTable.class)
	public Object getAllBossUsers(
			@SqlParameter("dsKey") Integer dsKey) {
		return "select id,user_name userName from boss_user_table ";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.bean, resultType = BossUserTable.class)
	public Object getBossUserByName(
			@SqlParameter("dsKey") Integer dsKey,@SqlParameter("userName") String userName) {
		return "select id,user_name userName from boss_user_table where user_name=#{userName} ";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.bean, resultType = BossUserTable.class)
	public Object getBossUserById(
			@SqlParameter("dsKey") Integer dsKey,@SqlParameter("id") Long id) {
		return "select id,user_name userName from boss_user_table where id=#{id} ";
	}
	/**
	 * 插入用户
	 * @param user
	 * @param dsKey
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Insert
	public Object addBossUser(@SqlParameter("user") BossUserTable user,@SqlParameter("dsKey") Integer dsKey){
		return "insert into boss_user_table values(#{AOF.snowflake},#{user.userName})";
	}
	
	/**
	 * 删除用户
	 * @param user
	 * @param dsKey
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Delete
	public Object delBossUser(@SqlParameter("id") Long id,@SqlParameter("dsKey") Integer dsKey){
		return "delete from boss_user_table where id=#{id}";
	}

}
