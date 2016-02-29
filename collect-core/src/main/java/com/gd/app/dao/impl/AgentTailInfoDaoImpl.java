package com.gd.app.dao.impl;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;

import com.gd.app.dao.AgentTailInfoDao;
import com.gd.app.entity.AgentTailInfo;

/**
 * 
 * @author ziyu.wei
 *
 */
@Repository("agentTailInfoDao")
public class AgentTailInfoDaoImpl implements AgentTailInfoDao {
	
	private Logger logger = LogManager.getLogger(AgentTailInfoDaoImpl.class);

	@Override
	@Author("ziyu.wei")
	@Shard(indexName = "username_index", indexColumn = "list." + ReservedWord.index + ".userName")
	@Insert
	public Object batchSaveAgentTailInfos(@SqlParameter("list") List<AgentTailInfo> list)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		
		sb.append("insert into AGENT_TRAIL_INFO (ID, USER_NAME, IMEI, X, Y, ")
			.append("OFFSET_X, OFFSET_Y, ALTITUDE, GPS_TIME, SPEED, DIRECTION, ")
			.append("POINT_COUNT, POINT_ACCURACY) values (")
			.append("#{").append(ReservedWord.snowflake).append("},")
			.append("#{list.").append(ReservedWord.index).append(".userName},")
			
			.append("#{list.").append(ReservedWord.index).append(".imei},")
			.append("#{list.").append(ReservedWord.index).append(".x},")
			.append("#{list.").append(ReservedWord.index).append(".y},")
			.append("#{list.").append(ReservedWord.index).append(".offsetX},")
			.append("#{list.").append(ReservedWord.index).append(".offsetY},")
			.append("#{list.").append(ReservedWord.index).append(".altitude},")
			.append("#{list.").append(ReservedWord.index).append(".gpsTime},")
			.append("#{list.").append(ReservedWord.index).append(".speed},")
			.append("#{list.").append(ReservedWord.index).append(".direction},")
			.append("#{list.").append(ReservedWord.index).append(".pointCount},")
			.append("#{list.").append(ReservedWord.index).append(".pointAccuracy})");
		
		logger.trace("sql: " + sb);
		
		return sb.toString();
	}

}
