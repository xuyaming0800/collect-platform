package com.autonavi.collect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.service.CollectTaskJobService;
@Service
public class CollectTaskJobServiceImpl implements CollectTaskJobService {
	@Autowired
    private PassiveTaskBusinessManager passiveTaskBusinessManager;
	
	@Override
	public void releasePassiveTask(CollectPassiveTask collectPassiveTask)throws Exception {
		passiveTaskBusinessManager.getTaskRelease().execute(collectPassiveTask);

	}

}
