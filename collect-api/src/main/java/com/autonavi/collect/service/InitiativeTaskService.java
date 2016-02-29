package com.autonavi.collect.service;

import java.util.List;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectTaskSubmitReturnEntity;

public interface InitiativeTaskService {
	public CollectTaskBase taskSave(CollectTaskSaveEntity entity) throws Exception;
	public CollectTaskSubmitReturnEntity taskSubmit(CollectTaskSubmitEntity entity) throws Exception;
	public Integer taskDelete(CollectBasePackage collectBasePackage) throws Exception;
	public void taskPackageSyncAudit(CollectTaskSubmitReturnEntity entity)throws Exception;
	public void taskSyncWaterImage(List<CollectTaskImg> collectTaskImgList,String imgurl,CollectTaskSubmitReturnEntity entity)throws Exception;
}
