package cn.dataup.importtask.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.sharding.dao.DaoHelper;
import cn.dataup.importtask.constant.ImportTaskConstant;
import cn.dataup.importtask.dao.CollectDataImportDao;
import cn.dataup.importtask.entity.ImportTaskEntity;
import cn.dataup.importtask.service.ImportDemoService;

import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
@Service
public class ImportDemoServiceImpl implements ImportDemoService {
	@Autowired
	private CollectDataImportDao collectDataImportDao;

	@Override
	public boolean importTasks(ArrayList<ImportTaskEntity> importTaskEntityList) {
		// 先插入任务包表
		try{
			importTaskPackage(importTaskEntityList);
			Long[] packageId = DaoHelper.getPrimaryKeys();
			int i = 0;
			StopWatch sw = new StopWatch();
			for (ImportTaskEntity entity : importTaskEntityList) {
				sw.reset();
				sw.start();
				List<CollectPassiveTask> collectPassiveTaskList = entity.getCollectPassiveTaskList();
				List<CollectOriginalCoordinate> collectOriginalCoordinateList = entity
						.getCollectOriginalCoordinateList();
				//设置逻辑外键
				for (CollectPassiveTask task : collectPassiveTaskList) {
					task.setTaskPackageId(packageId[i]);
				}
				//设置逻辑外键
				for (CollectOriginalCoordinate cood : collectOriginalCoordinateList) {
					cood.setPackageId(packageId[i]);
				}
				collectDataImportDao.insertCollectPassiveTaskList(
						ImportTaskConstant.getCollectDataSourceKey(), collectPassiveTaskList);
				collectDataImportDao.insertCollectOriginalCoordinateList(
						ImportTaskConstant.getCollectDataSourceKey(), collectOriginalCoordinateList);
				i++;
				sw.stop();
				System.out.println("换库中...task_id="+entity.toString()+"时间："+sw);
			}
		}catch(Exception e){
			e.printStackTrace();
			//判断一个方法是否抛出异常
			return false;
		}
		//throw new RuntimeException("导入数据测试完毕，回滚数据");
		return true;
	}

	/**
	 * 先导入任务包表
	 * @param importTaskEntityList
	 */
	private void importTaskPackage(
			ArrayList<ImportTaskEntity> importTaskEntityList) {
		// 先插入任务包表
		if(importTaskEntityList!=null && importTaskEntityList.size()>0){//为空就不执行了
			List<CollectPassivePackage> collectPassivePackageList = new ArrayList<CollectPassivePackage>();
			for (ImportTaskEntity entity : importTaskEntityList) {
				collectPassivePackageList.add(entity.getCollectPassivePackage());
			}
			collectDataImportDao.insertCollectPassivePackageList(
					ImportTaskConstant.getCollectDataSourceKey(),
					collectPassivePackageList);
		}
	}

}
