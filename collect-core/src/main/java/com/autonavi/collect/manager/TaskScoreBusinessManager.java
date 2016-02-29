package com.autonavi.collect.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonavi.collect.bean.CollectDicScoreDetail;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.dao.CollectTaskScoreDao;
import com.autonavi.collect.exception.BusinessException;
@Component
public class TaskScoreBusinessManager {
	@Autowired
	private CollectTaskScoreDao collectTaskScoreDao;
	
	@Autowired
	private AllScoreInfo allScoreInfo;
	
	
	public AllScoreInfo getAllScoreInfo() {
		return allScoreInfo;
	}


	/**
	 * 获取所有积分信息
	 * @author xuyaming
	 *
	 */
	@Component
	public class AllScoreInfo implements CollectCore<List<CollectDicScoreDetail>,Integer>{
		@Autowired
		public AllScoreInfo(TaskScoreBusinessManager taskScoreBusinessManager){
			
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<CollectDicScoreDetail> execute(Integer dsKey) throws BusinessException {
			return (List<CollectDicScoreDetail>)collectTaskScoreDao.getAllScoreDetail(dsKey);
		}
		
	}

}
