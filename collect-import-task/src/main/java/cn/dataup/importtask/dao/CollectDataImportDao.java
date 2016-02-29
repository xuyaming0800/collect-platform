package cn.dataup.importtask.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;

import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;

@Repository
public class CollectDataImportDao {
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Insert
	public Object insertCollectPassivePackageList(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("list") List<CollectPassivePackage> collectPassivePackageList){
		if(collectPassivePackageList==null||collectPassivePackageList.size()>3000){
			throw new BusinessRunException(BusinessExceptionEnum.TASK_PKG_IMPORT_SIZE_ERROR);
		}
		return "insert into collect_passive_package(ID,TASK_PACKAGE_NAME,TASK_PACKAGE_DESC,"
				+ "TASK_PACKAGE_COUNT,TASK_PACKAGE_PAY,TASK_PACKAGE_STATUS,TASK_PACKAGE_TYPE,"
				+ "CREATE_TIME,UPDATE_TIME,ALLOT_USER_ID,COLLECT_USER_ID,ALLOT_END_TIME,"
				+ "TASK_PACKAGE_CATE,VERIFY_MAINTAIN_TIME,ALLOT_MAINTAIN_TIME,VERIFY_FREEZE_TIME,"
				+ "RELEASE_FREEZE_TIME,TASK_PACKAGE_VERIFY_STATUS) values(#{"+ReservedWord.snowflake+"},"
				+ "#{list."+ReservedWord.index+".taskPackageName},#{list."+ReservedWord.index+".taskPackageDesc},#{list."+ReservedWord.index+".taskPackageCount},"
				+ "#{list."+ReservedWord.index+".taskPackagePay},#{list."+ReservedWord.index+".taskPackageStatus},#{list."+ReservedWord.index+".taskPackageType},"
				+ "unix_timestamp(now()),unix_timestamp(now()),null,null,#{list."+ReservedWord.index+".allotEndTime},"
				+ "#{list."+ReservedWord.index+".taskPackageCate},#{list."+ReservedWord.index+".verifyMaintainTime},#{list."+ReservedWord.index+".allotMaintainTime},"
				+ "#{list."+ReservedWord.index+".verifyFreezeTime},#{list."+ReservedWord.index+".releaseFreezeTime},#{list."+ReservedWord.index+".taskPackageVerifyStatus})";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Insert
	public Object insertCollectPassiveTaskList(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("list") List<CollectPassiveTask> collectPassiveTaskList){
		if(collectPassiveTaskList==null||collectPassiveTaskList.size()>3000){
			throw new BusinessRunException(BusinessExceptionEnum.TASK_DETAIL_MPORT_SIZE_ERROR);
		}
		return "insert into collect_passive_task(ID,DATA_NAME,TASK_STATUS,CREATE_TIME,UPDATE_TIME,"
				+ "ALLOT_USER_ID,COLLECT_USER_ID,COLLECT_DATA_NAME,VERIFY_DATA_NAME,POI,"
				+ "PRENAME,ALLOT_END_TIME,TASK_PACKAGE_ID,VERIFY_STATUS,RELEASE_FREEZE_TIME) values("
				+ "#{"+ReservedWord.snowflake+"},#{list."+ReservedWord.index+".dataName},#{list."+ReservedWord.index+".taskStatus},"
				+ "unix_timestamp(now()),unix_timestamp(now()),null,null,"
				+ "#{list."+ReservedWord.index+".collectDataName},null,#{list."+ReservedWord.index+".poi},#{list."+ReservedWord.index+".prename},"
				+ "#{list."+ReservedWord.index+".allotEndTime},#{list."+ReservedWord.index+".taskPackageId},null,#{list."+ReservedWord.index+".releaseFreezeTime})";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Insert
	public Object insertCollectOriginalCoordinateList(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("list") List<CollectOriginalCoordinate> collectOriginalCoordinateList){
		if(collectOriginalCoordinateList==null||collectOriginalCoordinateList.size()>3000){
			throw new BusinessRunException(BusinessExceptionEnum.TASK_DETAIL_MPORT_SIZE_ERROR);
		}
		return "insert into collect_original_coordinate(ID,PASSIVE_ID,ORIGINAL_X,ORIGINAL_Y,ORIGINAL_ADCODE,"
				+ "TASK_SAMPLE_IMG,COORDINATE_STATUS,PACKAGE_ID) values("
				+ "#{"+ReservedWord.snowflake+"},#{list."+ReservedWord.index+".passiveId},#{list."+ReservedWord.index+".originalX},"
				+ "#{list."+ReservedWord.index+".originalY},#{list."+ReservedWord.index+".originalAdcode},#{list."+ReservedWord.index+".taskSampleImg},"
				+ "#{list."+ReservedWord.index+".coordinateStatus},#{list."+ReservedWord.index+".packageId})";
	}
}
