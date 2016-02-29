package com.autonavi.collect.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectOriginalCoordinate;

@Repository
public class CollectOriginalCoordinateDao {
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object checkBaseCoordExist(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage){
		return "select count(0) count from collect_base_original_coordinate "
				+ " where package_id=#{collectBasePackage.passivePackageId}";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectOriginalCoordinate.class)
	public Object selectOriginalCoordinateByPid(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage){
		return "select id,passive_id passiveId,"
				+ "task_Sample_Img taskSampleImg,original_X originalX,"
				+ "original_Y originalY,original_Adcode originalAdcode,"
				+ "coordinate_Status coordinateStatus,package_Id packageId "
				+ " from collect_original_coordinate where "
				+ " package_id=#{collectBasePackage.passivePackageId}";
	}
	@Author("yaming.xu")
	@Shard(indexName = "USER_OWNER_ID_INDEX", indexColumn = "allotUserId,ownerId")
	@Insert
	public Object insertBaseCoordinates(@SqlParameter("list") List<CollectOriginalCoordinate> list,@SqlParameter("allotUserId") Long allotUserId,@SqlParameter("ownerId") Long ownerId){
		String sql = "insert into collect_base_original_coordinate values (#{"
				+ ReservedWord.snowflake + "},#{list." + ReservedWord.index
				+ ".passiveId},#{list." + ReservedWord.index
				+ ".originalX},#{list." + ReservedWord.index
				+ ".originalY},#{list." + ReservedWord.index
				+ ".originalAdcode},#{list." + ReservedWord.index
				+ ".taskSampleImg},#{list." + ReservedWord.index
				+ ".coordinateStatus},#{list." + ReservedWord.index
				+ ".packageId}) ";
		return sql;
	}

}
