package com.autonavi.collect.entity;

import com.autonavi.collect.bean.CollectAllotBaseUser;
import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectTaskBase;



/**
 * 被动任务整体信息
 * @author chunsheng.zhang
 *
 */
public class SearchPassiveTaskEntity  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2866553638409569L;
	
	private CollectBasePackage collectBasePackage;
	
	private CollectAllotBaseUser collectAllotBaseUser;
	
	private CollectOriginalCoordinate collectOriginalCoordinate;
	
	
	private Double distance;



	public CollectOriginalCoordinate getCollectOriginalCoordinate() {
		return collectOriginalCoordinate;
	}

	public void setCollectOriginalCoordinate(
			CollectOriginalCoordinate collectOriginalCoordinate) {
		this.collectOriginalCoordinate = collectOriginalCoordinate;
	}


	public CollectAllotBaseUser getCollectAllotBaseUser() {
		return collectAllotBaseUser;
	}

	public void setCollectAllotBaseUser(CollectAllotBaseUser collectAllotBaseUser) {
		this.collectAllotBaseUser = collectAllotBaseUser;
	}

	public CollectBasePackage getCollectBasePackage() {
		return collectBasePackage;
	}

	public void setCollectBasePackage(CollectBasePackage collectBasePackage) {
		this.collectBasePackage = collectBasePackage;
	}
	
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	
}
