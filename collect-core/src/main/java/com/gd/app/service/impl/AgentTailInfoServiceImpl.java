package com.gd.app.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gd.app.dao.AgentTailInfoDao;
import com.gd.app.entity.AgentTailInfo;
import com.gd.app.service.AgentTailInfoService;

//@Service("agentTailInfoService")
public class AgentTailInfoServiceImpl implements AgentTailInfoService {
	
	private Logger logger = LogManager.getLogger(AgentTailInfoServiceImpl.class);
	
	@Resource private AgentTailInfoDao dao;

	/*@Override
	public void saveAgentTailInfo(String userName, String x, String y,
			String imei, String altitude, String gpsTime, String speed,
			String direction, String pointCount, String pointAccuracy,
			String offsetX, String offsetY) throws Exception {
		logger.info("客户端保存轨迹信息");

	}*/

	@Override
	public void batchSaveAgentTailInfos(List<AgentTailInfo> agentTailInfos)
			throws Exception {
		logger.info("客户端批量保存轨迹信息");
		dao.batchSaveAgentTailInfos(agentTailInfos);
	}

}
