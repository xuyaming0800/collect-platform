package cn.dataup.collect.tools.service;

import com.autonavi.collect.exception.BusinessException;

public interface CollectShapeService {
	public Boolean checkLocationsVaild(Double x,Double y,Long clazzId,Integer gpsSystem)throws BusinessException;
}
