package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.constant.CommonConstant.TASK_CLAZZ_STATUS;

@Repository
public class CollectTaskCLazzDao {
	/**
	 * 排序查询分类树
	 * @param dsKey
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskClazz.class)
	public Object selectAllVaildTaskClazz(@SqlParameter("dsKey") Integer dsKey){
		return "select id id,PARENT_ID parentId,INIT_CLAZZ_ID initClazzId,TASK_TYPE taskType,CLAZZ_TYPE clazzType,owner_id ownerId,"
				+ "CLAZZ_NAME clazzName,CLAZZ_IMG_COUNT clazzImgCount,CLAZZ_NEAR_IMG_COUNT clazzNearImgCount,CLAZZ_FAR_IMG_COUNT clazzFarImgCount, "
				+ "CLAZZ_DESC clazzDesc,CLAZZ_STATUS clazzStatus,CLAZZ_PAY clazzPay,CLAZZ_DISTANCE clazzDistance,clazz_index clazzIndex,clazz_pay_type clazzPayType "
			    + "from collect_task_clazz where clazz_status="+TASK_CLAZZ_STATUS.VAILD.getCode()+" and owner_id<3 "
			    + "order by PARENT_ID,clazz_index asc";
	}
	/**
	 * 查询所有分类
	 * @param dsKey
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskClazz.class)
	public Object selectAllTaskClazz(@SqlParameter("dsKey") Integer dsKey){
		return "select id id,PARENT_ID parentId,INIT_CLAZZ_ID initClazzId,TASK_TYPE taskType,CLAZZ_TYPE clazzType,owner_id ownerId,"
				+ "CLAZZ_NAME clazzName,CLAZZ_IMG_COUNT clazzImgCount,CLAZZ_NEAR_IMG_COUNT clazzNearImgCount,CLAZZ_FAR_IMG_COUNT clazzFarImgCount, "
				+ "CLAZZ_DESC clazzDesc,CLAZZ_STATUS clazzStatus,CLAZZ_PAY clazzPay,CLAZZ_DISTANCE clazzDistance,clazz_index clazzIndex,clazz_pay_type clazzPayType "
			    + "from collect_task_clazz where owner_id<3 ";
	}
	/**
	 * 插入分类树
	 * @param dsKey
	 * @param collectTaskClazz
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Insert
	public Object insertTaskClazz(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("collectTaskClazz") CollectTaskClazz collectTaskClazz){
		return "insert into collect_task_clazz ( "
				+ "id,PARENT_ID,TASK_TYPE,"
				+ "CLAZZ_TYPE,CLAZZ_NAME,CLAZZ_IMG_COUNT,"
				+ "CLAZZ_NEAR_IMG_COUNT,CLAZZ_FAR_IMG_COUNT,"
				+ "CLAZZ_DESC,CLAZZ_STATUS,CLAZZ_PAY,CLAZZ_DISTANCE,"
				+ "CLAZZ_INDEX,CLAZZ_PAY_TYPE,OWNER_ID) "
				+ "values(#{"+ ReservedWord.snowflake+"},"
						+ "#{collectTaskClazz.parentId},"
						+ "#{collectTaskClazz.taskType},"
						+ "#{collectTaskClazz.clazzType},"
						+ "#{collectTaskClazz.clazzName},"
						+ "#{collectTaskClazz.clazzImgCount},"
						+ "#{collectTaskClazz.clazzNearImgCount},"
						+ "#{collectTaskClazz.clazzFarImgCount},"
						+ "#{collectTaskClazz.clazzDesc},"
						+ "#{collectTaskClazz.clazzStatus},"
						+ "#{collectTaskClazz.clazzPay},"
						+ "#{collectTaskClazz.clazzDistance},"
						+ "#{collectTaskClazz.clazzIndex},"
						+ "#{collectTaskCLazz.clazzPayType},"
						+ "#{collectTaskCLazz.ownerId})";
	}
	/**
	 * 更新分类信息-不支持钱数和类型的变更
	 * @param dsKey
	 * @param collectTaskClazz
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Update
	public Object updateTaskClazzById(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("collectTaskClazz") CollectTaskClazz collectTaskClazz){
		StringBuffer sb=new StringBuffer();
		if(collectTaskClazz.getClazzName()!=null){
			sb.append(",CLAZZ_NAME=#{collectTaskClazz.clazzName}");
		}
		if(collectTaskClazz.getClazzImgCount()!=null){
			sb.append(",CLAZZ_IMG_COUNT=#{collectTaskClazz.clazzImgCount}");
		}
		if(collectTaskClazz.getClazzNearImgCount()!=null){
			sb.append(",CLAZZ_NEAR_IMG_COUNT=#{collectTaskClazz.clazzNearImgCount}");
		}
		if(collectTaskClazz.getClazzFarImgCount()!=null){
			sb.append(",CLAZZ_FAR_IMG_COUNT=#{collectTaskClazz.clazzFarImgCount}");
		}
		if(collectTaskClazz.getClazzDesc()!=null){
			sb.append(",CLAZZ_DESC=#{collectTaskClazz.clazzDesc}");
		}
		if(collectTaskClazz.getClazzDistance()!=null){
			sb.append(",CLAZZ_DISTANCE=#{collectTaskClazz.clazzDistance}");
		}
		if(collectTaskClazz.getClazzPayType()!=null){
			sb.append(",CLAZZ_PAY_TYPE=#{collectTaskClazz.clazzPayType}");
		}
		
		return "update collect_task_clazz set id=id "
				+ sb.toString()
				+ " where id=#{collectTaskClazz.id}";
	}
	/**
	 * 根据pid获取分类列表-排序
	 * @param dsKey
	 * @param pid
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskClazz.class)
	public Object selectTaskClazzByPid(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid){
		String temp="";
		if(pid==null){
			temp=" where PARENT_ID is null ";
		}else{
			temp=" where PARENT_ID=#{pid} ";
		}
		return "select id id,PARENT_ID parentId,TASK_TYPE taskType,CLAZZ_TYPE clazzType,"
				+ "CLAZZ_NAME clazzName,CLAZZ_IMG_COUNT clazzImgCount,CLAZZ_NEAR_IMG_COUNT clazzNearImgCount,CLAZZ_FAR_IMG_COUNT clazzFarImgCount, "
				+ "CLAZZ_DESC clazzDesc,CLAZZ_STATUS clazzStatus,CLAZZ_PAY clazzPay,CLAZZ_DISTANCE clazzDistance,clazz_index clazzIndex,clazz_pay_type clazzPayType "
			    + "from collect_task_clazz "
			    + temp
			    + " order by clazz_index asc ";
	}
	/**
	 * 根据ID获取分类信息
	 * @param dsKey
	 * @param id
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskClazz.class)
	public Object selectTaskClazzById(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("id") Long id){
		return "select id id,PARENT_ID parentId,TASK_TYPE taskType,CLAZZ_TYPE clazzType,"
				+ "CLAZZ_NAME clazzName,CLAZZ_IMG_COUNT clazzImgCount,CLAZZ_NEAR_IMG_COUNT clazzNearImgCount,CLAZZ_FAR_IMG_COUNT clazzFarImgCount,owner_id ownerId, "
				+ "CLAZZ_DESC clazzDesc,CLAZZ_STATUS clazzStatus,CLAZZ_PAY clazzPay,CLAZZ_DISTANCE clazzDistance,clazz_index clazzIndex,clazz_pay_type clazzPayType "
			    + "from collect_task_clazz "
			    + " where id=#{id} and owner_id<3 ";
	}
	/**
	 * 根据父级和排序类型获取分了信息(有效的)
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskClazz.class)
	public Object selectTaskClazzByPidIndex(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid,@SqlParameter("index") Integer index){
		String temp="";
		if(pid==null){
			temp=" PARENT_ID is null ";
		}else{
			temp=" PARENT_ID=#{pid} ";
		}
		return "select id id,PARENT_ID parentId,TASK_TYPE taskType,CLAZZ_TYPE clazzType,"
				+ "CLAZZ_NAME clazzName,CLAZZ_IMG_COUNT clazzImgCount,CLAZZ_NEAR_IMG_COUNT clazzNearImgCount,CLAZZ_FAR_IMG_COUNT clazzFarImgCount, "
				+ "CLAZZ_DESC clazzDesc,CLAZZ_STATUS clazzStatus,CLAZZ_PAY clazzPay,CLAZZ_DISTANCE clazzDistance,clazz_index clazzIndex,clazz_pay_type clazzPayType "
			    + "from collect_task_clazz "
			    + " where "+temp+" and clazz_index=#{index} and CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode();
	}
	/**
	 * 根据父级和排序类型获取下一个记录(有效的)
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.column, resultType = Integer.class)
	public Object selectNextTaskClazzByPidIndex(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid,@SqlParameter("index") Integer index){
		String temp="";
		if(pid==null){
			temp=" PARENT_ID is null ";
		}else{
			temp=" PARENT_ID=#{pid} ";
		}
		return "select min(clazz_index) clazzIndex  "
			    + "from collect_task_clazz "
			    + " where "+temp+" and clazz_index>#{index} and CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode();
	}
	/**
	 * 根据父级和排序类型获取上一个记录(有效的)
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.column, resultType = Integer.class)
	public Object selectPervTaskClazzByPidIndex(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid,@SqlParameter("index") Integer index){
		String temp="";
		if(pid==null){
			temp=" PARENT_ID is null ";
		}else{
			temp=" PARENT_ID=#{pid} ";
		}
		return "select max(clazz_index) clazzIndex "
			    + "from collect_task_clazz "
			    + " where "+temp+" and clazz_index<#{index} and CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode();
	}
	/**
	 * 获取本级最大的序列号
	 * @param dsKey
	 * @param pid
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.column, resultType = Integer.class)
	public Object selectMaxIndexPid(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid){
		String temp="";
		if(pid==null){
			temp=" PARENT_ID is null ";
		}else{
			temp=" PARENT_ID=#{pid} ";
		}
		return "select max(clazz_index) clazzIndex "
			    + "from collect_task_clazz "
			    + " where "+temp+" and  CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode();
	}
	/**
	 * 更新菜单顺序
	 * @param dsKey
	 * @param id
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Update
	public Object updateTaskClazzIndex(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("id") Long id,@SqlParameter("index") Integer index){
		return "update collect_task_clazz set clazz_index=#{index} "
				+ " where  id=#{id} and  CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode();
	}
	/**
	 * 逻辑删除后更新排序
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Update
	public Object updateTaskClazzAfterLogicDel(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid,@SqlParameter("index") Integer index){
		String temp="";
		if(pid==null){
			temp=" PARENT_ID is null ";
		}else{
			temp=" PARENT_ID=#{pid} ";
		}
		return "update collect_task_clazz set clazz_index=clazz_index-1 "
				+ " where  "+temp+" and  CLAZZ_STATUS="+TASK_CLAZZ_STATUS.VAILD.getCode()+" and clazz_index>#{index} ";
	}
	
	/**
	 * 状态更新
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Update
	public Object updateTaskClazzStatusById(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("id") Long id,@SqlParameter("newStatus") Integer newStatus,
			@SqlParameter("currentStatus") Integer currentStatus,@SqlParameter("index") Integer index){
		return "update collect_task_clazz set clazz_status=#{newStatus} "
				+ ",clazz_index=#{index} "
				+ " where  id=#{id} and  CLAZZ_STATUS=#{currentStatus} ";
	}
	
	/**
	 * 状态更新
	 * @param dsKey
	 * @param pid
	 * @param index
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Update
	public Object updateTaskClazzStatusByPid(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("pid") Long pid,@SqlParameter("newStatus") Integer newStatus,@SqlParameter("currentStatus") Integer currentStatus){
		return "update collect_task_clazz set clazz_status=#{newStatus} "
				+ " where  PARENT_ID=#{pid} and  CLAZZ_STATUS=#{currentStatus} ";
	}
	

}
