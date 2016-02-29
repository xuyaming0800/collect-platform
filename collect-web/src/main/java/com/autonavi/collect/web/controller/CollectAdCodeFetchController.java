package com.autonavi.collect.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
/**
 * AdCode获取controller
 * @author yaming.xu
 *
 */
@Controller
public class CollectAdCodeFetchController extends BaseController<Object> {
	
	public CollectAdCodeFetchController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	
	
	//直辖市相关的城区/郊县
	private static ConcurrentHashMap<String, String> municipalitiesMap = new ConcurrentHashMap<String, String>();

	static {
		CollectAdCodeFetchController.municipalitiesMap.put("31", "上海");
		CollectAdCodeFetchController.municipalitiesMap.put("11", "北京");
		CollectAdCodeFetchController.municipalitiesMap.put("50", "重庆");
		CollectAdCodeFetchController.municipalitiesMap.put("12", "天津");
	}
	@RequestMapping("/fetchAdCode")
	public void fetchAdCode(HttpServletRequest req, HttpServletResponse resp){
		logger.info("获取adCode请求:" + req.getQueryString());
		ResultEntity entity = new ResultEntity();
		String x = req.getParameter("X");
		String y = req.getParameter("Y");
		String key = req.getParameter("flag");
		String isGPS = req.getParameter("isGPS");

		if (!this.valieateXY(x, y,logger)) {
			BusinessRunException ex = new BusinessRunException(BusinessExceptionEnum.COORDINATE_ERROR);
			this.writeError(entity, req, resp, ex);
			return;
		}
		Map<String, Object> adCodeMap = new HashMap<String, Object>();
		ResultDesc desc=new ResultDesc();
		desc.setCode("0");
		desc.setMsg(BaseController.SUCCESS);
		entity.setStatus(desc);
		try {
			if (!"0".equals(isGPS)) {
				String[] offsetList = taskCollectUtilService.offsetXY(x, y);
				if (offsetList.length == 2) {
					x = offsetList[0];
					y = offsetList[1];
				}
			}

			double corrX = Double.parseDouble(x);
			double corrY = Double.parseDouble(y);
			String adCode = taskCollectUtilService.fetchAdcode(corrX, corrY);
			if (adCode == null || "".equals(adCode) || (adCode.length() != 6)) {
				BusinessRunException ex = new BusinessRunException(BusinessExceptionEnum.FETCH_ADCODE_ERROR);
				this.writeError(entity, req, resp, ex);
				return;
			}
			logger.info("获取区县级adCode=" + adCode);
			if ("1".equals(key)) {
				String beginAdCode = adCode.substring(0, 2);
				if (CollectAdCodeFetchController.municipalitiesMap.containsKey(beginAdCode)) {
					adCode = beginAdCode + "0000";
				} else {
					adCode = adCode.substring(0, 4) + "00";
				}
			}
			adCodeMap.put("adCode", adCode);
			entity.setResult(adCodeMap);
		} catch (BusinessRunException e) {
			this.writeError(entity, req, resp, e);
			return;
			
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			BusinessRunException ex = new BusinessRunException(BusinessExceptionEnum.FETCH_ADCODE_ERROR);
			this.writeError(entity, req, resp, ex);
			return;
		}

		// 输出结果
		this.writeResult(entity, req, resp);
	}

}
