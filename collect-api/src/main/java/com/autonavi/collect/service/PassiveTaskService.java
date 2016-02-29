package com.autonavi.collect.service;

import java.util.List;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.entity.CollectTaskReceiveReturnEntity;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectTaskSubmitReturnEntity;

//dubbo service interface
public interface PassiveTaskService {
	CollectTaskBase taskSave(CollectTaskSaveEntity saveEntity) throws Exception;

	CollectTaskSubmitReturnEntity taskSubmit(CollectTaskSubmitEntity submitEntity) throws Exception;

	CollectTaskBase taskReceive(CollectTaskBase collectTaskBase) throws Exception;
	
	CollectTaskReceiveReturnEntity taskPackageReceive(CollectBasePackage collectBasePackage) throws Exception;
	
	CollectTaskSubmitReturnEntity taskPackageSubmit(CollectBasePackage collectBasePackage,Boolean isCheckRedis)throws Exception;
	
	public void taskPackageSyncAudit(CollectTaskSubmitReturnEntity entity)throws Exception;
	public void taskSyncWaterImage(List<CollectTaskImg> collectTaskImgList,String imgurl,CollectTaskSubmitReturnEntity entity)throws Exception;
	public Integer taskDelete(CollectBasePackage collectBasePackage) throws Exception;
}
