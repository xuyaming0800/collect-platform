package com.gd.app.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.dao.AgentLocationDao;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.security.identifyingCode.IdentifyingCode;
import com.gd.app.service.TaskUtilService;
import com.gd.app.util.CoordinateConvert;
import com.gd.app.util.SecureCodeUtil;
import com.gd.app.util.SysProps;
import com.mapabc.service.RegionSearch;
import com.mapabc.shp.io.model.RegionBean;
//@Service("taskUtilService")
public class TaskUtilServiceImpl implements TaskUtilService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	AgentLocationDao agentLocationDao;
	
	private PropertiesConfig pc=null;
	
	public TaskUtilServiceImpl()throws Exception{
		pc=PropertiesConfigUtil.getPropertiesConfigInstance();
	}

	@Override
	public boolean checkLocationVaild(double corrX, double corrY) {
		String shapePath="";
		try {
			shapePath = PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_SHAPEFILE_PATH).toString();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			AppException ex = new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
			throw ex;
		}
		RegionBean r=RegionSearch.getInstance(shapePath).queryRegion(corrX, corrY);
		return r.isSuccessful();
	}

	@Override
	public String[] offsetXY(String x, String y) {
		try {
			String offsetCorrdinate = CoordinateConvert.GPS2Deflexion(x + "," + y);
			String[] offsetList = offsetCorrdinate.split(",");
			return offsetList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			AppException ex = new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
			throw ex;
		}
	}

	@Override
	public String fetchAdcode(double corrX, double corrY) {
		try {
			com.mapabc.spatial.RegionSearch rs = com.mapabc.spatial.RegionSearch.getInstance();
			String adCode = rs.getPointADCode(corrX, corrY);
			return adCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			AppException ex = new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
			throw ex;
		}
	}

	@Override
	public Map<String, Object> generateSecureCode() {
		try {
			IdentifyingCode identifyingCode = SecureCodeUtil
					.getSecureCodeInstance();
			return identifyingCode.getCode();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			AppException ex = new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
			throw ex;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public String querySystemDateTime() {
		// TODO Auto-generated method stub
		List<Map<String,Object>> l=(List<Map<String,Object>>)agentLocationDao.getSystemTime();
		Map<String,Object> m=l.get(0);
		return m.get("time").toString();
	}

	@Override
	public void saveErrorInfo(String userName, String type, String version,
			String deviceInfo, String erCode, String erInfo) {
        try {
        	agentLocationDao.addAppErrorInfo(userName, type, version, deviceInfo, erCode, erInfo);
        } catch (Exception e) {
            logger.error("保存错误信息失败", e);
            throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
        }
		
	}

	@Override
	public String getProperty(String key) {
		return (String)pc.getProperty(key);
	}

}
