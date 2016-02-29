package com.autonavi.collect.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;

//id bigint(32) pk 
//base_id bigint(32) 
//img_name varchar(50) 
//collect_x double 
//collect_y double 
//collect_adcode int(6) 
//collect_offset_x double 
//collect_offset_y double 
//gps_accuracy double 
//gps_count int(2) 
//gps_time bigint(32) 
//location_type int(1) 
//position double 
//position_x double 
//position_y double 
//position_z double 
//photo_time bigint(32)
@Component
public class CollectTaskImageDao {

	// 注意：因为分区是靠info.userId决定的，所以info.userId落到哪个分区，
	// 就往哪个分区的collect_task_img写数据，目前collect_task_img虽然分区但是没有加userId字段
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("collectTaskImg") CollectTaskImg collectTaskImg) {
		String sql = "insert into collect_task_img(id, "
				+ "base_id, "
				+ "img_name, "
				+ "collect_x, "
				+ "collect_y, "
				+ "collect_adcode, "
				+ "collect_offset_x, collect_offset_y, "
				+ "gps_accuracy, "
				+ "gps_count, "
				+ "gps_time, "
				+ "location_type, "
				+ "position, "
				+ "position_x, "
				+ "position_y, "
				+ "position_z, photo_time,image_index) "
				+ "values  (#{AOF.snowflake}"
				+ ", #{collectTaskBase.id}"
				+ ", #{collectTaskImg.imgName}"
				+ ", #{collectTaskImg.collectX}"
				+ ", #{collectTaskImg.collectY}"
				+ ", #{collectTaskImg.collectAdcode}"
				+ ", #{collectTaskImg.collectOffsetX}"
				+ ", #{collectTaskImg.collectOffsetY}"
				+ ", #{collectTaskImg.gpsAccuracy}"
				+ ", #{collectTaskImg.gpsCount}"
				+ ", #{collectTaskImg.gpsTime}"
				+ ", #{collectTaskImg.locationType}"
				+ ", #{collectTaskImg.position}"
				+ ", #{collectTaskImg.positionX}"
				+ ", #{collectTaskImg.positionY}"
				+ ", #{collectTaskImg.positionZ}"
				+ ", #{collectTaskImg.photoTime}"
				+ ", #{collectTaskImg.imageIndex})";
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.beanList, resultType = CollectTaskImg.class)
	public Object selectByBaseId(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return "select img_name imgName from collect_task_img where base_id=#{collectTaskBase.id} order by id asc ";
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("collectTaskImgList") List<CollectTaskImg> collectTaskImgList) {
		String sql = "insert into collect_task_img(id, "
				+ "base_id, "
				+ "img_name, "
				+ "collect_x, "
				+ "collect_y, "
				+ "collect_adcode, "
				+ "collect_offset_x, collect_offset_y, "
				+ "gps_accuracy, "
				+ "gps_count, "
				+ "gps_time, "
				+ "location_type, "
				+ "position, "
				+ "position_x, "
				+ "position_y, "
				+ "position_z, photo_time,image_index,image_h5_id,task_clazz_id,image_batch_id,"
				+ "image_flag,image_status,temp_batch_id) "
				+ "values  (#{AOF.snowflake}"
				+ ", #{collectTaskBase.id}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imgName}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".collectX}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".collectY}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".collectAdcode}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".collectOffsetX}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".collectOffsetY}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".gpsAccuracy}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".gpsCount}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".gpsTime}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".locationType}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".position}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".positionX}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".positionY}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".positionZ}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".photoTime}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imageIndex}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imageH5Id}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".taskClazzId}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imageBatchId}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imageFlag}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".imageStatus}"
				+ ", #{collectTaskImgList."+ReservedWord.index+".tempBatchId})"
				+ " ON DUPLICATE KEY update img_name=#{collectTaskImgList."+ReservedWord.index+".imgName}, "
				+ " collect_x=#{collectTaskImgList."+ReservedWord.index+".collectX},"
				+ " collect_y=#{collectTaskImgList."+ReservedWord.index+".collectY},"
				+ " collect_adcode=#{collectTaskImgList."+ReservedWord.index+".collectAdcode},"
				+ " collect_offset_x=#{collectTaskImgList."+ReservedWord.index+".collectOffsetX},"
				+ " collect_offset_y=#{collectTaskImgList."+ReservedWord.index+".collectOffsetY},"
				+ " gps_accuracy=#{collectTaskImgList."+ReservedWord.index+".gpsAccuracy},"
				+ " gps_count=#{collectTaskImgList."+ReservedWord.index+".gpsCount},"
				+ " gps_time=#{collectTaskImgList."+ReservedWord.index+".gpsTime},"
				+ " location_type=#{collectTaskImgList."+ReservedWord.index+".locationType},"
				+ " position=#{collectTaskImgList."+ReservedWord.index+".position},"
				+ " position_x=#{collectTaskImgList."+ReservedWord.index+".positionX},"
				+ " position_y=#{collectTaskImgList."+ReservedWord.index+".positionY},"
				+ " position_z=#{collectTaskImgList."+ReservedWord.index+".positionZ},"
				+ " photo_time=#{collectTaskImgList."+ReservedWord.index+".photoTime},"
				+ " image_flag=#{collectTaskImgList."+ReservedWord.index+".imageFlag},"
				+ " image_status=#{collectTaskImgList."+ReservedWord.index+".imageStatus},"
				+ " image_index=#{collectTaskImgList."+ReservedWord.index+".imageIndex},"
//				+ " task_clazz_id=#{collectTaskImgList."+ReservedWord.index+".taskClazzId},"
				+ " image_h5_id=#{collectTaskImgList."+ReservedWord.index+".imageH5Id},"
				+ " temp_Batch_Id=#{collectTaskImgList."+ReservedWord.index+".tempBatchId}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object batchUpdateToUnUseByTemp(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("collectTaskImgList") List<CollectTaskImg> collectTaskImgList) {
		return " update collect_task_img set IMAGE_STATUS="+CommonConstant.TASK_IMG_STATUS.UNUSE.getCode()+ " "
				+ " where  base_id=#{collectTaskBase.id} and image_batch_id=#{collectTaskImgList."+ReservedWord.index+".imageBatchId} and temp_Batch_Id<>#{collectTaskImgList."+ReservedWord.index+".tempBatchId}" ;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object batchUpdateToUnUse(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("batchIdList") List<Long> batchIdList) {
		return " update collect_task_img set IMAGE_STATUS="+CommonConstant.TASK_IMG_STATUS.UNUSE.getCode()+ " "
				+ " where base_id=#{collectTaskBase.id} and IMAGE_BATCH_ID=#{batchIdList."+ReservedWord.index+"}" ;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object batchUpdateToVerifyStatus(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("batchIdList") List<Long> batchIdList,@SqlParameter("currentStatus") Integer currentStatus,@SqlParameter("status") Integer status) {
		return " update collect_task_img set IMAGE_STATUS=#{status} "
				+ " where base_id=#{collectTaskBase.id} and IMAGE_BATCH_ID=#{batchIdList."+ReservedWord.index+"} and (image_status="+TASK_IMG_STATUS.USE.getCode()+" or "
		        + " image_status=#{currentStatus})" ;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object batchUpdateToUnUse(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return " update collect_task_img set IMAGE_STATUS="+CommonConstant.TASK_IMG_STATUS.UNUSE.getCode()+ " "
				+ " where base_id=#{collectTaskBase.id} " ;
	}

}
