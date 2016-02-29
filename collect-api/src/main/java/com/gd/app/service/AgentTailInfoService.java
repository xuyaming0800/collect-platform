package com.gd.app.service;

import java.util.List;

import com.gd.app.entity.AgentTailInfo;

/**
 * 轨迹上报
 * 
 * @author ziyu.wei
 *
 */
public interface AgentTailInfoService {

	/*public void saveAgentTailInfo(String userName, String x, String y,
			String imei, String altitude, String gpsTime, String speed,
			String direction, String pointCount, String pointAccuracy,
			String offsetX, String offsetY) throws Exception;*/

	public void batchSaveAgentTailInfos(final List<AgentTailInfo> agentTailInfos)
			throws Exception;
}
