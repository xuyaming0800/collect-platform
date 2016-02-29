package cn.dataup.importtask.entity;

import java.util.List;

import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;

public class ImportTaskEntity {
	private CollectPassivePackage collectPassivePackage;
	private List<CollectPassiveTask> collectPassiveTaskList;
	private List<CollectOriginalCoordinate> collectOriginalCoordinateList;

	public CollectPassivePackage getCollectPassivePackage() {
		return collectPassivePackage;
	}

	public void setCollectPassivePackage(
			CollectPassivePackage collectPassivePackage) {
		this.collectPassivePackage = collectPassivePackage;
	}

	public List<CollectPassiveTask> getCollectPassiveTaskList() {
		return collectPassiveTaskList;
	}

	public void setCollectPassiveTaskList(
			List<CollectPassiveTask> collectPassiveTaskList) {
		this.collectPassiveTaskList = collectPassiveTaskList;
	}

	public List<CollectOriginalCoordinate> getCollectOriginalCoordinateList() {
		return collectOriginalCoordinateList;
	}

	public void setCollectOriginalCoordinateList(
			List<CollectOriginalCoordinate> collectOriginalCoordinateList) {
		this.collectOriginalCoordinateList = collectOriginalCoordinateList;
	}

}
