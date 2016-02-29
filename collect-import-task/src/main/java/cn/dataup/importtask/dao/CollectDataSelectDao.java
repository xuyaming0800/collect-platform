package cn.dataup.importtask.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;
import cn.dataup.importtask.entity.InputDataProjectName;

import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;

@Repository
public class CollectDataSelectDao {
	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = InputDataProjectName.class)
	public Object selectCollectPassivePackageListWithLimit(
			@SqlParameter("dsKey") Integer dsKey, String tableName,
			@SqlParameter("start") Long start,
			@SqlParameter("limit") Integer limit,
			@SqlParameter("geoOrImport") Integer geoOrImport) {
		// 表明不存在或者为空
		if (tableName == null || tableName.length() < 0) {
			throw new BusinessRunException(
					BusinessExceptionEnum.TASK_PKG_IMPORT_SIZE_ERROR);
		}
		String sql = "select task_id taskId,task_package_name taskPackageName,task_demo_pic taskDemoPic,task_city taskCity,"
				+ "task_type taskType,address_geocode addressGeocode,address_all addressAll,coord_x coordX,coord_y coordY,"
				+ "coord_type coordType,X x,Y y,geocode_level geocodeLevel,geocode_score geocodeScore,project_name projectName,"
				+ "ad_name adName,ad_start_time adStartTime,ad_end_time adEndTime,import_status importStatus,geo_status geoStatus,money money,ad_type adType from ";
		if(geoOrImport==1)
			sql += tableName + " where (coord_x is null and coord_y is null) or geo_status=2 limit #{start},#{limit}";
		else if(geoOrImport==2)
			sql += tableName + " where geo_status=1 and (import_status is null or import_status = 2) limit #{start},#{limit}";
		return sql;
	}

	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = InputDataProjectName.class)
	public Object selectCollectPassivePackageListWithOutLimit(
			@SqlParameter("dsKey") Integer dsKey, String tableName, Integer geoOrImport) {
		// 表明不存在或者为空
		if (tableName == null || tableName.length() < 0) {
			throw new BusinessRunException(
					BusinessExceptionEnum.TASK_PKG_IMPORT_SIZE_ERROR);
		}
		String sql = "select task_id taskId,task_package_name taskPackageName,task_demo_pic taskDemoPic,task_city taskCity,"
				+ "task_type taskType,address_geocode addressGeocode,address_all addressAll,coord_x coordX,coord_y coordY,"
				+ "coord_type coordType,X x,Y y,geocode_level geocodeLevel,geocode_score geocodeScore,project_name projectName,"
				+ "ad_name adName,ad_start_time adStartTime,ad_end_time adEndTime,import_status importStatus,geo_status geoStatus,money money,ad_type adType from ";
		if(geoOrImport==1)
			sql += tableName + " where (coord_x is null and coord_y is null) or geo_status=2 ";
		else if(geoOrImport==2)
			sql += tableName + " where geo_status=1 and (import_status is null or import_status = 2)";
		return sql;
	}

	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object getCountByTableName(@SqlParameter("dsKey") Integer dsKey,
			String tableName, Integer geoOrImport) {
		if(geoOrImport==1)//此处国标的是coordXY，需要偏移计算后获取xy
			return "select count(1) as count from " + tableName + " where (coord_x is null and coord_y is null) or geo_status=2";
		else {
			return "select count(1) as count from " + tableName + " where geo_status=1 and (import_status is null or import_status = 2)";
		}
	}

	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Select(collectionType = CollectionType.columnList, resultType = String.class)
	public Object getTables(@SqlParameter("dsKey") Integer dsKey) {
		return "show tables";
	}

	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Update
	public Object insertCollectPassivePackageList(
			@SqlParameter("dsKey") Integer dsKey,
			@SqlParameter("list") List<InputDataProjectName> inputDataProjectNameList, String tableName) {
		StringBuffer buffer = new StringBuffer(
				"update "+tableName+" set ");
		/*buffer.append("task_package_name=#{list." + ReservedWord.index
				+ ".taskPackageName},");
		buffer.append("task_demo_pic=#{list." + ReservedWord.index
				+ ".taskDemoPic},");
		buffer.append("task_type=#{list." + ReservedWord.index + ".taskType},");
		buffer.append("task_city=#{list." + ReservedWord.index + ".taskCity},");*/
		buffer.append("address_geocode=#{list." + ReservedWord.index
				+ ".addressGeocode},");
		/*buffer.append("address_all=#{list." + ReservedWord.index
				+ ".addressAll},");*/
		buffer.append("coord_x=#{list." + ReservedWord.index + ".coordX},");
		buffer.append("coord_y=#{list." + ReservedWord.index + ".coordY},");
		buffer.append("import_status=#{list." + ReservedWord.index + ".importStatus},");
		buffer.append("geo_status=#{list." + ReservedWord.index + ".geoStatus},");
		/*buffer.append("coord_type=#{list." + ReservedWord.index
				+ ".coordType},");*/
		buffer.append("x=#{list." + ReservedWord.index + ".x},");
		buffer.append("y=#{list." + ReservedWord.index + ".y}");
		/*buffer.append("geocode_level=#{list." + ReservedWord.index
				+ ".geocodeLevel},");
		buffer.append("geocode_score=#{list." + ReservedWord.index
				+ ".geocodeScore},");
		buffer.append("project_name=#{list." + ReservedWord.index
				+ ".projectName},");
		buffer.append("ad_name=#{list." + ReservedWord.index + ".adName},");
		buffer.append("ad_start_time=#{list." + ReservedWord.index
				+ ".adStartTime},");
		buffer.append("ad_type=#{list." + ReservedWord.index
				+ ".adType},");
		buffer.append("money=#{list." + ReservedWord.index
				+ ".money},");
		buffer.append("ad_end_time=#{list." + ReservedWord.index
				+ ".adEndTime}");*/
		buffer.append(" where task_id=#{list." + ReservedWord.index
				+ ".taskId}");
		return buffer.toString();
	}
	
	@Author("xusheng.liu")
	@SingleDataSource(keyName = "dsKey")
	@Update
	public Object updateImportStatusByTaskIdsAndImportStatus(@SqlParameter("dsKey") Integer dsKey,@SqlParameter("ids") String ids, String tableName, @SqlParameter("isf") Integer importStatusFlag) {
		return "update "+tableName+" set import_status = #{isf} where task_id in(${ids})";
	}
	
}
