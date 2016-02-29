package cn.dataup.collect.shape.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectShapeInfo;
import com.autonavi.collect.bean.CollectTaskClazzShape;
import com.autonavi.collect.constant.CommonConstant;

@Repository
public class CollectShapeDao {
	/**
	 * 获取所有有效的shape文件信息
	 * @param dsKey
	 * @return
	 */
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectShapeInfo.class)
	public Object getAllValidShapes(
			@SqlParameter("dsKey") Integer dsKey) {
		return "select id,shape_name shapeName,shape_path shapePath,status from collect_shape_info where status= "+CommonConstant.SHAPE_FILE_STATUS.VALID.getCode();
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskClazzShape.class)
	public Object getAllValidTaskClazzShapes(
			@SqlParameter("dsKey") Integer dsKey) {
		return "select id,shape_id shapeId,task_Clazz_Id taskClazzId,owner_id ownerId,status from collect_task_clazz_shape where status= "+CommonConstant.TASK_CLAZZ_SHAPE_STATUS.VALID.getCode();
	}
}
