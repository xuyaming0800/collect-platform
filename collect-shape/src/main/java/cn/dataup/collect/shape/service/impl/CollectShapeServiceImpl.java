package cn.dataup.collect.shape.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geo.util.CoordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dataup.collect.shape.component.ShapeInfoCacheComponent;
import cn.dataup.collect.tools.service.CollectShapeService;

import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.mapabc.service.RegionSearch;
@Service
public class CollectShapeServiceImpl implements CollectShapeService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private ShapeInfoCacheComponent shapeInfoCacheComponent;

	@Override
	public Boolean checkLocationsVaild(Double x, Double y, Long clazzId,
			Integer gpsSystem)throws BusinessException {
		logger.info("检测地址围栏开始 x，y=["+x+","+y+"] clazzId=["+clazzId+"]");
		try {
			double[] xy=new double[]{x,y};
			if(gpsSystem!=null){
				if(gpsSystem==CommonConstant.GPS_SYSTEM.BAIDU.getCode()){
					//百度转GPS
					CoordUtils.bd2gps(x, y, xy);
				}else if(gpsSystem==CommonConstant.GPS_SYSTEM.GCJ.getCode()){
					//国测局转GPS
					CoordUtils.gcj2gps(x, y, xy);
				}
			}
			RegionSearch rs=shapeInfoCacheComponent.getRegionSearch(clazzId);
			if(rs==null){
				logger.warn("没有找到对应的SHAPE信息");
				return true;
			}else{
				boolean result=rs.queryRegion(xy[0], xy[1]).isSuccessful();
				logger.info("检测地址围栏结果 x，y=["+x+","+y+"] clazzId=["+clazzId+"] result["+result+"]");
				return result;
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
			}
		}
	}

}
