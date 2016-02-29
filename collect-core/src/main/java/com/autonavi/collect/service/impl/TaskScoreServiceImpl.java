package com.autonavi.collect.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.autonavi.collect.bean.CollectDicScoreDetail;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.manager.TaskScoreBusinessManager;
import com.autonavi.collect.service.TaskScoreService;

@Service
public class TaskScoreServiceImpl implements TaskScoreService {

	private Logger logger = LogManager.getLogger(this.getClass());

	@Resource
	public TaskScoreBusinessManager taskScoreBusinessManager;

	@Override
	public List<CollectDicScoreDetail> getAllScoreInfo() {
		try {
			return taskScoreBusinessManager.getAllScoreInfo().execute(CommonConstant.getSingleDataSourceKey());
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(e.getMessage(), e);
		}
	}

}
