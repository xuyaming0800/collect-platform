package com.gd.app.dao;

import java.util.List;

import com.gd.app.entity.AgentTailInfo;

public interface AgentTailInfoDao {
	
	public Object batchSaveAgentTailInfos(List<AgentTailInfo> agentTailInfos)
			throws Exception;

}
