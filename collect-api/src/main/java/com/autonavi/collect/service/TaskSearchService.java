package com.autonavi.collect.service;

import java.util.List;

import com.autonavi.collect.entity.ActiveTaskAroundSearchEntity;
import com.autonavi.collect.entity.ActiveTaskAroundSearchResultEntity;
import com.autonavi.collect.entity.SearchPassiveTaskFacetResult;
import com.autonavi.collect.entity.SearchPassiveTaskResultEntity;
import com.autonavi.collect.entity.SearchTaskQueryEntity;

//dubbo service interface
public interface TaskSearchService {
	/**
	 * 检测附近是否有已经采集点
	 */
	boolean activeTaskAroundSearchCheck(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);
	
	/**
	 * 检测附近是否有已经采集点-H5 版本
	 */
	boolean activeH5TaskAroundSearchCheck(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);

	/**
	 * 主动任务周边搜索
	 * 
	 * @param activeTaskSearchEntity
	 * @return
	 */
	List<ActiveTaskAroundSearchResultEntity> activeTaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);
	
	/**
	 * 主动任务周边搜索-H5 版本
	 * 
	 * @param activeTaskSearchEntity
	 * @return
	 */
	List<ActiveTaskAroundSearchResultEntity> activeH5TaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);
	
	/**
	 * 主动任务周边搜索-母库 版本
	 * 
	 * @param activeTaskSearchEntity
	 * @return
	 */
	List<ActiveTaskAroundSearchResultEntity> activeTaskResourceAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);
	
	/**
	 * 主动任务周边搜索-保存，提交，审核通过
	 * 
	 * @param activeTaskSearchEntity
	 * @return
	 */
	List<ActiveTaskAroundSearchResultEntity> vaildActiveTaskAroundSearch(
			ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity);

	/**
	 * 查询坐标，range半径内的数据
	 * 
	 * @param x
	 * @param y
	 * @param range
	 *            半径，单位米
	 * @return
	 */
	SearchPassiveTaskResultEntity aroundTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

	SearchPassiveTaskResultEntity districtTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

	SearchPassiveTaskResultEntity aroundNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

	SearchPassiveTaskResultEntity districtNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity);
	
	SearchPassiveTaskResultEntity aroundNoUserAllTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

	/**
	 * 统计
	 * 
	 * @param searchTaskQueryEntity
	 * @return
	 */
	List<SearchPassiveTaskFacetResult> districtFacetTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

	/**
	 * 统计
	 * 
	 * @param searchTaskQueryEntity
	 * @return
	 */
	List<SearchPassiveTaskFacetResult> districtFacetNoUserTask(
			SearchTaskQueryEntity searchTaskQueryEntity);

}
