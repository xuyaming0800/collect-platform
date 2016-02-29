package com.gd.app.dao.impl;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select.Paging;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.gd.app.dao.AgentTaskBaseDao;
import com.gd.app.entity.AgentTaskBase;
import com.gd.app.entity.AgentTaskMz;
import com.gd.app.entity.AgentTaskUploadEntity;
import com.gd.app.entity.TaskLockResultEntity;
import com.gd.app.entity.ViewUserScore;

@Repository("agentTaskBaseDao")
public class AgentTaskBaseDaoImpl implements AgentTaskBaseDao {
	long primaryKey = -1;

	public long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(long primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select(collectionType = CollectionType.beanList, resultType = TaskLockResultEntity.class)
	public Object getAgentInfoByBaseId(@SqlParameter("id") String id,
			@SqlParameter("userName") String userName) {
		return "select a.status status, b.x x, b.y y,a.task_id taskId,a.data_name dataName,a.user_name userName,a.id baseId,a.CAN_APPEAL canAppeal from agent_task_base a join agent_task_mz b on a.id=b.base_id where a.id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select(collectionType = CollectionType.beanList, resultType = TaskLockResultEntity.class)
	public Object getAgentTaskBaseByBaseId(@SqlParameter("id") String id,
			@SqlParameter("userName") String userName) {
		return "select can_appeal canAppeal,status,task_id taskId,data_name dataName,user_name userName,id baseId,can_appeal canAppeal from agent_task_base where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getAutoNaviDBByName(
			@SqlParameter("subAdCode") String subAdCode,
			@SqlParameter("dataName") String dataName) {
		return " select rn_id id from t_streetno where substr(AD_ADCODE,1,4)=#{subAdCode} and EXTRA=#{dataName}"
				+ "union all"
				+ "select hn_id id  from  t_buildingno where substr(AD_ADCODE,1,4)=#{subAdCode} and EXTRA=#{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getAuditBackDBByName(
			@SqlParameter("subAdCode") String subAdCode,
			@SqlParameter("dataName") String dataName) {
		return "select count(*) counts from auditback where status=1 and substr(adcode,1,4)=#{subAdCode} and data_name=#{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(collectionType = CollectionType.beanList, resultType = TaskLockResultEntity.class)
	public Object getTaskPassiveById(@SqlParameter("id") String id) {
		return "select status,user_name userName,address_type addressType from task_all_passive where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Insert
	public Object addAgentTaskBase(@SqlParameter("m") AgentTaskBase base) {
		return "insert into agent_task_base(ID,TASK_ID,USER_NAME,DATA_NAME,STATUS,SUBMIT_TIME,END_TIME,AD_CODE,REG_DATANAME,timestamps,device_info,score_id) values(#{AOF.snowflake},#{m.taskId},#{m.userName},#{m.dataName},#{m.status},str_to_date(#{m.submitTime},'%Y-%m-%d %T'),str_to_date(#{m.endTime},'%Y-%m-%d %T'),#{m.adCode},#{m.regDataname},unix_timestamp()*1000,#{m.deviceInfo},#{m.scoreId}))";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Insert
	public Object addAgentTaskMz(@SqlParameter("m") AgentTaskMz mz) {
		return "insert into agent_task_mz(id,base_id,x,y,OFFSET_X,OFFSET_Y,user_name) values(#{AOF.snowflake},#{m.baseId},#{m.x},#{m.y},#{m.offsetX},#{m.offsetY},#{m.userName})";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object updateTaskPassive(@SqlParameter("status") int status,
			@SqlParameter("id") Long id,
			@SqlParameter("timestamps") Long timestamps,
			@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "update task_all_passive set status=#{status},timestamps=unix_timestamp()*1000,user_name=#{userName} where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object updateAllotTask(@SqlParameter("status") int status,
			@SqlParameter("id") Long id,
			@SqlParameter("timestamps") Long timestamps) {
		// TODO Auto-generated method stub
		return "update task_all_user set status=#{status},timestamps=unix_timestamp()*1000 where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Insert
	public Object moveTaskAll(@SqlParameter("id") Long id,
			@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "insert into task_all select * from task_all_passive where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Update
	public Object updateTaskAllUserInfo(@SqlParameter("id") Long id,
			@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "update task_all set user_name=#{userName} where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(collectionType = CollectionType.beanList, resultType = TaskLockResultEntity.class)
	public Object getAllotTaskById(@SqlParameter("id") String id) {
		return "select status status,user_name userName from task_all_user where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object modifyAgentTaskName(@SqlParameter("m") AgentTaskBase base) {
		if (base.getScoreId() == null || base.getScoreId().equals("")) {
			return "update agent_task_base set data_name=#{m.dataName},  reg_dataname=#{m.regDataname} ,timestamps=unix_timestamp()*1000 where  id = #{m.id}";
		} else {
			return "update agent_task_base set data_name=#{m.dataName},  reg_dataname=#{m.regDataname} ,score_id=#{m.scoreId} ,timestamps =unix_timestamp()*1000 where  id = #{m.id}";
		}

	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskBaseStatus(
			@SqlParameter("m") AgentTaskBase base) {
		// TODO Auto-generated method stub
		return "update agent_task_base set status=#{m.status},timestamps=unix_timestamp()*1000 where id=#{m.id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Update
	public Object updateTaskAll(@SqlParameter("status") int status,
			@SqlParameter("id") Long id,
			@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "update task_all set status=#{status},timestamps=unix_timestamp()*1000 where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select(collectionType = CollectionType.beanList, resultType = AgentTaskBase.class)
	public Object getUserSavePassiveTaskByTaskId(
			@SqlParameter("taskId") String taskId,
			@SqlParameter("userName") String userName) {
		return "select id,status from agent_task_base where status=3 and task_id=#{taskId}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskBase(
			@SqlParameter("m") AgentTaskUploadEntity base) {
		String sql = "update AGENT_TASK_BASE SET SUBMIT_TIME=Sysdate(),status=0,END_TIME=null,TASK_TYPE=#{m.taskType},ENTERPRISE_CODE=#{m.enterPriseCode},PHOTO_TIME=#{m.photoTime},GETGPS_TIME=#{m.gpsTime},timestamps=unix_timestamp()*1000,device_info=#{m.deviceInfo},score_id=#{m.scoreId},address_type=#{m.addressType},comments=#{m.comments}  where id=#{m.baseId} and status=3 and user_name=#{m.userName}";
		return sql;
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskMz(@SqlParameter("m") AgentTaskUploadEntity mz) {
		String sql = " update AGENT_TASK_MZ SET POINT_TYPE=#{m.pointType},POINT_LEVEL=#{m.pointLevel},IMAGE_ID=#{m.imageId},USER_NAME=#{m.userName},POINT_ACCURY=#{m.pointAccury},POSITION=#{m.position},POSITION_X=#{m.positionX},POSITION_Y=#{m.positionY},POSITION_Z=#{m.positionZ} WHERE BASE_ID=#{m.baseId}";
		return sql;
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object updateTaskPassive(@SqlParameter("status") int status,
			@SqlParameter("id") Long id,
			@SqlParameter("userName") String userName) {
		return "update task_all_passive set status=#{status},timestamps=unix_timestamp()*1000,user_name=#{userName} where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Insert
	public Object addAgentTaskBase(@SqlParameter("m") AgentTaskUploadEntity base) {
		String sql = "insert into agent_task_base(ID,TASK_ID,USER_NAME,TASK_TYPE,DATA_NAME,STATUS,SUBMIT_TIME,ENTERPRISE_CODE,PHOTO_TIME,GETGPS_TIME,AD_CODE,REG_DATANAME,timestamps,DEVICE_INFO,SCORE_ID,related_Lost_Id,address_type,comments)values(#{AOF.snowflake},#{m.taskId},#{m.userName},#{m.taskType},#{m.dataName},0,SysDate(),#{m.enterPriseCode},#{m.photoTime},#{m.gpsTime},#{m.adCode},#{m.regDataName},unix_timestamp()*1000,#{m.deviceInfo},#{m.scoreId},#{m.relatedLostId},#{m.addressType},#{m.comments})";
		return sql;
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Insert
	public Object addAgentTaskMz(@SqlParameter("m") AgentTaskUploadEntity mz) {
		String sql = "insert into agent_task_mz(ID,BASE_ID, X, Y,POINT_TYPE,POINT_LEVEL,IMAGE_ID,USER_NAME,OFFSET_X,OFFSET_Y,POINT_ACCURY,POSITION,POSITION_X,POSITION_Y,POSITION_Z) values(#{AOF.snowflake},#{m.baseId},#{m.x},#{m.y},#{m.pointType},#{m.pointLevel},#{m.imageId},#{m.userName},#{m.offsetX},#{m.offsetY},#{m.pointAccury},#{m.position},#{m.positionX},#{m.positionY},#{m.positionZ})";
		return sql;
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object getPassiveTaskLineLock(@SqlParameter("id") String id) {
		// TODO Auto-generated method stub
		return "update task_all_passive set id=id where id=#{id}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(collectionType = CollectionType.beanList,resultType = ViewUserScore.class)
	public Object getViewUserScore(@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "select username userName,ename,ecode,agenttype agentType from view_user_score_new where username=#{userName}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "m.userName", indexName = "username_index")
	@Update
	public Object updateAgentTaskBase(@SqlParameter("m") ViewUserScore view) {
		// TODO Auto-generated method stub
		return "update agent_task_base set ename=#{m.ename},agenttype=#{m.agentType} where id=#{m.baseId}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select
	public Object queryBaseStatusCount(@SqlParameter("userName") String userName) {
		return "select count(1) total from agent_task_base where user_name=#{userName} and status=0 union all select count(1) total from agent_task_base where user_name=#{userName} and status=1 union all select count(1) total from agent_task_base where user_name=#{userName} and status=2";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select
	public Object queryUserTaskCount(@SqlParameter("userName") String userName,
			@SqlParameter("status") int status, boolean isAll) {
		if (isAll) {
			return " select user_name as userName, count(id) as total from agent_task_base  where ( status=0 or status=1 or status=2 )  and user_name=#{userName}   group by user_name";
		} else {
			return "  select count(1) total from agent_task_base where user_name=#{userName} and status=#{status}";
		}
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object queryUserTask(@SqlParameter("userName") String userName,
			@SqlParameter("status") int status,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit,
			boolean isAll) {
		if (isAll) {
			return "select ID,CAN_APPEAL,TASK_ID,TASK_TYPE,STATUS,DATA_NAME,SCORE,date_format(END_TIME,'%Y-%m-%d %H:%i:%S') END_TIME ,date_format(SUBMIT_TIME,'%Y-%m-%d %H:%i:%S') SUBMIT_TIME,COMMENTS,score_id from agent_task_base "
					+ " where user_name=#{userName}  and ( status=0 or status=1 or status=2 ) order by SUBMIT_TIME  desc ";
		} else {
			return "select ID,CAN_APPEAL,TASK_ID,TASK_TYPE,STATUS,DATA_NAME,SCORE,SCORE_NEW,date_format(END_TIME,'%Y-%m-%d %H:%i:%S') END_TIME ,date_format(SUBMIT_TIME,'%Y-%m-%d %H:%i:%S') SUBMIT_TIME,COMMENTS,score_id from agent_task_base "
					+ " where user_name=#{userName} and status=#{status} order by SUBMIT_TIME  desc";
		}
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexName = "username_index", indexColumn = "userName")
	@Select
	public Object getUserTaskByTaskId(
			@SqlParameter("userName") String userName,
			@SqlParameter("dataName") String dataName,
			@SqlParameter("taskId") String taskId) {
		if (taskId == null || "".equals(taskId)) {
			return " select id from  agent_task_base where status=3 and user_name=#{userName} and data_Name=#{dataName}";
		}
		return " select id from  agent_task_base where status=3 and user_name=#{userName} and task_id=#{taskId}";
	}

	@Override
	@Author("yaming.xu")
	@Shard(indexColumn = "userName", indexName = "username_index")
	@Update
	public Object deleteTaskById(@SqlParameter("userName") String userName,
			@SqlParameter("id") String id) {
		// TODO Auto-generated method stub
		return "update agent_task_base set status=-1 where id=#{id} and user_name=#{userName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object initPassiveTask(@SqlParameter("taskId") String taskId,
			boolean isAllot) {
		if (!isAllot)
			return "update task_all_passive set status=0,timestamps=unix_timestamp()*1000,user_name=null where id=#{taskId}";
		else {
			return "update task_all_passive set status=-3,timestamps=unix_timestamp()*1000,user_name=null where id=#{taskId}";
		}
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Update
	public Object initAllotTask(@SqlParameter("taskId") String taskId,
			@SqlParameter("userName") String userName) {
		return "update task_all_user set status=0,timestamps=unix_timestamp()*1000 where id=#{taskId} and user_name=#{userName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getPassiveTaskCountByAdcode(
			@SqlParameter("adCode") String adCode,
			@SqlParameter("dataName") String dataName) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select count(id) total from task_all_passive task  where status=0 and DATA_SOURCE=1  and DISTRICT_ID=#{adCode}";
		}
		return "select count(id) total from task_all_passive task  where status=0 and DATA_SOURCE=1  and DISTRICT_ID=#{adCode} and data_name like #{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getPassiveTaskCountByAdcodeScope(
			@SqlParameter("minAdcode") int minAdcode,
			@SqlParameter("maxAdcode") int maxAdcode,
			@SqlParameter("dataName") String dataName) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select count(id) total from task_all_passive task where  status=0 and DATA_SOURCE=1 and DISTRICT_ID>=#{minAdcode}  and DISTRICT_ID<#{maxAdcode}";
		}
		return "select count(id) total from task_all_passive task where  status=0 and DATA_SOURCE=1 and DISTRICT_ID>=#{minAdcode}  and DISTRICT_ID<#{maxAdcode}  and data_name like #{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object getPassiveTaskByAdcode(@SqlParameter("adCode") String adCode,
			@SqlParameter("dataName") String dataName,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_passive task where  status=0 and DATA_SOURCE=1  and DISTRICT_ID=#{adCode} ";
		}
		return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_passive task where  status=0 and DATA_SOURCE=1  and DISTRICT_ID=#{adCode}  and data_name like #{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object getPassiveTaskByAdcodeScope(
			@SqlParameter("minAdcode") int minAdcode,
			@SqlParameter("maxAdcode") int maxAdcode,
			@SqlParameter("dataName") String dataName,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_passive task where  status=0 and DATA_SOURCE=1  and DISTRICT_ID>=#{minAdcode} and DISTRICT_ID<#{maxAdcode} ";
		}
		return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_passive task where  status=0 and DATA_SOURCE=1  and DISTRICT_ID>=#{minAdcode} and DISTRICT_ID<#{maxAdcode} and data_name like #{dataName}  ";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getNearPassiveTaskCount(
			@SqlParameter("taskType") int taskType,
			@SqlParameter("xStart") double xStart,
			@SqlParameter("xEnd") double xEnd,
			@SqlParameter("yStart") double yStart,
			@SqlParameter("yEnd") double yEnd,
			@SqlParameter("dataName") String dataName) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select count(id) TOTAL from task_all_passive task where task_type=#{taskType} and (x>=#{xStart} and x<=#{xEnd}) and (y>=#{yStart} and y<=#{yEnd}) and status=0 and DATA_SOURCE=1 ";
		}
		return "select count(id) TOTAL from task_all_passive task where task_type=#{taskType} and (x>=#{xStart} and x<=#{xEnd}) and (y>=#{yStart} and y<=#{yEnd}) and status=0 and DATA_SOURCE=1  and data_name like #{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object getNearPassiveTask(@SqlParameter("taskType") int taskType,
			@SqlParameter("xStart") double xStart,
			@SqlParameter("xEnd") double xEnd,
			@SqlParameter("yStart") double yStart,
			@SqlParameter("yEnd") double yEnd,
			@SqlParameter("dataName") String dataName,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus ,poi,prename,lost_count from task_all_passive task where task_type=#{taskType} and (x>=#{xStart} and x<=#{xEnd}) and (y>=#{yStart} and y<=#{yEnd}) and status=0 and DATA_SOURCE=1 order by prename ";
		}
		return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus ,poi,prename,lost_count from task_all_passive task where task_type=#{taskType} and (x>=#{xStart} and x<=#{xEnd}) and (y>=#{yStart} and y<=#{yEnd}) and status=0 and DATA_SOURCE=1  and data_name like #{dataName} order by prename";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object getAllotPassiveCountByUser(
			@SqlParameter("dataName") String dataName,
			@SqlParameter("userName") String userName) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select count(id) total from task_all_user task  where status=0 and DATA_SOURCE=1  and user_name=#{userName}";
		}
		return "select count(id) total from task_all_user task  where status=0 and DATA_SOURCE=1  and user_name=#{userName} and data_name like #{dataName}";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object getAllotPassiveByUser(
			@SqlParameter("dataName") String dataName,
			@SqlParameter("userName") String userName,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_user task where  status=0 and DATA_SOURCE=1  and user_name=#{userName}  order by x asc";
		}
		return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_user task where  status=0 and DATA_SOURCE=1  and user_name=#{userName} and data_name like #{dataName} order by x asc";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(paging = @Paging(skip = "start", size = "limit"))
	public Object getAllotPassiveByUserAndRoad(
			@SqlParameter("dataName") String dataName,
			@SqlParameter("userName") String userName,
			@SqlParameter("start") int start, @SqlParameter("limit") int limit) {
		if (dataName == null || dataName.equals("") || dataName.equals("%%")) {
			return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_user task where  status=0 and DATA_SOURCE=1  and user_name=#{userName}  order by prename asc";
		}
		return "select ID,DATA_NAME,TASK_TYPE,X,Y,4 submitStatus,poi,lost_count from task_all_user task where  status=0 and DATA_SOURCE=1  and user_name=#{userName} and data_name like #{dataName} order by prename asc";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select
	public Object polyPassiveTaskByAdcode(
			@SqlParameter("minAdcode") int minAdcode,
			@SqlParameter("maxAdcode") int maxAdcode) {
		return "select count(DISTRICT_ID) TOTAL,DISTRICT_ID from task_all_passive task  where 1=1 and status=0 and DATA_SOURCE=1 and  DISTRICT_ID>=#{minAdcode} and DISTRICT_ID<#{maxAdcode} group by DISTRICT_ID order by  DISTRICT_ID desc";
	}

	@Override
	@Author("yaming.xu")
	@Select
	public Object queryCityCountByUser(@SqlParameter("userName") String userName) {
		// TODO Auto-generated method stub
		return "select count(*) total from (select SUBSTRING(a.ad_code, 1, 4)  from agent_task_base a where user_name =#{userName} group by SUBSTRING(a.ad_code, 1, 4) ) b";
	}

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Insert
	public Object addPassiveTaskBack(@SqlParameter("userName") String userName,
			@SqlParameter("taskId") String taskId,
			@SqlParameter("dataName") String dataName,
			@SqlParameter("errorCode") String errorCode,
			@SqlParameter("comment") String comment,
			@SqlParameter("deviceInfo") String deviceInfo) {
		// TODO Auto-generated method stub
		return "insert into passive_task_back(ID,USER_NAME,TASK_ID,DATA_NAME,ERROR_CODE,COMMENTS,DEVICE_INFO,INSERT_TIME) values(#{AOF.snowflake},#{userName},#{taskId},#{dataName},#{errorCode},#{comment},#{deviceInfo},sysdate())";
	}

}
